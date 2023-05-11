package com.example.withapp;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.withapp.POJO.ProfileImg;
import com.example.withapp.POJO.SearchRecyclerData;
import com.example.withapp.RETROFIT.RetrofitInterface;
import com.example.withapp.RETROFIT.retrofit_client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentSearch extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private String memberId;
    private ImageView myImage;
    private TextView locationText;
    private TextView showingDistance;
    private Button refreshLocation;
    private RecyclerView recyclerView;
    private Switch locationSwitch;
    private String range;
    private String age;
    private String gender;
    private double gpsLongitude;
    private double gpslatitude;
    private Call<String> locationUpdate;
    private List<SearchRecyclerData> searchAdapterData;
    private LocationManager lm;
    private Call<List<SearchRecyclerData>> searchRecyclerData;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    refreshLocation.performClick();
                } else {
                    locationText.setText("위치정보없음");
                }
            });

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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        //뷰
        myImage = (ImageView) view.findViewById(R.id.myImage);
        locationText = (TextView) view.findViewById(R.id.locationText);
        refreshLocation = (Button) view.findViewById(R.id.locationRefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.searchRecyclerview);
        locationSwitch = (Switch) view.findViewById(R.id.locationSwitch);
        showingDistance = (TextView) view.findViewById(R.id.showingDistance);

        //위치 관리자 객체 생성
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        getLocationInfo();

        //필터 정보

        range = getRange();
        age = getAge();
        gender = getGender();
        String showingText = "반경:"+range+"Km("+age+"살/"+gender+")";
        showingDistance.setText(showingText);

        //레트로핏 객체 생성
        RetrofitInterface service = retrofit_client.retrofit.create(RetrofitInterface.class);
        Call<ProfileImg> profileImg = service.memberProfileImg(memberId);
        searchRecyclerData = service.searchRecyclerInfo(memberId, Integer.parseInt(range), Integer.parseInt(age),gender,gpslatitude, gpsLongitude);

        //위치제공여부 객체 초기화
        locationSwitch.setChecked(getLocationOnOff());

        //내 프로필 이미지 불러오기
        profileImg.enqueue(new Callback<ProfileImg>() {
            @Override
            public void onResponse(Call<ProfileImg> call, Response<ProfileImg> response) {

                String base64Image = response.body().getBase64Image();
                byte[] decodedString = Base64.getDecoder().decode(base64Image);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                myImage.setImageBitmap(bitmap);

            }

            @Override
            public void onFailure(Call<ProfileImg> call, Throwable t) {

            }
        });

        //리사이클러뷰 적용
        searchRecyclerData.enqueue(new Callback<List<SearchRecyclerData>>() {
            @Override
            public void onResponse(Call<List<SearchRecyclerData>> call, Response<List<SearchRecyclerData>> response) {
                searchAdapterData = response.body();
                if(searchAdapterData == null || searchAdapterData.size() == 0){
                    Toast.makeText(getActivity(),"탐색된 여행자가 없습니다.",Toast.LENGTH_LONG).show();
                }else {
                    //리사이클러뷰 매니저 객체 생성
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    SearchAdapter searchAdapter = new SearchAdapter(searchAdapterData);
                    recyclerView.setAdapter(searchAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<SearchRecyclerData>> call, Throwable t) {

            }
        });

        //위치 초기화
        refreshLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                } else {
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location != null){
                        gpsLongitude = location.getLongitude(); // 위도
                        gpslatitude = location.getLatitude(); // 경도

                        String address = getCurrentAddress(gpslatitude, gpsLongitude);
                        locationText.setText(address);

                    }
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            50000,
                            1,
                            gpsLocationListener);

                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            50000,
                            1,
                            gpsLocationListener);

                    locationUpdate = service.locationInfo(memberId, gpslatitude, gpsLongitude);
                    locationUpdate.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.d("위치정보", response.body());
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                    searchRecyclerData = service.searchRecyclerInfo(memberId, Integer.parseInt(range), Integer.parseInt(age),gender,gpslatitude, gpsLongitude);
                    searchRecyclerData.enqueue(new Callback<List<SearchRecyclerData>>() {
                        @Override
                        public void onResponse(Call<List<SearchRecyclerData>> call, Response<List<SearchRecyclerData>> response) {
                            searchAdapterData = response.body();
                            if(searchAdapterData == null || searchAdapterData.size() == 0){
                                Toast.makeText(getActivity(),"탐색된 여행자가 없습니다.",Toast.LENGTH_LONG).show();
                            }else {
                                //리사이클러뷰 매니저 객체 생성
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
                                recyclerView.setLayoutManager(gridLayoutManager);
                                SearchAdapter searchAdapter = new SearchAdapter(searchAdapterData);
                                recyclerView.setAdapter(searchAdapter);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<SearchRecyclerData>> call, Throwable t) {

                        }
                    });
                }
            }
        });

        //위치인식 버튼
        locationSwitch.setOnCheckedChangeListener(((compoundButton, isChecked) -> {
            Call<String> onOff = service.locationOnOff(memberId,isChecked);
            onOff.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d("위치정보제공여부", response.body());
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
            setLocationOnOff(isChecked);
        }));

        //위치조절 필터로 이동
        showingDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move(SearchFilter.class,getActivity());
            }
        });
        return view;
    }

    private void getLocationInfo(){

        if (ContextCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                gpsLongitude = location.getLongitude(); // 위도
                gpslatitude = location.getLatitude(); // 경도
                String address = getCurrentAddress(gpslatitude, gpsLongitude);
                locationText.setText(address);
            } else {
                locationText.setText("위치정보없음");
            }
        }
    }
    private void setLocationOnOff(boolean isChecked){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("위치정보제공여부", isChecked);
        editor.apply();
    }
    private boolean getLocationOnOff(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", MODE_PRIVATE);
        return sharedPreferences.getBoolean("위치정보제공여부", false);
    }
    LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            // 위치 리스너는 위치정보를 전달할 때 호출되므로 onLocationChanged()메소드 안에 위지청보를 처리를 작업을 구현 해야합니다.
            gpsLongitude = location.getLongitude(); // 위도
            gpslatitude = location.getLatitude(); // 경도
            String address = getCurrentAddress(gpslatitude, gpsLongitude);
            locationText.setText(address);

        } public void onStatusChanged(String provider, int status, Bundle extras) {

        } public void onProviderEnabled(String provider) {

        } public void onProviderDisabled(String provider) {

        }
    };
    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(getActivity(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getActivity(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getActivity(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getCountryName()+","+address.getThoroughfare()+","+address.getAdminArea();
        //return address.getAddressLine(0);
    }
    private String getRange() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("search_filter", MODE_PRIVATE);
        return sharedPreferences.getString("거리", "0");
    }
    private String getAge() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("search_filter", MODE_PRIVATE);
        return sharedPreferences.getString("나이", "0");
    }
    private String getGender() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("search_filter", MODE_PRIVATE);
        return sharedPreferences.getString("성별", "모두");
    }
    public static void move(Class<?> target, Context context){
        Intent moveIntent = new Intent(context, target);
        context.startActivity(moveIntent);
    }

}
