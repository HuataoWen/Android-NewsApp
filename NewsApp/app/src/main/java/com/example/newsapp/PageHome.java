package com.example.newsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import static android.app.Activity.RESULT_OK;

public class PageHome extends Fragment {
    private ArrayList<NewsCard> newsList;
    private RequestQueue mRequestQueue = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private BigCardAdapter bigCardAdapter = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.page_home, container, false);

        newsList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(getActivity()); // Internet
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_items); // Pull to efresh
        recyclerView = view.findViewById(R.id.recyclerView); // Display view

        // Get news
        fetchNews();

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

        return view;
    }

    private void fetchNews() {
        newsList.clear();
        if (bigCardAdapter != null){
            bigCardAdapter.notifyDataSetChanged();
        }

        MainActivity.showLoader();

        String url = "http://10.0.2.2:4000/mobile";

        Log.v("#PageHome -> ", "Start fetch news");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("#PageHome -> ", "Fetched news");
                    JSONArray jsonArray = response.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject newsItem = jsonArray.getJSONObject(i);
                        String newsId = newsItem.getString("id");
                        String newsUrl = newsItem.getString("url");
                        String newsTitle = newsItem.getString("title");
                        String newsImageUrl = newsItem.getString("urlToImage");
                        String newsTag = newsItem.getString("tag");
                        newsList.add(new NewsCard(newsId, newsUrl, newsImageUrl, newsTitle, newsTag, "24m ago | " + newsTag, "Apr 4", getBookmarkIconById(newsId, getActivity())));
                    }
                    MainActivity.hideLoader();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.v("#PageHome -> ", "Back from article activity");
            fetchNews();
        }
    }

    public void buildRecyclerView() {
        Log.v("#PageHome -> ", "Start buildRecyclerView");
        recyclerView.setHasFixedSize(true); // Keep size

        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        bigCardAdapter = new BigCardAdapter(getActivity(), "Big", newsList);
        recyclerView.setLayoutManager(layoutManager);
        // Separator
        DividerItemDecoration horizontalDivider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        horizontalDivider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.line_divider));
        recyclerView.addItemDecoration(horizontalDivider);
        recyclerView.setAdapter(bigCardAdapter);

        bigCardAdapter.setOnItemClickListener(new BigCardAdapter.OnItemClickListener() {
            // Open article
            public void onItemClick(int position) {
                Log.v("#PageHome -> ", "Open article");
                Intent detailIntent = new Intent(getActivity(), ArticleActivity.class);
                NewsCard newsCard = newsList.get(position);
                detailIntent.putExtra("newsID", newsCard.getID());
                startActivityForResult(detailIntent, 1);
            }

            // Expand dialog
            public void onItemLongClick(final int position) {
                Log.v("#PageHome -> ", "Expand dialog");
                // Init dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.layout_dialog, null);
                dialogBuilder.setView(view);

                ImageView dialogImage = ((ImageView) view.findViewById(R.id.dialogImage));
                Picasso.with(getActivity()).load(newsList.get(position).getImageResource()).into(dialogImage);

                TextView dialogTitle = view.findViewById(R.id.dialogTitle);
                dialogTitle.setText(newsList.get(position).getTitle());

                ImageButton imageButtonShare = view.findViewById(R.id.imageButton);
                final ImageButton imageButtonDelete = view.findViewById(R.id.imageButton2);
                imageButtonDelete.setImageResource(getBookmarkIconById(newsList.get(position).getID(), getActivity()));
                final AlertDialog aLertDialog = dialogBuilder.create();
                aLertDialog.show();

                // Click share icon
                imageButtonShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("#PageHome -> ", "Clicked share icon");
                        Toast.makeText(getActivity(), "TODO:: Share article on Twitter", Toast.LENGTH_SHORT).show();
                    }
                });

                // Click bookmark icon
                imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("#PageHome -> ", "Clicked bookmark icon");
                        String newsId = newsList.get(position).getID();
                        if (LocalStorage.isInBookmark(newsId, getActivity())) {

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
                    if (LocalStorage.isInBookmark(newsId, getActivity())) {
                        Log.v("#PageHome -> ", "Remove news(viewpager)");
                        removeNewsFromBookmarks(newsId, position);
                    } else {
                        Log.v("#PageHome -> ", "Add news(viewpager)");
                        addNewsToBookmarks(newsId, position);
                    }
                }
            }
        });
    }

    private void addNewsToBookmarks(String newsId, int position) {
        Log.v("#PageHome -> ", "Add news");
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
        LocalStorage.insertNews(news, getActivity());
        newsList.get(position).changeImageSource(R.drawable.ic_bookmark_red_24dp);
        bigCardAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), newsList.get(position).getTitle() + " was added to bookmarks", Toast.LENGTH_LONG).show();
    }

    private void removeNewsFromBookmarks(String newsId, int position){
        Log.v("#PageHome -> ", "Remove news");
        LocalStorage.deleteNews(newsId, getActivity());
        newsList.get(position).changeImageSource(R.drawable.ic_bookmark_border_red_24dp);
        bigCardAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), newsList.get(position).getTitle() + " was removed from bookmarks", Toast.LENGTH_LONG).show();
    }

    public static int getBookmarkIconById(String id, Context context) {
        if (LocalStorage.isInBookmark(id, context)) {
            return R.drawable.ic_bookmark_red_24dp;
        } else {
            return R.drawable.ic_bookmark_border_red_24dp;
        }
    }
}
