package com.exceedgulf.alainzoo.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Data.UrlsUtils;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.api.ApiInterface;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.database.models.Attraction;
import com.exceedgulf.alainzoo.database.models.Education;
import com.exceedgulf.alainzoo.database.models.Experience;
import com.exceedgulf.alainzoo.database.models.VisitorService;
import com.exceedgulf.alainzoo.database.models.WhatsNew;
import com.exceedgulf.alainzoo.managers.AnimalManager;
import com.exceedgulf.alainzoo.managers.AttractionsManager;
import com.exceedgulf.alainzoo.managers.EducationManager;
import com.exceedgulf.alainzoo.managers.ExperienceManager;
import com.exceedgulf.alainzoo.managers.VisitorServicesManager;
import com.exceedgulf.alainzoo.managers.WhatsNewManager;
import com.exceedgulf.alainzoo.utils.NetUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CacheService extends IntentService {

    private final static String TAG = CacheService.class.getSimpleName();
    private static final String VISITOR = "visitor_services";
    private static final String EDUCATIONS = "educations";
    private ApiInterface apiInterface;

    public CacheService() {
        super("CacheService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        apiInterface = AppAlainzoo.getAppAlainzoo().getServiceRetrofitAdapter().create(ApiInterface.class);
        if (ExperienceManager.getExperienceManager().isOlderData()) {
            Log.e(TAG, "Experience Older Data Found");
            getExperienceAPI();
        }
        if (AttractionsManager.getAttractionManager().isOlderData()) {
            Log.e(TAG, "Attraction Older Data Found");
            getAttractionAPI();
        }
        if (WhatsNewManager.getWhatsnewManager().isOlderData()) {
            Log.e(TAG, "WhatsNew Older Data Found");
            getWhatsNewAPI();
        }
        if (AnimalManager.getAnimalManager().isOlderData()) {
            Log.e(TAG, "Animal Older Data Found");
            getAnimalAPI();
        }
        if (VisitorServicesManager.getVisitorServicesManager().isOlderData()) {
            Log.e(TAG, "Visitor Service Older Data Found");
            getVisitorServiceAPI();
        }
        if (EducationManager.getEducationManager().isOlderData()) {
            Log.e(TAG, "Education Older Data Found");
            getEducationAPI();
        }
    }

    private void getWhatsNewAPI() {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            Log.e(TAG, "WhatsNew Call Started");
            apiInterface.getWhatsNewsList(UrlsUtils.getAllWhatsNewURL(null)).enqueue(new Callback<ArrayList<WhatsNew>>() {
                @Override
                public void onResponse(Call<ArrayList<WhatsNew>> call, Response<ArrayList<WhatsNew>> response) {
                    if (checkValidateResponce(response)) {
                        Log.e(TAG, "WhatsNew Call Success");
                        final ArrayList<WhatsNew> whatsnewList = response.body();
                        AlainZooDB.getInstance().whatsNewDao().insertOrReplaceAll(whatsnewList);
                    } else {
                        Log.e(TAG, "WhatsNew Call Failed");
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<WhatsNew>> call, Throwable t) {
                    Log.e(TAG, "WhatsNew Call Failure");
                }
            });
        }
    }

    private void getAttractionAPI() {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            Log.e(TAG, "Attraction Call Started");
            apiInterface.getAttractionsList(UrlsUtils.getAttractionsUrl(0)).enqueue(new Callback<ArrayList<Attraction>>() {
                @Override
                public void onResponse(Call<ArrayList<Attraction>> call, Response<ArrayList<Attraction>> response) {
                    if (checkValidateResponce(response)) {
                        Log.e(TAG, "Attraction Call Success");
                        final ArrayList<Attraction> attractionArrayList = response.body();
                        AlainZooDB.getInstance().attractionsDao().insertOrReplaceAll(attractionArrayList);
                    } else {
                        Log.e(TAG, "Attraction Call Failed");
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Attraction>> call, Throwable t) {
                    Log.e(TAG, "Attraction Call Failure");
                }
            });
        }
    }

    private void getExperienceAPI() {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            Log.e(TAG, "Experience Call Started");
            apiInterface.getExperiencesList(UrlsUtils.getExperiencesList(null)).enqueue(new Callback<ArrayList<Experience>>() {
                @Override
                public void onResponse(Call<ArrayList<Experience>> call, Response<ArrayList<Experience>> response) {
                    if (checkValidateResponce(response)) {
                        Log.e(TAG, "Experience Call Success");
                        final ArrayList<Experience> experienceList = response.body();
                        AlainZooDB.getInstance().experienceDao().insertOrReplaceAll(experienceList);
                    } else {
                        Log.e(TAG, "Experience Call Failed");
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Experience>> call, Throwable t) {
                    Log.e(TAG, "Experience Call Failure");
                }
            });
        }
    }

    private void getAnimalAPI() {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            Log.e(TAG, "Animal Call Started");
            apiInterface.getAnimalsList(UrlsUtils.getAllAnimalsURL(null)).enqueue(new Callback<ArrayList<Animal>>() {
                @Override
                public void onResponse(Call<ArrayList<Animal>> call, Response<ArrayList<Animal>> response) {
                    if (checkValidateResponce(response)) {
                        Log.e(TAG, "Animal Call Success");
                        final ArrayList<Animal> animalList = response.body();
                        AlainZooDB.getInstance().animalsDao().insertOrReplaceAll(animalList);
                    } else {
                        Log.e(TAG, "Animal Call Failed");
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Animal>> call, Throwable t) {
                    Log.e(TAG, "Animal Call Failure");
                }
            });
        }
    }

    private void getVisitorServiceAPI() {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            Log.e(TAG, "VisitorService Call Started");
            apiInterface.getVisitorServicesList(UrlsUtils.getServicesUrl(VISITOR, null)).enqueue(new Callback<ArrayList<VisitorService>>() {
                @Override
                public void onResponse(Call<ArrayList<VisitorService>> call, Response<ArrayList<VisitorService>> response) {
                    if (checkValidateResponce(response)) {
                        Log.e(TAG, "VisitorService Call Success");
                        final ArrayList<VisitorService> visitorServices = response.body();
                        AlainZooDB.getInstance().visitorServiceDao().insertOrReplaceAll(visitorServices);
                    } else {
                        Log.e(TAG, "VisitorService Call Failed");
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<VisitorService>> call, Throwable t) {
                    Log.e(TAG, "VisitorService Call Failure");
                }
            });
        }
    }

    private void getEducationAPI() {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            Log.e(TAG, "Education Call Started");
            apiInterface.getEducationsList(UrlsUtils.getServicesUrl(EDUCATIONS, null)).enqueue(new Callback<ArrayList<Education>>() {
                @Override
                public void onResponse(Call<ArrayList<Education>> call, Response<ArrayList<Education>> response) {
                    if (checkValidateResponce(response)) {
                        Log.e(TAG, "Education Call Success");
                        final ArrayList<Education> educationArrayList = response.body();
                        AlainZooDB.getInstance().educationDao().insertOrReplaceAll(educationArrayList);
                    } else {
                        Log.e(TAG, "Education Call Failed");
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Education>> call, Throwable t) {
                    Log.e(TAG, "Education Call Failure");
                }
            });
        }
    }

    public boolean checkValidateResponce(Response response) {
        return response != null && response.code() == 200 && response.body() != null && response.isSuccessful();
    }

}
