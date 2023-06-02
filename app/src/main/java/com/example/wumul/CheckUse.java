package com.example.wumul;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
    private Button btn_start, btn_stop, btn_reset;
    private TextView tv_info, tv_sink, tv_head;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private int get_sink = 0, get_head = 0;
    private DatabaseReference DB_SINK, DB_HEAD, DB_FLAG;

    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";
    private NotificationManager manager;
    private NotificationCompat.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_wateruse);
        tv_info = findViewById(R.id.id_info);
        tv_sink = findViewById(R.id.id_sink);
        tv_head = findViewById(R.id.id_head);
        mFamilyMembersLayout = findViewById(R.id.check_use_layout);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        btn_start = findViewById(R.id.id_start);
        btn_stop = findViewById(R.id.id_stop);
        btn_reset = findViewById(R.id.id_reset);

        String uid = getUid();

        DB_SINK = database.getReference("sink");
        DB_HEAD = database.getReference("head");
        DB_FLAG = database.getReference("flag");

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
                show_noti();
                gotoActivity(MainActivity.class);
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DB_SINK.setValue(0);
                DB_HEAD.setValue(0);
            }
        });

        ValueEventListener sinkValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    get_sink = Integer.parseInt(snapshot.getValue().toString());
                    tv_sink.setText(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        if (DB_SINK != null) {
            DB_SINK.addValueEventListener(sinkValueListener);
        }

        ValueEventListener headValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    get_head = Integer.parseInt(snapshot.getValue().toString());
                    tv_head.setText(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        if (DB_HEAD != null) {
            DB_HEAD.addValueEventListener(headValueListener);
        }

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

                    if (sinkValue != null && showerValue != null) {
                        Long sumValue = sinkValue + showerValue;  // sumValue 계산

                        String sinkStrValue = String.valueOf(sinkValue);
                        String showerStrValue = String.valueOf(showerValue);
                        String sumStrValue = String.valueOf(sumValue);

                        // 레이아웃 생성
                        LinearLayout itemLayout = createItemLayout(key, sinkStrValue, showerStrValue, sumStrValue);
                        mFamilyMembersLayout.addView(itemLayout);

                        // sinkValue와 showerValue를 업데이트
                        if (key.equals("sink")) {
                            get_sink = sinkValue.intValue();
                            tv_sink.setText(sinkStrValue);
                        } else if (key.equals("shower")) {
                            get_head = showerValue.intValue();
                            tv_head.setText(showerStrValue);
                        }
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

        TextView showerText = new TextView(this);
        showerText.setLayoutParams(layoutParams);
        showerText.setText(showerValue);
        itemLayout.addView(showerText);

        TextView sinkText = new TextView(this);
        sinkText.setLayoutParams(layoutParams);
        sinkText.setText(sinkValue);
        itemLayout.addView(sinkText);

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

        // sinkValue와 showerValue를 업데이트
        Long sinkValueLong = Long.parseLong(sinkValue);  // sinkValueLong 변수 초기화
        Long showerValueLong = Long.parseLong(showerValue);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedSinkValue = tv_sink.getText().toString();
                String updatedShowerValue = tv_head.getText().toString();

                // Firebase에 데이터 업데이트
                Long updatedSinkValueLong = Long.parseLong(updatedSinkValue);
                Long updatedShowerValueLong = Long.parseLong(updatedShowerValue);

                Long sinkValueLongUpdated = updatedSinkValueLong + sinkValueLong;
                Long showerValueLongUpdated = updatedShowerValueLong + showerValueLong;

                mDatabase.child(getUid()).child("family_members").child(key).child("sink").setValue(sinkValueLongUpdated);
                mDatabase.child(getUid()).child("family_members").child(key).child("shower").setValue(showerValueLongUpdated);
                mDatabase.child(getUid()).child("family_members").child(key).child("sum").setValue(sinkValueLongUpdated + showerValueLongUpdated);

                DB_HEAD.setValue(0);
                DB_SINK.setValue(0);
            }
        });



        itemLayout.addView(saveButton);

        return itemLayout;
    }

    private void show_noti() {
        // 알림 권한 확인
        boolean notificationGranted = NotificationManagerCompat.from(this).areNotificationsEnabled();
        if (!notificationGranted) {
            // 알림 권한이 없는 경우, 권한 요청
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivity(intent);
            return;
        }

        builder = null;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        builder.setContentTitle("물사용량 알림")
                .setContentText("구성원에게 물사용량을 저장해주세요.")
                .setSmallIcon(R.mipmap.ic_launcher);

        Notification notification = builder.build();
        manager.notify(1, notification);
    }
    private void gotoActivity(Class c){
        Intent intent = new Intent(this,c);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
