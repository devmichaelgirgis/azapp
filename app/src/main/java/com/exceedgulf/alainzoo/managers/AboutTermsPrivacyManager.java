package com.exceedgulf.alainzoo.managers;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.AboutTermsPrivacyModel;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Paras Ghasadiya on 29/01/18.
 */

public class AboutTermsPrivacyManager {

    // Values for fetching data from local db
    // Type=1 About Us
    // Type=2 Terms Condition
    // Type=1 PrivacyPolicy

    private static AboutTermsPrivacyManager aboutUSManager;

    public static AboutTermsPrivacyManager getAboutUSManager() {
        if (aboutUSManager == null) {
            aboutUSManager = new AboutTermsPrivacyManager();
        }
        return aboutUSManager;
    }

    public void getAboutUs(final ApiCallback apiCallback) {
        final ArrayList<AboutTermsPrivacyModel> aboutTermsPrivacyModelArrayList = (ArrayList<AboutTermsPrivacyModel>) (getAllEntitiesFromDB(1));
        if (aboutTermsPrivacyModelArrayList.size() > 0) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            apiCallback.onSuccess(aboutTermsPrivacyModelArrayList, false);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getPagesContant(Constants.DOMAIN_URL + LangUtils.getCurrentLanguage() + Constants.GET_ABOUT_US, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        checkIsSuccessResponse(response, apiCallback, 1);
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

    public void getDoAndDoNot(final ApiCallback apiCallback) {
        final ArrayList<AboutTermsPrivacyModel> aboutTermsPrivacyModelArrayList = (ArrayList<AboutTermsPrivacyModel>) (getAllEntitiesFromDB(4));
        if (aboutTermsPrivacyModelArrayList.size() > 0) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            apiCallback.onSuccess(aboutTermsPrivacyModelArrayList, false);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getPagesContant(Constants.DOMAIN_URL + LangUtils.getCurrentLanguage() + Constants.GET_DO_AND_DO_NOT, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        checkIsSuccessResponse(response, apiCallback, 4);
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

    public void getTermsCondition(final ApiCallback apiCallback) {
        final ArrayList<AboutTermsPrivacyModel> aboutTermsPrivacyModelArrayList = (ArrayList<AboutTermsPrivacyModel>) (getAllEntitiesFromDB(2));
        if (aboutTermsPrivacyModelArrayList.size() > 0) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            apiCallback.onSuccess(aboutTermsPrivacyModelArrayList, false);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getPagesContant(Constants.DOMAIN_URL + LangUtils.getCurrentLanguage() + Constants.GET_TERMS, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        checkIsSuccessResponse(response, apiCallback, 2);
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

    public void getPrivacyPolicy(final ApiCallback apiCallback) {
        final ArrayList<AboutTermsPrivacyModel> aboutTermsPrivacyModelArrayList = (ArrayList<AboutTermsPrivacyModel>) (getAllEntitiesFromDB(3));
        if (aboutTermsPrivacyModelArrayList.size() > 0) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            apiCallback.onSuccess(aboutTermsPrivacyModelArrayList, false);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getPagesContant(Constants.DOMAIN_URL + LangUtils.getCurrentLanguage() + Constants.GET_PRIVACY, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        checkIsSuccessResponse(response, apiCallback, 3);
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

    private void checkIsSuccessResponse(final Response<ResponseBody> responseBodyResponse, final ApiCallback apiCallback, final int type) {
        try {
            if (responseBodyResponse.isSuccessful()) {
                final ArrayList<AboutTermsPrivacyModel> aboutTermsPrivacyModelArrayList = new ArrayList<>();
                final JSONArray jsonArray = new JSONArray(responseBodyResponse.body().string());
                final JSONObject jsonObject = jsonArray.getJSONObject(0);
                final int id = jsonObject.optInt("id");
                final String titleMain = jsonObject.optString("title");
                final JSONArray sections = jsonObject.optJSONArray("sections");
                for (int i = 0; i < sections.length(); i++) {
                    final JSONObject object = sections.getJSONObject(i);
                    final String title = object.optString("title");
                    final String details = object.optString("details");
                    final String image = object.optString("image");
                    final AboutTermsPrivacyModel aboutTermsPrivacyModel = new AboutTermsPrivacyModel();
                    aboutTermsPrivacyModel.setType(type);
                    aboutTermsPrivacyModel.setTitle_main(titleMain);
                    //aboutTermsPrivacyModel.setId(id);
                    aboutTermsPrivacyModel.setId(getRandomNumber());
                    aboutTermsPrivacyModel.setTitle(title);
                    aboutTermsPrivacyModel.setDetails(details);
                    aboutTermsPrivacyModel.setImage(image);
                    aboutTermsPrivacyModelArrayList.add(aboutTermsPrivacyModel);

                }
                apiCallback.onSuccess(aboutTermsPrivacyModelArrayList, false);
                AlainZooDB.getInstance().aboutTermsPrimacyDao().insertOrReplaceAll(aboutTermsPrivacyModelArrayList);

            } else {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
            }
        } catch (Exception e) {
            e.printStackTrace();
            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
        }

    }

    private int getRandomNumber() {
        //return (new Random()).nextInt((max - min) + 1) + min;
        return (new Random()).nextInt((1000) + 1);
    }

    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }

    public List getAllEntitiesFromDB(int type) {
        // type : 1 - About Us 2 - Terms 3 - Privacy 4 - Do and Do Not
        return AlainZooDB.getInstance().aboutTermsPrimacyDao().getAll(LangUtils.getCurrentLanguage(), type);
    }
}
