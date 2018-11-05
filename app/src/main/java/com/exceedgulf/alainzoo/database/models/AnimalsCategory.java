package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;


/**
 * Created by michael.george on 12/7/2017.
 */

@Entity(tableName = "animals_categories")
public class AnimalsCategory extends CommonEntity {


    @ColumnInfo(name = "title")
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
