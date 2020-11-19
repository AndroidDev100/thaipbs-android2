package com.mvhub.mvhubplus.fragments.news.ui;

import android.os.Bundle;

import com.mvhub.mvhubplus.fragments.news.viewModel.NewsFragmentViewModel;
import com.mvhub.mvhubplus.beanModel.TabsBaseFragment;
import com.mvhub.mvhubplus.fragments.home.viewModel.HomeFragmentViewModel;

public class NewsFragment extends TabsBaseFragment<HomeFragmentViewModel> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel(NewsFragmentViewModel.class);
    }
}