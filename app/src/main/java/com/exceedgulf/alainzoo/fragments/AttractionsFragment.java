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
import com.exceedgulf.alainzoo.adapter.AttractionsAdapter;
import com.exceedgulf.alainzoo.databinding.FragmentAttractionsBinding;
import com.exceedgulf.alainzoo.managers.AttractionsManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.util.ArrayList;

/**
 * Created by P.G. on 27/12/17
 */
public class AttractionsFragment extends BaseFragment {
    private FragmentAttractionsBinding fragmentAttractionsBinding;
    private AttractionsAdapter attractionsAdapter;
    private int pageNumber = 0;
    private LinearLayoutManager layoutManager;
    private boolean isLoading = false;
    private boolean isLoadMore = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentAttractionsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_attractions, container, false);
        return fragmentAttractionsBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.attraction), getResources().getColor(R.color.colorSzdcl), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        layoutManager = new LinearLayoutManager(getActivity());
        fragmentAttractionsBinding.frAttraRvmain.setLayoutManager(layoutManager);
        fragmentAttractionsBinding.frAttraRvmain.setEmptyView(fragmentAttractionsBinding.frAttraTvEmptyView);
        attractionsAdapter = new AttractionsAdapter(getActivity());
        fragmentAttractionsBinding.frAttraRvmain.setAdapter(attractionsAdapter);
        //fragmentAttractionsBinding.frAttraRvmain.addOnScrollListener(recyclerViewOnScrollListener);
        getAttractionList();
    }

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
//                    getAttractionList();
//                }
//            }
//        }
//    };

    public void getAttractionList() {
        isLoading = true;
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        AttractionsManager.getAttractionManager().getAllEntitiesData(pageNumber, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                isLoadMore = isloadmore;
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    attractionsAdapter.addItems(resultList);
                    isLoading = false;
                    fragmentAttractionsBinding.frAttraTvEmptyView.setText(attractionsAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }


            @Override
            public void onFaild(String message) {
                isLoading = false;
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.camo);
                if (isAdded()) {
                    fragmentAttractionsBinding.frAttraTvEmptyView.setText(attractionsAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, false);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.attraction), getResources().getColor(R.color.colorSzdcl), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            //fragmentAttractionsBinding.frAttraRvmain.removeOnScrollListener(recyclerViewOnScrollListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
