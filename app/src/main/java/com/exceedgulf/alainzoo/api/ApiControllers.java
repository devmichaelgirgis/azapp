package com.exceedgulf.alainzoo.api;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Data.UrlsUtils;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.database.models.AnimalsCategory;
import com.exceedgulf.alainzoo.database.models.Attraction;
import com.exceedgulf.alainzoo.database.models.Avatars;
import com.exceedgulf.alainzoo.database.models.BeaconModel;
import com.exceedgulf.alainzoo.database.models.CameraFrame;
import com.exceedgulf.alainzoo.database.models.Education;
import com.exceedgulf.alainzoo.database.models.Emirates;
import com.exceedgulf.alainzoo.database.models.Experience;
import com.exceedgulf.alainzoo.database.models.ExploreMap;
import com.exceedgulf.alainzoo.database.models.FAQs;
import com.exceedgulf.alainzoo.database.models.FeedbackCategory;
import com.exceedgulf.alainzoo.database.models.Games;
import com.exceedgulf.alainzoo.database.models.HappeningNow;
import com.exceedgulf.alainzoo.database.models.LeaderBoard;
import com.exceedgulf.alainzoo.database.models.Nationalities;
import com.exceedgulf.alainzoo.database.models.OpeningHours;
import com.exceedgulf.alainzoo.database.models.Rating;
import com.exceedgulf.alainzoo.database.models.SearchDataModel;
import com.exceedgulf.alainzoo.database.models.ShuttleService;
import com.exceedgulf.alainzoo.database.models.TreasureHuntStatus;
import com.exceedgulf.alainzoo.database.models.VisitorService;
import com.exceedgulf.alainzoo.database.models.WhatsNew;
import com.exceedgulf.alainzoo.models.ClosingHourModel;
import com.exceedgulf.alainzoo.models.FeedbackModel;
import com.exceedgulf.alainzoo.models.NotificationModel;
import com.exceedgulf.alainzoo.models.OpeningHourFront;
import com.exceedgulf.alainzoo.models.ServiceFormModel;
import com.exceedgulf.alainzoo.models.TokenModel;
import com.exceedgulf.alainzoo.utils.LangUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Callback;

/**
 * Created by P.G on 26/12/17.
 */

public class ApiControllers {
    private static ApiControllers apiControllers;

    public static ApiControllers getApiControllers() {
        if (apiControllers == null) {
            apiControllers = new ApiControllers();
        }
        return apiControllers;
    }

    private ApiInterface getApiInterface() {
        return AppAlainzoo.getAppAlainzoo().getRetrofitAdapter().create(ApiInterface.class);
    }

    private ApiInterface getApiInterfaceSecond() {
        return AppAlainzoo.getAppAlainzoo().getRetrofitAdapterSecond().create(ApiInterface.class);
    }

    public void getAttractions(final int pageNumber, final Callback<ArrayList<Attraction>> attractionlistCallback) {
        getApiInterface().getAttractionsList(UrlsUtils.getAttractionsUrl(pageNumber)).enqueue(attractionlistCallback);
    }

    public void getAttractionsDetail(final int id, final Callback<ArrayList<Attraction>> attractionlistDetailCallback) {
        getApiInterface().getattractionDetail(id, LangUtils.getCurrentLanguage()).enqueue(attractionlistDetailCallback);
    }

    public void getExperiences(final Integer pageNumber, final Callback<ArrayList<Experience>> experienceslistCallback) {
        getApiInterface().getExperiencesList(UrlsUtils.getExperiencesList(pageNumber)).enqueue(experienceslistCallback);
    }

    public void getExperiencesDetail(final int id, final Callback<ArrayList<Experience>> experienceslistCallback) {
        getApiInterface().getexperiencesDetail(id, LangUtils.getCurrentLanguage()).enqueue(experienceslistCallback);
    }

    public void postFeedback(final String url, final FeedbackModel feedbackModel, final Callback<ResponseBody> feedbackResponseCallback) {
        getApiInterfaceSecond().postFeedback(url, feedbackModel).enqueue(feedbackResponseCallback);
    }

    public void getFeedbackCategory(final Callback<ArrayList<FeedbackCategory>> feedbackResponseCallback) {
        getApiInterfaceSecond().getFeedbackCategory(UrlsUtils.getFeedbackCategoriesUrl()).enqueue(feedbackResponseCallback);
    }


