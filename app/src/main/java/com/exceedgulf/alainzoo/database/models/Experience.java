package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.TypeConverters;

import com.exceedgulf.alainzoo.database.converters.ImageStringArrayConverter;
import com.exceedgulf.alainzoo.database.converters.VoteStringArrayConverter;

import java.io.Serializable;
import java.util.ArrayList;


@Entity(tableName = "experiences")
public class Experience extends CommonEntity implements Serializable {

    private String name;

    private String details;

    private String icon;

    private String plan_visit_icon;

    private String banner;

    private String thumbnail;

    private String image;

    private Integer ticket_price;

    private String opening_hours;

    private String latitude;

    private String longitude;
    @TypeConverters({VoteStringArrayConverter.class})
    private ArrayList<Vote> rating;
    @TypeConverters({ImageStringArrayConverter.class})
    private ArrayList<AnimalImages> images;

    public String getPlan_visit_icon() {
        return plan_visit_icon;
    }

    public void setPlan_visit_icon(String plan_visit_icon) {
        this.plan_visit_icon = plan_visit_icon;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public Integer getTicket_price() {
        return ticket_price;
    }

    public void setTicket_price(Integer ticket_price) {
        this.ticket_price = ticket_price;
    }

    public String getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(String opening_hours) {
        this.opening_hours = opening_hours;
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

    public ArrayList<Vote> getRating() {
        return rating;
    }

    public void setRating(ArrayList<Vote> rating) {
        this.rating = rating;
    }

    public ArrayList<AnimalImages> getImages() {
        return images;
    }

    public void setImages(ArrayList<AnimalImages> images) {
        this.images = images;
    }
}
