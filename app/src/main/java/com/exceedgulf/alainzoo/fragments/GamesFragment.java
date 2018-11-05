package com.exceedgulf.alainzoo.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.Interfaces.GamificationCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.GamesAdapter;
import com.exceedgulf.alainzoo.databinding.FragmentGamesBinding;
import com.exceedgulf.alainzoo.managers.GamesManager;
import com.exceedgulf.alainzoo.managers.GamificationManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.util.ArrayList;

import okhttp3.RequestBody;


/**
 * Created by P.G. on 01/22/2018
 */
public class GamesFragment extends BaseFragment implements GamificationCallback {
    private FragmentGamesBinding gamesBinding;
    private GamesAdapter gamesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        gamesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_games, container, false);
        return gamesBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.mGames), getResources().getColor(R.color.grape), null, false);

        gamesAdapter = new GamesAdapter(getActivity(), this);
        gamesBinding.frGameRvmain.setLayoutManager(new LinearLayoutManager(getActivity()));
        gamesBinding.frGameRvmain.setEmptyView(gamesBinding.frGameTvEmptyView);
        gamesBinding.frGameRvmain.setAdapter(gamesAdapter);
        getAllGames();
    }

    public void getAllGames() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        GamesManager.getGamesManager().getAllEntitiesData(0, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    gamesAdapter.addItems(resultList);
                    gamesBinding.frGameTvEmptyView.setText(gamesAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.grape);
                if (isAdded()) {
                    gamesBinding.frGameTvEmptyView.setText(gamesAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, false);
    }

    @Override
    public void callGamification(Integer id) {
        final RequestBody body = GamificationManager.getGamificationManager().createRequestBody("downloaded_game", String.valueOf(id));
        GamificationManager.getGamificationManager().postCreateGamification(body,  new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.e("gamification","success"+result.toString());
                ((HomeActivity)getActivity()).getUserDetailWithOutDialog();
            }

            @Override
            public void onFaild(String message) {
                Log.e("gamification","failure"+message);
            }
        });
    }
}
