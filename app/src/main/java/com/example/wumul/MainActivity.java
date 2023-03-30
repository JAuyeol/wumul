package com.example.wumul;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_FAMILY_COUNT = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // CountFamilyActivity에서 넘어온 결과 처리
        if (requestCode == REQUEST_CODE_FAMILY_COUNT && resultCode == Activity.RESULT_OK) {
            int familyCount = data.getIntExtra("familyCount", 0);
            // familyCount 값을 사용하여 원하는 작업을 수행
        }
    }
}