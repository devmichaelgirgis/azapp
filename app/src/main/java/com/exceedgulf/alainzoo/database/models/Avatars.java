package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.Entity;

/**
 * Created by Paras Ghasadiya on 09/01/18.
 */
@Entity(tableName = "avatars")
public class Avatars extends CommonEntity {
    private String title;
    private String image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
