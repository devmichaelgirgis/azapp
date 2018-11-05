package com.exceedgulf.alainzoo.database.models;

import android.arch.persistence.room.Entity;

/**
 * Created by Paras Ghasadiya on 29/01/18.
 */
@Entity(tableName = "about_terms_privacy")
public class AboutTermsPrivacyModel extends CommonEntity {
    private int type;
    private String title_main;
    private String title;
    private String details;
    private String image;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle_main() {
        return title_main;
    }

    public void setTitle_main(String title_main) {
        this.title_main = title_main;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
