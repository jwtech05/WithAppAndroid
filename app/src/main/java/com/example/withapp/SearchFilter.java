package com.example.withapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class SearchFilter extends AppCompatActivity {

    private TextView searchRangeshow;
    private TextView searchAgeshow;
    private TextView doneText;
    private SeekBar searchRange;
    private SeekBar searchAge;
    private Button everyButton;
    private Button manButton;
    private Button womanButton;
    private String range;
    private String age;
    private String gender;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter);
        Context context = this;
        searchRangeshow = findViewById(R.id.searchRangeShow);
        searchAgeshow = findViewById(R.id.searchAgeShow);
        doneText = findViewById(R.id.doneText);
        searchRange = findViewById(R.id.searchRangeSeekBar);
        searchAge = findViewById(R.id.searchAgeSeekbar);
        everyButton = findViewById(R.id.everyButton);
        manButton = findViewById(R.id.manButton);
        womanButton = findViewById(R.id.womanButton);

        range = getRange();
        age = getAge();
        gender = getGender();

        searchRangeshow.setText(range+" Km");
        searchAgeshow.setText(age+" age");
        searchRange.setProgress(Integer.parseInt(range));
        searchAge.setProgress(Integer.parseInt(age));
        setGenderButton(gender);


        //거리조절
        searchRange.setMax(100);
        searchRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                range = String.valueOf(i);
                searchRangeshow.setText(range+" Km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //나이조절
        searchAge.setMin(18);
        searchAge.setMax(100);
        searchAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                age = String.valueOf(i);
                searchAgeshow.setText(age+" age");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //성별조절
        everyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "모두";
                int color = ContextCompat.getColor(getApplicationContext(), R.color.teal_200);
                everyButton.setBackgroundColor(color);
                manButton.setBackgroundColor(Color.parseColor("#777A7A"));
                womanButton.setBackgroundColor(Color.parseColor("#777A7A"));
            }
        });
        manButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "남";
                int color = ContextCompat.getColor(getApplicationContext(), R.color.teal_200);
                manButton.setBackgroundColor(color);
                everyButton.setBackgroundColor(Color.parseColor("#777A7A"));
                womanButton.setBackgroundColor(Color.parseColor("#777A7A"));
            }
        });
        womanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "여";
                int color = ContextCompat.getColor(getApplicationContext(), R.color.teal_200);
                womanButton.setBackgroundColor(color);
                manButton.setBackgroundColor(Color.parseColor("#777A7A"));
                everyButton.setBackgroundColor(Color.parseColor("#777A7A"));
            }
        });

        //완료버튼
        doneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRange(range);
                setAge(age);
                setGender(gender);
                Intent intent = new Intent(context, BottomNavi.class);
                intent.putExtra("selected_tab", R.id.action_search);
                startActivity(intent);
            }
        });
    }
    private void setGenderButton(String gender){
        if(gender.equals("모두")){
            int color = ContextCompat.getColor(getApplicationContext(), R.color.teal_200);
            everyButton.setBackgroundColor(color);
        }else if(gender.equals("남")){
            int color = ContextCompat.getColor(getApplicationContext(), R.color.teal_200);
            manButton.setBackgroundColor(color);
        }else{
            int color = ContextCompat.getColor(getApplicationContext(), R.color.teal_200);
            womanButton.setBackgroundColor(color);
        }

    }

    private void setRange(String range) {
        SharedPreferences sharedPreferences = getSharedPreferences("search_filter", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("거리", range);
        editor.apply();
    }

    private String getRange() {
        SharedPreferences sharedPreferences = getSharedPreferences("search_filter", MODE_PRIVATE);
        return sharedPreferences.getString("거리", "0");
    }

    private void setAge(String age) {
        SharedPreferences sharedPreferences = getSharedPreferences("search_filter", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("나이", age);
        editor.apply();
    }

    private String getAge() {
        SharedPreferences sharedPreferences = getSharedPreferences("search_filter", MODE_PRIVATE);
        return sharedPreferences.getString("나이", "0");
    }

    private void setGender(String gender) {
        SharedPreferences sharedPreferences = getSharedPreferences("search_filter", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("성별", gender);
        editor.apply();
    }

    private String getGender() {
        SharedPreferences sharedPreferences = getSharedPreferences("search_filter", MODE_PRIVATE);
        return sharedPreferences.getString("성별", "모두");
    }

    private String getMemberId() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        return sharedPreferences.getString("회원번호", "");
    }
}