package com.exceedgulf.alainzoo.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.exceedgulf.alainzoo.database.converters.TicketModelStringArrayConverter;
import com.exceedgulf.alainzoo.database.converters.VisitAnimalModelStringArrayConverter;
import com.exceedgulf.alainzoo.database.converters.VisitAttractionsModelStringArrayConverter;
import com.exceedgulf.alainzoo.database.converters.VisitExperiencesModelStringArrayConverter;
import com.exceedgulf.alainzoo.database.converters.VisitOrderStringArrayConverter;
import com.exceedgulf.alainzoo.database.converters.VisitWhatsNewModelStringArrayConverter;
import com.exceedgulf.alainzoo.database.models.Ticket;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by P.P on 09/03/18.
 */

@Entity(tableName = "generalplan")
public class GeneralPlan implements Serializable {
    @PrimaryKey
    private int id;
    private String langcode;
    private String name;
    private String planeDate;
    private String duration;
    private String details;
    private String image;
    @TypeConverters({TicketModelStringArrayConverter.class})
    private ArrayList<Ticket> ticketArrayListAr;

    @TypeConverters({TicketModelStringArrayConverter.class})
    private ArrayList<Ticket> ticketArrayList;

    @TypeConverters({VisitAnimalModelStringArrayConverter.class})
    private ArrayList<VisitAnimalModel> animalArrayList;

    @TypeConverters({VisitWhatsNewModelStringArrayConverter.class})
    private ArrayList<VisitWhatsNewModel> visitWhatsNewModelArrayList;

    @TypeConverters({VisitAttractionsModelStringArrayConverter.class})
    private ArrayList<VisitAttractionsModel> attractionArrayList;

    @TypeConverters({VisitExperiencesModelStringArrayConverter.class})
    private ArrayList<VisitExperiencesModel> experienceArrayList;

    @TypeConverters({VisitOrderStringArrayConverter.class})
    private ArrayList<VisitOrder> visitOrderArrayList;

    public GeneralPlan() {
        ticketArrayList = new ArrayList<>();
        ticketArrayListAr = new ArrayList<>();
        animalArrayList = new ArrayList<>();
        visitWhatsNewModelArrayList = new ArrayList<>();
        attractionArrayList = new ArrayList<>();
        experienceArrayList = new ArrayList<>();
        visitOrderArrayList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLangcode() {
        return langcode;
    }

    public void setLangcode(String langcode) {
        this.langcode = langcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaneDate() {
        return planeDate;
    }

    public void setPlaneDate(String planeDate) {
        this.planeDate = planeDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<Ticket> getTicketArrayListAr() {
        return ticketArrayListAr;
    }

    public void setTicketArrayListAr(ArrayList<Ticket> ticketArrayListAr) {
        this.ticketArrayListAr = ticketArrayListAr;
    }

    public ArrayList<Ticket> getTicketArrayList() {
        return ticketArrayList;
    }

    public void setTicketArrayList(ArrayList<Ticket> ticketArrayList) {
        this.ticketArrayList = ticketArrayList;
    }

    public ArrayList<VisitAnimalModel> getAnimalArrayList() {
        return animalArrayList;
    }

    public void setAnimalArrayList(ArrayList<VisitAnimalModel> animalArrayList) {
        this.animalArrayList = animalArrayList;
    }

    public ArrayList<VisitWhatsNewModel> getVisitWhatsNewModelArrayList() {
        return visitWhatsNewModelArrayList;
    }

    public void setVisitWhatsNewModelArrayList(ArrayList<VisitWhatsNewModel> visitWhatsNewModelArrayList) {
        this.visitWhatsNewModelArrayList = visitWhatsNewModelArrayList;
    }

    public ArrayList<VisitAttractionsModel> getAttractionArrayList() {
        return attractionArrayList;
    }

    public void setAttractionArrayList(ArrayList<VisitAttractionsModel> attractionArrayList) {
        this.attractionArrayList = attractionArrayList;
    }

    public ArrayList<VisitExperiencesModel> getExperienceArrayList() {
        return experienceArrayList;
    }

    public void setExperienceArrayList(ArrayList<VisitExperiencesModel> experienceArrayList) {
        this.experienceArrayList = experienceArrayList;
    }

    public ArrayList<VisitOrder> getVisitOrderArrayList() {
        return visitOrderArrayList;
    }

    public void setVisitOrderArrayList(ArrayList<VisitOrder> visitOrderArrayList) {
        this.visitOrderArrayList = visitOrderArrayList;
    }
}
