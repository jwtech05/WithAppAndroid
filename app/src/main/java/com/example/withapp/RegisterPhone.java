package com.example.withapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.withapp.POJO.CallbackData;
import com.example.withapp.POJO.SmsRequest;
import com.example.withapp.POJO.SmsResponse;
import com.example.withapp.RETROFIT.RetrofitInterface;
import com.example.withapp.RETROFIT.retrofit_client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterPhone extends AppCompatActivity {

    private Spinner countryCodeSpinner;
    Context context;
    Intent intent;
    private EditText number;
    private EditText numVer;
    private Button nButton;
    private Button pVButton;
    private Button resendButton;
    private RetrofitInterface nService;
    private RetrofitInterface service;
    private TextView timerTextView;
    private TextView warningText;
    private Handler handler;
    private int randomNumber;
    private int remainingTimeInSeconds = 3 * 60;
    private Timer countdownTimer;
    //************인텐트 자료형************
    private String intentEmailId;
    private String intentPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);
        context = this;
        //전화번호 입력
        number = (EditText) findViewById(R.id.editTextPhoneNum);
        //인증번호 입력
        numVer = (EditText) findViewById(R.id.editTextNumVer);
        //전화번호 전송 버튼
        nButton = (Button) findViewById(R.id.sendButton);
        //인증번호 전송 버튼
        pVButton = (Button) findViewById(R.id.numVerButton);
        //재전송 버튼
        resendButton = (Button) findViewById(R.id.resendButton);
        //타이머 텍스트
        timerTextView = (TextView) findViewById(R.id.timeText);
        //인증번호 확인 텍스트
        warningText = (TextView) findViewById(R.id.warningText);
        //레트로핏 객체 선언
        service = retrofit_client.retrofit.create(RetrofitInterface.class);

        //인텐트값 받아오기
        intent = getIntent();
        intentEmailId = intent.getStringExtra("아이디");
        intentPassword = intent.getStringExtra("비밀번호");
        //*******************인증하기 버튼 클릭*******************
        nButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSms();
                sendNum();
            }
        });
        //*******************확인하기 버튼 클릭*******************
        pVButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVer();
            }
        });
        //*******************재전송 버튼 클릭*******************
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(handler != null){
                    handler.removeCallbacksAndMessages(null);
                }
                resendSmsNum();
            }
        });

        //countryCodeSpinner = findViewById(R.id.country_code_spinner);
        //setupCountryCodeSpinner();
    }
    //난수 생성
    private String randomNum(){
        Random random = new Random();
        randomNumber = random.nextInt(900000) + 100000; // 난수 범위는 100000 (포함) ~ 999999 (포함)입니다.
        return String.valueOf(randomNumber);
    }
    //네이버 SENS로 전송
    private void sendSms(){

        retrofit_client rtfc = new retrofit_client();
        nService = rtfc.naverLoginRetrofit.create(RetrofitInterface.class);
        String num = number.getText().toString();
        SmsRequest smsrequest = new SmsRequest();
        smsrequest.setType("SMS");
        smsrequest.setContentType("COMM");
        smsrequest.setCountryCode("82");
        smsrequest.setFrom("01050675799"); // 발신자 번호 (예시)
        smsrequest.setContent("[withapp]번호인증 \n인증번호 "+ randomNum() +" 입니다. 타인노출금지");

        SmsRequest.Message message = new SmsRequest.Message();
        message.setTo(num);

        List<SmsRequest.Message> messages = new ArrayList<>();
        messages.add(message);
        smsrequest.setMessages(messages);

        Call<SmsResponse> smsNumSend = nService.sendNaverSms(smsrequest);
        smsNumSend.enqueue(new Callback<SmsResponse>() {
            @Override
            public void onResponse(Call<SmsResponse> call, Response<SmsResponse> response) {
                if (response.isSuccessful()) {
                    SmsResponse smsResponse = response.body();
                    System.out.println("SMS 전송 성공: " + smsResponse.getMessageId());

                } else {
                    Toast.makeText(context,"재전송을 눌러주세요.", Toast.LENGTH_SHORT).show();
                    System.out.println("SMS 전송 실패: " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SmsResponse> call, Throwable t) {
                System.out.println("SMS 전송 중 오류 발생: " + t.getMessage());
            }
        });
    }
    //전화번호 withapp 서버로 전송
    private void sendNum(){

        Call<String> sendNumber = service.sendPhoneNum(number.getText().toString(), String.valueOf(randomNumber));
        sendNumber.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("전화번호 서버 전송 성공: " + response.body());
                nButton.setEnabled(false);
                pVButton.setVisibility(View.VISIBLE);
                timerTextView.setVisibility(View.VISIBLE);
                numVer.setVisibility(View.VISIBLE);
                resendButton.setVisibility(View.VISIBLE);
                warningText.setVisibility(View.VISIBLE);
                handler = new Handler(getMainLooper());
                startCountdownTimer();

            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
    //인증번호 전송
    private void sendVer(){
        Call<CallbackData> sendVerNumber = service.sendPhoneVerNum(number.getText().toString(), numVer.getText().toString());
        sendVerNumber.enqueue(new Callback<CallbackData>() {
            @Override
            public void onResponse(Call<CallbackData> call, Response<CallbackData> response) {
                CallbackData result = response.body();
                String pResult = result.getMessage();
                if(pResult.equals("1")){
                    warningText.setText("인증번호 유효시간이 지났습니다.");
                    warningText.setTextColor(Color.RED);
                }else if(pResult.equals("2")){
                    Log.d("검증검증", pResult);
                    warningText.setText("인증번호가 올바르지 않습니다.");
                    warningText.setTextColor(Color.RED);
                }else if(pResult.equals("3")){
                    move();
                }else{
                    warningText.setText("");
                }
            }
            @Override
            public void onFailure(Call<CallbackData> call, Throwable t) {
                Log.d("검증검증", t.toString());
            }
        });
    }
    //재전송
    private void resendSmsNum(){
        sendSms();
        sendNum();
    }
    //다음화면으로 이동
    private void move(){
        intent = new Intent(context, RegisterInfo.class);
        intent.putExtra("아이디", intentEmailId);
        intent.putExtra("비밀번호", intentPassword);
        intent.putExtra("전화번호", number.getText().toString());
        startActivity(intent);
    }
    //인증번호 제한시간 생성
    private void startCountdownTimer() {
        if(countdownTimer != null){
            countdownTimer.cancel();
        }
        remainingTimeInSeconds = 3 * 60;
        countdownTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (remainingTimeInSeconds > 0) {
                    remainingTimeInSeconds--;
                    int minutes = remainingTimeInSeconds / 60;
                    int seconds = remainingTimeInSeconds % 60;
                    handler.post(() -> timerTextView.setText(String.format("%d:%02d", minutes, seconds)));
                } else {
                    handler.post(() -> timerTextView.setText("0:00"));
                    countdownTimer.cancel();
                }
            }
        };
        countdownTimer.scheduleAtFixedRate(timerTask, 0, 1000);
    }
    /*private void setupCountryCodeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.country_codes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countryCodeSpinner.setAdapter(adapter);
        countryCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCountryCode = parent.getItemAtPosition(position).toString();
                // Do something with the selected country code
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }*/
}