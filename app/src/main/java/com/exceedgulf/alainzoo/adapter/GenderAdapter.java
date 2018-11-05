package com.exceedgulf.alainzoo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.models.AnimalsCategory;

import java.util.ArrayList;


public class GenderAdapter extends ArrayAdapter<String> {

    private Activity activity;


    public GenderAdapter(Activity activity) {
        super(activity, R.layout.single_line_spinner_item);
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("planViewHolder") final View topView = activity.getLayoutInflater().inflate(R.layout.single_line_default_spinner_item, null, false);
        final TextView textView = topView.findViewById(R.id.text);
        textView.setText(getItem(position));
        return topView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final View view1 = activity.getLayoutInflater().inflate(R.layout.single_line_spinner_item, null, false);
        final TextView textView = view1.findViewById(R.id.text);
        textView.setText(getItem(position));
        return view1;
    }
}
