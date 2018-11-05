package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.databinding.RowSearchTypeBinding;
import com.exceedgulf.alainzoo.models.SearchTypeModel;

import java.util.ArrayList;

/**
 * Created by R.S. on 19/12/17
 */
public class SearchTypeAdapter extends RecyclerView.Adapter<SearchTypeAdapter.Holder> {

    public String selectedPosition;
    private ArrayList<SearchTypeModel> models;
    private Context mContext;
    private onTypeSelectionListener onTypeSelectionListener;

    public SearchTypeAdapter(final Context mContext, onTypeSelectionListener onTypeSelectionListener) {
        this.models = new ArrayList<>();
        this.mContext = mContext;
        this.onTypeSelectionListener = onTypeSelectionListener;
    }

    public void addAllModel(final ArrayList<SearchTypeModel> models) {
        this.models.addAll(models);
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        // create a new view
        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final RowSearchTypeBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.row_search_type, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final SearchTypeModel model = models.get(holder.getAdapterPosition());
        holder.binding.setModel(model);
        holder.binding.rowSearchTypeCtv.setChecked(model.isSelected());
        holder.binding.rowSearchTypeCtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (selectedPosition != null) {
                    models.get(Integer.parseInt(selectedPosition)).setSelected(false);
                    selectedPosition = null;
                }
                if (!model.isSelected()) {
                    selectedPosition = String.valueOf(position);
                    model.setSelected(true);
                } else {
                    model.setSelected(false);
                }
                //model.setSelected(!model.isSelected());
                notifyDataSetChanged();
                onTypeSelectionListener.onTypeClick(model.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public interface onTypeSelectionListener {
        void onTypeClick(String name);
    }

    class Holder extends RecyclerView.ViewHolder {
        private final RowSearchTypeBinding binding;

        Holder(RowSearchTypeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}