package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.database.models.CameraFrame;
import com.exceedgulf.alainzoo.managers.ImageDownloadManager;
import com.exceedgulf.alainzoo.utils.ImageUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by P.P on 20/01/18.
 */

public class CameraFrameAdapter extends PagerAdapter {
    private ArrayList<CameraFrame> cameraFrameArrayList;

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public CameraFrameAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cameraFrameArrayList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return cameraFrameArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((RelativeLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final CameraFrame cameraFrame = cameraFrameArrayList.get(position);
        final View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
        final ImageView imageView = itemView.findViewById(R.id.imageView);
        if (!TextUtils.isEmpty(cameraFrame.getImage())) {
            final String imagePath = Constants.ZOO_CAMERA_FRAME + File.separator + ImageDownloadManager.getHashCodeBasedFileName(cameraFrame.getImage());
            final File file = new File(imagePath);
            if (file.exists()) {
                Glide.with(mContext).load(file).into(imageView);
            } else {

                Glide.with(mContext).asBitmap().load(cameraFrame.getImage()).listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        ImageDownloadManager.saveBitmapImage(resource, Constants.ZOO_CAMERA_FRAME, cameraFrame.getImage());
                        return false;
                    }
                }).into(imageView);

            }
        }
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }

    public void addAllItems(final ArrayList<CameraFrame> cameraFrameArrayList) {
        this.cameraFrameArrayList.clear();
        this.cameraFrameArrayList.addAll(cameraFrameArrayList);
        this.notifyDataSetChanged();
    }

}
