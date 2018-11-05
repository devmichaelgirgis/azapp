package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.database.models.ExploreZoo;
import com.exceedgulf.alainzoo.database.models.Family;
import com.exceedgulf.alainzoo.database.models.MyPlaneVisitedItem;

import java.util.List;

/**
 * Created by P.P on 01/03/2018.
 */
@Dao
public interface FamilyDao {

    @Insert
    void insertItem(Family family);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<Family> families);

//    @Query("SELECT * FROM family where language = (:languagecode) ORDER BY id DESC ")
//    List<Family> getAll(final String languagecode);

    @Query("SELECT * FROM family ORDER BY id DESC ")
    List<Family> getAll();

    @Query("DELETE FROM family")
    void deleteAll();

}
