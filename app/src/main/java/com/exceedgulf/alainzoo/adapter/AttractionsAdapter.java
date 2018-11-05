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
import com.exceedgulf.alainzoo.database.models.Attraction;
import com.exceedgulf.alainzoo.databinding.RowAttractionsBinding;
import com.exceedgulf.alainzoo.fragments.AttractionsDetailFragment;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.util.ArrayList;

/**
 * Created by P.G. on 27/12/17
 */
public class AttractionsAdapter extends RecyclerView.Adapter<AttractionsAdapter.AttractionsViewHolder> {

    private ArrayList<Attraction> attractionArrayList;
    private Context context;


    public AttractionsAdapter(final Context mContext) {
        this.context = mContext;
        attractionArrayList = new ArrayList<>();
    }


    @Override
    public AttractionsViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final RowAttractionsBinding rowAttractionsBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_attractions, parent, false);
        return new AttractionsViewHolder(rowAttractionsBinding);
    }

    @Override
    public void onBindViewHolder(final AttractionsViewHolder holder, final int position) {
        final Attraction attraction = attractionArrayList.get(position);
        holder.getRowEducationBinding().rowAttrTvname.setText(Html.fromHtml(attraction.getName()));
        holder.getRowEducationBinding().rowAttrTvdetail.setText(Html.fromHtml(attraction.getDescription()));
        ImageUtil.loadImageFromPicasso(context, attraction.getThumbnail().trim(), holder.getRowEducationBinding().rowAttrIvmain, holder.getRowEducationBinding().rowAttrIvplaceholder);

        holder.getRowEducationBinding().rowAttrBtnreadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) context).addFragment(((HomeActivity) context).getcurrentFragment(), AttractionsDetailFragment.getAttractionsDetailFragment(attraction));
            }
        });
        holder.getRowEducationBinding().rowAttrIvmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) context).addFragment(((HomeActivity) context).getcurrentFragment(), AttractionsDetailFragment.getAttractionsDetailFragment(attraction));
            }
        });
    }

    @Override
    public int getItemCount() {
        return attractionArrayList.size();
    }

    public void addItems(final ArrayList<Attraction> attractionArrayList) {
        // this.attractionArrayList.clear();
        //  this.attractionArrayList.trimToSize();
        this.attractionArrayList.addAll(attractionArrayList);
        this.notifyDataSetChanged();
    }


    class AttractionsViewHolder extends RecyclerView.ViewHolder {
        private RowAttractionsBinding rowAttractionsBinding;

        AttractionsViewHolder(final RowAttractionsBinding rowAttractionsBinding) {
            super(rowAttractionsBinding.getRoot());
            this.rowAttractionsBinding = rowAttractionsBinding;
        }

        public RowAttractionsBinding getRowEducationBinding() {
            return rowAttractionsBinding;
        }
    }
}