package com.example.newsapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class PageBookmark extends Fragment {
    private ArrayList<NewsCard> newsList;
    private RecyclerView recyclerView;
    private BigCardAdapter bigCardAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView bookmarkEmptyTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_bookmark, container, false);

        recyclerView = view.findViewById(R.id.bookmarkRecyclerView);
        bookmarkEmptyTextView = view.findViewById(R.id.bookmarkEmptyTextView);
        newsList = new ArrayList<>();

        buildRecyclerView();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //Intent refresh = new Intent(MainActivity.this, MainActivity.class);
            //startActivity(refresh);
            //MainActivity.this.finish();
            Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
        }
    }

    public void buildRecyclerView() {
        Log.v("info", "PageBookmark");
        //newsList = LocalStorage.getNews(getActivity());

        JSONArray jsonArray = LocalStorage.getNews(getActivity());
        if (jsonArray.length() == 0) {
            bookmarkEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject newsItem = null;
                try {
                    newsItem = jsonArray.getJSONObject(i);
                    String newsId = newsItem.getString("id");
                    String newsUrl = newsItem.getString("url");
                    String newsTitle = newsItem.getString("title");
                    String newsImageUrl = newsItem.getString("urlToImage");
                    String newsTag = newsItem.getString("tag");
                    newsList.add(new NewsCard(newsId, newsUrl, newsImageUrl, newsTitle, newsTag, "24m ago | " + newsTag, "Apr 4", getBookmarkIconById(newsId, getActivity())));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            recyclerView.setHasFixedSize(true); // Keep size

            //layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            layoutManager = new GridLayoutManager(getActivity(), 2);
            bigCardAdapter = new BigCardAdapter(getActivity(), "Small", newsList);
            recyclerView.setLayoutManager(layoutManager);
            // Separator
            DividerItemDecoration horizontalDivider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
            horizontalDivider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.line_divider));
            recyclerView.addItemDecoration(horizontalDivider);
            recyclerView.setAdapter(bigCardAdapter);

            bigCardAdapter.setOnItemClickListener(new BigCardAdapter.OnItemClickListener() {
                // Open article
                public void onItemClick(int position) {
                    Log.v("RageBookmark -> ", "Open article");
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
                            LocalStorage.deleteNews(newsId, getActivity());
                            Toast.makeText(getActivity(), newsList.get(position).getTitle() + " was removed from bookmarks", Toast.LENGTH_LONG).show();

                            removeItem(position);
                            aLertDialog.dismiss();


                            // Print to log
                            Log.v("unbook", newsId);
                            JSONArray newsList = LocalStorage.getNews(getActivity());
                            showLocalStorageInLog(newsList);
                        }
                    });
                }

                // Click bookmark icon on viewpager
                public void onBookmarkClick(int position) {
                    String newsId = newsList.get(position).getID();
                    if (position != RecyclerView.NO_POSITION) {
                        String newsTitle = newsList.get(position).getTitle();
                        Toast.makeText(getActivity(), newsTitle + " was removed from bookmarks", Toast.LENGTH_LONG).show();
                        removeItem(position);

                        LocalStorage.deleteNews(newsId, getActivity());


                        // Print to log
                        Log.v("unbook", newsId);
                        JSONArray newsList = LocalStorage.getNews(getActivity());
                        showLocalStorageInLog(newsList);
                    }
                }
            });
        }
    }

    public void removeItem(int position) {
        newsList.remove(position);
        bigCardAdapter.notifyItemRemoved(position);
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.v("Bookmark refresh", "ok");
        }
    }
}
