package com.example.cielo.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cielo.ExtractTemp;
import com.example.cielo.R;
import com.example.cielo.adapter.BoardForm;
import com.example.cielo.adapter.ScrapAdapter;
import com.example.cielo.adapter.ScrapForm;
import com.example.cielo.adapter.bscrapAdapter;
import com.example.cielo.adapter.listAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Frag_myboard extends Fragment {


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "my board)";
    ArrayList<String> key = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String tmp=user.getUid().toString();



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.myboard, container, false);
       v.findViewById(R.id.exit).setOnClickListener(onClickListener);


        return v;

    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.exit: //엑스버튼누르면 창닫기
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().remove(Frag_myboard.this).commit();
                    fragmentManager.popBackStack();
                    break;
            }
        }
    };

    @Override
    public void onStart() {

        super.onStart();

        RecyclerView rcv;
        LinearLayoutManager llm;
         ArrayList<BoardForm> list = new ArrayList<>();//넣을 데이터
        final bscrapAdapter[] item = new bscrapAdapter[1];

        rcv = getView().findViewById(R.id.scrapbe);
        llm = new LinearLayoutManager(getActivity());//종류는 총 3가지, ListView를 사용하기 위한 사용
        rcv.setLayoutManager(llm);//앞서 선언한 리싸이클러뷰를 레이아웃메니저에 붙힌다
        rcv.setHasFixedSize(true);//각 아이템이 보여지는 것을 일정하게
        List<DocumentSnapshot> ListofFireStore = new ArrayList<DocumentSnapshot>();
        ExtractTemp ex = new ExtractTemp();
        ex.getmyBoard(ListofFireStore,tmp);

        ArrayList<Integer> now = new ArrayList<Integer>();
        now.add(0);

        Handler handler = new Handler(){
            public void handleMessage(Message msg){
                item[0] = new bscrapAdapter(getActivity(), list);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.
                rcv.setAdapter(item[0]);// 그리고 만든 겍체를 리싸이클러뷰에 적용시킨다.
            }
        };
        new Thread(){
            //파일 받아오기 대기
            public void run(){
                while (true) {
                    try {
                        sleep(10);
                        if(ListofFireStore.size() != 0){
                            //성공
                            now.set(0, now.get(0)+1);

                            String title = ListofFireStore.get(now.get(0) - 1).get("title").toString();
                            String text = ListofFireStore.get(now.get(0) - 1).get("content").toString();
                            String commentNum = ListofFireStore.get(now.get(0) - 1).get("commentNum").toString();
                            String time = ListofFireStore.get(now.get(0) - 1).get("key").toString();
                            String theme = ListofFireStore.get(now.get(0) - 1).get("theme").toString();
                            String id = ListofFireStore.get(now.get(0) - 1).get("id").toString();
                            //ArrayList<String> comment = (ArrayList<String>) ListofFireStore.get(now.get(0) - 1).get("comment");
                            list.add(new BoardForm(getActivity(), title, text, commentNum, time,theme,id));

                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);



                        }
                    } catch (Exception e) {
                        Log.d("totalError", e.getMessage());
                        break;
                    }
                }
                Log.w("Out","out of thread");
            }
        }.start();


    }

    private void startToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }
}