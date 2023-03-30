package com.example.wumul;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CountFamilyActivity extends AppCompatActivity {

    private TextView family_count;
    private ImageButton plus_button, minus_button;
    private int count = 0;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.count_family);

        family_count = findViewById(R.id.count_text);
        family_count.setText(count+"");
        plus_button = findViewById(R.id.plus_button);
        minus_button = findViewById(R.id.minus_button);

        plus_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "onClick: btnAdd : "+v.getClass().getName());
                count++;
                family_count.setText(count+"");
            }
        });

        minus_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(count>0) {
                    count--;
                    family_count.setText(count + "");
                }
            }
        });



    }
}