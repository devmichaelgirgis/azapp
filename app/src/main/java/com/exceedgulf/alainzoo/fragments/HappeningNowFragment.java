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
import com.exceedgulf.alainzoo.adapter.HappeningsNowAdapter;
import com.exceedgulf.alainzoo.database.models.HappeningNow;
import com.exceedgulf.alainzoo.databinding.FragmentHappeningNowBinding;
import com.exceedgulf.alainzoo.managers.EducationManager;
import com.exceedgulf.alainzoo.managers.HappeningNowManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.util.ArrayList;

/**
 * Created by P.G. on 22/12/17
 */
public class HappeningNowFragment extends BaseFragment {
    private static final String BUNDLE_KEY_HAPPENING_NOW = "happeningnow";
    private FragmentHappeningNowBinding fragmentHappeningNowBinding;
    private HappeningsNowAdapter happeningsNowAdapter;
    private ArrayList<HappeningNow> happeningNowArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentHappeningNowBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_happening_now, container, false);
        return fragmentHappeningNowBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.titlehappeningnow), getResources().getColor(R.color.light_grey_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        happeningsNowAdapter = new HappeningsNowAdapter(getActivity());
        fragmentHappeningNowBinding.rvHappeningNow.setLayoutManager(new LinearLayoutManager(getActivity()));
        fragmentHappeningNowBinding.rvHappeningNow.setAdapter(happeningsNowAdapter);
        fragmentHappeningNowBinding.rvHappeningNow.setEmptyView(fragmentHappeningNowBinding.frHpnTvEmptyView);

        final Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(BUNDLE_KEY_HAPPENING_NOW)) {
            happeningNowArrayList = (ArrayList<HappeningNow>) bundle.getSerializable(BUNDLE_KEY_HAPPENING_NOW);

            if (happeningNowArrayList != null) {
                if (happeningNowArrayList.size() > 0) {
                    happeningsNowAdapter.addItems(happeningNowArrayList);
                    fragmentHappeningNowBinding.frHpnTvEmptyView.setText(happeningNowArrayList.size() == 0 ? getString(R.string.no_data_available) : "");
                } else {
                    getHappeningNow();
                }
            }
        }


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.titlehappeningnow), getResources().getColor(R.color.light_grey_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        }
    }

    public void getHappeningNow() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        HappeningNowManager.getHappeningNowManager().getAllEntitiesData(0, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    happeningsNowAdapter.addItems(resultList);
                    fragmentHappeningNowBinding.frHpnTvEmptyView.setText(happeningsNowAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.light_grey_blue);
                if (isAdded()) {
                    fragmentHappeningNowBinding.frHpnTvEmptyView.setText(happeningsNowAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, false);
    }


}
