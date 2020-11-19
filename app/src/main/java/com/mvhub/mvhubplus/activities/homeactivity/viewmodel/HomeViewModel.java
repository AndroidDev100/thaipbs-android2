package com.mvhub.mvhubplus.activities.homeactivity.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mvhub.mvhubplus.repository.home.HomeRepository;
import com.mvhub.mvhubplus.beanModel.emptyResponse.ResponseEmpty;
import com.mvhub.mvhubplus.beanModel.userProfile.UserProfileResponse;
import com.mvhub.mvhubplus.repository.userManagement.RegistrationLoginRepository;
import com.google.gson.JsonObject;


public class HomeViewModel extends AndroidViewModel {

    final HomeRepository homeRepository;
    final RegistrationLoginRepository loginRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);

        homeRepository = HomeRepository.getInstance();
        loginRepository = RegistrationLoginRepository.getInstance();
    }

    public LiveData<JsonObject> hitLogout(boolean session, String token) {
        return homeRepository.hitApiLogout(session, token);
    }

    public LiveData<ResponseEmpty> hitVerify(String token) {
        return homeRepository.hitApiVerifyUser(token);
    }

    public LiveData<UserProfileResponse> hitUserProfile(Context context, String token) {
        return loginRepository.getUserProfile(context,token);
    }
}
