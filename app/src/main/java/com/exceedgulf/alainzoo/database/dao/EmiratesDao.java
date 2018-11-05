package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.Emirates;

import java.util.List;

/**
 * Created by P.G on 01/29/2018.
 */
@Dao
public interface EmiratesDao {

    @Query("SELECT * FROM emirates WHERE language =(:languagecode) ")
    List<Emirates> getEmiratesEntity(String languagecode);


    @Query("SELECT * FROM emirates WHERE language =(:languagecode) and id =(:id)")
    Emirates getEmirates(final String languagecode, final int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplace(List<Emirates> emiratesList);


}
