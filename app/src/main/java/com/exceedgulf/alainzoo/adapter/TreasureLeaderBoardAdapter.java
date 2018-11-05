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
import com.exceedgulf.alainzoo.database.models.LeaderBoard;
import com.exceedgulf.alainzoo.database.models.TreasureHunt;
import com.exceedgulf.alainzoo.databinding.RowTreasureLeaderboardBinding;
import com.exceedgulf.alainzoo.models.TreasurLeaderBoard;

import java.util.ArrayList;


public class TreasureLeaderBoardAdapter extends RecyclerView.Adapter<TreasureLeaderBoardAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TreasurLeaderBoard> treasurLeaderBoardArrayList;

    public TreasureLeaderBoardAdapter(final Context context) {
        this.context = context;
        treasurLeaderBoardArrayList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RowTreasureLeaderboardBinding rowLeaderboardBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_treasure_leaderboard, parent, false);
        return new ViewHolder(rowLeaderboardBinding);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final TreasurLeaderBoard treasurLeaderBoard = treasurLeaderBoardArrayList.get(position);
        holder.getRowLeaderboardBinding().rowTresureLeaderboardIvTrophy.setVisibility((treasurLeaderBoard.isIsshildshow()) ? View.VISIBLE : View.INVISIBLE);
        if (!TextUtils.isEmpty(treasurLeaderBoard.getAvatar())) {
            Glide.with(context).load(treasurLeaderBoard.getAvatar()).into(holder.getRowLeaderboardBinding().rowTresureLeaderboardIvuserprofilepic);
        }
        holder.getRowLeaderboardBinding().rowTresureLeaderboardTvName.setText(treasurLeaderBoard.getName());
        holder.getRowLeaderboardBinding().rowTresureLeaderboardTvPoints.setText(treasurLeaderBoard.getTotal_points() + " " + context.getString(R.string.st_points));
        if (!TextUtils.isEmpty(treasurLeaderBoard.getTotal_hints())) {
            holder.getRowLeaderboardBinding().rowTresureLeaderboardTvClues.setText(treasurLeaderBoard.getTotal_hints() + " " + context.getString(R.string.st_hints));
        } else {
            holder.getRowLeaderboardBinding().rowTresureLeaderboardTvClues.setText("0 " + context.getString(R.string.st_hints));
        }
        holder.getRowLeaderboardBinding().rowTresureLeaderboardTvRank.setText(treasurLeaderBoard.getRank() + " " + context.getString(R.string.st_ranks));
    }

    @Override
    public int getItemCount() {
        return treasurLeaderBoardArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RowTreasureLeaderboardBinding rowLeaderboardBinding;

        public ViewHolder(final RowTreasureLeaderboardBinding rowLeaderboardBinding) {
            super(rowLeaderboardBinding.getRoot());
            this.rowLeaderboardBinding = rowLeaderboardBinding;
        }

        public RowTreasureLeaderboardBinding getRowLeaderboardBinding() {
            return rowLeaderboardBinding;
        }
    }

    public void addItems(final ArrayList<TreasurLeaderBoard> treasurLeaderBoardArrayList) {
        this.treasurLeaderBoardArrayList.addAll(treasurLeaderBoardArrayList);
        this.notifyDataSetChanged();
    }

    public ArrayList<TreasurLeaderBoard> getTreasurLeaderBoardArrayList() {
        return treasurLeaderBoardArrayList;
    }
}