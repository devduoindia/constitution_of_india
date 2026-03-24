package com.constitution.india.model;

import java.util.List;

public class Category {
    private String id;
    private String titleEn;
    private String titleHi;
    private String descriptionEn;
    private String descriptionHi;
    private String partNumber;
    private String iconName;
    private List<Article> articles;

    public Category() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitleEn() { return titleEn; }
    public void setTitleEn(String titleEn) { this.titleEn = titleEn; }

    public String getTitleHi() { return titleHi; }
    public void setTitleHi(String titleHi) { this.titleHi = titleHi; }

    public String getDescriptionEn() { return descriptionEn; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn; }

    public String getDescriptionHi() { return descriptionHi; }
    public void setDescriptionHi(String descriptionHi) { this.descriptionHi = descriptionHi; }

    public String getPartNumber() { return partNumber; }
    public void setPartNumber(String partNumber) { this.partNumber = partNumber; }

    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }

    public List<Article> getArticles() { return articles; }
    public void setArticles(List<Article> articles) { this.articles = articles; }

    public int getArticleCount() {
        return articles != null ? articles.size() : 0;
    }
}
