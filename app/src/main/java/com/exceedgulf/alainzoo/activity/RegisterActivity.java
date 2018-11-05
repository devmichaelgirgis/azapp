package com.exceedgulf.alainzoo.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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

import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
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
import com.exceedgulf.alainzoo.databinding.ActivityRegisterBinding;
import com.exceedgulf.alainzoo.managers.AvatarsManager;
import com.exceedgulf.alainzoo.managers.GamificationManager;
import com.exceedgulf.alainzoo.managers.LoginZooManager;
import com.exceedgulf.alainzoo.managers.RegistrationManager;
import com.exceedgulf.alainzoo.models.TokenModel;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
import com.exceedgulf.alainzoo.services.AccessTokenService;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.LangUtils;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.utils.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {
    private static final String KEY_BACK = "IS_BACK";
    private static final String KEY_RETURN = "IS_RETURN";
    private CallbackManager facebookCallbackManager;
    private ActivityRegisterBinding registerBinding;
    private String selectedDateOfBirth = "";
    private EmiratesNationalitiesAdapter emiratesAdapter;
    private EmiratesNationalitiesAdapter natinalityAdapter;
    private ArrayList<Emirates> emiratesArrayList;
    private ArrayList<Nationalities> nationalitiesArrayList;
    private AvatarAdapter avatarAdapter;
    private Avatars avatars = null;
    private boolean isBack = false;
    private FacebookCallback<LoginResult> loginResultFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            DisplayDialog.getInstance().showProgressDialog(RegisterActivity.this, "Loading", false);
            final GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            DisplayDialog.getInstance().dismissProgressDialog();
                            Log.v("LoginActivity", response.toString());

                            final String id = object.optString("id");
                            final String name = object.optString("name");
                            final String email = object.optString("email");
                            final String gender = object.optString("gender");
                            final String birthday = object.optString("birthday");
                            registerBinding.signinEdtemail.setText(email);
                            registerBinding.registerEdtName.setText(name);
                            if (!TextUtils.isEmpty(gender)) {
                                registerBinding.registerSpnGender.setSelection(gender.equalsIgnoreCase("male") ? 0 : 1);
                            }
                            if (!TextUtils.isEmpty(birthday)) {
                                final String[] dateArray = birthday.split("/");
                                if (dateArray.length > 0) {
                                    //04/22/1992
                                    selectedDateOfBirth = String.format(Locale.ENGLISH, "%04d-%02d-%02d", Integer.parseInt(dateArray[2]), Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]));
                                    registerBinding.registerTvDOB.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", Integer.parseInt(dateArray[2]), Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1])));
                                }
                            }

                        }

                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            SnackbarUtils.loadSnackBar(getString(R.string.error), RegisterActivity.this, R.color.dirt_brown);
        }

        @Override
        public void onError(FacebookException error) {
            SnackbarUtils.loadSnackBar(getString(R.string.error), RegisterActivity.this, R.color.dirt_brown);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updateStatusBarColor(ContextCompat.getColor(RegisterActivity.this, R.color.snot));
        super.onCreate(savedInstanceState);
        registerBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        facebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(facebookCallbackManager, loginResultFacebookCallback);

        final Intent intent = getIntent();
        if (intent.getBooleanExtra(KEY_BACK, false)) {
            isBack = true;
        }
        final GenderAdapter genderAdapter = new GenderAdapter(this);
        final String[] gender = getResources().getStringArray(R.array.gender);
        genderAdapter.add(gender[0]);
        genderAdapter.add(gender[1]);
        registerBinding.registerSpnGender.setAdapter(genderAdapter);

        avatarAdapter = new AvatarAdapter(RegisterActivity.this);

//        registerBinding.registerRvAvatar.setLayoutManager(new LinearLayoutManager(RegisterActivity.this, LinearLayoutManager.HORIZONTAL, false));
//        registerBinding.registerRvAvatar.setAdapter(avatarAdapter);

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

        emiratesAdapter = new EmiratesNationalitiesAdapter(RegisterActivity.this, emiratesArrayList);
        natinalityAdapter = new EmiratesNationalitiesAdapter(RegisterActivity.this, nationalitiesArrayList);


        registerBinding.registerSpnEmirate.setAdapter(emiratesAdapter);
        registerBinding.registerSpnNationality.setAdapter(natinalityAdapter);

        switchScreen(true);
        registerBinding.activityRegisterBtnCreateAccount.setOnClickListener(this);
        registerBinding.registerTvDOB.setOnClickListener(this);
        registerBinding.registerIvBack.setOnClickListener(this);
        registerBinding.registerSocialIvBack.setOnClickListener(this);
        registerBinding.registerBtnRegister.setOnClickListener(this);
        registerBinding.activityRegisterRlFacebook.setOnClickListener(this);
        registerBinding.registerIvAvatar.setOnClickListener(this);
        getAvatars();
        getEmirates();
        getNationality();


    }

    private void getAvatars() {
        final ArrayList<Avatars> avatarsArrayList = (ArrayList<Avatars>) AvatarsManager.getAvatarsManager().getTopThreeEntities();
        if (avatarsArrayList.size() > 0) {
            avatarAdapter.addAllModel(avatarsArrayList);
            if (avatarAdapter.getSelectedAvatars() != null) {
                avatars = avatarAdapter.getSelectedAvatars();
                if (!TextUtils.isEmpty(avatarAdapter.getSelectedAvatars().getImage())) {
                    Picasso.with(RegisterActivity.this).load(avatarAdapter.getSelectedAvatars().getImage()).into(registerBinding.registerIvAvatar);
                }
            }
        } else {
            AvatarsManager.getAvatarsManager().getAvatarsAllEntry(new ApiDetailCallback() {
                @Override
                public void onSuccess(Object result) {
                    avatarAdapter.addAllModel((ArrayList<Avatars>) result);
                    if (avatarAdapter.getSelectedAvatars() != null) {
                        avatars = avatarAdapter.getSelectedAvatars();
                        if (!TextUtils.isEmpty(avatarAdapter.getSelectedAvatars().getImage())) {
                            Picasso.with(RegisterActivity.this).load(avatarAdapter.getSelectedAvatars().getImage()).into(registerBinding.registerIvAvatar);
                        }
                    }
                }

                @Override
                public void onFaild(String message) {
                    SnackbarUtils.loadSnackBar(message, RegisterActivity.this, R.color.snot);
                }
            });
        }
    }

    private void getEmirates() {
        RegistrationManager.getRegistrationManager().getEmirates(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                emiratesArrayList.addAll(resultList);
                emiratesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFaild(String message) {
                SnackbarUtils.loadSnackBar(message, RegisterActivity.this, R.color.snot);
            }
        });
    }

    private void getNationality() {
        RegistrationManager.getRegistrationManager().getNationalities(new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isLoadYes) {
                nationalitiesArrayList.addAll(resultList);
                natinalityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFaild(String message) {
                SnackbarUtils.loadSnackBar(message, RegisterActivity.this, R.color.snot);
            }
        });
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == registerBinding.activityRegisterBtnCreateAccount) {
            if (validatePart1()) {
                switchScreen(false);
            }
        } else if (v == registerBinding.registerBtnRegister) {
            if (validatePart2()) {
                final String strEmail = registerBinding.signinEdtemail.getText().toString().trim();
                final String strMobile = registerBinding.registerEdtmobile.getText().toString().trim();
                final String strPassword = registerBinding.registerEdtpassword.getText().toString().trim();
                final String strName = registerBinding.registerEdtName.getText().toString().trim();
                final Emirates selectedEmirates = AlainZooDB.getInstance().emiratesDao().getEmirates(Locale.ENGLISH.toString(), (emiratesArrayList.get(registerBinding.registerSpnEmirate.getSelectedItemPosition()).getId()));
                final Nationalities selectedNationalities = AlainZooDB.getInstance().nationalitiesDao().getNationalities(Locale.ENGLISH.toString(), (nationalitiesArrayList.get(registerBinding.registerSpnNationality.getSelectedItemPosition()).getId()));

                final JSONObject jsonObject = new JSONObject();
                try {
                    //jsonObject.put("field_mobile_number", addToJson(strMobile));
                    //jsonObject.put("timezone", addToJson(TimeZone.getDefault().getID()));
                    jsonObject.put("name", addToJson(strMobile));
                    jsonObject.put("pass", addToJson(strPassword));
                    jsonObject.put("mail", addToJson(strEmail));
                    jsonObject.put("field_name", addToJson(strName));
                    if (!TextUtils.isEmpty(selectedDateOfBirth)) {
                        jsonObject.put("field_date_of_bi", addToJson(selectedDateOfBirth));
                    }
                    jsonObject.put("field_gender", addToJson(registerBinding.registerSpnGender.getSelectedItemPosition() == 0 ? "male" : "female"));
                    jsonObject.put("field_avatar", addArrayToJson(avatars == null ? 13 : avatars.getId(), "media"));
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
                register(body);
            }
        } else if (v == registerBinding.registerTvDOB) {
            final Calendar calendarMin = Calendar.getInstance();
            calendarMin.set(Calendar.YEAR, 1900);
            calendarMin.set(Calendar.MONTH, 0);
            calendarMin.set(Calendar.DAY_OF_MONTH, 1);

            final Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setLocale(Locale.ENGLISH);
            datePickerDialog.setAccentColor(ContextCompat.getColor(RegisterActivity.this, R.color.yellow_orange));
            datePickerDialog.setMinDate(calendarMin);
            datePickerDialog.setMaxDate(calendar);
            datePickerDialog.show(getFragmentManager(), "Date");

        } else if (v == registerBinding.registerIvBack) {
            super.onBackPressed();
            overridePendingTransition(R.anim.push_in_left, R.anim.push_out_right);
        } else if (v == registerBinding.registerSocialIvBack) {
            switchScreen(true);
        } else if (v == registerBinding.activityRegisterRlFacebook) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        } else if (v == registerBinding.registerIvAvatar) {
            avtarDialogShow();
        }
    }

    private void switchScreen(boolean isSocial) {
        if (isSocial) {
            registerBinding.activityRegisterRlRegisterHeaderSocial.setVisibility(View.VISIBLE);
            registerBinding.activityRegisterLlSocial.setVisibility(View.VISIBLE);
            registerBinding.activityRegisterRlRegisterHeader.setVisibility(View.GONE);
            registerBinding.activityRegisterLlRegister.setVisibility(View.GONE);
            updateStatusBarColor(ContextCompat.getColor(RegisterActivity.this, R.color.snot));
        } else {
            registerBinding.activityRegisterRlRegisterHeaderSocial.setVisibility(View.GONE);
            registerBinding.activityRegisterLlSocial.setVisibility(View.GONE);
            registerBinding.activityRegisterRlRegisterHeader.setVisibility(View.VISIBLE);
            registerBinding.activityRegisterLlRegister.setVisibility(View.VISIBLE);
            updateStatusBarColor(ContextCompat.getColor(RegisterActivity.this, R.color.yellow_orange));
        }
    }

    @Override
    public void onBackPressed() {
        if (registerBinding.activityRegisterRlRegisterHeaderSocial.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
            overridePendingTransition(R.anim.push_in_left, R.anim.push_out_right);
        } else {
            switchScreen(true);
        }
    }

    private boolean validatePart1() {

        final String strEmail = registerBinding.signinEdtemail.getText().toString().trim();
        final String strMobile = registerBinding.registerEdtmobile.getText().toString().trim();
        final String strPassword = registerBinding.registerEdtpassword.getText().toString().trim();
        final String strConfirmPassword = registerBinding.registerEdtConfirmpassword.getText().toString().trim();

        if (TextUtils.isEmpty(strEmail)) {
            registerBinding.signinEdtemail.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_email), RegisterActivity.this, R.color.snot);
            return false;
        }
        if (!Utils.isValidEmail(strEmail)) {
            registerBinding.signinEdtemail.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_email_valid), RegisterActivity.this, R.color.snot);
            return false;
        }
        if (TextUtils.isEmpty(strMobile)) {
            registerBinding.registerEdtmobile.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_phone_number), RegisterActivity.this, R.color.snot);
            return false;
        }
        // if ((!(strMobile.startsWith("0") || strMobile.startsWith("00") || strMobile.startsWith("+"))) || strMobile.length() < 6 || strMobile.length() > 12) {
        if (!Utils.isValidInternationalMobile(strMobile)) {
            registerBinding.registerEdtmobile.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_valid_phone_number), RegisterActivity.this, R.color.snot);
            return false;
        }
        if (TextUtils.isEmpty(strPassword)) {
            registerBinding.registerEdtpassword.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_password), RegisterActivity.this, R.color.snot);
            return false;
        }
        if (TextUtils.isEmpty(strConfirmPassword)) {
            registerBinding.registerEdtConfirmpassword.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_confirm_password), RegisterActivity.this, R.color.snot);
            return false;
        }
        if (!strPassword.equals(strConfirmPassword)) {
            SnackbarUtils.loadSnackBar(getString(R.string.validation_confirm_password_not_match), RegisterActivity.this, R.color.snot);
            return false;
        }

        return true;
    }

    private boolean validatePart2() {

        final String strName = registerBinding.registerEdtName.getText().toString().trim();
        if (avatars == null) {
            SnackbarUtils.loadSnackBar(getString(R.string.please_select_an_avatar), RegisterActivity.this, R.color.yellow_orange);
            return false;
        } else if (TextUtils.isEmpty(strName)) {
            registerBinding.registerEdtName.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.error_name), RegisterActivity.this, R.color.yellow_orange);
            return false;
        }
