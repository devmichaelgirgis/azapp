package com.exceedgulf.alainzoo.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.common.requirements.SystemRequirementsHelper;
import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.Interfaces.OnDisableCollapse;
import com.exceedgulf.alainzoo.Interfaces.OnEnableCollapse;
import com.exceedgulf.alainzoo.Interfaces.OnNavChildClick;
import com.exceedgulf.alainzoo.Interfaces.OnNavParentClick;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.Avatars;
import com.exceedgulf.alainzoo.database.models.BeaconModel;
import com.exceedgulf.alainzoo.databinding
        .ActivityHomeBinding;
import com.exceedgulf.alainzoo.estimote.BeaconID;
import com.exceedgulf.alainzoo.estimote.BeaconNotificationsManager;
import com.exceedgulf.alainzoo.fragments.AboutTheZooFragment;
import com.exceedgulf.alainzoo.fragments.AnimalFragment;
import com.exceedgulf.alainzoo.fragments.BuyTicketFragment;
import com.exceedgulf.alainzoo.fragments.ContactUsFragment;
import com.exceedgulf.alainzoo.fragments.DoAndDoNotFragment;
import com.exceedgulf.alainzoo.fragments.EducationFragment;
import com.exceedgulf.alainzoo.fragments.ExperianceDetailFragment;
import com.exceedgulf.alainzoo.fragments.ExperiencesFragment;
import com.exceedgulf.alainzoo.fragments.ExploreZooFragment;
import com.exceedgulf.alainzoo.fragments.FAQFragment;
import com.exceedgulf.alainzoo.fragments.GamesFragment;
import com.exceedgulf.alainzoo.fragments.HomeFragment;
import com.exceedgulf.alainzoo.fragments.NotificationFragment;
import com.exceedgulf.alainzoo.fragments.PlanVisitDetailFragment;
import com.exceedgulf.alainzoo.fragments.PlanVisitFragment;
import com.exceedgulf.alainzoo.fragments.PrivacyPolicyFragment;
import com.exceedgulf.alainzoo.fragments.ProfileFragment;
import com.exceedgulf.alainzoo.fragments.RememberParkFragment;
import com.exceedgulf.alainzoo.fragments.SearchFragment;
import com.exceedgulf.alainzoo.fragments.SettingsFragment;
import com.exceedgulf.alainzoo.fragments.ShuttleServiceMap;
import com.exceedgulf.alainzoo.fragments.TermsConditionFragment;
import com.exceedgulf.alainzoo.fragments.TreasureHuntFragment;
import com.exceedgulf.alainzoo.fragments.VisiterServicesFragment;
import com.exceedgulf.alainzoo.fragments.ZooCameraFragment;
import com.exceedgulf.alainzoo.helper.BottomNavigationViewHelper;
import com.exceedgulf.alainzoo.managers.AvatarsManager;
import com.exceedgulf.alainzoo.managers.BeaconManager;
import com.exceedgulf.alainzoo.managers.GamificationManager;
import com.exceedgulf.alainzoo.managers.LogoutManager;
import com.exceedgulf.alainzoo.managers.ProfileManager;
import com.exceedgulf.alainzoo.managers.SosManager;
import com.exceedgulf.alainzoo.models.UserModel;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.services.AccessTokenService;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.views.expands.Children;
import com.exceedgulf.alainzoo.views.expands.NavigationParent;
import com.exceedgulf.alainzoo.views.expands.ParentNavAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.RequestBody;


