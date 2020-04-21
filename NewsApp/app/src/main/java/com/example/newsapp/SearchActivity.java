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
import android.net.Uri;
import android.os.Bundle;
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
    private String keyword;
    private LinearLayout loader;
    private RequestQueue requestQueue = null;

    private Toolbar toolbar;
    private TextView toolbarTitle;

    private ArrayList<NewsCard> newsList;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private NewsCardAdapter newsCardAdapter = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Log.v("#SearchActivity -> ", "Start onCreate");

        Intent intent = getIntent();
        keyword = intent.getStringExtra("keyword");
        Log.v("#SearchActivity -> ", "Get intent keyword: " + keyword);
        requestQueue = Volley.newRequestQueue(SearchActivity.this); // Internet
        newsList = new ArrayList<>();

        loader = findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);

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

        recyclerView = findViewById(R.id.recyclerViewSearch); // Display view
        recyclerView.setHasFixedSize(true); // Keep size

        layoutManager = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        // Separator
        DividerItemDecoration horizontalDivider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        horizontalDivider.setDrawable(ContextCompat.getDrawable(SearchActivity.this, R.drawable.line_divider));
        recyclerView.addItemDecoration(horizontalDivider);

        mSwipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout); // Pull to refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNews();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        Log.v("#SearchActivity -> ", "End onCreate");

        fetchNews();
    }

    public void fetchNews() {
        newsList.clear();
        // Need to check for the first time call
        if (newsCardAdapter != null) {
            newsCardAdapter.notifyDataSetChanged();
        }
        loader.setVisibility(View.VISIBLE);
        String url = MyUtil.getBackendUrl() + "search/search?keyword=" + keyword;
        Log.v("#SearchActivity -> ", "Start fetch news" + keyword);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("#SearchActivity -> ", "Fetched news");
                    JSONArray jsonArray = response.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject newsItem = jsonArray.getJSONObject(i);
                        newsList.add(new NewsCard(newsItem, SearchActivity.this, "other"));
                    }
                    loader.setVisibility(View.INVISIBLE);
                    buildRecyclerView();
                    Log.v("#SearchActivity -> ", "Updated news");
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

    public void buildRecyclerView() {
        Log.v("#SearchActivity -> ", "Start buildRecyclerView");
        newsCardAdapter = new NewsCardAdapter(SearchActivity.this, "Big", newsList);
        recyclerView.setAdapter(newsCardAdapter);
        newsCardAdapter.setOnItemClickListener(new NewsCardAdapter.OnItemClickListener() {
            // Open article
            public void onItemClick(int position) {
                Log.v("#SearchActivity -> ", "Open article");
                Intent detailIntent = new Intent(SearchActivity.this, ArticleActivity.class);
                NewsCard newsCard = newsList.get(position);
                detailIntent.putExtra("newsId", newsCard.getNewsId());
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
                Picasso.with(SearchActivity.this).load(newsList.get(position).getNewsImageUrl()).into(dialogImage);

                TextView dialogTitle = view.findViewById(R.id.dialogTitle);
                dialogTitle.setText(newsList.get(position).getNewsTitle());

                ImageButton imageButtonShare = view.findViewById(R.id.imageButton);
                final ImageButton imageButtonDelete = view.findViewById(R.id.imageButton2);
                imageButtonDelete.setImageResource(getBookmarkIconById(newsList.get(position).getNewsId(), SearchActivity.this));
                final AlertDialog aLertDialog = dialogBuilder.create();
                aLertDialog.show();

                // Click share icon
                imageButtonShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("#SearchActivity -> ", "Clicked share icon");
                        String url = "https://twitter.com/intent/tweet?text=Check out this Link:&url=" + newsList.get(position).getNewsUrl() + "&hashtags=CSCI571NewsSearch";
                        Uri uri = Uri.parse(url);
                        Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent1);
                    }
                });

                // Click bookmark icon
                imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("#SearchActivity -> ", "Clicked bookmark icon");
                        String newsId = newsList.get(position).getNewsId();
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
                String newsId = newsList.get(position).getNewsId();
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
            news.put("newsId", newsId);
            news.put("newsUrl", newsList.get(position).getNewsUrl());
            news.put("newsTitle", newsList.get(position).getNewsTitle());
            news.put("newsImageUrl", newsList.get(position).getNewsImageUrl());
            news.put("newsPubDate", newsList.get(position).getNewsPubDate());
            news.put("newsTag", newsList.get(position).getNewsTag());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LocalStorage.insertNews(news, SearchActivity.this);
        newsList.get(position).changeImageSource(R.drawable.ic_bookmark_red_24dp);
        newsCardAdapter.notifyDataSetChanged();
        Toast.makeText(SearchActivity.this, newsList.get(position).getNewsTitle() + " was added to bookmarks", Toast.LENGTH_LONG).show();
    }

    private void removeNewsFromBookmarks(String newsId, int position) {
        Log.v("#SearchActivity -> ", "Remove news");
        LocalStorage.deleteNews(newsId, SearchActivity.this);
        newsList.get(position).changeImageSource(R.drawable.ic_bookmark_border_red_24dp);
        newsCardAdapter.notifyDataSetChanged();
        Toast.makeText(SearchActivity.this, newsList.get(position).getNewsTitle() + " was removed from bookmarks", Toast.LENGTH_LONG).show();
    }

    public static int getBookmarkIconById(String id, Context context) {
        if (LocalStorage.isInBookmark(id, context)) {
            return R.drawable.ic_bookmark_red_24dp;
        } else {
            return R.drawable.ic_bookmark_border_red_24dp;
        }
    }
}
