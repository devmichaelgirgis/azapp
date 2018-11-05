package com.exceedgulf.alainzoo.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.activity.NavigationActivity;
import com.exceedgulf.alainzoo.adapter.InfoMapAdapter;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.ExploreZoo;
import com.exceedgulf.alainzoo.database.models.MyPlaneVisitedItem;
import com.exceedgulf.alainzoo.databinding.FragmentExploreZooPlanBinding;
import com.exceedgulf.alainzoo.managers.ExploreMapManager;
import com.exceedgulf.alainzoo.models.RecommendedPlanModel;
import com.exceedgulf.alainzoo.models.VisitAnimalModel;
import com.exceedgulf.alainzoo.models.VisitAttractionsModel;
import com.exceedgulf.alainzoo.models.VisitExperiencesModel;
import com.exceedgulf.alainzoo.models.VisitOrder;
import com.exceedgulf.alainzoo.models.VisitWhatsNewModel;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.NetUtil;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.services.android.location.LostLocationEngine;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.ui.v5.route.OnRouteSelectionChangeListener;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P.G. on 19/12/17
 */
public class ExploreZooPlaneFragment extends BaseFragment implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks, MapboxMap.OnInfoWindowClickListener, LocationEngineListener, PermissionsListener, InfoMapAdapter.onSeeMoreListener {
    private static final String KEY_MY_PLAN = "MY_PLAN";
    private static final String KEY_EXPLORE_PLANE = "EXPLORE_PLANE";
    private static final String ANIMALS = "animals";
    private static final String EXPERIENCE = "experience";
    private static final String ATTRACTION = "attraction";
    private static final String WHATS_NEW = "whatsnew";
    private static final String VENUE = "venue";
    private FragmentExploreZooPlanBinding exploreZooPlanBinding;
    private RecommendedPlanModel recommendedPlanModel = null;
    private Bundle bundle;
    private MapboxMap mapboxMap;
    private boolean zoomCamera = false;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private Location originLocation;
    private String TAG = ExploreZooPlaneFragment.class.getSimpleName();
    private InfoMapAdapter infoMapAdapter;
    private ArrayList<ExploreZoo> exploreZooArrayList;
    private ArrayList<ExploreZoo> exploreZooArrayListTmp;
    private ArrayList<Marker> markerArrayList;
    private ArrayList<String> arrSelectedFilter;
    private boolean isMyPlan = false;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private ExploreZoo exploreZoo;
    private Marker marker;

