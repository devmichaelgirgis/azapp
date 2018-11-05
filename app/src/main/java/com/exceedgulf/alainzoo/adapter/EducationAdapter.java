package com.exceedgulf.alainzoo.adapter;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.database.models.Education;
import com.exceedgulf.alainzoo.database.models.Experience;
import com.exceedgulf.alainzoo.databinding.RowEducationBinding;
import com.exceedgulf.alainzoo.fragments.AnimalDetailFragment;
import com.exceedgulf.alainzoo.fragments.EducationDetailFragment;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.util.ArrayList;

/**
 * Created by P.G. on 19/12/17
 */
public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.EducationViewHolder> {


    private Context mContext;
    private ArrayList<Education> educationArrayList;


    public EducationAdapter(final Context mContext) {
        this.mContext = mContext;
        educationArrayList = new ArrayList<>();
    }


    @Override
    public EducationViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final RowEducationBinding rowEducationBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_education, parent, false);
        return new EducationViewHolder(rowEducationBinding);
    }

    @Override
    public void onBindViewHolder(final EducationViewHolder holder, final int position) {
        final Education education = educationArrayList.get(position);
        if (TextUtils.isEmpty(education.getDetails())) {
            holder.getRowEducationBinding().rowEduTvdetail.setText(mContext.getString(R.string.detail_not_available));
        } else {
            holder.getRowEducationBinding().rowEduTvdetail.setText(Html.fromHtml(education.getDetails()));
        }
        holder.getRowEducationBinding().rowEduTvname.setText(Html.fromHtml(education.getName()));
        ImageUtil.loadImageFromPicasso(mContext, education.getThumbnail(), holder.getRowEducationBinding().rowEduIvmain, holder.getRowEducationBinding().rowEduIvplaceholder);
        holder.getRowEducationBinding().rowEduBtnreadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).addFragment(((HomeActivity) mContext).getcurrentFragment(), EducationDetailFragment.getEducationDetailFragment(education));
            }
        });
        holder.getRowEducationBinding().rowEduIvmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).addFragment(((HomeActivity) mContext).getcurrentFragment(), EducationDetailFragment.getEducationDetailFragment(education));
            }
        });
    }

    @Override
    public int getItemCount() {
        return educationArrayList.size();
    }

    public void addItems(final ArrayList<Education> educationArrayList) {
        this.educationArrayList.addAll(educationArrayList);
        this.notifyDataSetChanged();
    }


    class EducationViewHolder extends RecyclerView.ViewHolder {
        private RowEducationBinding rowEducationBinding;

        EducationViewHolder(final RowEducationBinding rowEducationBinding) {
            super(rowEducationBinding.getRoot());
            this.rowEducationBinding = rowEducationBinding;
        }

        public RowEducationBinding getRowEducationBinding() {
            return rowEducationBinding;
        }
    }
}