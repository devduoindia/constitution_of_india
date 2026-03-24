package com.constitution.india.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.constitution.india.R;
import com.constitution.india.model.Article;
import com.constitution.india.model.Category;
import com.constitution.india.utils.AppPreferences;
import com.constitution.india.utils.ContentManager;
import com.constitution.india.utils.TtsManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ArticleDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ARTICLE_ID = "article_id";
    public static final String EXTRA_CATEGORY_ID = "category_id";

    private AppPreferences prefs;
    private ContentManager contentManager;
    private TtsManager ttsManager;
    private Article currentArticle;
    private List<Article> categoryArticles;
    private int currentIndex = -1;
    private boolean isSpeaking = false;
    private boolean isPaused = false;
    private int lastRangeStart = 0;
    private String fullTextToSpeak = "";

    private TextView tvArticleNumber, tvTitle, tvContent;
    private ImageView ivBookmark, ivBack, ivPrev, ivNext, ivTts;
    private TextView tvNavInfo;
    private EditText etNote;
    private Button btnSaveNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        prefs = new AppPreferences(this);
        contentManager = ContentManager.getInstance(this);
        ttsManager = TtsManager.getInstance(this);

        String articleId = getIntent().getStringExtra(EXTRA_ARTICLE_ID);
        String categoryId = getIntent().getStringExtra(EXTRA_CATEGORY_ID);

        initViews();

        if (articleId != null) {
            currentArticle = contentManager.getArticleById(articleId);
            if (categoryId != null) {
                Category cat = contentManager.getCategoryById(categoryId);
                if (cat != null && cat.getArticles() != null) {
                    categoryArticles = cat.getArticles();
                    for (int i = 0; i < categoryArticles.size(); i++) {
                        if (categoryArticles.get(i).getId().equals(articleId)) {
                            currentIndex = i;
                            break;
                        }
                    }
                }
            }
        }

        if (currentArticle == null) {
            finish();
            return;
        }

        displayArticle();
        setupNavigation();
        setupTtsListener();
        setupSelectionActions();
    }

    private void initViews() {
        tvArticleNumber = findViewById(R.id.tv_article_number_detail);
        tvTitle = findViewById(R.id.tv_article_title_detail);
        tvContent = findViewById(R.id.tv_article_content);
        ivBookmark = findViewById(R.id.iv_bookmark_detail);
        ivBack = findViewById(R.id.iv_back_detail);
        ivPrev = findViewById(R.id.iv_prev);
        ivNext = findViewById(R.id.iv_next);
        ivTts = findViewById(R.id.iv_tts);
        tvNavInfo = findViewById(R.id.tv_nav_info);
        etNote = findViewById(R.id.et_note);
        btnSaveNote = findViewById(R.id.btn_save_note);

        ivBack.setOnClickListener(v -> onBackPressed());
        ivBookmark.setOnClickListener(v -> toggleBookmark());
        ivTts.setOnClickListener(v -> toggleTts());

        btnSaveNote.setOnClickListener(v -> {
            prefs.saveNote(currentArticle.getId(), etNote.getText().toString());
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        });

        // Font size controls
        ImageView ivFontIncrease = findViewById(R.id.iv_font_increase);
        ImageView ivFontDecrease = findViewById(R.id.iv_font_decrease);

        ivFontIncrease.setOnClickListener(v -> {
            int size = prefs.getFontSize();
            if (size < 24) {
                prefs.setFontSize(size + 2);
                tvContent.setTextSize(prefs.getFontSize());
            }
        });

        ivFontDecrease.setOnClickListener(v -> {
            int size = prefs.getFontSize();
            if (size > 12) {
                prefs.setFontSize(size - 2);
                tvContent.setTextSize(prefs.getFontSize());
            }
        });
    }

    private void setupTtsListener() {
        ttsManager.setProgressListener(new TtsManager.TtsProgressListener() {
            @Override
            public void onStart(String utteranceId) {}

            @Override
            public void onDone(String utteranceId) {
                runOnUiThread(() -> {
                    isSpeaking = false;
                    isPaused = false;
                    lastRangeStart = 0;
                    ivTts.setImageResource(R.drawable.ic_play_arrow);
                    clearTtsHighlight();
                });
            }

            @Override
            public void onError(String utteranceId) {}

            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
                runOnUiThread(() -> {
                    lastRangeStart = start;
                    highlightTtsText(start, end);
                });
            }
        });
    }

    private void highlightTtsText(int start, int end) {
        String titleText = tvTitle.getText().toString();
        int highlightColor = ContextCompat.getColor(this, R.color.highlight_tts);
        if (start < titleText.length()) {
            SpannableString spannable = new SpannableString(titleText);
            spannable.setSpan(new BackgroundColorSpan(highlightColor), start, Math.min(end, titleText.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvTitle.setText(spannable);
        } else {
            // Offset for content text (title + ". ")
            int offset = titleText.length() + 2;
            int contentStart = start - offset;
            int contentEnd = end - offset;
            if (contentStart >= 0) {
                SpannableString spannable = new SpannableString(tvContent.getText().toString());
                spannable.setSpan(new BackgroundColorSpan(highlightColor), contentStart, Math.min(contentEnd, spannable.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvContent.setText(spannable);
                applySavedAnnotations(); // Restore user annotations as well
            }
        }
    }

    private void clearTtsHighlight() {
        tvTitle.setText(tvTitle.getText().toString());
        tvContent.setText(tvContent.getText().toString());
        applySavedAnnotations();
    }

    private void toggleTts() {
        boolean isEn = prefs.isEnglish();
        if (isSpeaking) {
            ttsManager.stop();
            ivTts.setImageResource(R.drawable.ic_play_arrow);
            isSpeaking = false;
            isPaused = true;
        } else {
            if (!isPaused) {
                fullTextToSpeak = tvTitle.getText().toString() + ". " + tvContent.getText().toString();
                lastRangeStart = 0;
            }
            String textToSpeak = fullTextToSpeak.substring(lastRangeStart);
            ttsManager.speak(textToSpeak, isEn ? "en" : "hi", "ARTICLE_TTS");
            ivTts.setImageResource(R.drawable.ic_pause);
            isSpeaking = true;
            isPaused = false;
        }
    }

    private void displayArticle() {
        if (isSpeaking) {
            ttsManager.stop();
            ivTts.setImageResource(R.drawable.ic_play_arrow);
            isSpeaking = false;
            isPaused = false;
            lastRangeStart = 0;
        }

        boolean isEn = prefs.isEnglish();
        String number = currentArticle.getNumber();
        String title = isEn ? currentArticle.getTitleEn() : currentArticle.getTitleHi();
        String content = isEn ? currentArticle.getContentEn() : currentArticle.getContentHi();

        if (number != null && !number.isEmpty()) {
            tvArticleNumber.setVisibility(View.VISIBLE);
            tvArticleNumber.setText(isEn ? "Article " + number : "अनुच्छेद " + number);
        } else {
            tvArticleNumber.setVisibility(View.GONE);
        }

        tvTitle.setText(title);
        tvContent.setText(content);
        tvContent.setTextSize(prefs.getFontSize());

        // Load note
        String savedNote = prefs.getNote(currentArticle.getId());
        etNote.setText(savedNote != null ? savedNote : "");

        applySavedAnnotations();

        boolean bookmarked = prefs.isBookmarked(currentArticle.getId());
        ivBookmark.setImageResource(bookmarked ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
    }

    private void applySavedAnnotations() {
        String contentStr = tvContent.getText().toString();
        SpannableString spannable = new SpannableString(contentStr);
        
        int highlightColor = ContextCompat.getColor(this, R.color.highlight_user);

        // Apply Highlights
        Set<String> highlights = prefs.getHighlights(currentArticle.getId());
        for (String h : highlights) {
            String[] parts = h.split("-");
            if (parts.length == 2) {
                int start = Integer.parseInt(parts[0]);
                int end = Integer.parseInt(parts[1]);
                if (start >= 0 && start < spannable.length() && end > start && end <= spannable.length()) {
                    spannable.setSpan(new BackgroundColorSpan(highlightColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        // Apply Underlines
        Set<String> underlines = prefs.getUnderlines(currentArticle.getId());
        for (String u : underlines) {
            String[] parts = u.split("-");
            if (parts.length == 2) {
                int start = Integer.parseInt(parts[0]);
                int end = Integer.parseInt(parts[1]);
                if (start >= 0 && start < spannable.length() && end > start && end <= spannable.length()) {
                    spannable.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        
        tvContent.setText(spannable);
    }

    private void setupSelectionActions() {
        tvContent.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                menu.add(0, 101, 0, "Highlight");
                menu.add(0, 102, 1, "Remove Highlight");
                menu.add(0, 103, 2, "Underline");
                menu.add(0, 104, 3, "Remove Underline");
                menu.add(0, 105, 4, "Copy");
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int start = tvContent.getSelectionStart();
                int end = tvContent.getSelectionEnd();
                if (start < 0 || end < 0 || start == end) return false;

                String articleId = currentArticle.getId();

                if (item.getItemId() == 101) {
                    Set<String> highlights = new HashSet<>(prefs.getHighlights(articleId));
                    highlights.add(start + "-" + end);
                    prefs.saveHighlights(articleId, highlights);
                    applySavedAnnotations();
                    mode.finish();
                    return true;
                } else if (item.getItemId() == 102) {
                    Set<String> highlights = new HashSet<>(prefs.getHighlights(articleId));
                    if (removeAnnotation(highlights, start, end)) {
                        prefs.saveHighlights(articleId, highlights);
                        applySavedAnnotations();
                        Toast.makeText(ArticleDetailActivity.this, "Highlight removed", Toast.LENGTH_SHORT).show();
                    }
                    mode.finish();
                    return true;
                } else if (item.getItemId() == 103) {
                    Set<String> underlines = new HashSet<>(prefs.getUnderlines(articleId));
                    underlines.add(start + "-" + end);
                    prefs.saveUnderlines(articleId, underlines);
                    applySavedAnnotations();
                    mode.finish();
                    return true;
                } else if (item.getItemId() == 104) {
                    Set<String> underlines = new HashSet<>(prefs.getUnderlines(articleId));
                    if (removeAnnotation(underlines, start, end)) {
                        prefs.saveUnderlines(articleId, underlines);
                        applySavedAnnotations();
                        Toast.makeText(ArticleDetailActivity.this, "Underline removed", Toast.LENGTH_SHORT).show();
                    }
                    mode.finish();
                    return true;
                } else if (item.getItemId() == 105) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Constitution", tvContent.getText().subSequence(start, end));
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(ArticleDetailActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    mode.finish();
                    return true;
                }
                return false;
            }

            private boolean removeAnnotation(Set<String> annotations, int selStart, int selEnd) {
                boolean removed = false;
                Iterator<String> iterator = annotations.iterator();
                while (iterator.hasNext()) {
                    String a = iterator.next();
                    String[] parts = a.split("-");
                    if (parts.length == 2) {
                        int aStart = Integer.parseInt(parts[0]);
                        int aEnd = Integer.parseInt(parts[1]);
                        // Overlap or match detection
                        if ((selStart >= aStart && selStart < aEnd) || (selEnd > aStart && selEnd <= aEnd)) {
                            iterator.remove();
                            removed = true;
                        }
                    }
                }
                return removed;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {}
        });
    }

    private void setupNavigation() {
        if (categoryArticles == null || categoryArticles.size() <= 1) {
            ivPrev.setVisibility(View.GONE);
            ivNext.setVisibility(View.GONE);
            tvNavInfo.setVisibility(View.GONE);
            return;
        }

        ivPrev.setEnabled(currentIndex > 0);
        ivNext.setEnabled(currentIndex < categoryArticles.size() - 1);
        ivPrev.setAlpha(currentIndex > 0 ? 1f : 0.3f);
        ivNext.setAlpha(currentIndex < categoryArticles.size() - 1 ? 1f : 0.3f);

        tvNavInfo.setText(String.format("%d / %d", currentIndex + 1, categoryArticles.size()));

        ivPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                currentArticle = categoryArticles.get(currentIndex);
                displayArticle();
                setupNavigation();
                NestedScrollView scrollView = findViewById(R.id.scroll_view);
                scrollView.smoothScrollTo(0, 0);
            }
        });

        ivNext.setOnClickListener(v -> {
            if (currentIndex < categoryArticles.size() - 1) {
                currentIndex++;
                currentArticle = categoryArticles.get(currentIndex);
                displayArticle();
                setupNavigation();
                NestedScrollView scrollView = findViewById(R.id.scroll_view);
                scrollView.smoothScrollTo(0, 0);
            }
        });
    }

    private void toggleBookmark() {
        boolean wasBookmarked = prefs.isBookmarked(currentArticle.getId());
        if (wasBookmarked) {
            prefs.removeBookmark(currentArticle.getId());
            ivBookmark.setImageResource(R.drawable.ic_bookmark_outline);
            Toast.makeText(this, "Bookmark removed", Toast.LENGTH_SHORT).show();
        } else {
            prefs.addBookmark(currentArticle.getId());
            ivBookmark.setImageResource(R.drawable.ic_bookmark_filled);
            Toast.makeText(this, "Bookmarked", Toast.LENGTH_SHORT).show();
        }
    }
}
