package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.Entity;

/**
 * Created by indianic on 07/03/18.
 */
@Entity(tableName = "camera_frame")
public class CameraFrame extends CommonEntity {
    private String title;
    private String image_position;
    private String image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage_position() {
        return image_position;
    }

    public void setImage_position(String image_position) {
        this.image_position = image_position;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
