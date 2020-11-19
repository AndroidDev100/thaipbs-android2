package com.mvhub.mvhubplus.fragments.home.ui;

import android.os.Bundle;

import com.mvhub.mvhubplus.fragments.home.viewModel.HomeFragmentViewModel;
import com.mvhub.mvhubplus.beanModel.TabsBaseFragment;

public class HomeFragment extends TabsBaseFragment<HomeFragmentViewModel> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel(HomeFragmentViewModel.class);
    }

}