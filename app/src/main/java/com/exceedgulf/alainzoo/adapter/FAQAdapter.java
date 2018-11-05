package com.exceedgulf.alainzoo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.models.Experience;
import com.exceedgulf.alainzoo.models.NotificationModel;

import java.util.ArrayList;

/**
 * Created by R.S. on 19/12/17
 */
public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.MyViewHolder> {

    private ArrayList<NotificationModel> models;
    private Context mContext;

    public FAQAdapter(final Context mContext) {
        this.models = new ArrayList<>();
        this.mContext = mContext;
    }

    public void addAllModel(final ArrayList<NotificationModel> models) {
        this.models.addAll(models);
        notifyDataSetChanged();
    }

    @Override
    public FAQAdapter.MyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_faq, parent, false);

        return new FAQAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FAQAdapter.MyViewHolder holder, final int position) {
        //holder.binding.setModel(models.get(position));
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtView;

        MyViewHolder(View view) {
            super(view);
            txtView = view.findViewById(R.id.row_faq_tv);
        }


        public void setData(Experience current, int position, FragmentManager fragMan, final Activity activity) {
            this.txtView.setText(current.getName().trim());
        }

        public void setListeners() {
        }

    }
}