package com.example.cielo.adapter;

import android.content.Context;

public class ScrapForm {

    String pad_name;
    String img;
    Context ct;

    public ScrapForm(Context ct, String pad_name, String img){
        this.ct = ct;
        this.pad_name = pad_name;
        this.img = img;
    }

    public String getImg() {
        return img;
    }


    public String get_name() {
        return pad_name;
    }

    public Context getCt()  {return ct;}
}
