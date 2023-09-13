package com.example.cielo.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cielo.ExtractTemp;
import com.example.cielo.R;
import com.example.cielo.activity.MainActivity;
import com.example.cielo.adapter.ItemAdapter;
import com.example.cielo.adapter.ResultForm;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class Frag_pad extends Fragment {
    String TAG = "pad Activity) ";
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    // 위의 저장소를 참조하는 파일명으로 지정

    public static Frag_pad newInstance() {
        return new Frag_pad();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.pad_frag,container,false);

        v.findViewById(R.id.searchBar).setOnClickListener(onClickListener);
        v.findViewById(R.id.searchicon).setOnClickListener(onClickListener);
        v.findViewById(R.id.inputtext).setOnClickListener(onClickListener);
        return v;

    }

    @Override
    public void onStart() {

        super.onStart();

        RecyclerView rcv;
        LinearLayoutManager llm;
        final ItemAdapter[] item = new ItemAdapter[1];

        List<DocumentSnapshot> ListofFireStore = new ArrayList<DocumentSnapshot>();
        ExtractTemp ex = new ExtractTemp();

        //여기를 state별로 다르게 하자
        ex.getPadResult(ListofFireStore);


        ArrayList<Integer> now = new ArrayList<Integer>();
        now.add(0);

        rcv = getView().findViewById(R.id.recyclerView);
        llm = new LinearLayoutManager(getActivity());//종류는 총 3가지, ListView를 사용하기 위한 사용
        rcv.setHasFixedSize(true);//각 아이템이 보여지는 것을 일정하게
        rcv.setLayoutManager(llm);//앞서 선언한 리싸이클러뷰를 레이아웃메니저에 붙힌다


        ArrayList<ResultForm> list = new ArrayList<>();//ItemFrom을 통해 받게되는 데이터를 어레이 리스트화 시킨다.
        Handler handler = new Handler(){
            public void handleMessage(Message msg){
                item[0] = new ItemAdapter(getActivity(), list);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.
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

                            ArrayList<ArrayList<String>> ingredients = new ArrayList<>();
                            String key = ListofFireStore.get(now.get(0)-1).get("key").toString();
                            String good = ListofFireStore.get(now.get(0)-1).get("recommend").toString();
                            String name = ListofFireStore.get(now.get(0)-1).get("pad_name").toString();
                            String price = ListofFireStore.get(now.get(0)-1).get("pad_price").toString();
                            String length = ListofFireStore.get(now.get(0)-1).get("length").toString();

                            ArrayList<String> waterproof = (ArrayList<String>) ListofFireStore.get(now.get(0)-1).get("waterproof");
                            ingredients.add(waterproof);
                            ArrayList<String> cover = (ArrayList<String>) ListofFireStore.get(now.get(0)-1).get("cover");
                            ingredients.add(cover);
                            ArrayList<String> inside_cover = (ArrayList<String>) ListofFireStore.get(now.get(0)-1).get("inside_cover");
                            ingredients.add(inside_cover);
                            ArrayList<String> absorber = (ArrayList<String>) ListofFireStore.get(now.get(0)-1).get("absorber");
                            ingredients.add(absorber);
                            ArrayList<String> adhesive = (ArrayList<String>) ListofFireStore.get(now.get(0)-1).get("adhesive");
                            ingredients.add(adhesive);
                            list.add(new ResultForm(getActivity(),name,key,good, price,ingredients, length));

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
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.searchBar:
                case R.id.inputtext:
                case R.id.searchicon:
                   // ((MainActivity)getActivity()).replaceFragment(Frag_padSearch.newInstance(),2);
                    ((MainActivity)getActivity()).showSearchbar(Frag_padSearch.newInstance());
                    break;

            }
        }
    };



}