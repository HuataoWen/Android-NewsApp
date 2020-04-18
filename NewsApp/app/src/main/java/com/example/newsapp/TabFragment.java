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

public class TabFragment extends Fragment implements PageHeadlines.MyInterface {
    private ArrayList<NewsCard> newsList;
    private String tabSection;
    private BigCardAdapter bigCardAdapter = null;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private RequestQueue mRequestQueue = null;

    private RecyclerView recyclerView = null;


    public TabFragment(String tabSection) {
        this.tabSection = tabSection;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_tab, container, false);

        mSwipeRefreshLayout = view.findViewById(R.id.headlinesSwipeRefresh);
        recyclerView = view.findViewById(R.id.tabRecyclerView);
        // Separator
        DividerItemDecoration horizontalDivider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        horizontalDivider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.line_divider));
        recyclerView.addItemDecoration(horizontalDivider);



        newsList = new ArrayList<>();
        Log.v("init", tabSection);

        mRequestQueue = Volley.newRequestQueue(getActivity());

        TextView textView = view.findViewById(R.id.headlinesTab);
        textView.setText(tabSection);

        //fetchNews();

        return view;
    }

    private void fetchNews() {
        newsList.clear();
        Log.v("show loader", tabSection);
        MainActivity.showLoader();

        Log.v("headlines fetchNews", tabSection);
        //String url = "https://pixabay.com/api/?key=5303976-fd6581ad4ac165d1b75cc15b3&q=kitten&image_type=photo&pretty=true";
        String url = "http://10.0.2.2:4000/mobile";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("headlines fetched", tabSection);
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
                    Log.v("hide loader", tabSection);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            //Intent refresh = new Intent(MainActivity.this, MainActivity.class);
            //startActivity(refresh);
            //MainActivity.this.finish();
            Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
        }
    }

    public void buildRecyclerView() {
        recyclerView.setHasFixedSize(true); // Keep size

        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        //layoutManager = new GridLayoutManager(this, 2);
        bigCardAdapter = new BigCardAdapter(getActivity(), "Big", newsList);
        recyclerView.setAdapter(bigCardAdapter);

        bigCardAdapter.setOnItemClickListener(new BigCardAdapter.OnItemClickListener() {
            // Open article
            public void onItemClick(int position) {
                Log.v("TabFragment -> ", "Open article");
                Intent detailIntent = new Intent(getActivity(), ArticleActivity.class);
                NewsCard newsCard = newsList.get(position);
                detailIntent.putExtra("newsID", newsCard.getID());
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
                            Toast.makeText(getActivity(), newsList.get(position).getTitle() + " was removed from bookmarks", Toast.LENGTH_LONG).show();


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
                                news.put("tag", newsList.get(position).getTag());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            LocalStorage.insertNews(news, getActivity());
                            newsList.get(position).changeImageSource(R.drawable.ic_bookmark_red_24dp);
                            bigCardAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), newsList.get(position).getTitle() + " was added to bookmarks", Toast.LENGTH_LONG).show();


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
                        Toast.makeText(getActivity(), newsList.get(position).getTitle() + " was removed from bookmarks", Toast.LENGTH_LONG).show();


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
                            news.put("tag", newsList.get(position).getTag());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        LocalStorage.insertNews(news, getActivity());
                        newsList.get(position).changeImageSource(R.drawable.ic_bookmark_red_24dp);
                        bigCardAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), newsList.get(position).getTitle() + " was added to bookmarks", Toast.LENGTH_LONG).show();


                        // Print to log
                        JSONArray newsList = LocalStorage.getNews(getActivity());
                        showLocalStorageInLog(newsList);
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
                Log.v("tag", tmp.getString("tag"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void myAction() {
        if (mRequestQueue != null) {
            Log.v("call_test", tabSection);
            newsList.clear();
            if (recyclerView == null) {
                Log.v("null recyclerView", tabSection);
            }
            if (bigCardAdapter != null) {
                bigCardAdapter.notifyDataSetChanged();
            }
            Log.v("show loader", tabSection);
            MainActivity.showLoader();


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    fetchNews();
                }
            }, 2000);
        }
    }
}
