package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "family")
public class Family extends CommonEntity {

    @SerializedName("gender")
    private String gender;

    @SerializedName("name")
    private String name;

    @SerializedName("relative")
    private String relative;

    public Family() {

    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelative() {
        return relative;
    }

    public void setRelative(String relative) {
        this.relative = relative;
    }
}