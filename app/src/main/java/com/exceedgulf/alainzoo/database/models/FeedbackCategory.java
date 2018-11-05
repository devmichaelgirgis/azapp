package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.Entity;

import java.io.Serializable;

@Entity(tableName = "feedback_category")
public class FeedbackCategory extends CommonEntity implements Serializable {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}