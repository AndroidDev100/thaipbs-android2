package com.vipa.userManagement.callBacks

import me.vipa.bookmarking.bean.BookmarkingResponse
import retrofit2.Response

interface BookmarkingCallback {

    fun success(status: Boolean, loginResponse: Response<BookmarkingResponse>){

    }
    fun failure(status: Boolean, errorCode : Int, message : String){

    }
}