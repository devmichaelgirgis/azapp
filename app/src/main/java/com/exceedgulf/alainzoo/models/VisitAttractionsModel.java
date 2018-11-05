package com.exceedgulf.alainzoo.models;

import java.io.Serializable;

/**
 * Created by Paras Ghasadiya on 18/01/18.
 */

public class VisitAttractionsModel implements Serializable {
    private int id;
    private String name;

    private String image;

    private String thumbnail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
