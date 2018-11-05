package com.exceedgulf.alainzoo.managers;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.FeedbackCategory;
import com.exceedgulf.alainzoo.models.FeedbackModel;
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
 * Created by Paras Ghasadiya on 12/01/18.
 */

public class FeedBackManager {
    private static FeedBackManager feedBackManager;

    public static FeedBackManager getFeedBackManager() {
        if (feedBackManager == null) {
            feedBackManager = new FeedBackManager();
        }
        return feedBackManager;
    }

    public void submitFeedback(final FeedbackModel feedbackModel, final ApiDetailCallback<String> stringApiDetailCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            final String url = LangUtils.getCurrentLanguage().equalsIgnoreCase("ar") ? Constants.VISITER_EDUCATION_FEEDBACK_INQUERYAR : Constants.VISITER_EDUCATION_FEEDBACK_INQUERYEN;

            ApiControllers.getApiControllers().postFeedback(url, feedbackModel, new Callback<ResponseBody>() {
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


    public void getFeedbackCategory(final ApiCallback apiCallback) {
        final ArrayList<FeedbackCategory> localFeedbackCategoryArrayList = (ArrayList<FeedbackCategory>) (checkEntitiesExistInCash());
        if (localFeedbackCategoryArrayList.size() > 0) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            apiCallback.onSuccess(localFeedbackCategoryArrayList, false);
        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getFeedbackCategory(new Callback<ArrayList<FeedbackCategory>>() {
                    @Override
                    public void onResponse(Call<ArrayList<FeedbackCategory>> call, Response<ArrayList<FeedbackCategory>> response) {
                        if (checkValidateResponce(response)) {
                            final ArrayList<FeedbackCategory> tmpFeedbackCategoryArrayList = response.body();
                            if (insertCategoryEntitiesToDB(tmpFeedbackCategoryArrayList)) {
                                apiCallback.onSuccess((ArrayList) checkEntitiesExistInCash(), false);
                            } else {
                                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                            }
                        } else {
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<FeedbackCategory>> call, Throwable t) {
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                });
            } else {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
        }
    }

    private boolean insertCategoryEntitiesToDB(List entities) {
        try {
            AlainZooDB.getInstance().feedBackCategoryDao().insertOrReplaceAll(entities);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    private boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }

    private List<FeedbackCategory> checkEntitiesExistInCash() {
        return AlainZooDB.getInstance().feedBackCategoryDao().getAllFeedbackCategories(LangUtils.getCurrentLanguage());
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
            stringApiDetailCallback.onSuccess(AppAlainzoo.getAppAlainzoo().getString(R.string.success_feedback));
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
