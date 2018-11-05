package com.exceedgulf.alainzoo.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.PlanVisitAdapter;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.databinding.FragmentPlanConfirmBinding;
import com.exceedgulf.alainzoo.managers.PlansVisitManager;
import com.exceedgulf.alainzoo.models.GeneralPlan;
import com.exceedgulf.alainzoo.models.PlanItem;
import com.exceedgulf.alainzoo.models.RecommendedPlanModel;
import com.exceedgulf.alainzoo.models.VisitAnimalModel;
import com.exceedgulf.alainzoo.models.VisitAttractionsModel;
import com.exceedgulf.alainzoo.models.VisitExperiencesModel;
import com.exceedgulf.alainzoo.models.VisitOrder;
import com.exceedgulf.alainzoo.models.VisitWhatsNewModel;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.utils.Utils;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Paras Ghasadiya on 20/01/18.
 */

public class PlaneConfirmFragment extends BaseFragment {
    private static final String KEY_CONFIRM_PLANE = "CONFIRM_PLANE";
    private static final String KEY_MY_PLAN = "MY_PLAN";
    private FragmentPlanConfirmBinding planConfirmBinding;
    private RecommendedPlanModel recommendedPlanModel = null;
    private String selectedDate = "";
    private PlanVisitAdapter<PlanItem> planVisitAnimalAdapter;
    private PlanVisitAdapter<PlanItem> planVisitWhatsNewAdapter;
    private PlanVisitAdapter<PlanItem> planVisitExperiencesAdapter;
    private PlanVisitAdapter<PlanItem> planVisitAttractionAdapter;
    private boolean isCustomPlan = false;
    private boolean isMyPlan = false;

