package com.example.cielo.adapter;

public class bcommentForm {
String ment;
String cID;
String ctime;

    public bcommentForm (String ment, String cID, String ctime) {

        this.ment = ment;
        this.cID = cID;
        this.ctime = ctime;

    }
    public String getment() {
        return ment;
    }
    public String getcID() {
        return cID;
    }
    public String getCtime() {
        return ctime;
    }
}
