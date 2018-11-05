package com.exceedgulf.alainzoo.fragments;


import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.database.models.VisitorService;
import com.exceedgulf.alainzoo.databinding.FragmentServiceApplyBinding;
import com.exceedgulf.alainzoo.managers.VisitorServicesManager;
import com.exceedgulf.alainzoo.models.FeedbackResponse;
import com.exceedgulf.alainzoo.models.ServiceFormModel;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.utils.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by P.G. on 25/12/17
 */
public class VisiterServiceApplyFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String KEY_VISITOR = "KeyVisitor";
    private static final String WEBFORM_ID = "visitors_services";
    private FragmentServiceApplyBinding serviceApplyBinding;
    private ServiceFormModel serviceFormModel;
    private VisitorService visitorService;
    private String selectedDate = "";
    private String selectedTime = "";

    public static VisiterServiceApplyFragment getVisitorServiceApplyFragment(final VisitorService visitorService) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_VISITOR, visitorService);
        final VisiterServiceApplyFragment serviceApplyFragment = new VisiterServiceApplyFragment();
        serviceApplyFragment.setArguments(bundle);
        return serviceApplyFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        serviceApplyBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_service_apply, container, false);
        return serviceApplyBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.apply), getResources().getColor(R.color.brownish_orange), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        final Bundle bundleExperiance = this.getArguments();
        if (bundleExperiance != null && bundleExperiance.containsKey(KEY_VISITOR)) {
            visitorService = (VisitorService) bundleExperiance.getSerializable(KEY_VISITOR);
        }

        if (SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn()) {
            serviceApplyBinding.frSerEnqEdtname.setText(SharedPrefceHelper.getInstance().get(PrefCons.USER_FIELD_NAME, ""));
            serviceApplyBinding.frSerEnqEdtemail.setText(SharedPrefceHelper.getInstance().get(PrefCons.USER_EMAIL, ""));
            serviceApplyBinding.frSerEnqEdtphonenumnber.setText(SharedPrefceHelper.getInstance().get(PrefCons.USER_NAME, ""));
        }

        serviceApplyBinding.frSerEnqBtninqueryforservices.setOnClickListener(this);
        serviceApplyBinding.frSrappTvTitle.setText(String.format(getString(R.string.apply_form_title), visitorService.getName()));
        serviceApplyBinding.frSerEnqTvdate.setOnClickListener(this);
        serviceApplyBinding.frSerEnqTvtime.setOnClickListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.apply), getResources().getColor(R.color.brownish_orange), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        }
    }


    private boolean validateData() {
        final String strName = serviceApplyBinding.frSerEnqEdtname.getText().toString();
        final String strEmail = serviceApplyBinding.frSerEnqEdtemail.getText().toString();
        final String strMobile = serviceApplyBinding.frSerEnqEdtphonenumnber.getText().toString();
        final String strComment = serviceApplyBinding.frSerEnqEdtcoment.getText().toString();

        if (TextUtils.isEmpty(strName) && TextUtils.isEmpty(strEmail) && TextUtils.isEmpty(strMobile) && TextUtils.isEmpty(selectedDate) && TextUtils.isEmpty(selectedTime) && TextUtils.isEmpty(strComment)) {
            SnackbarUtils.loadSnackBar(getString(R.string.error_mandetory), getActivity(), R.color.brownish_orange);
            return false;
        } else if (TextUtils.isEmpty(strName)) {
            serviceApplyBinding.frSerEnqEdtname.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_mandetory), getActivity(), R.color.brownish_orange);
            return false;
        } else if (TextUtils.isEmpty(strMobile)) {
            serviceApplyBinding.frSerEnqEdtphonenumnber.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_mandetory), getActivity(), R.color.brownish_orange);
            return false;
        } else if (!Utils.isValidInternationalMobile(strMobile)) {
            serviceApplyBinding.frSerEnqEdtphonenumnber.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_valid_phone_number), getActivity(), R.color.brownish_orange);
            return false;
        } else if (TextUtils.isEmpty(strEmail)) {
            serviceApplyBinding.frSerEnqEdtemail.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_mandetory), getActivity(), R.color.brownish_orange);
            return false;
        } else if (!Utils.isValidEmail(strEmail)) {
            serviceApplyBinding.frSerEnqEdtemail.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_email_valid), getActivity(), R.color.brownish_orange);
            return false;
        } else if (TextUtils.isEmpty(selectedDate)) {
            serviceApplyBinding.frSerEnqTvdate.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_mandetory), getActivity(), R.color.brownish_orange);
            return false;
        } else if (TextUtils.isEmpty(selectedTime)) {
            serviceApplyBinding.frSerEnqTvtime.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_mandetory), getActivity(), R.color.brownish_orange);
            return false;
        } else if (TextUtils.isEmpty(strComment)) {
            serviceApplyBinding.frSerEnqEdtcoment.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_mandetory), getActivity(), R.color.brownish_orange);
            return false;
        }
        serviceFormModel = new ServiceFormModel();
        serviceFormModel.setWebformId(WEBFORM_ID);
        serviceFormModel.setServiceName(String.valueOf(visitorService.getId()));
        serviceFormModel.setName(strName);
        serviceFormModel.setPhoneNumber(strMobile);
        serviceFormModel.setEmail(strEmail);
        serviceFormModel.setServiceDate(selectedDate);
        serviceFormModel.setServiceTime(selectedTime);
        serviceFormModel.setComments(strComment);
        return true;
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == serviceApplyBinding.frSerEnqBtninqueryforservices) {
            if (validateData()) {
                submitInquiry();
            }
        } else if (v == serviceApplyBinding.frSerEnqTvdate) {
            final Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setLocale(Locale.ENGLISH);
            datePickerDialog.setAccentColor(ContextCompat.getColor(getActivity(), R.color.brownish_orange));
            datePickerDialog.setMinDate(calendar);
            datePickerDialog.show(getActivity().getFragmentManager(), "Date");
        } else if (v == serviceApplyBinding.frSerEnqTvtime) {
            if (TextUtils.isEmpty(selectedDate)) {
                SnackbarUtils.loadSnackBar(getString(R.string.error_service_date), getActivity(), R.color.brownish_orange);
            } else {
                final Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.setLocale(Locale.ENGLISH);
                timePickerDialog.setAccentColor(ContextCompat.getColor(getActivity(), R.color.brownish_orange));
                if (selectedDate.equalsIgnoreCase(getTodaysDate())) {
                    timePickerDialog.setMinTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                }
                timePickerDialog.show(getActivity().getFragmentManager(), "Time");
            }
        }
    }

    private void submitInquiry() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading", false);
        VisitorServicesManager.getVisitorServicesManager().submitForm(serviceFormModel, new ApiDetailCallback<String>() {
            @Override
            public void onSuccess(String result) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    createDialog(result);
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.brownish_orange);
            }
        });

    }


    private void createDialog(String feedbackResponse) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_form_response, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView tvTitle = dialogView.findViewById(R.id.dialog_form_response_tvTitle);
        final TextView tvMessage = dialogView.findViewById(R.id.dialog_form_response_tvMessage);
        final TextView tvOkay = dialogView.findViewById(R.id.dialog_form_response_tvOkay);

        tvTitle.setText(String.format(getString(R.string.apply_form_responce), visitorService.getName()));
        tvMessage.setText(feedbackResponse);
        tvOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                getActivity().onBackPressed();
            }
        });
        alertDialog.show();
        final int width = (int) getResources().getDimension(R.dimen._250sdp);
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

    }


    private String getTime(int hr, int min, boolean english, boolean is24Hr) {
        final Calendar calendar = Calendar.getInstance(english ? Locale.ENGLISH : Locale.getDefault());
        calendar.set(Calendar.HOUR, hr);
        calendar.set(Calendar.MINUTE, min);
//        final Format formatter = is24Hr ? new SimpleDateFormat("HH:mm", english ? Locale.ENGLISH : Locale.getDefault()) : new SimpleDateFormat("hh:mm a", english ? Locale.ENGLISH : Locale.getDefault());
        final Format formatter = is24Hr ? new SimpleDateFormat("HH:mm", english ? Locale.ENGLISH : Locale.ENGLISH) : new SimpleDateFormat("hh:mm a", english ? Locale.ENGLISH : Locale.ENGLISH);
        return formatter.format(calendar.getTime());
    }


    private String getTodaysDate() {
        final Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        return String.format(Locale.ENGLISH, "%04d-%02d-%02d", calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DAY_OF_MONTH));
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        selectedTime = "";
        serviceApplyBinding.frSerEnqTvtime.setText("");
        selectedDate = String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
        serviceApplyBinding.frSerEnqTvdate.setText(String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth));
        Log.e("Date", selectedDate);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        selectedTime = getTime(hourOfDay, minute, true, true);
        serviceApplyBinding.frSerEnqTvtime.setText(getTime(hourOfDay, minute, false, false));
        Log.e("Time", selectedTime);
    }
}
