package com.example.com.textreader;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.TextAppearanceSpan;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReadActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private TextView txttitle;
    private TextView showtext;
    private TextView readprogress;
    private ImageView menu;
    private ImageView back;

    private GestureDetector mGestureDetector;

    private PopupWindow mPopupWindow;
    //private String textSize;
    //private EditText size;

    private TextView showmarks;
    String selectedText;
    SpannableStringBuilder builder;

    int tempmin;
    int tempmax;

    ArrayList<String> country;
    ArrayList<String> city;
    ArrayList<String> person;
    ArrayList<String> city_country;
    ArrayList<String> person_country;

    //拿来标记设过的span的位置
    ArrayList<ArrayList<Integer>> country_pos;
    ArrayList<ArrayList<Integer>> city_pos;
    ArrayList<ArrayList<Integer>> person_pos;
    ArrayList<Integer> tempcountrymin;
    ArrayList<Integer> tempcountrymax;
    ArrayList<Integer> tempcitymin;
    ArrayList<Integer> tempcitymax;
    ArrayList<Integer> temppersonmin;
    ArrayList<Integer> temppersonmax;

    BookPageFactory factory;
    File file;
    File dir;
    boolean iflastpage=false;

    //if marked
    boolean []changed = {false, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        //各种初始化
        menu=(ImageView)findViewById(R.id.menubtn);
        back=(ImageView)findViewById(R.id.backbtn);
        mGestureDetector = new GestureDetector(this,this);

        factory=new BookPageFactory();
        country=new ArrayList<String>();
        city=new ArrayList<String>();
        person=new ArrayList<String>();
        city_country=new ArrayList<String>();
        person_country=new ArrayList<String>();

        country_pos = new ArrayList<>();
        city_pos=new ArrayList<>();
        person_pos=new ArrayList<>();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initMenu();
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPopupWindow.isShowing()) {
                    show();
                }
                else {
                    mPopupWindow.dismiss();
                }
            }
        });

        String bookpath=getIntent().getStringExtra("filepath");
        //File dir = Environment.getExternalStorageDirectory();
        //file = new File(dir, bookpath);
        file = new File(bookpath);
        dir = new File(getExternalFilesDir(file.toString().replace("/", "").replace(".txt", "")).toString());
        String txtname = file.getName().substring(0, file.getName().lastIndexOf("."));

        txttitle=(TextView)findViewById(R.id.txttitle);
        txttitle.setText(txtname);

        showtext=(TextView)findViewById(R.id.showtext);
        readprogress=(TextView)findViewById(R.id.progress);

        try {
            factory.openBook(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //为showtext设定手势监听 是为了不与长按冲突  所以设置onsingletapup 在最下边实现
        showtext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });


        //第一次加载textview
        showtext.postDelayed(new Runnable() {
            @Override
            public void run() {
                tempcountrymin=new ArrayList<Integer>();
                tempcountrymax=new ArrayList<Integer>();
                tempcitymin=new ArrayList<Integer>();
                tempcitymax=new ArrayList<Integer>();
                temppersonmin=new ArrayList<Integer>();
                temppersonmax=new ArrayList<Integer>();

                long page = 0;
//                File page_file = new File(dir, "/page");
//                File country_file = new File(dir, "/note/country");
//                File city_file = new File(dir, "/note/city");
//                File person_file = new File(dir, "note/person");
                if(!dir.getParentFile().exists()){
                    dir.getParentFile().mkdir();
                }
//                if(page_file.exists()){
//                    try{
//                        FileInputStream is = new FileInputStream(page_file);
//                        byte[] arrayOfByte = new byte[is.available()];
//                        is.read(arrayOfByte);
//                        String page_js = new String(arrayOfByte);
//                        JSONObject jo = new JSONObject(page_js);
//                        JSONArray ja = jo.getJSONArray("read");
//                        if (ja.getJSONObject(0).getString("type").equals("page")) {
//                            page = ja.getJSONObject(0).getLong("page");
//                        }
//                    }catch(IOException e){
//                        System.out.println("can not get page");
//                    }catch(JSONException e){
//                        System.out.println("Page json build failed!");
//                        e.printStackTrace();
//                    }
//                }
//                if(!country_file.getParentFile().exists()) {
//                    country_file.getParentFile().mkdirs();
//                    System.out.println("Create dirs");
//                }
                if(dir.exists()){
                    try{
                        FileInputStream is = new FileInputStream(dir);
                        byte[] arrayOfByte = new byte[is.available()];
                        is.read(arrayOfByte);
                        String country_js = new String(arrayOfByte);
                        JSONObject jo = new JSONObject(country_js);
                        JSONArray ja = jo.getJSONArray("read");
                        System.out.println(ja.getJSONObject(0).getJSONArray("country").toString());
                        if (ja.getJSONObject(0).getString("type").equals("country")) {
                            JSONArray jCountry = ja.getJSONObject(0).getJSONArray("country");
                            for(int i = 0; i < jCountry.length(); i++) {
                                System.out.println(jCountry.getJSONObject(i).getJSONArray("country_pos"));
                                country.add(jCountry.getJSONObject(i).getString("country_name"));
                                ArrayList<Integer> tempcountrypos = new ArrayList<>();
                                for(int j = 0; j < jCountry.getJSONObject(i).getJSONArray("country_pos").length();j++){
                                    System.out.println("size is "+ jCountry.getJSONObject(i).getJSONArray("country_pos").length());
                                    tempcountrypos.add(jCountry.getJSONObject(i).getJSONArray("country_pos").getInt(j));
                                    tempcountrymin.add(jCountry.getJSONObject(i).getJSONArray("country_pos").getInt(j));
                                    tempcountrymax.add(jCountry.getJSONObject(i).getJSONArray("country_pos").getInt(j) + country.get(i).length());
                                }
                                country_pos.add(tempcountrypos);
                            }
                        }

                        System.out.println(ja.getJSONObject(1).getJSONArray("city").toString());
                        if (ja.getJSONObject(0).getString("type").equals("country")) {
                            JSONArray jCountry = ja.getJSONObject(0).getJSONArray("country");
                            for (int i = 0; i < jCountry.length(); i++) {
                                System.out.println(jCountry.getJSONObject(i).getJSONArray("country_pos"));
                                country.add(jCountry.getJSONObject(i).getString("country_name"));
                                ArrayList<Integer> tempcountrypos = new ArrayList<>();
                                for (int j = 0; j < jCountry.getJSONObject(i).getJSONArray("country_pos").length(); j++) {
                                    System.out.println("size is " + jCountry.getJSONObject(i).getJSONArray("country_pos").length());
                                    tempcountrypos.add(jCountry.getJSONObject(i).getJSONArray("country_pos").getInt(j));
                                    tempcountrymin.add(jCountry.getJSONObject(i).getJSONArray("country_pos").getInt(j));
                                    tempcountrymax.add(jCountry.getJSONObject(i).getJSONArray("country_pos").getInt(j) + country.get(i).length());
                                }
                                country_pos.add(tempcountrypos);
                            }
                        }
                    }catch(JSONException e){
                        System.out.println("C Json build failed!");
                        e.printStackTrace();
                    }
                    catch(IOException e){
                        System.out.println("Open country file failed!");
                    }
                }
//                if(country_file.exists()) {
//                    try{
//                        FileInputStream is = new FileInputStream(country_file);
//                        byte[] arrayOfByte = new byte[is.available()];
//                        is.read(arrayOfByte);
//                        String country_js = new String(arrayOfByte);
//                        JSONObject jo = new JSONObject(country_js);
//                        JSONArray ja = jo.getJSONArray("read");
//                        System.out.println(ja.getJSONObject(0).getJSONArray("country").toString());
//                        if (ja.getJSONObject(0).getString("type").equals("country")) {
//                            JSONArray jCountry = ja.getJSONObject(0).getJSONArray("country");
//                            for(int i = 0; i < jCountry.length(); i++) {
//                                System.out.println(jCountry.getJSONObject(i).getJSONArray("country_pos"));
//                                country.add(jCountry.getJSONObject(i).getString("country_name"));
//                                ArrayList<Integer> tempcountrypos = new ArrayList<>();
//                                for(int j = 0; j < jCountry.getJSONObject(i).getJSONArray("country_pos").length();j++){
//                                    System.out.println("size is "+ jCountry.getJSONObject(i).getJSONArray("country_pos").length());
//                                    tempcountrypos.add(jCountry.getJSONObject(i).getJSONArray("country_pos").getInt(j));
//                                    tempcountrymin.add(jCountry.getJSONObject(i).getJSONArray("country_pos").getInt(j));
//                                    tempcountrymax.add(jCountry.getJSONObject(i).getJSONArray("country_pos").getInt(j) + country.get(i).length());
//                                }
//                                country_pos.add(tempcountrypos);
//                            }
//                        }
//                    }catch(JSONException e){
//                        System.out.println("C Json build failed!");
//                        e.printStackTrace();
//                    }catch(IOException e){
//                        System.out.println("Open country file failed!");
//                    }
//                }
//                if(city_file.exists()) {
//                    try{
//                        FileInputStream is = new FileInputStream(city_file);
//                        byte[] arrayOfByte = new byte[is.available()];
//                        is.read(arrayOfByte);
//                        String city_js = new String(arrayOfByte);
//                        JSONObject jo = new JSONObject(city_js);
//                        JSONArray ja = jo.getJSONArray("read");
//                        System.out.println(ja.getJSONObject(0).getJSONArray("city").get(0).toString());
//                        if (ja.getJSONObject(0).getString("type").equals("city")) {
//                            JSONArray jCity = ja.getJSONObject(0).getJSONArray("city");
//                            for(int i = 0; i < jCity.length(); i++) {
//                                System.out.println(jCity.getJSONObject(i).getJSONArray("city_pos"));
//                                city.add(jCity.getJSONObject(i).getString("city_name"));
//                                city_country.add(jCity.getJSONObject(i).getString("city_country"));
//                                ArrayList<Integer> tempcitypos = new ArrayList<>();
//                                for(int j = 0; j < jCity.getJSONObject(i).getJSONArray("city_pos").length();j++){
//                                    System.out.println("size is "+ jCity.getJSONObject(i).getJSONArray("city_pos").length());
//                                    tempcitypos.add(jCity.getJSONObject(i).getJSONArray("city_pos").getInt(j));
//                                    tempcitymin.add(jCity.getJSONObject(i).getJSONArray("city_pos").getInt(j));
//                                    tempcitymax.add(jCity.getJSONObject(i).getJSONArray("city_pos").getInt(j) + city.get(i).length());
//                                }
//                                city_pos.add(tempcitypos);
//                            }
//                        }
//                    }catch(JSONException e){
//                        System.out.println("Json build failed!");
//                        e.printStackTrace();
//                    }catch(IOException e){
//                        System.out.println("Open country file failed!");
//                    }
//                }
//                if(person_file.exists()) {
//                    try{
//                        FileInputStream is = new FileInputStream(person_file);
//                        byte[] arrayOfByte = new byte[is.available()];
//                        is.read(arrayOfByte);
//                        String city_js = new String(arrayOfByte);
//                        JSONObject jo = new JSONObject(city_js);
//                        JSONArray ja = jo.getJSONArray("read");
//                        System.out.println(ja.getJSONObject(0).getJSONArray("person").get(0).toString());
//                        if (ja.getJSONObject(0).getString("type").equals("person")) {
//                            JSONArray jPerson = ja.getJSONObject(0).getJSONArray("person");
//                            for (int i = 0; i < jPerson.length(); i++) {
//                                System.out.println(jPerson.getJSONObject(i).getJSONArray("person_pos"));
//                                person.add(jPerson.getJSONObject(i).getString("person_name"));
//                                person_country.add(jPerson.getJSONObject(i).getString("person_country"));
//                                ArrayList<Integer> temppersonpos = new ArrayList<>();
//                                for (int j = 0; j < jPerson.getJSONObject(i).getJSONArray("person_pos").length(); j++) {
//                                    System.out.println("size is " + jPerson.getJSONObject(i).getJSONArray("person_pos").length());
//                                    temppersonpos.add(jPerson.getJSONObject(i).getJSONArray("person_pos").getInt(j));
//                                    temppersonmin.add(jPerson.getJSONObject(i).getJSONArray("person_pos").getInt(j));
//                                    temppersonmax.add(jPerson.getJSONObject(i).getJSONArray("person_pos").getInt(j) + person.get(i).length());
//                                }
//                                person_pos.add(temppersonpos);
//                            }
//                        }
//                    }catch(JSONException e){
//                        System.out.println("Json build failed!");
//                        e.printStackTrace();
//                    }catch(IOException e){
//                        System.out.println("Open country file failed!");
//                    }
//                }

                float v = showtext.getHeight() / showtext.getLineHeight();
                factory.setLenH((int) v);//行数
                factory.setLenW((int) (showtext.getWidth() / showtext.getTextSize()));//宽能容纳的文字数
                factory.loadSentences();
                showtext.setText(factory.setCurrentPageNum(page));
                builder = new SpannableStringBuilder(showtext.getText().toString());
                showtext.setText(builder);
                readprogress.setText("当前为第"+factory.GetCurrentPageNum()+"页");
                refreshSpan();

            }
        }, 300);

        showtext.setTextIsSelectable(true);
        showtext.setCustomSelectionActionModeCallback(new ActionMode.Callback(){
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater menuInflater = actionMode.getMenuInflater();
                menu.clear();
                menuInflater.inflate(R.menu.longclicktextmenu, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.markedas) {
                    String[] choices = {"国家", "城市", "人"};
                    //包含多个选项的对话框
                    AlertDialog dialog = new AlertDialog.Builder(ReadActivity.this)
                            .setTitle("标记为")
                            .setItems(choices, onselect).create();
                    dialog.show();

                    if (showtext.isFocused()) {
                        final int selStart = showtext.getSelectionStart();
                        final int selEnd = showtext.getSelectionEnd();
                        tempmin=Math.max(0, Math.min(selStart, selEnd));
                        tempmax=Math.max(0, Math.max(selStart, selEnd));
                    }

                    selectedText = showtext.getText().subSequence(tempmin, tempmax).toString();
                }
                return true;
            }
            //选择了“标记为”之后的操作
            DialogInterface.OnClickListener onselect = new DialogInterface.OnClickListener() {
                int i;
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    changed[which] = true;
                    boolean finished = false;
                    System.out.println("Changed is " + which);
                    int sentense_id = 0;
                    while(factory.sentences.get(sentense_id) < (int)(factory.getBegin()/2 + tempmin)){
                        sentense_id++;
                        if(sentense_id >= factory.sentences.size())break;
                        System.out.println("sentense id is "+ sentense_id + "number is " + factory.sentences.get(sentense_id) + "compared with" + (int)(factory.getBegin()/2 + tempmin));
                    }
                    sentense_id = sentense_id - 1;
                    System.out.println("sentence is " + factory.getSentence(sentense_id));

                    switch (which) {
                        case 0://标记为国家
                            boolean exists=false;
                            for(i=0;i<country.size();i++){
                                if(country.get(i).equals(selectedText)){
                                    exists=true;
                                    break;
                                }
                            }
                            if(!exists||country.size()==0) {
                                country.add(selectedText);
                                ArrayList<Integer> new_country = new ArrayList<>();
                                new_country.add(tempmin + (int)factory.getBegin());
                                country_pos.add(new_country);
                                System.out.println("new country has been added, now there are " + country_pos.size() + "countries");
                            }
                            else{
                                country_pos.get(i).add(tempmin + (int)factory.getBegin());
                            }
                            tempcountrymin.add(tempmin + (int)factory.getBegin());
                            tempcountrymax.add(tempmax + (int)factory.getBegin());
                            storeTag(0);
                            builder.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.countryspan)), tempmin, tempmax, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            break;
                        case 1://标记为城市
                            boolean exists2=false;
                            for(i=0;i<city.size();i++){
                                if(city.get(i).equals(selectedText)){
                                    exists2=true;
                                    break;
                                }
                            }
                            if(!exists2||city.size()==0){
                                city.add(selectedText);
                                ArrayList<Integer> new_city = new ArrayList<>();
                                new_city.add(tempmin + (int)factory.getBegin());
                                city_pos.add(new_city);
                            }
                            else
                                city_pos.get(i).add(tempmin  + (int)factory.getBegin());
                            tempcitymin.add(tempmin  + (int)factory.getBegin());
                            tempcitymax.add(tempmax  + (int)factory.getBegin());
                            builder.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.cityspan)), tempmin,tempmax, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            if(city.size()==0){
                                city_country.add("");
                                storeTag(1);
                                Toast.makeText(ReadActivity.this, "你还没有标注过一个国家", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String[] array =new String[country.size()];
                                country.toArray(array);
                                AlertDialog setcityrelation=new AlertDialog.Builder(ReadActivity.this)
                                        .setTitle("设置关系")
                                        .setItems(array, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                city_country.add(country.get(which));
                                                storeTag(1);
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                city_country.add("");
                                                storeTag(1);
                                            }
                                        })
                                        .create();
                                setcityrelation.show();
                            }

                            break;
                        case 2://标记为人
                            boolean exists3=false;
                            for(i=0;i<person.size();i++){
                                if(person.get(i).equals(selectedText)){
                                    exists3=true;
                                    break;
                                }
                            }
                            if(!exists3||person.size()==0){
                                person.add(selectedText);
                                ArrayList<Integer> new_person = new ArrayList<>();
                                new_person.add(tempmin  + (int)factory.getBegin());
                                person_pos.add(new_person);
                            }
                            else
                                person_pos.get(i).add(tempmin  + (int)factory.getBegin());
                            temppersonmin.add(tempmin + (int)factory.getBegin());
                            temppersonmax.add(tempmax + (int)factory.getBegin());
                            builder.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.personspan)), tempmin, tempmax, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            if(country.size()==0){
                                person_country.add("");
                                storeTag(2);
                                Toast.makeText(ReadActivity.this, "你还没有标注过一个国家", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String[] array1 =new String[country.size()];
                                country.toArray(array1);
                                AlertDialog setpersonrelation=new AlertDialog.Builder(ReadActivity.this)
                                        .setTitle("设置关系")
                                        .setItems(array1, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                person_country.add(country.get(which));
                                                storeTag(2);
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                person_country.add("");
                                                storeTag(2);
                                            }
                                        })
                                        .create();
                                setpersonrelation.show();
                            }
                            break;
                    }
                    showtext.setText(builder);
                }
            };

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });

