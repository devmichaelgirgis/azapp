package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.models.Avatars;
import com.exceedgulf.alainzoo.databinding.RowAddressDetailItemBinding;
import com.exceedgulf.alainzoo.databinding.RowAvatarBinding;
import com.exceedgulf.alainzoo.models.AddressInformationModel;

import java.util.ArrayList;

/**
 * Created by P.G. on 01/31/17
 */
public class AddressInformatinAdapter extends RecyclerView.Adapter<AddressInformatinAdapter.AddressHolder> {

    private ArrayList<AddressInformationModel> addressInformationModelArrayList;
    private Context mContext;

    public AddressInformatinAdapter(final Context mContext, ArrayList<AddressInformationModel> addressInformationModelArrayList) {
        this.addressInformationModelArrayList = addressInformationModelArrayList;
        this.mContext = mContext;
    }

    @Override
    public AddressHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        // create a new view
        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final RowAddressDetailItemBinding rowAddressDetailItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.row_address_detail_item, parent, false);
        return new AddressHolder(rowAddressDetailItemBinding);
    }

    @Override
    public void onBindViewHolder(final AddressHolder holder, final int position) {
        final AddressInformationModel addressInformationModel = addressInformationModelArrayList.get(position);
        holder.getRowAddressDetailItemBinding().frcontactTvcontactnumber.setText(addressInformationModel.getInfo());
        if (!TextUtils.isEmpty(addressInformationModel.getIcon())) {
            Glide.with(mContext).load(addressInformationModel.getIcon()).into(holder.getRowAddressDetailItemBinding().frcontactIvcontact);
        }
        holder.getRowAddressDetailItemBinding().frcontactIvcontactarrow.setVisibility(TextUtils.isEmpty(addressInformationModel.getLink()) ? View.GONE : View.VISIBLE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(addressInformationModel.getLink())) {
                    try {
                        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(addressInformationModel.getLink()));
                        mContext.startActivity(intent);
                    } catch (Exception ignore) {
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressInformationModelArrayList.size();
    }

    class AddressHolder extends RecyclerView.ViewHolder {
        private RowAddressDetailItemBinding rowAddressDetailItemBinding;

        AddressHolder(final RowAddressDetailItemBinding rowAddressDetailItemBinding) {
            super(rowAddressDetailItemBinding.getRoot());
            this.rowAddressDetailItemBinding = rowAddressDetailItemBinding;
        }

        public RowAddressDetailItemBinding getRowAddressDetailItemBinding() {
            return rowAddressDetailItemBinding;
        }
    }
}