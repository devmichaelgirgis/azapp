package com.exceedgulf.alainzoo.database.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Ticket implements Serializable {

    @SerializedName("title")
    private String title;

    @SerializedName("adult")
    private int adult;

    @SerializedName("child")
    private int child;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAdult() {
        return adult;
    }

    public void setAdult(int adult) {
        this.adult = adult;
    }

    public int getChild() {
        return child;
    }

    public void setChild(int child) {
        this.child = child;
    }
}