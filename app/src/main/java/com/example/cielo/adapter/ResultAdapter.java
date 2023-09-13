package com.example.cielo.adapter;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cielo.activity.MainActivity;
import com.example.cielo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by SM-PC on 2018-04-11.
 */

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewholder> {



    private Activity activity;
    private ArrayList<ResultForm> datalist;


    //getItemCount, onCreateViewHolder, MyViewHolder, onBindViewholder 순으로 들어오게 된다.
    // 뷰홀더에서 초기세팅해주고 바인드뷰홀더에서 셋텍스트해주는 값이 최종적으로 화면에 출력되는 값


    @Override
    public ResultAdapter.MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemsearchrecycler, parent, false);//뷰 생성(아이템 레이아웃을 기반으로)
        MyViewholder viewholder1 = new MyViewholder(view);//아이템레이아웃을 기반으로 생성된 뷰를 뷰홀더에 인자로 넣어줌


        return viewholder1;
    }

    @Override
    public void onBindViewHolder(ResultAdapter.MyViewholder holder, int position) {
        ResultForm data = datalist.get(position);//위치에 따라서 그에 맞는 데이터를 얻어오게 한다.
        holder.padName.setText(data.getPadName());//앞서 뷰홀더에 세팅해준 것을 각 위치에 맞는 것들로 보여주게 하기 위해서 세팅해준다.
        holder.padGood.setText(data.getPadGood());
        holder.padPrice.setText(data.getPadPrice());

        if(Integer.parseInt(data.getPadGood()) == 0){
            holder.heart.setImageResource(R.drawable.heart_b);
        }else{

            holder.heart.setImageResource(R.drawable.heart);
        }

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(data.getPadImg()+".jpg");
        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // Glide 이용하여 이미지뷰에 로딩
                    Glide.with(data.getCt())
                            .load(task.getResult())
                            .into(holder.padImg);
                } else {
                    // URL을 가져오지 못하면 토스트 메세지
                    Log.d("TAG", "failure");
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }
    public class MyViewholder extends RecyclerView.ViewHolder
    {
        ImageView padImg;
        TextView padGood;
        TextView padName;
        TextView padPrice;
        ImageView heart;

        public MyViewholder(View itemview){
            super(itemview);

            heart = (ImageView) itemview.findViewById(R.id.secondgoodbutton);
            padImg = (ImageView) itemview.findViewById(R.id.secondpic);//item_layout.xml에 만든걸 세팅해준다.
            padGood = (TextView) itemview.findViewById(R.id.secondgood);
            padName = (TextView) itemview.findViewById(R.id.secondname);
            padPrice = (TextView) itemview.findViewById(R.id.secondprice);
            itemview.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition(); //해당 adapter의 position get
                    if (pos!= RecyclerView.NO_POSITION){
                        ((MainActivity)v.getContext()).showSelectedItem(datalist.get(pos).getPadName(),datalist.get(pos).getPadImg(),datalist.get(pos).getIngredients(),datalist.get(pos).getLength(),false);
                    }
                }
            });

        }

    }
    public ResultAdapter(Activity activity, ArrayList<ResultForm> datalist){
        this.activity = activity;//보여지는 액티비티
        this.datalist = datalist;//내가 처리하고자 하는 아이템들의 리스트
    }
}