package com.example.cielo.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Frag_calendarRecord extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "success";

    final FirebaseUser user = mAuth.getCurrentUser();

    FloatingActionButton cal;
    TextView st_datetxt;
    TextView end_datetxt;
    TextView datetxt;

    private int mDate, mMonth, mYear; //오늘날짜
    private int st_date, st_month, st_year; //선택한 시작날짜
    private int end_date, end_month, end_year; //선택한 시작날짜

    private String starting_date;
    private String ending_date;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.calendar_past_record,container,false);

        v.findViewById(R.id.exitButton).setOnClickListener(onClickListener);
        v.findViewById(R.id.start_datePicker).setOnClickListener(onClickListener);
        v.findViewById(R.id.end_datePicker).setOnClickListener(onClickListener);
        v.findViewById(R.id.upload_btn).setOnClickListener(onClickListener);

        final Calendar Cal = Calendar.getInstance();
        mDate = Cal.get(Calendar.DATE);
        mMonth = Cal.get(Calendar.MONTH)+1;
        mYear = Cal.get(Calendar.YEAR);



        st_datetxt = v.findViewById(R.id.start_date);
        end_datetxt = v.findViewById(R.id.end_date);

        return v;

    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_datePicker:
                    DatePickerDialog datePickerDialog1 = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year_, int month_, int date_) {
                            st_month = month_ + 1;
                            st_year = year_;
                            st_date = date_;
                            st_datetxt.setText(st_year + "년 " + st_month + "월 " + st_date + "일");

                        }
                    }, mYear, mMonth - 1, mDate);
                    datePickerDialog1.show();
                    break;

                case R.id.end_datePicker:
                    DatePickerDialog datePickerDialog2 = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year_, int month_, int date_) {
                            end_month = month_ + 1;
                            end_year = year_;
                            end_date = date_;
                            //날짜검증
                            if ((st_month > end_month) || ((st_month ==end_month) && (st_date > end_date))) {
                                startToast("시작일보다 빠른 날짜는 선택할 수 없습니다.");
                            } else
                                end_datetxt.setText(end_year + "년 " + end_month + "월 " + end_date + "일");

                        }
                    }, st_year, st_month - 1, st_date);
                    datePickerDialog2.show();
                    break;

                case R.id.exitButton:
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().remove(Frag_calendarRecord.this).commit();
                    fragmentManager.popBackStack();
                    ((MainActivity)getActivity()).replaceAddFragment(new Frag_calendar(),"exit");

                    break;

                case R.id.upload_btn:

                    starting_date = (st_year + "," + st_month + "," + st_date);
                    ending_date = (end_year + "," + end_month + "," + end_date);

                    DocumentReference docRef = db.collection("Calendar").document(mAuth.getUid());

                    //데이터베이스에 업로드
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    try{
                                        ArrayList<Integer> db_blood = (ArrayList<Integer>) document.getData().get("blood_amount");
                                        db_blood.add(null);
                                        ArrayList<Integer> db_pain = (ArrayList<Integer>) document.getData().get("pain");
                                        db_pain.add(null);
                                        ArrayList<Integer> db_mood = (ArrayList<Integer>) document.getData().get("mood");
                                        db_mood.add(null);
                                        ArrayList<String> db_start_date = (ArrayList<String>) document.getData().get("start_date");
                                        db_start_date.add(starting_date);
                                        ArrayList<String> db_end_date = (ArrayList<String>) document.getData().get("end_date");
                                        db_end_date.add(ending_date);
                                        ArrayList<String> db_duration_date= (ArrayList<String>) document.getData().get("duration_date");

                                        ArrayList<Float> db_term = (ArrayList<Float>) document.getData().get("term");

                                        //duration date 계산 과정
                                        DateFormat df = new SimpleDateFormat("yyyy,MM,dd");

                                        Date date = new Date();
                                        String time3 = df.format(System.currentTimeMillis());

                                        String s1 = starting_date;
                                        String s2 = ending_date;;

                                        try{
                                            Date d1 = df.parse(s1);
                                            Date d2 = df.parse(s2);

                                            Calendar c1 = Calendar.getInstance();
                                            Calendar c2 = Calendar.getInstance();

                                            //Calendar 타입으로 변경 add()메소드로 1일씩 추가해 주기위해 변경
                                            c1.setTime( d1 );
                                            c2.setTime( d2 );

                                            //시작날짜와 끝 날짜를 비교해, 시작날짜가 작거나 같은 경우 출력
                                            while( c1.compareTo( c2 ) !=1 ){
                                                db_duration_date.add(df.format(c1.getTime()));
                                                //시작날짜 + 1 일
                                                c1.add(Calendar.DATE, 1);
                                            }
                                        }
                                        catch(Exception e){

                                        }


                                        //term에 대한 데이터 db에 업로드과정
                                        try{
                                            String strFormat = "yyyy,MM,dd";
                                            SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
                                            Date startDate = sdf.parse(starting_date);

                                            if((db_start_date.size() - 1) < 1){
                                                db_term.add((float)-1);
                                            } else if((db_start_date.size() - 1) == 0){
                                                Date endDate = sdf.parse(db_start_date.get(1));

                                                long diffDay = (startDate.getTime() - endDate.getTime()) / (24*60*60*1000);
                                                float int_diffDay = (float) (Math.abs((int) diffDay) + 1);
                                                db_term.add(int_diffDay);
                                            }
                                            else{ //term = 이전 종료일 ~ 이번 시작일
                                                Date endDate = sdf.parse(db_end_date.get(db_end_date.size() - 1));
                                                Log.d("test enddate",String.valueOf(endDate));
                                                Log.d("test startdate",String.valueOf(startDate));

                                                long diffDay = (startDate.getTime() - endDate.getTime()) / (24*60*60*1000);
                                                float int_diffDay = (float) (Math.abs((int) diffDay) + 1);
                                                db_term.add(int_diffDay);
                                            }


                                        }catch(ParseException e){
                                            e.printStackTrace();
                                        }

                                        DocumentReference doc = db.collection("Calendar").document(mAuth.getUid());

                                        doc
                                                .update("blood_amount", db_blood, "pain", db_pain, "mood", db_mood,
                                                        "start_date", db_start_date, "end_date", db_end_date, "duration_date", db_duration_date,
                                                        "term", db_term)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "upload success");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "upload fail", e);
                                                    }
                                                });

                                    } catch (NullPointerException e){
                                        ArrayList<Integer> db_blood2 = new ArrayList<>();
                                        db_blood2.add(null);
                                        ArrayList<Integer> db_pain2 = new ArrayList<>();
                                        db_pain2.add(null);
                                        ArrayList<Integer> db_mood2 = new ArrayList<>();
                                        db_mood2.add(null);
                                        ArrayList<String> db_start_date2 = new ArrayList<>();
                                        db_start_date2.add(starting_date);
                                        ArrayList<String> db_end_date2 = new ArrayList<>();
                                        db_end_date2.add(ending_date);

                                        ArrayList<String> db_duration_date2= new ArrayList<>();

                                        ArrayList<Float> db_term2 = new ArrayList<>();
                                        db_term2.add(null);

                                        //duration date 계산 과정
                                        DateFormat df = new SimpleDateFormat("yyyy,MM,dd");

                                        Date date = new Date();

                                        String s1 = starting_date;
                                        String s2 = ending_date;;

                                        try{
                                            Date d1 = df.parse(s1);
                                            Date d2 = df.parse(s2);

                                            Calendar c1 = Calendar.getInstance();
                                            Calendar c2 = Calendar.getInstance();

                                            //Calendar 타입으로 변경 add()메소드로 1일씩 추가해 주기위해 변경
                                            c1.setTime( d1 );
                                            c2.setTime( d2 );

                                            //시작날짜와 끝 날짜를 비교해, 시작날짜가 작거나 같은 경우 출력
                                            while( c1.compareTo( c2 ) !=1 ){
                                                db_duration_date2.add(df.format(c1.getTime()));
                                                //시작날짜 + 1 일
                                                c1.add(Calendar.DATE, 1);
                                            }
                                        }
                                        catch(Exception e1){

                                        }





                                        DocumentReference doc = db.collection("Calendar").document(mAuth.getUid());

                                        doc
                                                .update("blood_amount", db_blood2, "pain", db_pain2, "mood", db_mood2,
                                                        "start_date", db_start_date2, "end_date", db_end_date2, "duration_date", db_duration_date2,
                                                        "term", db_term2)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "upload success");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "upload fail", e);
                                                    }
                                                });
                                    }


                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                } else {
                                    Log.d(TAG, "No such document");
                                }

                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                    ((MainActivity)getActivity()).replaceFragment(new Frag_calendar());
                    break;


            }
        }
    };


    private void startToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }


}