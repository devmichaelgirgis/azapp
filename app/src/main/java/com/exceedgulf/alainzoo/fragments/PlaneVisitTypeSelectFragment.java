package com.exceedgulf.alainzoo.fragments;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
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
import com.exceedgulf.alainzoo.adapter.AnimalsCategorysAdapter;
import com.exceedgulf.alainzoo.adapter.PlanVisitTypeAdapter;
import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.database.models.AnimalsCategory;
import com.exceedgulf.alainzoo.databinding.FragmentPlaneVisitTypeSelectBinding;
import com.exceedgulf.alainzoo.managers.AnimalManager;
import com.exceedgulf.alainzoo.managers.AttractionsManager;
import com.exceedgulf.alainzoo.managers.ExperienceManager;
import com.exceedgulf.alainzoo.managers.WhatsNewManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.NetUtil;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.views.CustomTextview;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaneVisitTypeSelectFragment extends BaseFragment implements AnimalsCategorysAdapter.OnSelectedCategory {
    public static final String KEY_SELECTION_TYPE = "SELECTION_TYPE";
    String selectType = "";
    private FragmentPlaneVisitTypeSelectBinding planeVisitTypeSelectBinding;
    private PlanVisitTypeAdapter planVisitTypeAdapter;
    private PlanVisitTypeAdapter planVisitTypeFilterAnimalAdapter;
    private AnimalsCategorysAdapter animalsCategorysAdapter;
    private int pageNumber = 0;
    private boolean isLoading = false;
    private boolean isLoadMore = true;

    private int categoryId = 0;
    private int pageNumberFilter = 0;
    private boolean isLoadingFilter = false;
    private boolean isLoadMoreFilter = true;
    private LinearLayoutManager layoutManagerFilter;

    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        planeVisitTypeSelectBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_plane_visit_type_select, container, false);
        return planeVisitTypeSelectBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        planeVisitTypeSelectBinding.expandableLayout.collapse();
        ((HomeActivity) getActivity()).disableCollapse();
        final Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(KEY_SELECTION_TYPE)) {
            selectType = bundle.getString(KEY_SELECTION_TYPE);
        }
        planVisitTypeAdapter = new PlanVisitTypeAdapter(getActivity());
        layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManagerFilter = new GridLayoutManager(getActivity(), 2);


        planeVisitTypeSelectBinding.frAddAnimalRvplanes.setLayoutManager(layoutManager);
        planeVisitTypeSelectBinding.frAddAnimalRvplanes.setEmptyView(planeVisitTypeSelectBinding.frPlanVisitTvEmptyView);
        planeVisitTypeSelectBinding.frAddAnimalRvplanes.setAdapter(planVisitTypeAdapter);
//        planeVisitTypeSelectBinding.frAddAnimalRvplanes.addOnScrollListener(recyclerViewOnScrollListener);


        animalsCategorysAdapter = new AnimalsCategorysAdapter(getActivity(), this,false);
        planeVisitTypeSelectBinding.frAniRvfilter.setLayoutManager(new LinearLayoutManager(getActivity()));
        planeVisitTypeSelectBinding.frAniRvfilter.setAdapter(animalsCategorysAdapter);


        planVisitTypeFilterAnimalAdapter = new PlanVisitTypeAdapter(getActivity());
        planeVisitTypeSelectBinding.frAniRvfilteAnimal.setLayoutManager(layoutManagerFilter);
        planeVisitTypeSelectBinding.frAniRvfilteAnimal.setAdapter(planVisitTypeFilterAnimalAdapter);