//        else if (TextUtils.isEmpty(selectedDateOfBirth)) {
//            registerBinding.registerTvDOB.requestFocus();
//            SnackbarUtils.loadSnackBar(getString(R.string.error_date_of_birth), RegisterActivity.this, R.color.yellow_orange);
//            return false;
//        }

        return true;
    }

    private void register(final RequestBody requestBody) {

        DisplayDialog.getInstance().showProgressDialog(this, "Loading", false);
        RegistrationManager.getRegistrationManager().postRegister(requestBody, new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                final String strEmail = registerBinding.signinEdtemail.getText().toString();
                final String strPassword = registerBinding.registerEdtpassword.getText().toString();
                SharedPrefceHelper.getInstance().save(PrefCons.USERNAME, strEmail);
                SharedPrefceHelper.getInstance().save(PrefCons.PASSWORD, strPassword);
                final Map<String, Object> map = new HashMap<>();
                map.put(Constants.LOGIN_USERNAME, strEmail);
                map.put(Constants.LOGIN_PASSWORD, strPassword);
                final RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(map)).toString());
                login(body, (String) result);

            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                Log.e("Failure", message);
                alertMessage("", message, true);
            }
        });

    }

    public void alertMessage(final String title, final String message, final boolean isValidation) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_form_response, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogView.findViewById(R.id.dialog_form_response_tvTitle).setVisibility(View.GONE);
        final TextView tvTitle = dialogView.findViewById(R.id.dialog_form_response_tvTitle);
        tvTitle.setText(title);
        tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        final TextView tvMessage = dialogView.findViewById(R.id.dialog_form_response_tvMessage);
        final TextView tvOkay = dialogView.findViewById(R.id.dialog_form_response_tvOkay);

        tvMessage.setText(message);
        tvOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (!isValidation) {
                    if (isBack) {
                        finish();
                        overridePendingTransition(R.anim.push_in_left, R.anim.push_out_right);
                    } else {
                        final Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    }

                }
            }
        });
        alertDialog.show();
        final int width = (int) getResources().getDimension(R.dimen._250sdp);
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void login(final RequestBody requestBody, final String resultMessage) {
        //DisplayDialog.getInstance().showProgressDialog(this, "Loading", false);
        LoginZooManager.getLoginZooManager().userlogin(requestBody, new ApiDetailCallback<String>() {
            @Override
            public void onSuccess(String result) {
                getAccessTokenForUser(resultMessage);
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                alertMessage("", message, true);
                SharedPrefceHelper.getInstance().save(PrefCons.REFRESH_TOKEN, "");
                stopService(new Intent(RegisterActivity.this, AccessTokenService.class));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    startForegroundService(new Intent(RegisterActivity.this, AccessTokenService.class));
//                } else {
                    startService(new Intent(RegisterActivity.this, AccessTokenService.class));
//                }
            }
        });
    }

    public void getAccessTokenForUser(final String resultMessage) {
        final DateFormat df = new SimpleDateFormat(Constants.TIME_STAMP_FORMAT, Locale.ENGLISH);
        ApiControllers.getApiControllers().postUserAccessToken(registerBinding.signinEdtemail.getText().toString().trim(), registerBinding.registerEdtpassword.getText().toString().trim(), new Callback<TokenModel>() {
            @Override
            public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (response.code() == 200) {
                    callGamification();
                    final TokenModel tokenModel = response.body();
                    SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN, tokenModel.getAccessToken());
                    SharedPrefceHelper.getInstance().save(PrefCons.REFRESH_TOKEN, tokenModel.getRefreshToken());
                    SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_EXPIRES, tokenModel.getExpiresIn());
                    SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_CREATED, df.format(new Date()));
                    Log.e("UserAccess Token ", tokenModel.getAccessToken());
                } else {
                    SharedPrefceHelper.getInstance().save(PrefCons.REFRESH_TOKEN, "");
                }
                alertMessage("", (String) resultMessage, false);
                stopService(new Intent(RegisterActivity.this, AccessTokenService.class));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(RegisterActivity.this, AccessTokenService.class));
                } else {
                    startService(new Intent(RegisterActivity.this, AccessTokenService.class));
                }
            }

            @Override
            public void onFailure(Call<TokenModel> call, Throwable t) {
                DisplayDialog.getInstance().dismissProgressDialog();
                Log.e("Failure", t.getMessage());
                SharedPrefceHelper.getInstance().save(PrefCons.REFRESH_TOKEN, "");
                stopService(new Intent(RegisterActivity.this, AccessTokenService.class));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(RegisterActivity.this, AccessTokenService.class));
                } else {
                    startService(new Intent(RegisterActivity.this, AccessTokenService.class));
                }
                alertMessage("", (String) resultMessage, false);
                // SnackbarUtils.loadSnackBar(t.getMessage(), SignInActivity.this, R.color.dirt_brown);
            }
        });
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        selectedDateOfBirth = String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
        registerBinding.registerTvDOB.setText(String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getResources().updateConfiguration(LangUtils.getLocal(this), getApplicationContext().getResources().getDisplayMetrics());
        super.onConfigurationChanged(LangUtils.getLocal(this));
    }

    private void avtarDialogShow() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_avatar, null);
        builder.setView(dialogView);
        final RecyclerView rvAvatars = dialogView.findViewById(R.id.dialog_avatar_rvMain);
        final int spacingInPixels = getResources().getDimensionPixelSize(R.dimen._5sdp);
        rvAvatars.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        rvAvatars.setLayoutManager(new GridLayoutManager(RegisterActivity.this, 3));
        rvAvatars.setAdapter(avatarAdapter);

        final TextView tvDone = dialogView.findViewById(R.id.dialog_avatar_tvDone);
        final TextView tvCancel = dialogView.findViewById(R.id.dialog_avatar_tvCancel);

        final AlertDialog alertDialog = builder.create();


        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (avatarAdapter.getSelectedAvatars() == null) {
                    SnackbarUtils.loadSnackBar(dialogView, getString(R.string.please_select_an_avatar), RegisterActivity.this, R.color.yellow_orange);
                    return;
                }
                alertDialog.cancel();
                avatars = avatarAdapter.getSelectedAvatars();
                if (avatars != null && !TextUtils.isEmpty(avatars.getImage())) {
                    Picasso.with(RegisterActivity.this).load(avatars.getImage()).into(registerBinding.registerIvAvatar);
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

    public void callGamification() {
        final RequestBody body = GamificationManager.getGamificationManager().createRequestBody("registration", "0");
        GamificationManager.getGamificationManager().postCreateGamification(body, new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                Log.e("gamification", "success" + result.toString());
            }

            @Override
            public void onFaild(String message) {
                Log.e("gamification", "failure" + message);
            }
        });
    }
}
