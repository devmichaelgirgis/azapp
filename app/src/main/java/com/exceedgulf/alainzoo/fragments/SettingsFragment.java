package com.exceedgulf.alainzoo.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.activity.LanguageActivity;
import com.exceedgulf.alainzoo.databinding.FragmentSettingsBinding;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;

/**
 * Created by P.P on 25/01/18.
 */

public class SettingsFragment extends BaseFragment {

    private FragmentSettingsBinding settingsBinding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        settingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        return settingsBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.mSettings), getResources().getColor(R.color.colorSetting), null, true);

        final String strLanguage = SharedPrefceHelper.getInstance().get(PrefCons.APP_LANGUAGE, "en");
        if (strLanguage.equalsIgnoreCase("en")) {
            settingsBinding.fragSettingTvLanguage.setText(R.string.lang1_title);
        } else {
            settingsBinding.fragSettingTvLanguage.setText(R.string.lang2_title);
        }
        settingsBinding.fragSettingLlLanguage.setOnClickListener(this);

        settingsBinding.fragSettingSwNotification.setChecked(SharedPrefceHelper.getInstance().get(PrefCons.ONESIGNAL_NOTIFICATION, false));
        settingsBinding.fragSettingSwBeacon.setChecked(SharedPrefceHelper.getInstance().get(PrefCons.BEACON_NOTIFICATION, false));


        settingsBinding.fragSettingSwBeacon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPrefceHelper.getInstance().save(PrefCons.BEACON_NOTIFICATION, isChecked);
                if (isChecked) {
                    ((HomeActivity) getActivity()).userBluetoothDenied = false;
                    ((HomeActivity) getActivity()).userLocationDenied = false;
                    ((HomeActivity) getActivity()).userLocationPermDenied = false;
                    ((HomeActivity) getActivity()).checkBeaconPermissions();
                }
            }
        });

        settingsBinding.fragSettingSwNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPrefceHelper.getInstance().save(PrefCons.ONESIGNAL_NOTIFICATION, isChecked);
                AppAlainzoo.getAppAlainzoo().initNotification();
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.mSettings), getResources().getColor(R.color.colorSetting), null, true);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == settingsBinding.fragSettingLlLanguage) {
            final Intent intent = new Intent(getActivity(), LanguageActivity.class);
            intent.putExtra("KEY_FROM", "SettingFragment");
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
    }

}