//        planeVisitTypeSelectBinding.frAniRvfilteAnimal.addOnScrollListener(recyclerViewOnScrollListenerFilter);


        planeVisitTypeSelectBinding.frplanselecTvtitle.setText(String.format(getString(R.string.select_the_plan_your_visit), selectType));
        if (selectType.equalsIgnoreCase(getString(R.string.mAnimals))) {
            planeVisitTypeSelectBinding.frPlanVisitIvziczac.setColorFilter(ContextCompat.getColor(getActivity(), R.color.cool_blue));
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.add_animals), getResources().getColor(R.color.cool_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
            getAnimalList();
        } else if (selectType.equalsIgnoreCase(getString(R.string.mExperiences))) {
            planeVisitTypeSelectBinding.frPlanVisitIvziczac.setColorFilter(ContextCompat.getColor(getActivity(), R.color.cool_blue));
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.add_experinces), getResources().getColor(R.color.cool_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
            getExperianceslist();
        } else if (selectType.equalsIgnoreCase(getString(R.string.attractions))) {
            planeVisitTypeSelectBinding.frPlanVisitIvziczac.setColorFilter(ContextCompat.getColor(getActivity(), R.color.cool_blue));
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.add_attractions), getResources().getColor(R.color.cool_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
            getAttractionList();
        } else if (selectType.equalsIgnoreCase(getString(R.string.title_whats_new))) {
            planeVisitTypeSelectBinding.frPlanVisitIvziczac.setColorFilter(ContextCompat.getColor(getActivity(), R.color.cool_blue));
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.add_activites), getResources().getColor(R.color.cool_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
            getWhatsNewList();
        }

        planeVisitTypeSelectBinding.frAniLlDOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Fragment fragment = PlaneVisitTypeSelectFragment.this.getTargetFragment();
                if (fragment != null && (fragment instanceof PlanVisitDetailFragment)) {
                    ((PlanVisitDetailFragment) fragment).setSelectedItems(selectType);
                    getActivity().onBackPressed();
                }
            }
        });

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
                    planVisitTypeFilterAnimalAdapter.addAllModel(resultList);
                    isLoadingFilter = false;
                    planeVisitTypeSelectBinding.frAniTvEmptyViewFilter.setText(planVisitTypeFilterAnimalAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }

            @Override
            public void onFaild(String message) {
                isLoadingFilter = false;
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
                if (isAdded()) {
                    planeVisitTypeSelectBinding.frAniTvEmptyViewFilter.setText(planVisitTypeFilterAnimalAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        });
    }

    public void getAnimalList() {
        isLoading = true;
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        AnimalManager.getAnimalManager().getAllEntitiesData(pageNumber, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                isLoadMore = isloadmore;
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    planVisitTypeAdapter.addAllModel(resultList);
                    isLoading = false;
                    planeVisitTypeSelectBinding.frPlanVisitTvEmptyView.setText(planVisitTypeAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                    //getCelebrityData();
                }
            }

            @Override
            public void onFaild(String message) {
                isLoading = false;
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
                if (isAdded()) {
                    planeVisitTypeSelectBinding.frPlanVisitTvEmptyView.setText(planVisitTypeAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, false);
    }

    private void getCelebrityData() {
        planeVisitTypeSelectBinding.frAniRvfilteAnimal.setEmptyView(planeVisitTypeSelectBinding.frAniTvEmptyViewFilter);
        categoryId = 0;
        planeVisitTypeSelectBinding.frAniRvfilteAnimal.setVisibility(View.VISIBLE);
        planeVisitTypeSelectBinding.frAddAnimalRvplanes.setVisibility(View.GONE);
        planeVisitTypeSelectBinding.frPlanVisitTvEmptyView.setVisibility(View.GONE);
        planeVisitTypeSelectBinding.frAniRvfilteAnimal.removeAllViews();
        planVisitTypeFilterAnimalAdapter.clearItems();
        planeVisitTypeSelectBinding.frAniTvEmptyViewFilter.setVisibility(View.VISIBLE);
        planeVisitTypeSelectBinding.frAniTvEmptyViewFilter.setText(getString(R.string.fetching_data));
        getCelebrityAnimalList();
    }

    public void getExperianceslist() {
        isLoading = true;
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        ExperienceManager.getExperienceManager().getAllEntitiesData(pageNumber, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                isLoadMore = isloadmore;
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    planVisitTypeAdapter.addAllModel(resultList);
                    isLoading = false;
                    planeVisitTypeSelectBinding.frPlanVisitTvEmptyView.setText(planVisitTypeAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }


            @Override
            public void onFaild(String message) {
                isLoading = false;
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
                if (isAdded()) {
                    planeVisitTypeSelectBinding.frPlanVisitTvEmptyView.setText(planVisitTypeAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, false);
    }

    public void getAttractionList() {
        isLoading = true;
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        AttractionsManager.getAttractionManager().getAllEntitiesData(pageNumber, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                isLoadMore = isloadmore;
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    planVisitTypeAdapter.addAllModel(resultList);
                    isLoading = false;
                    planeVisitTypeSelectBinding.frPlanVisitTvEmptyView.setText(planVisitTypeAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }


            @Override
            public void onFaild(String message) {
                isLoading = false;
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
                if (isAdded()) {
                    planeVisitTypeSelectBinding.frPlanVisitTvEmptyView.setText(planVisitTypeAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, false);
    }

    public void getWhatsNewList() {
        isLoading = true;
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        WhatsNewManager.getWhatsnewManager().getAllEntitiesData(pageNumber, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                isLoadMore = isloadmore;
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    planVisitTypeAdapter.addAllModel(resultList);
                    isLoading = false;
                    planeVisitTypeSelectBinding.frPlanVisitTvEmptyView.setText(planVisitTypeAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }


            @Override
            public void onFaild(String message) {
                isLoading = false;
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
                if (isAdded()) {
                    planeVisitTypeSelectBinding.frPlanVisitTvEmptyView.setText(planVisitTypeAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            if (selectType.equalsIgnoreCase(getString(R.string.mAnimals))) {
                planeVisitTypeSelectBinding.frPlanVisitIvziczac.setColorFilter(ContextCompat.getColor(getActivity(), R.color.cool_blue));
                ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.add_animals), getResources().getColor(R.color.cool_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
                //getAnimalList();
            } else if (selectType.equalsIgnoreCase(getString(R.string.mExperiences))) {
                planeVisitTypeSelectBinding.frPlanVisitIvziczac.setColorFilter(ContextCompat.getColor(getActivity(), R.color.cool_blue));
                ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.add_experinces), getResources().getColor(R.color.cool_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
                //getExperianceslist();
            } else if (selectType.equalsIgnoreCase(getString(R.string.attractions))) {
                planeVisitTypeSelectBinding.frPlanVisitIvziczac.setColorFilter(ContextCompat.getColor(getActivity(), R.color.cool_blue));
                ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.add_attractions), getResources().getColor(R.color.cool_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
                //getAttractionList();
            } else if (selectType.equalsIgnoreCase(getString(R.string.title_whats_new))) {
                planeVisitTypeSelectBinding.frPlanVisitIvziczac.setColorFilter(ContextCompat.getColor(getActivity(), R.color.cool_blue));
                ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.add_activites), getResources().getColor(R.color.cool_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
                //getWhatsNewList();
            }

           // ((HomeActivity) getActivity()).setToolbarWithCenterTitle(selectType, getResources().getColor(R.color.cool_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.addanimal, menu);
        final LayoutInflater baseInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View myCustomView = baseInflater.inflate(R.layout.menu_item_textview, null);
        ((CustomTextview) myCustomView).setText(getString(R.string.done));
        final MenuItem item = menu.findItem(R.id.action_add_animal_done);
        final MenuItem itemfilter = menu.findItem(R.id.action_add_animal_filter);
        itemfilter.setVisible(selectType.equalsIgnoreCase(getString(R.string.mAnimals)));
        item.setActionView(myCustomView);
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Fragment fragment = PlaneVisitTypeSelectFragment.this.getTargetFragment();
                if (fragment != null && (fragment instanceof PlanVisitDetailFragment)) {
                    ((PlanVisitDetailFragment) fragment).setSelectedItems(selectType);
                    getActivity().onBackPressed();
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

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
//                    if (selectType.equalsIgnoreCase(getString(R.string.mAnimals))) {
//                        getFeaturesList();
//                    } else if (selectType.equalsIgnoreCase(getString(R.string.mExperiences))) {
//                        getExperianceslist();
//                    } else if (selectType.equalsIgnoreCase(getString(R.string.attractions))) {
//                        //getAttractionList();
//                    }
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


    private void getAnimalCategory() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        AnimalManager.getAnimalManager().getAllAnimalsCategoryData(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    animalsCategorysAdapter.addItem(resultList);
                    planeVisitTypeSelectBinding.expandableLayout.expand();
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (!isOnClick()) {
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.action_add_animal_filter) {
            if (animalsCategorysAdapter.getItemCount() == 0) {
                getAnimalCategory();
            } else {
                if (planeVisitTypeSelectBinding.expandableLayout.isExpanded()) {
                    planeVisitTypeSelectBinding.expandableLayout.collapse();
                } else {
                    if (NetUtil.isNetworkAvailable(getActivity())) {
                        planeVisitTypeSelectBinding.expandableLayout.expand();
                    } else {
                        SnackbarUtils.loadSnackBar(getString(R.string.no_internet_), getActivity(), R.color.cool_blue);
                        if (animalsCategorysAdapter.getItemCount() > 0) {
                            planeVisitTypeSelectBinding.expandableLayout.expand();
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSelectedItem(AnimalsCategory animalsCategory) {
        if (animalsCategory != null && animalsCategory.getId() != -1 && animalsCategory.getId() != -2) {
            pageNumberFilter = 0;
            planeVisitTypeSelectBinding.expandableLayout.collapse();
            planeVisitTypeSelectBinding.frAniRvfilteAnimal.setEmptyView(planeVisitTypeSelectBinding.frAniTvEmptyViewFilter);
            categoryId = animalsCategory.getId();
            planeVisitTypeSelectBinding.frAniRvfilteAnimal.setVisibility(View.VISIBLE);
            planeVisitTypeSelectBinding.frAddAnimalRvplanes.setVisibility(View.GONE);
            planeVisitTypeSelectBinding.frPlanVisitTvEmptyView.setVisibility(View.GONE);
            planeVisitTypeSelectBinding.frAniRvfilteAnimal.removeAllViews();
            planVisitTypeFilterAnimalAdapter.clearItems();
            planeVisitTypeSelectBinding.frAniTvEmptyViewFilter.setText(getString(R.string.fetching_data));
            planeVisitTypeSelectBinding.frAniTvEmptyViewFilter.setVisibility(View.VISIBLE);
            getFilterAnimalList();
        } else {
            if (animalsCategory.getId() == -2) {
                planeVisitTypeSelectBinding.expandableLayout.collapse();
                planeVisitTypeSelectBinding.frAniRvfilteAnimal.setEmptyView(planeVisitTypeSelectBinding.frAniTvEmptyViewFilter);
                categoryId = 0;
                planeVisitTypeSelectBinding.frAniRvfilteAnimal.setVisibility(View.VISIBLE);
                planeVisitTypeSelectBinding.frAddAnimalRvplanes.setVisibility(View.GONE);
                planeVisitTypeSelectBinding.frPlanVisitTvEmptyView.setVisibility(View.GONE);
                planeVisitTypeSelectBinding.frAniRvfilteAnimal.removeAllViews();
                planVisitTypeFilterAnimalAdapter.clearItems();
                planeVisitTypeSelectBinding.frAniTvEmptyViewFilter.setVisibility(View.VISIBLE);
                planeVisitTypeSelectBinding.frAniTvEmptyViewFilter.setText(getString(R.string.fetching_data));
                getCelebrityAnimalList();
            } else {
                categoryId = 0;
                planeVisitTypeSelectBinding.expandableLayout.collapse();
                planeVisitTypeSelectBinding.frAniRvfilteAnimal.removeEmptyView();
                planeVisitTypeSelectBinding.frAddAnimalRvplanes.setVisibility(View.VISIBLE);
                planeVisitTypeSelectBinding.frAniTvEmptyViewFilter.setVisibility(View.GONE);
                planeVisitTypeSelectBinding.frAniRvfilteAnimal.setVisibility(View.GONE);
                planeVisitTypeSelectBinding.frAniTvEmptyViewFilter.setVisibility(View.GONE);
            }
        }
    }

    private void getCelebrityAnimalList() {
        final ArrayList<Animal> tmpArray = new ArrayList<>();
        for (int i = 0; i < planVisitTypeAdapter.getPlanvisitTypeArrayListpe().size(); i++) {
            final Object object = planVisitTypeAdapter.getPlanvisitTypeArrayListpe().get(i);
            if (object instanceof Animal) {
                final Animal animal = (Animal) object;
                if (animal.getIs_celebrity().equalsIgnoreCase("yes")) {
                    tmpArray.add(animal);
                }
            }
        }
        planVisitTypeFilterAnimalAdapter.addAllModel(tmpArray);
    }
}