public class HomeActivity extends BaseActivity implements OnEnableCollapse, OnDisableCollapse, OnNavParentClick, OnNavChildClick, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_LOCATION = 1000;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 102;
    private static final int REQUEST_LOCATION = 120;
    private static final int REQUEST_BLUETOOTH = 121;
    private static final int REQUEST_LOCATION_PERM = 122;
    private static final String IS_BACK = "IS_BACK";
    public static boolean isForeground = false;
    private final long INTERVAL = 1000;
    private final long FASTEST_INTERVAL = 1000 * 5;
    public ActivityHomeBinding activityHomeBinding;
    public int selectedBottomMenuPosition = 0;
    public boolean userBluetoothDenied = false;
    public boolean userLocationDenied = false;
    public boolean userLocationPermDenied = false;
    boolean doubleBackToExitPressedOnce = false;
    boolean isSOSRequest = false;
    boolean isLocationPerm = true, isLocDis = true, isBluetoothDis = true;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            final Location location = locationResult.getLastLocation();
            Log.e(TAG, "Firing Service onLocationChanged....lat==>" + location.getLatitude() + "     long==>" + location.getLongitude());
            if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                if (getcurrentFragment() instanceof HomeFragment && isSOSRequest) {
                    isSOSRequest = false;
                    createSOSDialog(location);
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                }
            }
        }
    };
    private boolean beaconNotificationsEnabled = false;
    private BeaconNotificationsManager beaconNotificationsManager;
    private Map<String, Object> planeSelectedItems = new LinkedHashMap<String, Object>();
    private GoogleApiClient googleApiClient;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_home:
                    selectedBottomMenuPosition = 0;
                    if (getcurrentFragment() != null && !(getcurrentFragment() instanceof HomeFragment)) {
                        replaceFragment(new HomeFragment());
                    }
                    break;
                case R.id.navigation_explore_zoo:
                    selectedBottomMenuPosition = 1;
                    if (getcurrentFragment() != null && !(getcurrentFragment() instanceof ExploreZooFragment)) {
                        replaceFragment(new ExploreZooFragment());
                    }
                    break;
                case R.id.navigation_plan_visit:
                    selectedBottomMenuPosition = 2;
                    if (getcurrentFragment() != null && !(getcurrentFragment() instanceof PlanVisitFragment)) {
                        replaceFragment(new PlanVisitFragment());
                    }
                    break;
                case R.id.navigation_buy_ticket:
                    selectedBottomMenuPosition = 3;
                    if (getcurrentFragment() != null && !(getcurrentFragment() instanceof BuyTicketFragment)) {
                        replaceFragment(new BuyTicketFragment());
                    }
                    break;
            }

            return true;
        }
    };

    public static boolean isIsForeground() {
        return isForeground;
    }

    public Map<String, Object> getPlaneSelectedItems() {
        return planeSelectedItems;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAllCameraFrames();
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        setUpNavogationDrawer();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (isGooglePlayServicesAvailable()) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            googleApiClient.connect();
        } else {
            Log.e(TAG, "Location Service not Available.");
        }

        setToolbarWithCenterTitle("", getResources().getColor(R.color.cool_blue), null, true);
        activityHomeBinding.appbarHome.navigation.setAnimation(AnimationUtils.loadAnimation(this, R.anim.bottom_up));
        BottomNavigationViewHelper.removeShiftMode(activityHomeBinding.appbarHome.navigation);
        activityHomeBinding.appbarHome.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        activityHomeBinding.fragmentNavDrawer.navImgbtnfacebook.setOnClickListener(this);
        activityHomeBinding.fragmentNavDrawer.navImgbtnTwitter.setOnClickListener(this);
        activityHomeBinding.fragmentNavDrawer.navImgbtnSnapchat.setOnClickListener(this);
        activityHomeBinding.fragmentNavDrawer.navImgbtnInstagram.setOnClickListener(this);
        activityHomeBinding.fragmentNavDrawer.navImgbtnYoutube.setOnClickListener(this);
        activityHomeBinding.fragmentNavDrawer.navHeadIvLogout.setOnClickListener(this);
        activityHomeBinding.fragmentNavDrawer.navHeadBtnsignin.setOnClickListener(this);

        //Side Menu Manage for LoggedIn user & for guest.
        setUserData();
        //getBeaconsList();
        final Intent intent = getIntent();
        if (intent != null) {
            final Bundle bundle = intent.getExtras();
            if (bundle != null && bundle.containsKey("BEACON_NOTIFCATION")) {
                final HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(bundle);
                replaceFragment(homeFragment);
            } else {
                replaceFragment(new HomeFragment());
            }
        } else {
            replaceFragment(new HomeFragment());
        }
    }

    private void setBeaconNotification(ArrayList<BeaconModel> resultList) {
        if (!isBeaconNotificationsEnabled()) {
            Log.d(TAG, "Enabling beacon notifications");
            enableBeaconNotifications(resultList);
        }
    }

    public void checkBeaconPermissions() {
//        SystemRequirementsChecker.check(this, new SystemRequirementsChecker.Callback() {
//            @Override
//            public void onRequirementsMissing(EnumSet<SystemRequirementsChecker.Requirement> requirements) {
//                for (Object requirement : requirements) {
//                    Log.e("Missing", "Enum : " + requirement);
//                    if (requirement.toString().equalsIgnoreCase("LOCATION_PERMISSION")) {
//                        isLocationPerm = false;
//                    } else if (requirement.toString().equalsIgnoreCase("LOCATION_DISABLED")) {
//                        isLocDis = false;
//                    } else if (requirement.toString().equalsIgnoreCase("BLUETOOTH_DISABLED")) {
//                        isBluetoothDis = false;
//                    }
//                }
//            }
//        });

        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && !userLocationPermDenied) {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERM);
        } else {
            isLocationPerm = true;
        }

        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            isBluetoothDis = false;
        } else {
            isBluetoothDis = true;
        }

        if (!NetUtil.isGPSEnable(HomeActivity.this)) {
            isLocDis = false;
        } else {
            isLocDis = true;
        }

        if (!isLocationPerm && !userLocationPermDenied) {
            checkLocationPermissions();
        } else if (!isBluetoothDis && !userBluetoothDenied) {
            final Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH);
        } else if (!isLocDis && !userLocationDenied) {
            userLocationDenied = true;
            final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            final SettingsClient client = LocationServices.getSettingsClient(this);
            final Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    // ...
                    if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    isLocDis = true;
                    userLocationDenied = true;
                }
            });
            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(HomeActivity.this,
                                    101);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });
        } else {
            getBeaconsList();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkLocationPermissions() {
        if (!SystemRequirementsHelper.shouldShowCoarseLocationRequestPermissionRationale(this)) {
            buildPermissionRationaleDialog(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERM);
                }
            }).show();
        }
