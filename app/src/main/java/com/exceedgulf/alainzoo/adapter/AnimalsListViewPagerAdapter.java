package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.models.AnimalImages;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AnimalsListViewPagerAdapter extends PagerAdapter {
    private ArrayList<AnimalImages> animalImagesArrayList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public AnimalsListViewPagerAdapter(final Context context) {
        mContext = context;
        animalImagesArrayList = new ArrayList<>();
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return animalImagesArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        final View itemView = mLayoutInflater.inflate(R.layout.row_animal_images, container, false);
        final ImageView imageView = itemView.findViewById(R.id.ivMain);
        if (!TextUtils.isEmpty(animalImagesArrayList.get(position).getImage())) {
            Picasso.with(mContext).load(animalImagesArrayList.get(position).getImage()).into(imageView);
        }
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((FrameLayout) object);
    }

    public void addItems(final ArrayList<AnimalImages> animalImagesArrayList) {
        this.animalImagesArrayList.clear();
        this.animalImagesArrayList.addAll(animalImagesArrayList);
        notifyDataSetChanged();
    }

}