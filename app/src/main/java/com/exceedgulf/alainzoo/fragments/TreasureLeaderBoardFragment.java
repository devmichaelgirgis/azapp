package com.exceedgulf.alainzoo.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.TreasureLeaderBoardAdapter;
import com.exceedgulf.alainzoo.database.models.Avatars;
import com.exceedgulf.alainzoo.databinding.FragmentTreasureLeaderboardBinding;
import com.exceedgulf.alainzoo.managers.AvatarsManager;
import com.exceedgulf.alainzoo.managers.TreasureHuntManager;
import com.exceedgulf.alainzoo.models.TreasurLeaderBoard;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by P.G. on 02/06/18
 */
public class TreasureLeaderBoardFragment extends BaseFragment {
    private static final String KEY_TRESUREHUNT_ID = "KEY_TRESUREHUNT_ID";
    private FragmentTreasureLeaderboardBinding leaderboardBinding;
    private TreasureLeaderBoardAdapter treasureLeaderBoardAdapter;
    private int treaserhuntId;

    private int pageNumber = 0;
    private boolean isLoading = false;
    private boolean isLoadMore = true;
    private LinearLayoutManager layoutManager;
    private boolean isCompletedLoading = false;
    private boolean isFirstTimeCompleteCall = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        leaderboardBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_treasure_leaderboard, container, false);
        return leaderboardBinding.getRoot();
    }

    public static TreasureLeaderBoardFragment getTreasurhuntLeaderbordFragment(final int tresurhunt_id) {
        final Bundle bundle = new Bundle();
        bundle.putInt(KEY_TRESUREHUNT_ID, tresurhunt_id);
        final TreasureLeaderBoardFragment treasureLeaderBoardFragment = new TreasureLeaderBoardFragment();
        treasureLeaderBoardFragment.setArguments(bundle);
        return treasureLeaderBoardFragment;
    }


    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_leaderboard), getResources().getColor(R.color.colorTresurhunt), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        final Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(KEY_TRESUREHUNT_ID)) {
            treaserhuntId = bundle.getInt(KEY_TRESUREHUNT_ID, 0);
        }
        treasureLeaderBoardAdapter = new TreasureLeaderBoardAdapter(getActivity());
        layoutManager = new LinearLayoutManager(getActivity());
        leaderboardBinding.frTresureLeaderboardRvLeader.setLayoutManager(layoutManager);
        leaderboardBinding.frTresureLeaderboardRvLeader.setAdapter(treasureLeaderBoardAdapter);
        getLeaderBoardCompleted();
    }

    private void getLeaderBoardCompleted() {
        isLoading = true;
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        TreasureHuntManager.getTreasureHuntManager().getTreaserComletedList(treaserhuntId, pageNumber, isFirstTimeCompleteCall, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                if (isFirstTimeCompleteCall) {
                    leaderboardBinding.frTresureLeaderboardRvLeader.addOnScrollListener(recyclerViewOnScrollListener);
                }
                isFirstTimeCompleteCall = false;
                isCompletedLoading = isLoadYes;
                isLoadMore = isLoadYes;
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    isLoading = false;
                    if (!isCompletedLoading) {
                        pageNumber = -1;
                        isLoadMore = true;
                    }
                    Log.e("Success", resultList.size() + "");
                    treasureLeaderBoardAdapter.addItems(resultList);
                    if (treasureLeaderBoardAdapter.getItemCount() > 0) {
                        leaderboardBinding.frTresTvEmptyView.setVisibility(View.GONE);
                    }
                    //leaderboardBinding.frTresTvEmptyView.setText(treasureLeaderBoardAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                    //getLeaderBoardPending();


                }
            }

            @Override
            public void onFaild(String message) {
                leaderboardBinding.frTresTvEmptyView.setText(treasureLeaderBoardAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.colorTresurhunt);
            }
        });
    }

    private void getLeaderBoardPending() {
        isLoading = true;
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        TreasureHuntManager.getTreasureHuntManager().getTreaserPendingList(treaserhuntId, pageNumber, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                isLoadMore = isLoadYes;
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    isLoading = false;
                    Log.e("Success", resultList.size() + "");
                    treasureLeaderBoardAdapter.addItems(resultList);
                    if (treasureLeaderBoardAdapter.getItemCount() > 0) {
                        leaderboardBinding.frTresTvEmptyView.setVisibility(View.GONE);
                    }
                    leaderboardBinding.frTresTvEmptyView.setText(treasureLeaderBoardAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                    //leaderboardBinding.frTresureLeaderboardRvLeader.addOnScrollListener(recyclerViewOnScrollListener);
                    setCurentUserData();
                }
            }

            @Override
            public void onFaild(String message) {
                leaderboardBinding.frTresTvEmptyView.setText(treasureLeaderBoardAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.colorTresurhunt);
            }
        });
    }

    private final RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!isLoading && isLoadMore) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    pageNumber++;
                    if (isCompletedLoading) {
                        getLeaderBoardCompleted();
                    } else {
                        getLeaderBoardPending();
                    }
                }
            }
        }
    };

    private void setCurentUserData() {
        boolean isDataSeted = false;
        if (treasureLeaderBoardAdapter.getItemCount() > 0) {
            final ArrayList<TreasurLeaderBoard> treasurLeaderBoardArrayList = treasureLeaderBoardAdapter.getTreasurLeaderBoardArrayList();
            for (final TreasurLeaderBoard treasurLeaderBoard : treasurLeaderBoardArrayList) {
                if (treasurLeaderBoard.isCurrent_user()) {
                    if (!TextUtils.isEmpty(treasurLeaderBoard.getAvatar())) {
                        Glide.with(getActivity()).load(treasurLeaderBoard.getAvatar()).into(leaderboardBinding.frTresureLeaderboardIvuserprofilepic);
                    }
                    leaderboardBinding.frTresureLeaderboardTvName.setText(treasurLeaderBoard.getName());
                    leaderboardBinding.frTresureLeaderboardTvPoints.setText(treasurLeaderBoard.getTotal_points() + " " + getString(R.string.st_points));
                    if (!TextUtils.isEmpty(treasurLeaderBoard.getTotal_hints())) {
                        leaderboardBinding.frTresureLeaderboardTvClues.setText(treasurLeaderBoard.getTotal_hints() + " " + getString(R.string.st_hints));
                    } else {
                        leaderboardBinding.frTresureLeaderboardTvClues.setText("0 " + getString(R.string.st_hints));
                    }
                    leaderboardBinding.frTresureLeaderboardTvRank.setText(treasurLeaderBoard.getRank());
                    isDataSeted = true;
                    break;
                }

            }
        }

        if (!isDataSeted) {
            final Avatars avatars = (Avatars) AvatarsManager.getAvatarsManager().getEntityDetailsFromDB(SharedPrefceHelper.getInstance().get(PrefCons.USER_AVATAR_ID, 0));
            if (!TextUtils.isEmpty(SharedPrefceHelper.getInstance().get(PrefCons.USER_FIELD_NAME, ""))) {
                leaderboardBinding.frTresureLeaderboardTvName.setText(SharedPrefceHelper.getInstance().get(PrefCons.USER_FIELD_NAME, ""));
            }
            if (avatars != null && !TextUtils.isEmpty(avatars.getImage())) {
                Glide.with(getActivity()).load(avatars.getImage()).into(leaderboardBinding.frTresureLeaderboardIvuserprofilepic);
            }
            leaderboardBinding.frTresureLeaderboardTvPoints.setText("0 " + getString(R.string.st_points));
            leaderboardBinding.frTresureLeaderboardTvClues.setText("0 " + getString(R.string.st_hints));
            leaderboardBinding.frTresureLeaderboardTvRank.setText("n/a");
            leaderboardBinding.frTresureLeaderboardLlbotom.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_leaderboard), getResources().getColor(R.color.colorTresurhunt), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        }
    }
}
