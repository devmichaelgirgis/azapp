package com.exceedgulf.alainzoo.managers;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.Contactus;
import com.exceedgulf.alainzoo.models.AddressInformationModel;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

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
 * Created by Paras Ghasadiya on 29/01/18.
 */

public class ContactUSManager {
    private static ContactUSManager contactUSManager;

    public static ContactUSManager getContactUSManager() {
        if (contactUSManager == null) {
            contactUSManager = new ContactUSManager();
        }
        return contactUSManager;
    }

    public void getContactUs(final ApiDetailCallback apiDetailCallback) {
        final ArrayList<Contactus> contactUsModelArrayList = (ArrayList<Contactus>) (getAllEntitiesFromDB());
        if (contactUsModelArrayList.size() > 0 && !isOlderData()) {
            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
            apiDetailCallback.onSuccess(contactUsModelArrayList);

        } else {
            if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
                ApiControllers.getApiControllers().getPagesContant(Constants.DOMAIN_URL + LangUtils.getCurrentLanguage() + Constants.GET_CONTACT_US, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        checkIsSuccessResponse(response, apiDetailCallback);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                });
            } else {
                apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
            }
        }
    }

    private void checkIsSuccessResponse(final Response<ResponseBody> responseBodyResponse, final ApiDetailCallback detailCallback) {
        try {
            if (responseBodyResponse.isSuccessful()) {

                final JSONArray jsonArray = new JSONArray(responseBodyResponse.body().string());
                final JSONObject jsonObject = jsonArray.optJSONObject(0);

                final JSONArray address = jsonObject.optJSONArray("address");
                final ArrayList<Contactus> contactusArrayList = new ArrayList<>();
                for (int p = 0; p < address.length(); p++) {
                    final Contactus contactus = new Contactus();
                    final JSONObject jsonObjectTmp = address.optJSONObject(p);
                    final String title = jsonObjectTmp.optString("title");
                    final JSONArray address_details = jsonObjectTmp.optJSONArray("address_details");
                    final ArrayList<AddressInformationModel> addressInformationModelArrayList = new ArrayList<>();
                    for (int d = 0; d < address_details.length(); d++) {
                        final AddressInformationModel addressInformationModel = new AddressInformationModel();
                        final JSONObject jsonObjectDetail = address_details.optJSONObject(d);
                        final String icon = jsonObjectDetail.optString("icon");
                        final String info = jsonObjectDetail.optString("info");
                        final String link = jsonObjectDetail.optString("link");
                        addressInformationModel.setIcon(icon);
                        addressInformationModel.setInfo(info);
                        addressInformationModel.setLink(link);
                        addressInformationModelArrayList.add(addressInformationModel);
                    }
                    contactus.setTitle(title);
                    contactus.setAddressInformationModelArrayList(addressInformationModelArrayList);
                    contactusArrayList.add(contactus);
                }
                AlainZooDB.getInstance().contactusDao().insertorReplace(contactusArrayList);
                detailCallback.onSuccess(contactusArrayList);

            } else {
                detailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
            }
        } catch (Exception e) {
            e.printStackTrace();
            detailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
        }

    }


    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }

    public List getAllEntitiesFromDB() {
        return AlainZooDB.getInstance().contactusDao().getContactusEntity(LangUtils.getCurrentLanguage());
    }

    public boolean isOlderData() {
        final int value = AlainZooDB.getInstance().contactusDao().isOlder(LangUtils.getCurrentLanguage());
        return value == 0;
    }
}
