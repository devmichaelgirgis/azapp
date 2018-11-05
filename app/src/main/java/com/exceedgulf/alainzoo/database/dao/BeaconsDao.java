package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.database.models.BeaconModel;

import java.util.List;

/**
 * Created by ehab.alagoza on 12/26/2017.
 */
@Dao
public interface BeaconsDao {

    @Query("SELECT * FROM beacons")
    List<BeaconModel> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<BeaconModel> animals);

    @Query("select (CASE WHEN date((select date(creation_date) from beacons where language = (:languagecode) ORDER BY date(creation_date) ASC limit 1)) = date('now','localtime') THEN 1 ELSE 0 END) isolder")
    int isOlder(final String languagecode);
}
