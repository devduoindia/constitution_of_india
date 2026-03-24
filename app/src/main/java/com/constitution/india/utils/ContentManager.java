package com.constitution.india.utils;

import android.content.Context;
import android.util.Log;

import com.constitution.india.model.Article;
import com.constitution.india.model.Category;
import com.constitution.india.model.ConstitutionData;
import com.constitution.india.model.QuizSet;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * ContentManager loads the Constitution content from a single JSON asset file.
 *
 * TO UPDATE CONTENT:
 * Edit app/src/main/assets/constitution_en.json
 *
 * Each entry has both titleEn/contentEn (English) and titleHi/contentHi (Hindi).
 * The adapter picks the correct field based on the current language preference.
 */
public class ContentManager {

    private static final String TAG = "ContentManager";
    // Single source of truth — contains both EN and HI text in every entry
    private static final String DATA_FILE = "constitution_en.json";
    private static final String QUIZ_FILE = "quiz.json";

    private static ContentManager instance;
    private final Context context;
    private final AppPreferences prefs;

    private ConstitutionData data;
    private List<QuizSet> quizSets;

    private ContentManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = new AppPreferences(this.context);
        loadData();
        loadQuizData();
    }

    public static synchronized ContentManager getInstance(Context context) {
        if (instance == null) {
            instance = new ContentManager(context);
        }
        return instance;
    }

    private void loadData() {
        try {
            InputStream is = context.getAssets().open(DATA_FILE);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            data = new Gson().fromJson(json, ConstitutionData.class);
        } catch (IOException | JsonSyntaxException e) {
            Log.e(TAG, "Error loading " + DATA_FILE, e);
            data = new ConstitutionData();
        }
    }

    private void loadQuizData() {
        try {
            InputStream is = context.getAssets().open(QUIZ_FILE);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            ConstitutionData quizData = new Gson().fromJson(json, ConstitutionData.class);
            if (quizData != null) {
                quizSets = quizData.getQuizSets();
            }
        } catch (IOException | JsonSyntaxException e) {
            Log.e(TAG, "Error loading " + QUIZ_FILE, e);
            quizSets = new ArrayList<>();
        }
    }

    /** Reload from disk (call after updating the JSON file at runtime) */
    public void reload() {
        loadData();
        loadQuizData();
    }

    public List<Category> getCategories() {
        if (data == null || data.getCategories() == null) return new ArrayList<>();
        return data.getCategories();
    }

    public Category getCategoryById(String id) {
        for (Category c : getCategories()) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }

    public Article getArticleById(String articleId) {
        for (Category c : getCategories()) {
            if (c.getArticles() != null) {
                for (Article a : c.getArticles()) {
                    if (a.getId().equals(articleId)) return a;
                }
            }
        }
        return null;
    }

    public List<Article> getAllArticles() {
        List<Article> all = new ArrayList<>();
        for (Category c : getCategories()) {
            if (c.getArticles() != null) {
                for (Article a : c.getArticles()) {
                    a.setCategoryId(c.getId());
                    all.add(a);
                }
            }
        }
        return all;
    }

    public List<Article> searchArticles(String query) {
        List<Article> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return results;
        String lower = query.toLowerCase().trim();

        for (Article a : getAllArticles()) {
            // Search in both languages so user can type in either script
            String titleEn  = a.getTitleEn()   != null ? a.getTitleEn().toLowerCase()   : "";
            String titleHi  = a.getTitleHi()   != null ? a.getTitleHi().toLowerCase()   : "";
            String contentEn = a.getContentEn() != null ? a.getContentEn().toLowerCase() : "";
            String contentHi = a.getContentHi() != null ? a.getContentHi().toLowerCase() : "";
            String number    = a.getNumber()    != null ? a.getNumber().toLowerCase()    : "";

            if (titleEn.contains(lower) || titleHi.contains(lower)
                    || contentEn.contains(lower) || contentHi.contains(lower)
                    || number.contains(lower)) {
                results.add(a);
            }
        }
        return results;
    }

    public List<Article> getBookmarkedArticles() {
        List<Article> bookmarked = new ArrayList<>();
        for (Article a : getAllArticles()) {
            if (prefs.isBookmarked(a.getId())) {
                a.setBookmarked(true);
                bookmarked.add(a);
            }
        }
        return bookmarked;
    }

    public List<QuizSet> getQuizSets() {
        return quizSets != null ? quizSets : new ArrayList<>();
    }

    public String getVersion() {
        return data != null && data.getVersion() != null ? data.getVersion() : "1.0";
    }

    public String getLastUpdated() {
        return data != null && data.getLastUpdated() != null ? data.getLastUpdated() : "-";
    }
}
