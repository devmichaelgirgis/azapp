package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.models.Avatars;
import com.exceedgulf.alainzoo.databinding.RowAvatarBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by P.G. on 01/31/17
 */
public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarHolder> {

    private ArrayList<Avatars> avatarsArrayList;
    private Context mContext;
    private Avatars selectedAvatars = null;

    public AvatarAdapter(final Context mContext) {
        this.avatarsArrayList = new ArrayList<>();
        this.mContext = mContext;
    }

    public void addAllModel(final ArrayList<Avatars> avatarsArrayList) {
        this.avatarsArrayList.addAll(avatarsArrayList);
        if (selectedAvatars == null) {
            selectedAvatars = avatarsArrayList.size() > 0 ? avatarsArrayList.get(0) : null;
        }
        AvatarAdapter.this.notifyDataSetChanged();
    }

    @Override
    public AvatarHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        // create a new view
        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final RowAvatarBinding rowAvatarBinding = DataBindingUtil.inflate(layoutInflater, R.layout.row_avatar, parent, false);
        return new AvatarHolder(rowAvatarBinding);
    }

    @Override
    public void onBindViewHolder(final AvatarHolder holder, final int position) {
        final Avatars avatars = avatarsArrayList.get(position);
        holder.getRowAvatarBinding().rowAvatarLlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedAvatars = avatars;
                AvatarAdapter.this.notifyDataSetChanged();
            }
        });
        holder.getRowAvatarBinding().rowAvatarIvMain.setBorderColor((selectedAvatars != null && (Objects.equals(selectedAvatars.getId(), avatars.getId()))) ? ContextCompat.getColor(mContext, R.color.yellow_orange) : ContextCompat.getColor(mContext, R.color.white_badge_text));
        //holder.getRowAvatarBinding().rowAvatarLlMain.setBackgroundResource((selectedAvatars != null && (Objects.equals(selectedAvatars.getId(), avatars.getId()))) ? R.drawable.bg_avatar_selected : R.drawable.bg_avatar_unselected);
        //ImageUtil.loadImageFromPicasso(mContext, avatars.getImage(), holder.getRowAvatarBinding().rowAvatarIvMain, holder.getRowAvatarBinding().rowAvatarIvPlaceholder);
        if (!TextUtils.isEmpty(avatars.getImage())) {
            Picasso.with(mContext).load(avatars.getImage()).into(holder.getRowAvatarBinding().rowAvatarIvMain);
        }

    }

    @Override
    public int getItemCount() {
        return avatarsArrayList.size();
    }

    public Avatars getSelectedAvatars() {
        return selectedAvatars;
    }

    public void setSelectedAvatars(Avatars selectedAvatars) {
        this.selectedAvatars = selectedAvatars;
    }

    class AvatarHolder extends RecyclerView.ViewHolder {
        private RowAvatarBinding rowAvatarBinding;

        AvatarHolder(final RowAvatarBinding rowAvatarBinding) {
            super(rowAvatarBinding.getRoot());
            this.rowAvatarBinding = rowAvatarBinding;
        }

        public RowAvatarBinding getRowAvatarBinding() {
            return rowAvatarBinding;
        }
    }
}