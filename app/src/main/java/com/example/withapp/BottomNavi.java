package com.example.withapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.SharedPreferences;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavi extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    int selectedTab;
    Bundle bundle;

    FragmentProfile profileFragment;
    FragmentSearch profileSearch;
    FragmentCommunity profileCommunity;
    FragmentChat profileChat;
    FragmentSettings profileSettings;
    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                        } else {
                            // No location access granted.
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        bottomNavigation = findViewById(R.id.navigationView);

        profileFragment = new FragmentProfile();
        profileSearch = new FragmentSearch();
        profileCommunity = new FragmentCommunity();
        profileChat = new FragmentChat();
        profileSettings = new FragmentSettings();

        bundle = new Bundle();
        bundle.putSerializable("memberId", getMemberId());

        selectedTab = getIntent().getIntExtra("selected_tab", R.id.action_profile);

        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        bottomNavigation.setSelectedItemId(selectedTab);
        loadFragment(selectedTab);

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                loadFragment(item.getItemId());
                return true;
            }
        });
    }

    private void loadFragment(int menuItemId) {
        switch(menuItemId){
            case R.id.action_search:
                profileSearch.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileSearch).commit();
                break;
            case R.id.action_community:
                profileCommunity.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileCommunity).commit();
                break;
            case R.id.action_chat:
                profileChat.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileChat).commit();
                break;
            case R.id.action_profile:
                profileFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragment).commit();
                break;
            case R.id.action_settings:
                profileSettings.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileSettings).commit();
                break;
        }

    }
    private String getMemberId() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        return sharedPreferences.getString("회원번호", "");
    }
}