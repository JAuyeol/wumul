package com.example.wumul;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "SignupActivity";

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        findViewById(R.id.create_account_button).setOnClickListener(onClickListener);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }


    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.signup_button:
                    signUp();
                    break;

            }
        }
    };

    public void signUp(){
        String email = ((EditText)findViewById(R.id.create_user_ID)).getText().toString();
        String password = ((EditText)findViewById(R.id.create_user_PASSWARD)).getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
              .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                     if (task.isSuccessful()) {
                     // Sign in success, update UI with the signed-in user's information
                         Log.d(TAG, "createUserWithEmail:success");
                         FirebaseUser user = mAuth.getCurrentUser();
//                       updateUI(user);
                          // 로그인 성공
                     } else {
                      // If sign in fails, display a message to the user.
                         Log.w(TAG, "createUserWithEmail:failure", task.getException());

//                       updateUI(null);
                         // 실패했을 때
                     }
                 }
             });
    }
}
