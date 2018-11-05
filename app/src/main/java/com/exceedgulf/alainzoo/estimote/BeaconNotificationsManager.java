package com.exceedgulf.alainzoo.estimote;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.database.models.BeaconModel;
import com.exceedgulf.alainzoo.fragments.AnimalDetailFragment;
import com.exceedgulf.alainzoo.fragments.AttractionsDetailFragment;
import com.exceedgulf.alainzoo.fragments.ExperianceDetailFragment;
import com.exceedgulf.alainzoo.fragments.WhatsNewDetailFragment;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class BeaconNotificationsManager {

    private static final String TAG = "BeaconNotifications";

    private BeaconManager beaconManager;

    private List<BeaconRegion> regionsToMonitor = new ArrayList<>();
    private HashMap<String, String> enterMessages = new HashMap<>();
    private HashMap<String, String> exitMessages = new HashMap<>();
    private HashMap<String, BeaconModel> beaconModels = new HashMap<>();
    private AlertDialog alertDialog;
    private Context context;

    private int notificationID = 0;

    public BeaconNotificationsManager(final Context context) {
        this.context = context;
        beaconManager = new BeaconManager(context);
        beaconManager.setMonitoringListener(new BeaconManager.BeaconMonitoringListener() {
            @Override
            public void onEnteredRegion(BeaconRegion region, List<Beacon> list) {
                Log.d(TAG, "onEnteredRegion: " + region.getIdentifier());
                final String message = enterMessages.get(region.getIdentifier());
                final BeaconModel beaconModel = beaconModels.get(region.getIdentifier());
                if (message != null && !TextUtils.isEmpty(message) && context != null) {
                    if (SharedPrefceHelper.getInstance().get(PrefCons.BEACON_NOTIFICATION, false)) {
                        showNotification(message, beaconModel);
                    }
//                    else if (SharedPrefceHelper.getInstance().get(PrefCons.ONESIGNAL_NOTIFICATION, false)) {
//                        if (HomeActivity.isIsForeground()) {
//                            showAlert(message, beaconModel);
//                        }
//                    }
                }
            }

            @Override
            public void onExitedRegion(BeaconRegion region) {
                Log.d(TAG, "onExitedRegion: " + region.getIdentifier());
                final String message = exitMessages.get(region.getIdentifier());
                final BeaconModel beaconModel = beaconModels.get(region.getIdentifier());
                if (message != null && !TextUtils.isEmpty(message) && context != null) {
                    if (SharedPrefceHelper.getInstance().get(PrefCons.BEACON_NOTIFICATION, false)) {
                        showNotification(message, beaconModel);
                    }
//                    else if (SharedPrefceHelper.getInstance().get(PrefCons.ONESIGNAL_NOTIFICATION, false)) {
//                        if (context != null && HomeActivity.isIsForeground()) {
//                            showAlert(message, beaconModel);
//                        }
//                    }
                }
            }
        });
    }

//    public void addNotification(BeaconID beaconID, String enterMessage, String exitMessage) {
//        BeaconRegion region = beaconID.toBeaconRegion();
//        enterMessages.put(region.getIdentifier(), enterMessage);
//        exitMessages.put(region.getIdentifier(), exitMessage);
//        regionsToMonitor.add(region);
//    }

    public void addNotification(BeaconID beaconID, BeaconModel beaconModel, String enterMessage, String exitMessage) {
        BeaconRegion region = beaconID.toBeaconRegion();
        enterMessages.put(region.getIdentifier(), enterMessage);
        exitMessages.put(region.getIdentifier(), exitMessage);
        beaconModels.put(region.getIdentifier(), beaconModel);
        regionsToMonitor.add(region);
    }

    public void clearAllRecord() {
        regionsToMonitor.clear();
        enterMessages.clear();
        exitMessages.clear();
        beaconModels.clear();
    }

//    private void showAlert(String message, final BeaconModel beaconModel) {
//        if (alertDialog != null && alertDialog.isShowing()) {
//            return;
//        }
//        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
//                .setCancelable(false);
//        final LayoutInflater inflater = LayoutInflater.from(context);
//        final View dialogView = inflater.inflate(R.layout.dialog_sos_request, null);
//        builder.setView(dialogView);
//        alertDialog = builder.create();
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        final TextView tvTitle = dialogView.findViewById(R.id.dialog_custom_tvTitle);
//        final TextView tvYes = dialogView.findViewById(R.id.dialog_custom_tvYes);
//        final TextView tvNo = dialogView.findViewById(R.id.dialog_custom_tvNo);
//        final TextView tvMessage = dialogView.findViewById(R.id.dialog_custom_tvMessage);
//        tvYes.setText("Show Details");
//        tvNo.setText(context.getString(R.string.cancel));
//
//        tvTitle.setText("Alert");
//        tvMessage.setText(message);
//        tvYes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialog.dismiss();
//                if (beaconModel.getActionId() != null && !TextUtils.isEmpty(beaconModel.getActionId().trim()) && beaconModel.getActionName() != null && !TextUtils.isEmpty(beaconModel.getActionName().trim())) {
//                    if (beaconModel.getActionName().equalsIgnoreCase("animal")) {
//                        if (((HomeActivity) context).getcurrentFragment() != null) {
//                            ((HomeActivity) context).addFragment(((HomeActivity) context).getcurrentFragment(), AnimalDetailFragment.getAnimalDetailFragment(Integer.parseInt(beaconModel.getActionId())));
//                        } else {
//                            ((HomeActivity) context).replaceFragment(AnimalDetailFragment.getAnimalDetailFragment(Integer.parseInt(beaconModel.getActionId())));
//                        }
//                    } else if (beaconModel.getActionName().equalsIgnoreCase("experience")) {
//                        if (((HomeActivity) context).getcurrentFragment() != null) {
//                            ((HomeActivity) context).addFragment(((HomeActivity) context).getcurrentFragment(), ExperianceDetailFragment.getExperianceDetailFragment(Integer.parseInt(beaconModel.getActionId())));
//                        } else {
//                            ((HomeActivity) context).replaceFragment(ExperianceDetailFragment.getExperianceDetailFragment(Integer.parseInt(beaconModel.getActionId())));
//                        }
//                    } else if (beaconModel.getActionName().equalsIgnoreCase("attraction")) {
//                        if (((HomeActivity) context).getcurrentFragment() != null) {
//                            ((HomeActivity) context).addFragment(((HomeActivity) context).getcurrentFragment(), AttractionsDetailFragment.getAttractionsDetailFragment(Integer.parseInt(beaconModel.getActionId())));
//                        } else {
//                            ((HomeActivity) context).replaceFragment(AttractionsDetailFragment.getAttractionsDetailFragment(Integer.parseInt(beaconModel.getActionId())));
//                        }
//                    } else if (beaconModel.getActionName().equalsIgnoreCase("venue")) {
//                        if (((HomeActivity) context).getcurrentFragment() != null) {
//                            ((HomeActivity) context).addFragment(((HomeActivity) context).getcurrentFragment(), WhatsNewDetailFragment.getWhatsNewDetailFragment(Integer.parseInt(beaconModel.getActionId())));
//                        } else {
//                            ((HomeActivity) context).replaceFragment(WhatsNewDetailFragment.getWhatsNewDetailFragment(Integer.parseInt(beaconModel.getActionId())));
//                        }
//                    }
//                }
//            }
//        });
//        tvNo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialog.dismiss();
//            }
//        });
//        if (alertDialog != null && !alertDialog.isShowing())
//            alertDialog.show();
//        final int width = (int) context.getResources().getDimension(R.dimen._260sdp);
//        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//    }

    public void startMonitoring() {
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                for (BeaconRegion region : regionsToMonitor) {
                    beaconManager.startMonitoring(region);
                }
            }
        });
    }

    private void showNotification(final String message, final BeaconModel beaconModel) {
        if (!AppAlainzoo.getAppAlainzoo().isNavigationRunning()) {
            final Intent resultIntent = new Intent(context, HomeActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            resultIntent.putExtra("BEACON_NOTIFCATION", true);
            resultIntent.putExtra("ACTION_NAME", beaconModel.getActionName());
            resultIntent.putExtra("ACTION_ID", beaconModel.getActionId());
            final Random r = new Random();
            final int i1 = r.nextInt(100);
            final PendingIntent resultPendingIntent = PendingIntent.getActivity(context, i1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_small_notification)
                    .setContentTitle(context.getString(R.string.application_name))
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(resultPendingIntent);

            final NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(i1, builder.build());
        }
    }
}
