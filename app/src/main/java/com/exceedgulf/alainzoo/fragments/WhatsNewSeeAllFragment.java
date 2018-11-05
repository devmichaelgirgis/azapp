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
import com.exceedgulf.alainzoo.adapter.WhatsNewSeeAllAdapter;
import com.exceedgulf.alainzoo.databinding.FragmentWhatsNewSeeAllBinding;
import com.exceedgulf.alainzoo.managers.AttractionsManager;
import com.exceedgulf.alainzoo.managers.WhatsNewManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.util.ArrayList;

/**
 * Created by P.G Ghasadiya on 25/12/17.
 */

public class WhatsNewSeeAllFragment extends BaseFragment {
    public static final String KEY_SEARCH_KEYWORD = "SEARCH_KEYWORD";
    private FragmentWhatsNewSeeAllBinding fragmentWhatsNewSeeAllBinding;
    private WhatsNewSeeAllAdapter whatsNewSeeAllAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentWhatsNewSeeAllBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_whats_new_see_all, container, false);
        return fragmentWhatsNewSeeAllBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_whats_new), getResources().getColor(R.color.slate_grey), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        whatsNewSeeAllAdapter = new WhatsNewSeeAllAdapter(getActivity());
        fragmentWhatsNewSeeAllBinding.frWhnRvSeeall.setLayoutManager(new LinearLayoutManager(getActivity()));
        fragmentWhatsNewSeeAllBinding.frWhnRvSeeall.setAdapter(whatsNewSeeAllAdapter);
        getAttractionList();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.whatsnew, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (!isOnClick()) {
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.action_what_search) {
            final Fragment fragment = ((HomeActivity) getActivity()).getcurrentFragment();
            final SearchFragment searchFragment = new SearchFragment();
            final Bundle bundle = new Bundle();
            bundle.putString(KEY_SEARCH_KEYWORD, getString(R.string.title_whats_new));
            searchFragment.setArguments(bundle);
            ((HomeActivity) getActivity()).addFragment(fragment, searchFragment);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_whats_new), getResources().getColor(R.color.slate_grey), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        }
    }


    public void getAttractionList() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        WhatsNewManager.getWhatsnewManager().getAllEntitiesData(0, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    whatsNewSeeAllAdapter.addItems(resultList);
                    fragmentWhatsNewSeeAllBinding.frWhnTvEmptyView.setText(whatsNewSeeAllAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }


            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.slate_grey);
                if (isAdded()) {
                    fragmentWhatsNewSeeAllBinding.frWhnTvEmptyView.setText(whatsNewSeeAllAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, false);
    }
}
