package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.database.models.ShuttleService;

import java.util.List;

/**
 * Created by P.S. on 16/02/18.
 */
@Dao
public interface ShuttleServiceDao {

    @Query("SELECT * FROM shuttle")
    List<ShuttleService> getAllShuttleService();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<ShuttleService> shuttleServices);

    @Query("select (CASE WHEN date((select date(creation_date) from shuttle where language = (:languagecode) ORDER BY date(creation_date) ASC limit 1)) = date('now','localtime') THEN 1 ELSE 0 END) isolder")
    int isOlder(final String languagecode);
}
