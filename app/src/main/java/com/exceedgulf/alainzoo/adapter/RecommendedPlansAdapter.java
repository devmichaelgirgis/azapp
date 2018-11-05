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
import com.exceedgulf.alainzoo.databinding.RowPlansBinding;
import com.exceedgulf.alainzoo.fragments.PlanVisitDetailFragment;
import com.exceedgulf.alainzoo.models.RecommendedPlanModel;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.util.ArrayList;

/**
 * Created by R.S. on 19/12/17
 */
public class RecommendedPlansAdapter extends RecyclerView.Adapter<RecommendedPlansAdapter.PlanViewHolder> {
    private Context mContext;
    private ArrayList<RecommendedPlanModel> recommendedPlanModels;

    public RecommendedPlansAdapter(final Context mContext) {
        this.mContext = mContext;
        recommendedPlanModels = new ArrayList<>();
    }


    @Override
    public PlanViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final RowPlansBinding rowPlansBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_plans, parent, false);
        return new PlanViewHolder(rowPlansBinding);
    }

    @Override
    public void onBindViewHolder(final PlanViewHolder holder, final int position) {
        final RecommendedPlanModel recommendedPlanModel = recommendedPlanModels.get(position);
        holder.getRowPlansBinding().rowPlanTvname.setText(Html.fromHtml(recommendedPlanModel.getName()));
        ImageUtil.loadImageFromPicasso(mContext, recommendedPlanModel.getImage(), holder.getRowPlansBinding().rowPlanIvmain, holder.getRowPlansBinding().rowPlanIvplaceholder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).addFragment(((HomeActivity) mContext).getcurrentFragment(), PlanVisitDetailFragment.getPlanYourVisitFragment(recommendedPlanModel));
            }
        });
        holder.getRowPlansBinding().rowPlanTvSeeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).addFragment(((HomeActivity) mContext).getcurrentFragment(), PlanVisitDetailFragment.getPlanYourVisitFragment(recommendedPlanModel));
            }
        });

    }

    @Override
    public int getItemCount() {
        return recommendedPlanModels.size();
    }


    class PlanViewHolder extends RecyclerView.ViewHolder {
        private RowPlansBinding rowPlansBinding;

        PlanViewHolder(final RowPlansBinding rowPlansBinding) {
            super(rowPlansBinding.getRoot());
            this.rowPlansBinding = rowPlansBinding;

        }

        public RowPlansBinding getRowPlansBinding() {
            return rowPlansBinding;
        }
    }

    public void addItems(final ArrayList<RecommendedPlanModel> recommendedPlanModelArrayList) {
        recommendedPlanModels.addAll(recommendedPlanModelArrayList);
        this.notifyDataSetChanged();
    }
}