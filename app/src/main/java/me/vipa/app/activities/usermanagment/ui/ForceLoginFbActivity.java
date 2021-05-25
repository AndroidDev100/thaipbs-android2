package me.vipa.app.activities.usermanagment.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import me.vipa.app.activities.usermanagment.viewmodel.RegistrationLoginViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.R;
import me.vipa.app.activities.homeactivity.ui.HomeActivity;
import me.vipa.app.beanModel.configBean.ResponseConfig;
import me.vipa.app.beanModel.responseModels.LoginResponse.Data;
import me.vipa.app.databinding.ActivityForceLoginBinding;
import me.vipa.app.fragments.dialog.AlertDialogFragment;
import me.vipa.app.fragments.dialog.AlertDialogSingleButtonFragment;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.helpers.CheckInternetConnection;

import me.vipa.app.utils.helpers.StringUtils;
import me.vipa.app.utils.helpers.ToastHandler;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import java.util.Objects;

import me.vipa.app.activities.usermanagment.viewmodel.RegistrationLoginViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;

public class ForceLoginFbActivity extends BaseBindingActivity<ActivityForceLoginBinding> implements AlertDialogFragment.AlertDialogListener {
    private RegistrationLoginViewModel viewModel;
    private KsPreferenceKeys preference;
    private String name, fbId, fbProfilePic, accessToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(ForceLoginFbActivity.this).get(RegistrationLoginViewModel.class);
        callBinding();
    }

    @Override
    public ActivityForceLoginBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivityForceLoginBinding.inflate(inflater);
    }


    public void callBinding() {
        FacebookSdk.sdkInitialize(ForceLoginFbActivity.this);
        if (CheckInternetConnection.isOnline(ForceLoginFbActivity.this)) {
            final Bundle extra = getIntent().getExtras();
            if (extra != null) {
                getBundleValue();
            }
        } else {
            Logger.e("ForceLoginFBACtivity", "No Internet Connection");

        }
    }


    public void getBundleValue() {
        preference = KsPreferenceKeys.getInstance();
        getBinding().toolbar.llSearchIcon.setVisibility(View.GONE);
        getBinding().toolbar.homeIcon.setVisibility(View.GONE);
        getBinding().toolbar.screenText.setText(ForceLoginFbActivity.this.getResources().getString(R.string.email_id));
        getBinding().toolbar.backLayout.setVisibility(View.VISIBLE);
        getBinding().toolbar.mediaRouteButton.setVisibility(View.GONE);
        if (getIntent().hasExtra(AppConstants.EXTRA_REGISTER_USER)) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                extras = extras.getBundle(AppConstants.EXTRA_REGISTER_USER);
                name = Objects.requireNonNull(extras).getString("fbName");
                fbId = extras.getString("fbId");
                fbProfilePic = extras.getString("fbProfilePic");
                accessToken = extras.getString("fbToken");
            }
        }


        getBinding().toolbar.backLayout.setOnClickListener(view -> onBackPressed());

        getBinding().tvSendEmail.setOnClickListener(view -> hitApiFBLogin());


    }

    public void hitApiFBLogin() {
        if (CheckInternetConnection.isOnline(ForceLoginFbActivity.this)) {
            if (validateEmptyEmail() && validateEmail()) {

                getBinding().progressBar.setVisibility(View.VISIBLE);
                viewModel.hitApiForceFbLogin(ForceLoginFbActivity.this, getBinding().etPasswordRecoveryEmail.getText().toString().trim(), accessToken, name, fbId, fbProfilePic, true).observe(ForceLoginFbActivity.this, loginResponseModelResponse -> {

                    getBinding().progressBar.setVisibility(View.GONE);
                    if (Objects.requireNonNull(loginResponseModelResponse).getResponseCode() == 2000) {
                        Gson gson = new Gson();
                        String stringJson = gson.toJson(loginResponseModelResponse.getData());
                        saveUserDetails(stringJson, loginResponseModelResponse.getData().getId(), false);
                    }else {
                        dismissLoading(getBinding().progressBar);
                        showDialog(ForceLoginFbActivity.this.getResources().getString(R.string.error), loginResponseModelResponse.getDebugMessage().toString());
                    }
                  /*  else if (loginResponseModelResponse.getResponseCode() == 401 || loginResponseModelResponse.getResponseCode() == 404) {
                        dismissLoading(getBinding().progressBar);
                        showDialog(ForceLoginFbActivity.this.getResources().getString(R.string.error), loginResponseModelResponse.getDebugMessage().toString());
                    }
                    else if (loginResponseModelResponse.getResponseCode() == 409) {
                        dismissLoading(getBinding().progressBar);
                        showDialog(ForceLoginFbActivity.this.getResources().getString(R.string.error), loginResponseModelResponse.getDebugMessage().toString());
                    }
                    else if (loginResponseModelResponse.getResponseCode() == 500) {
                        dismissLoading(getBinding().progressBar);
                        showDialog(ForceLoginFbActivity.this.getResources().getString(R.string.error), ForceLoginFbActivity.this.getResources().getString(R.string.server_error));
                    }
                    else {
                        dismissLoading(getBinding().progressBar);
                    }*/

                });
            }
        } else
            new ToastHandler(ForceLoginFbActivity.this).show(ForceLoginFbActivity.this.getResources().getString(R.string.no_internet_connection));
    }

    public void saveUserDetails(String response, int userID, boolean isManual) {
        Data fbLoginData = new Gson().fromJson(response, Data.class);
        Gson gson = new Gson();
        String stringJson = gson.toJson(fbLoginData);
        if (isManual)
            preference.setAppPrefLoginType(AppConstants.UserLoginType.Manual.toString());
        else
            preference.setAppPrefLoginType(AppConstants.UserLoginType.FbLogin.toString());
        preference.setAppPrefProfile(stringJson);
        preference.setAppPrefLoginStatus(AppConstants.UserStatus.Login.toString());
        preference.setAppPrefUserId(String.valueOf(fbLoginData.getId()));
        preference.setAppPrefUserName(String.valueOf(fbLoginData.getName()));
        preference.setAppPrefUserEmail(String.valueOf(fbLoginData.getEmail()));
        AppCommonMethod.userId = String.valueOf(fbLoginData.getId());
        new ActivityLauncher(ForceLoginFbActivity.this).homeScreen(ForceLoginFbActivity.this, HomeActivity.class);
    }



    private void showDialog(String title, String message) {
        FragmentManager fm = getSupportFragmentManager();
        AlertDialogSingleButtonFragment alertDialog = AlertDialogSingleButtonFragment.newInstance(title, message, getResources().getString(R.string.ok));
        alertDialog.setCancelable(false);
        alertDialog.setAlertDialogCallBack(this);
        alertDialog.show(fm, "fragment_alert");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LoginManager.getInstance().logOut();
        ResponseConfig dataConfig = AppCommonMethod.callpreference(ForceLoginFbActivity.this);
        preference.clear();
        preference.setAppPrefLoginType("");
        Gson gson = new Gson();
        String json = gson.toJson(dataConfig);
        preference.setAppPrefConfigResponse(json);

    }

    private boolean validateEmptyEmail() {
        boolean check = false;
        if (StringUtils.isNullOrEmptyOrZero(getBinding().etPasswordRecoveryEmail.getText().toString().trim())) {
            getBinding().errorEmail.setText(getResources().getString(R.string.empty_string));
            getBinding().errorEmail.setVisibility(View.VISIBLE);
        } else {
            check = true;
            getBinding().errorEmail.setVisibility(View.INVISIBLE);
        }
        return check;
    }

    public boolean validateEmail() {
        boolean check = false;
        if (getBinding().etPasswordRecoveryEmail.getText().toString().trim().matches(AppConstants.EMAIL_REGEX)) {
            check = true;
            getBinding().errorEmail.setVisibility(View.INVISIBLE);
        } else {
            getBinding().errorEmail.setText(getResources().getString(R.string.valid_email));
            getBinding().errorEmail.setVisibility(View.VISIBLE);
        }
        return check;
    }


    @Override
    public void onFinishDialog() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
