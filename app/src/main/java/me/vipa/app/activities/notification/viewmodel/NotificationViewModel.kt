package me.vipa.app.activities.notification.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import me.vipa.app.repository.notification.NotificationRepository

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val notificationRepository: NotificationRepository = NotificationRepository.getInstance()

}