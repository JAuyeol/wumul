    package com.example.wumul;

    import android.annotation.SuppressLint;
    import android.app.Person;
    import android.content.Context;
    import android.content.Intent;
    import android.graphics.Typeface;
    import android.os.Bundle;
    import android.text.InputType;
    import android.util.Log;
    import android.view.KeyEvent;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;
    import android.view.View.OnKeyListener;
    import android.view.View;
    import android.view.KeyEvent;

    import androidx.appcompat.app.AppCompatActivity;

    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.Map;

    public class PersonEdit extends AppCompatActivity {
        private int familyCount;
        LinearLayout editPerson;
        private Button confirmButton;
        private FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        Map<String, Object> data1 = new HashMap<>();
                        data1.put("flag",0);
                        data1.put("head",0);
                        data1.put("sink",0);
                        mDatabase.setValue(data1);
                        for(int i = 0; i < familyCount; i++) {
                            EditText et = (EditText) editPerson.getChildAt(i);
                            String text = et.getText().toString();
                            Log.d("PersonEdit", "EditText " + i + ": " + text);
    //                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();;

                            if (user != null) {
                                String pathString = text;

                                Map<String, Object> data = new HashMap<>();
                                data.put("sink",0);
                                data.put("shower",0);
                                data.put("sum",0);

                                mDatabase.child("users").child(uid).child("family_members").child(pathString).setValue(data);
                                Log.w("Database", "정보 저장됨");
                                Toast.makeText(getApplicationContext(), "저장을 완료했습니다.", Toast.LENGTH_SHORT).show();
                            }
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