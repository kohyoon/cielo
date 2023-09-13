package com.example.cielo.database;

import java.util.ArrayList;

public class ChemicalList{
    ArrayList<Chemical> chem = new ArrayList<>();

    public ChemicalList(){
        chem.add(new Chemical("","",0));
        chem.add(new Chemical("C001","흡수지",1));
        chem.add(new Chemical("C002","황색205호",1));
        chem.add(new Chemical("C003","하이드로카본수지",1));
        chem.add(new Chemical("C004","폴리프로필렌섬유",1));
        chem.add(new Chemical("C005","폴리우레탄섬유",1));
        chem.add(new Chemical("C006","폴리에틸렌필름NB",1));
        chem.add(new Chemical("C007","폴리에틸렌필름",2));
        chem.add(new Chemical("C008","폴리에틸렌폴리프로필렌복합섬유",1));
        chem.add(new Chemical("C009","폴리에틸렌폴리에스테르복합섬유",1));
        chem.add(new Chemical("C010","폴리에틸렌",5));
        chem.add(new Chemical("C011","폴리에틸렌섬유",1));
        chem.add(new Chemical("C012","폴리에스테르폴리에틸렌복합섬유",2));
        chem.add(new Chemical("C013","폴리아크릴산나트륨가교체",1));
        chem.add(new Chemical("C014","폴리아크릴산나트륨",1));
        chem.add(new Chemical("C015","파라핀계탄화수소",4));
        chem.add(new Chemical("C016","탄화수소지",4));
        chem.add(new Chemical("C017","청색404호",3));
        chem.add(new Chemical("C018","적색202호",3));
        chem.add(new Chemical("C019","적색201호",5));
        chem.add(new Chemical("C020","우드셀룰로오스섬유",1));
        chem.add(new Chemical("C021","올레핀중합체",1));
        chem.add(new Chemical("C022","열용융형접착제G",1));
        chem.add(new Chemical("C023","열용융형접착제C",1));
        chem.add(new Chemical("C024","스틸렌블록공중합체",2));
        chem.add(new Chemical("C025","스틸렌부티디엔공중합체",1));
        chem.add(new Chemical("C026","스틸렌부타티엔블록공중합체",1));
        chem.add(new Chemical("C027","스티렌블록공중합체",1));
        chem.add(new Chemical("C028","순면부직포",1));
        chem.add(new Chemical("C029","산화티탄",4));
        chem.add(new Chemical("C030","부직포",1));
        chem.add(new Chemical("C031","벤젠",4));
        chem.add(new Chemical("C032","백색부직포",1));
        chem.add(new Chemical("C033","면상펄프",2));
        chem.add(new Chemical("C034","SBC열가소성고무수지",1));
        chem.add(new Chemical("C035","PP섬유",1));
        chem.add(new Chemical("C036","PET섬유",1));
        chem.add(new Chemical("C037","PET",1));
        chem.add(new Chemical("C038","PE",1));
        chem.add(new Chemical("C039","폴리에스테르폴리에틸렌부직포",1));
    }

    public ArrayList<Chemical> getChem() {
        return chem;
    }

   public int returnGrade(String key){
        int index = Integer.parseInt(key.substring(key.length()-2));
        return chem.get(index).getGrade();
    }


}
