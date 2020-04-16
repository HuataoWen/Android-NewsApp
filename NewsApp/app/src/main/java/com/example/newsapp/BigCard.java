package com.example.newsapp;

public class BigCard {
    private String imageSource;
    private String title;
    private String dateTag;

    public BigCard(String imageResource, String text1, String text2) {
        imageSource = imageResource;
        title = text1;
        dateTag = text2;
    }

    public void changeText1(String text) {
        title = text;
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
}
