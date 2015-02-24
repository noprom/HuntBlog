package com.noprom.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.noprom.app.fragment.TabFragment;


/**
 * Created by noprom on 2015/2/21.
 */
public class TabAdapter extends FragmentPagerAdapter {

    public static String[] titles = new String[]{"资讯", "热点", "博客", "推荐"};


    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        TabFragment fragment = new TabFragment(position);
        return fragment;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return titles[position];
    }
}
