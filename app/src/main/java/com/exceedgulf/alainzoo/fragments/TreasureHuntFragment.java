package com.exceedgulf.alainzoo.fragments;


import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.TreasureHuntAdapter;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.database.models.HintsItem;
import com.exceedgulf.alainzoo.database.models.TreasureHunt;
import com.exceedgulf.alainzoo.database.models.TreasureHuntStatus;
import com.exceedgulf.alainzoo.databinding.FragmentTreasureHuntBinding;
import com.exceedgulf.alainzoo.managers.GamificationManager;
import com.exceedgulf.alainzoo.managers.TreasureHuntManager;
import com.exceedgulf.alainzoo.models.TokenModel;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.NetUtil;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P.G on 02/06/2018
 */
public class TreasureHuntFragment extends BaseFragment {
    private FragmentTreasureHuntBinding treasureHuntBinding;
    private TreasureHuntAdapter treasureHuntAdapter;
    private ArrayList<HintsItem> hintsItemArrayList;
    private TreasureHunt treasureHunt;
    private HashMap<String, Integer> hintData = new HashMap<>();
    private String strRemaining = "";
    private AlertDialog alertTreasureDetail;
    private Menu menuTresur;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        treasureHuntBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_treasure_hunt, container, false);
        return treasureHuntBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.treasure_hunt), getResources().getColor(R.color.colorTresurhunt), null, false);
        hintsItemArrayList = new ArrayList<>();
        treasureHuntAdapter = new TreasureHuntAdapter(getActivity());
        treasureHuntBinding.frTresRvmain.setLayoutManager(new LinearLayoutManager(getActivity()));
        treasureHuntBinding.frTresRvmain.setAdapter(treasureHuntAdapter);
        treasureHuntBinding.frProfileTvLeaderboard.setOnClickListener(this);
        //treasureHuntBinding.frTreasurTvRank.setText(getString(R.string.rank) + " " + String.format(Locale.ENGLISH, "%d", 32));
