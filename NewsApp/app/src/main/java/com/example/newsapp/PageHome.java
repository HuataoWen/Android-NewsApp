package com.example.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class PageHome extends Fragment implements ExampleDialog.ExampleDialogListener {
    private ArrayList<BigCard> newsList;
    private RequestQueue mRequestQueue;
    private RecyclerView recyclerView;
    private BigCardAdapter bigCardAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ExampleDialog exampleDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_home, container, false);

        newsList=new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        parseJSOn();

        return view;
    }

    private void parseJSOn() {
        String url = "https://pixabay.com/api/?key=5303976-fd6581ad4ac165d1b75cc15b3&q=kitten&image_type=photo&pretty=true";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("hits");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject hit = jsonArray.getJSONObject(i);
                        String creatorName = hit.getString("user");
                        String imageUrl = hit.getString("webformatURL");
                        //int likeCount = hit.getInt("likes");

                        newsList.add(new BigCard(imageUrl, creatorName, creatorName));
                    }
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
        recyclerView = getView().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // Keep size

        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        //layoutManager = new GridLayoutManager(this, 2);

        bigCardAdapter = new BigCardAdapter(getActivity().getApplicationContext(), newsList);

        recyclerView.setLayoutManager(layoutManager);
        // Separator
        DividerItemDecoration horizontalDivider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        horizontalDivider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.line_divider));
        recyclerView.addItemDecoration(horizontalDivider);
        recyclerView.setAdapter(bigCardAdapter);

        bigCardAdapter.setOnItemClickListener(new BigCardAdapter.OnItemClickListener() {
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

            public void onItemLongClick(int position) {
                exampleDialog = new ExampleDialog(position, "v.xue.taobao.com/learn.htm?itemId=566048780252");
                exampleDialog.show(getFragmentManager(), "example dialog");
            }

            public void onDeleteClick(int position) {
                removeItem(position);
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

    @Override
    public void delete(int position) {
        removeItem(position);
        exampleDialog.dismiss();
        Toast.makeText(getActivity(), newsList.get(position).getTitle() + " was removed from bookmarks", Toast.LENGTH_SHORT).show();
    }
}
