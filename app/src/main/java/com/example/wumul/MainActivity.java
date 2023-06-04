package com.example.wumul;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main_Activity";
    private static final int MAX_CAPACITY = 110;
    WaveLoadingView waveLoadingView;
    private DatabaseReference mDatabase;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FrameLayout frameLayout;
    Long familyUsedSum = Long.valueOf(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout = findViewById(R.id.frame_layout);

        waveLoadingView = findViewById(R.id.waveLoadingView);
        waveLoadingView.setAnimDuration(3000);
        waveLoadingView.setProgressValue(0);

        Intent intent = getIntent();
        int familyCount = intent.getIntExtra("familyCount", 0);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        String key = getIntent().getStringExtra("key");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String uid = mAuth.getCurrentUser().getUid();
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Typeface typeface = ResourcesCompat.getFont(MainActivity.this, R.font.cafe24surround);

                    DataSnapshot familyMembersSnapshot = snapshot.child(uid).child("family_members");
                    long familyMembersCount = familyMembersSnapshot.getChildrenCount();

                    long totalCapacity = familyMembersCount * MAX_CAPACITY;
                    long totalUsage = 0;

                    frameLayout.removeAllViews();
                    for (DataSnapshot childSnapshot : familyMembersSnapshot.getChildren()) {
                        String key = childSnapshot.getKey();
                        DataSnapshot data = childSnapshot;

                        Long showerValue = data.child("shower").getValue(Long.class);
                        Long sinkValue = data.child("sink").getValue(Long.class);
                        Long sumValue = data.child("sum").getValue(Long.class);

                        if (showerValue != null) {
                            totalUsage += showerValue;
                        }

                        if (sinkValue != null) {
                            totalUsage += sinkValue;
                        }

                        if (sumValue != null) {
                            familyUsedSum += sumValue;
                        }
                    }

                    double totalUsageLiters = (double) totalUsage / 1000.0;
                    double percent = (totalUsageLiters / (double) totalCapacity) * 100;
                    double roundedPercent = Math.round(percent * 10) / 10.0;
                    int displayPercent = (int)(percent);
                    waveLoadingView.startAnimation();
                    waveLoadingView.setProgressValue(displayPercent);
                    waveLoadingView.setCenterTitle(roundedPercent + "%");

                    if (roundedPercent > 100.0) {
                        double excessPercent = roundedPercent - 100.0;
                        double roundExcessPercent = Math.round(excessPercent * 10) / 10.0;
                        int displayExcessPercent = (int) roundExcessPercent;

                        int centerTitleStrokeColor = ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_dark);
                        int centerTitleColor = ContextCompat.getColor(MainActivity.this, android.R.color.white);
                        waveLoadingView.setCenterTitleStrokeColor(centerTitleStrokeColor);
                        waveLoadingView.setCenterTitleSize(centerTitleColor);
                        int redColor = Color.parseColor("#FF6E6E");
                        waveLoadingView.setWaveColor(redColor);

                        waveLoadingView.setBorderColor(Color.parseColor("#FF4646"));

                        waveLoadingView.setProgressValue(displayExcessPercent);
                        waveLoadingView.setCenterTitle(roundExcessPercent + "% 초과");
                    }
                    frameLayout.addView(waveLoadingView);

                    ImageView imageView = new ImageView(MainActivity.this);
                    imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                    imageView.setImageResource(R.drawable.waterfamily_resize);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    imageLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                    imageLayoutParams.topMargin = toolbar.getHeight();
                    imageView.setLayoutParams(imageLayoutParams);
                    frameLayout.addView(imageView);

                    TextView userSumTextView = new TextView(MainActivity.this);
                    userSumTextView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                    userSumTextView.setText("우리 가족의 하루 총 사용량 \n" + String.valueOf(totalUsageLiters) + " L");
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
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.menu_wateruse:
                        gotoActivity(MainActivity.class);
                        break;
                    case R.id.menu_people:
                        gotoActivity(UsedFamily.class);
                        break;
                    case R.id.menu_tips:
                        gotoActivity(TipActivity.class);
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

    private void gotoActivity(Class c) {
        Intent intent = new Intent(this, c);
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