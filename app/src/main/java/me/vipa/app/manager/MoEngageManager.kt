package me.vipa.app.manager

import android.app.Application
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.LogLevel
import com.moengage.core.MoEngage
import com.moengage.core.config.FcmConfig
import com.moengage.core.config.LogConfig
import com.moengage.core.config.NotificationConfig
import com.moengage.core.model.AppStatus
import com.moengage.firebase.listener.FirebaseEventListener
import com.moengage.pushbase.MoEPushHelper
import me.vipa.app.BuildConfig
import me.vipa.app.R
import me.vipa.app.utils.helpers.CheckInstallStatus
import me.vipa.brightcovelibrary.Logger

object MoEngageManager: FirebaseEventListener() {

    fun init(context: Application) {
        val moEngage = MoEngage.Builder(context, BuildConfig.MOENGAGE_APP_ID)
            .configureNotificationMetaData(
                NotificationConfig(
                    R.drawable.ic_notification,
                    -1, R.color.more_text_color_dark, "sound",
                    isMultipleNotificationInDrawerEnabled = true,
                    isBuildingBackStackEnabled = true,
                    isLargeIconDisplayEnabled = true
                )
            )
            .configureFcm(FcmConfig(false))
            .configureLogs(LogConfig(LogLevel.VERBOSE, false))
            .build()
        MoEngage.initialise(moEngage)
        if(CheckInstallStatus.isFirstInstall(context)){
            MoEHelper.getInstance(context).setAppStatus(AppStatus.INSTALL)
        }
        else if(CheckInstallStatus.isInstallFromUpdate(context)){
            MoEHelper.getInstance(context).setAppStatus(AppStatus.UPDATE)
        }
        MoEPushHelper.getInstance().messageListener = MoEPushMessageListener()
    }

    override fun onTokenAvailable(token: String) {
        super.onTokenAvailable(token)
        Logger.d("firebase token: $token")
    }
}