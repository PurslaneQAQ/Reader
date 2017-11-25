package com.example.com.textreader;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/24 0024.
 */

public class MarksPageAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"国家","城市","人物"};
    Context context;
    ArrayList<ArrayList<String>> informations;

    public MarksPageAdapter(FragmentManager fm, Context context, ArrayList<ArrayList<String>> infos) {
        super(fm);
        this.context = context;
        this.informations=infos;
    }

    @Override
    public Fragment getItem(int position) {
        return MarkFragment.newInstance(position + 1,informations);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}



