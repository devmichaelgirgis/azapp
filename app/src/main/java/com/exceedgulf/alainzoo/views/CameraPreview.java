package com.exceedgulf.alainzoo.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.Utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

/**
 * This class assumes the parent layout is RelativeLayout.LayoutParams.
 */
@SuppressLint("ViewConstructor")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, MediaScannerConnection.MediaScannerConnectionClient, SensorEventListener {
    private static final String LOG_TAG = "CameraPreviewSample";
    public static int camera_position_landscape = 0;
    static int support_height = 0;
    private static boolean DEBUGGING = true;
    public boolean frontCamera = false;
    public boolean is270Degrees = false;
    protected Activity mActivity;
    protected Camera mCamera;
    protected List<Camera.Size> mPreviewSizeList;
    protected List<Camera.Size> mPictureSizeList;
    protected Camera.Size mPreviewSize;
    protected Camera.Size mPictureSize;
    /**
     * State flag: true when surface's layout size is set and surfaceChanged()
     * process has not been completed.
     */
    protected boolean mSurfaceConfiguring = false;
    boolean hasFlash;

    //    private int mCenterPosY;
    PreviewReadyCallback mPreviewReadyCallback = null;
    private SurfaceHolder mHolder;
    private Camera.Parameters cameraParams;
    private MediaScannerConnection conn;
    private SensorManager mSensorManager;

    public CameraPreview(Activity activity, int cameraId) {
        super(activity); // Always necessary
        mActivity = activity;
        hasFlash = mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        mActivity.getWindow().setFormat(PixelFormat.UNKNOWN);
        mSensorManager = (SensorManager) mActivity.getSystemService(SENSOR_SERVICE);
        initCamera(cameraId);
    }

    public static Camera.Size getBestPictureSize(Camera camera, List<Camera.Size> supportedSizes, boolean preview, int displayWidth, int displayHeight) {

        final int PREVIEW_SIZE_WIDTH_EMULATOR = 176;
        final int PREVIEW_SIZE_HEIGHT_EMULATOR = 144;
        final int PICTURE_SIZE_WIDTH_EMULATOR = 213;
        final int PICTURE_SIZE_HEIGHT_EMULATOR = 350;

        double temporalDiff = 0;
        double diff = Integer.MAX_VALUE;

        Camera.Size size = null;
        Camera.Size supportedSize = null;

        if (supportedSizes == null) {
            if (isAndroidEmulator(android.os.Build.MODEL)) {
                if (preview) {
                    size = camera.new Size(
                            PREVIEW_SIZE_WIDTH_EMULATOR,
                            PREVIEW_SIZE_HEIGHT_EMULATOR);
                    support_height = size.height;
                } else {
                    size = camera.new Size(
                            PICTURE_SIZE_WIDTH_EMULATOR,
                            PICTURE_SIZE_HEIGHT_EMULATOR);
                    support_height = size.height;
                }
            }
        } else {
            Iterator<Camera.Size> iterator = supportedSizes.iterator();
            while (iterator.hasNext()) {
                supportedSize = iterator.next();
                temporalDiff = Math.sqrt(
                        Math.pow(supportedSize.width - displayWidth, 2) +
                                Math.pow(supportedSize.height - displayHeight, 2));

                if (temporalDiff < diff) {
                    diff = temporalDiff;
                    size = supportedSize;
                    support_height = size.height;
                }
            }

        }
        support_height = size.height;
        return size;
    }

    public static boolean isAndroidEmulator(String model) {

        return (model.compareToIgnoreCase("sdk") == 0);
    }

    private void initCamera(final int cameraId) {
        try {
            // Destroy previuos Holder
            if (mHolder != null) {
                surfaceDestroyed(mHolder);
                mHolder.removeCallback(CameraPreview.this);
            }

            mHolder = getHolder();
            mHolder.addCallback(CameraPreview.this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            final int mCameraId;
            if (Camera.getNumberOfCameras() > cameraId) {
                mCameraId = cameraId;
            } else {
                mCameraId = 0;
            }

            mCamera = Camera.open(mCameraId);
            Camera.Parameters cameraParams = mCamera.getParameters();
            mPreviewSizeList = cameraParams.getSupportedPreviewSizes();
            mPictureSizeList = cameraParams.getSupportedPictureSizes();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (mHolder == null) {
                mHolder = holder;
            }
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (!mActivity.isFinishing()) {
            doSurfaceChanged(width, height);
        }
    }

    private void doSurfaceChanged(int width, int height) {
        try {
            mCamera.stopPreview();

            cameraParams = mCamera.getParameters();
            boolean portrait = isPortrait();

            // The code in this if-statement is prevented from executed again when surfaceChanged is
            // called again due to the change of the layout size in this if-statement.
            if (!mSurfaceConfiguring) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height1 = displayMetrics.heightPixels;
                int width1 = displayMetrics.widthPixels;
//            Camera.Size previewSize = determinePreviewSize(portrait, width, height);
//            Camera.Size pictureSize = determinePictureSize(previewSize);

                Camera.Size previewSize = getBestPreviewSize(width1, height1, cameraParams);
//            Camera.Size pictureSize = determinePictureSize(previewSize);
                Camera.Size pictureSize = getBestPictureSize(mCamera, cameraParams.getSupportedPictureSizes(), true, width1, height1);
                if (DEBUGGING) {
                    Log.v(LOG_TAG, "Desired Preview Size - w: " + width + ", h: " + height);
                }
                mPreviewSize = previewSize;
                mPictureSize = pictureSize;

                if (cameraParams.getSupportedPreviewSizes() != null) {
                    cameraParams.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                }

                if (cameraParams.getSupportedPictureSizes() != null) {
                    cameraParams.setPictureSize(mPictureSize.width, mPictureSize.height);
                }
                mCamera.setParameters(cameraParams);
                //            mSurfaceConfiguring = adjustSurfaceLayoutSize(previewSize, portrait, width, height);
                // Continue executing this method if this method is called recursively.
                // Recursive call of surfaceChanged is very special case, which is a path from
                // the catch clause at the end of this method.
                // The later part of this method should be executed as well in the recursive
                // invocation of this method, because the layout change made in this recursive
                // call will not trigger another invocation of this method.
                //            if (mSurfaceConfiguring && (mSurfaceChangedCallDepth <= 1)) {
                //                return;
                //            }
            }

            configureCameraParameters();
            mSurfaceConfiguring = false;


            mCamera.startPreview();
        } catch (Exception e) {
            Log.w(LOG_TAG, "Failed to start preview: " + e.getMessage());

            // Remove failed size
            mPreviewSizeList.remove(mPreviewSize);
            mPreviewSize = null;

            // Reconfigure
            if (mPreviewSizeList.size() > 0) { // prevent infinite loop
                surfaceChanged(null, 0, width, height);
            } else {
                Toast.makeText(mActivity, "Can't start preview", Toast.LENGTH_LONG).show();
                Log.w(LOG_TAG, "Gave up starting preview");
            }
        }

        if (null != mPreviewReadyCallback) {
            mPreviewReadyCallback.onPreviewReady();
        }
    }

    /**
     * @param portrait  true if orientation is portrait else false
     * @param reqWidth  must be the value of the parameter passed in surfaceChanged
     * @param reqHeight must be the value of the parameter passed in surfaceChanged
     * @return Camera.Size object that is an element of the list returned from Camera.Parameters.getSupportedPreviewSizes.
     */
    protected Camera.Size determinePreviewSize(boolean portrait, int reqWidth, int reqHeight) {
        // Meaning of width and height is switched for preview when portrait,
        // while it is the same as user's view for surface and metrics.
        // That is, width must always be larger than height for setPreviewSize.
        final int reqPreviewWidth; // requested width in terms of camera hardware
        final int reqPreviewHeight; // requested height in terms of camera hardware

        if (portrait) {
            reqPreviewWidth = reqWidth;
            reqPreviewHeight = reqHeight;
        } else {
            reqPreviewWidth = reqWidth;
            reqPreviewHeight = reqHeight;
        }

        if (DEBUGGING) {
            Log.v(LOG_TAG, "Listing all supported preview sizes");
            for (Camera.Size size : mPreviewSizeList) {
                Log.v(LOG_TAG, "  w: " + size.width + ", h: " + size.height);
            }
            Log.v(LOG_TAG, "Listing all supported picture sizes");
            for (Camera.Size size : mPictureSizeList) {
                Log.v(LOG_TAG, "  w: " + size.width + ", h: " + size.height);
            }
        }

        // Adjust surface size with the closest aspect-ratio
        float reqRatio = ((float) reqPreviewWidth) / reqPreviewHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : mPreviewSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
    }

    protected Camera.Size determinePictureSize(Camera.Size previewSize) {
        Camera.Size retSize = null;
        for (Camera.Size size : mPictureSizeList) {
            if (size.equals(previewSize)) {
                return size;
            }
        }

        if (DEBUGGING) {
            Log.v(LOG_TAG, "Same picture size not found.");
        }

        // if the preview size is not supported as a picture size
        float reqRatio = ((float) previewSize.width) / previewSize.height;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;

        for (Camera.Size size : mPictureSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
    }

    protected void configureCameraParameters() {
        // for 2.2 and later
        is270Degrees = false;
        int angle;
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        switch (display.getRotation()) {
            case Surface.ROTATION_0: // This is display orientation
                angle = 90; // This is camera orientation
                break;
            case Surface.ROTATION_90:
                angle = 0;
                break;
            case Surface.ROTATION_180:
                angle = 270;
                break;
            case Surface.ROTATION_270:
                is270Degrees = true;
                angle = 180;
                break;
            default:
                angle = 90;
                break;
        }

        Log.v(LOG_TAG, "angle: " + angle);
        Log.v(LOG_TAG, "Utils.getDeviceName(): " + Utils.getDeviceName());
        if (Utils.getDeviceName().contains("Nexus 6") && camera_position_landscape == 1) {
            // rotate camera 180°
            mCamera.setDisplayOrientation(angle + 180);
        } else if (Utils.getDeviceName().contains("Samsung SM-T805")) {
            mCamera.setDisplayOrientation(angle - 90);
        } else {
            mCamera.setDisplayOrientation(angle);
        }

        cameraParams.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        cameraParams.setPictureSize(mPictureSize.width, mPictureSize.height);
        if (DEBUGGING) {
            Log.v(LOG_TAG, "Preview Actual Size - w: " + mPreviewSize.width + ", h: " + mPreviewSize.height);
            Log.v(LOG_TAG, "Picture Actual Size - w: " + mPictureSize.width + ", h: " + mPictureSize.height);
        }

        setCamFocusMode();
        mCamera.setParameters(cameraParams);
    }

    private void setCamFocusMode() {
        if (null == mCamera) {
            return;
        }

    /* Set Auto focus */
        List<String> focusModes = cameraParams.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (!mActivity.isFinishing()) {
            stop();
        }
    }

    public void stop() {
        if (null == mCamera) {
            return;
        }
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;

        // Destroy previuos Holder
        if (mHolder != null) {
            surfaceDestroyed(mHolder);
            mHolder.removeCallback(CameraPreview.this);
        }
    }

    //    public void setOneShotPreviewCallback(PreviewCallback callback) {
    //        if (null == mCamera) {
    //            return;
    //        }
    //        mCamera.setOneShotPreviewCallback(callback);
    //    }
    //
    //    public void setPreviewCallback(PreviewCallback callback) {
    //        if (null == mCamera) {
    //            return;
    //        }
    //        mCamera.setPreviewCallback(callback);
    //    }

    public boolean isPortrait() {
        return (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    //    public Camera.Size getPreviewSize() {
    //        return mPreviewSize;
    //    }
    //
    //    public void setOnPreviewReady(PreviewReadyCallback cb) {
    //        mPreviewReadyCallback = cb;
    //    }

    public void setPictureCallback(Camera.PictureCallback callback) {
        if (null == mCamera) {
            return;
        }
        mCamera.takePicture(null, null, callback);
    }

    //open front mCamera if available
    public void openFrontCam() {
        if (mCamera != null && Camera.getNumberOfCameras() >= 2) {
            mCamera.stopPreview();
            mCamera.release();
            //"camera_position" is just an integer flag

            switch (camera_position_landscape) {
                case Camera.CameraInfo.CAMERA_FACING_BACK:
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    camera_position_landscape = 1;
                    frontCamera = true;
                    break;
                case Camera.CameraInfo.CAMERA_FACING_FRONT:
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    camera_position_landscape = 0;
                    frontCamera = false;
                    break;
            }

            try {
                //                mCamera = Camera.open(camera_position_landscape);
                mCamera.setPreviewDisplay(mHolder);
                Camera.Parameters cameraParams = mCamera.getParameters();

                DisplayMetrics displayMetrics = new DisplayMetrics();
                mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height1 = displayMetrics.heightPixels;
                int width1 = displayMetrics.widthPixels;
//            Camera.Size previewSize = determinePreviewSize(portrait, width, height);
//            Camera.Size pictureSize = determinePictureSize(previewSize);

                Camera.Size previewSize = getBestPreviewSize(width1, height1, cameraParams);
//            Camera.Size pictureSize = determinePictureSize(previewSize);
                Camera.Size pictureSize = getBestPictureSize(mCamera, cameraParams.getSupportedPictureSizes(), true, width1, height1);
                if (DEBUGGING) {
                    Log.v(LOG_TAG, "Desired Preview Size - w: " + width1 + ", h: " + height1);
                }
                mPreviewSize = previewSize;
                mPictureSize = pictureSize;

                if (cameraParams.getSupportedPreviewSizes() != null) {
                    cameraParams.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                }

                if (cameraParams.getSupportedPictureSizes() != null) {
                    cameraParams.setPictureSize(mPictureSize.width, mPictureSize.height);
                }
                mCamera.setParameters(cameraParams);

//                mPreviewSizeList = cameraParams.getSupportedPreviewSizes();
//                mPictureSizeList = cameraParams.getSupportedPictureSizes();
//                mCamera.setParameters(cameraParams);
                this.surfaceChanged(mHolder, 0, mPreviewSize.width, mPictureSize.height);
            } catch (IOException exception) {
                mCamera.release();
            }


            //            try {
            //                initCamera(camera_position_landscape);
            //                mCamera.setPreviewDisplay(mHolder);
            //                this.surfaceChanged(mHolder, 0, mPreviewSize.width, mPictureSize.height);
            //            } catch (IOException e) {
            //                mCamera.release();
            //                mCamera = null;
            //            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onMediaScannerConnected() {

    }

    @Override
    public void onScanCompleted(String path, Uri uri) {

    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return (result);
    }

    interface PreviewReadyCallback {
        void onPreviewReady();
    }
}
