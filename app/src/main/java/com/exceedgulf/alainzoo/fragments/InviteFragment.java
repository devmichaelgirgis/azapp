package com.exceedgulf.alainzoo.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.activity.RegisterActivity;
import com.exceedgulf.alainzoo.databinding.FragmentInviteBinding;
import com.exceedgulf.alainzoo.managers.InviteManager;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class InviteFragment extends BaseFragment {

    private FragmentInviteBinding inviteBinding;

    public InviteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inviteBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_invite, container, false);
        return inviteBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_invite), getResources().getColor(R.color.light_eggplant), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        inviteBinding.frInviteBtnSend.setOnClickListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_invite), getResources().getColor(R.color.light_eggplant), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == inviteBinding.frInviteBtnSend) {
            if (TextUtils.isEmpty(inviteBinding.frInviteEdtEmail.getText().toString().trim())) {
                inviteBinding.frInviteEdtEmail.requestFocus();
                SnackbarUtils.loadSnackBar(getString(R.string.error_email), getActivity(), R.color.light_eggplant);
            }
        } else if (!Utils.isValidEmail(inviteBinding.frInviteEdtEmail.getText().toString().trim())) {
            inviteBinding.frInviteEdtEmail.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_email_valid), getActivity(), R.color.light_eggplant);
        } else {
            InviteManager.getInviteManager().submitInvite(inviteBinding.frInviteEdtEmail.getText().toString().trim(), new ApiDetailCallback<Object>() {
                @Override
                public void onSuccess(Object result) {

                }

                @Override
                public void onFaild(String message) {

                }
            });
        }
    }
}
