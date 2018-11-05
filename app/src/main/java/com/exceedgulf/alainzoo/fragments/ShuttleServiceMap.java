package com.exceedgulf.alainzoo.fragments;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.databinding.FragmentShuttleServiceMapBinding;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.utils.Utils;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.services.android.location.LostLocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;

import java.util.List;

/**
 * Created by Paras Ghasadiya on 25/01/18.
 */

public class ShuttleServiceMap extends BaseFragment implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {
    private FragmentShuttleServiceMapBinding serviceMapBinding;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private Location originLocation;
    private String shuttleCode = "";
    private AlertDialog alertDialog;
    private boolean isDialogNeedtoShow = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        serviceMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_shuttle_service_map, container, false);
        return serviceMapBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.shuttle_service), getResources().getColor(R.color.cool_blue), null, false);
        serviceMapBinding.mapView.setStyleUrl(getString(R.string.map_style));
        showShuttlePurchaseDialog();
        serviceMapBinding.frshuttelmapTvRequest.setOnClickListener(this);
        serviceMapBinding.mapView.getMapAsync(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (alertDialog != null && !alertDialog.isShowing() && !isDialogNeedtoShow) {
                alertDialog.show();
            }
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.shuttle_service), getResources().getColor(R.color.cool_blue), null, false);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == serviceMapBinding.frshuttelmapTvRequest) {
            if (originLocation != null) {
                final Bundle bundle = new Bundle();
                bundle.putDouble(ShuttleServiceFragment.KEY_LATITUDE, originLocation.getLatitude());
                bundle.putDouble(ShuttleServiceFragment.KEY_LONGITUDE, originLocation.getLongitude());
                final ShuttleServiceFragment shuttleServiceFragment = new ShuttleServiceFragment();
                shuttleServiceFragment.setArguments(bundle);
                ((HomeActivity) getActivity()).addFragment(ShuttleServiceMap.this, shuttleServiceFragment);
            } else {
                SnackbarUtils.loadSnackBar(getString(R.string.no_location_available), getActivity(), R.color.cool_blue);
            }
        }
    }

    private void showShuttlePurchaseDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_shuttle_map, null);
        final EditText edtShuttleCode = dialogView.findViewById(R.id.dialog_shuttle_edtShuttleCode);
        final TextView tvihaveit = dialogView.findViewById(R.id.dialog_shuttle_tvIhaveIt);
        final TextView tviDonthaveit = dialogView.findViewById(R.id.dialog_shuttle_tvIDonthaveIt);
        final AppCompatImageView ivClose = dialogView.findViewById(R.id.dialog_shuttle_ivClose);

        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.dialog_black)));
        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK &&
                        event.getAction() == KeyEvent.ACTION_UP &&
                        !event.isCanceled()) {
                    dialog.cancel();
                    getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
                ((HomeActivity) getActivity()).replaceFragment(new HomeFragment());
            }
        });

        tvihaveit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strCode = edtShuttleCode.getText().toString();

                if (!TextUtils.isEmpty(strCode) && strCode.length() == 5) {
                    if ((strCode.toLowerCase().startsWith("sh")) && TextUtils.isDigitsOnly(strCode.substring(strCode.length() - 3, strCode.length()))) {
                        alertDialog.cancel();
                        isDialogNeedtoShow = true;

                        if (originLocation != null) {
                            final Bundle bundle = new Bundle();
                            bundle.putDouble(ShuttleServiceFragment.KEY_LATITUDE, originLocation.getLatitude());
                            bundle.putDouble(ShuttleServiceFragment.KEY_LONGITUDE, originLocation.getLongitude());
                            final ShuttleServiceFragment shuttleServiceFragment = new ShuttleServiceFragment();
                            shuttleServiceFragment.setArguments(bundle);
                            ((HomeActivity) getActivity()).addFragment(ShuttleServiceMap.this, shuttleServiceFragment);
                        } else {
                            SnackbarUtils.loadSnackBar(getString(R.string.no_location_available), getActivity(), R.color.cool_blue);
                        }

                    } else {
                        SnackbarUtils.loadSnackBar(dialogView, getString(R.string.please_enter_valid_code), getActivity(), R.color.cool_blue);
                    }
                } else {
                    SnackbarUtils.loadSnackBar(dialogView, getString(R.string.please_enter_shuttle_code), getActivity(), R.color.cool_blue);
                }
            }
        });
        edtShuttleCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Utils.getInstance().hideSoftKeyboard(getActivity());
                    final String strCode = edtShuttleCode.getText().toString();

                    if (!TextUtils.isEmpty(strCode) && strCode.length() == 5) {
                        if ((strCode.toLowerCase().startsWith("sh")) && TextUtils.isDigitsOnly(strCode.substring(strCode.length() - 3, strCode.length()))) {
                            shuttleCode = edtShuttleCode.getText().toString().trim();
                            alertDialog.cancel();
                            isDialogNeedtoShow = true;

                            if (originLocation != null) {
                                final Bundle bundle = new Bundle();
                                bundle.putDouble(ShuttleServiceFragment.KEY_LATITUDE, originLocation.getLatitude());
                                bundle.putDouble(ShuttleServiceFragment.KEY_LONGITUDE, originLocation.getLongitude());
                                final ShuttleServiceFragment shuttleServiceFragment = new ShuttleServiceFragment();
                                shuttleServiceFragment.setArguments(bundle);
                                ((HomeActivity) getActivity()).addFragment(ShuttleServiceMap.this, shuttleServiceFragment);
                            } else {
                                SnackbarUtils.loadSnackBar(getString(R.string.no_location_available), getActivity(), R.color.cool_blue);
                            }

                        } else {
                            SnackbarUtils.loadSnackBar(dialogView, getString(R.string.please_enter_valid_code), getActivity(), R.color.cool_blue);
                        }
                    } else {
                        SnackbarUtils.loadSnackBar(dialogView, getString(R.string.please_enter_shuttle_code), getActivity(), R.color.cool_blue);
                    }
                }
                return false;
            }
        });

        tviDonthaveit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
                createInfoDialog();
            }
        });
    }

    private void createInfoDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_form_response, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView tvTitle = dialogView.findViewById(R.id.dialog_form_response_tvTitle);
        final TextView tvMessage = dialogView.findViewById(R.id.dialog_form_response_tvMessage);
        final TextView tvOkay = dialogView.findViewById(R.id.dialog_form_response_tvOkay);

        tvTitle.setText(getString(R.string.shuttle_service));
        tvMessage.setText(getString(R.string.dialog_shuttle_service_info_message));
        tvOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                ((HomeActivity) getActivity()).addFragment(ShuttleServiceMap.this, new VisiterServicesFragment());
            }
        });
        alertDialog.show();
        final int width = (int) getResources().getDimension(R.dimen._250sdp);
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

    }


    @SuppressWarnings({"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {
            // Create an instance of LOST location engine
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(serviceMapBinding.mapView, mapboxMap, locationEngine);
            locationPlugin.setLocationLayerEnabled(LocationLayerMode.COMPASS);
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
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 16));
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
        serviceMapBinding.mapView.onStart();
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
        serviceMapBinding.mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        serviceMapBinding.mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("MAP", "onPause");
        serviceMapBinding.mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("MAP", "onResume");
        serviceMapBinding.mapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e("MAP", "onLowMemory");
        serviceMapBinding.mapView.onLowMemory();
    }

    @Override
    public void onMapReady(final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        this.mapboxMap.setMaxZoomPreference(16);
        this.mapboxMap.setMinZoomPreference(14);
        enableLocationPlugin();
        CameraPosition position = new CameraPosition.Builder()
                .target(new com.mapbox.mapboxsdk.geometry.LatLng(24.179517, 55.739580)) // Sets the new camera position
                .zoom(16)// Sets the zoom
                .bearing(180) // Rotate the camera
                .tilt(30) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        this.mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 2000);
        this.mapboxMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
