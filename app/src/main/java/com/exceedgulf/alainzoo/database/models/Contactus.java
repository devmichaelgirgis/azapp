package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.exceedgulf.alainzoo.database.converters.AddressInformationStringArrayConverter;
import com.exceedgulf.alainzoo.database.converters.TimestampConverter;
import com.exceedgulf.alainzoo.models.AddressInformationModel;
import com.exceedgulf.alainzoo.utils.LangUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Paras Ghasadiya on 29/01/18.
 */
@Entity(tableName = "contat_us")
public class Contactus {

    @PrimaryKey(autoGenerate = true)
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


    private String title;
    @TypeConverters({AddressInformationStringArrayConverter.class})
    private ArrayList<AddressInformationModel> addressInformationModelArrayList;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<AddressInformationModel> getAddressInformationModelArrayList() {
        return addressInformationModelArrayList;
    }

    public void setAddressInformationModelArrayList(ArrayList<AddressInformationModel> addressInformationModelArrayList) {
        this.addressInformationModelArrayList = addressInformationModelArrayList;
    }

    //    private String locationTitle;
//    private String locationDetails;
//    private String locationImage;
//
//    private String poboxTitle;
//    private String poboxDetails;
//    private String poboxImage;
//
//    private String faxTitle;
//    private String faxDetails;
//    private String faxImage;
//
//    private String contactTitle;
//    private String contactIconOne;
//    private String contactInfoOne;
//
//    private String contactIconTwo;
//    private String contactInfoTwo;
//
//    private String contactIconThree;
//    private String contactInfoThree;
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getLocationTitle() {
//        return locationTitle;
//    }
//
//    public void setLocationTitle(String locationTitle) {
//        this.locationTitle = locationTitle;
//    }
//
//    public String getLocationDetails() {
//        return locationDetails;
//    }
//
//    public void setLocationDetails(String locationDetails) {
//        this.locationDetails = locationDetails;
//    }
//
//    public String getLocationImage() {
//        return locationImage;
//    }
//
//    public void setLocationImage(String locationImage) {
//        this.locationImage = locationImage;
//    }
//
//    public String getPoboxTitle() {
//        return poboxTitle;
//    }
//
//    public void setPoboxTitle(String poboxTitle) {
//        this.poboxTitle = poboxTitle;
//    }
//
//    public String getPoboxDetails() {
//        return poboxDetails;
//    }
//
//    public void setPoboxDetails(String poboxDetails) {
//        this.poboxDetails = poboxDetails;
//    }
//
//    public String getPoboxImage() {
//        return poboxImage;
//    }
//
//    public void setPoboxImage(String poboxImage) {
//        this.poboxImage = poboxImage;
//    }
//
//    public String getContactTitle() {
//        return contactTitle;
//    }
//
//    public void setContactTitle(String contactTitle) {
//        this.contactTitle = contactTitle;
//    }
//
//    public String getContactIconOne() {
//        return contactIconOne;
//    }
//
//    public void setContactIconOne(String contactIconOne) {
//        this.contactIconOne = contactIconOne;
//    }
//
//    public String getContactInfoOne() {
//        return contactInfoOne;
//    }
//
//    public void setContactInfoOne(String contactInfoOne) {
//        this.contactInfoOne = contactInfoOne;
//    }
//
//    public String getContactIconTwo() {
//        return contactIconTwo;
//    }
//
//    public void setContactIconTwo(String contactIconTwo) {
//        this.contactIconTwo = contactIconTwo;
//    }
//
//    public String getContactInfoTwo() {
//        return contactInfoTwo;
//    }
//
//    public void setContactInfoTwo(String contactInfoTwo) {
//        this.contactInfoTwo = contactInfoTwo;
//    }
//
//    public String getContactIconThree() {
//        return contactIconThree;
//    }
//
//    public void setContactIconThree(String contactIconThree) {
//        this.contactIconThree = contactIconThree;
//    }
//
//    public String getContactInfoThree() {
//        return contactInfoThree;
//    }
//
//    public void setContactInfoThree(String contactInfoThree) {
//        this.contactInfoThree = contactInfoThree;
//    }
//
//    public String getFaxTitle() {
//        return faxTitle;
//    }
//
//    public void setFaxTitle(String faxTitle) {
//        this.faxTitle = faxTitle;
//    }
//
//    public String getFaxDetails() {
//        return faxDetails;
//    }
//
//    public void setFaxDetails(String faxDetails) {
//        this.faxDetails = faxDetails;
//    }
//
//    public String getFaxImage() {
//        return faxImage;
//    }
//
//    public void setFaxImage(String faxImage) {
//        this.faxImage = faxImage;
//    }
}
