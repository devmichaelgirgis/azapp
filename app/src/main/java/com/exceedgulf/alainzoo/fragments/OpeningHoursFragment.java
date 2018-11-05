package com.exceedgulf.alainzoo.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.database.models.OpeningHours;
import com.exceedgulf.alainzoo.database.models.OpeningHoursChild;
import com.exceedgulf.alainzoo.databinding.FragmentOpeningHoursBinding;
import com.exceedgulf.alainzoo.databinding.RowOpeningHoursBinding;
import com.exceedgulf.alainzoo.databinding.RowOpeningHoursTimeItemBinding;
import com.exceedgulf.alainzoo.managers.OpeningHoursManager;
import com.exceedgulf.alainzoo.models.OpeningHourFront;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.ImageUtil;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.util.ArrayList;


/**
 * Created by P.G. on 25/12/17
 */
public class OpeningHoursFragment extends BaseFragment {
    private static final int OPENING_ZOO_ID_EN = 1353;
    private static final int OPENING_ZOO_ID_AR = 1500;
    private FragmentOpeningHoursBinding fragmentOpeningHoursBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentOpeningHoursBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_opening_hours, container, false);

        return fragmentOpeningHoursBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_opening_hours), getResources().getColor(R.color.dark_blue_grey), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        getOpeningHoursList();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_opening_hours), getResources().getColor(R.color.dark_blue_grey), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        }
    }

    public void getOpeningHoursList() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        OpeningHoursManager.getOpeningHoursManager().getAllEntitiesData(0, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    final ArrayList<OpeningHours> openingHoursArrayList = new ArrayList<>();
                    if (resultList != null && resultList.size() > 0) {
                        for (int i = 0; i < resultList.size(); i++) {
                            final OpeningHours openingHours = (OpeningHours) resultList.get(i);
                            if (openingHours.getId() == OPENING_ZOO_ID_EN || openingHours.getId() == OPENING_ZOO_ID_AR) {
                                openingHoursArrayList.add(0, openingHours);
                            } else {
                                openingHoursArrayList.add(openingHours);
                            }
                        }
                    }
                    setOpeningHourseData(openingHoursArrayList);
                    fragmentOpeningHoursBinding.frOphrTvEmptyView.setText(fragmentOpeningHoursBinding.frOpeninghrRootview.getChildCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }


            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.dark_blue_grey);
                if (isAdded()) {
                    fragmentOpeningHoursBinding.frOphrTvEmptyView.setText(fragmentOpeningHoursBinding.frOpeninghrRootview.getChildCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, false);
    }

    private void setOpeningHourseData(final ArrayList<OpeningHours> openingHoursArrayList) {
        fragmentOpeningHoursBinding.frOphrTvEmptyView.setVisibility(openingHoursArrayList.size() > 0 ? View.GONE : View.VISIBLE);
        for (OpeningHours openingHours : openingHoursArrayList) {
            final RowOpeningHoursBinding rowOpeningHoursBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.row_opening_hours, fragmentOpeningHoursBinding.frOpeninghrRootview, false);

            int child = 0;
            int color;
            if (openingHours.getTitle().equalsIgnoreCase(getString(R.string.zoo_s_hours).trim())) {
                color = ContextCompat.getColor(getActivity(), R.color.cool_blue);
            } else if (openingHours.getTitle().equalsIgnoreCase(getString(R.string.szdlc_hours).trim())) {
                color = ContextCompat.getColor(getActivity(), R.color.snot);
            } else {
                color = ContextCompat.getColor(getActivity(), R.color.very_light_brown);
            }
            rowOpeningHoursBinding.rowOphrTvtitle.setText(openingHours.getTitle());
            rowOpeningHoursBinding.rowOphrTvtitle.setTextColor(color);
            for (OpeningHoursChild openingHoursChild : openingHours.getOpening_hours()) {
                final RowOpeningHoursTimeItemBinding rowOpeningHoursTimeItemBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.row_opening_hours_time_item, rowOpeningHoursBinding.rowOphrLltime, false);
                final boolean col1 = TextUtils.isEmpty(Html.fromHtml(openingHoursChild.getCol1()));
                final boolean col2 = TextUtils.isEmpty(Html.fromHtml(openingHoursChild.getCol2()));
                final boolean col3 = TextUtils.isEmpty(Html.fromHtml(openingHoursChild.getCol3()));

                rowOpeningHoursTimeItemBinding.rowOphrtimeTvcol1.setVisibility(col1 ? View.INVISIBLE : View.VISIBLE);
                rowOpeningHoursTimeItemBinding.rowOphrtimeTvcol2.setVisibility(col2 ? View.INVISIBLE : View.VISIBLE);
                rowOpeningHoursTimeItemBinding.rowOphrtimeTvcol3.setVisibility(col3 ? View.GONE : View.VISIBLE);
                ImageUtil.loadImageFromPicasso(getActivity(), openingHours.getThumbnail_image(), rowOpeningHoursBinding.rowOphrIvmain, rowOpeningHoursBinding.rowOphrIvplaceholder);
                if (child == 0) {
                    rowOpeningHoursTimeItemBinding.rowOphrtimeTvcol1.setText(Html.fromHtml(openingHoursChild.getCol1().toUpperCase()).toString().trim());
                    rowOpeningHoursTimeItemBinding.rowOphrtimeTvcol2.setText(Html.fromHtml(openingHoursChild.getCol2().toUpperCase()).toString().trim());
                    rowOpeningHoursTimeItemBinding.rowOphrtimeTvcol3.setText(Html.fromHtml(openingHoursChild.getCol3().toUpperCase()).toString().trim());
                    rowOpeningHoursBinding.rowOphrLltime.addView(rowOpeningHoursTimeItemBinding.getRoot());
                } else {
                    rowOpeningHoursTimeItemBinding.rowOphrtimeTvcol1.setText(Html.fromHtml(openingHoursChild.getCol1()).toString().trim());
                    rowOpeningHoursTimeItemBinding.rowOphrtimeTvcol2.setText(Html.fromHtml(openingHoursChild.getCol2()).toString().trim());
                    rowOpeningHoursTimeItemBinding.rowOphrtimeTvcol3.setText(Html.fromHtml(openingHoursChild.getCol3()).toString().trim());

                    rowOpeningHoursTimeItemBinding.rowOphrtimeTvcol1.setTextColor(color);
                    rowOpeningHoursTimeItemBinding.rowOphrtimeTvcol2.setTextColor(color);
                    rowOpeningHoursTimeItemBinding.rowOphrtimeTvcol3.setTextColor(color);
                    rowOpeningHoursBinding.rowOphrLltime.addView(rowOpeningHoursTimeItemBinding.getRoot());
                }
                child++;
            }
            fragmentOpeningHoursBinding.frOpeninghrRootview.addView(rowOpeningHoursBinding.getRoot());
        }

    }

}
