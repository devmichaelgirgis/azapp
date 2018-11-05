package com.exceedgulf.alainzoo.managers;

import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.models.Attraction;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P.G on 29/12/2017.
 */

public class AttractionsManager implements MainManager {
    private static final String TAG = "AttractionsManager";
    private static AttractionsManager attractionsManager;

    public static AttractionsManager getAttractionManager() {
        if (attractionsManager == null) {
            attractionsManager = new AttractionsManager();
        }
        return attractionsManager;
    }

    @Override
    public void getAllEntitiesData(final int pageNumber, final ApiCallback apiCallback, final boolean isHome) {
        final ArrayList<Attraction> experienceList = (ArrayList<Attraction>) (isHome ? getTopThreeEntities() : getAllEntitiesFromDB(pageNumber));
        if (experienceList.size() > 0 && !isOlderData()) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            // apiCallback.onSuccess(experienceList, experienceList.size() > 0);
            apiCallback.onSuccess(experienceList, false);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getAttractions(pageNumber, new Callback<ArrayList<Attraction>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Attraction>> call, Response<ArrayList<Attraction>> response) {
                        if (checkValidateResponce(response)) {
                            final ArrayList<Attraction> experienceList = response.body();
                            if (insertEntitiesToDB(experienceList)) {
                                if (isHome) {
                                    apiCallback.onSuccess((ArrayList) getTopThreeEntities(), false);
                                } else {
                                    //apiCallback.onSuccess(experienceList, experienceList != null && experienceList.size() > 0);
                                    apiCallback.onSuccess(experienceList, false);
                                }
                            }
                        } else {
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Attraction>> call, Throwable t) {
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
            ApiControllers.getApiControllers().getAttractionsDetail(id, new Callback<ArrayList<Attraction>>() {
                @Override
                public void onResponse(Call<ArrayList<Attraction>> call, Response<ArrayList<Attraction>> response) {
                    if (checkValidateResponce(response)) {
                        final ArrayList<Attraction> attractionArrayList = response.body();
                        if (attractionArrayList != null && attractionArrayList.size() > 0) {
                            apiDetailCallback.onSuccess(attractionArrayList.get(0));
                        } else {
                            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Attraction>> call, Throwable t) {
                    apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });

        } else {
            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));

        }
        return null;
    }

    @Override
    public Object getEntityDetailsFromDB(Integer id) {
        return alainZooDB.attractionsDao().getAttractionsDetail(id, LangUtils.getCurrentLanguage());
    }

    @Override
    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }


    @Override
    public boolean insertEntitiesToDB(List entities) {
        try {
            alainZooDB.attractionsDao().insertOrReplaceAll(entities);
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
    public List<Attraction> getAllEntitiesFromDB(int pageNumber) {
        //return alainZooDB.attractionsDao().getAllAttractionss(LangUtils.getCurrentLanguage(), pageNumber);
        return alainZooDB.attractionsDao().getAll(LangUtils.getCurrentLanguage());

    }

    @Override
    public List getTopThreeEntities() {
        return alainZooDB.attractionsDao().getTopThreeAttractions(LangUtils.getCurrentLanguage());
    }

    @Override
    public boolean isOlderData() {
        final int value = alainZooDB.attractionsDao().isOlder(LangUtils.getCurrentLanguage());
        return value == 0;
    }


}
