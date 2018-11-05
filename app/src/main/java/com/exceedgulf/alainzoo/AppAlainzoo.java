package com.exceedgulf.alainzoo;


import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.estimote.coresdk.common.config.EstimoteSDK;
import com.exceedgulf.alainzoo.activity.NavigationActivity;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.services.AccessTokenService;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.facebook.FacebookSdk;
import com.facebook.stetho.Stetho;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.onesignal.OneSignal;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AppAlainzoo extends Application implements Application.ActivityLifecycleCallbacks {
    private static AppAlainzoo appAlainzoo;
    boolean isTempLoggedIn = false;
    private Retrofit retrofitAdapter;
    private Retrofit retrofitAdapterSecond;
    private DirectionsRoute currentRoute = null;
    private boolean isNavigationRunning = false;

    public static AppAlainzoo getAppAlainzoo() {
        return appAlainzoo;
    }

    public boolean isTempLoggedIn() {
        return isTempLoggedIn;
    }

    public void setTempLoggedIn(boolean tempLoggedIn) {
        isTempLoggedIn = tempLoggedIn;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appAlainzoo = this;
        registerActivityLifecycleCallbacks(this);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        initNotification();
        FacebookSdk.sdkInitialize(this);
        EstimoteSDK.enableDebugLogging(true);
        Mapbox.getInstance(this, Constants.MAPBOX_KEY);
        Fabric.with(this, new Crashlytics());
        Stetho.initializeWithDefaults(this);
        if (!isTempLoggedIn && !SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false)) {
            SharedPrefceHelper.getInstance().save(PrefCons.USERNAME, "");
            SharedPrefceHelper.getInstance().save(PrefCons.PASSWORD, "");
            SharedPrefceHelper.getInstance().save(PrefCons.CSRF_TOKEN, "");
            SharedPrefceHelper.getInstance().save(PrefCons.LOGOUT_TOKEN, "");
            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN, "");
            SharedPrefceHelper.getInstance().save(PrefCons.REFRESH_TOKEN, "");
            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_EXPIRES, "");
            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_CREATED, "");
            stopService(new Intent(this, AccessTokenService.class));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(new Intent(this, AccessTokenService.class));
//            } else {
                startService(new Intent(this, AccessTokenService.class));
//            }
        }
    }

    public void initNotification() {
        OneSignal.setSubscription(SharedPrefceHelper.getInstance().get(PrefCons.ONESIGNAL_NOTIFICATION, false));
    }

    public DirectionsRoute getCurrentRoute() {
        return currentRoute;
    }

    public void setCurrentRoute(final DirectionsRoute currentRoute) {
        this.currentRoute = currentRoute;
    }

    public void setLanguage() {
        getAppAlainzoo().getResources().updateConfiguration(LangUtils.getLocal(getAppAlainzoo()), getAppAlainzoo().getResources().getDisplayMetrics());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public boolean isNavigationRunning() {
        return isNavigationRunning;
    }

    public Retrofit getRetrofitAdapter() {
        if (retrofitAdapter == null) {
            final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            final okhttp3.OkHttpClient okHttpClient = new OkHttpClient();
            final OkHttpClient.Builder builder = okHttpClient.newBuilder();
            builder.addInterceptor(logging);
            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {

                    Request original = chain.request();
                    HttpUrl originalHttpUrl = original.url();

                    HttpUrl url = originalHttpUrl.newBuilder()
                            .addQueryParameter(Constants.LANGUAGE_CODE, SharedPrefceHelper.getInstance().get(PrefCons.APP_LANGUAGE, "en"))
                            .build();

                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .url(url);

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
            builder.connectTimeout(240, TimeUnit.SECONDS);
            builder.readTimeout(240, TimeUnit.SECONDS);
            builder.writeTimeout(240, TimeUnit.SECONDS);
            retrofitAdapter = new Retrofit.Builder()
                    .baseUrl(Constants.DOMAIN_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(builder.build())
                    .build();
        }
        return retrofitAdapter;
    }

    public Retrofit getServiceRetrofitAdapter() {
        if (retrofitAdapter == null) {
            final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            final okhttp3.OkHttpClient okHttpClient = new OkHttpClient();
            final OkHttpClient.Builder builder = okHttpClient.newBuilder();
            builder.addInterceptor(logging);
            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {

                    Request original = chain.request();
                    HttpUrl originalHttpUrl = original.url();

                    HttpUrl url = originalHttpUrl.newBuilder()
                            .addQueryParameter(Constants.LANGUAGE_CODE, SharedPrefceHelper.getInstance().get(PrefCons.APP_LANGUAGE, "en"))
                            .build();

                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .url(url);

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
            builder.connectTimeout(3000, TimeUnit.MILLISECONDS);
            builder.readTimeout(3000, TimeUnit.MILLISECONDS);
            builder.writeTimeout(3000, TimeUnit.MILLISECONDS);
            retrofitAdapter = new Retrofit.Builder()
                    .baseUrl(Constants.DOMAIN_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(builder.build())
                    .build();

        }
        return retrofitAdapter;
    }

    public Retrofit getRetrofitAdapterSecond() {
        if (retrofitAdapterSecond == null) {
            final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            final okhttp3.OkHttpClient okHttpClient = new OkHttpClient();
            final OkHttpClient.Builder builder = okHttpClient.newBuilder();
            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    if (!original.url().toString().toLowerCase().contains("format=json")
                            && !original.url().toString().toLowerCase().contains("my-plan")
                            && !original.url().toString().toLowerCase().contains("zoo-search")
                            && !original.url().toString().toLowerCase().contains("user/treasures/")
                            && !original.url().toString().toLowerCase().contains("my_plan/delete")
                            && !original.url().toString().toLowerCase().contains("leaderboard/last-month")
                            && !original.url().toString().toLowerCase().contains("experience/user-rating")
                            && !original.url().toString().toLowerCase().contains("gamification/user/hints/completed")
                            && !original.url().toString().toLowerCase().contains("treasure-hunt/leaderboard/")) {
                        return chain.proceed(original);
                    }

                    Log.e("Url_Form", "" + original.url().toString());
                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + SharedPrefceHelper.getInstance().get(PrefCons.APPLICATION_TOKEN, ""))
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });
            builder.addInterceptor(logging);
            builder.connectTimeout(240, TimeUnit.SECONDS);
            builder.readTimeout(240, TimeUnit.SECONDS);
            builder.writeTimeout(240, TimeUnit.SECONDS);
            retrofitAdapterSecond = new Retrofit.Builder()
                    .baseUrl(Constants.DOMAIN_URL_SECOND)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(builder.build())
                    .build();

        }
        return retrofitAdapterSecond;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activity instanceof NavigationActivity) {
            isNavigationRunning = true;
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (activity instanceof NavigationActivity) {
            isNavigationRunning = false;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}