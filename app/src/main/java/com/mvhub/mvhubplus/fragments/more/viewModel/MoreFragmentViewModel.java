package com.mvhub.mvhubplus.fragments.more.viewModel;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.mvhub.baseCollection.baseCategoryModel.BaseCategory;
import com.mvhub.mvhubplus.baseModels.HomeBaseViewModel;
import com.mvhub.mvhubplus.repository.more.MoreFragmentRepository;
import com.mvhub.mvhubplus.beanModel.params.ParamBean;
import com.mvhub.mvhubplus.databinding.FragmentMoreBinding;

import java.util.List;


public class MoreFragmentViewModel extends HomeBaseViewModel {

    public MoreFragmentViewModel(@NonNull Application application, ParamBean mParam, Activity context, FragmentMoreBinding binding) {
        super(application);
        MoreFragmentRepository.getInstance().setAdapter(mParam, context, binding);
    }

    @Override
    public LiveData<List<BaseCategory>> getAllCategories() {
        return null;
    }

    @Override
    public void resetObject() {

    }
}