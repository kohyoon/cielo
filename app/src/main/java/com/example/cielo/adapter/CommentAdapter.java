package com.example.cielo.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cielo.R;
import com.example.cielo.database.Comment;
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

import java.util.ArrayList;

/**
 * Created by SM-PC on 2018-04-11.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewholder> {


    ArrayList<String> nickname, comment, uid;
    ArrayList<Integer> age, blood;
    private Activity activity;
    private ArrayList<CommentForm> datalist;

    AlertDialog.Builder dlg;

    //getItemCount, onCreateViewHolder, MyViewHolder, onBindViewholder 순으로 들어오게 된다.
    // 뷰홀더에서 초기세팅해주고 바인드뷰홀더에서 셋텍스트해주는 값이 최종적으로 화면에 출력되는 값

    @Override
    public CommentAdapter.MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentrecycler, parent, false);//뷰 생성(아이템 레이아웃을 기반으로)
        CommentAdapter.MyViewholder viewholder1 = new CommentAdapter.MyViewholder(view);//아이템레이아웃을 기반으로 생성된 뷰를 뷰홀더에 인자로 넣어줌


        return viewholder1;
    }

    @Override
    public void onBindViewHolder(CommentAdapter.MyViewholder holder, int position) {
        dlg = new AlertDialog.Builder(datalist.get(0).getCt());
        CommentForm data = datalist.get(position);//위치에 따라서 그에 맞는 데이터를 얻어오게 한다.

        holder.nickname.setText(data.getNickname()); //nickname setting

        int roughAge = (data.getAge()/10)*10; //age setting


        holder.age.setText(String.valueOf(roughAge)); //age setting


        switch(data.getBlood()){ //blood setting
            case 1:
                holder.blood.setText("생리양 적음");
                break;
            case 2:
                holder.blood.setText("생리양 보통");
                break;
            case 3:
                holder.blood.setText("생리양 많음");
                break;
        }

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(data.getUid());
        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // Glide 이용하여 이미지뷰에 로딩
                    holder.profile.setScaleType(ImageView.ScaleType.FIT_XY);
                    Glide.with(data.getCt())
                            .load(task.getResult())
                            .apply(new RequestOptions().circleCrop())
                            .into(holder.profile);

                } else {
                    holder.profile.setMaxWidth(100);
                    holder.profile.setImageResource(R.drawable.profiledefault);
                }
            }
        });


        holder.comment.setText(data.getComment());

    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }
    public class MyViewholder extends RecyclerView.ViewHolder
    {
        ImageView profile;
        TextView nickname;
        TextView age;
        TextView blood;
        TextView comment;

        public MyViewholder(View itemview){
            super(itemview);


            profile = (ImageView) itemview.findViewById(R.id.profile_image);//item_layout.xml에 만든걸 세팅해준다.
            nickname = (TextView) itemview.findViewById(R.id.info_nickname);
            age = (TextView) itemview.findViewById(R.id.info_roughAge);
            blood = (TextView) itemview.findViewById(R.id.info_blood);
            comment=  (TextView) itemview.findViewById(R.id.comment);
            itemview.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition(); //해당 adapter의 position get
                     dlg.setMessage("리뷰를 삭제하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("네",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            deleteComment(pos, v, datalist.get(pos).getUid());
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
            });

        }

    }

    public void deleteComment(int pos, View v, String temp){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Review").document(datalist.get(pos).getKey());
        final String[] uid_ = {temp};
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) { //comment 자료를 찾았으면

                        uid = (ArrayList<String>) document.get("uid");
                        nickname = (ArrayList<String>) document.get("nickname");
                        age = (ArrayList<Integer>) document.get("age");
                        blood = (ArrayList<Integer>) document.get("blood");
                        comment = (ArrayList<String>) document.get("comment");


                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        DocumentReference docRef = db.collection("userInfo").document(user.getUid());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        String userKey = document.getData().get("key").toString();

                                        if(userKey.equals(uid_[0])){
                                            nickname.remove(pos);
                                            comment.remove(pos);
                                            age.remove(pos);
                                            blood.remove(pos);
                                            uid.remove(pos);

                                            Comment cmt = new Comment();
                                            cmt.setKey(datalist.get(pos).getKey());
                                            cmt.setNickname(nickname);
                                            cmt.setComment(comment);
                                            cmt.setBlood(blood);
                                            cmt.setAge(age);
                                            cmt.setUid(uid);

                                            db.collection("Review").document(datalist.get(pos).getKey()).set(cmt).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    startToast("리뷰가 삭제되었습니다.");

                                                }
                                            })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            startToast("리뷰가 삭제되지 않았습니다.");
                                                        }
                                                    });
                                        }else{
                                            startToast("본인의 리뷰만 삭제할 수 있습니다.");
                                        }
                                    } else {
                                        Log.d("getting user info", "document doesn't exist");
                                    }
                                } else {
                                    Log.d("getting user info", "get failed with ", task.getException());
                                }
                            }
                        }); //end getting user



                        Log.d("Deleting Comment", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("Deleting Comment", "No such document");
                    }
                } else {
                    Log.d("Deleting Comment", "get failed with ", task.getException());
                }
            }
        });




    }
    private void startToast(String msg) {
        Toast.makeText(datalist.get(0).getCt(), msg, Toast.LENGTH_SHORT).show();
    }
    public CommentAdapter(Activity activity, ArrayList<CommentForm> datalist){
        this.activity = activity;//보여지는 액티비티
        this.datalist = datalist;//내가 처리하고자 하는 아이템들의 리스트
    }
}