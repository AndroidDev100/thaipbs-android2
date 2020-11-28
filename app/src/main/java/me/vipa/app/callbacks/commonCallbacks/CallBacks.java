package me.vipa.app.callbacks.commonCallbacks;


import me.vipa.app.beanModel.responseModels.VersionVerification.Result;

public interface CallBacks {
    void onCountryClick(Result country, int position);

    default void common(boolean status){

    }
}
