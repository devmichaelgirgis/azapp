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
import com.exceedgulf.alainzoo.adapter.GenderAdapter;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.Family;
import com.exceedgulf.alainzoo.databinding.FragmentAddFamilyBinding;
import com.exceedgulf.alainzoo.managers.InviteManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import org.json.JSONException;

/**
 * Created by P.P on 25/01/2018
 */
public class AddFamilyFragment extends BaseFragment {

    private FragmentAddFamilyBinding addFamilyBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        addFamilyBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_family, container, false);
        return addFamilyBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.add_family_member), getResources().getColor(R.color.light_eggplant), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        final GenderAdapter genderAdapter = new GenderAdapter(getActivity());
        final String[] gender = getResources().getStringArray(R.array.gender);
        genderAdapter.add(gender[0]);
        genderAdapter.add(gender[1]);
        addFamilyBinding.frFamilySpnGender.setAdapter(genderAdapter);
        addFamilyBinding.frFamilyBtnSendInvitation.setOnClickListener(this);
    }

    private boolean validation() {
        if (TextUtils.isEmpty(addFamilyBinding.frFamilyEdtname.getText().toString().trim())) {
            SnackbarUtils.loadSnackBar(getString(R.string.error_name), getActivity(), R.color.light_eggplant);
            return false;
        }
        if (TextUtils.isEmpty(addFamilyBinding.frFamilyEdtRelative.getText().toString().trim())) {
            SnackbarUtils.loadSnackBar(getString(R.string.error_relative), getActivity(), R.color.light_eggplant);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == addFamilyBinding.frFamilyBtnSendInvitation) {
            if (validation()) {
                try {
                    DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
                    final String requestData = InviteManager.getInviteManager().createMamberRequestData(addFamilyBinding.frFamilyEdtname.getText().toString().trim(), addFamilyBinding.frFamilyEdtRelative.getText().toString().trim(), (addFamilyBinding.frFamilySpnGender.getSelectedItemPosition() == 0 ? "m" : "f"));
                    InviteManager.getInviteManager().submitInvite(requestData, new ApiDetailCallback<Object>() {
                        @Override
                        public void onSuccess(Object result) {
                            DisplayDialog.getInstance().dismissProgressDialog();
                            final Family family = (Family) result;
                            ((HomeActivity) getActivity()).alertMessageWithBack("", family.getName(), true);
                            family.setName(addFamilyBinding.frFamilyEdtname.getText().toString().trim());
                            family.setGender(addFamilyBinding.frFamilySpnGender.getSelectedItem().toString());
                            family.setRelative(addFamilyBinding.frFamilyEdtRelative.getText().toString());
                            AlainZooDB.getInstance().familyDao().insertItem(family);
                        }

                        @Override
                        public void onFaild(String message) {
                            DisplayDialog.getInstance().dismissProgressDialog();
                            ((HomeActivity) getActivity()).alertMessageWithBack("", message, false);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    DisplayDialog.getInstance().dismissProgressDialog();
                    SnackbarUtils.loadSnackBar(getString(R.string.error), getActivity(), R.color.light_eggplant);
                }

            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.add_family_member), getResources().getColor(R.color.light_eggplant), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        }
    }
}
