package com.example.javahybridsample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.javahybridsample.databinding.ActivityIntroBinding;
import com.example.javahybridsample.dialog.CommonBottomSheetDialog;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Arrays;

public class IntroActivity extends AppCompatActivity {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private ActivityResultLauncher<String> requestPermissionLauncher;

    private ActivityIntroBinding binding;

    //권한 모두 받은 상태에서 인트로 화면이 너무 빠르게 넘어가기 때문에 아래 플래그를 이용하여 타이머 설정함.
    private boolean locationPermisionCheckFlag = false;
    private boolean batteryOptimizationCheckFlag = false;

    private MutableLiveData<Boolean> isPermissionChecked = new MutableLiveData<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * view binding
         * */
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*
         * 권한 체크
         * */
        checkPermission();

        /*
         * 권한체크 이후 sample activity 이동
         * */
        isPermissionChecked.observe(this, isGranted -> {
            if(isGranted) {
                Intent intent = new Intent(this, SampleActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    /**
     * 레인알람에 필요한 권한을 체크하고 요청한다.
     * */
    private void checkPermission() {
        //저장된 위치정보가 없을 때, 최초 실행시 초기 위치 설정을 위해 위치정보수집 권한을 요청한다. 거부시 현재위치 시청으로 초기 설정합니다.
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            locationPermisionCheckFlag = true;
            showLocationPermissionBottomSheet();
        } else {
            isPermissionChecked.postValue(true);
        }
    }

    /**
     * 위치정보 수집 권한 설정 바텀 시트팝업을 호출한다.
     * */
    private void showLocationPermissionBottomSheet() {
        CommonBottomSheetDialog bottomSheetDialog = new CommonBottomSheetDialog();
        bottomSheetDialog.setBottomSheetOption(bottomSheetDialog.new BottomSheetOption("",getString(R.string.location_permission_comment),false, "거부", "설정"));

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {

            } else {
                checkPermission();
            }
        });

        bottomSheetDialog.setClickListener(new CommonBottomSheetDialog.BottomSheetButtonClickListener() {
            @Override
            public void onLeftButtonClicked(View view) {
                checkPermission();
            }

            @Override
            public void onRightButtonClicked(View view) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        });
        bottomSheetDialog.show(getSupportFragmentManager(), "locationPermissionBottomSheet");
    }
}
