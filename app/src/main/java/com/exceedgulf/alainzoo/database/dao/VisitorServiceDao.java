package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.VisitorService;

import java.util.List;

/**
 * Created by P.P on 3/1/2017.
 */
@Dao
public interface VisitorServiceDao {
    @Query("SELECT * from visitor_services where language = (:languagecode) ORDER BY id DESC")
    List<VisitorService> getAll(final String languagecode);


    @Query("SELECT * from visitor_services where id = (:id) and language = (:languagecode)")
    VisitorService getVisitorServiceDetail(final Integer id, final String languagecode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<VisitorService> visitorServiceList);

    @Query("select (CASE WHEN date((select date(creation_date) from visitor_services where language = (:languagecode) ORDER BY date(creation_date) ASC limit 1)) = date('now','localtime') THEN 1 ELSE 0 END) isolder")
    int isOlder(final String languagecode);
}
