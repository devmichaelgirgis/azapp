package com.exceedgulf.alainzoo.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.CameraFrameAdapter;
import com.exceedgulf.alainzoo.database.models.CameraFrame;
import com.exceedgulf.alainzoo.databinding.FragmentZooCameraBinding;
import com.exceedgulf.alainzoo.managers.CameraFrameManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.Facing;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by P.P on 19/01/2018
 */
public class ZooCameraFragment extends BaseFragment {
    private static final int MY_PERMISSIONS_CAMERA = 101;
    private static final int MY_PERMISSIONS_STORAGE = 103;
    private CameraFrameAdapter cameraFrameAdapter;

    CameraListener mCameraListener = new CameraListener() {
        @Override
        public void onPictureTaken(byte[] picture) {
            // Create a bitmap or a file...
            // CameraUtils will read EXIF orientation for you, in a worker thread.
            CameraUtils.decodeBitmap(picture, new CameraUtils.BitmapCallback() {
                @Override
                public void onBitmapReady(Bitmap bitmap) {
                    final AsyncBitmap asyncBitmap = new AsyncBitmap();
                    asyncBitmap.execute(bitmap);
                }
            });
        }
    };
    private FragmentZooCameraBinding zooCameraBinding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        zooCameraBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_zoo_camera, container, false);
        return zooCameraBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.camera_front, menu);
        final LayoutInflater baseInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View myCustomView = baseInflater.inflate(R.layout.menu_item_camera, null);
//        ((CustomTextview) myCustomView).setText(getString(R.string.feedback));
        final MenuItem item = menu.findItem(R.id.action_front);
        item.setActionView(myCustomView);
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zooCameraBinding.camera != null) {
                    if (zooCameraBinding.camera.getFacing() == Facing.FRONT) {
                        ((AppCompatImageView) myCustomView).setImageResource(R.drawable.ic_camera_rear);
                        zooCameraBinding.camera.setFacing(Facing.BACK);
                    } else {
                        ((AppCompatImageView) myCustomView).setImageResource(R.drawable.ic_camera_front);
                        zooCameraBinding.camera.setFacing(Facing.FRONT);
                    }
                }
            }
        });
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.zoo_camrea), getResources().getColor(R.color.light_eggplant), null, true);
        zooCameraBinding.fragIvCapture.setOnClickListener(this);
        cameraFrameAdapter = new CameraFrameAdapter(getActivity());
        zooCameraBinding.fragVpFrames.setAdapter(cameraFrameAdapter);
        zooCameraBinding.fragVpFrames.setOffscreenPageLimit(2);

        zooCameraBinding.fragIvArrowNext.setOnClickListener(this);
        zooCameraBinding.fragIvArrowPrevious.setOnClickListener(this);

        getAllCameraFrames();
    }

    public void getAllCameraFrames() {
        CameraFrameManager.getCameraFrameManager().getAllCameraFramesEntry(new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.e("Frame", "Success");
                cameraFrameAdapter.addAllItems((ArrayList<CameraFrame>) result);
            }

            @Override
            public void onFaild(String message) {
                Log.e("Frame", "Fail--" + message);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.zoo_camrea), getResources().getColor(R.color.light_eggplant), null, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_CAMERA);
        } else {
            zooCameraBinding.camera.start();
            zooCameraBinding.camera.addCameraListener(mCameraListener);
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    zooCameraBinding.camera.start();
                    zooCameraBinding.camera.addCameraListener(mCameraListener);
                }
                return;
            }
            case MY_PERMISSIONS_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    zooCameraBinding.camera.addCameraListener(mCameraListener);
//                    zooCameraBinding.camera.capturePicture();
                }
                return;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        zooCameraBinding.camera.stop();
        zooCameraBinding.camera.destroy();
    }

    @Override
    public void onClick(View v) {
        if (!isOnClick()) {
            return;
        }
        if (v == zooCameraBinding.fragIvCapture) {
            zooCameraBinding.camera.capturePicture();
        } else if (v == zooCameraBinding.fragIvArrowPrevious) {
            zooCameraBinding.fragVpFrames.setCurrentItem(getItem(-1), true);
        } else if (v == zooCameraBinding.fragIvArrowNext) {
            zooCameraBinding.fragVpFrames.setCurrentItem(getItem(+1), true);
        }
    }

    private int getItem(int i) {
        return zooCameraBinding.fragVpFrames.getCurrentItem() + i;
    }

    private File getDirectory() {
        final File mkDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/AlainZoo/");
        if (!mkDir.exists()) {
            mkDir.mkdirs();
        }
        return mkDir;
    }

    private int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        Matrix mat = new Matrix();
        final int height = bmp1.getHeight();
        final int width = bmp1.getWidth();
        mat.setScale(width, height);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, null, new RectF(0, 0, width, height), null);
        return bmOverlay;
    }

    private class AsyncBitmap extends AsyncTask<Bitmap, Void, Void> {

        private File mFileTemp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DisplayDialog.getInstance().showProgressDialog(getActivity(), "", false);
        }

        @Override
        protected Void doInBackground(Bitmap... params) {
            try {
                Bitmap bitmap;
                if (zooCameraBinding.camera.getFacing() == Facing.FRONT) {
                    bitmap = flip(params[0], Direction.HORIZONTAL);
                } else {
                    bitmap = params[0];
                }
                zooCameraBinding.fragVpFrames.setDrawingCacheEnabled(true);
                zooCameraBinding.fragVpFrames.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                zooCameraBinding.fragVpFrames.buildDrawingCache();
                if (zooCameraBinding.fragVpFrames.getDrawingCache() != null) {
                    final Bitmap frameBitmap = zooCameraBinding.fragVpFrames.getDrawingCache();
                    final Bitmap newBitmap = overlay(bitmap, frameBitmap);
                    mFileTemp = new File(getDirectory(), getString(R.string.application_name) + "Image_" + Long.toString(System.currentTimeMillis()) + ".jpeg");
                    final FileOutputStream out = new FileOutputStream(mFileTemp);
                    final BufferedOutputStream stream = new BufferedOutputStream(out);
                    newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    out.flush();
                    out.close();
                    if (newBitmap != null) {
                        newBitmap.recycle();
                    }
                    if (frameBitmap != null) {
                        frameBitmap.recycle();
                    }
                    zooCameraBinding.fragVpFrames.setDrawingCacheEnabled(false);
                    zooCameraBinding.fragVpFrames.destroyDrawingCache();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            DisplayDialog.getInstance().dismissProgressDialog();
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mFileTemp)));
            final Bundle bundle = new Bundle();
            bundle.putString("file", mFileTemp.getAbsolutePath());
            final PreviewFragment previewFragment = new PreviewFragment();
            previewFragment.setArguments(bundle);
            ((HomeActivity) getActivity()).addFragment(ZooCameraFragment.this, previewFragment);
        }
    }

    public enum Direction {VERTICAL, HORIZONTAL}

    public static Bitmap flip(Bitmap src, Direction type) {
        final Matrix matrix = new Matrix();

        if (type == Direction.VERTICAL) {
            matrix.preScale(1.0f, -1.0f);
        } else if (type == Direction.HORIZONTAL) {
            matrix.preScale(-1.0f, 1.0f);
        } else {
            return src;
        }
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

}
