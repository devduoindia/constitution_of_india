package com.constitution.india.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AppPreferences {
    private static final String PREF_NAME = "constitution_prefs";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_BOOKMARKS = "bookmarks";
    private static final String KEY_FONT_SIZE = "font_size";
    private static final String KEY_THEME = "theme_mode"; // Changed from KEY_DARK_MODE
    private static final String KEY_TTS_PITCH = "tts_pitch";
    private static final String KEY_TTS_RATE = "tts_rate";
    private static final String KEY_TTS_VOICE = "tts_voice";
    private static final String KEY_NOTES = "notes";
    private static final String KEY_HIGHLIGHTS = "highlights";
    private static final String KEY_UNDERLINES = "underlines";

    public static final String LANG_ENGLISH = "en";
    public static final String LANG_HINDI = "hi";

    public static final int THEME_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    public static final int THEME_LIGHT = AppCompatDelegate.MODE_NIGHT_NO;
    public static final int THEME_DARK = AppCompatDelegate.MODE_NIGHT_YES;

    private final SharedPreferences prefs;
    private final Gson gson;

    public AppPreferences(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, LANG_ENGLISH);
    }

    public void setLanguage(String language) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply();
    }

    public boolean isEnglish() {
        return LANG_HINDI.equals(getLanguage()) ? false : true;
    }

    public Set<String> getBookmarks() {
        return prefs.getStringSet(KEY_BOOKMARKS, new HashSet<>());
    }

    public void addBookmark(String articleId) {
        Set<String> bookmarks = new HashSet<>(getBookmarks());
        bookmarks.add(articleId);
        prefs.edit().putStringSet(KEY_BOOKMARKS, bookmarks).apply();
    }

    public void removeBookmark(String articleId) {
        Set<String> bookmarks = new HashSet<>(getBookmarks());
        bookmarks.remove(articleId);
        prefs.edit().putStringSet(KEY_BOOKMARKS, bookmarks).apply();
    }

    public boolean isBookmarked(String articleId) {
        return getBookmarks().contains(articleId);
    }

    public int getFontSize() {
        return prefs.getInt(KEY_FONT_SIZE, 16);
    }

    public void setFontSize(int size) {
        prefs.edit().putInt(KEY_FONT_SIZE, size).apply();
    }

    public int getThemeMode() {
        return prefs.getInt(KEY_THEME, THEME_SYSTEM);
    }

    public void setThemeMode(int themeMode) {
        prefs.edit().putInt(KEY_THEME, themeMode).apply();
    }

    public float getTtsPitch() {
        return prefs.getFloat(KEY_TTS_PITCH, 1.0f);
    }

    public void setTtsPitch(float pitch) {
        prefs.edit().putFloat(KEY_TTS_PITCH, pitch).apply();
    }

    public float getTtsRate() {
        return prefs.getFloat(KEY_TTS_RATE, 1.0f);
    }

    public void setTtsRate(float rate) {
        prefs.edit().putFloat(KEY_TTS_RATE, rate).apply();
    }

    public String getTtsVoice() {
        return prefs.getString(KEY_TTS_VOICE, "");
    }

    public void setTtsVoice(String voiceName) {
        prefs.edit().putString(KEY_TTS_VOICE, voiceName).apply();
    }

    public String getNote(String articleId) {
        Map<String, String> notes = getNotesMap();
        return notes.get(articleId);
    }

    public void saveNote(String articleId, String note) {
        Map<String, String> notes = getNotesMap();
        if (note == null || note.isEmpty()) {
            notes.remove(articleId);
        } else {
            notes.put(articleId, note);
        }
        prefs.edit().putString(KEY_NOTES, gson.toJson(notes)).apply();
    }

    private Map<String, String> getNotesMap() {
        String json = prefs.getString(KEY_NOTES, "");
        if (json.isEmpty()) return new HashMap<>();
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public Set<String> getHighlights(String articleId) {
        Map<String, Set<String>> highlights = getHighlightsMap();
        Set<String> res = highlights.get(articleId);
        return res != null ? res : new HashSet<>();
    }

    public void saveHighlights(String articleId, Set<String> articleHighlights) {
        Map<String, Set<String>> highlights = getHighlightsMap();
        if (articleHighlights == null || articleHighlights.isEmpty()) {
            highlights.remove(articleId);
        } else {
            highlights.put(articleId, articleHighlights);
        }
        prefs.edit().putString(KEY_HIGHLIGHTS, gson.toJson(highlights)).apply();
    }

    private Map<String, Set<String>> getHighlightsMap() {
        String json = prefs.getString(KEY_HIGHLIGHTS, "");
        if (json.isEmpty()) return new HashMap<>();
        Type type = new TypeToken<HashMap<String, Set<String>>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public Set<String> getUnderlines(String articleId) {
        Map<String, Set<String>> underlines = getUnderlinesMap();
        Set<String> res = underlines.get(articleId);
        return res != null ? res : new HashSet<>();
    }

    public void saveUnderlines(String articleId, Set<String> articleUnderlines) {
        Map<String, Set<String>> underlines = getUnderlinesMap();
        if (articleUnderlines == null || articleUnderlines.isEmpty()) {
            underlines.remove(articleId);
        } else {
            underlines.put(articleId, articleUnderlines);
        }
        prefs.edit().putString(KEY_UNDERLINES, gson.toJson(underlines)).apply();
    }

    private Map<String, Set<String>> getUnderlinesMap() {
        String json = prefs.getString(KEY_UNDERLINES, "");
        if (json.isEmpty()) return new HashMap<>();
        Type type = new TypeToken<HashMap<String, Set<String>>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
