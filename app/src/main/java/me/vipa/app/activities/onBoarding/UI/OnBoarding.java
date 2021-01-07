package me.vipa.app.activities.onBoarding.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.databinding.ActivityOnBoardingBinding;
import me.vipa.app.databinding.SettingsActivityBinding;

public class OnBoarding extends BaseBindingActivity<ActivityOnBoardingBinding> {

    @Override
    public ActivityOnBoardingBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivityOnBoardingBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add All Fragments to ViewPager
        viewPagerAdapter.addFragment(new StepOneFragment());
        viewPagerAdapter.addFragment(new StepTwoFragment());
        viewPagerAdapter.addFragment(new StepThreeFragment());


        // Set Adapter for ViewPager
        getBinding().viewPagerOnBoarding.setAdapter(viewPagerAdapter);

        // Setup dot's indicator

        getBinding().tabLayoutIndicator.setupWithViewPager(getBinding().viewPagerOnBoarding);

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