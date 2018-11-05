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
import com.exceedgulf.alainzoo.databinding.RowWhatsNewBinding;
import com.exceedgulf.alainzoo.fragments.WhatsNewDetailFragment;
import com.exceedgulf.alainzoo.utils.ImageUtil;
import com.exceedgulf.alainzoo.utils.Utils;

import java.util.ArrayList;
import java.util.Locale;


public class WhatsNewSeeAllAdapter extends RecyclerView.Adapter<WhatsNewSeeAllAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WhatsNew> whatsNewsArrayList;

    public WhatsNewSeeAllAdapter(Context context) {
        this.context = context;
        whatsNewsArrayList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final RowWhatsNewBinding rowWhatsNewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_whats_new, parent, false);
        return new ViewHolder(rowWhatsNewBinding);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final WhatsNew whatsNew = whatsNewsArrayList.get(position);
        holder.rowWhatsNewBinding.frHpnDeTvtitle.setText(Html.fromHtml(whatsNew.getName()).toString().trim());
        holder.rowWhatsNewBinding.frHpnDeTvdetail.setText(Html.fromHtml(whatsNew.getDetails()).toString().trim());
        holder.rowWhatsNewBinding.frWtsneDate.setText(String.format(Locale.ENGLISH, "%s", Utils.formatDateTime(whatsNew.getDates().get(0).getStart_date(), "yyyy-MM-dd'T'HH:mm:ss", "d MMM yyyy")));
        holder.rowWhatsNewBinding.frWtsneTvtime.setText(String.format(Locale.ENGLISH, "%s", Utils.formatDateTime(whatsNew.getDates().get(0).getStart_date(), "yyyy-MM-dd'T'HH:mm:ss", "h:mm a")));
        holder.rowWhatsNewBinding.frWtsneTvendDate.setText(String.format(Locale.ENGLISH, "%s", Utils.formatDateTime(whatsNew.getDates().get(0).getEnd_date(), "yyyy-MM-dd'T'HH:mm:ss", "d MMM yyyy")));
        holder.rowWhatsNewBinding.frWtsneTvendTime.setText(String.format(Locale.ENGLISH, "%s", Utils.formatDateTime(whatsNew.getDates().get(0).getEnd_date(), "yyyy-MM-dd'T'HH:mm:ss", "h:mm a")));
        ImageUtil.loadImageFromPicasso(context, whatsNew.getThumbnail(), holder.rowWhatsNewBinding.frWtsneIvmain, holder.rowWhatsNewBinding.frWtsneIvplaceholder);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RowWhatsNewBinding rowWhatsNewBinding;

        public ViewHolder(final RowWhatsNewBinding rowWhatsNewBinding) {
            super(rowWhatsNewBinding.getRoot());
            this.rowWhatsNewBinding = rowWhatsNewBinding;
        }

    }


}