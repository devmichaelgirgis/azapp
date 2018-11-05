package com.exceedgulf.alainzoo.managers;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.models.HappeningNow;
import com.exceedgulf.alainzoo.utils.NetUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P.S. on 12/02/18.
 */

public class HappeningNowManager {
    private static final String TAG = "HappeningNowManager";
    private static HappeningNowManager happeningNowManager;

    public static HappeningNowManager getHappeningNowManager() {
        if (happeningNowManager == null) {
            happeningNowManager = new HappeningNowManager();
        }
        return happeningNowManager;
    }

    public void getAllEntitiesData(final int pageNumber, final ApiCallback apiCallback, final boolean isHome) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getHappeningNow(new Callback<ArrayList<HappeningNow>>() {
                @Override
                public void onResponse(Call<ArrayList<HappeningNow>> call, Response<ArrayList<HappeningNow>> response) {
                    if (checkValidateResponce(response)) {
                        final ArrayList<HappeningNow> happeningNowArrayList = response.body();
                        apiCallback.onSuccess(happeningNowArrayList, false);
                    } else {
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<HappeningNow>> call, Throwable t) {
                    apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
    }

    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }
}
