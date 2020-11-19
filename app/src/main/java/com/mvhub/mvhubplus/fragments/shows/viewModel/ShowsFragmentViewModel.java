package com.mvhub.mvhubplus.fragments.shows.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.mvhub.baseCollection.baseCategoryModel.BaseCategory;
import com.mvhub.mvhubplus.baseModels.HomeBaseViewModel;
import com.mvhub.mvhubplus.repository.home.HomeFragmentRepository;
import com.mvhub.mvhubplus.utils.constants.AppConstants;

import java.util.List;

public class ShowsFragmentViewModel extends HomeBaseViewModel {
    public ShowsFragmentViewModel(@NonNull Application application) {
        super(application);
    }


    @Override
    public LiveData<List<BaseCategory>> getAllCategories() {

        return HomeFragmentRepository.getInstance().getCategories(AppConstants.HOME_ENVEU);
    }


    @Override
    public void resetObject() {

    }
}
