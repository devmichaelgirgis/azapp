package com.exceedgulf.alainzoo.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.ExperiencesAdapter;
import com.exceedgulf.alainzoo.databinding.FragmentExperiencesSeeAllBinding;
import com.exceedgulf.alainzoo.managers.ExperienceManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.util.ArrayList;

/**
 * Created by P.G. on 22/12/17
 */
public class ExperiencesFragment extends BaseFragment {
    public static final String KEY_IS_BACKARROW = "IS_BACKARROW";
    private FragmentExperiencesSeeAllBinding fragmentExperiencesSeeAllBinding;
    private ExperiencesAdapter experiencesSeeAllRecyclerViewAdapter;
    private int pageNumber = 0;
    private LinearLayoutManager layoutManager;
    private boolean isLoading = false;
    private boolean isLoadMore = true;
    private boolean isBackArrow = false;

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.experience, menu);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exp_search) {
            final Bundle bundle = new Bundle();
            bundle.putString(SearchFragment.KEY_SEARCH_KEYWORD, getString(R.string.mExperiences));
            final Fragment fragment = new SearchFragment();
            fragment.setArguments(bundle);
            ((HomeActivity) getActivity()).addFragment(ExperiencesFragment.this, fragment);

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentExperiencesSeeAllBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_experiences_see_all, container, false);
        return fragmentExperiencesSeeAllBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        final Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(KEY_IS_BACKARROW)) {
            isBackArrow = true;
        } else {
            isBackArrow = false;
        }
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.mExperiences), getResources().getColor(R.color.tomato), isBackArrow ? ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar) : null, false);
        layoutManager = new LinearLayoutManager(getActivity());
        experiencesSeeAllRecyclerViewAdapter = new ExperiencesAdapter(getActivity());
        fragmentExperiencesSeeAllBinding.rvExperiencesseeAll.setLayoutManager(layoutManager);
        fragmentExperiencesSeeAllBinding.rvExperiencesseeAll.setEmptyView(fragmentExperiencesSeeAllBinding.frExperianTvEmptyView);
        fragmentExperiencesSeeAllBinding.rvExperiencesseeAll.setAdapter(experiencesSeeAllRecyclerViewAdapter);
        //fragmentExperiencesSeeAllBinding.rvExperiencesseeAll.addOnScrollListener(recyclerViewOnScrollListener);
        getExperianceslist();
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
//                    getExperianceslist();
//                }
//            }
//        }
//    };

    public void getExperianceslist() {
        isLoading = true;
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        ExperienceManager.getExperienceManager().getAllEntitiesData(pageNumber, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                isLoadMore = isloadmore;
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    experiencesSeeAllRecyclerViewAdapter.addItems(resultList);
                    isLoading = false;
                    fragmentExperiencesSeeAllBinding.frExperianTvEmptyView.setText(experiencesSeeAllRecyclerViewAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }


            @Override
            public void onFaild(String message) {
                isLoading = false;
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.tomato);
                if (isAdded()) {
                    fragmentExperiencesSeeAllBinding.frExperianTvEmptyView.setText(experiencesSeeAllRecyclerViewAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.mExperiences), getResources().getColor(R.color.tomato), isBackArrow ? ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar) : null, false);
            getExperianceslist();
        }
    }


}
