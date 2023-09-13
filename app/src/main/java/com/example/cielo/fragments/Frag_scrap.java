package com.example.cielo.fragments;

import android.os.Bundle;
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

import com.example.cielo.R;
import com.example.cielo.adapter.ScrapAdapter;
import com.example.cielo.adapter.ScrapForm;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Frag_scrap extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "scrap)";
    ArrayList<String> key = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser user = mAuth.getCurrentUser();
    ArrayList<ArrayList<DocumentSnapshot>> r_code = new ArrayList<ArrayList<DocumentSnapshot>>();
    ArrayList<ArrayList<DocumentSnapshot>> r_name = new ArrayList<ArrayList<DocumentSnapshot>>();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.myscrap, container, false);

        v.findViewById(R.id.exit).setOnClickListener(onClickListener);


        return v;

    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.exit: //엑스버튼누르면 창닫기
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().remove(Frag_scrap.this).commit();
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
        final ArrayList<ScrapForm> list = new ArrayList<>();//넣을 데이터
        final ScrapAdapter item = new ScrapAdapter(getActivity(), list);

        rcv = getView().findViewById(R.id.scrapcycle);
        llm = new LinearLayoutManager(getActivity());//종류는 총 3가지, ListView를 사용하기 위한 사용
        rcv.setLayoutManager(llm);//앞서 선언한 리싸이클러뷰를 레이아웃메니저에 붙힌다


        DocumentReference docRef = db.collection("Myscrap").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        r_code.add((ArrayList<DocumentSnapshot>) document.get("pad_code"));
                        r_name.add((ArrayList<DocumentSnapshot>) document.get("pad_name"));
                        try{
                            for(int i=0;i<r_code.get(0).size();i++) {
                                String pad_code = String.valueOf(r_code.get(0).get(i));
                                String pad_name = String.valueOf(r_name.get(0).get(i));
                                list.add(new ScrapForm(getActivity(), pad_name, pad_code));
                                rcv.setAdapter(item);
                            }
                        } catch (Exception e) {
                            startToast("스크랩 된 제품이 없습니다.");
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

    private void startToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }
}