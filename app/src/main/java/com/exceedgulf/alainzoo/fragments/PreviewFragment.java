package com.exceedgulf.alainzoo.fragments;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.BuildConfig;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.databinding.FragmentPreviewBinding;
import com.exceedgulf.alainzoo.managers.GamificationManager;
import com.exceedgulf.alainzoo.managers.LoginZooManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.GenericFileProvider;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by P.P on 20/01/2018
 */
public class PreviewFragment extends BaseFragment {

    private FragmentPreviewBinding previewBinding;
    private String strFile;

    public PreviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        previewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_preview, container, false);
        return previewBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        final Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("file")) {
            strFile = bundle.getString("file");
            if (!TextUtils.isEmpty(strFile)) {
                Picasso.with(getActivity()).load(new File(strFile)).into(previewBinding.fragIvPreview);
            }
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.camera, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.zoo_camrea), getResources().getColor(R.color.light_eggplant), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.putExtra(Intent.EXTRA_STREAM, GenericFileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID, new File(strFile)));
            } else {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(strFile)));
            }


            startActivity(Intent.createChooser(intent, getString(R.string.share_image)));
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.zoo_camrea), getResources().getColor(R.color.light_eggplant), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        }
    }


}
