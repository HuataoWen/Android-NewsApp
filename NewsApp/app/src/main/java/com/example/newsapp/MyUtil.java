package com.example.newsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class MyUtil {

    public static String GetTimeDifference(String newsTime) {
        LocalDateTime localDateTime = LocalDateTime.now();
        /*Log.v("Localtime", Integer.toString(localDateTime.getYear()) + " " +
                Integer.toString(localDateTime.getMonthValue()) + " " +
                Integer.toString(localDateTime.getDayOfMonth()) + " " +
                Integer.toString(localDateTime.getHour()) + " " +
                Integer.toString(localDateTime.getMinute()) + " " +
                Integer.toString(localDateTime.getDayOfMonth()));*/

        LocalDateTime newsDateTime = LocalDateTime.of(Integer.parseInt(newsTime.substring(0, 4)),
                Integer.parseInt(newsTime.substring(5, 7)),
                Integer.parseInt(newsTime.substring(8, 10)),
                Integer.parseInt(newsTime.substring(11, 13)),
                Integer.parseInt(newsTime.substring(14, 16)),
                Integer.parseInt(newsTime.substring(17, 19)));
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
        long diffInMonth= ChronoUnit.MONTHS.between(dateTime, dateTime2);
        long diffInDays = ChronoUnit.DAYS.between(dateTime, dateTime2);
        long diffInHours = ChronoUnit.HOURS.between(dateTime, dateTime2);
        long diffInMinutes = ChronoUnit.MINUTES.between(dateTime, dateTime2);
        long diffInSeconds = ChronoUnit.SECONDS.between(dateTime, dateTime2);
        //Log.v("Month difference:", Long.toString(diffInMonth));
        //Log.v("Day difference:", Long.toString(diffInDays));
        //Log.v("Hour difference:", Long.toString(diffInHours));
        //Log.v("Minutes difference:", Long.toString(diffInMinutes));

        if (diffInMonth != 0) return Long.toString(diffInMonth) + "M";
        else if (diffInDays != 0) return Long.toString(diffInDays) + "d";
        else if (diffInHours != 0) return Long.toString(diffInHours) + "h";
        else if (diffInMinutes != 0) return Long.toString(diffInMinutes) + "m";
        else return Long.toString(diffInSeconds) + "s";
    }

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
