package me.vipa.app.fragments.news.ui;

import android.os.Bundle;

import me.vipa.app.fragments.news.viewModel.NewsFragmentViewModel;
import me.vipa.app.beanModel.TabsBaseFragment;
import me.vipa.app.fragments.home.viewModel.HomeFragmentViewModel;
import me.vipa.app.fragments.news.viewModel.NewsFragmentViewModel;

public class NewsFragment extends TabsBaseFragment<HomeFragmentViewModel> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel(NewsFragmentViewModel.class);
    }
}