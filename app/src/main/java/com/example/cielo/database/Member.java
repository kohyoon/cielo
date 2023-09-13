package com.example.cielo.database;

import java.util.Date;

enum Sex{
    Female,
    Male
}

public class Member {
    private String key; //회원번호
    private String name;
    int blood; //1: 적음 2: 보통 3: 많음
    private Date birthday;
    private int age;
    private Sex sex;
    private String nickname;
    private float manner = (float) 36.5;
    private boolean manage;

    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }



    public boolean isManage() {
        return manage;
    }

    public void setManage(boolean manage) {
        this.manage = manage;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public Sex getSex() {
        return sex;
    }

    public String getName() {
        return name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public int getAge() {
        return age;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public float getManner() {
        return manner;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public String getNickname() {
        return nickname;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setManner(float manner) {
        this.manner = manner;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
