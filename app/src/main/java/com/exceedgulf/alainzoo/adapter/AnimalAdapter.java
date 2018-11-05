package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.databinding.RowAnimalBinding;
import com.exceedgulf.alainzoo.fragments.AnimalDetailFragment;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.util.ArrayList;

/**
 * Created by R.S. on 19/12/17
 */
public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder> {
    private Context mContext;
    private ArrayList<Animal> animalArrayList;

    public AnimalAdapter(final Context mContext) {
        this.mContext = mContext;
        this.animalArrayList = new ArrayList<>();
    }

    public ArrayList<Animal> getAnimalArrayList() {
        return animalArrayList;
    }

    @Override
    public AnimalViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final RowAnimalBinding rowAnimalBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_animal, parent, false);
        return new AnimalViewHolder(rowAnimalBinding);
    }

    @Override
    public void onBindViewHolder(final AnimalViewHolder holder, final int position) {
        final Animal animal = animalArrayList.get(position);
        holder.getRowAnimalBinding().rowAnimalTvdetail.setText(Html.fromHtml(animal.getDetails()));
        holder.getRowAnimalBinding().rowAnimalTvname.setText(Html.fromHtml(animal.getName()));
        ImageUtil.loadImageFromPicasso(mContext, animal.getThumbnail().trim(), holder.getRowAnimalBinding().rowAnimalIvmain, holder.getRowAnimalBinding().rowAnimalIvplaceholder);
        holder.getRowAnimalBinding().rowAnimalBtnreadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).addFragment(((HomeActivity) mContext).getcurrentFragment(), AnimalDetailFragment.getAnimalDetailFragment(animal));
            }
        });
        holder.getRowAnimalBinding().rowAnimalIvmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).addFragment(((HomeActivity) mContext).getcurrentFragment(), AnimalDetailFragment.getAnimalDetailFragment(animal));
            }
        });
    }

    @Override
    public int getItemCount() {
        return animalArrayList.size();
    }

    class AnimalViewHolder extends RecyclerView.ViewHolder {
        private RowAnimalBinding rowAnimalBinding;

        AnimalViewHolder(final RowAnimalBinding rowAnimalBinding) {
            super(rowAnimalBinding.getRoot());
            this.rowAnimalBinding = rowAnimalBinding;

        }

        public RowAnimalBinding getRowAnimalBinding() {
            return rowAnimalBinding;
        }
    }

    public void addItems(final ArrayList<Animal> animalArrayList) {
        this.animalArrayList.addAll(animalArrayList);
        this.notifyDataSetChanged();
    }

    public void clearItems() {
        this.animalArrayList.clear();
        this.animalArrayList.trimToSize();
        this.notifyDataSetChanged();
    }

}