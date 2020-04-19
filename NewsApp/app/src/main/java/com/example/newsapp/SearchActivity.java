package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    String keyword;

    LinearLayout loader;

    private RequestQueue mRequestQueue = null;

    private Toolbar toolbar;
    TextView toolbarTitle;

    String newsID;
    String newsTitle;
    String newsImageUrl;
    String newsTag;
    String newsDate;
    String newsDescription;
    String newsUrl;

    private ArrayList<NewsCard> newsList;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private BigCardAdapter bigCardAdapter = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        loader = findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);

        mRequestQueue = Volley.newRequestQueue(SearchActivity.this); // Internet

        Intent intent = getIntent();
        keyword = intent.getStringExtra("newsID");

        toolbar = findViewById(R.id.articleToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, null);
                finish();
            }
        });

        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Search Results for " + keyword);

        newsList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView); // Display view

        mSwipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout); // Pull to efresh

        // Pull refresh function
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mRequestQueue != null) {
                    fetchNews();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        fetchNews();
    }

    public void fetchNews() {
        newsList.clear();
        // Need to check for the first time call
        if (bigCardAdapter != null){
            bigCardAdapter.notifyDataSetChanged();
        }
        loader.setVisibility(View.VISIBLE);

        //String url = "http://10.0.2.2:4000/mobile/search/search?keyword=" + keyword;
        String url = "http://ec2-52-14-208-196.us-east-2.compute.amazonaws.com:4000/mobile/search/search?keyword=" + keyword;

        Log.v("#SearchActivity -> ", "Start fetch news");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("#SearchActivity -> ", "Fetched news");
                    JSONArray jsonArray = response.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject newsItem = jsonArray.getJSONObject(i);
                        newsID = newsItem.getString("newsId");
                        newsTitle = newsItem.getString("newsTitle");
                        newsImageUrl = newsItem.getString("newsImageUrl");
                        newsTag = newsItem.getString("newsTag");
                        newsDate = newsItem.getString("newsPubDate");
                        newsUrl = newsItem.getString("newsUrl");
                        String timeDiff = MyUtil.GetTimeDifference(newsDate);
                        newsList.add(new NewsCard(newsID, newsUrl, newsImageUrl, newsTitle, newsTag, timeDiff + " ago | " + newsTag, "Apr 4", getBookmarkIconById(newsID, SearchActivity.this)));
                    }
                    loader.setVisibility(View.INVISIBLE);
                    buildRecyclerView();
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

    public void buildRecyclerView() {
        Log.v("#SearchActivity -> ", "Start buildRecyclerView");
        recyclerView.setHasFixedSize(true); // Keep size

        layoutManager = new LinearLayoutManager(SearchActivity.this);
        bigCardAdapter = new BigCardAdapter(SearchActivity.this, "Big", newsList);
        recyclerView.setLayoutManager(layoutManager);
        // Separator
        DividerItemDecoration horizontalDivider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        horizontalDivider.setDrawable(ContextCompat.getDrawable(SearchActivity.this, R.drawable.line_divider));
        recyclerView.addItemDecoration(horizontalDivider);
        recyclerView.setAdapter(bigCardAdapter);

        bigCardAdapter.setOnItemClickListener(new BigCardAdapter.OnItemClickListener() {
            // Open article
            public void onItemClick(int position) {
                Log.v("#SearchActivity -> ", "Open article");
                Intent detailIntent = new Intent(SearchActivity.this, ArticleActivity.class);
                NewsCard newsCard = newsList.get(position);
                detailIntent.putExtra("newsID", newsCard.getID());
                startActivityForResult(detailIntent, 1);
            }

            // Expand dialog
            public void onItemLongClick(final int position) {
                Log.v("#SearchActivity -> ", "Expand dialog");
                // Init dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SearchActivity.this);
                LayoutInflater inflater = SearchActivity.this.getLayoutInflater();
                View view = inflater.inflate(R.layout.layout_dialog, null);
                dialogBuilder.setView(view);

                ImageView dialogImage = ((ImageView) view.findViewById(R.id.dialogImage));
                Picasso.with(SearchActivity.this).load(newsList.get(position).getImageResource()).into(dialogImage);

                TextView dialogTitle = view.findViewById(R.id.dialogTitle);
                dialogTitle.setText(newsList.get(position).getTitle());

                ImageButton imageButtonShare = view.findViewById(R.id.imageButton);
                final ImageButton imageButtonDelete = view.findViewById(R.id.imageButton2);
                imageButtonDelete.setImageResource(getBookmarkIconById(newsList.get(position).getID(), SearchActivity.this));
                final AlertDialog aLertDialog = dialogBuilder.create();
                aLertDialog.show();

                // Click share icon
                imageButtonShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("#SearchActivity -> ", "Clicked share icon");
                        Toast.makeText(SearchActivity.this, "TODO:: Share article on Twitter", Toast.LENGTH_SHORT).show();
                    }
                });

                // Click bookmark icon
                imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("#SearchActivity -> ", "Clicked bookmark icon");
                        String newsId = newsList.get(position).getID();
                        if (LocalStorage.isInBookmark(newsId, SearchActivity.this)) {

                            imageButtonDelete.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
                            removeNewsFromBookmarks(newsId, position);
                        } else {

                            imageButtonDelete.setImageResource(R.drawable.ic_bookmark_red_24dp);
                            addNewsToBookmarks(newsId, position);
                        }
                    }
                });
            }

            // Click bookmark icon on viewpager
            public void onBookmarkClick(int position) {
                String newsId = newsList.get(position).getID();
                if (position != RecyclerView.NO_POSITION) {
                    if (LocalStorage.isInBookmark(newsId, SearchActivity.this)) {
                        Log.v("#SearchActivity -> ", "Remove news(viewpager)");
                        removeNewsFromBookmarks(newsId, position);
                    } else {
                        Log.v("#SearchActivity -> ", "Add news(viewpager)");
                        addNewsToBookmarks(newsId, position);
                    }
                }
            }
        });
    }

    private void addNewsToBookmarks(String newsId, int position) {
        Log.v("#SearchActivity -> ", "Add news");
        JSONObject news = new JSONObject();
        try {
            news.put("id", newsId);
            news.put("url", newsList.get(position).getIUrl());
            news.put("title", newsList.get(position).getTitle());
            news.put("urlToImage", newsList.get(position).getImageResource());
            news.put("publishDate", newsList.get(position).getPublishDate());
            news.put("tag", newsList.get(position).getTag());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LocalStorage.insertNews(news, SearchActivity.this);
        newsList.get(position).changeImageSource(R.drawable.ic_bookmark_red_24dp);
        bigCardAdapter.notifyDataSetChanged();
        Toast.makeText(SearchActivity.this, newsList.get(position).getTitle() + " was added to bookmarks", Toast.LENGTH_LONG).show();
    }

    private void removeNewsFromBookmarks(String newsId, int position) {
        Log.v("#SearchActivity -> ", "Remove news");
        LocalStorage.deleteNews(newsId, SearchActivity.this);
        newsList.get(position).changeImageSource(R.drawable.ic_bookmark_border_red_24dp);
        bigCardAdapter.notifyDataSetChanged();
        Toast.makeText(SearchActivity.this, newsList.get(position).getTitle() + " was removed from bookmarks", Toast.LENGTH_LONG).show();
    }

    public static int getBookmarkIconById(String id, Context context) {
        if (LocalStorage.isInBookmark(id, context)) {
            return R.drawable.ic_bookmark_red_24dp;
        } else {
            return R.drawable.ic_bookmark_border_red_24dp;
        }
    }
}
