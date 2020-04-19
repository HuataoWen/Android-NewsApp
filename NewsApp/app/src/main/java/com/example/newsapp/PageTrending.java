package com.example.newsapp;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
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
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PageTrending extends Fragment {
    LineChart lineChart;
    Legend l;
    EditText trendingEditText;
    LineDataSet lineDataSet1;
    private RequestQueue mRequestQueue = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_trending, container, false);

        mRequestQueue = Volley.newRequestQueue(getActivity());
        trendingEditText = view.findViewById(R.id.trendingEditText);

        lineChart = view.findViewById(R.id.line_chart);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.setMinimumHeight(1500);
        l = lineChart.getLegend();
        l.setTextSize(18);
        l.setFormSize(18);

        int colorValue = Color.parseColor("#502ca6");
        lineDataSet1 = new LineDataSet(dataValues1(), "Trending Chart for CoronaVirus");
        lineDataSet1.setColor(colorValue);
        lineDataSet1.setCircleColor(colorValue);
        lineDataSet1.setFillColor(colorValue);
        lineDataSet1.setValueTextSize(10);
        lineDataSet1.setCircleHoleColor(colorValue);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);

        lineChart.invalidate();

        trendingEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    fetchNews();
                }
                return false;
            }
        });

        return view;
    }

    public void fetchNews() {
        Log.v("#PageTrending -> ", "Start fetch word trending");
        //String url = "http://10.0.2.2:4000/mobile/getTrending?keyword=" + trendingEditText.getText().toString();
        String url = "http://ec2-52-14-208-196.us-east-2.compute.amazonaws.com:4000/mobile/getTrending?keyword=" + trendingEditText.getText().toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("#ArticleActivity -> ", "Fetched news");
                    JSONArray jsonArray = response.getJSONArray("result");
                    ArrayList<Entry> dataVals = new ArrayList<Entry>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        dataVals.add(new Entry(i, jsonArray.getInt(i)));
                    }

                    List<LegendEntry> entries = new ArrayList<>();
                    LegendEntry entry = new LegendEntry();
                    entry.formColor = Color.parseColor("#502ca6");
                    entry.label = "Trending Chart for " + trendingEditText.getText().toString();
                    entries.add(entry);
                    l.setCustom(entries);

                    int colorValue = Color.parseColor("#502ca6");
                    lineDataSet1 = new LineDataSet(dataVals, "Trending Chart for " + trendingEditText.getText().toString());
                    lineDataSet1.setColor(colorValue);
                    lineDataSet1.setCircleColor(colorValue);
                    lineDataSet1.setFillColor(colorValue);
                    lineDataSet1.setValueTextSize(10);
                    lineDataSet1.setCircleHoleColor(colorValue);
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(lineDataSet1);

                    LineData data = new LineData(dataSets);
                    lineChart.setData(data);
                    lineChart.invalidate();

                    Log.v("#ArticleActivity -> ", "Updated");
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

    private ArrayList<Entry> dataValues1() {
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        dataVals.add(new Entry(0, 20));
        dataVals.add(new Entry(1, 24));
        dataVals.add(new Entry(2, 2));
        dataVals.add(new Entry(3, 400));
        dataVals.add(new Entry(4, 208));

        return dataVals;
    }
}
