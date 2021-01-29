package me.vipa.app.activities.usermanagment.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import me.vipa.app.activities.contentPreference.UI.ContentPreference;
import me.vipa.app.activities.usermanagment.viewmodel.RegistrationLoginViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.R;
import me.vipa.app.beanModel.responseModels.LoginResponse.Data;
import me.vipa.app.beanModel.responseModels.LoginResponse.LoginResponseModel;
import me.vipa.app.databinding.ActivityMainBinding;
import me.vipa.app.databinding.SignupActivityBinding;
import me.vipa.app.fragments.dialog.AlertDialogFragment;
import me.vipa.app.fragments.dialog.AlertDialogSingleButtonFragment;
import me.vipa.app.tarcker.EventConstant;
import me.vipa.app.tarcker.FCMEvents;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.helpers.CheckInternetConnection;
import me.vipa.app.utils.helpers.NetworkConnectivity;

import me.vipa.app.utils.helpers.StringUtils;
import me.vipa.app.utils.helpers.ToastHandler;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;

import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;

import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.vipa.app.activities.usermanagment.viewmodel.RegistrationLoginViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;

import static me.vipa.app.R.font.sukhumvittadmai_normal;

public class SignUpActivity extends BaseBindingActivity<SignupActivityBinding> implements AlertDialogFragment.AlertDialogListener {

    String regex = "(.)*(\\d)(.)*";
    private KsPreferenceKeys preference;
    private RegistrationLoginViewModel viewModel;
    private long mLastClickTime = 0;
    Typeface font;
    private CallbackManager callbackManager;
    private String accessTokenFB;
    private String name = "", email = "", id = "";
    private URL profile_pic;
    boolean isFbLoginClick = false;
    boolean hasFbEmail;
    private Data modelLogin;
    private final List<String> permissionNeeds = Arrays.asList("email", "public_profile");
    private boolean isNotificationEnable = false;

