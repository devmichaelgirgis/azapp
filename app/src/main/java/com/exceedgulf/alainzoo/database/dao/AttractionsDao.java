package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.database.models.Attraction;

import java.util.List;

/**
 * Created by P.G on 29/12/2017.
 */
@Dao
public interface AttractionsDao {
    @Query("SELECT * from attraction where language = (:languagecode)")
    List<Attraction> getAll(final String languagecode);

    @Query("SELECT * from attraction where language = (:languagecode) limit 3 offset 0")
    List<Attraction> getTopThreeAttractions(final String languagecode);

    @Query("SELECT * from attraction where id = (:id) and language = (:languagecode)")
    Attraction getAttractionsDetail(final Integer id, final String languagecode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<Attraction> attractionList);

    @Query("select (CASE WHEN date((select date(creation_date) from attraction where language = (:languagecode) ORDER BY date(creation_date) ASC limit 1)) = date('now','localtime') THEN 1 ELSE 0 END) isolder")
    int isOlder(final String languagecode);
}
