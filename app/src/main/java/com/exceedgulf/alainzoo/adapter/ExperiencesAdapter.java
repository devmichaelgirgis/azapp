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
import com.exceedgulf.alainzoo.database.models.Experience;
import com.exceedgulf.alainzoo.database.models.Vote;
import com.exceedgulf.alainzoo.databinding.RowExperienceSeeAllBinding;
import com.exceedgulf.alainzoo.fragments.ExperianceDetailFragment;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.util.ArrayList;
import java.util.Locale;


public class ExperiencesAdapter extends RecyclerView.Adapter<ExperiencesAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Experience> experienceArrayList;

    public ExperiencesAdapter(Context context) {
        this.context = context;
        experienceArrayList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final RowExperienceSeeAllBinding rowExperienceSeeAllBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_experience_see_all, parent, false);
        return new ViewHolder(rowExperienceSeeAllBinding);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Experience experience = experienceArrayList.get(position);
        holder.getRowExperienceSeeAllBinding().rwExpSeeTvdetail.setText(Html.fromHtml(experience.getDetails()));
        holder.getRowExperienceSeeAllBinding().rwExpSeeTvtitle.setText(experience.getName());
        if (experience.getRating().size() > 0) {
            final Vote vote = experience.getRating().get(0);
            holder.getRowExperienceSeeAllBinding().rwExpSeeRbratingbar.setRating(vote.getValue());
            holder.getRowExperienceSeeAllBinding().rwExpSeeTvratingcount.setText(String.format(Locale.ENGLISH, "%.1f", vote.getValue()));
        } else {
            holder.getRowExperienceSeeAllBinding().rwExpSeeRbratingbar.setRating(0);
            holder.getRowExperienceSeeAllBinding().rwExpSeeTvratingcount.setText(String.format(Locale.ENGLISH, "%d", 0));
        }
        ImageUtil.loadImageFromPicasso(context, experience.getImage(), holder.getRowExperienceSeeAllBinding().rwExpSeeIvmain, holder.getRowExperienceSeeAllBinding().rwExpSeeIvplaceholder);
        holder.rowExperienceSeeAllBinding.rwExpSeeIvreadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) context).addFragment(((HomeActivity) context).getcurrentFragment(), ExperianceDetailFragment.getExperianceDetailFragment(experience));
            }
        });
        holder.rowExperienceSeeAllBinding.rwExpSeeIvmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) context).addFragment(((HomeActivity) context).getcurrentFragment(), ExperianceDetailFragment.getExperianceDetailFragment(experience));
            }
        });
    }

    @Override
    public int getItemCount() {
        return experienceArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RowExperienceSeeAllBinding rowExperienceSeeAllBinding;

        public ViewHolder(final RowExperienceSeeAllBinding rowExperienceSeeAllBinding) {
            super(rowExperienceSeeAllBinding.getRoot());
            this.rowExperienceSeeAllBinding = rowExperienceSeeAllBinding;
        }

        public RowExperienceSeeAllBinding getRowExperienceSeeAllBinding() {
            return rowExperienceSeeAllBinding;
        }
    }

    public void addItems(final ArrayList<Experience> experienceArrayList) {
        this.experienceArrayList.clear();
        this.experienceArrayList.addAll(experienceArrayList);
        this.notifyDataSetChanged();
    }

}