    @Override
    public SignupActivityBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return SignupActivityBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setupUI(getBinding().root);
        callBinding();
    }

    private void callBinding() {
        viewModel = ViewModelProviders.of(SignUpActivity.this).get(RegistrationLoginViewModel.class);
        preference = KsPreferenceKeys.getInstance();
        getBinding().radioPasswordEye.setChecked(false);
        getBinding().confirmPasswordEye.setChecked(false);
        font = ResourcesCompat.getFont(SignUpActivity.this, sukhumvittadmai_normal);
        getBinding().toolbar.titleToolbar.setVisibility(View.VISIBLE);
        getBinding().toolbar.titleToolbar.setText(getResources().getString(R.string.signup));
        getBinding().toolbar.backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        connectObservors();

    }

    @Override
    protected void onResume() {
        super.onResume();
        isFbLoginClick = false;
        getBinding().llFooter.setVisibility(View.VISIBLE);
        dismissLoading(getBinding().progressBar);
        if (preference.getAppPrefLoginStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            onBackPressed();
        }
        //resetUI();
    }



    private void connectionObserver() {
        if (NetworkConnectivity.isOnline(SignUpActivity.this)) {
            connectionValidation(true);
        } else {
            connectionValidation(false);
        }
    }

    private void connectionValidation(Boolean aBoolean) {
        if (aBoolean) {
            getBinding().parentLayout.setVisibility(View.VISIBLE);
            getBinding().noConnectionLayout.setVisibility(View.GONE);
            resetUI();
        } else {
            noConnectionLayout();
        }
    }

    private void noConnectionLayout() {
        getBinding().parentLayout.setVisibility(View.GONE);
        getBinding().noConnectionLayout.setVisibility(View.VISIBLE);
        getBinding().connection.retryTxt.setOnClickListener(view -> connectionObserver());
    }

    String loginCallingFrom="";
    public void resetUI() {
        loginCallingFrom=getIntent().getStringExtra("loginFrom");
        getBinding().errorName.setVisibility(View.INVISIBLE);
        getBinding().errorEmail.setVisibility(View.INVISIBLE);
        getBinding().errorPassword.setVisibility(View.INVISIBLE);
        getBinding().etName.getText().clear();
        getBinding().etPassword.getText().clear();
        getBinding().etEmail.getText().clear();
    }

    public void connectObservors() {

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        //  getBinding().fbButton.setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY);
        callbackManager = CallbackManager.Factory.create();
        getBinding().fbButton.setReadPermissions(permissionNeeds);
        getBinding().errorName.setVisibility(View.INVISIBLE);
        getBinding().errorEmail.setVisibility(View.INVISIBLE);
        getBinding().errorPassword.setVisibility(View.INVISIBLE);

        getBinding().tvCancel.setOnClickListener(v -> finish());

        getBinding().llSignUp.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            if (CheckInternetConnection.isOnline(SignUpActivity.this)) {
                if (validateNameEmpty() && validateEmptyEmail() && validateEmail() && passwordCheck(getBinding().etPassword.getText().toString()) && confirmPasswordCheck(getBinding().etPassword.getText().toString(),getBinding().etCnfPassword.getText().toString())) {
                    getBinding().errorName.setVisibility(View.INVISIBLE);
                    getBinding().errorEmail.setVisibility(View.INVISIBLE);
                    getBinding().errorPassword.setVisibility(View.INVISIBLE);
                    getBinding().errorCnfPassword.setVisibility(View.INVISIBLE);
                    showLoading(getBinding().progressBar, true);

                    preference.setAppPrefAccessToken("");

                    viewModel.hitSignUpAPI(getBinding().etName.getText().toString(), getBinding().etEmail.getText().toString(), getBinding().etPassword.getText().toString(),isNotificationEnable).observe(SignUpActivity.this, signupResponseAccessToken -> {
                        dismissLoading(getBinding().progressBar);

                        try {
                            if (signupResponseAccessToken.getResponseModel().getResponseCode() == 200) {
                                Gson gson = new Gson();
                                preference.setAppPrefAccessToken(signupResponseAccessToken.getAccessToken());
                                String stringJson = gson.toJson(signupResponseAccessToken.getResponseModel().getData());
                                saveUserDetails(stringJson, signupResponseAccessToken.getResponseModel().getData().getId(), true);
                               // onBackPressed();
                                //new ActivityLauncher(SignUpActivity.this).homeScreen(SignUpActivity.this, HomeActivity.class);
                                //finish();

                            } else if (signupResponseAccessToken.getResponseModel().getResponseCode() == 4901) {
                                showDialog(SignUpActivity.this.getResources().getString(R.string.error), signupResponseAccessToken.getDebugMessage().toString());
                            } else if (signupResponseAccessToken.getResponseModel().getResponseCode() == 400) {

                                showDialog(SignUpActivity.this.getResources().getString(R.string.error), signupResponseAccessToken.getDebugMessage().toString());
                            }
                        } catch (NullPointerException e) {

                        }


                    });
                }
            } else {
                dismissLoading(getBinding().progressBar);
                connectionValidation(false);
                //  new ToastHandler(SignUpActivity.this).show(SignUpActivity.this.getResources().getString(R.string.no_internet_connection));

            }
        });

        getBinding().tvAlreadyUser.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            finish();
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        });

        getBinding().etName.setOnClickListener(view -> getBinding().errorName.setVisibility(View.INVISIBLE));


        getBinding().etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getBinding().errorName.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        getBinding().etName.setOnTouchListener((view, motionEvent) -> {
            getBinding().errorName.setVisibility(View.INVISIBLE);
            return false;
        });


        getBinding().etEmail.setOnClickListener(view -> getBinding().errorEmail.setVisibility(View.INVISIBLE));
        getBinding().etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getBinding().errorEmail.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        getBinding().etEmail.setOnEditorActionListener((textView, i, keyEvent) -> {
            getBinding().errorEmail.setVisibility(View.INVISIBLE);
            return false;
        });


        getBinding().etPassword.setLongClickable(false);
        getBinding().etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getBinding().errorPassword.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        getBinding().etCnfPassword.setOnClickListener(view -> getBinding().errorPassword.setVisibility(View.INVISIBLE));

        getBinding().etCnfPassword.setLongClickable(false);
        getBinding().etCnfPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getBinding().errorCnfPassword.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        getBinding().etCnfPassword.setOnClickListener(view -> getBinding().errorCnfPassword.setVisibility(View.INVISIBLE));


        getBinding().radioPasswordEye.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                getBinding().etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                getBinding().etPassword.setTypeface(font);
            } else {
                getBinding().etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                getBinding().etPassword.setSelection(getBinding().etPassword.getText().length());
                getBinding().etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                hideSoftKeyboard(getBinding().radioPasswordEye);
                getBinding().etPassword.setTypeface(font);
            }
        });

        getBinding().confirmPasswordEye.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                getBinding().etCnfPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                getBinding().etCnfPassword.setTypeface(font);
            } else {
                getBinding().etCnfPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                getBinding().etCnfPassword.setSelection(getBinding().etCnfPassword.getText().length());
                getBinding().etCnfPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                hideSoftKeyboard(getBinding().confirmPasswordEye);
                getBinding().etCnfPassword.setTypeface(font);
            }
        });

        getBinding().termsText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isNotificationEnable = isChecked;
            }
        });

        getBinding().rlFacebookLogin.setOnClickListener(view -> {

            if (CheckInternetConnection.isOnline(SignUpActivity.this)) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                clearEditView();
                isFbLoginClick = true;
                getBinding().fbButton.performClick();

            } else
                connectionObserver();
        });

        getBinding().fbButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("onSuccess");

                accessTokenFB = loginResult.getAccessToken().getToken();
                Logger.i("accessToken", accessTokenFB);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        (object, response) -> {

                            Logger.i("LoginActivity",
                                    response.toString());
                            try {
                                id = object.getString("id");
                                try {
                                    profile_pic = new URL(
                                            "https://graph.facebook.com/" + id + "/picture?type=large");


                                    Logger.i("profile_pic",
                                            profile_pic + "");
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                name = object.getString("name");
                                if (object.has("email")) {
                                    email = object.getString("email");
                                    hasFbEmail = true;
                                } else
                                    hasFbEmail = false;

                              /*  try {
                                    final URL imageUrl = new URL(
                                            "http://graph.facebook.com/" + id + "/picture?type=large");
                                    bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                                } catch (Exception e) {
                                    Logger.e("LoginActivity", "" + e.toString());

                                }*/
                                showHideProgress(getBinding().progressBar);
                                //  setFileToUpload();
                                hitApiFBLogin();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields",
                        "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                if (error instanceof FacebookAuthorizationException)
                    LoginManager.getInstance().logOut();
                // Logger.e("LoginActivity", error.getCause().toString());
            }
        });




    }

    private void hitApiFBLogin() {
        if (CheckInternetConnection.isOnline(SignUpActivity.this)) {

            showLoading(getBinding().progressBar, true);

            viewModel.hitFbLogin(SignUpActivity.this, email, accessTokenFB, name, id, String.valueOf(profile_pic), hasFbEmail).observe(SignUpActivity.this, new Observer<LoginResponseModel>() {
                @Override
                public void onChanged(LoginResponseModel loginResponseModelResponse) {
                    if (Objects.requireNonNull(loginResponseModelResponse).getResponseCode() == 2000) {
                        Gson gson = new Gson();
                        modelLogin = loginResponseModelResponse.getData();
                        String stringJson = gson.toJson(loginResponseModelResponse.getData());
                        saveUserDetails(stringJson, loginResponseModelResponse.getData().getId(), false);
                    } else if (loginResponseModelResponse.getResponseCode() == 403) {
                        new ActivityLauncher(SignUpActivity.this).forceLogin(SignUpActivity.this, ForceLoginFbActivity.class, accessTokenFB, id, name, String.valueOf(profile_pic));
                    } else {
                        dismissLoading(getBinding().progressBar);
                        showDialog(SignUpActivity.this.getResources().getString(R.string.error), loginResponseModelResponse.getDebugMessage().toString());
                    }
                }
            });
        } else {
            new ToastHandler(SignUpActivity.this).show(SignUpActivity.this.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void clearEditView() {
        getBinding().etName.setText("");
        getBinding().etEmail.setText("");
        getBinding().etPassword.setText("");
        getBinding().etCnfPassword.setText("");
        getBinding().errorEmail.setVisibility(View.INVISIBLE);
        getBinding().errorName.setVisibility(View.INVISIBLE);
        getBinding().errorCnfPassword.setVisibility(View.INVISIBLE);
        getBinding().errorPassword.setVisibility(View.INVISIBLE);
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
       // onBackPressed();
        new ActivityLauncher(SignUpActivity.this).onContentScreen(SignUpActivity.this, ContentPreference.class,isNotificationEnable);

        //new ActivityLauncher(SignUpActivity.this).homeScreen(SignUpActivity.this, HomeActivity.class);

        try {
            trackEvent(String.valueOf(fbLoginData.getName()),String.valueOf(fbLoginData.getEmail()));
        }catch (Exception e){

        }
    }

    private void trackEvent(String name, String type) {
        final JsonObject requestParam = new JsonObject();
        requestParam.addProperty(EventConstant.Name, name);
        requestParam.addProperty(EventConstant.PlatformType, type);
        FCMEvents.getInstance().setContext(SignUpActivity.this).trackEvent(1,requestParam);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new ActivityLauncher(SignUpActivity.this).loginActivity(SignUpActivity.this, LoginActivity.class);

    }

    private void showDialog(String title, String message) {
        FragmentManager fm = getSupportFragmentManager();
        AlertDialogSingleButtonFragment alertDialog = AlertDialogSingleButtonFragment.newInstance(title, message, getResources().getString(R.string.ok));
        alertDialog.setCancelable(false);
        alertDialog.setAlertDialogCallBack(this);
        alertDialog.show(fm, "fragment_alert");
    }

    public boolean validateNameEmpty() {
        boolean check = false;
        if (StringUtils.isNullOrEmptyOrZero(getBinding().etName.getText().toString().trim())) {
            getBinding().errorName.setText(getResources().getString(R.string.empty_name));
            getBinding().errorName.setVisibility(View.VISIBLE);
        } else {
            check = true;
            getBinding().errorName.setVisibility(View.INVISIBLE);
        }
        return check;
    }


    private boolean validateEmptyEmail() {
        boolean check = false;
        if (StringUtils.isNullOrEmptyOrZero(getBinding().etEmail.getText().toString().trim())) {
            getBinding().errorEmail.setText(getResources().getString(R.string.email_check_signup));
            getBinding().errorEmail.setVisibility(View.VISIBLE);
        } else {
            check = true;
            getBinding().errorEmail.setVisibility(View.INVISIBLE);
        }
        return check;
    }

    public boolean validateEmail() {
        boolean check = false;
        if (getBinding().etEmail.getText().toString().trim().matches(AppConstants.EMAIL_REGEX)) {
            check = true;
            getBinding().errorEmail.setVisibility(View.INVISIBLE);
        } else {
            getBinding().errorEmail.setText(getResources().getString(R.string.valid_email));
            getBinding().errorEmail.setVisibility(View.VISIBLE);
        }
        return check;
    }


    public boolean passwordCheck(String password) {
       // String passwordRegex="^(?=.*[!&^%$#@()\\_+-])[A-Za-z0-9\\d!&^%$#@()\\_+-]{6,20}$";
        boolean check = false;
       // Pattern mPattern = Pattern.compile(passwordRegex);
       // Matcher matcher = mPattern.matcher(password.toString());
        if(!(password.length() >=6))
        {
            getBinding().errorPassword.setVisibility(View.VISIBLE);
            getBinding().errorPassword.setText(getResources().getString(R.string.strong_password_required));
          //  showDialog(SignUpActivity.this.getResources().getString(R.string.error), getResources().getString(R.string.strong_password_required));
        }else {
            getBinding().errorPassword.setVisibility(View.INVISIBLE);
            check = true;
        }
        return check;
    }

    public boolean confirmPasswordCheck(String password, String confirmPassword){
        boolean check = false;
        if (!password.equalsIgnoreCase(confirmPassword)){
            getBinding().errorCnfPassword.setVisibility(View.VISIBLE);
            getBinding().errorCnfPassword.setText(getResources().getString(R.string.password_match));
        }else {
            getBinding().errorCnfPassword.setVisibility(View.INVISIBLE);
            check = true;
        }
        return check;
    }

    public boolean stringContainsNumber(String s) {
        return Pattern.compile("[0-9]").matcher(s).find();
    }


    @Override
    public void onFinishDialog() {

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isFbLoginClick)
            getBinding().llFooter.setVisibility(View.GONE);
            getBinding().etPassword.setText("");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            Logger.e("LoginActivity", "" + e.toString());

        }
    }
}