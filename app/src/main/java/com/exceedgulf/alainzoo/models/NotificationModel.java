package com.exceedgulf.alainzoo.models;

import com.exceedgulf.alainzoo.database.models.CommonEntity;

/**
 * Created by R.S. on 19/12/17
 */
public class NotificationModel extends CommonEntity {
    private String title;
    private String message_en;
    private String message_ar;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage_en() {
        return message_en;
    }

    public void setMessage_en(String message_en) {
        this.message_en = message_en;
    }

    public String getMessage_ar() {
        return message_ar;
    }

    public void setMessage_ar(String message_ar) {
        this.message_ar = message_ar;
    }
}
