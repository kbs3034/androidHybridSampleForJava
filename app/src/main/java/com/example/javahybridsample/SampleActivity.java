package com.example.javahybridsample;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.javahybridsample.api.data.sample.SampleUser;
import com.example.javahybridsample.databinding.ActivitySampleBinding;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SampleActivity extends AppCompatActivity {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private ActivitySampleBinding binding;
    private SampleViewModel viewModel;

    private int tHour = -1;
    private int tMinute = -1;

    private JSONObject place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySampleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SampleViewModel.class);

        viewModel.result.observe(this, jsonObject -> {
            TextView tv_result = (TextView) binding.cardApiTestResult.getChildAt(0);
            tv_result.setText(jsonObject.toString());
        });

        binding.cardApiTestBtn1.setOnClickListener(view -> {
                viewModel.doGetListResources();
            }
        );

        binding.cardApiTestBtn2.setOnClickListener(view -> {
            SampleUser sampleUser = new SampleUser();
                    viewModel.createUser(sampleUser);
                }
        );

        binding.cardApiTestBtn3.setOnClickListener(view -> {
                    viewModel.doGetUserListForJsonObject("1");
                }
        );

        binding.cardApiTestBtn4.setOnClickListener(view -> {
                    viewModel.doCreateUserWithField("morpheus","leader");
                }
        );

        binding.cardApiTestBtn5.setOnClickListener(view -> {
                    viewModel.doGetUserList("1");
                }
        );

        binding.cardWebviewBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this,SampleWebviewActivity.class);
            intent.putExtra("url","file:///android_asset/sample.html");
            startActivity(intent);
        });

    }
}
