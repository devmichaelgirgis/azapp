package com.exceedgulf.alainzoo.managers;

import android.util.Log;
import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.ShuttleService;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by michael.george on 12/27/2017.
 */

public class ShuttleServiceManager{
    private static final String TAG = "ShuttleServiceManager";
    private static ShuttleServiceManager shuttleServiceManager;

    public static ShuttleServiceManager getShuttleServiceManager() {
        if (shuttleServiceManager == null) {
            shuttleServiceManager = new ShuttleServiceManager();
        }
        return shuttleServiceManager;
    }

    public void getAllEntitiesData(final int pageNumber, final ApiCallback apiCallback, final boolean isHome) {
        final ArrayList<ShuttleService> shuttleServiceArrayList = (ArrayList<ShuttleService>) (getAllEntitiesFromDB(pageNumber));
        if (shuttleServiceArrayList.size() > 0 && !isOlderData()) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            // apiCallback.onSuccess(animalList, animalList.size() > 0);
            apiCallback.onSuccess(shuttleServiceArrayList, false);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getShuttleServiceList(null, new Callback<ArrayList<ShuttleService>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ShuttleService>> call, Response<ArrayList<ShuttleService>> response) {
                        if (checkValidateResponce(response)) {
                            final ArrayList<ShuttleService> shuttleServices = response.body();
                            if (insertEntitiesToDB(shuttleServices)) {
                                //apiCallback.onSuccess(animalList, animalList != null && animalList.size() > 0);
                                apiCallback.onSuccess(shuttleServices, false);
                            } else {
                                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                            }
                        } else {
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<ShuttleService>> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                });
            } else {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
        }
    }
    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }

    public boolean insertEntitiesToDB(List entities) {
        try {
            AlainZooDB.getInstance().shuttleServiceDao().insertOrReplaceAll(entities);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    public List getAllEntitiesFromDB(int pageNumber) {
        return AlainZooDB.getInstance().shuttleServiceDao().getAllShuttleService();
    }

    public boolean isOlderData() {
        final int value = AlainZooDB.getInstance().shuttleServiceDao().isOlder(LangUtils.getCurrentLanguage());
        return value == 0;
    }
}
