package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by indianic on 28/02/18.
 */
@Entity(tableName = "myplanevisiteditem")
public class MyPlaneVisitedItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private Integer planId;
    private int visitedItemId;
    private String visitedItemType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public int getVisitedItemId() {
        return visitedItemId;
    }

    public void setVisitedItemId(int visitedItemId) {
        this.visitedItemId = visitedItemId;
    }

    public String getVisitedItemType() {
        return visitedItemType;
    }

    public void setVisitedItemType(String visitedItemType) {
        this.visitedItemType = visitedItemType;
    }
}
