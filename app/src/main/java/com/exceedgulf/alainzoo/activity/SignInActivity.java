package com.exceedgulf.alainzoo.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.exceedgulf.alainzoo.AppAlainzoo;
import com.exceedgulf.alainzoo.Interfaces.ApiDetailCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.api.ApiControllers;
import com.exceedgulf.alainzoo.constants.Constants;
import com.exceedgulf.alainzoo.databinding.ActivitySigninBinding;
import com.exceedgulf.alainzoo.managers.GamificationManager;
import com.exceedgulf.alainzoo.managers.LoginZooManager;
import com.exceedgulf.alainzoo.managers.ProfileManager;
import com.exceedgulf.alainzoo.models.TokenModel;
import com.exceedgulf.alainzoo.models.UserModel;
import com.exceedgulf.alainzoo.preference.PrefCons;
import com.exceedgulf.alainzoo.preference.SharedPrefceHelper;
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

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Paras Ghasadiya on 29/12/17.
 */

public class SignInActivity extends BaseActivity {
    private static final String KEY_BACK = "IS_BACK";
    private static final String KEY_RETURN = "IS_RETURN";
    private ActivitySigninBinding signinBinding;
    private CallbackManager facebookCallbackManager;
    private boolean isBack = false;
    private FacebookCallback<LoginResult> loginResultFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            DisplayDialog.getInstance().showProgressDialog(SignInActivity.this, "Loading", false);
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
                        }

                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            SnackbarUtils.loadSnackBar(getString(R.string.error), SignInActivity.this, R.color.dirt_brown);
        }

        @Override
        public void onError(FacebookException error) {
            SnackbarUtils.loadSnackBar(getString(R.string.error), SignInActivity.this, R.color.dirt_brown);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updateStatusBarColor(ContextCompat.getColor(SignInActivity.this, R.color.dirt_brown));
        super.onCreate(savedInstanceState);
        signinBinding = DataBindingUtil.setContentView(this, R.layout.activity_signin);

        facebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(facebookCallbackManager, loginResultFacebookCallback);
        final Intent intent = getIntent();
        if (intent.getBooleanExtra(KEY_BACK, false)) {
            isBack = true;
        }
//        if (intent.getBooleanExtra(KEY_RETURN, false)) {
//            isBack = false;
//        }
        signinBinding.signinTvregister.setOnClickListener(this);
        signinBinding.signinBtnlogin.setOnClickListener(this);
        signinBinding.signinIvBack.setOnClickListener(this);
        signinBinding.actSigninRlFacebook.setOnClickListener(this);
        signinBinding.signinBtnforgetpassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (!isOnClick()) {
            return;
        }
        if (view == signinBinding.signinIvBack) {
            onBackPressed();
            return;
        } else if (view == signinBinding.signinTvregister) {
            final Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra(KEY_BACK, isBack);
            startActivity(intent);
            if (isBack) {
                finish();
            }
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        } else if (view == signinBinding.signinBtnlogin) {
            if (validate()) {
                final String strEmail = signinBinding.signinEdtemail.getText().toString();
                final String strPassword = signinBinding.signinEdtpassword.getText().toString();
                final Map<String, Object> map = new HashMap<>();
                map.put(Constants.LOGIN_USERNAME, strEmail);
                map.put(Constants.LOGIN_PASSWORD, strPassword);
                final RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(map)).toString());
                login(body);
            }
        } else if (view == signinBinding.actSigninRlFacebook) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        } else if (view == signinBinding.signinBtnforgetpassword) {
            forgetPassword();
        }
    }

    private void forgetPassword() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_resetpassword, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView tvSubmit = dialogView.findViewById(R.id.dialog_rspass_tvsubmit);
        final TextView tvCancel = dialogView.findViewById(R.id.dialog_rspass_tvcancel);
        final TextView tvUserName = dialogView.findViewById(R.id.dialog_rspass_tvusername);
        final String text = tvUserName.getText().toString().trim() + " ";
        tvUserName.setText(Html.fromHtml(text + "<font color='#d80003'>*</font>"));
        final EditText edtUserName = dialogView.findViewById(R.id.dialog_rspass_edtusername);

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edtUserName.getText().toString().trim())) {
                    signinBinding.signinEdtemail.requestFocus();
                    SnackbarUtils.loadSnackBar(dialogView, getString(R.string.validation_enter_username), SignInActivity.this, R.color.dirt_brown);
                    return;
                }
                if (!Utils.isValidEmail(edtUserName.getText().toString().trim()) && !Utils.isValidMobile(edtUserName.getText().toString().trim())) {
                    signinBinding.signinEdtemail.requestFocus();
                    SnackbarUtils.loadSnackBar(dialogView, getString(R.string.validation_enter_valid_username), SignInActivity.this, R.color.dirt_brown);
                    return;
                }
                if (Utils.isValidMobile(edtUserName.getText().toString().trim())) {
                    if (edtUserName.getText().toString().trim().length() < 7 || edtUserName.getText().toString().trim().length() > 14) {
                        signinBinding.signinEdtemail.requestFocus();
                        SnackbarUtils.loadSnackBar(dialogView, getString(R.string.validation_enter_valid_username), SignInActivity.this, R.color.dirt_brown);
                        return;
                    }
                }
                Utils.getInstance().hideSoftKeyboard(SignInActivity.this);
                DisplayDialog.getInstance().showProgressDialog(SignInActivity.this, "Loading", false);
                LoginZooManager.getLoginZooManager().resetPassword(edtUserName.getText().toString().trim(), new ApiDetailCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        DisplayDialog.getInstance().dismissProgressDialog();
                        alertDialog.dismiss();
                        alertMessage("", result, true);
                    }

                    @Override
                    public void onFaild(String message) {
                        DisplayDialog.getInstance().dismissProgressDialog();
                        alertMessage("", message, true);
                    }
                });

            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
        final int width = (int) getResources().getDimension(R.dimen._260sdp);
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    private void login(final RequestBody requestBody) {
        DisplayDialog.getInstance().showProgressDialog(this, "Loading", false);
        LoginZooManager.getLoginZooManager().userlogin(requestBody, new ApiDetailCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //DisplayDialog.getInstance().dismissProgressDialog();
                getAccessTokenForUser();
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                //SnackbarUtils.loadSnackBar(message, SignInActivity.this, R.color.dirt_brown);
                alertMessage("", message, true);
            }
        });
    }

    public void getAccessTokenForUser() {
        //DisplayDialog.getInstance().showProgressDialog(SignInActivity.this, "", false);
        final DateFormat df = new SimpleDateFormat(Constants.TIME_STAMP_FORMAT, Locale.ENGLISH);
        ApiControllers.getApiControllers().postUserAccessToken(signinBinding.signinEdtemail.getText().toString().trim(), signinBinding.signinEdtpassword.getText().toString().trim(), new Callback<TokenModel>() {
            @Override
            public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                //DisplayDialog.getInstance().dismissProgressDialog();
                if (response.code() == 200) {
                    final TokenModel tokenModel = response.body();
                    SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN, tokenModel.getAccessToken());
                    SharedPrefceHelper.getInstance().save(PrefCons.REFRESH_TOKEN, tokenModel.getRefreshToken());
                    SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_EXPIRES, tokenModel.getExpiresIn());
                    SharedPrefceHelper.getInstance().save(PrefCons.APPLICATION_TOKEN_CREATED, df.format(new Date()));
                    Log.e("UserAccess Token ", tokenModel.getAccessToken());
                    getUserDetail();
                }
                else {
                    DisplayDialog.getInstance().dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<TokenModel> call, Throwable t) {
                DisplayDialog.getInstance().dismissProgressDialog();
                Log.e("Failure", t.getMessage());
                SnackbarUtils.loadSnackBar(t.getMessage(), SignInActivity.this, R.color.dirt_brown);
            }
        });
    }

    private void getUserDetail() {
        //DisplayDialog.getInstance().showProgressDialog(SignInActivity.this, "", false);
        ProfileManager.getProfileManager().fetchUserInformation(new ApiDetailCallback() {
            @Override
            public void onSuccess(Object result) {
                DisplayDialog.getInstance().dismissProgressDialog();
                if (result != null) {
                    final UserModel userModel = (UserModel) result;
                    SharedPrefceHelper.getInstance().save(PrefCons.IS_LOGGEDIN, signinBinding.signinChkkeeplogin.isChecked());
                    AppAlainzoo.getAppAlainzoo().setTempLoggedIn(!signinBinding.signinChkkeeplogin.isChecked());
                    final String strEmail = signinBinding.signinEdtemail.getText().toString();
                    final String strPassword = signinBinding.signinEdtpassword.getText().toString();
                    SharedPrefceHelper.getInstance().save(PrefCons.USERNAME, strEmail);
                    SharedPrefceHelper.getInstance().save(PrefCons.PASSWORD, strPassword);
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

                    callGamification();

                    if (isBack) {
                        finish();
                        overridePendingTransition(R.anim.push_in_left, R.anim.push_out_right);
                    } else {
                        final Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                    }

                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                SnackbarUtils.loadSnackBar(message, SignInActivity.this, R.color.dirt_brown);
            }
        });

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

    private boolean validate() {
        final String strEmail = signinBinding.signinEdtemail.getText().toString();
        final String strPassword = signinBinding.signinEdtpassword.getText().toString();
        if (TextUtils.isEmpty(strEmail)) {
            signinBinding.signinEdtemail.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.validation_enter_username), SignInActivity.this, R.color.dirt_brown);
            return false;
        }
        if (!Utils.isValidEmail(strEmail) && !Utils.isValidMobile(strEmail)) {
            signinBinding.signinEdtemail.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.validation_enter_valid_username), SignInActivity.this, R.color.dirt_brown);
            return false;
        }
        if (Utils.isValidMobile(strEmail)) {
            if (strEmail.length() < 7 || strEmail.length() > 14) {
                signinBinding.signinEdtemail.requestFocus();
                SnackbarUtils.loadSnackBar(getString(R.string.validation_enter_valid_username), SignInActivity.this, R.color.dirt_brown);
                return false;
            }
        }
        if (TextUtils.isEmpty(strPassword)) {
            signinBinding.signinEdtpassword.requestFocus();
            SnackbarUtils.loadSnackBar(getString(R.string.validation_enter_password), SignInActivity.this, R.color.dirt_brown);
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isBack) {
            finish();
            overridePendingTransition(R.anim.push_in_left, R.anim.push_out_right);
        } else {
            final Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_in_left, R.anim.push_out_right);
            finish();
        }
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

}
