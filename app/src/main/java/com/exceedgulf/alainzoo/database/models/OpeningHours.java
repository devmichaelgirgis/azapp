package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.TypeConverters;

import com.exceedgulf.alainzoo.database.converters.OpeningHoursStringArrayConverter;

import java.util.ArrayList;

/**
 * Created by Paras Ghasadiya on 08/01/18.
 */
@Entity(tableName = "openinghours")
public class OpeningHours extends CommonEntity {
    private Boolean promoted;
    private String title;
    private String description;
    private String thumbnail_image;

    @TypeConverters({OpeningHoursStringArrayConverter.class})
    private ArrayList<OpeningHoursChild> opening_hours;

    public Boolean getPromoted() {
        return promoted;
    }

    public void setPromoted(Boolean promoted) {
        this.promoted = promoted;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail_image() {
        return thumbnail_image;
    }

    public void setThumbnail_image(String thumbnail_image) {
        this.thumbnail_image = thumbnail_image;
    }

    public ArrayList<OpeningHoursChild> getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(ArrayList<OpeningHoursChild> opening_hours) {
        this.opening_hours = opening_hours;
    }
}
