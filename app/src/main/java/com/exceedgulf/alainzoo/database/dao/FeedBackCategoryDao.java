package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.Avatars;
import com.exceedgulf.alainzoo.database.models.FeedbackCategory;

import java.util.List;

/**
 * Created by P.G on 01/09/2018.
 */
@Dao
public interface FeedBackCategoryDao {

    @Query("SELECT * from feedback_category where language = (:languagecode) order by id asc")
    List<FeedbackCategory> getAllFeedbackCategories(final String languagecode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<FeedbackCategory> feedbackCategoryList);


}
