package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class MyUtil {
    public static String getPubDate(String newsTime) {
        LocalDateTime newsDateTime = LocalDateTime.of(Integer.parseInt(newsTime.substring(0, 4)),
                Integer.parseInt(newsTime.substring(5, 7)),
                Integer.parseInt(newsTime.substring(8, 10)),
                Integer.parseInt(newsTime.substring(11, 13)),
                Integer.parseInt(newsTime.substring(14, 16)),
                Integer.parseInt(newsTime.substring(17, 19)));
        String date = Integer.toString(newsDateTime.getDayOfMonth());
        String month = newsDateTime.getMonth().getDisplayName(TextStyle.SHORT, Locale.US);
        String year = Integer.toString(newsDateTime.getYear());
        return date + " " + month + " " + year;
    }

    public static Intent getShareIntent(String url) {
        String shareUrl = "https://twitter.com/intent/tweet?text=Check out this Link:&url=" + url + "&hashtags=CSCI571NewsSearch";
        Uri uri = Uri.parse(shareUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        return intent;
    }

    public static int getBookmarkIconById(String id, Context context) {
        if (LocalStorage.isInBookmark(id, context)) {
            return R.drawable.ic_bookmark_red_24dp;
        } else {
            return R.drawable.ic_bookmark_border_red_24dp;
        }
    }

    public static String getBackendUrl() {
        //return "http://10.0.2.2:4000/mobile/";
        return "http://ec2-52-14-208-196.us-east-2.compute.amazonaws.com:4000/mobile/";
    }

    public static void printLocalStorage(JSONArray newsList) {
        Log.v("info", "--------------------------------");
        for (int i = 0; i < newsList.length(); i++) {
            JSONObject news = null;
            try {
                news = newsList.getJSONObject(i);
                Log.v("newsId", news.getString("newsId"));
                Log.v("newsUrl", news.getString("newsUrl"));
                Log.v("newsTitle", news.getString("newsTitle"));
                Log.v("newsImageUrl", news.getString("newsImageUrl"));
                Log.v("newsPubDate", news.getString("newsPubDate"));
                Log.v("newsTag", news.getString("newsTag"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.v("info", "--------------------------------");
    }
}
