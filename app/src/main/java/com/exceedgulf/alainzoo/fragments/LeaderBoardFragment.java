package com.exceedgulf.alainzoo.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.LeaderBoardRecyclerViewAdapter;
import com.exceedgulf.alainzoo.database.models.Avatars;
import com.exceedgulf.alainzoo.database.models.HintsItem;
import com.exceedgulf.alainzoo.database.models.LeaderBoard;
import com.exceedgulf.alainzoo.database.models.TreasureHunt;
import com.exceedgulf.alainzoo.databinding.FragmentLeaderboardBinding;
import com.exceedgulf.alainzoo.managers.AvatarsManager;
import com.exceedgulf.alainzoo.managers.LeaderBoardManager;
import com.exceedgulf.alainzoo.managers.TreasureHuntManager;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.ImageUtil;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by P.P. on 25/12/17
 */
public class LeaderBoardFragment extends BaseFragment {

    Comparator<LeaderBoard> FruitNameComparator
            = new Comparator<LeaderBoard>() {

        public int compare(LeaderBoard leaderBoard1, LeaderBoard leaderBoard2) {
            //ascending order
            return leaderBoard1.compareTo(leaderBoard2);
        }

    };
    private FragmentLeaderboardBinding leaderboardBinding;
    private LeaderBoardRecyclerViewAdapter leaderBoardRecyclerViewAdapter;
    private LeaderBoard currentUserLeaderBoard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        leaderboardBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_leaderboard, container, false);
        return leaderboardBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_leaderboard), getResources().getColor(R.color.light_eggplant), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        leaderBoardRecyclerViewAdapter = new LeaderBoardRecyclerViewAdapter(getActivity());
        leaderboardBinding.frLeaderboardRvLeader.setLayoutManager(new LinearLayoutManager(getActivity()));
        leaderboardBinding.frLeaderboardRvLeader.setEmptyView(leaderboardBinding.frLeaderboardTvRecycleEmptyView);
        leaderboardBinding.frLeaderboardRvLeader.setAdapter(leaderBoardRecyclerViewAdapter);
        getLeaderBoardList();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_leaderboard), getResources().getColor(R.color.light_eggplant), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        }
    }

    private void getLeaderBoardList() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        LeaderBoardManager.getLeaderBoardManager().getAllEntitiesData(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                DisplayDialog.getInstance().dismissProgressDialog();
                leaderboardBinding.frLeaderboardTvEmptyView.setVisibility(View.GONE);
                boolean isAnyDataSeted = false;
                if (isAdded()) {
                    final ArrayList<LeaderBoard> leaderBoardArrayList = resultList;
                    if (leaderBoardArrayList != null && leaderBoardArrayList.size() > 0) {
                        for (int i = 0; i < leaderBoardArrayList.size(); i++) {
                            final LeaderBoard leaderBoard = leaderBoardArrayList.get(i);
                            if (leaderBoard.isCurrentUser() && leaderBoard.getRank().equalsIgnoreCase("n/a")) {
                                currentUserLeaderBoard = leaderBoard;
                                isAnyDataSeted = true;
                                setCurrentUserData();
                                leaderBoardArrayList.remove(leaderBoard);
                                break;
                            } else if (leaderBoard.isCurrentUser()) {
                                currentUserLeaderBoard = leaderBoard;
                                isAnyDataSeted = true;
                                setCurrentUserData();
                                break;
                            }
                        }
                        Collections.sort(leaderBoardArrayList, FruitNameComparator);
                        leaderBoardRecyclerViewAdapter.addAllItems(leaderBoardArrayList);

                        if(!isAnyDataSeted){
                            final Avatars avatars = (Avatars) AvatarsManager.getAvatarsManager().getEntityDetailsFromDB(SharedPrefceHelper.getInstance().get(PrefCons.USER_AVATAR_ID, 0));
                            if (!TextUtils.isEmpty(SharedPrefceHelper.getInstance().get(PrefCons.USER_FIELD_NAME, ""))) {
                                leaderboardBinding.leaderboardItemTvName.setText(SharedPrefceHelper.getInstance().get(PrefCons.USER_FIELD_NAME, ""));
                            }
                            if (avatars != null && !TextUtils.isEmpty(avatars.getImage())) {
                                Glide.with(getActivity()).load(avatars.getImage()).into(leaderboardBinding.leaderboardItemIvProfileImage);
                            }
                            leaderboardBinding.leaderboardItemTvPoints.setText("0 " + getString(R.string.st_points));
                            leaderboardBinding.leaderboardItemTvRanks.setText("n/a");
                           leaderboardBinding.leaderboardItemLlBottomForRank.setVisibility(View.INVISIBLE);
                        }

                    } else {
                        leaderboardBinding.frLeaderboardTvRecycleEmptyView.setVisibility(View.VISIBLE);
                        leaderboardBinding.frLeaderboardTvRecycleEmptyView.setText(leaderBoardRecyclerViewAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                    }
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                leaderboardBinding.frLeaderboardTvEmptyView.setVisibility(View.GONE);
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.light_eggplant);
            }
        }, false);
    }

    private void setCurrentUserData() {
        leaderboardBinding.leaderboardItemTvName.setText(currentUserLeaderBoard.getUser().get(0).getName());
        leaderboardBinding.leaderboardItemTvPoints.setText(String.valueOf(currentUserLeaderBoard.getPoints()) + " " + getString(R.string.st_points));
        leaderboardBinding.leaderboardItemTvRanks.setText(String.valueOf(currentUserLeaderBoard.getRank()));
        ImageUtil.loadImageFromPicasso(getActivity(), currentUserLeaderBoard.getUser().get(0).getAvatar(), leaderboardBinding.leaderboardItemIvProfileImage, leaderboardBinding.leaderboardItemIvProfilePlaceHolder);
    }
}
