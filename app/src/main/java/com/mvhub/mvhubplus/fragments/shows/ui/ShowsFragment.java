package com.mvhub.mvhubplus.fragments.shows.ui;

import android.os.Bundle;

import com.mvhub.mvhubplus.fragments.shows.viewModel.ShowsFragmentViewModel;
import com.mvhub.mvhubplus.beanModel.TabsBaseFragment;
import com.mvhub.mvhubplus.fragments.home.viewModel.HomeFragmentViewModel;

public class ShowsFragment extends TabsBaseFragment<HomeFragmentViewModel> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel(ShowsFragmentViewModel.class);
    }
}