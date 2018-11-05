package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.models.ShuttleService;
import com.exceedgulf.alainzoo.databinding.RowShuttleServiceBinding;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by P.G. on 01/23/18
 */
public class ShuttleServiceAdapter extends RecyclerView.Adapter<ShuttleServiceAdapter.Holder> {

    private Context mContext;
    private int selectedPosition = -1;
    private onItemSelec onItemSelec;
    private ArrayList<ShuttleService> shuttleServiceArrayList;

    public ShuttleServiceAdapter(final Context mContext, final onItemSelec onItemSelec) {
        this.mContext = mContext;
        this.onItemSelec = onItemSelec;
        this.shuttleServiceArrayList = new ArrayList<>();
    }

    public ArrayList<ShuttleService> getShuttleServiceArrayList() {
        return shuttleServiceArrayList;
    }

    @Override
    public ShuttleServiceAdapter.Holder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final RowShuttleServiceBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_shuttle_service, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(final ShuttleServiceAdapter.Holder holder, final int position) {
        final ShuttleService shuttleService = shuttleServiceArrayList.get(position);
        if (LangUtils.getCurrentLanguage().equalsIgnoreCase("en")) {
            holder.getBinding().rowShuttleTvName.setText(shuttleService.getNameEn());
        } else if (LangUtils.getCurrentLanguage().equalsIgnoreCase("ar")) {
            holder.getBinding().rowShuttleTvName.setText(shuttleService.getNameAr());
        }
        if (shuttleService.getIcon() != null && !TextUtils.isEmpty(shuttleService.getIcon())) {
            Glide.with(mContext).load(shuttleService.getIcon()).into(holder.getBinding().rowShuttleIvMain);
        } else {
            holder.getBinding().rowShuttleIvMain.setImageResource(R.drawable.ic_zarafa_cafe);
        }
        //holder.getBinding().rowShuttleIvMain.setImageResource(shuttleService.getIcon());
//        if (position == 0) {
//            holder.getBinding().rowShuttleTvName.setText(shuttleService.getNameEn());
//            holder.getBinding().rowShuttleIvMain.setImageResource(R.drawable.ic_zarafa_cafe);
//
//        } else if (position == 1) {
//            holder.getBinding().rowShuttleTvName.setText("Main Gate");
//            holder.getBinding().rowShuttleIvMain.setImageResource(R.drawable.ic_main_gate);
//        } else if (position == 2) {
//            holder.getBinding().rowShuttleTvName.setText("Monkey Highest");
//            holder.getBinding().rowShuttleIvMain.setImageResource(R.drawable.ic_monkey_highest);
//        } else if (position == 3) {
//            holder.getBinding().rowShuttleTvName.setText("Zarafa Cafe");
//            holder.getBinding().rowShuttleIvMain.setImageResource(R.drawable.ic_zarafa_cafe_second);
//        } else if (position == 4) {
//            holder.getBinding().rowShuttleTvName.setText("Main Gate");
//            holder.getBinding().rowShuttleIvMain.setImageResource(R.drawable.ic_main_gate_second);
//        } else if (position == 5) {
//            holder.getBinding().rowShuttleTvName.setText("Monkey Highest");
//            holder.getBinding().rowShuttleIvMain.setImageResource(R.drawable.ic_monkey_highest_second);
//        }
        holder.getBinding().rowShuttleLlMain.setBackgroundResource(selectedPosition == position ? R.drawable.bg_shuttle_item_selected : R.drawable.bg_shuttle_item_border);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition == position) {
                    selectedPosition = -1;
                } else {
                    selectedPosition = position;
                }
                onItemSelec.onItemClick(selectedPosition);
                ShuttleServiceAdapter.this.notifyDataSetChanged();
            }
        });

    }

    public void addItems(final ArrayList<ShuttleService> shuttleServiceArrayList) {
        this.shuttleServiceArrayList.addAll(shuttleServiceArrayList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return shuttleServiceArrayList.size();
    }


    public interface onItemSelec {
        void onItemClick(final int position);
    }

    class Holder extends RecyclerView.ViewHolder {
        private final RowShuttleServiceBinding binding;

        Holder(final RowShuttleServiceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public RowShuttleServiceBinding getBinding() {
            return binding;
        }
    }

}