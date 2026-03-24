package com.constitution.india.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.constitution.india.R;
import com.constitution.india.model.Article;
import com.constitution.india.utils.AppPreferences;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private List<Article> articles;
    private final AppPreferences prefs;
    private OnArticleClickListener listener;

    public interface OnArticleClickListener {
        void onArticleClick(Article article);
        void onBookmarkClick(Article article, int position);
    }

    public ArticleAdapter(Context context, List<Article> articles) {
        this.articles = articles;
        this.prefs = new AppPreferences(context);
    }

    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.listener = listener;
    }

    public void updateArticles(List<Article> newArticles) {
        this.articles = newArticles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articles.get(position);
        boolean isEn = prefs.isEnglish();

        String title = isEn ? article.getTitleEn() : article.getTitleHi();
        String content = isEn ? article.getContentEn() : article.getContentHi();
        String number = article.getNumber();

        holder.tvArticleNumber.setText(number != null && !number.isEmpty() ? number : "#");
        holder.tvTitle.setText(title);

        // Show first 100 chars as preview
        if (content != null && content.length() > 120) {
            holder.tvPreview.setText(content.substring(0, 120) + "…");
        } else {
            holder.tvPreview.setText(content);
        }

        // Bookmark state
        boolean bookmarked = prefs.isBookmarked(article.getId());
        holder.ivBookmark.setImageResource(bookmarked ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onArticleClick(article);
        });

        holder.ivBookmark.setOnClickListener(v -> {
            if (listener != null) listener.onBookmarkClick(article, position);
        });
    }

    @Override
    public int getItemCount() {
        return articles != null ? articles.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvArticleNumber, tvTitle, tvPreview;
        ImageView ivBookmark;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvArticleNumber = itemView.findViewById(R.id.tv_article_number);
            tvTitle = itemView.findViewById(R.id.tv_article_title);
            tvPreview = itemView.findViewById(R.id.tv_article_preview);
            ivBookmark = itemView.findViewById(R.id.iv_bookmark);
        }
    }
}
