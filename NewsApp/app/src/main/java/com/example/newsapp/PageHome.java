package com.example.newsapp;


import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class PageHome extends Fragment implements MainActivity.FragmentInterface {
    private ArrayList<NewsCard> newsList;
    private RequestQueue requestQueue = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private NewsCardAdapter newsCardAdapter = null;
    private Geocoder geocoder;

    MaterialCardView materialCardView;
    private ImageView weatherImageView;
    private TextView weatherCityView;
    private TextView weatherStateView;
    private TextView weatherTemperatureView;
    private TextView weatherTypeView;

    private String testDate = "2020-04-22T06:21:17Z";

    String city, state;
    JSONArray responseJsonArray = null;
    boolean isNeedUpdateBookmark = false;
    boolean locationChecked = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v("-->PageHome", "Enter onCreateView");
        final View view = inflater.inflate(R.layout.page_home, container, false);

        MainActivity.showLoader();

        materialCardView = view.findViewById(R.id.weatherCard);
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        weatherImageView = view.findViewById(R.id.weatherImageView);
        weatherCityView = view.findViewById(R.id.weatherCityView);
        weatherStateView = view.findViewById(R.id.weatherStateView);
        weatherTemperatureView = view.findViewById(R.id.weatherTemperatureView);
        weatherTypeView = view.findViewById(R.id.weatherTypeView);
        materialCardView.setVisibility(View.INVISIBLE);

        newsList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity()); // Internet
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh_items); // Pull to refresh
        recyclerView = view.findViewById(R.id.recyclerView); // Display view
        recyclerView.setHasFixedSize(true); // Keep size
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        // Separator
        DividerItemDecoration horizontalDivider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        horizontalDivider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.line_divider));
        recyclerView.addItemDecoration(horizontalDivider);
        newsCardAdapter = new NewsCardAdapter(getActivity(), "Big", newsList);
        recyclerView.setAdapter(newsCardAdapter);

        // Pull refresh function
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCurrentLocation();
                fetchNews();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        Log.v("-->PageHome", "End onCreate");

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("-->PageHome", "Back from other article activity");
        if (resultCode == RESULT_OK) {
            isNeedUpdateBookmark = true;
        }
    }

    @Override
    public void requireUpdate(int requestCode) {
        Log.v("-->PageHome", "Enter requireUpdate");
        if (requestCode == 2) {
            //MainActivity.showLoader();
            //getCurrentLocation();
            //fetchNews();
            locationChecked = true;
        } else {
            isNeedUpdateBookmark = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("-->PageHome", "Enter onResume");

        if (locationChecked) {
            if (isNeedUpdateBookmark && responseJsonArray != null) {
                updateBookmark();
            } else {
                MainActivity.showLoader();
                getCurrentLocation();
                fetchNews();
            }
            isNeedUpdateBookmark = false;
        }
    }

    private void updateBookmark() {
        Log.v("-->PageHome", "updateBookmark");
        newsList.clear();
        for (int i = 0; i < responseJsonArray.length(); i++) {
            JSONObject newsItem = null;
            try {
                newsItem = responseJsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            newsList.add(new NewsCard(newsItem, getActivity(), "other"));
        }
        buildRecyclerView();
    }

    private void getCurrentLocation() {
        Log.v("-->PageHome", "Start get location");
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    List<Address> addresses;
                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                    Double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    Double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                    Log.v("-->PageHome", "Location coordinate: " + latitude.toString() + " " + longitude.toString());
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);

                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();

                        Log.v("-->PageHome", "Location: " + city + " " + state);

                        updateWeatherCard();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void updateWeatherCard() {
                Log.v("-->PageHome", "Start fetch weather");
                String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=bb6deec062930bb99b4524f160a1e291";
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.v("-->PageHome", "Fetched weather");
                            int weatherTemperature = response.getJSONObject("main").getInt("temp");
                            String weatherType = response.getJSONArray("weather").getJSONObject(0).getString("main");

                            Log.v("-->PageHome", "Weather temperature: " + weatherTemperature);
                            Log.v("-->PageHome", "Weather type: " + weatherType);

                            weatherTemperatureView.setText(Integer.toString(weatherTemperature) + "°C");
                            //weatherImageView.setImageResource(getWeatherImageByType(weatherType));
                            weatherTypeView.setText(weatherType);
                            switch (weatherType) {
                                case "Clouds":
                                    weatherImageView.setImageResource(R.drawable.cloudy_weather);
                                    break;
                                case "Clear":
                                    weatherImageView.setImageResource(R.drawable.clear_weather);
                                    break;
                                case "Snow":
                                    weatherImageView.setImageResource(R.drawable.snowy_weather);
                                    break;
                                case "Rain":
                                    weatherImageView.setImageResource(R.drawable.rainy_weather);
                                    break;
                                case "Thunderstorm":
                                    weatherImageView.setImageResource(R.drawable.thunder_weather);
                                    break;
                                default:
                                    weatherImageView.setImageResource(R.drawable.sunny_weather);
                                    weatherTypeView.setText("Sunny");
                            }
                            weatherCityView.setText(city);
                            weatherStateView.setText(state);
                            materialCardView.setVisibility(View.VISIBLE);
                            Log.v("-->PageHome", "Updated weather card");
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

            private int getWeatherImageByType(String weatherType) {
                switch (weatherType) {
                    case "Clouds":
                        return R.drawable.cloudy_weather;
                    case "Clear":
                        return R.drawable.clear_weather;
                    case "Snow":
                        return R.drawable.snowy_weather;
                    case "Rain":
                        return R.drawable.rainy_weather;
                    case "Thunderstorm":
                        return R.drawable.thunder_weather;
                    default:
                        return R.drawable.sunny_weather;
                }
            }
        }, Looper.getMainLooper());
    }

    private void fetchNews() {
        newsList.clear();
        if (newsCardAdapter != null) {
            newsCardAdapter.notifyDataSetChanged();
        }
        MainActivity.showLoader();
        String url = MyUtil.getBackendUrl() + "home";

        Log.v("-->PageHome", "Start fetch news");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("-->PageHome", "Fetched news");
                    responseJsonArray = response.getJSONArray("result");
                    for (int i = 0; i < responseJsonArray.length(); i++) {
                        JSONObject newsItem = responseJsonArray.getJSONObject(i);
                        newsList.add(new NewsCard(newsItem, getActivity(), "other"));
                    }
                    MainActivity.hideLoader();
                    buildRecyclerView();
                    Log.v("-->PageHome", "Updated news");
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

    public void buildRecyclerView() {
        Log.v("-->PageHome", "Start buildRecyclerView");
        newsCardAdapter = new NewsCardAdapter(getActivity(), "Big", newsList);
        recyclerView.setAdapter(newsCardAdapter);
        newsCardAdapter.setOnItemClickListener(new NewsCardAdapter.OnItemClickListener() {
            // Open article
            public void onItemClick(int position) {
                Log.v("-->PageHome", "Open article");
                Intent detailIntent = new Intent(getActivity(), ArticleActivity.class);
                NewsCard newsCard = newsList.get(position);
                detailIntent.putExtra("newsId", newsCard.getNewsId());
                startActivityForResult(detailIntent, 1);
            }

            // Expand dialog
            public void onItemLongClick(final int position) {
                Log.v("-->PageHome", "Expand dialog");
                // Init dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.layout_dialog, null);

                ImageView dialogImage = ((ImageView) view.findViewById(R.id.dialogImage));
                Picasso.with(getActivity()).load(newsList.get(position).getNewsImageUrl()).into(dialogImage);

                TextView dialogTitle = view.findViewById(R.id.dialogTitle);
                dialogTitle.setText(newsList.get(position).getNewsTitle());

                ImageButton imageButtonShare = view.findViewById(R.id.imageButton);
                final ImageButton imageButtonDelete = view.findViewById(R.id.imageButton2);
                imageButtonDelete.setImageResource(getBookmarkIconById(newsList.get(position).getNewsId(), getActivity()));

                dialogBuilder.setView(view);
                final AlertDialog aLertDialog = dialogBuilder.create();
                aLertDialog.show();

                // Click share icon
                imageButtonShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("-->PageHome", "Clicked share icon");
                        Intent intent = MyUtil.getShareIntent(newsList.get(position).getNewsUrl());
                        startActivity(intent);
                    }
                });

                // Click bookmark icon
                imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("-->PageHome", "Clicked bookmark icon");
                        String newsId = newsList.get(position).getNewsId();
                        if (LocalStorage.isInBookmark(newsId, getActivity())) {
                            imageButtonDelete.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
                            removeNewsFromBookmarks(newsId, position);
                        } else {
                            imageButtonDelete.setImageResource(R.drawable.ic_bookmark_red_24dp);
                            addNewsToBookmarks(newsId, position);
                        }
                    }
                });
            }

            // Click bookmark icon on viewpager
            public void onBookmarkClick(int position) {
                String newsId = newsList.get(position).getNewsId();
                if (position != RecyclerView.NO_POSITION) {
                    if (LocalStorage.isInBookmark(newsId, getActivity())) {
                        Log.v("-->PageHome", "Remove news(viewpager)");
                        removeNewsFromBookmarks(newsId, position);
                    } else {
                        Log.v("-->PageHome", "Add news(viewpager)");
                        addNewsToBookmarks(newsId, position);
                    }
                }
            }
        });
    }

    private void addNewsToBookmarks(String newsId, int position) {
        Log.v("-->PageHome", "Add news");
        JSONObject news = new JSONObject();
        try {
            news.put("newsId", newsId);
            news.put("newsUrl", newsList.get(position).getNewsUrl());
            news.put("newsTitle", newsList.get(position).getNewsTitle());
            news.put("newsImageUrl", newsList.get(position).getNewsImageUrl());
            news.put("newsPubDate", newsList.get(position).getNewsPubDate());
            news.put("newsTag", newsList.get(position).getNewsTag());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LocalStorage.insertNews(news, getActivity());
        newsList.get(position).changeImageSource(R.drawable.ic_bookmark_red_24dp);
        newsCardAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), '"' + newsList.get(position).getNewsTitle() + '"' + " was added to bookmarks", Toast.LENGTH_LONG).show();
    }

    private void removeNewsFromBookmarks(String newsId, int position) {
        Log.v("-->PageHome", "Remove news");
        LocalStorage.deleteNews(newsId, getActivity());
        newsList.get(position).changeImageSource(R.drawable.ic_bookmark_border_red_24dp);
        newsCardAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), '"' + newsList.get(position).getNewsTitle() + '"' + " was removed from bookmarks", Toast.LENGTH_LONG).show();
    }

    public static int getBookmarkIconById(String id, Context context) {
        if (LocalStorage.isInBookmark(id, context)) {
            return R.drawable.ic_bookmark_red_24dp;
        } else {
            return R.drawable.ic_bookmark_border_red_24dp;
        }
    }
}
