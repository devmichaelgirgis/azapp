package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.Nationalities;

import java.util.List;

/**
 * Created by P.G on 01/29/2018.
 */
@Dao
public interface NationalitiesDao {

    @Query("SELECT * FROM nationalities WHERE language =(:languagecode) ")
    List<Nationalities> getNationalitiesEntity(String languagecode);

    @Query("SELECT * FROM nationalities WHERE language =(:languagecode) and id =(:id)")
    Nationalities getNationalities(final String languagecode, final int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplace(List<Nationalities> nationalitiesList);


}
