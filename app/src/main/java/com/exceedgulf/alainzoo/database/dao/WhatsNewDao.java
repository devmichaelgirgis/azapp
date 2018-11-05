package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.WhatsNew;

import java.util.List;

/**
 * Created by P.G on 12/26/2017.
 */
@Dao
public interface WhatsNewDao {


    @Query("SELECT * from whatsnews where language = (:languagecode) AND name = (:name) ORDER BY id DESC limit 10 offset (:pageNumber*10)")
    List<WhatsNew> getAllWhatsnews(final String languagecode, final int pageNumber, String name);

    @Query("SELECT * from Whatsnews where language = (:languagecode) ORDER BY id DESC limit 3 offset 0")
    List<WhatsNew> getTopThreeWhatsnews(final String languagecode);

    @Query("SELECT * FROM whatsnews WHERE language IN (:languagecode) ORDER BY id DESC")
    List<WhatsNew> getAll(final String languagecode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<WhatsNew> whatsnews);

    @Query("select (CASE WHEN date((select date(creation_date) from whatsnews where language = (:languagecode) ORDER BY date(creation_date) ASC limit 1)) = date('now','localtime') THEN 1 ELSE 0 END) isolder")
    int isOlder(final String languagecode);

    @Query("SELECT * from Whatsnews where id = (:id) and language = (:languagecode)")
    WhatsNew getWhatsNewDetail(int id, String languagecode);
}
