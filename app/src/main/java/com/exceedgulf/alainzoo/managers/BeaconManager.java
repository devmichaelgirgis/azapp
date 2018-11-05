package com.exceedgulf.alainzoo.managers;

import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.database.models.AnimalsCategory;
import com.exceedgulf.alainzoo.database.models.BeaconModel;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P.P on 16/2/2018.
 */

public class BeaconManager {
    private static final String TAG = "BeaconManager";
    private static BeaconManager beaconManager;

    public static BeaconManager getBeaconManager() {
        if (beaconManager == null) {
            beaconManager = new BeaconManager();
        }
        return beaconManager;
    }

    public void getAllEntitiesData(final ApiCallback apiCallback) {
        final ArrayList<BeaconModel> beaconModelArrayList = (ArrayList<BeaconModel>) (getAllEntitiesFromDB());
        if (beaconModelArrayList.size() > 0 && !isOlderData()) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            apiCallback.onSuccess(beaconModelArrayList, false);
        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getAllBeacons(new Callback<ArrayList<BeaconModel>>() {
                    @Override
                    public void onResponse(Call<ArrayList<BeaconModel>> call, Response<ArrayList<BeaconModel>> response) {
                        if (checkValidateResponce(response)) {
                            final ArrayList<BeaconModel> beaconModels = response.body();
                            if (insertEntitiesToDB(beaconModels)) {
                                apiCallback.onSuccess(beaconModels, false);
                            } else {
                                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                            }
                        } else {
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<BeaconModel>> call, Throwable t) {
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
            AlainZooDB.getInstance().beaconsDao().insertOrReplaceAll(entities);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }

    }

    public List getAllEntitiesFromDB() {
        return AlainZooDB.getInstance().beaconsDao().getAll();
    }

    public boolean isOlderData() {
        final int value = AlainZooDB.getInstance().beaconsDao().isOlder(LangUtils.getCurrentLanguage());
        return value == 0;
    }
}
