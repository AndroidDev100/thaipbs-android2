package com.mvhub.mvhubplus.activities.notification.viewmodel;

import android.app.Activity;
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.mvhub.mvhubplus.repository.notification.NotificationRepository;
import com.mvhub.mvhubplus.databinding.NotificationBinding;

public class NotificationViewModel extends AndroidViewModel {
    public NotificationViewModel(Application application, Activity activity, NotificationBinding binding) {
        super(application);
        NotificationRepository.getInstance().setAdapter(activity, binding);
    }
}
