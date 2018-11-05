package com.exceedgulf.alainzoo.managers;

import android.text.TextUtils;
import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.ExploreMap;
import com.exceedgulf.alainzoo.database.models.ExploreZoo;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P.P on 2/02/2018.
 */

public class ExploreMapManager {
    private static final String TAG = "ExploreMapManager";
    private static ExploreMapManager exploreMapManager;

    public static ExploreMapManager getExploreMapManager() {
        if (exploreMapManager == null) {
            exploreMapManager = new ExploreMapManager();
        }
        return exploreMapManager;
    }

    public void getAllEntitiesData(final ApiDetailCallback apiCallback) {
//        final ExploreMap exploreMap = getAllEntitiesFromDB();
//        if (exploreMap != null && !isOlderData()) {
//            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
//                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
//            }
//            apiCallback.onSuccess(exploreMap);
//        } else {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getAllExploreMapFeatures(new Callback<ExploreMap>() {
                @Override
                public void onResponse(Call<ExploreMap> call, Response<ExploreMap> response) {
                    if (checkValidateResponce(response)) {
                        final ExploreMap exploreMap = response.body();
                        //if (insertEntitiesToDB(exploreMap)) {
                        apiCallback.onSuccess(exploreMap);
                        //}
                    } else {
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ExploreMap> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                    apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }

        //}
    }

    public void getAllExploreZoo(final Integer id, final String categoryId, final String type, final ApiCallback apiCallback) {
        final ArrayList<ExploreZoo> exploreZooArrayList = (ArrayList<ExploreZoo>) getAllEntitiesFromDB(id, categoryId, type);
        if (exploreZooArrayList != null && !isOlderData()) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            apiCallback.onSuccess(exploreZooArrayList, false);
        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getAllExploreZoo(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        checkAndValidateResponse(id, response, categoryId, type, apiCallback);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                });
            } else {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
        }

    }

    private void checkAndValidateResponse(final Integer itemId, final Response<ResponseBody> responseBodyResponse, final String categoryId, final String type, final ApiCallback apiCallback) {

        if (responseBodyResponse.isSuccessful()) {
            try {
                final String responseString = responseBodyResponse.body().string();
                final JSONObject jsonObject = new JSONObject(responseString);
                final JSONArray jsonArray = jsonObject.optJSONArray("features");
                final ArrayList<ExploreZoo> exploreZooArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    final JSONObject jsonObjectChild = jsonArray.optJSONObject(i);
                    final JSONObject properties = jsonObjectChild.optJSONObject("properties");
                    final int id = properties.optInt("id");
                    final String area = properties.optString("area");
                    final String category = properties.optString("category");
                    final String name = properties.optString("name");
                    final String types = properties.optString("type");
                    final String icon = properties.optString("icon");
                    final String thumbnail = properties.optString("thumbnail");
                    final String path = properties.optString("path");
                    final String body = properties.optString("body");
                    final JSONObject geometry = jsonObjectChild.optJSONObject("geometry");
                    final JSONArray coordinates = geometry.optJSONArray("coordinates");
                    String latitude = "";
                    String longitude = "";
                    try {
                        if (coordinates.length() > 0) {
                            latitude = (String) coordinates.get(1);
                            longitude = (String) coordinates.get(0);
                        }
                    } catch (Exception e) {
                    }
                    final ExploreZoo exploreZoo = new ExploreZoo();
                    exploreZoo.setId(id);
                    exploreZoo.setArea(area);
                    exploreZoo.setCategory(category);
                    exploreZoo.setName(name);
                    exploreZoo.setType(types);
                    exploreZoo.setIcon(icon);
                    exploreZoo.setThumbnail(thumbnail);
                    exploreZoo.setPath(path);
                    exploreZoo.setBody(body);
                    exploreZoo.setLatitude(latitude);
                    exploreZoo.setLongitude(longitude);
                    exploreZooArrayList.add(exploreZoo);
                }
                deleteAll();
                if (insertEntitiesToDB(exploreZooArrayList)) {
                    apiCallback.onSuccess((ArrayList) getAllEntitiesFromDB(itemId, categoryId, type), false);

                } else {
                    apiCallback.onSuccess(exploreZooArrayList, false);
                }


            } catch (Exception e) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
            }
        } else {
            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
        }


    }


    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }

    public boolean insertEntitiesToDB(List entities) {
        try {
            AlainZooDB.getInstance().exploreZooDao().insertOrReplaceAll(entities);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }

    }

    public List getAllEntitiesFromDB(final Integer id, final String categoryId, final String type) {
        if (id != null && !TextUtils.isEmpty(type)) {
            return AlainZooDB.getInstance().exploreZooDao().findByID(LangUtils.getCurrentLanguage(), id, type);
        } else if (!TextUtils.isEmpty(categoryId) && !TextUtils.isEmpty(type)) {
            return AlainZooDB.getInstance().exploreZooDao().findByIDAndType(LangUtils.getCurrentLanguage(), categoryId, type);
        } else if (TextUtils.isEmpty(categoryId) && !TextUtils.isEmpty(type)) {
            return AlainZooDB.getInstance().exploreZooDao().findByType(LangUtils.getCurrentLanguage(), type);
        } else {
            return AlainZooDB.getInstance().exploreZooDao().getAll(LangUtils.getCurrentLanguage());
        }
    }

    public int getCountFromDB() {
        return AlainZooDB.getInstance().exploreZooDao().getTotalCount(LangUtils.getCurrentLanguage());
    }


    public void deleteAll() {
        AlainZooDB.getInstance().exploreZooDao().deleteAll();
    }

    public boolean isOlderData() {
        final int value = AlainZooDB.getInstance().exploreZooDao().isOlder(LangUtils.getCurrentLanguage());
        return value == 0;
    }

}
