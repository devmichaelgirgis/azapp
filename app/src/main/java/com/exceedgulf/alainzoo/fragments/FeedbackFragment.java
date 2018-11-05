package com.exceedgulf.alainzoo.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.FeedbackCategoryAdapter;
import com.exceedgulf.alainzoo.database.models.FeedbackCategory;
import com.exceedgulf.alainzoo.databinding.FragmentFeedbackBinding;
import com.exceedgulf.alainzoo.managers.FeedBackManager;
import com.exceedgulf.alainzoo.models.FeedbackModel;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.utils.Utils;

import java.util.ArrayList;

/**
 * Created by P.P. on 22/12/17
 */
public class FeedbackFragment extends BaseFragment {
    private FragmentFeedbackBinding feedbackBinding;
    private FeedbackCategoryAdapter spinnerAdapter;
    private ArrayList<FeedbackCategory> feedbackCategoryArrayList;
    private FeedbackModel feedbackModel;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        feedbackBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_feedback, container, false);
        return feedbackBinding.getRoot();
    }

    @Override
    public void initView(final View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.feedback), getResources().getColor(R.color.very_light_brown), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        feedbackCategoryArrayList = new ArrayList<>();
        feedbackModel = new FeedbackModel();
        feedbackBinding.setFeedbackModel(feedbackModel);

        if (SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn()) {
            feedbackBinding.frFeedbackEdtName.setText(SharedPrefceHelper.getInstance().get(PrefCons.USER_FIELD_NAME, ""));
            feedbackBinding.frFeedbackEdtEmail.setText(SharedPrefceHelper.getInstance().get(PrefCons.USER_EMAIL, ""));
            feedbackModel.setName(SharedPrefceHelper.getInstance().get(PrefCons.USER_FIELD_NAME, ""));
            feedbackModel.setEmail(SharedPrefceHelper.getInstance().get(PrefCons.USER_EMAIL, ""));
        }

        spinnerAdapter = new FeedbackCategoryAdapter(getActivity(), feedbackCategoryArrayList);
        feedbackBinding.frFeedbackSpCategory.setAdapter(spinnerAdapter);
        feedbackBinding.frFeedbackBtnSend.setOnClickListener(this);
        getFeedBackCatecory();
    }

    private void getFeedBackCatecory() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        FeedBackManager.getFeedBackManager().getFeedbackCategory(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    feedbackCategoryArrayList.addAll(resultList);
                    spinnerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.very_light_brown);
            }
        });
    }


    private boolean validateData() {
        final String strName = feedbackBinding.frFeedbackEdtName.getText().toString();
        final String strEmail = feedbackBinding.frFeedbackEdtEmail.getText().toString();
        final int categoryId = feedbackCategoryArrayList.size() > 0 ? feedbackCategoryArrayList.get(feedbackBinding.frFeedbackSpCategory.getSelectedItemPosition()).getId() : 0;
        final String strMessage = feedbackBinding.frFeedbackEdtMessage.getText().toString();
        //final String strType = (feedbackBinding.frFeedbackRgType.getCheckedRadioButtonId() == feedbackBinding.frFeedbackRbComplain.getId()) ? feedbackBinding.frFeedbackRbComplain.getText().toString() : feedbackBinding.frFeedbackRbSuggestion.getText().toString();
        final String strType = (feedbackBinding.frFeedbackRgType.getCheckedRadioButtonId() == feedbackBinding.frFeedbackRbComplain.getId()) ? "Complain" : "Suggestion";


        if (TextUtils.isEmpty(strName) && TextUtils.isEmpty(strEmail) && TextUtils.isEmpty(strMessage)) {
            SnackbarUtils.loadSnackBar(getString(R.string.error_mandetory), getActivity(), R.color.very_light_brown);
            return false;
        } else if (TextUtils.isEmpty(strName)) {
            feedbackBinding.frFeedbackEdtName.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_mandetory), getActivity(), R.color.very_light_brown);
            return false;
        } else if (TextUtils.isEmpty(strEmail)) {
            feedbackBinding.frFeedbackEdtEmail.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_mandetory), getActivity(), R.color.very_light_brown);
            return false;
        } else if (!Utils.isValidEmail(strEmail)) {
            feedbackBinding.frFeedbackEdtEmail.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_email_valid), getActivity(), R.color.very_light_brown);
            return false;
        } else if (categoryId == 0) {
            SnackbarUtils.loadSnackBar(getString(R.string.error_category), getActivity(), R.color.very_light_brown);
            return false;
        } else if (TextUtils.isEmpty(strMessage)) {
            feedbackBinding.frFeedbackEdtMessage.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_mandetory), getActivity(), R.color.very_light_brown);
            return false;
        }
        feedbackModel.setWebformId("feedback");
        feedbackModel.setName(feedbackBinding.frFeedbackEdtName.getText().toString());
        feedbackModel.setEmail(feedbackBinding.frFeedbackEdtEmail.getText().toString());
        feedbackModel.setType(strType.toLowerCase());
        feedbackModel.setCategory(categoryId);
        feedbackModel.setComment(feedbackBinding.frFeedbackEdtMessage.getText().toString());
        return true;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == feedbackBinding.frFeedbackBtnSend) {
            if (validateData()) {
                submitFeedback();
            }
        }

    }

    private void submitFeedback() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading", false);
        FeedBackManager.getFeedBackManager().submitFeedback(feedbackModel, new ApiDetailCallback<String>() {
            @Override
            public void onSuccess(String result) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(result, getActivity(), R.color.very_light_brown);
                getActivity().onBackPressed();
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.very_light_brown);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.feedback), getResources().getColor(R.color.very_light_brown), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        }
    }
}
