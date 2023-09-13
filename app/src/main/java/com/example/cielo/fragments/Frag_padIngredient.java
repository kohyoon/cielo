package com.example.cielo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cielo.R;
import com.example.cielo.adapter.IngreForm;
import com.example.cielo.adapter.PadAdapter;
import com.example.cielo.database.Chemical;
import com.example.cielo.database.ChemicalList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Frag_padIngredient extends Fragment {
    public static Frag_padIngredient newInstance() {
        return new Frag_padIngredient();
    }

    String TAG = "pad Activity) ";
    ChemicalList chem = new ChemicalList();

    TextView pad_name;
    TextView ingre_num;
    RadioGroup select_grade;
    RadioButton water_btn, cover_btn, adhesive_btn, absorber_btn, inside_btn, all_btn;
    RecyclerView rcv;


    String padName, padKey, length, grade;
    ArrayList<String> waterproof, pad, inside_pad, adhesive, absorber;
    ArrayList<Chemical> chemical_info = chem.getChem();
    List<String> total_chem = new ArrayList<String>();
    int chem_index;

    HashSet<String> dup_check = new HashSet<String>(); //중복체크집합



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pad_ingredient_frag, container, false);
        pad_name = (TextView) v.findViewById(R.id.p_pad_name);
        ingre_num = (TextView) v.findViewById(R.id.ingre_num);


        Bundle info = getArguments(); //bundle
        padName = info.getString("padName");
        padKey = info.getString("padKey");

        length = info.getString("length");
        waterproof = info.getStringArrayList("waterproof");
        pad = info.getStringArrayList("pad");
        inside_pad = info.getStringArrayList("inside_pad");
        adhesive = info.getStringArrayList("adhesive");
        absorber = info.getStringArrayList("absorber");
        grade = info.getString("grade");
        water_btn = (RadioButton) v.findViewById(R.id.water_btn);
        cover_btn = (RadioButton) v.findViewById(R.id.cover_btn);
        inside_btn = (RadioButton) v.findViewById(R.id.side_btn);
        absorber_btn = (RadioButton) v.findViewById(R.id.absorber_btn);
        adhesive_btn = (RadioButton) v.findViewById(R.id.adhesive_btn);
        all_btn = (RadioButton) v.findViewById(R.id.all);

        v.findViewById(R.id.exitButton2).setOnClickListener(onClickListener);

        select_grade = (RadioGroup) v.findViewById(R.id.gradegroup);


        return v;


    }


    @Override
    public void onStart() {


        super.onStart();

        LinearLayoutManager llm;
        final ArrayList<IngreForm> list = new ArrayList<>(); // 넣을데이터
        final PadAdapter item = new PadAdapter(getActivity(), list);




        //txt.setText(result);전성분표시
        pad_name.setText(padName.toString());
        ingre_num.setText(Integer.toString(absorber.size() + inside_pad.size() + pad.size() + waterproof.size() + adhesive.size()));

        rcv = getView().findViewById(R.id.ingrecycler1);
        llm = new LinearLayoutManager(getActivity());
        rcv.setLayoutManager(llm);

        total_chem.addAll(pad);
        total_chem.addAll(inside_pad);
        total_chem.addAll(adhesive);
        total_chem.addAll(waterproof);
        total_chem.addAll(absorber);


        dup_check.addAll(total_chem); //집합전체중복체크
        Iterator pos = dup_check.iterator();

        while(pos.hasNext()) {
            String pos_name = (pos.next()).toString();
            chem_index = Integer.parseInt(pos_name.substring(2)) ;// get chemical index
            list.add(get_ingre(chem_index));

        } //list add
        rcv.setAdapter(item);// 그리고 만든 겍체를 리싸이클러뷰에 적용시킨다.





        select_grade.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup select_grade, @IdRes int i) {
                list.clear();
                Iterator pos = dup_check.iterator();
                if(i == R.id.all){
                    while(pos.hasNext()) {
                        String pos_name = (pos.next()).toString();
                        chem_index = Integer.parseInt(pos_name.substring(2)) ;// get chemical index
                        list.add(get_ingre(chem_index));

                    } //list add
                    PadAdapter item = new PadAdapter(getActivity(), list);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.
                    rcv.setAdapter(item);// 그리고 만든 겍체를 리싸이클러뷰에 적용시킨다.

                }
                if (i == R.id.cover_btn) {
                    for (int j = 0; j < pad.size(); j++) {
                        chem_index = Integer.parseInt(pad.get(j).substring(2)) ;// get chemical index
                        list.add(get_ingre(chem_index));

                    } //list add
                    PadAdapter item = new PadAdapter(getActivity(), list);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.
                    rcv.setAdapter(item);// 그리고 만든 겍체를 리싸이클러뷰에 적용시킨다.
                } else if (i == R.id.side_btn) {
                    for (int j = 0; j < inside_pad.size(); j++) {
                        chem_index = Integer.parseInt(inside_pad.get(j).substring(2)) ;// get chemical index
                        list.add(get_ingre(chem_index));

                    } //list add
                    PadAdapter item = new PadAdapter(getActivity(), list);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.
                    rcv.setAdapter(item);// 그리고 만든 겍체를 리싸이클러뷰에 적용시킨다.
                } else if (i == R.id.absorber_btn) {
                    for (int j = 0; j < absorber.size(); j++) {
                        chem_index = Integer.parseInt(absorber.get(j).substring(2)) ;// get chemical index
                        list.add(get_ingre(chem_index));

                    } //list add
                    PadAdapter item = new PadAdapter(getActivity(), list);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.
                    rcv.setAdapter(item);// 그리고 만든 겍체를 리싸이클러뷰에 적용시킨다.
                } else if (i == R.id.adhesive_btn) {
                    for (int j = 0; j < adhesive.size(); j++) {
                        chem_index = Integer.parseInt(adhesive.get(j).substring(2)) ;// get chemical index
                        list.add(get_ingre(chem_index));
                    } //list add
                    PadAdapter item = new PadAdapter(getActivity(), list);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.
                    rcv.setAdapter(item);// 그리고 만든 겍체를 리싸이클러뷰에 적용시킨다.
                } else if (i == R.id.water_btn) {
                    for (int j = 0; j < waterproof.size(); j++) {
                        chem_index = Integer.parseInt(waterproof.get(j).substring(2)) ;// get chemical index
                        list.add(get_ingre(chem_index));

                    } //list add
                    PadAdapter item = new PadAdapter(getActivity(), list);//앞서 만든 리스트를 어뎁터에 적용시켜 객체를 만든다.
                    rcv.setAdapter(item);// 그리고 만든 겍체를 리싸이클러뷰에 적용시킨다.
                }
            }


        });


    }




    public IngreForm get_ingre (int chem_index) {

        int img_id1 = getContext().getResources().getIdentifier("grade1", "drawable", getContext().getPackageName());//grade img id
        int img_id2 = getContext().getResources().getIdentifier("grade2", "drawable", getContext().getPackageName());//
        int img_id3 = getContext().getResources().getIdentifier("grade3", "drawable", getContext().getPackageName());//
        int img_id4 = getContext().getResources().getIdentifier("grade4", "drawable", getContext().getPackageName());//
        int img_id5 = getContext().getResources().getIdentifier("grade5", "drawable", getContext().getPackageName());

        List<IngreForm> list = new ArrayList<IngreForm>();


        if (chemical_info.get(chem_index).getGrade() == 1) { //select icon
            list.add(new IngreForm(getActivity(), chemical_info.get(chem_index).getName(), img_id1));

        }
        if (chemical_info.get(chem_index).getGrade() == 2) {
            list.add(new IngreForm(getActivity(), chemical_info.get(chem_index).getName(), img_id2));
        }
        if (chemical_info.get(chem_index).getGrade() == 3) {
            list.add(new IngreForm(getActivity(), chemical_info.get(chem_index).getName(), img_id3));
        }
        if (chemical_info.get(chem_index).getGrade() == 4) {
            list.add(new IngreForm(getActivity(), chemical_info.get(chem_index).getName(), img_id4));
        }
        if (chemical_info.get(chem_index).getGrade() == 5) {
            list.add(new IngreForm(getActivity(), chemical_info.get(chem_index).getName(), img_id5));
        }


        return list.get(0);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.exitButton2: //엑스버튼누르면 창닫기
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().remove(Frag_padIngredient.this).commit();
                    fragmentManager.popBackStack();
                    break;


            }
        }
    };


}