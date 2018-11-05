package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.databinding.RowBadgesBinding;
import com.exceedgulf.alainzoo.models.BadgeModel;

import java.util.ArrayList;

/**
 * Created by R.S. on 19/12/17
 */
public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.Holder> {

    private ArrayList<BadgeModel> models;
    private Context mContext;

    public BadgeAdapter(final Context mContext) {
        this.models = new ArrayList<>();
        this.mContext = mContext;
    }

    public void addAllModel(final ArrayList<BadgeModel> models) {
        this.models.addAll(models);
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        // create a new view
        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final RowBadgesBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.row_badges, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        holder.binding.setModel(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private final RowBadgesBinding binding;

        Holder(RowBadgesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}