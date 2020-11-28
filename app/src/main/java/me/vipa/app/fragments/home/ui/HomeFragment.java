package me.vipa.app.fragments.home.ui;

import android.os.Bundle;

import me.vipa.app.fragments.home.viewModel.HomeFragmentViewModel;
import me.vipa.app.beanModel.TabsBaseFragment;
import me.vipa.app.fragments.home.viewModel.HomeFragmentViewModel;

public class HomeFragment extends TabsBaseFragment<HomeFragmentViewModel> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel(HomeFragmentViewModel.class);
    }

}