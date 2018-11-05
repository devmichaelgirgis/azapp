package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.TypeConverters;

import com.exceedgulf.alainzoo.constants.TagsName;
import com.exceedgulf.alainzoo.database.converters.DateStringArrayConverter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by michael.george on 12/28/2017.
 */
@Entity(tableName = "whatsnews")
public class WhatsNew extends CommonEntity implements Serializable {

    @ColumnInfo(name = TagsName.NAME)
    private String name;

    @ColumnInfo(name = TagsName.DETAILS)
    private String details;

    @ColumnInfo(name = TagsName.DESCRIPTION)
    private String description;

    @ColumnInfo(name = TagsName.BANNER)
    private String bannerImage;

    @ColumnInfo(name = TagsName.IMAGE)
    private String image;

    @ColumnInfo(name = TagsName.LATITUDE)
    private String latitude;

    @ColumnInfo(name = TagsName.LONGITUDE)
    private String longitude;

    @ColumnInfo(name = TagsName.THUMBNAIL)
    private String thumbnail;

    @ColumnInfo(name = TagsName.DATES)
    @SerializedName("date")
    @TypeConverters({DateStringArrayConverter.class})
    private ArrayList<WhatsNewDate> dates;

    @ColumnInfo(name = TagsName.IMAGES)
    private transient String images;

    public ArrayList<WhatsNewDate> getDates() {
        return dates;
    }

    public void setDates(ArrayList<WhatsNewDate> dates) {
        this.dates = dates;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
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


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
