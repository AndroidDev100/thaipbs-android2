package me.vipa.app.manager

import android.app.Application
import com.moengage.core.LogLevel
import com.moengage.core.MoEngage
import com.moengage.core.config.FcmConfig
import com.moengage.core.config.LogConfig
import com.moengage.core.config.NotificationConfig
import com.moengage.firebase.MoEFireBaseHelper
import com.moengage.firebase.listener.FirebaseEventListener
import com.moengage.pushbase.MoEPushHelper
import com.moengage.pushbase.push.PushMessageListener
import me.vipa.app.BuildConfig
import me.vipa.app.MvHubPlusApplication
import me.vipa.app.R
import me.vipa.brightcovelibrary.Logger

object MoEngageManager: FirebaseEventListener() {

    fun init(context: Application) {
        val moEngage = MoEngage.Builder(context, BuildConfig.MOENGAGE_APP_ID)
            .configureNotificationMetaData(
                NotificationConfig(
                    R.drawable.enveu_logo_small,
                    R.drawable.enveu_blue_logo, R.color.google_color, null,
                    isMultipleNotificationInDrawerEnabled = true,
                    isBuildingBackStackEnabled = true,
                    isLargeIconDisplayEnabled = true
                )
            )
            .configureFcm(FcmConfig(false))
            .configureLogs(LogConfig(LogLevel.VERBOSE, false))
            .build()
        MoEngage.initialise(moEngage)

        MoEPushHelper.getInstance().messageListener = PushMessageListener()
    }

    override fun onTokenAvailable(token: String) {
        super.onTokenAvailable(token)
        Logger.d("token: $token")
        MoEFireBaseHelper.getInstance().passPushToken(MvHubPlusApplication.getInstance(), token)
    }
}