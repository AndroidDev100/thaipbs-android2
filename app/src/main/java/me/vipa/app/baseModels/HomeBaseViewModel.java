package me.vipa.app.baseModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import me.vipa.baseCollection.baseCategoryModel.BaseCategory;

import java.util.List;

import me.vipa.baseCollection.baseCategoryModel.BaseCategory;


public abstract class HomeBaseViewModel extends AndroidViewModel {
    protected HomeBaseViewModel(@NonNull Application application) {
        super(application);
    }

    public abstract LiveData<List<BaseCategory>> getAllCategories();

    public abstract void resetObject();

}
