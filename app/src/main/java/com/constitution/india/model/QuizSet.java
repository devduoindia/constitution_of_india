package com.constitution.india.model;

import java.util.List;

public class QuizSet {
    private String id;
    private String nameEn;
    private String nameHi;
    private List<QuizQuestion> questions;

    public QuizSet() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getNameHi() { return nameHi; }
    public void setNameHi(String nameHi) { this.nameHi = nameHi; }

    public List<QuizQuestion> getQuestions() { return questions; }
    public void setQuestions(List<QuizQuestion> questions) { this.questions = questions; }
}
