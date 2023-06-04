package com.example.wumul;

import android.app.Person;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UsedFamily extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private LinearLayout mFamilyMembersLayout;
    private Button backCountFamily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.used_family);

        backCountFamily = findViewById(R.id.back_countFamily_button);

        mFamilyMembersLayout = findViewById(R.id.family_use_layout);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        // Firebase 데이터 변경 감지
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 레이아웃 초기화
                mFamilyMembersLayout.removeAllViews();

                // Firebase에서 데이터 읽어와서 동적으로 레이아웃 추가
                DataSnapshot familyMembersSnapshot = snapshot.child(getUid()).child("family_members");
                for (DataSnapshot childSnapshot : familyMembersSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();


                    if (!key.equals("flag") && !key.equals("get_shower") && !key.equals("get_sink")) {
                        LinearLayout buttonLayout = createItemLayout(key, childSnapshot);
                        mFamilyMembersLayout.addView(buttonLayout);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 처리 중 오류가 발생한 경우 여기에서 처리합니다.
                Log.e("UsedFamily", "Firebase 데이터베이스 읽기 실패", error.toException());
            }
        });
    }

    private String getUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    private LinearLayout createItemLayout(String key, DataSnapshot dataSnapshot) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;

        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setLayoutParams(layoutParams);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button familyButton = new Button(this);
        familyButton.setLayoutParams(layoutParams);
        familyButton.setBackgroundResource(R.drawable.button3_pattern);
        familyButton.setTextColor(getResources().getColor(R.color.white));
        familyButton.setTextSize(getResources().getDimension(R.dimen.button_text_size_small)); // 작은 텍스트 크기 설정
        Typeface typeface = ResourcesCompat.getFont(this, R.font.cafe24surround);
        familyButton.setTypeface(typeface);

        // Firebase에서 데이터 가져오기
        DataSnapshot sinkSnapshot = dataSnapshot.child("sink");
        DataSnapshot showerSnapshot = dataSnapshot.child("shower");
        if (sinkSnapshot.exists() && showerSnapshot.exists()) {
            Long sinkValue = sinkSnapshot.getValue(Long.class);
            Long showerValue = showerSnapshot.getValue(Long.class);
            if (sinkValue != null && showerValue != null) {
                Long sumValue = sinkValue + showerValue;

                // 버튼 텍스트 설정
                String buttonText;
                if (sumValue < 1000) {
                    buttonText = key + "\n총 사용량: " + sumValue + " mL";
                } else {
                    double sumValueInLiters = (double) sumValue / 1000.0;  // mL을 L로 변환
                    String formattedValue;
                    if (sumValueInLiters >= 10) {
                        formattedValue = String.format("%4.1f", sumValueInLiters);  // 소수점 한 자리까지 포맷팅
                    } else {
                        formattedValue = String.format("%4.3f", sumValueInLiters);  // 소수점 세 자리까지 포맷팅
                    }
                    buttonText = key + "\n총 사용량: " + formattedValue + " L";
                }

                familyButton.setText(buttonText);
            }
        } else {
            // 데이터가 없는 경우 처리
            familyButton.setText(key);
        }

        itemLayout.addView(familyButton);

        familyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UserInfoActivity로 전환
                Intent intent = new Intent(UsedFamily.this, UserInfoActivity.class);
                intent.putExtra("key", key);
                startActivity(intent);
            }
        });

        backCountFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity(CountFamilyActivity.class);
            }
        });

        return itemLayout;
    }

    private void gotoActivity(Class c){
        Intent intent = new Intent(this,c);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}