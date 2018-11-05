package com.exceedgulf.alainzoo.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.AddressInformatinAdapter;
import com.exceedgulf.alainzoo.database.models.Contactus;
import com.exceedgulf.alainzoo.databinding.FragmentContactUsBinding;
import com.exceedgulf.alainzoo.managers.ContactUSManager;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.views.CustomTextview;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by R.S. on 21/12/17
 */
public class ContactUsFragment extends BaseFragment {
    private FragmentContactUsBinding contactUsBinding;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        contactUsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_us, container, false);
        return contactUsBinding.getRoot();
    }

    @Override
    public void initView(final View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.contact_us), getResources().getColor(R.color.very_light_brown), null, false);
        getContactUsDetail();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.contactus, menu);
        final LayoutInflater baseInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View myCustomView = baseInflater.inflate(R.layout.menu_item_textview, null);
        ((CustomTextview) myCustomView).setText(getString(R.string.feedback));
        final MenuItem item = menu.findItem(R.id.action_feedback);
        item.setActionView(myCustomView);
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).addFragment(ContactUsFragment.this, new FeedbackFragment());
            }
        });
    }

    public void getContactUsDetail() {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        ContactUSManager.getContactUSManager().getContactUs(new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    final ArrayList<Contactus> contactusArrayList = (ArrayList<Contactus>) result;
                    showContactUsData(contactusArrayList);
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.very_light_brown);
                if (isAdded()) {
                    contactUsBinding.frcontactTvEmptyView.setVisibility(View.VISIBLE);
                    contactUsBinding.frcontactTvEmptyView.setText(getString(R.string.no_data_available));
                }
            }
        });
    }

    private void showContactUsData(ArrayList<Contactus> contactusArrayList) {
        for (int i = 0; i < contactusArrayList.size(); i++) {
            final Contactus contactus = contactusArrayList.get(i);
            final View viewContactInfo = LayoutInflater.from(getActivity()).inflate(R.layout.row_address_detail, contactUsBinding.llContactInformation, false);
            final CustomTextview customTextviewTitle = viewContactInfo.findViewById(R.id.frcontact_tvTitle);
            final RecyclerView recyclerViewAddressList = viewContactInfo.findViewById(R.id.frcontact_recycler_view);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerViewAddressList.setLayoutManager(layoutManager);
            final AddressInformatinAdapter addressInformatinAdapter = new AddressInformatinAdapter(getActivity(), contactus.getAddressInformationModelArrayList());
            recyclerViewAddressList.setAdapter(addressInformatinAdapter);
            customTextviewTitle.setText(contactus.getTitle());
            contactUsBinding.llContactInformation.addView(viewContactInfo);
        }
        contactUsBinding.frcontactTvEmptyView.setVisibility(contactusArrayList.size() > 0 ? View.GONE : View.VISIBLE);
        if (contactusArrayList.size() == 0) {
            contactUsBinding.frcontactTvEmptyView.setText(getString(R.string.no_data_available));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.contact_us), getResources().getColor(R.color.very_light_brown), null, false);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
//        if (v == contactUsBinding.frcontactTvlocationDetail) {
//            ((HomeActivity) getActivity()).addFragment(this, new ContactUsMapFragment());
//        }
    }
}
