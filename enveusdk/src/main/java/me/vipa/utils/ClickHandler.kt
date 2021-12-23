package me.vipa.utils

import android.os.SystemClock

object ClickHandler {
    private var mLastClickTime: Long = 0

    fun allowClick(): Boolean {
        val currentTime = SystemClock.elapsedRealtime()
        val allow = currentTime - mLastClickTime > 1200
        if (allow) {
            mLastClickTime = currentTime
        }
        return allow
    }

    fun disallowClick() = !allowClick()
}