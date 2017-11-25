package com.example.com.textreader;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/11/24 0024.
 */

public class MarkFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    ArrayList<String>country;
    ArrayList<String>city;
    ArrayList<String>person;
    ArrayList<String>city_country;
    ArrayList<String>person_country;

    public static MarkFragment newInstance(int page,ArrayList<ArrayList<String>> infos) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putStringArrayList("country",infos.get(0));
        args.putStringArrayList("city",infos.get(1));
        args.putStringArrayList("person",infos.get(2));
        args.putStringArrayList("city_country",infos.get(3));
        args.putStringArrayList("person_country",infos.get(4));

        MarkFragment pageFragment = new MarkFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        country=getArguments().getStringArrayList("country");
        city=getArguments().getStringArrayList("city");
        person=getArguments().getStringArrayList("person");
        city_country=getArguments().getStringArrayList("city_country");
        person_country=getArguments().getStringArrayList("person_country");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.markpagefragment_layout, container, false);
        ListView lv=(ListView) view.findViewById(R.id.list);
        ArrayList<HashMap<String,Object>>listItem=new ArrayList<HashMap<String, Object>>();
        switch (mPage){
            case 1://国家
                for(int i=0;i<country.size();i++) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("name", country.get(i));
                    map.put("relation", null);
                    listItem.add(map);
                    SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), listItem, R.layout.item_amark,
                            new String[]{"name", "relation"}, new int[]{R.id.name, R.id.relation});
                    lv.setAdapter(simpleAdapter);
                }
                break;
            case 2://城市
                for(int i=0;i<city.size();i++) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("name", city.get(i));
                    if(city_country.get(i).equals("")){
                        map.put("relation",null);
                    }
                    else{
                        map.put("relation", "从属于"+city_country.get(i));
                    }
                    listItem.add(map);
                    SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), listItem, R.layout.item_amark,
                            new String[]{"name", "relation"}, new int[]{R.id.name, R.id.relation});
                    lv.setAdapter(simpleAdapter);
                }

                break;
            case 3://人
                for(int i=0;i<person.size();i++) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("name", person.get(i));
                    if(person_country.get(i).equals("")){
                        map.put("relation",null);
                    }
                    else{
                        map.put("relation","国籍为"+person_country.get(i));
                    }
                    listItem.add(map);
                    SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), listItem, R.layout.item_amark,
                            new String[]{"name", "relation"}, new int[]{R.id.name, R.id.relation});
                    lv.setAdapter(simpleAdapter);
                }
                break;
        }

        return view;
    }
}

