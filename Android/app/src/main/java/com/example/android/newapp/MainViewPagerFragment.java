package com.example.android.newapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by admin on 9/10/15.
 */
public class MainViewPagerFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager, null);
        final ViewPager vpTabs = (ViewPager) view.findViewById(R.id.vpTabs);
        final ActionBar actionBar = MainActivity.actionBar;

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.removeAllTabs();
        vpTabs.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                switch (position) {
                    case 0:
                        return new ExplorePlacesFragment();
                    case 1:
                        return new SearchFragment();
                    case 2:
                        return new SavedLocationsFragment();
                }
                return new Fragment();
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        vpTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
                int position = tab.getPosition();
                vpTabs.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

            }

        };


        actionBar.addTab(actionBar.newTab().setText("Explore").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Nearby").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Favourite").setTabListener(tabListener));
        vpTabs.setOffscreenPageLimit(1);

        return view;
    }
}
