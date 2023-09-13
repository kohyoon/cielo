package com.example.cielo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cielo.R;
import com.example.cielo.database.ChemicalList;

import java.util.ArrayList;


public class PadAdapter extends RecyclerView.Adapter<PadAdapter.Viewholder> {
    private Activity activity;
    ArrayList<IngreForm> datalist;
    ChemicalList chem = new ChemicalList();
    String TAG = "pad Activity) ";


    @Override
    public PadAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingrecycler, parent, false);//뷰 생성(아이템 레이아웃을 기반으로)
        Viewholder viewholder1 = new Viewholder(view);//아이템레이아웃을 기반으로 생성된 뷰를 뷰홀더에 인자로 넣어줌


        return viewholder1;
    }
    public void onBindViewHolder(PadAdapter.Viewholder holder, int position) {
        IngreForm data = datalist.get(position);//위치에 따라서 그에 맞는 데이터를 얻어오게 한다.
        holder.ingreName.setText(data.get_ingre());//앞서 뷰홀더에 세팅해준 것을 각 위치에 맞는 것들로 보여주게 하기 위해서 세팅해준다.
        holder.gradeImage.setImageResource(data.getImg());

    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }


    public class Viewholder extends RecyclerView.ViewHolder
    {
        ImageView gradeImage;
        TextView ingreName;


        public Viewholder(View itemview){
            super(itemview);

            gradeImage = (ImageView) itemview.findViewById(R.id.ingre_grade);//item_layout.xml에 만든걸 세팅해준다.
            ingreName = (TextView) itemview.findViewById(R.id.ingre_name);


        }

    }

    public PadAdapter(Activity activity, ArrayList<IngreForm> datalist){
        this.activity = activity;//보여지는 액티비티
        this.datalist  = datalist;//내가 처리하고자 하는 아이템들의 리스트
    }

}