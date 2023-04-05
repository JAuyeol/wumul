package com.example.wumul;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
// 정규현
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, CountFamilyActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000); // 2초 대기 후 MainActivity로 이동

    }
}

// 정금열
/*
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                int familyCount = sharedPreferences.getInt("familyCount", -1);

                Intent intent;
                if (familyCount == -1) {
                    intent = new Intent(SplashActivity.this, CountFamilyActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 2000); // 2초 대기 후 MainActivity로 이동
 */