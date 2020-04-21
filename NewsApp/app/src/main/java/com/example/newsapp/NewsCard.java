package com.example.newsapp;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
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
    private String page;

    public NewsCard(JSONObject newsItem, Context context, String page) {
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
        this.page = page;
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

    private int getBookmarkIconById() {
        if (LocalStorage.isInBookmark(newsId, context)) {
            return R.drawable.ic_bookmark_red_24dp;
        } else {
            return R.drawable.ic_bookmark_border_red_24dp;
        }
    }

    private String getPubDateTagLocal() {
        if (page.equals("bookmark")) {
            LocalDateTime newsDateTime = LocalDateTime.of(Integer.parseInt(newsPubDate.substring(0, 4)),
                    Integer.parseInt(newsPubDate.substring(5, 7)),
                    Integer.parseInt(newsPubDate.substring(8, 10)),
                    Integer.parseInt(newsPubDate.substring(11, 13)),
                    Integer.parseInt(newsPubDate.substring(14, 16)),
                    Integer.parseInt(newsPubDate.substring(17, 19)));
            String date = Integer.toString(newsDateTime.getDayOfMonth());
            String month = newsDateTime.getMonth().getDisplayName(TextStyle.SHORT, Locale.US);
            return date + " " + month + " | " + newsTag;
        } else {
            LocalDateTime localDateTime = LocalDateTime.now();
        /*Log.v("Localtime", Integer.toString(localDateTime.getYear()) + " " +
                Integer.toString(localDateTime.getMonthValue()) + " " +
                Integer.toString(localDateTime.getDayOfMonth()) + " " +
                Integer.toString(localDateTime.getHour()) + " " +
                Integer.toString(localDateTime.getMinute()) + " " +
                Integer.toString(localDateTime.getDayOfMonth()));*/

            LocalDateTime newsDateTime = LocalDateTime.of(Integer.parseInt(newsPubDate.substring(0, 4)),
                    Integer.parseInt(newsPubDate.substring(5, 7)),
                    Integer.parseInt(newsPubDate.substring(8, 10)),
                    Integer.parseInt(newsPubDate.substring(11, 13)),
                    Integer.parseInt(newsPubDate.substring(14, 16)),
                    Integer.parseInt(newsPubDate.substring(17, 19)));
        /*Log.v("Localtime", Integer.toString(newsDateTime.getYear()) + " " +
                Integer.toString(newsDateTime.getMonthValue()) + " " +
                Integer.toString(newsDateTime.getDayOfMonth()) + " " +
                Integer.toString(newsDateTime.getHour()) + " " +
                Integer.toString(newsDateTime.getMinute()) + " " +
                Integer.toString(newsDateTime.getDayOfMonth()));*/
            ZonedDateTime newsDateTimeInUTC = newsDateTime.atZone(ZoneId.of("UTC"));
            ZonedDateTime newsLocalDateTime = newsDateTimeInUTC.withZoneSameInstant(ZoneId.of("America/Los_Angeles"));
        /*Log.v("Localtime", Integer.toString(newsLocalDateTime.getYear()) + " " +
                Integer.toString(newsLocalDateTime.getMonthValue()) + " " +
                Integer.toString(newsLocalDateTime.getDayOfMonth()) + " " +
                Integer.toString(newsLocalDateTime.getHour()) + " " +
                Integer.toString(newsLocalDateTime.getMinute()) + " " +
                Integer.toString(newsLocalDateTime.getDayOfMonth()));*/

            LocalDateTime dateTime2 = LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth(), localDateTime.getHour(), localDateTime.getMinute());
            LocalDateTime dateTime = LocalDateTime.of(newsLocalDateTime.getYear(), newsLocalDateTime.getMonthValue(), newsLocalDateTime.getDayOfMonth(), newsLocalDateTime.getHour(), newsLocalDateTime.getMinute());
            long diffInMonth = ChronoUnit.MONTHS.between(dateTime, dateTime2);
            long diffInDays = ChronoUnit.DAYS.between(dateTime, dateTime2);
            long diffInHours = ChronoUnit.HOURS.between(dateTime, dateTime2);
            long diffInMinutes = ChronoUnit.MINUTES.between(dateTime, dateTime2);
            long diffInSeconds = ChronoUnit.SECONDS.between(dateTime, dateTime2);
            //Log.v("Month difference:", Long.toString(diffInMonth));
            //Log.v("Day difference:", Long.toString(diffInDays));
            //Log.v("Hour difference:", Long.toString(diffInHours));
            //Log.v("Minutes difference:", Long.toString(diffInMinutes));

            if (diffInMonth != 0) return Long.toString(diffInMonth) + "M ago | " + newsTag;
            else if (diffInDays != 0) return Long.toString(diffInDays) + "d ago | " + newsTag;
            else if (diffInHours != 0) return Long.toString(diffInHours) + "h ago | " + newsTag;
            else if (diffInMinutes != 0) return Long.toString(diffInMinutes) + "m ago | " + newsTag;
            else return Long.toString(diffInSeconds) + "s ago | " + newsTag;
        }
    }
}
