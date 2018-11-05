package com.exceedgulf.alainzoo.managers;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.utils.NetUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Paras Ghasadiya on 05/02/18.
 */

public class SosManager {
    private static SosManager sosManager;

    public static SosManager getSosManager() {
        if (sosManager == null) {
            sosManager = new SosManager();
        }
        return sosManager;
    }

    public void postSosRequest(final String sosBody, final ApiDetailCallback apiDetailCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            final RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), sosBody);
            ApiControllers.getApiControllers().postSosRequest(body, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        checkIsSuccessResponse(response, apiDetailCallback);
                    } catch (Exception e) {
                        e.printStackTrace();
                        apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
    }

    private void checkIsSuccessResponse(final Response<ResponseBody> responseBodyResponse, final ApiDetailCallback apiDetailCallback) {
        try {
            if (responseBodyResponse.isSuccessful()) {
                getResponseStatus(responseBodyResponse.body().string(), apiDetailCallback);
            } else if (responseBodyResponse.code() == 403) {
                final JSONObject jsonObject = new JSONObject(responseBodyResponse.errorBody().string());
                final String message = jsonObject.optString("message");
                apiDetailCallback.onFaild(message);
            } else {
                getResponseStatus(responseBodyResponse.errorBody().string(), apiDetailCallback);
            }
        } catch (Exception e) {
            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
        }

    }

    private void getResponseStatus(final String responseString, ApiDetailCallback apiDetailCallback) throws IOException, JSONException {
        final JSONObject jsonObject = new JSONObject(responseString);
        if (jsonObject.has("field_status")) {
            apiDetailCallback.onSuccess(AppAlainzoo.getAppAlainzoo().getString(R.string.sos_success));
        } else {
            final String message = jsonObject.optString("message");
            apiDetailCallback.onFaild(message);
        }
    }


    public String sosRequestData(final String title, final String field_type, final Double latitude, final Double longitude, final String field_description, final String types) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", getRequestJsonArray("value", title));
        jsonObject.put("field_type", getRequestJsonArray("value", field_type));
        jsonObject.put("field_latitude", getRequestJsonArray("value", latitude));
        jsonObject.put("field_longitude", getRequestJsonArray("value", longitude));
        jsonObject.put("field_description", getRequestJsonArray("value", field_description));
        jsonObject.put("type", getRequestJsonArray("target_id", types));
        return jsonObject.toString();
    }

    private JSONArray getRequestJsonArray(final String key, final Object value) throws JSONException {
        final JSONArray jsonArray = new JSONArray();
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, value);
        jsonArray.put(jsonObject);
        return jsonArray;

    }

}
