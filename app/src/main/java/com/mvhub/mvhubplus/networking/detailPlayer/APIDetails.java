package com.mvhub.mvhubplus.networking.detailPlayer;

import com.mvhub.mvhubplus.beanModel.AssetHistoryContinueWatching.ResponseAssetHistory;
import com.mvhub.mvhubplus.beanModel.addComment.ResponseAddComment;
import com.mvhub.mvhubplus.beanModel.allComments.ResponseAllComments;
import com.mvhub.mvhubplus.beanModel.allWatchList.ResponseAllWatchList;
import com.mvhub.mvhubplus.beanModel.cancelPurchase.ResponseCancelPurchase;
import com.mvhub.mvhubplus.beanModel.deleteComment.ResponseDeleteComment;
import com.mvhub.mvhubplus.beanModel.emptyResponse.ResponseEmpty;
import com.mvhub.mvhubplus.beanModel.isWatchList.ResponseContentInWatchlist;
import com.mvhub.mvhubplus.beanModel.membershipAndPlan.ResponseMembershipAndPlan;
import com.mvhub.mvhubplus.beanModel.purchaseModel.PurchaseResponseModel;
import com.mvhub.mvhubplus.beanModel.responseModels.detailPlayer.ResponseDetailPlayer;
import com.mvhub.mvhubplus.beanModel.responseModels.series.SeriesResponse;
import com.mvhub.mvhubplus.beanModel.responseModels.series.season.SeasonResponse;
import com.mvhub.mvhubplus.beanModel.watchHistory.ResponseWatchHistory;
import com.mvhub.mvhubplus.beanModel.watchList.ResponseWatchList;
import com.mvhub.mvhubplus.beanModel.entitle.ResponseEntitle;
import com.mvhub.mvhubplus.beanModel.isLike.ResponseIsLike;
import com.mvhub.mvhubplus.beanModel.like.ResponseAddLike;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIDetails {

    @Headers("x-platform: android")
    @GET("detail/videoAsset/{Id}")
    Call<ResponseDetailPlayer> getPlayerDetails(@Path("Id") int id);


    @Headers("x-platform: android")
    @GET("homescreen/getYouMayLikeContent")
    Call<SeasonResponse> getYouMayLike(@Query("contentTypeId") int contentTypeId, @Query("pageNo") int pageNo, @Query("length") int length);


    @Headers("x-platform: android")
    @GET("detail/series/{seriesId}")
    Call<SeriesResponse> getSeriesDetails(@Path("seriesId") int seriesId);

    @Headers("x-platform: android")
    @GET("season/vodBySeason")
    Call<SeasonResponse> getSeasonEpisode(@Query("seasonId") int seasonId, @Query("pageNo") int pageNo, @Query("length") int length);


    @Headers("x-platform: android")
    @GET("season/vodBySeason")
    io.reactivex.Observable<SeasonResponse> getSeasonEpisodeMulti(@Query("seasonId") int seasonId, @Query("pageNo") int pageNo, @Query("length") int length);


    @Headers("x-platform: android")
    @GET("season/vodBySeason")
    Call<SeasonResponse> getSeasonEpisodeSingle(@Query("seasonId") int seasonId, @Query("pageNo") int pageNo, @Query("length") int length);


    @Headers("x-platform: android")
    @GET("series/vodBySeries")
    Call<SeasonResponse> getVOD(@Query("seriesId") int seriesId, @Query("pageNo") int pageNo, @Query("length") int length);


    @Headers("x-platform: android")
    @POST("watchList/addToWatchList")
    Call<ResponseWatchList> getAddToWatchList(@Body JsonObject data);

    @Headers("x-platform: android")
    @POST("watchList/isContentPresentOnWatchList")
    Call<ResponseContentInWatchlist> getIsContentWatchList(@Body JsonObject data);


    @Headers("x-platform: android")
    @POST("like/islike")
    Call<ResponseIsLike> getIsLike(@Body JsonObject data);


    @Headers("x-platform: android")
    @POST("like/likes")
    Call<ResponseAddLike> getAddLike(@Body JsonObject data);


    @Headers("x-platform: android")
    @POST("like/delete")
    Call<ResponseEmpty> getUnLike(@Body JsonObject data);


    @Headers("x-platform: android")
    @POST("watchList/removeFromWatchList")
    Call<ResponseEmpty> getRemoveFromWatchList(@Query("watchListItemId") String id);

    @Headers("x-platform: android")
    @GET("watchList/getUsersWatchList")
    Call<ResponseAllWatchList> getAllWatchList(@Query("pageNo") int pageNo, @Query("length") int length);


    @Headers("x-platform: android")
    @GET("watchHistory/getUsersWatchHistory")
    Call<ResponseWatchHistory> getWatchHistory(@Query("pageNo") int pageNo, @Query("length") int length);

    @Headers("x-platform: android")
    @GET("v2/subscription/checkEntitlement")
    Call<ResponseEntitle> checkEntitlement(@Query("vodSKU") String sku);


    @Headers("x-platform: android")
    @POST("comment/listComments")
    Call<ResponseAllComments> getAllComments(@Query("size") String size, @Query("page") int page, @Body JsonObject data);


    @Headers("x-platform: android")
    @POST("comment/addComment")
    Call<ResponseAddComment> getAddComments(@Body JsonObject data);

    @Headers("x-platform: android")
    @POST("comment/delete")
    Call<ResponseDeleteComment> getDeleteComment(@Query("commentId") String commentId);


    @Headers("x-platform: android")
    @POST("order/new")
    Call<PurchaseResponseModel> getCreateNewPurchase(@Body JsonObject data);

    @Headers("x-platform: android")
    @POST("payment/initiate")
    Call<PurchaseResponseModel> initiatePurchase(@Body JsonObject data);

    @POST("v2/content/like/VOD/{assetId}")
    Call<ResponseEmpty> addToLikeVod(@Path("assetId") int asset);

    @Headers("x-platform: android")
    @POST("payment/{paymentId}")
    Call<PurchaseResponseModel> updatePurchase(@Path("paymentId") String paymentId , @Body JsonObject data);

    @Headers("x-platform: android")
    @GET("v2/subscription/getPlans")
    Call<ResponseMembershipAndPlan> getPlans(@Query("subscriptionOfferType") String a, @Query("subscriptionCheckEntitlement") boolean b);

    @Headers("x-platform: android")
    @GET("v2/purchases/cancelPurchase")
    Call<ResponseCancelPurchase> cancelPurchase();


    @Headers("x-platform: android")
    @POST("continueWatching/addBookmark")
    Call<ResponseEmpty> getBookMark(@Body JsonObject assetRequest);


    @POST("continueWatching/assetHistory")
    Call<ResponseAssetHistory> getAssetHistory(@Body JsonObject assetRequest);


}
