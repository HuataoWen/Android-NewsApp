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
    private static Context mContext;
    private ArrayList<NewsCard> newsList;
    private OnItemClickListener onItemClickListener;
    private String cardType;

    public NewsCardAdapter(Context context, String cardType, ArrayList<NewsCard> exampleList) {
        this.cardType = cardType;
        mContext = context;
        newsList = exampleList;
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
        public ImageView bigCardImageView;
        public TextView bigCardTitleView;
        public TextView bigCardDateTagView;
        public ImageView bigCardBookmarkView;

        public ExampleViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            bigCardImageView = itemView.findViewById(R.id.bigCardImageView);
            bigCardTitleView = itemView.findViewById(R.id.bigCardTitleView);
            bigCardTitleView.setMaxLines(3);
            bigCardTitleView.setEllipsize(TextUtils.TruncateAt.END);
            bigCardDateTagView = itemView.findViewById(R.id.bigCardDateTagView);
            bigCardBookmarkView = itemView.findViewById(R.id.bigCardBookmarkView);

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

            bigCardBookmarkView.setOnClickListener(new View.OnClickListener() {
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
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card, parent, false);

        if (cardType.equals("Small")) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.small_card, parent, false);
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
        int newsBookmarkSrc = currentItem.getNewsBookmarkSrc();

        Picasso.with(mContext).load(newsImageUrl).fit().centerInside().into(holder.bigCardImageView);
        holder.bigCardTitleView.setText(newsTitle);
        holder.bigCardDateTagView.setText(newsPubDate);
        holder.bigCardBookmarkView.setImageResource(newsBookmarkSrc);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

}
