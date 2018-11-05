package com.exceedgulf.alainzoo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.exceedgulf.alainzoo.database.models.AboutTermsPrivacyModel;
import com.exceedgulf.alainzoo.database.models.Avatars;

import java.util.List;

/**
 * Created by P.G on 01/29/2018.
 */
@Dao
public interface AboutTermsPrimacyDao {

    @Query("SELECT * from about_terms_privacy where language = (:languagecode) and type = (:type)")
    List<AboutTermsPrivacyModel> getAll(final String languagecode, final int type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrReplaceAll(List<AboutTermsPrivacyModel> aboutTermsPrivacyModelList);


}
