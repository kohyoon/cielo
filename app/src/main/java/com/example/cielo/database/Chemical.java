package com.example.cielo.database;

public class Chemical {
    String key;
    String name;
    int grade;



    Chemical(String key, String name, int grade){
        this.key = key;
        this.name = name;
        this.grade = grade;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

}

