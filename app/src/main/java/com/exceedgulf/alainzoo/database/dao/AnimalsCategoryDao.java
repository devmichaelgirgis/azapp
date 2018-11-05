package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.AnimalsCategory;

import java.util.List;

/**
 * Created by michael.george on 12/27/2017.
 */
@Dao
public interface AnimalsCategoryDao {

    @Query("SELECT * FROM animals_categories where language = (:languagecode)")
    List<AnimalsCategory> getAll(final String languagecode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<AnimalsCategory> animalsCategories);

}
