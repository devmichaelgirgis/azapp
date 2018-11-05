package com.exceedgulf.alainzoo.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.BaseActivity;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.AttractionsHomeAdapter;
import com.exceedgulf.alainzoo.adapter.ExperiencesHomeAdapter;
import com.exceedgulf.alainzoo.adapter.OpeningHoursHomeAdapter;
import com.exceedgulf.alainzoo.adapter.WhatsNewHomeAdapter;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.HappeningNow;
import com.exceedgulf.alainzoo.databinding.FragmentHomeBinding;
import com.exceedgulf.alainzoo.managers.AttractionsManager;
import com.exceedgulf.alainzoo.managers.ExperienceManager;
import com.exceedgulf.alainzoo.managers.HappeningNowManager;
import com.exceedgulf.alainzoo.managers.OpeningHourFrontManager;
import com.exceedgulf.alainzoo.managers.WhatsNewManager;
import com.exceedgulf.alainzoo.models.OpeningHourFront;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class HomeFragment extends BaseFragment {
    private static final String BUNDLE_KEY_HAPPENING_NOW = "happeningnow";
    private static final int OPENING_ZOO_ID_EN = 1353;
    private static final int OPENING_ZOO_ID_AR = 1500;
    private FragmentHomeBinding fragmentHomeBinding;
    private ExperiencesHomeAdapter experiencesHomeAdapter;
    private AttractionsHomeAdapter attractionsHomeAdapter;
    private WhatsNewHomeAdapter whatsNewHomeAdapter;
    private OpeningHoursHomeAdapter openingHoursHomeAdapter;
    private ArrayList<HappeningNow> happeningNowArrayList;
    private long maxOfNeg;
    private long minOfPos;

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.home, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return fragmentHomeBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).enableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle("", getResources().getColor(R.color.cool_blue), null, true);
        //DisplayDialog.getInstance().showProgressDialog(getActivity(), "", false);
        final LinearLayoutManager openingHoursLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        openingHoursHomeAdapter = new OpeningHoursHomeAdapter(getActivity());
        fragmentHomeBinding.happeningNowFragment.tvCount.setText(String.format(Locale.ENGLISH, "%d", 0));
        fragmentHomeBinding.openingNowHome.rvOpeninghr.setLayoutManager(openingHoursLinearLayoutManager);
        // fragmentHomeBinding.openingNowHome.rvOpeninghr.setEmptyView(fragmentHomeBinding.openingNowHome.openinghrTvEmptyView);
        fragmentHomeBinding.openingNowHome.rvOpeninghr.setAdapter(openingHoursHomeAdapter);

        final LinearLayoutManager attractionsLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        attractionsHomeAdapter = new AttractionsHomeAdapter(getActivity());
        fragmentHomeBinding.attractionsHome.attractionsRecyclerView.setLayoutManager(attractionsLinearLayoutManager);
        fragmentHomeBinding.attractionsHome.attractionsRecyclerView.setEmptyView(fragmentHomeBinding.attractionsHome.attractionsTvEmptyView);
        fragmentHomeBinding.attractionsHome.attractionsRecyclerView.setAdapter(attractionsHomeAdapter);

        final LinearLayoutManager experiencesLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        experiencesHomeAdapter = new ExperiencesHomeAdapter(getActivity());
        fragmentHomeBinding.experiencesHome.rvExperiences.setLayoutManager(experiencesLinearLayoutManager);
        fragmentHomeBinding.experiencesHome.rvExperiences.setEmptyView(fragmentHomeBinding.experiencesHome.experiencesTvEmptyView);
        fragmentHomeBinding.experiencesHome.rvExperiences.setAdapter(experiencesHomeAdapter);

        final LinearLayoutManager whatisnewLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        whatsNewHomeAdapter = new WhatsNewHomeAdapter(getActivity());
        fragmentHomeBinding.whatIsNewHome.rvWhatIsNew.setLayoutManager(whatisnewLinearLayoutManager);
        fragmentHomeBinding.whatIsNewHome.rvWhatIsNew.setAdapter(whatsNewHomeAdapter);

        fragmentHomeBinding.openingNowHome.tvOpeningHoursSeeAll.setOnClickListener(this);
        fragmentHomeBinding.happeningNowFragment.happeningNowHome.setOnClickListener(this);
        fragmentHomeBinding.attractionsHome.tvAttractionSeeAll.setOnClickListener(this);
        fragmentHomeBinding.experiencesHome.tvExperienceSeeAll.setOnClickListener(this);
        fragmentHomeBinding.whatIsNewHome.tvWhatsIsNewSeeAll.setOnClickListener(this);
        getClosingHours();
        fetchAttractionsData();
        fetchExperiencesData();
        fetchWhatsNewData();
        getHappeningNow();

        final Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey("BEACON_NOTIFCATION")) {
            if (bundle.getBoolean("BEACON_NOTIFCATION", false)) {
                final String type = bundle.getString("ACTION_NAME");
                final String action_id = bundle.getString("ACTION_ID");
                if (type != null && !TextUtils.isEmpty(type) && action_id != null && !TextUtils.isEmpty(action_id)) {
                    final int id = Integer.parseInt(action_id);
                    if (type.equalsIgnoreCase("animal") || type.equalsIgnoreCase("animals")) {
                        if (((HomeActivity) getActivity()).getcurrentFragment() != null) {
                            ((HomeActivity) getActivity()).addFragment(((HomeActivity) getActivity()).getcurrentFragment(), AnimalDetailFragment.getAnimalDetailFragment(id));
                        } else {
                            ((HomeActivity) getActivity()).replaceFragment(AnimalDetailFragment.getAnimalDetailFragment(id));
                        }
                    } else if (type.equalsIgnoreCase("experience") || type.equalsIgnoreCase("experiences")) {
                        if (((HomeActivity) getActivity()).getcurrentFragment() != null) {
                            ((HomeActivity) getActivity()).addFragment(((HomeActivity) getActivity()).getcurrentFragment(), ExperianceDetailFragment.getExperianceDetailFragment(id));
                        } else {
                            ((HomeActivity) getActivity()).replaceFragment(ExperianceDetailFragment.getExperianceDetailFragment(id));
                        }
                    } else if (type.equalsIgnoreCase("attraction") || type.equalsIgnoreCase("attractions")) {
                        if (((HomeActivity) getActivity()).getcurrentFragment() != null) {
                            ((HomeActivity) getActivity()).addFragment(((HomeActivity) getActivity()).getcurrentFragment(), AttractionsDetailFragment.getAttractionsDetailFragment(id));
                        } else {
                            ((HomeActivity) getActivity()).replaceFragment(AttractionsDetailFragment.getAttractionsDetailFragment(id));
                        }
                    } else if (type.equalsIgnoreCase("venue") || type.equalsIgnoreCase("venues")) {
                        if (((HomeActivity) getActivity()).getcurrentFragment() != null) {
                            ((HomeActivity) getActivity()).addFragment(((HomeActivity) getActivity()).getcurrentFragment(), WhatsNewDetailFragment.getWhatsNewDetailFragment(id));
                        } else {
                            ((HomeActivity) getActivity()).replaceFragment(WhatsNewDetailFragment.getWhatsNewDetailFragment(id));
                        }
                    }
                }
            }
        }
    }

    public void fetchAttractionsData() {
        AttractionsManager.getAttractionManager().getAllEntitiesData(0, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                if (isAdded()) {
                    attractionsHomeAdapter.addItems(resultList);
                    fragmentHomeBinding.attractionsHome.attractionsTvEmptyView.setText(attractionsHomeAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }


            @Override
            public void onFaild(String message) {
                //SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
                if (isAdded()) {
                    fragmentHomeBinding.attractionsHome.attractionsTvEmptyView.setText(attractionsHomeAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, true);
    }

    public void fetchExperiencesData() {
        ExperienceManager.getExperienceManager().getAllEntitiesData(0, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                if (isAdded()) {
                    experiencesHomeAdapter.addItems(resultList);
                    fragmentHomeBinding.experiencesHome.experiencesTvEmptyView.setText(experiencesHomeAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }


            @Override
            public void onFaild(String message) {
                //SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
                if (isAdded()) {
                    fragmentHomeBinding.experiencesHome.experiencesTvEmptyView.setText(experiencesHomeAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, true);

    }

    public void fetchWhatsNewData() {
        WhatsNewManager.getWhatsnewManager().getAllEntitiesData(0, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                if (isAdded()) {
                    whatsNewHomeAdapter.addItems(resultList);
                    fragmentHomeBinding.whatIsNewHome.whatsnewTvEmptyView.setText(whatsNewHomeAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }


            @Override
            public void onFaild(String message) {
                //SnackbarUtils.loadSnackBar(message, getActivity(), R.color.cool_blue);
                if (isAdded()) {
                    fragmentHomeBinding.whatIsNewHome.whatsnewTvEmptyView.setText(whatsNewHomeAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
                }
            }
        }, true);

    }

    private void getClosingHours() {
        final ArrayList<OpeningHourFront> openingHourFronts = (ArrayList<OpeningHourFront>) OpeningHourFrontManager.getPlansVisitManager().getAllEntitiesFromDB();
        if (openingHourFronts.size() > 0) {
            openingHoursHomeAdapter.addItems(openingHourFronts);
            fragmentHomeBinding.openingNowHome.openinghrTvEmptyView.setVisibility(View.GONE);
        }
        OpeningHourFrontManager.getPlansVisitManager().getClosingHours(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                if (isAdded()) {
                    if (!isLoadYes) {
                        final ArrayList<OpeningHourFront> openingHourFrontArrayList = new ArrayList<>();
                        if (resultList != null && resultList.size() > 0) {
                            openingHoursHomeAdapter.clearAllItems();
                            for (int i = 0; i < resultList.size(); i++) {
                                final OpeningHourFront openingHourFront = (OpeningHourFront) resultList.get(i);
                                if (openingHourFront.getId() == OPENING_ZOO_ID_EN || openingHourFront.getId() == OPENING_ZOO_ID_AR) {
                                    openingHourFrontArrayList.add(0, openingHourFront);
                                } else {
                                    openingHourFrontArrayList.add(openingHourFront);
                                }
                            }
                        }
                        openingHoursHomeAdapter.addItems(openingHourFrontArrayList);
                    }
                    fragmentHomeBinding.openingNowHome.openinghrTvEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFaild(String message) {
                if (isAdded()) {
                    if (openingHourFronts.size() > 0) {
                        fragmentHomeBinding.openingNowHome.openinghrTvEmptyView.setVisibility(View.GONE);
                    } else {
                        fragmentHomeBinding.openingNowHome.openinghrTvEmptyView.setText(getString(R.string.no_data_available));
                        fragmentHomeBinding.openingNowHome.openinghrTvEmptyView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public void getHappeningNow() {
        //DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        HappeningNowManager.getHappeningNowManager().getAllEntitiesData(0, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                //DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    if (resultList != null && resultList.size() > 0) {
                        happeningNowArrayList = resultList;
                        fragmentHomeBinding.happeningNowFragment.tvCount.setText("" + resultList.size());
                        final ArrayList<Long> negLongs = new ArrayList<>();
                        final ArrayList<Long> posLongs = new ArrayList<>();
                        final HashMap<Long, String> longStringHashMap = new HashMap<>();
                        for (int i = 0; i < happeningNowArrayList.size(); i++) {
                            final HappeningNow happeningNow = happeningNowArrayList.get(i);

                            final long startDiff = getTimeDiff(happeningNow.getSchedule().getTimes().get(0).getStartTime());
                            final Pair<Integer, Integer> diff = getDiffHoursAndMin(startDiff);
                            Log.e("@@@", "startdiff" + startDiff + "pair first" + diff.first + "pair second" + diff.second);

                            longStringHashMap.put(startDiff, happeningNow.getName());
                            if (startDiff < 0) {
                                negLongs.add(startDiff);
                            } else {
                                posLongs.add(startDiff);
                            }
                        }

                        if (negLongs.size() > 0) {
                            maxOfNeg = Collections.max(negLongs);
                        }
                        if (posLongs.size() > 0) {
                            minOfPos = Collections.min(posLongs);
                        }

                        ArrayList<Long> finalLongs = new ArrayList<>();
                        if (maxOfNeg != 0) {
                            finalLongs.add(maxOfNeg);
                        }
                        if (minOfPos != 0) {
                            finalLongs.add(minOfPos);
                        }

                        String title = longStringHashMap.get(Collections.min((finalLongs)));
                        Log.e("@@@", "collection min " + Collections.min(finalLongs) + "title:" + title);
                        fragmentHomeBinding.happeningNowFragment.tvHappeningNow.setText(Html.fromHtml(title));
                    }
                }
            }

            @Override
            public void onFaild(String message) {
                //DisplayDialog.getInstance().dismissProgressDialog();
                //SnackbarUtils.loadSnackBar(message, getActivity(), R.color.light_grey_blue);
                //fragmentHappeningNowBinding.frHpnTvEmptyView.setText(happeningsNowAdapter.getItemCount() == 0 ? getString(R.string.no_data_available) : "");
            }
        }, false);
    }

    private long getTimeDiff(String happeningNow) {
        Calendar c = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);
        final Date currentDate = c.getTime();
        final String currentTime = dateFormat.format(currentDate);
        Log.e("Home", "currentTime" + currentTime);
        try {

            final SimpleDateFormat formatter_to = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

            final Date d = formatter_to.parse(happeningNow);

            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

            final Date serverDate = calendar.getTime();

            long difference = serverDate.getTime() - currentDate.getTime();

            return difference;
            //return getDiffHoursAndMin(currentDate, serverDate, difference);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @NonNull
    private Pair<Integer, Integer> getDiffHoursAndMin(long difference) {
        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        hours = (hours < 0 ? -hours : hours);
//        Log.e("======= Hours", " current: " + currentDate + "server:" + serverDate);
//        Log.e("======= Hours", " :: " + hours + "min:" + min);
        return new Pair<>(hours, min);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (!isOnClick()) {
            return;
        }
        if (view == fragmentHomeBinding.openingNowHome.tvOpeningHoursSeeAll) {
            ((BaseActivity) getActivity()).addFragment(this, new OpeningHoursFragment());
        } else if (view == fragmentHomeBinding.happeningNowFragment.happeningNowHome) {
            final HappeningNowFragment happeningNowFragment = new HappeningNowFragment();
            final Bundle bundle = new Bundle();
            bundle.putSerializable(BUNDLE_KEY_HAPPENING_NOW, happeningNowArrayList);
            happeningNowFragment.setArguments(bundle);
            ((BaseActivity) getActivity()).addFragment(this, happeningNowFragment);
        } else if (view == fragmentHomeBinding.attractionsHome.tvAttractionSeeAll) {
            ((BaseActivity) getActivity()).addFragment(this, new AttractionsFragment());
        } else if (view == fragmentHomeBinding.experiencesHome.tvExperienceSeeAll) {
            final Bundle bundle = new Bundle();
            bundle.putBoolean(ExperiencesFragment.KEY_IS_BACKARROW, true);
            final Fragment experiencesFragment = new ExperiencesFragment();
            experiencesFragment.setArguments(bundle);
            ((BaseActivity) getActivity()).addFragment(this, experiencesFragment);
        } else if (view == fragmentHomeBinding.whatIsNewHome.tvWhatsIsNewSeeAll) {
            ((BaseActivity) getActivity()).addFragment(this, new WhatsNewSeeAllFragment());
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (getActivity() == null) {
                return;
            }
            ((HomeActivity) getActivity()).enableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle("", getResources().getColor(R.color.cool_blue), null, true);
            if (fragmentHomeBinding.openingNowHome.openinghrTvEmptyView.getVisibility() == View.VISIBLE) {
                getClosingHours();
            }
            if (attractionsHomeAdapter.getItemCount() == 0) {
                fetchAttractionsData();
            }
            if (experiencesHomeAdapter.getItemCount() == 0) {
                fetchExperiencesData();
            }
            if (whatsNewHomeAdapter.getItemCount() == 0) {
                fetchWhatsNewData();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        final ViewGroup mContainer = getActivity().findViewById(R.id.frame_container);
        mContainer.removeAllViews();
        super.onDestroyView();
    }

}
