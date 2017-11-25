package com.example.com.textreader;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/11/18 0018.
 */

public class MyFontSizePicker extends LinearLayout {

    private EditText mEditText;
    private TextView bAdd;
    private TextView bReduce;
    String size;

    public MyFontSizePicker(final Context ctxt, AttributeSet attrs) {
            super(ctxt,attrs);
        }


    protected void onFinishInflate() {
        super.onFinishInflate();

        LayoutInflater.from(getContext()).inflate(R.layout.myfontsizepicker, this);
        init_widget();
        addListener();

    }

    public void init_widget(){
        mEditText = (EditText)findViewById(R.id.size);
        bAdd = (TextView) findViewById(R.id.plus);
        bReduce = (TextView) findViewById(R.id.minus);
        mEditText.setText("20");
        size=mEditText.getText().toString();
    }

    public void addListener(){
        bAdd.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                int num = Integer.valueOf(mEditText.getText().toString());
                num++;
                if(num>30){
                    num=30;
                }
                mEditText.setText(Integer.toString(num));
            }
        });

        bReduce.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int num = Integer.valueOf(mEditText.getText().toString());
                num--;
                if(num<15){
                    num=15;
                }
                mEditText.setText(Integer.toString(num));
            }
        });
    }
    public EditText FindEditText(){
        return mEditText;
    }

}
