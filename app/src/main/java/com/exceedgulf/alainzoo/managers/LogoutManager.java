package com.exceedgulf.alainzoo.managers;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Data.UrlsUtils;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.models.FeedbackModel;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P.P on 17/01/18.
 */

public class LogoutManager {
    private static LogoutManager logoutManager;

    public static LogoutManager logoutManager() {
        if (logoutManager == null) {
            logoutManager = new LogoutManager();
        }
        return logoutManager;
    }

    public void logout(final ApiDetailCallback<String> stringApiDetailCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().postLogout(new Callback<ResponseBody>() {
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
                    DisplayDialog.getInstance().dismissProgressDialog();
                    stringApiDetailCallback.onFaild(t.getMessage());
                }
            });
        } else {
            stringApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
    }

    private void checkIsSuccessResponse(final Response<ResponseBody> responseBodyResponse, final ApiDetailCallback<String> stringApiDetailCallback) {
        try {
            if (responseBodyResponse.isSuccessful()) {
                final String responseString = responseBodyResponse.body().string();
                final JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.has("status")) {
                    final String status = jsonObject.optString("status");
                    if (status.equalsIgnoreCase("success")) {
                        final JSONObject jsonObjectMessage = jsonObject.optJSONObject("messages");
                        final String en = jsonObjectMessage.optString("en");
                        final String ar = jsonObjectMessage.optString("ar");
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
            } else {
                final JSONObject jsonErrorObject = new JSONObject(responseBodyResponse.errorBody().string());
                stringApiDetailCallback.onFaild(jsonErrorObject.optString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            stringApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
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
