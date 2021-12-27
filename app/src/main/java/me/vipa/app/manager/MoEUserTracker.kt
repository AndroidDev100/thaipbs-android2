package me.vipa.app.manager

import android.content.Context
import android.text.format.DateFormat
import com.google.gson.Gson
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.model.UserGender
import me.vipa.app.MvHubPlusApplication
import me.vipa.app.beanModel.userProfile.UserProfileResponse
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys
import me.vipa.brightcovelibrary.Logger
import me.vipa.brightcovelibrary.utils.ObjectHelper
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
        Logger.d("Logging out user")
        getMoEHelper(context).logoutUser()
    }

    fun setUniqueId(context: Context?, id: String?) {
        Logger.d("setting unique id: $id")
        id?.let { _id -> getMoEHelper(context).setUniqueId(_id) }
    }

    fun setEmail(context: Context?, email: String?) {
        Logger.d("setting email: $email")
        email?.let { _email -> getMoEHelper(context).setEmail(_email) }
    }

    fun setUsername(context: Context?, username: String?) {
        Logger.d("setting username: $username")
        username?.let { _username -> getMoEHelper(context).setFullName(_username) }
    }

    private fun setPhone(context: Context?, phone: String?) {
        Logger.d("setting phone: $phone")
        phone?.let { _phone -> getMoEHelper(context).setNumber(_phone) }
    }

    private fun setGender(context: Context?, gender: String?) {
        Logger.d("setting gender: $gender")
        gender?.let { _gender -> getMoEHelper(context).setGender(UserGender.valueOf(_gender)) }
    }

    private fun setDob(context: Context?, dob: Double?) {
        Logger.d("setting dob: $dob")
        dob?.let { _dob ->
            val decimalFormat = DecimalFormat("#")
            decimalFormat.maximumFractionDigits = 0
            val longDate = decimalFormat.format(_dob).toLong()
            Logger.d("dob (long): $longDate")
            val dateString = DateFormat.format("dd MMMM yyyy", Date(longDate)).toString()
            Logger.d("dob: $dateString")
            getMoEHelper(context).setBirthDate(Date(longDate))
        }
    }

    fun setUserProperties(context: Context?) {
        Logger.d("called from : ${Logger.getTag()}")
        val json = KsPreferenceKeys.getInstance().userProfileData
        Logger.d("user data: $json")
        val response: UserProfileResponse? = Gson().fromJson(json, UserProfileResponse::class.java)
        response?.data?.let { user ->
            setUsername(context, user.name)
            if (ObjectHelper.isNotEmpty(user.dateOfBirth)) {
                setDob(context, user.dateOfBirth as Double)
            }
            setEmail(context, user.email?.toString())
            setPhone(context, user.phoneNumber?.toString())
            setGender(context, user.gender?.toString())
            setUniqueId(context, user.id?.toString())
        }
    }
}