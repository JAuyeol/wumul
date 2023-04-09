package com.example.wumul;

import android.annotation.SuppressLint;
import android.app.Person;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PersonEdit extends AppCompatActivity {
    private int familyCount;
    LinearLayout editPerson;
    private Button confirmButton;

    ArrayList<String> strList;
    //@SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.persondialog);

        strList = new ArrayList<String>();
        confirmButton = findViewById(R.id.confirm_button);
        editPerson = findViewById(R.id.Person);
        Intent intent = getIntent();

        familyCount= intent.getIntExtra("familyCount",0);

        for(int i = 0;i<familyCount;i++)
        {
            EditText et = new EditText(getApplicationContext());
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            et.setLayoutParams(p);
            et.setHint("구성원" + (i+1) );
            et.setId(i);
            editPerson.addView(et);
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(familyCount>0)
                {
                    for(int i = 0; i < familyCount; i++) {
                        EditText et = (EditText) editPerson.getChildAt(i);
                        String text = et.getText().toString();
                        Log.d("PersonEdit", "EditText " + i + ": " + text);
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("num", familyCount);
                    intent.putStringArrayListExtra("strList", strList);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),"EditText 박스 만들기 버튼을 눌러 박스를 만들어 주세요.",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}
