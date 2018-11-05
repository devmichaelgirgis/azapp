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
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
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

public class RegistrationManager implements MainManager {

    private static final String TAG = "RegistrationManager";
    private static RegistrationManager registrationManager;

    public static RegistrationManager getRegistrationManager() {
        if (registrationManager == null) {
            registrationManager = new RegistrationManager();
        }
        return registrationManager;
    }

    public void postRegister(final RequestBody requestBody, final ApiDetailCallback apiCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().postRegister(requestBody, new Callback<ResponseBody>() {
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
            final UserModel userModel = parseUserDataFromJson(jsonObject.optString("data"));
            if (userModel != null) {
                SharedPrefceHelper.getInstance().save(PrefCons.IS_LOGGEDIN, true);
                AppAlainzoo.getAppAlainzoo().setTempLoggedIn(false);
                SharedPrefceHelper.getInstance().save(PrefCons.USER_DOB, userModel.getField_date_of_bi());
                SharedPrefceHelper.getInstance().save(PrefCons.USER_EMIRATE, userModel.getField_emirate());
                SharedPrefceHelper.getInstance().save(PrefCons.USER_GENDER, userModel.getField_gender());
                SharedPrefceHelper.getInstance().save(PrefCons.USER_MOBILE, userModel.getField_mobile());
                SharedPrefceHelper.getInstance().save(PrefCons.USER_FIELD_NAME, userModel.getField_name());
                SharedPrefceHelper.getInstance().save(PrefCons.USER_NATIONALITY, userModel.getField_nationality());
                SharedPrefceHelper.getInstance().save(PrefCons.USER_LANG_CODE, userModel.getLangcode());
                SharedPrefceHelper.getInstance().save(PrefCons.USER_EMAIL, userModel.getMail());
                SharedPrefceHelper.getInstance().save(PrefCons.USER_NAME, userModel.getName());
                SharedPrefceHelper.getInstance().save(PrefCons.USER_AVATAR_ID, userModel.getTarget_id_avatar());
                SharedPrefceHelper.getInstance().save(PrefCons.USER_UID, userModel.getUid());
                SharedPrefceHelper.getInstance().save(PrefCons.USER_UUID, userModel.getUuid());
                SharedPrefceHelper.getInstance().save(PrefCons.USER_NATIONALITY_ID, userModel.getNationalityId());
                SharedPrefceHelper.getInstance().save(PrefCons.USER_EMIRATE_ID, userModel.getCityId());
                SharedPrefceHelper.getInstance().save(PrefCons.USER_POINTS, userModel.getField_points());
                SharedPrefceHelper.getInstance().save(PrefCons.USER_TOTAL_POINTS, userModel.getField_total_points());
            }
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

    private UserModel parseUserDataFromJson(final String responseData) {
        try {
            final JSONObject jsonObjectUser = new JSONObject(responseData);
            final String uid = getValue(jsonObjectUser.optString("uid"));
            final String uuid = getValue(jsonObjectUser.optString("uuid"));
            final String langcode = getValue(jsonObjectUser.optString("langcode"));
            final String preferred_langcode = getValue(jsonObjectUser.optString("preferred_langcode"));
            final String preferred_admin_langcode = getValue(jsonObjectUser.optString("preferred_admin_langcode"));
            final String name = getValue(jsonObjectUser.optString("name"));
            final String mail = getValue(jsonObjectUser.optString("mail"));
            final String timezone = getValue(jsonObjectUser.optString("timezone"));
            final String created = getValue(jsonObjectUser.optString("created"));
            final String changed = getValue(jsonObjectUser.optString("changed"));
            final String default_langcode = getValue(jsonObjectUser.optString("default_langcode"));
            final String content_translation_source = getValue(jsonObjectUser.optString("content_translation_source"));
            final String content_translation_outdated = getValue(jsonObjectUser.optString("content_translation_outdated"));

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
            final String field_date_of_bi = getValue(jsonObjectUser.optString("field_date_of_bi"));
            final String field_emirate = jsonObjectUser.optString("field_emirate");
            final String field_gender = getValue(jsonObjectUser.optString("field_gender"));
            final String field_mobile_number = getValue(jsonObjectUser.optString("field_mobile_number"));
            final String field_name = getValue(jsonObjectUser.optString("field_name"));
            final String field_nationality = jsonObjectUser.optString("field_nationality");
            final UserModel userModel = new UserModel();
            userModel.setUid(Integer.parseInt(uid));
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
            userModel.setField_mobile(field_mobile_number);
            userModel.setField_nationality(field_nationality);
            final boolean isArrayCity = checkIsJsonArray(field_emirate);
            if (isArrayCity) {
                final JSONArray jsonArrayCity = new JSONArray(field_emirate);
                if (jsonArrayCity.length() > 0) {
                    userModel.setCityId(jsonArrayCity.getJSONObject(0).optInt("target_id"));
                }
            }
            final boolean isArrayNatinality = checkIsJsonArray(field_nationality);
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
            new JSONArray(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getValue(final String resposedata) {
        try {
            final JSONArray jsonArray = new JSONArray(resposedata);
            if (jsonArray.length() > 0) {
                final JSONObject jsonObject = jsonArray.getJSONObject(0);
                return getValueFromObject(jsonObject);
            }
        } catch (Exception e) {
        }

        return "";
    }

    private String getValueFromObject(final JSONObject jsonObject) {
        String message = "";
        final Iterator<?> i = jsonObject.keys();
        do {
            final String key = i.next().toString();
            message = jsonObject.optString(key);

        } while (i.hasNext());
        return message;
    }

    public void getEmirates(final ApiCallback apiCallback) {
        final ArrayList<Emirates> emiratesArrayList = (ArrayList<Emirates>) (getAllEntitiesFromDB(0));
        if (emiratesArrayList.size() > 0) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            apiCallback.onSuccess(emiratesArrayList, false);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getEmirates(new Callback<ArrayList<Emirates>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Emirates>> call, Response<ArrayList<Emirates>> response) {
                        if (checkValidateResponce(response)) {
                            final ArrayList<Emirates> tmpEmiratesArrayList = response.body();
                            if (insertEntitiesToDB(tmpEmiratesArrayList)) {
                                apiCallback.onSuccess(tmpEmiratesArrayList, false);
                            } else {
                                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                            }
                        } else {
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Emirates>> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                });
            } else {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
        }
    }

    public void getNationalities(final ApiCallback apiCallback) {
        final ArrayList<Nationalities> nationalitiesArrayList = (ArrayList<Nationalities>) (getTopThreeEntities());
        if (nationalitiesArrayList.size() > 0) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            apiCallback.onSuccess(nationalitiesArrayList, false);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getNationalities(new Callback<ArrayList<Nationalities>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Nationalities>> call, Response<ArrayList<Nationalities>> response) {
                        if (checkValidateResponce(response)) {
                            final ArrayList<Nationalities> tmpNationalitiesArrayList = response.body();
                            if (insertEntityToDB(tmpNationalitiesArrayList)) {
                                apiCallback.onSuccess(tmpNationalitiesArrayList, false);
                            } else {
                                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                            }
                        } else {
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Nationalities>> call, Throwable t) {
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
    public boolean insertEntitiesToDB(List entities) {
        try {
            alainZooDB.emiratesDao().insertOrReplace(entities);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    @Override
    public List getAllEntitiesFromDB(int pageNumber) {
        return alainZooDB.emiratesDao().getEmiratesEntity(LangUtils.getCurrentLanguage());
    }

    @Override
    public boolean insertEntityToDB(Object entity) {
        try {
            alainZooDB.nationalitiesDao().insertOrReplace((List<Nationalities>) entity);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }


    @Override
    public List getTopThreeEntities() {
        return alainZooDB.nationalitiesDao().getNationalitiesEntity(LangUtils.getCurrentLanguage());
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
