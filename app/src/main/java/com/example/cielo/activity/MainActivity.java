package com.example.cielo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cielo.R;
import com.example.cielo.fragments.Frag_BoardEdit;
import com.example.cielo.fragments.Frag_BoardList;
import com.example.cielo.fragments.Frag_BoardView;
import com.example.cielo.fragments.Frag_BoardViewEditable;
import com.example.cielo.fragments.Frag_alert;
import com.example.cielo.fragments.Frag_calendar;
import com.example.cielo.fragments.Frag_calendarMemo;
import com.example.cielo.fragments.Frag_community;
import com.example.cielo.fragments.Frag_info;
import com.example.cielo.fragments.Frag_pad;
import com.example.cielo.fragments.Frag_padDetail;
import com.example.cielo.fragments.Frag_padIngredient;
import com.example.cielo.fragments.Frag_padResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String tmpname = currentUser.getUid().toString();
    String TAG = "MAIN";
    private final static int CALENDAR_ID = 1;
    private final static int PAD_ID = 2;
    private final static int MAIN_ID = 3;
    private final static int INFO_ID = 4;
    private final static int COMMUNITY_ID = 5;
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
        setContentView(R.layout.activity_main);
//툴바세팅
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D1E6F5")));

//네비게이션 세팅
        bottomNav= findViewById(R.id.bottom_nav);

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment = null;
                switch(id){
                    case R.id.pad:
                        fragment = new Frag_pad();
                        break;
                    case R.id.info:
                        fragment = new Frag_info();
                        break;
                    case R.id.calendar:
                        fragment = new Frag_calendar();
                        break;
                    case R.id.community:
                        fragment = new Frag_community();
                        break;
                }

                if(fragment!=null){
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.popBackStackImmediate(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment)
                            .commit();
                }else{
                    Log.e("NAV", "Error in creating Fragment");
                }
            }

        });

        //첫화면정의
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, new Frag_calendar())
                .commit();

    }// onCreate()..

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //call super
        super.onActivityResult(requestCode, resultCode, data);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                openActivity(LoginActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    private void init(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //로그인되어있으면 메인그대로, 안되어있으면 로그인페이지로
        if (firebaseUser == null) {
            openActivity(LoginActivity.class);
            finish();
        } else {
            //회원정보입력안했을시 회원정보화면으로
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("userInfo").document(firebaseUser.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                                openActivity(MemberInitActivity.class);

                                finish();
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }
    private void openActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    public void showSearchbar(Fragment search){ //클릭하면 서치화면나오는걸로
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.add(R.id.frame_container, search,"searchFragment");
        ft.addToBackStack("search");
        ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom,0,0);
        if(search != null) ft.show(search).commit();
    }


    public void ShowSearchResults(String keyword, int minPrice, int maxPrice, int maxGrade){ //클릭하면 검색결과화면으로

        Frag_padResult result = new Frag_padResult();
        Bundle bundle = new Bundle();
        bundle.putString("keyword", keyword);
        bundle.putInt("minPrice", minPrice);
        bundle.putInt("maxPrice", maxPrice);
        bundle.putInt("maxGrade", maxGrade);
        result.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(); //added here
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,R.anim.slide_in_right,R.anim.slide_in_right);
        ft.replace(R.id.frame_container, result);
        ft.addToBackStack("result");
        ft.commit();
    }

    public void showSelectedItem(String padName, String padKey, ArrayList<ArrayList<String>> ingredients, String length, boolean refresh){ // (검색결과)추천상품클릭 => detail 화면으로
        Frag_padDetail next = new Frag_padDetail();
        Bundle bundle = new Bundle();
        bundle.putString("padName", padName);
        bundle.putString("padKey", padKey);
        bundle.putString("length",length);
        bundle.putStringArrayList("waterproof", ingredients.get(0));
        bundle.putStringArrayList("pad", ingredients.get(1));
        bundle.putStringArrayList("inside_pad", ingredients.get(2));
        bundle.putStringArrayList("absorber", ingredients.get(3));
        bundle.putStringArrayList("adhesive", ingredients.get(4));
        next.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (!refresh) {
            ft.addToBackStack("detail");
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,R.anim.slide_in_right,R.anim.slide_in_right);
        }

        ft.replace(R.id.frame_container,next);
        ft.commit();
    }
    public void showSelectedTheme(String theme) {

        Frag_BoardList next = new Frag_BoardList();
        Bundle bundle = new Bundle();
        bundle.putString("theme",theme);
        next.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.frame_container, next);
        ft.addToBackStack("theme");
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0);
        ft.commit();


    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment).commit();
    }

    public void replaceAddFragment (Fragment fragment,String tag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.frame_container, fragment,tag);
        ft.addToBackStack(tag);
        ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom,0,0);
        if(fragment != null) ft.show(fragment).commit();

    }
    public void showAlert (Fragment fragment,String tag,String time){
        Frag_alert next = new Frag_alert();
        Bundle bundle = new Bundle();
        bundle.putString("time", time);
        next.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.frame_container, next,tag);
        ft.addToBackStack(tag);
        ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom,0,0);
        if(next != null) ft.show(next).commit();



    }

    public void showIngredient(String padName, String padKey, ArrayList<ArrayList<String>> ingredients, String length) {
        Frag_padIngredient next = new Frag_padIngredient(); //fragment 선언
        Bundle bundle = new Bundle();
        bundle.putString("padName", padName);
        bundle.putString("padKey", padKey);
        bundle.putString("length",length);
        bundle.putStringArrayList("waterproof", ingredients.get(0));
        bundle.putStringArrayList("pad", ingredients.get(1));
        bundle.putStringArrayList("inside_pad", ingredients.get(2));
        bundle.putStringArrayList("absorber", ingredients.get(3));
        bundle.putStringArrayList("adhesive", ingredients.get(4));
        next.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction(); //넘길때
        ft.add(R.id.frame_container, next,"searchFragment");
        ft.addToBackStack("search");
        ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom,0,0);
        if(next != null) ft.show(next).commit();


    }
    public void showedit(String title, String text, String time,String theme,Boolean bool){

        Frag_BoardEdit next = new Frag_BoardEdit();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("text", text);
        bundle.putString("time", time);
        bundle.putString("theme", theme);
        bundle.putBoolean("anon",bool);
        next.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.frame_container, next);
        ft.addToBackStack("view333");
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0);
        ft.commit();

    }
    public void showBoardedit(String title, String text, String time){

            Frag_BoardViewEditable next = new Frag_BoardViewEditable();
            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            bundle.putString("text", text);
            bundle.putString("time", time);
            next.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack("view444", fragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.frame_container, next);
            ft.addToBackStack("view444");
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0);
            ft.commit();

    }
    public void showBoard(String title, String text, String time) {

        Frag_BoardView next = new Frag_BoardView();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("text", text);
        bundle.putString("time", time);
        next.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack("view2",fragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.frame_container, next);
        ft.addToBackStack("view2");
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0);
        ft.commit();

    }

    public void showSelectedBoard(String title, String text, String time,String id) {

            if(tmpname.equals(id)){showBoardedit(title,text,time);}
            else{showBoard(title,text,time);}

    }
    public void showSelectedBoard2(String title, String text, String time,String id) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack("view444", fragmentManager.POP_BACK_STACK_INCLUSIVE);

        if(tmpname.equals(id)){showBoardedit(title,text,time);}
        else{showBoard(title,text,time);}

    }

    public void showBoardwrite(Fragment boardwrite) { //클릭하면 작성화면
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.frame_container, boardwrite, "boardwritefragment");
        ft.addToBackStack("boardwrite");
        ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom, 0, 0);
        if (boardwrite != null) ft.show(boardwrite).commit();
    }



    public void refreshTheme(String theme) {

        Frag_BoardList next = new Frag_BoardList();
        Bundle bundle = new Bundle();
        bundle.putString("theme",theme);
        next.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        fragmentManager.popBackStack("theme",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ft.replace(R.id.frame_container, next);
        ft.addToBackStack("theme");
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0);
        ft.commit();


    }

    public void replaceAddFragment(Fragment next, String memo, int yearS, int monthS, int dateS) {
        Bundle bundle = new Bundle();
        bundle.putInt("year",yearS);
        bundle.putInt("month", monthS);
        bundle.putInt("date", dateS);
        next.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.frame_container, next,memo);
        ft.addToBackStack(memo);
        ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom,0,0);
        if(next != null) ft.show(next).commit();
    }
}