package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.Experience;

import java.util.List;

/**
 * Created by P.G on 12/26/2017.
 */
@Dao
public interface ExperienceDao {

    @Query("SELECT * from experiences where language = (:languagecode) ORDER BY id DESC limit 3 offset 0")
    List<Experience> getTopThreeExperiances(final String languagecode);

    @Query("SELECT * from experiences where id = (:id) and language = (:languagecode)")
    Experience getExperienceDetail(final Integer id, final String languagecode);

    @Query("SELECT * FROM experiences WHERE language IN (:languagecode) ORDER BY id DESC")
    List<Experience> getAll(final String languagecode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<Experience> experiences);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceSingle(Experience experiences);

    // 0 mean need to refresh data for updating
    @Query("select (CASE WHEN date((select date(creation_date) from experiences where language = (:languagecode) ORDER BY date(creation_date) ASC limit 1)) = date('now','localtime') THEN 1 ELSE 0 END) isolder")
    int isOlder(final String languagecode);
}
