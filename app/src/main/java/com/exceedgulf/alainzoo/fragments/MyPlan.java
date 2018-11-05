package com.exceedgulf.alainzoo.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.PlanVisitAdapter;
import com.exceedgulf.alainzoo.databinding.FragmentMyplanBinding;
import com.exceedgulf.alainzoo.managers.PlansVisitManager;
import com.exceedgulf.alainzoo.models.PlanItem;
import com.exceedgulf.alainzoo.models.RecommendedPlanModel;
import com.exceedgulf.alainzoo.models.VisitAnimalModel;
import com.exceedgulf.alainzoo.models.VisitAttractionsModel;
import com.exceedgulf.alainzoo.models.VisitExperiencesModel;
import com.exceedgulf.alainzoo.models.VisitWhatsNewModel;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;

/**
 * Created by Paras Ghasadiya on 30/01/18.
 */

public class MyPlan extends BaseFragment {
    private FragmentMyplanBinding myplanBinding;
    private PlanVisitAdapter<PlanItem> planVisitAnimalAdapter;
    private PlanVisitAdapter<PlanItem> planVisitWhatsNewAdapter;
    private PlanVisitAdapter<PlanItem> planVisitExperiencesAdapter;
    private PlanVisitAdapter<PlanItem> planVisitAttractionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myplanBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_myplan, container, false);
        return myplanBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.my_plan), getResources().getColor(R.color.cool_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), true);
        getMyPlanList();
    }

    private void getMyPlanList() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        PlansVisitManager.getPlansVisitManager().getMyPlanList(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    if (resultList.size() > 0) {
                        DisplayDialog.getInstance().dismissProgressDialog();
                        final RecommendedPlanModel recommendedPlanModel = (RecommendedPlanModel) resultList.get(0);
                        myplanBinding.frplandetailEdtplanename.setText(recommendedPlanModel.getName());
                        myplanBinding.frplandetailEdtplanename.setEnabled(false);
                        myplanBinding.frplandetailEdtplanename.setFocusable(false);
                        setPlanSelectedData(recommendedPlanModel);
                        myplanBinding.frMyPlanTvEmptyView.setVisibility(View.GONE);
                    } else {
                        myplanBinding.frMyPlanTvEmptyView.setVisibility(View.VISIBLE);
                        myplanBinding.frMyPlanTvEmptyView.setText(getString(R.string.no_data_available));
                    }
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
                if (isAdded()) {
                    myplanBinding.frMyPlanTvEmptyView.setText(getString(R.string.no_data_available));
                }
            }
        });
    }

    private void setPlanSelectedData(final RecommendedPlanModel recommendedPlanModel) {
        planVisitAnimalAdapter = new PlanVisitAdapter<>(getActivity(), getString(R.string.str_plane_noanimal));
        final FlexboxLayoutManager layoutManagerAnimal = new FlexboxLayoutManager(getActivity());
        layoutManagerAnimal.setFlexDirection(FlexDirection.ROW);
        layoutManagerAnimal.setJustifyContent(JustifyContent.FLEX_START);
        myplanBinding.frpdetailRvanimal.setLayoutManager(layoutManagerAnimal);
        myplanBinding.frpdetailRvanimal.setAdapter(planVisitAnimalAdapter);
        if (recommendedPlanModel != null) {
            final ArrayList<PlanItem> planItemAnimalArrayList = new ArrayList<>();
            for (VisitAnimalModel visitAnimalModel : recommendedPlanModel.getAnimalArrayList()) {
                final PlanItem planItem = new PlanItem();
                planItem.setId(visitAnimalModel.getId());
                planItem.setTitle(visitAnimalModel.getName());
                planItemAnimalArrayList.add(planItem);

            }
            planVisitAnimalAdapter.addAllModel(planItemAnimalArrayList);
        }

        planVisitWhatsNewAdapter = new PlanVisitAdapter<>(getActivity(), getString(R.string.str_plane_nowhatsnew));
        final FlexboxLayoutManager layoutManagerWhatsnew = new FlexboxLayoutManager(getActivity());
        layoutManagerWhatsnew.setFlexDirection(FlexDirection.ROW);
        layoutManagerWhatsnew.setJustifyContent(JustifyContent.FLEX_START);
        myplanBinding.frpdetailRvwhatsnew.setLayoutManager(layoutManagerWhatsnew);
        myplanBinding.frpdetailRvwhatsnew.setAdapter(planVisitWhatsNewAdapter);
        if (recommendedPlanModel != null) {
            final ArrayList<PlanItem> planItemWhatsnewArrayList = new ArrayList<>();
            for (VisitWhatsNewModel visitWhatsNewModel : recommendedPlanModel.getVisitWhatsNewModelArrayList()) {
                final PlanItem planItem = new PlanItem();
                planItem.setId(visitWhatsNewModel.getId());
                planItem.setTitle(visitWhatsNewModel.getName());
                planItemWhatsnewArrayList.add(planItem);

            }
            planVisitWhatsNewAdapter.addAllModel(planItemWhatsnewArrayList);
        }

        final FlexboxLayoutManager layoutManagerExperiences = new FlexboxLayoutManager(getActivity());
        layoutManagerExperiences.setFlexDirection(FlexDirection.ROW);
        layoutManagerExperiences.setJustifyContent(JustifyContent.FLEX_START);

        planVisitExperiencesAdapter = new PlanVisitAdapter<>(getActivity(), getString(R.string.str_plane_noexperiance));
        myplanBinding.frpdetailRvexperience.setLayoutManager(layoutManagerExperiences);
        myplanBinding.frpdetailRvexperience.setAdapter(planVisitExperiencesAdapter);
        if (recommendedPlanModel != null) {
            final ArrayList<PlanItem> planItemExperiancesArrayList = new ArrayList<>();
            for (VisitExperiencesModel visitExperiencesModel : recommendedPlanModel.getExperienceArrayList()) {
                final PlanItem planItem = new PlanItem();
                planItem.setId(visitExperiencesModel.getId());
                planItem.setTitle(visitExperiencesModel.getName());
                planItemExperiancesArrayList.add(planItem);

            }
            planVisitExperiencesAdapter.addAllModel(planItemExperiancesArrayList);
        }

        final FlexboxLayoutManager layoutManagerAttraction = new FlexboxLayoutManager(getActivity());
        layoutManagerAttraction.setFlexDirection(FlexDirection.ROW);
        layoutManagerAttraction.setJustifyContent(JustifyContent.FLEX_START);

        planVisitAttractionAdapter = new PlanVisitAdapter<>(getActivity(), getString(R.string.str_plane_noattractions));
        myplanBinding.frpdetailRvattractions.setLayoutManager(layoutManagerAttraction);
        myplanBinding.frpdetailRvattractions.setAdapter(planVisitAttractionAdapter);
        if (recommendedPlanModel != null) {

            final ArrayList<PlanItem> planItemAttractionArrayList = new ArrayList<>();
            for (VisitAttractionsModel visitAttractionsModel : recommendedPlanModel.getAttractionArrayList()) {
                final PlanItem planItem = new PlanItem();
                planItem.setId(visitAttractionsModel.getId());
                planItem.setTitle(visitAttractionsModel.getName());
                planItemAttractionArrayList.add(planItem);

            }

            planVisitAttractionAdapter.addAllModel(planItemAttractionArrayList);
        }
        myplanBinding.frpdetailIvanimal.setImageResource(planVisitAnimalAdapter.getAllItems().size() == 0 ? R.drawable.ic_add_animals_empty : R.drawable.ic_add_animals_fill);
        myplanBinding.frpdetailIvwhatsnew.setImageResource(planVisitWhatsNewAdapter.getAllItems().size() == 0 ? R.drawable.ic_add_new_empty : R.drawable.ic_add_new_fill);
        myplanBinding.frpdetailIvexperiances.setImageResource(planVisitExperiencesAdapter.getAllItems().size() == 0 ? R.drawable.ic_add_experience_empty : R.drawable.ic_add_experience_fill);
        myplanBinding.frpdetailIvattractions.setImageResource(planVisitAttractionAdapter.getAllItems().size() == 0 ? R.drawable.ic_add_attractions_empty : R.drawable.ic_add_attractions_fill);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.my_plan), getResources().getColor(R.color.cool_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), true);

        }
    }
}
