package com.exceedgulf.alainzoo.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by R.S. on 20/12/17
 */
public class BadgeModel implements Parcelable {

    public static final Parcelable.Creator<BadgeModel> CREATOR = new Parcelable.Creator<BadgeModel>() {
        @Override
        public BadgeModel createFromParcel(Parcel source) {
            return new BadgeModel(source);
        }

        @Override
        public BadgeModel[] newArray(int size) {
            return new BadgeModel[size];
        }
    };
    private int id;
    private String badgeName;
    private boolean isBadgeEarned;

    public BadgeModel(final String badgeName, final boolean isBadgeEarned) {
        this.badgeName = badgeName;
        this.isBadgeEarned = isBadgeEarned;
    }

    private BadgeModel(Parcel in) {
        this.id = in.readInt();
        this.badgeName = in.readString();
        this.isBadgeEarned = in.readByte() != 0;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(final String badgeName) {
        this.badgeName = badgeName;
    }

    public boolean isBadgeEarned() {
        return isBadgeEarned;
    }

    public void setBadgeEarned(final boolean badgeEarned) {
        isBadgeEarned = badgeEarned;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.badgeName);
        dest.writeByte(this.isBadgeEarned ? (byte) 1 : (byte) 0);
    }
}
