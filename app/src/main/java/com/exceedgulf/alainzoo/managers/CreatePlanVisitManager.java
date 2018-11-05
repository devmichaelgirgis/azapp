package com.exceedgulf.alainzoo.managers;

import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.models.Emirates;
import com.exceedgulf.alainzoo.database.models.Nationalities;
import com.exceedgulf.alainzoo.models.UserModel;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created on 10/01/18.
 */

public class CreatePlanVisitManager implements MainManager {

    private static final String TAG = "CreatePlanVisitManager";
    private static CreatePlanVisitManager createPlanVisitManager;

    public static CreatePlanVisitManager createPlanVisitManager() {
        if (createPlanVisitManager == null) {
            createPlanVisitManager = new CreatePlanVisitManager();
        }
        return createPlanVisitManager;
    }

    public void postCreatePlan(final RequestBody requestBody, final ApiDetailCallback apiCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().postCreatePlan(requestBody, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        checkIsSuccessResponse(response, apiCallback);
                    } catch (Exception e) {
                        e.printStackTrace();
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                    apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
    }

    private void checkIsSuccessResponse(final Response<ResponseBody> responseBodyResponse, final ApiDetailCallback apiDetailCallback) {
        try {
            if (responseBodyResponse.isSuccessful()) {
                getResponseStatus(responseBodyResponse.body().string(), apiDetailCallback);
            } else if (responseBodyResponse.code() == 403) {
                apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
            } else if (responseBodyResponse.code() == 422) {
                getResponseStatus(responseBodyResponse.errorBody().string(), apiDetailCallback);
            } else {
                getResponseStatus(responseBodyResponse.errorBody().string(), apiDetailCallback);
            }
        } catch (Exception e) {
            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
        }

    }

    private void getResponseStatus(final String responseString, ApiDetailCallback apiDetailCallback) throws IOException, JSONException {
        final JSONObject jsonObject = new JSONObject(responseString);
        final String status = jsonObject.optString("status");
        if (status.equalsIgnoreCase("success")) {
            final JSONObject jsonObjectMessage = jsonObject.optJSONObject("messages");
            final String en = jsonObjectMessage.optString("en");
            final String ar = jsonObjectMessage.optString("ar");
            final String data = jsonObject.optString("data");
            //apiDetailCallback.onSuccess(LangUtils.getCurrentLanguage().equalsIgnoreCase("ar") ? ar : en);
            apiDetailCallback.onSuccess(data);
        } else if (status.equalsIgnoreCase("failed")) {
            final JSONObject jsonObjectMessage = jsonObject.optJSONObject("messages");
            if (jsonObjectMessage.has("en")) {
                final String en = jsonObjectMessage.optString("en");
                final String ar = jsonObjectMessage.optString("ar");
                apiDetailCallback.onFaild(LangUtils.getCurrentLanguage().equalsIgnoreCase("ar") ? ar : en);
            } else {
                final Iterator<?> keys = jsonObjectMessage.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (jsonObjectMessage.get(key) instanceof JSONObject) {
                        final JSONObject mess = (JSONObject) jsonObjectMessage.get(key);
                        final String en = mess.optString("en");
                        final String ar = mess.optString("ar");
                        apiDetailCallback.onFaild(LangUtils.getCurrentLanguage().equalsIgnoreCase("ar") ? ar : en);
                        break;
                    }
                }
            }
        }
    }

    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }

    @Override
    public boolean insertEntitiesToDB(List entities) {
        return false;
    }

    @Override
    public List getAllEntitiesFromDB(int pageNumber) {
        return null;
    }

    @Override
    public boolean insertEntityToDB(Object entity) {
        return false;
    }


    @Override
    public List getTopThreeEntities() {
        return null;
    }

    @Override
    public void getAllEntitiesData(int pageNumber, ApiCallback apiCallback, boolean isHome) {

    }

    @Override
    public void filterEntitiesData(int pageNumber, int categeoryId, ApiCallback apiCallback) {

    }

    @Override
    public Object getEntityDetails(Integer id, ApiDetailCallback apiDetailCallback) {
        return null;
    }

    @Override
    public Object getEntityDetailsFromDB(Integer id) {
        return null;
    }

    @Override
    public boolean isOlderData() {
        return false;
    }
}
