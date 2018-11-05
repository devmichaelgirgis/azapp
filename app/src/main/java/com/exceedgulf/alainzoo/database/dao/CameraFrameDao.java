package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.Avatars;
import com.exceedgulf.alainzoo.database.models.CameraFrame;

import java.util.List;

/**
 * Created by P.G on 03/07/2018.
 */
@Dao
public interface CameraFrameDao {

    @Query("SELECT * from camera_frame orderd ORDER BY id ASC")
    List<CameraFrame> getAllFrames();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<CameraFrame> avatarsList);


}
