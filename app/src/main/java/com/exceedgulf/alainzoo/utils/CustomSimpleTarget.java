package com.exceedgulf.alainzoo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.ExploreZoo;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.ArrayList;

/**
 * Created by P.P on 19/02/18.
 */

public class CustomSimpleTarget extends SimpleTarget<Bitmap> {

    private ArrayList<Marker> markerArrayList;
    private int position;
    private ExploreZoo exploreZoo;
    private Context context;
    private MapboxMap mapboxMap;

    public CustomSimpleTarget(Context context, ArrayList<Marker> markerArrayList, int position, ExploreZoo exploreZoo, MapboxMap mapboxMap) {
        this.context = context;
        this.markerArrayList = markerArrayList;
        this.position = position;
        this.exploreZoo = exploreZoo;
        this.mapboxMap = mapboxMap;
    }

    @Override
    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
        final Bitmap resized = Bitmap.createScaledBitmap(resource, 120, 120, false);
        final int visiteItem = AlainZooDB.getInstance().myPlaneVisitedItemDao().isVisited(exploreZoo.getId(), exploreZoo.getType());
        if (visiteItem != 0) {
            final Paint paint = new Paint();
            final ColorFilter filter = new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.colorBgSeeInfo), PorterDuff.Mode.SRC_IN);
            paint.setColorFilter(filter);
            final Canvas canvas = new Canvas(resized);
            canvas.drawBitmap(resized, 0, 0, paint);
        }
        final MarkerOptions markerOptions = new com.mapbox.mapboxsdk.annotations.MarkerOptions()
                .position(new com.mapbox.mapboxsdk.geometry.LatLng(Double.parseDouble(exploreZoo.getLatitude()), Double.parseDouble(exploreZoo.getLongitude())))
                .setSnippet(String.valueOf(position))
                .setIcon(IconFactory.getInstance(context).fromBitmap(resized));
        markerArrayList.add(mapboxMap.addMarker(markerOptions));
        Log.e("Success", "Marker");
    }
}
