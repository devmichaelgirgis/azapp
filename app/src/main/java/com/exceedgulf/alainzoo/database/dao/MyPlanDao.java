package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.models.GeneralPlan;
import com.exceedgulf.alainzoo.models.RecommendedPlanModel;

import java.util.List;

/**
 * Created by P.G on 01/09/2018.
 */
@Dao
public interface MyPlanDao {

    @Query("SELECT * from myplan")
    List<RecommendedPlanModel> getAllMyplan();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<RecommendedPlanModel> recommendedPlanModel);

    @Query("DELETE from myplan")
    void deleteAllMyplan();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(GeneralPlan generalPlans);

    @Query("DELETE from generalplan")
    void deleteGeneral();

    @Query("SELECT * from generalplan")
    List<GeneralPlan> getGeneralplan();
}
