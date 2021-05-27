package me.vipa.userManagement.callBacks


import me.vipa.userManagement.bean.allSecondaryDetails.AllSecondaryDetails
import retrofit2.Response

interface AllListCallBack {

    fun success(status: Boolean, allListResponse : Response<AllSecondaryDetails>){

    }
    fun failure(status: Boolean, errorCode : Int, message : String){

    }
}