package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.models.OpeningHours;
import com.exceedgulf.alainzoo.databinding.RowClosingHoursBinding;
import com.exceedgulf.alainzoo.databinding.RowOpeningHoursHomeBinding;
import com.exceedgulf.alainzoo.models.OpeningHourFront;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class OpeningHoursHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int OPENING_ZOO_ID_EN = 1353;
    private static final int OPENING_ZOO_ID_AR = 1500;
    private final int SZDLC = 1352;
    private final int ZOO = 1353;
    private Context context;
    private ArrayList<OpeningHourFront> openingHoursArrayList;

    public OpeningHoursHomeAdapter(Context context) {
        this.context = context;
        openingHoursArrayList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == -2) {
            final RowOpeningHoursHomeBinding rowOpeningHoursHomeBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_opening_hours_home, parent, false);
            return new OpeningHoursViewHolder(rowOpeningHoursHomeBinding);
        } else {
            final RowClosingHoursBinding closingHoursBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_closing_hours, parent, false);
            return new ClosingHoursViewHolder(closingHoursBinding);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OpeningHoursViewHolder) {
            final OpeningHoursViewHolder viewHolder = (OpeningHoursViewHolder) holder;
            final OpeningHourFront openingHours = openingHoursArrayList.get(position);
            if (!openingHours.getId().equals(OPENING_ZOO_ID_EN) && !openingHours.getId().equals(OPENING_ZOO_ID_AR)) {
                viewHolder.getRowOpeningHoursHomeBinding().rowOpeningHoursNameTv.setTextColor(ContextCompat.getColor(context, R.color.colorSzdcl));
                viewHolder.getRowOpeningHoursHomeBinding().rowOpeningHoursNameTv.setText(openingHours.getTitle());
                viewHolder.getRowOpeningHoursHomeBinding().rowOpeningHoursIv.setImageResource(R.drawable.ic_szdcl_new);
                viewHolder.getRowOpeningHoursHomeBinding().rowOpeningHoursTimeTv.setText(openingHours.getOpeningHours());
            } else {
                viewHolder.getRowOpeningHoursHomeBinding().rowOpeningHoursNameTv.setTextColor(ContextCompat.getColor(context, R.color.cool_blue));
                viewHolder.getRowOpeningHoursHomeBinding().rowOpeningHoursIv.setImageResource(R.drawable.ic_zoo_hours);
                viewHolder.getRowOpeningHoursHomeBinding().rowOpeningHoursNameTv.setText(openingHours.getTitle());
                viewHolder.getRowOpeningHoursHomeBinding().rowOpeningHoursTimeTv.setText(openingHours.getOpeningHours());
            }
        }
    }

    @Override
    public int getItemCount() {
        return openingHoursArrayList.size() == 0 ? 1 : openingHoursArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return openingHoursArrayList.size() == 0 ? -1 : -2;
    }

    public void addItems(final ArrayList<OpeningHourFront> openingHoursArrayList) {
        this.openingHoursArrayList.addAll(openingHoursArrayList);
        this.notifyDataSetChanged();
    }

    public void clearAllItems() {
        this.openingHoursArrayList.clear();
        notifyDataSetChanged();
    }

    class OpeningHoursViewHolder extends RecyclerView.ViewHolder {
        private RowOpeningHoursHomeBinding rowOpeningHoursHomeBinding;

        OpeningHoursViewHolder(final RowOpeningHoursHomeBinding rowOpeningHoursHomeBinding) {
            super(rowOpeningHoursHomeBinding.getRoot());
            this.rowOpeningHoursHomeBinding = rowOpeningHoursHomeBinding;
        }

        public RowOpeningHoursHomeBinding getRowOpeningHoursHomeBinding() {
            return rowOpeningHoursHomeBinding;
        }

    }

    class ClosingHoursViewHolder extends RecyclerView.ViewHolder {
        private RowClosingHoursBinding closingHoursBinding;

        ClosingHoursViewHolder(final RowClosingHoursBinding closingHoursBinding) {
            super(closingHoursBinding.getRoot());
            this.closingHoursBinding = closingHoursBinding;
        }

        public RowClosingHoursBinding getClosingHoursBinding() {
            return closingHoursBinding;
        }

    }

}
