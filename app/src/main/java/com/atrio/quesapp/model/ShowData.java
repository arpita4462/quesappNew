package com.atrio.quesapp.model;

/**
 * Created by Admin on 13-06-2017.
 */

public class ShowData {
    String sub;
    String img;
    String Lang;
    public String getLang() {
        return Lang;
    }

    public void setLang(String lang) {
        Lang = lang;
    }
    public ShowData() {
        super();
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }}