//        else {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERM);
//        }
    }

    private AlertDialog.Builder buildPermissionRationaleDialog(DialogInterface.OnClickListener listener) {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.requesting_location_permission)
                .setMessage(R.string.requesting_location_access_rationale)
                .setPositiveButton(android.R.string.ok, listener)
                .setCancelable(true);
    }

    public void setUserData() {
        if (SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn()) {
            activityHomeBinding.fragmentNavDrawer.navLlUserinfo.setVisibility(View.VISIBLE);
            activityHomeBinding.fragmentNavDrawer.navHeadRlbuttons.setVisibility(View.GONE);
            final Avatars avatars = (Avatars) AvatarsManager.getAvatarsManager().getEntityDetailsFromDB(SharedPrefceHelper.getInstance().get(PrefCons.USER_AVATAR_ID, 0));
            if (!TextUtils.isEmpty(SharedPrefceHelper.getInstance().get(PrefCons.USER_FIELD_NAME, ""))) {
                activityHomeBinding.fragmentNavDrawer.navTvusername.setText(SharedPrefceHelper.getInstance().get(PrefCons.USER_FIELD_NAME, ""));
            }
            if (avatars != null && !TextUtils.isEmpty(avatars.getImage())) {
                Picasso.with(HomeActivity.this).load(avatars.getImage()).into(activityHomeBinding.fragmentNavDrawer.navIvuserprofilepic);
            }
            activityHomeBinding.fragmentNavDrawer.navTvuserpoints.setText(String.valueOf(SharedPrefceHelper.getInstance().get(PrefCons.USER_POINTS, 0)));
        } else {
            activityHomeBinding.fragmentNavDrawer.navLlUserinfo.setVisibility(View.GONE);
        }
    }

    public void getBeaconsList() {
        if (beaconNotificationsManager != null) {
            beaconNotificationsManager.clearAllRecord();
            beaconNotificationsEnabled = false;
        }
        BeaconManager.getBeaconManager().getAllEntitiesData(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                setBeaconNotification(resultList);
            }

            @Override
            public void onFaild(String message) {
            }
        });
    }

    public void enableBeaconNotifications(final ArrayList<BeaconModel> beaconModelArrayList) {
        if (beaconNotificationsEnabled) {
            return;
        }
        beaconNotificationsManager = new BeaconNotificationsManager(HomeActivity.this);

        for (int i = 0; i < beaconModelArrayList.size(); i++) {
            final BeaconModel beaconModel = beaconModelArrayList.get(i);
            if (!TextUtils.isEmpty(beaconModel.getUUID()) && !TextUtils.isEmpty(beaconModel.getMajor()) && !TextUtils.isEmpty(beaconModel.getMinor())) {
                try {
                    final BeaconID beaconID = new BeaconID(beaconModel.getUUID().trim(), Integer.parseInt(beaconModel.getMajor()), Integer.parseInt(beaconModel.getMinor()));
                    beaconNotificationsManager.addNotification(beaconID, beaconModel,
                            (LangUtils.getCurrentLanguage().equalsIgnoreCase("en")) ? beaconModel.getEnterMessageEn() : beaconModel.getEnterMessageAr(),
                            (LangUtils.getCurrentLanguage().equalsIgnoreCase("en")) ? beaconModel.getExitMessageEn() : beaconModel.getExitMessageAr());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
//        //Start Comment
//        final BeaconID beaconIDIce = new BeaconID("45DB6ADA-890D-486D-ABEE-266396D8AAA6", 396, 2);
//        final BeaconID beaconIDBlueberry = new BeaconID("B9407F30-F5F8-466E-AFF9-25556B57FE6D", 48291, 59225);
//        final BeaconID beaconIDMint = new BeaconID("45DB6ADA-890D-486D-ABEE-266396D8AAA6", 396, 1);
//
//        final BeaconModel beaconModel = new BeaconModel();
//        beaconModel.setActionName("animals");
//        beaconModel.setActionId(3038);
//
//        final BeaconModel beaconModel1 = new BeaconModel();
//        beaconModel1.setActionName("experience");
//        beaconModel1.setActionId(2090);
//
//        final BeaconModel beaconModel2 = new BeaconModel();
//        beaconModel2.setActionName("venue");
//        beaconModel2.setActionId(2106);
//
//        beaconNotificationsManager.addNotification(
//                beaconIDIce,
//                beaconModel,
//                "Ice Entered",
//                "Ice Exit");
//
//        beaconNotificationsManager.addNotification(
//                beaconIDBlueberry,
//                beaconModel1,
//                "Blueberry Entered",
//                "Blueberry Exit");
//
//        beaconNotificationsManager.addNotification(
//                beaconIDMint,
//                beaconModel2,
//                "Mint Entered",
//                "Mint Exit");
        //End Comment

        beaconNotificationsManager.startMonitoring();

        beaconNotificationsEnabled = true;
    }

    public boolean isBeaconNotificationsEnabled() {
        return beaconNotificationsEnabled;
    }

    private boolean isGooglePlayServicesAvailable() {
        final GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == result) {
            return true;
        } else {
            //googleAPI.getErrorDialog(LocationService.this, result, 0).show();
            return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search_home) {
            final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
            addFragment(fragment, new SearchFragment());

        } else if (id == R.id.action_sos) {
            if (SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn()) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                } else {
                    isSOSRequest = true;
                    final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(mLocationRequest);
                    final SettingsClient client = LocationServices.getSettingsClient(this);
                    final Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
                    task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            // All location settings are satisfied. The client can initialize
                            // location requests here.
                            // ...
                            if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                        }
                    });

                    task.addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof ResolvableApiException) {
                                // Location settings are not satisfied, but this can be fixed
                                // by showing the user a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    ResolvableApiException resolvable = (ResolvableApiException) e;
                                    resolvable.startResolutionForResult(HomeActivity.this,
                                            101);
                                } catch (IntentSender.SendIntentException sendEx) {
                                    // Ignore the error.
                                }
                            }
                        }
                    });
                }
            } else {
                loginDialog(getString(R.string.sos_login_required), true);
            }
        } else if (id == R.id.action_notification) {
            addFragment(getcurrentFragment(), new NotificationFragment());
        }
        return super.onOptionsItemSelected(item);
    }

    public LocationRequest getmLocationRequest() {
        return mLocationRequest;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getcurrentFragment() instanceof RememberParkFragment) {
            getcurrentFragment().onActivityResult(requestCode, resultCode, data);
            return;
        } else if (getcurrentFragment() instanceof PlanVisitDetailFragment) {
            setUserData();
            getcurrentFragment().onActivityResult(requestCode, resultCode, data);
            return;
        } else if (getcurrentFragment() instanceof ExperianceDetailFragment) {
            setUserData();
            getcurrentFragment().onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == 101) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
