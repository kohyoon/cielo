package com.example.cielo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cielo.R;


public class testActivity extends AppCompatActivity {
    String TAG = "testActivity)";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myscrap);






    }


    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void openActivity(Class c) {
        Intent intent = new Intent(this, c);
        // finish();
        startActivity(intent);
    }


}

