package com.example.cielo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cielo.R;
import com.example.cielo.activity.MainActivity;

import java.util.ArrayList;

public class bscrapAdapter extends RecyclerView.Adapter<bscrapAdapter.MyViewholder> {
    String TAG = "bscrapAdapter ) ";
    private Context context;
    private ArrayList<BoardForm> datalist;



    @Override
    public bscrapAdapter.MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hotbordrecycler, parent, false);//뷰 생성(아이템 레이아웃을 기반으로)
        MyViewholder viewholder = new MyViewholder(view);//아이템레이아웃을 기반으로 생성된 뷰를 뷰홀더에 인자로 넣어줌


        return viewholder;
    }

    @Override
    public void onBindViewHolder(bscrapAdapter.MyViewholder holder, int position) {
        BoardForm data = datalist.get(position);//위치에 따라서 그에 맞는 데이터를 얻어오게 한다.
        holder.hbtitle.setText(data.getBoardtitle());//앞서 뷰홀더에 세팅해준 것을 각 위치에 맞는 것들로 보여주게 하기 위해서 세팅해준다.
        holder.hbtext.setText(data.getBoardtext());
        holder.hbcommentnum.setText(data.getBoardcommentNum());
        holder.hbtheme.setText(data.getboardtheme());





    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }
    public class MyViewholder extends RecyclerView.ViewHolder
    {

        TextView hbtitle;
        TextView hbtext;
        TextView hbcommentnum;
        TextView hbtheme;

        public MyViewholder(View itemview){
            super(itemview);
            this.hbtitle =itemview.findViewById(R.id.hbtitle);
            this.hbtext =itemview.findViewById(R.id.hbtext);
            this.hbcommentnum = itemview.findViewById(R.id.hbcomment);
            this.hbtheme=itemview.findViewById(R.id.hottheme);
            Log.d(TAG, "Success on my view hlder");
            itemview.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos!= RecyclerView.NO_POSITION){
                        ((MainActivity)v.getContext()).showSelectedBoard(datalist.get(pos).getBoardtitle(),datalist.get(pos).getBoardtext(),datalist.get(pos).getBoardtime(),datalist.get(pos).getBoardid());
                    }
                }
            });

        }

    }
    public bscrapAdapter(Context context, ArrayList<BoardForm> datalist){
        this.context = context;//보여지는 액티비티
        this.datalist = datalist;//내가 처리하고자 하는 아이템들의 리스트
    }
}