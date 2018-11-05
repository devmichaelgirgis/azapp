package com.exceedgulf.alainzoo.models;

import java.io.Serializable;

/**
 * Created by Paras Ghasadiya on 18/01/18.
 */

public class VisitAnimalModel implements Serializable {
    private int id;
    private String name;

    private String details;

    private int categoryId;

    private String category;

    private String is_celebrity;

    private String feeding_hours;

    private String banner;

    private String image;

    private String thumbnail;

    private String latitude;

    private String longitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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
}
