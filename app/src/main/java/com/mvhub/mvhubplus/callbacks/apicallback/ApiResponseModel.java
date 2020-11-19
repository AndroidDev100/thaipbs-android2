package com.mvhub.mvhubplus.callbacks.apicallback;

import com.mvhub.baseCollection.baseCategoryModel.BaseCategory;
import com.mvhub.mvhubplus.networking.errormodel.ApiErrorModel;

import java.util.List;

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