package com.exceedgulf.alainzoo.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by R.S. on 20/12/17
 */
public class SearchItemModel implements Parcelable {

    public static final Parcelable.Creator<SearchItemModel> CREATOR = new Parcelable.Creator<SearchItemModel>() {
        @Override
        public SearchItemModel createFromParcel(Parcel source) {
            return new SearchItemModel(source);
        }

        @Override
        public SearchItemModel[] newArray(int size) {
            return new SearchItemModel[size];
        }
    };
    private int id;
    private String name;
    private String type;
    private String desc;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public SearchItemModel(final String name, final String type, final String desc) {
        this.name = name;
        this.type = type;
        this.desc = desc;
    }

    public SearchItemModel() {
    }

    private SearchItemModel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(final String desc) {
        this.desc = desc;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
    }
}
