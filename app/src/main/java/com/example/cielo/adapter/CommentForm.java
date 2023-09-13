package com.example.cielo.adapter;

import android.content.Context;

public class CommentForm {

    String key; //생리대제품키
    String nickname;
    String uid;
    int age;
    int blood; // 1:적음, 2: 보통, 3: 많음
    String comment;
    Context ct;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public CommentForm(String key, String nickname, String uid, int age, int blood, String comment, Context ct) {
        this.key = key;
        this.uid= uid;
        this.nickname = nickname;
        this.age = age;
        this.blood = blood;
        this.comment = comment;
        this.ct = ct;
    }

    public CommentForm() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Context getCt() {
        return ct;
    }

    public void setCt(Context ct) {
        this.ct = ct;
    }
}
