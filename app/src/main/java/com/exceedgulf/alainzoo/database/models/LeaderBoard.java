package com.exceedgulf.alainzoo.database.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.List;

public class LeaderBoard implements Comparable<LeaderBoard> {

    @SerializedName("rank")
    private String rank;

    @SerializedName("user")
    private List<UserItem> user;

    @SerializedName("current_user")
    private boolean currentUser;

    @SerializedName("points")
    private int points;

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public List<UserItem> getUser() {
        return user;
    }

    public void setUser(List<UserItem> user) {
        this.user = user;
    }

    public boolean isCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(boolean currentUser) {
        this.currentUser = currentUser;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public int compareTo(@NonNull LeaderBoard leaderBoard) {
        int compareQuantity = Integer.parseInt(leaderBoard.getRank());
        //ascending order
        return Integer.parseInt(this.rank) - compareQuantity;
    }
}