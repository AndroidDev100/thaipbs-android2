package com.mvhub.mvhubplus.callbacks.commonCallbacks;


import com.mvhub.mvhubplus.beanModel.responseModels.VersionVerification.Result;

public interface CallBacks {
    void onCountryClick(Result country, int position);

    default void common(boolean status){

    }
}
