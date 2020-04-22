package com.example.newsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class PageHeadlines extends Fragment implements MainActivity.FragmentInterface {
    TabLayout tabLayout;
    ViewPager viewPager;

    String[] tabsTitle = {"World", "Business", "Politics", "Sports", "Technology", "Science"};
    ArrayList<Fragment> fragments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v("-->PageHeadlines", "Enter onCreateView");
        final View view = inflater.inflate(R.layout.page_headlines, container, false);

        Log.v("#PageHeadlines -> ", "Start onCreate");
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.headlinesViewPager);

        initFragment();

        final FragmentPagerAdapter tabAdapter = new TabAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        Log.v("#PageHeadlines -> ", "End onCreate");

        return view;
    }

    @Override
    public void requireUpdate(int requestCode) {
        Log.v("-->PageHeadlines", "Enter requireUpdate");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("-->PageHeadlines", "Enter onResume");
        LocalStorage.getNews(getActivity());
    }

    private void initFragment() {
        Log.v("#PageHeadlines -> ", "Init fragment");

        fragments = new ArrayList<Fragment>();

        TabFragment fragment1 = new TabFragment("World");
        TabFragment fragment2 = new TabFragment("Business");
        TabFragment fragment3 = new TabFragment("Politics");
        TabFragment fragment4 = new TabFragment("Sports");
        TabFragment fragment5 = new TabFragment("Technology");
        TabFragment fragment6 = new TabFragment("Science");

        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);
        fragments.add(fragment4);
        fragments.add(fragment5);
        fragments.add(fragment6);
    }

    private class TabAdapter extends FragmentPagerAdapter {
        private List<Fragment> list;

        public TabAdapter(@NonNull FragmentManager fm, List<Fragment> list) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
