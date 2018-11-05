package com.exceedgulf.alainzoo.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.exceedgulf.alainzoo.R;


/**
 * Created by PG on 04/04/17.
 */

public class CustomImageView extends ImageView {


    public CustomImageView(Context context) {
        super(context);

    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //float radius = 36.0f;
        final int radius = (int) getResources().getDimension(R.dimen._6sdp);
        @SuppressLint("DrawAllocation") Path clipPath = new Path();
        @SuppressLint("DrawAllocation") RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }
}
