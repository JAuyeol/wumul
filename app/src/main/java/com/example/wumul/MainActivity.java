package com.example.wumul;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Type;

import me.itangqi.waveloadingview.WaveLoadingView;
//import android.widget.Toolbar;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main_Activity";
    private static final int MAX_CAPACITY = 110;
    WaveLoadingView waveLoadingView;
    private DatabaseReference mDatabase;
    //  툴바, 네비게이션 start
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    //  툴바, 네비게이션 end
    private FrameLayout frameLayout;
    Long familyUsedSum= Long.valueOf(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout = findViewById(R.id.frame_layout);

        waveLoadingView = findViewById(R.id.waveLoadingView);
        waveLoadingView.setAnimDuration(3000);




//툴바, 네비게이션 바 관련 start

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
//툴바, 네비게이션 바 관련 end
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        String key = getIntent().getStringExtra("key");



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Typeface typeface = ResourcesCompat.getFont(MainActivity.this, R.font.cafe24surround);

                    long familyMembersCount = snapshot.child(getUid()).child("family_members").getChildrenCount();
                    long totalCapacity = familyMembersCount * MAX_CAPACITY;
                    long totalUsage = 0;
//                    frameLayout.removeAllViews();
                    for (DataSnapshot childSnapshot : snapshot.child(getUid()).child("family_members").getChildren()) {
                        String key = childSnapshot.getKey();
                        DataSnapshot data = childSnapshot;

                        Long showerValue = data.child("shower").getValue(Long.class);
                        Long sinkValue = data.child("sink").getValue(Long.class);
                        Long sumValue = data.child("sum").getValue(Long.class);

                        if (showerValue != null) {
                            totalUsage += showerValue*0.001;
                        }

                        if (sinkValue != null) {
                            totalUsage += sinkValue*0.001;
                        }


                        if(sumValue !=null){
                            familyUsedSum += sumValue;
                        }

                    }
                    int percent = (int) ((totalUsage / (double) totalCapacity) * 100);
                    waveLoadingView.setProgressValue(percent);
                    waveLoadingView.setCenterTitle(percent+"%");

                    // waveLoadingView의 애니메이션 시작 위치 설정
                    if (percent >= 1) {
                        //int startAngle = (int) (360 * (percent / 100.0));
                        waveLoadingView.startAnimation();
                    }




                    ImageView imageView = new ImageView(MainActivity.this);
                    imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                    imageView.setImageResource(R.drawable.waterfamily_resize);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    imageLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                    imageLayoutParams.topMargin = toolbar.getHeight(); // 툴바의 높이만큼 topMargin 설정
                    imageView.setLayoutParams(imageLayoutParams);
                    frameLayout.addView(imageView);



                    TextView userSumTextView = new TextView(MainActivity.this);
                    userSumTextView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                    userSumTextView.setText("우리 가족의 총 사용량 \n"+String.valueOf(familyUsedSum)+" L \n 사용했어요");
                    userSumTextView.setTextSize(30);
                    userSumTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                    userSumTextView.setTypeface(typeface, Typeface.BOLD);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.gravity = Gravity.CENTER;
                    userSumTextView.setLayoutParams(layoutParams);
                    frameLayout.addView(userSumTextView);

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 처리 중 오류가 발생한 경우 여기에서 처리합니다.
                }
            });
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // 네비게이션 메뉴 아이템 선택 시 처리할 코드 작성
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.menu_wateruse:
                        gotoActivity(MainActivity.class);
                        break;
                    case R.id.menu_detailuse:
                        // "세부 사용량" 메뉴 선택 시 처리할 코드 작성
                        break;
                    case R.id.menu_people:
                        // "구성원" 메뉴 선택 시 처리할 코드 작성
                        gotoActivity(UsedFamily.class);
                        break;
                    case R.id.menu_tips:
                        gotoActivity(TipActivity.class);
                        // "절약 TIP" 메뉴 선택 시 처리할 코드 작성
                        break;
                    case R.id.check_use:
                        gotoActivity(CheckUse.class);
                        break;
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                        gotoActivity(LoginActivity.class);
                        finish();
                        break;
                }

                // 네비게이션 드로어 닫기
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    private void gotoActivity(Class c){
        Intent intent = new Intent(this,c);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private String getUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

}
