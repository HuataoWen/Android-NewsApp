package com.example.newsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Delayed;

public class PageHome extends Fragment {
    private ArrayList<BigCard> newsList;
    private RequestQueue mRequestQueue = null;
    private RecyclerView recyclerView;
    private BigCardAdapter bigCardAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.page_home, container, false);

        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_items);
        recyclerView = view.findViewById(R.id.recyclerView);

        newsList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(getActivity());

        fetchNews();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mRequestQueue != null) {
                    newsList.clear();
                    bigCardAdapter.notifyDataSetChanged();
                    MainActivity.showLoader();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            fetchNews();
                        }
                    }, 2000);

                    mSwipeRefreshLayout.setRefreshing(false);
                    Log.v("refresh", "ok");
                }
            }
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mRequestQueue != null) {
                newsList.clear();
                bigCardAdapter.notifyDataSetChanged();
                MainActivity.showLoader();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        fetchNews();
                    }
                }, 2000);

                Log.v("refresh", "ok");
            }
        }
    }

    private void fetchNews() {
        //String url = "https://pixabay.com/api/?key=5303976-fd6581ad4ac165d1b75cc15b3&q=kitten&image_type=photo&pretty=true";
        String url = "http://10.0.2.2:4000/mobile";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("fetched", "ok");
                    JSONArray jsonArray = response.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject newsItem = jsonArray.getJSONObject(i);
                        String newsId = newsItem.getString("id");
                        String newsUrl = newsItem.getString("url");
                        String newsTitle = newsItem.getString("title");
                        String newsImageUrl = newsItem.getString("urlToImage");
                        String newsTag = newsItem.getString("tag");
                        newsList.add(new BigCard(newsId, newsUrl, newsImageUrl, newsTitle, "24m ago | " + newsTag, "Apr 4", getBookmarkIconById(newsId, getActivity())));
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

    public void buildRecyclerView() {
        recyclerView.setHasFixedSize(true); // Keep size

        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        //layoutManager = new GridLayoutManager(this, 2);
        bigCardAdapter = new BigCardAdapter(getActivity(), newsList);
        recyclerView.setLayoutManager(layoutManager);
        // Separator
        DividerItemDecoration horizontalDivider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        horizontalDivider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.line_divider));
        recyclerView.addItemDecoration(horizontalDivider);
        recyclerView.setAdapter(bigCardAdapter);

        bigCardAdapter.setOnItemClickListener(new BigCardAdapter.OnItemClickListener() {
            // Open article
            public void onItemClick(int position) {
                //changeItem(position, "Clicked");
                Toast.makeText(getActivity(), "Open article", Toast.LENGTH_SHORT).show();

                /*Intent detailIntent = new Intent(MainActivity.this, ArticleActivity.class);
                BigCard smallCard = newsList.get(position);

                detailIntent.putExtra(EXTRA_URL, smallCard.getImageResource());
                detailIntent.putExtra(EXTRA_CREATOR, smallCard.getTitle());
                detailIntent.putExtra(EXTRA_LIKES, smallCard.getTitle());

                startActivityForResult(detailIntent, 1);
                 */
                //startActivity(detailIntent);
            }

            // Expand dialog
            public void onItemLongClick(final int position) {
                // Init dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.layout_dialog, null);
                dialogBuilder.setView(view);
                ImageButton imageButtonShare = view.findViewById(R.id.imageButton);
                final ImageButton imageButtonDelete = view.findViewById(R.id.imageButton2);
                imageButtonDelete.setImageResource(getBookmarkIconById(newsList.get(position).getID(), getActivity()));
                final AlertDialog aLertDialog = dialogBuilder.create();
                aLertDialog.show();

                // Click share icon
                imageButtonShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Share", Toast.LENGTH_SHORT).show();
                    }
                });

                // Click bookmark icon
                imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newsId = newsList.get(position).getID();
                        if (LocalStorage.isInBookmark(newsId, getActivity())) {
                            imageButtonDelete.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
                            LocalStorage.deleteNews(newsId, getActivity());
                            newsList.get(position).changeImageSource(R.drawable.ic_bookmark_border_red_24dp);
                            bigCardAdapter.notifyDataSetChanged();

                            //aLertDialog.dismiss();

                            Log.v("unbook", newsId);
                        } else {
                            imageButtonDelete.setImageResource(R.drawable.ic_bookmark_red_24dp);
                            JSONObject news = new JSONObject();
                            try {
                                news.put("id", newsId);
                                news.put("url", newsList.get(position).getIUrl());
                                news.put("title", newsList.get(position).getTitle());
                                news.put("urlToImage", newsList.get(position).getImageResource());
                                news.put("publishDate", newsList.get(position).getPublishDate());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            LocalStorage.insertNews(news, getActivity());
                            newsList.get(position).changeImageSource(R.drawable.ic_bookmark_red_24dp);
                            bigCardAdapter.notifyDataSetChanged();

                            Log.v("book", newsId);
                        }
                    }
                });
            }

            // Click bookmark icon on viewpager
            public void onBookmarkClick(int position) {
                String newsId = newsList.get(position).getID();
                if (position != RecyclerView.NO_POSITION) {
                    if (LocalStorage.isInBookmark(newsId, getActivity())) {
                        LocalStorage.deleteNews(newsId, getActivity());
                        newsList.get(position).changeImageSource(R.drawable.ic_bookmark_border_red_24dp);
                        bigCardAdapter.notifyDataSetChanged();

                        // Print to log
                        JSONArray newsList = LocalStorage.getNews(getActivity());
                        showLocalStorageInLog(newsList);
                    } else {
                        JSONObject news = new JSONObject();
                        try {
                            news.put("id", newsId);
                            news.put("url", newsList.get(position).getIUrl());
                            news.put("title", newsList.get(position).getTitle());
                            news.put("urlToImage", newsList.get(position).getImageResource());
                            news.put("publishDate", newsList.get(position).getPublishDate());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        LocalStorage.insertNews(news, getActivity());
                        newsList.get(position).changeImageSource(R.drawable.ic_bookmark_red_24dp);
                        bigCardAdapter.notifyDataSetChanged();

                        // Print to log
                        JSONArray newsList = LocalStorage.getNews(getActivity());
                        showLocalStorageInLog(newsList);
                    }
                }
            }
        });
    }

    public void insertItem(int position) {
        //newsList.add(position, new SmallCard(R.drawable.img, "New Item At Position" + position, "This is Line 2"));
        bigCardAdapter.notifyItemInserted(position);
    }

    public void removeItem(int position) {
        newsList.remove(position);
        bigCardAdapter.notifyItemRemoved(position);
    }

    public void changeItem(int position, String text) {
        newsList.get(position).changeText1(text);
        bigCardAdapter.notifyItemChanged(position);
    }

    public static int getBookmarkIconById(String id, Context context) {
        if (LocalStorage.isInBookmark(id, context)) {
            return R.drawable.ic_bookmark_red_24dp;
        } else {
            return R.drawable.ic_bookmark_border_red_24dp;
        }
    }

    private static void showLocalStorageInLog(JSONArray newsList) {
        Log.v("info", "----------------");
        for (int i = 0; i < newsList.length(); i++) {
            JSONObject tmp = null;
            try {
                tmp = newsList.getJSONObject(i);
                Log.v("id", tmp.getString("id"));
                Log.v("url", tmp.getString("url"));
                Log.v("title", tmp.getString("title"));
                Log.v("urlToImage", tmp.getString("urlToImage"));
                Log.v("publishDate", tmp.getString("publishDate"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
