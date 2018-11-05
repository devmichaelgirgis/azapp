package com.exceedgulf.alainzoo.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.exceedgulf.alainzoo.adapter.AnimalAdapter;
import com.exceedgulf.alainzoo.adapter.AnimalsCategorysAdapter;
import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.database.models.AnimalsCategory;
import com.exceedgulf.alainzoo.databinding.FragmentAnimalBinding;
import com.exceedgulf.alainzoo.managers.AnimalManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.NetUtil;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.util.ArrayList;

/**
 * Created by R.S. on 21/12/17
 */
public class AnimalFragment extends BaseFragment implements AnimalsCategorysAdapter.OnSelectedCategory {
    private FragmentAnimalBinding fragmentAnimalBinding;
    private AnimalsCategorysAdapter animalsCategorysAdapter;
    private AnimalAdapter animalAdapter;
    private AnimalAdapter animalAdapterFilter;
    private int categoryId = 0;

    private int pageNumber = 0;
    private boolean isLoading = false;
    private boolean isLoadMore = true;


    private int pageNumberFilter = 0;
    private boolean isLoadingFilter = false;
    private boolean isLoadMoreFilter = true;

    private LinearLayoutManager layoutManager;
    private LinearLayoutManager layoutManagerFilter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        fragmentAnimalBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_animal, container, false);
        return fragmentAnimalBinding.getRoot();
    }

    @Override
    public void initView(final View view) {
        fragmentAnimalBinding.expandableLayout.collapse();
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.animal), getResources().getColor(R.color.camo), null, false);

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManagerFilter = new LinearLayoutManager(getActivity());

        fragmentAnimalBinding.frAniRvmain.setLayoutManager(layoutManager);
        fragmentAnimalBinding.frAniRvmain.setEmptyView(fragmentAnimalBinding.frAniTvEmptyView);
        animalAdapter = new AnimalAdapter(getActivity());
        fragmentAnimalBinding.frAniRvmain.setAdapter(animalAdapter);
        //fragmentAnimalBinding.frAniRvmain.addOnScrollListener(recyclerViewOnScrollListener);

        animalAdapterFilter = new AnimalAdapter(getActivity());
        fragmentAnimalBinding.frAniRvfilteAnimal.setLayoutManager(layoutManagerFilter);
        fragmentAnimalBinding.frAniRvfilteAnimal.setAdapter(animalAdapterFilter);
        //fragmentAnimalBinding.frAniRvfilteAnimal.addOnScrollListener(recyclerViewOnScrollListenerFilter);

        animalsCategorysAdapter = new AnimalsCategorysAdapter(getActivity(), this,true);
        fragmentAnimalBinding.frAniRvfilter.setLayoutManager(new LinearLayoutManager(getActivity()));
        fragmentAnimalBinding.frAniRvfilter.setAdapter(animalsCategorysAdapter);

        getAnimalList();

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
//                    getFeaturesList();
//                }
//            }
//        }
//    };
//
//    private final RecyclerView.OnScrollListener recyclerViewOnScrollListenerFilter = new RecyclerView.OnScrollListener() {
//        @Override
//        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            super.onScrollStateChanged(recyclerView, newState);
//        }
//
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//            int visibleItemCount = layoutManagerFilter.getChildCount();
//            int totalItemCount = layoutManagerFilter.getItemCount();
//            int firstVisibleItemPosition = layoutManagerFilter.findFirstVisibleItemPosition();
//
//            if (!isLoadingFilter && isLoadMoreFilter) {
//                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                        && firstVisibleItemPosition >= 0) {
//                    pageNumberFilter++;
//                    getFilterAnimalList();
//                }
//            }
//        }
//    };

    public void getAnimalList() {
        isLoading = true;
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        AnimalManager.getAnimalManager().getAllEntitiesData(pageNumber, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                isLoadMore = isloadmore;
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    animalAdapter.addItems(resultList);
                    isLoading = false;
                    fragmentAnimalBinding.frAniTvEmptyView.setText(animalAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                    setSelectedCelebrityData();
                }
            }

            @Override
            public void onFaild(String message) {
                isLoading = false;
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    SnackbarUtils.loadSnackBar(message, getActivity(), R.color.camo);
                    fragmentAnimalBinding.frAniTvEmptyView.setText(animalAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, false);
    }

    public void getFilterAnimalList() {
        isLoadingFilter = true;
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        AnimalManager.getAnimalManager().filterEntitiesData(pageNumberFilter, categoryId, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                isLoadMoreFilter = isloadmore;
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    animalAdapterFilter.addItems(resultList);
                    isLoadingFilter = false;
                    fragmentAnimalBinding.frAniTvEmptyViewFilter.setText(animalAdapterFilter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }

            @Override
            public void onFaild(String message) {
                isLoadingFilter = false;
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.camo);
                if (isAdded()) {
                    fragmentAnimalBinding.frAniTvEmptyViewFilter.setText(animalAdapterFilter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        });
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.animal, menu);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.animal), getResources().getColor(R.color.camo), null, false);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (!isOnClick()) {
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.action_ani_search) {
            final Bundle bundle = new Bundle();
            bundle.putString(SearchFragment.KEY_SEARCH_KEYWORD, getString(R.string.mAnimals));
            final Fragment fragment = new SearchFragment();
            fragment.setArguments(bundle);
            ((HomeActivity) getActivity()).addFragment(AnimalFragment.this, fragment);

        } else if (id == R.id.action_ani_setings) {
            if (animalsCategorysAdapter.getItemCount() == 0) {
                getAnimalCategory();
            } else {
                if (fragmentAnimalBinding.expandableLayout.isExpanded()) {
                    fragmentAnimalBinding.expandableLayout.collapse();
                } else {
                    if (NetUtil.isNetworkAvailable(getActivity())) {
                        fragmentAnimalBinding.expandableLayout.expand();
                    } else {
                        SnackbarUtils.loadSnackBar(getString(R.string.no_internet_), getActivity(), R.color.camo);
                        if (animalsCategorysAdapter.getItemCount() > 0) {
                            fragmentAnimalBinding.expandableLayout.expand();
                        }
                    }
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void getAnimalCategory() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        AnimalManager.getAnimalManager().getAllAnimalsCategoryData(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    animalsCategorysAdapter.addItem(resultList);
                    fragmentAnimalBinding.expandableLayout.expand();
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.camo);
            }
        });
    }

    @Override
    public void onSelectedItem(AnimalsCategory animalsCategory) {
        if (animalsCategory != null && animalsCategory.getId() != -1 && animalsCategory.getId() != -2) {
            pageNumberFilter = 0;
            fragmentAnimalBinding.expandableLayout.collapse();
            fragmentAnimalBinding.frAniRvfilteAnimal.setEmptyView(fragmentAnimalBinding.frAniTvEmptyViewFilter);
            categoryId = animalsCategory.getId();
            fragmentAnimalBinding.frAniRvfilteAnimal.setVisibility(View.VISIBLE);
            fragmentAnimalBinding.frAniRvmain.setVisibility(View.GONE);
            fragmentAnimalBinding.frAniTvEmptyView.setVisibility(View.GONE);
            fragmentAnimalBinding.frAniRvfilteAnimal.removeAllViews();
            animalAdapterFilter.clearItems();
            fragmentAnimalBinding.frAniTvEmptyViewFilter.setVisibility(View.VISIBLE);
            fragmentAnimalBinding.frAniTvEmptyViewFilter.setText(getString(R.string.fetching_data));
            getFilterAnimalList();
        } else {
            if (animalsCategory.getId() == -2) {
                fragmentAnimalBinding.expandableLayout.collapse();
                fragmentAnimalBinding.frAniRvfilteAnimal.setEmptyView(fragmentAnimalBinding.frAniTvEmptyViewFilter);
                categoryId = 0;
                fragmentAnimalBinding.frAniRvfilteAnimal.setVisibility(View.VISIBLE);
                fragmentAnimalBinding.frAniRvmain.setVisibility(View.GONE);
                fragmentAnimalBinding.frAniTvEmptyView.setVisibility(View.GONE);
                fragmentAnimalBinding.frAniRvfilteAnimal.removeAllViews();
                animalAdapterFilter.clearItems();
                fragmentAnimalBinding.frAniTvEmptyViewFilter.setVisibility(View.VISIBLE);
                fragmentAnimalBinding.frAniTvEmptyViewFilter.setText(getString(R.string.fetching_data));
                getCelebrityAnimalList();
            } else {
                categoryId = 0;
                fragmentAnimalBinding.expandableLayout.collapse();
                fragmentAnimalBinding.frAniRvfilteAnimal.removeEmptyView();
                fragmentAnimalBinding.frAniRvmain.setVisibility(View.VISIBLE);
                fragmentAnimalBinding.frAniTvEmptyViewFilter.setVisibility(View.GONE);
                fragmentAnimalBinding.frAniRvfilteAnimal.setVisibility(View.GONE);
                fragmentAnimalBinding.frAniTvEmptyViewFilter.setVisibility(View.GONE);
            }
        }


    }

    private void setSelectedCelebrityData() {
        fragmentAnimalBinding.frAniRvfilteAnimal.setEmptyView(fragmentAnimalBinding.frAniTvEmptyViewFilter);
        categoryId = 0;
        fragmentAnimalBinding.frAniRvfilteAnimal.setVisibility(View.VISIBLE);
        fragmentAnimalBinding.frAniRvmain.setVisibility(View.GONE);
        fragmentAnimalBinding.frAniTvEmptyView.setVisibility(View.GONE);
        fragmentAnimalBinding.frAniRvfilteAnimal.removeAllViews();
        animalAdapterFilter.clearItems();
        fragmentAnimalBinding.frAniTvEmptyViewFilter.setVisibility(View.VISIBLE);
        fragmentAnimalBinding.frAniTvEmptyViewFilter.setText(getString(R.string.fetching_data));
        getCelebrityAnimalList();
    }

    private void getCelebrityAnimalList() {
        animalAdapterFilter.clearItems();
        final ArrayList<Animal> tmpArray = new ArrayList<>();
        for (int i = 0; i < animalAdapter.getAnimalArrayList().size(); i++) {
            final Animal animal = animalAdapter.getAnimalArrayList().get(i);
            if (animal.getIs_celebrity().equalsIgnoreCase("yes")) {
                tmpArray.add(animal);
            }
        }
        animalAdapterFilter.addItems(tmpArray);
    }
}
