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
import com.exceedgulf.alainzoo.adapter.VisitorServicesAdapter;
import com.exceedgulf.alainzoo.databinding.FragmentServicesBinding;
import com.exceedgulf.alainzoo.managers.VisitorServicesManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.util.ArrayList;

/**
 * Created by P.G. on 25/12/17
 */
public class VisiterServicesFragment extends BaseFragment {
    private FragmentServicesBinding fragmentServicesBinding;
    private VisitorServicesAdapter servicesAdapter;
    private int pageNumber = 0;
    private LinearLayoutManager layoutManager;
    private boolean isLoading = false;
    private boolean isLoadMore = true;

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
//                    getVisitorServicesList();
//                }
//            }
//        }
//    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentServicesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_services, container, false);
        return fragmentServicesBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.services), ContextCompat.getColor(getActivity(), R.color.brownish_orange), null, false);
        layoutManager = new LinearLayoutManager(getActivity());

        fragmentServicesBinding.frSerRvservice.setLayoutManager(layoutManager);
        fragmentServicesBinding.frSerRvservice.setEmptyView(fragmentServicesBinding.frSerTvEmptyView);

        servicesAdapter = new VisitorServicesAdapter(getActivity());
        fragmentServicesBinding.frSerRvservice.setAdapter(servicesAdapter);

        //fragmentServicesBinding.frSerRvservice.addOnScrollListener(recyclerViewOnScrollListener);

        getVisitorServicesList();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.services), ContextCompat.getColor(getActivity(), R.color.brownish_orange), null, false);
        }
    }

    public void getVisitorServicesList() {
        isLoading = true;
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        VisitorServicesManager.getVisitorServicesManager().getAllEntitiesData(pageNumber, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                isLoadMore = isloadmore;
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    servicesAdapter.addItems(resultList);
                    isLoading = false;
                    fragmentServicesBinding.frSerTvEmptyView.setText(servicesAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }

            @Override
            public void onFaild(String message) {
                isLoading = false;
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.brownish_orange);
                if (isAdded()) {
                    fragmentServicesBinding.frSerTvEmptyView.setText(servicesAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, false);
    }
}
