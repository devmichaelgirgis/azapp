package com.exceedgulf.alainzoo.managers;

import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.models.Avatars;
import com.exceedgulf.alainzoo.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Paras Ghasadiya on 01/09/18.
 */

public class AvatarsManager implements MainManager {
    private final String TAG = AvatarsManager.class.getSimpleName();
    private static AvatarsManager avatarsManager;

    public static AvatarsManager getAvatarsManager() {
        if (avatarsManager == null) {
            avatarsManager = new AvatarsManager();
        }
        return avatarsManager;
    }

    public void getAvatarsAllEntry(final ApiDetailCallback avatarsApiDetailCallback) {
        final ArrayList<Avatars> avatarsArrayList = (ArrayList<Avatars>) getTopThreeEntities();
        if (avatarsArrayList.size() > 0) {
            avatarsApiDetailCallback.onSuccess(avatarsArrayList);
        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getAvatars(new Callback<ArrayList<Avatars>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Avatars>> call, Response<ArrayList<Avatars>> response) {
                        if (checkValidateResponce(response)) {
                            final List<Avatars> avatarsArrayList = response.body();
                            if (insertEntitiesToDB(avatarsArrayList)) {
                                avatarsApiDetailCallback.onSuccess(avatarsArrayList);
                            } else {
                                avatarsApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                            }
                        } else {
                            avatarsApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Avatars>> call, Throwable t) {
                        avatarsApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                });

            } else {
                avatarsApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }

        }
    }

    public void getAvatarsSingleEntry(final int id, final ApiDetailCallback avatarsApiDetailCallback) {
        if (getTopThreeEntities().size() > 0) {
            avatarsApiDetailCallback.onSuccess(getEntityDetailsFromDB(id));
        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getAvatars(new Callback<ArrayList<Avatars>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Avatars>> call, Response<ArrayList<Avatars>> response) {
                        if (checkValidateResponce(response)) {
                            final List<Avatars> avatarsArrayList = response.body();
                            if (insertEntitiesToDB(avatarsArrayList)) {
                                avatarsApiDetailCallback.onSuccess(getEntityDetailsFromDB(id));
                            } else {
                                avatarsApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                            }
                        } else {
                            avatarsApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Avatars>> call, Throwable t) {
                        avatarsApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                });

            } else {
                avatarsApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }

        }
    }

    @Override
    public boolean checkValidateResponce(Response response) {
        return response != null &&  response.body() != null && response.isSuccessful();
    }

    @Override
    public boolean insertEntitiesToDB(List entities) {
        try {
            alainZooDB.avatarsDao().insertOrReplaceAll(entities);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }


    }

    @Override
    public Object getEntityDetailsFromDB(Integer id) {
        return alainZooDB.avatarsDao().getAvatar(id);
    }

    @Override
    public boolean isOlderData() {
        return false;
    }

    @Override
    public boolean insertEntityToDB(Object entity) {
        return false;
    }

    @Override
    public List getTopThreeEntities() {
        return alainZooDB.avatarsDao().getAllAvatars();
    }

    @Override
    public List getAllEntitiesFromDB(int pageNumber) {
        return null;
    }

    @Override
    public void filterEntitiesData(int pageNumber, int categeoryId, ApiCallback apiCallback) {

    }

    @Override
    public Object getEntityDetails(Integer id, ApiDetailCallback apiDetailCallback) {
        return null;
    }

    @Override
    public void getAllEntitiesData(int pageNumber, ApiCallback apiCallback, boolean isHome) {

    }

}
