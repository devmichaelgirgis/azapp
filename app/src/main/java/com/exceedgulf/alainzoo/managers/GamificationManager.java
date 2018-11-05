package com.exceedgulf.alainzoo.managers;

import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.database.models.TreasureHuntStatus;
import com.exceedgulf.alainzoo.models.UserModel;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created on 10/01/18.
 */

public class GamificationManager {

    private static final String TAG = "GamificationManager";
    private static GamificationManager gamificationManager;

    public String REGISTRATION = "registration";
    public String SHARE_APP = "share_app";
    public String PLAN_VISIT = "plan_visit";
    public String SHARE_CONTENT = "share_content";
    public String TRASURE_HUNT = "treasure_hunt";
    public String HINT = "hint";
    public String DOWNLOADED_GAME = "downloaded_game";

    public static GamificationManager getGamificationManager() {
        if (gamificationManager == null) {
            gamificationManager = new GamificationManager();
        }
        return gamificationManager;
    }

    public void postCreateGamification(final RequestBody requestBody, final ApiDetailCallback apiCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            if (SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn()) {
                ApiControllers.getApiControllers().postCreateGamification(requestBody, new Callback<ResponseBody>() {
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
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                });
            } else {
                // alert to show if user is not logged in
            }
        } else {
            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
    }

    public void getTreasureStatus(int id, final ApiCallback apiCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getTreasureHuntStatus(id, new Callback<ArrayList<TreasureHuntStatus>>() {
                @Override
                public void onResponse(Call<ArrayList<TreasureHuntStatus>> call, Response<ArrayList<TreasureHuntStatus>> response) {
                    if (checkValidateResponce(response)) {
                        final ArrayList<TreasureHuntStatus> treasureHuntStatuses = response.body();
                        apiCallback.onSuccess(treasureHuntStatuses, false);
                    } else {
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<TreasureHuntStatus>> call, Throwable t) {
                    apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
    }

    public RequestBody createRequestBody(final String type, final String actionId) {
        RequestBody body = null;
        if (type.equalsIgnoreCase(REGISTRATION) ||type.equalsIgnoreCase(SHARE_APP) || type.equalsIgnoreCase(SHARE_CONTENT) || type.equalsIgnoreCase(PLAN_VISIT) || type.equalsIgnoreCase(DOWNLOADED_GAME)) {
            final Map<String, Object> map = new HashMap<>();
            map.put(Constants.GAMIFICATION_TYPE, type);
            //map.put(Constants.GAMIFICATION_POINTS, point);
            //map.put(Constants.GAMIFICATION_USER, SharedPrefceHelper.getInstance().get(PrefCons.USER_UID));
            map.put(Constants.GAMIFICATION_ACTION_ID, actionId);
            return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(map)).toString());
        }
        return null;
    }

    public RequestBody createTreasureRequestBody(final String type, final String point, final int treasureHunt, final int hint) {
        RequestBody body = null;
        if (type.equalsIgnoreCase(TRASURE_HUNT)) {
            final Map<String, Object> map = new HashMap<>();
            map.put(Constants.GAMIFICATION_TYPE, type);
            map.put(Constants.GAMIFICATION_POINTS, point);
            map.put(Constants.GAMIFICATION_TREASURE_HUNT, treasureHunt);
            return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(map)).toString());
        } else if (type.equalsIgnoreCase(HINT)) {
            final Map<String, Object> map = new HashMap<>();
            map.put(Constants.GAMIFICATION_TYPE, type);
            map.put(Constants.GAMIFICATION_POINTS, point);
            map.put(Constants.GAMIFICATION_TREASURE_HUNT, treasureHunt);
            map.put(Constants.GAMIFICATION_HINT, hint);
            return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(map)).toString());
        }
        return null;
    }

    private void checkIsSuccessResponse(final Response<ResponseBody> responseBodyResponse, final ApiDetailCallback apiDetailCallback) {
        try {
            if (responseBodyResponse.isSuccessful()) {
                getResponseStatus(responseBodyResponse.body().string(), apiDetailCallback);
            } else if (responseBodyResponse.code() == 403) {
                apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
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
            apiDetailCallback.onSuccess(LangUtils.getCurrentLanguage().equalsIgnoreCase("ar") ? ar : en);
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

    public enum GamificationType {
        REGISTRATION, SHARE_APP, PLAN_VISIT, SHARE_CONTENT,
        TRASURE_HUNT, HINT, DOWNLOADED_GAME
    }

}
