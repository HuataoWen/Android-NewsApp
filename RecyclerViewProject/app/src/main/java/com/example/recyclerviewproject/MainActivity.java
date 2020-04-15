package com.example.recyclerviewproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener {
    public static final String EXTRA_URL = "imageUrl";
    public static final String EXTRA_CREATOR = "creatorName";
    public static final String EXTRA_LIKES = "likeCount";

    private ArrayList<SmallCard> newsList;
    private RecyclerView recyclerView;
    private SmallCardAdapter smallCardAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private RequestQueue mRequestQueue;

    ExampleDialog exampleDialog;

    private Button buttonInsert;
    private Button buttonRemove;
    private EditText editTextInsert;
    private EditText editTextRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(this);
        parseJSOn();

        //createExampleList();
        //buildRecyclerView();
        //setButtons();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            //Intent refresh = new Intent(MainActivity.this, MainActivity.class);
            //startActivity(refresh);
            //MainActivity.this.finish();
            Toast.makeText(MainActivity.this, "Refresh", Toast.LENGTH_SHORT).show();
        }
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

                        newsList.add(new SmallCard(imageUrl, creatorName, creatorName));
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

    @Override
    public void delete(int position) {
        removeItem(position);
        exampleDialog.dismiss();
        Toast.makeText(MainActivity.this, newsList.get(position).getTitle() + " was removed from bookmarks", Toast.LENGTH_SHORT).show();
    }

    public void createExampleList() {
        newsList = new ArrayList<>();
        //newsList.add(new SmallCard(R.drawable.img, "Plagues and wars alter economic polices: but not for ever | Kelly William ", "04 Apr | Politics"));
        //newsList.add(new SmallCard(R.drawable.img, "Line2", "05 Apr | World news"));
        //newsList.add(new SmallCard(R.drawable.img, "Line3", "05 Apr | Stage"));
    }

    public void buildRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // Keep size

        layoutManager = new LinearLayoutManager(this);
        //layoutManager = new GridLayoutManager(this, 2);

        smallCardAdapter = new SmallCardAdapter(MainActivity.this, newsList);

        recyclerView.setLayoutManager(layoutManager);
        // Separator
        DividerItemDecoration horizontalDivider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        horizontalDivider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.line_divider));
        recyclerView.addItemDecoration(horizontalDivider);
        recyclerView.setAdapter(smallCardAdapter);

        smallCardAdapter.setOnItemClickListener(new SmallCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //changeItem(position, "Clicked");
                Toast.makeText(MainActivity.this, "Open article", Toast.LENGTH_SHORT).show();

                Intent detailIntent = new Intent(MainActivity.this, ArticleActivity.class);
                SmallCard smallCard = newsList.get(position);

                detailIntent.putExtra(EXTRA_URL, smallCard.getImageResource());
                detailIntent.putExtra(EXTRA_CREATOR, smallCard.getTitle());
                detailIntent.putExtra(EXTRA_LIKES, smallCard.getTitle());

                startActivityForResult(detailIntent, 1);
                //startActivity(detailIntent);
            }

            @Override
            public void onItemLongClick(int position) {
                exampleDialog = new ExampleDialog(position, "v.xue.taobao.com/learn.htm?itemId=566048780252");
                exampleDialog.show(getSupportFragmentManager(), "example dialog");
            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
    }

    /*
    public void setButtons() {
        buttonInsert = findViewById(R.id.button_insert);
        buttonRemove = findViewById(R.id.button_remove);
        editTextInsert = findViewById(R.id.edittext_insert);
        editTextRemove = findViewById(R.id.edittext_remove);

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(editTextInsert.getText().toString());
                insertItem(position);
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(editTextRemove.getText().toString());
                removeItem(position);
            }
        });
    }
    */

    public void insertItem(int position) {
        //newsList.add(position, new SmallCard(R.drawable.img, "New Item At Position" + position, "This is Line 2"));
        smallCardAdapter.notifyItemInserted(position);
    }

    public void removeItem(int position) {
        newsList.remove(position);
        smallCardAdapter.notifyItemRemoved(position);
    }

    public void changeItem(int position, String text) {
        newsList.get(position).changeText1(text);
        smallCardAdapter.notifyItemChanged(position);
    }
}
