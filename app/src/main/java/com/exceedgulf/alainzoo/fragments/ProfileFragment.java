package com.exceedgulf.alainzoo.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.BadgeAdapter;
import com.exceedgulf.alainzoo.adapter.UsersAdapter;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.Avatars;
import com.exceedgulf.alainzoo.database.models.Family;
import com.exceedgulf.alainzoo.databinding.FragmentProfileBinding;
import com.exceedgulf.alainzoo.managers.AvatarsManager;
import com.exceedgulf.alainzoo.managers.ProfileManager;
import com.exceedgulf.alainzoo.models.BadgeModel;
import com.exceedgulf.alainzoo.models.UserModel;
import com.exceedgulf.alainzoo.models.UsersModel;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.views.CustomTextview;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class ProfileFragment extends BaseFragment implements UsersAdapter.AddUserListener {

    private BadgeAdapter badgeAdapter;
    private UsersAdapter usersAdapter;
    private FragmentProfileBinding profileBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return profileBinding.getRoot();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.profile, menu);
        final LayoutInflater baseInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View myCustomView = baseInflater.inflate(R.layout.menu_item_textview, null);
        final SpannableString content = new SpannableString(getString(R.string.my_visit_plan));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        ((CustomTextview) myCustomView).setText(content);
        final MenuItem item = menu.findItem(R.id.action_my_visit_plan);
        item.setActionView(myCustomView);
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).addFragment(ProfileFragment.this, new PlaneConfirmFragment());
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void initView(final View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_profile), getResources().getColor(R.color.light_eggplant), null, false);

        profileBinding.frProfileRvBadges.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        badgeAdapter = new BadgeAdapter(getActivity());
        profileBinding.frProfileRvBadges.setAdapter(badgeAdapter);

        profileBinding.frProfileRvUsers.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        usersAdapter = new UsersAdapter(getActivity(), this);
        profileBinding.frProfileRvUsers.setAdapter(usersAdapter);

        profileBinding.frProfileTvLeaderboard.setOnClickListener(this);
        profileBinding.frProfileIvEdit.setOnClickListener(this);
        profileBinding.frProfileIvLogout.setOnClickListener(this);
        profileBinding.frProfileIvinfo.setOnClickListener(this);

        //getUserDetail();

        setUserInformation();
        setBadgeData();
        setUsersData();
    }

    private void setUserInformation() {
        if (SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn()) {
            final Avatars avatars = (Avatars) AvatarsManager.getAvatarsManager().getEntityDetailsFromDB(SharedPrefceHelper.getInstance().get(PrefCons.USER_AVATAR_ID, 0));
            if (!TextUtils.isEmpty(SharedPrefceHelper.getInstance().get(PrefCons.USER_FIELD_NAME, ""))) {
                profileBinding.frProfileTvUsername.setText(SharedPrefceHelper.getInstance().get(PrefCons.USER_FIELD_NAME, ""));
            }
            if (avatars != null && !TextUtils.isEmpty(avatars.getImage())) {
                Picasso.with(getActivity()).load(avatars.getImage()).into(profileBinding.navIvuserprofilepic);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (getActivity() != null) {
                ((HomeActivity) getActivity()).disableCollapse();
                ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.title_profile), getResources().getColor(R.color.light_eggplant), null, false);
                setUserInformation();
                setUsersData();
                ((HomeActivity) getActivity()).setUserData();
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == profileBinding.frProfileTvLeaderboard) {
            ((HomeActivity) getActivity()).addFragment(this, new LeaderBoardFragment());
        }
        if (v == profileBinding.frProfileIvEdit) {
            ((HomeActivity) getActivity()).addFragment(this, new ProfileEditFragment());
        }
        if (v == profileBinding.frProfileIvLogout) {
            ((HomeActivity) getActivity()).createLogoutDialog(false);
        }
        if (v == profileBinding.frProfileIvinfo) {
            ((HomeActivity) getActivity()).alertDetail("", getString(R.string.share_description), true);
        }
    }


    private void setBadgeData() {
        final ArrayList<BadgeModel> models = new ArrayList<>();
        models.add(new BadgeModel("Badge Title1", true));
        models.add(new BadgeModel("Badge Title2", true));
        models.add(new BadgeModel("Badge Title3", false));
        models.add(new BadgeModel("Badge Title4", false));
        models.add(new BadgeModel("Badge Title5", false));

        badgeAdapter.addAllModel(models);
    }

    private void setUsersData() {
        usersAdapter.clearItems();
        final ArrayList<Family> models = new ArrayList<>();
        //models.addAll(AlainZooDB.getInstance().familyDao().getAll(LangUtils.getCurrentLanguage()));
        models.addAll(AlainZooDB.getInstance().familyDao().getAll());
        usersAdapter.addAllModel(models);
    }

    @Override
    public void addUser() {
        ((HomeActivity) getActivity()).addFragment(this, new AddFamilyFragment());
    }
}
