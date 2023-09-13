package com.example.cielo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cielo.R;

public class LoadingActivity extends AppCompatActivity {

    String TAG = "MAIN";
    int key = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        startLoading();
    }// onCreate()..

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);  //메인화면을 띄운다.
                finish();   //현재 액티비티 종료
            }
        }, 2000); // 화면에 Logo 2초간 보이기
    }// startLoading Method..
}