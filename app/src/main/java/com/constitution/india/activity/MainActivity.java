package com.constitution.india.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.constitution.india.R;
import com.constitution.india.adapter.CategoryAdapter;
import com.constitution.india.model.Category;
import com.constitution.india.utils.AppPreferences;
import com.constitution.india.utils.ContentManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ContentManager contentManager;
    private AppPreferences prefs;
    private CategoryAdapter adapter;
    private TextView tvGreeting, tvLanguageLabel, tvQuizTitle, tvQuizSubtitle, tvBrowseLabel;
    private CardView cvQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = new AppPreferences(this);
        contentManager = ContentManager.getInstance(this);

        initViews();
        setupRecyclerView();
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch fresh categories in the current language and push to adapter
        List<Category> freshCategories = contentManager.getCategories();
        if (adapter != null) {
            adapter.updateCategories(freshCategories);
        }
        updateUI();
    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tv_greeting);
        tvLanguageLabel = findViewById(R.id.tv_language_label);
        tvQuizTitle = findViewById(R.id.tv_quiz_title);
        tvQuizSubtitle = findViewById(R.id.tv_quiz_subtitle);
        tvBrowseLabel = findViewById(R.id.tv_browse_label);
        cvQuiz = findViewById(R.id.cv_quiz);

        ImageView ivSettings = findViewById(R.id.iv_settings);
        ImageView ivBookmarks = findViewById(R.id.iv_bookmarks);
        ImageView ivSearch = findViewById(R.id.iv_search);

        ivSettings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
        ivBookmarks.setOnClickListener(v ->
                startActivity(new Intent(this, BookmarksActivity.class)));
        ivSearch.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));

        cvQuiz.setOnClickListener(v ->
                startActivity(new Intent(this, QuizListActivity.class)));
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        List<Category> categories = contentManager.getCategories();
        adapter = new CategoryAdapter(this, categories);
        adapter.setOnCategoryClickListener(category -> {
            Intent intent = new Intent(this, CategoryActivity.class);
            intent.putExtra(CategoryActivity.EXTRA_CATEGORY_ID, category.getId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        recyclerView.setAdapter(adapter);
    }

    private void updateUI() {
        boolean isEn = prefs.isEnglish();
        // Show the opposite script as a decorative subtitle, current language as badge
        tvGreeting.setText(isEn ? "भारत का संविधान" : "Constitution of India");
        tvLanguageLabel.setText(isEn ? "EN" : "HI");

        if (isEn) {
            tvQuizTitle.setText("Knowledge Test");
            tvQuizSubtitle.setText("Challenge yourself with 10 sets of MCQs");
            tvBrowseLabel.setText("Browse by Category");
        } else {
            tvQuizTitle.setText("ज्ञान परीक्षण");
            tvQuizSubtitle.setText("MCQ के 10 सेटों के साथ खुद को चुनौती दें");
            tvBrowseLabel.setText("श्रेणी के अनुसार ब्राउज़ करें");
        }
    }
}
