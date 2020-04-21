package com.example.newsapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.content.Context.MODE_PRIVATE;

public class LocalStorage {
    private static final String FILE_NAME = "newsBookmarks2.txt";

    public static void saveNews(JSONArray newsList, Context context) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(FILE_NAME, MODE_PRIVATE);
            String news = newsList.toString();
            fos.write(news.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void insertNews(JSONObject news, Context context) {
        JSONArray newsList = getNews(context);
        JSONObject tmp = null;
        for (int i = 0; i < newsList.length(); i++) {
            try {
                tmp = newsList.getJSONObject(i);
                if (news.getString("newsId").equals(tmp.getString("newsId"))) {
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        newsList.put(news);
        saveNews(newsList, context);
    }

    public static void deleteNews(String id, Context context) {
        JSONArray oldNewsList = getNews(context);
        JSONArray newNewsList = new JSONArray();

        for (int i = 0; i < oldNewsList.length(); i++) {
            try {
                JSONObject tmp = new JSONObject();
                tmp = oldNewsList.getJSONObject(i);
                if (id.equals(tmp.getString("newsId"))) {
                    continue;
                }
                newNewsList.put(tmp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        saveNews(newNewsList, context);
    }

    public static JSONArray getNews(Context context) {
        FileInputStream fis = null;
        JSONArray result = new JSONArray();

        try {
            fis = context.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader((fis));
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text);
            }

            result = new JSONArray(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public static boolean isInBookmark(String id, Context context) {
        JSONArray oldNewsList = getNews(context);

        for (int i = 0; i < oldNewsList.length(); i++) {
            try {
                JSONObject tmp = new JSONObject();
                tmp = oldNewsList.getJSONObject(i);
                if (id.equals(tmp.getString("newsId"))) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
