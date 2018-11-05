package com.exceedgulf.alainzoo.constants;


import com.exceedgulf.alainzoo.AppAlainzoo;

import java.io.File;

public interface Constants {

    String ZOO_CAMERA_FRAME = AppAlainzoo.getAppAlainzoo().getFilesDir().getAbsolutePath() + File.separator + "Zoo_frame" + File.separator;

    String MAPBOX_KEY = "pk.eyJ1IjoibW9obG9heSIsImEiOiJjajZodG9wazcwbHprMndueTFsaGlwbGVkIn0.bYKN4ejyhP2l4VXy3HPgHg";
    String List_ITEMS_PER_PAGE = "10";
    Integer HOME_ITEMS_PER_PAGE = 3;

    String CLIENT_ID = "4b3caf71-9aa8-4844-b910-2a706ce824b7";
    String CLIENT_SECRET = "93dfcaf3d923ec47edb8580667473987";
    String GRANT_TYPE = "client_credentials";
    String USER_GRANT_TYPE = "password";
    String REFRESH_GRANT_TYPE = "refresh_token";

    String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    String ACCEPT = "application/json";

    String ID = "id";
    String DOMAIN_URL = "http://ain-dev.exceedgulf.net/api/v1/";
    String DOMAIN_URL_SECOND = "http://ain-dev.exceedgulf.net/";
    String LANGUAGE_CODE = "langcode";
    String ITEMS_PER_PAGE = "items_per_page=" + List_ITEMS_PER_PAGE;
    String PAGE_NUMBER = "page";

    //String EXPERIENCE_ALL_URL = "experiences/all?" + ITEMS_PER_PAGE;
    String EXPERIENCE_ALL_URL = DOMAIN_URL + "experiences/all?";
    String EXPERIENCE_DETAIL_URL = "experience/{id}";

    String ATTRACTION_ALL_URL = "attractions/all?";
    String ATTRACTION_DETAIL_URL = "attraction/{id}";

    String HAPPENING_NOW_ALL_URL = DOMAIN_URL + "happening-now/all";

    String GET_BEACON = DOMAIN_URL + "beacon/all";

    String VISITER_EDUCATION_FEEDBACK_INQUERYEN = DOMAIN_URL_SECOND + "webform_rest/submit?_format=json";
    String VISITER_EDUCATION_FEEDBACK_INQUERYAR = DOMAIN_URL_SECOND + "ar/webform_rest/submit?_format=json";
    String FEEDBACK_CATEGORIES = DOMAIN_URL + "types/feedback?";


    String SEARCH = DOMAIN_URL + "zoo-search?";

    String EXPERIENCE_RATING = DOMAIN_URL + "experience/user-rating/";

    String APPLICATION_TOKEN = DOMAIN_URL_SECOND + "oauth/token";

    String LOGIN = DOMAIN_URL_SECOND + "user/login";
    String RESET_PASSWORD = DOMAIN_URL_SECOND + "user/password?_format=json";
    String REGISTER = DOMAIN_URL_SECOND + "user/register?_format=json";
    String FAQS_CATEGORIES = DOMAIN_URL + "types/faqs?";

    String CREATE_GAMIFICATION = DOMAIN_URL_SECOND + "gamification/create?_format=json";

    String RECOMMENDED_PLANES_URL = DOMAIN_URL + "recommended-plans/all?";
    String CONVERSATION_URL = DOMAIN_URL + "conservations/all";
    String TREASURE_HUNT_URL = DOMAIN_URL + "treasure-hunts/all";
    String WHATSNEW_URL = DOMAIN_URL + "whats-new/all";
    String TICKETS_URL = DOMAIN_URL + "tickets/all?";

    String ALL_USER_AVATARS_URL = DOMAIN_URL + "avatars/all";

    String CLOSING_HOURS_URL = DOMAIN_URL + "closing-hours/all?";
    String OPENING_HOURS_URL = DOMAIN_URL + "opening-hours/all?";
    String OPENING_HOURS_FRONT_URL = DOMAIN_URL + "opening-hours-frontpage/all?";

    String PAGE_URL = DOMAIN_URL + "page/";

    String SERVICE_URL = DOMAIN_URL + "services/";

    String EXPLORE_MAP_URL = DOMAIN_URL + "map/all";

