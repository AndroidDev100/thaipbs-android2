package me.vipa.networkRequestManager


import me.vipa.bookmarking.bean.BookmarkingResponse
import me.vipa.bookmarking.bean.GetBookmarkResponse
import me.vipa.bookmarking.bean.continuewatching.GetContinueWatchingBean
import me.vipa.userManagement.bean.LoginResponse.LoginResponseModel
import me.vipa.userManagement.bean.UserProfile.UserProfileResponse
import me.vipa.watchHistory.beans.ResponseWatchHistoryAssetList
import com.google.gson.JsonObject
import com.watcho.enveu.bean.EnveuCategory
import me.vipa.userManagement.bean.allSecondaryDetails.AllSecondaryDetails
import retrofit2.Call
import retrofit2.http.*

interface EnveuEndpoints {

    @GET("v4/user/secondaryAccount/listAll")
    fun getKidsModeUsers(): Call<AllSecondaryDetails>

    @GET("screen?")
    fun categoryService(@Header("x-device") device: String, @Header("x-platform") platform: String, @Header("x-api-key") key: String, @Query("screenId") screenId: String): Call<EnveuCategory>

    @Headers("x-platform: android")
    @POST("v3/user/login/manual")
    fun getLogin(@Body apiLogin: JsonObject): Call<LoginResponseModel>

    @Headers("x-platform: android")
    @POST("v3/user/register/manual")
    fun getSignUp(@Body apiSignUp: JsonObject): Call<LoginResponseModel>


    @Headers("x-platform: android")
    @POST("v3/user/login/fb")
    fun getFbLogin(@Body user: JsonObject): Call<LoginResponseModel>

    @Headers("x-platform: android")
    @POST("v3/user/login/fb/force")
    fun getForceFbLogin(@Body user: JsonObject): Call<LoginResponseModel>

    @Headers("x-platform: android")
    @POST("v3/user/logout/false")
    fun getLogout(): Call<JsonObject>

    @Headers("x-platform: android")
    @POST("v3/user/changePassword")
    fun getChangePassword(@Body user: JsonObject): Call<LoginResponseModel>

    @Headers("x-platform: android")
    @POST("v2/content/continueWatching/bookmark/{assetId}/{position}")
    fun bookmarkVideo(@Path("assetId") assetId: Int, @Path("position") position: Int): Call<BookmarkingResponse>

    @Headers("x-platform: android")
    @GET("v2/content/continueWatching/bookmark/{assetId}")
    fun getBookmarkByVideoId(@Path("assetId") videoId: Int): Call<GetBookmarkResponse>

    @Headers("x-platform: android")
    @POST("v2/content/continueWatching/finishedWatching/{assetId}")
    fun finishBookmark(@Path("assetId") assestId: Int): Call<BookmarkingResponse>

    @Headers("x-platform: android")
    @GET("v2/content/continueWatching/bookmarks")
    fun getAllBookmarks(@Query("page") pageNumber: Int, @Query("size") pageSize: Int): Call<GetContinueWatchingBean>

    @Headers("x-platform: android")
    @GET("v3/user/profile")
    fun getUserProfile(): Call<UserProfileResponse>

    @Headers("x-platform: android")
    @PATCH("v3/user/profile/update")
    fun getUserUpdateProfile(@Body user: JsonObject): Call<UserProfileResponse>

    @Headers("x-platform: android")
    @GET("v2/user/watchHistory/get")
    fun getWatchHistoryList(@Query("page") page: Int, @Query("size") size: Int): Call<ResponseWatchHistoryAssetList>

    @Headers("x-platform: android")
    @POST("v2/user/watchHistory/add/VOD/{assetId}")
    fun addToWatchHistory(@Path("assetId") assestId: Int): Call<BookmarkingResponse>

    @Headers("x-platform: android")
    @DELETE("v2/user/watchHistory/delete/{assetId}")
    fun deleteFromWatchHistory(@Path("assetId") assetId: Int): Call<BookmarkingResponse>

    @Headers("x-platform: android")
    @GET("v2/user/watchlist/get")
    fun getWatchListData(@Query("page") pageNumber: Int, @Query("size") pageSize: Int): Call<ResponseWatchHistoryAssetList>

    @GET("v3/user/forgotPasswordRequest")
    fun getForgotPassword(@Query("email") param1: String): Call<JsonObject>

}