package com.mvhub.userManagement.callBacks

import com.mvhub.userManagement.bean.LoginResponse.LoginResponseModel
import retrofit2.Response

interface LoginCallBack {

    fun success(status: Boolean, loginResponse : Response<LoginResponseModel>){

    }
    fun failure(status: Boolean, errorCode : Int, message : String){

    }
}