//        size.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                textSize = size.getText().toString();
//                if (!showtext.getText().toString().equals("")) {
//                    showtext.setTextSize(Integer.parseInt(textSize));
//                }
//                showtext.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        float v = showtext.getHeight() / showtext.getLineHeight();
//                        factory.setLenH((int) v);//行数
//                        factory.setLenW((int) (showtext.getWidth() / showtext.getTextSize()));//宽能容纳的文字数
//                        showtext.setText(factory.readPageUTF8());
//                    }
//                }, 300);
//
//            }
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });

    }
    //实例化PopupWindow创建菜单
    private void initMenu() {
        //获取LayoutInflater实例
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        //获取弹出菜单的布局
        View layout = inflater.inflate(R.layout.bottompopupmenu, null);
        //设置popupWindow的布局
        mPopupWindow = new PopupWindow(layout, WindowManager.LayoutParams.MATCH_PARENT, 420);
        mPopupWindow.setFocusable(true);
        //MyFontSizePicker fontsize = (MyFontSizePicker) layout.findViewById(R.id.sizepicker);
       // size = fontsize.FindEditText();
       // textSize=size.getText().toString();
        showmarks=(TextView)layout.findViewById(R.id.showmarks);
        showmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ReadActivity.this,Marks.class);
                intent.putStringArrayListExtra("country",country);
                intent.putStringArrayListExtra("city",city);
                intent.putStringArrayListExtra("person",person);
                intent.putStringArrayListExtra("city_country",city_country);
                intent.putStringArrayListExtra("person_country",person_country);
                startActivity(intent);
                mPopupWindow.dismiss();
            }
        });
    }

    //显示菜单
    private void show() {
        //设置位置
        mPopupWindow.showAtLocation(this.findViewById(R.id.showtext), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置在屏幕中的显示位置
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // TODO Auto-generated method stub
        //向后滑动
        if (e1.getX() - e2.getX() > 75) {
            turn2NextPage();
        }
        // 向前滑动
        if (e2.getX() - e1.getX() > 75){
            turn2PrePage();
        }
        return false;
    }

    public void turn2NextPage(){
        String tempstring=factory.nextPage();//已经读了 当前页面已经为下一页了
        String currentstring=showtext.getText().toString();
        builder=new SpannableStringBuilder(tempstring);
        if(tempstring.equals(currentstring)){
            iflastpage=true;
            Toast.makeText(ReadActivity.this, "已经是最后一页了！", Toast.LENGTH_SHORT).show();
        }
        showtext.setText(tempstring);
        storePage(factory.GetCurrentPageNum());
        refreshSpan();
        readprogress.setText("当前为第"+factory.GetCurrentPageNum()+"页");
    }

    public void turn2PrePage(){
        String tempstring=factory.prePage();//已经读了 currentpage就是前一页的页码
        String nowstring=showtext.getText().toString();
        builder=new SpannableStringBuilder(tempstring);
        if(tempstring.equals(nowstring)){
            Toast.makeText(ReadActivity.this, "已经是第一页了！", Toast.LENGTH_SHORT).show();
        }
        showtext.setText(tempstring);
        storePage(factory.GetCurrentPageNum());
        refreshSpan();
        readprogress.setText("当前为第"+factory.GetCurrentPageNum()+"页");
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        //上一页 下一页
        if(e.getX()>showtext.getWidth()/2+100){//下一页
            turn2NextPage();

            //if(factory.GetCurrentPageNum()==factory.GetCurrentAllPages()&&!iflastpage){//如果现在的页面等于读出的目前总页数 则 这一页没加载过
//                            country_min.add(tempcountrymin);
//                            country_max.add(tempcountrymax);
//                            city_min.add(tempcitymin);
//                            city_max.add(tempcitymax);
//                            person_min.add(temppersonmin);
//                            person_max.add(temppersonmax);
//
//                            tempcountrymin=new ArrayList<Integer>();
//                            tempcountrymax=new ArrayList<Integer>();
//                            tempcitymin=new ArrayList<Integer>();
//                            tempcitymax=new ArrayList<Integer>();
//                            temppersonmin=new ArrayList<Integer>();
//                            temppersonmax=new ArrayList<Integer>();
//
//                            builder=new SpannableStringBuilder(tempstring);
                        // }
                        //if(factory.GetCurrentPageNum()<factory.GetCurrentAllPages()||iflastpage){//这一页加载过
//                            builder=new SpannableStringBuilder(tempstring);
//                            int currentpage=(int)factory.GetCurrentPageNum();
//                            for(int i=0;i<country_min.get(currentpage-1).size();i++){
//                                builder.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.countryspan)), country_min.get(currentpage-1).get(i), country_max.get(currentpage-1).get(i), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            }
//                            for(int i=0;i<city_min.get(currentpage-1).size();i++){
//                                builder.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.cityspan)), city_min.get(currentpage-1).get(i), city_max.get(currentpage-1).get(i), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            }
//                            for(int i=0;i<person_min.get(currentpage-1).size();i++){
//                                builder.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.personspan)), person_min.get(currentpage-1).get(i),person_max.get(currentpage-1).get(i), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            }
                        // }
        }
        else if(e.getX()<showtext.getWidth()/2-100){//上一页
            turn2PrePage();
//                        else{
//                            int currentpage=(int)factory.GetCurrentPageNum();
//                            for(int i=0;i<country_min.get(currentpage-1).size();i++){
//                                builder.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.countryspan)), country_min.get(currentpage-1).get(i), country_max.get(currentpage-1).get(i), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            }
//                            for(int i=0;i<city_min.get(currentpage-1).size();i++){
//                                builder.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.cityspan)), city_min.get(currentpage-1).get(i), city_max.get(currentpage-1).get(i), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            }
//                            for(int i=0;i<person_min.get(currentpage-1).size();i++){
//                                builder.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.personspan)), person_min.get(currentpage-1).get(i),person_max.get(currentpage-1).get(i), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            }
//                            showtext.setText(builder);
//                        }
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    public void refreshSpan(){
        int offset = (int)factory.getBegin();
        int size = (int)factory.getPageSize();
        for(int i = 0; i < tempcountrymin.size(); i++){
            System.out.println(tempcountrymin.get(i) + "compared with "+ offset);
            if(offset < tempcountrymin.get(i) && tempcountrymin.get(i) < offset + size)
                builder.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.countryspan)), tempcountrymin.get(i) - offset, size>(tempcountrymax.get(i) - offset)?(tempcountrymax.get(i) - offset):size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for(int i = 0; i < tempcitymin.size(); i++){
            if(offset < tempcitymin.get(i) && tempcitymin.get(i) < offset + size)
                builder.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.cityspan)), tempcitymin.get(i) - offset, size>(tempcitymax.get(i) - offset)?(tempcitymax.get(i) - offset):size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for(int i = 0; i < temppersonmin.size(); i++){
            if(offset < temppersonmin.get(i) && temppersonmin.get(i) < offset + size)
                builder.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.personspan)), temppersonmin.get(i) - offset, size>(temppersonmax.get(i) - offset)?(temppersonmax.get(i) - offset):size, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        showtext.setText(builder);

    }

    public void storePage(long page){
        JSONObject inf;
        inf = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject arr = new JSONObject();
        try {
            arr.put("type", "page");
            arr.put("page", page);
            System.out.println(arr.toString());
            array.put(arr);
            inf.put("read", array);

            File page_file = new File(dir, "/page");
            if (page_file.exists()) page_file.delete();
            page_file.createNewFile();
            FileOutputStream outStream = new FileOutputStream(page_file);
            outStream.write(inf.toString().getBytes());
            outStream.close();
        }catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Failed to write file");
        }
    }

    public void storeTag(int type){
        JSONObject inf;
        inf = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject arr = new JSONObject();
        switch (type){
            case 0:
                try {
                    arr.put("type", "country");
                    arr.put("filePath", file);
                    JSONArray jCountry = new JSONArray();
                    for (int i = 0; i < country.size(); i++){
                        JSONObject ACountry = new JSONObject();
                        JSONArray jCountrypos = new JSONArray();
                        for (int j = 0; j< country_pos.get(i).size(); j++){
                            jCountrypos.put(country_pos.get(i).get(j));
                            System.out.println(country_pos.get(i).get(j));
                        }
                        ACountry.put("country_name", country.get(i));
                        ACountry.put("country_pos", jCountrypos);
                        jCountry.put(i, ACountry);
                    }
                    arr.put("country", jCountry);
                    System.out.println(arr.toString());
                    array.put(arr);
                    inf.put("read", array);
                    System.out.println(array.toString());
                    System.out.println(inf.toString());

                    File country_file = new File(dir, "/note/country");
                    if (country_file.exists()) country_file.delete();
                    country_file.createNewFile();
                    FileOutputStream outStream = new FileOutputStream(country_file);
                    outStream.write(inf.toString().getBytes());
                    outStream.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("Failed to write file");
                }
                break;
            case 1:
                try {
                    arr.put("type", "city");
                    arr.put("filePath", file);
                    JSONArray jCity = new JSONArray();
                    for (int i = 0; i < city.size(); i++){
                        JSONObject ACity = new JSONObject();
                        JSONArray jCitypos = new JSONArray();
                        for (int j = 0; j< city_pos.get(i).size(); j++){
                            jCitypos.put(city_pos.get(i).get(j));
                            System.out.println(city_pos.get(i).get(j));
                        }
                        ACity.put("city_name", city.get(i));
                        ACity.put("city_country", city_country.get(i));
                        ACity.put("city_pos", jCitypos);
                        jCity.put(i, ACity);
                    }
                    arr.put("city", jCity);
                    System.out.println(arr.toString());
                    array.put(arr);
                    inf.put("read", array);

                    System.out.println(arr.toString());
                    array.put(arr);
                    inf.put("read", array);
                    System.out.println(array.toString());
                    System.out.println(inf.toString());

                    File city_file = new File(dir, "/note/city");
                    if (city_file.exists()) city_file.delete();
                    city_file.createNewFile();
                    FileOutputStream outStream = new FileOutputStream(city_file);
                    outStream.write(inf.toString().getBytes());
                    outStream.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("Failed to write file");
                }
                break;
            case 2:
                try {
                    arr.put("type", "person");
                    arr.put("filePath", file);
                    JSONArray jPerson = new JSONArray();
                    for (int i = 0; i < person.size(); i++){
                        JSONObject APerson = new JSONObject();
                        JSONArray jPersonpos = new JSONArray();
                        for (int j = 0; j< person_pos.get(i).size(); j++){
                            jPersonpos.put(person_pos.get(i).get(j));
                            System.out.println(person_pos.get(i).get(j));
                        }
                        APerson.put("person_name", person.get(i));
                        APerson.put("person_country", person_country.get(i));
                        APerson.put("person_pos", jPersonpos);
                        jPerson.put(i, APerson);
                    }
                    arr.put("person", jPerson);
                    System.out.println(arr.toString());
                    array.put(arr);
                    inf.put("read", array);

                    File person_file = new File(dir, "/note/person");
                    if (person_file.exists()) person_file.delete();
                    person_file.createNewFile();
                    FileOutputStream outStream = new FileOutputStream(person_file);
                    outStream.write(inf.toString().getBytes());
                    outStream.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("Failed to write file");
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void finish() {
        //zyr
        //super.onDestroy();
        if (changed[0]) {
            storeTag(0);
        }
        if (changed[1]) {
            storeTag(1);
        }
        if (changed[2]) {
            storeTag(2);
        }
        storePage(factory.GetCurrentPageNum());
    }
}
