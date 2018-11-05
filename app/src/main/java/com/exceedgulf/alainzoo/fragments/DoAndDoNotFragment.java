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
import com.exceedgulf.alainzoo.databinding.FragmentDoAndDoNotBinding;
import com.exceedgulf.alainzoo.managers.AboutTermsPrivacyManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.util.ArrayList;

public class DoAndDoNotFragment extends BaseFragment {
    private FragmentDoAndDoNotBinding doAndDoNotBinding;
    private AboutTermsPrivacyAdapter aboutUsAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        doAndDoNotBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_do_and_do_not, container, false);
        return doAndDoNotBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.do_and_do_not), getResources().getColor(R.color.brownish_grey_three), null, false);
        layoutManager = new LinearLayoutManager(getActivity());
        doAndDoNotBinding.frDoAndDoNotRvmain.setLayoutManager(layoutManager);
        doAndDoNotBinding.frDoAndDoNotRvmain.setEmptyView(doAndDoNotBinding.frDoAndDoNotTvEmptyView);
        aboutUsAdapter = new AboutTermsPrivacyAdapter(getActivity());
        doAndDoNotBinding.frDoAndDoNotRvmain.setAdapter(aboutUsAdapter);
        getDoAndDoNot();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.do_and_do_not), getResources().getColor(R.color.brownish_grey_three), null, false);
        }
    }

    private void getDoAndDoNot() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        AboutTermsPrivacyManager.getAboutUSManager().getDoAndDoNot(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    aboutUsAdapter.addItems(resultList);
                    doAndDoNotBinding.frDoAndDoNotTvEmptyView.setText(aboutUsAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.brownish_grey_three);
                if (isAdded()) {
                    doAndDoNotBinding.frDoAndDoNotTvEmptyView.setText(aboutUsAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        });
    }
}