    public void userLogin(final RequestBody requestBody, final Callback<ResponseBody> loginResponseModelCallback) {
        getApiInterfaceSecond().userLogin(requestBody).enqueue(loginResponseModelCallback);
    }

    public void resetPassword(final RequestBody requestBody, final Callback<ResponseBody> loginResponseModelCallback) {
        getApiInterface().resetPassword(requestBody).enqueue(loginResponseModelCallback);
    }

    public void postLogout(final Callback<ResponseBody> feedbackResponseCallback) {
        getApiInterfaceSecond().postLogout(UrlsUtils.postLogoutUrl()).enqueue(feedbackResponseCallback);
    }

    public void postRegister(final RequestBody requestBody, final Callback<ResponseBody> feedbackResponseCallback) {
        getApiInterfaceSecond().postRegister(requestBody).enqueue(feedbackResponseCallback);
    }

    public void postCreatePlan(final RequestBody requestBody, final Callback<ResponseBody> responseBodyCallback) {
        getApiInterfaceSecond().postCreatePlan(requestBody).enqueue(responseBodyCallback);
    }

    public void postCreateGamification(final RequestBody requestBody, final Callback<ResponseBody> feedbackResponseCallback) {
        getApiInterfaceSecond().postCreateGamification(requestBody).enqueue(feedbackResponseCallback);
    }

    public void postApplicationToken(final Callback<TokenModel> tokenModelCallback) {
        getApiInterfaceSecond().postApplicationToken(Constants.CLIENT_ID, Constants.CLIENT_SECRET, Constants.GRANT_TYPE).enqueue(tokenModelCallback);
    }

    public void postUserAccessToken(final String username, final String password, final Callback<TokenModel> tokenModelCallback) {
        getApiInterfaceSecond().postUserAccessToken(Constants.CLIENT_ID, Constants.CLIENT_SECRET, Constants.USER_GRANT_TYPE, username, password).enqueue(tokenModelCallback);
    }

    public void postRefreshToken(final String refreshToken, final Callback<TokenModel> tokenModelCallback) {
        getApiInterfaceSecond().postRefreshToken(Constants.CLIENT_ID, Constants.CLIENT_SECRET, Constants.REFRESH_GRANT_TYPE, refreshToken).enqueue(tokenModelCallback);
    }

    public void postSearch(final int page, final String title, final String type, final Callback<ArrayList<SearchDataModel>> arrayListCallback) {
        getApiInterfaceSecond().postSearch(UrlsUtils.postSearch(page, title, type)).enqueue(arrayListCallback);
    }

    /*Visitor Service, Education Inquiry Form*/
    public void postVisitorEducationInquiry(final String Url, final ServiceFormModel formModel, final Callback<ResponseBody> inquiryResponseCallback) {
        getApiInterfaceSecond().postVisitorInquiry(Url, formModel).enqueue(inquiryResponseCallback);
    }

    /* Home API*/
    public void getHomeAttractions(final Callback<ArrayList<Attraction>> attractionlistCallback) {
        getApiInterface().getAttractionsList(UrlsUtils.getHomeAttractions()).enqueue(attractionlistCallback);
    }

    public void getHomeExperiences(final Callback<ArrayList<Experience>> experListCallback) {
        getApiInterface().getExperiencesList(UrlsUtils.getHomeExperiences()).enqueue(experListCallback);
    }

    public void getExperienceRating(final int experienceId, final Callback<ArrayList<Rating>> listCallback) {
        getApiInterfaceSecond().getExperienceRating(UrlsUtils.getExperienceRating(experienceId)).enqueue(listCallback);
    }

    public void getClosingHours(final Callback<ArrayList<ClosingHourModel>> bodyCallback) {
        getApiInterface().getClosingHours(UrlsUtils.getClosingHoursUrl()).enqueue(bodyCallback);
    }

    public void getOpeningHoursFront(final Callback<ArrayList<OpeningHourFront>> bodyCallback) {
        getApiInterface().getOpeningHoursFront(UrlsUtils.getOpeningHoursFrontUrl()).enqueue(bodyCallback);
    }

    /* Animals API */
    public void getShuttleServiceList(Integer page, final Callback<ArrayList<ShuttleService>> serviceListCallback) {
        getApiInterface().getShuttleServiceList(UrlsUtils.getShuttleServiceURL(page)).enqueue(serviceListCallback);
    }

    /* Animals API */
    public void getAllAnimals(Integer page, final Callback<ArrayList<Animal>> animalsListCallback) {
        getApiInterface().getAnimalsList(UrlsUtils.getAllAnimalsURL(page)).enqueue(animalsListCallback);
    }

