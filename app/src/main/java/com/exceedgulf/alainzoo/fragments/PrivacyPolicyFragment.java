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
import com.exceedgulf.alainzoo.databinding.FragmentPrivacyPolicyBinding;
import com.exceedgulf.alainzoo.managers.AboutTermsPrivacyManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.util.ArrayList;


public class PrivacyPolicyFragment extends BaseFragment {
    private FragmentPrivacyPolicyBinding policyBinding;
    private AboutTermsPrivacyAdapter privacyAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        policyBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy_policy, container, false);
        return policyBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.privacy_policy), getResources().getColor(R.color.brownish_grey_three), null, false);
        layoutManager = new LinearLayoutManager(getActivity());
        policyBinding.frPolicyRvmain.setLayoutManager(layoutManager);
        policyBinding.frPolicyRvmain.setEmptyView(policyBinding.frPolicyTvEmptyView);
        privacyAdapter = new AboutTermsPrivacyAdapter(getActivity());
        policyBinding.frPolicyRvmain.setAdapter(privacyAdapter);
        getTermsCondition();
    }

    private void getTermsCondition() {
//        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
//        TermPrivacyContactusManager.getTermPrivacyContactusManager().getEntityDetails(152, new ApiDetailCallback() {
//            @Override
//            public void onSuccess(Object result) {
//                DisplayDialog.getInstance().dismissProgressDialog();
//                if (result != null) {
//                    final TermPrivacyContactus termPrivacyContactus = (TermPrivacyContactus) result;
//                    //   ((HomeActivity) getActivity()).setToolbarWithCenterTitle(termPrivacyContactus.getTitle(), getResources().getColor(R.color.brownish_grey_three), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
//                    policyBinding.frPrivacyTvEmptyView.setVisibility(TextUtils.isEmpty(termPrivacyContactus.getDetails()) ? View.VISIBLE : View.GONE);
//                    policyBinding.frPrivTvdetail.setText(termPrivacyContactus.getDetails());
//                }
//            }
//
//            @Override
//            public void onFaild(String message) {
//                DisplayDialog.getInstance().dismissProgressDialog();
//                policyBinding.frPrivacyTvEmptyView.setVisibility(View.VISIBLE);
//                policyBinding.frPrivacyTvEmptyView.setText(getString(R.string.no_data_available));
//                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.brownish_grey_three);
//            }
//        });
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        AboutTermsPrivacyManager.getAboutUSManager().getPrivacyPolicy(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    privacyAdapter.addItems(resultList);
                    policyBinding.frPolicyTvEmptyView.setText(privacyAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.brownish_grey_three);
                if (isAdded()) {
                    policyBinding.frPolicyTvEmptyView.setText(privacyAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.privacy_policy), getResources().getColor(R.color.brownish_grey_three), null, false);

        }
    }
}
