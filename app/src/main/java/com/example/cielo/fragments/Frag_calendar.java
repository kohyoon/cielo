package com.example.cielo.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cielo.R;
import com.example.cielo.activity.MainActivity;
import com.example.cielo.ml.Cyclepredict;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;

import static android.graphics.Color.RED;


public class Frag_calendar extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "success";
    String[] temp_pred_date;
    int yearS, monthS, dateS;
    boolean calclick = false;
    ProgressDialog progressDialog;
    TextView text_btn;
    TextView word,countday, word2;
    boolean dperiod;
    //String[] result;
    String temp_date, temp_st_date, temp_result;
    int temp_pred_term;
    float[] term_arr;
    MaterialCalendarView materialCalendarView;

    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();


    public void loading() {
        //로딩
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("잠시만 기다려주세요.");
                        progressDialog.show();
                    }
                }, 0);
    }

    public void loadingEnd() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.calendar_frag,container,false);
        calclick = false;

        text_btn = (TextView) v.findViewById(R.id.textView13); //시작하기, 종료하기 textview
        word = (TextView) v.findViewById(R.id.word);
        countday = (TextView) v.findViewById(R.id.countday);
        word2 = (TextView) v.findViewById(R.id.word2);

        //v.findViewById(R.id.memo_btn).setOnClickListener(onClickListener);

        materialCalendarView = (MaterialCalendarView) v.findViewById(R.id.calendar);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(new SundayDecorator(), new SaturdayDecorator(), oneDayDecorator);



        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                yearS = date.getYear();
                monthS = date.getMonth() + 1;
                dateS = date.getDay();

                if(text_btn.getText().toString().equals("시작하기")){
                    AlertDialog.Builder dlg;
                    dlg = new AlertDialog.Builder(getContext());
                    dlg.setMessage(String.valueOf(monthS)+"월 "+String.valueOf(dateS)+"일에 시작하셨나요?")
                            .setCancelable(false)
                            .setPositiveButton("네",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ((MainActivity)getActivity()).replaceAddFragment(new Frag_calendarMemo(),"memo",yearS, monthS,dateS);
                                        }
                                    }
                            ).setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            }
                    );

                    AlertDialog alert = dlg.create();
                    alert.show();
                    //  materialCalendarView.clearSelection();
                }else{
                    AlertDialog.Builder dlg;
                    dlg = new AlertDialog.Builder(getContext());
                    dlg.setMessage(String.valueOf(monthS)+"월 "+String.valueOf(dateS)+"일에 종료하셨나요?")
                            .setCancelable(false)
                            .setPositiveButton("네",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {


                                            DocumentReference docRef = db.collection("Calendar").document(mAuth.getUid());
                                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            dperiod = document.getBoolean("period");
                                                            ArrayList<String> end_date = (ArrayList<String>) document.getData().get("end_date");

                                                            end_date.add(yearS+","+monthS+","+dateS);
                                                            dperiod = false;

                                                            ArrayList<String> db_st_date = (ArrayList<String>) document.getData().get("start_date");
                                                            ArrayList<String> db_end_date = (ArrayList<String>) document.getData().get("end_date");
                                                            ArrayList<Integer> db_term = (ArrayList<Integer>) document.getData().get("term");
                                                            ArrayList<String> db_duration_date = (ArrayList<String>) document.getData().get("duration_date");

                                                            //duration date 계산 과정
                                                            DateFormat df = new SimpleDateFormat("yyyy,MM,dd");


                                                            String s1 = db_st_date.get(db_st_date.size() - 1);
                                                            String s2 = yearS+","+monthS+","+dateS;

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
                                                            String strFormat = "yyyy,MM,dd";

                                                            SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
                                                            try{
                                                                Date startDate1 = sdf.parse(db_st_date.get(db_st_date.size() - 1));
                                                                Date startDate2 = sdf.parse(db_end_date.get(db_end_date.size() - 1));

                                                                //두날짜 사이의 시간 차이(ms)를 하루 동안의 ms(24시*60분*60초*1000밀리초) 로 나눈다.
                                                                long diffDay = (startDate1.getTime() - startDate2.getTime()) / (24*60*60*1000);
                                                                Integer int_diffDay = Math.abs((int) diffDay);
                                                                db_term.add(int_diffDay);

                                                            }catch(ParseException e){
                                                                e.printStackTrace();
                                                            }




                                                            DocumentReference doc = db.collection("Calendar").document(mAuth.getUid());

                                                            doc
                                                                    .update("end_date", end_date, "period", dperiod,
                                                                            "duration_date", db_duration_date, "term", db_term)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Log.d(TAG, "upload success");
                                                                            //저장완료 안내메시지
                                                                            Toast.makeText(getActivity().getApplicationContext(),
                                                                                    "종료일이 등록되었습니다", Toast.LENGTH_SHORT).show();
                                                                            loadingEnd();
                                                                            ((MainActivity)getActivity()).replaceFragment(new Frag_calendar());
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.w(TAG, "upload fail", e);
                                                                        }
                                                                    });

                                                        } else {
                                                            Log.d(TAG, "No such document");
                                                        }

                                                    } else {
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });//end here
                                        }
                                    }
                            ).setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            }
                    );

                    AlertDialog alert = dlg.create();
                    alert.show();



                }

            }
        });


        v.findViewById(R.id.memo_btn).setOnClickListener(onClickListener);
        v.findViewById(R.id.upload_btn).setOnClickListener(onClickListener);


        return v;
    }


    @Override
    public void onStart() {

        super.onStart();


        loading();
        //파이어베이스에서 날짜 읽어와서 변수에 저장 & period 읽어와서 memo_btn의 text 수정
        DocumentReference docRef = db.collection("Calendar").document(mAuth.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {


                        ArrayList<String> db_st_date = (ArrayList<String>) document.getData().get("start_date");

                        ArrayList<String> db_end_date = (ArrayList<String>) document.getData().get("end_date");



                        if(db_st_date==null || db_st_date.size()<1){
                            if(db_st_date!=null) Log.d("test",String.valueOf(db_st_date.size()));
                            startToast("생리일을 2회 이상 기록해주세요.");
                            loadingEnd();
                            ((MainActivity)getActivity()).replaceFragment(new Frag_calendarRecord());
                        }
                        else {

                            if(db_st_date.size()==1) {
                                loadingEnd();
                                ((MainActivity)getActivity()).replaceFragment(new Frag_calendar());
                            }
                            else{

                                ArrayList<String> db_duration_date = (ArrayList<String>) document.getData().get("duration_date");


                                temp_pred_date = new String[1];

                                ArrayList<Float> db_term = (ArrayList<Float>) document.getData().get("term");

                                try {
                                    int n = db_term.size();
                                    term_arr = new float[n];

                                    for (int i = 1; i < n; i++) {
                                        term_arr[i] = Float.parseFloat(String.valueOf(db_term.get(i)));
                                    }
                                } catch (Exception e) {
                                    Log.d("test", e.getMessage());
                                    term_arr = new float[]{0};
                                }


                                try {
                                    dperiod = document.getBoolean("period");
                                    if (dperiod == true) { //생리중일때
                                        text_btn.setText("종료하기");
                                        ArrayList<String> tmp_duration_date = new ArrayList<String>();

                                        temp_date = db_st_date.get(db_st_date.size() - 1); //가장 마지막에 등록된 start date 가져오기

                                        //duration date 계산 과정
                                        DateFormat df = new SimpleDateFormat("yyyy,MM,dd");

                                        String time3 = df.format(System.currentTimeMillis());
                                        String s1 = db_st_date.get(db_st_date.size() - 1);
                                        String s2 = time3;;

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
                                                tmp_duration_date.add(df.format(c1.getTime()));
                                                //시작날짜 + 1 일
                                                c1.add(Calendar.DATE, 1);
                                            }
                                        }
                                        catch(Exception e){

                                        }

                                        // 생리 며칠째인지 표현하는 과정
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy,MM,dd");
                                        Date date = new Date();


                                        String strFormat = "yyyy,MM,dd";
                                        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
                                        try {
                                            Date startDate1 = sdf.parse(temp_date);
                                            Date startDate2 = sdf.parse(time3);

                                            long diffDay = (startDate2.getTime() - startDate1.getTime()) / (24 * 60 * 60 * 1000);
                                            Integer int_diffDay = Math.abs((int) diffDay);
                                            String fin_diffDay = String.valueOf(int_diffDay);
                                            countday.setText("" + fin_diffDay);

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        word2.setText("일째");

                                        //calendar에 표시할 날짜들 arraylist로 합친 후에 array로 변환 후 apisimulator 실행
                                        ArrayList<String> res_arr_list = new ArrayList<>();
                                        res_arr_list.addAll(db_st_date);
                                        res_arr_list.addAll(db_end_date);
                                        res_arr_list.addAll(db_duration_date);
                                        res_arr_list.addAll(tmp_duration_date);

                                        String[] result = res_arr_list.toArray(new String[res_arr_list.size()]);

                                        //calendar에 구현하는 함수 호출
                                        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

                                        loadingEnd();
                                    } else { //생리중이 아닐때
                                        text_btn.setText("시작하기");


                                        //calendar에 표시할 날짜들 arraylist로 합친 후에 array로 변환 후 apisimulator 실행
                                        ArrayList<String> res_arr_list = new ArrayList<>();
                                        res_arr_list.addAll(db_st_date);
                                        res_arr_list.addAll(db_end_date);
                                        res_arr_list.addAll(db_duration_date);

                                        String[] result = res_arr_list.toArray(new String[res_arr_list.size()]);

                                        //calendar에 구현하는 함수 호출
                                        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());


                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy,MM,dd");

                                        temp_pred_term = predict(term_arr); //예측모델로 예측term 받기
                                        Log.d("test", String.valueOf(temp_pred_term));
                                        temp_st_date = db_st_date.get(db_st_date.size() - 1); //가장 마지막에 등록된 start date 가져오기

//start
                                        try {
                                            Date date = simpleDateFormat.parse(temp_st_date);
                                            Calendar cal = Calendar.getInstance();
                                            cal.setTime(date);
                                            cal.add(Calendar.DAY_OF_MONTH, temp_pred_term); //temp_pred_term 이후의 날짜

                                            temp_result = simpleDateFormat.format(cal.getTime());
                                            temp_pred_date[0] = temp_result;


                                            String time3 = simpleDateFormat.format(System.currentTimeMillis());
                                            Date endDate = simpleDateFormat.parse(temp_result);
                                            Date todate = simpleDateFormat.parse(time3);

                                            while (endDate.before(todate)) { //오늘 날짜가 예정일을 지났으면 예정일 + 예상주기 계산을 통해 다음 예측일 구하기
                                                Date date2 = simpleDateFormat.parse(temp_result);
                                                cal.setTime(date2);
                                                cal.add(Calendar.DAY_OF_MONTH, temp_pred_term); //temp_pred_term 이후의 날짜
                                                temp_result = simpleDateFormat.format(cal.getTime());
                                                Log.d("test3", temp_result);

                                                endDate = simpleDateFormat.parse(temp_result);

                                                endDate.before(todate);

                                            }



                                            temp_result = simpleDateFormat.format(cal.getTime());
                                            temp_pred_date[0] = temp_result;

                                            temp_result = simpleDateFormat.format(cal.getTime());
                                            temp_pred_date[0] = temp_result;









                                        } catch (ParseException ep) {
                                            ep.printStackTrace();
                                        }



                                        //생리예정일 구현하는 함수 호출
                                        new ApiSimulator2(temp_pred_date).executeOnExecutor(Executors.newSingleThreadExecutor());

                                        //생리 며칠째인지 표현하는 과정
                                        String time3 = simpleDateFormat.format(System.currentTimeMillis()); //오늘날짜

                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy,MM,dd");
                                        try {
                                            Date startDate1 = sdf.parse(temp_date);
                                            Date startDate2 = sdf.parse(time3);

                                            long diffDay = (startDate1.getTime() - startDate2.getTime()) / (24 * 60 * 60 * 1000);
                                            Integer int_diffDay = Math.abs((int) diffDay);
                                            String fin_diffDay = String.valueOf(int_diffDay);
                                            countday.setText("" + fin_diffDay); //생리며칠전인지 출력

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        word2.setText("일전");

                                        loadingEnd();

                                        DocumentReference doc = db.collection("Calendar").document(mAuth.getUid());

                                        doc
                                                .update("pred_date", temp_result)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "upload success");

                                                        loadingEnd();
                                                        ((MainActivity)getActivity()).replaceFragment(new Frag_calendar());
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "upload fail", e);
                                                    }
                                                });
                                    }
                                } catch (NullPointerException e) {

                                    try {
                                        //temp_result로부터 오늘까지의 날짜 계산
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy,MM,dd");
                                        Date date = new Date();
                                        String time3 = sdf.format(System.currentTimeMillis());
                                        Date today_date = sdf.parse(time3);
                                        Date endDate = sdf.parse(temp_result);

                                        long diffDay = (endDate.getTime() - today_date.getTime()) / (24 * 60 * 60 * 1000);
                                        int int_diffDay = (Math.abs((int) diffDay));


                                        countday.setText(Integer.toString(int_diffDay)); //예측날짜 받기
                                        word2.setText("일전");

                                        loadingEnd();
                                    } catch (ParseException e2) {

                                    }



                                }
                            }



                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }




    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.memo_btn:

                    loading();

                    DocumentReference docRef = db.collection("Calendar").document(mAuth.getUid());

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    dperiod = document.getBoolean("period");
                                    if (dperiod == true){ //생리중일때

                                        ArrayList<String> end_date = (ArrayList<String>) document.getData().get("end_date");
                                        //오늘 날짜를 구해서 db의 end date에 업로드
                                        long now = System.currentTimeMillis();
                                        Date tmp_date = new Date(now);
                                        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy,MM,dd");
                                        String today = simpleDate.format(tmp_date);

                                        end_date.add(today);
                                        dperiod = false;

                                        ArrayList<String> db_st_date = (ArrayList<String>) document.getData().get("start_date");
                                        ArrayList<String> db_end_date = (ArrayList<String>) document.getData().get("end_date");
                                        ArrayList<Integer> db_term = (ArrayList<Integer>) document.getData().get("term");
                                        ArrayList<String> db_duration_date = (ArrayList<String>) document.getData().get("duration_date");

                                        //duration date 계산 과정
                                        DateFormat df = new SimpleDateFormat("yyyy,MM,dd");

                                        String time3 = df.format(System.currentTimeMillis());
                                        String s1 = db_st_date.get(db_st_date.size() - 1);
                                        String s2 = time3;;

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
                                        String strFormat = "yyyy,MM,dd";

                                        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
                                        try{
                                            Date startDate1 = sdf.parse(db_st_date.get(db_st_date.size() - 1));
                                            Date startDate2 = sdf.parse(db_end_date.get(db_end_date.size() - 1));

                                            //두날짜 사이의 시간 차이(ms)를 하루 동안의 ms(24시*60분*60초*1000밀리초) 로 나눈다.
                                            long diffDay = (startDate1.getTime() - startDate2.getTime()) / (24*60*60*1000);
                                            Integer int_diffDay = Math.abs((int) diffDay);
                                            db_term.add(int_diffDay);

                                        }catch(ParseException e){
                                            e.printStackTrace();
                                        }




                                        DocumentReference doc = db.collection("Calendar").document(mAuth.getUid());

                                        doc
                                                .update("end_date", end_date, "period", dperiod,
                                                        "duration_date", db_duration_date, "term", db_term)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "upload success");
                                                        //저장완료 안내메시지
                                                        Toast.makeText(getActivity().getApplicationContext(),
                                                                "종료일이 등록되었습니다", Toast.LENGTH_SHORT).show();
                                                        loadingEnd();
                                                        ((MainActivity)getActivity()).replaceFragment(new Frag_calendar());
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "upload fail", e);
                                                    }
                                                });
                                    }
                                    else{ //생리중이 아닐때 버튼 클릭
                                        //add here
                                        loadingEnd();
                                        ((MainActivity)getActivity()).replaceAddFragment(new Frag_calendarMemo(),"memo");
                                    }

                                } else {
                                    Log.d(TAG, "No such document");
                                }

                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });

                    break;
                case R.id.upload_btn:
                    ((MainActivity)getActivity()).replaceAddFragment(new Frag_calendarRecord(),"record");
                    break;
            }
        }
    };


    //토요일 파란색으로
    public class SaturdayDecorator implements DayViewDecorator {

        private final Calendar calendar = Calendar.getInstance();

        public SaturdayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SATURDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.BLUE));
        }
    }

    //일요일 빨간색으로
    public class SundayDecorator implements DayViewDecorator {

        private final Calendar calendar = Calendar.getInstance();

        public SundayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SUNDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(RED));
        }
    }

    //오늘 날짜 표시
    public class OneDayDecorator implements DayViewDecorator {

        private CalendarDay date;

        public OneDayDecorator() {
            date = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new RelativeSizeSpan(1.4f));
            view.addSpan(new ForegroundColorSpan(Color.rgb(4, 106, 150)));
        }

        public void setDate(Date date) {
            this.date = CalendarDay.from(date);
        }
    }


    //이벤트데이 표시
    public class EventDecorator implements DayViewDecorator {

        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates, Context applicationContext) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, Color.RED));
        }
    }

    //이벤트데이 표시
    public class EventDecorator2 implements DayViewDecorator {

        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator2(int color, Collection<CalendarDay> dates, Context applicationContext) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.heart_img));
        }
    }


    //예측모델 함수
    public int predict(float[] info){
        int nextDuration;
        try {
            assert info.length >= 7;
            float[] duration = {info[info.length-6], info[info.length-5], info[info.length-4], info[info.length-3], info[info.length-2], info[info.length-1]}; //최근 6개월 주기 데이터
            Cyclepredict model = Cyclepredict.newInstance(getActivity());
            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 6, 1}, DataType.FLOAT32);
            inputFeature0.loadArray(duration);

            // Runs model inference and gets result.
            Cyclepredict.Outputs outputs = model.process(inputFeature0);

            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            nextDuration = (int) outputFeature0.getFloatArray()[0];
            // Releases model resources if no longer used.
            model.close();
        } catch (AssertionError | IOException e) {
            nextDuration = 28; //6개월치 분량이 없으면 기본 28일로 세팅
        }

        System.out.println(nextDuration);
        return nextDuration;
    }

    //생리한 날짜 표시해주는 apisimulator
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int로 변환
            for (int i = 0; i < Time_Result.length; i++) {

                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                calendar.set(year, month - 1, dayy);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (getActivity().isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator(Color.GREEN, calendarDays,getActivity().getApplicationContext()));
        }
    }

    //생리예정일 표시해주는 apisimulator2
    private class ApiSimulator2 extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator2(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int로 변환
            for (int i = 0; i < Time_Result.length; i++) {

                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                calendar.set(year, month - 1, dayy);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (getActivity().isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator2(Color.GREEN, calendarDays,getActivity().getApplicationContext()));
        }
    }
    private void startToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }


}