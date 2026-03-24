package com.constitution.india.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.constitution.india.R;
import com.constitution.india.adapter.QuizSetAdapter;
import com.constitution.india.model.QuizSet;
import com.constitution.india.utils.AppPreferences;
import com.constitution.india.utils.ContentManager;

import java.util.List;

public class QuizListActivity extends AppCompatActivity {

    private AppPreferences prefs;
    private ContentManager contentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        prefs = new AppPreferences(this);
        contentManager = ContentManager.getInstance(this);

        initViews();
        setupRecyclerView();
    }

    private void initViews() {
        ImageView ivBack = findViewById(R.id.iv_back);
        TextView tvTitle = findViewById(R.id.tv_title);

        ivBack.setOnClickListener(v -> finish());
        
        boolean isEn = prefs.isEnglish();
        tvTitle.setText(isEn ? "Knowledge Test" : "ज्ञान परीक्षण");
    }

    private void setupRecyclerView() {
        RecyclerView rvQuizSets = findViewById(R.id.rv_quiz_sets);
        rvQuizSets.setLayoutManager(new LinearLayoutManager(this));

        List<QuizSet> quizSets = contentManager.getQuizSets();
        QuizSetAdapter adapter = new QuizSetAdapter(this, quizSets);
        adapter.setOnSetClickListener(set -> {
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("SET_ID", set.getId());
            startActivity(intent);
        });
        rvQuizSets.setAdapter(adapter);
    }
}
