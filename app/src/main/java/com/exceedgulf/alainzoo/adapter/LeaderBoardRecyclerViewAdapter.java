package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.models.LeaderBoard;
import com.exceedgulf.alainzoo.databinding.RowLeaderboardBinding;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.util.ArrayList;
import java.util.Comparator;


public class LeaderBoardRecyclerViewAdapter extends RecyclerView.Adapter<LeaderBoardRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<LeaderBoard> leaderBoardArrayList;

    public LeaderBoardRecyclerViewAdapter(final Context context) {
        this.context = context;
        leaderBoardArrayList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RowLeaderboardBinding rowLeaderboardBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_leaderboard, parent, false);
        return new ViewHolder(rowLeaderboardBinding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LeaderBoard leaderBoard = leaderBoardArrayList.get(position);
        holder.getLeaderboardListItemBinding().leaderboardItemTvName.setText(leaderBoard.getUser().get(0).getName());
        holder.getLeaderboardListItemBinding().leaderboardItemTvPoints.setText(String.valueOf(leaderBoard.getPoints()) + " " + context.getString(R.string.st_points));
        ImageUtil.loadImageFromPicasso(context, leaderBoard.getUser().get(0).getAvatar(), holder.getLeaderboardListItemBinding().leaderboardItemIvProfileImage, holder.getLeaderboardListItemBinding().leaderboardItemIvPlaceHolder);
    }

    @Override
    public int getItemCount() {
        return leaderBoardArrayList.size();
    }

    public void addAllItems(ArrayList<LeaderBoard> leaderBoardArrayList) {
        this.leaderBoardArrayList.addAll(leaderBoardArrayList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RowLeaderboardBinding rowLeaderboardBinding;

        public ViewHolder(final RowLeaderboardBinding rowLeaderboardBinding) {
            super(rowLeaderboardBinding.getRoot());
            this.rowLeaderboardBinding = rowLeaderboardBinding;
        }

        public RowLeaderboardBinding getLeaderboardListItemBinding() {
            return rowLeaderboardBinding;
        }
    }


}