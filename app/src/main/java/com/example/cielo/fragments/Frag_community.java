package com.example.cielo.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cielo.ExtractTemp;
import com.example.cielo.R;
import com.example.cielo.activity.MainActivity;
import com.example.cielo.adapter.BoardForm;
import com.example.cielo.adapter.HottopicAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Frag_community extends Fragment {
    public static Frag_community newInstance(){ return new Frag_community(); }
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser user = mAuth.getCurrentUser();
    String TAG = "community)";
    String curtime;

    TextView nick, count;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.community_frag,container,false);
        v.findViewById(R.id.submitbut).setOnClickListener(onClickListener);
        v.findViewById(R.id.openFood).setOnClickListener(onClickListener);
        v.findViewById(R.id.openLove).setOnClickListener(onClickListener);
        v.findViewById(R.id.openfree).setOnClickListener(onClickListener);
        v.findViewById(R.id.openSecret).setOnClickListener(onClickListener);
        v.findViewById(R.id.openHealth).setOnClickListener(onClickListener);
        v.findViewById(R.id.mywrite).setOnClickListener(onClickListener);
        nick = v.findViewById(R.id.com_nick);
        count = v.findViewById(R.id.com_count);
        v.findViewById(R.id.hotboardview).setNestedScrollingEnabled(false);

        return v;


    }
    @Override
    public void onStart() {
        super.onStart();

        String ctime = new Timestamp(System.currentTimeMillis()).toString();
        curtime=getDateformat(ctime);
        DocumentReference docRef = db.collection("userInfo").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        nick.setText(document.getData().get("nickname").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        db.collection("Board")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int num = -1;
                        if (task.isSuccessful()) {
                            num = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getData().get("id").toString().equals(user.getUid())){
                                    num++;
                                }
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            count.setText(String.valueOf(num));
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        RecyclerView rcv;
        LinearLayoutManager llm;
        List<DocumentSnapshot> ListinDoc  = new ArrayList<DocumentSnapshot>();
        final HottopicAdapter[] item = new HottopicAdapter[1];

        rcv = getView().findViewById(R.id.hotboardview);
        llm = new LinearLayoutManager(getActivity());//종류는 총 3가지, ListView를 사용하기 위한 사용
        rcv.setHasFixedSize(true);//각 아이템이 보여지는 것을 일정하게
        rcv.setLayoutManager(llm);//앞서 선언한 리싸이클러뷰를 레이아웃메니저에 붙힌다
        ArrayList<BoardForm> list = new ArrayList<>(5);

        ExtractTemp ex = new ExtractTemp();
        ex.getHottopic(ListinDoc);
        ArrayList<Integer> now = new ArrayList<Integer>();
        now.add(0);


        llm = new LinearLayoutManager(getActivity());//종류는 총 3가지, ListView를 사용하기 위한 사용
        rcv.setHasFixedSize(true);//각 아이템이 보여지는 것을 일정하게
        rcv.setLayoutManager(llm);//앞서 선언한 리싸이클러뷰를 레이아웃메니저에 붙힌다
        Handler handler = new Handler(){
            public void handleMessage(Message msg){

                item[0] = new HottopicAdapter(getActivity(), list);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.
                rcv.setAdapter(item[0]);// 그리고 만든 겍체를 리싸이클러뷰에 적용시킨다.
            }
        };
        new Thread(){
            //파일 받아오기 대기
            public void run(){
                while (true) {
                    try {
                        sleep(10);


                        if (ListinDoc.size() != 0) {
                            now.set(0, now.get(0) + 1);
                            Log.d("Frag=커뮤니티", "Success on my list inti");
                            String theme = ListinDoc.get(now.get(0) - 1).get("theme").toString();
                            String title = ListinDoc.get(now.get(0) - 1).get("title").toString();
                            String text = ListinDoc.get(now.get(0) - 1).get("content").toString();
                            String commentNum = ListinDoc.get(now.get(0) - 1).get("commentNum").toString();
                            String time = ListinDoc.get(now.get(0) - 1).get("key").toString();
                            String id = ListinDoc.get(now.get(0) - 1).get("id").toString();

                            String mytime=getDateformat(time);


                            if(curtime.equals(getDateformat(time))&&list.size()<5) {

                                Log.d("Frag=커뮤니티", theme + title + text + commentNum);
                                list.add(new BoardForm(getActivity(), title, text, commentNum, time, theme, id));


                                Message msg = handler.obtainMessage();
                                handler.sendMessage(msg);
                            }


                        }

                    } catch(Exception e){
                        Log.d("totalError", e.getMessage());
                        break;
                    }

                }
                Log.w("Out","out of thread");
            }
        }.start();

    }



    View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(android.view.View v) {
            switch (v.getId()) {
                case R.id.submitbut:
                    ((MainActivity) getActivity()).showBoardwrite(Frag_BoardWrite.newInstance());
                    break;
                case R.id.openfree:
                    ((MainActivity)v.getContext()).showSelectedTheme("Free");
                    break;
                case R.id.openLove:
                    ((MainActivity)v.getContext()).showSelectedTheme("Love");
                    break;
                case R.id.openFood:
                    ((MainActivity)v.getContext()).showSelectedTheme("Food");
                    break;
                case R.id.openHealth:
                    ((MainActivity)v.getContext()).showSelectedTheme("Health");
                    break;
                case R.id.openSecret:
                    ((MainActivity)v.getContext()).showSelectedTheme("Secret");
                    break;
                case R.id.mywrite:
                    ((MainActivity)getActivity()).replaceAddFragment(new Frag_myboard(),"scrap");
                    break;

            }}};
    public String getDateformat(String time){
        String dfDate=time.substring(0,10);
        return dfDate;
    }
}