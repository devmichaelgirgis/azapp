package com.exceedgulf.alainzoo.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.databinding.ActivityNavigationViewBinding;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.feedback.FeedbackItem;
import com.mapbox.services.android.navigation.ui.v5.instruction.InstructionView;
import com.mapbox.services.android.navigation.ui.v5.listeners.FeedbackListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigationOptions;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.mapbox.services.android.navigation.v5.utils.RouteUtils;
import com.mapbox.services.api.directions.v5.DirectionsCriteria;

// classes needed to initialize map
// classes needed to add location layer
// classes needed to add a marker
// classes to calculate a route
// classes needed to launch navigation UI

/**
 * Created by P.P on 08/02/18.
 */
public class NavigationActivity extends BaseActivity implements OnNavigationReadyCallback, ProgressChangeListener, FeedbackListener {

    private static final String TAG = "DirectionsActivity";
    // variables for calculating and drawing a route
    private Point originPosition;
    private Point destinationPosition;
    private DirectionsRoute directionsRoute;
    private boolean directionsRouteData;
    private ActivityNavigationViewBinding navigationViewBinding;
    private String placeName;
    private boolean isArrived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, Constants.MAPBOX_KEY);
        final NotificationManager notificationManager = (NotificationManager) NavigationActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        navigationViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_navigation_view);
        navigationViewBinding.mapNavigationView.onCreate(savedInstanceState);
        final InstructionView instructionView = navigationViewBinding.mapNavigationView.findViewById(R.id.instructionView);
        if (instructionView != null && !instructionView.isMuted) {
            instructionView.toggleMute();
        }
        final Intent intent = getIntent();
        if (intent != null) {
            final Bundle bundle = intent.getExtras();
            if (bundle != null && bundle.containsKey("Route")) {
                directionsRouteData = bundle.getBoolean("Route", false);
                placeName = bundle.getString("Name");
                directionsRoute = AppAlainzoo.getAppAlainzoo().getCurrentRoute();
            } else if (bundle != null && bundle.containsKey("Origin")) {
                originPosition = (Point) bundle.getSerializable("Origin");
                placeName = bundle.getString("Name");
                destinationPosition = (Point) bundle.getSerializable("Dest");
            }
        }
        navigationViewBinding.mapNavigationView.getNavigationAsync(this);
        navigationViewBinding.mapNavigationView.findViewById(R.id.feedbackFab).setVisibility(View.GONE);
        navigationViewBinding.mapNavigationView.findViewById(R.id.soundFab).setVisibility(View.GONE);
        navigationViewBinding.mapNavigationView.findViewById(R.id.soundText).setVisibility(View.GONE);
        navigationViewBinding.actNavTvPlaceName.setText(placeName);
        navigationViewBinding.actNavEndNavigation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == navigationViewBinding.actNavEndNavigation) {
            final Intent intent = new Intent();
            intent.putExtra("isFinished", true);
            setResult(102, intent);
            finish();
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        navigationViewBinding.mapNavigationView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        navigationViewBinding.mapNavigationView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navigationViewBinding.mapNavigationView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationViewBinding.mapNavigationView.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationViewBinding.mapNavigationView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        navigationViewBinding.mapNavigationView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        navigationViewBinding.mapNavigationView.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationViewBinding.mapNavigationView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        navigationViewBinding.mapNavigationView.onBackPressed();
        //if (isArrived) {
        final Intent intent = new Intent();
        intent.putExtra("isFinished", true);
        setResult(102, intent);
        finish();
//        } else {
//            super.onBackPressed();
//        }
    }

    @Override
    public void onNavigationReady() {
        final MapboxNavigationOptions optionsMap = MapboxNavigationOptions.builder()
                .enableNotification(false)
                .snapToRoute(false)
                .build();
        if (directionsRouteData && directionsRoute != null) {
            NavigationViewOptions options = NavigationViewOptions.builder()
                    .directionsRoute(directionsRoute)
                    .navigationOptions(optionsMap)
                    .navigationListener(new NavigationListener() {
                        @Override
                        public void onCancelNavigation() {
                            final Intent intent = new Intent();
                            intent.putExtra("isFinished", false);
                            setResult(102, intent);
                            finish();
                        }

                        @Override
                        public void onNavigationFinished() {
                            final Intent intent = new Intent();
                            intent.putExtra("isFinished", false);
                            setResult(102, intent);
                            finish();
                        }

                        @Override
                        public void onNavigationRunning() {

                        }
                    })
                    .awsPoolId(null)
                    .shouldSimulateRoute(false)
                    .directionsProfile(DirectionsCriteria.PROFILE_WALKING)
                    .build();

            navigationViewBinding.mapNavigationView.startNavigation(options);

        } else {
            NavigationViewOptions options = NavigationViewOptions.builder()
                    .origin(originPosition)
                    .navigationOptions(optionsMap)
                    .destination(destinationPosition)
                    .feedbackListener(this)
                    .navigationListener(new NavigationListener() {
                        @Override
                        public void onCancelNavigation() {
                            final Intent intent = new Intent();
                            intent.putExtra("isFinished", false);
                            setResult(102, intent);
                            finish();
                        }

                        @Override
                        public void onNavigationFinished() {
                            final Intent intent = new Intent();
                            intent.putExtra("isFinished", false);
                            setResult(102, intent);
                            finish();
                        }

                        @Override
                        public void onNavigationRunning() {

                        }
                    })
                    .awsPoolId(null)
                    .shouldSimulateRoute(false)
                    .directionsProfile(DirectionsCriteria.PROFILE_WALKING)
                    .progressChangeListener(this)
                    .build();

            final CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(originPosition.latitude(), originPosition.longitude())) // Sets the new camera position
                    .zoom(16)// Sets the zoom
                    .tilt(30) // Set the camera tilt
                    .build(); // Creates a CameraPosition from the builder

            navigationViewBinding.mapNavigationView.getMapboxMap().animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 1000);

            navigationViewBinding.mapNavigationView.startNavigation(options);
        }
    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        Log.e("Route Distance", ": " + routeProgress.distanceRemaining());
        if (RouteUtils.isArrivalEvent(routeProgress) && routeProgress.distanceRemaining() < 30) {
            isArrived = true;
            navigationViewBinding.bottomSheet.setVisibility(View.VISIBLE);
        } else {
            isArrived = false;
            navigationViewBinding.bottomSheet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFeedbackOpened() {
        navigationViewBinding.bottomSheet.setVisibility(View.GONE);
    }

    @Override
    public void onFeedbackCancelled() {
        if (isArrived) {
            navigationViewBinding.bottomSheet.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onFeedbackSent(FeedbackItem feedbackItem) {
        if (isArrived) {
            navigationViewBinding.bottomSheet.setVisibility(View.VISIBLE);
        }
    }
}

