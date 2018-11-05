package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.models.AnimalsCategory;
import com.exceedgulf.alainzoo.databinding.RowAnimalsFilterBinding;
import com.exceedgulf.alainzoo.databinding.RowTypeAnimalsFilterBinding;

import java.util.ArrayList;


public class AnimalsCategorysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final AnimalsCategory animalsCategoryTmp = new AnimalsCategory();
    final AnimalsCategory animalsCategoryCelebrity = new AnimalsCategory();
    private Context context;
    private ArrayList<AnimalsCategory> animalsCategoryArrayList;
    private AnimalsCategory selectedAnimalsCategory = new AnimalsCategory();
    private OnSelectedCategory onSelectedCategory;
    private boolean isFromAnimalScreen = false;
    private Typeface typefaceNormal;
    private Typeface typefaceBold;


    public AnimalsCategorysAdapter(final Context context, final OnSelectedCategory onSelectedCategory, final boolean isFromAnimalScreen) {
        this.context = context;
        typefaceNormal = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.avenir_next_regular));
        typefaceBold = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.avenir_next_demi_bold));
        animalsCategoryArrayList = new ArrayList<>();
        this.onSelectedCategory = onSelectedCategory;
        this.isFromAnimalScreen = isFromAnimalScreen;
        animalsCategoryTmp.setId(-1);
        animalsCategoryTmp.setTitle(context.getString(R.string.all));

        //selectedAnimalsCategory.setId(-1);
        //selectedAnimalsCategory.setTitle(context.getString(R.string.all));

        animalsCategoryCelebrity.setId(-2);
        animalsCategoryCelebrity.setTitle(context.getString(R.string.celebrities));

        if (isFromAnimalScreen) {
            selectedAnimalsCategory.setId(-2);
            selectedAnimalsCategory.setTitle(context.getString(R.string.celebrities));
        } else {
            selectedAnimalsCategory.setId(-1);
            selectedAnimalsCategory.setTitle(context.getString(R.string.all));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isFromAnimalScreen) {
            final RowAnimalsFilterBinding rowAnimalsFilterBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_animals_filter, parent, false);
            return new ViewHolder(rowAnimalsFilterBinding);
        } else {
            final RowTypeAnimalsFilterBinding rowTypeAnimalsFilterBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_type_animals_filter, parent, false);
            return new TypeViewHolder(rowTypeAnimalsFilterBinding);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            final AnimalsCategory animalsCategory = animalsCategoryArrayList.get(position);
            viewHolder.getRowAnimalsFilterBinding().rowAnimcatChkbox.setText(animalsCategory.getTitle());
            final boolean isChecked = ((selectedAnimalsCategory != null) && selectedAnimalsCategory.getId() == animalsCategory.getId());
            viewHolder.getRowAnimalsFilterBinding().rowAnimcatChkbox.setTypeface(isChecked ? typefaceBold : typefaceNormal);
            viewHolder.getRowAnimalsFilterBinding().rowAnimcatChkbox.setChecked(isChecked);
            viewHolder.getRowAnimalsFilterBinding().rowAnimcatChkbox.setTextColor(isChecked ? ContextCompat.getColor(context, R.color.white) : ContextCompat.getColor(context, R.color.light_grey_green));
            viewHolder.getRowAnimalsFilterBinding().rowAnimcatChkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.getRowAnimalsFilterBinding().rowAnimcatChkbox.isChecked()) {
                        selectedAnimalsCategory = animalsCategory;
                        viewHolder.getRowAnimalsFilterBinding().rowAnimcatChkbox.setTextColor(ContextCompat.getColor(context, R.color.white));

                    } else {
                        selectedAnimalsCategory = animalsCategoryCelebrity;
                        viewHolder.getRowAnimalsFilterBinding().rowAnimcatChkbox.setTextColor(ContextCompat.getColor(context, R.color.light_grey_green));

                    }
                    onSelectedCategory.onSelectedItem(selectedAnimalsCategory);
                    notifyDataSetChanged();
                }
            });
        } else if (holder instanceof TypeViewHolder) {
            final TypeViewHolder viewHolder = (TypeViewHolder) holder;
            final AnimalsCategory animalsCategory = animalsCategoryArrayList.get(position);
            viewHolder.getRowTypeAnimalsFilterBinding().rowAnimcatChkbox.setText(animalsCategory.getTitle());
            final boolean isChecked = ((selectedAnimalsCategory != null) && selectedAnimalsCategory.getId() == animalsCategory.getId());
            viewHolder.getRowTypeAnimalsFilterBinding().rowAnimcatChkbox.setTypeface(isChecked ? typefaceBold : typefaceNormal);
            viewHolder.getRowTypeAnimalsFilterBinding().rowAnimcatChkbox.setChecked(isChecked);
            viewHolder.getRowTypeAnimalsFilterBinding().rowAnimcatChkbox.setTextColor(isChecked ? ContextCompat.getColor(context, R.color.white) : ContextCompat.getColor(context, R.color.light_grey_blue));
            viewHolder.getRowTypeAnimalsFilterBinding().rowAnimcatChkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.getRowTypeAnimalsFilterBinding().rowAnimcatChkbox.isChecked()) {
                        selectedAnimalsCategory = animalsCategory;
                        viewHolder.getRowTypeAnimalsFilterBinding().rowAnimcatChkbox.setTextColor(ContextCompat.getColor(context, R.color.white));

                    } else {
                        selectedAnimalsCategory = animalsCategoryCelebrity;
                        viewHolder.getRowTypeAnimalsFilterBinding().rowAnimcatChkbox.setTextColor(ContextCompat.getColor(context, R.color.light_grey_blue));

                    }
                    onSelectedCategory.onSelectedItem(selectedAnimalsCategory);
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return animalsCategoryArrayList.size();
    }

    public void addItem(final ArrayList<AnimalsCategory> animalsCategoryArrayList) {
        this.animalsCategoryArrayList.clear();
        this.animalsCategoryArrayList.trimToSize();

        this.animalsCategoryArrayList.add(animalsCategoryTmp);
        this.animalsCategoryArrayList.add(animalsCategoryCelebrity);
        this.animalsCategoryArrayList.addAll(animalsCategoryArrayList);

        this.notifyDataSetChanged();
    }

    public interface OnSelectedCategory {
        void onSelectedItem(final AnimalsCategory animalsCategory);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RowAnimalsFilterBinding rowAnimalsFilterBinding;

        public ViewHolder(RowAnimalsFilterBinding rowAnimalsFilterBinding) {
            super(rowAnimalsFilterBinding.getRoot());
            this.rowAnimalsFilterBinding = rowAnimalsFilterBinding;
        }

        public RowAnimalsFilterBinding getRowAnimalsFilterBinding() {
            return rowAnimalsFilterBinding;
        }
    }

    public class TypeViewHolder extends RecyclerView.ViewHolder {
        private RowTypeAnimalsFilterBinding rowTypeAnimalsFilterBinding;

        public TypeViewHolder(RowTypeAnimalsFilterBinding rowTypeAnimalsFilterBinding) {
            super(rowTypeAnimalsFilterBinding.getRoot());
            this.rowTypeAnimalsFilterBinding = rowTypeAnimalsFilterBinding;
        }

        public RowTypeAnimalsFilterBinding getRowTypeAnimalsFilterBinding() {
            return rowTypeAnimalsFilterBinding;
        }
    }


}