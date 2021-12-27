package me.vipa.app.manager

import android.app.Application
import com.moengage.core.LogLevel
import com.moengage.core.MoEngage
import com.moengage.core.config.FcmConfig
import com.moengage.core.config.LogConfig
import com.moengage.core.config.NotificationConfig
import com.moengage.firebase.listener.FirebaseEventListener
import com.moengage.pushbase.MoEPushHelper
import me.vipa.app.BuildConfig
import me.vipa.app.R
import me.vipa.brightcovelibrary.Logger

object MoEngageManager: FirebaseEventListener() {

    fun init(context: Application) {
        val moEngage = MoEngage.Builder(context, BuildConfig.MOENGAGE_APP_ID)
            .configureNotificationMetaData(
                NotificationConfig(
                    R.drawable.notification_icon,
                    R.drawable.notification_icon, R.color.google_color, "sound",
                    isMultipleNotificationInDrawerEnabled = true,
                    isBuildingBackStackEnabled = true,
                    isLargeIconDisplayEnabled = true
                )
            )
            .configureFcm(FcmConfig(false))
            .configureLogs(LogConfig(LogLevel.VERBOSE, false))
            .build()
        MoEngage.initialise(moEngage)

        MoEPushHelper.getInstance().messageListener = MoEPushMessageListener()
    }

    override fun onTokenAvailable(token: String) {
        super.onTokenAvailable(token)
        Logger.d("firebase token: $token")
    }
}