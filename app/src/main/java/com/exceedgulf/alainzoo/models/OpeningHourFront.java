package com.exceedgulf.alainzoo.models;

import android.arch.persistence.room.Entity;

import com.exceedgulf.alainzoo.database.models.CommonEntity;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "opening_hours_front")
public class OpeningHourFront extends CommonEntity {

    @SerializedName("mobile_url")
    private String mobileUrl;

    @SerializedName("opening_hours")
    private String openingHours;

    @SerializedName("title")
    private String title;

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
}