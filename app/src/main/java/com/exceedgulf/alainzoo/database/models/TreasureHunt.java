package com.exceedgulf.alainzoo.database.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TreasureHunt {

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("hints")
    private List<HintsItem> hints;

    @SerializedName("name")
    private String name;

    @SerializedName("details")
    private String details;

    @SerializedName("id")
    private int id;

    @SerializedName("status")
    private String status;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("points")
    private String points;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<HintsItem> getHints() {
        return hints;
    }

    public void setHints(List<HintsItem> hints) {
        this.hints = hints;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}