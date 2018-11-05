package com.exceedgulf.alainzoo.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.EducationAdapter;
import com.exceedgulf.alainzoo.databinding.FragmentEducationBinding;
import com.exceedgulf.alainzoo.managers.EducationManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.util.ArrayList;

/**
 * Created by P.G. on 27/12/17
 */
public class EducationFragment extends BaseFragment {
    private FragmentEducationBinding fragmentEducationBinding;
    private int pageNumber = 0;
    private LinearLayoutManager layoutManager;
    private boolean isLoading = false;
    private boolean isLoadMore = true;
    private EducationAdapter educationAdapter;
//    private final RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
//        @Override
//        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            super.onScrollStateChanged(recyclerView, newState);
//        }
//
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//            int visibleItemCount = layoutManager.getChildCount();
//            int totalItemCount = layoutManager.getItemCount();
//            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//
//            if (!isLoading && isLoadMore) {
//                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                        && firstVisibleItemPosition >= 0) {
//                    pageNumber++;
//                    getEducationList();
//                }
//            }
//        }
//    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentEducationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_education, container, false);
        return fragmentEducationBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.mEducation), ContextCompat.getColor(getActivity(), R.color.light_eggplant), null, false);


        layoutManager = new LinearLayoutManager(getActivity());
        fragmentEducationBinding.frEduRvmain.setLayoutManager(layoutManager);
        fragmentEducationBinding.frEduRvmain.setEmptyView(fragmentEducationBinding.frEduTvEmptyView);

        educationAdapter = new EducationAdapter(getActivity());
        fragmentEducationBinding.frEduRvmain.setAdapter(educationAdapter);

//        fragmentEducationBinding.frEduRvmain.addOnScrollListener(recyclerViewOnScrollListener);

        getEducationList();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.mEducation), ContextCompat.getColor(getActivity(), R.color.light_eggplant), null, false);
        }
    }


    public void getEducationList() {
        isLoading = true;
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        EducationManager.getEducationManager().getAllEntitiesData(pageNumber, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                isLoadMore = isloadmore;
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    educationAdapter.addItems(resultList);
                    isLoading = false;
                    fragmentEducationBinding.frEduTvEmptyView.setText(educationAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }

            @Override
            public void onFaild(String message) {
                isLoading = false;
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.light_eggplant);
                if (isAdded()) {
                    fragmentEducationBinding.frEduTvEmptyView.setText(educationAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, false);
    }
}
