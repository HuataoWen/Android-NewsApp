package com.example.newsapp;

public class NewsCard {
    private String id;
    private String url;
    private String imageSource;
    private String title;
    private String tag;
    private String dateTag;
    private String publishDate;
    private int bookmarkSrc;

    public NewsCard(String id, String url, String imageSource, String title, String tag, String dateTag, String publishDate, int bookmarkSrc) {
        this.id = id;
        this.url = url;
        this.imageSource = imageSource;
        this.publishDate = publishDate;
        this.title = title;
        this.tag = tag;
        this.dateTag = dateTag;
        this.bookmarkSrc = bookmarkSrc;
    }

    public void changeText1(String text) {
        title = text;
    }

    public void changeImageSource(int src) {
        bookmarkSrc = src;
    }

    public String getID() {
        return id;
    }

    public String getIUrl() {
        return url;
    }

    public String getImageResource() {
        return imageSource;
    }

    public String getTitle() {
        return title;
    }

    public String getDateTag() {
        return dateTag;
    }

    public String getTag() {
        return tag;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public int getBookmark() {
        return bookmarkSrc;
    }
}
