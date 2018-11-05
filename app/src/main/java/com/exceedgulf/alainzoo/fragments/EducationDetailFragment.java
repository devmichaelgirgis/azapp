package com.exceedgulf.alainzoo.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.AnimalImageListAdapter;
import com.exceedgulf.alainzoo.database.models.Education;
import com.exceedgulf.alainzoo.databinding.FragmentEducationDetailBinding;
import com.exceedgulf.alainzoo.managers.GamificationManager;
import com.exceedgulf.alainzoo.utils.ImageUtil;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.Utils;

import java.util.Locale;

import okhttp3.RequestBody;

/**
 * Created by P.P. on 3/1/17
 */
public class EducationDetailFragment extends BaseFragment implements AnimalImageListAdapter.OnItemClick {

    private static final String KEY_EDUCATION = "KeyEducation";
    private FragmentEducationDetailBinding detailBinding;
    private Education education;
    private String seeMoreInfoUrl;

    public static EducationDetailFragment getEducationDetailFragment(final Education education) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EDUCATION, education);
        final EducationDetailFragment educationDetailFragment = new EducationDetailFragment();
        educationDetailFragment.setArguments(bundle);
        return educationDetailFragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        detailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_education_detail, container, false);
        return detailBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.mEducation), getResources().getColor(R.color.light_eggplant), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        final WebSettings settings = detailBinding.frEdudeWvdetail.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        int textSize = (int) getResources().getDimension(R.dimen._5sdp);
        settings.setDefaultFontSize(textSize);

        detailBinding.frEdudeRvimages.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        final AnimalImageListAdapter animalImageListAdapter = new AnimalImageListAdapter(getActivity(), this);
        detailBinding.frEdudeRvimages.setAdapter(animalImageListAdapter);
        detailBinding.frSerDetBtninqueryforservices.setOnClickListener(this);
        detailBinding.frSerDetBtnSeeMoreDetails.setOnClickListener(this);

        final Bundle bundleExperiance = this.getArguments();
        if (bundleExperiance != null && bundleExperiance.containsKey(KEY_EDUCATION)) {
            education = (Education) bundleExperiance.getSerializable(KEY_EDUCATION);
        }

        if (education != null) {
            // ((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(education.getName()).toString().trim(), getResources().getColor(R.color.light_eggplant), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
            if (TextUtils.isEmpty(education.getDetails())) {
                detailBinding.frEdudeWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(getString(R.string.detail_not_available)), "text/html", "UTF-8", null);
            } else {
                detailBinding.frEdudeWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(education.getDetails()), "text/html", "UTF-8", null);
            }
            //detailBinding.frEdudeTvdetail.setText(Html.fromHtml(education.getDetails()));
            detailBinding.frEdudeTvname.setText(Html.fromHtml(education.getName()).toString().trim());
            ImageUtil.loadImageFromPicasso(getActivity(), education.getThumbnail(), detailBinding.frEdudeIvmain, detailBinding.frEdudeIvplaceholder);
            if (LangUtils.getCurrentLanguage().equalsIgnoreCase("ar")) {
                seeMoreInfoUrl = "http://ain-dev.exceedgulf.net/ar/node/" + education.getId();
            } else {
                seeMoreInfoUrl = "http://ain-dev.exceedgulf.net/node/" + education.getId();
            }
        }
        detailBinding.frEdudeWvdetail.setWebViewClient(new MyWebViewClient());
        detailBinding.frSerDetBtnSeeMoreDetails.setText(getString(R.string.see_more_info));
        detailBinding.frSerDetBtninqueryforservices.setText(getString(R.string.enquire_for_service));
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
//            if (education != null) {
//                ((HomeActivity) getActivity()).setToolbarWithCenterTitle(education.getName(), getResources().getColor(R.color.light_eggplant), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
//            } else {
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.mEducation), getResources().getColor(R.color.light_eggplant), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

            // }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == detailBinding.frSerDetBtninqueryforservices) {
            ((HomeActivity) getActivity()).addFragment(EducationDetailFragment.this, EducationFormFragment.getEducationFormFragment(education));
        } else if (v == detailBinding.frSerDetBtnSeeMoreDetails) {
            try {
                final Uri uri = Uri.parse(seeMoreInfoUrl); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void OnSelectedItem(int position) {

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
            if (education != null) {
                callGamification();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, seeMoreInfoUrl);
                startActivity(Intent.createChooser(intent, getString(R.string.share)));
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void callGamification() {
        final RequestBody body = GamificationManager.getGamificationManager().createRequestBody("share_content", String.valueOf(education.getId()));
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
