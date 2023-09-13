package com.example.cielo.database;

import java.util.ArrayList;

public class Comment {
    String key; //생리대제품키
    ArrayList<String> nickname;
    ArrayList<Integer> age;
    ArrayList<Integer> blood; // 1:적음, 2: 보통, 3: 많음
    ArrayList<String> comment;
    ArrayList<String> uid;

    public Comment() {
    }

    public ArrayList<String> getUid() {
        return uid;
    }

    public void setUid(ArrayList<String> uid) {
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }


    public ArrayList<String> getNickname() {
        return nickname;
    }


    public ArrayList<Integer> getAge() {
        return age;
    }


    public ArrayList<Integer> getBlood() {
        return blood;
    }


    public ArrayList<String> getComment() {
        return comment;
    }



    public void setKey(String key) {
        this.key = key;
    }

    public void setNickname(ArrayList<String> nickname) {
        this.nickname = nickname;
    }

    public void setAge(ArrayList<Integer> age) {
        this.age = age;
    }

    public void setBlood(ArrayList<Integer> blood) {
        this.blood = blood;
    }

    public void setComment(ArrayList<String> comment) {
        this.comment = comment;
    }
}
