package com.example.volunteers.adapter;

import java.util.Date;

public class task {
    private String id;
    private String locate;
    private String message;
    private Date time;
    private int sumtime;
    private int sump;
    private boolean joined;

    public task(String id, String locate, String message, Date time, int sumtime, int sump,boolean joined) {
        this.id = id;
        this.locate = locate;
        this.message = message;
        this.time = time;
        this.sumtime = sumtime;
        this.sump = sump;
        this.joined=joined;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocate() {
        return locate;
    }

    public void setLocate(String locate) {
        this.locate = locate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getSumtime() {
        return sumtime;
    }

    public void setSumtime(int sumtime) {
        this.sumtime = sumtime;
    }

    public int getSump() {
        return sump;
    }

    public void setSump(int sump) {
        this.sump = sump;
    }
}
