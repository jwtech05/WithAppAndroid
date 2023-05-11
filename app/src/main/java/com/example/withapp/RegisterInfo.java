package com.example.withapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class RegisterInfo extends AppCompatActivity {
    private Context context;
    private Intent intent;
    private TextView nickName;
    private TextView birthText;
    private Button man;
    private Button woman;
    private String gender;
    private TextView editTextCountry;
    private Button continueInfoButton;
    //************인텐트 자료형************
    private String intentEmailId;
    private String intentPassword;
    private String intentPhone;
    private int year, month, day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info);

        context = this;
        nickName = findViewById(R.id.editTextNickName);
        birthText = findViewById(R.id.birthPicker);
        man = findViewById(R.id.genderMan);
        woman = findViewById(R.id.genderWoman);
        editTextCountry = findViewById(R.id.editTextCountry);
        continueInfoButton = findViewById(R.id.continueInfoButton);

        intent = getIntent();
        intentEmailId = intent.getStringExtra("아이디");
        intentPassword = intent.getStringExtra("비밀번호");
        intentPhone = intent.getStringExtra("전화번호");
        gender="남";

        //***************생년월일선택*****************
        birthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        //****************나라선택*****************
        editTextCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCountryListDialog();
            }
        });
        //***************성별선택***************
        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = "남";
                int color = ContextCompat.getColor(getApplicationContext(), R.color.teal_200);
                man.setBackgroundColor(color);
                woman.setBackgroundColor(Color.parseColor("#777A7A"));
            }
        });
        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = "여";
                int color = ContextCompat.getColor(getApplicationContext(), R.color.teal_200);
                woman.setBackgroundColor(color);
                man.setBackgroundColor(Color.parseColor("#777A7A"));
            }
        });
        //****************이동버튼*****************
        continueInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move();
            }
        });
    }
    private void showCountryListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Country");

        // Load country names from the string-array resource
        final String[] countryNames = getResources().getStringArray(R.array.country_names);

        builder.setItems(countryNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedCountry = countryNames[which];
                // Do something with the selected country name
                editTextCountry.setText(selectedCountry);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showDatePickerDialog() {
        // 년, 월, 일을 선택할 수 있는 NumberPicker 생성

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        final NumberPicker yearPicker = new NumberPicker(this);
        yearPicker.setMinValue(1930);
        yearPicker.setMaxValue(2023);
        yearPicker.setValue(2023);

        final NumberPicker monthPicker = new NumberPicker(this);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(5);

        final NumberPicker dayPicker = new NumberPicker(this);
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);
        dayPicker.setValue(4);

        // Dialog 빌더 생성 및 NumberPicker 추가
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("생년월일 선택");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.addView(yearPicker);
        layout.addView(monthPicker);
        layout.addView(dayPicker);
        builder.setView(layout);

        // 확인 버튼 클릭 시
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 선택된 년, 월, 일 값을 가져와서 TextView에 설정
                int year = yearPicker.getValue();
                int month = monthPicker.getValue();
                int day = dayPicker.getValue();

                String date = year + "/" + month + "/" + day;
                birthText.setText(date);
            }
        });

        // 취소 버튼 클릭 시
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Dialog 보여주기
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void move(){
        intent = new Intent(context, RegisterPhoto.class);
        intent.putExtra("아이디", intentEmailId);
        intent.putExtra("비밀번호", intentPassword);
        intent.putExtra("전화번호", intentPhone);
        intent.putExtra("닉네임", nickName.getText().toString().trim());
        intent.putExtra("생년월일", birthText.getText().toString().trim());
        intent.putExtra("성별", gender);
        intent.putExtra("나라", editTextCountry.getText().toString().trim());
        startActivity(intent);
    }
}