package com.example.recyclerviewproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BigCardAdapter extends RecyclerView.Adapter<BigCardAdapter.ExampleViewHolder> {
    private Context mContext;
    private ArrayList<SmallCard> newsList;
    private OnItemClickListener onItemClickListener;

    public BigCardAdapter(Context context, ArrayList<SmallCard> exampleList) {

        mContext = context;
        newsList = exampleList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onItemLongClick(int position);

        void onDeleteClick(int position);
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView smallCardImageView;
        public TextView smallCardTitleView;
        public TextView smallCardDateTagView;
        public ImageView smallCardBookmarkView;

        public ExampleViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            smallCardImageView = itemView.findViewById(R.id.smallCardImageView);
            smallCardTitleView = itemView.findViewById(R.id.smallCardTitleView);
            smallCardDateTagView = itemView.findViewById(R.id.smallCardDateTagView);
            smallCardBookmarkView = itemView.findViewById(R.id.smallCardBookmarkView);

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

            smallCardBookmarkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.small_card, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, onItemClickListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        SmallCard currentItem = newsList.get(position);

        String imageUrl = currentItem.getImageResource();
        String creatorName = currentItem.getTitle();

        holder.smallCardTitleView.setText(creatorName);
        holder.smallCardDateTagView.setText(creatorName);
        Picasso.with(mContext).load(imageUrl).fit().centerInside().into(holder.smallCardImageView);

        //holder.smallCardImageView.setText(currentItem.getImageResource());
        //holder.smallCardTitleView.setText(currentItem.getTitle());
        //holder.smallCardDateTagView.setText(currentItem.getDateTag());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
}
