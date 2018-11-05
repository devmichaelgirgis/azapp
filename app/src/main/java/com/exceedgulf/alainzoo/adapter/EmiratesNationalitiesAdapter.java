package com.exceedgulf.alainzoo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.database.models.Emirates;
import com.exceedgulf.alainzoo.database.models.Nationalities;

import java.util.ArrayList;


public class EmiratesNationalitiesAdapter<T> extends ArrayAdapter<T> {

    private Activity activity;
    private ArrayList<T> emiratesNatinality;

    public EmiratesNationalitiesAdapter(Activity activity, ArrayList<T> emiratesNatinality) {
        super(activity, R.layout.single_line_spinner_item, emiratesNatinality);
        this.activity = activity;
        this.emiratesNatinality = emiratesNatinality;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("ViewHolder") final View parenteView = activity.getLayoutInflater().inflate(R.layout.single_line_default_spinner_item, null, false);
        final TextView textView = parenteView.findViewById(R.id.text);
        parenteView.findViewById(R.id.ivArrow).setVisibility(View.GONE);
        final Object object = emiratesNatinality.get(position);
        if (object instanceof Emirates) {
            final Emirates emirates = (Emirates) object;
            textView.setText(emirates.getTitle());
        } else {
            final Nationalities nationalities = (Nationalities) object;
            textView.setText(nationalities.getTitle());
        }

        return parenteView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final View childView = activity.getLayoutInflater().inflate(R.layout.single_line_spinner_item, null, false);
        final TextView textView = childView.findViewById(R.id.text);
        final Object object = emiratesNatinality.get(position);
        if (object instanceof Emirates) {
            final Emirates emirates = (Emirates) object;
            textView.setText(emirates.getTitle());
        } else {
            final Nationalities nationalities = (Nationalities) object;
            textView.setText(nationalities.getTitle());
        }
        return childView;
    }
}
