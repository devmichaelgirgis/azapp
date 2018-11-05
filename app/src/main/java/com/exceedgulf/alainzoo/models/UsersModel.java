package com.exceedgulf.alainzoo.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by R.S. on 20/12/17
 */
public class UsersModel implements Parcelable {

    public static final Parcelable.Creator<UsersModel> CREATOR = new Parcelable.Creator<UsersModel>() {
        @Override
        public UsersModel createFromParcel(Parcel source) {
            return new UsersModel(source);
        }

        @Override
        public UsersModel[] newArray(int size) {
            return new UsersModel[size];
        }
    };
    private int id;
    private String name;
    private String url;

    public UsersModel(final String name) {
        this.name = name;
    }

    private UsersModel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.url = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.url);
    }
}