    public static PlaneConfirmFragment getPlanVisitConfirmFragment(final RecommendedPlanModel recommendedPlanModel) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CONFIRM_PLANE, recommendedPlanModel);
        final PlaneConfirmFragment fragment = new PlaneConfirmFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static PlaneConfirmFragment getPlanVisitConfirmFragment(final RecommendedPlanModel recommendedPlanModel, final boolean isMyPlan) {
        final Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_MY_PLAN, isMyPlan);
        bundle.putSerializable(KEY_CONFIRM_PLANE, recommendedPlanModel);
        final PlaneConfirmFragment fragment = new PlaneConfirmFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        planConfirmBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_plan_confirm, container, false);
        return planConfirmBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).selectedBottomMenuPosition = 2;
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_my_plan), getResources().getColor(R.color.cool_blue), null, false);
        final Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(KEY_MY_PLAN)) {
            recommendedPlanModel = (RecommendedPlanModel) bundle.getSerializable(KEY_CONFIRM_PLANE);
            final ArrayList<RecommendedPlanModel> recommendedPlanModelsList = (ArrayList<RecommendedPlanModel>) AlainZooDB.getInstance().myPlanDao().getAllMyplan();
            if (recommendedPlanModelsList.size() > 0) {
                final RecommendedPlanModel recommendedPlanModelTmp = recommendedPlanModelsList.get(0);
                if (recommendedPlanModel.getId() != recommendedPlanModelTmp.getId()) {
                    getMyPlanList(false);
                }
            }
            setPlanSelectedData(true);
        } else if (bundle != null && bundle.containsKey(KEY_CONFIRM_PLANE)) {
            isMyPlan = true;
            recommendedPlanModel = (RecommendedPlanModel) bundle.getSerializable(KEY_CONFIRM_PLANE);
            getMyPlanList(true);
        } else {
            final ArrayList<RecommendedPlanModel> recommendedPlanModelsList = (ArrayList<RecommendedPlanModel>) AlainZooDB.getInstance().myPlanDao().getAllMyplan();
            if (recommendedPlanModelsList.size() > 0) {
                this.recommendedPlanModel = recommendedPlanModelsList.get(0);
                setPlanSelectedData(true);
                getMyPlanList(false);
            } else {
                getMyPlanList(true);
            }
        }

        planConfirmBinding.frplandetailTvAdult.setText(String.format("%s %s", getString(R.string.suggested_tickets), getString(R.string.for_adult)));
        planConfirmBinding.frplandetailTvChild.setText(String.format("%s %s", getString(R.string.suggested_tickets), getString(R.string.for_child)));

        planConfirmBinding.frpdetailBtnSthowOnMap.setOnClickListener(this);
        planConfirmBinding.frpdetailBtnRemovePlan.setOnClickListener(this);
    }

    private void getMyPlanList(final boolean isShowLoader) {
        if (isShowLoader) {
            DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        }
        PlansVisitManager.getPlansVisitManager().getMyPlanList(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                if (isShowLoader) {
                    DisplayDialog.getInstance().dismissProgressDialog();
                }
                if (isAdded()) {
                    if (resultList.size() > 0) {
                        final ArrayList<RecommendedPlanModel> recommendedPlanModels = resultList;
                        if (recommendedPlanModels.size() > 0) {
                            recommendedPlanModel = recommendedPlanModels.get(0);
                            //final RecommendedPlanModel recommendedPlanModel = recommendedPlanModels.get(0);
                            if (recommendedPlanModel.getVisitOrderArrayList().size() == 0) {
                                isCustomPlan = true;
                                recommendedPlanModel.setCustomPlan(true);
                                final ArrayList<GeneralPlan> generalPlanArrayList = (ArrayList<GeneralPlan>) AlainZooDB.getInstance().myPlanDao().getGeneralplan();
                                if (generalPlanArrayList != null && generalPlanArrayList.size() > 0) {
                                    final GeneralPlan generalPlan = generalPlanArrayList.get(0);
                                    if (generalPlan.getVisitOrderArrayList() != null && generalPlan.getVisitOrderArrayList().size() > 0) {
                                        final ArrayList<VisitOrder> visitOrder = generalPlan.getVisitOrderArrayList();
                                        final ArrayList<VisitOrder> myPlanVisitOrder = recommendedPlanModel.getVisitOrderArrayList();

                                        for (int i = 0; i < recommendedPlanModel.getAnimalArrayList().size(); i++) {
                                            final VisitOrder myVisitorOrder = new VisitOrder();
                                            myVisitorOrder.setId(recommendedPlanModel.getAnimalArrayList().get(i).getId());
                                            myVisitorOrder.setType("animals");
                                            myPlanVisitOrder.add(myVisitorOrder);
                                        }

                                        for (int i = 0; i < recommendedPlanModel.getAttractionArrayList().size(); i++) {
                                            final VisitOrder myVisitorOrder = new VisitOrder();
                                            myVisitorOrder.setId(recommendedPlanModel.getAttractionArrayList().get(i).getId());
                                            myVisitorOrder.setType("attraction");
                                            myPlanVisitOrder.add(myVisitorOrder);
                                        }

                                        for (int i = 0; i < recommendedPlanModel.getExperienceArrayList().size(); i++) {
                                            final VisitOrder myVisitorOrder = new VisitOrder();
                                            myVisitorOrder.setId(recommendedPlanModel.getExperienceArrayList().get(i).getId());
                                            myVisitorOrder.setType("experience");
                                            myPlanVisitOrder.add(myVisitorOrder);
                                        }

                                        for (int i = 0; i < recommendedPlanModel.getVisitWhatsNewModelArrayList().size(); i++) {
                                            final VisitOrder myVisitorOrder = new VisitOrder();
                                            myVisitorOrder.setId(recommendedPlanModel.getVisitWhatsNewModelArrayList().get(i).getId());
                                            myVisitorOrder.setType("events");
                                            myPlanVisitOrder.add(myVisitorOrder);
                                        }

                                        final ArrayList<VisitOrder> newOrder = new ArrayList<>();
                                        for (int i = 0; i < visitOrder.size(); i++) {
                                            final VisitOrder visitOrder1 = visitOrder.get(i);
                                            for (int j = 0; j < myPlanVisitOrder.size(); j++) {
                                                if (visitOrder1.getId() == myPlanVisitOrder.get(j).getId() && visitOrder1.getType().equalsIgnoreCase(myPlanVisitOrder.get(j).getType())) {
                                                    newOrder.add(myPlanVisitOrder.get(j));
                                                    myPlanVisitOrder.remove(j);
                                                }
                                            }
                                        }
                                        recommendedPlanModel.setVisitOrderArrayList(newOrder);
                                    }
                                }
                            } else {
                                isCustomPlan = false;
                                recommendedPlanModel.setCustomPlan(false);
                            }
                            setPlanSelectedData(true);
                            AlainZooDB.getInstance().myPlanDao().deleteAllMyplan();
                            AlainZooDB.getInstance().myPlanDao().insertOrReplaceAll(recommendedPlanModels);
                            planConfirmBinding.frPlanconTvEmptyView.setVisibility(View.GONE);
                        } else {
                            planConfirmBinding.frPlanconTvEmptyView.setVisibility(View.GONE);
                            planConfirmBinding.frMyPlanVisitLlNoMyPlan.setVisibility(View.VISIBLE);
                            planConfirmBinding.frMyPlanVisitTvShowRecommend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((HomeActivity) getActivity()).replaceFragment(new PlanVisitFragment());
                                }
                            });

                        }
                    } else {
                        planConfirmBinding.frPlanconTvEmptyView.setVisibility(View.GONE);
                        planConfirmBinding.frMyPlanVisitLlNoMyPlan.setVisibility(View.VISIBLE);
                        planConfirmBinding.frMyPlanVisitTvShowRecommend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ((HomeActivity) getActivity()).replaceFragment(new PlanVisitFragment());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFaild(String message) {
                if (isShowLoader) {
                    DisplayDialog.getInstance().dismissProgressDialog();
                    SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
                }
            }
        });
    }

    private void setPlanSelectedData(final boolean isMyPlan) {
        this.isMyPlan = isMyPlan;
        planConfirmBinding.frPlanconTvEmptyView.setVisibility(View.GONE);
        planConfirmBinding.frplandetailEdtplanename.setText(recommendedPlanModel.getName());
        planConfirmBinding.frplandetailTvdate.setText(Utils.formatDateTime(recommendedPlanModel.getPlaneDate(), "yyyy-MM-dd'T'HH:mm:ss", "d/MM/yyyy"));
        planConfirmBinding.frplandetailTvduration.setText(recommendedPlanModel.getDuration());
        planConfirmBinding.frplandetailTvduration.setEnabled(false);
        planConfirmBinding.frplandetailTvduration.setFocusable(false);
        planConfirmBinding.frplandetailEdtplanename.setEnabled(false);
        planConfirmBinding.frplandetailEdtplanename.setFocusable(false);

        if (LangUtils.getCurrentLanguage().equalsIgnoreCase("en")) {
            if (recommendedPlanModel.getTicketArrayList() != null && recommendedPlanModel.getTicketArrayList().size() > 0) {
                planConfirmBinding.feplandetailTvAdultTicket.setText(String.format("%s %s", String.valueOf(recommendedPlanModel.getTicketArrayList().get(0).getAdult()), getString(R.string.aed)));
                planConfirmBinding.feplandetailTvChildTicket.setText(String.format("%s %s", String.valueOf(recommendedPlanModel.getTicketArrayList().get(0).getChild()), getString(R.string.aed)));
                planConfirmBinding.feplandetailTvAdultTitle.setText(recommendedPlanModel.getTicketArrayList().get(0).getTitle());
                planConfirmBinding.feplandetailTvChildTitle.setText(recommendedPlanModel.getTicketArrayList().get(0).getTitle());
                planConfirmBinding.feplandetailTvAdultTitle.setVisibility(View.VISIBLE);
                planConfirmBinding.feplandetailTvChildTitle.setVisibility(View.VISIBLE);
            } else {
                planConfirmBinding.feplandetailTvAdultTicket.setText("0 AED");
                planConfirmBinding.feplandetailTvChildTicket.setText("0 AED");
                planConfirmBinding.feplandetailTvAdultTitle.setVisibility(View.GONE);
                planConfirmBinding.feplandetailTvChildTitle.setVisibility(View.GONE);
            }
        } else {
            if (recommendedPlanModel.getTicketArrayListAr() != null && recommendedPlanModel.getTicketArrayListAr().size() > 0) {
                planConfirmBinding.feplandetailTvAdultTicket.setText(String.format("%s %s", String.valueOf(recommendedPlanModel.getTicketArrayListAr().get(0).getAdult()), getString(R.string.aed)));
                planConfirmBinding.feplandetailTvChildTicket.setText(String.format("%s %s", String.valueOf(recommendedPlanModel.getTicketArrayList().get(0).getChild()), getString(R.string.aed)));
                planConfirmBinding.feplandetailTvAdultTitle.setText(recommendedPlanModel.getTicketArrayListAr().get(0).getTitle());
                planConfirmBinding.feplandetailTvChildTitle.setText(recommendedPlanModel.getTicketArrayListAr().get(0).getTitle());
                planConfirmBinding.feplandetailTvAdultTitle.setVisibility(View.VISIBLE);
                planConfirmBinding.feplandetailTvChildTitle.setVisibility(View.VISIBLE);
            } else {
                planConfirmBinding.feplandetailTvAdultTicket.setText("0 AED");
                planConfirmBinding.feplandetailTvChildTicket.setText("0 AED");
                planConfirmBinding.feplandetailTvAdultTitle.setVisibility(View.GONE);
                planConfirmBinding.feplandetailTvChildTitle.setVisibility(View.GONE);
            }
        }
        planVisitAnimalAdapter = new PlanVisitAdapter<>(getActivity(), getString(R.string.str_plane_noanimal));
        final FlexboxLayoutManager layoutManagerAnimal = new FlexboxLayoutManager(getActivity());
        layoutManagerAnimal.setFlexDirection(FlexDirection.ROW);
        layoutManagerAnimal.setJustifyContent(JustifyContent.FLEX_START);
        planConfirmBinding.frpdetailRvanimal.setLayoutManager(layoutManagerAnimal);
        planConfirmBinding.frpdetailRvanimal.setAdapter(planVisitAnimalAdapter);
        if (recommendedPlanModel != null) {
            final ArrayList<PlanItem> planItemAnimalArrayList = new ArrayList<>();
            for (VisitAnimalModel visitAnimalModel : recommendedPlanModel.getAnimalArrayList()) {
                final PlanItem planItem = new PlanItem();
                planItem.setId(visitAnimalModel.getId());
                planItem.setTitle(visitAnimalModel.getName());
                planItem.setLatitude(visitAnimalModel.getLatitude());
                planItem.setLongitude(visitAnimalModel.getLongitude());
                planItem.setImage(visitAnimalModel.getImage());
                planItemAnimalArrayList.add(planItem);

            }
            planVisitAnimalAdapter.addAllModel(planItemAnimalArrayList);
        }
        if (planVisitAnimalAdapter.getAllItems().size() == 0) {
            planConfirmBinding.frplandetailLlanimal.setVisibility(View.GONE);
        } else {
            planConfirmBinding.frplandetailLlanimal.setVisibility(View.VISIBLE);
        }

        planVisitWhatsNewAdapter = new PlanVisitAdapter<>(getActivity(), getString(R.string.str_plane_nowhatsnew));
        final FlexboxLayoutManager layoutManagerWhatsnew = new FlexboxLayoutManager(getActivity());
        layoutManagerWhatsnew.setFlexDirection(FlexDirection.ROW);
        layoutManagerWhatsnew.setJustifyContent(JustifyContent.FLEX_START);
        planConfirmBinding.frpdetailRvwhatsnew.setLayoutManager(layoutManagerWhatsnew);
        planConfirmBinding.frpdetailRvwhatsnew.setAdapter(planVisitWhatsNewAdapter);
        if (recommendedPlanModel != null) {
            final ArrayList<PlanItem> planItemWhatsnewArrayList = new ArrayList<>();
            for (VisitWhatsNewModel visitWhatsNewModel : recommendedPlanModel.getVisitWhatsNewModelArrayList()) {
                final PlanItem planItem = new PlanItem();
                planItem.setId(visitWhatsNewModel.getId());
                planItem.setTitle(visitWhatsNewModel.getName());
                planItem.setLatitude(visitWhatsNewModel.getLatitude());
                planItem.setLongitude(visitWhatsNewModel.getLongitude());
                planItem.setImage(visitWhatsNewModel.getImage());
                planItemWhatsnewArrayList.add(planItem);


            }
            planVisitWhatsNewAdapter.addAllModel(planItemWhatsnewArrayList);
        }

        if (planVisitWhatsNewAdapter.getAllItems().size() == 0) {
            planConfirmBinding.frplandetailLlwhatsnew.setVisibility(View.GONE);
        } else {
            planConfirmBinding.frplandetailLlwhatsnew.setVisibility(View.VISIBLE);
        }

        final FlexboxLayoutManager layoutManagerExperiences = new FlexboxLayoutManager(getActivity());
        layoutManagerExperiences.setFlexDirection(FlexDirection.ROW);
        layoutManagerExperiences.setJustifyContent(JustifyContent.FLEX_START);

        planVisitExperiencesAdapter = new PlanVisitAdapter<>(getActivity(), getString(R.string.str_plane_noexperiance));
        planConfirmBinding.frpdetailRvexperience.setLayoutManager(layoutManagerExperiences);
        planConfirmBinding.frpdetailRvexperience.setAdapter(planVisitExperiencesAdapter);
        if (recommendedPlanModel != null) {
            final ArrayList<PlanItem> planItemExperiancesArrayList = new ArrayList<>();
            for (VisitExperiencesModel visitExperiencesModel : recommendedPlanModel.getExperienceArrayList()) {
                final PlanItem planItem = new PlanItem();
                planItem.setId(visitExperiencesModel.getId());
                planItem.setTitle(visitExperiencesModel.getName());
                planItem.setLatitude(visitExperiencesModel.getLatitude());
                planItem.setLongitude(visitExperiencesModel.getLongitude());
                planItem.setImage(visitExperiencesModel.getImage());
                planItemExperiancesArrayList.add(planItem);

            }
            planVisitExperiencesAdapter.addAllModel(planItemExperiancesArrayList);
        }

        if (planVisitExperiencesAdapter.getAllItems().size() == 0) {
            planConfirmBinding.frplandetailLlExperience.setVisibility(View.GONE);
        } else {
            planConfirmBinding.frplandetailLlExperience.setVisibility(View.VISIBLE);
        }

        final FlexboxLayoutManager layoutManagerAttraction = new FlexboxLayoutManager(getActivity());
        layoutManagerAttraction.setFlexDirection(FlexDirection.ROW);
        layoutManagerAttraction.setJustifyContent(JustifyContent.FLEX_START);

        planVisitAttractionAdapter = new PlanVisitAdapter<>(getActivity(), getString(R.string.str_plane_noattractions));
        planConfirmBinding.frpdetailRvattractions.setLayoutManager(layoutManagerAttraction);
        planConfirmBinding.frpdetailRvattractions.setAdapter(planVisitAttractionAdapter);
        if (recommendedPlanModel != null) {

            final ArrayList<PlanItem> planItemAttractionArrayList = new ArrayList<>();
            for (VisitAttractionsModel visitAttractionsModel : recommendedPlanModel.getAttractionArrayList()) {
                final PlanItem planItem = new PlanItem();
                planItem.setId(visitAttractionsModel.getId());
                planItem.setTitle(visitAttractionsModel.getName());
                planItem.setImage(visitAttractionsModel.getImage());
                planItemAttractionArrayList.add(planItem);

            }

            planVisitAttractionAdapter.addAllModel(planItemAttractionArrayList);
        }

        if (planVisitAttractionAdapter.getAllItems().size() == 0) {
            planConfirmBinding.frplandetailLlattractions.setVisibility(View.GONE);
        } else {
            planConfirmBinding.frplandetailLlattractions.setVisibility(View.VISIBLE);
        }

        planConfirmBinding.frpdetailIvanimal.setImageResource(planVisitAnimalAdapter.getAllItems().size() == 0 ? R.drawable.ic_add_animals_empty : R.drawable.ic_add_animals_fill);
        planConfirmBinding.frpdetailIvwhatsnew.setImageResource(planVisitWhatsNewAdapter.getAllItems().size() == 0 ? R.drawable.ic_add_new_empty : R.drawable.ic_add_new_fill);
        planConfirmBinding.frpdetailIvexperiances.setImageResource(planVisitExperiencesAdapter.getAllItems().size() == 0 ? R.drawable.ic_add_experience_empty : R.drawable.ic_add_experience_fill);
        planConfirmBinding.frpdetailIvattractions.setImageResource(planVisitAttractionAdapter.getAllItems().size() == 0 ? R.drawable.ic_add_attractions_empty : R.drawable.ic_add_attractions_fill);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == planConfirmBinding.frpdetailBtnSthowOnMap) {
            ((HomeActivity) getActivity()).addFragment(PlaneConfirmFragment.this, ExploreZooPlaneFragment.getZooPlaneFragment(recommendedPlanModel, isMyPlan));
        } else if (v == planConfirmBinding.frpdetailBtnRemovePlan) {
            alertConfirmDeletePlan();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (isAdded()) {
            if (!hidden) {
                ((HomeActivity) getActivity()).selectedBottomMenuPosition = 2;
                ((HomeActivity) getActivity()).disableCollapse();
                ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_my_plan), getResources().getColor(R.color.cool_blue), null, false);
            }
        }
    }

    public void alertConfirmDeletePlan() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView tvPositiveButton = dialogView.findViewById(R.id.dialog_login_tvlogin);
        final TextView tvNegativeButton = dialogView.findViewById(R.id.dialog_login_tvcancel);
        final TextView tvTitle = dialogView.findViewById(R.id.dialog_login_tvTitle);
        final TextView tvMessage = dialogView.findViewById(R.id.dialog_login_tvMessage);
        tvTitle.setVisibility(View.GONE);
        tvMessage.setText(getString(R.string.alert_plane_confirm_delete));
        tvPositiveButton.setText(getString(R.string.yes));
        tvNegativeButton.setText(getString(R.string.no));
        tvPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                deletePlan();
                //getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        tvNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
        final int width = (int) getResources().getDimension(R.dimen._260sdp);
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void deletePlan() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        PlansVisitManager.getPlansVisitManager().deleteMyPlan(new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                DisplayDialog.getInstance().dismissProgressDialog();
                AlainZooDB.getInstance().myPlaneVisitedItemDao().deleteAll();
                AlainZooDB.getInstance().myPlanDao().deleteAllMyplan();
                ((HomeActivity) getActivity()).replaceFragment(PlanVisitFragment.getPlanVisitFragment(true));
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
            }
        });
    }

}
