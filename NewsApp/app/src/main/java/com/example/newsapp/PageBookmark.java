package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PageBookmark extends Fragment implements MainActivity.FragmentInterface {
    private ArrayList<NewsCard> newsList;
    private NewsCardAdapter newsCardAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView bookmarkRecyclerView;
    private TextView bookmarkEmptyTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v("-->PageBookmark", "Enter onCreateView");
        final View view = inflater.inflate(R.layout.page_bookmark, container, false);
        Log.v("#PageBookmark -> ", "Init view");
        newsList = new ArrayList<>();
        bookmarkRecyclerView = view.findViewById(R.id.bookmarkRecyclerView);
        bookmarkRecyclerView.setHasFixedSize(true); // Keep size
        bookmarkEmptyTextView = view.findViewById(R.id.bookmarkEmptyTextView);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        bookmarkRecyclerView.setLayoutManager(layoutManager);
        // Separator
        DividerItemDecoration horizontalDivider = new DividerItemDecoration(bookmarkRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        horizontalDivider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.line_divider));
        bookmarkRecyclerView.addItemDecoration(horizontalDivider);
        newsCardAdapter = new NewsCardAdapter(getActivity(), "Small", newsList);
        bookmarkRecyclerView.setAdapter(newsCardAdapter);
        return view;
    }

    @Override
    public void requireUpdate(int requestCode) {
        Log.v("-->PageBookmark", "Enter requireUpdate");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("-->PageBookmark", "Enter onResume");
        MainActivity.hideLoader();
        buildRecyclerView();
    }

    public void buildRecyclerView() {
        Log.v("-->PageBookmark", "Start buildRecyclerView");
        newsList.clear();

        JSONArray jsonArray = LocalStorage.getNews(getActivity());
        if (jsonArray.length() == 0) {
            newsCardAdapter = new NewsCardAdapter(getActivity(), "Small", newsList);
            bookmarkRecyclerView.setAdapter(newsCardAdapter);
            bookmarkEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            bookmarkEmptyTextView.setVisibility(View.INVISIBLE);
            Log.v("-->PageBookmark", "Read local storage");
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject newsItem = jsonArray.getJSONObject(i);
                    newsList.add(new NewsCard(newsItem, getActivity(), "bookmark"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.v("-->PageBookmark", "Update adapter");
            newsCardAdapter = new NewsCardAdapter(getActivity(), "Small", newsList);
            bookmarkRecyclerView.setAdapter(newsCardAdapter);

            newsCardAdapter.setOnItemClickListener(new NewsCardAdapter.OnItemClickListener() {
                // Open article
                public void onItemClick(int position) {
                    Log.v("-->PageBookmark", "Open article");
                    Intent intent = new Intent(getActivity(), ArticleActivity.class);
                    intent.putExtra("newsId", newsList.get(position).getNewsId());
                    startActivityForResult(intent, 1);
                }

                // Expand dialog
                public void onItemLongClick(final int position) {
                    Log.v("-->PageBookmark", "Build dialog");
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View view = inflater.inflate(R.layout.layout_dialog, null);

                    // Init dialog
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                    ImageView dialogImage = ((ImageView) view.findViewById(R.id.dialogImage));
                    Picasso.with(getActivity()).load(newsList.get(position).getNewsImageUrl()).into(dialogImage);

                    TextView dialogTitle = view.findViewById(R.id.dialogTitle);
                    dialogTitle.setText(newsList.get(position).getNewsTitle());

                    ImageButton imageButtonShare = view.findViewById(R.id.imageButton);
                    ImageButton imageButtonDelete = view.findViewById(R.id.imageButton2);
                    imageButtonDelete.setImageResource(MyUtil.getBookmarkIconById(newsList.get(position).getNewsId(), getActivity()));

                    dialogBuilder.setView(view);
                    final AlertDialog aLertDialog = dialogBuilder.create();
                    aLertDialog.show();

                    // Click share icon
                    imageButtonShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.v("-->PageBookmark", "Share article");
                            String url = newsList.get(position).getNewsUrl();
                            Intent intent = MyUtil.getShareIntent(url);
                            startActivity(intent);
                        }
                    });

                    // Click bookmark icon
                    imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeNews(position, getActivity());
                            aLertDialog.dismiss();
                        }
                    });
                }

                // Click bookmark icon on viewpager
                public void onBookmarkClick(int position) {
                    if (position != RecyclerView.NO_POSITION) {
                        removeNews(position, getActivity());
                    }
                }
            });
        }
    }

    public void removeNews(int position, Context context) {
        Log.v("-->PageBookmark", "Remove article from bookmark");
        String newsId = newsList.get(position).getNewsId();
        LocalStorage.deleteNews(newsId, getActivity());
        Toast.makeText(getActivity(), '"' + newsList.get(position).getNewsTitle() + '"' + " was removed from bookmarks", Toast.LENGTH_LONG).show();
        newsList.remove(position);
        newsCardAdapter.notifyItemRemoved(position);
        if (newsList.size() == 0) bookmarkEmptyTextView.setVisibility(View.VISIBLE);
    }
}
