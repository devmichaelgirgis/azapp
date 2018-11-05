package com.exceedgulf.alainzoo.managers;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.models.Family;
import com.exceedgulf.alainzoo.utils.NetUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by indianic on 27/02/18.
 */

public class InviteManager {
    private static InviteManager inviteManager;

    public static InviteManager getInviteManager() {
        if (inviteManager == null) {
            inviteManager = new InviteManager();
        }
        return inviteManager;
    }

    public void submitInvite(final String requestMemberData, final ApiDetailCallback<Object> inviteCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            final RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestMemberData);
            ApiControllers.getApiControllers().createFamilyMember(body, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        checkIsSuccessResponse(response, inviteCallback);
                    } catch (Exception e) {
                        e.printStackTrace();
                        inviteCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    inviteCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            inviteCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
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
        if (jsonObject.optString("status").equalsIgnoreCase("success")) {
            final JSONObject jsonObjectmessages = jsonObject.optJSONObject("messages");
            final String en = jsonObjectmessages.optString("en");
            final String ar = jsonObjectmessages.optString("ar");
            final String id = jsonObject.optString("data");
            final Family family = new Family();
            family.setId(Integer.valueOf(id));
            family.setName(Locale.getDefault().toLanguageTag().equals("ar") ? ar : en);
            apiDetailCallback.onSuccess(family);
        } else {
            final String message = jsonObject.optString("message");
            apiDetailCallback.onFaild(message);
        }
    }

    public String createMamberRequestData(final String name, final String relative, final String gender) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("relative", relative);
        jsonObject.put("gender", gender);
        return jsonObject.toString();
    }
}
