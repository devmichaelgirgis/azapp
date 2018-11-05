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
import com.exceedgulf.alainzoo.database.models.SearchDataModel;
import com.exceedgulf.alainzoo.databinding.RowSearchItemBinding;
import com.exceedgulf.alainzoo.fragments.AnimalDetailFragment;
import com.exceedgulf.alainzoo.fragments.ExperianceDetailFragment;
import com.exceedgulf.alainzoo.fragments.ExperiencesFragment;
import com.exceedgulf.alainzoo.fragments.WhatsNewDetailFragment;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.util.ArrayList;

/**
 * Created by R.S. on 19/12/17
 */
public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.Holder> {
    private static String WHATSNEW = "events";
    private static String ANIMALS = "animals";
    private static String EXPERIENCE = "experience";
    private Context context;
    private ArrayList<SearchDataModel> searchDataModels;

    public SearchItemAdapter(Context context) {
        this.context = context;
        searchDataModels = new ArrayList<>();
    }

    @Override
    public Holder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        // create a new view
        final RowSearchItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_search_item, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final SearchDataModel searchDataModel = searchDataModels.get(position);
        holder.binding.rowSearchItemTvName.setText(Html.fromHtml(searchDataModel.getTitle()));
        holder.binding.rowSearchItemTvDesc.setText(Html.fromHtml(searchDataModel.getDetails()));
        if(searchDataModel.getType().equalsIgnoreCase(ANIMALS)){
            holder.binding.rowSearchItemTvType.setText(context.getString(R.string.animal));
        }else if(searchDataModel.getType().equalsIgnoreCase(EXPERIENCE)){
            holder.binding.rowSearchItemTvType.setText(context.getString(R.string.experience));
        }else if(searchDataModel.getType().equalsIgnoreCase(WHATSNEW)){
            holder.binding.rowSearchItemTvType.setText(context.getString(R.string.what_s_new));
        }
        ImageUtil.loadImageFromPicasso(context, searchDataModel.getBanner(), holder.binding.rowSearchItemIv, holder.binding.rowSearchItemIvplaceHolder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchDataModel.getType().equalsIgnoreCase(ANIMALS)) {
                    ((HomeActivity) context).addFragment(((HomeActivity) context).getcurrentFragment(), AnimalDetailFragment.getAnimalDetailFragment(searchDataModel.getId()));
                } else if (searchDataModel.getType().equalsIgnoreCase(EXPERIENCE)) {
                    ((HomeActivity) context).addFragment(((HomeActivity) context).getcurrentFragment(), ExperianceDetailFragment.getExperianceDetailFragment(searchDataModel.getId()));
                } else if (searchDataModel.getType().equalsIgnoreCase(WHATSNEW)) {
                    ((HomeActivity) context).addFragment(((HomeActivity) context).getcurrentFragment(), WhatsNewDetailFragment.getWhatsNewDetailFragment(searchDataModel.getId()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchDataModels.size();
    }

    public void addItems(ArrayList<SearchDataModel> resultList) {
        //searchDataModels.clear();
        searchDataModels.addAll(resultList);
        notifyDataSetChanged();
    }

    public void clearItems() {
        this.searchDataModels.clear();
        this.searchDataModels.trimToSize();
        this.notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder {
        private final RowSearchItemBinding binding;

        Holder(RowSearchItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}