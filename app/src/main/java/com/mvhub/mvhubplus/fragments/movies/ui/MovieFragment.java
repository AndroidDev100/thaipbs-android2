package com.mvhub.mvhubplus.fragments.movies.ui;

import android.os.Bundle;

import com.mvhub.mvhubplus.fragments.movies.viewModel.MovieFragmentViewModel;
import com.mvhub.mvhubplus.beanModel.TabsBaseFragment;
import com.mvhub.mvhubplus.fragments.home.viewModel.HomeFragmentViewModel;

public class MovieFragment extends TabsBaseFragment<HomeFragmentViewModel> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel(MovieFragmentViewModel.class);
    }
}