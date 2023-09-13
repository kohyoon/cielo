package com.example.cielo.database;


import java.util.ArrayList;

public class Board {
    String key; //게시글 번호
    Theme theme;
    String title;
    String content;
    String id;
    int commentNum;
    //추가
    boolean anonymous;
    ArrayList<String> comment= new ArrayList<String>();//
    ArrayList<String> commenttime=new ArrayList<String>();
    ArrayList<String> commentID= new ArrayList<String>();

    //추가
    public boolean getAnonymous() {
        return anonymous;
    }
    //추가
    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public ArrayList<String> getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {

        String[] split = commentID.split(" ");
        this.commentID.add(commentID);
    }


    public ArrayList<String> getCommenttime() {
        return commenttime;
    }

    public void setCommenttime(String commenttime) {
        this.commenttime.add(commenttime);}
    public ArrayList<String> getComment() {
        return comment;
    }
    public void setComment( String comment) {
        this.comment.add(comment);
    }



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }


    public int getCommentNum() {
        return commentNum;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public void setTitle(String title) {
        this.title = title;
    }

}





