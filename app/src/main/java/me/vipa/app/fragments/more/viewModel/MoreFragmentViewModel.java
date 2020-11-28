package me.vipa.app.fragments.more.viewModel;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.vipa.baseCollection.baseCategoryModel.BaseCategory;
import me.vipa.app.baseModels.HomeBaseViewModel;
import me.vipa.app.repository.more.MoreFragmentRepository;
import me.vipa.app.beanModel.params.ParamBean;
import com.vipa.app.databinding.FragmentMoreBinding;

import java.util.List;

import me.vipa.app.baseModels.HomeBaseViewModel;
import me.vipa.app.repository.more.MoreFragmentRepository;
import me.vipa.baseCollection.baseCategoryModel.BaseCategory;


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