package me.vipa.app.fragments.shows.ui;

import android.os.Bundle;

import me.vipa.app.fragments.shows.viewModel.ShowsFragmentViewModel;
import me.vipa.app.beanModel.TabsBaseFragment;
import me.vipa.app.fragments.home.viewModel.HomeFragmentViewModel;
import me.vipa.app.fragments.shows.viewModel.ShowsFragmentViewModel;

public class ShowsFragment extends TabsBaseFragment<HomeFragmentViewModel> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel(ShowsFragmentViewModel.class);
    }
}