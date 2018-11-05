package com.exceedgulf.alainzoo.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.BuildConfig;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.databinding.FragmentPreviewBinding;
import com.exceedgulf.alainzoo.databinding.FragmentRememberParkBinding;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.GenericFileProvider;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by P.P on 20/01/2018
 */
public class RememberParkFragment extends BaseFragment implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 105;
    private static final int GPS_REQUEST = 108;
    private FragmentRememberParkBinding rememberParkBinding;
    private SupportMapFragment supportMapFragment;
    private GoogleMap map;
    private String strLocation;
    private boolean isGPSON = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rememberParkBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_remember_park, container, false);
        return rememberParkBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.parking), getResources().getColor(R.color.snot), null, true);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        rememberParkBinding.fragMyParkBtnGoto.setOnClickListener(this);
        rememberParkBinding.fragMyParkBtnSave.setOnClickListener(this);
        rememberParkBinding.fragMyParkLlSave.setOnClickListener(this);
        rememberParkBinding.fragMyParkLlDelete.setOnClickListener(this);
        rememberParkBinding.fragMyParkBtnYes.setOnClickListener(this);
        rememberParkBinding.fragMyParkBtnDismiss.setOnClickListener(this);
        supportMapFragment.getMapAsync(RememberParkFragment.this);
        strLocation = SharedPrefceHelper.getInstance().get(PrefCons.PARKING_LOCATION, "");

        if (!TextUtils.isEmpty(strLocation.trim())) {
            rememberParkBinding.fragMyParkBtnSave.setText(getString(R.string.delete));
            rememberParkBinding.fragMyParkBtnGoto.setVisibility(View.VISIBLE);
        } else {
            rememberParkBinding.fragMyParkBtnGoto.setVisibility(View.GONE);
            rememberParkBinding.fragMyParkBtnSave.setText(getString(R.string.save_my_parking));
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.parking), getResources().getColor(R.color.snot), null, true);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(getActivity());

        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (!TextUtils.isEmpty(strLocation)) {
                    map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(strLocation.split(",")[0]), Double.parseDouble(strLocation.split(",")[1]))));
                    map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(strLocation.split(",")[0]), Double.parseDouble(strLocation.split(",")[1]))));
                    final CameraPosition cameraPosition = CameraPosition.builder()
                            .target(new LatLng(Double.parseDouble(strLocation.split(",")[0]), Double.parseDouble(strLocation.split(",")[1])))
                            .zoom(20)
                            .bearing(0)
                            .build();

                    // Animate the change in camera view over 2 seconds
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                            2000, null);
                } else {
                    final Location location = map.getMyLocation();
                    if (location != null) {
                        final CameraPosition cameraPosition = CameraPosition.builder()
                                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                .zoom(20)
                                .bearing(0)
                                .build();

                        // Animate the change in camera view over 2 seconds
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                                2000, null);
                    }
                }
            }
        });

        map.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {
                map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
                //map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                final CameraPosition cameraPosition = CameraPosition.builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .zoom(20)
                        .bearing(0)
                        .build();

                // Animate the change in camera view over 2 seconds
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                        2000, null);
            }
        });

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == rememberParkBinding.fragMyParkBtnGoto) {
            strLocation = SharedPrefceHelper.getInstance().get(PrefCons.PARKING_LOCATION, "");
            if (!TextUtils.isEmpty(strLocation)) {
                final Uri gmmIntentUri = Uri.parse("google.navigation:q=" + strLocation.split(",")[0] + "," + strLocation.split(",")[1]);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Uri gmmIntentUri1 = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + strLocation.split(",")[0] + "," + strLocation.split(",")[1]);
                    Intent mapIntent1 = new Intent(Intent.ACTION_VIEW, gmmIntentUri1);
                    startActivity(mapIntent1);
                }
            }
        } else if (v == rememberParkBinding.fragMyParkBtnSave) {
            if (rememberParkBinding.fragMyParkBtnSave.getText().toString().equalsIgnoreCase(getString(R.string.save_my_parking))) {
                askToTurnGPS();
            } else {
                rememberParkBinding.fragMyParkLlSave.setVisibility(View.GONE);
                rememberParkBinding.fragMyParkLlDelete.setVisibility(View.VISIBLE);
            }
        } else if (v == rememberParkBinding.fragMyParkBtnYes) {
            map.clear();
            rememberParkBinding.fragMyParkBtnSave.setText(getString(R.string.save_my_parking));
            rememberParkBinding.fragMyParkBtnGoto.setVisibility(View.GONE);
            SharedPrefceHelper.getInstance().delete(PrefCons.PARKING_LOCATION);
            rememberParkBinding.fragMyParkLlSave.setVisibility(View.VISIBLE);
            rememberParkBinding.fragMyParkLlDelete.setVisibility(View.GONE);
        } else if (v == rememberParkBinding.fragMyParkBtnDismiss) {
            rememberParkBinding.fragMyParkLlSave.setVisibility(View.VISIBLE);
            rememberParkBinding.fragMyParkLlDelete.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(((HomeActivity) getActivity()).getmLocationRequest());

            final SettingsClient client = LocationServices.getSettingsClient(getActivity());

            final Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    // ...
                    isGPSON = true;
                }
            });

            task.addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            isGPSON = false;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(getActivity(),
                                    106);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });
        }
    }


    private void askToTurnGPS() {
        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(((HomeActivity) getActivity()).getmLocationRequest());
        final SettingsClient client = LocationServices.getSettingsClient(getActivity());
        final Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                isGPSON = true;
                final Location location = map.getMyLocation();
                if (location != null) {
                    final CameraPosition cameraPosition = CameraPosition.builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))
                            .zoom(20)
                            .bearing(0)
                            .build();

                    // Animate the change in camera view over 2 seconds
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                            2000, null);
                    map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
                    SharedPrefceHelper.getInstance().save(PrefCons.PARKING_LOCATION, location.getLatitude() + "," + location.getLongitude());
                    rememberParkBinding.fragMyParkBtnSave.setText(getString(R.string.delete));
                    rememberParkBinding.fragMyParkBtnGoto.setVisibility(View.VISIBLE);
                } else {
                    SnackbarUtils.loadSnackBar(getString(R.string.no_location_available), getActivity(), R.color.snot);
                }
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        isGPSON = false;
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                106);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }
}
