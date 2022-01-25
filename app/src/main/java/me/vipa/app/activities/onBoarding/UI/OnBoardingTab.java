package me.vipa.app.activities.onBoarding.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import me.vipa.app.activities.homeactivity.ui.HomeActivity;
import me.vipa.app.activities.usermanagment.ui.SignUpActivity;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.databinding.ActivityOnBoardingTabBinding;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

public class OnBoardingTab extends BaseBindingActivity<ActivityOnBoardingTabBinding> {
    private KsPreferenceKeys preference;

    @Override
    public ActivityOnBoardingTabBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivityOnBoardingTabBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preference = KsPreferenceKeys.getInstance();


        getBinding().skipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.getInstance().homeScreen(OnBoardingTab.this, HomeActivity.class);
            }
        });
        getBinding().register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityLauncher.getInstance().signUpActivity(OnBoardingTab.this, SignUpActivity.class, "");

            }
        });

    }

    @Override
    public void onBackPressed() {
        ActivityLauncher.getInstance().homeScreen(OnBoardingTab.this, HomeActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preference.getAppPrefRegisterStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            ActivityLauncher.getInstance().homeScreen(OnBoardingTab.this, HomeActivity.class);
        }
    }
}