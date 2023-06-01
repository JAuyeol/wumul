//package com.example.wumul;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//public class Testmb extends AppCompatActivity {
//
//    private DatabaseReference mDatabase;
//    private LinearLayout mFamilyMembersLayout;
//
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.testmem);
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mFamilyMembersLayout = findViewById(R.id.family_members_layout);
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            String uid = user.getUid();
//            mDatabase.child(uid).child("family_members").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    // 데이터베이스에서 가져온 데이터를 처리하는 코드
//                    mFamilyMembersLayout.removeAllViews(); // 기존에 추가된 View들을 모두 삭제
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        String name = ds.child("name").getValue(String.class);
//
//                        // 구성원 목록을 표시하는 View를 생성
//                        View familyMemberView = LayoutInflater.from(Testmb.this).inflate(R.layout.family_member_item, null);
//                        TextView familyMemberNameTextView = familyMemberView.findViewById(R.id.family_member_button);
//                        familyMemberNameTextView.setText(name);
//
//
//                        // 생성한 View를 LinearLayout에 추가
//                        mFamilyMembersLayout.addView(familyMemberView);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    // Failed to read value
//                    Log.w("Database", "Failed to read value.", error.toException());
//                }
//            });
//        }
//    }
//
//}
//
//
//
//
