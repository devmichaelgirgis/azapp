package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.exceedgulf.alainzoo.database.converters.TimestampConverter;
import com.exceedgulf.alainzoo.utils.LangUtils;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Paras Ghasadiya on 08/02/18.
 */
@Entity(tableName = "explorezoo")
public class ExploreZoo {
    @PrimaryKey(autoGenerate = true)
    private int mapId;
    private String area;
    private String category;
    private String name;
    private String type;
    private String icon;
    private String thumbnail;
    private String path;
    private String body;
    private String latitude;
    private String longitude;

    @ColumnInfo(name = "id")
    private Integer id;

    @NonNull
    @ColumnInfo(name = "language")
    private String language = LangUtils.getCurrentLanguage();

    @ColumnInfo(name = "modified_date")
    @TypeConverters({TimestampConverter.class})
    private Date modifiedDate = new Date();

    @ColumnInfo(name = "creation_date")
    @TypeConverters({TimestampConverter.class})
    private Date creationDate = new Date();

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
