package com.example.cielo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.cielo.R;
import com.example.cielo.activity.MainActivity;

public class Frag_padSearch extends Fragment {
    String TAG = "pad Activity) ";
    String keyword;
    boolean pass = false;
    int minPrice=0, maxPrice=99999, maxGrade=5;
    EditText keywordText, minPriceText, maxPriceText;

    public static Frag_padSearch newInstance() {
        return new Frag_padSearch();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.pad_search_frag,container,false);

        minPriceText = (EditText)v.findViewById(R.id.minPrice);
        maxPriceText = (EditText)v.findViewById(R.id.maxPrice);
        keywordText = (EditText)v.findViewById(R.id.searchingKeyword);
        v.findViewById(R.id.grade12).setOnClickListener(onClickListener);
        v.findViewById(R.id.grade3).setOnClickListener(onClickListener);
        v.findViewById(R.id.grade4).setOnClickListener(onClickListener);
        v.findViewById(R.id.grade5).setOnClickListener(onClickListener);
        v.findViewById(R.id.exitButton).setOnClickListener(onClickListener);
        v.findViewById(R.id.floatingActionButton2).setOnClickListener(onClickListener);
        return v;

    }



    @Override
    public void onStart() {

        super.onStart();


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.exitButton: //엑스버튼누르면 창닫기
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().remove(Frag_padSearch.this).commit();
                    fragmentManager.popBackStack();
                    break;
                case R.id.grade12:
                    pass = true;
                    maxGrade = 2;
                    break;
                case R.id.grade3:
                    pass = true;
                    maxGrade = 3;
                    break;
                case R.id.grade4:
                    pass = true;
                    maxGrade = 4;
                    break;
                case R.id.grade5:
                    pass = true;
                    maxGrade = 5;
                    break;
                case R.id.floatingActionButton2: //검색버튼누르면 result화면으로
                    keyword = keywordText.getText().toString();
                    minPrice = (minPriceText.getText().toString().equals(""))? 0 : Integer.parseInt(minPriceText.getText().toString());
                    maxPrice = (maxPriceText.getText().toString().equals(""))? 99999 : Integer.parseInt(maxPriceText.getText().toString());
                    if(pass == false && keyword.length()==0 && minPriceText.length()==0 && maxPriceText.length()==0){
                        startToast("검색어, 조건 중 하나라도 입력해 주세요");
                    }else{
                        if(minPrice< maxPrice){
                            fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().remove(Frag_padSearch.this).commit();
                            ((MainActivity)v.getContext()).ShowSearchResults(keyword, minPrice,maxPrice,maxGrade);
                        }else{
                            startToast("가격범위를 확인해주세요.");
                        }
                    }


            }
        }
    };


    private void startToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


}