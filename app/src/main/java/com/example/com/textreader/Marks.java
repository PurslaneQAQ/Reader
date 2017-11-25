package com.example.com.textreader;

import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class Marks extends AppCompatActivity {
    ArrayList<String> country;
    ArrayList<String> city;
    ArrayList<String> person;
    ArrayList<String> city_country;
    ArrayList<String> person_country;
    ArrayList<ArrayList<String>> informations;
    private ImageView back;

    ViewPager mViewPager;
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);
        country=getIntent().getStringArrayListExtra("country");
        city=getIntent().getStringArrayListExtra("city");
        person=getIntent().getStringArrayListExtra("person");
        city_country=getIntent().getStringArrayListExtra("city_country");
        person_country=getIntent().getStringArrayListExtra("person_country");

        informations=new ArrayList<>();
        informations.add(country);
        informations.add(city);
        informations.add(person);
        informations.add(city_country);
        informations.add(person_country);

        back=(ImageView)findViewById(R.id.backbtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mTabLayout=(TabLayout)findViewById(R.id.tabs);
        //Bind the adapter with the mViewPager as well as mTablayout
        MarksPageAdapter myAdapter = new MarksPageAdapter(getSupportFragmentManager(),this,informations);;
        mViewPager.setAdapter(myAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置标签的模式,默认系统模式
    }
}
