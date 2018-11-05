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
import com.exceedgulf.alainzoo.databinding.RowAttractionsHomeBinding;
import com.exceedgulf.alainzoo.fragments.AttractionsDetailFragment;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.util.ArrayList;


public class AttractionsHomeAdapter extends RecyclerView.Adapter<AttractionsHomeAdapter.AttractionViewHolder> {

    private Context context;
    private ArrayList<Attraction> attractionArrayList;

    public AttractionsHomeAdapter(final Context context) {
        this.context = context;
        attractionArrayList = new ArrayList<>();
    }


    class AttractionViewHolder extends RecyclerView.ViewHolder {
        private RowAttractionsHomeBinding rowAttractionsHomeBinding;

        AttractionViewHolder(final RowAttractionsHomeBinding rowAttractionsHomeBinding) {
            super(rowAttractionsHomeBinding.getRoot());
            this.rowAttractionsHomeBinding = rowAttractionsHomeBinding;
        }

        public RowAttractionsHomeBinding getRowAttractionsHomeBinding() {
            return rowAttractionsHomeBinding;
        }
    }

    @Override
    public AttractionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RowAttractionsHomeBinding rowAttractionsHomeBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_attractions_home, parent, false);
        return new AttractionViewHolder(rowAttractionsHomeBinding);
    }

    @Override
    public void onBindViewHolder(final AttractionViewHolder holder, final int position) {
        final Attraction attraction = attractionArrayList.get(position);
        ImageUtil.loadImageFromPicasso(context, attraction.getThumbnail(), holder.getRowAttractionsHomeBinding().rowAttractionIvmain, holder.getRowAttractionsHomeBinding().rowAttrHomeIvplaceholder);
        holder.getRowAttractionsHomeBinding().rowAttractionTvname.setText(Html.fromHtml(attraction.getName()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
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
        this.attractionArrayList.clear();
        this.attractionArrayList.trimToSize();
        this.attractionArrayList.addAll(attractionArrayList);
        this.notifyDataSetChanged();
    }

}
