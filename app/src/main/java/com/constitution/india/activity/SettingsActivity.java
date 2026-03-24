package com.constitution.india.activity;

import android.os.Bundle;
import android.speech.tts.Voice;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.constitution.india.R;
import com.constitution.india.utils.AppPreferences;
import com.constitution.india.utils.ContentManager;
import com.constitution.india.utils.TtsManager;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private AppPreferences prefs;
    private TtsManager ttsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = new AppPreferences(this);
        ttsManager = TtsManager.getInstance(this);

        ImageView ivBack = findViewById(R.id.iv_back_settings);
        ivBack.setOnClickListener(v -> onBackPressed());

        setupThemeControls();
        setupLanguageToggle();
        setupFontSizeControls();
        setupTtsControls();
        setupAbout();
    }

    private void setupThemeControls() {
        RadioGroup rgTheme = findViewById(R.id.rg_theme);
        int currentTheme = prefs.getThemeMode();

        if (currentTheme == AppPreferences.THEME_LIGHT) {
            rgTheme.check(R.id.rb_theme_light);
        } else if (currentTheme == AppPreferences.THEME_DARK) {
            rgTheme.check(R.id.rb_theme_dark);
        } else {
            rgTheme.check(R.id.rb_theme_system);
        }

        rgTheme.setOnCheckedChangeListener((group, checkedId) -> {
            int newTheme;
            if (checkedId == R.id.rb_theme_light) {
                newTheme = AppPreferences.THEME_LIGHT;
            } else if (checkedId == R.id.rb_theme_dark) {
                newTheme = AppPreferences.THEME_DARK;
            } else {
                newTheme = AppPreferences.THEME_SYSTEM;
            }

            if (newTheme != prefs.getThemeMode()) {
                prefs.setThemeMode(newTheme);
                AppCompatDelegate.setDefaultNightMode(newTheme);
            }
        });
    }

    private void setupLanguageToggle() {
        SwitchCompat switchLanguage = findViewById(R.id.switch_language);
        TextView tvLangLabel = findViewById(R.id.tv_lang_label);
        TextView tvLangDesc = findViewById(R.id.tv_lang_desc);

        boolean isEn = prefs.isEnglish();
        switchLanguage.setChecked(!isEn);
        updateLangLabel(tvLangLabel, tvLangDesc, isEn);

        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean nowEnglish = !isChecked;
            prefs.setLanguage(nowEnglish ? AppPreferences.LANG_ENGLISH : AppPreferences.LANG_HINDI);
            updateLangLabel(tvLangLabel, tvLangDesc, nowEnglish);
            setupVoiceSpinner(); // Refresh voices for new language
            Toast.makeText(this,
                    nowEnglish ? "Language set to English" : "भाषा हिंदी में बदली गई",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void updateLangLabel(TextView tvLabel, TextView tvDesc, boolean isEnglish) {
        tvLabel.setText(isEnglish ? "English" : "हिंदी");
        tvDesc.setText(isEnglish
                ? "Currently showing content in English"
                : "अभी हिंदी में सामग्री दिखाई जा रही है");
    }

    private void setupFontSizeControls() {
        TextView tvFontSize = findViewById(R.id.tv_font_size_value);
        ImageView ivFontMinus = findViewById(R.id.iv_font_minus);
        ImageView ivFontPlus = findViewById(R.id.iv_font_plus);

        tvFontSize.setText(String.valueOf(prefs.getFontSize()));

        ivFontPlus.setOnClickListener(v -> {
            int cur = prefs.getFontSize();
            if (cur < 26) {
                prefs.setFontSize(cur + 2);
                tvFontSize.setText(String.valueOf(prefs.getFontSize()));
            }
        });

        ivFontMinus.setOnClickListener(v -> {
            int cur = prefs.getFontSize();
            if (cur > 12) {
                prefs.setFontSize(cur - 2);
                tvFontSize.setText(String.valueOf(prefs.getFontSize()));
            }
        });
    }

    private void setupTtsControls() {
        SeekBar sbPitch = findViewById(R.id.sb_pitch);
        SeekBar sbRate = findViewById(R.id.sb_rate);

        sbPitch.setProgress((int) (prefs.getTtsPitch() * 10));
        sbRate.setProgress((int) (prefs.getTtsRate() * 10));

        sbPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) prefs.setTtsPitch(progress / 10.0f);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) { ttsManager.updateSettings(); }
        });

        sbRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) prefs.setTtsRate(progress / 10.0f);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) { ttsManager.updateSettings(); }
        });

        setupVoiceSpinner();
    }

    private void setupVoiceSpinner() {
        Spinner spinnerVoice = findViewById(R.id.spinner_voice);
        List<Voice> voices = ttsManager.getAvailableVoices(prefs.getLanguage());
        List<String> voiceNames = new ArrayList<>();
        
        int selectedIndex = 0;
        String savedVoice = prefs.getTtsVoice();

        for (int i = 0; i < voices.size(); i++) {
            String name = voices.get(i).getName();
            voiceNames.add("Voice " + (i + 1) + " (" + name + ")");
            if (name.equals(savedVoice)) {
                selectedIndex = i;
            }
        }

        if (voiceNames.isEmpty()) {
            voiceNames.add("Default System Voice");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, voiceNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVoice.setAdapter(adapter);
        spinnerVoice.setSelection(selectedIndex);

        spinnerVoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!voices.isEmpty()) {
                    prefs.setTtsVoice(voices.get(position).getName());
                    ttsManager.updateSettings();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupAbout() {
        ContentManager cm = ContentManager.getInstance(this);
        TextView tvVersion = findViewById(R.id.tv_content_version);
        TextView tvUpdated = findViewById(R.id.tv_content_updated);
        tvVersion.setText("Content Version: " + cm.getVersion());
        tvUpdated.setText("Last Updated: " + cm.getLastUpdated());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
