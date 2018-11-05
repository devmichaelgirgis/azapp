package com.exceedgulf.alainzoo.database.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TreasureHuntStatus {

    @SerializedName("hints")
    private List<HintsItem> hints;

    @SerializedName("treasure_hunt")
    private TreasureHunt treasureHunt;

    public List<HintsItem> getHints() {
        return hints;
    }

    public void setHints(List<HintsItem> hints) {
        this.hints = hints;
    }

    public TreasureHunt getTreasureHunt() {
        return treasureHunt;
    }

    public void setTreasureHunt(TreasureHunt treasureHunt) {
        this.treasureHunt = treasureHunt;
    }
}