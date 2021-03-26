package me.vipa.app.activities.usermanagment.ui;


import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import me.vipa.app.activities.usermanagment.viewmodel.RegistrationLoginViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.R;
import me.vipa.app.databinding.ForgotPasswordBinding;
import me.vipa.app.fragments.dialog.AlertDialogFragment;
import me.vipa.app.fragments.dialog.AlertDialogSingleButtonFragment;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.CheckInternetConnection;
import me.vipa.app.utils.helpers.StringUtils;
import me.vipa.app.utils.helpers.ToolBarHandler;
import me.vipa.app.activities.usermanagment.viewmodel.RegistrationLoginViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;

public class ForgotPasswordActivity extends BaseBindingActivity<ForgotPasswordBinding> implements AlertDialogFragment.AlertDialogListener {

    private RegistrationLoginViewModel viewModel;
    private long mLastClickTime = 0;
    private boolean errorDialog = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callBinding();
    }

    @Override
    public ForgotPasswordBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ForgotPasswordBinding.inflate(inflater);
    }

    private void callBinding() {
        viewModel = ViewModelProviders.of(ForgotPasswordActivity.this).get(RegistrationLoginViewModel.class);
        connectionObserver();
        connectObservors();
    }


    private void connectionObserver() {
        if (NetworkConnectivity.isOnline(ForgotPasswordActivity.this)) {
            connectionValidation(true);
        } else {
            connectionValidation(false);
        }
    }


    private void connectionValidation(Boolean aBoolean) {
        if (aBoolean) {
            getBinding().root.setVisibility(View.VISIBLE);
            getBinding().noConnectionLayout.setVisibility(View.GONE);
        } else {
            noConnectionLayout();
        }
    }

    private void noConnectionLayout() {
        getBinding().root.setVisibility(View.GONE);
        getBinding().noConnectionLayout.setVisibility(View.VISIBLE);
        getBinding().connection.retryTxt.setOnClickListener(view -> {
            getBinding().etPasswordRecoveryEmail.setText("");
            connectionObserver();
        });
    }


    public void connectObservors() {
        new ToolBarHandler(ForgotPasswordActivity.this).setAction(getBinding().toolbar, "ForgotPasswordActivity");

        getBinding().toolbar.backLayout.setOnClickListener(view -> onBackPressed());

        getBinding().tvSendEmail.setOnClickListener(view -> {
            if (CheckInternetConnection.isOnline(ForgotPasswordActivity.this)) {
                if (validateEmptyEmail() && validateEmail()) {

                    showProgressDialog();
                    //hit api to update
                    viewModel.hitForgotPasswordApi(getBinding().etPasswordRecoveryEmail.getText().toString().trim()).observe(ForgotPasswordActivity.this, jsonObject -> {
                        dismissLoading(getBinding().progressBar);
                        if (jsonObject.getCode() == 200){
                            errorDialog = false;
                            showDialog("", getResources().getString(R.string.forgot_password_response));
                        }

                        else{
                            if (jsonObject.getDebugMessage()!=null && !jsonObject.getDebugMessage().equalsIgnoreCase("")){
                                errorDialog = true;
                                showDialog(ForgotPasswordActivity.this.getResources().getString(R.string.error),jsonObject.getDebugMessage());
                            }else {
                                errorDialog = false;
                                showDialog("", getResources().getString(R.string.forgot_password_response));
                            }

                        }

                    });
                }
            } else
                connectionObserver();
            //new ToastHandler(ForgotPasswordActivity.this).show(ForgotPasswordActivity.this.getResources().getString(R.string.no_internet_connection));
        });

    }


    public void showProgressDialog() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        showLoading(getBinding().progressBar, true);

    }


    private void showDialog(String title, String message) {
        FragmentManager fm = getSupportFragmentManager();
        AlertDialogSingleButtonFragment alertDialog = AlertDialogSingleButtonFragment.newInstance(title, message, getResources().getString(R.string.ok));
        alertDialog.setCancelable(false);
        alertDialog.setAlertDialogCallBack(this);
        alertDialog.show(fm, "fragment_alert");
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
        if (errorDialog){

        }else {
            onBackPressed();
        }

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