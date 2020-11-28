package me.vipa.app.callbacks.apicallback;

import com.vipa.baseCollection.baseCategoryModel.BaseCategory;
import me.vipa.app.networking.errormodel.ApiErrorModel;

import java.util.List;

import me.vipa.app.networking.errormodel.ApiErrorModel;
import me.vipa.baseCollection.baseCategoryModel.BaseCategory;

public interface ApiResponseModel<T> {
    void onStart();

    default void onSuccess(T response){

    }
    default void onSuccess(List<BaseCategory> catList){

    }
    default void onError(ApiErrorModel apiError){

    }

    default void onFailure(ApiErrorModel httpError){

    }
}