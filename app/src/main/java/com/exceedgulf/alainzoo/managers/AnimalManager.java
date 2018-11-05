package com.exceedgulf.alainzoo.managers;

import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.database.models.AnimalsCategory;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by michael.george on 12/27/2017.
 */

public class AnimalManager implements MainManager {
    private static final String TAG = "AnimalManager";
    private static AnimalManager animalManager;

    public static AnimalManager getAnimalManager() {
        if (animalManager == null) {
            animalManager = new AnimalManager();
        }
        return animalManager;
    }

    @Override
    public void getAllEntitiesData(final int pageNumber, final ApiCallback apiCallback, final boolean isHome) {
        final ArrayList<Animal> animalList = (ArrayList<Animal>) (getAllEntitiesFromDB(pageNumber));
        if (animalList.size() > 0 && !isOlderData()) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            // apiCallback.onSuccess(animalList, animalList.size() > 0);
            apiCallback.onSuccess(animalList, false);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getAllAnimals(null, new Callback<ArrayList<Animal>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Animal>> call, Response<ArrayList<Animal>> response) {
                        if (checkValidateResponce(response)) {
                            final ArrayList<Animal> animalList = response.body();
                            if (insertEntitiesToDB(animalList)) {
                                apiCallback.onSuccess(animalList, false);
                            } else {
                                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                            }
                        } else {
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Animal>> call, Throwable t) {
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                });
            } else {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
        }
    }

    @Override
    public Object getEntityDetails(final Integer id, final ApiDetailCallback apiDetailCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getAnimalLanding(id, new Callback<List<Animal>>() {
                @Override
                public void onResponse(Call<List<Animal>> call, Response<List<Animal>> response) {
                    if (checkValidateResponce(response)) {
                        final List<Animal> animalList = response.body();
                        if (insertEntitiesToDB(animalList)) {
                            apiDetailCallback.onSuccess(animalList.get(0));
                        } else {
                            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    } else {
                        apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<List<Animal>> call, Throwable t) {
                    //Log.e(TAG, t.getMessage());
                    apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });

        } else {
            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
        return null;
    }

    @Override
    public void filterEntitiesData(int pageNumber, int categeoryId, final ApiCallback apiCallback) {
        final ArrayList<Animal> animalList = (ArrayList<Animal>) (getEntitiesByCategoryId(pageNumber, categeoryId));
        if (animalList.size() > 0) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            apiCallback.onSuccess(animalList, animalList.size() > 0);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getAllAnimalsByCategory(null, categeoryId, new Callback<ArrayList<Animal>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Animal>> call, Response<ArrayList<Animal>> response) {
                        if (checkValidateResponce(response)) {
                            final ArrayList<Animal> animalList = response.body();
                            //if (insertEntitiesToDB(animalList)) {
                            //apiCallback.onSuccess(animalList, animalList != null && animalList.size() > 0);
                            apiCallback.onSuccess(animalList, false);
                            //}
                        } else {
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Animal>> call, Throwable t) {
                        //Log.e(TAG, t.getMessage());
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                });
            } else {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
        }
    }

    private List<Animal> getEntitiesByCategoryId(final int pageNumber, final int categoryId) {
        return alainZooDB.animalsDao().getAllAnimalsByCategoryId(LangUtils.getCurrentLanguage(), categoryId);
    }

    @Override
    public Object getEntityDetailsFromDB(Integer id) {
        return alainZooDB.animalsDao().findById(id, LangUtils.getCurrentLanguage());
    }


    public void getAllAnimalsCategoryData(final ApiCallback apiCallback) {
        final ArrayList<AnimalsCategory> animalsCategoriesList = (ArrayList<AnimalsCategory>) (checkEntitiesExistInCash());
        if (animalsCategoriesList.size() > 0) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            apiCallback.onSuccess(animalsCategoriesList, false);
        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getAllAnimalsCategories(new Callback<ArrayList<AnimalsCategory>>() {
                    @Override
                    public void onResponse(Call<ArrayList<AnimalsCategory>> call, Response<ArrayList<AnimalsCategory>> response) {
                        if (checkValidateResponce(response)) {
                            final ArrayList<AnimalsCategory> animalsCategoriesList = response.body();
                            if (insertCategoryEntitiesToDB(animalsCategoriesList)) {
                                apiCallback.onSuccess(animalsCategoriesList, false);
                            } else {
                                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                            }
                        } else {
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<AnimalsCategory>> call, Throwable t) {
                        //Log.e(TAG, t.getMessage());
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                });
            } else {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
        }
    }


    private List<AnimalsCategory> checkEntitiesExistInCash() {
        return alainZooDB.animalsCategoryDao().getAll(LangUtils.getCurrentLanguage());
    }

    public boolean insertCategoryEntitiesToDB(List entities) {
        try {
            alainZooDB.animalsCategoryDao().insertOrReplaceAll(entities);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }

    }

    @Override
    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }


    @Override
    public boolean insertEntitiesToDB(List entities) {
        try {
            alainZooDB.animalsDao().insertOrReplaceAll(entities);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }

    }

    @Override
    public boolean insertEntityToDB(Object entity) {
        try {
            alainZooDB.animalsDao().insertOrReplace((Animal) entity);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    @Override
    public List getAllEntitiesFromDB(int pageNumber) {
        return alainZooDB.animalsDao().getAll(LangUtils.getCurrentLanguage());
    }


    @Override
    public List getTopThreeEntities() {
        return null;
    }

    @Override
    public boolean isOlderData() {
        final int value = alainZooDB.animalsDao().isOlder(LangUtils.getCurrentLanguage());
        return value == 0;
    }
}
