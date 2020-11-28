package me.vipa.app.fragments.movies.ui;

import android.os.Bundle;

import me.vipa.app.fragments.movies.viewModel.MovieFragmentViewModel;
import me.vipa.app.beanModel.TabsBaseFragment;
import me.vipa.app.fragments.home.viewModel.HomeFragmentViewModel;

public class MovieFragment extends TabsBaseFragment<HomeFragmentViewModel> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel(MovieFragmentViewModel.class);
    }
}