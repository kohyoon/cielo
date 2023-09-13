package com.example.cielo.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.cielo.R;
import com.example.cielo.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class Frag_calendarMemo extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "success";

    final FirebaseUser user = mAuth.getCurrentUser();

    FloatingActionButton cal;
    TextView datetxt;

    private int date, month, year; //선택한날짜
    private int mDate, mMonth, mYear; //오늘날짜

    private RadioButton r_bleed1, r_bleed2, r_bleed3, r_bleed4;
    private RadioGroup g_bleeds;

    private RadioButton m_good, m_tired, m_angry, m_crying;
    private RadioGroup g_moods;

    private ArrayList<String> db_date = new ArrayList<>();
    private int bloodamount; //생리양
    private int todaymood;
    private Integer painStrength; //생리통강도
    private boolean period;
    private String tmp_date;
    private ArrayList<String> fdate = new ArrayList<>();
    private ArrayList<Integer> fpain = new ArrayList<>();
    private ArrayList<Integer> fmood = new ArrayList<>();
    private ArrayList<Integer> fblood = new ArrayList<>();


    Boolean passed = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try{

            Bundle fromcal = getArguments();
            year = fromcal.getInt("year");
            month = fromcal.getInt("month");
            date = fromcal.getInt("date");
            passed= true;
        } catch (Exception e) {
            passed = false;
            e.printStackTrace();
        }
        View v=inflater.inflate(R.layout.calendar_note_frag,container,false);
        v.findViewById(R.id.exitButton).setOnClickListener(onClickListener);
        v.findViewById(R.id.datePicker).setOnClickListener(onClickListener);
        v.findViewById(R.id.upload_btn).setOnClickListener(onClickListener);

        datetxt = v.findViewById(R.id.date);

        SeekBar seekBar = (SeekBar) v.findViewById(R.id.painseek);
        TextView pain = (TextView) v.findViewById(R.id.painresult);
        TextView word = (TextView) v.findViewById(R.id.word);


        final Calendar Cal = Calendar.getInstance();

        mDate = Cal.get(Calendar.DATE);
        mMonth = Cal.get(Calendar.MONTH)+1;
        mYear = Cal.get(Calendar.YEAR);

        if(passed == false){

            datetxt.setText(mYear+"년 "+mMonth+"월 "+mDate+"일");

            date = mDate;
            month = mMonth;
            year = mYear;
        }else{
            datetxt.setText(year+"년 "+month+"월 "+date+"일");
        }
        //생리량에 대한 라디오버튼
        r_bleed1 = (RadioButton) v.findViewById(R.id.bleed1);
        r_bleed2 = (RadioButton) v.findViewById(R.id.bleed2);
        r_bleed3 = (RadioButton) v.findViewById(R.id.bleed3);
        r_bleed4 = (RadioButton) v.findViewById(R.id.bleed4);

        //생리량에 대한 라디오그룹
        g_bleeds = (RadioGroup) v.findViewById(R.id.bleeds);

        //생리양 라디오 버튼에 대한 listener
        g_bleeds.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.bleed1:
                        bloodamount = 1; //적음
                        break;
                    case R.id.bleed2:
                        bloodamount = 2; //보통
                        break;
                    case R.id.bleed3:
                        bloodamount = 3; //많음
                        break;
                    case R.id.bleed4:
                        bloodamount = 4; //매우많음
                        break;
                    default: //?
                        Toast.makeText(getActivity().getApplicationContext(),
                                "생리양을 선택해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //기분에 대한 라디오버튼
        m_good = (RadioButton) v.findViewById(R.id.moodgood);
        m_tired = (RadioButton) v.findViewById(R.id.moodtired);
        m_angry = (RadioButton) v.findViewById(R.id.moodangry);
        m_crying = (RadioButton) v.findViewById(R.id.moodcrying);

        //기분에 대한 라디오그룹
        g_moods = (RadioGroup) v.findViewById(R.id.moods);

        //기분 라디오 버튼에 대한 listener
        g_moods.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.moodgood:
                        todaymood = 1; //기분좋음
                        break;
                    case R.id.moodtired:
                        todaymood = 2; //피곤함
                        break;
                    case R.id.moodangry:
                        todaymood = 3; //예민함
                        break;
                    case R.id.moodcrying:
                        todaymood = 4; //우울함
                        break;
                    default: //?
                        Toast.makeText(getActivity().getApplicationContext(),
                                "기분을 선택해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (progress % 20 == 0) {
                    pain.setText(String.valueOf(progress/20));
                } else {
                    seekBar.setProgress((progress / 20) * 20);
                    pain.setText(String.valueOf(progress / 20));
                    painStrength=Integer.valueOf(progress / 20);
                }
                word.setText("단계");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return v;



    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.datePicker:

                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year_, int month_, int date_) {
                            month = month_ + 1;
                            year = year_;
                            Log.d("test", String.valueOf(month));
                            date = date_;
                            //날짜검증
                            if ((month > mMonth) || ((month == mMonth) && (date > mDate))) {
                                startToast("오늘 이후의 날짜는 선택할 수 없습니다.");
                            } else datetxt.setText(year + "년 " + month + "월 " + date + "일");

                        }
                    }, year, month-1, date);
                    datePickerDialog.show();
                    break;
                case R.id.exitButton:
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().remove(Frag_calendarMemo.this).commit();
                    fragmentManager.popBackStack();
                    ((MainActivity)getActivity()).replaceFragment(new Frag_calendar());

                    break;

                case R.id.upload_btn:

                    period = true;
                    Log.d("test", String.valueOf(month));
                    tmp_date = (year + "," + month + "," + date);
                    Log.d("test",tmp_date);




                    DocumentReference docRef = db.collection("Calendar").document(mAuth.getUid());

                    //데이터베이스에 업로드

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    //ArrayList<String> db_pred_date= (ArrayList<String>) document.getData().get("pred_date");

                                    ArrayList<Integer> blood = (ArrayList<Integer>) document.getData().get("blood_amount");
                                    blood.add(bloodamount);
                                    ArrayList<Integer> pain = (ArrayList<Integer>) document.getData().get("pain");
                                    pain.add(painStrength);
                                    ArrayList<Integer> mood = (ArrayList<Integer>) document.getData().get("mood");
                                    mood.add(todaymood);
                                    ArrayList<String> start_date = (ArrayList<String>) document.getData().get("start_date");
                                    start_date.add(tmp_date);
                                    ArrayList<String> end_date = (ArrayList<String>) document.getData().get("end_date");


                                    DocumentReference doc = db.collection("Calendar").document(mAuth.getUid());

                                    doc
                                            .update("blood_amount", blood, "pain", pain, "mood", mood,
                                                    "start_date", start_date, "period", period, "uid", mAuth.getUid(),
                                                    "pred_date", null)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "upload success");
                                                    //저장완료 안내메시지
                                                    Toast.makeText(getActivity().getApplicationContext(), "등록되었습니다", Toast.LENGTH_SHORT).show();

                                                    ((MainActivity)getActivity()).replaceFragment(new Frag_calendar());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "upload fail", e);
                                                }
                                            });


                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                } else { //해당 document가 없을때



                                    Log.d(TAG, "No such document");
                                }

                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                    break;


            }
        }
    };


    private void startToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }



}