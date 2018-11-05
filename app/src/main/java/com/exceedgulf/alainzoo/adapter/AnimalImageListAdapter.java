package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.models.AnimalImages;
import com.exceedgulf.alainzoo.databinding.RowAnimalImageListBinding;
import com.exceedgulf.alainzoo.models.Image;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.util.ArrayList;

/**
 * Created by R.S. on 19/12/17
 */
public class AnimalImageListAdapter extends RecyclerView.Adapter<AnimalImageListAdapter.ImageListViewHolder> {

    private ArrayList<AnimalImages> animalImagesArrayList;
    private Context mContext;
    private OnItemClick onItemClick;

    public AnimalImageListAdapter(final Context mContext, final OnItemClick onItemClick) {
        this.animalImagesArrayList = new ArrayList<>();
        this.mContext = mContext;
        this.onItemClick = onItemClick;
    }


    @Override
    public ImageListViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final RowAnimalImageListBinding rowAnimalImageListBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_animal_image_list, parent, false);
        return new ImageListViewHolder(rowAnimalImageListBinding);
    }

    @Override
    public void onBindViewHolder(final ImageListViewHolder holder, final int position) {
        final AnimalImages animalImages = animalImagesArrayList.get(position);
        ImageUtil.loadImageFromPicasso(mContext, animalImages.getImage(), holder.getRowAnimalImageListBinding().rowAnimdeSubIvmain, holder.getRowAnimalImageListBinding().rowAnimdeSubIvplaceholder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClick != null) {
                    onItemClick.OnSelectedItem(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return animalImagesArrayList.size();
    }


    public class ImageListViewHolder extends RecyclerView.ViewHolder {
        private RowAnimalImageListBinding rowAnimalImageListBinding;

        ImageListViewHolder(final RowAnimalImageListBinding rowAnimalImageListBinding) {
            super(rowAnimalImageListBinding.getRoot());
            this.rowAnimalImageListBinding = rowAnimalImageListBinding;
        }

        public RowAnimalImageListBinding getRowAnimalImageListBinding() {
            return rowAnimalImageListBinding;
        }
    }

    public void addItems(final ArrayList<AnimalImages> animalImagesArrayList) {
        this.animalImagesArrayList.clear();
        this.animalImagesArrayList.trimToSize();
        for (final AnimalImages animalImages : animalImagesArrayList) {
            if (!TextUtils.isEmpty(animalImages.getImage())) {
                this.animalImagesArrayList.add(animalImages);
            }
        }
        this.notifyDataSetChanged();
    }

    public interface OnItemClick {
        void OnSelectedItem(final int position);
    }

}