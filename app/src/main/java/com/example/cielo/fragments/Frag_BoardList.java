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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cielo.ExtractTemp;
import com.example.cielo.R;
import com.example.cielo.activity.MainActivity;
import com.example.cielo.adapter.BoardForm;
import com.example.cielo.adapter.listAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Frag_BoardList extends Fragment {
    public static Frag_BoardList newInstance() {
        return new Frag_BoardList();
    }

    public String theme;
    TextView text1;


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.board_list_frag, container, false);
        Bundle fromitem = getArguments();
        theme = fromitem.getString("theme");
        v.findViewById(R.id.write_btn).setOnClickListener(onClickListener);
        text1=v.findViewById(R.id.themename);
        SwipeRefreshLayout swipeRefreshLayout= v.findViewById(R.id.swiperefreshlayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity)v.getContext()).refreshTheme(theme);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;
    }
    @Override
    public void onStart() {

        super.onStart();

        text1.setText(theme);
        RecyclerView rcv;
        LinearLayoutManager llm;
        final listAdapter[] item = new listAdapter[1];

        List<DocumentSnapshot> ListofFireStore = new ArrayList<DocumentSnapshot>();
        ExtractTemp ex = new ExtractTemp();
        ex.getBoardResult(ListofFireStore,theme);

        ArrayList<Integer> now = new ArrayList<Integer>();
        now.add(0);

        rcv = getView().findViewById(R.id.blistview);
        llm = new LinearLayoutManager(getActivity());//종류는 총 3가지, ListView를 사용하기 위한 사용
        rcv.setHasFixedSize(true);//각 아이템이 보여지는 것을 일정하게
        rcv.setLayoutManager(llm);//앞서 선언한 리싸이클러뷰를 레이아웃메니저에 붙힌다


        ArrayList<BoardForm> list = new ArrayList<>();
        Handler handler = new Handler(){
            public void handleMessage(Message msg){
                item[0] = new listAdapter(getActivity(), list);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.
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
                                String id=ListofFireStore.get(now.get(0) - 1).get("id").toString();
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

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.write_btn:
                    ((MainActivity) getActivity()).showBoardwrite(Frag_BoardWrite.newInstance());
                    break;
            }
        }
    };

}