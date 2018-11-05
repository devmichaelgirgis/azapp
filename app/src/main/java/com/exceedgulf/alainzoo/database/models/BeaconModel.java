package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "beacons")
public class BeaconModel extends CommonEntity {

    @SerializedName("minor")
    private String minor;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("enter_message_ar")
    private String enterMessageAr;

    @SerializedName("enter_message_en")
    private String enterMessageEn;

    @SerializedName("exit_message_ar")
    private String exitMessageAr;

    @SerializedName("major")
    private String major;

    @SerializedName("action_id")
    private String actionId;

    @SerializedName("action_name")
    private String actionName;

    @SerializedName("exit_message_en")
    private String exitMessageEn;

    @SerializedName("admin_name")
    private String adminName;

    @SerializedName("UUID")
    private String uUID;

    @SerializedName("longitude")
    private String longitude;

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getEnterMessageAr() {
        return enterMessageAr;
    }

    public void setEnterMessageAr(String enterMessageAr) {
        this.enterMessageAr = enterMessageAr;
    }

    public String getEnterMessageEn() {
        return enterMessageEn;
    }

    public void setEnterMessageEn(String enterMessageEn) {
        this.enterMessageEn = enterMessageEn;
    }

    public String getExitMessageAr() {
        return exitMessageAr;
    }

    public void setExitMessageAr(String exitMessageAr) {
        this.exitMessageAr = exitMessageAr;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getExitMessageEn() {
        return exitMessageEn;
    }

    public void setExitMessageEn(String exitMessageEn) {
        this.exitMessageEn = exitMessageEn;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getUUID() {
        return uUID;
    }

    public void setUUID(String uUID) {
        this.uUID = uUID;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}