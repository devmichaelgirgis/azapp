package com.exceedgulf.alainzoo.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.databinding.FragmentContactUsMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUsMapFragment extends BaseFragment implements OnMapReadyCallback {

    private FragmentContactUsMapBinding mapBinding;
    private SupportMapFragment mapFragment;
    private LatLng destination;

    public ContactUsMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_us_map, container, false);
        return mapBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.application_name), getResources().getColor(R.color.very_light_brown), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar) ,false);
        destination = new LatLng(24.179248, 55.739554);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frContact_map);
        mapFragment.getMapAsync(this);
        mapBinding.frContactTvGo.setOnClickListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.application_name), getResources().getColor(R.color.very_light_brown), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar),false);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.getUiSettings().setMyLocationButtonEnabled(false);
        MapsInitializer.initialize(getActivity());
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                getActivity(), R.raw.style_json));
        map.addMarker(new MarkerOptions()
                .title(getString(R.string.application_name))
//                        .icon(VectorDrawableCompat.create(getResources(),R.drawable.ic_marker,getActivity().getTheme()))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                .position(destination));

        final CameraPosition cameraPosition = CameraPosition.builder()
                .target(destination)
                .zoom(16)
                .bearing(0)
                .build();

//        // Animate the change in camera view over 2 seconds
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                marker.hideInfoWindow();
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == mapBinding.frContactTvGo) {
            final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        //BaseFragment.this.location = location;
                        Log.v("Location Changed", location.getLatitude() + " and " + location.getLongitude());
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            final Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Intent intent;
            if (location != null) {
                intent = new Intent(
                        Intent.ACTION_VIEW, Uri
                        .parse("http://maps.google.com/maps?saddr="
                                + location.getLatitude() + ","
                                + location.getLongitude()
                                + "&daddr="
                                + "24.1791918"
                                + ","
                                + "55.7393857"));
                intent.setClassName("com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity");
            } else {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=loc:" + "24.1791918" + "," + "55.7393857" + " ()"));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            }
            startActivity(intent);
        }

    }
}
