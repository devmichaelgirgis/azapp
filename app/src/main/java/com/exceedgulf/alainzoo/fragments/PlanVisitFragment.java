package com.exceedgulf.alainzoo.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.RecommendedPlansAdapter;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.databinding.FragmentPlanVisitBinding;
import com.exceedgulf.alainzoo.managers.PlansVisitManager;
import com.exceedgulf.alainzoo.models.GeneralPlan;
import com.exceedgulf.alainzoo.models.RecommendedPlanModel;
import com.exceedgulf.alainzoo.models.TokenModel;
import com.exceedgulf.alainzoo.models.VisitAnimalModel;
import com.exceedgulf.alainzoo.models.VisitOrder;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.NetUtil;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlanVisitFragment extends BaseFragment {
    private static final String KEY_CONFIRM_PLANE = "CONFIRM_PLANE";
    private static final String KEY_MY_PLAN = "MY_PLAN";
    private static final String KEY_DELETE = "DELETE";
    private FragmentPlanVisitBinding planVisitBinding;
    private RecommendedPlansAdapter recommendedPlansAdapter;

    public PlanVisitFragment() {
        // Required empty public constructor
    }

    public static PlanVisitFragment getPlanVisitFragment(final boolean isDeleted) {
        final Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_DELETE, isDeleted);
        final PlanVisitFragment fragment = new PlanVisitFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        planVisitBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_plan_visit, container, false);
        return planVisitBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_plan_visit), getResources().getColor(R.color.cool_blue), null, true);

        recommendedPlansAdapter = new RecommendedPlansAdapter(getActivity());
        planVisitBinding.frPlanVisitRvPlans.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        planVisitBinding.frPlanVisitRvPlans.setEmptyView(planVisitBinding.frPlanVisitTvEmptyView);
        planVisitBinding.frPlanVisitRvPlans.setAdapter(recommendedPlansAdapter);
        planVisitBinding.frPlanVisitLlAddPlan.setOnClickListener(this);
        planVisitBinding.frMyPlanVisitLlMyPlan.setOnClickListener(this);
        final Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(KEY_DELETE)) {
            if (bundle.getBoolean(KEY_DELETE)) {
                getRecommendedPlanList();
            }
        } else {
            if ((SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn())) {
                final ArrayList<RecommendedPlanModel> recommendedPlanModelsList = (ArrayList<RecommendedPlanModel>) AlainZooDB.getInstance().myPlanDao().getAllMyplan();
                if (recommendedPlanModelsList.size() == 0) {
                    refreshToken();
                } else {
                    final RecommendedPlanModel recommendedPlanModel = recommendedPlanModelsList.get(0);
                    final Bundle bundleTmp = new Bundle();
                    bundleTmp.putBoolean(KEY_MY_PLAN, true);
                    bundleTmp.putSerializable(KEY_CONFIRM_PLANE, recommendedPlanModel);
                    ((HomeActivity) getActivity()).replaceFragment(PlaneConfirmFragment.getPlanVisitConfirmFragment(recommendedPlanModel, true));
                }
            } else {
                getRecommendedPlanList();
            }
        }
    }

    private void refreshToken() {
        if (NetUtil.isNetworkAvailable(getActivity())) {
            DisplayDialog.getInstance().showProgressDialog(getActivity(), "", false);
            final DateFormat df = new SimpleDateFormat(Constants.TIME_STAMP_FORMAT, Locale.ENGLISH);
            final String strRefreshToken = SharedPrefceHelper.getInstance().get(PrefCons.REFRESH_TOKEN, "");
            if (!TextUtils.isEmpty(strRefreshToken.trim())) {
                Log.e("API Call", "Refresh Token");
                ApiControllers.getApiControllers().postRefreshToken(strRefreshToken, new Callback<TokenModel>() {
                    @Override
                    public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                        if (response.code() == 200) {
                            final TokenModel tokenModel = response.body();
                            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN, tokenModel.getAccessToken());
                            SharedPrefceHelper.getInstance().save(PrefCons.REFRESH_TOKEN, tokenModel.getRefreshToken());
                            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_EXPIRES, tokenModel.getExpiresIn());
                            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_CREATED, df.format(new Date()));
                            Log.e("Refresh Token ", tokenModel.getAccessToken());
                            getMyPlanList();
                        } else {
                            DisplayDialog.getInstance().dismissProgressDialog();
                            SnackbarUtils.loadSnackBar(getString(R.string.error), getActivity(), R.color.cool_blue);
                            planVisitBinding.frPlanVisitTvEmptyView.setText(recommendedPlansAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenModel> call, Throwable t) {
                        DisplayDialog.getInstance().dismissProgressDialog();
                        if (isAdded()) {
                            SnackbarUtils.loadSnackBar(getString(R.string.error), getActivity(), R.color.cool_blue);
                            planVisitBinding.frPlanVisitTvEmptyView.setText(recommendedPlansAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                        }
                    }
                });
            } else {
                SnackbarUtils.loadSnackBar(getString(R.string.error), getActivity(), R.color.cool_blue);
                planVisitBinding.frPlanVisitTvEmptyView.setText(recommendedPlansAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
            }
        } else {
            SnackbarUtils.loadSnackBar(getString(R.string.no_internet_), getActivity(), R.color.cool_blue);
            planVisitBinding.frPlanVisitTvEmptyView.setText(recommendedPlansAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
        }
    }

    private void getMyPlanList() {
        //DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        PlansVisitManager.getPlansVisitManager().getMyPlanList(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    final ArrayList<RecommendedPlanModel> recommendedPlanModels = resultList;
                    if (recommendedPlanModels.size() > 0) {
                        final RecommendedPlanModel recommendedPlanModel = recommendedPlanModels.get(0);
                        if (recommendedPlanModel.getVisitOrderArrayList().size() == 0) {
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
                        }

                        AlainZooDB.getInstance().myPlanDao().deleteAllMyplan();
                        AlainZooDB.getInstance().myPlanDao().insertOrReplaceAll(recommendedPlanModels);

                        final Bundle bundle = new Bundle();
                        bundle.putBoolean(KEY_MY_PLAN, true);
                        bundle.putSerializable(KEY_CONFIRM_PLANE, recommendedPlanModel);
                        ((HomeActivity) getActivity()).replaceFragment(PlaneConfirmFragment.getPlanVisitConfirmFragment(recommendedPlanModel, true));
                    } else {
                        getRecommendedPlanList();
                    }
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
                planVisitBinding.frPlanVisitTvEmptyView.setText(recommendedPlansAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
            }
        });
    }

    private void getRecommendedPlanList() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        PlansVisitManager.getPlansVisitManager().getRecommendedPlanList(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    for (int i = 0; i < resultList.size(); i++) {
                        final RecommendedPlanModel planModel = (RecommendedPlanModel) resultList.get(i);
                        if (planModel.getName().trim().equalsIgnoreCase("General Plan")) {
                            final GeneralPlan generalPlan = new GeneralPlan();
                            generalPlan.setId(planModel.getId());
                            generalPlan.setName(planModel.getName());
                            generalPlan.setDetails(planModel.getDetails());
                            generalPlan.setDuration(planModel.getDuration());
                            generalPlan.setImage(planModel.getImage());
                            generalPlan.setLangcode(planModel.getLangcode());
                            generalPlan.setPlaneDate(planModel.getPlaneDate());
                            generalPlan.setAnimalArrayList(planModel.getAnimalArrayList());
                            generalPlan.setAttractionArrayList(planModel.getAttractionArrayList());
                            generalPlan.setExperienceArrayList(planModel.getExperienceArrayList());
                            generalPlan.setTicketArrayList(planModel.getTicketArrayList());
                            generalPlan.setTicketArrayListAr(planModel.getTicketArrayListAr());
                            generalPlan.setVisitOrderArrayList(planModel.getVisitOrderArrayList());
                            generalPlan.setVisitWhatsNewModelArrayList(planModel.getVisitWhatsNewModelArrayList());
                            PlansVisitManager.getPlansVisitManager().insertGeneralPlan(generalPlan);
                            resultList.remove(i);
                            break;
                        }
                    }
                    recommendedPlansAdapter.addItems(resultList);
                    planVisitBinding.frPlanVisitLlAddPlan.setVisibility(View.VISIBLE);
                    planVisitBinding.frMyPlanVisitLlMyPlan.setVisibility((SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn()) ? View.VISIBLE : View.GONE);
                    planVisitBinding.frPlanVisitTvEmptyView.setText(recommendedPlansAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
                if (isAdded()) {
                    planVisitBinding.frPlanVisitLlAddPlan.setVisibility(View.VISIBLE);
                    planVisitBinding.frMyPlanVisitLlMyPlan.setVisibility((SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn()) ? View.VISIBLE : View.GONE);
                    planVisitBinding.frPlanVisitTvEmptyView.setText(recommendedPlansAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).selectedBottomMenuPosition = 2;
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_plan_visit), getResources().getColor(R.color.cool_blue), null, true);
            planVisitBinding.frMyPlanVisitLlMyPlan.setVisibility((SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn()) ? View.VISIBLE : View.GONE);

        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == planVisitBinding.frPlanVisitLlAddPlan) {
            ((HomeActivity) getActivity()).addFragment(this, new PlanVisitDetailFragment());
        } else if (v == planVisitBinding.frMyPlanVisitLlMyPlan) {
            ((HomeActivity) getActivity()).addFragment(this, new PlaneConfirmFragment());
        }
    }
}
