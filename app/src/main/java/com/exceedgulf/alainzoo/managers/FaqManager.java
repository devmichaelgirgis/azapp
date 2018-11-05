package com.exceedgulf.alainzoo.managers;

import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.models.FAQs;
import com.exceedgulf.alainzoo.database.models.WhatsNew;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by p.p on 01/02/2017.
 */

public class FaqManager implements MainManager {
    private static final String TAG = "FaqManager";
    private static FaqManager faqManager;

    public static FaqManager getFaqManager() {
        if (faqManager == null) {
            faqManager = new FaqManager();
        }
        return faqManager;
    }

    @Override
    public void getAllEntitiesData(final int pageNumber, final ApiCallback apiCallback, final boolean isHome) {
//        final ArrayList<FAQs> faqsList = (ArrayList<FAQs>) getAllEntitiesFromDB(0);
//        if (faqsList.size() > 0) {
//            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
//                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
//            }
//            apiCallback.onSuccess(faqsList, faqsList.size() > 0);
//
//        } else {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getFAQs(new Callback<ArrayList<FAQs>>() {
                @Override
                public void onResponse(Call<ArrayList<FAQs>> call, Response<ArrayList<FAQs>> response) {
                    if (checkValidateResponce(response)) {
                        final ArrayList<FAQs> faqsList = response.body();
                        if (insertEntitiesToDB(faqsList)) {
                            apiCallback.onSuccess(faqsList, false);
                        }
                    } else {
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<FAQs>> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                    apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
        //}
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


    @Override
    public void filterEntitiesData(int pageNumber, int categeoryId, ApiCallback apiCallback) {

    }

    public List<WhatsNew> getEntitiesByname(int pageNumber, String name) {
        return alainZooDB.whatsNewDao().getAllWhatsnews(LangUtils.getCurrentLanguage(), pageNumber, name);
    }

    @Override
    public boolean checkValidateResponce(Response response) {
        return response != null  && response.body() != null && response.isSuccessful();
    }


    @Override
    public boolean insertEntitiesToDB(List entities) {
        try {
            alainZooDB.faQsDao().insertorReplace(entities);
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
    public List<FAQs> getAllEntitiesFromDB(int page) {
        return alainZooDB.faQsDao().getAllCategory(LangUtils.getCurrentLanguage());

    }


    @Override
    public List getTopThreeEntities() {
        return null;
    }


}
