package com.exceedgulf.alainzoo.managers;

import android.text.TextUtils;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.Family;
import com.exceedgulf.alainzoo.models.UserModel;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.NetUtil;
import com.google.gson.Gson;

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
 * Created by Paras Ghasadiya on 09/01/18.
 */

public class ProfileManager {

    private static ProfileManager profileManager;

    public static ProfileManager getProfileManager() {
        if (profileManager == null) {
            profileManager = new ProfileManager();
        }
        return profileManager;
    }

    public void fetchUserInformation(final ApiDetailCallback apiDetailCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getUserDetails(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (checkValidateResponce(response)) {
                        try {
                            final UserModel userModel = parseUserDataFromJson(response.body().string());
                            apiDetailCallback.onSuccess(userModel);
                        } catch (Exception e) {
                            e.printStackTrace();
                            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    } else {
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


    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }

    private UserModel parseUserDataFromJson(final String responseData) {
        try {

            final JSONObject jsonObjectUser = new JSONObject(responseData);
            final int uid = jsonObjectUser.optInt("uid");
            final String uuid = jsonObjectUser.optString("uuid");
            final String langcode = jsonObjectUser.optString("langcode");
            final String preferred_langcode = jsonObjectUser.optString("preferred_langcode");
            final String preferred_admin_langcode = jsonObjectUser.optString("preferred_admin_langcode");
            final String name = jsonObjectUser.optString("name");
            final String mail = jsonObjectUser.optString("mail");
            final String timezone = jsonObjectUser.optString("timezone");
            final String created = jsonObjectUser.optString("created");
            final String changed = jsonObjectUser.optString("changed");
            final String default_langcode = jsonObjectUser.optString("default_langcode");
            final String content_translation_source = jsonObjectUser.optString("content_translation_source");
            final String content_translation_outdated = jsonObjectUser.optString("content_translation_outdated");

            int target_id_contant_translate = 0;
            final JSONArray content_translation_uid = jsonObjectUser.optJSONArray("content_translation_uid");
            if (content_translation_uid.length() > 0) {
                target_id_contant_translate = content_translation_uid.optJSONObject(0).optInt("target_id");
            }
            final int content_translation_status = jsonObjectUser.optInt("content_translation_status");
            final long content_translation_created = jsonObjectUser.optLong("content_translation_created");
            int target_id_avatar = 0;
            final JSONArray field_avatar = jsonObjectUser.optJSONArray("field_avatar");
            if (field_avatar.length() > 0) {
                target_id_avatar = field_avatar.getJSONObject(0).optInt("target_id");
            }
            final JSONArray field_family_member = jsonObjectUser.optJSONArray("field_family_member");
            final Gson gson = new Gson();
            AlainZooDB.getInstance().familyDao().deleteAll();
            for (int i = 0; i < field_family_member.length(); i++) {
                final JSONArray field_family_member_inner = field_family_member.getJSONArray(i);
                try {
                    AlainZooDB.getInstance().familyDao().insertItem(gson.fromJson(field_family_member_inner.get(0).toString(), Family.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            final String field_date_of_bi = jsonObjectUser.optString("field_date_of_bi");
            final String field_emirate = jsonObjectUser.optString("field_emirate");
            final boolean isArrayCity = checkIsJsonArray(field_emirate);
            final String field_gender = jsonObjectUser.optString("field_gender");
            final String field_name = jsonObjectUser.optString("field_name");
            final String field_nationality = jsonObjectUser.optString("field_nationality");
            final boolean isArrayNatinality = checkIsJsonArray(field_nationality);
            final int field_points = jsonObjectUser.optInt("field_points");
            final int field_total_points = jsonObjectUser.optInt("field_total_points");


            final UserModel userModel = new UserModel();
            userModel.setUid(uid);
            userModel.setUuid(uuid);
            userModel.setLangcode(langcode);
            userModel.setPreferred_langcode(preferred_langcode);
            userModel.setPreferred_admin_langcode(preferred_admin_langcode);
            userModel.setName(name);
            userModel.setMail(mail);
            userModel.setTimezone(timezone);
            userModel.setCreated(created);
            userModel.setChanged(changed);
            userModel.setDefault_langcode(default_langcode);
            userModel.setContent_translation_source(content_translation_source);
            userModel.setContent_translation_outdated(content_translation_outdated);
            userModel.setTarget_id_contant_translate(target_id_contant_translate);
            userModel.setContent_translation_status(content_translation_status);
            userModel.setContent_translation_created(content_translation_created);
            userModel.setTarget_id_avatar(target_id_avatar);
            userModel.setField_date_of_bi(field_date_of_bi);
            userModel.setField_emirate(field_emirate);
            userModel.setField_gender(field_gender);
            userModel.setField_name(field_name);
            userModel.setField_nationality(field_nationality);
            userModel.setField_points(field_points);
            userModel.setField_total_points(field_total_points);
            if (isArrayCity) {
                final JSONArray jsonArrayCity = new JSONArray(field_emirate);
                if (jsonArrayCity.length() > 0) {
                    userModel.setCityId(jsonArrayCity.getJSONObject(0).optInt("target_id"));
                }
            }
            if (isArrayNatinality) {
                final JSONArray jsonArrayNatinality = new JSONArray(field_nationality);
                if (jsonArrayNatinality.length() > 0) {
                    userModel.setNationalityId(jsonArrayNatinality.getJSONObject(0).optInt("target_id"));
                }
            }

            return userModel;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private boolean checkIsJsonArray(final String data) {
        try {
            final JSONArray jsonArray = new JSONArray(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void updateProfile(final RequestBody requestBody, final ApiDetailCallback apiDetailCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().updateProfile(SharedPrefceHelper.getInstance().get(PrefCons.USER_UID, 0).toString(), requestBody, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (checkValidateResponce(response)) {
                        try {
                            final UserModel userModel = parseUserDataFromJson(response.body().string());
                            apiDetailCallback.onSuccess(userModel);
                        } catch (Exception e) {
                            e.printStackTrace();
                            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    } else {
                        try {
                            final JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            apiDetailCallback.onFaild(TextUtils.isEmpty(jsonObject.optString("message")) ? AppAlainzoo.getAppAlainzoo().getString(R.string.error) : jsonObject.optString("message"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
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


    public void changePassword(final RequestBody requestBody, final ApiDetailCallback apiDetailCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().changePassword(SharedPrefceHelper.getInstance().get(PrefCons.USER_UID, 0).toString(), requestBody, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (checkValidateResponce(response)) {
                        try {
                            final JSONObject jsonObject = new JSONObject(response.body().string());
                            final String status = jsonObject.optString("status");
                            if (status.equalsIgnoreCase("1")) {
                                apiDetailCallback.onSuccess(AppAlainzoo.getAppAlainzoo().getString(R.string.str_password_change_success));
                            } else {
                                apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    } else {
                        try {
                            final JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            apiDetailCallback.onFaild(TextUtils.isEmpty(jsonObject.optString("message")) ? AppAlainzoo.getAppAlainzoo().getString(R.string.error) : jsonObject.optString("message"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
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

}
