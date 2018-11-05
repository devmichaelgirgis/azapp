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
import com.exceedgulf.alainzoo.database.models.Attraction;
import com.exceedgulf.alainzoo.databinding.FragmentAttractionDetailBinding;
import com.exceedgulf.alainzoo.managers.AttractionsManager;
import com.exceedgulf.alainzoo.managers.GamificationManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.ImageUtil;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.utils.Utils;

import java.util.Locale;

import okhttp3.RequestBody;

/**
 * Created by P.G. on 27/12/17
 */
public class AttractionsDetailFragment extends BaseFragment {
    public static final String KEY_ATTRACTION = "KeyAttraction";
    public static final String KEY_ATTRACTION_ID = "KeyAttractionId";
    private FragmentAttractionDetailBinding detailBinding;
    private Attraction attraction;
    private int id;
    private String seeMoreInfoUrl;


    public static AttractionsDetailFragment getAttractionsDetailFragment(final Attraction attraction) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_ATTRACTION, attraction);
        final AttractionsDetailFragment attractionsDetailFragment = new AttractionsDetailFragment();
        attractionsDetailFragment.setArguments(bundle);
        return attractionsDetailFragment;

    }

    public static AttractionsDetailFragment getAttractionsDetailFragment(final int attractionId) {
        final Bundle bundle = new Bundle();
        bundle.putInt(KEY_ATTRACTION_ID, attractionId);
        final AttractionsDetailFragment attractionsDetailFragment = new AttractionsDetailFragment();
        attractionsDetailFragment.setArguments(bundle);
        return attractionsDetailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        detailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_attraction_detail, container, false);
        return detailBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        final Bundle bundleExperiance = this.getArguments();

        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.attraction), getResources().getColor(R.color.colorSzdcl), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        final WebSettings settings = detailBinding.frAttdeWvdetail.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        int textSize = (int) getResources().getDimension(R.dimen._5sdp);
        settings.setDefaultFontSize(textSize);

        if (bundleExperiance != null && bundleExperiance.containsKey(KEY_ATTRACTION)) {
            attraction = (Attraction) bundleExperiance.getSerializable(KEY_ATTRACTION);
            getAttractionDetail();
        } else if (bundleExperiance != null && bundleExperiance.containsKey(KEY_ATTRACTION_ID)) {
            id = bundleExperiance.getInt(KEY_ATTRACTION_ID);
            getAttractionDetail(id);
        }
    }

    private void getAttractionDetail() {
        if (attraction != null) {
            //((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(attraction.getName()).toString().trim(), getResources().getColor(R.color.camo), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
            detailBinding.frAttdeWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(attraction.getDescription()), "text/html", "UTF-8", null);
            detailBinding.frAttdeTvname.setText(Html.fromHtml(attraction.getName()).toString().trim());
            ImageUtil.loadImageFromPicasso(getActivity(), attraction.getThumbnail(), detailBinding.frAttdeIvmain, detailBinding.frAttdeIvplaceholder);
            detailBinding.frAttdeTvEmptyView.setVisibility(View.GONE);
        } else {
            DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
            AttractionsManager.getAttractionManager().getEntityDetails(attraction.getId(), new ApiDetailCallback() {
                @Override
                public void onSuccess(Object result) {
                    DisplayDialog.getInstance().dismissProgressDialog();
                    if (isAdded()) {
                        if (result != null) {
                            attraction = (Attraction) result;
                            //((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(attraction.getName()).toString().trim(), getResources().getColor(R.color.camo), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
                            //  detailBinding.frAttdeTvdetail.setText(Html.fromHtml(attraction.getDescription()));
                            detailBinding.frAttdeWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(attraction.getDescription()), "text/html", "UTF-8", null);
                            detailBinding.frAttdeTvname.setText(Html.fromHtml(attraction.getName()).toString().trim());
                            ImageUtil.loadImageFromPicasso(getActivity(), attraction.getThumbnail(), detailBinding.frAttdeIvmain, detailBinding.frAttdeIvplaceholder);
                            detailBinding.frAttdeTvEmptyView.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFaild(String message) {
                    DisplayDialog.getInstance().dismissProgressDialog();
                    SnackbarUtils.loadSnackBar(message, getActivity(), R.color.camo);
                    if (isAdded()) {
                        if (attraction != null) {
                            //((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(attraction.getName()).toString().trim(), getResources().getColor(R.color.camo), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
                            //    detailBinding.frAttdeTvdetail.setText(Html.fromHtml(attraction.getDescription()));
                            detailBinding.frAttdeWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(attraction.getDescription()), "text/html", "UTF-8", null);
                            detailBinding.frAttdeTvname.setText(Html.fromHtml(attraction.getName()).toString().trim());
                            ImageUtil.loadImageFromPicasso(getActivity(), attraction.getThumbnail(), detailBinding.frAttdeIvmain, detailBinding.frAttdeIvplaceholder);
                            detailBinding.frAttdeTvEmptyView.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }

    private void getAttractionDetail(int attractionId) {
        attraction = (Attraction) AttractionsManager.getAttractionManager().getEntityDetailsFromDB(attractionId);
        if (attraction != null) {
            //((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(attraction.getName()).toString().trim(), getResources().getColor(R.color.camo), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
            //    detailBinding.frAttdeTvdetail.setText(Html.fromHtml(attraction.getDescription()));
            detailBinding.frAttdeWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(attraction.getDescription()), "text/html", "UTF-8", null);
            detailBinding.frAttdeTvname.setText(Html.fromHtml(attraction.getName()).toString().trim());
            ImageUtil.loadImageFromPicasso(getActivity(), attraction.getThumbnail(), detailBinding.frAttdeIvmain, detailBinding.frAttdeIvplaceholder);
            detailBinding.frAttdeTvEmptyView.setVisibility(View.GONE);

        } else {
            DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
            AttractionsManager.getAttractionManager().getEntityDetails(attractionId, new ApiDetailCallback() {
                @Override
                public void onSuccess(Object result) {
                    DisplayDialog.getInstance().dismissProgressDialog();
                    if (isAdded()) {
                        if (result != null) {
                            attraction = (Attraction) result;
                            //((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(attraction.getName()).toString().trim(), getResources().getColor(R.color.camo), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
                            //  detailBinding.frAttdeTvdetail.setText(Html.fromHtml(attraction.getDescription()));
                            detailBinding.frAttdeWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(attraction.getDescription()), "text/html", "UTF-8", null);
                            detailBinding.frAttdeTvname.setText(Html.fromHtml(attraction.getName()).toString().trim());
                            ImageUtil.loadImageFromPicasso(getActivity(), attraction.getThumbnail(), detailBinding.frAttdeIvmain, detailBinding.frAttdeIvplaceholder);
                            detailBinding.frAttdeTvEmptyView.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFaild(String message) {
                    DisplayDialog.getInstance().dismissProgressDialog();
                    SnackbarUtils.loadSnackBar(message, getActivity(), R.color.camo);
                    if (isAdded()) {
                        if (attraction != null) {
                            //((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(attraction.getName()).toString().trim(), getResources().getColor(R.color.camo), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
                            //    detailBinding.frAttdeTvdetail.setText(Html.fromHtml(attraction.getDescription()));
                            detailBinding.frAttdeWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(attraction.getDescription()), "text/html", "UTF-8", null);
                            detailBinding.frAttdeTvname.setText(Html.fromHtml(attraction.getName()).toString().trim());
                            ImageUtil.loadImageFromPicasso(getActivity(), attraction.getThumbnail(), detailBinding.frAttdeIvmain, detailBinding.frAttdeIvplaceholder);
                            detailBinding.frAttdeTvEmptyView.setVisibility(View.GONE);
                        }
                    }
                }
            });
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
            if (attraction != null) {
                if (LangUtils.getCurrentLanguage().equalsIgnoreCase("ar")) {
                    seeMoreInfoUrl = "http://ain-dev.exceedgulf.net/ar/node/" + attraction.getId();
                } else {
                    seeMoreInfoUrl = "http://ain-dev.exceedgulf.net/node/" + attraction.getId();
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
        final RequestBody body = GamificationManager.getGamificationManager().createRequestBody("share_content", String.valueOf(attraction.getId()));
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.attraction), getResources().getColor(R.color.colorSzdcl), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        }
    }

}
