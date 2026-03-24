package com.constitution.india.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.constitution.india.R;
import com.constitution.india.model.Category;
import com.constitution.india.utils.AppPreferences;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private final Context context;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    /** Call this when language changes to swap in the new list and redraw */
    public void updateCategories(List<Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        // Read prefs fresh each bind so language changes are picked up immediately
        AppPreferences prefs = new AppPreferences(context);
        boolean isEn = prefs.isEnglish();

        holder.tvTitle.setText(isEn ? category.getTitleEn() : category.getTitleHi());
        holder.tvDescription.setText(isEn ? category.getDescriptionEn() : category.getDescriptionHi());

        int count = category.getArticleCount();
        holder.tvArticleCount.setText(count + (isEn ? " Articles" : " अनुच्छेद"));

        if (category.getPartNumber() != null && !category.getPartNumber().isEmpty()) {
            holder.tvPartNumber.setVisibility(View.VISIBLE);
            holder.tvPartNumber.setText(isEn ? "Part " + category.getPartNumber()
                    : "भाग " + category.getPartNumber());
        } else {
            holder.tvPartNumber.setVisibility(View.GONE);
        }

        int[] colors = {
                R.color.cat_orange, R.color.cat_blue, R.color.cat_green,
                R.color.cat_purple, R.color.cat_red, R.color.cat_teal,
                R.color.cat_amber, R.color.cat_indigo, R.color.cat_pink,
                R.color.cat_cyan, R.color.cat_lime, R.color.cat_brown
        };
        holder.vColorStrip.setBackgroundResource(colors[position % colors.length]);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCategoryClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvArticleCount, tvPartNumber;
        View vColorStrip;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_category_title);
            tvDescription = itemView.findViewById(R.id.tv_category_description);
            tvArticleCount = itemView.findViewById(R.id.tv_article_count);
            tvPartNumber = itemView.findViewById(R.id.tv_part_number);
            vColorStrip = itemView.findViewById(R.id.v_color_strip);
        }
    }
}