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
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class PageHeadlines extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    SwipeRefreshLayout mSwipeRefreshLayout = null;


    String[] tabsTitle = {"World", "Business", "Politics", "Sports", "Technology", "Science"};
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

        final FragmentPagerAdapter tabAdapter = new myAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(tabAdapter);

        tabLayout.setupWithViewPager(viewPager);

        listeners.get(0).myAction();





        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.v("tabListener","ddddddddddddddddddddd");
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

    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Boolean isVisible = isVisibleToUser;
        if (isVisible) {
            listeners.get(0).myAction();
        }
    }

     */


    @Override
    public void onResume() {
        super.onResume();
        listeners.get(tabLayout.getSelectedTabPosition()).myAction();
        Log.v("PageHeadlines","***********************************************************");
    }


    private void initFragment() {
        fragments = new ArrayList<Fragment>();
        //"Sports", "Technology", "Science"

        TabFragment fragment1 = new TabFragment("World");
        TabFragment fragment2 = new TabFragment("Business");
        TabFragment fragment3 = new TabFragment("Politics");
        TabFragment fragment4 = new TabFragment("Sports");
        TabFragment fragment5 = new TabFragment("Technology");
        TabFragment fragment6 = new TabFragment("Science");


        listeners.add((MyInterface) fragment1);
        listeners.add((MyInterface) fragment2);
        listeners.add((MyInterface) fragment3);
        listeners.add((MyInterface) fragment4);
        listeners.add((MyInterface) fragment5);
        listeners.add((MyInterface) fragment6);

        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);
        fragments.add(fragment4);
        fragments.add(fragment5);
        fragments.add(fragment6);
    }

    private class myAdapter extends FragmentPagerAdapter {
        private List<Fragment> list;

        public myAdapter(@NonNull FragmentManager fm, List<Fragment> list) {
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
