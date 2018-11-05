package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.database.models.HappeningNow;
import com.exceedgulf.alainzoo.databinding.RowHappeningNowBinding;
import com.exceedgulf.alainzoo.fragments.HappeningNowDetailFragment;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HappeningsNowAdapter extends RecyclerView.Adapter<HappeningsNowAdapter.ViewHolder> {
    private Context context;
    private ArrayList<HappeningNow> happeningNowArrayList;
    private static final String BUNDLE_KEY_HAPPENING_NOW = "happeningnow";

    public HappeningsNowAdapter(final Context context) {
        this.context = context;
        happeningNowArrayList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RowHappeningNowBinding rowHappeningNowBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_happening_now, parent, false);
        return new ViewHolder(rowHappeningNowBinding);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final HappeningNow happeningNow = happeningNowArrayList.get(position);
        holder.rowHappeningNowBinding.rowHappeTvtitle.setText(Html.fromHtml(happeningNow.getName()));
        ImageUtil.loadImageFromPicasso(context, happeningNow.getImage().trim(), holder.rowHappeningNowBinding.rowHappeIvmain,holder.rowHappeningNowBinding.rowHappeIvplaceholder);

        long startDiff = getTimeDiff(happeningNow.getSchedule().getTimes().get(0).getStartTime());
        long endDiff = getTimeDiff(happeningNow.getSchedule().getTimes().get(0).getEndTime());


        //Log.e("Home", "diffstart:" + "hours:" + diffStart.first + "mins:" + diffStart.second);
        //og.e("Home", "diffend:" + "hours:" + diffEnd.first + "mins:" + diffEnd.second);

        if (startDiff < 0 && endDiff < 0) {
            holder.rowHappeningNowBinding.rowHappeTvsubtitle.setText(context.getString(R.string.closed));
        } else if (startDiff < 0 && endDiff > 0) {
            holder.rowHappeningNowBinding.rowHappeTvsubtitle.setText(context.getString(R.string.happening_now));
        } else if (startDiff > 0 && endDiff > 0) {
            Pair<Integer, Integer> diff = getDiffHoursAndMin(startDiff);
            if (diff.first == 0) {

                holder.rowHappeningNowBinding.rowHappeTvsubtitle.setText(String.format("%s %d %s", context.getString(R.string.start_within), Math.abs(diff.second), context.getString(R.string.mins)));
            } else if (diff.first > 0) {
                holder.rowHappeningNowBinding.rowHappeTvsubtitle.setText(String.format("%s %d %s", context.getString(R.string.start_within), diff.first, context.getString(R.string.hours)));
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HappeningNowDetailFragment happeningNowDetailFragment = new HappeningNowDetailFragment();
                final Bundle happeningNowBundle = new Bundle();
                happeningNowBundle.putSerializable(BUNDLE_KEY_HAPPENING_NOW, happeningNow);
                happeningNowDetailFragment.setArguments(happeningNowBundle);
                ((HomeActivity) context).addFragment(((HomeActivity) context).getcurrentFragment(), happeningNowDetailFragment);
            }
        });
    }

    private long getTimeDiff(String happeningNow) {
        Calendar c = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy HH:mm");
        final Date currentDate = c.getTime();
        final String currentTime = dateFormat.format(currentDate);
        Log.e("Home", "currentTime" + currentTime);
        try {

            final SimpleDateFormat formatter_to = new SimpleDateFormat("HH:mm");

            final Date d = formatter_to.parse(happeningNow);

            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

            final Date serverDate = calendar.getTime();

            long difference = serverDate.getTime() - currentDate.getTime();

            return difference;
            //return getDiffHoursAndMin(currentDate, serverDate, difference);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @NonNull
    private Pair<Integer, Integer> getDiffHoursAndMin(long difference) {
        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        hours = (hours < 0 ? -hours : hours);
//        Log.e("======= Hours", " current: " + currentDate + "server:" + serverDate);
//        Log.e("======= Hours", " :: " + hours + "min:" + min);
        return new Pair<>(hours, min);
    }

    @Override
    public int getItemCount() {
        return happeningNowArrayList.size();
    }

    public void addItems(final ArrayList<HappeningNow> happeningNowArrayList) {
        this.happeningNowArrayList.addAll(happeningNowArrayList);
        this.notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private RowHappeningNowBinding rowHappeningNowBinding;

        public ViewHolder(final RowHappeningNowBinding rowHappeningNowBinding) {
            super(rowHappeningNowBinding.getRoot());
            this.rowHappeningNowBinding = rowHappeningNowBinding;
        }
    }


}