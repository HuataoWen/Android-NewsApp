package com.example.newsapp;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;


public class NewsCard {
    private Context context;
    private String newsId;
    private String newsImageUrl;
    private String newsTitle;
    private String newsPubDate;
    private String newsTag;
    private String newsUrl;
    private String newsPubDateTag;
    private int newsBookmarkSrc;
    private String displayPage;

    public NewsCard(JSONObject newsItem, Context context, String displayPage) {
        this.context = context;

        try {
            this.newsId = newsItem.getString("newsId");
            this.newsImageUrl = newsItem.getString("newsImageUrl");
            this.newsTitle = newsItem.getString("newsTitle");
            this.newsPubDate = newsItem.getString("newsPubDate");
            this.newsTag = newsItem.getString("newsTag");
            this.newsUrl = newsItem.getString("newsUrl");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.newsBookmarkSrc = getBookmarkIconById();
        this.displayPage = displayPage;
        newsPubDateTag = getPubDateTagLocal();
    }

    public void changeImageSource(int src) {
        newsBookmarkSrc = src;
    }

    public String getNewsId() {
        return newsId;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public String getNewsImageUrl() {
        return newsImageUrl;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsPubDateTag() {
        return newsPubDateTag;
    }

    public String getNewsTag() {
        return newsTag;
    }

    public String getNewsPubDate() {
        return newsPubDate;
    }

    public int getNewsBookmarkSrc() {
        return newsBookmarkSrc;
    }

    private String getPubDateTagLocal() {
        return "3s ago | " + newsTag;
    }

    private int getBookmarkIconById() {
        if (LocalStorage.isInBookmark(newsId, context)) {
            return R.drawable.ic_bookmark_red_24dp;
        } else {
            return R.drawable.ic_bookmark_border_red_24dp;
        }
    }
}
