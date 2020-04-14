package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    List<Fragment> fragments;
    MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init view
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Init fragments
        fragments = new ArrayList<>();
        fragments.add(new PageHome());
        fragments.add(new PageHeadlines());
        fragments.add(new PageTrending());
        fragments.add(new PageBookmark());

        // New adapter for navigation
        BottomNavigationAdapter bottomNavigationAdapter = new BottomNavigationAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(bottomNavigationAdapter);

        // Menu click event
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_headlines:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.nav_trending:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.nav_bookmark:
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

    // Bottom navigation view adapter
    private class BottomNavigationAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public BottomNavigationAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
            super(fm);
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
}
