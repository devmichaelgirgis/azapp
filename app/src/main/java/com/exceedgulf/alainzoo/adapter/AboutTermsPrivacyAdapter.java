package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.databinding.RowAboutTermsPrivacyBinding;
import com.exceedgulf.alainzoo.database.models.AboutTermsPrivacyModel;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.util.ArrayList;

/**
 * Created by P.G. on 01/29/18
 */
public class AboutTermsPrivacyAdapter extends RecyclerView.Adapter<AboutTermsPrivacyAdapter.AboutUsViewHolder> {
    private Context mContext;
    private ArrayList<AboutTermsPrivacyModel> aboutTermsPrivacyModelArrayList;

    public AboutTermsPrivacyAdapter(final Context mContext) {
        this.mContext = mContext;
        this.aboutTermsPrivacyModelArrayList = new ArrayList<>();
    }


    @Override
    public AboutUsViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final RowAboutTermsPrivacyBinding rowAboutTermsPrivacyBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.row_about_terms_privacy, parent, false);
        return new AboutUsViewHolder(rowAboutTermsPrivacyBinding);
    }

    @Override
    public void onBindViewHolder(final AboutUsViewHolder holder, final int position) {
        final AboutTermsPrivacyModel aboutTermsPrivacyModel = aboutTermsPrivacyModelArrayList.get(position);
        holder.getRowAboutTermsPrivacyBinding().rowAboutTvDetail.setVisibility(TextUtils.isEmpty(aboutTermsPrivacyModel.getDetails()) ? View.GONE : View.VISIBLE);
        holder.getRowAboutTermsPrivacyBinding().rowAboutTvTitle.setVisibility(TextUtils.isEmpty(aboutTermsPrivacyModel.getTitle()) ? View.GONE : View.VISIBLE);
        holder.getRowAboutTermsPrivacyBinding().rowAboutFrImages.setVisibility(TextUtils.isEmpty(aboutTermsPrivacyModel.getImage()) ? View.GONE : View.VISIBLE);
        holder.getRowAboutTermsPrivacyBinding().rowAboutTvDetail.setText(Html.fromHtml(aboutTermsPrivacyModel.getDetails()));
        holder.getRowAboutTermsPrivacyBinding().rowAboutTvTitle.setText(aboutTermsPrivacyModel.getTitle());
        if (aboutTermsPrivacyModel.getImage() != null && !TextUtils.isEmpty(aboutTermsPrivacyModel.getImage())) {
            ImageUtil.loadImageFromPicasso(mContext, aboutTermsPrivacyModel.getImage(), holder.getRowAboutTermsPrivacyBinding().rowAboutIvMain, holder.getRowAboutTermsPrivacyBinding().rowAboutIvPlaceHolder);
        }
    }

    @Override
    public int getItemCount() {
        return aboutTermsPrivacyModelArrayList.size();
    }

    class AboutUsViewHolder extends RecyclerView.ViewHolder {
        private RowAboutTermsPrivacyBinding rowAboutTermsPrivacyBinding;

        AboutUsViewHolder(final RowAboutTermsPrivacyBinding rowAboutTermsPrivacyBinding) {
            super(rowAboutTermsPrivacyBinding.getRoot());
            this.rowAboutTermsPrivacyBinding = rowAboutTermsPrivacyBinding;

        }

        public RowAboutTermsPrivacyBinding getRowAboutTermsPrivacyBinding() {
            return rowAboutTermsPrivacyBinding;
        }
    }

    public void addItems(final ArrayList<AboutTermsPrivacyModel> aboutUsModelArrayList) {
        this.aboutTermsPrivacyModelArrayList.addAll(aboutUsModelArrayList);
        this.notifyDataSetChanged();
    }

}