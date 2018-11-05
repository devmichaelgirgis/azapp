package com.exceedgulf.alainzoo.managers;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P.P on 17/01/18.
 */

public class LoginZooManager {
    private static LoginZooManager loginZooManager;

    public static LoginZooManager getLoginZooManager() {
        if (loginZooManager == null) {
            loginZooManager = new LoginZooManager();
        }
        return loginZooManager;
    }

    public void userlogin(final RequestBody requestBody, final ApiDetailCallback<String> stringApiDetailCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().userLogin(requestBody, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        checkIsSuccessResponse(response, stringApiDetailCallback);
                    } catch (Exception e) {
                        e.printStackTrace();
                        stringApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    stringApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            stringApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
    }

    public void resetPassword(final String userName, final ApiDetailCallback<String> stringApiDetailCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            final RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), createReserPassReq(userName));
            ApiControllers.getApiControllers().resetPassword(body, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful()) {
                            checkResetPassworStatus(response.body().string(), stringApiDetailCallback);
                        } else {
                            checkResetPassworStatus(response.errorBody().string(), stringApiDetailCallback);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        stringApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    stringApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            stringApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }

    }

    private void checkResetPassworStatus(final String responseString, ApiDetailCallback<String> stringApiDetailCallback) throws JSONException {
        final JSONObject jsonObject = new JSONObject(responseString);
        final String status = jsonObject.optString("status");
        final JSONObject jsonObjectmessages = jsonObject.optJSONObject("messages");
        final String en = jsonObjectmessages.optString("en");
        final String ar = jsonObjectmessages.optString("ar");
        if (status.equalsIgnoreCase("success")) {
            stringApiDetailCallback.onSuccess(LangUtils.getCurrentLanguage().equalsIgnoreCase("ar") ? ar : en);
        } else {
            stringApiDetailCallback.onFaild(LangUtils.getCurrentLanguage().equalsIgnoreCase("ar") ? ar : en);
        }

    }

    private String createReserPassReq(final String userName) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", userName);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}";
        }

    }

    private void checkIsSuccessResponse(final Response<ResponseBody> responseBodyResponse, final ApiDetailCallback<String> stringApiDetailCallback) {
        try {
            if (responseBodyResponse.isSuccessful()) {
                getSuccessOrFailMessage(responseBodyResponse.body().string(), stringApiDetailCallback);
            } else {
                getSuccessOrFailMessage(responseBodyResponse.errorBody().string(), stringApiDetailCallback);
            }
        } catch (Exception e) {
            e.printStackTrace();
            stringApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
        }

    }

    private void getSuccessOrFailMessage(final String responseString, ApiDetailCallback<String> stringApiDetailCallback) throws IOException, JSONException {
        final JSONObject jsonObject = new JSONObject(responseString);
        final String status = jsonObject.optString("status");
        if (status.equalsIgnoreCase("success")) {
            final JSONObject jsonObjectMessage = jsonObject.optJSONObject("messages");
            final String en = jsonObjectMessage.optString("en");
            final String ar = jsonObjectMessage.optString("ar");
            final JSONObject data = jsonObject.getJSONObject("data");
            final JSONObject current_user = data.getJSONObject("current_user");
            final int uid = current_user.optInt("uid");
            final int name = current_user.optInt("name");
            final String csrf_token = data.optString("csrf_token");
            final String logout_token = data.optString("logout_token");

            SharedPrefceHelper.getInstance().save(PrefCons.USER_UID, uid);
            SharedPrefceHelper.getInstance().save(PrefCons.CSRF_TOKEN, csrf_token);
            SharedPrefceHelper.getInstance().save(PrefCons.LOGOUT_TOKEN, logout_token);

            stringApiDetailCallback.onSuccess(LangUtils.getCurrentLanguage().equalsIgnoreCase("ar") ? ar : en);
        } else if (status.equalsIgnoreCase("failed")) {
            final JSONObject jsonObjectMessage = jsonObject.optJSONObject("messages");
            if (isJsonObject(jsonObjectMessage.optString("en"))) {
                stringApiDetailCallback.onFaild(LangUtils.getCurrentLanguage().equalsIgnoreCase("ar") ? getResponseMessage(jsonObjectMessage.optJSONObject("ar")) : getResponseMessage(jsonObjectMessage.optJSONObject("en")));
            } else {
                final String en = jsonObjectMessage.optString("en");
                final String ar = jsonObjectMessage.optString("ar");
                stringApiDetailCallback.onFaild(LangUtils.getCurrentLanguage().equalsIgnoreCase("ar") ? ar : en);
            }
        }
    }

    private boolean isJsonObject(final String data) {
        try {
            new JSONObject(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getResponseMessage(final JSONObject jsonObject) {
        String message = "";
        final Iterator<?> i = jsonObject.keys();
        do {
            final String key = i.next().toString();
            message = jsonObject.optString(key);

        } while (i.hasNext());
        return message;
    }
}
