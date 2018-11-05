package com.exceedgulf.alainzoo.database.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by P.S. on 12/02/18.
 *
 */

public class Schedule implements Serializable {

    @SerializedName("times")
    @Expose
    private List<TimeModel> times = null;

    public List<TimeModel> getTimes() {
        return times;
    }

    public void setTimes(List<TimeModel> times) {
        this.times = times;
    }

}