//        else if (requestCode == REQUEST_LOCATION) {
//            if (!NetUtil.isGPSEnable(HomeActivity.this)) {
//                isLocDis = false;
//            } else {
//                isLocDis = true;
//                checkBeaconPermissions();
//            }
//
//        }
        else if (requestCode == REQUEST_BLUETOOTH) {
            final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
                isBluetoothDis = false;
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    // Bluetooth is not enable :)
                    isBluetoothDis = false;
                    userBluetoothDenied = true;
                } else {
                    isBluetoothDis = true;
                    userBluetoothDenied = false;
                }
            }
        }
    }

    private void createSOSDialog(final Location location) {
        if (HomeActivity.this == null) {
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
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

        tvTitle.setText(getString(R.string.sos_request));
        tvMessage.setText(R.string.sos_request_message);
        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayDialog.getInstance().showProgressDialog(HomeActivity.this, "Loading...", false);
                try {
                    final String request = SosManager.getSosManager().sosRequestData("Request a sos", "sos", location.getLatitude(), location.getLongitude(), "", "request");
                    SosManager.getSosManager().postSosRequest(request, new ApiDetailCallback() {
                        @Override
                        public void onSuccess(Object result) {
                            DisplayDialog.getInstance().dismissProgressDialog();
                            alertDialog.dismiss();
                            alertMessage("", (String) result, true);
                        }

                        @Override
                        public void onFaild(String message) {
                            DisplayDialog.getInstance().dismissProgressDialog();
                            alertDialog.dismiss();
                            alertMessage("", message, true);
                        }
                    });
                } catch (Exception e) {
                    alertMessage("", getString(R.string.error), true);
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

    public void loginDialog(final String message, final boolean isClearStack) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView tvPositiveButton = dialogView.findViewById(R.id.dialog_login_tvlogin);
        final TextView tvNegativeButton = dialogView.findViewById(R.id.dialog_login_tvcancel);
        //final TextView tvTitle = dialogView.findViewById(R.id.dialog_login_tvTitle);
        final TextView tvMessage = dialogView.findViewById(R.id.dialog_login_tvMessage);
        //tvTitle.setText(title);
        tvMessage.setText(message);
        tvPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (isClearStack) {
                    startActivity(new Intent(HomeActivity.this, SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    final Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
                    intent.putExtra(IS_BACK, true);
                    startActivityForResult(intent, 104);
                }
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (getcurrentFragment() instanceof ZooCameraFragment) {
            getcurrentFragment().onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (getcurrentFragment() instanceof RememberParkFragment) {
            getcurrentFragment().onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(mLocationRequest);
                    final SettingsClient client = LocationServices.getSettingsClient(this);
                    final Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
                    task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            // All location settings are satisfied. The client can initialize
                            // location requests here.
                            // ...
                            if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                        }
                    });

                    task.addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof ResolvableApiException) {
                                // Location settings are not satisfied, but this can be fixed
                                // by showing the user a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    ResolvableApiException resolvable = (ResolvableApiException) e;
                                    resolvable.startResolutionForResult(HomeActivity.this,
                                            101);
                                } catch (IntentSender.SendIntentException sendEx) {
                                    // Ignore the error.
                                }
                            }
                        }
                    });
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case REQUEST_LOCATION_PERM:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isLocationPerm = true;
                    userLocationPermDenied = false;
                } else {
                    userLocationPermDenied = true;
                    isLocationPerm = false;
                }
                break;
        }
    }

    @Override
    public void enableCollapse() {
        activityHomeBinding.appbarHome.appbar.setExpanded(true, false);
        activityHomeBinding.appbarHome.avatar.setVisibility(View.VISIBLE);
        activityHomeBinding.appbarHome.collapsingToolbar.setTitleEnabled(true);
        final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) activityHomeBinding.appbarHome.frameContainer.getLayoutParams();
        params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        activityHomeBinding.appbarHome.frameContainer.requestLayout();
        final CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) activityHomeBinding.appbarHome.appbar.getLayoutParams();
        lp.height = (int) getResources().getDimension(R.dimen._100sdp);
        activityHomeBinding.appbarHome.kharboshImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void disableCollapse() {
        activityHomeBinding.appbarHome.appbar.setExpanded(false, false);
        activityHomeBinding.appbarHome.avatar.setVisibility(View.GONE);
        activityHomeBinding.appbarHome.collapsingToolbar.setTitleEnabled(false);
        final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) activityHomeBinding.appbarHome.frameContainer.getLayoutParams();
        //params.setBehavior(null);
        activityHomeBinding.appbarHome.frameContainer.requestLayout();
        final CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) activityHomeBinding.appbarHome.appbar.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        activityHomeBinding.appbarHome.appbar.requestLayout();
        activityHomeBinding.appbarHome.kharboshImage.setVisibility(View.GONE);
    }

    public Fragment getcurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.frame_container);
    }

    public void setUpNavogationDrawer() {
        final List<NavigationParent> navItems = new ArrayList<>();
        final int[] imageIds = getImages();
        final String[] titles = getTitles();
        for (int i = 0; i < titles.length; i++) {
            final NavigationParent navigationParent = new NavigationParent();
            navigationParent.setmName(titles[i]);
            if (i == 10) {
                final List<Children> childsList = Arrays.asList(new Children(getString(R.string.about_the_zoo)), new Children(
                                getString(R.string.terms_and_conditions)), new Children(
                                getString(R.string.privacy_policy)),
                        new Children(getString(R.string.faq)),
                        new Children(getString(R.string.contact_us)));
                navigationParent.setmChilds(childsList);
            }
            navigationParent.setmImage(imageIds[i]);
            navItems.add(navigationParent);
        }
        final ParentNavAdapter mAdapter = new ParentNavAdapter(HomeActivity.this, navItems, this, this);
        activityHomeBinding.fragmentNavDrawer.navRvmainmenu.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        activityHomeBinding.fragmentNavDrawer.navRvmainmenu.setAdapter(mAdapter);
        activityHomeBinding.fragmentNavDrawer.navIvuserprofilepic.setOnClickListener(this);
        activityHomeBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        getUserDetailWithOutDialog();
        if (SharedPrefceHelper.getInstance().get(PrefCons.BEACON_NOTIFICATION, false)) {
            checkBeaconPermissions();
        }
        //getBeaconsList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
        //checkBeaconPermissions();
        //getBeaconsList();
    }

    private int[] getImages() {
        return new int[]{
                R.drawable.ic_menu_home,
                R.drawable.ic_menu_map,
                R.drawable.ic_menu_animal,
                R.drawable.ic_menu_services,
                R.drawable.ic_menu_experience,
                R.drawable.ic_menu_education,
                R.drawable.ic_menu_zoo_camera,
                R.drawable.ic_menu_shuttle,
                R.drawable.ic_menu_games,
                R.drawable.ic_menu_my_park,
                R.drawable.ic_menu_about,
                R.drawable.ic_menu_treasure,
                R.drawable.ic_menu_do_and_dont,
                R.drawable.ic_share_blue1,
                R.drawable.ic_share_blue1,
                R.drawable.ic_menu_settings
        };
    }

    private String[] getTitles() {
        return getResources().getStringArray(R.array.home_nav_menu_parent);
    }

    @Override
    public void onChildClicked(int position, String name) {
        selectedBottomMenuPosition = 0;
        Log.e("ChildClick", position + " -- " + name);
        if (!isOnClick()) {
            return;
        }
        closeDrawer();

        if (name.equalsIgnoreCase(getString(R.string.about_the_zoo))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof AboutTheZooFragment)) {
                addFragment(getcurrentFragment(), new AboutTheZooFragment());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.terms_and_conditions))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof TermsConditionFragment)) {
                addFragment(getcurrentFragment(), new TermsConditionFragment());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.privacy_policy))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof PrivacyPolicyFragment)) {
                addFragment(getcurrentFragment(), new PrivacyPolicyFragment());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.faq))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof FAQFragment)) {
                addFragment(getcurrentFragment(), new FAQFragment());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.contact_us))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof ContactUsFragment)) {
                addFragment(getcurrentFragment(), new ContactUsFragment());
            }
        }
    }

    public void closeDrawer() {
        if (activityHomeBinding.drawerLayout.isDrawerOpen(Gravity.START)) {
            activityHomeBinding.drawerLayout.closeDrawer(Gravity.START);
        }
    }

    @Override
    public void onParentClicked(int position, String name) {
        selectedBottomMenuPosition = 0;
        Log.e("ParentClick", position + " -- " + name);
        if (!isOnClick()) {
            return;
        }
        closeDrawer();

        if (name.equalsIgnoreCase(getString(R.string.mHome))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof HomeFragment)) {
                replaceFragment(new HomeFragment());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.mAboutUs))) {
        } else if (name.equalsIgnoreCase(getString(R.string.mAnimals))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof AnimalFragment)) {
                addFragment(getcurrentFragment(), new AnimalFragment());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.mMap))) {
//            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof QRCodeFragment)) {
//                replaceFragment(new QRCodeFragment());
//            }
            //replaceFragment(new TreasureHuntFragment());

            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof ExploreZooFragment)) {
                addFragment(getcurrentFragment(), new ExploreZooFragment());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.mRequestShuttle))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof ShuttleServiceMap)) {
                addFragment(getcurrentFragment(), new ShuttleServiceMap());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.mMyPark))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof RememberParkFragment)) {
                addFragment(getcurrentFragment(), new RememberParkFragment());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.mExperiences))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof ExperiencesFragment)) {
                addFragment(getcurrentFragment(), new ExperiencesFragment());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.mEducation))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof EducationFragment)) {
                addFragment(getcurrentFragment(), new EducationFragment());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.mServices))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof VisiterServicesFragment)) {
                addFragment(getcurrentFragment(), new VisiterServicesFragment());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.mZooCamrea))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof ZooCameraFragment)) {
                addFragment(getcurrentFragment(), new ZooCameraFragment());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.mGames))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof GamesFragment)) {
                addFragment(getcurrentFragment(), new GamesFragment());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.mNotifications))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof NotificationFragment)) {
                addFragment(getcurrentFragment(), new NotificationFragment());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.mSettings))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof SettingsFragment)) {
                addFragment(getcurrentFragment(), new SettingsFragment());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.mTreasureHunt))) {
            if (SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn()) {
                if (getcurrentFragment() != null && !(getcurrentFragment() instanceof TreasureHuntFragment)) {
                    addFragment(getcurrentFragment(), new TreasureHuntFragment());
                }
            } else {
                loginDialog(getString(R.string.sos_login_required), true);
            }
        } else if (name.equalsIgnoreCase(getString(R.string.do_and_do_not))) {
            if (getcurrentFragment() != null && !(getcurrentFragment() instanceof DoAndDoNotFragment)) {
                addFragment(getcurrentFragment(), new DoAndDoNotFragment());
            }
        } else if (name.equalsIgnoreCase(getString(R.string.mShare))) {
            closeDrawer();
            callGamification();
            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "http://www.alainzoo.ae/en/");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                intent.putExtra(Intent.EXTRA_STREAM, GenericFileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID, new File(strFile)));
            } else {
//                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(strFile)));
            }

            startActivity(Intent.createChooser(intent, getString(R.string.share)));
        } else if (name.equalsIgnoreCase(getString(R.string.mHelp))) {
            final Intent intent = new Intent(HomeActivity.this, TutorialActivity.class);
            intent.putExtra("Is_FROM_HOME", true);
            startActivity(intent);

        }
    }

    public void callGamification() {
        final RequestBody body = GamificationManager.getGamificationManager().createRequestBody("share_app", "0");
        createGamification(body);
    }

    private void createGamification(final RequestBody requestBody) {
        //DisplayDialog.getInstance().showProgressDialog(mContext, "Loading", false);
        GamificationManager.getGamificationManager().postCreateGamification(requestBody, new ApiDetailCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //DisplayDialog.getInstance().dismissProgressDialog();
                Log.e("Gamification", "success" + result);
                getUserDetailWithOutDialog();
            }

            @Override
            public void onFaild(String message) {
                Log.e("Gamification", "failure" + message);
                //DisplayDialog.getInstance().dismissProgressDialog();
                //SnackbarUtils.loadSnackBar(message, mContext, R.color.dirt_brown);
                //alertMessage("", message, true);
            }
        });
    }

    public void getUserDetailWithOutDialog() {
        if (SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn()) {
            ProfileManager.getProfileManager().fetchUserInformation(new ApiDetailCallback() {
                @Override
                public void onSuccess(Object result) {
                    if (result != null) {
                        final UserModel userModel = (UserModel) result;
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_POINTS, userModel.getField_points());
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_TOTAL_POINTS, userModel.getField_total_points());
                        setUserData();
                    }
                }

                @Override
                public void onFaild(String message) {
                }
            });
        }
    }

    public void setToolbarWithCenterTitle(final String title, int color, final Drawable navigationIcon, final boolean isShowNav) {
        setSupportActionBar(activityHomeBinding.appbarHome.toolbar);
        if (!TextUtils.isEmpty(title)) {
            activityHomeBinding.appbarHome.toolbarTitle.setVisibility(View.VISIBLE);
            activityHomeBinding.appbarHome.toolbarTitle.setText(title);
            activityHomeBinding.appbarHome.toolbarTitle.setAllCaps(true);
        } else {
            activityHomeBinding.appbarHome.toolbarTitle.setText("");
            activityHomeBinding.appbarHome.toolbarTitle.setVisibility(View.GONE);
        }
        activityHomeBinding.appbarHome.toolbar.setBackgroundColor(color);
        //activityHomeBinding.appbarHome.navigation.setVisibility(isShowNav ? View.VISIBLE : View.GONE);
        activityHomeBinding.appbarHome.navigation.setVisibility(View.VISIBLE);
        updateStatusBarColor(color);
        activityHomeBinding.appbarHome.kharboshImage.setColorFilter(color);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        closeDrawer();
        if (navigationIcon != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activityHomeBinding.appbarHome.toolbar.setNavigationIcon(navigationIcon);
            activityHomeBinding.appbarHome.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {

            final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(HomeActivity.this, activityHomeBinding.drawerLayout, activityHomeBinding.appbarHome.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            activityHomeBinding.drawerLayout.addDrawerListener(toggle);
            activityHomeBinding.appbarHome.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!activityHomeBinding.drawerLayout.isDrawerOpen(Gravity.START)) {
                        activityHomeBinding.drawerLayout.openDrawer(Gravity.START);
                    }
                }
            });
            toggle.syncState();
        }
        setBottomMenuSelection();
    }

    private void setBottomMenuSelection() {
        activityHomeBinding.appbarHome.navigation.setOnNavigationItemSelectedListener(null);
        switch (selectedBottomMenuPosition) {
            case 0:
                activityHomeBinding.appbarHome.navigation.setSelectedItemId(R.id.navigation_home);
                break;
            case 1:
                activityHomeBinding.appbarHome.navigation.setSelectedItemId(R.id.navigation_explore_zoo);
                break;
            case 2:
                activityHomeBinding.appbarHome.navigation.setSelectedItemId(R.id.navigation_plan_visit);
                break;
            case 3:
                activityHomeBinding.appbarHome.navigation.setSelectedItemId(R.id.navigation_buy_ticket);
                break;
            default:
                break;
        }
        activityHomeBinding.appbarHome.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            closeDrawer();
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.message_exit_app), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1000);

        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (!isOnClick()) {
            return;
        }

        closeDrawer();
        if (view == activityHomeBinding.fragmentNavDrawer.navIvuserprofilepic) {
            final Fragment currentFragment = getcurrentFragment();
            if (!(currentFragment instanceof ProfileFragment)) {
                replaceFragment(new ProfileFragment());
            }

        } else if (view == activityHomeBinding.fragmentNavDrawer.navHeadBtnsignin) {
            // Sign in
            final Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            finish();

        } else if (view == activityHomeBinding.fragmentNavDrawer.navHeadIvLogout) {
            createLogoutDialog(true);
        } else if (view == activityHomeBinding.fragmentNavDrawer.navImgbtnfacebook) {
            openSocialPage(getString(R.string.facebook_url));
        } else if (view == activityHomeBinding.fragmentNavDrawer.navImgbtnTwitter) {
            openSocialPage(getString(R.string.twitter_url));
        } else if (view == activityHomeBinding.fragmentNavDrawer.navImgbtnSnapchat) {
            openSocialPage(getString(R.string.snapchat_url));
        } else if (view == activityHomeBinding.fragmentNavDrawer.navImgbtnInstagram) {
            openSocialPage(getString(R.string.instagram_url));
        } else if (view == activityHomeBinding.fragmentNavDrawer.navImgbtnYoutube) {
            openSocialPage(getString(R.string.youtube_url));
        }

    }

    public void createLogoutDialog(final boolean isHome) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this)
                .setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_logout, null);
        builder.setView(dialogView);
        final AlertDialog alertDialogLogout = builder.create();

        final TextView tvCancel = dialogView.findViewById(R.id.dialog_logout_tvcancel);
        final TextView tvLogout = dialogView.findViewById(R.id.dialog_logout_tvlogout);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogLogout.cancel();
            }
        });
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogLogout.cancel();
                logout(isHome);
            }
        });
        alertDialogLogout.show();
        final int width = (int) getResources().getDimension(R.dimen._260sdp);
        alertDialogLogout.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void logout(final boolean isHome) {
        DisplayDialog.getInstance().showProgressDialog(HomeActivity.this, "Loading", false);
        LogoutManager.logoutManager().logout(new ApiDetailCallback<String>() {
            @Override
            public void onSuccess(String result) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isHome)
                    SnackbarUtils.loadSnackBar(result, HomeActivity.this, R.color.cool_blue);
                else
                    SnackbarUtils.loadSnackBar(result, HomeActivity.this, R.color.light_eggplant);

                clearUserData();
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isHome)
                    SnackbarUtils.loadSnackBar(message, HomeActivity.this, R.color.cool_blue);
                else
                    SnackbarUtils.loadSnackBar(message, HomeActivity.this, R.color.light_eggplant);
                clearUserData();
            }
        });
    }

    private void clearUserData() {
        AlainZooDB.getInstance().myPlanDao().deleteAllMyplan();
        AlainZooDB.getInstance().myPlaneVisitedItemDao().deleteAll();
        AlainZooDB.getInstance().familyDao().deleteAll();
        SharedPrefceHelper.getInstance().save(PrefCons.IS_LOGGEDIN, false);
        AppAlainzoo.getAppAlainzoo().setTempLoggedIn(false);
        SharedPrefceHelper.getInstance().save(PrefCons.USERNAME, "");
        SharedPrefceHelper.getInstance().save(PrefCons.PASSWORD, "");
        SharedPrefceHelper.getInstance().save(PrefCons.CSRF_TOKEN, "");
        SharedPrefceHelper.getInstance().save(PrefCons.LOGOUT_TOKEN, "");
        SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN, "");
        SharedPrefceHelper.getInstance().save(PrefCons.REFRESH_TOKEN, "");
        SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_EXPIRES, "");
        SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_CREATED, "");
        stopService(new Intent(HomeActivity.this, AccessTokenService.class));
        startService(new Intent(HomeActivity.this, AccessTokenService.class));
        startActivity(new Intent(HomeActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    public void openSocialPage(final String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(HomeActivity.this, AccessTokenService.class));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "Google Api connect");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Google Api disconnect");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Google Api connectfail");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getResources().updateConfiguration(LangUtils.getLocal(this), getApplicationContext().getResources().getDisplayMetrics());
        super.onConfigurationChanged(LangUtils.getLocal(this));
    }

}
