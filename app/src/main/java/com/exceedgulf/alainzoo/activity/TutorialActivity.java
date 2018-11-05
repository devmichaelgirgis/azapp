package com.exceedgulf.alainzoo.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.adapter.ViewPagerAdapter;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.LangUtils;

public class TutorialActivity extends BaseActivity implements View.OnClickListener, ViewPagerAdapter.OnClickImageListener {
    int[] mResources = {
            R.drawable.tutorial1,
            R.drawable.tutorial2,
            R.drawable.tutorial7,
            R.drawable.tutorial3,
            R.drawable.tutorial4,
            R.drawable.tutorial5,
            R.drawable.tutorial6,
    };
    private ViewPager viewPager;
    private boolean isClick = false;
    private boolean isFromHome = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateStatusBarColor(ContextCompat.getColor(TutorialActivity.this, R.color.cool_blue));
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("Is_FROM_HOME")) {
            isFromHome = true;
        }
        setContentView(R.layout.activity_tutorial);
        viewPager = findViewById(R.id.viewpager);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(this, this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
            }

            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {
                isClick = false;
//                if (position >= mResources.length) {
//                    SharedPrefceHelper.getInstance().save(PrefCons.SHOW_TUTORIAL, false);
//                    final Intent intent = new Intent(TutorialActivity.this, HomeActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
//                }

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public void onClickChange(int position) {
        isClick = true;
        int size = position + 1;
        if (size < mResources.length) {
            viewPager.setCurrentItem(size);
        }
//        else {
//            SharedPrefceHelper.getInstance().save(PrefCons.SHOW_TUTORIAL, false);
//            final Intent intent = new Intent(TutorialActivity.this, HomeActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
//        }
    }

    @Override
    public void onSkipClick() {
        if (!isFromHome) {
            SharedPrefceHelper.getInstance().save(PrefCons.SHOW_TUTORIAL, false);
            final Intent intent = new Intent(TutorialActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        } else {
            finish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getResources().updateConfiguration(LangUtils.getLocal(this), getApplicationContext().getResources().getDisplayMetrics());
        super.onConfigurationChanged(LangUtils.getLocal(this));
    }
}
