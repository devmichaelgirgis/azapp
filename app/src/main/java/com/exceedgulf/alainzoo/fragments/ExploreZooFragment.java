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
import com.exceedgulf.alainzoo.database.models.ExploreMap;
import com.exceedgulf.alainzoo.database.models.ExploreZoo;
import com.exceedgulf.alainzoo.database.models.MyPlaneVisitedItem;
import com.exceedgulf.alainzoo.databinding.FragmentExploreZooBinding;
import com.exceedgulf.alainzoo.managers.ExploreMapManager;
import com.exceedgulf.alainzoo.utils.CustomSimpleTarget;
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
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by R.S. on 19/12/17
 */
public class ExploreZooFragment extends BaseFragment implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks, MapboxMap.OnInfoWindowClickListener, LocationEngineListener, PermissionsListener, InfoMapAdapter.onSeeMoreListener {
    private static final int RC_LOCATION = 1000;
    private static final String ANIMALS = "animals";
    private static final String EXPERIENCE = "experience";
    private static final String VENUE = "venue";
    private static final String ATTRACTION = "attraction";
    private FragmentExploreZooBinding fragmentExploreZooBinding;
    private Bundle bundle;
    private MapboxMap mapboxMap;
    // variables for adding location layer
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private Location originLocation;
    private String TAG = ExploreZooFragment.class.getSimpleName();
    private ArrayList<ExploreZoo> exploreZooArrayListTmp;
    private ArrayList<ExploreZoo> exploreZooArrayList;
    private ArrayList<Marker> markerArrayList;
    private ArrayList<String> arrSelectedFilter;
    private InfoMapAdapter infoMapAdapter;
    private ExploreZoo exploreZoo;
    private Marker marker;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        bundle = savedInstanceState;
        fragmentExploreZooBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_explore_zoo, container, false);
        return fragmentExploreZooBinding.getRoot();
    }

    @Override
    public void initView(final View view) {
        exploreZooArrayList = new ArrayList<>();
        exploreZooArrayListTmp = new ArrayList<>();
        markerArrayList = new ArrayList<>();
        arrSelectedFilter = new ArrayList<>();
        arrSelectedFilter.add(ANIMALS);
        arrSelectedFilter.add(EXPERIENCE);
        arrSelectedFilter.add(VENUE);
        arrSelectedFilter.add(ATTRACTION);
        fragmentExploreZooBinding.frExplAnimals.setSelected(true);
        fragmentExploreZooBinding.frExplExperience.setSelected(true);
        fragmentExploreZooBinding.frExplRestaurant.setSelected(true);
        select(fragmentExploreZooBinding.frExplAnimals);
        select(fragmentExploreZooBinding.frExplExperience);
        select(fragmentExploreZooBinding.frExplRestaurant);
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_explore_zoo), getResources().getColor(R.color.reddish), null, true);
        fragmentExploreZooBinding.expandableLayout.collapse();
        fragmentExploreZooBinding.mapView.setStyleUrl(getString(R.string.map_style));
        fragmentExploreZooBinding.frExplAnimals.setOnClickListener(this);
        fragmentExploreZooBinding.frExplExperience.setOnClickListener(this);
        fragmentExploreZooBinding.frExplRestaurant.setOnClickListener(this);
        fragmentExploreZooBinding.frExplTransport.setOnClickListener(this);
        fragmentExploreZooBinding.mapView.getMapAsync(this);
        fragmentExploreZooBinding.frExplBtnGpsCenter.setOnClickListener(this);

    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {
            // Create an instance of LOST location engine
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(fragmentExploreZooBinding.mapView, mapboxMap, locationEngine);
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

        final Location lastLocation = locationEngine.getLastLocation();
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
        fragmentExploreZooBinding.mapView.onStart();
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
        fragmentExploreZooBinding.mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentExploreZooBinding.mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).selectedBottomMenuPosition = 1;
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_explore_zoo), getResources().getColor(R.color.reddish), null, true);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.explorezoo, menu);
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
        if (id == R.id.action_explore_search) {

            ((HomeActivity) getActivity()).addFragment(((HomeActivity) getActivity()).getcurrentFragment(), new SearchFragment());

        } else if (id == R.id.action_explore_setings) {
            if (fragmentExploreZooBinding.expandableLayout.isExpanded()) {
                fragmentExploreZooBinding.expandableLayout.collapse();
            } else {
                fragmentExploreZooBinding.expandableLayout.expand();
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
        if (v == fragmentExploreZooBinding.frExplAnimals) {
            if (fragmentExploreZooBinding.expandableLayout.isExpanded()) {
                fragmentExploreZooBinding.expandableLayout.collapse();
            }
            if (mapboxMap != null) {
                mapboxMap.clear();
            }
            if (!arrSelectedFilter.contains(ANIMALS)) {
                arrSelectedFilter.add(ANIMALS);
                fragmentExploreZooBinding.frExplAnimals.setSelected(true);
                select(v);
            } else {
                if (arrSelectedFilter.size() > 2) {
                    arrSelectedFilter.remove(ANIMALS);
                    fragmentExploreZooBinding.frExplAnimals.setSelected(false);
                    select(v);
                } else {
                    fragmentExploreZooBinding.frExplAnimals.setSelected(true);
                    select(v);
                }
            }
            switchSelections();
        }
        if (v == fragmentExploreZooBinding.frExplExperience) {
            if (fragmentExploreZooBinding.expandableLayout.isExpanded()) {
                fragmentExploreZooBinding.expandableLayout.collapse();
            }
            if (mapboxMap != null) {
                mapboxMap.clear();
            }
            select(v);
            if (!arrSelectedFilter.contains(EXPERIENCE)) {
                arrSelectedFilter.add(EXPERIENCE);
                fragmentExploreZooBinding.frExplExperience.setSelected(true);
                select(v);
            } else {
                if (arrSelectedFilter.size() > 2) {
                    arrSelectedFilter.remove(EXPERIENCE);
                    fragmentExploreZooBinding.frExplExperience.setSelected(false);
                    select(v);
                } else {
                    fragmentExploreZooBinding.frExplExperience.setSelected(true);
                    select(v);
                }
            }
            switchSelections();
        }

        if (v == fragmentExploreZooBinding.frExplRestaurant) {
            if (fragmentExploreZooBinding.expandableLayout.isExpanded()) {
                fragmentExploreZooBinding.expandableLayout.collapse();
            }
            if (mapboxMap != null) {
                mapboxMap.clear();
            }
            select(v);
            if (!arrSelectedFilter.contains(VENUE)) {
                arrSelectedFilter.add(VENUE);
                fragmentExploreZooBinding.frExplRestaurant.setSelected(true);
                select(v);
            } else {
                if (arrSelectedFilter.size() > 2) {
                    arrSelectedFilter.remove(VENUE);
                    fragmentExploreZooBinding.frExplRestaurant.setSelected(false);
                    select(v);
                } else {
                    fragmentExploreZooBinding.frExplRestaurant.setSelected(true);
                    select(v);
                }
            }
            switchSelections();
        }
        if (v == fragmentExploreZooBinding.frExplBtnGpsCenter) {
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

    private void switchSelections() {
        if (mapboxMap != null) {
            mapboxMap.clear();
            exploreZooArrayListTmp.clear();
            exploreZooArrayList.clear();
            markerArrayList.clear();
        }
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        for (int i = 0; i < arrSelectedFilter.size(); i++) {
            ExploreMapManager.getExploreMapManager().getAllExploreZoo(null, "", arrSelectedFilter.get(i), new ApiCallback() {
                @Override
                public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                    if (isAdded()) {
                        exploreZooArrayListTmp.addAll(resultList);
                    }
                }

                @Override
                public void onFaild(String message) {

                }
            });
        }
        loadGeometry(exploreZooArrayListTmp);
        DisplayDialog.getInstance().dismissProgressDialog();
    }

    public void select(View view) {
        final Typeface fontRegular = Typeface.createFromAsset(getContext().getAssets(), getString(R.string.avenir_next_regular));
        final Typeface fontBold = Typeface.createFromAsset(getContext().getAssets(), getString(R.string.avenir_next_demi_bold));
        if (view == fragmentExploreZooBinding.frExplAnimals) {
            if (fragmentExploreZooBinding.frExplAnimals.isSelected()) {
                fragmentExploreZooBinding.frExplAnimals.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_explore_zoo_filter));
                fragmentExploreZooBinding.frExplTvAnimal.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                fragmentExploreZooBinding.frExplTvAnimal.setTypeface(fontBold);
                fragmentExploreZooBinding.frExplAnimal.setImageResource(R.drawable.ic_show_selected_animal);
            } else {
                fragmentExploreZooBinding.frExplAnimal.setImageResource(R.drawable.ic_show_animal);
                fragmentExploreZooBinding.frExplAnimals.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.reddish));
                fragmentExploreZooBinding.frExplTvAnimal.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorexplorezoofilter));
                fragmentExploreZooBinding.frExplTvAnimal.setTypeface(fontRegular);
            }

        } else if (view == fragmentExploreZooBinding.frExplExperience) {
            if (fragmentExploreZooBinding.frExplExperience.isSelected()) {
                fragmentExploreZooBinding.frExplExperience.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_explore_zoo_filter));
                fragmentExploreZooBinding.frExplTvExperience.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                fragmentExploreZooBinding.frExplTvExperience.setTypeface(fontBold);
                fragmentExploreZooBinding.frExplIvExperience.setImageResource(R.drawable.ic_show_selected_experience);
            } else {
                fragmentExploreZooBinding.frExplIvExperience.setImageResource(R.drawable.ic_show_experience);
                fragmentExploreZooBinding.frExplExperience.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.reddish));
                fragmentExploreZooBinding.frExplTvExperience.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorexplorezoofilter));
                fragmentExploreZooBinding.frExplTvExperience.setTypeface(fontRegular);
            }

        } else if (view == fragmentExploreZooBinding.frExplRestaurant) {
            if (fragmentExploreZooBinding.frExplRestaurant.isSelected()) {
                fragmentExploreZooBinding.frExplRestaurant.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_explore_zoo_filter));
                fragmentExploreZooBinding.frExplTvRestaurant.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                fragmentExploreZooBinding.frExplTvRestaurant.setTypeface(fontBold);
                fragmentExploreZooBinding.frExplIvRestaurant.setImageResource(R.drawable.ic_show_selected_restaurant);
            } else {
                fragmentExploreZooBinding.frExplIvRestaurant.setImageResource(R.drawable.ic_show_restaurant);
                fragmentExploreZooBinding.frExplRestaurant.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.reddish));
                fragmentExploreZooBinding.frExplTvRestaurant.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorexplorezoofilter));
                fragmentExploreZooBinding.frExplTvRestaurant.setTypeface(fontRegular);
            }

        } else if (view == fragmentExploreZooBinding.frExplTransport) {
            if (fragmentExploreZooBinding.frExplTransport.isSelected()) {
                fragmentExploreZooBinding.frExplTransport.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_explore_zoo_filter));
                fragmentExploreZooBinding.frExplTvTransport.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                fragmentExploreZooBinding.frExplTvTransport.setTypeface(fontBold);
                fragmentExploreZooBinding.frExplIvTransport.setImageResource(R.drawable.ic_show_selected_transport);
            } else {
                fragmentExploreZooBinding.frExplIvTransport.setImageResource(R.drawable.ic_show_transpot);
                fragmentExploreZooBinding.frExplTransport.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.reddish));
                fragmentExploreZooBinding.frExplTvTransport.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorexplorezoofilter));
                fragmentExploreZooBinding.frExplTvTransport.setTypeface(fontRegular);
            }

        }
    }

    public void getFeaturesList() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        ExploreMapManager.getExploreMapManager().getAllExploreZoo(null, "", "", new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    switchSelections();
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
            }
        });
    }

    private void loadGeometry(ArrayList<ExploreZoo> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            final ExploreZoo exploreZoo = arrayList.get(i);
            if (!TextUtils.isEmpty(exploreZoo.getLatitude()) && !TextUtils.isEmpty(exploreZoo.getLongitude())) {
//                if (!TextUtils.isEmpty(exploreZoo.getIcon())) {
//                    Glide.with(getActivity())
//                            .asBitmap()
//                            .load(exploreZoo.getIcon())
//                            .into(new CustomSimpleTarget(getActivity(), markerArrayList, i, exploreZoo, mapboxMap));
//                } else {
                final MarkerOptions markerOptions = new com.mapbox.mapboxsdk.annotations.MarkerOptions()
                        .position(new com.mapbox.mapboxsdk.geometry.LatLng(Double.parseDouble(exploreZoo.getLatitude()), Double.parseDouble(exploreZoo.getLongitude())))
                        .setSnippet(String.valueOf(i));
                exploreZooArrayList.add(exploreZoo);
                markerArrayList.add(mapboxMap.addMarker(markerOptions));
//                }
            }

        }
        updateMarkerImage();
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
                                    if (isAdded()) {
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
                                }
                            });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MarkerException", e.getMessage());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("MAP", "onPause");
        fragmentExploreZooBinding.mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("MAP", "onResume");
        fragmentExploreZooBinding.mapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e("MAP", "onLowMemory");
        fragmentExploreZooBinding.mapView.onLowMemory();
    }

    @Override
    public void onMapReady(final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        this.mapboxMap.setMaxZoomPreference(16);
        this.mapboxMap.setMinZoomPreference(14);
        this.mapboxMap.setMyLocationEnabled(true);
        enableLocationPlugin();
        CameraPosition position = new CameraPosition.Builder()
                .target(new com.mapbox.mapboxsdk.geometry.LatLng(24.179517, 55.739580)) // Sets the new camera position
                .zoom(16)// Sets the zoom
                .bearing(180)
                .tilt(30) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        this.mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 2000);


        this.mapboxMap.setOnInfoWindowClickListener(this);
        this.mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                fragmentExploreZooBinding.expandableLayout.collapse();
                return false;
            }
        });
        getFeaturesList();
        infoMapAdapter = new InfoMapAdapter(getActivity(), exploreZooArrayList, markerArrayList, mapboxMap, this, null);
        mapboxMap.setInfoWindowAdapter(infoMapAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        //EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //fragmentExploreZooBinding.mapView.getMapAsync(this);
        // getFeaturesList();
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
            intent.putExtra("Name", exploreZoo.getName());
            intent.putExtra("Origin", origin);
            intent.putExtra("Dest", dest);
            startActivityForResult(intent, 106);
        } else {
            SnackbarUtils.loadSnackBar(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_), getActivity(), R.color.reddish);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 106 && data != null) {
            createNavigationFinishDialog();
            AppAlainzoo.getAppAlainzoo().setCurrentRoute(null);
        }
    }

    private void createNavigationFinishDialog() {
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

    @Override
    public void reRouteDraw(Point origin, Point dest) {
        getRoute(origin, dest);
    }

    private void getRoute(final Point origin, Point destination) {
        final NavigationRoute.Builder navigationRoute = NavigationRoute.builder();
        navigationRoute.accessToken(Mapbox.getAccessToken());
        navigationRoute.origin(origin);
        navigationRoute.profile(DirectionsCriteria.PROFILE_WALKING);
        navigationRoute.destination(destination);
        navigationRoute.build().
                getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse
                            (Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
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
                            navigationMapRoute = new NavigationMapRoute(null, fragmentExploreZooBinding.mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

}
