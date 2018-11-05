package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.Contactus;
import java.util.List;

/**
 * Created by P.G on 01/29/2018.
 */
@Dao
public interface ContactusDao {

    @Query("SELECT * FROM contat_us WHERE language =(:languagecode) ")
    List<Contactus> getContactusEntity(String languagecode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertorReplace(List<Contactus> contactUsModelArrayList);

    @Query("select (CASE WHEN date((select date(creation_date) from contat_us where language = (:languagecode) ORDER BY date(creation_date) ASC limit 1)) = date('now','localtime') THEN 1 ELSE 0 END) isolder")
    int isOlder(final String languagecode);
}
