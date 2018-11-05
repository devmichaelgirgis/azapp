package com.exceedgulf.alainzoo.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.database.models.VisitorService;
import com.exceedgulf.alainzoo.databinding.FragmentServicesDetailsBinding;
import com.exceedgulf.alainzoo.managers.GamificationManager;
import com.exceedgulf.alainzoo.utils.ImageUtil;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.Utils;

import java.util.Locale;

import okhttp3.RequestBody;

/**
 * Created by Paras Ghasadiya on 25/12/17.
 */

public class VisiterServicesDetailFragment extends BaseFragment {
    private static final String KEY_VISITOR = "KeyVisitor";
    private FragmentServicesDetailsBinding detailsBinding;
    private VisitorService visitorService;
    private String seeMoreInfoUrl;

    public static VisiterServicesDetailFragment getVisitorServiceDetailFragment(final VisitorService visitorService) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_VISITOR, visitorService);
        final VisiterServicesDetailFragment servicesDetailFragment = new VisiterServicesDetailFragment();
        servicesDetailFragment.setArguments(bundle);
        return servicesDetailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        detailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_services_details, container, false);
        return detailsBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.services), getResources().getColor(R.color.brownish_orange), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        detailsBinding.frSerDetBtninqueryforservices.setOnClickListener(this);

        final Bundle bundleExperiance = this.getArguments();
        if (bundleExperiance != null && bundleExperiance.containsKey(KEY_VISITOR)) {
            visitorService = (VisitorService) bundleExperiance.getSerializable(KEY_VISITOR);
        }

        if (visitorService != null) {
            ((HomeActivity) getActivity()).activityHomeBinding.appbarHome.toolbarTitle.setAllCaps(false);
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(visitorService.getName(), getResources().getColor(R.color.brownish_orange), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
            //   detailsBinding.frSrvDeTvdetail.setText(Html.fromHtml(visitorService.getDetails()));
            detailsBinding.frSrvDeWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(visitorService.getDetails()), "text/html", "UTF-8", null);
            detailsBinding.frSrvDeTvtitle.setText(visitorService.getName());
            if (!TextUtils.isEmpty(visitorService.getPrice())) {
                detailsBinding.frSerDetTvrate.setVisibility(View.VISIBLE);
                detailsBinding.frSerDetTvrate.setText(visitorService.getPrice());
            } else {
                detailsBinding.frSerDetTvrate.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(visitorService.getLocation())) {
                detailsBinding.frSerDetTvlocation.setVisibility(View.VISIBLE);
                detailsBinding.frSerDetTvlocation.setText(String.format("%s%s%s", getString(R.string.location), ": ", visitorService.getLocation()));
            } else {
                detailsBinding.frSerDetTvlocation.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(visitorService.getPhone())) {
                detailsBinding.frSerDetTvbookingno.setVisibility(View.VISIBLE);
                detailsBinding.frSerDetTvbookingno.setText(String.format("%s%s%s", getString(R.string.phone_number), ": ", visitorService.getPhone()));
            } else {
                detailsBinding.frSerDetTvbookingno.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(visitorService.getEmail())) {
                detailsBinding.frSerDetTvEmail.setVisibility(View.VISIBLE);
                detailsBinding.frSerDetTvEmail.setText(String.format("%s%s%s", getString(R.string.email_address), ": ", visitorService.getEmail()));
            } else {
                detailsBinding.frSerDetTvEmail.setVisibility(View.GONE);
            }
            ImageUtil.loadImageFromPicasso(getActivity(), visitorService.getThumbnail(), detailsBinding.frSrvDeIvmain, detailsBinding.frSrvDeIvplaceholder);
        }

        detailsBinding.frSerDetTvbookingno.setOnClickListener(this);

        detailsBinding.frSrvDeWvdetail.setWebViewClient(new MyWebViewClient());
        detailsBinding.frSerDetBtninqueryforservices.setText(getString(R.string.enquire_for_service));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            if (visitorService != null) {
                ((HomeActivity) getActivity()).activityHomeBinding.appbarHome.toolbarTitle.setAllCaps(false);
                ((HomeActivity) getActivity()).setToolbarWithCenterTitle(visitorService.getName(), getResources().getColor(R.color.brownish_orange), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
            } else {
                ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.services), getResources().getColor(R.color.brownish_orange), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == detailsBinding.frSerDetBtninqueryforservices) {
            ((HomeActivity) getActivity()).addFragment(VisiterServicesDetailFragment.this, VisiterServiceApplyFragment.getVisitorServiceApplyFragment(visitorService));

        } else if (v == detailsBinding.frSerDetTvbookingno) {
            final Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + detailsBinding.frSerDetTvbookingno.getText().toString().trim()));
            startActivity(callIntent);
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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            if (visitorService != null) {
                if (LangUtils.getCurrentLanguage().equalsIgnoreCase("ar")) {
                    seeMoreInfoUrl = "http://ain-dev.exceedgulf.net/ar/node/" + visitorService.getId();
                } else {
                    seeMoreInfoUrl = "http://ain-dev.exceedgulf.net/node/" + visitorService.getId();
                }
                callGamification();
                final Uri uri = Uri.parse(seeMoreInfoUrl); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, seeMoreInfoUrl);
                startActivity(Intent.createChooser(intent, getString(R.string.share)));
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void callGamification() {
        final RequestBody body = GamificationManager.getGamificationManager().createRequestBody("share_content", String.valueOf(visitorService.getId()));
        GamificationManager.getGamificationManager().postCreateGamification(body, new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.e("gamification", "success" + result.toString());
                ((HomeActivity) getActivity()).getUserDetailWithOutDialog();
            }

            @Override
            public void onFaild(String message) {
                Log.e("gamification", "failure" + message);
            }
        });
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }


}
