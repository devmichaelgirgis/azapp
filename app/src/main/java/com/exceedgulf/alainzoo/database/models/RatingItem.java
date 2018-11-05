package com.exceedgulf.alainzoo.database.models;

import com.google.gson.annotations.SerializedName;

public class RatingItem {

    @SerializedName("value")
    private double value;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}