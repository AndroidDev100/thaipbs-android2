package me.vipa.app.fragments.home.ui;

import android.os.Bundle;
import android.util.Log;

import com.moengage.inapp.MoEInAppHelper;

import me.vipa.app.activities.homeactivity.ui.HomeActivity;
import me.vipa.app.fragments.home.viewModel.HomeFragmentViewModel;
import me.vipa.app.beanModel.TabsBaseFragment;
import me.vipa.app.fragments.home.viewModel.HomeFragmentViewModel;
import me.vipa.app.utils.constants.AppConstants;

public class HomeFragment extends TabsBaseFragment<HomeFragmentViewModel> {
    private boolean isKidsMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel(HomeFragmentViewModel.class);
    }
}