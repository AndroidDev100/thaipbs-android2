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
        getMoEHelper(context).logoutUser()
    }

    private fun setUniqueId(context: Context?, id: String?) {
        id?.let { _id -> getMoEHelper(context).setUniqueId(_id) }
    }

    private fun setEmail(context: Context?, email: String?) {
        email?.let { _email -> getMoEHelper(context).setEmail(_email) }
    }

    private fun setUsername(context: Context?, username: String?) {
        username?.let { _username -> getMoEHelper(context).setFullName(_username) }
    }

    private fun setPhone(context: Context?, phone: String?) {
        phone?.let { _phone -> getMoEHelper(context).setNumber(_phone) }
    }

    private fun setGender(context: Context?, gender: String?) {
        gender?.let { _gender -> getMoEHelper(context).setGender(UserGender.valueOf(_gender)) }
    }

    private fun setDob(context: Context?, dob: Double?) {
        dob?.let { _dob ->
            val decimalFormat = DecimalFormat("#")
            decimalFormat.maximumFractionDigits = 0
            val longDate = decimalFormat.format(_dob).toLong()
            val dateString = DateFormat.format("dd MMMM yyyy", Date(longDate)).toString()
            Logger.d("dob: $dateString")
            getMoEHelper(context).setBirthDate(Date(longDate))
        }
    }

    fun setUserProperties(context: Context?) {
        val json = KsPreferenceKeys.getInstance().userProfileData
        Logger.d("user data: $json")
        val response: UserProfileResponse? = Gson().fromJson(json, UserProfileResponse::class.java)
        response?.data?.let { user ->
            setUsername(context, user.name)
            if (ObjectHelper.isNotEmpty(user.dateOfBirth)) {
                setDob(context, user.dateOfBirth as Double)
            }
            setEmail(context, user.email.toString())
            setPhone(context, user.phoneNumber.toString())
            setGender(context, user.gender.toString())
            setUniqueId(context, user.id.toString())
        }
    }
}