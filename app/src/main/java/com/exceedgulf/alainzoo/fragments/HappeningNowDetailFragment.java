package com.exceedgulf.alainzoo.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.database.models.HappeningNow;
import com.exceedgulf.alainzoo.databinding.FragmentHappeningNowDetailsBinding;
import com.exceedgulf.alainzoo.managers.GamificationManager;
import com.exceedgulf.alainzoo.utils.ImageUtil;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.Utils;

import java.util.Locale;

import okhttp3.RequestBody;

/**
 * Created by P.G. on 25/12/17
 */
public class HappeningNowDetailFragment extends BaseFragment {

    private static final String BUNDLE_KEY_HAPPENING_NOW = "happeningnow";
    private FragmentHappeningNowDetailsBinding fragmentHappeningNowDetailsBinding;
    private HappeningNow happeningNow;
    private String seeMoreInfoUrl;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        fragmentHappeningNowDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_happening_now_details, container, false);
        return fragmentHappeningNowDetailsBinding.getRoot();
    }

    @Override
    public void initView(final View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.titlehappeningnow), getResources().getColor(R.color.light_grey_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        final Bundle bundleExperiance = this.getArguments();
        if (bundleExperiance != null && bundleExperiance.containsKey(BUNDLE_KEY_HAPPENING_NOW)) {
            happeningNow = (HappeningNow) bundleExperiance.getSerializable(BUNDLE_KEY_HAPPENING_NOW);
            if (happeningNow != null && happeningNow.getName() != null) {
                fragmentHappeningNowDetailsBinding.frHpnDeTvtitle.setText(Html.fromHtml(happeningNow.getName()));
            }
            final WebSettings settings = fragmentHappeningNowDetailsBinding.frHpnDeWvdetail.getSettings();
            settings.setDefaultTextEncodingName("utf-8");
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            int textSize = (int) getResources().getDimension(R.dimen._5sdp);
            settings.setDefaultFontSize(textSize);
            if (happeningNow != null && happeningNow.getDetails() != null) {
                fragmentHappeningNowDetailsBinding.frHpnDeWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(happeningNow.getDetails()), "text/html", "UTF-8", null);
            }
            ImageUtil.loadImageFromPicasso(getActivity(), happeningNow.getImage().trim(), fragmentHappeningNowDetailsBinding.frHpnDeIvmain, fragmentHappeningNowDetailsBinding.frHpnDeIvplaceholder);
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.titlehappeningnow), getResources().getColor(R.color.light_grey_blue), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
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
            if (happeningNow != null) {
                if (LangUtils.getCurrentLanguage().equalsIgnoreCase("ar")) {
                    seeMoreInfoUrl = "http://ain-dev.exceedgulf.net/ar/node/" + happeningNow.getId();
                } else {
                    seeMoreInfoUrl = "http://ain-dev.exceedgulf.net/node/" + happeningNow.getId();
                }
                callGamification();
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, seeMoreInfoUrl);
                startActivity(Intent.createChooser(intent, getString(R.string.share)));
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void callGamification() {
        final RequestBody body = GamificationManager.getGamificationManager().createRequestBody("share_content", String.valueOf(happeningNow.getId()));
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

}
