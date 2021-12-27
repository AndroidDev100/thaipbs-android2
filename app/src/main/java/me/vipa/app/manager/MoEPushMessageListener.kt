package me.vipa.app.manager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.moengage.pushbase.push.PushMessageListener
import me.vipa.app.activities.splash.ui.ActivitySplash
import me.vipa.brightcovelibrary.Logger

class MoEPushMessageListener: PushMessageListener() {
    override fun onHandleRedirection(activity: Activity, payload: Bundle) {
        super.onHandleRedirection(activity, payload)

        Logger.d("payload: $payload")

        val assetType: String? = payload.getString("contentType")
        val assetId: String? = payload.getString("id")
        val seriesId: String? = payload.getString("seriesId")
        val seasonNumber: String? = payload.getString("seasonNumber")

        val intent = Intent(activity, ActivitySplash::class.java).apply {
            putExtra("assetId", assetId)
            putExtra("assetType", assetType)
            putExtra("seriesId", seriesId)
            putExtra("seasonNumber", seasonNumber)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        activity.startActivity(intent)
    }

    override fun onNotificationReceived(context: Context, payload: Bundle) {
        super.onNotificationReceived(context, payload)
        MoEngageNotificationManager.getAllNotifications()
    }
}