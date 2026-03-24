# Constitution of India — Android App

A modern, feature-rich Android application presenting the complete Constitution of India in both **English** and **Hindi**, with simplified language while preserving all legal context.

---

## 📱 Features
- 🌐 **Bilingual toggle** — Switch between English and Hindi in Settings
- 📚 **16 Categories** — Preamble, all Parts, Schedules, Key Amendments
- 📖 **67+ Articles** — Simplified plain-language explanations
- 🔍 **Full-text search** across all articles
- 🔖 **Bookmarks** — Save articles for quick access
- 🔠 **Font size control** — Adjust reading comfort
- ⬅ ➡ **Article navigation** — Prev/Next within a Part
- 🎨 **Modern UI** — Indian tri-color inspired design with smooth animations

---

## 🗂️ Project Structure
```
app/src/main/
├── assets/
│   ├── constitution_en.json   ← English content (EDIT TO UPDATE)
│   └── constitution_hi.json   ← Hindi content  (EDIT TO UPDATE)
├── java/com/constitution/india/
│   ├── activity/
│   │   ├── SplashActivity.java
│   │   ├── MainActivity.java
│   │   ├── CategoryActivity.java
│   │   ├── ArticleDetailActivity.java
│   │   ├── SearchActivity.java
│   │   ├── SettingsActivity.java
│   │   └── BookmarksActivity.java
│   ├── adapter/
│   │   ├── CategoryAdapter.java
│   │   └── ArticleAdapter.java
│   ├── model/
│   │   ├── ConstitutionData.java
│   │   ├── Category.java
│   │   └── Article.java
│   └── utils/
│       ├── AppPreferences.java
│       └── ContentManager.java
└── res/
    ├── layout/       ← All XML layouts
    ├── values/       ← colors.xml, strings.xml, themes.xml
    ├── drawable/     ← Vector icons and shapes
    └── anim/         ← Slide animations
```

---

## ✏️ How to Update Content

### To add/edit an Article:
Edit `app/src/main/assets/constitution_en.json` (and `constitution_hi.json` for Hindi).

**JSON Structure:**
```json
{
  "version": "1.1",
  "lastUpdated": "2025-01-01",
  "categories": [
    {
      "id": "unique_category_id",
      "titleEn": "Category Title",
      "titleHi": "श्रेणी शीर्षक",
      "descriptionEn": "Short description",
      "descriptionHi": "संक्षिप्त विवरण",
      "partNumber": "I",
      "articles": [
        {
          "id": "unique_article_id",
          "number": "1",
          "titleEn": "Article Title",
          "titleHi": "अनुच्छेद शीर्षक",
          "contentEn": "Full article content in English",
          "contentHi": "हिंदी में पूरी सामग्री"
        }
      ]
    }
  ]
}
```

After editing, rebuild the app. No code changes needed.

---

## 🏗️ Build Requirements
- Android Studio Arctic Fox or later
- Android SDK 34
- Java 8
- minSdkVersion 21 (Android 5.0+)

---

## 📦 Dependencies
- Material Components for Android 1.11.0
- RecyclerView 1.3.2
- CardView 1.0.0
- Gson 2.10.1
- ConstraintLayout 2.1.4

---

## 🎨 Design System
- **Primary:** #1A237E (Deep Navy Blue)
- **Accent:** #FF9933 (Saffron)
- **Background:** #F0F2F8 (Light Blue-Grey)
- Font: System Sans-Serif

---

## 📌 Content Coverage
| Category | Articles Covered |
|----------|-----------------|
| Preamble | Full text with explanation |
| Part I — Union & Territory | Arts. 1–4 |
| Part II — Citizenship | Arts. 5, 6, 9, 11 |
| Part III — Fundamental Rights | Arts. 12–35 (all key articles) |
| Part IV — Directive Principles | Arts. 38–51 |
| Part IVA — Fundamental Duties | Art. 51A (all 11 duties) |
| Part V — Union Govt | Arts. 52, 53, 54, 63, 74, 79, 83, 112 |
| Part VI — State Govts | Arts. 153, 163, 168 |
| Part XI — Centre-State Relations | Arts. 245, 246 |
| Part XII — Finance | Arts. 280, 300A |
| Part XIV — Services/UPSC | Art. 315 |
| Part XV — Elections | Arts. 324, 326 |
| Part XVIII — Emergency | Arts. 352, 356, 360 |
| Part XX — Amendment | Art. 368 |
| Schedules | 1st, 7th, 10th |
| Key Amendments | 1st, 42nd, 44th, 73rd, 86th |
