package com.mvhub.watchHistory.callbacks

import com.mvhub.watchHistory.beans.ResponseWatchHistoryAssetList
import retrofit2.Response

interface GetWatchHistoryCallBack {

    fun success(status: Boolean, loginResponse: Response<ResponseWatchHistoryAssetList>){

    }
    fun failure(status: Boolean, errorCode : Int, message : String){

    }
}