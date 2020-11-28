package me.vipa.app.networking.apiendpoints;

import me.vipa.app.beanModel.AssetHistoryContinueWatching.ResponseAssetHistory;
import me.vipa.app.beanModel.tempData.ResponseContinueList;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIInterFaceContinue {

    @POST("getAssetHistory")
    Call<ResponseAssetHistory> getAssetHistory(@Body JsonObject assetRequest);

    @POST("getAssetHistoryForSeasons")
    Call<ResponseAssetHistory> getAssetHistorySeries(@Body JsonObject assetRequest);


    @POST("getAssetHistory")
    io.reactivex.Observable<ResponseAssetHistory> getAssetHistorySeriesMulti(@Body JsonObject assetRequest);


    @POST("getAssetList")
    Call<ResponseContinueList> getAssetList(@Body JSONObject assetRequest);


}
