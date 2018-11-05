package com.exceedgulf.alainzoo.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.database.models.ShuttleService;
import com.exceedgulf.alainzoo.databinding.FragmentShuttleConfirmServiceBinding;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.squareup.picasso.Picasso;

import java.util.Locale;

/**
 * Created by Paras Ghasadiya on 23/01/18.
 */

public class ShuttleConfirmServiceFragment extends BaseFragment {
    public static final String KEY_SELECTED_SHUTTLE = "SELECTED_SHUTTLE";
    public static final String KEY_SHUTTLESERVICE = "shuttle";
    private FragmentShuttleConfirmServiceBinding confirmServiceBinding;
    private int position = -1;
    private ShuttleService shuttleService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        confirmServiceBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_shuttle_confirm_service, container, false);
        return confirmServiceBinding.getRoot();
    }

    public static ShuttleConfirmServiceFragment getShuttleConfirmServiceFragment(final int position) {
        final Bundle bundle = new Bundle();
        bundle.putInt(KEY_SELECTED_SHUTTLE, position);
        final ShuttleConfirmServiceFragment shuttleConfirmServiceFragment = new ShuttleConfirmServiceFragment();
        shuttleConfirmServiceFragment.setArguments(bundle);
        return shuttleConfirmServiceFragment;

    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.shuttle_service), getResources().getColor(R.color.cool_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        final Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(KEY_SHUTTLESERVICE)) {
            shuttleService = (ShuttleService) bundle.getSerializable("shuttle");
            if (LangUtils.getCurrentLanguage().equalsIgnoreCase("ar")) {
                confirmServiceBinding.frshuttelconTvName.setText(shuttleService.getNameAr());
            } else {
                confirmServiceBinding.frshuttelconTvName.setText(shuttleService.getNameEn());
            }
            if (!TextUtils.isEmpty(shuttleService.getIcon())) {
                Picasso.with(getActivity()).load(shuttleService.getIcon()).into(confirmServiceBinding.frshuttelconIvMain);
            }
        }
//        if (bundle != null && bundle.containsKey(KEY_SELECTED_SHUTTLE)) {
//            position = bundle.getInt(KEY_SELECTED_SHUTTLE);
//        }
//        if (position == 0) {
//            confirmServiceBinding.frshuttelconTvName.setText("Zarafa Cafe");
//            confirmServiceBinding.frshuttelconIvMain.setImageResource(R.drawable.ic_zarafa_cafe);
//
//        } else if (position == 1) {
//            confirmServiceBinding.frshuttelconTvName.setText("Main Gate");
//            confirmServiceBinding.frshuttelconIvMain.setImageResource(R.drawable.ic_main_gate);
//        } else if (position == 2) {
//            confirmServiceBinding.frshuttelconTvName.setText("Monkey Highest");
//            confirmServiceBinding.frshuttelconIvMain.setImageResource(R.drawable.ic_monkey_highest);
//        } else if (position == 3) {
//            confirmServiceBinding.frshuttelconTvName.setText("Zarafa Cafe");
//            confirmServiceBinding.frshuttelconIvMain.setImageResource(R.drawable.ic_zarafa_cafe_second);
//        } else if (position == 4) {
//            confirmServiceBinding.frshuttelconTvName.setText("Main Gate");
//            confirmServiceBinding.frshuttelconIvMain.setImageResource(R.drawable.ic_main_gate_second);
//        } else if (position == 5) {
//            confirmServiceBinding.frshuttelconTvName.setText("Monkey Highest");
//            confirmServiceBinding.frshuttelconIvMain.setImageResource(R.drawable.ic_monkey_highest_second);
//        }
        confirmServiceBinding.frshuttelconTvcancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == confirmServiceBinding.frshuttelconTvcancel) {
//            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ((HomeActivity) getActivity()).replaceFragment(new HomeFragment());
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.shuttle_service), getResources().getColor(R.color.cool_blue), null, false);

        }
    }

}
