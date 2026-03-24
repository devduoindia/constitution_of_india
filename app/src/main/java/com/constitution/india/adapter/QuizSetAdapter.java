package com.constitution.india.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.constitution.india.R;
import com.constitution.india.model.QuizSet;
import com.constitution.india.utils.AppPreferences;

import java.util.List;

public class QuizSetAdapter extends RecyclerView.Adapter<QuizSetAdapter.ViewHolder> {

    private final Context context;
    private final List<QuizSet> quizSets;
    private final AppPreferences prefs;
    private OnSetClickListener listener;

    public interface OnSetClickListener {
        void onSetClick(QuizSet set);
    }

    public QuizSetAdapter(Context context, List<QuizSet> quizSets) {
        this.context = context;
        this.quizSets = quizSets;
        this.prefs = new AppPreferences(context);
    }

    public void setOnSetClickListener(OnSetClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quiz_set, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int viewType) {
        QuizSet set = quizSets.get(holder.getAdapterPosition());
        boolean isEn = prefs.isEnglish();

        holder.tvName.setText(isEn ? set.getNameEn() : set.getNameHi());
        holder.tvQuestionCount.setText((isEn ? "5 Questions" : "5 प्रश्न"));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSetClick(set);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizSets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuestionCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_set_name);
            tvQuestionCount = itemView.findViewById(R.id.tv_question_count);
        }
    }
}
