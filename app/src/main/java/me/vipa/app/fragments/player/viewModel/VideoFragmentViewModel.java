package me.vipa.app.fragments.player.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import me.vipa.baseCollection.baseCategoryModel.BaseCategory;
import me.vipa.app.baseModels.HomeBaseViewModel;
import me.vipa.app.repository.home.HomeFragmentRepository;
import me.vipa.app.utils.constants.AppConstants;

import java.util.List;

import me.vipa.app.baseModels.HomeBaseViewModel;
import me.vipa.app.repository.home.HomeFragmentRepository;
import me.vipa.baseCollection.baseCategoryModel.BaseCategory;

public class VideoFragmentViewModel extends HomeBaseViewModel {

    public VideoFragmentViewModel(@NonNull Application application) {
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
