package com.exceedgulf.alainzoo.managers;

import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.models.Experience;
import com.exceedgulf.alainzoo.database.models.Rating;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ehab.alagoza on 12/27/2017.
 */

public class ExperienceManager implements MainManager {
    private static final String TAG = "ExperienceManager";
    private static ExperienceManager experienceManager;

    public static ExperienceManager getExperienceManager() {
        if (experienceManager == null) {
            experienceManager = new ExperienceManager();
        }
        return experienceManager;
    }

    @Override
    public void getAllEntitiesData(final int pageNumber, final ApiCallback apiCallback, final boolean isHome) {
        final ArrayList<Experience> experienceList = (ArrayList<Experience>) (isHome ? getTopThreeEntities() : getAllEntitiesFromDB(pageNumber));
        if (experienceList.size() > 0 && !isOlderData()) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            // apiCallback.onSuccess(experienceList, experienceList.size() > 0);
            apiCallback.onSuccess(experienceList, false);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getExperiences(null, new Callback<ArrayList<Experience>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Experience>> call, Response<ArrayList<Experience>> response) {
                        if (checkValidateResponce(response)) {
                            final ArrayList<Experience> experienceList = response.body();
                            if (insertEntitiesToDB(experienceList)) {
                                if (isHome) {
                                    apiCallback.onSuccess((ArrayList) getTopThreeEntities(), false);
                                } else {
                                    // apiCallback.onSuccess(experienceList, experienceList != null && experienceList.size() > 0);
                                    apiCallback.onSuccess(experienceList, false);
                                }
                            }
                        } else {
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Experience>> call, Throwable t) {
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                });
            } else {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
        }
    }

    public void submitVote(final int id, final float rating, final ApiDetailCallback<String> apiCallback) {

        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            final RequestBody requestBody = createVoteRequest(id, rating);
            if (requestBody == null) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                return;
            }
            ApiControllers.getApiControllers().submitVote(requestBody, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            if (voteDataParse(response.body().string())) {
                                apiCallback.onSuccess(AppAlainzoo.getAppAlainzoo().getString(R.string.rating_success));
                            } else {
                                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    } else {
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } else {
            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
    }

    private boolean voteDataParse(final String response) {
        try {
            final JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("id") && jsonObject.has("uuid")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private RequestBody createVoteRequest(final int id, final float rating) {
        try {
            final JSONObject jsonObjectVote = new JSONObject();
            jsonObjectVote.put("entity_id", createRequestArray("target_id", id));
            jsonObjectVote.put("value", createRequestArray("value", rating));
            jsonObjectVote.put("type", createRequestArray("target_id", "vote"));
            jsonObjectVote.put("field_name", createRequestArray("value", "field_rating"));
            jsonObjectVote.put("value_type", createRequestArray("value", "percent"));
            jsonObjectVote.put("entity_type", createRequestArray("value", "node"));
            return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObjectVote.toString());
        } catch (Exception e) {
            return null;
        }

    }

    private JSONArray createRequestArray(final String key, final Object value) {
        final JSONArray jsonArray = new JSONArray();
        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(key, value);
            jsonArray.put(jsonObject);
            return jsonArray;
        } catch (Exception e) {
            return jsonArray;
        }
    }

    public void getExperienceRating(final int experienceId, final ApiCallback apiCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getExperienceRating(experienceId, new Callback<ArrayList<Rating>>() {
                @Override
                public void onResponse(Call<ArrayList<Rating>> call, Response<ArrayList<Rating>> response) {
                    if (checkValidateResponce(response)) {
                        final ArrayList<Rating> ratingArrayList = response.body();
                        apiCallback.onSuccess(ratingArrayList, false);
                    } else {
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Rating>> call, Throwable t) {
                    apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
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
                            insertEntityToDB(experienceArrayList.get(0));
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
        return alainZooDB.experienceDao().getExperienceDetail(id, LangUtils.getCurrentLanguage());
    }

    @Override
    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }


    @Override
    public boolean insertEntitiesToDB(List entities) {
        try {
            alainZooDB.experienceDao().insertOrReplaceAll(entities);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }

    }

    @Override
    public boolean insertEntityToDB(Object entity) {
        try {
            alainZooDB.experienceDao().insertOrReplaceSingle((Experience) entity);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }


    @Override
    public List<Experience> getAllEntitiesFromDB(int pageNumber) {
        //return alainZooDB.experienceDao().getAllExperiances(LangUtils.getCurrentLanguage(), pageNumber);
        return alainZooDB.experienceDao().getAll(LangUtils.getCurrentLanguage());

    }

    @Override
    public List getTopThreeEntities() {
        return alainZooDB.experienceDao().getTopThreeExperiances(LangUtils.getCurrentLanguage());
    }

    @Override
    public boolean isOlderData() {
        final int value = alainZooDB.experienceDao().isOlder(LangUtils.getCurrentLanguage());
        return value == 0;
    }
}
