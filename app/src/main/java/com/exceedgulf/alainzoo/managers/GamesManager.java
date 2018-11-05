package com.exceedgulf.alainzoo.managers;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.models.Games;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Paras Ghasadiya on 01/25/2018.
 */

public class GamesManager implements MainManager {
    private static GamesManager gamesManager = null;

    public static GamesManager getGamesManager() {
        if (gamesManager == null) {
            gamesManager = new GamesManager();
        }
        return gamesManager;
    }

    @Override
    public void getAllEntitiesData(int pageNumber, final ApiCallback apiCallback, boolean isHome) {
        final ArrayList<Games> gamelist = (ArrayList<Games>) (getAllEntitiesFromDB(pageNumber));
        if (gamelist.size() > 0) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            apiCallback.onSuccess(gamelist, false);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getGames(new Callback<ArrayList<Games>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Games>> call, Response<ArrayList<Games>> response) {
                        if (checkValidateResponce(response)) {
                            final ArrayList<Games> tmpgamelist = response.body();
                            if (insertEntitiesToDB(tmpgamelist)) {
                                apiCallback.onSuccess(tmpgamelist, false);
                            } else {
                                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                            }
                        } else {
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Games>> call, Throwable t) {
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
            alainZooDB.gamesDao().insertOrReplaceAll(entities);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }

    @Override
    public boolean insertEntityToDB(Object entity) {
        return false;
    }

    @Override
    public List getAllEntitiesFromDB(int pageNumber) {
        return alainZooDB.gamesDao().getAllGames(LangUtils.getCurrentLanguage());
    }

    @Override
    public List getTopThreeEntities() {
        return null;
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
