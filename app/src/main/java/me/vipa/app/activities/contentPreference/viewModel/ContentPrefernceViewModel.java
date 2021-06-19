package me.vipa.app.activities.contentPreference.viewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import me.vipa.app.beanModel.userProfile.UserProfileResponse;
import me.vipa.app.repository.userManagement.RegistrationLoginRepository;

public class ContentPrefernceViewModel extends AndroidViewModel {

    final RegistrationLoginRepository loginRepository;

    public ContentPrefernceViewModel(@NonNull Application application) {
        super(application);
        loginRepository = RegistrationLoginRepository.getInstance();
    }

    public LiveData<UserProfileResponse> hitUpdateProfile(Context context, String token, String name, String mobile, String spinnerValue, String dob, String address, String imageUrl, String via, String contentPreference, boolean isNotificationEnable,String pin,boolean pinFlow) {
        return loginRepository.getUpdateProfile(context,token,name,mobile,spinnerValue,dob,address,imageUrl,via,contentPreference,isNotificationEnable,pin,pinFlow);
    }

}
