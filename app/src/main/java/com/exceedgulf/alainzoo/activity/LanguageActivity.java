package com.exceedgulf.alainzoo.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.databinding.ActivityLanguageBinding;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.LangUtils;


public class LanguageActivity extends BaseActivity {
    private final String KEY_FROM = "KEY_FROM";
    private ActivityLanguageBinding languageBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languageBinding = DataBindingUtil.setContentView(this, R.layout.activity_language);
        languageBinding.languageLlEnglish.setOnClickListener(this);
        languageBinding.languageLlArabic.setOnClickListener(this);
        languageBinding.languageIvBack.setOnClickListener(this);
        final Typeface typefaceMedium = Typeface.createFromAsset(getAssets(), "fonts/arabic_roman.ttf");
        final Typeface typefaceRegular = Typeface.createFromAsset(getAssets(), "fonts/arabic_roman.ttf");
        languageBinding.lanTvArtitle.setTypeface(typefaceMedium);
        languageBinding.lanTvArSubtitle.setTypeface(typefaceRegular);
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(KEY_FROM)) {
            languageBinding.languageIvBack.setVisibility(View.VISIBLE);
        } else {
            languageBinding.languageIvBack.setVisibility(View.GONE);
            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            final int margin = (int) getResources().getDimension(R.dimen._16sdp);
            layoutParams.setMargins(margin, 0, margin, 0);
            languageBinding.languageTvHeader.setLayoutParams(layoutParams);
        }


    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == languageBinding.languageIvBack) {
            onBackPressed();
            overridePendingTransition(R.anim.push_in_left, R.anim.push_out_right);
            return;
        }
        if (v == languageBinding.languageLlEnglish) {
            SharedPrefceHelper.getInstance().save(PrefCons.IS_FIRST_TIME, false);
            SharedPrefceHelper.getInstance().save(PrefCons.APP_LANGUAGE, "en");

        } else if (v == languageBinding.languageLlArabic) {
            SharedPrefceHelper.getInstance().save(PrefCons.IS_FIRST_TIME, false);
            SharedPrefceHelper.getInstance().save(PrefCons.APP_LANGUAGE, "ar");
        }
        AppAlainzoo.getAppAlainzoo().setLanguage();
        if (SharedPrefceHelper.getInstance().get(PrefCons.SHOW_TUTORIAL, true)) {
            final Intent intent = new Intent(this, TutorialActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        } else {
            final Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getResources().updateConfiguration(LangUtils.getLocal(this), getApplicationContext().getResources().getDisplayMetrics());
        super.onConfigurationChanged(LangUtils.getLocal(this));
    }
}
