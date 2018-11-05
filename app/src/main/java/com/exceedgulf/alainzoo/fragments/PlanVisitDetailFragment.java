package com.exceedgulf.alainzoo.fragments;


import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.PlanVisitAdapter;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.database.models.Attraction;
import com.exceedgulf.alainzoo.database.models.Experience;
import com.exceedgulf.alainzoo.database.models.WhatsNew;
import com.exceedgulf.alainzoo.databinding.FragmentPlanVisitDetailBinding;
import com.exceedgulf.alainzoo.managers.CreatePlanVisitManager;
import com.exceedgulf.alainzoo.managers.GamificationManager;
import com.exceedgulf.alainzoo.managers.PlansVisitManager;
import com.exceedgulf.alainzoo.models.PlanItem;
import com.exceedgulf.alainzoo.models.RecommendedPlanModel;
import com.exceedgulf.alainzoo.models.TokenModel;
import com.exceedgulf.alainzoo.models.VisitAnimalModel;
import com.exceedgulf.alainzoo.models.VisitAttractionsModel;
import com.exceedgulf.alainzoo.models.VisitExperiencesModel;
import com.exceedgulf.alainzoo.models.VisitWhatsNewModel;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.NetUtil;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.utils.Utils;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlanVisitDetailFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener {
    public static final String KEY_RECOMMENDED_PLAN = "RECOMMENDED_PLAN";


    private RecommendedPlanModel recommendedPlanModel = null;
    private FragmentPlanVisitDetailBinding planBinding;
    private String selectedDate = "";
    private PlanVisitAdapter<PlanItem> planVisitAnimalAdapter;
    private PlanVisitAdapter<PlanItem> planVisitWhatsNewAdapter;
    private PlanVisitAdapter<PlanItem> planVisitExperiencesAdapter;
    private PlanVisitAdapter<PlanItem> planVisitAttractionAdapter;
    private boolean isFromRecomendedPlan = false;

    public static PlanVisitDetailFragment getPlanYourVisitFragment(final RecommendedPlanModel recommendedPlanModel) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_RECOMMENDED_PLAN, recommendedPlanModel);
        final PlanVisitDetailFragment fragment = new PlanVisitDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        planBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_plan_visit_detail, container, false);
        return planBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_plan_visit), getResources().getColor(R.color.cool_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        final Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(KEY_RECOMMENDED_PLAN)) {
            recommendedPlanModel = (RecommendedPlanModel) bundle.getSerializable(KEY_RECOMMENDED_PLAN);
            isFromRecomendedPlan = true;
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(recommendedPlanModel.getName()).toString(), getResources().getColor(R.color.cool_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        }
        planBinding.frplandetailTvdate.setOnClickListener(this);
        planBinding.frpdetailBtnconfirm.setOnClickListener(this);
        if (!isFromRecomendedPlan) {
            planBinding.frpdetailIvanimal.setOnClickListener(this);
            planBinding.frpdetailIvwhatsnew.setOnClickListener(this);
            planBinding.frpdetailIvexperiances.setOnClickListener(this);
            planBinding.frpdetailIvattractions.setOnClickListener(this);
            planBinding.frplandetailEdtplanename.setEnabled(true);
            planBinding.frplandetailEdtplanename.setFocusable(true);
            planBinding.frplandetailEdtplanename.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        Utils.getInstance().hideSoftKeyboard(getActivity());
                    }
                    return false;
                }
            });
        } else {
            planBinding.frplandetailEdtplanename.setText(recommendedPlanModel.getName());
            planBinding.frplandetailEdtplanename.setEnabled(false);
            planBinding.frplandetailEdtplanename.setFocusable(false);
            planBinding.frplandetailTvTitlePlanName.setTag(null);
            setMandatoryStar();
        }

        planBinding.frplandetailTvTitleSelectDate.setText((isFromRecomendedPlan) ? getString(R.string.select_date) : getString(R.string.step_select_date));
        planBinding.frplandetailTvTitlePlanName.setText((isFromRecomendedPlan) ? getString(R.string.plan_name) : getString(R.string.step_plan_name));
        if (isFromRecomendedPlan)
            planBinding.frpdetailTvStep3.setVisibility(View.GONE);
        else
            planBinding.frpdetailTvStep3.setVisibility(View.VISIBLE);

        setPlanSelectedData();
    }

    private void setPlanSelectedData() {
        planVisitAnimalAdapter = new PlanVisitAdapter<>(getActivity(), getString(R.string.str_plane_noanimal));
        final FlexboxLayoutManager layoutManagerAnimal = new FlexboxLayoutManager(getActivity());
        layoutManagerAnimal.setFlexDirection(FlexDirection.ROW);
        layoutManagerAnimal.setJustifyContent(JustifyContent.FLEX_START);
        planBinding.frpdetailRvanimal.setLayoutManager(layoutManagerAnimal);
        planBinding.frpdetailRvanimal.setAdapter(planVisitAnimalAdapter);
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

        if (isFromRecomendedPlan) {
            if (planVisitAnimalAdapter.getAllItems().size() == 0) {
                planBinding.frplandetailLlanimal.setVisibility(View.GONE);
            } else {
                planBinding.frplandetailLlanimal.setVisibility(View.VISIBLE);
            }
        }

        planVisitWhatsNewAdapter = new PlanVisitAdapter<>(getActivity(), getString(R.string.str_plane_nowhatsnew));
        final FlexboxLayoutManager layoutManagerWhatsnew = new FlexboxLayoutManager(getActivity());
        layoutManagerWhatsnew.setFlexDirection(FlexDirection.ROW);
        layoutManagerWhatsnew.setJustifyContent(JustifyContent.FLEX_START);
        planBinding.frpdetailRvwhatsnew.setLayoutManager(layoutManagerWhatsnew);
        planBinding.frpdetailRvwhatsnew.setAdapter(planVisitWhatsNewAdapter);
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

        if (isFromRecomendedPlan) {
            if (planVisitWhatsNewAdapter.getAllItems().size() == 0) {
                planBinding.frplandetailLlwhatsnew.setVisibility(View.GONE);
            } else {
                planBinding.frplandetailLlwhatsnew.setVisibility(View.VISIBLE);
            }
        }

        final FlexboxLayoutManager layoutManagerExperiences = new FlexboxLayoutManager(getActivity());
        layoutManagerExperiences.setFlexDirection(FlexDirection.ROW);
        layoutManagerExperiences.setJustifyContent(JustifyContent.FLEX_START);

        planVisitExperiencesAdapter = new PlanVisitAdapter<>(getActivity(), getString(R.string.str_plane_noexperiance));
        planBinding.frpdetailRvexperience.setLayoutManager(layoutManagerExperiences);
        planBinding.frpdetailRvexperience.setAdapter(planVisitExperiencesAdapter);
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

        if (isFromRecomendedPlan) {
            if (planVisitExperiencesAdapter.getAllItems().size() == 0) {
                planBinding.frplandetailLlexperianse.setVisibility(View.GONE);
            } else {
                planBinding.frplandetailLlexperianse.setVisibility(View.VISIBLE);
            }
        }

        final FlexboxLayoutManager layoutManagerAttraction = new FlexboxLayoutManager(getActivity());
        layoutManagerAttraction.setFlexDirection(FlexDirection.ROW);
        layoutManagerAttraction.setJustifyContent(JustifyContent.FLEX_START);

        planVisitAttractionAdapter = new PlanVisitAdapter<>(getActivity(), getString(R.string.str_plane_noattractions));
        planBinding.frpdetailRvattractions.setLayoutManager(layoutManagerAttraction);
        planBinding.frpdetailRvattractions.setAdapter(planVisitAttractionAdapter);
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

        if (isFromRecomendedPlan) {
            if (planVisitAttractionAdapter.getAllItems().size() == 0) {
                planBinding.frplandetailLlattractions.setVisibility(View.GONE);
            } else {
                planBinding.frplandetailLlattractions.setVisibility(View.VISIBLE);
            }
        }

        planBinding.frpdetailIvanimal.setImageResource(planVisitAnimalAdapter.getAllItems().size() == 0 ? R.drawable.ic_add_animals_empty : R.drawable.ic_add_animals_fill);
        planBinding.frpdetailIvwhatsnew.setImageResource(planVisitWhatsNewAdapter.getAllItems().size() == 0 ? R.drawable.ic_add_new_empty : R.drawable.ic_add_new_fill);
        planBinding.frpdetailIvexperiances.setImageResource(planVisitExperiencesAdapter.getAllItems().size() == 0 ? R.drawable.ic_add_experience_empty : R.drawable.ic_add_experience_fill);
        planBinding.frpdetailIvattractions.setImageResource(planVisitAttractionAdapter.getAllItems().size() == 0 ? R.drawable.ic_add_attractions_empty : R.drawable.ic_add_attractions_fill);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            if (isFromRecomendedPlan) {
                ((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(recommendedPlanModel.getName()).toString(), getResources().getColor(R.color.cool_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
            } else {
                ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_plan_visit), getResources().getColor(R.color.cool_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (!isOnClick()) {
            return;
        }
        if (view == planBinding.frplandetailTvdate) {

            final Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setLocale(Locale.ENGLISH);
            datePickerDialog.setAccentColor(ContextCompat.getColor(getActivity(), R.color.cool_blue));
            datePickerDialog.setMinDate(calendar);
            datePickerDialog.show(getActivity().getFragmentManager(), "Date");

        } else if (view == planBinding.frpdetailBtnconfirm) {
            if (validateSelectedDate()) {
                if (planVisitAnimalAdapter.getAllItems().size() == 0 && planVisitWhatsNewAdapter.getAllItems().size() == 0 && planVisitAttractionAdapter.getAllItems().size() == 0 && planVisitExperiencesAdapter.getAllItems().size() == 0) {
                    SnackbarUtils.loadSnackBar(getString(R.string.plane_item_select_error), getActivity(), R.color.cool_blue);
                } else {
                    if (SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn()) {
                        confirmPlan();
                    } else {
                        ((HomeActivity) getActivity()).loginDialog(getString(R.string.login_to_apply_plan), false);
                    }
                }
            }
        } else if (view == planBinding.frpdetailIvanimal) {
            if (validateSelectedDate()) {
                ((HomeActivity) getActivity()).getPlaneSelectedItems().clear();
                for (PlanItem planItem : planVisitAnimalAdapter.getAllItems()) {
                    final Animal animal = new Animal();
                    animal.setId(planItem.getId());
                    animal.setThumbnail(planItem.getImage());
                    animal.setName(planItem.getTitle());
                    animal.setLatitude(planItem.getLatitude());
                    animal.setLongitude(planItem.getLongitude());
                    ((HomeActivity) getActivity()).getPlaneSelectedItems().put(String.valueOf(planItem.getId()), animal);
                }
                openSelectionFragment(getString(R.string.mAnimals));
            }
        } else if (view == planBinding.frpdetailIvwhatsnew) {
            if (validateSelectedDate()) {
                ((HomeActivity) getActivity()).getPlaneSelectedItems().clear();
                for (PlanItem planItem : planVisitWhatsNewAdapter.getAllItems()) {
                    final WhatsNew whatsNew = new WhatsNew();
                    whatsNew.setId(planItem.getId());
                    whatsNew.setImage(planItem.getImage());
                    whatsNew.setName(planItem.getTitle());
                    whatsNew.setLatitude(planItem.getLatitude());
                    whatsNew.setLongitude(planItem.getLongitude());

                    ((HomeActivity) getActivity()).getPlaneSelectedItems().put(String.valueOf(planItem.getId()), whatsNew);
                }
                openSelectionFragment(getString(R.string.title_whats_new));
            }
        } else if (view == planBinding.frpdetailIvexperiances) {
            if (validateSelectedDate()) {
                ((HomeActivity) getActivity()).getPlaneSelectedItems().clear();
                for (PlanItem planItem : planVisitExperiencesAdapter.getAllItems()) {

                    final Experience experience = new Experience();
                    experience.setId(planItem.getId());
                    experience.setImage(planItem.getImage());
                    experience.setName(planItem.getTitle());
                    experience.setLatitude(planItem.getLatitude());
                    experience.setLongitude(planItem.getLongitude());

                    ((HomeActivity) getActivity()).getPlaneSelectedItems().put(String.valueOf(planItem.getId()), experience);
                }
                openSelectionFragment(getString(R.string.mExperiences));
            }
        } else if (view == planBinding.frpdetailIvattractions) {
            if (validateSelectedDate()) {
                ((HomeActivity) getActivity()).getPlaneSelectedItems().clear();
                for (PlanItem planItem : planVisitAttractionAdapter.getAllItems()) {

                    final Attraction attraction = new Attraction();
                    attraction.setId(planItem.getId());
                    attraction.setImage(planItem.getImage());
                    attraction.setName(planItem.getTitle());
                    // attraction.setLatitude(planItem.getLatitude());
                    //attraction.setLongitude(planItem.getLongitude());

                    ((HomeActivity) getActivity()).getPlaneSelectedItems().put(String.valueOf(planItem.getId()), attraction);
                }
                openSelectionFragment(getString(R.string.attractions));
            }
        }
    }

    private void openSelectionFragment(final String type) {
        final Bundle bundle = new Bundle();
        bundle.putString(PlaneVisitTypeSelectFragment.KEY_SELECTION_TYPE, type);
        final PlaneVisitTypeSelectFragment planeVisitTypeSelectFragment = new PlaneVisitTypeSelectFragment();
        planeVisitTypeSelectFragment.setArguments(bundle);
        planeVisitTypeSelectFragment.setTargetFragment(this, 0);
        ((HomeActivity) getActivity()).addFragment(this, planeVisitTypeSelectFragment);
    }

    private boolean validateSelectedDate() {
        if (TextUtils.isEmpty(selectedDate)) {
            SnackbarUtils.loadSnackBar(getString(R.string.pls_select_date), getActivity(), R.color.cool_blue);
            return false;
        } else if (TextUtils.isEmpty(planBinding.frplandetailEdtplanename.getText().toString().trim())) {
            SnackbarUtils.loadSnackBar(getString(R.string.please_enter_planname), getActivity(), R.color.cool_blue);
            return false;
        }
        return true;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        selectedDate = String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
        planBinding.frplandetailTvdate.setText(String.format(Locale.ENGLISH, "%02d/%02d/%04d", dayOfMonth, (monthOfYear + 1), year));
    }


    public void setSelectedItems(String selectType) {
        if (selectType.equalsIgnoreCase(getString(R.string.mAnimals))) {
            planVisitAnimalAdapter.clearItems();
            if (((HomeActivity) getActivity()).getPlaneSelectedItems().size() > 0) {
                planVisitAnimalAdapter.addAllModel(getSelectedPlanItems(selectType));
            }
        } else if (selectType.equalsIgnoreCase(getString(R.string.mExperiences))) {
            planVisitExperiencesAdapter.clearItems();
            if (((HomeActivity) getActivity()).getPlaneSelectedItems().size() > 0) {
                planVisitExperiencesAdapter.addAllModel(getSelectedPlanItems(selectType));
            }
        } else if (selectType.equalsIgnoreCase(getString(R.string.attractions))) {
            planVisitAttractionAdapter.clearItems();

            if (((HomeActivity) getActivity()).getPlaneSelectedItems().size() > 0) {
                planVisitAttractionAdapter.addAllModel(getSelectedPlanItems(selectType));
            }
        } else if (selectType.equalsIgnoreCase(getString(R.string.title_whats_new))) {
            planVisitWhatsNewAdapter.clearItems();

            if (((HomeActivity) getActivity()).getPlaneSelectedItems().size() > 0) {
                planVisitWhatsNewAdapter.addAllModel(getSelectedPlanItems(selectType));
            }
        }


        planVisitAnimalAdapter.notifyDataSetChanged();
        planVisitExperiencesAdapter.notifyDataSetChanged();
        planVisitAttractionAdapter.notifyDataSetChanged();
        planVisitWhatsNewAdapter.notifyDataSetChanged();

        planBinding.frpdetailIvanimal.setImageResource(planVisitAnimalAdapter.getAllItems().size() == 0 ? R.drawable.ic_add_animals_empty : R.drawable.ic_add_animals_fill);
        planBinding.frpdetailIvwhatsnew.setImageResource(planVisitWhatsNewAdapter.getAllItems().size() == 0 ? R.drawable.ic_add_new_empty : R.drawable.ic_add_new_fill);
        planBinding.frpdetailIvexperiances.setImageResource(planVisitExperiencesAdapter.getAllItems().size() == 0 ? R.drawable.ic_add_experience_empty : R.drawable.ic_add_experience_fill);
        planBinding.frpdetailIvattractions.setImageResource(planVisitAttractionAdapter.getAllItems().size() == 0 ? R.drawable.ic_add_attractions_empty : R.drawable.ic_add_attractions_fill);

    }

    private ArrayList<PlanItem> getSelectedPlanItems(final String selectType) {
        final ArrayList<PlanItem> planItemArrayList = new ArrayList<>();
        for (String key : ((HomeActivity) getActivity()).getPlaneSelectedItems().keySet()) {
            final PlanItem planItem = new PlanItem();
            if (selectType.equalsIgnoreCase(getString(R.string.mAnimals))) {
                final Animal animal = (Animal) ((HomeActivity) getActivity()).getPlaneSelectedItems().get(key);
                planItem.setId(Integer.parseInt(key));
                planItem.setTitle(animal.getName());
                planItem.setLatitude(animal.getLatitude());
                planItem.setLongitude(animal.getLongitude());
                planItem.setImage(animal.getThumbnail());
            } else if (selectType.equalsIgnoreCase(getString(R.string.mExperiences))) {
                final Experience experience = (Experience) ((HomeActivity) getActivity()).getPlaneSelectedItems().get(key);
                planItem.setId(Integer.parseInt(key));
                planItem.setTitle(experience.getName());
                planItem.setLatitude(experience.getLatitude());
                planItem.setLongitude(experience.getLongitude());
                planItem.setImage(experience.getImage());
            } else if (selectType.equalsIgnoreCase(getString(R.string.attractions))) {
                final Attraction attraction = (Attraction) ((HomeActivity) getActivity()).getPlaneSelectedItems().get(key);
                planItem.setId(Integer.parseInt(key));
                planItem.setTitle(attraction.getName());
                //planItem.setLatitude(experience.getLatitude());
                //planItem.setLongitude(experience.getLongitude());
                planItem.setImage(attraction.getImage());
            } else if (selectType.equalsIgnoreCase(getString(R.string.title_whats_new))) {
                final WhatsNew whatsNew = (WhatsNew) ((HomeActivity) getActivity()).getPlaneSelectedItems().get(key);
                planItem.setId(Integer.parseInt(key));
                planItem.setTitle(whatsNew.getName());
                planItem.setLatitude(whatsNew.getLatitude());
                planItem.setLongitude(whatsNew.getLongitude());
                planItem.setImage(whatsNew.getImage());
            }
            planItemArrayList.add(planItem);
        }
        return planItemArrayList;
    }

    private void confirmPlan() {

        if (recommendedPlanModel == null || !isFromRecomendedPlan) {
            recommendedPlanModel = new RecommendedPlanModel();
            for (PlanItem planItem : planVisitAnimalAdapter.getAllItems()) {
                final VisitAnimalModel visitAnimalModel = new VisitAnimalModel();
                visitAnimalModel.setId(planItem.getId());
                visitAnimalModel.setName(planItem.getTitle());
                visitAnimalModel.setLatitude(planItem.getLatitude());
                visitAnimalModel.setLongitude(planItem.getLongitude());
                visitAnimalModel.setThumbnail(planItem.getImage());
                recommendedPlanModel.getAnimalArrayList().add(visitAnimalModel);
            }
            for (PlanItem planItem : planVisitExperiencesAdapter.getAllItems()) {
                final VisitExperiencesModel visitExperiencesModel = new VisitExperiencesModel();
                visitExperiencesModel.setId(planItem.getId());
                visitExperiencesModel.setName(planItem.getTitle());
                visitExperiencesModel.setLatitude(planItem.getLatitude());
                visitExperiencesModel.setLongitude(planItem.getLongitude());
                visitExperiencesModel.setImage(planItem.getImage());
                recommendedPlanModel.getExperienceArrayList().add(visitExperiencesModel);
            }
            for (PlanItem planItem : planVisitAttractionAdapter.getAllItems()) {
                final VisitAttractionsModel visitAttractionsModel = new VisitAttractionsModel();
                visitAttractionsModel.setId(planItem.getId());
                visitAttractionsModel.setName(planItem.getTitle());
                //visitExperiencesModel.setLatitude(planItem.getLatitude());
                //visitExperiencesModel.setLongitude(planItem.getLongitude());
                visitAttractionsModel.setImage(planItem.getImage());
                recommendedPlanModel.getAttractionArrayList().add(visitAttractionsModel);
            }
            for (PlanItem planItem : planVisitWhatsNewAdapter.getAllItems()) {
                final VisitWhatsNewModel visitWhatsNewModel = new VisitWhatsNewModel();
                visitWhatsNewModel.setId(planItem.getId());
                visitWhatsNewModel.setName(planItem.getTitle());
                visitWhatsNewModel.setLatitude(planItem.getLatitude());
                visitWhatsNewModel.setLongitude(planItem.getLongitude());
                visitWhatsNewModel.setImage(planItem.getImage());
                recommendedPlanModel.getVisitWhatsNewModelArrayList().add(visitWhatsNewModel);
            }
            recommendedPlanModel.setPlaneDate(planBinding.frplandetailTvdate.getText().toString());
            recommendedPlanModel.setName(planBinding.frplandetailEdtplanename.getText().toString());
            prepareRecommendedPlan(recommendedPlanModel);
            recommendedPlanModel.setCustomPlan(true);
        } else {
            recommendedPlanModel.setPlaneDate(planBinding.frplandetailTvdate.getText().toString());
            recommendedPlanModel.setCustomPlan(false);
            prepareRecommendedPlan(recommendedPlanModel);
        }
    }

    private void prepareRecommendedPlan(RecommendedPlanModel recommendedPlanModel) {
        final JSONObject jsonObject = new JSONObject();
        if (isFromRecomendedPlan) {
            try {
                jsonObject.put("visit_date", selectedDate);
                jsonObject.put("recommended_plan", recommendedPlanModel.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                jsonObject.put("visit_date", selectedDate);
                jsonObject.put("title", planBinding.frplandetailEdtplanename.getText().toString());
                final JSONArray jsonArrayAttraction = new JSONArray();
                for (int i = 0; i < recommendedPlanModel.getAttractionArrayList().size(); i++) {
                    final VisitAttractionsModel attractionsModel = recommendedPlanModel.getAttractionArrayList().get(i);
                    jsonArrayAttraction.put(attractionsModel.getId());
                }
                jsonObject.put("attractions", jsonArrayAttraction);
                final JSONArray jsonArrayAnimals = new JSONArray();
                for (int i = 0; i < recommendedPlanModel.getAnimalArrayList().size(); i++) {
                    final VisitAnimalModel visitAnimalModel = recommendedPlanModel.getAnimalArrayList().get(i);
                    jsonArrayAnimals.put(visitAnimalModel.getId());
                }
                jsonObject.put("animals", jsonArrayAnimals);
                final JSONArray jsonArrayExperience = new JSONArray();
                for (int i = 0; i < recommendedPlanModel.getExperienceArrayList().size(); i++) {
                    final VisitExperiencesModel experiencesModel = recommendedPlanModel.getExperienceArrayList().get(i);
                    jsonArrayExperience.put(experiencesModel.getId());
                }
                jsonObject.put("experiences", jsonArrayExperience);
                final JSONArray jsonArrayEvent = new JSONArray();
                for (int i = 0; i < recommendedPlanModel.getVisitWhatsNewModelArrayList().size(); i++) {
                    final VisitWhatsNewModel whatsNewModel = recommendedPlanModel.getVisitWhatsNewModelArrayList().get(i);
                    jsonArrayEvent.put(whatsNewModel.getId());
                }
                jsonObject.put("events", jsonArrayEvent);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        final RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        refreshToken(body);
    }

    private void refreshToken(final RequestBody body) {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "", false);
        final DateFormat df = new SimpleDateFormat(Constants.TIME_STAMP_FORMAT, Locale.ENGLISH);
        final String strRefreshToken = SharedPrefceHelper.getInstance().get(PrefCons.REFRESH_TOKEN, "");
        if (!TextUtils.isEmpty(strRefreshToken.trim())) {
            if (NetUtil.isNetworkAvailable(getActivity())) {
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
                            createPlan(body);
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenModel> call, Throwable t) {
                        DisplayDialog.getInstance().dismissProgressDialog();
                        if (isAdded()) {
                            SnackbarUtils.loadSnackBar(getString(R.string.error), getActivity(), R.color.cool_blue);
                        }
                    }
                });
            }
        }
    }


    private void createPlan(RequestBody body) {
        CreatePlanVisitManager.createPlanVisitManager().postCreatePlan(body, new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    if (result != null) {
                        callGamification(result.toString());
                    }
                    if (!TextUtils.isEmpty(result.toString())) {
                        final FragmentManager manager = getActivity().getSupportFragmentManager();
                        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        recommendedPlanModel.setId(Integer.parseInt(result.toString()));
                        //((HomeActivity) getActivity()).addFragment(PlanVisitDetailFragment.this, PlaneConfirmFragment.getPlanVisitConfirmFragment(recommendedPlanModel));
                        ((HomeActivity) getActivity()).replaceFragment(PlaneConfirmFragment.getPlanVisitConfirmFragment(recommendedPlanModel));
                    } else {
                        SnackbarUtils.loadSnackBar(getString(R.string.error), getActivity(), R.color.cool_blue);
                    }
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    if (message.equalsIgnoreCase("You are allowed to create only one visit plan.")) {
                        alertConfirmDeletePlan();
                    } else {
                        SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
                    }
                }
            }
        });
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
        tvMessage.setText(getString(R.string.error_alreadyhave_palan));
        tvPositiveButton.setText(getString(R.string.yes));
        tvNegativeButton.setText(getString(R.string.no));
        tvPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                deletePlan();
            }
        });

        tvNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                final FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ((HomeActivity) getActivity()).replaceFragment(new PlaneConfirmFragment());
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
                if (!isFromRecomendedPlan) {
                    recommendedPlanModel = null;
                }
                confirmPlan();
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
            }
        });
    }

    public void callGamification(String s) {
        final RequestBody body = GamificationManager.getGamificationManager().createRequestBody("plan_visit", s);
        GamificationManager.getGamificationManager().postCreateGamification(body, new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.e("gamification", "success" + result.toString());
                ((HomeActivity) getActivity()).getUserDetailWithOutDialog();
            }

            @Override
            public void onFaild(String message) {
                Log.e("gamification", "failure" + message);
            }
        });
    }

}
