package com.example.wumul;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
//import android.widget.Toolbar;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main_Activity";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


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
                        break;
                    case R.id.menu_tips:
                        gotoActivity(TipActivity.class);
                        // "절약 TIP" 메뉴 선택 시 처리할 코드 작성
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
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

}

