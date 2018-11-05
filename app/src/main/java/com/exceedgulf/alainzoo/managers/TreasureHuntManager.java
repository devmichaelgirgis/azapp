package com.exceedgulf.alainzoo.managers;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.database.models.TreasureHunt;
import com.exceedgulf.alainzoo.models.TreasurLeaderBoard;
import com.exceedgulf.alainzoo.utils.NetUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P.P on 20/2/2018.
 */

public class TreasureHuntManager {
    private static final String TAG = "TreasureHuntManager";
    private static TreasureHuntManager treasureHuntManager;

    public static TreasureHuntManager getTreasureHuntManager() {
        if (treasureHuntManager == null) {
            treasureHuntManager = new TreasureHuntManager();
        }
        return treasureHuntManager;
    }

    public void getAllEntitiesData(final ApiCallback apiCallback) {
//        final ArrayList<BeaconModel> beaconModelArrayList = (ArrayList<BeaconModel>) (getAllEntitiesFromDB());
//        if (beaconModelArrayList.size() > 0 && !isOlderData()) {
//            if (!NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
//                apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
//            }
//            apiCallback.onSuccess(beaconModelArrayList, false);
//        } else {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getAllTreasureHunts(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (checkValidateResponce(response)) {
                        try {
                            final ArrayList<TreasureHunt> treasureHunts = new ArrayList<>();
                            final String strResponse = response.body().string();
                            final JSONArray jsonArray = new JSONArray(strResponse);
                            if (jsonArray.length() > 0) {
                                final Gson gson = new Gson();
                                final TreasureHunt treasureHunt = gson.fromJson(jsonArray.get(0).toString(), TreasureHunt.class);
                                treasureHunts.add(treasureHunt);
                                apiCallback.onSuccess(treasureHunts, false);
                            } else {
                                apiCallback.onSuccess(treasureHunts, false);
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                        }

                    } else {
                        apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                }

            });
        } else {
            apiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_));
        }
    }

    public boolean checkValidateResponce(Response response) {
        return response != null && response.body() != null && response.isSuccessful();
    }

    public void getTreaserComletedList(final int id, final int pageNumber, final boolean isFirstTime, final ApiCallback listApiCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getAllCompletedTreasureHuntsList(Constants.TRESURHUNT_COMPLETED + id + "?page=" + pageNumber, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    getComletedListResponseData(response, listApiCallback, true, isFirstTime);
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

    public void getTreaserPendingList(final int id, final int pageNumber, final ApiCallback listApiCallback) {
        if (NetUtil.isNetworkAvailable(AppAlainzoo.getAppAlainzoo())) {
            ApiControllers.getApiControllers().getAllPendingTreasureHuntsList(Constants.TRESURHUNT_PENDING + id + "?page=" + pageNumber, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    getComletedListResponseData(response, listApiCallback, true, false);
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

    Comparator<TreasurLeaderBoard> leaderBoardComparator
            = new Comparator<TreasurLeaderBoard>() {

        public int compare(TreasurLeaderBoard leaderBoard1, TreasurLeaderBoard leaderBoard2) {
            //ascending order
            return leaderBoard1.compareTo(leaderBoard2);
        }

    };

    private void getComletedListResponseData(Response<ResponseBody> response, final ApiCallback listApiCallback, final boolean isCompleted, boolean isFirstTime) {
        final ArrayList<TreasurLeaderBoard> treasurLeaderBoardArrayList = new ArrayList<>();
        if (response.isSuccessful()) {
            try {
                final String responseData = response.body().string();
                final JSONArray jsonArrayLeaderboard = new JSONArray(responseData);
                for (int i = 0; i < jsonArrayLeaderboard.length(); i++) {
                    final TreasurLeaderBoard treasurLeaderBoard = new TreasurLeaderBoard();
                    final JSONObject rootObject = jsonArrayLeaderboard.getJSONObject(i);
                    if (rootObject.has("current_user_rank")) {
                        continue;
                    }
                    final boolean current_user = rootObject.optBoolean("current_user");
                    final String rank = rootObject.optString("rank");
                    final String total_hints = rootObject.optString("total_hints");
                    final JSONArray user = rootObject.optJSONArray("user");
                    if (user.length() > 0) {
                        final JSONObject jsonObjectUser = user.getJSONObject(0);
                        final int id = jsonObjectUser.optInt("id");
                        final String mobile = jsonObjectUser.optString("mobile");
                        final String name = jsonObjectUser.optString("name");
                        final String avatar = jsonObjectUser.optString("avatar");
                        final String gender = jsonObjectUser.optString("gender");
                        final String current_points = jsonObjectUser.optString("current_points");
                        final String total_points = jsonObjectUser.optString("total_points");

                        treasurLeaderBoard.setUser_id(id);
                        treasurLeaderBoard.setMobile(mobile);
                        treasurLeaderBoard.setName(name);
                        treasurLeaderBoard.setAvatar(avatar);
                        treasurLeaderBoard.setGender(gender);
                        treasurLeaderBoard.setCurrent_points(current_points);
                        treasurLeaderBoard.setTotal_points(total_points);
                    }
                    treasurLeaderBoard.setCurrent_user(current_user);
                    treasurLeaderBoard.setRank(rank);
                    treasurLeaderBoard.setTotal_hints(total_hints);
                    treasurLeaderBoard.setIsshildshow(false);
                    treasurLeaderBoardArrayList.add(treasurLeaderBoard);
                }

            } catch (Exception e) {
                e.printStackTrace();
                listApiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
                return;
            }
            if (isCompleted && isFirstTime) {
                if (treasurLeaderBoardArrayList.size() > 0) {
                    Collections.sort(treasurLeaderBoardArrayList, leaderBoardComparator);
                    for (int p = 0; p < treasurLeaderBoardArrayList.size(); p++) {
                        if (p == 0 || p == 1 || p == 2) {
                            treasurLeaderBoardArrayList.get(p).setIsshildshow(true);
                        }
                        if (p == 2) {
                            break;
                        }
                    }
                }
            }
            boolean isAllowLoading = true;
            if (isCompleted && treasurLeaderBoardArrayList.size() < 10) {
                isAllowLoading = false;
            } else if (treasurLeaderBoardArrayList.size() < 10) {
                isAllowLoading = false;
            }
            listApiCallback.onSuccess(treasurLeaderBoardArrayList, isAllowLoading);
        } else {
            listApiCallback.onFaild(AppAlainzoo.getAppAlainzoo().getString(R.string.error));
        }
    }

}
