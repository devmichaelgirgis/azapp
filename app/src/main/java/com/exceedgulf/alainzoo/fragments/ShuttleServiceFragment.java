package com.exceedgulf.alainzoo.fragments;


import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.ShuttleServiceAdapter;
import com.exceedgulf.alainzoo.custom.SpacesItemDecoration;
import com.exceedgulf.alainzoo.databinding.FragmentShuttleServiceBinding;
import com.exceedgulf.alainzoo.managers.ShuttleServiceManager;
import com.exceedgulf.alainzoo.managers.SosManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShuttleServiceFragment extends BaseFragment implements ShuttleServiceAdapter.onItemSelec {
    public static final String KEY_LATITUDE = "KEY_LATITUDE";
    public static final String KEY_LONGITUDE = "KEY_LONGITUDE";
    public static final String KEY_SHUTTLECODE = "KEY_SHUTTLECODE";
    public static final String KEY_SHUTTLESERVICE = "shuttle";
    private FragmentShuttleServiceBinding shuttleBinding;
    private int selectedItem = -1;
    private int pageNumber = 0;
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private String shuttleCode = "";
    private ShuttleServiceAdapter shuttleServiceAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        shuttleBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_shuttle_service, container, false);
        return shuttleBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.shuttle_service), getResources().getColor(R.color.cool_blue), null, false);
        final Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(KEY_LATITUDE) && bundle.containsKey(KEY_LONGITUDE)) {
            latitude = bundle.getDouble(KEY_LATITUDE, 0.0);
            longitude = bundle.getDouble(KEY_LONGITUDE, 0.0);
            shuttleCode = bundle.getString(KEY_SHUTTLECODE, "");
        }


        shuttleServiceAdapter = new ShuttleServiceAdapter(getActivity(), this);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen._8sdp);
        shuttleBinding.frshuttelRvShuttleType.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        final LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        shuttleBinding.frshuttelRvShuttleType.setLayoutManager(layoutManager);
        shuttleBinding.frshuttelRvShuttleType.setAdapter(shuttleServiceAdapter);
        shuttleBinding.frshuttelTvRequest.setOnClickListener(this);

        getShuttleServiceList();
    }

    public void getShuttleServiceList() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        ShuttleServiceManager.getShuttleServiceManager().getAllEntitiesData(pageNumber, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    shuttleServiceAdapter.addItems(resultList);
                    shuttleBinding.frshuttelTvEmptyView.setText(shuttleServiceAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
                if (isAdded()) {
                    shuttleBinding.frshuttelTvEmptyView.setText(shuttleServiceAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, false);
    }

    @Override
    public void onItemClick(int position) {
        selectedItem = position;
        shuttleBinding.frshuttelTvRequest.setBackgroundResource(position == -1 ? R.drawable.bg_shuttel_request_unselected : R.drawable.bg_red_corner_edt);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == shuttleBinding.frshuttelTvRequest) {
            if (selectedItem != -1) {
//                if (selectedItem == 0 || selectedItem == 3) {
//                    shuttleCode = shuttleCode + "/Zarafa Cafe";
//                } else if (selectedItem == 1 || selectedItem == 4) {
//                    shuttleCode = shuttleCode + "/Main Gate";
//                } else {
//                    shuttleCode = shuttleCode + "/Monkey Highest";
//                }
                shuttleCode = shuttleCode + shuttleServiceAdapter.getShuttleServiceArrayList().get(selectedItem).getNameEn();

                showRequestDialog();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.shuttle_service), getResources().getColor(R.color.cool_blue), null, false);
        }
    }

    private void showRequestDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_shuttle_request, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final int width = (int) getResources().getDimension(R.dimen._230sdp);

        alertDialog.show();
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        try {
            final String request = SosManager.getSosManager().sosRequestData("Request a shuttle", "shuttle", latitude, longitude, shuttleCode, "request");
            SosManager.getSosManager().postSosRequest(request, new ApiDetailCallback() {
                @Override
                public void onSuccess(Object result) {
                    DisplayDialog.getInstance().dismissProgressDialog();
                    alertDialog.dismiss();
                    final ShuttleConfirmServiceFragment shuttleConfirmServiceFragment = new ShuttleConfirmServiceFragment();
                    final Bundle bundle = new Bundle();
                    bundle.putSerializable(KEY_SHUTTLESERVICE, shuttleServiceAdapter.getShuttleServiceArrayList().get(selectedItem));
                    shuttleConfirmServiceFragment.setArguments(bundle);
                    ((HomeActivity) getActivity()).addFragment(ShuttleServiceFragment.this, shuttleConfirmServiceFragment);
                }

                @Override
                public void onFaild(String message) {
                    DisplayDialog.getInstance().dismissProgressDialog();
                    alertDialog.dismiss();
                    ((HomeActivity) getActivity()).alertMessage("", message, true);
                }
            });
        } catch (Exception e) {
            ((HomeActivity) getActivity()).alertMessage("", getString(R.string.error), true);
        }
    }

}
