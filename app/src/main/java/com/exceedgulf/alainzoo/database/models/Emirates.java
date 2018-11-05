package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.Entity;

/**
 * Created by Paras Ghasadiya on 31/01/18.
 */
@Entity(tableName = "emirates")
public class Emirates extends CommonEntity {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
