package com.example.cielo.adapter;
import android.content.Context;

public class IngreForm {

    String pad_ingre;
    int img;
    Context ct;

    public IngreForm(Context ct, String pad_ingre, int img){
        this.ct = ct;
        this.pad_ingre = pad_ingre;
        this.img = img;
    }

    public int getImg() {
        return img;
    }

    public String get_ingre() {
        return pad_ingre;
    }

}