    public void getAllAnimalsByCategory(Integer page, Integer categoryId, final Callback<ArrayList<Animal>> animalsListCallback) {
        getApiInterface().getAnimalsList(UrlsUtils.getAllAnimalByCategoryURL(categoryId, page)).enqueue(animalsListCallback);
    }

    public void getAllTreasureHunts(final Callback<ResponseBody> treasureCallback) {
        getApiInterface().getTreasureHuntList(UrlsUtils.getAllTreasureHunt()).enqueue(treasureCallback);
    }

    public void getTreasureHuntStatus(int id, final Callback<ArrayList<TreasureHuntStatus>> treasureCallback) {
        getApiInterfaceSecond().getTreasureHuntStatus(UrlsUtils.getTreasureHuntStatus(id)).enqueue(treasureCallback);
    }

    public void getAllCompletedTreasureHuntsList(final String url, final Callback<ResponseBody> treasureCallback) {
        getApiInterfaceSecond().getTreasureHuntCompletedList(url).enqueue(treasureCallback);
    }

    public void getAllPendingTreasureHuntsList(final String url, final Callback<ResponseBody> treasureCallback) {
        getApiInterfaceSecond().getTreasureHuntPendingList(url).enqueue(treasureCallback);
    }

    public void searchAboutAnimals(Integer page, String name, final Callback<ArrayList<Animal>> animalsListCallback) {
        getApiInterface().getAnimalsList(UrlsUtils.searchAboutAnimalsURL(name, page)).enqueue(animalsListCallback);
    }

    public void getAnimalLanding(Integer animalId, final Callback<List<Animal>> animalCallback) {
        getApiInterface().getAnimalDetails(UrlsUtils.getAnimalLandingUrl(animalId)).enqueue(animalCallback);
    }

    public void getAllAnimalsCategories(final Callback<ArrayList<AnimalsCategory>> animalsCategoryListCallback) {
        getApiInterface().getAnimalsCateogeriesList(UrlsUtils.getAllAnimalsCategoriesURL()).enqueue(animalsCategoryListCallback);
    }


    /*whats New */

    public void getWhatsNew(final Callback<ArrayList<WhatsNew>> whatsNewListCallback) {
        getApiInterface().getWhatsNewsList(UrlsUtils.getAllWhatsNewURL(null)).enqueue(whatsNewListCallback);
    }

    public void searchAboutWhatsNew(Integer pageNumber, String name, final Callback<ArrayList<WhatsNew>> whatsNewListCallback) {
        getApiInterface().getWhatsNewsList(UrlsUtils.getWhatsNewByTitleUrl(pageNumber, name)).enqueue(whatsNewListCallback);
    }

    /*happening now */

    public void getHappeningNow(final Callback<ArrayList<HappeningNow>> happeningNowListCallback) {
        getApiInterface().gethappeningNow(UrlsUtils.getAllHappeningNowURL(null)).enqueue(happeningNowListCallback);
    }


    /*FAQs*/
    public void getFAQs(final Callback<ArrayList<FAQs>> faqsCallback) {
        getApiInterface().getFAQs(UrlsUtils.getFAQSUrl()).enqueue(faqsCallback);
    }

    /*Education*/

    public void getAllEducation(String serviceId, Integer page, final Callback<ArrayList<Education>> educationsListCallback) {
        getApiInterface().getEducationsList(UrlsUtils.getServicesUrl(serviceId, page)).enqueue(educationsListCallback);
    }

    /*Visitor Services*/

    public void getAllVisitorServices(String serviceId, Integer page, final Callback<ArrayList<VisitorService>> visitorListCallback) {
        getApiInterface().getVisitorServicesList(UrlsUtils.getServicesUrl(serviceId, page)).enqueue(visitorListCallback);
    }

    public void getAllExploreMapFeatures(final Callback<ExploreMap> exploreMapCallback) {
        getApiInterface().getExploreMapFeature(UrlsUtils.getExploreMapUrl()).enqueue(exploreMapCallback);
    }

    public void getAllBeacons(final Callback<ArrayList<BeaconModel>> beaconModelCallback) {
        getApiInterfaceSecond().getBeacons(UrlsUtils.getBeaconUrl()).enqueue(beaconModelCallback);
    }

    public void getAllExploreZoo(final Callback<ResponseBody> responseBodyCallback) {
        getApiInterface().getExploreZoo(UrlsUtils.getExploreMapUrl()).enqueue(responseBodyCallback);
    }

