package com.exceedgulf.alainzoo.fragments;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.AnimalImageListAdapter;
import com.exceedgulf.alainzoo.adapter.AnimalsListViewPagerAdapter;
import com.exceedgulf.alainzoo.database.models.Experience;
import com.exceedgulf.alainzoo.database.models.Rating;
import com.exceedgulf.alainzoo.database.models.Vote;
import com.exceedgulf.alainzoo.databinding.FragmentExperianceDetailBinding;
import com.exceedgulf.alainzoo.managers.ExperienceManager;
import com.exceedgulf.alainzoo.managers.GamificationManager;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.ImageUtil;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.utils.Utils;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.RequestBody;

/**
 * Created by P.G. on 22/12/17
 */
public class ExperianceDetailFragment extends BaseFragment implements AnimalImageListAdapter.OnItemClick {
    public static final String KEY_EXPERIANCE = "KeyExperiance";
    public static final String KEY_EXPERIANCE_ID = "KeyExperianceId";
    private FragmentExperianceDetailBinding detailBinding;
    private Experience experience = null;
    private AnimalImageListAdapter animalImageListAdapter;
    private int id;
    private Rating userRating;
    private String seeMoreInfoUrl;
    private RatingBar.OnRatingBarChangeListener onRatingBarChangeListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            //if (SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn()) {
            if (rating < 1.0f) {
                detailBinding.expDetRbratingbar.setOnRatingBarChangeListener(null);
                ratingBar.setRating(1.0f);
                detailBinding.expDetRbratingbar.setOnRatingBarChangeListener(onRatingBarChangeListener);
            }

            if (SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn()) {
                if (userRating != null && userRating.getRating().size() > 0) {
                    SnackbarUtils.loadSnackBar(getString(R.string.rate_already_sent), getActivity(), R.color.tomato);
                    if (experience.getRating().size() > 0) {
                        final Vote vote = experience.getRating().get(0);
                        detailBinding.expDetRbratingbar.setRating(vote.getValue());
                    }
                } else {
                    openRatingDialog();
                }
            } else {
                ((HomeActivity) getActivity()).loginDialog(getString(R.string.login_to_rate), false);
            }

