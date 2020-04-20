package com.example.newsapp;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ArticleActivity extends AppCompatActivity {
    private ScrollView articleCard;
    private Toolbar toolbar;
    private ImageView articleBookmark;
    private ImageView articleShare;
    private TextView articleToolBarTitle;
    private TextView articleContentTitle;
    private ImageView articleImage;
    private TextView articleTag;
    private TextView articleDate;
    private TextView articleDescription;
    private TextView articleUrl;
    private RequestQueue mRequestQueue = null;

    private static ProgressBar progressBar;
    private static TextView progressText;

    String newsID;
    String newsTitle;
    String newsImageUrl;
    String newsTag;
    String newsDate;
    String newsDescription;
    String newsUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Intent intent = getIntent();
        newsID = intent.getStringExtra("newsID");

        toolbar = findViewById(R.id.articleToolbar);

        articleCard = findViewById(R.id.articleCard);
        articleCard.setVisibility(View.INVISIBLE);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        showLoader();

        articleBookmark = findViewById(R.id.articleBookmark);
        articleShare = findViewById(R.id.articleShare);
        articleToolBarTitle = findViewById(R.id.articleToolBarTitle);
        articleContentTitle = findViewById(R.id.articleContentTitle);
        articleImage = findViewById(R.id.articleImage);
        articleTag = findViewById(R.id.articleTag);
        articleDate = findViewById(R.id.articleDate);
        articleDescription = findViewById(R.id.articleDescription);
        articleUrl = findViewById(R.id.articleUrl);

        String text = "View Full Article";
        mRequestQueue = Volley.newRequestQueue(ArticleActivity.this);

        SpannableString spannableString = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Log.v("articleActivity -> ", "Open article URL");
                Uri uri = Uri.parse(newsUrl);
                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent1);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.GRAY);
                ds.bgColor = Color.WHITE;
                ds.setUnderlineText(true);
            }
        };

        spannableString.setSpan(clickableSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        articleUrl.setText(spannableString);
        articleUrl.setMovementMethod(LinkMovementMethod.getInstance());

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, null);
                finish();
            }
        });

        fetchNews();

        articleBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("#ArticleActivity -> ", "Clicked bookmark icon");
                bookmarkClickHandle();
            }
        });

        articleShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("#ArticleActivity -> ", "Clicked share icon");
                String url = "https://twitter.com/intent/tweet?text=Check out this Link:&url="+newsUrl+"&hashtags=CSCI571NewsSearch";
                Uri uri = Uri.parse(url);
                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent1);
            }
        });
    }

    public void fetchNews() {
        //String url = "http://10.0.2.2:4000/mobile/getArticle?article_id=" + newsID + "&source=true";
        String url = "http://ec2-52-14-208-196.us-east-2.compute.amazonaws.com:4000/mobile/getArticle?article_id=" + newsID + "&source=true";

        Log.v("#ArticleActivity -> ", "Start fetch news");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("#ArticleActivity -> ", "Fetched news");
                    JSONArray jsonArray = response.getJSONArray("result");

                    JSONObject newsItem = jsonArray.getJSONObject(0);

                    newsID = newsItem.getString("newsID");
                    newsTitle = newsItem.getString("newsTitle");
                    newsImageUrl = newsItem.getString("newsImageUrl");
                    newsTag = newsItem.getString("newsTag");
                    newsDate = newsItem.getString("newsDate");
                    newsDescription = newsItem.getString("newsDescription");
                    newsUrl = newsItem.getString("newsUrl");

                    articleBookmark.setImageResource(getBookmarkIconById(newsID, ArticleActivity.this));
                    articleToolBarTitle.setText(newsTitle);
                    articleContentTitle.setText(newsTitle);
                    Picasso.with(ArticleActivity.this).load(newsImageUrl).fit().centerInside().into(articleImage);
                    articleTag.setText(newsTag);
                    articleDate.setText(newsDate);
                    articleDescription.setText(Html.fromHtml(newsDescription).toString());

                    hideLoader();
                    articleCard.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }

    private void bookmarkClickHandle() {
        if (LocalStorage.isInBookmark(newsID, ArticleActivity.this)) {
            Log.v("#ArticleActivity -> ", "Remove news");
            LocalStorage.deleteNews(newsID, ArticleActivity.this);
            articleBookmark.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
            Toast.makeText(ArticleActivity.this, newsTitle + " was removed from bookmarks", Toast.LENGTH_SHORT).show();
        } else {
            Log.v("#ArticleActivity -> ", "Add news");
            JSONObject newsObj = new JSONObject();
            try {
                newsObj.put("id", newsID);
                newsObj.put("url", newsUrl);
                newsObj.put("title", newsTitle);
                newsObj.put("urlToImage", newsImageUrl);
                newsObj.put("publishDate", newsDate);
                newsObj.put("tag", newsTag);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LocalStorage.insertNews(newsObj, ArticleActivity.this);
            articleBookmark.setImageResource(R.drawable.ic_bookmark_red_24dp);
            Toast.makeText(ArticleActivity.this, newsTitle + " was added to bookmarks", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoader() {
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        progressBar.setVisibility(View.INVISIBLE);
        progressText.setVisibility(View.INVISIBLE);
    }

    private int getBookmarkIconById(String id, Context context) {
        if (LocalStorage.isInBookmark(id, context)) {
            return R.drawable.ic_bookmark_red_24dp;
        } else {
            return R.drawable.ic_bookmark_border_red_24dp;
        }
    }
}
