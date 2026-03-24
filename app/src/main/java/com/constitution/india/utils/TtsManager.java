package com.constitution.india.utils;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TtsManager implements TextToSpeech.OnInitListener {
    private static final String TAG = "TtsManager";
    private static TtsManager instance;
    private TextToSpeech tts;
    private boolean isInitialized = false;
    private final Context context;
    private final AppPreferences prefs;
    private TtsProgressListener progressListener;

    public interface TtsProgressListener {
        void onStart(String utteranceId);
        void onDone(String utteranceId);
        void onError(String utteranceId);
        void onRangeStart(String utteranceId, int start, int end, int frame);
    }

    private TtsManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = new AppPreferences(this.context);
        tts = new TextToSpeech(this.context, this);
    }

    public static synchronized TtsManager getInstance(Context context) {
        if (instance == null) {
            instance = new TtsManager(context);
        }
        return instance;
    }

    public void setProgressListener(TtsProgressListener listener) {
        this.progressListener = listener;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true;
            setupProgressListener();
            updateSettings();
        } else {
            Log.e(TAG, "Initialization failed");
        }
    }

    private void setupProgressListener() {
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                if (progressListener != null) progressListener.onStart(utteranceId);
            }

            @Override
            public void onDone(String utteranceId) {
                if (progressListener != null) progressListener.onDone(utteranceId);
            }

            @Override
            public void onError(String utteranceId) {
                if (progressListener != null) progressListener.onError(utteranceId);
            }

            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
                if (progressListener != null) progressListener.onRangeStart(utteranceId, start, end, frame);
            }
        });
    }

    public void updateSettings() {
        if (!isInitialized) return;

        tts.setPitch(prefs.getTtsPitch());
        tts.setSpeechRate(prefs.getTtsRate());

        String voiceName = prefs.getTtsVoice();
        if (!voiceName.isEmpty()) {
            Set<Voice> voices = tts.getVoices();
            if (voices != null) {
                for (Voice v : voices) {
                    if (v.getName().equals(voiceName)) {
                        tts.setVoice(v);
                        break;
                    }
                }
            }
        }
    }

    public void speak(String text, String lang, String utteranceId) {
        if (!isInitialized) return;

        updateSettings();
        tts.setLanguage(new Locale(lang));
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    public void stop() {
        if (tts != null) {
            tts.stop();
        }
    }

    public List<Voice> getAvailableVoices(String lang) {
        List<Voice> filteredVoices = new ArrayList<>();
        if (!isInitialized) return filteredVoices;

        Set<Voice> allVoices = tts.getVoices();
        if (allVoices != null) {
            for (Voice v : allVoices) {
                if (v.getLocale().getLanguage().equals(lang)) {
                    filteredVoices.add(v);
                }
            }
        }
        return filteredVoices;
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        instance = null;
    }
}
