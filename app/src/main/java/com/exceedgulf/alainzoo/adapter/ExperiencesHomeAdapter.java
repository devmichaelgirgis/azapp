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
import com.exceedgulf.alainzoo.databinding.RowExperiencesHomeBinding;
import com.exceedgulf.alainzoo.fragments.ExperianceDetailFragment;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.util.ArrayList;

/**
 * Created by P.G on 25/12/2017.
 */

public class ExperiencesHomeAdapter extends RecyclerView.Adapter<ExperiencesHomeAdapter.ExperiencesHomeViewHolder> {
    private Context context;
    private ArrayList<Experience> experienceArrayList;

    public ExperiencesHomeAdapter(Context context) {
        this.context = context;
        experienceArrayList = new ArrayList<>();
    }

    @Override
    public ExperiencesHomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RowExperiencesHomeBinding rowExperiencesHomeBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_experiences_home, parent, false);
        return new ExperiencesHomeViewHolder(rowExperiencesHomeBinding);
    }

    @Override
    public void onBindViewHolder(final ExperiencesHomeViewHolder holder, final int position) {
        final Experience experience = experienceArrayList.get(position);
        ImageUtil.loadImageFromPicasso(context, experience.getImage(), holder.getRowExperiencesHomeBinding().rowExpHomeIvmain, holder.getRowExperiencesHomeBinding().rowExpHomeIvplaceholder);
        holder.getRowExperiencesHomeBinding().rowExpHomeTvname.setText(Html.fromHtml(experience.getName()).toString().trim());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
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

    public void addItems(final ArrayList<Experience> experienceArrayList) {
        this.experienceArrayList.clear();
        this.experienceArrayList.trimToSize();
        this.experienceArrayList.addAll(experienceArrayList);
        this.notifyDataSetChanged();
    }

    public class ExperiencesHomeViewHolder extends RecyclerView.ViewHolder {

        private RowExperiencesHomeBinding rowExperiencesHomeBinding;

        ExperiencesHomeViewHolder(final RowExperiencesHomeBinding rowExperiencesHomeBinding) {
            super(rowExperiencesHomeBinding.getRoot());
            this.rowExperiencesHomeBinding = rowExperiencesHomeBinding;
        }

        public RowExperiencesHomeBinding getRowExperiencesHomeBinding() {
            return rowExperiencesHomeBinding;
        }
    }


}
