package com.exceedgulf.alainzoo.managers;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.models.NotificationModel;
import com.exceedgulf.alainzoo.utils.NetUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Paras Ghasadiya on 05/02/18.
 */

public class NotificationManager {
    private static NotificationManager notificationManager;

    public static NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = new NotificationManager();
        }
        return notificationManager;
    }

    public void getAllNotifications(final ApiCallback apiCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getAllNotifications(new Callback<ArrayList<NotificationModel>>() {
                @Override
                public void onResponse(Call<ArrayList<NotificationModel>> call, Response<ArrayList<NotificationModel>> response) {
                    if (response.isSuccessful()) {
                        final ArrayList<NotificationModel> notificationModelList = response.body();
                        apiCallback.onSuccess(notificationModelList, false);
                    } else {
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<NotificationModel>> call, Throwable t) {
                    apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
    }

}
