package com.example.recyclerviewproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener {
    private ArrayList<SmallCard> newsList;
    private RecyclerView recyclerView;
    private SmallCardAdapter smallCardAdapter;
    private RecyclerView.LayoutManager layoutManager;

    ExampleDialog exampleDialog;

    private Button buttonInsert;
    private Button buttonRemove;
    private EditText editTextInsert;
    private EditText editTextRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createExampleList();
        buildRecyclerView();
        setButtons();
    }

    @Override
    public void delete(int position) {
        removeItem(position);
        exampleDialog.dismiss();
        Toast.makeText(MainActivity.this, newsList.get(position).getTitle() + " was removed from bookmarks", Toast.LENGTH_SHORT).show();
    }

    public void createExampleList() {
        newsList = new ArrayList<>();
        newsList.add(new SmallCard(R.drawable.img, "Plagues and wars alter economic polices: but not for ever | Kelly William ", "04 Apr | Politics"));
        newsList.add(new SmallCard(R.drawable.img, "Line2", "05 Apr | World news"));
        newsList.add(new SmallCard(R.drawable.img, "Line3", "05 Apr | Stage"));
    }

    public void buildRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // Keep size
        //mLayoutManager = new LinearLayoutManager(this);
        layoutManager = new GridLayoutManager(this, 2);
        smallCardAdapter = new SmallCardAdapter(newsList);

        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration horizontalDivider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        horizontalDivider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.line_divider));
        recyclerView.addItemDecoration(horizontalDivider);
        recyclerView.setAdapter(smallCardAdapter);

        smallCardAdapter.setOnItemClickListener(new SmallCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                changeItem(position, "Clicked");
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

    public void insertItem(int position) {
        newsList.add(position, new SmallCard(R.drawable.img, "New Item At Position" + position, "This is Line 2"));
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
