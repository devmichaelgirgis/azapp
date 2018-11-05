package com.exceedgulf.alainzoo.api;

import android.arch.persistence.room.Delete;

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
import com.exceedgulf.alainzoo.models.LoginResponseModel;
import com.exceedgulf.alainzoo.models.NotificationModel;
import com.exceedgulf.alainzoo.models.OpeningHourFront;
import com.exceedgulf.alainzoo.models.ServiceFormModel;
import com.exceedgulf.alainzoo.models.TokenModel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Paras Ghasadiya on 26/12/17.
 */

public interface ApiInterface {

    @GET(Constants.EXPERIENCE_DETAIL_URL)
    Call<ArrayList<Experience>> getexperiencesDetail(@Path(Constants.ID) int id, @Query(Constants.LANGUAGE_CODE) String languagecode);

    @GET(Constants.ATTRACTION_DETAIL_URL)
    Call<ArrayList<Attraction>> getattractionDetail(@Path(Constants.ID) int id, @Query(Constants.LANGUAGE_CODE) String languagecode);

    @POST
    @Headers("Content-Type: application/json")
    Call<ResponseBody> postFeedback(@Url String url, @Body FeedbackModel feedbackModel);

    @GET
    Call<ArrayList<FeedbackCategory>> getFeedbackCategory(@Url String url);


    @POST
    @Headers("Content-Type: application/json")
    Call<ResponseBody> postVisitorInquiry(@Url String url, @Body ServiceFormModel formModel);


    @POST(Constants.LOGIN)
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<ResponseBody> userLogin(@Body RequestBody requestBody);

    @POST(Constants.RESET_PASSWORD)
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<ResponseBody> resetPassword(@Body RequestBody requestBody);

    @POST
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<ResponseBody> postLogout(@Url String url);


    @POST(Constants.REGISTER)
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<ResponseBody> postRegister(@Body RequestBody requestBody);


    @POST(Constants.CREATE_GAMIFICATION)
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<ResponseBody> postCreateGamification(@Body RequestBody requestBody);


    @POST(Constants.APPLICATION_TOKEN)
    @FormUrlEncoded
    @Headers("Accept: application/json")
    Call<TokenModel> postApplicationToken(@Field("client_id") String clientID, @Field("client_secret") String clientSecret, @Field("grant_type") String grantType);

    @POST(Constants.APPLICATION_TOKEN)
    @FormUrlEncoded
    @Headers("Accept: application/json")
    Call<TokenModel> postUserAccessToken(@Field("client_id") String clientID, @Field("client_secret") String clientSecret, @Field("grant_type") String grantType, @Field("username") String username, @Field("password") String password);

    @POST(Constants.APPLICATION_TOKEN)
    @FormUrlEncoded
    @Headers("Accept: application/json")
    Call<TokenModel> postRefreshToken(@Field("client_id") String clientID, @Field("client_secret") String clientSecret, @Field("grant_type") String grantType, @Field("refresh_token") String refreshToken);

    @GET
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<ArrayList<SearchDataModel>> postSearch(@Url String url);


    @POST(Constants.PLAN_CREATE)
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<ResponseBody> postCreatePlan(@Body RequestBody requestBody);

    @GET
    Call<ArrayList<ClosingHourModel>> getClosingHours(@Url String url);

    @GET
    Call<ArrayList<OpeningHourFront>> getOpeningHoursFront(@Url String url);

    @GET
    Call<ArrayList<FAQs>> getFAQs(@Url String url);

    @GET
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<ArrayList<Rating>> getExperienceRating(@Url String url);

    @GET
    Call<ArrayList<Attraction>> getAttractionsList(@Url String url);

    @GET
    Call<ResponseBody> getTreasureHuntList(@Url String url);

    @GET
    Call<ArrayList<TreasureHuntStatus>> getTreasureHuntStatus(@Url String url);

    @GET
    Call<ResponseBody> getTreasureHuntCompletedList(@Url String url);

    @GET
    Call<ResponseBody> getTreasureHuntPendingList(@Url String url);

    @GET
    Call<ArrayList<Experience>> getExperiencesList(@Url String url);

    @GET
    Call<ArrayList<WhatsNew>> getWhatsNewsList(@Url String url);

    @GET
    Call<ArrayList<Animal>> getAnimalsList(@Url String url);

    @GET
    Call<ArrayList<ShuttleService>> getShuttleServiceList(@Url String url);

    @GET
    Call<ArrayList<AnimalsCategory>> getAnimalsCateogeriesList(@Url String url);

    @GET
    Call<List<Animal>> getAnimalDetails(@Url String url);

    @GET
    Call<ArrayList<Education>> getEducationsList(@Url String url);

    @GET
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<ArrayList<LeaderBoard>> getLeaderBoardList(@Url String url);

    @GET
    Call<ArrayList<VisitorService>> getVisitorServicesList(@Url String url);

    @GET
    Call<ExploreMap> getExploreMapFeature(@Url String url);

    @GET
    Call<ArrayList<BeaconModel>> getBeacons(@Url String url);

    @GET
    Call<ResponseBody> getExploreZoo(@Url String url);

    @GET
    Call<ArrayList<OpeningHours>> getOpeningHoursList(@Url String url);

    @GET
    Call<ArrayList<Avatars>> getAvatarsList(@Url String url);

    @GET
    Call<ArrayList<HappeningNow>> gethappeningNow(@Url String url);

    @GET
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"})
    Call<ResponseBody> getUserDetails(@Url String url);

    @GET
    Call<ResponseBody> getPlanes(@Url String url);

    @GET
    Call<ArrayList<Games>> getGames(@Url String url);

    @GET
    Call<ResponseBody> getPagesContant(@Url String url);

    @POST(Constants.SUBMIT_VOTE)
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<ResponseBody> submitVote(@Body RequestBody requestBody);

    @GET
    Call<ArrayList<Emirates>> getEmiratesList(@Url String url);

    @GET
    Call<ArrayList<Nationalities>> getNationalitiesList(@Url String url);

    @POST(Constants.SOS_URL)
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<ResponseBody> postSosRequest(@Body RequestBody requestBody);

    @GET
    Call<ArrayList<NotificationModel>> getNotificationList(@Url String url);

    @PATCH(Constants.UPDATE_PROFILE)
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<ResponseBody> updateProfile(@Path("userId") String userId, @Query("_format") String format, @Body RequestBody requestBody);

    @POST(Constants.FAMILIY_MEMBER_CREATE)
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<ResponseBody> createFamilyMember(@Body RequestBody requestBody);

    @DELETE
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<ResponseBody> deleteMyPlan(@Url String url);

    @PATCH(Constants.CHANGE_PASSWORD)
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<ResponseBody> changePassword(@Path("userId") String userId, @Query("_format") String format, @Body RequestBody requestBody);

    @GET
    Call<ArrayList<CameraFrame>> getCameraFramesList(@Url String url);

}
