package com.exceedgulf.alainzoo.views.expands;

import java.util.List;

public class NavigationParent implements ParentListItem {


    private String mName;
    private int mImage;
    private boolean allowedToExpand;
    private List<Children> mChilds;

    public NavigationParent() {

    }

    public NavigationParent(String name, List<Children> movies) {
        mName = name;
        mChilds = movies;
    }

    public boolean isAllowedToExpand() {
        return allowedToExpand;
    }

    public void setAllowedToExpand(boolean allowedToExpand) {
        this.allowedToExpand = allowedToExpand;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getmImage() {
        return mImage;
    }

    public void setmImage(int mImage) {
        this.mImage = mImage;
    }

    public void setmChilds(List<Children> mChilds) {
        this.mChilds = mChilds;
    }

    public String getName() {
        return mName;
    }

    @Override
    public List<?> getChildItemList() {
        return mChilds;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
