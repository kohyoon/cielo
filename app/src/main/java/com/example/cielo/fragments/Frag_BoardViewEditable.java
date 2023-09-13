package com.example.cielo.fragments;
//view에서 이전화면은 게시글 목록화면임


import android.content.Context;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cielo.R;
import com.example.cielo.activity.MainActivity;
import com.example.cielo.adapter.BoardcomAdapter;
import com.example.cielo.adapter.bcommentForm;
import com.example.cielo.database.Board;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Timestamp;
import java.util.ArrayList;


public class Frag_BoardViewEditable extends Fragment {
    private static final String TAG = "BoardView";
    private Context context;
    TextView v_title;
    TextView v_text;
    TextView nickname;
    TextView date;

    EditText addcomment;
    String ttime;
    String tmpname;
    ImageView profile;
    boolean ano;
    int i;
    String time;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String title, text,id;
    Boolean exists;
    ArrayList<String> comment, commentID, commenttime;

    public static Frag_BoardViewEditable newInstance() {
        return new Frag_BoardViewEditable();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.boardvieweditable, container, false);
        Bundle fromitem = getArguments();
        title = fromitem.getString("title");
        text = fromitem.getString("text");
        time = fromitem.getString("time");
        id=fromitem.getString("id");
        context=container.getContext();
        tmpname = user.getUid().toString();
        profile=v.findViewById(R.id.anopic);
        v_title = v.findViewById(R.id.view_title);
        v_text = v.findViewById(R.id.view_text);
        nickname= v.findViewById(R.id.bvnickname);
        date= v.findViewById(R.id.bvdate);
        v.findViewById(R.id.deleteable).setOnClickListener(onClickListener);
        v.findViewById(R.id.editable).setOnClickListener(onClickListener);
        addcomment = (EditText) v.findViewById(R.id.comment_viewbox);
        v.findViewById(R.id.comment_view_submit).setOnClickListener(onClickListener);
        SwipeRefreshLayout swipeRefreshLayout= v.findViewById(R.id.swiperefreshlayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity)v.getContext()).showBoardedit(title,text,time);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return v;

    }

    public void onStart() {
        super.onStart();
        v_title.setText(title);
        v_text.setText(text);
        ttime= new Timestamp(System.currentTimeMillis()).toString();
        String texttime=settimetext(time);

        date.setText(texttime);

        RecyclerView rcv;
        LinearLayoutManager llm;
        final BoardcomAdapter[] item = new BoardcomAdapter[1];

        rcv = getView().findViewById(R.id.comment_view);
        llm = new LinearLayoutManager(getActivity());//종류는 총 3가지, ListView를 사용하기 위한 사용
        rcv.setHasFixedSize(true);//각 아이템이 보여지는 것을 일정하게
        rcv.setLayoutManager(llm);//앞서 선언한 리싸이클러뷰를 레이아웃메니저에 붙힌다
        ArrayList<bcommentForm> list = new ArrayList<>();

        exists = false;
        DocumentReference docRef = db.collection("Board").document(time);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        exists = true;
                        comment = (ArrayList<String>) document.getData().get("comment");
                        commentID = (ArrayList<String>) document.getData().get("commentID");
                        commenttime = (ArrayList<String>) document.getData().get("commenttime");

                        ano=(Boolean) document.getData().get("anonymous");
                        String uid = (String) document.get("id");


                        if(ano==false){
                            anomy(uid);
                        }

                        else {
                            profile.setImageResource(R.drawable.nonameperson);
                        }

                        for (int i = 0; i < comment.size(); i++) {
                            if (uid.equals(commentID.get(i))) {
                                String name = "글쓴이";
                                String tme=settimetext(commenttime.get(i));
                                list.add(new bcommentForm(comment.get(i), name, tme));
                            } else {
                                String name = "익명";
                                String tme=settimetext(commenttime.get(i));
                                list.add(new bcommentForm(comment.get(i), name, tme));
                            }

                            item[0] = new BoardcomAdapter(getActivity(), list);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.
                            rcv.setAdapter(item[0]);// 그리고 만든 겍체를 리싸이클러뷰에 적용시킨다.}
                        }
                    } else { //리뷰가 없을때
                        startToast("아직 작성된 댓글이 없습니다.");
                        Log.d("getting comment", "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }

            private void startToast(String msg) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });


    } //end onStart


    View.OnClickListener onClickListener = new View.OnClickListener() {
        private static final String TAG = "Comment Upload) ";
        ;

        @Override
        public void onClick(View v) {
            DocumentReference ref = db.collection("Board").document(time);
            switch (v.getId()) {
                case R.id.comment_view_submit:
                    String ment = addcomment.getText().toString();
                    if (ment.getBytes().length <= 0) {
                        Log.d(TAG, "댓글을 입력하세요");
                        startToast("댓글을 입력하세요");

                        break;
                    }
                    Timestamp ctime = new Timestamp(System.currentTimeMillis());

                    ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {

                                    ArrayList<String> comment = new ArrayList<>();
                                    ArrayList<String> commentID = new ArrayList<>();
                                    ArrayList<String> commenttime = new ArrayList<>();
                                    int num;
                                    if (document.getData().get("comment") != null) {
                                        comment = (ArrayList<String>) document.getData().get("comment");
                                        commentID = (ArrayList<String>) document.getData().get("commentID");
                                        commenttime = (ArrayList<String>) document.getData().get("commenttime");
                                        i = comment.size();
                                    }

                                    comment.add(ment);
                                    commentID.add(tmpname);
                                    commenttime.add(ctime.toString());

                                    db.collection("Board").document(time).update("comment", comment);
                                    db.collection("Board").document(time).update("commentID", commentID);
                                    db.collection("Board").document(time).update("commenttime", commenttime).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Success on board registration");
                                            db.collection("Board").document(time)
                                                    .update("commentNum", i + 1);
                                            updatemanner(tmpname);
                                            ((MainActivity) v.getContext()).showBoardedit(title, text, time);

                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Failed to register board", e);
                                                }
                                            });

                                }
                            }
                        }

                    });
                    break;
                case R.id.editable:
                    ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String theme = (String) document.getData().get("theme");
                                    Boolean anon= (Boolean)document.getData().get("anonymous");

                                    ((MainActivity) v.getContext()).showedit(title, text, time, theme,anon);
                                }
                            }
                        }
                    });
                    break;
                case R.id.deleteable:
                    ((MainActivity) v.getContext()).showAlert(new Frag_alert(),"alert",time);
            }
        }
    };

    private void startToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    private void updatemanner(String tmpname) {
        final float[] number = new float[1];
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("userInfo").document(tmpname);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String number1 = (String) document.getData().get("manner").toString();
                        String name = (String) document.getData().get("name");

                        number[0] = Float.valueOf(number1); //String -> Float
                        number[0] += 0.01f;
                        db.collection("userInfo").document(tmpname)
                                .update("manner", number[0]);
                        Log.d("String value is this", String.valueOf(number[0]));


                    }
                }
            }
        });
    }
    private void anomy(String uid) {

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(uid);
        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                // Glide 이용하여 이미지뷰에 로딩
                profile.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(getActivity())
                        .load(task.getResult())
                        .apply(new RequestOptions().circleCrop())
                        .into(profile);}});

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference uref = db.collection("userInfo").document(uid);
        uref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String nickname1 = (String) document.getData().get("nickname");
                        Log.d("Name is this", nickname1);
                        nickname.setText(nickname1);


                    }
                }
            }
        });
    }
    public String getDateformat(String time){
        String dfDate=time.substring(0,10);
        return dfDate;
    }
    public String getTimeformat(String time){
        String dfTime= time.substring(11,16);
        return dfTime;
    }
    public String settimetext(String time){
        String starttime=getDateformat(ttime);
        String endtime=getDateformat(time);
        if(!starttime.equals(endtime)){
            return endtime;
        }
        else{
            return getTimeformat(time);
        }


    }

}




