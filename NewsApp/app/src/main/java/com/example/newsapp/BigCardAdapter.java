package com.example.newsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.BigCard;
import com.example.newsapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BigCardAdapter extends RecyclerView.Adapter<BigCardAdapter.ExampleViewHolder> {
    private static Context mContext;
    private ArrayList<BigCard> newsList;
    private OnItemClickListener onItemClickListener;

    public BigCardAdapter(Context context, ArrayList<BigCard> exampleList) {

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.big_card, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, onItemClickListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        BigCard currentItem = newsList.get(position);

        String imageUrl = currentItem.getImageResource();
        String newsTitle = currentItem.getTitle();
        String newsDateTag = currentItem.getDateTag();
        int newsBookmark = currentItem.getBookmark();

        Picasso.with(mContext).load(imageUrl).fit().centerInside().into(holder.bigCardImageView);
        holder.bigCardTitleView.setText(newsTitle);
        holder.bigCardDateTagView.setText(newsDateTag);
        holder.bigCardBookmarkView.setImageResource(newsBookmark);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

}
