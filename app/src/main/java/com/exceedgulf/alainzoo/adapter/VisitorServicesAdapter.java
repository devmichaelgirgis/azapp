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
import com.exceedgulf.alainzoo.database.models.VisitorService;
import com.exceedgulf.alainzoo.databinding.RowVisitorServicesBinding;
import com.exceedgulf.alainzoo.fragments.VisiterServicesDetailFragment;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.util.ArrayList;


public class VisitorServicesAdapter extends RecyclerView.Adapter<VisitorServicesAdapter.ViewHolder> {
    private Context context;
    private ArrayList<VisitorService> serviceArrayList;

    public VisitorServicesAdapter(final Context context) {
        this.context = context;
        serviceArrayList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RowVisitorServicesBinding rowVisitorServicesBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_visitor_services, parent, false);
        return new ViewHolder(rowVisitorServicesBinding);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final VisitorService visitorService = serviceArrayList.get(position);
        holder.getRowServicesBinding().rowServTvtitle.setText(Html.fromHtml(visitorService.getName()));
        holder.getRowServicesBinding().rowServTvsubtitle.setText(Html.fromHtml(visitorService.getDetails()));
        ImageUtil.loadImageFromPicasso(context, visitorService.getThumbnail(), holder.getRowServicesBinding().rowServIvMain, holder.getRowServicesBinding().rowServIvplaceholder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) context).addFragment(((HomeActivity) context).getcurrentFragment(), VisiterServicesDetailFragment.getVisitorServiceDetailFragment(visitorService));
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceArrayList.size();
    }

    public void addItems(ArrayList resultList) {
        serviceArrayList.addAll(resultList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RowVisitorServicesBinding rowVisitorServicesBinding;

        public ViewHolder(final RowVisitorServicesBinding rowVisitorServicesBinding) {
            super(rowVisitorServicesBinding.getRoot());
            this.rowVisitorServicesBinding = rowVisitorServicesBinding;
        }

        public RowVisitorServicesBinding getRowServicesBinding() {
            return rowVisitorServicesBinding;
        }
    }


}