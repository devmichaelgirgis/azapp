package com.exceedgulf.alainzoo.models;

import android.support.annotation.NonNull;

import com.exceedgulf.alainzoo.database.models.LeaderBoard;

/**
 * Created by indianic on 01/03/18.
 */

public class TreasurLeaderBoard implements Comparable<TreasurLeaderBoard> {
    private boolean current_user;
    private String rank;
    private String total_hints;
    private int user_id;
    private String mobile;
    private String name;
    private String avatar;
    private String gender;
    private String current_points;
    private String total_points;
    private boolean isshildshow;

    public boolean isCurrent_user() {
        return current_user;
    }

    public void setCurrent_user(boolean current_user) {
        this.current_user = current_user;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getTotal_hints() {
        return total_hints;
    }

    public void setTotal_hints(String total_hints) {
        this.total_hints = total_hints;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCurrent_points() {
        return current_points;
    }

    public void setCurrent_points(String current_points) {
        this.current_points = current_points;
    }

    public String getTotal_points() {
        return total_points;
    }

    public void setTotal_points(String total_points) {
        this.total_points = total_points;
    }

    public boolean isIsshildshow() {
        return isshildshow;
    }

    public void setIsshildshow(boolean isshildshow) {
        this.isshildshow = isshildshow;
    }

    @Override
    public int compareTo(@NonNull TreasurLeaderBoard leaderBoard) {
        int compareQuantity = Integer.parseInt(leaderBoard.getRank());
        //ascending order
        return Integer.parseInt(this.rank) - compareQuantity;
    }

}
