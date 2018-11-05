package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.databinding.RowNotificationBinding;
import com.exceedgulf.alainzoo.models.NotificationModel;

import java.util.ArrayList;

/**
 * Created by R.S. on 19/12/17
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Holder> {

    private ArrayList<NotificationModel> notificationModelArrayList;
    private Context mContext;

    public NotificationAdapter(final Context mContext) {
        this.notificationModelArrayList = new ArrayList<>();
        this.mContext = mContext;
    }

    public void addAllModel(final ArrayList<NotificationModel> models) {
        this.notificationModelArrayList.addAll(models);
        this.notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        // create a new view
        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final RowNotificationBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.row_notification, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final NotificationModel notificationModel = notificationModelArrayList.get(position);
        holder.binding.rowNotificationTvText.setText(notificationModel.getTitle());
    }

    @Override
    public int getItemCount() {
        return notificationModelArrayList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private final RowNotificationBinding binding;

        Holder(final RowNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}