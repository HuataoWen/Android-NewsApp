package com.example.localstorage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME = "example.txt";

    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = findViewById(R.id.edit_text);
    }

    public void save(View v) {
        JSONObject news = new JSONObject();
        JSONObject news1 = new JSONObject();
        try {
            news.put("id", "1");
            news.put("title", "Google");
            news1.put("id", "2");
            news1.put("title", "Google");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray newsList = new JSONArray();
        newsList.put(news);

        //LocalStorage.saveNews(newsList, MainActivity.this);
        //LocalStorage.insertNews(news1, MainActivity.this);
        LocalStorage.deleteNews("2",MainActivity.this);
        /*
        String text = mEditText.getText().toString();
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);

            fos.write(text.getBytes());
            mEditText.getText().clear();
            Toast.makeText(this, "Save to " + getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

         */
    }

    public void load(View v) {
        JSONArray newsList = LocalStorage.getNews(MainActivity.this);

        for (int i = 0; i < newsList.length(); i++) {
            JSONObject news = null;
            try {
                news = newsList.getJSONObject(i);
                mEditText.setText(news.toString());
                Log.v("id", news.getString("id"));
                Log.v("title", news.getString("title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        /*

        FileInputStream fis = null;

        String a = "[{'image': '1', 'title': '2', 'time':'3', 'section': '4', 'id': '5'},{'image': '1', 'title': '2', 'time':'3', 'section': '4', 'id': '7'}]";

        try {
            JSONArray aArr = new JSONArray(a);
            for (int i =0;i<aArr.length();i++){
                JSONObject news = aArr.getJSONObject(i);

                Log.v("id", news.getString("id"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader((fis));
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            mEditText.setText(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

         */
    }

}
