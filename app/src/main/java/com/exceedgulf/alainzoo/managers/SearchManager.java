package com.exceedgulf.alainzoo.managers;

import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.models.SearchDataModel;
import com.exceedgulf.alainzoo.utils.NetUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P.P on 13/02/2018.
 */

public class SearchManager {
    private static final String TAG = "SearchManager";
    private static SearchManager searchManager;

    public static SearchManager getSearchManager() {
        if (searchManager == null) {
            searchManager = new SearchManager();
        }
        return searchManager;
    }


    public void getAllEntitiesData(final int page, final String title, final String type, final ApiCallback apiCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().postSearch(page, title, type, new Callback<ArrayList<SearchDataModel>>() {
                @Override
                public void onResponse(Call<ArrayList<SearchDataModel>> call, Response<ArrayList<SearchDataModel>> response) {
                    if (checkValidateResponce(response)) {
                        final ArrayList<SearchDataModel> searchDataModels = response.body();
                        apiCallback.onSuccess(searchDataModels, searchDataModels != null && searchDataModels.size() > 0 ? true : false);
                    } else {
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<SearchDataModel>> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
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
