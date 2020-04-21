package com.example.newsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class TabFragment extends Fragment {
    private ArrayList<NewsCard> newsList;
    private String tabSection;
    private NewsCardAdapter newsCardAdapter = null;
    private RecyclerView.LayoutManager layoutManager;
    private RequestQueue requestQueue = null;
    private RecyclerView recyclerView = null;
    SwipeRefreshLayout swipeRefreshLayout;

    boolean isNeedUpdateBookmark = false;
    JSONArray responseJsonArray = null;

    public TabFragment(String tabSection) {
        this.tabSection = tabSection;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_tab, container, false);

        Log.v("#TabFragment ->", "Start onCreate " + tabSection);

        requestQueue = Volley.newRequestQueue(getActivity());
        newsList = new ArrayList<>();
        newsCardAdapter = new NewsCardAdapter(getActivity(), "Big", newsList);

        recyclerView = view.findViewById(R.id.tabRecyclerView);
        recyclerView.setHasFixedSize(true); // Keep size
        layoutManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        // Separator
        DividerItemDecoration horizontalDivider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        horizontalDivider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.line_divider));
        recyclerView.addItemDecoration(horizontalDivider);
        newsCardAdapter = new NewsCardAdapter(getActivity(), "Big", newsList);
        recyclerView.setAdapter(newsCardAdapter);

        swipeRefreshLayout = view.findViewById(R.id.headlinesSwipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNews();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        Log.v("#TabFragment ->", "End onCreate " + tabSection);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("#TabFragment -> ", "onResume " + tabSection);
        if (isNeedUpdateBookmark) {
            updateBookmark();
            isNeedUpdateBookmark = false;
        } else {
            fetchNews();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.v("#TabFragment ->", "Back from other article activity");
            isNeedUpdateBookmark = true;
        }
    }

    private void updateBookmark() {
        Log.v("#TabFragment -> ", "updateBookmark");
        newsList.clear();
        for (int i = 0; i < responseJsonArray.length(); i++) {
            JSONObject newsItem = null;
            try {
                newsItem = responseJsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            newsList.add(new NewsCard(newsItem, getActivity(), "other"));
        }
        buildRecyclerView();
    }

    private void fetchNews() {
        newsList.clear();
        if (newsCardAdapter != null) {
            newsCardAdapter.notifyDataSetChanged();
        }
        MainActivity.showLoader();

        String url = MyUtil.getBackendUrl() + tabSection.toLowerCase();

        Log.v("#TabFragment ->", "Start fetch news " + tabSection);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("#TabFragment -> ", "Fetched news for " + tabSection);
                    responseJsonArray = response.getJSONArray("result");
                    for (int i = 0; i < responseJsonArray.length(); i++) {
                        JSONObject newsItem = responseJsonArray.getJSONObject(i);
                        newsList.add(new NewsCard(newsItem, getActivity(), "other"));
                    }
                    MainActivity.hideLoader();
                    buildRecyclerView();
                    Log.v("#TabFragment ->", "Updated news for " + tabSection);
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
        Log.v("#PageHome -> ", "Start buildRecyclerView");
        newsCardAdapter = new NewsCardAdapter(getActivity(), "Big", newsList);
        recyclerView.setAdapter(newsCardAdapter);

        newsCardAdapter.setOnItemClickListener(new NewsCardAdapter.OnItemClickListener() {
            // Open article
            public void onItemClick(int position) {
                Log.v("#TabFragment ->", "Open article");
                Intent detailIntent = new Intent(getActivity(), ArticleActivity.class);
                NewsCard newsCard = newsList.get(position);
                detailIntent.putExtra("newsId", newsCard.getNewsId());
                startActivityForResult(detailIntent, 1);
            }

            // Expand dialog
            public void onItemLongClick(final int position) {
                // Init dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.layout_dialog, null);
                dialogBuilder.setView(view);

                ImageView dialogImage = ((ImageView) view.findViewById(R.id.dialogImage));
                Picasso.with(getActivity()).load(newsList.get(position).getNewsImageUrl()).into(dialogImage);

                TextView dialogTitle = view.findViewById(R.id.dialogTitle);
                dialogTitle.setText(newsList.get(position).getNewsTitle());

                ImageButton imageButtonShare = view.findViewById(R.id.imageButton);
                final ImageButton imageButtonDelete = view.findViewById(R.id.imageButton2);
                imageButtonDelete.setImageResource(getBookmarkIconById(newsList.get(position).getNewsId(), getActivity()));

                final AlertDialog aLertDialog = dialogBuilder.create();
                aLertDialog.show();

                // Click share icon
                imageButtonShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("#TabFragment ->", "Clicked share icon");
                        Intent intent = MyUtil.getShareIntent(newsList.get(position).getNewsUrl());
                        startActivity(intent);
                    }
                });

                // Click bookmark icon
                imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newsId = newsList.get(position).getNewsId();
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
                String newsId = newsList.get(position).getNewsId();
                if (position != RecyclerView.NO_POSITION) {
                    if (LocalStorage.isInBookmark(newsId, getActivity())) {
                        removeNewsFromBookmarks(newsId, position);
                    } else {
                        addNewsToBookmarks(newsId, position);
                    }
                }
            }
        });
    }

    public static int getBookmarkIconById(String id, Context context) {
        if (LocalStorage.isInBookmark(id, context)) {
            return R.drawable.ic_bookmark_red_24dp;
        } else {
            return R.drawable.ic_bookmark_border_red_24dp;
        }
    }

    private void addNewsToBookmarks(String newsId, int position) {
        Log.v("#TabFragment ->", "Add news");
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
        LocalStorage.insertNews(news, getActivity());
        newsList.get(position).changeImageSource(R.drawable.ic_bookmark_red_24dp);
        newsCardAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), newsList.get(position).getNewsTitle() + " was added to bookmarks", Toast.LENGTH_LONG).show();
    }

    private void removeNewsFromBookmarks(String newsId, int position) {
        Log.v("#TabFragment ->", "Remove news");
        LocalStorage.deleteNews(newsId, getActivity());
        newsList.get(position).changeImageSource(R.drawable.ic_bookmark_border_red_24dp);
        newsCardAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), newsList.get(position).getNewsTitle() + " was removed from bookmarks", Toast.LENGTH_LONG).show();
    }
}
