package com.exceedgulf.alainzoo.managers;

import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.constants.TagsName;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.Experience;
import com.exceedgulf.alainzoo.models.ClosingHourModel;
import com.exceedgulf.alainzoo.models.OpeningHourFront;
import com.exceedgulf.alainzoo.models.RecommendedPlanModel;
import com.exceedgulf.alainzoo.models.VisitAnimalModel;
import com.exceedgulf.alainzoo.models.VisitAttractionsModel;
import com.exceedgulf.alainzoo.models.VisitExperiencesModel;
import com.exceedgulf.alainzoo.models.VisitWhatsNewModel;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Paras Ghasadiya on 18/01/18.
 */

public class OpeningHourFrontManager {
    private static OpeningHourFrontManager openingHourFrontManager;

    public static OpeningHourFrontManager getPlansVisitManager() {
        if (openingHourFrontManager == null) {
            openingHourFrontManager = new OpeningHourFrontManager();
        }
        return openingHourFrontManager;
    }

    public void getClosingHours(final ApiCallback listApiCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getClosingHours(new Callback<ArrayList<ClosingHourModel>>() {
                @Override
                public void onResponse(Call<ArrayList<ClosingHourModel>> call, Response<ArrayList<ClosingHourModel>> response) {
                    if (response != null && response.body() != null && response.isSuccessful()) {
                        final ArrayList<ClosingHourModel> hourModels = response.body();
                        if (hourModels.size() == 0) {
                            getOpeningHours(listApiCallback);
                        } else {
                            listApiCallback.onSuccess(hourModels, true);
                        }
                    } else {
                        listApiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<ClosingHourModel>> call, Throwable t) {
                    listApiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            final ArrayList<OpeningHourFront> openingHourFronts = (ArrayList<OpeningHourFront>) getAllEntitiesFromDB();
            if (openingHourFronts.size() > 0) {
                listApiCallback.onSuccess(openingHourFronts, false);
            } else {
                listApiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
        }
    }

    private void getOpeningHours(final ApiCallback listApiCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getOpeningHoursFront(new Callback<ArrayList<OpeningHourFront>>() {
                @Override
                public void onResponse(Call<ArrayList<OpeningHourFront>> call, Response<ArrayList<OpeningHourFront>> response) {
                    if (response != null && response.body() != null && response.isSuccessful()) {
                        final ArrayList<OpeningHourFront> hourFronts = response.body();
                        if (insertEntitiesToDB(hourFronts)) {
                            listApiCallback.onSuccess(hourFronts, false);
                        }
                    } else {
                        listApiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<OpeningHourFront>> call, Throwable t) {
                    listApiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            listApiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
    }

    public boolean insertEntitiesToDB(List entities) {
        try {
            AlainZooDB.getInstance().openingHoursFrontDao().deleteAll();
            AlainZooDB.getInstance().openingHoursFrontDao().insertOrReplaceAll(entities);
            return true;
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
            return false;
        }
    }

    public List<OpeningHourFront> getAllEntitiesFromDB() {
        if (LangUtils.getCurrentLanguage().equalsIgnoreCase("ar")) {
            return AlainZooDB.getInstance().openingHoursFrontDao().getAllOpeningHours(LangUtils.getCurrentLanguage());
        } else {
            return AlainZooDB.getInstance().openingHoursFrontDao().getDescAllOpeningHours(LangUtils.getCurrentLanguage());
        }
    }
}
