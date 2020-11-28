package me.vipa.watchHistory.callbacks

import me.vipa.watchHistory.beans.ResponseWatchHistoryAssetList
import retrofit2.Response

interface GetWatchHistoryCallBack {

    fun success(status: Boolean, loginResponse: Response<ResponseWatchHistoryAssetList>){

    }
    fun failure(status: Boolean, errorCode : Int, message : String){

    }
}