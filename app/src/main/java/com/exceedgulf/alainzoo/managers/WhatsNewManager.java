package com.exceedgulf.alainzoo.managers;

import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.models.WhatsNew;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by michael.george on 12/28/2017.
 */

public class WhatsNewManager implements MainManager {
    private static final String TAG = "WhatsnewManager";
    private static WhatsNewManager whatsnewManager;

    public static WhatsNewManager getWhatsnewManager() {
        if (whatsnewManager == null) {
            whatsnewManager = new WhatsNewManager();
        }
        return whatsnewManager;
    }

    @Override
    public void getAllEntitiesData(final int pageNumber, final ApiCallback apiCallback, final boolean isHome) {
        final ArrayList<WhatsNew> whatsnewList = (ArrayList<WhatsNew>) (isHome ? getTopThreeEntities() : getAllEntitiesFromDB(pageNumber));
        if (whatsnewList.size() > 0 && !isOlderData()) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            apiCallback.onSuccess(whatsnewList, false);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getWhatsNew(new Callback<ArrayList<WhatsNew>>() {
                    @Override
                    public void onResponse(Call<ArrayList<WhatsNew>> call, Response<ArrayList<WhatsNew>> response) {
                        if (checkValidateResponce(response)) {
                            final ArrayList<WhatsNew> whatsnewList = response.body();
                            if (insertEntitiesToDB(whatsnewList)) {
                                if (isHome) {
                                    apiCallback.onSuccess((ArrayList) getTopThreeEntities(), false);
                                } else {
                                    apiCallback.onSuccess(whatsnewList, false);
                                }
                            }
                        } else {
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<WhatsNew>> call, Throwable t) {
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                });
            } else {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
        }
    }


    @Override
    public Object getEntityDetails(Integer id, ApiDetailCallback apiDetailCallback) {
        return null;
    }

    @Override
    public Object getEntityDetailsFromDB(Integer id) {
        return alainZooDB.whatsNewDao().getWhatsNewDetail(id, LangUtils.getCurrentLanguage());
    }

    @Override
    public void filterEntitiesData(int pageNumber, int categeoryId, ApiCallback apiCallback) {

    }

    public List<WhatsNew> getEntitiesByname(int pageNumber, String name) {
        return alainZooDB.whatsNewDao().getAllWhatsnews(LangUtils.getCurrentLanguage(), pageNumber, name);
    }

    @Override
    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }


    @Override
    public boolean insertEntitiesToDB(List entities) {
        try {
            alainZooDB.whatsNewDao().insertOrReplaceAll(entities);
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
    public List<WhatsNew> getAllEntitiesFromDB(int pageNumber) {
        return alainZooDB.whatsNewDao().getAll(LangUtils.getCurrentLanguage());

    }

    @Override
    public List getTopThreeEntities() {
        return alainZooDB.whatsNewDao().getTopThreeWhatsnews(LangUtils.getCurrentLanguage());
    }

    @Override
    public boolean isOlderData() {
        final int value = alainZooDB.whatsNewDao().isOlder(LangUtils.getCurrentLanguage());
        return value == 0;
    }

}
