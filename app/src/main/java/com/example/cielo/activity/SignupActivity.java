package com.example.cielo.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cielo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    String TAG = "SignUp ) ";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        findViewById(R.id.goLogin).setOnTouchListener(btnTouch);
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
                        Log.v(TAG,"Getting STARTED -------------------------------");
                        signUp();
                        break;
                }

            }
            return true;
        }
    };
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void signUp(){
        String email = ((EditText)findViewById(R.id.emailSignText)).getText().toString();
        String password = ((EditText)findViewById(R.id.pwSignText)).getText().toString();
        String passwordCheck = ((EditText)findViewById(R.id.pwCheckSignText)).getText().toString();

        if(email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0){
            if(password.equals(passwordCheck)){
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startToast("회원가입에 성공하였습니다.");
                                    openActivity(LoginActivity.class);
                                } else {
                                    try{
                                        throw task.getException();
                                    } catch(FirebaseAuthWeakPasswordException e) {
                                        startToast("비밀번호가 취약합니다.");
                                    } catch(FirebaseAuthInvalidCredentialsException e) {
                                        startToast("이메일 형식이 맞지 않습니다.");
                                    } catch(FirebaseAuthUserCollisionException e) {
                                        startToast("이미 존재하는 이메일입니다.");
                                    } catch(Exception e) {
                                        Log.e(TAG, e.getMessage());
                                        startToast("다시 확인해주세요");
                                    }
                                }
                            }
                        });
            }else{
                startToast("비밀번호가 일치하지 않습니다.");
            }
        }else {
            startToast("이메일 또는 비밀번호를 입력해 주세요.");
        }
    }
    private void openActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
