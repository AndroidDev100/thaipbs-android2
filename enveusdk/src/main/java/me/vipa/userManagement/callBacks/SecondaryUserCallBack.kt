package me.vipa.userManagement.callBacks

import me.vipa.userManagement.bean.allSecondaryDetails.AllSecondaryDetails
import me.vipa.userManagement.bean.allSecondaryDetails.SecondaryUserDetails
import retrofit2.Response

interface SecondaryUserCallBack {

    fun success(status: Boolean, allListResponse : Response<SecondaryUserDetails>){

    }
    fun failure(status: Boolean, errorCode : Int, message : String){

    }
}