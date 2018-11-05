package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.exceedgulf.alainzoo.R;


public class ViewPagerAdapter extends PagerAdapter {
    int[] mResources = {
            R.drawable.tutorial1,
            R.drawable.tutorial2,
            R.drawable.tutorial7,
            R.drawable.tutorial3,
            R.drawable.tutorial4,
            R.drawable.tutorial5,
            R.drawable.tutorial6,
    };
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private OnClickImageListener imageListener;

    public ViewPagerAdapter(Context context, OnClickImageListener imageListener) {
        mContext = context;
        this.imageListener = imageListener;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = null;
        if (position == 0) {
            view = mLayoutInflater.inflate(R.layout.tutorial_layout_1, container, false);
        } else if (position == 1) {
            view = mLayoutInflater.inflate(R.layout.tutorial_layout_2, container, false);
        } else if (position == 2) {
            view = mLayoutInflater.inflate(R.layout.tutorial_layout_3, container, false);
        } else if (position == 3) {
            view = mLayoutInflater.inflate(R.layout.tutorial_layout_4, container, false);
        } else if (position == 4) {
            view = mLayoutInflater.inflate(R.layout.tutorial_layout_5, container, false);
        } else if (position == 5) {
            view = mLayoutInflater.inflate(R.layout.tutorial_layout_6, container, false);
        } else if (position == 6) {
            view = mLayoutInflater.inflate(R.layout.tutorial_layout_7, container, false);
        }
        //final View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
        final Button btnSKip = view.findViewById(R.id.tutorial_skip);
//        imageView.setImageResource(mResources[position]);
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        btnSKip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageListener.onSkipClick();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageListener.onClickChange(position);
            }
        });

//        Configuration config = mContext.getResources().getConfiguration();
//        if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
//            //RTL
//        }


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public interface OnClickImageListener {
        void onClickChange(int position);

        void onSkipClick();
    }

}