package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.Entity;

import java.io.Serializable;


@Entity(tableName = "attraction")
public class Attraction extends CommonEntity implements Serializable{

    private String name;

    private String description;

    private String image;

    private String thumbnail;

    private String banner_image;

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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getBanner_image() {
        return banner_image;
    }

    public void setBanner_image(String banner_image) {
        this.banner_image = banner_image;
    }
}
