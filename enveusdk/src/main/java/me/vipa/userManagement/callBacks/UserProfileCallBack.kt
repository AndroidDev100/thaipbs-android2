package me.vipa.userManagement.callBacks

import retrofit2.Response
import me.vipa.userManagement.bean.UserProfile.UserProfileResponse


interface UserProfileCallBack {

    fun success(status: Boolean, loginResponse : Response<UserProfileResponse>){

    }
    fun failure(status: Boolean, errorCode : Int, message : String){

    }
}