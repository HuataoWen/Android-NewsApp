package com.example.newsapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;


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
        if (displayPage.equals("bookmark")) {
            LocalDateTime newsDateTime = LocalDateTime.of(Integer.parseInt(newsPubDate.substring(0, 4)),
                    Integer.parseInt(newsPubDate.substring(5, 7)),
                    Integer.parseInt(newsPubDate.substring(8, 10)),
                    Integer.parseInt(newsPubDate.substring(11, 13)),
                    Integer.parseInt(newsPubDate.substring(14, 16)),
                    Integer.parseInt(newsPubDate.substring(17, 19)));
            String date = Integer.toString(newsDateTime.getDayOfMonth());
            String month = newsDateTime.getMonth().getDisplayName(TextStyle.SHORT, Locale.US);
            return date + " " + month;
        } else {
            LocalDateTime localDateTime = LocalDateTime.now();
            /*Log.v("-->PageHome", "Localtime: " + Integer.toString(localDateTime.getYear()) + " " +
                    Integer.toString(localDateTime.getMonthValue()) + " " +
                    Integer.toString(localDateTime.getDayOfMonth()) + " " +
                    Integer.toString(localDateTime.getHour()) + " " +
                    Integer.toString(localDateTime.getMinute()) + " " +
                    Integer.toString(localDateTime.getSecond()));*/

            LocalDateTime newsDateTime = LocalDateTime.of(Integer.parseInt(newsPubDate.substring(0, 4)),
                    Integer.parseInt(newsPubDate.substring(5, 7)),
                    Integer.parseInt(newsPubDate.substring(8, 10)),
                    Integer.parseInt(newsPubDate.substring(11, 13)),
                    Integer.parseInt(newsPubDate.substring(14, 16)),
                    Integer.parseInt(newsPubDate.substring(17, 19)));
            /*Log.v("-->PageHome", "newsDateTime: " + Integer.toString(newsDateTime.getYear()) + " " +
                    Integer.toString(newsDateTime.getMonthValue()) + " " +
                    Integer.toString(newsDateTime.getDayOfMonth()) + " " +
                    Integer.toString(newsDateTime.getHour()) + " " +
                    Integer.toString(newsDateTime.getMinute()) + " " +
                    Integer.toString(newsDateTime.getSecond()));*/

            ZonedDateTime newsDateTimeInUTC = newsDateTime.atZone(ZoneId.of("UTC"));
            ZonedDateTime newsLocalDateTime = newsDateTimeInUTC.withZoneSameInstant(ZoneId.of("America/Los_Angeles"));
            /*Log.v("-->PageHome", "newsLocalDateTime: " + Integer.toString(newsLocalDateTime.getYear()) + " " +
                    Integer.toString(newsLocalDateTime.getMonthValue()) + " " +
                    Integer.toString(newsLocalDateTime.getDayOfMonth()) + " " +
                    Integer.toString(newsLocalDateTime.getHour()) + " " +
                    Integer.toString(newsLocalDateTime.getMinute()) + " " +
                    Integer.toString(newsLocalDateTime.getSecond()));*/

            int diff = localDateTime.getMonthValue() - newsLocalDateTime.getMonthValue();
            if (diff > 0) {
                return diff + "M ago";
            }
            diff = localDateTime.getDayOfMonth() - newsLocalDateTime.getDayOfMonth();
            if (diff > 0) {
                return diff + "d ago";
            }
            diff = localDateTime.getHour() - newsLocalDateTime.getHour();
            if (diff > 0) {
                return diff + "h ago";
            }
            diff = localDateTime.getMinute() - newsLocalDateTime.getMinute();
            if (diff > 0) {
                return diff + "m ago";
            }
            diff = localDateTime.getSecond() - newsLocalDateTime.getSecond();
            if (diff > 0) {
                return diff + "s ago";
            }
        }
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
