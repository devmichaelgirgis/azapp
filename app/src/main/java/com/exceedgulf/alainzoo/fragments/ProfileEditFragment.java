package com.exceedgulf.alainzoo.fragments;


import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.AvatarAdapter;
import com.exceedgulf.alainzoo.adapter.EmiratesNationalitiesAdapter;
import com.exceedgulf.alainzoo.adapter.GenderAdapter;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.custom.SpacesItemDecoration;
import com.exceedgulf.alainzoo.database.AlainZooDB;
import com.exceedgulf.alainzoo.database.models.Avatars;
import com.exceedgulf.alainzoo.database.models.Emirates;
import com.exceedgulf.alainzoo.database.models.Nationalities;
import com.exceedgulf.alainzoo.databinding.FragmentProfileEditBinding;
import com.exceedgulf.alainzoo.managers.AvatarsManager;
import com.exceedgulf.alainzoo.managers.ProfileManager;
import com.exceedgulf.alainzoo.managers.RegistrationManager;
import com.exceedgulf.alainzoo.models.TokenModel;
import com.exceedgulf.alainzoo.models.UserModel;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.NetUtil;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by P.G 01/11/2018
 */
public class ProfileEditFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener {

    private FragmentProfileEditBinding editBinding;
    private EmiratesNationalitiesAdapter emiratesAdapter;
    private EmiratesNationalitiesAdapter natinalityAdapter;
    private ArrayList<Emirates> emiratesArrayList;
    private ArrayList<Nationalities> nationalitiesArrayList;
    private String selectedDateOfBirth = "";
    private AvatarAdapter avatarAdapter;
    private Avatars avatars = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        editBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_edit, container, false);
        return editBinding.getRoot();
    }

    @Override
    public void initView(View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.edit_profile), getResources().getColor(R.color.light_eggplant), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        final GenderAdapter genderAdapter = new GenderAdapter(getActivity());
        final String[] gender = getResources().getStringArray(R.array.gender);
        genderAdapter.add(gender[0]);
        genderAdapter.add(gender[1]);
        editBinding.frProeditSpnGender.setAdapter(genderAdapter);

        avatarAdapter = new AvatarAdapter(getActivity());

        emiratesArrayList = new ArrayList<>();
        final Emirates emirates = new Emirates();
        emirates.setId(-1);
        emirates.setTitle(getString(R.string.none));

        emiratesArrayList.add(emirates);
        nationalitiesArrayList = new ArrayList<>();
        final Nationalities nationalities = new Nationalities();
        nationalities.setId(-1);
        nationalities.setTitle(getString(R.string.none));
        nationalitiesArrayList.add(nationalities);

        emiratesAdapter = new EmiratesNationalitiesAdapter(getActivity(), emiratesArrayList);
        natinalityAdapter = new EmiratesNationalitiesAdapter(getActivity(), nationalitiesArrayList);


        editBinding.frProeditSpnEmirate.setAdapter(emiratesAdapter);
        editBinding.frProeditSpnNationality.setAdapter(natinalityAdapter);
        getAvatars();
        getEmirates();
        getNationality();

        setUserInformation();

        editBinding.frProeditTvSaveChanges.setOnClickListener(this);
        editBinding.frProeditTvDOB.setOnClickListener(this);
        editBinding.frProeditIvuserprofilepic.setOnClickListener(this);
        editBinding.frProeditTvChangePassword.setOnClickListener(this);

    }

    private void setUserInformation() {
        if (SharedPrefceHelper.getInstance().get(PrefCons.IS_LOGGEDIN, false) || AppAlainzoo.getAppAlainzoo().isTempLoggedIn()) {
            avatars = (Avatars) AvatarsManager.getAvatarsManager().getEntityDetailsFromDB(SharedPrefceHelper.getInstance().get(PrefCons.USER_AVATAR_ID, 0));
            if (!TextUtils.isEmpty(SharedPrefceHelper.getInstance().get(PrefCons.USER_FIELD_NAME, ""))) {
                editBinding.frProeditEdtName.setText(SharedPrefceHelper.getInstance().get(PrefCons.USER_FIELD_NAME, ""));
            }
            if (avatars != null && !TextUtils.isEmpty(avatars.getImage())) {
                Picasso.with(getActivity()).load(avatars.getImage()).into(editBinding.frProeditIvuserprofilepic);
                avatarAdapter.setSelectedAvatars(avatars);
            }
            editBinding.frProeditEdtemail.setText(SharedPrefceHelper.getInstance().get(PrefCons.USER_EMAIL, ""));
            editBinding.frProeditEdtphone.setText(SharedPrefceHelper.getInstance().get(PrefCons.USER_NAME, ""));
            if (!SharedPrefceHelper.getInstance().get(PrefCons.USER_DOB, "").equalsIgnoreCase("[]")) {
                selectedDateOfBirth = SharedPrefceHelper.getInstance().get(PrefCons.USER_DOB, "");
            }
            editBinding.frProeditTvDOB.setText(selectedDateOfBirth);
            editBinding.frProeditSpnGender.setSelection(SharedPrefceHelper.getInstance().get(PrefCons.USER_GENDER, "male").equalsIgnoreCase("male") ? 0 : 1);
            try {
                final List<Nationalities> selectedNationalities = AlainZooDB.getInstance().nationalitiesDao().getNationalitiesEntity(LangUtils.getCurrentLanguage());
                if (selectedNationalities != null && selectedNationalities.size() > 0) {
                    for (int i = 0; i < selectedNationalities.size(); i++) {
                        if (selectedNationalities.get(i).getId().equals(SharedPrefceHelper.getInstance().get(PrefCons.USER_NATIONALITY_ID))) {
                            editBinding.frProeditSpnNationality.setSelection(i + 1);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
            }
            try {
                final List<Emirates> selectedEmirates = AlainZooDB.getInstance().emiratesDao().getEmiratesEntity(LangUtils.getCurrentLanguage());

                if (selectedEmirates != null && selectedEmirates.size() > 0) {
                    for (int i = 0; i < selectedEmirates.size(); i++) {
                        if (selectedEmirates.get(i).getId().equals(SharedPrefceHelper.getInstance().get(PrefCons.USER_EMIRATE_ID))) {
                            editBinding.frProeditSpnEmirate.setSelection(i + 1);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
            }

            // editBinding.frProeditSpnEmirate.setSelection(emiratesArrayList.get(editBinding.frProeditSpnEmirate.getSelectedItemPosition()).getId());
        }
    }

    private void getEmirates() {
        RegistrationManager.getRegistrationManager().getEmirates(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                if (isAdded()) {
                    emiratesArrayList.addAll(resultList);
                    emiratesAdapter.notifyDataSetChanged();
                    setUserInformation();
                }
            }

            @Override
            public void onFaild(String message) {
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.light_eggplant);
            }
        });
    }

    private void getNationality() {
        RegistrationManager.getRegistrationManager().getNationalities(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                if (isAdded()) {
                    nationalitiesArrayList.addAll(resultList);
                    natinalityAdapter.notifyDataSetChanged();
                    setUserInformation();
                }
            }

            @Override
            public void onFaild(String message) {
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.light_eggplant);
            }
        });
    }

    private void getAvatars() {
        final ArrayList<Avatars> avatarsArrayList = (ArrayList<Avatars>) AvatarsManager.getAvatarsManager().getTopThreeEntities();
        if (avatarsArrayList.size() > 0) {
            avatarAdapter.addAllModel(avatarsArrayList);

        } else {
            AvatarsManager.getAvatarsManager().getAvatarsAllEntry(new ApiDetailCallback() {
                @Override
                public void onSuccess(Object result) {
                    if (isAdded()) {
                        avatarAdapter.addAllModel((ArrayList<Avatars>) result);
                    }
                }

                @Override
                public void onFaild(String message) {
                    SnackbarUtils.loadSnackBar(message, getActivity(), R.color.snot);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == editBinding.frProeditTvSaveChanges) {
            if (TextUtils.isEmpty(editBinding.frProeditEdtName.getText().toString())) {
                editBinding.frProeditEdtName.requestFocus();
                SnackbarUtils.loadSnackBar(getString(R.string.error_name), getActivity(), R.color.light_eggplant);
            } else {
                final String strName = editBinding.frProeditEdtName.getText().toString().trim();
                final Emirates selectedEmirates = AlainZooDB.getInstance().emiratesDao().getEmirates(Locale.ENGLISH.toString(), (emiratesArrayList.get(editBinding.frProeditSpnEmirate.getSelectedItemPosition()).getId()));
                final Nationalities selectedNationalities = AlainZooDB.getInstance().nationalitiesDao().getNationalities(Locale.ENGLISH.toString(), (nationalitiesArrayList.get(editBinding.frProeditSpnNationality.getSelectedItemPosition()).getId()));
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("field_name", addToJson(strName));
                    if (!TextUtils.isEmpty(selectedDateOfBirth)) {
                        jsonObject.put("field_date_of_bi", addToJson(selectedDateOfBirth));
                    }
                    jsonObject.put("field_gender", addToJson(editBinding.frProeditSpnGender.getSelectedItemPosition() == 0 ? "male" : "female"));
                    jsonObject.put("field_avatar", addArrayToJson(avatars == null ? 306 : avatars.getId(), "media"));
                    if (selectedNationalities != null) {
                        jsonObject.put("field_nationality", addArrayToJson(selectedNationalities.getId(), "taxonomy_term"));

                    }
                    if (selectedEmirates != null) {
                        jsonObject.put("field_emirate", addArrayToJson(selectedEmirates.getId(), "taxonomy_term"));

                    }
                    jsonObject.put("timezone", addToJson("Asia/Dubai"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                updateProfile(body);
            }
        } else if (v == editBinding.frProeditTvDOB) {
            final Calendar calendarMin = Calendar.getInstance();
            calendarMin.set(Calendar.YEAR, 1900);
            calendarMin.set(Calendar.MONTH, 0);
            calendarMin.set(Calendar.DAY_OF_MONTH, 1);

            final Calendar calendar = Calendar.getInstance();
            final Calendar calendarTmp = Calendar.getInstance(Locale.ENGLISH);
            calendarTmp.setTimeInMillis(getMiliSecong(selectedDateOfBirth));
            final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendarTmp.get(Calendar.YEAR), calendarTmp.get(Calendar.MONTH), calendarTmp.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setLocale(Locale.ENGLISH);
            datePickerDialog.setAccentColor(ContextCompat.getColor(getActivity(), R.color.light_eggplant));
            datePickerDialog.setMinDate(calendarMin);
            datePickerDialog.setMaxDate(calendar);
            datePickerDialog.show(getActivity().getFragmentManager(), "Date");
        } else if (v == editBinding.frProeditIvuserprofilepic) {
            avtarDialogShow();
        } else if (v == editBinding.frProeditTvChangePassword) {
            ((HomeActivity) getActivity()).addFragment(this, new ChangePasswordFragment());
        }
    }

    private void updateProfile(final RequestBody requestBody) {
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading", false);
        ProfileManager.getProfileManager().updateProfile(requestBody, new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (isAdded()) {
                    if (result != null) {
                        refreshToken();
                        final UserModel userModel = (UserModel) result;
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_DOB, userModel.getField_date_of_bi());
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_EMIRATE, userModel.getField_emirate());
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_GENDER, userModel.getField_gender());
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_MOBILE, userModel.getField_mobile());
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_FIELD_NAME, userModel.getField_name());
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_NATIONALITY, userModel.getField_nationality());
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_LANG_CODE, userModel.getLangcode());
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_EMAIL, userModel.getMail());
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_NAME, userModel.getName());
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_AVATAR_ID, userModel.getTarget_id_avatar());
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_UID, userModel.getUid());
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_UUID, userModel.getUuid());
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_NATIONALITY_ID, userModel.getNationalityId());
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_EMIRATE_ID, userModel.getCityId());
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_POINTS, userModel.getField_points());
                        SharedPrefceHelper.getInstance().save(PrefCons.USER_TOTAL_POINTS, userModel.getField_total_points());
                        SnackbarUtils.loadSnackBar(getString(R.string.profile_update_success), getActivity(), R.color.light_eggplant);
                        getActivity().onBackPressed();
                    }
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.light_eggplant);
            }
        });
    }


    private void refreshToken() {
        final DateFormat df = new SimpleDateFormat(Constants.TIME_STAMP_FORMAT, Locale.ENGLISH);
        final String strRefreshToken = SharedPrefceHelper.getInstance().get(PrefCons.REFRESH_TOKEN, "");
        if (!TextUtils.isEmpty(strRefreshToken.trim())) {
            if (NetUtil.isNetworkAvailable(getActivity())) {
                Log.e("API Call", "Refresh Token");
                ApiControllers.getApiControllers().postRefreshToken(strRefreshToken, new Callback<TokenModel>() {
                    @Override
                    public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                        if (response.code() == 200) {
                            final TokenModel tokenModel = response.body();
                            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN, tokenModel.getAccessToken());
                            SharedPrefceHelper.getInstance().save(PrefCons.REFRESH_TOKEN, tokenModel.getRefreshToken());
                            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_EXPIRES, tokenModel.getExpiresIn());
                            SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_CREATED, df.format(new Date()));
                            Log.e("Refresh Token ", tokenModel.getAccessToken());
                            //interval = tokenModel.getExpiresIn() * 800;
                            //Log.e("API Features ", "Interval Changed To : " + interval);
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenModel> call, Throwable t) {
                        Log.e("API Call-Failure", t.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        selectedDateOfBirth = String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
        editBinding.frProeditTvDOB.setText(String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth));
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.edit_profile), getResources().getColor(R.color.light_eggplant), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        }
    }

    private void avtarDialogShow() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_avatar, null);
        builder.setView(dialogView);
        final RecyclerView rvAvatars = dialogView.findViewById(R.id.dialog_avatar_rvMain);
        final int spacingInPixels = getResources().getDimensionPixelSize(R.dimen._5sdp);
        rvAvatars.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        rvAvatars.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvAvatars.setAdapter(avatarAdapter);

        final TextView tvDone = dialogView.findViewById(R.id.dialog_avatar_tvDone);
        final TextView tvCancel = dialogView.findViewById(R.id.dialog_avatar_tvCancel);

        final AlertDialog alertDialog = builder.create();


        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (avatarAdapter.getSelectedAvatars() == null) {
                    SnackbarUtils.loadSnackBar(dialogView, getString(R.string.please_select_an_avatar), getActivity(), R.color.light_eggplant);
                    return;
                }
                alertDialog.cancel();
                avatars = avatarAdapter.getSelectedAvatars();
                if (avatars != null && !TextUtils.isEmpty(avatars.getImage())) {
                    Picasso.with(getActivity()).load(avatars.getImage()).into(editBinding.frProeditIvuserprofilepic);
                }

            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }

    private long getMiliSecong(final String date) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            if (!TextUtils.isEmpty(date)) {
                final Date mDate = sdf.parse(date);
                return mDate.getTime();
            } else {
                return Calendar.getInstance().getTimeInMillis();
            }
        } catch (Exception e) {
            return Calendar.getInstance().getTimeInMillis();
        }
    }

    private JSONObject addToJson(final String strValue) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("value", strValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONArray addArrayToJson(final int target_id, final String target_type) {
        final JSONArray jsonArray = new JSONArray();
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("target_id", target_id);
            jsonObject.put("target_type", target_type);
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

}
