package com.constitution.india.model;

import java.util.List;

public class ConstitutionData {
    private String version;
    private String lastUpdated;
    private List<Category> categories;
    private List<QuizSet> quizSets;

    public ConstitutionData() {}

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    public List<Category> getCategories() { return categories; }
    public void setCategories(List<Category> categories) { this.categories = categories; }

    public List<QuizSet> getQuizSets() { return quizSets; }
    public void setQuizSets(List<QuizSet> quizSets) { this.quizSets = quizSets; }
}