    public void getOpeningHours(final Callback<ArrayList<OpeningHours>> openinghourslistCallback) {
        getApiInterface().getOpeningHoursList(UrlsUtils.getOpeningHoursUrl()).enqueue(openinghourslistCallback);
    }

    public void getAvatars(final Callback<ArrayList<Avatars>> avatarslistCallback) {
        getApiInterface().getAvatarsList(UrlsUtils.getAllAvatarsUrl()).enqueue(avatarslistCallback);
    }

    public void getUserDetails(final Callback<ResponseBody> responseBodyCallback) {
        getApiInterfaceSecond().getUserDetails(UrlsUtils.getUserDetails()).enqueue(responseBodyCallback);
    }

    public void getRecommendedPlans(final Callback<ResponseBody> responseBodyCallback) {
        getApiInterfaceSecond().getPlanes(UrlsUtils.getRecommendedPlanesUrl()).enqueue(responseBodyCallback);
    }

    public void getMyPlans(final Callback<ResponseBody> responseBodyCallback) {
        getApiInterfaceSecond().getPlanes(UrlsUtils.getMyPlanesUrl()).enqueue(responseBodyCallback);
    }
//    public void getMyPlans(final Callback<ResponseBody> responseBodyCallback) {
//        getApiInterfaceSecond().getPlanes("http://www.mocky.io/v2/5a96a9ba3200006e005e2ed0").enqueue(responseBodyCallback);
//    }


    public void getGames(final Callback<ArrayList<Games>> responseBodyCallback) {
        getApiInterface().getGames(UrlsUtils.getGamesUrl()).enqueue(responseBodyCallback);
    }

    public void getPagesContant(final String url, final Callback<ResponseBody> responseBodyCallback) {
        getApiInterfaceSecond().getPagesContant(url).enqueue(responseBodyCallback);
    }

    public void submitVote(final RequestBody requestBody, final Callback<ResponseBody> responseBodyCallback) {
        getApiInterfaceSecond().submitVote(requestBody).enqueue(responseBodyCallback);
    }

    public void getEmirates(final Callback<ArrayList<Emirates>> emiratesListCallback) {
        getApiInterface().getEmiratesList(UrlsUtils.getEmiratesURL()).enqueue(emiratesListCallback);
    }

    public void getNationalities(final Callback<ArrayList<Nationalities>> nationalitiesListCallback) {
        getApiInterface().getNationalitiesList(UrlsUtils.getNationalitiesURL()).enqueue(nationalitiesListCallback);
    }

    public void postSosRequest(final RequestBody requestBody, final Callback<ResponseBody> sosResponseModelCallback) {
        getApiInterfaceSecond().postSosRequest(requestBody).enqueue(sosResponseModelCallback);
    }

    public void createFamilyMember(final RequestBody requestBody, final Callback<ResponseBody> responseBodyCallback) {
        getApiInterfaceSecond().createFamilyMember(requestBody).enqueue(responseBodyCallback);
    }

    public void getAllNotifications(final Callback<ArrayList<NotificationModel>> notificationModelCallback) {
        getApiInterface().getNotificationList(UrlsUtils.getNotificationsUrl()).enqueue(notificationModelCallback);
    }

    public void updateProfile(final String userId, final RequestBody requestBody, final Callback<ResponseBody> updateProfileCallback) {
        getApiInterfaceSecond().updateProfile(userId, "json", requestBody).enqueue(updateProfileCallback);
    }

    public void changePassword(final String userId, final RequestBody requestBody, final Callback<ResponseBody> updateProfileCallback) {
        getApiInterfaceSecond().changePassword(userId, "json", requestBody).enqueue(updateProfileCallback);
    }

    public void getLeaderBoardList(Callback<ArrayList<LeaderBoard>> callback) {
        getApiInterfaceSecond().getLeaderBoardList(UrlsUtils.getLeaderBoard()).enqueue(callback);
    }

    public void deleteMyPlan(Callback<ResponseBody> bodyCallback) {
        getApiInterfaceSecond().deleteMyPlan(UrlsUtils.getDeletePlan()).enqueue(bodyCallback);
    }

    public void getCameraFrames(final Callback<ArrayList<CameraFrame>> cameraframeslistCallback) {
        getApiInterface().getCameraFramesList(UrlsUtils.getAllCameraFramesUrl()).enqueue(cameraframeslistCallback);
    }

}
