package com.example.cielo.database;

import java.util.ArrayList;

class Sanitarypad {
    String pad_name;
    int pad_price;
    int key; //생리대 품번
    int length; //길이
    int recommend; //추천수

    ArrayList<String> inside_cover = new ArrayList<String>();
    ArrayList<String> cover=new ArrayList<String>();
    ArrayList<String> waterproof = new ArrayList<String>();
    ArrayList<String> absorber = new ArrayList<String>();
    ArrayList<String> adhesive = new ArrayList<String>();

    public int getLength() {
        return length;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
    public String getPad_name() {
        return pad_name;
    }

    public int getPad_price() {
        return pad_price;
    }

    public ArrayList<String> getAbsorber() {
        return absorber;
    }

    public ArrayList<String> getAdhesive() {
        return adhesive;
    }

    public ArrayList<String> getCover() {
        return cover;
    }

    public ArrayList<String> getInside_cover() {
        return inside_cover;
    }

    public ArrayList<String> getWaterproof() {
        return waterproof;
    }

    public void setPad_name(String pad_name) {
        this.pad_name = pad_name;
    }


    public void setPad_price(int pad_price) {

        this.pad_price = pad_price;

    }
    public void setAbsorber(String absorber) {
        String[] split = absorber.split(" ");
        for(int i = 0 ; i<split.length; i++){
            this.absorber.add(split[i]);
        }

    }

    public void setAdhesive(String adhesive) {
        String[] split = adhesive.split(" ");
        for(int i = 0 ; i<split.length; i++){
            this.adhesive.add(split[i]);
        }
    }

    public void setCover(String cover) {
        String[] split = cover.split(" ");
        for(int i = 0 ; i<split.length; i++){
            this.cover.add(split[i]);
        }
    }

    public void setInside_cover(String inside_cover) {
        String[] split = inside_cover.split(" ");
        for(int i = 0 ; i<split.length; i++){
            this.inside_cover.add(split[i]);
        }
    }

    public void setWaterproof(String waterproof) {
        String[] split = waterproof.split(" ");
        for(int i = 0 ; i<split.length; i++){
            this.waterproof.add(split[i]);
        }
    }
}

