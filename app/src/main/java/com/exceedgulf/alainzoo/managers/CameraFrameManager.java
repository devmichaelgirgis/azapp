package com.exceedgulf.alainzoo.managers;

import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.models.Avatars;
import com.exceedgulf.alainzoo.database.models.CameraFrame;
import com.exceedgulf.alainzoo.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Paras Ghasadiya on 03/07/18.
 */

public class CameraFrameManager implements MainManager {
    private final String TAG = CameraFrameManager.class.getSimpleName();
    private static CameraFrameManager cameraFrameManager;

    public static CameraFrameManager getCameraFrameManager() {
        if (cameraFrameManager == null) {
            cameraFrameManager = new CameraFrameManager();
        }
        return cameraFrameManager;
    }

    public void getAllCameraFramesEntry(final ApiDetailCallback avatarsApiDetailCallback) {
        final ArrayList<CameraFrame> avatarsArrayList = (ArrayList<CameraFrame>) getTopThreeEntities();
        if (avatarsArrayList.size() > 0) {
            avatarsApiDetailCallback.onSuccess(avatarsArrayList);
        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getCameraFrames(new Callback<ArrayList<CameraFrame>>() {
                    @Override
                    public void onResponse(Call<ArrayList<CameraFrame>> call, Response<ArrayList<CameraFrame>> response) {
                        if (checkValidateResponce(response)) {
                            final List<CameraFrame> avatarsArrayList = response.body();
                            if (insertEntitiesToDB(avatarsArrayList)) {
                                avatarsApiDetailCallback.onSuccess((ArrayList<CameraFrame>) getTopThreeEntities());
                            } else {
                                avatarsApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                            }
                        } else {
                            avatarsApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<CameraFrame>> call, Throwable t) {
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
        return response != null && response.body() != null && response.isSuccessful();
    }

    @Override
    public boolean insertEntitiesToDB(List entities) {
        try {
            alainZooDB.cameraFrameDao().insertOrReplaceAll(entities);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }


    }

    @Override
    public List getTopThreeEntities() {
        return alainZooDB.cameraFrameDao().getAllFrames();
    }

    @Override
    public Object getEntityDetailsFromDB(Integer id) {
        return null;
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
