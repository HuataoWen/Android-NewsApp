package com.example.newsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class util {

    public static void printLocalStorage(JSONArray newsList) {
        Log.v("info", "--------------------------------");
        for (int i = 0; i < newsList.length(); i++) {
            JSONObject news = null;
            try {
                news = newsList.getJSONObject(i);
                Log.v("id", news.getString("id"));
                Log.v("url", news.getString("url"));
                Log.v("title", news.getString("title"));
                Log.v("urlToImage", news.getString("urlToImage"));
                Log.v("publishDate", news.getString("publishDate"));
                Log.v("tag", news.getString("tag"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.v("info", "--------------------------------");
    }
}
