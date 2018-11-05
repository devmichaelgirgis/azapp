package com.exceedgulf.alainzoo.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.AboutTermsPrivacyAdapter;
import com.exceedgulf.alainzoo.databinding.FragmentAboutTheZooBinding;
import com.exceedgulf.alainzoo.managers.AboutTermsPrivacyManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.util.ArrayList;


public class AboutTheZooFragment extends BaseFragment {
    private FragmentAboutTheZooBinding aboutTheZooBinding;
    private AboutTermsPrivacyAdapter aboutUsAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        aboutTheZooBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_about_the_zoo, container, false);
        return aboutTheZooBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.about_the_zoo), getResources().getColor(R.color.brownish_grey_three), null, false);
        layoutManager = new LinearLayoutManager(getActivity());
        aboutTheZooBinding.frAboutRvmain.setLayoutManager(layoutManager);
        aboutTheZooBinding.frAboutRvmain.setEmptyView(aboutTheZooBinding.frAboutTvEmptyView);
        aboutUsAdapter = new AboutTermsPrivacyAdapter(getActivity());
        aboutTheZooBinding.frAboutRvmain.setAdapter(aboutUsAdapter);
        getAboutUs();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.about_the_zoo), getResources().getColor(R.color.brownish_grey_three), null, false);
        }
    }

    private void getAboutUs() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        AboutTermsPrivacyManager.getAboutUSManager().getAboutUs(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    aboutUsAdapter.addItems(resultList);
                    aboutTheZooBinding.frAboutTvEmptyView.setText(aboutUsAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.brownish_grey_three);
                if (isAdded()) {
                    aboutTheZooBinding.frAboutTvEmptyView.setText(aboutUsAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        });
    }
}
