package com.exceedgulf.alainzoo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by michael.george on 12/6/2017.
 */

public class Image {


    @SerializedName("image")
    @Expose
    String image;

    public String getFieldImages() {
        return image;
    }

    public void setFieldImages(String image) {
        this.image = image;
    }
}
