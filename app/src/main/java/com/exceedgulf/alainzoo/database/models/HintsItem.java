package com.exceedgulf.alainzoo.database.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class HintsItem implements Parcelable {

    public static final Creator<HintsItem> CREATOR = new Creator<HintsItem>() {
        @Override
        public HintsItem createFromParcel(Parcel in) {
            return new HintsItem(in);
        }

        @Override
        public HintsItem[] newArray(int size) {
            return new HintsItem[size];
        }
    };
    @SerializedName("message_ar")
    private String messageAr;
    @SerializedName("qr")
    private String qr;
    @SerializedName("done_message_en")
    private String doneMessageEn;
    @SerializedName("message_en")
    private String messageEn;
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("title_ar")
    private String title_ar;
    @SerializedName("done_message_ar")
    private String doneMessageAr;
    @SerializedName("points")
    private int points;
    @SerializedName("status")
    private String status;

    protected HintsItem(Parcel in) {
        messageAr = in.readString();
        qr = in.readString();
        doneMessageEn = in.readString();
        messageEn = in.readString();
        id = in.readInt();
        title = in.readString();
        title_ar = in.readString();
        doneMessageAr = in.readString();
        status = in.readString();
        points = in.readInt();
    }

    public String getTitle_ar() {
        return title_ar;
    }

    public void setTitle_ar(String title_ar) {
        this.title_ar = title_ar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessageAr() {
        return messageAr;
    }

    public void setMessageAr(String messageAr) {
        this.messageAr = messageAr;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public String getDoneMessageEn() {
        return doneMessageEn;
    }

    public void setDoneMessageEn(String doneMessageEn) {
        this.doneMessageEn = doneMessageEn;
    }

    public String getMessageEn() {
        return messageEn;
    }

    public void setMessageEn(String messageEn) {
        this.messageEn = messageEn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDoneMessageAr() {
        return doneMessageAr;
    }

    public void setDoneMessageAr(String doneMessageAr) {
        this.doneMessageAr = doneMessageAr;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(messageAr);
        parcel.writeString(qr);
        parcel.writeString(doneMessageEn);
        parcel.writeString(messageEn);
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(title_ar);
        parcel.writeString(doneMessageAr);
        parcel.writeString(status);
        parcel.writeInt(points);
    }
}