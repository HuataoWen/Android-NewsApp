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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PageTrending extends Fragment {
    LineChart lineChart;
    Legend l;
    EditText trendingEditText;
    LineDataSet lineDataSet1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_trending, container, false);

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
                    List<LegendEntry> entries = new ArrayList<>();
                    LegendEntry entry = new LegendEntry();
                    entry.formColor = Color.parseColor("#502ca6");
                    entry.label = "Trending Chart for " + trendingEditText.getText().toString();
                    entries.add(entry);
                    l.setCustom(entries);

                    int colorValue = Color.parseColor("#502ca6");
                    lineDataSet1 = new LineDataSet(dataValues2(), "Trending Chart for " + trendingEditText.getText().toString());
                    lineDataSet1.setColor(colorValue);
                    lineDataSet1.setCircleColor(colorValue);
                    lineDataSet1.setFillColor(colorValue);
                    lineDataSet1.setValueTextSize(10);
                    lineDataSet1.setCircleHoleColor(colorValue);
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(lineDataSet1);

                    LineData data = new LineData(dataSets);
                    lineChart.setData(data);
                }
                return false;
            }
        });

        return view;
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

    private ArrayList<Entry> dataValues2() {
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        dataVals.add(new Entry(0, 120));
        dataVals.add(new Entry(1, 124));
        dataVals.add(new Entry(2, 12));
        dataVals.add(new Entry(3, 400));
        dataVals.add(new Entry(4, 208));

        return dataVals;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.v("Trending refresh", "ok");
        }
    }
}
