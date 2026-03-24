package com.constitution.india.model;

import java.util.List;

public class QuizQuestion {
    private String questionEn;
    private String questionHi;
    private List<String> optionsEn;
    private List<String> optionsHi;
    private int correctOptionIndex;

    public QuizQuestion() {}

    public String getQuestionEn() { return questionEn; }
    public void setQuestionEn(String questionEn) { this.questionEn = questionEn; }

    public String getQuestionHi() { return questionHi; }
    public void setQuestionHi(String questionHi) { this.questionHi = questionHi; }

    public List<String> getOptionsEn() { return optionsEn; }
    public void setOptionsEn(List<String> optionsEn) { this.optionsEn = optionsEn; }

    public List<String> getOptionsHi() { return optionsHi; }
    public void setOptionsHi(List<String> optionsHi) { this.optionsHi = optionsHi; }

    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public void setCorrectOptionIndex(int correctOptionIndex) { this.correctOptionIndex = correctOptionIndex; }
}
