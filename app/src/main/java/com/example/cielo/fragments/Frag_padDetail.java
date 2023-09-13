package com.example.cielo.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cielo.R;
import com.example.cielo.activity.MainActivity;
import com.example.cielo.adapter.CommentAdapter;
import com.example.cielo.adapter.CommentForm;
import com.example.cielo.database.ChemicalList;
import com.example.cielo.database.Comment;
import com.example.cielo.database.Myscrap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Frag_padDetail extends Fragment {
    String TAG = "pad Activity) ";
    String padName, padKey, length;
    ArrayList<String> waterproof, pad, inside_pad, adhesive, absorber;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText reviewText;
    Button summary;
    ImageButton recommend, scrap;
    ArrayList<ArrayList<String>> ingredients = new ArrayList<>();
    String nick_,comment_;
    int age_, blood_;

    ArrayList<String> nicknames, comments, uids;

    ArrayList<Integer> ages, bloods;
    String review;
    Boolean exists;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.pad_detail_frag,container,false);
        
        //bundle에서 넘어온 정보 get 
        Bundle info = getArguments();
        padName = info.getString("padName");
        padKey = info.getString("padKey");
        length = info.getString("length");
        waterproof = info.getStringArrayList("waterproof");
        pad = info.getStringArrayList("pad");
        inside_pad = info.getStringArrayList("inside_pad");
        adhesive = info.getStringArrayList("adhesive");
        absorber = info.getStringArrayList("absorber");
        ingredients.add(waterproof);
        ingredients.add(pad);
        ingredients.add(inside_pad);
        ingredients.add(adhesive);
        ingredients.add(absorber);
        SwipeRefreshLayout swipeRefreshLayout= v.findViewById(R.id.swiperefreshlayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity)v.getContext()).showSelectedItem(padName,padKey,ingredients,length,true);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        v.findViewById(R.id.reviewSend).setOnClickListener(onClickListener);

        TextView padName_t = (TextView)v.findViewById(R.id.padName);
        padName_t.setText(padName);

        TextView length_t = (TextView)v.findViewById(R.id.textView7) ;
        length_t.setText(length+"cm");

        summary = (Button)v.findViewById(R.id.viewingre);
        summary.setOnClickListener(onClickListener);
        recommend = (ImageButton)v.findViewById(R.id.recommend_btn);
        recommend.setOnClickListener(onClickListener);
        scrap = (ImageButton)v.findViewById(R.id.scrap_btn);
        scrap.setOnClickListener(onClickListener);

        int grade = numOfDanger();
        summary.setText("위험등급 성분 "+grade+"개");

        ImageView padImg_i = (ImageView)v.findViewById(R.id.imageView13);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(padKey+".jpg");
        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // Glide 이용하여 이미지뷰에 로딩
                    if(getActivity()!=null){
                        Glide.with(getActivity())
                                .load(task.getResult())
                                .apply(new RequestOptions().circleCrop())
                                .into(padImg_i);
                    }


                } else {
                    // URL을 가져오지 못하면 토스트 메세지
                    Log.d("TAG", "failure");
                }
            }
        });



        reviewText = (EditText) v.findViewById(R.id.review);
        return v;

    }


    @Override
    public void onStart() {

        super.onStart();
        RecyclerView rcv;
        LinearLayoutManager llm;
        final CommentAdapter[] item = new CommentAdapter[1];

        rcv = getView().findViewById(R.id.commentRecycler);
        llm = new LinearLayoutManager(getActivity());//종류는 총 3가지, ListView를 사용하기 위한 사용
        rcv.setHasFixedSize(true);//각 아이템이 보여지는 것을 일정하게
        rcv.setLayoutManager(llm);//앞서 선언한 리싸이클러뷰를 레이아웃메니저에 붙힌다
        ArrayList<CommentForm> list = new ArrayList<>();//ResultForm을 통해 받게되는 데이터를 어레이 리스트화 시킨다.
        //제품에대한 comment정보 가져오기

        exists= false;
        DocumentReference docRef = db.collection("Review").document(padKey);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        exists = true;
                        nicknames = (ArrayList<String>) document.get("nickname");
                        ages = (ArrayList<Integer>) document.get("age");
                        bloods = (ArrayList<Integer>) document.get("blood");
                        comments = (ArrayList<String>) document.get("comment");
                        uids = (ArrayList<String>) document.get("uid");
                        assert((nicknames.size()==ages.size()) && (bloods.size()==comments.size())); //각 데이터가 빠짐없이 있는지 확인
                        for (int i = 0 ; i < nicknames.size(); i++){
                            list.add(new CommentForm(padKey, nicknames.get(i), uids.get(i), Integer.parseInt(String.valueOf(ages.get(i))) , Integer.parseInt(String.valueOf(bloods.get(i))), comments.get(i), getActivity()));
                            item[0] = new CommentAdapter(getActivity(), list);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.
                            rcv.setAdapter(item[0]);// 그리고 만든 겍체를 리싸이클러뷰에 적용시킨다.
                        }
                    } else { //리뷰가 없을때
                        startToast("아직 작성된 리뷰가 없습니다.");
                        Log.d("getting comment", "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    } //end onStart

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.reviewSend:
                    review = reviewText.getText().toString();

                    DocumentReference docRef = db.collection("userInfo").document(user.getUid()); //declare document
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) { //user정보를 가져왔으면 db에접근
                                    nick_ = document.getData().get("nickname").toString();
                                    age_ = Integer.parseInt(document.getData().get("age").toString());
                                    blood_ = Integer.parseInt(document.getData().get("blood").toString());
                                    DocumentReference docRef = db.collection("Review").document(padKey);
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) { //db에 comment가 있으면 (첫 comment가 아니라면) 기존 comment 배열에 추가
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    ArrayList<String> nickname = new ArrayList<>();
                                                    ArrayList<String> comment= new ArrayList<>();
                                                    ArrayList<Integer> age= new ArrayList<>();
                                                    ArrayList<Integer> blood= new ArrayList<>();
                                                    ArrayList<String> uid= new ArrayList<>();
                                                    if(document.getData().get("nickname") != null){
                                                        nickname = (ArrayList<String>) document.getData().get("nickname");
                                                        comment = (ArrayList<String>) document.getData().get("comment");
                                                        age = (ArrayList<Integer>) document.getData().get("age");
                                                        blood = (ArrayList<Integer>) document.getData().get("blood");
                                                        uid = (ArrayList<String>) document.getData().get("uid");
                                                    }

                                                    nickname.add(nick_);
                                                    comment.add(review);
                                                    age.add(age_);
                                                    blood.add(blood_);
                                                    uid.add(user.getUid());
                                                    Comment cmt = new Comment();
                                                    cmt.setAge(age);
                                                    cmt.setBlood(blood);
                                                    cmt.setComment(comment);
                                                    cmt.setKey(padKey);
                                                    cmt.setNickname(nickname);
                                                    cmt.setUid(uid);

                                                    db.collection("Review").document(padKey).set(cmt).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            reviewText.setText("");
                                                            startToast("리뷰가 등록되었습니다.");

                                                            ((MainActivity)v.getContext()).showSelectedItem(padName,padKey,ingredients,length,true);
                                                        }
                                                    })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    startToast("리뷰가 등록되지 않았습니다.");
                                                                }
                                                            });


                                                } else { //첫 comment라면 배열을 만듦
                                                    ArrayList<String> nickname = new ArrayList<>();
                                                    ArrayList<String> comment= new ArrayList<>();
                                                    ArrayList<Integer> age= new ArrayList<>();
                                                    ArrayList<Integer> blood= new ArrayList<>();
                                                    ArrayList<String> uid= new ArrayList<>();

                                                    nickname.add(nick_);
                                                    comment.add(review);
                                                    age.add(age_);
                                                    blood.add(blood_);
                                                    uid.add(user.getUid());


                                                    Comment cmt = new Comment();
                                                    cmt.setAge(age);
                                                    cmt.setBlood(blood);
                                                    cmt.setComment(comment);
                                                    cmt.setKey(padKey);
                                                    cmt.setNickname(nickname);
                                                    cmt.setUid(uid);

                                                    db.collection("Review").document(padKey).set(cmt).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            reviewText.setText("");
                                                            startToast("리뷰가 등록되었습니다.");

                                                            ((MainActivity)v.getContext()).showSelectedItem(padName,padKey,ingredients,length,true); //here
                                                        }
                                                    })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    startToast("리뷰가 등록되지 않았습니다.");
                                                                }
                                                            });
                                                }
                                            } else {
                                                Log.d(TAG, "get failed with ", task.getException());
                                            }
                                        }
                                    });
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                } else {
                                    Log.d(TAG, "No such document2");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                    break;
                case R.id.viewingre:
                    ((MainActivity)v.getContext()).showIngredient(padName,padKey,ingredients,length);
                    break;
                case R.id.recommend_btn:
                    DocumentReference doc = db.collection("PAD_ingredient").document(padKey); //declare document
                    doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    doc.update("recommend", FieldValue.increment(1));
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }

                        }
                    });
                    startToast("추천되었습니다.");
                    break;
                case R.id.scrap_btn:
                    DocumentReference docref = db.collection("Myscrap").document(user.getUid());
                    docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<String> padkeys = new ArrayList<>();
                                ArrayList<String> padnames = new ArrayList<>();
                                DocumentSnapshot document = task.getResult();
                                if (document.getData().get("pad_code")!=null) { //user정보를 가져왔으면 db에접근
                                    padkeys = (ArrayList<String>) document.getData().get("pad_code");
                                    padnames =(ArrayList<String>) document.getData().get("pad_name");
                                }
                                try{
                                    assert !padkeys.contains(padKey);

                                    padkeys.add(padKey);
                                    padnames.add(padName);

                                    Myscrap obj = new Myscrap();
                                    obj.setPad_code(padkeys);
                                    obj.setPad_name(padnames);

                                    db.collection("Myscrap").document(user.getUid()).set(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            reviewText.setText("");
                                            startToast("스크랩이 완료되었습니다.");

                                            ((MainActivity)v.getContext()).showSelectedItem(padName,padKey,ingredients,length,true);
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                } catch (AssertionError e) {
                                    startToast("이미 스크랩한 제품입니다.");
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
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private int numOfDanger(){
        int dangerCount = 0;
        ChemicalList chem = new ChemicalList();
        for(int i=0; i< waterproof.size(); i++){
            int grade = chem.returnGrade(waterproof.get(i));
            dangerCount = (grade >= 4)? dangerCount+1 : dangerCount;
        }
        for(int i=0; i< pad.size(); i++){
            int grade = chem.returnGrade(pad.get(i));
            dangerCount = (grade >= 4)? dangerCount+1 : dangerCount;
        }
        for(int i=0; i< inside_pad.size(); i++){
            int grade = chem.returnGrade(inside_pad.get(i));
            dangerCount = (grade >= 4)? dangerCount+1 : dangerCount;
        }
        for(int i=0; i< adhesive.size(); i++){
            int grade = chem.returnGrade(adhesive.get(i));
            dangerCount = (grade >= 4)? dangerCount+1 : dangerCount;
        }
        for(int i=0; i< absorber.size(); i++){
            int grade = chem.returnGrade(absorber.get(i));
            dangerCount = (grade >= 4)? dangerCount+1 : dangerCount;
        }
        return dangerCount;
    }
}