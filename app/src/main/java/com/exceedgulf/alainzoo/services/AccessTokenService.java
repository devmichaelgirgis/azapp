package com.exceedgulf.alainzoo.services;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.models.TokenModel;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.NetUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccessTokenService extends JobService {
    final DateFormat df = new SimpleDateFormat(Constants.TIME_STAMP_FORMAT, Locale.ENGLISH);
    final Handler handler = new Handler();
    private int interval = 10000;
    private boolean isInCall = false;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isInCall) {
                callTokenAPI();
            }
        }
    };

    public AccessTokenService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isInCall) {
                    callTokenAPI();
                }
            }
        }, interval);

        return START_NOT_STICKY;
    }

    private void callTokenAPI() {
        if (SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn()) {
            final String strRefreshToken = SharedPrefceHelper.getInstance().get(PrefCons.REFRESH_TOKEN, "");
            if (!TextUtils.isEmpty(strRefreshToken.trim())) {
                //if (System.currentTimeMillis() > calculateTimeInterval()) {
                //Log.e("API Call", "Refresh Interval: " + calculateTimeInterval() + " CurrentTime" + System.currentTimeMillis());
                if (NetUtil.isNetworkAvailable(this)) {
                    Log.e("API Call", "Refresh Token");
                    isInCall = true;
                    ApiControllers.getApiControllers().postRefreshToken(strRefreshToken, new Callback<TokenModel>() {
                        @Override
                        public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                            isInCall = false;
                            if (response.code() == 200) {
                                final TokenModel tokenModel = response.body();
                                SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN, tokenModel.getAccessToken());
                                SharedPrefceHelper.getInstance().save(PrefCons.REFRESH_TOKEN, tokenModel.getRefreshToken());
                                SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_EXPIRES, tokenModel.getExpiresIn());
                                SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_CREATED, df.format(new Date()));
                                Log.e("Refresh Token ", tokenModel.getAccessToken());
                                //interval = tokenModel.getExpiresIn() * 800;
                                interval = 200000;
                                handler.postDelayed(runnable, interval);
                                //Log.e("API Features ", "Interval Changed To : " + interval);
                            }
                        }

                        @Override
                        public void onFailure(Call<TokenModel> call, Throwable t) {
                            Log.e("API Call-Failure", t.getMessage());
                            isInCall = false;
                            handler.postDelayed(runnable, interval);
                        }
                    });
                }
                // }
            } else {
                final String strUsername = SharedPrefceHelper.getInstance().get(PrefCons.USERNAME, "");
                final String strPassword = SharedPrefceHelper.getInstance().get(PrefCons.PASSWORD, "");
                if (!TextUtils.isEmpty(strUsername) && !TextUtils.isEmpty(strPassword)) {
                    // if (System.currentTimeMillis() > calculateTimeInterval()) {
                    //Log.e("API Call", "UserAccess Interval: " + calculateTimeInterval() + " CurrentTime" + System.currentTimeMillis());
                    if (NetUtil.isNetworkAvailable(this)) {
                        Log.e("API Call", "UserAccess Token");
                        isInCall = true;
                        ApiControllers.getApiControllers().postUserAccessToken(strUsername, strPassword, new Callback<TokenModel>() {
                            @Override
                            public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                                isInCall = false;
                                if (response.code() == 200) {
                                    final TokenModel tokenModel = response.body();
                                    SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN, tokenModel.getAccessToken());
                                    SharedPrefceHelper.getInstance().save(PrefCons.REFRESH_TOKEN, tokenModel.getRefreshToken());
                                    SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_EXPIRES, tokenModel.getExpiresIn());
                                    SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_CREATED, df.format(new Date()));
                                    Log.e("UserAccess Token ", tokenModel.getAccessToken());
                                    //interval = tokenModel.getExpiresIn() * 800;
                                    interval = 200000;
                                    handler.postDelayed(runnable, interval);
                                    //Log.e("API Features ", "Interval Changed To : " + interval);
                                }
                            }

                            @Override
                            public void onFailure(Call<TokenModel> call, Throwable t) {
                                Log.e("UserAccess-Failure", t.getMessage());
                                handler.postDelayed(runnable, interval);
                                isInCall = false;
                            }
                        });
                    }
                    // }
                }
            }
        } else {
            //if (System.currentTimeMillis() > calculateTimeInterval()) {
            //Log.e("API Call", "Application Interval: " + calculateTimeInterval() + " CurrentTime" + System.currentTimeMillis());
            if (NetUtil.isNetworkAvailable(this)) {
                Log.e("API Call", "Application Token");
                isInCall = true;
                ApiControllers.getApiControllers().postApplicationToken(new Callback<TokenModel>() {
                    @Override
                    public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                        isInCall = false;
                        if (response.code() == 200) {
                            final TokenModel tokenModel = response.body();
                            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN, tokenModel.getAccessToken());
                            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_EXPIRES, tokenModel.getExpiresIn());
                            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_CREATED, df.format(new Date()));
                            Log.e("Application Token ", tokenModel.getAccessToken());
                            //interval = tokenModel.getExpiresIn() * 800;
                            interval = 200000;
                            handler.postDelayed(runnable, interval);
                            //Log.e("API Features ", "Interval Changed To : " + interval);
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenModel> call, Throwable t) {
                        Log.e("API Call-Failure", t.getMessage());
                        handler.postDelayed(runnable, interval);
                        isInCall = false;
                    }
                });
            }
            //}
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }
}
