package com.exceedgulf.alainzoo.fragments;


import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.database.models.HintsItem;
import com.exceedgulf.alainzoo.databinding.FragmentQrcodeBinding;
import com.exceedgulf.alainzoo.managers.GamificationManager;
import com.exceedgulf.alainzoo.models.TokenModel;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P.P on 1/02/2018
 */
public class QRCodeFragment extends DialogFragment implements SurfaceHolder.Callback, Detector.Processor<Barcode>, View.OnClickListener {

    private FragmentQrcodeBinding qrcodeBinding;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private ArrayList<HintsItem> hintsItemArrayList;
    private int treasureId;
    private SurfaceHolder frameHolder;
    private boolean noDetectionFound = false;

    public QRCodeFragment() {
        // Required empty public constructor
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        qrcodeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_qrcode, container, false);
        return qrcodeBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((HomeActivity) getActivity()).disableCollapse();
        //((HomeActivity) getActivity()).setToolbarWithCenterTitle("", getResources().getColor(R.color.colorTresurhunt), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), true);
        barcodeDetector = new BarcodeDetector.Builder(getActivity())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        barcodeDetector.setProcessor(this);
        cameraSource = new CameraSource.Builder(getActivity(), barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();
        qrcodeBinding.cameraView.getHolder().addCallback(this);
        qrcodeBinding.frQrcodeClose.setOnClickListener(this);

        frameHolder = qrcodeBinding.frame.getHolder();

        frameHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                draw();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });

        frameHolder.setFormat(PixelFormat.TRANSLUCENT);

        qrcodeBinding.frame.setZOrderMediaOverlay(true);


        final Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("hint")) {
            hintsItemArrayList = bundle.getParcelableArrayList("hint");
            treasureId = bundle.getInt("treasure_id");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    public void initView(View view) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            //((HomeActivity) getActivity()).setToolbarWithCenterTitle("", getResources().getColor(R.color.colorTresurhunt), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), true);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                String[] strPermission = {Manifest.permission.CAMERA};
                requestPermissions(strPermission, 109);
                return;
            }
            cameraSource.start(qrcodeBinding.cameraView.getHolder());
        } catch (IOException ie) {
            Log.e("CAMERA SOURCE", ie.getMessage());
            if (cameraSource != null) cameraSource.release();
            try {
                cameraSource = new CameraSource.Builder(getActivity(), barcodeDetector)
                        .setAutoFocusEnabled(true)
                        .build();
                cameraSource.start(qrcodeBinding.cameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void draw() {
        final Canvas canvas = frameHolder.lockCanvas(null);

        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setStyle(Paint.Style.STROKE);

        paint.setColor(ContextCompat.getColor(getActivity(), R.color.colorTresurhunt));

        paint.setStrokeWidth(12);

        int padding = 150;
        Rect rectangle = new Rect(
                (int) (canvas.getWidth() * 0.10), // Left
                (int) (canvas.getHeight() * 0.30), // Top
                (int) (canvas.getWidth() * 0.90), // Right
                (int) (canvas.getHeight() * 0.70)// Bottom
        );

        canvas.drawRect(rectangle, paint);
        frameHolder.unlockCanvasAndPost(canvas);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            try {
                cameraSource.start(qrcodeBinding.cameraView.getHolder());
                barcodeDetector.setProcessor(this);
            } catch (IOException e) {
                Log.e("CAMERA SOURCE", e.getMessage());
                if (cameraSource != null) cameraSource.release();
                try {
                    cameraSource = new CameraSource.Builder(getActivity(), barcodeDetector)
                            .setAutoFocusEnabled(true)
                            .build();
                    cameraSource.start(qrcodeBinding.cameraView.getHolder());
                    barcodeDetector.setProcessor(this);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            String[] strPermission = {Manifest.permission.CAMERA};
            requestPermissions(strPermission, 109);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        cameraSource.stop();
    }

    @Override
    public void release() {

    }

    @Override
    public void onPause() {
        super.onPause();
        cameraSource.stop();
        barcodeDetector.release();
    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        final SparseArray barcodes = detections.getDetectedItems();
        if (barcodes.size() != 0) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(getActivity(), ((Barcode) barcodes.valueAt(0)).displayValue, Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < hintsItemArrayList.size(); i++) {
                        if (!TextUtils.isEmpty(hintsItemArrayList.get(i).getQr())) {
                            if (((Barcode) barcodes.valueAt(0)).displayValue.equalsIgnoreCase(hintsItemArrayList.get(i).getQr())) {
                                noDetectionFound = true;
                                barcodeDetector.release();
                                showRequestDialog(hintsItemArrayList.get(i));
                                break;
                            }
                        }
                    }

                    if (!noDetectionFound) {
                        barcodeDetector.release();
                        showRequestDialog(null);
                    }
                }
            });


//            barcodeInfo.post(new Runnable() {
//                /// / Use the post method of the TextView
//                public void run() {
//                    barcodeInfo.setText(barcodes.valueAt(0).displayValue);
//                }
//            });
        }
    }

    private void getHint(final HintsItem hintsItem) {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        final RequestBody requestBody = GamificationManager.getGamificationManager().createTreasureRequestBody(GamificationManager.getGamificationManager().HINT, String.valueOf(hintsItem.getPoints()), treasureId, hintsItem.getId());
        GamificationManager.getGamificationManager().postCreateGamification(requestBody, new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                DisplayDialog.getInstance().dismissProgressDialog();
                ((HomeActivity) getActivity()).getUserDetailWithOutDialog();
                refreshToken(hintsItem, true);
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(getString(R.string.error_clue_already_scanned), getActivity(), R.color.colorTresurhunt);
                refreshToken(hintsItem, true);
            }
        });
    }

    private void showRequestDialog(final HintsItem hintMessage) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_hint_success, null);
        final TextView tvTitle = dialogView.findViewById(R.id.dialog_hint_tvTitle);
        final TextView tvSubTitle = dialogView.findViewById(R.id.dialog_hint_tvSubTitle);
        final TextView tvHintMessage = dialogView.findViewById(R.id.dialog_hint_tvHintMessage);
        final TextView tvCollect = dialogView.findViewById(R.id.dialog_hint_tvCollect);
        if (hintMessage == null) {
            tvTitle.setVisibility(View.GONE);
            tvSubTitle.setVisibility(View.GONE);
            tvHintMessage.setText(getString(R.string.qr_code_not_found));
            tvCollect.setText(getString(R.string.cancel));
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            tvSubTitle.setVisibility(View.VISIBLE);
            tvHintMessage.setText(Html.fromHtml((LangUtils.getCurrentLanguage().equalsIgnoreCase("en")) ? hintMessage.getDoneMessageEn() : hintMessage.getDoneMessageAr()));
            tvCollect.setText(getString(R.string.collect));
        }
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        tvCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (hintMessage != null) {
                    getHint(hintMessage);
                } else {
                    getDialog().dismiss();
//                    getFragmentManager().popBackStack();
                }
            }
        });


        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final int width = (int) getResources().getDimension(R.dimen._230sdp);
        alertDialog.show();
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void refreshToken(final HintsItem hintsItem, final boolean stayIn) {
        if (NetUtil.isNetworkAvailable(getActivity())) {
            DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
            final DateFormat df = new SimpleDateFormat(Constants.TIME_STAMP_FORMAT, Locale.ENGLISH);
            final String strRefreshToken = SharedPrefceHelper.getInstance().get(PrefCons.REFRESH_TOKEN, "");
            if (!TextUtils.isEmpty(strRefreshToken.trim())) {
                Log.e("API Call", "Refresh Token");
                if (!stayIn)
                    DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
                ApiControllers.getApiControllers().postRefreshToken(strRefreshToken, new Callback<TokenModel>() {
                    @Override
                    public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                        DisplayDialog.getInstance().dismissProgressDialog();
                        if (response.code() == 200) {
                            final TokenModel tokenModel = response.body();
                            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN, tokenModel.getAccessToken());
                            SharedPrefceHelper.getInstance().save(PrefCons.REFRESH_TOKEN, tokenModel.getRefreshToken());
                            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_EXPIRES, tokenModel.getExpiresIn());
                            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_CREATED, df.format(new Date()));
                            Log.e("Refresh Token ", tokenModel.getAccessToken());
                            if (stayIn) {
                                final TreasureHuntFragment treasureHuntFragment = (TreasureHuntFragment) getTargetFragment();
                                treasureHuntFragment.setHintDone();
                                getDialog().dismiss();
//                                getFragmentManager().popBackStack();
                            } else {
                                barcodeDetector.setProcessor(QRCodeFragment.this);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenModel> call, Throwable t) {
                        Log.e("API Call-Failure", t.getMessage());
                        DisplayDialog.getInstance().dismissProgressDialog();
                        final TreasureHuntFragment treasureHuntFragment = (TreasureHuntFragment) getTargetFragment();
                        treasureHuntFragment.setHintDone();
                        getDialog().dismiss();
//                        getFragmentManager().popBackStack();
                    }
                });
            }
        } else {
            SnackbarUtils.loadSnackBar(getString(R.string.no_internet_), getActivity(), R.color.colorTresurhunt);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == qrcodeBinding.frQrcodeClose) {
            dismiss();
        }
    }
}
