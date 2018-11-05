package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.models.HintsItem;
import com.exceedgulf.alainzoo.database.models.TreasureHunt;
import com.exceedgulf.alainzoo.databinding.RowTreasureHuntBinding;
import com.exceedgulf.alainzoo.utils.LangUtils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by P.G. on 02/06/18
 */
public class TreasureHuntAdapter extends RecyclerView.Adapter<TreasureHuntAdapter.TreasureHuntHolder> {

    private Context mContext;
    private ArrayList<HintsItem> hintsItemArrayList;
    private int size = 0;

    public TreasureHuntAdapter(final Context mContext) {
        this.mContext = mContext;
        hintsItemArrayList = new ArrayList<>();
    }

    public ArrayList<HintsItem> getHintsItemArrayList() {
        return hintsItemArrayList;
    }

    @Override
    public TreasureHuntHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        // create a new view
        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final RowTreasureHuntBinding rowTreasureHuntBinding = DataBindingUtil.inflate(layoutInflater, R.layout.row_treasure_hunt, parent, false);
        return new TreasureHuntHolder(rowTreasureHuntBinding);
    }

    @Override
    public void onBindViewHolder(final TreasureHuntHolder holder, final int position) {
        final HintsItem hintsItem = hintsItemArrayList.get(position);
        holder.getRowTreasureHuntBinding().rowTresTvTitle.setText((LangUtils.getCurrentLanguage().equalsIgnoreCase("en")) ? hintsItem.getTitle() : hintsItem.getTitle_ar());
        holder.getRowTreasureHuntBinding().rowTresTvHintId.setText(String.valueOf(hintsItem.getId()));
        holder.getRowTreasureHuntBinding().rowTresTvDescription.setText((LangUtils.getCurrentLanguage().equalsIgnoreCase("en")) ? hintsItem.getMessageEn() : hintsItem.getMessageAr());
        if (!TextUtils.isEmpty(hintsItem.getStatus())) {
            if (hintsItem.getStatus().equalsIgnoreCase("completed")) {
                holder.getRowTreasureHuntBinding().rowTresChMain.setChecked(true);
            } else {
                holder.getRowTreasureHuntBinding().rowTresChMain.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        int tmpSize = 2;
        int tmpCom = 0;

        for (HintsItem hintsItem : hintsItemArrayList) {
            if (!TextUtils.isEmpty(hintsItem.getStatus()) && hintsItem.getStatus().equalsIgnoreCase("completed")) {
                tmpCom++;
            }
        }
        if (tmpCom < hintsItemArrayList.size() && hintsItemArrayList.size() > 0) {
            int count = (tmpCom * 2) + tmpSize;
            if (count < hintsItemArrayList.size()) {
                return count;
            } else {
                return hintsItemArrayList.size();
            }
        } else {
            return hintsItemArrayList.size();
        }
    }

    public void addItem(ArrayList resultList) {
        hintsItemArrayList.addAll(resultList);
        notifyDataSetChanged();
    }

    class TreasureHuntHolder extends RecyclerView.ViewHolder {
        private RowTreasureHuntBinding rowTreasureHuntBinding;

        TreasureHuntHolder(final RowTreasureHuntBinding rowTreasureHuntBinding) {
            super(rowTreasureHuntBinding.getRoot());
            this.rowTreasureHuntBinding = rowTreasureHuntBinding;
        }

        public RowTreasureHuntBinding getRowTreasureHuntBinding() {
            return rowTreasureHuntBinding;
        }
    }

}