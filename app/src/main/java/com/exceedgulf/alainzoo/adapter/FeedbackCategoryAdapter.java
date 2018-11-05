package com.exceedgulf.alainzoo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.models.FeedbackCategory;

import java.util.ArrayList;


public class FeedbackCategoryAdapter extends ArrayAdapter<FeedbackCategory> {

    private Activity activity;
    private ArrayList<FeedbackCategory> feedbackCategoryArrayList;

    public FeedbackCategoryAdapter(Activity activity, ArrayList<FeedbackCategory> feedbackCategoryArrayList) {
        super(activity, R.layout.single_line_spinner_item, feedbackCategoryArrayList);
        this.activity = activity;
        this.feedbackCategoryArrayList = feedbackCategoryArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("planViewHolder") final View topView = activity.getLayoutInflater().inflate(R.layout.single_line_default_spinner_item, null, false);
        final TextView textView = topView.findViewById(R.id.text);
        topView.findViewById(R.id.ivArrow).setVisibility(View.GONE);
        final FeedbackCategory feedbackCategory = getItem(position);
        textView.setText(feedbackCategory.getTitle());
        return topView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final View view1 = activity.getLayoutInflater().inflate(R.layout.single_line_spinner_item, null, false);
        final TextView textView = view1.findViewById(R.id.text);
        final FeedbackCategory feedbackCategory = getItem(position);
        textView.setText(feedbackCategory.getTitle());
        return view1;
    }
}
