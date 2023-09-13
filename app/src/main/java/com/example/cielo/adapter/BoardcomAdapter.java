package com.example.cielo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cielo.R;

import java.util.ArrayList;


public class BoardcomAdapter extends RecyclerView.Adapter<BoardcomAdapter.MyViewholder> {

    private Context context;
    private ArrayList<bcommentForm> datalist;


    @Override
    public MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bcommentrecycler, parent, false);//뷰 생성(아이템 레이아웃을 기반으로)
        MyViewholder viewholder = new MyViewholder(view);//아이템레이아웃을 기반으로 생성된 뷰를 뷰홀더에 인자로 넣어줌


        return viewholder;
    }

    @Override
    public void onBindViewHolder(MyViewholder holder, int position) {
        bcommentForm data = datalist.get(position);//위치에 따라서 그에 맞는 데이터를 얻어오게 한다.
        holder.cment.setText(data.getment());//앞서 뷰홀더에 세팅해준 것을 각 위치에 맞는 것들로 보여주게 하기 위해서 세팅해준다.
        holder.cID.setText(data.getcID());
        holder.ctime.setText(data.getCtime());





    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }
    public class MyViewholder extends RecyclerView.ViewHolder
    {

        TextView cment;
        TextView cID;
        TextView ctime;

        public MyViewholder(View itemview){
            super(itemview);
            this.cment =itemview.findViewById(R.id.com_description);
            this.cID =itemview.findViewById(R.id.com_writer);
            this.ctime = itemview.findViewById(R.id.com_date);


            }

        }


    public BoardcomAdapter(Context context, ArrayList<bcommentForm> datalist){
        this.context = context;//보여지는 액티비티
        this.datalist = datalist;//내가 처리하고자 하는 아이템들의 리스트
    }
}