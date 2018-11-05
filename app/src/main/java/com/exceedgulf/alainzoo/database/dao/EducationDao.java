package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.Attraction;
import com.exceedgulf.alainzoo.database.models.Education;

import java.util.List;

/**
 * Created by P.P on 3/1/2017.
 */
@Dao
public interface EducationDao {
       @Query("SELECT * from education where language = (:languagecode) ORDER BY id DESC")
    List<Education> getAll(final String languagecode);

    @Query("SELECT * from education where id = (:id) and language = (:languagecode)")
    Education getEducationsDetail(final Integer id, final String languagecode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<Education> educationList);

    @Query("select (CASE WHEN date((select date(creation_date) from education where language = (:languagecode) ORDER BY date(creation_date) ASC limit 1)) = date('now','localtime') THEN 1 ELSE 0 END) isolder")
    int isOlder(final String languagecode);
}
