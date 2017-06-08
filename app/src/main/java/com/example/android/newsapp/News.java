package com.example.android.newsapp;

/**
 * Created by Edvinas on 08/06/2017.
 */

public class News {
    private String sectionName;
    private String articleName;
    private String url;

    public News(String sectionName, String articleName, String url) {
        this.sectionName = sectionName;
        this.articleName = articleName;
        this.url = url;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
