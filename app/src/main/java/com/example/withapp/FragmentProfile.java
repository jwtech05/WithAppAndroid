package com.example.withapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.withapp.POJO.ProfileData;
import com.example.withapp.POJO.ProfileImg;
import com.example.withapp.RETROFIT.RetrofitInterface;
import com.example.withapp.RETROFIT.retrofit_client;

import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentProfile extends Fragment {

    private String memberId;
    private TextView nickNameText;
    private TextView ageText;
    private TextView genderText;
    private TextView countryText;
    private TextView commentText;
    private ImageView imageView;
    private Button moveButton;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            memberId = (String) getArguments().getSerializable("memberId");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        nickNameText = (TextView) view.findViewById(R.id.pNickName);
        ageText = (TextView) view.findViewById(R.id.pAge);
        genderText = (TextView) view.findViewById(R.id.pGender);
        countryText = (TextView) view.findViewById(R.id.pCountry);
        commentText = (TextView) view.findViewById(R.id.pSelfIntro);
        moveButton = (Button) view.findViewById(R.id.moveButton);
        RetrofitInterface service = retrofit_client.retrofit.create(RetrofitInterface.class);
        Call<ProfileData> profileInfo = service.memberProfileInfo(memberId);
        Call<ProfileImg> profileImg = service.memberProfileImg(memberId);
        profileInfo.enqueue(new Callback<ProfileData>() {
            @Override
            public void onResponse(Call<ProfileData> call, Response<ProfileData> response) {

                nickNameText.setText(response.body().getNickName());
                ageText.setText(response.body().getAge());
                genderText.setText(response.body().getGender());
                countryText.setText(response.body().getCountry());
                commentText.setText(response.body().getComment());
            }

            @Override
            public void onFailure(Call<ProfileData> call, Throwable t) {
                Log.d("프로필에러", t.toString());
            }
        });

        profileImg.enqueue(new Callback<ProfileImg>() {
            @Override
            public void onResponse(Call<ProfileImg> call, Response<ProfileImg> response) {

                String base64Image = response.body().getBase64Image();
                byte[] decodedString = Base64.getDecoder().decode(base64Image);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView = view.findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(Call<ProfileImg> call, Throwable t) {

            }
        });

        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Move();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void Move(){
        Intent intent = new Intent(getActivity(), ProfileChange.class);
        startActivity(intent);
    }




}