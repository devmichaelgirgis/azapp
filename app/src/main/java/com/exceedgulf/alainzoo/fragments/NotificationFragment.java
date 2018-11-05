package com.exceedgulf.alainzoo.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.NotificationAdapter;
import com.exceedgulf.alainzoo.databinding.FragmentNotificationBinding;
import com.exceedgulf.alainzoo.managers.NotificationManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.util.ArrayList;

public class NotificationFragment extends BaseFragment {

    private NotificationAdapter notificationAdapter;
    private FragmentNotificationBinding notificationBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        notificationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        return notificationBinding.getRoot();
    }

    @Override
    public void initView(final View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.mNotifications), ContextCompat.getColor(getActivity(), R.color.yellow_orange), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        notificationBinding.fragmentNotificationRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        notificationAdapter = new NotificationAdapter(getActivity());
        notificationBinding.fragmentNotificationRv.setAdapter(notificationAdapter);
        getNotificationData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.mNotifications), ContextCompat.getColor(getActivity(), R.color.yellow_orange), null, false);
        }
    }

    private void getNotificationData() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        NotificationManager.getNotificationManager().getAllNotifications(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    notificationAdapter.addAllModel(resultList);
                    notificationBinding.fragmentNotificationTvEmptyView.setText(notificationAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.yellow_orange);
                if (isAdded()) {
                    notificationBinding.fragmentNotificationTvEmptyView.setText(notificationAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        });
    }

}
