package com.exceedgulf.alainzoo.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.database.models.WhatsNew;
import com.exceedgulf.alainzoo.databinding.FragmentWhatsNewDetailsBinding;
import com.exceedgulf.alainzoo.managers.GamificationManager;
import com.exceedgulf.alainzoo.managers.WhatsNewManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.ImageUtil;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.utils.Utils;

import java.util.Locale;

import okhttp3.RequestBody;

/**
 * Created by Paras Ghasadiya on 25/12/17.
 */

public class WhatsNewDetailFragment extends BaseFragment {
    private static final String KEY_WHATS_NEW = "KEY_WHATS_NEW";
    private static final String KEY_WHATS_NEW_ID = "KEY_WHATS_NEW_ID";
    private FragmentWhatsNewDetailsBinding detailsBinding;
    private WhatsNew whatsNew;
    private String seeMoreInfoUrl;

    public static WhatsNewDetailFragment getWhatsNewDetailFragment(final WhatsNew whatsNew) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_WHATS_NEW, whatsNew);
        final WhatsNewDetailFragment whatsNewDetailFragment = new WhatsNewDetailFragment();
        whatsNewDetailFragment.setArguments(bundle);
        return whatsNewDetailFragment;
    }

    public static WhatsNewDetailFragment getWhatsNewDetailFragment(final int whatsNewId) {
        final Bundle bundle = new Bundle();
        bundle.putInt(KEY_WHATS_NEW_ID, whatsNewId);
        final WhatsNewDetailFragment whatsNewDetailFragment = new WhatsNewDetailFragment();
        whatsNewDetailFragment.setArguments(bundle);
        return whatsNewDetailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        detailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_whats_new_details, container, false);
        return detailsBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_whats_new), getResources().getColor(R.color.slate_grey), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        final Bundle bundleExperiance = this.getArguments();
        if (bundleExperiance != null && bundleExperiance.containsKey(KEY_WHATS_NEW)) {
            whatsNew = (WhatsNew) bundleExperiance.getSerializable(KEY_WHATS_NEW);
        } else if (bundleExperiance != null && bundleExperiance.containsKey(KEY_WHATS_NEW_ID)) {
            whatsNew = getWhatsNewDetail(bundleExperiance.getInt(KEY_WHATS_NEW_ID));
        }

        final WebSettings settings = detailsBinding.frWtsneDetWvdetail.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        int textSize = (int) getResources().getDimension(R.dimen._5sdp);
        settings.setDefaultFontSize(textSize);
        if (whatsNew != null) {
            //((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(whatsNew.getName()).toString().trim(), getResources().getColor(R.color.slate_grey), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
            detailsBinding.frWtsneTvtitle.setText(Html.fromHtml(whatsNew.getName()).toString().trim());
            // detailsBinding.frWtsneDetTvdetail.setText(Html.fromHtml(whatsNew.getDetails()).toString().trim());
            detailsBinding.frWtsneDetWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(whatsNew.getDetails()), "text/html", "UTF-8", null);
            detailsBinding.frWtsneDetDate.setText(String.format(Locale.ENGLISH, "%s", Utils.formatDateTime(whatsNew.getDates().get(0).getStart_date(), "yyyy-MM-dd'T'HH:mm:ss", "d MMM yyyy")));
            detailsBinding.frWtsneDetTvtime.setText(String.format(Locale.ENGLISH, "%s", Utils.formatDateTime(whatsNew.getDates().get(0).getStart_date(), "yyyy-MM-dd'T'HH:mm:ss", "h:mm a")));
            detailsBinding.frWtsneDetTvendDate.setText(String.format(Locale.ENGLISH, "%s", Utils.formatDateTime(whatsNew.getDates().get(0).getEnd_date(), "yyyy-MM-dd'T'HH:mm:ss", "d MMM yyyy")));
            detailsBinding.frWtsneDetTvendTime.setText(String.format(Locale.ENGLISH, "%s", Utils.formatDateTime(whatsNew.getDates().get(0).getEnd_date(), "yyyy-MM-dd'T'HH:mm:ss", "h:mm a")));
            ImageUtil.loadImageFromPicasso(getActivity(), whatsNew.getThumbnail(), detailsBinding.frWtsneDetIvmain, detailsBinding.frWtsneDetIvplaceholder);
        }

        detailsBinding.frWtsneDetTvviewonmap.setOnClickListener(this);
        //getWhatsNewDetail();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_whats_new), getResources().getColor(R.color.slate_grey), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        }
    }

    private void getWhatsNewDetail() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        WhatsNewManager.getWhatsnewManager().getEntityDetails(whatsNew.getId(), new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (result != null) {
                    whatsNew = (WhatsNew) result;
                    //((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(whatsNew.getName()).toString().trim(), getResources().getColor(R.color.slate_grey), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
                    // detailsBinding.frWtsneDetTvdetail.setText(Html.fromHtml(whatsNew.getDescription()).toString().trim());
                    detailsBinding.frWtsneDetWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(whatsNew.getDetails()), "text/html", "UTF-8", null);
                    detailsBinding.frWtsneTvtitle.setText(Html.fromHtml(whatsNew.getName()).toString().trim());
                    ImageUtil.loadImageFromPicasso(getActivity(), whatsNew.getThumbnail(), detailsBinding.frWtsneDetIvmain, detailsBinding.frWtsneDetIvplaceholder);
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.slate_grey);
                if (whatsNew != null) {
                    //((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(whatsNew.getName()).toString().trim(), getResources().getColor(R.color.slate_grey), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
                    //   detailsBinding.frWtsneDetTvdetail.setText(Html.fromHtml(whatsNew.getDetails()).toString().trim());
                    detailsBinding.frWtsneDetWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(whatsNew.getDetails()), "text/html", "UTF-8", null);
                    detailsBinding.frWtsneTvtitle.setText(Html.fromHtml(whatsNew.getName()).toString().trim());
                    ImageUtil.loadImageFromPicasso(getActivity(), whatsNew.getThumbnail(), detailsBinding.frWtsneDetIvmain, detailsBinding.frWtsneDetIvplaceholder);
                }
            }
        });
    }

    private WhatsNew getWhatsNewDetail(int id) {
        return (WhatsNew) WhatsNewManager.getWhatsnewManager().getEntityDetailsFromDB(id);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == detailsBinding.frWtsneDetTvviewonmap) {
            final String lat = whatsNew.getLatitude();
            final String lon = whatsNew.getLongitude();
            if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lon)) {
                final Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lon);
                final Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    getActivity().startActivity(mapIntent);
                } else {
                    Uri gmmIntentUri1 = Uri.parse("https://www.google.com/maps/search/?api=1&destination=" + lat + "," + lon);
                    final Intent mapIntent1 = new Intent(Intent.ACTION_VIEW, gmmIntentUri1);
                    getActivity().startActivity(mapIntent1);
                }
            } else {
                SnackbarUtils.loadSnackBar(getString(R.string.no_location_available), getActivity(), R.color.slate_grey);
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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            if (whatsNew != null) {
                if (LangUtils.getCurrentLanguage().equalsIgnoreCase("ar")) {
                    seeMoreInfoUrl = "http://ain-dev.exceedgulf.net/ar/node/" + whatsNew.getId();
                } else {
                    seeMoreInfoUrl = "http://ain-dev.exceedgulf.net/node/" + whatsNew.getId();
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
        final RequestBody body = GamificationManager.getGamificationManager().createRequestBody("share_content", String.valueOf(whatsNew.getId()));
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
