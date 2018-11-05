package com.exceedgulf.alainzoo.managers;

import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.models.Education;
import com.exceedgulf.alainzoo.database.models.Experience;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ehab.alagoza on 12/27/2017.
 */

public class EducationManager implements MainManager {
    private static final String TAG = "ExperienceManager";
    private static final String EDUCATIONS = "educations";
    private static EducationManager educationManager;

    public static EducationManager getEducationManager() {
        if (educationManager == null) {
            educationManager = new EducationManager();
        }
        return educationManager;
    }

    @Override
    public void getAllEntitiesData(final int pageNumber, final ApiCallback apiCallback, final boolean isHome) {
        final ArrayList<Education> educationArrayList = (ArrayList<Education>) getAllEntitiesFromDB(pageNumber);
        if (educationArrayList.size() > 0 && !isOlderData()) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            // apiCallback.onSuccess(educationArrayList, educationArrayList.size() > 0);
            apiCallback.onSuccess(educationArrayList, false);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getAllEducation(EDUCATIONS, null, new Callback<ArrayList<Education>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Education>> call, Response<ArrayList<Education>> response) {
                        if (checkValidateResponce(response)) {
                            final ArrayList<Education> experienceList = response.body();
                            if (insertEntitiesToDB(experienceList)) {
//                                apiCallback.onSuccess(experienceList, experienceList != null && experienceList.size() > 0);
                                apiCallback.onSuccess(experienceList, false);
                            }
                        } else {
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Education>> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                });
            } else {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
        }
    }

    @Override
    public void filterEntitiesData(int pageNumber, int categeoryId, ApiCallback apiCallback) {

    }

    @Override
    public Object getEntityDetails(final Integer id, final ApiDetailCallback apiDetailCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getExperiencesDetail(id, new Callback<ArrayList<Experience>>() {
                @Override
                public void onResponse(Call<ArrayList<Experience>> call, Response<ArrayList<Experience>> response) {
                    if (checkValidateResponce(response)) {
                        final ArrayList<Experience> experienceArrayList = response.body();
                        if (experienceArrayList != null && experienceArrayList.size() > 0) {
                            apiDetailCallback.onSuccess(experienceArrayList.get(0));
                        } else {
                            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Experience>> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                    apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });

        } else {
            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            return getEntityDetailsFromDB(id);
        }
        return null;
    }

    @Override
    public Object getEntityDetailsFromDB(Integer id) {
        return alainZooDB.educationDao().getEducationsDetail(id, LangUtils.getCurrentLanguage());
    }

    @Override
    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }

    @Override
    public boolean insertEntitiesToDB(List entities) {
        try {
            alainZooDB.educationDao().insertOrReplaceAll(entities);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }

    }

    @Override
    public boolean insertEntityToDB(Object entity) {
        return false;
    }


    @Override
    public List<Education> getAllEntitiesFromDB(int pageNumber) {
        //return alainZooDB.educationDao().getAllEducations(LangUtils.getCurrentLanguage(), pageNumber);
        return alainZooDB.educationDao().getAll(LangUtils.getCurrentLanguage());
    }

    @Override
    public List getTopThreeEntities() {
        return null;
    }


    @Override
    public boolean isOlderData() {
        final int value = alainZooDB.educationDao().isOlder(LangUtils.getCurrentLanguage());
        return value == 0;
    }

}