    public static ExploreZooPlaneFragment getZooPlaneFragment(final RecommendedPlanModel recommendedPlanModel, boolean isFromMyplan) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXPLORE_PLANE, recommendedPlanModel);
        bundle.putBoolean(KEY_MY_PLAN, isFromMyplan);
        final ExploreZooPlaneFragment fragment = new ExploreZooPlaneFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        bundle = savedInstanceState;
        exploreZooPlanBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_explore_zoo_plan, container, false);
        return exploreZooPlanBinding.getRoot();
    }

    @Override
    public void initView(final View view) {
        exploreZooArrayList = new ArrayList<>();
        exploreZooArrayListTmp = new ArrayList<>();
        markerArrayList = new ArrayList<>();
        exploreZooPlanBinding.expandableLayout.collapse();
        ((HomeActivity) getActivity()).disableCollapse();
        arrSelectedFilter = new ArrayList<>();
        arrSelectedFilter.add(ANIMALS);
        arrSelectedFilter.add(EXPERIENCE);
        arrSelectedFilter.add(VENUE);
        arrSelectedFilter.add(ATTRACTION);
        exploreZooPlanBinding.frExplAnimals.setSelected(true);
        exploreZooPlanBinding.frExplExperience.setSelected(true);
        exploreZooPlanBinding.frExplRestaurant.setSelected(true);
        select(exploreZooPlanBinding.frExplAnimals);
        select(exploreZooPlanBinding.frExplExperience);
        select(exploreZooPlanBinding.frExplRestaurant);
        final Bundle tmpBundle = this.getArguments();
        if (tmpBundle != null && tmpBundle.containsKey(KEY_EXPLORE_PLANE)) {
            recommendedPlanModel = (RecommendedPlanModel) tmpBundle.getSerializable(KEY_EXPLORE_PLANE);
            isMyPlan = tmpBundle.getBoolean(KEY_MY_PLAN, false);
        }
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_my_plan), getResources().getColor(R.color.reddish), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), true);
        exploreZooPlanBinding.mapView.setStyleUrl(getString(R.string.map_style));
        exploreZooPlanBinding.mapView.getMapAsync(this);
        exploreZooPlanBinding.frExplAnimals.setOnClickListener(this);
        exploreZooPlanBinding.frExplExperience.setOnClickListener(this);
        exploreZooPlanBinding.frExplRestaurant.setOnClickListener(this);
        exploreZooPlanBinding.frExplTransport.setOnClickListener(this);
        exploreZooPlanBinding.frExplBtnGpsCenter.setOnClickListener(this);
        exploreZooPlanBinding.frExplTvStart.setOnClickListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).selectedBottomMenuPosition = 2;
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_my_plan), getResources().getColor(R.color.reddish), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.myplanmap, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (!isOnClick()) {
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.action_explore_setings) {
            if (exploreZooPlanBinding.expandableLayout.isExpanded()) {
                exploreZooPlanBinding.expandableLayout.collapse();
            } else {
                exploreZooPlanBinding.expandableLayout.expand();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == exploreZooPlanBinding.frExplTvStart) {
            startNextNavigation(true);
        }

        if (v == exploreZooPlanBinding.frExplAnimals) {
            if (exploreZooPlanBinding.expandableLayout.isExpanded()) {
                exploreZooPlanBinding.expandableLayout.collapse();
            }
            if (mapboxMap != null) {
                mapboxMap.clear();
            }
            if (!arrSelectedFilter.contains(ANIMALS)) {
                arrSelectedFilter.add(ANIMALS);
                exploreZooPlanBinding.frExplAnimals.setSelected(true);
                select(v);
            } else {
                if (arrSelectedFilter.size() > 2) {
                    arrSelectedFilter.remove(ANIMALS);
                    exploreZooPlanBinding.frExplAnimals.setSelected(false);
                    select(v);
                } else {
                    exploreZooPlanBinding.frExplAnimals.setSelected(true);
                    select(v);
                }
            }
            filterData();
        }
        if (v == exploreZooPlanBinding.frExplExperience) {
            if (exploreZooPlanBinding.expandableLayout.isExpanded()) {
                exploreZooPlanBinding.expandableLayout.collapse();
            }
            if (mapboxMap != null) {
                mapboxMap.clear();
            }
            select(v);
            if (!arrSelectedFilter.contains(EXPERIENCE)) {
                arrSelectedFilter.add(EXPERIENCE);
                exploreZooPlanBinding.frExplExperience.setSelected(true);
                select(v);
            } else {
                if (arrSelectedFilter.size() > 2) {
                    arrSelectedFilter.remove(EXPERIENCE);
                    exploreZooPlanBinding.frExplExperience.setSelected(false);
                    select(v);
                } else {
                    exploreZooPlanBinding.frExplExperience.setSelected(true);
                    select(v);
                }
            }
            filterData();
        }
        if (v == exploreZooPlanBinding.frExplRestaurant) {
            if (exploreZooPlanBinding.expandableLayout.isExpanded()) {
                exploreZooPlanBinding.expandableLayout.collapse();
            }
            if (mapboxMap != null) {
                mapboxMap.clear();
            }
            select(v);
            if (!arrSelectedFilter.contains(VENUE)) {
                arrSelectedFilter.add(VENUE);
                exploreZooPlanBinding.frExplRestaurant.setSelected(true);
                select(v);
            } else {
                if (arrSelectedFilter.size() > 2) {
                    arrSelectedFilter.remove(VENUE);
                    exploreZooPlanBinding.frExplRestaurant.setSelected(false);
                    select(v);
                } else {
                    exploreZooPlanBinding.frExplRestaurant.setSelected(true);
                    select(v);
                }
            }
            filterData();
        }
        if (v == exploreZooPlanBinding.frExplBtnGpsCenter) {
            if (mapboxMap == null) {
                return;
            }
            final Location location = mapboxMap.getMyLocation();
            if (location == null) {
                SnackbarUtils.loadSnackBar(getString(R.string.no_location_available), getActivity(), R.color.reddish);
                return;
            }
            final LatLng latLngZoo = new LatLng(24.179517, 55.739580);
            final LatLng latLngDestination = new LatLng(location.getLatitude(), location.getLongitude());
            final float distance = (int) (latLngZoo.distanceTo(latLngDestination));
            if (distance <= 5000) {
                final CameraPosition position = new CameraPosition.Builder()
                        .target(new com.mapbox.mapboxsdk.geometry.LatLng(location.getLatitude(), location.getLongitude())) // Sets the new camera position
                        .zoom(16)// Sets the zoom
                        .bearing(180)
                        .tilt(30) // Set the camera tilt
                        .build(); // Creates a CameraPosition from the builder

                this.mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 2000);
            } else {
                ((HomeActivity) getActivity()).alertMessage("", getString(R.string.oops_away_from_zoo), true);
            }
        }

    }

    public void select(View view) {
        final Typeface fontRegular = Typeface.createFromAsset(getContext().getAssets(), getString(R.string.avenir_next_regular));
        final Typeface fontBold = Typeface.createFromAsset(getContext().getAssets(), getString(R.string.avenir_next_demi_bold));
        if (view == exploreZooPlanBinding.frExplAnimals) {
            if (exploreZooPlanBinding.frExplAnimals.isSelected()) {
                exploreZooPlanBinding.frExplAnimals.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_explore_zoo_filter));
                exploreZooPlanBinding.frExplTvAnimal.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                exploreZooPlanBinding.frExplTvAnimal.setTypeface(fontBold);
                exploreZooPlanBinding.frExplAnimal.setImageResource(R.drawable.ic_show_selected_animal);
            } else {
                exploreZooPlanBinding.frExplAnimal.setImageResource(R.drawable.ic_show_animal);
                exploreZooPlanBinding.frExplAnimals.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.reddish));
                exploreZooPlanBinding.frExplTvAnimal.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorexplorezoofilter));
                exploreZooPlanBinding.frExplTvAnimal.setTypeface(fontRegular);
            }

        } else if (view == exploreZooPlanBinding.frExplExperience) {
            if (exploreZooPlanBinding.frExplExperience.isSelected()) {
                exploreZooPlanBinding.frExplExperience.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_explore_zoo_filter));
                exploreZooPlanBinding.frExplTvExperience.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                exploreZooPlanBinding.frExplTvExperience.setTypeface(fontBold);
                exploreZooPlanBinding.frExplIvExperience.setImageResource(R.drawable.ic_show_selected_experience);
            } else {
                exploreZooPlanBinding.frExplIvExperience.setImageResource(R.drawable.ic_show_experience);
                exploreZooPlanBinding.frExplExperience.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.reddish));
                exploreZooPlanBinding.frExplTvExperience.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorexplorezoofilter));
                exploreZooPlanBinding.frExplTvExperience.setTypeface(fontRegular);
            }

        } else if (view == exploreZooPlanBinding.frExplRestaurant) {
            if (exploreZooPlanBinding.frExplRestaurant.isSelected()) {
                exploreZooPlanBinding.frExplRestaurant.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_explore_zoo_filter));
                exploreZooPlanBinding.frExplTvRestaurant.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                exploreZooPlanBinding.frExplTvRestaurant.setTypeface(fontBold);
                exploreZooPlanBinding.frExplIvRestaurant.setImageResource(R.drawable.ic_show_selected_restaurant);
            } else {
                exploreZooPlanBinding.frExplIvRestaurant.setImageResource(R.drawable.ic_show_restaurant);
                exploreZooPlanBinding.frExplRestaurant.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.reddish));
                exploreZooPlanBinding.frExplTvRestaurant.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorexplorezoofilter));
                exploreZooPlanBinding.frExplTvRestaurant.setTypeface(fontRegular);
            }

        } else if (view == exploreZooPlanBinding.frExplTransport) {
            if (exploreZooPlanBinding.frExplTransport.isSelected()) {
                exploreZooPlanBinding.frExplTransport.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_explore_zoo_filter));
                exploreZooPlanBinding.frExplTvTransport.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                exploreZooPlanBinding.frExplTvTransport.setTypeface(fontBold);
                exploreZooPlanBinding.frExplIvTransport.setImageResource(R.drawable.ic_show_selected_transport);
            } else {
                exploreZooPlanBinding.frExplIvTransport.setImageResource(R.drawable.ic_show_transpot);
                exploreZooPlanBinding.frExplTransport.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.reddish));
                exploreZooPlanBinding.frExplTvTransport.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorexplorezoofilter));
                exploreZooPlanBinding.frExplTvTransport.setTypeface(fontRegular);
            }

        }
    }

    private void filterData() {
        exploreZooArrayList.clear();
        markerArrayList.clear();
        if (navigationMapRoute != null) {
            navigationMapRoute.removeRoute();
        }
        for (int j = 0; j < arrSelectedFilter.size(); j++) {
            final String strType = arrSelectedFilter.get(j);
            for (int i = 0; i < exploreZooArrayListTmp.size(); i++) {
                final ExploreZoo exploreZoo = exploreZooArrayListTmp.get(i);
                if (strType.equalsIgnoreCase(exploreZoo.getType())) {
                    exploreZooArrayList.add(exploreZoo);
//                    final MarkerOptions markerOptions = new MarkerOptions()
//                            .position(new LatLng(Double.parseDouble(exploreZoo.getLatitude()), Double.parseDouble(exploreZoo.getLongitude())))
//                            .setSnippet(String.valueOf(exploreZooArrayList.size() - 1));
//                    markerArrayList.add(mapboxMap.addMarker(markerOptions));
                }
            }
        }
        if (recommendedPlanModel.getVisitOrderArrayList() != null && recommendedPlanModel.getVisitOrderArrayList().size() > 0) {
            final ArrayList<ExploreZoo> exploreZoosSorted = new ArrayList<>();
            final ArrayList<ExploreZoo> exploreZoosUnSorted = new ArrayList<>();
            final ArrayList<String> sortedIdList = new ArrayList<>();
            for (int i = 0; i < recommendedPlanModel.getVisitOrderArrayList().size(); i++) {
                final VisitOrder visitOrder = recommendedPlanModel.getVisitOrderArrayList().get(i);
                for (int j = 0; j < exploreZooArrayList.size(); j++) {
                    final ExploreZoo exploreZoo = exploreZooArrayList.get(j);
                    if (exploreZoo.getId().equals(visitOrder.getId())) {
                        exploreZoosSorted.add(exploreZoo);
                        sortedIdList.add(String.valueOf(exploreZoo.getId()));
                    }
                }
            }

            for (int i = 0; i < exploreZooArrayList.size(); i++) {
                if (!sortedIdList.contains(String.valueOf(exploreZooArrayList.get(i).getId()))) {
                    exploreZoosUnSorted.add(exploreZooArrayList.get(i));
                }
            }

            exploreZooArrayList.clear();
            exploreZooArrayList.trimToSize();
            exploreZooArrayList.addAll(exploreZoosSorted);
            exploreZooArrayList.addAll(exploreZoosUnSorted);
            addMarkerToMap();
        } else {
            addMarkerToMap();
        }
        updateMarkerImage();
        drawRoute();
    }

    @Override
    public void onMapReady(final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        //this.mapboxMap.setMaxZoomPreference(16);
        //this.mapboxMap.setMinZoomPreference(14);
        this.mapboxMap.setMyLocationEnabled(true);
        enableLocationPlugin();
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(24.179517, 55.739580)) // Sets the new camera position
                .zoom(16)// Sets the zoom
                .bearing(180) // Rotate the camera
                .tilt(30) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        this.mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 2000);

        this.mapboxMap.setOnInfoWindowClickListener(this);
        this.mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                exploreZooPlanBinding.expandableLayout.collapse();
                return false;
            }
        });
        infoMapAdapter = new InfoMapAdapter(getActivity(), exploreZooArrayList, markerArrayList, this.mapboxMap, this, isMyPlan ? recommendedPlanModel.getId() : null);
        addDataToMaps();

    }

    private void addDataToMaps() {
        if (recommendedPlanModel != null) {
            mapboxMap.setInfoWindowAdapter(infoMapAdapter);
            zoomCamera = false;
            if (ExploreMapManager.getExploreMapManager().getCountFromDB() > 0) {
                zoomCamera = false;
                for (VisitAnimalModel visitAnimalModel : recommendedPlanModel.getAnimalArrayList()) {
                    final List list = getMapItem(visitAnimalModel.getId(), "animals");
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            final ExploreZoo exploreZoo = (ExploreZoo) list.get(i);
                            if (!TextUtils.isEmpty(exploreZoo.getLatitude()) && !TextUtils.isEmpty(exploreZoo.getLongitude())) {
                                exploreZooArrayList.add(exploreZoo);
//                            final MarkerOptions markerOptions = new MarkerOptions()
//                                    .position(new LatLng(Double.parseDouble(exploreZoo.getLatitude()), Double.parseDouble(exploreZoo.getLongitude())))
//                                    .setSnippet(String.valueOf((exploreZooArrayList.size() - 1)));
//                            markerArrayList.add(mapboxMap.addMarker(markerOptions));
                            }
                        }
                    }
                }

                // Experiance data ading

                for (VisitExperiencesModel visitExperiencesModel : recommendedPlanModel.getExperienceArrayList()) {
                    final List list = getMapItem(visitExperiencesModel.getId(), "experience");
                    if (list.size() > 0) {
                        final ExploreZoo exploreZoo = (ExploreZoo) list.get(0);
                        if (!TextUtils.isEmpty(exploreZoo.getLatitude()) && !TextUtils.isEmpty(exploreZoo.getLongitude())) {
                            exploreZooArrayList.add(exploreZoo);
//                            final MarkerOptions markerOptions = new MarkerOptions()
//                                    .position(new LatLng(Double.parseDouble(exploreZoo.getLatitude()), Double.parseDouble(exploreZoo.getLongitude())))
//                                    .setSnippet(String.valueOf((exploreZooArrayList.size() - 1)));
//
//                            markerArrayList.add(mapboxMap.addMarker(markerOptions));
                        }
                    }

                }

                for (VisitAttractionsModel visitAttractionsModel : recommendedPlanModel.getAttractionArrayList()) {
                    final List list = getMapItem(visitAttractionsModel.getId(), "attraction");
                    if (list.size() > 0) {
                        final ExploreZoo exploreZoo = (ExploreZoo) list.get(0);
                        if (!TextUtils.isEmpty(exploreZoo.getLatitude()) && !TextUtils.isEmpty(exploreZoo.getLongitude())) {
                            exploreZooArrayList.add(exploreZoo);
//                            final MarkerOptions markerOptions = new MarkerOptions()
//                                    .position(new LatLng(Double.parseDouble(exploreZoo.getLatitude()), Double.parseDouble(exploreZoo.getLongitude())))
//                                    .setSnippet(String.valueOf((exploreZooArrayList.size() - 1)));
//
//                            markerArrayList.add(mapboxMap.addMarker(markerOptions));
                        }

                    }

                }

                for (VisitWhatsNewModel visitWhatsNewModel : recommendedPlanModel.getVisitWhatsNewModelArrayList()) {
                    if (!TextUtils.isEmpty(visitWhatsNewModel.getLatitude()) && !TextUtils.isEmpty(visitWhatsNewModel.getLongitude())) {
                        final ExploreZoo exploreZoo = new ExploreZoo();
                        exploreZoo.setType(WHATS_NEW);
                        exploreZoo.setLatitude(visitWhatsNewModel.getLatitude());
                        exploreZoo.setLongitude(visitWhatsNewModel.getLongitude());
                        exploreZoo.setId(visitWhatsNewModel.getId());
                        exploreZoo.setName(visitWhatsNewModel.getName());
                        exploreZoo.setBody("");

                        exploreZooArrayList.add(exploreZoo);
//                        final MarkerOptions markerOptions = new MarkerOptions()
//                                .position(new LatLng(Double.parseDouble(exploreZoo.getLatitude()), Double.parseDouble(exploreZoo.getLongitude())))
//                                .setSnippet(String.valueOf((exploreZooArrayList.size() - 1)));
//
//                        markerArrayList.add(mapboxMap.addMarker(markerOptions));
                    }
                }
            } else {
                DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
                ExploreMapManager.getExploreMapManager().getAllExploreZoo(null, "", "", new ApiCallback() {
                    @Override
                    public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                        DisplayDialog.getInstance().dismissProgressDialog();
                        if (isAdded()) {
                            addDataToMaps();
                        }
                    }

                    @Override
                    public void onFaild(String message) {
                        DisplayDialog.getInstance().dismissProgressDialog();
                        SnackbarUtils.loadSnackBar(message, getActivity(), R.color.reddish);
                    }
                });

            }
        }

        if (recommendedPlanModel.getVisitOrderArrayList() != null && recommendedPlanModel.getVisitOrderArrayList().size() > 0) {
            final ArrayList<ExploreZoo> exploreZoosSorted = new ArrayList<>();
            final ArrayList<ExploreZoo> exploreZoosUnSorted = new ArrayList<>();
            final ArrayList<String> sortedIdList = new ArrayList<>();
            for (int i = 0; i < recommendedPlanModel.getVisitOrderArrayList().size(); i++) {
                final VisitOrder visitOrder = recommendedPlanModel.getVisitOrderArrayList().get(i);
                for (int j = 0; j < exploreZooArrayList.size(); j++) {
                    final ExploreZoo exploreZoo = exploreZooArrayList.get(j);
                    if (exploreZoo.getId().equals(visitOrder.getId())) {
                        exploreZoosSorted.add(exploreZoo);
                        sortedIdList.add(String.valueOf(exploreZoo.getId()));
                    }
                }
            }

            for (int i = 0; i < exploreZooArrayList.size(); i++) {
                if (!sortedIdList.contains(String.valueOf(exploreZooArrayList.get(i).getId()))) {
                    exploreZoosUnSorted.add(exploreZooArrayList.get(i));
                }
            }

            exploreZooArrayList.clear();
            exploreZooArrayList.trimToSize();
            exploreZooArrayList.addAll(exploreZoosSorted);
            exploreZooArrayList.addAll(exploreZoosUnSorted);

            exploreZoosSorted.clear();
            exploreZoosUnSorted.clear();
            sortedIdList.clear();

            if (!recommendedPlanModel.isCustomPlan()) {
                for (int i = 0; i < exploreZooArrayList.size(); i++) {
                    final ExploreZoo exploreZoo = exploreZooArrayList.get(i);
                    final String id = String.valueOf(exploreZoo.getId());
                    if (!sortedIdList.contains(id)) {
                        sortedIdList.add(id);
                        exploreZoosSorted.add(exploreZoo);
                    }
                }
                exploreZooArrayList.clear();
                exploreZooArrayList.trimToSize();
                exploreZooArrayList.addAll(exploreZoosSorted);
            }

            addMarkerToMap();

        } else {
            if (!recommendedPlanModel.isCustomPlan()) {
                final ArrayList<ExploreZoo> exploreZoos = new ArrayList<>();
                final ArrayList<String> sortedIdList = new ArrayList<>();
                for (int i = 0; i < exploreZooArrayList.size(); i++) {
                    final ExploreZoo exploreZoo = exploreZooArrayList.get(i);
                    final String id = String.valueOf(exploreZoo.getId());
                    if (!sortedIdList.contains(id)) {
                        sortedIdList.add(id);
                        exploreZoos.add(exploreZoo);
                    }
                }
                exploreZooArrayList.clear();
                exploreZooArrayList.trimToSize();
                exploreZooArrayList.addAll(exploreZoos);
            }

            addMarkerToMap();
        }

        if (exploreZooArrayListTmp.size() == 0) {
            exploreZooArrayListTmp.addAll(exploreZooArrayList);
        }

        updateMarkerImage();
        drawRoute();
    }

    private void addMarkerToMap() {
        if (mapboxMap != null) {
            mapboxMap.clear();
        }
        for (int p = 0; p < exploreZooArrayList.size(); p++) {
            final ExploreZoo exploreZoo = exploreZooArrayList.get(p);
            final MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(exploreZoo.getLatitude()), Double.parseDouble(exploreZoo.getLongitude())))
                    .setSnippet(String.valueOf(p));
            markerArrayList.add(mapboxMap.addMarker(markerOptions));
        }
    }

    private void updateMarkerImage() {
        try {
            for (int i = 0; i < exploreZooArrayList.size(); i++) {
                final ExploreZoo exploreZoo = exploreZooArrayList.get(i);
                if (!TextUtils.isEmpty(exploreZoo.getIcon())) {
                    final Marker marker = markerArrayList.get(i);
                    Glide.with(getActivity())
                            .asBitmap()
                            .load(exploreZoo.getIcon())
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                    final Bitmap resized = Bitmap.createScaledBitmap(resource, 120, 120, false);
                                    final int visiteItem = AlainZooDB.getInstance().myPlaneVisitedItemDao().isVisited(exploreZoo.getId(), exploreZoo.getType());
                                    if (visiteItem != 0) {
                                        final Bitmap bmpMonochrome = Bitmap.createBitmap(resized.getWidth(), resized.getHeight(), Bitmap.Config.ARGB_8888);
                                        final Canvas canvas = new Canvas(bmpMonochrome);
                                        final ColorMatrix ma = new ColorMatrix();
                                        ma.setSaturation(0);
                                        final Paint paint = new Paint();
                                        paint.setColorFilter(new ColorMatrixColorFilter(ma));
                                        canvas.drawBitmap(resized, 0, 0, paint);
                                        marker.setIcon(IconFactory.getInstance(getActivity()).fromBitmap(bmpMonochrome));

                                    } else {
                                        marker.setIcon(IconFactory.getInstance(getActivity()).fromBitmap(resized));
                                    }
                                }
                            });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MarkerException", e.getMessage());
        }

    }

    private void startNextNavigation(boolean showPopup) {
        if (mapboxMap == null) {
            return;
        }
        final Location location = mapboxMap.getMyLocation();
        if (location == null) {
            SnackbarUtils.loadSnackBar(getString(R.string.no_location_available), getActivity(), R.color.reddish);
            return;
        }
        final LatLng latLngZoo = new LatLng(24.179517, 55.739580);
        final LatLng latLngDestination = new LatLng(location.getLatitude(), location.getLongitude());
        final float distance = (int) (latLngZoo.distanceTo(latLngDestination));
        if (distance <= 5000) {
            for (int i = 0; i < exploreZooArrayList.size(); i++) {
                final ExploreZoo exploreZoo = exploreZooArrayList.get(i);
                final int visiteItem = AlainZooDB.getInstance().myPlaneVisitedItemDao().isVisited(exploreZoo.getId(), exploreZoo.getType());
                if (visiteItem == 0) {
                    this.exploreZoo = exploreZoo;
                    marker = markerArrayList.get(i);
                    break;
                }
            }
            if (exploreZoo == null) {
                return;
            }

            if (showPopup) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setCancelable(false);
                final LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_sos_request, null);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final TextView tvTitle = dialogView.findViewById(R.id.dialog_custom_tvTitle);
                final TextView tvYes = dialogView.findViewById(R.id.dialog_custom_tvYes);
                final TextView tvNo = dialogView.findViewById(R.id.dialog_custom_tvNo);
                final TextView tvMessage = dialogView.findViewById(R.id.dialog_custom_tvMessage);

                tvTitle.setVisibility(View.GONE);
                tvMessage.setText(String.format(getString(R.string.navigate_to_next_point), exploreZoo.getName()));
                tvYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        if (NetUtil.isNetworkAvailable(getActivity())) {
                            if (!NetUtil.isGPSEnable(getActivity())) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                return;
                            }

                            final Point origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                            final Point dest = Point.fromLngLat(Double.parseDouble(exploreZoo.getLongitude()), Double.parseDouble(exploreZoo.getLatitude()));
                            final Intent intent = new Intent(getActivity(), NavigationActivity.class);
                            intent.putExtra("Name", exploreZoo.getName());
                            intent.putExtra("Origin", origin);
                            intent.putExtra("Dest", dest);
                            startActivityForResult(intent, 109);
                        } else {
                            SnackbarUtils.loadSnackBar(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_), getActivity(), R.color.reddish);
                        }
                    }
                });

                tvNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
                final int width = (int) getResources().getDimension(R.dimen._260sdp);
                alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                if (NetUtil.isNetworkAvailable(getActivity())) {
                    if (!NetUtil.isGPSEnable(getActivity())) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        return;
                    }

                    final Point origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                    final Point dest = Point.fromLngLat(Double.parseDouble(exploreZoo.getLongitude()), Double.parseDouble(exploreZoo.getLatitude()));
                    final Intent intent = new Intent(getActivity(), NavigationActivity.class);
                    intent.putExtra("Name", exploreZoo.getName());
                    intent.putExtra("Origin", origin);
                    intent.putExtra("Dest", dest);
                    startActivityForResult(intent, 109);
                } else {
                    SnackbarUtils.loadSnackBar(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_), getActivity(), R.color.reddish);
                }
            }
        } else {
            ((HomeActivity) getActivity()).alertMessage("", getString(R.string.oops_away_from_zoo), true);
        }

    }

    public List getMapItem(final Integer id, String type) {
        return ExploreMapManager.getExploreMapManager().getAllEntitiesFromDB(id, "", type);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public boolean onInfoWindowClick(@NonNull final Marker marker) {
//        if (marker.getInfoWindow().getView().findViewById(R.id.row_info_tvNavigate).isEnabled()) {
//            marker.getInfoWindow().getView().findViewById(R.id.row_info_tvNavigate).performClick();
//        } else if (marker.getInfoWindow().getView().findViewById(R.id.row_info_tvSeeInfo).isEnabled()) {
//            marker.getInfoWindow().getView().findViewById(R.id.row_info_tvSeeInfo).performClick();
//        }
        return true;
    }

    @Override
    public void seeMoreClick(ExploreZoo exploreZoo) {
        if (exploreZoo.getType().equalsIgnoreCase(ANIMALS)) {
            ((HomeActivity) getActivity()).addFragment(this, AnimalDetailFragment.getAnimalDetailFragment(exploreZoo.getId()));
        } else if (exploreZoo.getType().equalsIgnoreCase(EXPERIENCE)) {
            ((HomeActivity) getActivity()).addFragment(this, ExperianceDetailFragment.getExperianceDetailFragment(exploreZoo.getId()));
        } else if (exploreZoo.getType().equalsIgnoreCase(ATTRACTION)) {
            ((HomeActivity) getActivity()).addFragment(this, AttractionsDetailFragment.getAttractionsDetailFragment(exploreZoo.getId()));
        } else if (exploreZoo.getType().equalsIgnoreCase(WHATS_NEW)) {
            ((HomeActivity) getActivity()).addFragment(this, WhatsNewDetailFragment.getWhatsNewDetailFragment(exploreZoo.getId()));
        } else {
            SnackbarUtils.loadSnackBar(getString(R.string.no_data_available), getActivity(), R.color.reddish);
        }
    }

    @Override
    public void startNavigationPlan(ExploreZoo exploreZoo, Marker marker) {
        this.exploreZoo = exploreZoo;
        this.marker = marker;

        if (NetUtil.isNetworkAvailable(getActivity())) {
            if (!NetUtil.isGPSEnable(getActivity())) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                return;
            }
            if (mapboxMap == null) {
                return;
            }
            final Location location = mapboxMap.getMyLocation();
            if (location == null) {
                SnackbarUtils.loadSnackBar(getString(R.string.no_location_available), getActivity(), R.color.reddish);
                return;
            }
            final Point origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());
            final Point dest = Point.fromLngLat(Double.parseDouble(exploreZoo.getLongitude()), Double.parseDouble(exploreZoo.getLatitude()));
            final Intent intent = new Intent(getActivity(), NavigationActivity.class);
            intent.putExtra("Name", this.exploreZoo.getName());
            intent.putExtra("Origin", origin);
            intent.putExtra("Dest", dest);
            startActivityForResult(intent, 106);
        } else {
            SnackbarUtils.loadSnackBar(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_), getActivity(), R.color.reddish);
        }
    }

    public void startNavigationPlan() {
        if (mapboxMap == null) {
            return;
        }
        final Location location = mapboxMap.getMyLocation();
        if (location == null) {
            SnackbarUtils.loadSnackBar(getString(R.string.no_location_available), getActivity(), R.color.reddish);
            return;
        }
        final LatLng latLngZoo = new LatLng(24.179517, 55.739580);
        final LatLng latLngDestination = new LatLng(location.getLatitude(), location.getLongitude());
        final float distance = (int) (latLngZoo.distanceTo(latLngDestination));
        if (distance <= 5000) {
            if (NetUtil.isNetworkAvailable(getActivity())) {
                if (!NetUtil.isGPSEnable(getActivity())) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    return;
                }
                if (currentRoute != null) {
                    for (int i = 0; i < exploreZooArrayList.size(); i++) {
                        final ExploreZoo exploreZoo = exploreZooArrayList.get(i);
                        final int visiteItem = AlainZooDB.getInstance().myPlaneVisitedItemDao().isVisited(exploreZoo.getId(), exploreZoo.getType());
                        if (visiteItem == 0) {
                            this.exploreZoo = exploreZoo;
                            marker = markerArrayList.get(i);
                            break;
                        }
                    }
                    AppAlainzoo.getAppAlainzoo().setCurrentRoute(currentRoute);
                    final Intent intent = new Intent(getActivity(), NavigationActivity.class);
                    intent.putExtra("Name", exploreZoo.getName());
                    intent.putExtra("Route", true);
                    startActivityForResult(intent, 106);
                } else {
                    drawRoute();
                }
            } else {
                SnackbarUtils.loadSnackBar(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_), getActivity(), R.color.reddish);
            }
        } else {
            ((HomeActivity) getActivity()).alertMessage("", getString(R.string.oops_away_from_zoo), true);
        }
    }

    @Override
    public void reRouteDraw(final Point origin, final Point dest) {
//        if (origin == null && dest == null) {
//            drawRoute();
//            return;
//        }
//        final ArrayList<Point> pointArrayList = new ArrayList<>();
//        pointArrayList.add(dest);
//        getRoute(origin, pointArrayList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 106) {
            createNavigationFinishDialog(false);
        } else if (requestCode == 109 && data != null) {
            createNavigationFinishDialog(true);

        }
    }

    private void createNavigationFinishDialog(final boolean showNext) {
        if (marker == null && exploreZoo == null) {
            return;
        }

        final int visiteItem = AlainZooDB.getInstance().myPlaneVisitedItemDao().isVisited(exploreZoo.getId(), exploreZoo.getType());
        if (visiteItem != 0) {
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_sos_request, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView tvTitle = dialogView.findViewById(R.id.dialog_custom_tvTitle);
        final TextView tvYes = dialogView.findViewById(R.id.dialog_custom_tvYes);
        final TextView tvNo = dialogView.findViewById(R.id.dialog_custom_tvNo);
        final TextView tvMessage = dialogView.findViewById(R.id.dialog_custom_tvMessage);

        tvTitle.setVisibility(View.GONE);
        tvMessage.setText(getString(R.string.need_to_mark_location_visited));
        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (marker != null && marker.getIcon() != null && marker.getIcon().getBitmap() != null) {
                    final Bitmap tmpBitmap = marker.getIcon().getBitmap();
                    final Bitmap bmpMonochrome = Bitmap.createBitmap(tmpBitmap.getWidth(), tmpBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    final Canvas canvas = new Canvas(bmpMonochrome);
                    final ColorMatrix ma = new ColorMatrix();
                    ma.setSaturation(0);
                    final Paint paint = new Paint();
                    paint.setColorFilter(new ColorMatrixColorFilter(ma));
                    canvas.drawBitmap(tmpBitmap, 0, 0, paint);
                    marker.setIcon(IconFactory.getInstance(getActivity()).fromBitmap(bmpMonochrome));
                }
                if (exploreZoo != null) {
                    final MyPlaneVisitedItem myPlaneVisitedItem = new MyPlaneVisitedItem();
                    myPlaneVisitedItem.setPlanId(0);
                    myPlaneVisitedItem.setVisitedItemId(exploreZoo.getId());
                    myPlaneVisitedItem.setVisitedItemType(exploreZoo.getType());
                    AlainZooDB.getInstance().myPlaneVisitedItemDao().insertItem(myPlaneVisitedItem);
                }
                exploreZoo = null;
                marker = null;
                if (showNext) {
                    startNextNavigationDialog();
                }
                //drawRoute();
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (showNext) {
                    startNextNavigationDialog();
                }
            }
        });
        alertDialog.show();
        final int width = (int) getResources().getDimension(R.dimen._260sdp);
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void startNextNavigationDialog() {
        ExploreZoo exploreZooTmp = null;
        for (int i = 0; i < exploreZooArrayList.size(); i++) {
            final ExploreZoo exploreZoo = exploreZooArrayList.get(i);
            final int visiteItem = AlainZooDB.getInstance().myPlaneVisitedItemDao().isVisited(exploreZoo.getId(), exploreZoo.getType());
            if (visiteItem == 0) {
                exploreZooTmp = exploreZoo;
                break;
            }
        }
        if (exploreZooTmp != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setCancelable(false);
            final LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_sos_request, null);
            builder.setView(dialogView);
            final AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            final TextView tvTitle = dialogView.findViewById(R.id.dialog_custom_tvTitle);
            final TextView tvYes = dialogView.findViewById(R.id.dialog_custom_tvYes);
            final TextView tvNo = dialogView.findViewById(R.id.dialog_custom_tvNo);
            final TextView tvMessage = dialogView.findViewById(R.id.dialog_custom_tvMessage);

            tvTitle.setVisibility(View.GONE);
            tvMessage.setText(String.format(getString(R.string.navigate_to_next_point), exploreZooTmp.getName()));
            tvYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    startNextNavigation(false);
                }
            });

            tvNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();
            final int width = (int) getResources().getDimension(R.dimen._260sdp);
            alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

    }

    public void drawRoute() {
        final List<Point> destination = new ArrayList<>();
//        if (recommendedPlanModel.getVisitOrderArrayList() != null && recommendedPlanModel.getVisitOrderArrayList().size() > 0) {
//            for (int i = 0; i < recommendedPlanModel.getVisitOrderArrayList().size(); i++) {
//                final VisitOrder visitOrder = recommendedPlanModel.getVisitOrderArrayList().get(i);
//                for (int j = 0; j < exploreZooArrayList.size(); j++) {
//                    final ExploreZoo exploreZoo = exploreZooArrayList.get(j);
//                    if (exploreZoo.getId().equals(visitOrder.getId())) {
//
//                        final Double latitude = Double.parseDouble(exploreZoo.getLatitude());
//                        final Double longitude = Double.parseDouble(exploreZoo.getLongitude());
//                        Log.e("ID", exploreZoo.getId() + " visitorder" + visitOrder.getId());
//                        final int visiteItem = AlainZooDB.getInstance().myPlaneVisitedItemDao().isVisited(exploreZoo.getId(), exploreZoo.getType());
//                        if (visiteItem == 0) {
//                            destination.add(Point.fromLngLat(longitude, latitude));
//                        }
//                    }
//                }
//            }
//        } else {
        final ArrayList<String> sortedIdList = new ArrayList<>();
        for (final ExploreZoo exploreZoo : exploreZooArrayList) {
            final Double latitude = Double.parseDouble(exploreZoo.getLatitude());
            final Double longitude = Double.parseDouble(exploreZoo.getLongitude());
            final int visiteItem = AlainZooDB.getInstance().myPlaneVisitedItemDao().isVisited(exploreZoo.getId(), exploreZoo.getType());
            if (visiteItem == 0) {
                if (!sortedIdList.contains(String.valueOf(exploreZoo.getId()))) {
                    sortedIdList.add(String.valueOf(exploreZoo.getId()));
                    destination.add(Point.fromLngLat(longitude, latitude));
                }
            }
        }
//        }
        if (mapboxMap.getMyLocation() != null) {
            final Location location = mapboxMap.getMyLocation();

            final LatLng latLngZoo = new LatLng(24.179517, 55.739580);
            final LatLng latLngDestination = new LatLng(location.getLatitude(), location.getLongitude());
            final float distance = (int) (latLngZoo.distanceTo(latLngDestination));
            if (distance <= 5000) {
                if (destination.size() > 0) {
                    getRoute(Point.fromLngLat(location.getLongitude(), location.getLatitude()), destination);
                }
            } else if (destination.size() > 1) {
                final Point point = Point.fromLngLat(destination.get(0).longitude(), destination.get(0).latitude());
                destination.remove(0);
                getRoute(point, destination);
            } else {
                SnackbarUtils.loadSnackBar(getString(R.string.no_location_available), getActivity(), R.color.reddish);
            }
        } else {
            if (destination.size() > 1) {
                final Point point = Point.fromLngLat(destination.get(0).longitude(), destination.get(0).latitude());
                destination.remove(0);
                getRoute(point, destination);
            } else {
                SnackbarUtils.loadSnackBar(getString(R.string.no_location_available), getActivity(), R.color.reddish);
            }
        }
    }

    private void getRoute(final Point origin, List<Point> destination) {
        final NavigationRoute.Builder navigationRoute = NavigationRoute.builder();
        navigationRoute.accessToken(Mapbox.getAccessToken());
        navigationRoute.origin(origin);
        navigationRoute.profile(DirectionsCriteria.PROFILE_WALKING);

        for (int i = 0; i < destination.size(); i++) {
            if (i == (destination.size() - 1)) {
                navigationRoute.destination(destination.get(i));
            } else {
                navigationRoute.addWaypoint(destination.get(i));
            }
        }
        navigationRoute.build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            SnackbarUtils.loadSnackBar(getString(R.string.no_route_found), getActivity(), R.color.reddish);
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            SnackbarUtils.loadSnackBar(getString(R.string.no_route_found), getActivity(), R.color.reddish);
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, exploreZooPlanBinding.mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
//                        navigationMapRoute.setOnRouteSelectionChangeListener(new OnRouteSelectionChangeListener() {
//                            @Override
//                            public void onNewPrimaryRouteSelected(DirectionsRoute directionsRoute) {
//                                Log.e("Click", "Yes");
//                                startNavigationPlan();
//
//                            }
//                        });
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {
            // Create an instance of LOST location engine
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(exploreZooPlanBinding.mapView, mapboxMap, locationEngine);
            locationPlugin.setLocationLayerEnabled(LocationLayerMode.NONE);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void initializeLocationEngine() {
        locationEngine = new LostLocationEngine(getActivity());
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void setCameraPosition(Location location) {
//        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                new LatLng(location.getLatitude(), location.getLongitude()), 16));
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
            //finish();
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            originLocation = location;
            setCameraPosition(location);
            locationEngine.removeLocationEngineListener(this);
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
        exploreZooPlanBinding.mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("MAP", "onStart");
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
        exploreZooPlanBinding.mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        exploreZooPlanBinding.mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("MAP", "onPause");
        exploreZooPlanBinding.mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("MAP", "onResume");
        exploreZooPlanBinding.mapView.onResume();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e("MAP", "onLowMemory");
        exploreZooPlanBinding.mapView.onLowMemory();
    }
}
