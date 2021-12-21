package me.vipa.app.activities.notification.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.toolbar.view.*
import me.vipa.app.R
import me.vipa.app.activities.notification.viewmodel.NotificationViewModel
import me.vipa.app.baseModels.BaseBindingActivity
import me.vipa.app.databinding.ActivityNotificationBinding
import me.vipa.app.utils.helpers.ToolBarHandler

class NotificationActivity : BaseBindingActivity<ActivityNotificationBinding?>() {
    private var viewModel: NotificationViewModel? = null
    override fun inflateBindingLayout(inflater: LayoutInflater): ActivityNotificationBinding {
        return ActivityNotificationBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)

        setupToolbarIcons()
    }

    private fun setupToolbarIcons() {
        binding?.toolbar?.let {
            it.titleText.screen_text.setText(R.string.notification)
            it.titleText.visibility = View.VISIBLE
            it.backLayout.visibility = View.VISIBLE
            it.clNotification.visibility = View.GONE
            it.searchIcon.visibility = View.GONE
            it.clDelete.visibility = View.VISIBLE

            ToolBarHandler(this@NotificationActivity).setNotificationAction(it)
        }
    }
}