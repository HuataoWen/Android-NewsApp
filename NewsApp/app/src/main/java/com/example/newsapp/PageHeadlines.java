package com.example.newsapp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class PageHeadlines extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    SwipeRefreshLayout mSwipeRefreshLayout = null;


    String[] tabsTitle = {"World", "Business", "Politics"};
    ArrayList<Fragment> fragments;
    ArrayList<MyInterface> listeners = new ArrayList<>();

    public interface MyInterface {
        void myAction() ;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_headlines, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.headlinesViewPager);
        mSwipeRefreshLayout = view.findViewById(R.id.headlinesSwipeRefresh);

        initFragment();

        final FragmentPagerAdapter tabadapter = new myAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(tabadapter);
        tabLayout.setupWithViewPager(viewPager);

        listeners.get(0).myAction();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                listeners.get(tabLayout.getSelectedTabPosition()).myAction();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                listeners.get(tabLayout.getSelectedTabPosition()).myAction();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listeners.get(tabLayout.getSelectedTabPosition()).myAction();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void initFragment() {
        fragments = new ArrayList<Fragment>();

        TabFragment fragment1 = new TabFragment("World");
        TabFragment fragment2 = new TabFragment("Business");
        TabFragment fragment3 = new TabFragment("Politics");

        listeners.add((MyInterface) fragment1);
        listeners.add((MyInterface) fragment2);
        listeners.add((MyInterface) fragment3);

        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);
    }

    private class myAdapter extends FragmentPagerAdapter {
        private List<Fragment> list;

        public myAdapter(@NonNull FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabsTitle[position];
        }
    }
}
