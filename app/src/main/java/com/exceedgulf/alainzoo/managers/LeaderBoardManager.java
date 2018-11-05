package com.exceedgulf.alainzoo.managers;

import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.LeaderBoard;
import com.exceedgulf.alainzoo.database.models.ShuttleService;
import com.exceedgulf.alainzoo.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P.P on 26/02/2018.
 */

public class LeaderBoardManager {
    private static final String TAG = "LeaderBoardManager";
    private static LeaderBoardManager leaderBoardManager;

    public static LeaderBoardManager getLeaderBoardManager() {
        if (leaderBoardManager == null) {
            leaderBoardManager = new LeaderBoardManager();
        }
        return leaderBoardManager;
    }

    public void getAllEntitiesData(final ApiCallback apiCallback, final boolean isHome) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getLeaderBoardList(new Callback<ArrayList<LeaderBoard>>() {
                @Override
                public void onResponse(Call<ArrayList<LeaderBoard>> call, Response<ArrayList<LeaderBoard>> response) {
                    if (checkValidateResponce(response)) {
                        final ArrayList<LeaderBoard> leaderBoardArrayList = response.body();
                        apiCallback.onSuccess(leaderBoardArrayList, false);
                    } else {
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<LeaderBoard>> call, Throwable t) {
                    apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
    }

    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }

//    public boolean insertEntitiesToDB(List entities) {
//        try {
//            AlainZooDB.getInstance().shuttleServiceDao().insertOrReplaceAll(entities);
//            return true;
//        } catch (Exception e) {
//            Log.e(TAG, e.getMessage());
//            return false;
//        }
//    }
//
//    public List getAllEntitiesFromDB(int pageNumber) {
//        return AlainZooDB.getInstance().shuttleServiceDao().getAllShuttleService();
//    }
//
//    public boolean isOlderData() {
//        final int value = AlainZooDB.getInstance().shuttleServiceDao().isOlder(LangUtils.getCurrentLanguage());
//        return value == 0;
//    }

}
