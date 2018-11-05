package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.OpeningHours;
import com.exceedgulf.alainzoo.models.OpeningHourFront;

import java.util.List;

/**
 * Created by P.P on 21/02/2018.
 */
@Dao
public interface OpeningHoursFrontDao {

    @Query("SELECT * from opening_hours_front where language = (:languagecode) order by id asc")
    List<OpeningHourFront> getAllOpeningHours(final String languagecode);

    @Query("SELECT * from opening_hours_front where language = (:languagecode) order by id desc")
    List<OpeningHourFront> getDescAllOpeningHours(final String languagecode);

    @Query("SELECT * from opening_hours_front where language = (:languagecode) limit 2 offset 1")
    List<OpeningHourFront> getTopOpeningHours(final String languagecode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<OpeningHourFront> openingHoursList);

    @Delete
    void delete(OpeningHourFront openingHours);

    @Query("Delete from opening_hours_front")
    void deleteAll();


}
