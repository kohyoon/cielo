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
import com.bumptech.glide.request.RequestOptions;
import com.example.cielo.R;
import com.example.cielo.database.ChemicalList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ScrapAdapter extends RecyclerView.Adapter<ScrapAdapter.Viewholder> {

    private Activity activity;
    ArrayList<ScrapForm> datalist;
    ChemicalList chem = new ChemicalList();
    String TAG = "pad Activity) ";


    @Override
    public ScrapAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scrapcycler, parent, false);//뷰 생성(아이템 레이아웃을 기반으로)
        ScrapAdapter.Viewholder viewholder1 = new Viewholder(view);//아이템레이아웃을 기반으로 생성된 뷰를 뷰홀더에 인자로 넣어줌


        return viewholder1;
    }
    public void onBindViewHolder(ScrapAdapter.Viewholder holder, int position) {
        ScrapForm data = datalist.get(position);//위치에 따라서 그에 맞는 데이터를 얻어오게 한다.
        holder.padName.setText(data.get_name());//앞서 뷰홀더에 세팅해준 것을 각 위치에 맞는 것들로 보여주게 하기 위해서 세팅해준다.
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(data.getImg()+".jpg");
        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // Glide 이용하여 이미지뷰에 로딩
                    Glide.with(data.getCt())
                            .load(task.getResult())
                            .apply(new RequestOptions().circleCrop())
                            .into(holder.padImage);
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


    public class Viewholder extends RecyclerView.ViewHolder
    {
        ImageView padImage;
        TextView padName;


        public Viewholder(View itemview){
            super(itemview);

            padImage = (ImageView) itemview.findViewById(R.id.scrappadimg);//item_layout.xml에 만든걸 세팅해준다.
            padName = (TextView) itemview.findViewById(R.id.scrappadname);


        }

    }

    public ScrapAdapter(Activity activity, ArrayList<ScrapForm> datalist){
        this.activity = activity;//보여지는 액티비티
        this.datalist  = datalist;//내가 처리하고자 하는 아이템들의 리스트
    }
}
