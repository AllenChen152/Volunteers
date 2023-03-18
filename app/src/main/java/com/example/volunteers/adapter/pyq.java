package com.example.volunteers.adapter;

import android.graphics.Bitmap;

public class pyq {
    private String name;
    private String p_id;
    private String text;
    private String time;
    private Bitmap picture;

    public pyq(String name, String p_id, String text, String time, Bitmap picture) {
        this.name = name;
        this.p_id = p_id;
        this.text = text;
        this.time = time;
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
