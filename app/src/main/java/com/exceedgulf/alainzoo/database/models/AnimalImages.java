package com.exceedgulf.alainzoo.database.models;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.exceedgulf.alainzoo.database.converters.TimestampConverter;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Paras Ghasadiya on 02/01/18.
 */

public class AnimalImages implements Serializable {

    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
