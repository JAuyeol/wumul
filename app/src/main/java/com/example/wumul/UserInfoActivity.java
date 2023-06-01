package com.example.wumul;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInfoActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private LinearLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        parentLayout = findViewById(R.id.parent_layout);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        // 넘겨받은 key 변수 값
        String key = getIntent().getStringExtra("key");

        // Firebase에서 데이터 읽어오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            mDatabase.child(uid).child("family_members").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // sink value, shower value, sum value 가져오기
                        Long sinkValue = dataSnapshot.child("sink").getValue(Long.class);
                        Long showerValue = dataSnapshot.child("shower").getValue(Long.class);
                        Long sumValue = dataSnapshot.child("sum").getValue(Long.class);

                        Typeface typeface = ResourcesCompat.getFont(UserInfoActivity.this, R.font.cafe24surround);


// 첫 번째 TextView
                        // 첫 번째 TextView
                        TextView keyTextView = new TextView(UserInfoActivity.this);
                        LinearLayout.LayoutParams keyTextViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        keyTextViewParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL; // 화면 상단에 위치
                        keyTextView.setLayoutParams(keyTextViewParams);
                        keyTextView.setText(key + " 님은 현재");
                        keyTextView.setTextSize(40);
                        keyTextView.setTypeface(typeface);
                        parentLayout.addView(keyTextView);

// 두 번째 TextView
                        TextView usedShowerTextView = new TextView(UserInfoActivity.this);
                        LinearLayout.LayoutParams usedShowerTextViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        usedShowerTextViewParams.setMargins(150, 50, 50, 0); // 첫 번째 TextView와 50dp 간격 추가
                        usedShowerTextView.setLayoutParams(usedShowerTextViewParams);
                        usedShowerTextView.setBackgroundResource(R.drawable.button2_pattern);
                        usedShowerTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shower_resize, 0, 0, 0);
                        usedShowerTextView.setCompoundDrawablePadding(30);
                        usedShowerTextView.setText("샤워기에서 \n" + showerValue + " L 만큼 사용했어요");
                        usedShowerTextView.setTextSize(20);
                        usedShowerTextView.setGravity(Gravity.CENTER);
                        usedShowerTextView.setTypeface(typeface);
                        parentLayout.addView(usedShowerTextView);

// 세 번째 TextView
                        TextView usedWashstandTextView = new TextView(UserInfoActivity.this);
                        LinearLayout.LayoutParams usedWashstandTextViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        usedWashstandTextViewParams.setMargins(100, 50, 100, 0); // 첫 번째 TextView와 50dp 간격 추가
                        usedWashstandTextView.setLayoutParams(usedWashstandTextViewParams);
                        usedWashstandTextView.setBackgroundResource(R.drawable.button_pattern);
                        usedWashstandTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.washstand_resize, 0, 0, 0);
                        usedWashstandTextView.setCompoundDrawablePadding(30);
                        usedWashstandTextView.setText("세면대에서 \n" + sinkValue + " L 만큼 사용했어요");
                        usedWashstandTextView.setTextSize(20);
                        usedWashstandTextView.setGravity(Gravity.CENTER);
                        usedWashstandTextView.setTypeface(typeface);
                        parentLayout.addView(usedWashstandTextView);

// 네 번째 TextView
                        TextView usedTotalTextView = new TextView(UserInfoActivity.this);
                        LinearLayout.LayoutParams usedTotalTextViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        usedTotalTextViewParams.setMargins(50, 50, 150, 0); // 첫 번째 TextView와 50dp 간격 추가
                        usedTotalTextView.setLayoutParams(usedTotalTextViewParams);
                        usedTotalTextView.setBackgroundResource(R.drawable.button1_pattern);
                        usedTotalTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.waterdrop_resize, 0, 0, 0);
                        usedTotalTextView.setCompoundDrawablePadding(30);
                        usedTotalTextView.setText("총 " + sumValue + " L 만큼 사용했어요");
                        usedTotalTextView.setTextSize(20);
                        usedTotalTextView.setGravity(Gravity.CENTER);
                        usedTotalTextView.setTypeface(typeface);
                        parentLayout.addView(usedTotalTextView);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 처리 중 오류가 발생한 경우 여기에서 처리합니다.
                    // 예: 데이터베이스 읽기 실패
                }
            });
        }
    }
}
