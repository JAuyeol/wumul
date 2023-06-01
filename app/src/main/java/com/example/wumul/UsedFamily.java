package com.example.wumul;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.used_family);

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
                        LinearLayout buttonLayout = createItemLayout(key);
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

    private LinearLayout createItemLayout(String key){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight=1;

        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setLayoutParams(layoutParams);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button familyButton = new Button(this);
        familyButton.setLayoutParams(layoutParams);
        familyButton.setText(key);
        familyButton.setBackgroundResource(R.drawable.button3_pattern); // 버튼 배경 이미지 적용
        familyButton.setTextColor(getResources().getColor(R.color.white)); // 버튼 텍스트 색상 설정
        familyButton.setTextSize(getResources().getDimension(R.dimen.button_text_size)); // 버튼 텍스트 크기 설정

        Typeface typeface = ResourcesCompat.getFont(this, R.font.cafe24surround);
        familyButton.setTypeface(typeface);

        familyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UserInfoActivity로 전환
                Intent intent = new Intent(UsedFamily.this, UserInfoActivity.class);
                intent.putExtra("key", key);
                startActivity(intent);
            }
        });

        itemLayout.addView(familyButton);

        return itemLayout;
    }

}
