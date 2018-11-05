package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.ExploreZoo;

import java.util.List;

/**
 * Created by P.P.
 */
@Dao
public interface ExploreZooDao {

    @Query("SELECT * FROM explorezoo where language = (:languagecode) ORDER BY id DESC ")
    List<ExploreZoo> getAll(final String languagecode);

    @Query("SELECT * FROM explorezoo where language = (:languagecode) and id = (:id) and type = (:type) ORDER BY id DESC ")
    List<ExploreZoo> findByID(final String languagecode, final Integer id, final String type);

    @Query("SELECT * FROM explorezoo where language = (:languagecode) and category = (:id) and type = (:type) ORDER BY id DESC ")
    List<ExploreZoo> findByIDAndType(final String languagecode, final String id, final String type);

    @Query("SELECT * FROM explorezoo where language = (:languagecode) and type = (:type) ORDER BY id DESC ")
    List<ExploreZoo> findByType(final String languagecode, final String type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<ExploreZoo> exploreZooList);

    @Query("select (CASE WHEN date((select date(creation_date) from explorezoo where language = (:languagecode) ORDER BY date(creation_date) ASC limit 1)) = date('now','localtime') THEN 1 ELSE 0 END) isolder")
    int isOlder(final String languagecode);

    @Query("Delete from explorezoo")
    void deleteAll();

    @Query("select count(*) from explorezoo where language = (:languagecode)")
    int getTotalCount(final String languagecode);

}
