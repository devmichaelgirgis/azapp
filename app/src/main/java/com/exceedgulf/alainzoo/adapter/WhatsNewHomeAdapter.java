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
import com.exceedgulf.alainzoo.database.models.WhatsNew;
import com.exceedgulf.alainzoo.databinding.RowWhatsNewHomeBinding;
import com.exceedgulf.alainzoo.fragments.WhatsNewDetailFragment;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.util.ArrayList;

/**
 * Created by P.G on 25/12/2017.
 */

public class WhatsNewHomeAdapter extends RecyclerView.Adapter<WhatsNewHomeAdapter.WhatsNewHomeViewHolder> {
    private Context context;
    private ArrayList<WhatsNew> whatsNewsArrayList;

    public WhatsNewHomeAdapter(Context context) {
        this.context = context;
        whatsNewsArrayList = new ArrayList<>();
    }

    @Override
    public WhatsNewHomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RowWhatsNewHomeBinding rowWhatsNewHomeBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_whats_new_home, parent, false);
        return new WhatsNewHomeViewHolder(rowWhatsNewHomeBinding);
    }

    @Override
    public void onBindViewHolder(final WhatsNewHomeViewHolder holder, final int position) {
        final WhatsNew whatsNew = whatsNewsArrayList.get(position);
        holder.rowWhatsNewHomeBinding.rowWhtNewHomeTvname.setText(Html.fromHtml(whatsNew.getName()).toString().trim());
        ImageUtil.loadImageFromPicasso(context, whatsNew.getThumbnail(), holder.rowWhatsNewHomeBinding.rowWhtNewHomeIvmain, holder.rowWhatsNewHomeBinding.rowWhtNewHomeIvplaceholder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) context).addFragment(((HomeActivity) context).getcurrentFragment(), WhatsNewDetailFragment.getWhatsNewDetailFragment(whatsNew));
            }
        });
    }

    @Override
    public int getItemCount() {
        return whatsNewsArrayList.size();
    }

    public void addItems(final ArrayList<WhatsNew> whatsNewsArrayList) {
        this.whatsNewsArrayList.addAll(whatsNewsArrayList);
        this.notifyDataSetChanged();
    }

    public class WhatsNewHomeViewHolder extends RecyclerView.ViewHolder {

        private RowWhatsNewHomeBinding rowWhatsNewHomeBinding;

        WhatsNewHomeViewHolder(final RowWhatsNewHomeBinding rowWhatsNewHomeBinding) {
            super(rowWhatsNewHomeBinding.getRoot());
            this.rowWhatsNewHomeBinding = rowWhatsNewHomeBinding;
        }
    }

}
