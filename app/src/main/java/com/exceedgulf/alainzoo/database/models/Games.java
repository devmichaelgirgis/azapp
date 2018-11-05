package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.Entity;

import java.io.Serializable;

@Entity(tableName = "games")
public class Games extends CommonEntity implements Serializable {
    private String name;
    private String description;
    private String image;
    private String android;
    private String ios;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAndroid() {
        return android;
    }

    public void setAndroid(String android) {
        this.android = android;
    }

    public String getIos() {
        return ios;
    }

    public void setIos(String ios) {
        this.ios = ios;
    }
}