package com.exceedgulf.alainzoo.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.databinding.FragmentChangePasswordBinding;
import com.exceedgulf.alainzoo.managers.ProfileManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by indianic on 27/02/18.
 */

public class ChangePasswordFragment extends BaseFragment {
    private FragmentChangePasswordBinding changePasswordBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        changePasswordBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_password, container, false);
        return changePasswordBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.change_password), getResources().getColor(R.color.light_eggplant), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        changePasswordBinding.frcpBtnchangepassword.setOnClickListener(this);
    }

    public boolean isValidate() {
        if (TextUtils.isEmpty(changePasswordBinding.frcpEdtoldpassword.getText().toString().trim())) {
            SnackbarUtils.loadSnackBar(getString(R.string.str_enter_oldpassword), getActivity(), R.color.light_eggplant);
            return false;
        } else if (TextUtils.isEmpty(changePasswordBinding.frcpEdtnewpassword.getText().toString().trim())) {
            SnackbarUtils.loadSnackBar(getString(R.string.str_enter_newpassword), getActivity(), R.color.light_eggplant);
            return false;
        } else if (TextUtils.isEmpty(changePasswordBinding.frcpEdtconfimpassword.getText().toString().trim())) {
            SnackbarUtils.loadSnackBar(getString(R.string.str_enter_confirmpassword), getActivity(), R.color.light_eggplant);
            return false;
        } else if (!changePasswordBinding.frcpEdtnewpassword.getText().toString().trim().equals(changePasswordBinding.frcpEdtconfimpassword.getText().toString().trim())) {
            SnackbarUtils.loadSnackBar(getString(R.string.str_password_not_match), getActivity(), R.color.light_eggplant);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == changePasswordBinding.frcpBtnchangepassword) {
            if (isValidate()) {
                try {
                    final JSONObject jsonObject = new JSONObject();
                    final JSONArray passWord = new JSONArray();
                    final JSONObject passwordObject = new JSONObject();
                    passwordObject.put("existing", changePasswordBinding.frcpEdtoldpassword.getText().toString().trim());
                    passwordObject.put("value", changePasswordBinding.frcpEdtnewpassword.getText().toString().trim());
                    passWord.put(passwordObject);
                    jsonObject.put("pass", passWord);
                    updatePassword(jsonObject.toString());
                } catch (Exception e) {
                }
            }
        }
    }

    private void updatePassword(final String requestData) {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading", false);
        final RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestData);
        ProfileManager.getProfileManager().changePassword(body, new ApiDetailCallback() {
            @Override
            public void onSuccess(final Object result) {
                DisplayDialog.getInstance().dismissProgressDialog();
                alertDialogResponse("", (String) result, false);
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                alertDialogResponse("", message, true);
            }
        });
    }

    public void alertDialogResponse(final String title, final String message, final boolean isOnlyAlert) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_form_response, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogView.findViewById(R.id.dialog_form_response_tvTitle).setVisibility(View.GONE);
        final TextView tvTitle = dialogView.findViewById(R.id.dialog_form_response_tvTitle);
        tvTitle.setText(title);
        tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        final TextView tvMessage = dialogView.findViewById(R.id.dialog_form_response_tvMessage);
        final TextView tvOkay = dialogView.findViewById(R.id.dialog_form_response_tvOkay);

        tvMessage.setText(message);
        tvOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (!isOnlyAlert) {
                    ((HomeActivity) getActivity()).logout(false);
                }
            }
        });
        alertDialog.show();
        final int width = (int) getResources().getDimension(R.dimen._250sdp);
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (isAdded()) {
            if (!hidden) {
                ((HomeActivity) getActivity()).disableCollapse();
                ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.change_password), getResources().getColor(R.color.light_eggplant), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
            }
        }
    }
}
