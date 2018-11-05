package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.MyPlaneVisitedItem;

import java.util.List;

/**
 * Created by P.G on 02/28/2018.
 */
@Dao
public interface MyPlaneVisitedItemDao {

    @Insert
    void insertItem(MyPlaneVisitedItem myPlaneVisitedItem);

    @Query("DELETE FROM myplanevisiteditem where visitedItemId = (:id) and visitedItemType = (:visitedItemType)")
    void deleteItem(final int id, final String visitedItemType);

    @Query("select count(*) from myplanevisiteditem where visitedItemId = (:id) and visitedItemType = (:visitedItemType)")
    int isVisited(final int id, final String visitedItemType);

    @Query("DELETE FROM myplanevisiteditem")
    void deleteAll();
}
