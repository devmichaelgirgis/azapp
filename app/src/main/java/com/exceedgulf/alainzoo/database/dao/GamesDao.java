package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.Games;

import java.util.List;

/**
 * Created by P.G on 01/09/2018.
 */
@Dao
public interface GamesDao {

    @Query("SELECT * from games where language = (:languagecode)")
    List<Games> getAllGames(final String languagecode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<Games> gamesList);


}
