package com.exceedgulf.alainzoo.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    private long mLastClickTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public abstract void initView(View view);

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getResources().updateConfiguration(LangUtils.getLocal(getActivity()), getResources().getDisplayMetrics());
        super.onViewCreated(view, savedInstanceState);
        checkMyPlaneVisitedItem();
        initView(view);

    }

    @Override
    public void onClick(View v) {
        Utils.getInstance().hideSoftKeyboard(getActivity());
    }

    public boolean isOnClick() {
        /*
          Prevents the Launch of the component multiple times
          on clicks encountered in quick succession.
         */
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }


    @SuppressLint("SetTextI18n")
    private void setMandatoryStar(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);

            if (v instanceof ViewGroup) {
                this.setMandatoryStar((ViewGroup) v);
            }

            if (v.getTag() == null) {
                continue;
            }
            final String tag = v.getTag().toString();

            if (v instanceof TextView) {
                final TextView textView = (TextView) v;
                if (tag.equalsIgnoreCase("star") && !textView.getText().toString().trim().contains("*")) {
                    final String text = textView.getText().toString().trim() + " ";
                    textView.setText(Html.fromHtml(text + "<font color='#d80003'>*</font>"));

                }
            }
        }
    }

    protected void setMandatoryStar() {
        if (getView() == null) {
            return;
        }
        final ViewGroup viewGroup = (ViewGroup) getView().findViewById(R.id.root_view);
        if (viewGroup != null) {
            setMandatoryStar(viewGroup);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getResources().updateConfiguration(LangUtils.getLocal(getActivity()), getResources().getDisplayMetrics());
        setMandatoryStar();
    }

    private void checkMyPlaneVisitedItem() {
        final Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        final Calendar calendarTwel = Calendar.getInstance(Locale.ENGLISH);
        calendarTwel.set(Calendar.HOUR_OF_DAY, 12);
        calendarTwel.set(Calendar.MINUTE, 0);
        calendarTwel.set(Calendar.SECOND, 0);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        final int hours = (calendar.get(Calendar.HOUR_OF_DAY) == 0 ? 24 : (calendar.get(Calendar.HOUR_OF_DAY)));
        Log.e("Time", hours + "");
        if (!TextUtils.isEmpty(SharedPrefceHelper.getInstance().get(PrefCons.MY_PLANE_DELETE_LAST_TIME, ""))) {
            try {
                Log.e("TimePref", SharedPrefceHelper.getInstance().get(PrefCons.MY_PLANE_DELETE_LAST_TIME, ""));
                final Date lastClearDate = dateFormat.parse(SharedPrefceHelper.getInstance().get(PrefCons.MY_PLANE_DELETE_LAST_TIME, ""));
                if ((calendar.after(calendarTwel)) && !DateUtils.isToday(lastClearDate.getTime())) {
                    Log.e("Time", "After");
                    SharedPrefceHelper.getInstance().save(PrefCons.MY_PLANE_DELETE_LAST_TIME, dateFormat.format(calendar.getTime()));
                    AlainZooDB.getInstance().myPlaneVisitedItemDao().deleteAll();
                    Log.e("Time", "ClearData");
                } else {
                    Log.e("Time", "Before");
                    Log.e("Time", "No Need ClearData");
                }
            } catch (Exception ignored) {
            }
        } else {
            SharedPrefceHelper.getInstance().save(PrefCons.MY_PLANE_DELETE_LAST_TIME, dateFormat.format(calendar.getTime()));
            Log.e("Time", "ClearData");
        }

    }

}
