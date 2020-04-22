package com.example.newsapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PageTrending extends Fragment implements MainActivity.FragmentInterface {
    private LineChart lineChart;
    private Legend legend;
    private EditText trendingEditText;
    private LineDataSet lineDataSet;
    private RequestQueue requestQueue = null;
    private String searchWord;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v("-->PageTrending", "Enter onCreateView");
        final View view = inflater.inflate(R.layout.page_tredning, container, false);
        requestQueue = Volley.newRequestQueue(getActivity());
        trendingEditText = view.findViewById(R.id.trendingEditText);

        searchWord = "CoronaVirus"; // Search word init as CoronaVirus

        lineChart = view.findViewById(R.id.line_chart);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.setMinimumHeight(500);
        legend = lineChart.getLegend();
        legend.setTextSize(18);
        legend.setFormSize(18);


        trendingEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchWord = trendingEditText.getText().toString();
                    fetchTrending();
                }
                return false;
            }
        });
        Log.v("#PageTrending -> ", "End onCreate");
        return view;
    }

    @Override
    public void requireUpdate(int requestCode) {
        Log.v("-->PageTrending", "Enter requireUpdate");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("-->PageTrending", "Enter onResume");
        MainActivity.hideLoader();
        fetchTrending();
    }

    public void fetchTrending() {
        Log.v("#PageTrending -> ", "Start fetch " + searchWord + " trending");
        String url = MyUtil.getBackendUrl() + "getTrending?keyword=" + searchWord;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("#PageTrending -> ", "Fetched trending");
                    JSONArray jsonArray = response.getJSONArray("result");
                    ArrayList<Entry> dataValues = new ArrayList<Entry>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        dataValues.add(new Entry(i, jsonArray.getInt(i)));
                    }

                    int colorValue = Color.parseColor("#502ca6");
                    lineDataSet = new LineDataSet(dataValues, "Trending Chart for " + searchWord);
                    lineDataSet.setColor(colorValue);
                    lineDataSet.setCircleColor(colorValue);
                    lineDataSet.setFillColor(colorValue);
                    lineDataSet.setValueTextSize(10);
                    lineDataSet.setCircleHoleColor(colorValue);
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(lineDataSet);
                    LineData data = new LineData(dataSets);
                    lineChart.setData(data);
                    lineChart.invalidate();

                    Log.v("#PageTrending -> ", "Trending updated");
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

        requestQueue.add(request);
    }
}
