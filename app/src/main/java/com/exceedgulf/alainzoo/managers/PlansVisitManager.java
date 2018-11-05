package com.exceedgulf.alainzoo.managers;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.constants.TagsName;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.Ticket;
import com.exceedgulf.alainzoo.models.GeneralPlan;
import com.exceedgulf.alainzoo.models.RecommendedPlanModel;
import com.exceedgulf.alainzoo.models.VisitAnimalModel;
import com.exceedgulf.alainzoo.models.VisitAttractionsModel;
import com.exceedgulf.alainzoo.models.VisitExperiencesModel;
import com.exceedgulf.alainzoo.models.VisitOrder;
import com.exceedgulf.alainzoo.models.VisitWhatsNewModel;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Paras Ghasadiya on 18/01/18.
 */

public class PlansVisitManager {
    private static PlansVisitManager plansVisitManager;

    public static PlansVisitManager getPlansVisitManager() {
        if (plansVisitManager == null) {
            plansVisitManager = new PlansVisitManager();
        }
        return plansVisitManager;
    }

    public void getRecommendedPlanList(final ApiCallback listApiCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getRecommendedPlans(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    getResponseData(response, listApiCallback);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    listApiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            listApiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
    }

    public void getMyPlanList(final ApiCallback listApiCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getMyPlans(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    getResponseData(response, listApiCallback);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    listApiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }
            });
        } else {
            listApiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
    }

    public void deleteMyPlan(final ApiDetailCallback apiDetailCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().deleteMyPlan(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        checkIsSuccessResponse(response, apiDetailCallback);
                    } catch (Exception e) {
                        e.printStackTrace();
                        apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
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

    private void getResponseData(Response<ResponseBody> response, final ApiCallback listApiCallback) {
        final ArrayList<RecommendedPlanModel> recommendedPlanModels = new ArrayList<>();
        if (response.isSuccessful()) {
            try {
                final String responseData = response.body().string();
                final JSONArray jsonArrayRecommendedPlans = new JSONArray(responseData);
                for (int i = 0; i < jsonArrayRecommendedPlans.length(); i++) {

                    final RecommendedPlanModel recommendedPlanModel = new RecommendedPlanModel();

                    final JSONObject rootObject = jsonArrayRecommendedPlans.getJSONObject(i);
                    // final JSONObject rootObject = new JSONObject(responseData);
                    final int id = rootObject.optInt(TagsName.ID);
                    final String langcode = rootObject.optString(TagsName.LANGCODE);
                    final String name = rootObject.optString(TagsName.NAME);
                    final String details = rootObject.optString(TagsName.DETAILS);
                    final String image = rootObject.optString(TagsName.IMAGE);
                    final String visitDate = rootObject.optString(TagsName.VISIT_DATE);
                    final String duration = rootObject.optString(TagsName.VISIT_DURATION);
                    final JSONArray suggested_tickets_en = rootObject.optJSONArray(TagsName.TICKET_EN);
                    final JSONArray suggested_tickets_ar = rootObject.optJSONArray(TagsName.TICKET_AR);
                    final JSONArray animalArray = rootObject.getJSONArray(TagsName.ANIMALS);
                    final JSONArray attractionsArray = rootObject.getJSONArray(TagsName.ATTRACTIONS);
                    final JSONArray experiencesArray = rootObject.getJSONArray(TagsName.EXPERIENCES);
                    final JSONArray eventsArray = rootObject.getJSONArray(TagsName.EVENTS);
                    final JSONArray visitOrderArray = rootObject.optJSONArray(TagsName.VISIT_ORDER);


                    if (suggested_tickets_en != null) {
                        for (int t = 0; t < suggested_tickets_en.length(); t++) {
                            final JSONObject ticketRoot = suggested_tickets_en.getJSONObject(t);
                            final Ticket ticket = new Ticket();
                            ticket.setTitle(ticketRoot.optString(TagsName.TITLE));
                            ticket.setAdult(ticketRoot.optInt(TagsName.ADULT));
                            ticket.setChild(ticketRoot.optInt(TagsName.CHILD));
                            recommendedPlanModel.getTicketArrayList().add(ticket);
                        }
                    }

                    if (suggested_tickets_ar != null) {
                        for (int t1 = 0; t1 < suggested_tickets_ar.length(); t1++) {
                            final JSONObject ticketRoot = suggested_tickets_ar.getJSONObject(t1);
                            final Ticket ticket = new Ticket();
                            ticket.setTitle(ticketRoot.optString(TagsName.TITLE));
                            ticket.setAdult(ticketRoot.optInt(TagsName.ADULT));
                            ticket.setChild(ticketRoot.optInt(TagsName.CHILD));
                            recommendedPlanModel.getTicketArrayListAr().add(ticket);
                        }
                    }

                    for (int a = 0; a < animalArray.length(); a++) {
                        final JSONObject animalRoot = animalArray.getJSONObject(a);
                        final VisitAnimalModel visitAnimalModel = new VisitAnimalModel();
                        visitAnimalModel.setId(animalRoot.optInt(TagsName.ID));
                        visitAnimalModel.setName(animalRoot.optString(TagsName.NAME));
                        visitAnimalModel.setImage(animalRoot.optString(TagsName.IMAGE));
                        visitAnimalModel.setCategoryId(animalRoot.optInt(TagsName.CATEGORY_ID));
                        visitAnimalModel.setCategory(animalRoot.optString(TagsName.CATEGORY));
                        visitAnimalModel.setIs_celebrity(animalRoot.optString(TagsName.IS_CELEBRITY));
                        visitAnimalModel.setFeeding_hours(animalRoot.optString(TagsName.FEEDING_HOURS));
                        visitAnimalModel.setLatitude(animalRoot.optString(TagsName.LATITUDE));
                        visitAnimalModel.setLongitude(animalRoot.optString(TagsName.LONGITUDE));
                        recommendedPlanModel.getAnimalArrayList().add(visitAnimalModel);
                    }

                    for (int at = 0; at < attractionsArray.length(); at++) {
                        final JSONObject attractionsRoot = attractionsArray.getJSONObject(at);
                        final VisitAttractionsModel attraction = new VisitAttractionsModel();
                        attraction.setId(attractionsRoot.optInt(TagsName.ID));
                        attraction.setName(attractionsRoot.optString(TagsName.NAME));
                        attraction.setImage(attractionsRoot.optString(TagsName.IMAGE));
                        recommendedPlanModel.getAttractionArrayList().add(attraction);
                    }

                    for (int e = 0; e < experiencesArray.length(); e++) {
                        final JSONObject experiencesRoot = experiencesArray.getJSONObject(e);
                        final VisitExperiencesModel visitExperiencesModel = new VisitExperiencesModel();
                        visitExperiencesModel.setId(experiencesRoot.optInt(TagsName.ID));
                        visitExperiencesModel.setName(experiencesRoot.optString(TagsName.NAME));
                        visitExperiencesModel.setImage(experiencesRoot.optString(TagsName.IMAGE));
                        visitExperiencesModel.setTicket_price(experiencesRoot.optString(TagsName.TICKET_PRICE));
                        visitExperiencesModel.setOpening_hours(experiencesRoot.optString(TagsName.OPENING_HOURS));
                        visitExperiencesModel.setLatitude(experiencesRoot.optString(TagsName.LATITUDE));
                        visitExperiencesModel.setLongitude(experiencesRoot.optString(TagsName.LONGITUDE));
                        recommendedPlanModel.getExperienceArrayList().add(visitExperiencesModel);
                    }
                    for (int ev = 0; ev < eventsArray.length(); ev++) {
                        final JSONObject eventsRoot = eventsArray.getJSONObject(ev);
                        final VisitWhatsNewModel visitWhatsNewModel = new VisitWhatsNewModel();
                        visitWhatsNewModel.setId(eventsRoot.optInt(TagsName.ID));
                        visitWhatsNewModel.setName(eventsRoot.optString(TagsName.NAME));
                        visitWhatsNewModel.setImage(eventsRoot.optString(TagsName.IMAGE));
                        visitWhatsNewModel.setLatitude(eventsRoot.optString(TagsName.LATITUDE));
                        visitWhatsNewModel.setLongitude(eventsRoot.optString(TagsName.LONGITUDE));
                        recommendedPlanModel.getVisitWhatsNewModelArrayList().add(visitWhatsNewModel);
                    }

                    for (int vo = 0; vo < visitOrderArray.length(); vo++) {
                        final JSONObject jsonObject = visitOrderArray.getJSONObject(vo);
                        final int voId = jsonObject.optInt("id");
                        final String type = jsonObject.optString("type");
                        final VisitOrder visitOrder = new VisitOrder();
                        visitOrder.setId(voId);
                        visitOrder.setType(type);
                        recommendedPlanModel.getVisitOrderArrayList().add(visitOrder);

                    }

                    recommendedPlanModel.setId(id);
                    recommendedPlanModel.setLangcode(langcode);
                    recommendedPlanModel.setPlaneDate(visitDate);
                    recommendedPlanModel.setDuration(duration);
                    recommendedPlanModel.setName(name);
                    recommendedPlanModel.setDetails(details);
                    recommendedPlanModel.setImage(image);
                    recommendedPlanModels.add(recommendedPlanModel);

                }


            } catch (Exception e) {
                e.printStackTrace();
                listApiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                return;
            }
            listApiCallback.onSuccess(recommendedPlanModels, false);
        } else {
            listApiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
        }

    }

    private void checkIsSuccessResponse(final Response<ResponseBody> responseBodyResponse, final ApiDetailCallback apiDetailCallback) {
        try {
            if (responseBodyResponse.isSuccessful()) {
                getResponseStatus(responseBodyResponse.body().string(), apiDetailCallback);
            } else if (responseBodyResponse.code() == 403) {
                apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
            } else {
                getResponseStatus(responseBodyResponse.errorBody().string(), apiDetailCallback);
            }
        } catch (Exception e) {
            apiDetailCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
        }

    }

    private void getResponseStatus(final String responseString, ApiDetailCallback apiDetailCallback) throws IOException, JSONException {
        final JSONObject jsonObject = new JSONObject(responseString);
        final String status = jsonObject.optString("status");
        if (status.equalsIgnoreCase("success")) {
            final JSONObject jsonObjectMessage = jsonObject.optJSONObject("messages");
            final String en = jsonObjectMessage.optString("en");
            final String ar = jsonObjectMessage.optString("ar");
            apiDetailCallback.onSuccess(LangUtils.getCurrentLanguage().equalsIgnoreCase("ar") ? ar : en);
        } else if (status.equalsIgnoreCase("failed")) {
            final JSONObject jsonObjectMessage = jsonObject.optJSONObject("messages");
            if (jsonObjectMessage.has("en")) {
                final String en = jsonObjectMessage.optString("en");
                final String ar = jsonObjectMessage.optString("ar");
                apiDetailCallback.onFaild(LangUtils.getCurrentLanguage().equalsIgnoreCase("ar") ? ar : en);
            } else {
                final Iterator<?> keys = jsonObjectMessage.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (jsonObjectMessage.get(key) instanceof JSONObject) {
                        final JSONObject mess = (JSONObject) jsonObjectMessage.get(key);
                        final String en = mess.optString("en");
                        final String ar = mess.optString("ar");
                        apiDetailCallback.onFaild(LangUtils.getCurrentLanguage().equalsIgnoreCase("ar") ? ar : en);
                        break;
                    }
                }
            }
        }
    }

    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }

    public void insertGeneralPlan(final GeneralPlan generalPlan) {
        AlainZooDB.getInstance().myPlanDao().deleteGeneral();
        AlainZooDB.getInstance().myPlanDao().insertOrReplaceAll(generalPlan);
    }
}
