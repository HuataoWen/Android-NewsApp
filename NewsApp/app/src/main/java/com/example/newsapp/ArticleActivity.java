package com.example.newsapp;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

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
    private RequestQueue requestQueue = null;
    private static ProgressBar progressBar;
    private static TextView progressText;

    String newsId;
    String newsTitle;
    String newsImageUrl;
    String newsTag;
    String newsPubDate;
    String newsDescription;
    String newsUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        // Init
        Log.v("-->ArticleActivity", "Start onCreate");

        // Obtain intent
        Intent intent = getIntent();
        newsId = intent.getStringExtra("newsId");
        Log.v("-->ArticleActivity", "Get intent newsId: " + newsId);
        requestQueue = Volley.newRequestQueue(ArticleActivity.this);
        initView();
        createClickSpan();
        getArticle();
        setClickListener();

        Log.v("-->ArticleActivity", "End onCreate");
    }

    private void initView() {
        Log.v("-->ArticleActivity", "Init view");
        toolbar = findViewById(R.id.articleToolbar);
        articleBookmark = findViewById(R.id.articleBookmark);
        articleShare = findViewById(R.id.articleShare);
        articleCard = findViewById(R.id.articleCard);
        articleCard.setVisibility(View.INVISIBLE);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        showLoader();
        articleToolBarTitle = findViewById(R.id.articleToolBarTitle);
        articleContentTitle = findViewById(R.id.articleContentTitle);
        articleImage = findViewById(R.id.articleImage);
        articleTag = findViewById(R.id.articleTag);
        articleDate = findViewById(R.id.articleDate);
        articleDescription = findViewById(R.id.articleDescription);
        articleUrl = findViewById(R.id.articleUrl);
    }

    private void createClickSpan() {
        Log.v("-->ArticleActivity", "Create clickSpan");
        String text = "View Full Article";
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Log.v("-->ArticleActivity", "Open article URL");
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
    }

    private void setClickListener() {
        Log.v("-->ArticleActivity", "Set clickListener");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, null);
                finish();
            }
        });

        articleBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("-->ArticleActivity", "Clicked bookmark icon");
                bookmarkClickHandle();
            }
        });

        articleShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("-->ArticleActivity", "Clicked share icon");
                Intent intent = MyUtil.getShareIntent(newsUrl);
                startActivity(intent);
            }
        });
    }

    private void getArticle() {
        Log.v("-->ArticleActivity", "Start fetch news");
        String url = MyUtil.getBackendUrl() + "getArticle?article_id=" + newsId + "&source=true";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("-->ArticleActivity", "Fetched news");
                    JSONArray jsonArray = response.getJSONArray("result");
                    JSONObject newsItem = jsonArray.getJSONObject(0);
                    newsId = newsItem.getString("newsId");
                    newsTitle = newsItem.getString("newsTitle");
                    newsImageUrl = newsItem.getString("newsImageUrl");
                    newsTag = newsItem.getString("newsTag");
                    newsPubDate = newsItem.getString("newsPubDate");
                    newsDescription = newsItem.getString("newsDescription");
                    newsUrl = newsItem.getString("newsUrl");

                    articleToolBarTitle.setText(newsTitle);
                    articleContentTitle.setText(newsTitle);
                    articleBookmark.setImageResource(getBookmarkIconById(newsId, ArticleActivity.this));
                    Picasso.with(ArticleActivity.this).load(newsImageUrl).fit().centerInside().into(articleImage);
                    articleTag.setText(newsTag);
                    articleDate.setText(MyUtil.getPubDate(newsPubDate));
                    articleDescription.setText(HtmlCompat.fromHtml(newsDescription, HtmlCompat.FROM_HTML_MODE_LEGACY));

                    hideLoader();
                    articleCard.setVisibility(View.VISIBLE);
                    Log.v("-->ArticleActivity", "Updated view");

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

        requestQueue.add(request);
    }

    private void bookmarkClickHandle() {
        if (LocalStorage.isInBookmark(newsId, ArticleActivity.this)) {
            Log.v("-->ArticleActivity", "Remove news");
            LocalStorage.deleteNews(newsId, ArticleActivity.this);
            articleBookmark.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
            Toast.makeText(ArticleActivity.this, '"' + newsTitle + '"' + " was removed from bookmarks", Toast.LENGTH_SHORT).show();
        } else {
            Log.v("-->ArticleActivity", "Add news");
            JSONObject newsObj = new JSONObject();
            try {
                newsObj.put("newsId", newsId);
                newsObj.put("newsUrl", newsUrl);
                newsObj.put("newsTitle", newsTitle);
                newsObj.put("newsImageUrl", newsImageUrl);
                newsObj.put("newsPubDate", newsPubDate);
                newsObj.put("newsTag", newsTag);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LocalStorage.insertNews(newsObj, ArticleActivity.this);
            articleBookmark.setImageResource(R.drawable.ic_bookmark_red_24dp);
            Toast.makeText(ArticleActivity.this, '"' + newsTitle + '"' + " was added to bookmarks", Toast.LENGTH_SHORT).show();
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