//        getTreasureHunt();

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == treasureHuntBinding.frProfileTvLeaderboard) {
            if (treasureHunt != null) {
                ((HomeActivity) getActivity()).addFragment(TreasureHuntFragment.this, TreasureLeaderBoardFragment.getTreasurhuntLeaderbordFragment(treasureHunt.getId()));
            }
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.treasurehunt, menu);
        this.menuTresur = menu;
        getTreasureHunt();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (!isOnClick()) {
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.action_info) {
            if (treasureHunt != null) {
                showTreasureDetailsDialog();
            }
        } else if (id == R.id.action_camera) {
            if (hintsItemArrayList.size() > 0 && treasureHunt != null) {
                final QRCodeFragment qrCodeFragment = new QRCodeFragment();
                final Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("hint", hintsItemArrayList);
                bundle.putInt("treasure_id", treasureHunt.getId());
                qrCodeFragment.setArguments(bundle);
                qrCodeFragment.setTargetFragment(TreasureHuntFragment.this, 300);
                qrCodeFragment.show(getFragmentManager(), QRCodeFragment.class.getSimpleName());
                //((HomeActivity) getActivity()).addFragment(TreasureHuntFragment.this, qrCodeFragment);
            } else {
                getTreasureHunt();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTreasureDetailsDialog() {
        if (alertTreasureDetail != null && alertTreasureDetail.isShowing()) {
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_hint_success, null);
        final TextView tvTitle = dialogView.findViewById(R.id.dialog_hint_tvTitle);
        final TextView tvSubTitle = dialogView.findViewById(R.id.dialog_hint_tvSubTitle);
        final TextView tvHintMessage = dialogView.findViewById(R.id.dialog_hint_tvHintMessage);
        final TextView tvCollect = dialogView.findViewById(R.id.dialog_hint_tvCollect);
        //tvTitle.setVisibility(View.GONE);
        tvSubTitle.setVisibility(View.GONE);
        tvTitle.setText(getString(R.string.mHelp));
        tvHintMessage.setText(treasureHunt.getDetails());
        tvCollect.setText(getString(R.string.close));
        builder.setView(dialogView);
        alertTreasureDetail = builder.create();
        tvCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertTreasureDetail.dismiss();
            }
        });
        alertTreasureDetail.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final int width = (int) getResources().getDimension(R.dimen._230sdp);
        alertTreasureDetail.show();
        alertTreasureDetail.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.treasure_hunt), getResources().getColor(R.color.colorTresurhunt), null, false);
        }
    }

    private void getTreasureHunt() {
        treasureHuntBinding.frTresTvEmptyView.setText(getString(R.string.fetching_data));
        if (treasureHunt != null) {
            return;
        }
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        TreasureHuntManager.getTreasureHuntManager().getAllEntitiesData(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    final ArrayList<TreasureHunt> treasureHunts = resultList;
                    if (treasureHunts.size() > 0) {
                        treasureHuntBinding.frTreasurLlMain.setVisibility(View.VISIBLE);
                        treasureHuntBinding.frTreasurLlHiden.setVisibility(View.GONE);
                        treasureHuntBinding.frTresTvEmptyView.setVisibility(View.GONE);
                        treasureHunt = treasureHunts.get(0);
                        hintsItemArrayList.addAll(treasureHunt.getHints());
                        if (isTreasureHuntRunning(treasureHunt.getStartDate(), treasureHunt.getEndDate())) {
                            treasureHuntAdapter.addItem((ArrayList) treasureHunt.getHints());
                            getTreasureHuntStatus(treasureHunt.getId(), true);
                            for (int i = 0; i < treasureHunts.get(0).getHints().size(); i++) {
                                final HintsItem hintsItem = treasureHunts.get(0).getHints().get(i);
                                hintData.put(hintsItem.getQr(), i);
                            }
                        } else {
                            treasureHuntBinding.frTreasurLlMain.setVisibility(View.GONE);
                            treasureHuntBinding.frTresTvEmptyView.setVisibility(View.GONE);
                            treasureHuntBinding.frTreasurLlHiden.setVisibility(View.VISIBLE);
                            treasureHuntBinding.frTreasurTvTime.setText(getString(R.string.come_back_at_11_00_am_to_find_the_zoo_hidden_treasure, strRemaining));

                        }
                    } else {
                        treasureHuntBinding.frTreasurLlMain.setVisibility(View.GONE);
                        treasureHuntBinding.frTresTvEmptyView.setVisibility(View.GONE);
                        treasureHuntBinding.frTreasurLlHiden.setVisibility(View.VISIBLE);
                        treasureHuntBinding.frTreasurTvTime.setText(getString(R.string.come_back_at_tomorrow_to_find_the_zoo_hidden_treasure));
                    }
                }
                if (menuTresur != null) {
                    for (int i = 0; i < menuTresur.size(); i++)
                        if (menuTresur.getItem(i).getItemId() == R.id.action_info) {
                            menuTresur.getItem(i).setVisible(treasureHuntBinding.frTreasurLlHiden.getVisibility() == View.GONE);
                        }
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.colorTresurhunt);
                treasureHuntBinding.frTreasurLlMain.setVisibility(View.GONE);
                treasureHuntBinding.frTresTvEmptyView.setVisibility(View.VISIBLE);
                treasureHuntBinding.frTresTvEmptyView.setText(getString(R.string.no_data_available));
                treasureHuntBinding.frTreasurLlHiden.setVisibility(View.GONE);
                treasureHuntBinding.frTreasurTvTime.setText(getString(R.string.come_back_at_tomorrow_to_find_the_zoo_hidden_treasure));
                if (menuTresur != null) {
                    for (int i = 0; i < menuTresur.size(); i++)
                        if (menuTresur.getItem(i).getItemId() == R.id.action_info) {
                            menuTresur.getItem(i).setVisible(false);
                        }
                }
            }
        });
    }

    private void getTreasureHuntStatus(int id, final boolean isFirstTime) {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        GamificationManager.getGamificationManager().getTreasureStatus(id, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    int pendingHint = 0;
                    final ArrayList<TreasureHuntStatus> treasureHunts = resultList;
                    if (treasureHunts.size() > 0) {
                        for (int i = 0; i < treasureHunts.get(0).getHints().size(); i++) {
                            updateStatus(treasureHunts.get(0).getHints().get(i).getId(), treasureHunts.get(0).getHints().get(i).getStatus());
                            if (treasureHunts.get(0).getHints().get(i).getStatus().equalsIgnoreCase("pending")) {
                                pendingHint++;
                            }
                        }
                        if (pendingHint == 0) {
                            if (!isFirstTime && treasureHunts.get(0).getTreasureHunt().getStatus().equalsIgnoreCase("completed"))
                                getTreasureHuntDone();
                        }
                        treasureHuntAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.colorTresurhunt);
            }
        });
    }

    private void getTreasureHuntDone() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        final RequestBody requestBody = GamificationManager.getGamificationManager().createTreasureRequestBody(GamificationManager.getGamificationManager().TRASURE_HUNT, String.valueOf(treasureHunt.getPoints()), treasureHunt.getId(), 0);
        GamificationManager.getGamificationManager().postCreateGamification(requestBody, new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                DisplayDialog.getInstance().dismissProgressDialog();
                ((HomeActivity) getActivity()).getUserDetailWithOutDialog();
                refreshToken();
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                refreshToken();
            }
        });
    }

    private void updateStatus(int id, String status) {
        for (int i = 0; i < hintsItemArrayList.size(); i++) {
            if (id == hintsItemArrayList.get(i).getId()) {
                hintsItemArrayList.get(i).setStatus(status);
            }
        }
    }

    private boolean isTreasureHuntRunning(String startDate, String endDate) {

        boolean isStarted = false;
        boolean isEnded = false;

        //HH converts hour in 24 hours format (0-23), day calculation
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(startDate);
            d2 = format.parse(endDate);

            final Calendar calendar = Calendar.getInstance();
            final long currentMillis = calendar.getTimeInMillis();

            //in milliseconds
            long startDiff = currentMillis - d1.getTime();

            long diffSeconds = startDiff / 1000 % 60;
            long diffMinutes = startDiff / (60 * 1000) % 60;
            long diffHours = startDiff / (60 * 60 * 1000);
            int diffInDays = (int) ((startDiff) / (1000 * 60 * 60 * 24));

            long endDiff = currentMillis - d2.getTime();

            long endDiffSeconds = endDiff / 1000 % 60;
            long endDiffMinutes = endDiff / (60 * 1000) % 60;
            long endDiffHours = endDiff / (60 * 60 * 1000);
            int endDiffInDays = (int) ((endDiff) / (1000 * 60 * 60 * 24));

            if (startDiff > 0) {
                isStarted = true;
//                if (diffInDays > 1) {
//                    System.err.println("Difference in number of days (2) : " + diffInDays);
//                } else if (diffHours > 24) {
//                    System.err.println(">24");
//                } else if ((diffHours == 24) && (diffMinutes >= 1)) {
//                    System.err.println("minutes");
//                }
            } else {
                isStarted = false;
                strRemaining = Utils.formatDateTime(startDate, "yyyy-MM-dd'T'HH:mm:ss", "HH:mm");
            }

            if (endDiff > 0) {
                isEnded = true;
//                if (endDiffInDays > 1) {
//                    System.err.println("Difference in number of days (2) : " + diffInDays);
//                } else if (endDiffHours > 24) {
//                    System.err.println(">24");
//                } else if ((endDiffHours == 24) && (endDiffMinutes >= 1)) {
//                    System.err.println("minutes");
//                }
            } else {
                isEnded = false;
            }
            return isStarted && !isEnded;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setHintDone() {
        if (treasureHunt != null) {
            getTreasureHuntStatus(treasureHunt.getId(), false);
        }
    }

    private void refreshToken() {
        final DateFormat df = new SimpleDateFormat(Constants.TIME_STAMP_FORMAT, Locale.ENGLISH);
        final String strRefreshToken = SharedPrefceHelper.getInstance().get(PrefCons.REFRESH_TOKEN, "");
        if (!TextUtils.isEmpty(strRefreshToken.trim())) {
            if (NetUtil.isNetworkAvailable(getActivity())) {
                Log.e("API Call", "Refresh Token");
                ApiControllers.getApiControllers().postRefreshToken(strRefreshToken, new Callback<TokenModel>() {
                    @Override
                    public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                        if (response.code() == 200) {
                            final TokenModel tokenModel = response.body();
                            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN, tokenModel.getAccessToken());
                            SharedPrefceHelper.getInstance().save(PrefCons.REFRESH_TOKEN, tokenModel.getRefreshToken());
                            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_EXPIRES, tokenModel.getExpiresIn());
                            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_CREATED, df.format(new Date()));
                            Log.e("Refresh Token ", tokenModel.getAccessToken());
                            //interval = tokenModel.getExpiresIn() * 800;
                            //Log.e("API Features ", "Interval Changed To : " + interval);
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenModel> call, Throwable t) {
                        Log.e("API Call-Failure", t.getMessage());
                    }
                });
            }
        }
    }

}

