package com.example.cielo.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cielo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    LinearLayout loginForm, logoForm;
    Animation logoAni; // 위치 이동
    Animation loginFormAni; // 투명도 조절
    String TAG = "LogIn ) ";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.signUp).setOnTouchListener(btnTouch);
        findViewById(R.id.goLogin).setOnTouchListener(btnTouch);

        loginForm = findViewById(R.id.formframe);
        logoForm = findViewById(R.id.logoframe);

        logoAni = new TranslateAnimation(0, 0, 0, -360);

        logoAni.setDuration(1500); // 지속시간
        logoAni.setFillAfter(true); // 이동 후 이동한 자리에 남아있을건지
        logoAni.setStartOffset(1500); // 딜레이

        loginFormAni = new AlphaAnimation(0, 1);
        loginFormAni.setDuration(1000);
        loginFormAni.setStartOffset(2500);

        logoForm.setAnimation(logoAni); // 애니메이션을 세팅해줌
        loginForm.setAnimation(loginFormAni);





    }
    //edittext 키보드내리기
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    private View.OnTouchListener btnTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageView view =(ImageView)v;
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                view.setPadding(2,2,2,2);
            }else if (event.getAction() == MotionEvent.ACTION_UP){
                view.setPadding(0,0,0,0);
                switch(v.getId()){
                    case R.id.goLogin:
                        login();
                        break;
                    case R.id.signUp:
                        gotoSignUpActivity();
                }

            }
            return true;
        }
    };

    private void login(){
        String email = ((EditText) findViewById(R.id.emailBlank)).getText().toString();
        String pw = ((EditText) findViewById(R.id.pwBlank)).getText().toString();
        //id constraint
        if(email.length()>0 && pw.length()>0){
            mAuth.signInWithEmailAndPassword(email, pw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                startToast("로그인에 성공하였습니다.");
                                Log.d(TAG, "signInWithEmail:success");
                                openActivity(MainActivity.class);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                startToast("로그인에 실패하였습니다.");
                            }
                        }
                    });
        }

    }
    private void openActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    private void gotoSignUpActivity(){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
