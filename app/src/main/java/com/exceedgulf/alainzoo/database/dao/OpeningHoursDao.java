package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.OpeningHours;

import java.util.List;

/**
 * Created by P.G on 01/08/2018.
 */
@Dao
public interface OpeningHoursDao {

    @Query("SELECT * from openinghours where language = (:languagecode)")
    List<OpeningHours> getAllOpeningHours(final String languagecode);


   // @Query("SELECT * from openinghours where language = (:languagecode) order by id ASC limit 2 offset 1")
    @Query("SELECT * from openinghours where language = (:languagecode) limit 2 offset 1")
    List<OpeningHours> getTopOpeningHours(final String languagecode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<OpeningHours> openingHoursList);

    @Delete
    void delete(OpeningHours openingHours);

    @Query("select (CASE WHEN date((select date(creation_date) from openinghours where language = (:languagecode) ORDER BY date(creation_date) ASC limit 1)) = date('now','localtime') THEN 1 ELSE 0 END) isolder")
    int isOlder(final String languagecode);
}
