package com.constitution.india.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.constitution.india.R;
import com.constitution.india.adapter.ArticleAdapter;
import com.constitution.india.model.Article;
import com.constitution.india.model.Category;
import com.constitution.india.utils.AppPreferences;
import com.constitution.india.utils.ContentManager;

import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_ID = "category_id";

    private ContentManager contentManager;
    private AppPreferences prefs;
    private ArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        prefs = new AppPreferences(this);
        contentManager = ContentManager.getInstance(this);

        String categoryId = getIntent().getStringExtra(EXTRA_CATEGORY_ID);
        if (categoryId == null) {
            finish();
            return;
        }

        Category category = contentManager.getCategoryById(categoryId);
        if (category == null) {
            finish();
            return;
        }

        setupToolbar(category);
        setupRecyclerView(category);
    }

    private void setupToolbar(Category category) {
        boolean isEn = prefs.isEnglish();

        TextView tvTitle = findViewById(R.id.tv_toolbar_title);
        TextView tvSubtitle = findViewById(R.id.tv_toolbar_subtitle);
        TextView tvArticleCount = findViewById(R.id.tv_toolbar_count);
        ImageView ivBack = findViewById(R.id.iv_back);

        tvTitle.setText(isEn ? category.getTitleEn() : category.getTitleHi());

        String desc = isEn ? category.getDescriptionEn() : category.getDescriptionHi();
        if (desc != null && !desc.isEmpty()) {
            tvSubtitle.setVisibility(View.VISIBLE);
            tvSubtitle.setText(desc);
        } else {
            tvSubtitle.setVisibility(View.GONE);
        }

        int count = category.getArticleCount();
        tvArticleCount.setText(count + (isEn ? " Articles" : " अनुच्छेद"));

        ivBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void setupRecyclerView(Category category) {
        RecyclerView recyclerView = findViewById(R.id.rv_articles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Article> articles = category.getArticles();
        // Mark bookmarks
        for (Article a : articles) {
            a.setBookmarked(prefs.isBookmarked(a.getId()));
        }

        adapter = new ArticleAdapter(this, articles);
        adapter.setOnArticleClickListener(new ArticleAdapter.OnArticleClickListener() {
            @Override
            public void onArticleClick(Article article) {
                Intent intent = new Intent(CategoryActivity.this, ArticleDetailActivity.class);
                intent.putExtra(ArticleDetailActivity.EXTRA_ARTICLE_ID, article.getId());
                intent.putExtra(ArticleDetailActivity.EXTRA_CATEGORY_ID, category.getId());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

            @Override
            public void onBookmarkClick(Article article, int position) {
                boolean wasBookmarked = prefs.isBookmarked(article.getId());
                if (wasBookmarked) {
                    prefs.removeBookmark(article.getId());
                    Toast.makeText(CategoryActivity.this,
                            prefs.isEnglish() ? "Bookmark removed" : "बुकमार्क हटाया",
                            Toast.LENGTH_SHORT).show();
                } else {
                    prefs.addBookmark(article.getId());
                    Toast.makeText(CategoryActivity.this,
                            prefs.isEnglish() ? "Bookmarked!" : "बुकमार्क किया!",
                            Toast.LENGTH_SHORT).show();
                }
                adapter.notifyItemChanged(position);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
