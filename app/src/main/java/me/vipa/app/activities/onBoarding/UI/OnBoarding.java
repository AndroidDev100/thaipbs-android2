package me.vipa.app.activities.onBoarding.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import me.vipa.app.activities.homeactivity.ui.HomeActivity;
import me.vipa.app.activities.usermanagment.ui.SignUpActivity;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.databinding.ActivityOnBoardingBinding;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

public class OnBoarding extends BaseBindingActivity<ActivityOnBoardingBinding> {
    private KsPreferenceKeys preference;

    @Override
    public ActivityOnBoardingBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivityOnBoardingBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preference = KsPreferenceKeys.getInstance();

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add All Fragments to ViewPager
        viewPagerAdapter.addFragment(new StepOneFragment());
        viewPagerAdapter.addFragment(new StepTwoFragment());
        viewPagerAdapter.addFragment(new StepThreeFragment());


        // Set Adapter for ViewPager
        getBinding().viewPagerOnBoarding.setAdapter(viewPagerAdapter);

        // Setup dot's indicator

        getBinding().tabLayoutIndicator.setupWithViewPager(getBinding().viewPagerOnBoarding);

        getBinding().skipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.getInstance().homeScreen(OnBoarding.this, HomeActivity.class);
            }
        });
        getBinding().register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityLauncher.getInstance().signUpActivity(OnBoarding.this, SignUpActivity.class, "OnBoarding");

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preference.getAppPrefRegisterStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            ActivityLauncher.getInstance().homeScreen(OnBoarding.this, HomeActivity.class);
        }
    }

    @Override
    public void onBackPressed() {
        ActivityLauncher.getInstance().homeScreen(OnBoarding.this, HomeActivity.class);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }
        @Override
        public Fragment getItem(int i) {
            return mList.get(i);
        }
        @Override
        public int getCount() {
            return mList.size();
        }
        public void addFragment(Fragment fragment) {
            mList.add(fragment);
        }
    }


}