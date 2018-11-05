package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import java.io.Serializable;

/**
 * Created on 03/01/18.
 */
@Entity(tableName = "visitor_services")
public class VisitorService extends CommonEntity implements Serializable {

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "details")
    private String details;
    @ColumnInfo(name = "thumbnail")
    private String thumbnail;
    private String banner_image;
    private String latitude;
    private String longitude;
    private String email;
    private String location;
    private String phone;
    private String price;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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
