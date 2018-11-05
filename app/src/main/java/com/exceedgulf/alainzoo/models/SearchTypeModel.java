package com.exceedgulf.alainzoo.models;

/**
 * Created by R.S. on 20/12/17
 */
public class SearchTypeModel {

    private int id;
    private String name;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(final boolean selected) {
        isSelected = selected;
    }

    public SearchTypeModel(final String name) {
        this.name = name;
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


}
