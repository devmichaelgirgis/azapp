package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.FAQs;

import java.util.List;

/**
 * Created by michael.george on 12/27/2017.
 */
@Dao
public interface FAQsDao {

    @Query("SELECT * FROM faqs WHERE language =(:languagecode) AND category_id= (:cat_id) ")
    List<FAQs> getAll(String languagecode, Integer cat_id);

    @Query("SELECT * FROM faqs WHERE language =(:languagecode)")
    List<FAQs> getAllCategory(String languagecode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertorReplace(List<FAQs> faqs);

    @Delete
    void delete(FAQs faqs);
}
