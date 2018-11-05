package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.TypeConverters;

import com.exceedgulf.alainzoo.database.converters.ImageStringArrayConverter;

import java.io.Serializable;
import java.util.ArrayList;


@Entity(tableName = "animal")
public class Animal extends CommonEntity implements Serializable {


    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "details")
    private String details;


    @ColumnInfo(name = "category_id")
    private Integer category_id;

    private String category;

    private String banner;

    @TypeConverters({ImageStringArrayConverter.class})
    private ArrayList<AnimalImages> images;

    @ColumnInfo(name = "thumbnail")
    private String thumbnail;

    private String is_celebrity;
    private String feeding_hours;
    private String latitude;
    private String longitude;

    public String getIs_celebrity() {
        return is_celebrity;
    }

    public void setIs_celebrity(String is_celebrity) {
        this.is_celebrity = is_celebrity;
    }

    public String getFeeding_hours() {
        return feeding_hours;
    }

    public void setFeeding_hours(String feeding_hours) {
        this.feeding_hours = feeding_hours;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public ArrayList<AnimalImages> getImages() {
        return images;
    }

    public void setImages(ArrayList<AnimalImages> images) {
        this.images = images;
    }
}
