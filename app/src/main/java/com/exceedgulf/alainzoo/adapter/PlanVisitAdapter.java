package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.databinding.RowNoPlanSelectedBinding;
import com.exceedgulf.alainzoo.databinding.RowPlanItemBinding;
import com.exceedgulf.alainzoo.models.PlanItem;

import java.util.ArrayList;

/**
 * Created by P.G. on 01/18/18
 */
public class PlanVisitAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int NO_SELECT_VIEW = -1;
    private final int SELECT_VIEW = 0;
    private ArrayList<T> visitPlanesModelArrayList;

    private Context mContext;
    private String emptyText = "";

    public PlanVisitAdapter(final Context mContext, final String emptyText) {
        this.visitPlanesModelArrayList = new ArrayList<>();

        this.mContext = mContext;
        this.emptyText = emptyText;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if (viewType == NO_SELECT_VIEW) {
            final RowNoPlanSelectedBinding rowNoPlanSelectedBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_no_plan_selected, parent, false);
            return new PlanVisitEmptyViewHolder(rowNoPlanSelectedBinding);
        } else {
            final RowPlanItemBinding rowPlanItemBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_plan_item, parent, false);
            return new PlanVisitViewHolder(rowPlanItemBinding);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PlanVisitAdapter.PlanVisitViewHolder) {
            final PlanVisitViewHolder planVisitViewHolder = (PlanVisitViewHolder) holder;
            final PlanItem planItem = (PlanItem) visitPlanesModelArrayList.get(holder.getAdapterPosition());
            planVisitViewHolder.getPlanItemBinding().rowPlanTvname.setText(Html.fromHtml(planItem.getTitle()));

        } else {
            final PlanVisitEmptyViewHolder planVisitEmptyViewHolder = (PlanVisitEmptyViewHolder) holder;
            planVisitEmptyViewHolder.getNoPlanSelectedBinding().rowplanTvnoplanselected.setText(emptyText);
        }
    }

    @Override
    public int getItemCount() {
        return visitPlanesModelArrayList.size() == 0 ? 1 : visitPlanesModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return visitPlanesModelArrayList.size() == 0 ? NO_SELECT_VIEW : SELECT_VIEW;
    }

    public void addAllModel(final ArrayList<T> visitPlansModelArrayList) {
        this.visitPlanesModelArrayList.clear();
        this.visitPlanesModelArrayList.trimToSize();
        this.visitPlanesModelArrayList.addAll(visitPlansModelArrayList);
        this.notifyDataSetChanged();
    }

    public void addSingleModel(final T singleModel) {
        this.visitPlanesModelArrayList.add(singleModel);
    }

    class PlanVisitViewHolder<T> extends RecyclerView.ViewHolder {
        private RowPlanItemBinding rowPlanItemBinding;

        PlanVisitViewHolder(final RowPlanItemBinding rowPlanItemBinding) {
            super(rowPlanItemBinding.getRoot());
            this.rowPlanItemBinding = rowPlanItemBinding;
        }

        public RowPlanItemBinding getPlanItemBinding() {
            return rowPlanItemBinding;
        }
    }

    class PlanVisitEmptyViewHolder<T> extends RecyclerView.ViewHolder {
        private RowNoPlanSelectedBinding rowNoPlanSelectedBinding;

        PlanVisitEmptyViewHolder(final RowNoPlanSelectedBinding rowNoPlanSelectedBinding) {
            super(rowNoPlanSelectedBinding.getRoot());
            this.rowNoPlanSelectedBinding = rowNoPlanSelectedBinding;
        }

        public RowNoPlanSelectedBinding getNoPlanSelectedBinding() {
            return rowNoPlanSelectedBinding;
        }
    }

    public void clearItems() {
        visitPlanesModelArrayList.clear();
        visitPlanesModelArrayList.trimToSize();
    }

    public ArrayList<T> getAllItems() {
        return visitPlanesModelArrayList;
    }

}