package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.Avatars;
import com.exceedgulf.alainzoo.database.models.OpeningHours;

import java.util.List;

/**
 * Created by P.G on 01/09/2018.
 */
@Dao
public interface AvatarsDao {

    @Query("SELECT * from avatars")
    List<Avatars> getAllAvatars();

    @Query("SELECT Count(*) from avatars")
    int getAllAvatarsCount();

    @Query("SELECT * from avatars where id = (:id)")
    Avatars getAvatar(final int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<Avatars> avatarsList);


}
