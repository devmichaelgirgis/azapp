package com.exceedgulf.alainzoo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.database.models.CameraFrame;
import com.exceedgulf.alainzoo.managers.CameraFrameManager;
import com.exceedgulf.alainzoo.managers.ImageDownloadManager;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Paras Ghasadiya on 19/12/17.
 */

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private long mLastClickTime = 0;
    private final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getResources().updateConfiguration(LangUtils.getLocal(this), getApplicationContext().getResources().getDisplayMetrics());
    }


    @Override
    public void onClick(View v) {

    }

    public boolean isOnClick() {
        Utils.getInstance().hideSoftKeyboard(BaseActivity.this);
        /*
          Prevents the Launch of the component multiple times
          on clicks encountered in quick succession.
         */
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }

    public void addFragment(Fragment hideFragment, Fragment newFragment) {
        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frame_container, newFragment, newFragment.getClass().getSimpleName());
        transaction.hide(hideFragment);
        transaction.addToBackStack(hideFragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    public void replaceFragment(Fragment fragment) {
        final FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_container, fragment, fragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    public void updateStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    @SuppressLint("SetTextI18n")
    public void setMandatoryStar(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);

            if (v instanceof ViewGroup) {
                this.setMandatoryStar((ViewGroup) v);
            }

            if (v.getTag() == null) {
                continue;
            }
            final String tag = v.getTag().toString();

            if (v instanceof TextView) {
                final TextView textView = (TextView) v;
                if (tag.equalsIgnoreCase("star") && !textView.getText().toString().trim().contains("*")) {
                    final String text = textView.getText().toString().trim() + " ";
                    textView.setText(Html.fromHtml(text + "<font color='#d80003'>*</font>"));

                }
            }
        }
    }

    public void getAllCameraFrames() {
        CameraFrameManager.getCameraFrameManager().getAllCameraFramesEntry(new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.e("Frame", "Success");
                downloadImages((ArrayList<CameraFrame>) result);
            }

            @Override
            public void onFaild(String message) {
                Log.e("Frame", "Fail--" + message);
            }
        });
    }

    protected void setMandatoryStar() {
        final ViewGroup viewGroup = findViewById(R.id.root_view);
        if (viewGroup != null) {
            setMandatoryStar(viewGroup);
        }
    }

    @SuppressLint("CheckResult")
    private void downloadImages(final ArrayList<CameraFrame> cameraFrameArrayList) {
        final List<String> imageUrlsArrayList = new ArrayList<>();
        for (final CameraFrame cameraFrame : cameraFrameArrayList) {
            if (!TextUtils.isEmpty(cameraFrame.getImage())) {
                final String imagePath = Constants.ZOO_CAMERA_FRAME + File.separator + ImageDownloadManager.getHashCodeBasedFileName(cameraFrame.getImage());
                final File file = new File(imagePath);
                if (!file.exists()) {
                    imageUrlsArrayList.add(cameraFrame.getImage());
                }
            }
        }
        if (imageUrlsArrayList.size() > 0) {
            ImageDownloadManager.getInstance(getApplicationContext()).addTask(
                    new ImageDownloadManager.ImageDownloadTask(this, ImageDownloadManager.Task.DOWNLOAD, imageUrlsArrayList,
                            Constants.ZOO_CAMERA_FRAME, new ImageDownloadManager.Callback() {
                        @Override
                        public void onSuccess(ImageDownloadManager.ImageDownloadTask task) {
                            Log.e(ImageDownloadManager.class.getSimpleName(), "Image save success news ");
                        }

                        @Override
                        public void onFailure(ImageDownloadManager.ImageSaveFailureReason reason) {
                            Log.e(ImageDownloadManager.class.getSimpleName(), "Image save fail news " + reason);
                        }
                    }));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMandatoryStar();
    }

    public void alertMessage(final String title, final String message, final boolean isValidation) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_form_response, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogView.findViewById(R.id.dialog_form_response_tvTitle).setVisibility(View.GONE);
        final TextView tvTitle = dialogView.findViewById(R.id.dialog_form_response_tvTitle);
        tvTitle.setText(title);
        tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        final TextView tvMessage = dialogView.findViewById(R.id.dialog_form_response_tvMessage);
        final TextView tvOkay = dialogView.findViewById(R.id.dialog_form_response_tvOkay);

        tvMessage.setText(message);
        tvOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (!isValidation) {
                    final Intent intent = new Intent(BaseActivity.this, SignInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                }
            }
        });
        alertDialog.show();
        final int width = (int) getResources().getDimension(R.dimen._250sdp);
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void alertMessageWithBack(final String title, final String message, final boolean isBack) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_form_response, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogView.findViewById(R.id.dialog_form_response_tvTitle).setVisibility(View.GONE);
        final TextView tvTitle = dialogView.findViewById(R.id.dialog_form_response_tvTitle);
        tvTitle.setText(title);
        tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        final TextView tvMessage = dialogView.findViewById(R.id.dialog_form_response_tvMessage);
        final TextView tvOkay = dialogView.findViewById(R.id.dialog_form_response_tvOkay);

        tvMessage.setText(message);
        tvOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (isBack) {
                    onBackPressed();
                }
            }
        });
        alertDialog.show();
        final int width = (int) getResources().getDimension(R.dimen._250sdp);
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void alertDetail(final String title, final String message, final boolean isValidation) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_share_info, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogView.findViewById(R.id.dialog_form_response_tvTitle).setVisibility(View.GONE);
        final TextView tvTitle = dialogView.findViewById(R.id.dialog_form_response_tvTitle);
        tvTitle.setText(title);
        tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        final TextView tvMessage = dialogView.findViewById(R.id.dialog_form_response_tvMessage);
        final TextView tvOkay = dialogView.findViewById(R.id.dialog_form_response_tvOkay);

        tvMessage.setText(message);
        tvOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (!isValidation) {
                    final Intent intent = new Intent(BaseActivity.this, SignInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                }
            }
        });
        alertDialog.show();
        final int width = (int) getResources().getDimension(R.dimen._250sdp);
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

}
