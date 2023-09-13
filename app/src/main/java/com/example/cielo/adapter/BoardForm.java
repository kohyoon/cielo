package com.example.cielo.adapter;

import android.content.Context;


public class BoardForm {
    String boardtitle;
    String boardtext;
    String boardcommentNum;
    String boardtime;
    String boardtheme;
    String boardid;
    Context t;

    public BoardForm(Context t, String title, String text, String commentnum,  String time,String boardtheme,String boardid) {//new생성자를 통해서 생성자가 만들어진다.
        this.t=t;
        this.boardtitle = title;
        this.boardtext = text;
        this.boardcommentNum = commentnum;
        this.boardtime = time;
        this.boardtheme=boardtheme;
        this.boardid=boardid;

    }


    public Context getT() {
        return t;
    }
    public String getBoardtitle() {
        return boardtitle;
    }
    public String getBoardtext() {
        return boardtext;
    }
    public String getBoardcommentNum() {
        return boardcommentNum;
    }
    public String getboardtheme() {
        return boardtheme;
    }
    public String getBoardtime() {
        return boardtime;
    }
    public String getBoardid(){return boardid;}
}



