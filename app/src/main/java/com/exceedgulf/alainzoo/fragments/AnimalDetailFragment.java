package com.exceedgulf.alainzoo.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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

import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.AnimalImageListAdapter;
import com.exceedgulf.alainzoo.adapter.AnimalsListViewPagerAdapter;
import com.exceedgulf.alainzoo.database.models.Animal;
import com.exceedgulf.alainzoo.databinding.FragmentAnimalDetailBinding;
import com.exceedgulf.alainzoo.managers.AnimalManager;
import com.exceedgulf.alainzoo.managers.GamificationManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.ImageUtil;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.utils.Utils;

import java.util.Locale;

import okhttp3.RequestBody;

/**
 * Created by R.S. on 21/12/17
 */
public class AnimalDetailFragment extends BaseFragment implements AnimalImageListAdapter.OnItemClick {
    public static final String KEY_ANIMAL = "KeyAnimal";
    private static final String KEY_ANIMAL_ID = "KeyAnimalId";
    private FragmentAnimalDetailBinding detailBinding;
    private Animal animal = null;
    private AnimalImageListAdapter animalImageListAdapter;
    private int id;
    private String seeMoreInfoUrl;

    public static AnimalDetailFragment getAnimalDetailFragment(final Animal animal) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_ANIMAL, animal);
        final AnimalDetailFragment animalDetailFragment = new AnimalDetailFragment();
        animalDetailFragment.setArguments(bundle);
        return animalDetailFragment;
    }

    public static AnimalDetailFragment getAnimalDetailFragment(final int id) {
        final Bundle bundle = new Bundle();
        bundle.putInt(KEY_ANIMAL_ID, id);
        final AnimalDetailFragment animalDetailFragment = new AnimalDetailFragment();
        animalDetailFragment.setArguments(bundle);
        return animalDetailFragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        detailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_animal_detail, container, false);
        return detailBinding.getRoot();
    }

    @Override
    public void initView(final View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.animal), getResources().getColor(R.color.camo), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        final Bundle bundleExperiance = this.getArguments();

        detailBinding.animDetRvimages.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        animalImageListAdapter = new AnimalImageListAdapter(getActivity(), this);
        detailBinding.animDetRvimages.setAdapter(animalImageListAdapter);
        final WebSettings settings = detailBinding.animDetWvdetail.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        int textSize = (int) getResources().getDimension(R.dimen._5sdp);
        settings.setDefaultFontSize(textSize);

        if (bundleExperiance != null && bundleExperiance.containsKey(KEY_ANIMAL)) {
            animal = (Animal) bundleExperiance.getSerializable(KEY_ANIMAL);
            getAnimalDetail();
        } else if (bundleExperiance != null && bundleExperiance.containsKey(KEY_ANIMAL_ID)) {
            id = bundleExperiance.getInt(KEY_ANIMAL_ID);
            getAnimalDetail(id);
        }
        detailBinding.animDetWvdetail.setWebViewClient(new MyWebViewClient());
        detailBinding.animDetTvFeedinghrTitle.setText(getString(R.string.feeding_opening_hours));
    }

    private void getAnimalDetail() {
        if (animal != null) {
            detailBinding.animDetWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(animal.getDetails()), "text/html", "UTF-8", null);
            detailBinding.animDetTvname.setText(Html.fromHtml(animal.getName()).toString().trim());
            detailBinding.animDetTvFeedinghr.setText(Html.fromHtml(animal.getFeeding_hours()));
            ImageUtil.loadImageFromPicasso(getActivity(), animal.getThumbnail(), detailBinding.animDetIvmain, detailBinding.animDetIvplaceholder);
            animalImageListAdapter.addItems(animal.getImages());
            detailBinding.frAnimDetTvEmptyView.setVisibility(View.GONE);
        } else {
            DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
            AnimalManager.getAnimalManager().getEntityDetails(animal.getId(), new ApiDetailCallback() {
                @Override
                public void onSuccess(Object result) {
                    DisplayDialog.getInstance().dismissProgressDialog();
                    if (isAdded()) {
                        if (result != null) {
                            animal = (Animal) result;
                            detailBinding.animDetWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(animal.getDetails()), "text/html", "UTF-8", null);
                            detailBinding.animDetTvname.setText(Html.fromHtml(animal.getName()).toString().trim());
                            detailBinding.animDetTvFeedinghr.setText(Html.fromHtml(animal.getFeeding_hours()));
                            ImageUtil.loadImageFromPicasso(getActivity(), animal.getThumbnail(), detailBinding.animDetIvmain, detailBinding.animDetIvplaceholder);
                            animalImageListAdapter.addItems(animal.getImages());
                            detailBinding.frAnimDetTvEmptyView.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFaild(String message) {
                    DisplayDialog.getInstance().dismissProgressDialog();
                    SnackbarUtils.loadSnackBar(message, getActivity(), R.color.camo);
                    if (isAdded()) {
                        if (animal != null) {
                            detailBinding.animDetWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(animal.getDetails()), "text/html", "UTF-8", null);
                            detailBinding.animDetTvname.setText(Html.fromHtml(animal.getName()).toString().trim());
                            detailBinding.animDetTvFeedinghr.setText(Html.fromHtml(animal.getFeeding_hours()));
                            ImageUtil.loadImageFromPicasso(getActivity(), animal.getThumbnail(), detailBinding.animDetIvmain, detailBinding.animDetIvplaceholder);
                            animalImageListAdapter.addItems(animal.getImages());
                            detailBinding.frAnimDetTvEmptyView.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }

    private void getAnimalDetail(int id) {
        animal = (Animal) AnimalManager.getAnimalManager().getEntityDetailsFromDB(id);
        if (animal != null) {
            detailBinding.animDetWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(animal.getDetails()), "text/html", "UTF-8", null);
            detailBinding.animDetTvname.setText(Html.fromHtml(animal.getName()).toString().trim());
            detailBinding.animDetTvFeedinghr.setText(Html.fromHtml(animal.getFeeding_hours()));
            ImageUtil.loadImageFromPicasso(getActivity(), animal.getThumbnail(), detailBinding.animDetIvmain, detailBinding.animDetIvplaceholder);
            animalImageListAdapter.addItems(animal.getImages());
            detailBinding.frAnimDetTvEmptyView.setVisibility(View.GONE);
        } else {
            DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
            AnimalManager.getAnimalManager().getEntityDetails(id, new ApiDetailCallback() {
                @Override
                public void onSuccess(Object result) {
                    DisplayDialog.getInstance().dismissProgressDialog();
                    if (isAdded()) {
                        if (result != null) {
                            animal = (Animal) result;
                            //((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(animal.getName()).toString().trim(), getResources().getColor(R.color.camo), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
                            //  detailBinding.animDetTvdetail.setText(Html.fromHtml(animal.getDetails()));
                            detailBinding.animDetWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(animal.getDetails()), "text/html", "UTF-8", null);
                            detailBinding.animDetTvname.setText(Html.fromHtml(animal.getName()).toString().trim());
                            detailBinding.animDetTvFeedinghr.setText(Html.fromHtml(animal.getFeeding_hours()));
                            ImageUtil.loadImageFromPicasso(getActivity(), animal.getThumbnail(), detailBinding.animDetIvmain, detailBinding.animDetIvplaceholder);
                            animalImageListAdapter.addItems(animal.getImages());
                            detailBinding.frAnimDetTvEmptyView.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFaild(String message) {
                    DisplayDialog.getInstance().dismissProgressDialog();
                    SnackbarUtils.loadSnackBar(message, getActivity(), R.color.camo);
                    if (isAdded()) {
                        if (animal != null) {
                            //((HomeActivity) getActivity()).setToolbarWithCenterTitle(Html.fromHtml(animal.getName()).toString().trim(), getResources().getColor(R.color.camo), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
                            //  detailBinding.animDetTvdetail.setText(Html.fromHtml(animal.getDetails()));
                            detailBinding.animDetWvdetail.loadDataWithBaseURL("", Utils.getInstance().getFormatedHtml(animal.getDetails()), "text/html", "UTF-8", null);
                            detailBinding.animDetTvname.setText(Html.fromHtml(animal.getName()).toString().trim());
                            detailBinding.animDetTvFeedinghr.setText(Html.fromHtml(animal.getFeeding_hours()));
                            ImageUtil.loadImageFromPicasso(getActivity(), animal.getThumbnail(), detailBinding.animDetIvmain, detailBinding.animDetIvplaceholder);
                            animalImageListAdapter.addItems(animal.getImages());
                            detailBinding.frAnimDetTvEmptyView.setVisibility(View.GONE);
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
            if (animal != null) {
                if (LangUtils.getCurrentLanguage().equalsIgnoreCase("ar")) {
                    seeMoreInfoUrl = "http://ain-dev.exceedgulf.net/ar/node/" + animal.getId();
                } else {
                    seeMoreInfoUrl = "http://ain-dev.exceedgulf.net/node/" + animal.getId();
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
        final RequestBody body = GamificationManager.getGamificationManager().createRequestBody("share_content", String.valueOf(animal.getId()));
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
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.animal), getResources().getColor(R.color.camo), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

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
        animalsListAdapter.addItems(animal.getImages());
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

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }
}
