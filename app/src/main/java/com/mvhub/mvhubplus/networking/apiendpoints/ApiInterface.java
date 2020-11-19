package com.mvhub.mvhubplus.networking.apiendpoints;


import com.mvhub.mvhubplus.beanModel.AssetHistoryContinueWatching.ResponseAssetHistory;
import com.mvhub.mvhubplus.beanModel.changePassword.ResponseChangePassword;
import com.mvhub.mvhubplus.beanModel.configBean.ResponseConfig;
import com.mvhub.mvhubplus.beanModel.connectFb.ResponseConnectFb;
import com.mvhub.mvhubplus.beanModel.emptyResponse.ResponseEmpty;
import com.mvhub.mvhubplus.beanModel.enveuCommonRailData.config.EnveuConfigResponse;
import com.mvhub.mvhubplus.beanModel.mobileAds.ResponseMobileAds;
import com.mvhub.mvhubplus.beanModel.popularSearch.ResponsePopularSearch;
import com.mvhub.mvhubplus.beanModel.responseGetWatchlist.ResponseGetIsWatchlist;
import com.mvhub.mvhubplus.beanModel.responseIsLike.ResponseIsLike;
import com.mvhub.mvhubplus.beanModel.responseModels.LoginResponse.LoginResponseModel;
import com.mvhub.mvhubplus.beanModel.responseModels.RegisterSignUpModels.ResponseRegisteredSignup;
import com.mvhub.mvhubplus.beanModel.responseModels.SignUp.SignUpResponseModel;
import com.mvhub.mvhubplus.beanModel.responseModels.landingTabResponses.playlistResponse.PlaylistResponses;
import com.mvhub.mvhubplus.beanModel.responseModels.landingTabResponses.railData.PlaylistRailData;
import com.mvhub.mvhubplus.beanModelV3.continueWatching.ContinueWatchingModel;
import com.mvhub.mvhubplus.beanModelV3.playListModelV2.EnveuCommonResponse;
import com.mvhub.mvhubplus.beanModelV3.searchV2.ResponseSearch;
import com.mvhub.mvhubplus.beanModelV3.videoDetailsV2.EnveuVideoDetailsBean;
import com.mvhub.mvhubplus.redeemcoupon.RedeemCouponResponseModel;
import com.mvhub.mvhubplus.userAssetList.ResponseUserAssetList;
import com.google.gson.JsonObject;
import com.mvhub.mvhubplus.utils.config.bean.ConfigBean;
import com.mvhub.mvhubplus.utils.recoSense.bean.RecosenceResponse;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.requery.PostDelete;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @Headers("x-platform: android")
    @GET("config")
    Call<ResponseConfig> getConfiguration(@Query("getLatest") String param1);

    @Headers("x-platform: android")
    @POST("user/login/manual")
    Call<LoginResponseModel> getLogin(@Body JsonObject apiLogin);

    @Headers("x-platform: android")
    @POST("user/register/manual")
    Call<SignUpResponseModel> getSignUp(@Body JsonObject apiSignUp);

    @Headers("x-platform: android")
    @PATCH("user/update/profile")
    Call<ResponseRegisteredSignup> getRegistrationStep(@Body JsonObject user);

    @GET("content/listAll")
    Call<EnveuCommonResponse> getLinkedContentList(@Query("linkedContentId") String seriesId, @Query("seasonNumber") int seasonNumber, @Query("page") int page, @Query("size") int size);

    @GET("user/forgotPassword")
    Call<JsonObject> getForgotPassword(@Query("email") String param1);

    @Headers("x-platform: android")
    @POST("user/changePassword")
    Call<ResponseChangePassword> getChangePassword(@Body JsonObject user);

    @Headers("x-platform: android")
    @POST("user/login/fb")
    Call<LoginResponseModel> getFbLogin(@Body JsonObject user);

    @Headers("x-platform: android")
    @POST("user/connectToFb")
    Call<ResponseConnectFb> getConnectFb(@Body JsonObject user);

    @Headers("x-platform: android")
    @POST("user/login/fb/force")
    Call<LoginResponseModel> getForceFbLogin(@Body JsonObject user);

    @Headers("x-platform: android")
    @GET("user/logout")
    Call<JsonObject> getLogout(@Query("removeAllSession") boolean param1);

    @Headers("x-platform: android")
    @POST("user/logout/false")
    Call<JsonObject> getUserLogout();


    @Headers("x-platform: android")
    @GET("user/sendVerificationEmail")
    Call<ResponseEmpty> getVerifyUser();


    @Headers("x-platform: android")
    @GET("popularSearch")
    Call<ResponsePopularSearch> getPopularSearch(@Query("size") int size, @Query("page") int page);

    @Headers("x-platform: android")
    @GET("searchSeries")
    Call<ResponsePopularSearch> getSearchKeyword(@Query("query") String keyword, @Query("size") int size, @Query("page") int page);

    @Headers("x-platform: android")
    @GET("homescreen/getConfig")
    Call<PlaylistResponses> getPlaylists(@Query("id") int Id);

    @Headers("x-platform: android")
    @GET("homescreen/getBaseCategories")
    Call<PlaylistRailData> getPlaylistsData(@Query("id") int id, @Query("pageNo") int pageNo, @Query("length") int length);


    @Headers("x-platform: android")
    @GET("homescreen/getBaseCategories")
    Observable<PlaylistRailData> getPlaylistsDataHome(@Query("id") int Id, @Query("pageNo") int pageNo, @Query("length") int length);


    @Headers("x-platform: android")
    @GET("ad/fetchAds")
    Call<ResponseMobileAds> getAdsData(@Query("navigationScreenId") int Id, @Query("platform") String platform);


    @POST("continueWatching/assetHistory")
    Call<ResponseAssetHistory> getAssetHistory(@Body JsonObject assetRequest);

    @POST("continueWatching/userAssetList")
    Call<ResponseUserAssetList> getAssetList(@Body JsonObject assetRequest);


    @GET("config")
    Call<EnveuConfigResponse> getConfig();


    //V2 changes applied in below APIs-->> Versioning moved to endpoints
    @GET("v3/playlist")
    Call<EnveuCommonResponse> getPlaylistDetailsById(@Query("playlistId") String playListId, @Query("locale") String locale, @Query("page") int pageNumber, @Query("size") int pageSize);

    //V2 PI for getting asset details
    @GET("v3/content")
    Call<EnveuVideoDetailsBean> getVideoDetails(@Query("contentId") String manualImageAssetId, @Query("locale") String locale);

    // v2 API for getting related content -->> all the season episodes
    @GET("v3/content/listAll")
    Call<EnveuCommonResponse> getRelatedContent(@Query("linkedContentId") int seriesId, @Query("seasonNumber") int seasonNumber, @Query("page") int pageNumber, @Query("size") int pageSize, @Query("locale") String locale);

    // v2 API for getting related content -->> all the episodes
    @GET("v3/content/listAll")
    Call<EnveuCommonResponse> getRelatedContentWithoutSNo(@Query("linkedContentId") int seriesId, @Query("page") int pageNumber, @Query("size") int pageSize, @Query("locale") String locale);

    //V2 PI for getting asset details
    @GET("v3/content")
    Call<ContinueWatchingModel> getVideos(@Query("contentId") String manualImageAssetId, @Query("locale") String locale);

    @Headers("x-platform: android")
    @GET("v3/search")
    io.reactivex.Observable<ResponseSearch> getSearch(@Query("keyword") String keyword, @Query("contentType") String type, @Query("size") int size, @Query("offset") int page, @Query("locale") String locale);

    @Headers("x-platform: android")
    @GET("v3/search")
    Call<ResponseSearch> getSearchResults(@Query("keyword") String keyword, @Query("contentType") String type, @Query("size") int size, @Query("offset") int page, @Query("locale") String locale);


    @DELETE("v2/content/like/delete/{assetId}")
    Call<ResponseEmpty> deleteLike(@Path("assetId") int asset);

    @DELETE("v2/user/watchlist/delete/{assetId}")
    Call<ResponseEmpty> removeWatchlist(@Path("assetId") int asset);

    @GET("v2/user/watchlist/get/VOD/{assetId}")
    Call<ResponseGetIsWatchlist> getIsWatchList(@Path("assetId") int asset);


    @POST("v2/user/watchlist/add/VOD/{assetId}")
    Call<ResponseEmpty> addToWatchList(@Path("assetId") int asset);

    @POST("v2/user/watchHistory/add/VOD")
    Call<ResponseEmpty> addToWatchHistory(@Query("") int asset);


    @POST("v2/content/like/VOD/{assetId}")
    Call<ResponseEmpty> addToLikeVod(@Path("assetId") int asset);

    @GET("v2/content/like/VOD/{assetId}")
    Call<ResponseIsLike> getIsLikeVod(@Path("assetId") int asset);

    @GET("getConfig")
    Call<ConfigBean> getConfig(@Query("version") int manualImageAssetId);

    @POST("webhooks")
    Call<RecosenceResponse> getRecoClick(@Body JSONObject assetRequest);

    @POST("coupon/redeemCoupon")
    Call<RedeemCouponResponseModel> redeemCoupon(@Query("redeemCode") String redeemCode);
}

