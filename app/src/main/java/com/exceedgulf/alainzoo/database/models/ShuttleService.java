package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by P.S. on 16/02/18.
 */
@Entity(tableName = "shuttle")
public class ShuttleService extends CommonEntity implements Serializable {

    @SerializedName("name_en")
    private String nameEn;
    @SerializedName("name_ar")
    private String nameAr;
    private String latitude;
    private String longitude;
    private String icon;
//    @SerializedName("beacon")
//    @Expose
//    private List<Object> beacon = null;

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

//    public List<Object> getBeacon() {
//        return beacon;
//    }
//
//    public void setBeacon(List<Object> beacon) {
//        this.beacon = beacon;
//    }

}
