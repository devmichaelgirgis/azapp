package com.exceedgulf.alainzoo.managers;

import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.models.OpeningHours;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P.G on 01/08/2018.
 */

public class OpeningHoursManager implements MainManager {
    private static final String TAG = "OpeningHoursManager";
    private static OpeningHoursManager openingHoursManager;

    public static OpeningHoursManager getOpeningHoursManager() {
        if (openingHoursManager == null) {
            openingHoursManager = new OpeningHoursManager();
        }
        return openingHoursManager;
    }

    @Override
    public void getAllEntitiesData(final int pageNumber, final ApiCallback apiCallback, final boolean isHome) {
        final ArrayList<OpeningHours> openingHoursArrayList = (ArrayList<OpeningHours>) getAllEntitiesFromDB(pageNumber);
        if (openingHoursArrayList.size() > 0 && !isOlderData()) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            apiCallback.onSuccess(openingHoursArrayList, false);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getOpeningHours(new Callback<ArrayList<OpeningHours>>() {
                    @Override
                    public void onResponse(Call<ArrayList<OpeningHours>> call, Response<ArrayList<OpeningHours>> response) {
                        if (checkValidateResponce(response)) {
                            final ArrayList<OpeningHours> openingHoursArrayListRes = response.body();
                            if (insertEntitiesToDB(openingHoursArrayListRes)) {
                                if (isHome) {
                                    apiCallback.onSuccess((ArrayList) getAllEntitiesFromDB(pageNumber), false);
                                } else {
                                    apiCallback.onSuccess(openingHoursArrayListRes, false);
                                }
                            }
                        } else {
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<OpeningHours>> call, Throwable t) {
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
        return null;
    }

    @Override
    public Object getEntityDetailsFromDB(Integer id) {
        return null;
    }

    @Override
    public boolean isOlderData() {
        final int value = alainZooDB.openingHoursDao().isOlder(LangUtils.getCurrentLanguage());
        return value == 0;
    }


    @Override
    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }


    @Override
    public boolean insertEntitiesToDB(List entities) {
        try {
            alainZooDB.openingHoursDao().insertOrReplaceAll(entities);
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
    public List<OpeningHours> getAllEntitiesFromDB(int pageNumber) {
        return alainZooDB.openingHoursDao().getAllOpeningHours(LangUtils.getCurrentLanguage());

    }

    @Override
    public List getTopThreeEntities() {
        return alainZooDB.openingHoursDao().getTopOpeningHours(LangUtils.getCurrentLanguage());
    }


}
