package com.constitution.india.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import java.util.List;

public class BookmarksActivity extends AppCompatActivity {

    private AppPreferences prefs;
    private ContentManager contentManager;
    private ArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        prefs = new AppPreferences(this);
        contentManager = ContentManager.getInstance(this);

        ImageView ivBack = findViewById(R.id.iv_back_bookmarks);
        ivBack.setOnClickListener(v -> onBackPressed());

        TextView tvEmpty = findViewById(R.id.tv_empty_bookmarks);
        RecyclerView recyclerView = findViewById(R.id.rv_bookmarks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Article> bookmarked = contentManager.getBookmarkedArticles();

        if (bookmarked.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setText(prefs.isEnglish() ? "No bookmarks yet.\nTap ☆ on any article to save it." : "अभी कोई बुकमार्क नहीं।\nकिसी भी अनुच्छेद पर ☆ दबाएं।");
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new ArticleAdapter(this, bookmarked);
            adapter.setOnArticleClickListener(new ArticleAdapter.OnArticleClickListener() {
                @Override
                public void onArticleClick(Article article) {
                    Intent intent = new Intent(BookmarksActivity.this, ArticleDetailActivity.class);
                    intent.putExtra(ArticleDetailActivity.EXTRA_ARTICLE_ID, article.getId());
                    intent.putExtra(ArticleDetailActivity.EXTRA_CATEGORY_ID, article.getCategoryId());
                    startActivity(intent);
                }

                @Override
                public void onBookmarkClick(Article article, int position) {
                    prefs.removeBookmark(article.getId());
                    bookmarked.remove(position);
                    adapter.notifyItemRemoved(position);
                    if (bookmarked.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
