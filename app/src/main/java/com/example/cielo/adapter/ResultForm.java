package com.example.cielo.adapter;

import android.content.Context;

import java.util.ArrayList;

public class ResultForm {

    String padName;
    String padImg;
    ArrayList<ArrayList<String>> ingredients;
    String padGood;
    String padPrice;
    Context ct;
    String length;


    public ResultForm(Context ct, String padName, String padImg, String padGood, String padPrice, ArrayList<ArrayList<String>> ingredients, String length){//new생성자를 통해서 생성자가 만들어진다.
        this.ct = ct;
        this.padName = padName;
        this.padImg = padImg;
        this.padGood = padGood;
        this.padPrice = padPrice;
        this.ingredients = ingredients;
        this.length = length;

    }
    public String getPadPrice() {
        return padPrice;
    }

    public ArrayList<ArrayList<String>> getIngredients() {
        return ingredients;
    }

    public String getLength() {
        return length;
    }

    public Context getCt() {
        return ct;
    }


    public String getPadName() {
        return padName;
    }


    public String getPadImg() {
        return padImg;
    }


    public String getPadGood() {
        return padGood;
    }

}
