package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.exceedgulf.alainzoo.database.converters.TimestampConverter;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Locale;

public class Features {

    @NonNull
    @ColumnInfo(name = "language")
    private String language = LangUtils.getCurrentLanguage();

    @ColumnInfo(name = "modified_date")
    @TypeConverters({TimestampConverter.class})
    private Date modifiedDate = new Date();

    @ColumnInfo(name = "creation_date")
    @TypeConverters({TimestampConverter.class})
    private Date creationDate = new Date();

    @SerializedName("geometry")
    private Geometry geometry;

    @SerializedName("type")
    private String type;

    @SerializedName("properties")
    private Properties properties;

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @NonNull
    public String getLanguage() {
        return language;
    }

    public void setLanguage(@NonNull String language) {
        this.language = language;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}