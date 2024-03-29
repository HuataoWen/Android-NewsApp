package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static LinearLayout mainActivityLoader;

    Toolbar toolbar;
    private ArrayAdapter<String> suggestionsAdapter;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private String searchKeyword;
    private RequestQueue requestQueue;

    private ViewPager viewPager;
    private List<Fragment> fragments;
    private BottomNavigationView bottomNavigationView;
    private MenuItem menuItem;

    private List<FragmentInterface> fragmentInterfaces = null;

    private int selectedPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("-->MainActivity", "Enter onCreate");

        mainActivityLoader = findViewById(R.id.mainActivityLoader);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestQueue = Volley.newRequestQueue(this);

        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        initFragments();

        BottomNavigationAdapter bottomNavigationAdapter = new BottomNavigationAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(bottomNavigationAdapter);

        checkLocationPermission();

        // Menu click event
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        selectedPage = 0;
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_headlines:
                        selectedPage = 1;
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.nav_trending:
                        selectedPage = 2;
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.nav_bookmark:
                        selectedPage = 3;
                        viewPager.setCurrentItem(3);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        // Menu swipe event
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // The first time to start app
                if (menuItem == null) {
                    menuItem = bottomNavigationView.getMenu().getItem(0);
                }
                menuItem.setChecked(false); // Deselect previous page
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true); // Select current page
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("-->MainActivity", "Enter onResume");
        if (fragmentInterfaces != null){
            fragmentInterfaces.get(selectedPage).requireUpdate(1);
        }

        // Test
        //fragmentInterfaces.get(2).requireUpdate(1);
    }

    private void initFragments() {
        fragments = new ArrayList<>();
        fragmentInterfaces = new ArrayList<>();

        Fragment fragment = new PageHome();
        fragmentInterfaces.add((FragmentInterface) fragment);
        fragments.add(fragment);

        fragment = new PageHeadlines();
        fragmentInterfaces.add((FragmentInterface) fragment);
        fragments.add(fragment);

        fragment = new PageTrending();
        fragmentInterfaces.add((FragmentInterface) fragment);
        fragments.add(fragment);

        fragment = new PageBookmark();
        fragmentInterfaces.add((FragmentInterface) fragment);
        fragments.add(fragment);
    }

    private void checkLocationPermission() {
        //Check Location Permission already granted or not
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.v("-->MainActivity", "Location permission is granted");
            fragmentInterfaces.get(selectedPage).requireUpdate(2);
        } else {
            Log.v("-->MainActivity", "Location permission not granted. Requesting");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v("-->MainActivity", "Enter onRequestPermissionsResult");
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                // Check Location permission is granted or not
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("-->MainActivity", "Location  permission granted");
                    fragmentInterfaces.get(selectedPage).requireUpdate(2);
                } else {
                    Log.v("-->MainActivity", "Location  permission denied");
                }
                break;
        }
    }

    public interface FragmentInterface {
        void requireUpdate(int requestCode);
    }

    private class BottomNavigationAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public BottomNavigationAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_main, menu);
        MenuItem searchMenu = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenu.getActionView();
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setDropDownBackgroundResource(R.color.white);
        searchAutoComplete.setThreshold(3);

        suggestionsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
        searchAutoComplete.setAdapter(suggestionsAdapter);

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString = (String) adapterView.getItemAtPosition(itemIndex);
                Log.v("-->MainActivity", "Choose search keyword" + queryString);
                searchAutoComplete.setText("" + queryString);
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    Log.v("-->MainActivity", "Fetch suggestions for " + searchKeyword);
                    updateSuggestions();
                }
                return false;
            }
        });

        // Below event is triggered when submit search query.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.v("-->MainActivity", "Open SearchActivity with search keyword " + searchKeyword);
                Intent detailIntent = new Intent(MainActivity.this, SearchActivity.class);
                detailIntent.putExtra("keyword", query);
                startActivityForResult(detailIntent, 1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 1) {
                    Log.v("-->MainActivity", "Get new search keyword " + searchKeyword);
                    searchKeyword = newText;
                    handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                    handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void updateSuggestions() {
        Log.v("-->MainActivity", "Fetch suggestions for " + searchKeyword);
        String url = "https://xiaobudai.cognitiveservices.azure.com/bing/v7.0/suggestions?mkt=en-US&q=" + searchKeyword;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("-->MainActivity", "Fetched suggestions");
                    JSONArray jsonArray = response.getJSONArray("suggestionGroups").getJSONObject(0).getJSONArray("searchSuggestions");
                    suggestionsAdapter.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject hit = jsonArray.getJSONObject(i);
                        suggestionsAdapter.add(jsonArray.getJSONObject(i).getString("displayText"));
                    }
                    suggestionsAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Ocp-Apim-Subscription-Key", "8cfdb72a5dfe44ba94f7d66f8a598f0a");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public static void showLoader() {
        mainActivityLoader.setVisibility(View.VISIBLE);
    }

    public static void hideLoader() {
        mainActivityLoader.setVisibility(View.INVISIBLE);
    }
}