            // } else {
            //    ((HomeActivity) getActivity()).loginDialog();
            //}
        }
    };

    public static ExperianceDetailFragment getExperianceDetailFragment(final Experience experience) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXPERIANCE, experience);
        final ExperianceDetailFragment experianceDetailFragment = new ExperianceDetailFragment();
        experianceDetailFragment.setArguments(bundle);
        return experianceDetailFragment;
    }

    public static ExperianceDetailFragment getExperianceDetailFragment(final int id) {
        final Bundle bundle = new Bundle();
        bundle.putInt(KEY_EXPERIANCE_ID, id);
        final ExperianceDetailFragment experianceDetailFragment = new ExperianceDetailFragment();
        experianceDetailFragment.setArguments(bundle);
        return experianceDetailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        detailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_experiance_detail, container, false);
        return detailBinding.getRoot();
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
            if (experience != null) {
                if (LangUtils.getCurrentLanguage().equalsIgnoreCase("ar")) {
                    seeMoreInfoUrl = "http://ain-dev.exceedgulf.net/ar/node/" + experience.getId();
                } else {
                    seeMoreInfoUrl = "http://ain-dev.exceedgulf.net/node/" + experience.getId();
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

    @Override
    public void initView(View view) {

        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.experiences), getResources().getColor(R.color.tomato), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        final Bundle bundleExperiance = this.getArguments();

        detailBinding.expDetRvimages.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        animalImageListAdapter = new AnimalImageListAdapter(getActivity(), this);
        detailBinding.expDetRvimages.setAdapter(animalImageListAdapter);

        detailBinding.expDetTvratingcount.setText(String.format(Locale.ENGLISH, "%d", 0));
        final WebSettings settings = detailBinding.expDetWvdetail.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        int textSize = (int) getResources().getDimension(R.dimen._5sdp);
        settings.setDefaultFontSize(textSize);

        if (bundleExperiance != null && bundleExperiance.containsKey(KEY_EXPERIANCE)) {
            experience = (Experience) bundleExperiance.getSerializable(KEY_EXPERIANCE);
            getExperienceDetail();
            getExperienceRating(experience.getId());
        } else if (bundleExperiance != null && bundleExperiance.containsKey(KEY_EXPERIANCE_ID)) {
            id = bundleExperiance.getInt(KEY_EXPERIANCE_ID);
            getExperienceDetail(id);
        }
    }

    private void getExperienceDetail() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        ExperienceManager.getExperienceManager().getEntityDetails(experience.getId(), new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (result != null) {
                    experience = (Experience) result;
                    if (experience.getRating().size() > 0) {
                        final Vote vote = experience.getRating().get(0);
                        detailBinding.expDetRbratingbar.setRating(vote.getValue());
                        detailBinding.expDetTvratingcount.setText(String.format(Locale.ENGLISH, "%.1f", vote.getValue()));

                    } else {
                        detailBinding.expDetRbratingbar.setRating(0);
                        detailBinding.expDetTvratingcount.setText(String.format(Locale.ENGLISH, "%d", 0));
                    }
                    //((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(experience.getName()).toString().trim(), getResources().getColor(R.color.tomato), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
                    //detailBinding.expDetTvdetail.setText(Html.fromHtml(experience.getDetails()));
                    detailBinding.expDetWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(experience.getDetails()), "text/html", "UTF-8", null);
                    //detailBinding.expDetWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml("<a href= \"https://developer.android.com/reference/android/webkit/WebView.HitTestResult.html\">hi</a>"), "text/html", "UTF-8", null);
                    detailBinding.expDetTvtitle.setText(Html.fromHtml(experience.getName()).toString().trim());
                    detailBinding.expDetTvTimePrice.setText(experience.getOpening_hours());
                    ImageUtil.loadImageFromPicasso(getActivity(), experience.getImage(), detailBinding.expDetIvmain, detailBinding.expDetIvplaceholder);
                    animalImageListAdapter.addItems(experience.getImages());
                    detailBinding.expDetRbratingbar.setOnRatingBarChangeListener(onRatingBarChangeListener);
                    detailBinding.frexpDetTvEmptyView.setVisibility(View.GONE);

                    detailBinding.expDetWvdetail.setWebViewClient(new MyWebViewClient());


                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.tomato);
                if (experience != null) {
                    //((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(experience.getName()).toString().trim(), getResources().getColor(R.color.tomato), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
                    //  detailBinding.expDetTvdetail.setText(Html.fromHtml(experience.getDetails()));
                    detailBinding.expDetWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(experience.getDetails()), "text/html", "UTF-8", null);
                    detailBinding.expDetTvtitle.setText(Html.fromHtml(experience.getName()).toString().trim());
                    ImageUtil.loadImageFromPicasso(getActivity(), experience.getImage(), detailBinding.expDetIvmain, detailBinding.expDetIvplaceholder);
                    if (experience.getRating().size() > 0) {
                        final Vote vote = experience.getRating().get(0);
                        detailBinding.expDetRbratingbar.setRating(vote.getValue());
                        detailBinding.expDetTvratingcount.setText(String.format(Locale.ENGLISH, "%.1f", vote.getValue()));

                    } else {
                        detailBinding.expDetRbratingbar.setRating(0);
                        detailBinding.expDetTvratingcount.setText(String.format(Locale.ENGLISH, "%d", 0));
                    }
                    animalImageListAdapter.addItems(experience.getImages());
                    detailBinding.expDetRbratingbar.setOnRatingBarChangeListener(onRatingBarChangeListener);
                    detailBinding.frexpDetTvEmptyView.setVisibility(View.GONE);


                }
            }
        });
    }

    private void getExperienceDetail(int id) {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        ExperienceManager.getExperienceManager().getEntityDetails(id, new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (result != null) {
                    experience = (Experience) result;
                    if (experience.getRating().size() > 0) {
                        final Vote vote = experience.getRating().get(0);
                        detailBinding.expDetRbratingbar.setRating(vote.getValue());
                        detailBinding.expDetTvratingcount.setText(String.format(Locale.ENGLISH, "%.1f", vote.getValue()));

                    } else {
                        detailBinding.expDetRbratingbar.setRating(0);
                        detailBinding.expDetTvratingcount.setText(String.format(Locale.ENGLISH, "%d", 0));
                    }
                    //((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(experience.getName()).toString().trim(), getResources().getColor(R.color.tomato), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
                    //detailBinding.expDetTvdetail.setText(Html.fromHtml(experience.getDetails()));
                    detailBinding.expDetWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(experience.getDetails()), "text/html", "UTF-8", null);
                    detailBinding.expDetTvtitle.setText(Html.fromHtml(experience.getName()).toString().trim());
                    ImageUtil.loadImageFromPicasso(getActivity(), experience.getImage(), detailBinding.expDetIvmain, detailBinding.expDetIvplaceholder);
                    animalImageListAdapter.addItems(experience.getImages());
                    detailBinding.expDetRbratingbar.setOnRatingBarChangeListener(onRatingBarChangeListener);
                    detailBinding.frexpDetTvEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.tomato);
                if (experience != null) {
                    //((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(experience.getName()).toString().trim(), getResources().getColor(R.color.tomato), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
                    //  detailBinding.expDetTvdetail.setText(Html.fromHtml(experience.getDetails()));
                    detailBinding.expDetWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(experience.getDetails()), "text/html", "UTF-8", null);
                    detailBinding.expDetTvtitle.setText(Html.fromHtml(experience.getName()).toString().trim());
                    ImageUtil.loadImageFromPicasso(getActivity(), experience.getImage(), detailBinding.expDetIvmain, detailBinding.expDetIvplaceholder);
                    if (experience.getRating().size() > 0) {
                        final Vote vote = experience.getRating().get(0);
                        detailBinding.expDetRbratingbar.setRating(vote.getValue());
                        detailBinding.expDetTvratingcount.setText(String.format(Locale.ENGLISH, "%.1f", vote.getValue()));

                    } else {
                        detailBinding.expDetRbratingbar.setRating(0);
                        detailBinding.expDetTvratingcount.setText(String.format(Locale.ENGLISH, "%d", 0));
                    }
                    animalImageListAdapter.addItems(experience.getImages());
                    detailBinding.expDetRbratingbar.setOnRatingBarChangeListener(onRatingBarChangeListener);
                    detailBinding.frexpDetTvEmptyView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getExperienceRating(int experienceId) {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        ExperienceManager.getExperienceManager().getExperienceRating(experienceId, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (resultList.size() > 0) {
                    userRating = (Rating) resultList.get(0);
                    if (userRating.getRating().size() > 0) {
                        //detailBinding.expDetRbratingbar.setOnRatingBarChangeListener(null);
                        //detailBinding.expDetRbratingbar.setIsIndicator(true);
                        detailBinding.expDetRbratingbar.setFocusable(false);
                    } else {
                        //detailBinding.expDetRbratingbar.setOnRatingBarChangeListener(onRatingBarChangeListener);
                    }
                } else {
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (!isOnClick()) {
            return;
        }

    }

    private void openRatingDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_rating, null);
        builder.setView(dialogView);
        final AlertDialog alertDialogLogout = builder.create();
        final AppCompatRatingBar appCompatRatingBar = dialogView.findViewById(R.id.dialog_exp_rbRating);
        appCompatRatingBar.setRating(detailBinding.expDetRbratingbar.getRating());
        final TextView tvCancel = dialogView.findViewById(R.id.dialog_exp_tvcancel);
        final TextView tvRateNow = dialogView.findViewById(R.id.dialog_exp_tvRateNow);
        appCompatRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 1.0f) {
                    ratingBar.setRating(1.0f);
                }
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogLogout.cancel();
                appCompatRatingBar.setRating(detailBinding.expDetRbratingbar.getRating());
            }
        });
        tvRateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (appCompatRatingBar.getRating() == 0.0) {
                    SnackbarUtils.loadSnackBar(dialogView, getString(R.string.rating_error), getActivity(), R.color.tomato);
                } else {
                    alertDialogLogout.cancel();
                    submitVote(appCompatRatingBar.getRating());
                }
            }
        });
        alertDialogLogout.show();
        final int width = (int) getResources().getDimension(R.dimen._260sdp);
        alertDialogLogout.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void submitVote(final float rating) {
        detailBinding.expDetRbratingbar.setOnRatingBarChangeListener(null);
        detailBinding.expDetRbratingbar.setIsIndicator(true);
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        ExperienceManager.getExperienceManager().submitVote(experience.getId(), rating, new ApiDetailCallback<String>() {
            @Override
            public void onSuccess(String result) {
                DisplayDialog.getInstance().dismissProgressDialog();
                //detailBinding.expDetRbratingbar.setRating(rating);
                //detailBinding.expDetTvratingcount.setText(String.format(Locale.getDefault(), "%.1f", rating));
                getExperienceDetail();
                SnackbarUtils.loadSnackBar(result, getActivity(), R.color.tomato);
                //detailBinding.expDetRbratingbar.setOnRatingBarChangeListener(onRatingBarChangeListener);
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.tomato);
                // detailBinding.expDetRbratingbar.setRating(0);
                // detailBinding.expDetTvratingcount.setText(String.format(Locale.getDefault(), "%d", 0));
                detailBinding.expDetRbratingbar.setOnRatingBarChangeListener(onRatingBarChangeListener);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.mExperiences), getResources().getColor(R.color.tomato), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (experience != null) {
            getExperienceRating(experience.getId());
        } else if (id != 0) {
            getExperienceRating(id);
        }
    }

    private void openAnimalImagesDialog(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setCancelable(true);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_images, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.black_71)));
        final ImageView ivBack = dialogView.findViewById(R.id.dialog_images_ivBack);
        final ViewPager vpMain = dialogView.findViewById(R.id.dialog_images_vpMain);
        final AnimalsListViewPagerAdapter animalsListAdapter = new AnimalsListViewPagerAdapter(getActivity());
        animalsListAdapter.addItems(experience.getImages());
        vpMain.setAdapter(animalsListAdapter);
        vpMain.setCurrentItem(position);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void OnSelectedItem(int position) {
        if (!isOnClick()) {
            return;
        }
        openAnimalImagesDialog(position);
    }

    public void callGamification() {
        final RequestBody body = GamificationManager.getGamificationManager().createRequestBody("share_content", String.valueOf(experience.getId()));
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
