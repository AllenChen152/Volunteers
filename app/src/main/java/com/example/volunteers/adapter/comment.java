package com.example.volunteers.adapter;

import android.graphics.Bitmap;

public class comment {
    public String u_name;
    public String p_id;
    public String c_text;
    public String c_time;
   // public List<Comment> comments; // 用于保存该动态的评论列表

    public comment(String u_name, String c_text, String c_time) {
        this.u_name = u_name;
        this.c_text = c_text;
        this.c_time = c_time;

    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getC_text() {
        return c_text;
    }

    public void setC_text(String c_text) {
        this.c_text = c_text;
    }

    public String getC_time() {
        return c_time;
    }

    public void setC_time(String c_time) {
        this.c_time = c_time;
    }
}
