package me.vipa.app.activities.usermanagment.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Objects;

import me.vipa.app.R;
import me.vipa.app.activities.profile.ui.ProfileActivityNew;
import me.vipa.app.activities.usermanagment.viewmodel.RegistrationLoginViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.beanModel.userProfile.UserProfileResponse;
import me.vipa.app.databinding.ActivitySignUpThirdPageBinding;
import me.vipa.app.databinding.ProfileActivityNewBinding;
import me.vipa.app.fragments.dialog.AlertDialogFragment;
import me.vipa.app.fragments.dialog.AlertDialogSingleButtonFragment;
import me.vipa.app.utils.helpers.CheckInternetConnection;
import me.vipa.app.utils.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.StringUtils;
import me.vipa.app.utils.helpers.ToastHandler;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

public class SignUpThirdPage extends BaseBindingActivity<ActivitySignUpThirdPageBinding> implements AlertDialogFragment.AlertDialogListener {
    private RegistrationLoginViewModel viewModel;
    private KsPreferenceKeys preference;
    private boolean isloggedout = false;
    String spin_val;

    String[] gender = {"Gender", "Male", "Female", "Others"};
    private int mYear, mMonth, mDay;

    @Override
    public ActivitySignUpThirdPageBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivitySignUpThirdPageBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionObserver();
    }

    private void connectionObserver() {
        preference = KsPreferenceKeys.getInstance();
       // callModel();
        setToolbar();

        if (NetworkConnectivity.isOnline(SignUpThirdPage.this)) {
            connectionValidation(true);
        } else {
            connectionValidation(false);
        }
    }
    private void setToolbar(){
        getBinding().toolbar.backLayout.setVisibility(View.VISIBLE);
        getBinding().toolbar.titleToolbar.setVisibility(View.GONE);
        getBinding().toolbar.titleSkip.setText(getResources().getString(R.string.skip));
    }



    private void callModel() {
        viewModel = ViewModelProviders.of(SignUpThirdPage.this).get(RegistrationLoginViewModel.class);
    }

    private void connectionValidation(boolean connected) {
        if (connected) {
            getBinding().noConnectionLayout.setVisibility(View.GONE);
            setSpinner();
            setClicks();
        } else {
            noConnectionLayout();
        }
    }

    private void setSpinner() {
        getBinding().spinnerId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spin_val = gender[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(SignUpThirdPage.this, R.layout.spinner_item, gender);

// setting adapters to spinners

        getBinding().spinnerId.setAdapter(spin_adapter);
    }

    private void setClicks() {
        getBinding().toolbar.backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getBinding().dobLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.YEAR,-18);
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpThirdPage.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                getBinding().etDob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private void noConnectionLayout() {
            getBinding().noConnectionLayout.setVisibility(View.VISIBLE);
            getBinding().connection.retryTxt.setOnClickListener(view -> connectionObserver());
        }




    public boolean validateNameEmpty() {
       boolean check = false;
//        if (StringUtils.isNullOrEmptyOrZero(getBinding().etName.getText().toString().trim())) {
//            getBinding().errorName.setText(getResources().getString(R.string.empty_name));
//            getBinding().errorName.setVisibility(View.VISIBLE);
//        } else {
//            check = true;
//            getBinding().errorName.setVisibility(View.INVISIBLE);
//        }
       return check;
    }



    private void showDialog(String title, String message) {
        FragmentManager fm = getSupportFragmentManager();
        AlertDialogSingleButtonFragment alertDialog = AlertDialogSingleButtonFragment.newInstance(title, message, getResources().getString(R.string.ok));
        alertDialog.setCancelable(false);
        alertDialog.setAlertDialogCallBack(this);
        alertDialog.show(fm, "fragment_alert");
    }

    private void logoutCall() {
        if (CheckInternetConnection.isOnline(Objects.requireNonNull(this))) {
            clearCredientials(preference);
            hitApiLogout(this, preference.getAppPrefAccessToken());
        } else {
            // new ToastHandler(this).show(getResources().getString(R.string.no_internet_connection));
        }
    }

    @Override
    public void onFinishDialog() {
        if (isloggedout) {
            logoutCall();
        } else {
            onBackPressed();
        }

    }
}