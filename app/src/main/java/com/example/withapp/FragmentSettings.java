package com.example.withapp;

import static android.content.Context.MODE_PRIVATE;

import static com.example.withapp.Login.naverIdLoginSDK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.navercorp.nid.NaverIdLoginSDK;


public class FragmentSettings extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Button logout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        logout = (Button) view.findViewById(R.id.logoutB);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                naverIdLoginSDK.logout();
                getRidOfData();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void getRidOfData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("자동로그인", false);
        editor.putString("회원번호", "");
        editor.apply();
    }
}