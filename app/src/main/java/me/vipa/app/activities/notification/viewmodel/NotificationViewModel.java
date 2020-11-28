package me.vipa.app.activities.notification.viewmodel;

import android.app.Activity;
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import me.vipa.app.repository.notification.NotificationRepository;
import com.vipa.app.databinding.NotificationBinding;

import me.vipa.app.repository.notification.NotificationRepository;

public class NotificationViewModel extends AndroidViewModel {
    public NotificationViewModel(Application application, Activity activity, NotificationBinding binding) {
        super(application);
        NotificationRepository.getInstance().setAdapter(activity, binding);
    }
}
