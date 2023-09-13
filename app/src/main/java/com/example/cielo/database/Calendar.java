package com.example.cielo.database;

import java.util.ArrayList;

public class Calendar {
    ArrayList<Integer> blood_amount ;
    ArrayList<String> duration_date;
    String pred_date;
    ArrayList<String> end_date ;
    ArrayList<Integer> mood ;
    ArrayList<Integer> pain ;
    Boolean period ;
    ArrayList<String> start_date ;
    ArrayList<Float> term ;
    String uid;

    public Calendar(String uid) {
        this.uid = uid;
        this.period= false;
    }

    public String getPred_date() {
        return pred_date;
    }

    public void setPred_date(String pred_date) {
        this.pred_date = pred_date;
    }


    public ArrayList<Integer> getBlood_amount() {
        return blood_amount;
    }

    public void setBlood_amount(ArrayList<Integer> blood_amount) {
        this.blood_amount = blood_amount;
    }

    public ArrayList<String> getDuration_date() {
        return duration_date;
    }

    public void setDuration_date(ArrayList<String> duration_date) {
        this.duration_date = duration_date;
    }

    public ArrayList<String> getEnd_date() {
        return end_date;
    }

    public void setEnd_date(ArrayList<String> end_date) {
        this.end_date = end_date;
    }

    public ArrayList<Integer> getMood() {
        return mood;
    }

    public void setMood(ArrayList<Integer> mood) {
        this.mood = mood;
    }

    public ArrayList<Integer> getPain() {
        return pain;
    }

    public void setPain(ArrayList<Integer> pain) {
        this.pain = pain;
    }

    public Boolean getPeriod() {
        return period;
    }

    public void setPeriod(Boolean period) {
        this.period = period;
    }

    public ArrayList<String> getStart_date() {
        return start_date;
    }

    public void setStart_date(ArrayList<String> start_date) {
        this.start_date = start_date;
    }

    public ArrayList<Float> getTerm() {
        return term;
    }

    public void setTerm(ArrayList<Float> term) {
        this.term = term;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
