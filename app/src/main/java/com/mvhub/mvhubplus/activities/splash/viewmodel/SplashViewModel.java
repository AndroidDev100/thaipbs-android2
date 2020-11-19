package com.mvhub.mvhubplus.activities.splash.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mvhub.mvhubplus.repository.splash.SplashRepository;

@SuppressWarnings("FieldCanBeLocal")
public class SplashViewModel extends AndroidViewModel {

    private final Context context;
    private final SplashRepository splashRepository;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        this.context = application;
        splashRepository = SplashRepository.getInstance();
    }
}