    String CONSERVATIONS_URL = DOMAIN_URL + "conservations/all?";

    String RECOMMENDED_PLANS_URL = DOMAIN_URL + "recommended-plans/all?";
    String ATTRACTIONS_URL = DOMAIN_URL + "attractions/all";
    String FAQS_CATEGORIES_URL = DOMAIN_URL + "types/faqs?";

    String FAQS_URL = DOMAIN_URL + "faqs/all?";
    String ANIMAL_URL = DOMAIN_URL + "animals/all?";

    String SHUTTLE_SERVICE_LIST_URL = DOMAIN_URL + "shuttle/all?";

    String ANIMALS_CATEGORY_URL = DOMAIN_URL + "types/animals?";
    String ANIMAL_LANDING_URL = DOMAIN_URL + "animal/";

    String EXPERIENCE_LANDING_URL = DOMAIN_URL + "experience/";

    String TREASURE_STATUS_URL = DOMAIN_URL + "gamification/user/treasures/";

    String LEADERBOARD_URL = DOMAIN_URL + "gamification/leaderboard/last-month";

    String DELETE_PLAN_URL = DOMAIN_URL_SECOND + "my_plan/delete";

    String REGISTRATION_URL = "http://ain-dev.exceedgulf.net/user/register?_format=json";

    String FEED_BACK_EDUCATION_VISITOR_SERVICE_URL = "http://ain-dev.exceedgulf.net/webform_rest/submit?_format=json";
    int SPLASH_TIME_OUT = 1000;

    String GET_USER_INFORMATION = DOMAIN_URL_SECOND + "user?_format=json";

    String TAG_HOME_FRAGMENT = "HOME_FRAG";

    String TAG_ANIMAL_FRAGMENT = "ANIMAL_FRAG";
    String TAG_EXPERIENCE_FRAGMENT = "EXPERIENCE_FRAGM";
    String TAG_SEARCH_FRAGMENT = "SEARCH_FRAG";
    String TAG_PROFILE_FRAGMENT = "PROFILE_FRAG";
    String TIME_STAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    /*Params*/
    String LOGIN_USERNAME = "username";

    String GAMIFICATION_TYPE = "type";
    String GAMIFICATION_POINTS = "points";
    String GAMIFICATION_USER = "user";
    String GAMIFICATION_ACTION_ID = "action_id";
    String GAMIFICATION_TREASURE_HUNT = "treasure_hunt";
    String GAMIFICATION_HINT = "hint";


    String LOGIN_PASSWORD = "pass";
    String IS_LOGGEDIN = "IS_LOGGEDIN";

    String REFRESH_TOKEN = "REFRESH_TOKEN";
    String LOGOUT = DOMAIN_URL_SECOND + "user/logout?";

    String GET_GAMES = DOMAIN_URL + "games?";
    String GET_CONTACT_US = "/page/contact";
    String GET_ABOUT_US = "/page/about";
    String GET_DO_AND_DO_NOT = "/page/do-do-not";
    String GET_TERMS = "/page/terms";
    String GET_PRIVACY = "/page/privacy";
    String GET_MY_PLAN = DOMAIN_URL + "my-plan/all";
    String SUBMIT_VOTE = DOMAIN_URL_SECOND + "entity/vote?_format=json";
    String GET_EMIRATES = DOMAIN_URL + "emirates";

    String GET_NATIONALITIES = DOMAIN_URL + "nationalities";
    String SOS_URL = DOMAIN_URL_SECOND + "node?_format=json";
    String NOTIFICATIONS_URL = DOMAIN_URL + "notifications";
    String UPDATE_PROFILE = DOMAIN_URL_SECOND + "user/{userId}";

    String PLAN_CREATE = DOMAIN_URL_SECOND + "visit_plan/create?_format=json";
    String FAMILIY_MEMBER_CREATE = DOMAIN_URL_SECOND + "family_member/create?_format=json";

    String TRESURHUNT_COMPLETED = DOMAIN_URL + "gamification/user/hints/completed/";
    String TRESURHUNT_PENDING = DOMAIN_URL + "treasure-hunt/leaderboard/";

    String CHANGE_PASSWORD = DOMAIN_URL_SECOND + "user/{userId}";
    String ALL_CAMERA_FRAMES_URL = DOMAIN_URL + "frame/all";
}
