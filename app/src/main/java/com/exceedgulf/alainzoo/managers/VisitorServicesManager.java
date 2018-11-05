package com.exceedgulf.alainzoo.managers;

import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.database.models.Experience;
import com.exceedgulf.alainzoo.database.models.VisitorService;
import com.exceedgulf.alainzoo.models.ServiceFormModel;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P.P on 03/01/2017.
 */

public class VisitorServicesManager implements MainManager {
    private static final String TAG = "ExperienceManager";
    private static final String VISITOR = "visitor_services";
    private static VisitorServicesManager visitorServicesManager;

    public static VisitorServicesManager getVisitorServicesManager() {
        if (visitorServicesManager == null) {
            visitorServicesManager = new VisitorServicesManager();
        }
        return visitorServicesManager;
    }

    @Override
    public void getAllEntitiesData(final int pageNumber, final ApiCallback apiCallback, final boolean isHome) {
        final ArrayList<VisitorService> visitorServiceArrayList = (ArrayList<VisitorService>) getAllEntitiesFromDB(pageNumber);
        if (visitorServiceArrayList.size() > 0 && !isOlderData()) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            //apiCallback.onSuccess(visitorServiceArrayList, visitorServiceArrayList.size() > 0);
            apiCallback.onSuccess(visitorServiceArrayList, false);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getAllVisitorServices(VISITOR, null, new Callback<ArrayList<VisitorService>>() {
                    @Override
                    public void onResponse(Call<ArrayList<VisitorService>> call, Response<ArrayList<VisitorService>> response) {
                        if (checkValidateResponce(response)) {
                            final ArrayList<VisitorService> visitorServices = response.body();
                            if (insertEntitiesToDB(visitorServices)) {
//                                apiCallback.onSuccess(visitorServices, visitorServices != null && visitorServices.size() > 0);
                                apiCallback.onSuccess(visitorServices, false);
                            }
                        } else {
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<VisitorService>> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                });
            } else {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
        }
    }

    @Override
    public void filterEntitiesData(int pageNumber, int categeoryId, ApiCallback apiCallback) {

    }

    @Override
    public Object getEntityDetails(final Integer id, final ApiDetailCallback apiDetailCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getExperiencesDetail(id, new Callback<ArrayList<Experience>>() {
                @Override
                public void onResponse(Call<ArrayList<Experience>> call, Response<ArrayList<Experience>> response) {
                    if (checkValidateResponce(response)) {
                        final ArrayList<Experience> experienceArrayList = response.body();
                        if (experienceArrayList != null && experienceArrayList.size() > 0) {
                            apiDetailCallback.onSuccess(experienceArrayList.get(0));
                        } else {
                            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Experience>> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                    apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });

        } else {
            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            return getEntityDetailsFromDB(id);
        }
        return null;
    }

    @Override
    public Object getEntityDetailsFromDB(Integer id) {
        return alainZooDB.visitorServiceDao().getVisitorServiceDetail(id, LangUtils.getCurrentLanguage());
    }

    @Override
    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }

    @Override
    public boolean insertEntitiesToDB(List entities) {
        try {
            alainZooDB.visitorServiceDao().insertOrReplaceAll(entities);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }

    }

    @Override
    public boolean insertEntityToDB(Object entity) {
        return false;
    }


    @Override
    public List<VisitorService> getAllEntitiesFromDB(int pageNumber) {
        //return alainZooDB.visitorServiceDao().getAllVisitorServices(LangUtils.getCurrentLanguage(), pageNumber);
        return alainZooDB.visitorServiceDao().getAll(LangUtils.getCurrentLanguage());
    }

    @Override
    public List getTopThreeEntities() {
        return null;
    }

    public void submitForm(final ServiceFormModel serviceFormModel, final ApiDetailCallback<String> apiDetailCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            final String url = LangUtils.getCurrentLanguage().equalsIgnoreCase("ar") ? Constants.VISITER_EDUCATION_FEEDBACK_INQUERYAR : Constants.VISITER_EDUCATION_FEEDBACK_INQUERYEN;
            ApiControllers.getApiControllers().postVisitorEducationInquiry(url, serviceFormModel, new Callback<ResponseBody>() {
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
                    Log.e("Failure", t.getMessage());
                    apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
    }


    private void checkIsSuccessResponse(final Response<ResponseBody> responseBodyResponse, final ApiDetailCallback<String> stringApiDetailCallback) {
        try {
            if (responseBodyResponse.isSuccessful()) {
                getResponseStatus(responseBodyResponse.body().string(), stringApiDetailCallback);
            } else {
                getResponseStatus(responseBodyResponse.errorBody().string(), stringApiDetailCallback);
            }
        } catch (Exception e) {
            e.printStackTrace();
            stringApiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
        }

    }

    private void getResponseStatus(final String responseString, ApiDetailCallback<String> stringApiDetailCallback) throws IOException, JSONException {
        final JSONObject jsonObject = new JSONObject(responseString);
        final String status = jsonObject.optString("status");
        if (status.equalsIgnoreCase("success")) {
            final JSONObject jsonObjectMessage = jsonObject.optJSONObject("messages");
            final String en = jsonObjectMessage.optString("en");
            final String ar = jsonObjectMessage.optString("ar");
            stringApiDetailCallback.onSuccess(AppAlainzoo.getAppAlainzoo().getString(R.string.success_form));
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


    @Override
    public boolean isOlderData() {
        final int value = alainZooDB.visitorServiceDao().isOlder(LangUtils.getCurrentLanguage());
        return value == 0;
    }
}
