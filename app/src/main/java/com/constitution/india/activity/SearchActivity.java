package com.constitution.india.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.constitution.india.R;
import com.constitution.india.adapter.ArticleAdapter;
import com.constitution.india.model.Article;
import com.constitution.india.utils.AppPreferences;
import com.constitution.india.utils.ContentManager;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ContentManager contentManager;
    private AppPreferences prefs;
    private ArticleAdapter adapter;
    private TextView tvEmpty, tvResultCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        prefs = new AppPreferences(this);
        contentManager = ContentManager.getInstance(this);

        ImageView ivBack = findViewById(R.id.iv_back_search);
        ivBack.setOnClickListener(v -> {
            onBackPressed();
        });

        tvEmpty = findViewById(R.id.tv_empty_search);
        tvResultCount = findViewById(R.id.tv_result_count);

        RecyclerView recyclerView = findViewById(R.id.rv_search_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ArticleAdapter(this, new ArrayList<>());
        adapter.setOnArticleClickListener(new ArticleAdapter.OnArticleClickListener() {
            @Override
            public void onArticleClick(Article article) {
                Intent intent = new Intent(SearchActivity.this, ArticleDetailActivity.class);
                intent.putExtra(ArticleDetailActivity.EXTRA_ARTICLE_ID, article.getId());
                intent.putExtra(ArticleDetailActivity.EXTRA_CATEGORY_ID, article.getCategoryId());
                startActivity(intent);
            }

            @Override
            public void onBookmarkClick(Article article, int position) {
                boolean was = prefs.isBookmarked(article.getId());
                if (was) prefs.removeBookmark(article.getId());
                else prefs.addBookmark(article.getId());
                adapter.notifyItemChanged(position);
            }
        });
        recyclerView.setAdapter(adapter);

        EditText etSearch = findViewById(R.id.et_search);
        boolean isEn = prefs.isEnglish();
        etSearch.setHint(isEn ? "Search articles, rights, duties..." : "अनुच्छेद खोजें...");
        etSearch.requestFocus();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    adapter.updateArticles(new ArrayList<>());
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText(isEn ? "Type to search…" : "खोजने के लिए टाइप करें…");
                    tvResultCount.setVisibility(View.GONE);
                } else {
                    List<Article> results = contentManager.searchArticles(query);
                    adapter.updateArticles(results);
                    tvResultCount.setVisibility(View.VISIBLE);
                    tvResultCount.setText(results.size() + (isEn ? " results" : " परिणाम"));
                    if (results.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        tvEmpty.setText(isEn ? "No results found" : "कोई परिणाम नहीं मिला");
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                    }
                }
            }
        });

        tvEmpty.setVisibility(View.VISIBLE);
        tvEmpty.setText(isEn ? "Type to search…" : "खोजने के लिए टाइप करें…");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
