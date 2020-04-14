package com.example.newsapp;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageBookmark extends Fragment {
    GridView gridView;
    SimpleAdapter simpleAdapter;
    String[] title = new String[]{"Apple", "Microsoft", "eBay", "Google", "Microsoft", "eBay", "Google"};
    int[] img = new int[]{R.drawable.dd,
            R.drawable.dd,
            R.drawable.dd,
            R.drawable.dd,
            R.drawable.dd,
            R.drawable.dd,
            R.drawable.dd};

    CardView cardView;
    ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_bookmark, container, false);

        // Init view
        gridView = view.findViewById(R.id.gridView);
        imageView = view.findViewById(R.id.imageView6);

        // Init data for adapter
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < title.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", title[i]);
            map.put("img", img[i]);
            list.add(map);
        }
        // Init adapter
        simpleAdapter = new SimpleAdapter(getActivity(), list, R.layout.bookmark_item,
                new String[]{"name", "img"}, new int[]{R.id.bookmarkTextView, R.id.bookmarkImgView});
        gridView.setAdapter(simpleAdapter);

        /*
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //imageView = view.findViewById(R.id.imageView6);
                //imageView.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                if (R.id.imageView6 == id) {
                    Toast.makeText(getActivity(), title[position], Toast.LENGTH_SHORT).show();
                }
            }
        });
         */
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "hello", Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }
}
