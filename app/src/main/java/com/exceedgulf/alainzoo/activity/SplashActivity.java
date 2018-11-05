package com.exceedgulf.alainzoo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.databinding.ActivitySplashBinding;
import com.exceedgulf.alainzoo.managers.AvatarsManager;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.services.AccessTokenService;
import com.exceedgulf.alainzoo.utils.LangUtils;

public class SplashActivity extends BaseActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 102;
    private Handler handler;
    private ActivitySplashBinding splashBinding;
    private MediaPlayer player;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (SharedPrefceHelper.getInstance().get(PrefCons.IS_FIRST_TIME, true)) {
                SharedPrefceHelper.getInstance().save(PrefCons.BEACON_NOTIFICATION, true);
                SharedPrefceHelper.getInstance().save(PrefCons.ONESIGNAL_NOTIFICATION, true);
                final Intent intent = new Intent(SplashActivity.this, LanguageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            } else {
                final Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        }
    };

    private Runnable delayRunnable = new Runnable() {
        @Override
        public void run() {
            player.start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        SlideToAbove();
        SlideToDown();
        startService(new Intent(this, AccessTokenService.class));
        //startService(new Intent(this, CacheService.class));
        AvatarsManager.getAvatarsManager().getAvatarsSingleEntry(0, new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {

            }

            @Override
            public void onFaild(String message) {

            }
        });
        handler = new Handler();
        String path = "android.resource://" + getPackageName() + "/" + R.raw.splash;
        splashBinding.splashActivityVvZoo.setVideoURI(Uri.parse(path));
        splashBinding.splashActivityVvZoo.setZOrderOnTop(true);
        splashBinding.splashActivityVvZoo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SplashActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                } else {
                    handler.postDelayed(runnable, Constants.SPLASH_TIME_OUT);
                }
                return true;
            }
        });
        splashBinding.splashActivityVvZoo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {
                player = mediaPlayer;
                handler.postDelayed(delayRunnable, 1000);
            }
        });
        splashBinding.splashActivityVvZoo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                splashBinding.splashActivityVvZoo.seekTo(splashBinding.splashActivityVvZoo.getDuration());
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SplashActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                } else {
                    handler.postDelayed(runnable, Constants.SPLASH_TIME_OUT);
                }
            }
        });
    }

    public void SlideToAbove() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.from_top);
        anim.setInterpolator((new DecelerateInterpolator()));
        anim.setFillAfter(true);
        splashBinding.splashActivityIvLogo.setAnimation(anim);

    }

    public void SlideToDown() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.from_bottom);
        anim.setInterpolator((new DecelerateInterpolator()));
        anim.setFillAfter(true);
        splashBinding.splashActivityIvBottom.setAnimation(anim);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    handler.postDelayed(runnable, Constants.SPLASH_TIME_OUT);
                } else {
                    handler.postDelayed(runnable, Constants.SPLASH_TIME_OUT);
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //handler.removeCallbacks(runnable);
        if (handler != null) {
            handler.removeCallbacks(delayRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler.removeCallbacks(delayRunnable);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getResources().updateConfiguration(LangUtils.getLocal(this), getApplicationContext().getResources().getDisplayMetrics());
        super.onConfigurationChanged(LangUtils.getLocal(this));
    }
}
