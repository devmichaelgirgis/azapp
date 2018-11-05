package com.exceedgulf.alainzoo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.ExploreZoo;
import com.exceedgulf.alainzoo.database.models.MyPlaneVisitedItem;
import com.exceedgulf.alainzoo.utils.NetUtil;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.ArrayList;

/**
 * Created by P.P on 01/02/18.
 */

public class InfoMapAdapter implements MapboxMap.InfoWindowAdapter {
    private static final String ANIMALS = "animals";
    private static final String EXPERIENCE = "experience";
    private View mView;
    private Activity mContext;
    private ArrayList<ExploreZoo> arrayList;
    private ArrayList<Marker> markerArrayList;
    private int width;
    private MapboxMap mapboxMap;
    private onSeeMoreListener onSeeMoreListener;
    private Integer planeId;

    public InfoMapAdapter(final Activity context, ArrayList<ExploreZoo> arrayList, ArrayList<Marker> markerArrayList, final MapboxMap mapboxMap, onSeeMoreListener onSeeMoreListener, Integer planeId) {
        mContext = context;
        this.arrayList = arrayList;
        this.markerArrayList = markerArrayList;
        this.mapboxMap = mapboxMap;
        this.onSeeMoreListener = onSeeMoreListener;
        mView = LayoutInflater.from(mContext).inflate(R.layout.row_info_window, null);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        this.planeId = planeId;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull final Marker marker) {
        final LinearLayout llParent = mView.findViewById(R.id.row_info_llParent);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    /*width*/ (int) (width * 0.7),
                   /*height*/ ViewGroup.LayoutParams.WRAP_CONTENT
        );
        llParent.setLayoutParams(param);
        llParent.invalidate();
        final TextView tvTitle = mView.findViewById(R.id.row_info_tvtitle);
        final TextView tvDescription = mView.findViewById(R.id.row_info_tvsubtitle);
        final TextView tvNavigation = mView.findViewById(R.id.row_info_tvNavigate);
        final TextView tvSeeInfo = mView.findViewById(R.id.row_info_tvSeeInfo);
        final AppCompatCheckBox cbVisited = mView.findViewById(R.id.row_info_cbvisited);

        final AppCompatImageView ivImage = mView.findViewById(R.id.row_info_ivImage);
        final String strPosition = marker.getSnippet();
        final ExploreZoo exploreZoo = arrayList.get(Integer.parseInt(strPosition));
        if (exploreZoo != null) {
            tvTitle.setText(Html.fromHtml(exploreZoo.getName()).toString().trim());
            tvDescription.setText(Html.fromHtml(exploreZoo.getBody()).toString().trim());

            final int visiteItem = AlainZooDB.getInstance().myPlaneVisitedItemDao().isVisited(exploreZoo.getId(), exploreZoo.getType());
            cbVisited.setChecked(visiteItem != 0);
            //cbVisited.setVisibility(planeId == null ? View.INVISIBLE : View.VISIBLE);

            Glide.with(mContext).load(exploreZoo.getThumbnail()).into(ivImage);
        }
        tvNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker.hideInfoWindow();
                if (NetUtil.isNetworkAvailable(mContext)) {
                    if (!NetUtil.isGPSEnable(mContext)) {
                        mContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        return;
                    }
                    if (mapboxMap == null) {
                        return;
                    }
                    final Location location = mapboxMap.getMyLocation();
                    if (location == null) {
                        SnackbarUtils.loadSnackBar(AppAlainzoo.getAppAlainzoo().getString(R.string.no_location_available), ((HomeActivity) mContext), R.color.reddish);
                        return;
                    }
                    final LatLng latLngZoo = new LatLng(24.179517, 55.739580);
                    final LatLng latLngDestination = new LatLng(location.getLatitude(), location.getLongitude());
                    final float distance = (int) (latLngZoo.distanceTo(latLngDestination));
                    if (distance <= 5000) {
                        onSeeMoreListener.startNavigationPlan(exploreZoo, marker);
                        final Point origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                        //final Point dest = Point.fromLngLat(marker.getPosition().getLongitude(), marker.getPosition().getLatitude());
                        final Point dest = Point.fromLngLat(Double.parseDouble(exploreZoo.getLongitude()), Double.parseDouble(exploreZoo.getLatitude()));
                        onSeeMoreListener.reRouteDraw(origin, dest);
                    } else {
                        ((HomeActivity) mContext).alertMessage("", mContext.getString(R.string.oops_away_from_zoo), true);
                    }
                } else {
                    SnackbarUtils.loadSnackBar(AppAlainzoo.getAppAlainzoo().getString(R.string.no_internet_), ((HomeActivity) mContext), R.color.reddish);
                }
            }
        });

        tvSeeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSeeMoreListener.seeMoreClick(exploreZoo);
            }
        });
        cbVisited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbVisited.isChecked()) {
                    final MyPlaneVisitedItem myPlaneVisitedItem = new MyPlaneVisitedItem();
                    myPlaneVisitedItem.setPlanId(planeId);
                    myPlaneVisitedItem.setVisitedItemId(exploreZoo.getId());
                    myPlaneVisitedItem.setVisitedItemType(exploreZoo.getType());
                    AlainZooDB.getInstance().myPlaneVisitedItemDao().insertItem(myPlaneVisitedItem);
                    if (marker.getIcon() != null && marker.getIcon().getBitmap() != null) {
                        final Bitmap tmpBitmap = marker.getIcon().getBitmap();
                        final Bitmap bmpMonochrome = Bitmap.createBitmap(tmpBitmap.getWidth(), tmpBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                        final Canvas canvas = new Canvas(bmpMonochrome);
                        final ColorMatrix ma = new ColorMatrix();
                        ma.setSaturation(0);
                        final Paint paint = new Paint();
                        paint.setColorFilter(new ColorMatrixColorFilter(ma));
                        canvas.drawBitmap(tmpBitmap, 0, 0, paint);
                        marker.setIcon(IconFactory.getInstance(mContext).fromBitmap(bmpMonochrome));
                    }

                } else {
                    if (!TextUtils.isEmpty(exploreZoo.getIcon())) {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(exploreZoo.getIcon())
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                        final Bitmap resized = Bitmap.createScaledBitmap(resource, 120, 120, false);
                                        marker.setIcon(IconFactory.getInstance(mContext).fromBitmap(resized));
                                    }
                                });
                    } else {
                        marker.setIcon(null);
                    }
                    AlainZooDB.getInstance().myPlaneVisitedItemDao().deleteItem(exploreZoo.getId(), exploreZoo.getType());
                }
                //onSeeMoreListener.reRouteDraw(null, null);
            }
        });
        return mView;
    }


    public interface onSeeMoreListener {
        void seeMoreClick(ExploreZoo exploreZoo);

        void startNavigationPlan(ExploreZoo exploreZoo, Marker marker);

        void reRouteDraw(final Point origin, Point dest);
    }

}
