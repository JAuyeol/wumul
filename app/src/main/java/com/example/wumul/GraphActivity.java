package com.example.wumul;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GraphActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private LinearLayout mFamilyMembersLayout;
    private LineChart mDailyLineChart;
    private LineChart mMonthlyLineChart;
    private Map<String, ArrayList<Entry>> mDataEntryMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.cafe24surround);

        mFamilyMembersLayout = findViewById(R.id.graph_textView_layout);
        mDailyLineChart = findViewById(R.id.daily_line_chart);
        mMonthlyLineChart = findViewById(R.id.monthly_line_chart);
        mDataEntryMap = new HashMap<>();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFamilyMembersLayout.removeAllViews();

                DataSnapshot familyMembersSnapshot = snapshot.child(getUid()).child("family_members");
                for (DataSnapshot childSnapshot : familyMembersSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();

                    TextView textView = createTextView(key);
                    mFamilyMembersLayout.addView(textView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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

    private TextView createTextView(String key) {
        Typeface typeface = ResourcesCompat.getFont(GraphActivity.this, R.font.cafe24surround);
        TextView textView = new TextView(this);
        textView.setText(key);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setPadding(10, 0, 10, 0);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textView.setTypeface(typeface, Typeface.BOLD);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGraphForFamilyMember(key);
            }
        });
        return textView;
    }

    private void showGraphForFamilyMember(String key) {
        mDailyLineChart.clear(); // 기존의 그래프 데이터 제거
        mMonthlyLineChart.clear();

        mDatabase.child(getUid()).child("family_members").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("shower") && snapshot.hasChild("sink") && snapshot.hasChild("sum")) {
                    long shower = snapshot.child("shower").getValue(Long.class);
                    long sink = snapshot.child("sink").getValue(Long.class);
                    long sum = snapshot.child("sum").getValue(Long.class);

                    ArrayList<Entry> showerEntries = new ArrayList<>();
                    showerEntries.add(new Entry(0, shower));
                    LineDataSet showerDataSet = new LineDataSet(showerEntries, "샤워");
                    showerDataSet.setColor(Color.BLUE);
                    showerDataSet.setLineWidth(2f);
                    showerDataSet.setValueTextColor(Color.BLACK);
                    showerDataSet.setValueTextSize(12f);

                    ArrayList<Entry> sinkEntries = new ArrayList<>();
                    sinkEntries.add(new Entry(0, sink));
                    LineDataSet sinkDataSet = new LineDataSet(sinkEntries, "싱크대");
                    sinkDataSet.setColor(Color.RED);
                    sinkDataSet.setLineWidth(2f);
                    sinkDataSet.setValueTextColor(Color.BLACK);
                    sinkDataSet.setValueTextSize(12f);

                    ArrayList<Entry> sumEntries = new ArrayList<>();
                    sumEntries.add(new Entry(0, sum));
                    LineDataSet sumDataSet = new LineDataSet(sumEntries, "합계");
                    sumDataSet.setColor(Color.GREEN);
                    sumDataSet.setLineWidth(2f);
                    sumDataSet.setValueTextColor(Color.BLACK);
                    sumDataSet.setValueTextSize(12f);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(showerDataSet);
                    dataSets.add(sinkDataSet);
                    dataSets.add(sumDataSet);

                    LineData dailyLineData = new LineData(dataSets);
                    mDailyLineChart.setData(dailyLineData);

                    Description dailyDescription = new Description();
                    dailyDescription.setText("일간 사용 그래프");
                    mDailyLineChart.setDescription(dailyDescription);

                    mDailyLineChart.invalidate(); // 그래프 업데이트

                    // 월간 사용 그래프 데이터 설정
                    // TODO: 월간 사용 그래프 데이터 설정 및 표시 (위와 유사한 방식으로)

                } else {
                    Toast.makeText(GraphActivity.this, "해당 가족 구성원의 그래프 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("GraphActivity", "Firebase 데이터베이스 읽기 실패", error.toException());
            }
        });
    }

}
