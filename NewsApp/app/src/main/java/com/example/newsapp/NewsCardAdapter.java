package com.example.newsapp;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsCardAdapter extends RecyclerView.Adapter<NewsCardAdapter.ExampleViewHolder> {
    private static Context context;
    private ArrayList<NewsCard> newsList;
    private OnItemClickListener onItemClickListener;
    private String cardType;

    public NewsCardAdapter(Context context, String cardType, ArrayList<NewsCard> newsList) {
        this.cardType = cardType;
        this.context = context;
        this.newsList = newsList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onItemLongClick(int position);

        void onBookmarkClick(int position);
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView newsCardImageView;
        public TextView newsCardTitleView;
        public TextView newsCardDateTagView;
        public TextView newsCardTagView;
        public ImageView newsCardBookmarkView;

        public ExampleViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            newsCardImageView = itemView.findViewById(R.id.newsCardImageView);
            newsCardTitleView = itemView.findViewById(R.id.newsCardTitleView);
            newsCardTitleView.setMaxLines(3);
            newsCardTitleView.setEllipsize(TextUtils.TruncateAt.END);
            newsCardDateTagView = itemView.findViewById(R.id.newsCardDateTagView);
            newsCardTagView = itemView.findViewById(R.id.newsCardTagView);
            newsCardBookmarkView = itemView.findViewById(R.id.newsCardBookmarkView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemLongClick(position);
                            return true;
                        }
                    }
                    return true;
                }
            });

            newsCardBookmarkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();  // Get card index
                        listener.onBookmarkClick(position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public NewsCardAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.big_news_card, parent, false);

        if (cardType.equals("Small")) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.small_news_card, parent, false);
        }

        ExampleViewHolder evh = new ExampleViewHolder(v, onItemClickListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        NewsCard currentItem = newsList.get(position);

        String newsImageUrl = currentItem.getNewsImageUrl();
        String newsTitle = currentItem.getNewsTitle();
        String newsPubDate = currentItem.getNewsPubDateTag();
        String newsTag = currentItem.getNewsTag();
        int newsBookmarkSrc = currentItem.getNewsBookmarkSrc();

        Picasso.with(context).load(newsImageUrl).fit().centerInside().into(holder.newsCardImageView);
        holder.newsCardTitleView.setText(newsTitle);
        holder.newsCardDateTagView.setText(newsPubDate);
        holder.newsCardTagView.setText(newsTag);
        holder.newsCardBookmarkView.setImageResource(newsBookmarkSrc);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
}
