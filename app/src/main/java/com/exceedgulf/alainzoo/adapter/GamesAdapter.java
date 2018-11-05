package com.exceedgulf.alainzoo.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.Interfaces.GamificationCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.models.Games;
import com.exceedgulf.alainzoo.databinding.RowGamesBinding;
import com.exceedgulf.alainzoo.utils.ImageUtil;

import java.util.ArrayList;

/**
 * Created by P.G. on 01/25/2018
 */
public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.GameHolder> {

    private Context mContext;
    private ArrayList<Games> gamesArrayList;
    private GamificationCallback gamificationCallback;


    public GamesAdapter(final Context mContext, final GamificationCallback gamificationCallback) {
        this.mContext = mContext;
        this.gamificationCallback = gamificationCallback;
        this.gamesArrayList = new ArrayList<>();
    }

    @Override
    public GamesAdapter.GameHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final RowGamesBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.row_games, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new GameHolder(binding);
    }

    @Override
    public void onBindViewHolder(final GamesAdapter.GameHolder holder, final int position) {
        final Games games = gamesArrayList.get(position);
        holder.getBinding().rowGameTvtitle.setText(Html.fromHtml(games.getName()).toString().trim());
        holder.getBinding().rowGameTvdescription.setText(Html.fromHtml(games.getDescription()).toString().trim());
        ImageUtil.loadImageFromPicasso(mContext, games.getImage(), holder.getBinding().rowGameIvMain, holder.getBinding().rowGameIvplaceholder);
        holder.getBinding().rowGameIviOsStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(games.getIos());
                gamificationCallback.callGamification(games.getId());
            }
        });
        holder.getBinding().rowGameIvAndroidStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(games.getAndroid());
                gamificationCallback.callGamification(games.getId());
            }
        });
    }



    @Override
    public int getItemCount() {
        return gamesArrayList.size();
    }

    private void openUrl(final String url) {
        try {
            final Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            mContext.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addItems(final ArrayList<Games> gamesArrayList) {
        this.gamesArrayList.addAll(gamesArrayList);
        this.notifyDataSetChanged();

    }

    class GameHolder extends RecyclerView.ViewHolder {
        private final RowGamesBinding binding;

        GameHolder(final RowGamesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public RowGamesBinding getBinding() {
            return binding;
        }
    }

}