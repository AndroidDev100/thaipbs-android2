package me.vipa.userManagement.callBacks


import me.vipa.userManagement.bean.allSecondaryDetails.SwitchUserDetails
import retrofit2.Response

interface SwitchUserCallBack {

    fun success(status: Boolean, allListResponse : Response<SwitchUserDetails>){

    }
    fun failure(status: Boolean, errorCode : Int, message : String){

    }
}