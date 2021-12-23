package me.vipa.app.manager

import android.content.Context
import android.text.format.DateFormat
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.model.UserGender
import me.vipa.app.MvHubPlusApplication
import me.vipa.brightcovelibrary.Logger
import java.text.DecimalFormat
import java.util.*

object MoEUserTracker {

    private fun getNonNullContext(context: Context?): Context {
        return context ?: MvHubPlusApplication.getInstance()
    }

    private fun getMoEHelper(context: Context?): MoEHelper {
        return MoEHelper.getInstance(getNonNullContext(context))
    }

    fun logout(context: Context?) {
        getMoEHelper(context).logoutUser()
    }

    fun setUniqueId(context: Context?, id: String?) {
        id?.let { _id -> getMoEHelper(context).setUniqueId(_id) }
    }

    fun setEmail(context: Context?, email: String?) {
        email?.let { _email -> getMoEHelper(context).setEmail(_email) }
    }

    fun setUsername(context: Context?, username: String?) {
        username?.let { _username -> getMoEHelper(context).setFullName(_username) }
    }

    fun setPhone(context: Context?, phone: String?) {
        phone?.let { _phone -> getMoEHelper(context).setNumber(_phone) }
    }

    fun setGender(context: Context?, gender: String?) {
        gender?.let { _gender -> getMoEHelper(context).setGender(UserGender.valueOf(_gender)) }
    }

    fun setDob(context: Context?, dob: Double?) {
        dob?.let { _dob ->
            val decimalFormat = DecimalFormat("#")
            decimalFormat.maximumFractionDigits = 0
            val longDate = decimalFormat.format(_dob).toLong()
            val dateString = DateFormat.format("dd MMMM yyyy", Date(longDate)).toString()
            Logger.d("dob: $dateString")
            getMoEHelper(context).setBirthDate(Date(longDate))
        }
    }
}