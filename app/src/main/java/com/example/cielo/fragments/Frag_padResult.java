package com.example.cielo.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cielo.ExtractTemp;
import com.example.cielo.R;
import com.example.cielo.activity.MainActivity;
import com.example.cielo.adapter.ResultAdapter;
import com.example.cielo.adapter.ResultForm;
import com.example.cielo.database.ChemicalList;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Frag_padResult extends Fragment {
    int searchType = 1; // 1:추천수정렬, 2: 가격낮은순, 3: 가격높은순 4: 리뷰순
    String TAG = "pad Activity) ";
    RadioGroup rg;
    ChemicalList chem = new ChemicalList();
    private String keyword;
    private int minPrice, maxPrice, maxGrade;


    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public static Frag_padResult newInstance() {
        return new Frag_padResult();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.pad_result_frag,container,false);
        rg = (RadioGroup)v.findViewById(R.id.options);
        Bundle fromitem = getArguments();
        keyword = fromitem.getString("keyword");
        minPrice = fromitem.getInt("minPrice");
        maxPrice = fromitem.getInt("maxPrice");
        maxGrade = fromitem.getInt("maxGrade");

        v.findViewById(R.id.searchBar3).setOnClickListener(onClickListener);
        v.findViewById(R.id.searchicon3).setOnClickListener(onClickListener);
        v.findViewById(R.id.gottenKeyword).setOnClickListener(onClickListener);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.byrecommend:
                        searchType = 1;
                        break;
                    case R.id.byminprice:
                        searchType = 2;
                        break;
                    case R.id.bymaxprice:
                        searchType = 3;
                        break;
                }//end switch

                RecyclerView rcv;
                LinearLayoutManager llm;
                final ResultAdapter[] item = new ResultAdapter[1];
                List<DocumentSnapshot> ListofFireStore = new ArrayList<DocumentSnapshot>();
                ExtractTemp ex = new ExtractTemp();
                ex.getResultByOption(ListofFireStore, minPrice, maxPrice, searchType);

                ArrayList<Integer> now = new ArrayList<Integer>();
                now.add(0); //어댑터 현재위치

                rcv = v.findViewById(R.id.recyclerView3);
                llm = new LinearLayoutManager(getActivity());//종류는 총 3가지, ListView를 사용하기 위한 사용
                rcv.setHasFixedSize(true);//각 아이템이 보여지는 것을 일정하게
                rcv.setLayoutManager(llm);//앞서 선언한 리싸이클러뷰를 레이아웃메니저에 붙힌다

                ArrayList<ResultForm> list = new ArrayList<>();//ResultForm을 통해 받게되는 데이터를 어레이 리스트화 시킨다.
                Handler handler = new Handler(){
                    public void handleMessage(Message msg){
                        item[0] = new ResultAdapter(getActivity(), list);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.
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

                                    Boolean check = keywordMatch(keyword, name) && gradeMatch(waterproof,maxGrade) &&gradeMatch(inside_cover,maxGrade)
                                            &&gradeMatch(absorber, maxGrade) && gradeMatch(adhesive, maxGrade) && gradeMatch(cover, maxGrade)
                                            &&priceMatch(Integer.parseInt(price), minPrice,maxPrice);
                                    if(check){
                                        list.add(new ResultForm(getActivity(),name,key,good, price,ingredients, length));
                                    }


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
        }); //end listener

        TextView keywordTextView = (TextView) v.findViewById(R.id.gottenKeyword);
        keywordTextView.setText(keyword);
        return v;

    }//end onCreateView

    @Override
    public void onStart() {
        super.onStart();

        RecyclerView rcv;
        LinearLayoutManager llm;
        final ResultAdapter[] item = new ResultAdapter[1];
        List<DocumentSnapshot> ListofFireStore = new ArrayList<DocumentSnapshot>();
        ExtractTemp ex = new ExtractTemp();
        ex.getResultByOption(ListofFireStore, minPrice, maxPrice, searchType);

        ArrayList<Integer> now = new ArrayList<Integer>();
        now.add(0); //어댑터 현재위치

        rcv = getView().findViewById(R.id.recyclerView3);
        llm = new LinearLayoutManager(getActivity());//종류는 총 3가지, ListView를 사용하기 위한 사용
        rcv.setHasFixedSize(true);//각 아이템이 보여지는 것을 일정하게
        rcv.setLayoutManager(llm);//앞서 선언한 리싸이클러뷰를 레이아웃메니저에 붙힌다

        ArrayList<ResultForm> list = new ArrayList<>();//ResultForm을 통해 받게되는 데이터를 어레이 리스트화 시킨다.
        Handler handler = new Handler(){
            public void handleMessage(Message msg){
                item[0] = new ResultAdapter(getActivity(), list);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.
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

                            Boolean check = keywordMatch(keyword, name) && gradeMatch(waterproof,maxGrade) &&gradeMatch(inside_cover,maxGrade)
                                    &&gradeMatch(absorber, maxGrade) && gradeMatch(adhesive, maxGrade) && gradeMatch(cover, maxGrade)
                                    &&priceMatch(Integer.parseInt(price), minPrice,maxPrice);
                            if(check){
                                list.add(new ResultForm(getActivity(),name,key,good, price,ingredients,length));
                            }


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


    private boolean priceMatch(int price, int minPrice, int maxPrice) {
        return (price<=maxPrice) && (price>=minPrice);
    }


    Boolean keywordMatch(String keyword, String productName){
        return (productName.toLowerCase().contains(keyword));
    }

    boolean gradeMatch(ArrayList<String> array, int maxGrade){
        int grade = 0;
        for (int i=0; i<array.size(); i++){
            grade = chem.returnGrade(array.get(i));
            if(grade>maxGrade) return false;

        }
        return true;
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.searchBar3:
                case R.id.searchicon3:
                case R.id.gottenKeyword:
                    // ((MainActivity)getActivity()).replaceFragment(Frag_padSearch.newInstance(),2);


                    ((MainActivity)getActivity()).showSearchbar(Frag_padSearch.newInstance());
                    break;

            }
        }
    };

}
