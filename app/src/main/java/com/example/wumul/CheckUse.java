package com.example.wumul;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class CheckUse extends AppCompatActivity {

    private LinearLayout mFamilyMembersLayout;
    private DatabaseReference mDatabase;
    private Button      btn_start, btn_stop, btn_reset;
    private TextView    tv_info;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference DB_SINK = database.getReference().child("users").child("sink");
    private DatabaseReference DB_HEAD = database.getReference().child("users").child("head");
    private DatabaseReference DB_FLAG = database.getReference().child("users").child("flag");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_wateruse);
        tv_info = (TextView)findViewById(R.id.id_info);
        mFamilyMembersLayout = findViewById(R.id.check_use_layout);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        btn_start = (Button)findViewById(R.id.id_start);      btn_stop = (Button)findViewById(R.id.id_stop);        btn_reset = (Button)findViewById(R.id.id_reset);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DB_FLAG.setValue(1);
                tv_info.setText("측정시작");
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DB_FLAG.setValue(0);
                tv_info.setText("측정종료");
//                show_noti();
            }
        });



        // Firebase 데이터 변경 감지
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 레이아웃 초기화
                mFamilyMembersLayout.removeAllViews();

                // Firebase에서 데이터 읽어와서 동적으로 레이아웃 추가
                for (DataSnapshot childSnapshot : snapshot.child(getUid()).child("family_members").getChildren()) {
                    String key = childSnapshot.getKey();
                    DataSnapshot data = childSnapshot;

                    Long sinkValue = data.child("sink").getValue(Long.class);
                    Long showerValue = data.child("shower").getValue(Long.class);
                    Long sumValue = data.child("sum").getValue(Long.class);

                    if (sinkValue != null && showerValue != null && sumValue != null) {
                        String sinkStrValue = String.valueOf(sinkValue);
                        String showerStrValue = String.valueOf(showerValue);
                        String sumStrValue = String.valueOf(sumValue);

                        // 레이아웃 생성
                        LinearLayout itemLayout = createItemLayout(key, sinkStrValue, showerStrValue, sumStrValue);
                        mFamilyMembersLayout.addView(itemLayout);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 처리 중 오류가 발생한 경우 여기에서 처리합니다.
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

    private LinearLayout createItemLayout(String key, String sinkValue, String showerValue, String sumValue) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;

        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setLayoutParams(layoutParams);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);


        Button saveButton = new Button(this);
        saveButton.setLayoutParams(layoutParams);
        saveButton.setText("Save");

        // TextView 생성 및 설정
        TextView keyTextView = new TextView(this);
        keyTextView.setLayoutParams(layoutParams);
        keyTextView.setText(key);
        itemLayout.addView(keyTextView);

        TextView sinkText = new TextView(this);
        sinkText.setLayoutParams(layoutParams);
        sinkText.setText(sinkValue);
        itemLayout.addView(sinkText);

        TextView showerText = new TextView(this);
        showerText.setLayoutParams(layoutParams);
        showerText.setText(showerValue);
        itemLayout.addView(showerText);

        TextView sumText = new TextView(this);
        sumText.setLayoutParams(layoutParams);
        sumText.setText(sumValue);
        itemLayout.addView(sumText);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.cafe24surround);
        saveButton.setTypeface(typeface);
        keyTextView.setTypeface(typeface);
        sinkText.setTypeface(typeface);
        showerText.setTypeface(typeface);
        sumText.setTypeface(typeface);



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedSinkValue = sinkText.getText().toString();
                String updatedShowerValue = showerText.getText().toString();
                String updatedSumValue = sumText.getText().toString();

                // Firebase에 데이터 업데이트
                mDatabase.child(getUid()).child("family_members").child(key).child("sink").setValue(Long.parseLong(updatedSinkValue));
                mDatabase.child(getUid()).child("family_members").child(key).child("shower").setValue(Long.parseLong(updatedShowerValue));
                mDatabase.child(getUid()).child("family_members").child(key).child("sum").setValue(Long.parseLong(updatedSumValue));

                Log.d("저장버튼","저장버튼 클릭");
            }
        });
        itemLayout.addView(saveButton);

        return itemLayout;
    }

}
