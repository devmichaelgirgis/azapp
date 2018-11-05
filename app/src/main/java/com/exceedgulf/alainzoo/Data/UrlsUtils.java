package com.exceedgulf.alainzoo.Data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.constants.TagsName;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.LangUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by michael.george on 12/13/2017.
 */

public class UrlsUtils {

    /* Animals */

    @NonNull
    public static String getAllAnimalsURL(Integer page) {
        final Uri.Builder builtUri = getBasicURL(Constants.ANIMAL_URL, page);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getShuttleServiceURL(Integer page) {
        final Uri.Builder builtUri = getBasicURL(Constants.SHUTTLE_SERVICE_LIST_URL, page);
        return builtUri.build().toString();
    }

    public static String getAllAnimalByCategoryURL(Integer categoryId, Integer page) {
        final Uri.Builder builtUri = getBasicURL(Constants.ANIMAL_URL, page);
        builtUri.appendQueryParameter(TagsName.CATEGORY_ID, Integer.toString(categoryId));
        return builtUri.build().toString();
    }

    @NonNull
    public static String getAnimalLandingUrl(Integer animalId) {
        final Uri.Builder builtUri = getBasicURL(Constants.ANIMAL_LANDING_URL, null);
        builtUri.appendPath(Integer.toString(animalId));
        return builtUri.build().toString();
    }

    @NonNull
    public static String getAllAnimalsCategoriesURL() {
        final Uri.Builder builtUri = getBasicURL(Constants.ANIMALS_CATEGORY_URL, null);
        return builtUri.build().toString();
    }

    @NonNull
    public static String postLogoutUrl() {
        Uri.Builder builtUri = Uri.parse(Constants.LOGOUT).buildUpon();
        builtUri.appendQueryParameter("_format", "json");
        builtUri.appendQueryParameter("token", SharedPrefceHelper.getInstance().get(PrefCons.LOGOUT_TOKEN, ""));
        return builtUri.build().toString();
    }

    public static String searchAboutAnimalsURL(String animal, int page) {
        final Uri.Builder builtUri = getBasicURL(Constants.ANIMAL_URL, page);
        builtUri.appendQueryParameter(TagsName.NAME, animal);
        return builtUri.build().toString();
    }


    /* Attractions */

    @NonNull
    public static String getAttractionsUrl(int pagenumber) {
        final Uri.Builder builtUri = getBasicURL(Constants.ATTRACTION_ALL_URL, null);
        return builtUri.build().toString();

    }

    @NonNull
    public static String getAttractionLandingUrl(int attractionId) {
        final Uri.Builder builtUri = getBasicURL(Constants.ATTRACTION_ALL_URL, null);
        builtUri.appendPath(Integer.toString(attractionId));
        return builtUri.build().toString();

    }

    @NonNull
    public static String getFAQSUrl() {
        final Uri.Builder builtUri = getBasicURL(Constants.FAQS_URL, null);
        builtUri.appendQueryParameter(Constants.LANGUAGE_CODE, LangUtils.getCurrentLanguage());
        return builtUri.build().toString();
    }

    @NonNull
    public static String getFAQSCategoriesUrl() {
        final Uri.Builder builtUri = getBasicURL(Constants.FAQS_CATEGORIES, null);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getFeedbackCategoriesUrl() {
        final Uri.Builder builtUri = getBasicURL(Constants.FEEDBACK_CATEGORIES, null);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getRecommendedPlanesUrl() {
        final Uri.Builder builtUri = getBasicURL(Constants.RECOMMENDED_PLANES_URL, null);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getMyPlanesUrl() {
        final Uri.Builder builtUri = getBasicURL(Constants.GET_MY_PLAN, null);
        builtUri.appendQueryParameter("t", String.valueOf(System.currentTimeMillis()));
        return builtUri.build().toString();
    }

    @NonNull
    public static String getConversationByTitleUrl(String title) {
        final Uri.Builder builtUri = getBasicURL(Constants.CONVERSATION_URL, null);
        builtUri.appendQueryParameter(TagsName.TITLE, title);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getWhatsNewByTitleUrl(Integer page, String title) {
        final Uri.Builder builtUri = getBasicURL(Constants.CONVERSATION_URL, page);
        builtUri.appendQueryParameter(TagsName.TITLE, title);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getWhatsNewByTitleAndDateUrl(String title, Date startDateMin, Date startDateMax, Date endDateMin, Date endDateMax, Integer page) {
        final SimpleDateFormat sdfr = new SimpleDateFormat("YYYY-MM-DD");
        Uri.Builder builtUri = getBasicURL(Constants.CONVERSATION_URL, page);
        builtUri.appendQueryParameter(TagsName.TITLE, title);
        builtUri.appendQueryParameter(TagsName.START_DATE_MIN, sdfr.format(startDateMin));
        builtUri.appendQueryParameter(TagsName.START_DATE_MAX, sdfr.format(startDateMax));
        builtUri.appendQueryParameter(TagsName.END_DATE_MIN, sdfr.format(endDateMin));
        builtUri.appendQueryParameter(TagsName.END_DATE_MAX, sdfr.format(endDateMax));
        return builtUri.build().toString();

    }

    @NonNull
    public static String getAllWhatsNewURL(Integer page) {
        final Uri.Builder builtUri = getBasicURL(Constants.WHATSNEW_URL, page);
        return builtUri.build().toString();

    }

    @NonNull
    public static String getAllHappeningNowURL(Integer page) {
        final Uri.Builder builtUri = getBasicURL(Constants.HAPPENING_NOW_ALL_URL, page);
        return builtUri.build().toString();

    }

    /**
     * @param pageId - 151: Condition and terms.
     *               - 152: Privacy & Policy.
     *               - 8: ContactInfo Us.
     * @return
     */
    @NonNull
    public static String getTermsAndConditionsUrl(int pageId) {

        final Uri.Builder builtUri = getBasicURL(Constants.PAGE_URL, null);
        builtUri.appendPath(Integer.toString(pageId));
        return builtUri.build().toString();
    }

    /**
     * @param serviceTypeId - educations
     *                      - special_occasion.
     *                      - visitor_services
     * @return
     */
    @NonNull
    public static String getServicesUrl(String serviceTypeId, Integer page) {
        final Uri.Builder builtUri = getBasicURL(Constants.SERVICE_URL, page);
        builtUri.appendPath(serviceTypeId);
        builtUri.appendPath("all");
        return builtUri.build().toString();
    }

    public static String getExploreMapUrl() {
        final Uri.Builder builtUri = getBasicURL(Constants.EXPLORE_MAP_URL, null);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getOpeningHoursUrl() {
        final Uri.Builder builtUri = getBasicURL(Constants.OPENING_HOURS_URL, null);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getOpeningHoursFrontUrl() {
        final Uri.Builder builtUri = getBasicURL(Constants.OPENING_HOURS_FRONT_URL, null);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getClosingHoursUrl() {
        final Uri.Builder builtUri = getBasicURL(Constants.CLOSING_HOURS_URL, null);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getTicketsUrl() {
        final Uri.Builder builtUri = getBasicURL(Constants.TICKETS_URL, null);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getFeedbackEducationVisitorServiceUrl() {
        final Uri.Builder builtUri = getBasicURL(Constants.FEED_BACK_EDUCATION_VISITOR_SERVICE_URL, null);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getAllAvatarsUrl() {
        final Uri.Builder builtUri = getBasicURL(Constants.ALL_USER_AVATARS_URL, null);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getRegistrationUrl() {
        final Uri.Builder builtUri = getBasicURL(Constants.REGISTRATION_URL, null);
        return builtUri.build().toString();
    }


    @NonNull
    public static String getHomeExperiences() {
        final Uri.Builder builtUri = getBasicURL(Constants.EXPERIENCE_ALL_URL, null);
        builtUri.appendQueryParameter(TagsName.ITEMS_PER_PAGE, Integer.toString(Constants.HOME_ITEMS_PER_PAGE));
        return builtUri.build().toString();
    }

    @NonNull
    public static String getHomeAttractions() {
        final Uri.Builder builtUri = getBasicURL(Constants.ATTRACTIONS_URL, 0);
        builtUri.appendQueryParameter(TagsName.ITEMS_PER_PAGE, Integer.toString(Constants.HOME_ITEMS_PER_PAGE));
        return builtUri.build().toString();
    }

    @NonNull
    public static String getExperiencesList(Integer page) {
        final Uri.Builder builtUri = getBasicURL(Constants.EXPERIENCE_ALL_URL, page);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getExperienceDetails(Integer experienceId) {
        final Uri.Builder builtUri = getBasicURL(Constants.EXPERIENCE_LANDING_URL, null);
        builtUri.appendPath(Integer.toString(experienceId));
        return builtUri.build().toString();
    }

    @NonNull
    public static String getUserDetails() {
        final Uri.Builder builtUri = getBasicURL(Constants.GET_USER_INFORMATION, null);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getGamesUrl() {
        final Uri.Builder builtUri = getBasicURL(Constants.GET_GAMES, null);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getEmiratesURL() {
        final Uri.Builder builtUri = getBasicURL(Constants.GET_EMIRATES, null);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getNationalitiesURL() {
        final Uri.Builder builtUri = getBasicURL(Constants.GET_NATIONALITIES, null);
        return builtUri.build().toString();
    }

    @NonNull
    public static String getNotificationsUrl() {
        final Uri.Builder builtUri = getBasicURL(Constants.NOTIFICATIONS_URL, null);
        return builtUri.build().toString();
    }

    @NonNull
    public static String postSearch(final int page, String title, String type) {
        Uri.Builder builtUri = getBasicURL(Constants.SEARCH, page);
        builtUri.appendQueryParameter("title", title);
        if (!TextUtils.isEmpty(type)) {
            if (!type.equalsIgnoreCase("all")) {
                builtUri.appendQueryParameter("type", type);
            }
        }
        return builtUri.build().toString();
    }

    @NonNull
    public static String getExperienceRating(final int experienceId) {
        Uri.Builder builtUri = Uri.parse(Constants.EXPERIENCE_RATING).buildUpon();
        builtUri.appendPath(String.valueOf(experienceId));
        return builtUri.build().toString();
    }

    @NonNull
    public static String getBeaconUrl() {
        Uri.Builder builtUri = Uri.parse(Constants.GET_BEACON).buildUpon();
        return builtUri.build().toString();
    }

    @NonNull
    public static String getAllTreasureHunt() {
        Uri.Builder builtUri = Uri.parse(Constants.TREASURE_HUNT_URL).buildUpon();
        builtUri.appendQueryParameter(TagsName.LANGCODE, SharedPrefceHelper.getInstance().get(PrefCons.APP_LANGUAGE, "en"));
        return builtUri.build().toString();
    }

    @NonNull
    public static String getTreasureHuntStatus(int id) {
        Uri.Builder builtUri = Uri.parse(Constants.TREASURE_STATUS_URL).buildUpon();
        builtUri.appendPath(String.valueOf(id));
        return builtUri.build().toString();
    }

    @NonNull
    public static String getLeaderBoard() {
        Uri.Builder builtUri = Uri.parse(Constants.LEADERBOARD_URL).buildUpon();
        return builtUri.build().toString();
    }

    @NonNull
    public static String getDeletePlan() {
        Uri.Builder builtUri = Uri.parse(Constants.DELETE_PLAN_URL).buildUpon();
        return builtUri.build().toString();
    }

    @NonNull
    public static String getAllCameraFramesUrl() {
        final Uri.Builder builtUri = getBasicURL(Constants.ALL_CAMERA_FRAMES_URL, null);
        return builtUri.build().toString();
    }


    public static Uri.Builder getBasicURL(String url, Integer page) {
        final Uri.Builder builtUri = Uri.parse(url)
                .buildUpon();
        if (page != null) {
            builtUri.appendQueryParameter(TagsName.PAGE, Integer.toString(page));
            builtUri.appendQueryParameter(TagsName.ITEMS_PER_PAGE, Constants.List_ITEMS_PER_PAGE);
        }
        builtUri.appendQueryParameter(TagsName.LANGCODE, LangUtils.getCurrentLanguage());
        return builtUri;
    }

}
