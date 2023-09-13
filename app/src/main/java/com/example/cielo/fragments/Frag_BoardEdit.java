package com.example.cielo.fragments;



import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.cielo.R;
import com.example.cielo.activity.MainActivity;
import com.example.cielo.database.Board;
import com.example.cielo.database.Theme;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.sql.Timestamp;

public class Frag_BoardEdit extends Fragment {

    private Context context;
    String TAG = "Board Edit) ";
    int selectType = 5;// Free,Love,Health,Secret,Food
    RadioGroup rg;
    String key;
    EditText w_title;
    EditText w_context;
    TextView editing;
    //추가
    CheckBox anocheck;
    Board b1;
    String tmpname,title,text;
    Timestamp time;
    String xtitle,xtext,xtime,xtheme;
    Boolean xbool;
    String[] items = {"등록", "취소"};
    Theme theme;


    public static Frag_BoardWrite newInstance() {
        return new Frag_BoardWrite();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.boardwrite, container, false);
        Bundle fromitem = getArguments();
        xtitle = fromitem.getString("title");
        xtext = fromitem.getString("text");
        xtime = fromitem.getString("time");
        xtheme = fromitem.getString("theme");
        xbool=fromitem.getBoolean("anon");
        context = container.getContext();
        b1 = new Board();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseFirestore.getInstance();
        tmpname = user.getUid().toString();
        //추가
        anocheck=(CheckBox) v.findViewById(R.id.ancheck);
        editing=(TextView) v.findViewById(R.id.writing);
        w_title = (EditText) v.findViewById(R.id.title_write);
        w_context = (EditText) v.findViewById(R.id.context_write);
        rg = (RadioGroup) v.findViewById(R.id.theme_name_radiobtn);

        //추가
        anocheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // 체크되면 모두 보이도록 설정
                if (anocheck.isChecked() == true) {
                    b1.setAnonymous(true);
                } else {
                    b1.setAnonymous(false);

                }
            }
        });
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.Freecheck:
                        selectType = 0;
                        b1.setTheme(Theme.Free);
                        break;
                    case R.id.Lovecheck:
                        selectType = 1;
                        b1.setTheme(Theme.Love);
                        break;
                    case R.id.Foodcheck:
                        selectType = 2;
                        b1.setTheme(Theme.Food);
                        break;
                    case R.id.Healthcheck:
                        selectType = 3;
                        b1.setTheme(Theme.Health);
                        break;
                    case R.id.Secretcheck:
                        selectType = 4;
                        b1.setTheme(Theme.Secret);
                        break;
                }
            }
        });

        v.findViewById(R.id.before_btn).setOnClickListener(onClickListener);
        v.findViewById(R.id.submit_btn).setOnClickListener(onClickListener);

        return v;

    }

    @Override
    public void onStart() {

            super.onStart();
            editing.setText("수정하기");
            w_title.setText(xtitle);
            w_context.setText(xtext);
            switch (xtheme) {
                case "Free":
                    rg.check(R.id.Freecheck);
                    break;
                case "Love":
                    rg.check(R.id.Lovecheck);
                    break;
                case "Health":
                    rg.check(R.id.Healthcheck);
                    break;
                case "Secret":
                    rg.check(R.id.Secretcheck);
                    break;
                case "Food":
                    rg.check(R.id.Foodcheck);
                    break;
            }

            if(xbool==true){
               anocheck.setChecked(true);
            }



    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            switch (v.getId()) {
                case R.id.before_btn:
                    fragmentManager.beginTransaction().remove(Frag_BoardEdit.this).commit();
                    fragmentManager.popBackStack();
                    break;

                case R.id.submit_btn:
                    title = w_title.getText().toString();
                    title = title.trim();
                    text = w_context.getText().toString();
                    text = text.trim();


                    if (selectType == 5) {
                        startToast("게시판을 선택해주세요");
                        break;
                    }

                    if (title.getBytes().length <= 0) {
                        startToast("제목을 입력해주세요");
                        break;
                    }
                    if (text.getBytes().length <= 0) {
                        startToast("내용을 입력해주세요");
                        break;
                    }
                    else{
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Board").document(xtime).update("content", text);
                    db.collection("Board").document(xtime).update("title", title);
                    db.collection("Board").document(xtime).update("theme", b1.getTheme());
                    db.collection("Board").document(xtime).update("anonymous", b1.getAnonymous()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Success on board registration");

                            fragmentManager.beginTransaction().remove(Frag_BoardEdit.this).commit();
                            ((MainActivity) v.getContext()).showSelectedBoard2(title, text, xtime,tmpname);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Failed to register board", e);
                        }
                    });}
            }


            }
        };


    private void startToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }



   }






