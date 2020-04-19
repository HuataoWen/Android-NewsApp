package com.example.newsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.core.app.ActivityCompat;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.Manifest;

import static android.app.Activity.RESULT_OK;
import static java.time.Month.AUGUST;

public class PageHome extends Fragment {
    private ArrayList<NewsCard> newsList;
    private RequestQueue mRequestQueue = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private BigCardAdapter bigCardAdapter = null;
    private Geocoder geocoder;

    private ImageView weatherImageView;
    private TextView weatherCityView;
    private TextView weatherStateView;
    private TextView weatherTemperatureView;
    private TextView weatherTypeView;


    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    String city, state;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.page_home, container, false);


        weatherImageView = view.findViewById(R.id.weatherImageView);
        weatherCityView = view.findViewById(R.id.weatherCityView);
        weatherStateView = view.findViewById(R.id.weatherStateView);
        weatherTemperatureView = view.findViewById(R.id.weatherTemperatureView);
        weatherTypeView = view.findViewById(R.id.weatherTypeView);

        //Check Location Permission already granted or not
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            // Request Location Permission  locationManager.RemoveUpdates(this);
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        newsList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(getActivity()); // Internet
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_items); // Pull to efresh
        recyclerView = view.findViewById(R.id.recyclerView); // Display view

        // Get news
        fetchNews();

        // Pull refresh function
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mRequestQueue != null) {
                    getCurrentLocation();
                    fetchNews();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void getCurrentLocation() {
        Log.v("#PageHome -> ", "Start get location");
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
                    Log.v("#PageHome -> ", "Location coordinate: " + latitude.toString() + " " + longitude.toString());
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();
                        Log.v("#PageHome -> ", "Location: " + city + " " + state);

                        updateWeatherCard();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void updateWeatherCard() {
                weatherCityView.setText(city);
                weatherStateView.setText(state);
                fetchWeather();
            }

            private void fetchWeather() {
                Log.v("#PageHome -> ", "Start fetch weather");
                String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=bb6deec062930bb99b4524f160a1e291";
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.v("#PageHome -> ", "Fetched weather");
                            int weatherTemperature = response.getJSONObject("main").getInt("temp");
                            String weatherType = response.getJSONArray("weather").getJSONObject(0).getString("main");

                            Log.v("#PageHome -> ", "Weather temperature: " + weatherTemperature);
                            Log.v("#PageHome -> ", "Weather type: " + weatherType);

                            weatherTemperatureView.setText(Integer.toString(weatherTemperature));
                            weatherImageView.setImageResource(getWeatherImageByType(weatherType));
                            weatherTypeView.setText(weatherType);

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
        if (bigCardAdapter != null) {
            bigCardAdapter.notifyDataSetChanged();
        }
        MainActivity.showLoader();

        //String url = "http://10.0.2.2:4000/mobile/home";
        String url = "http://ec2-52-14-208-196.us-east-2.compute.amazonaws.com:4000/mobile/home";

        Log.v("#PageHome -> ", "Start fetch news");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("#PageHome -> ", "Fetched news");
                    JSONArray jsonArray = response.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject newsItem = jsonArray.getJSONObject(i);
                        String newsID = newsItem.getString("newsID");
                        String newsURL = newsItem.getString("newsURL");
                        String newsTitle = newsItem.getString("newsTitle");
                        String newsImageURL = newsItem.getString("newsImageURL");
                        String newsTag = newsItem.getString("newsTag");
                        String newsTime = newsItem.getString("newsTime");


                        String timeDiff = MyUtil.GetTimeDifference(newsTime);

                        Log.v("#PageHome -> ", "Start fetch news");
                        newsList.add(new NewsCard(
                                newsID,
                                newsURL,
                                newsImageURL,
                                newsTitle,
                                newsTag,
                                timeDiff + " ago | " + newsTag,
                                newsTime,
                                getBookmarkIconById(newsID, getActivity())));
                    }
                    MainActivity.hideLoader();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.v("#PageHome -> ", "Back from article activity");
            fetchNews();
        }
    }

    public void buildRecyclerView() {
        Log.v("#PageHome -> ", "Start buildRecyclerView");
        recyclerView.setHasFixedSize(true); // Keep size

        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        bigCardAdapter = new BigCardAdapter(getActivity(), "Big", newsList);
        recyclerView.setLayoutManager(layoutManager);
        // Separator
        DividerItemDecoration horizontalDivider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        horizontalDivider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.line_divider));
        recyclerView.addItemDecoration(horizontalDivider);
        recyclerView.setAdapter(bigCardAdapter);


        bigCardAdapter.setOnItemClickListener(new BigCardAdapter.OnItemClickListener() {
            // Open article
            public void onItemClick(int position) {
                Log.v("#PageHome -> ", "Open article");
                Intent detailIntent = new Intent(getActivity(), ArticleActivity.class);
                NewsCard newsCard = newsList.get(position);
                detailIntent.putExtra("newsID", newsCard.getID());
                startActivityForResult(detailIntent, 1);
            }

            // Expand dialog
            public void onItemLongClick(final int position) {
                Log.v("#PageHome -> ", "Expand dialog");
                // Init dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.layout_dialog, null);
                dialogBuilder.setView(view);

                ImageView dialogImage = ((ImageView) view.findViewById(R.id.dialogImage));
                Picasso.with(getActivity()).load(newsList.get(position).getImageResource()).into(dialogImage);

                TextView dialogTitle = view.findViewById(R.id.dialogTitle);
                dialogTitle.setText(newsList.get(position).getTitle());

                ImageButton imageButtonShare = view.findViewById(R.id.imageButton);
                final ImageButton imageButtonDelete = view.findViewById(R.id.imageButton2);
                imageButtonDelete.setImageResource(getBookmarkIconById(newsList.get(position).getID(), getActivity()));
                final AlertDialog aLertDialog = dialogBuilder.create();
                aLertDialog.show();

                // Click share icon
                imageButtonShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("#PageHome -> ", "Clicked share icon");
                        Toast.makeText(getActivity(), "TODO:: Share article on Twitter", Toast.LENGTH_SHORT).show();
                    }
                });

                // Click bookmark icon
                imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("#PageHome -> ", "Clicked bookmark icon");
                        String newsId = newsList.get(position).getID();
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
                String newsId = newsList.get(position).getID();
                if (position != RecyclerView.NO_POSITION) {
                    if (LocalStorage.isInBookmark(newsId, getActivity())) {
                        Log.v("#PageHome -> ", "Remove news(viewpager)");
                        removeNewsFromBookmarks(newsId, position);
                    } else {
                        Log.v("#PageHome -> ", "Add news(viewpager)");
                        addNewsToBookmarks(newsId, position);
                    }
                }
            }
        });
    }

    private void addNewsToBookmarks(String newsId, int position) {
        Log.v("#PageHome -> ", "Add news");
        JSONObject news = new JSONObject();
        try {
            news.put("id", newsId);
            news.put("url", newsList.get(position).getIUrl());
            news.put("title", newsList.get(position).getTitle());
            news.put("urlToImage", newsList.get(position).getImageResource());
            news.put("publishDate", newsList.get(position).getPublishDate());
            news.put("tag", newsList.get(position).getTag());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LocalStorage.insertNews(news, getActivity());
        newsList.get(position).changeImageSource(R.drawable.ic_bookmark_red_24dp);
        bigCardAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), newsList.get(position).getTitle() + " was added to bookmarks", Toast.LENGTH_LONG).show();
    }

    private void removeNewsFromBookmarks(String newsId, int position) {
        Log.v("#PageHome -> ", "Remove news");
        LocalStorage.deleteNews(newsId, getActivity());
        newsList.get(position).changeImageSource(R.drawable.ic_bookmark_border_red_24dp);
        bigCardAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), newsList.get(position).getTitle() + " was removed from bookmarks", Toast.LENGTH_LONG).show();
    }

    public static int getBookmarkIconById(String id, Context context) {
        if (LocalStorage.isInBookmark(id, context)) {
            return R.drawable.ic_bookmark_red_24dp;
        } else {
            return R.drawable.ic_bookmark_border_red_24dp;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v("#PageHome -> ", "Request get location permission");
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:

                // Check Location permission is granted or not
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Location  permission granted", Toast.LENGTH_SHORT).show();
                    getCurrentLocation();
                } else {
                    Toast.makeText(getActivity(), "Location  permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
