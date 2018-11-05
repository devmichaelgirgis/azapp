package com.exceedgulf.alainzoo.database.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Geometry {

    @SerializedName("coordinates")
    private List<String> coordinates;

    @SerializedName("type")
    private String type;

    public List<String> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<String> coordinates) {
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}