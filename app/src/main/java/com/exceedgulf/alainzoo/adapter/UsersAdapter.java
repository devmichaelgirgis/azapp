package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.models.Family;
import com.exceedgulf.alainzoo.databinding.RowAddUserBinding;
import com.exceedgulf.alainzoo.databinding.RowUsersBinding;
import com.exceedgulf.alainzoo.models.UsersModel;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.util.ArrayList;

/**
 * Created by R.S. on 19/12/17
 */
public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final static int VIEW_ADD_USER = 0;
    final static int VIEW_USERS = 1;
    private ArrayList<Family> models;
    private Context mContext;
    private AddUserListener addUserListener;

    public UsersAdapter(final Context mContext, AddUserListener addUserListener) {
        this.models = new ArrayList<>();
        this.mContext = mContext;
        this.addUserListener = addUserListener;
        models.add(new Family());
    }

    public void addAllModel(final ArrayList<Family> models) {
        this.models.addAll(0, models);
        this.models.add(new Family());
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_USERS) {
            // create a new view
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            final RowUsersBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.row_users, parent, false);
            // set the view's size, margins, paddings and layout parameters
            viewHolder = new Holder(binding);
        } else {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            final RowAddUserBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.row_add_user, parent, false);
            // set the view's size, margins, paddings and layout parameters
            viewHolder = new AddUserHolder(binding);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof Holder) {
            final Holder viewHolder = (Holder) holder;
            final Family family = models.get(position);
            Glide.with(mContext).load(family.getGender().equalsIgnoreCase("male") ? R.drawable.boy_avatar : R.drawable.girl_avatar).into(viewHolder.binding.roundedImageView);
            ((Holder) holder).binding.setModel(models.get(position));
        } else {
            ((AddUserHolder) holder).addUserBinding.rowAddUserIvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addUserListener.addUser();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == models.size()-1) ? VIEW_ADD_USER : VIEW_USERS;
    }

    public void clearItems() {
        models.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public interface AddUserListener {
        void addUser();
    }

    class Holder extends RecyclerView.ViewHolder {
        private final RowUsersBinding binding;

        Holder(RowUsersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class AddUserHolder extends RecyclerView.ViewHolder {
        private final RowAddUserBinding addUserBinding;

        AddUserHolder(RowAddUserBinding binding) {
            super(binding.getRoot());
            this.addUserBinding = binding;
        }
    }
}