package com.constitution.india.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.constitution.india.R;
import com.constitution.india.model.QuizQuestion;
import com.constitution.india.model.QuizSet;
import com.constitution.india.utils.AppPreferences;
import com.constitution.india.utils.ContentManager;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private AppPreferences prefs;
    private ContentManager contentManager;
    private QuizSet currentSet;
    private List<QuizQuestion> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private List<Integer> userAnswers = new ArrayList<>();

    private TextView tvQuestion, tvProgress, tvTitle;
    private RadioGroup rgOptions;
    private RadioButton rb1, rb2, rb3, rb4;
    private Button btnNext;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        prefs = new AppPreferences(this);
        contentManager = ContentManager.getInstance(this);

        String setId = getIntent().getStringExtra("SET_ID");
        for (QuizSet set : contentManager.getQuizSets()) {
            if (set.getId().equals(setId)) {
                currentSet = set;
                break;
            }
        }

        if (currentSet == null) {
            finish();
            return;
        }

        questions = currentSet.getQuestions();
        initViews();
        displayQuestion();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvQuestion = findViewById(R.id.tv_question);
        tvProgress = findViewById(R.id.tv_progress);
        rgOptions = findViewById(R.id.rg_options);
        rb1 = findViewById(R.id.rb_option1);
        rb2 = findViewById(R.id.rb_option2);
        rb3 = findViewById(R.id.rb_option3);
        rb4 = findViewById(R.id.rb_option4);
        btnNext = findViewById(R.id.btn_next);
        progressBar = findViewById(R.id.progress_bar);
        ImageView ivBack = findViewById(R.id.iv_back);

        ivBack.setOnClickListener(v -> showExitConfirmation());

        boolean isEn = prefs.isEnglish();
        tvTitle.setText(isEn ? currentSet.getNameEn() : currentSet.getNameHi());

        btnNext.setOnClickListener(v -> {
            int selectedId = rgOptions.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, isEn ? "Please select an option" : "कृपया एक विकल्प चुनें", Toast.LENGTH_SHORT).show();
                return;
            }

            int answerIndex = -1;
            if (selectedId == R.id.rb_option1) answerIndex = 0;
            else if (selectedId == R.id.rb_option2) answerIndex = 1;
            else if (selectedId == R.id.rb_option3) answerIndex = 2;
            else if (selectedId == R.id.rb_option4) answerIndex = 3;

            userAnswers.add(answerIndex);
            if (answerIndex == questions.get(currentQuestionIndex).getCorrectOptionIndex()) {
                score++;
            }

            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                displayQuestion();
            } else {
                showResults();
            }
        });
    }

    private void displayQuestion() {
        rgOptions.clearCheck();
        QuizQuestion q = questions.get(currentQuestionIndex);
        boolean isEn = prefs.isEnglish();

        tvQuestion.setText(isEn ? q.getQuestionEn() : q.getQuestionHi());
        List<String> options = isEn ? q.getOptionsEn() : q.getOptionsHi();

        rb1.setText(options.get(0));
        rb2.setText(options.get(1));
        rb3.setText(options.get(2));
        rb4.setText(options.get(3));

        tvProgress.setText(String.format("%d / %d", currentQuestionIndex + 1, questions.size()));
        progressBar.setProgress(((currentQuestionIndex + 1) * 100) / questions.size());

        if (currentQuestionIndex == questions.size() - 1) {
            btnNext.setText(isEn ? "Finish" : "समाप्त करें");
        } else {
            btnNext.setText(isEn ? "Next" : "अगला");
        }
    }

    private void showResults() {
        boolean isEn = prefs.isEnglish();
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_quiz_result, null);
        
        TextView tvStatus = dialogView.findViewById(R.id.tv_result_status);
        TextView tvScore = dialogView.findViewById(R.id.tv_result_score);
        TextView tvReviewTitle = dialogView.findViewById(R.id.tv_review_title);
        TextView tvDetails = dialogView.findViewById(R.id.tv_wrong_answers_details);
        Button btnFinish = dialogView.findViewById(R.id.btn_finish);

        // Pass criteria: 60% (3 out of 5)
        boolean passed = score >= (questions.size() * 0.6);
        
        if (passed) {
            tvStatus.setText(isEn ? "PASSED" : "उत्तीर्ण");
            tvStatus.setTextColor(getResources().getColor(R.color.india_green));
        } else {
            tvStatus.setText(isEn ? "FAILED" : "अनुत्तीर्ण");
            tvStatus.setTextColor(getResources().getColor(R.color.cat_red));
        }

        tvScore.setText((isEn ? "Score: " : "स्कोर: ") + score + " / " + questions.size());
        tvReviewTitle.setText(isEn ? "Review Answers:" : "उत्तरों की समीक्षा करें:");
        btnFinish.setText(isEn ? "Finish" : "समाप्त करें");

        StringBuilder reviewBuilder = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            QuizQuestion q = questions.get(i);
            int userAns = userAnswers.get(i);
            int correctAns = q.getCorrectOptionIndex();
            List<String> options = isEn ? q.getOptionsEn() : q.getOptionsHi();

            reviewBuilder.append("<b>Q").append(i + 1).append(":</b> ")
                    .append(isEn ? q.getQuestionEn() : q.getQuestionHi()).append("<br/>");

            if (userAns == correctAns) {
                reviewBuilder.append("<font color='#138808'>✓ Correct: ")
                        .append(options.get(correctAns)).append("</font><br/><br/>");
            } else {
                reviewBuilder.append("<font color='#EF4444'>✗ Your Answer: ")
                        .append(options.get(userAns)).append("</font><br/>")
                        .append("<font color='#138808'>✓ Correct Answer: ")
                        .append(options.get(correctAns)).append("</font><br/><br/>");
            }
        }
        tvDetails.setText(Html.fromHtml(reviewBuilder.toString()));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnFinish.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }

    private void showExitConfirmation() {
        boolean isEn = prefs.isEnglish();
        new AlertDialog.Builder(this)
                .setTitle(isEn ? "Exit Quiz?" : "परीक्षा से बाहर निकलें?")
                .setMessage(isEn ? "Your progress will be lost." : "आपकी प्रगति खो जाएगी।")
                .setPositiveButton(isEn ? "Yes" : "हाँ", (dialog, which) -> finish())
                .setNegativeButton(isEn ? "No" : "नहीं", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        showExitConfirmation();
    }
}
