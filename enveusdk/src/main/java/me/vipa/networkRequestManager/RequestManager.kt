package me.vipa.networkRequestManager

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.vipa.userManagement.callBacks.BookmarkingCallback
import com.vipa.userManagement.callBacks.GetBookmarkCallback
import com.vipa.userManagement.callBacks.GetContinueWatchingCallback
import com.watcho.enveu.bean.EnveuCategory
import me.vipa.baseClient.BaseConfiguration
import me.vipa.baseCollection.baseCategoryModel.BaseCategory
import me.vipa.baseCollection.baseCategoryModel.ModelGenerator
import me.vipa.bookmarking.bean.BookmarkingResponse
import me.vipa.bookmarking.bean.GetBookmarkResponse
import me.vipa.bookmarking.bean.continuewatching.GetContinueWatchingBean
import me.vipa.callBacks.EnveuCallBacks
import me.vipa.userManagement.bean.LoginResponse.LoginResponseModel
import me.vipa.userManagement.bean.UserProfile.UserProfileResponse
import me.vipa.userManagement.bean.allSecondaryDetails.AllSecondaryDetails
import me.vipa.userManagement.bean.allSecondaryDetails.SecondaryUserDetails
import me.vipa.userManagement.bean.allSecondaryDetails.SwitchUserDetails
import me.vipa.userManagement.callBacks.*
import me.vipa.userManagement.params.UserManagement
import me.vipa.watchHistory.beans.ResponseWatchHistoryAssetList
import me.vipa.watchHistory.callbacks.GetWatchHistoryCallBack
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.Comparator


class RequestManager {

    companion object {
        val instance = RequestManager()
    }

    fun categoryCall(screenId: String, enveuCallBacks: EnveuCallBacks) {
        val endPoint = NetworkSetup().client?.create<EnveuEndpoints>(EnveuEndpoints::class.java)
        val call = endPoint?.categoryService(BaseConfiguration.instance.clients.getDeviceType().toString(), BaseConfiguration.instance.clients.getPlatform().toString(), BaseConfiguration.instance.clients.getApiKey().toString(), screenId)
        call?.enqueue(object : Callback<EnveuCategory> {
            override fun onResponse(call: Call<EnveuCategory>, response: Response<EnveuCategory>) {
                /*val requestMade=response.raw().sentRequestAtMillis();
                val requestReceived=response.raw().receivedResponseAtMillis()
                System.out.println("responsetime : "+(requestReceived - requestMade)+" ms");*/
                val model = ModelGenerator.instance.setGateWay(BaseConfiguration.instance.clients.getGateway()).createModel(response)
                // val sortedModel=getSortedList(model)
                enveuCallBacks.success(true, model)

            }

            override fun onFailure(call: Call<EnveuCategory>, t: Throwable) {
                t.message?.let { enveuCallBacks.failure(false, 0, it) }
                val model = ModelGenerator.instance.setGateWay(BaseConfiguration.instance.clients.getGateway()).createModel(t)
                enveuCallBacks.success(false, model)
            }
        })
    }

    private fun getSortedList(model: List<BaseCategory>): Any {
        return Collections.sort(model, Comparator<BaseCategory?> { o1, o2 -> o1?.displayOrder!!.compareTo(o2?.displayOrder!!) })
    }

    fun loginCall(userName: String, password: String, loginCallBacks: LoginCallBack) {
        val endPoint = NetworkSetup().userMngmtClient?.create<EnveuEndpoints>(EnveuEndpoints::class.java)
        val requestParam = JsonObject()
        requestParam.addProperty(UserManagement.email.name, userName)
        requestParam.addProperty(UserManagement.password.name, password)

        val call = endPoint?.getLogin(requestParam)
        call?.enqueue(object : Callback<LoginResponseModel> {
            override fun onResponse(call: Call<LoginResponseModel>, response: Response<LoginResponseModel>) {
                loginCallBacks.success(true, response)
            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                t.message?.let { loginCallBacks.failure(false, 0, it) }
                loginCallBacks.failure(false, 0, "")
            }
        })
    }

    fun registerCall(userName: String, email: String, password: String, notificationEnable: Boolean, loginCallBacks: LoginCallBack) {
        val endPoint = NetworkSetup().userMngmtClient?.create<EnveuEndpoints>(EnveuEndpoints::class.java)
        val requestParam = JsonObject()
        val customParam = JsonObject()
        customParam.addProperty("NotificationCheck", notificationEnable)
        requestParam.addProperty("name", userName)
        requestParam.addProperty(UserManagement.email.name, email)
        requestParam.addProperty(UserManagement.password.name, password)
        requestParam.add("customData", customParam)

        val call = endPoint?.getSignUp(requestParam)
        call?.enqueue(object : Callback<LoginResponseModel> {
            override fun onResponse(call: Call<LoginResponseModel>, response: Response<LoginResponseModel>) {
                loginCallBacks.success(true, response)
            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                t.message?.let { loginCallBacks.failure(false, 0, it) }
                loginCallBacks.failure(false, 0, "")
            }
        })
    }

    fun bookmarkVideo(token: String, assetId: Int, position: Int, bookmarkingCallback: BookmarkingCallback) {
        val endPoint = NetworkSetup().subscriptionClient(token).create<EnveuEndpoints>(EnveuEndpoints::class.java)
        val requestParam = JsonObject()
        val call = endPoint.bookmarkVideo(assetId, position)
        call.enqueue(object : Callback<BookmarkingResponse> {
            override fun onResponse(call: Call<BookmarkingResponse>, response: Response<BookmarkingResponse>) {
                bookmarkingCallback.success(true, response)
            }

            override fun onFailure(call: Call<BookmarkingResponse>, t: Throwable) {
                t.message?.let { bookmarkingCallback.failure(false, 0, it) }
                bookmarkingCallback.failure(false, 0, "")
            }
        })
    }

    fun getBookmarkByVideoId(token: String, videoId: Int, getBookmarkCallback: GetBookmarkCallback) {
        val endPoint = NetworkSetup().subscriptionClient(token).create<EnveuEndpoints>(EnveuEndpoints::class.java)
        val call = endPoint.getBookmarkByVideoId(videoId)
        call.enqueue(object : Callback<GetBookmarkResponse> {

            override fun onResponse(call: Call<GetBookmarkResponse>, response: Response<GetBookmarkResponse>) {
                getBookmarkCallback.success(true, response)
            }

            override fun onFailure(call: Call<GetBookmarkResponse>, t: Throwable) {
                getBookmarkCallback.failure(false, 0, "")
            }
        })
    }

    fun finishBookmark(token: String, assestId: Int, bookmarkingCallback: BookmarkingCallback) {
        val endPoint = NetworkSetup().subscriptionClient(token).create<EnveuEndpoints>(EnveuEndpoints::class.java)
        val call = endPoint.finishBookmark(assestId)
        call.enqueue(object : Callback<BookmarkingResponse> {
            override fun onResponse(call: Call<BookmarkingResponse>, response: Response<BookmarkingResponse>) {
                bookmarkingCallback.success(true, response)
            }

            override fun onFailure(call: Call<BookmarkingResponse>, t: Throwable) {
                bookmarkingCallback.failure(false, 0, "")
            }
        })
    }

    fun fbLoginCall(params: JsonObject, loginCallBacks: LoginCallBack) {
        val endPoint = NetworkSetup().userMngmtClient?.create<EnveuEndpoints>(EnveuEndpoints::class.java)

        val call = endPoint?.getFbLogin(params)
        call?.enqueue(object : Callback<LoginResponseModel> {
            override fun onResponse(call: Call<LoginResponseModel>, response: Response<LoginResponseModel>) {
                loginCallBacks.success(true, response)
            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                t.message?.let { loginCallBacks.failure(false, 0, it) }
                loginCallBacks.failure(false, 0, "")
            }
        })
    }

    fun fbForceLoginCall(params: JsonObject, loginCallBacks: LoginCallBack) {
        val endPoint = NetworkSetup().userMngmtClient?.create<EnveuEndpoints>(EnveuEndpoints::class.java)

        val call = endPoint?.getForceFbLogin(params)
        call?.enqueue(object : Callback<LoginResponseModel> {
            override fun onResponse(call: Call<LoginResponseModel>, response: Response<LoginResponseModel>) {
                loginCallBacks.success(true, response)
            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                t.message?.let { loginCallBacks.failure(false, 0, it) }
                loginCallBacks.failure(false, 0, "")
            }
        })
    }

    fun changePasswordCall(params: JsonObject, token: String, loginCallBacks: LoginCallBack) {
        val endPoint = NetworkSetup().subscriptionClient(token).create<EnveuEndpoints>(EnveuEndpoints::class.java)

        val call = endPoint.getChangePassword(params)
        call?.enqueue(object : Callback<LoginResponseModel> {
            override fun onResponse(call: Call<LoginResponseModel>, response: Response<LoginResponseModel>) {
                loginCallBacks.success(true, response)
            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                t.message?.let { loginCallBacks.failure(false, 0, it) }
                loginCallBacks.failure(false, 0, "")
            }
        })
    }

    fun userProfileCall(loginCallBacks: UserProfileCallBack, token: String) {
        val endPoint = NetworkSetup().subscriptionClient(token).create<EnveuEndpoints>(EnveuEndpoints::class.java)

        val call = endPoint.getUserProfile()
        call.enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                loginCallBacks.success(true, response)
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                t.message?.let { loginCallBacks.failure(false, 0, it) }
                loginCallBacks.failure(false, 0, "")
            }
        })
    }

    fun userUpdateProfileCall(loginCallBacks: UserProfileCallBack, token: String, name: String, mobile: String, spinnerValue: String, dob: String, address: String, imageUrl: String, via: String, contentPreference: String, notificationEnable: Boolean) {
        val endPoint = NetworkSetup().subscriptionClient(token).create<EnveuEndpoints>(EnveuEndpoints::class.java)
        val requestParam = JsonObject()
        val customParam = JsonObject()
        try {
//            customParam.addProperty("NotificationCheck", notificationEnable)
            if (!contentPreference.equals("")){
                customParam.addProperty("contentPreferences", contentPreference)
            }
            customParam.addProperty("address", address)
            if (via.equals("Avatar")) {
                customParam.addProperty("profileAvatar", imageUrl)
            }else if (via.equals("Gallery")){
                requestParam.addProperty("profilePicURL", imageUrl)
            }
            requestParam.addProperty("name", name)
            if(!mobile.equals("")) {
                requestParam.addProperty("phoneNumber", mobile)
            }
            if (!spinnerValue.equals("")) {
                requestParam.addProperty("gender", spinnerValue.toUpperCase())
            }
            requestParam.addProperty("dateOfBirth", dob)
//        requestParam.addProperty("address", address)
//        requestParam.addProperty("profilePicURL", imageUrl)
            requestParam.add("customData", customParam)
        } catch (e: JSONException) {

        }

        val call = endPoint.getUserUpdateProfile(requestParam)
        call?.enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                loginCallBacks.success(true, response)
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                t.message?.let { loginCallBacks.failure(false, 0, it) }
                loginCallBacks.failure(false, 0, "")
            }
        })
    }


    fun logoutCall(token: String, loginCallBacks: LogoutCallBack) {
        val endPoint = NetworkSetup().subscriptionClient(token).create<EnveuEndpoints>(EnveuEndpoints::class.java)

        val call = endPoint.getLogout()
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                loginCallBacks.success(true, response)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                t.message?.let { loginCallBacks.failure(false, 0, it) }
                loginCallBacks.failure(false, 0, "")
            }
        })
    }

    fun getContinueWatchingData(token: String, pageNumber: Int, pageSize: Int, getBookmarkCallback: GetContinueWatchingCallback) {
        val endPoint = NetworkSetup().subscriptionClient(token).create<EnveuEndpoints>(EnveuEndpoints::class.java)
        val call = endPoint.getAllBookmarks(pageNumber, pageSize)
        call.enqueue(object : Callback<GetContinueWatchingBean> {
            override fun onResponse(call: Call<GetContinueWatchingBean>, response: Response<GetContinueWatchingBean>) {
                getBookmarkCallback.success(true, response)
            }

            override fun onFailure(call: Call<GetContinueWatchingBean>, t: Throwable) {
                getBookmarkCallback.failure(false, 0, "")
            }
        })
    }

    fun getWatchHistory(token: String, pageNumber: Int, pageSize: Int, getWatchHistoryCallBack: GetWatchHistoryCallBack) {
        val endPoint = NetworkSetup().subscriptionClient(token).create<EnveuEndpoints>(EnveuEndpoints::class.java)
        val call = endPoint.getWatchHistoryList(pageNumber, pageSize)
        call.enqueue(object : Callback<ResponseWatchHistoryAssetList> {
            override fun onResponse(call: Call<ResponseWatchHistoryAssetList>, response: Response<ResponseWatchHistoryAssetList>) {
                getWatchHistoryCallBack.success(true, response)
            }

            override fun onFailure(call: Call<ResponseWatchHistoryAssetList>, t: Throwable) {
                getWatchHistoryCallBack.failure(false, 0, "")
            }
        })
    }

    fun addToWatchHistory(token: String, assestId: Int, bookmarkingCallback: BookmarkingCallback) {
        val endPoint = NetworkSetup().subscriptionClient(token).create<EnveuEndpoints>(EnveuEndpoints::class.java)
        val call = endPoint.addToWatchHistory(assestId)
        call.enqueue(object : Callback<BookmarkingResponse> {
            override fun onResponse(call: Call<BookmarkingResponse>, response: Response<BookmarkingResponse>) {
                bookmarkingCallback.success(true, response)
            }

            override fun onFailure(call: Call<BookmarkingResponse>, t: Throwable) {
                t.message?.let { bookmarkingCallback.failure(false, 0, it) }
                bookmarkingCallback.failure(false, 0, "")
            }
        })
    }

    fun deleteFromWatchHistory(token: String, assetId: Int, bookmarkingCallback: BookmarkingCallback) {
        val endPoint = NetworkSetup().subscriptionClient(token).create<EnveuEndpoints>(EnveuEndpoints::class.java)
        val call = endPoint.deleteFromWatchHistory(assetId)
        call.enqueue(object : Callback<BookmarkingResponse> {
            override fun onResponse(call: Call<BookmarkingResponse>, response: Response<BookmarkingResponse>) {
                bookmarkingCallback.success(true, response)
            }

            override fun onFailure(call: Call<BookmarkingResponse>, t: Throwable) {
                t.message?.let { bookmarkingCallback.failure(false, 0, it) }
                bookmarkingCallback.failure(false, 0, "")
            }
        })
    }

    fun getWatchListData(token: String, pageNumber: Int, pageSize: Int, getWatchHistoryCallBack: GetWatchHistoryCallBack) {
        val endPoint = NetworkSetup().subscriptionClient(token).create<EnveuEndpoints>(EnveuEndpoints::class.java)
        val call = endPoint.getWatchListData(pageNumber, pageSize)
        call.enqueue(object : Callback<ResponseWatchHistoryAssetList> {
            override fun onResponse(call: Call<ResponseWatchHistoryAssetList>, response: Response<ResponseWatchHistoryAssetList>) {
                getWatchHistoryCallBack.success(true, response)
            }

            override fun onFailure(call: Call<ResponseWatchHistoryAssetList>, t: Throwable) {
                getWatchHistoryCallBack.failure(false, 0, "")
            }
        })
    }

    fun forgotPasswordCall(screenId: String, enveuCallBacks: ForgotPasswordCallBack) {
        val endPoint = NetworkSetup().userMngmtClient?.create<EnveuEndpoints>(EnveuEndpoints::class.java)
        val call = endPoint?.getForgotPassword(screenId)
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                enveuCallBacks.success(true, response)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                enveuCallBacks.failure(false, 0, "")
            }
        })
    }


    fun secondaryUsersCall(allListCallBack: AllListCallBack,token: String) {
        val endPoint = NetworkSetup().kidsModeClientAllUsers(token).create<EnveuEndpoints>(EnveuEndpoints::class.java)
        val call = endPoint?.getKidsModeUsers();
        call?.enqueue(object : Callback<AllSecondaryDetails> {
            override fun onResponse(call: Call<AllSecondaryDetails>, response: Response<AllSecondaryDetails>) {
                allListCallBack.success(true, response)
            }

            override fun onFailure(call: Call<AllSecondaryDetails>, t: Throwable) {
                t.message?.let { allListCallBack.failure(false, 0, it) }
                allListCallBack.failure(false, 0, "")
            }
        })
    }

    fun addSecondaryUsersCall(allListCallBack: SecondaryUserCallBack,token: String) {
        val endPoint = NetworkSetup().kidsModeSecondaryUsers(token).create<EnveuEndpoints>(EnveuEndpoints::class.java)
        val requestParam = JsonObject()
        requestParam.addProperty("name", "Profile 1")
        requestParam.addProperty("kidsAccount", true)


        var gson = Gson()
        //Convert the Json object to JsonString
        var jsonString:String = gson.toJson(requestParam)

        Log.e("DATA", jsonString);
        val call = endPoint?.getKidsSecondaryUser(requestParam);
        call?.enqueue(object : Callback<SecondaryUserDetails> {
            override fun onResponse(call: Call<SecondaryUserDetails>, response: Response<SecondaryUserDetails>) {
                allListCallBack.success(true, response)
            }

            override fun onFailure(call: Call<SecondaryUserDetails>, t: Throwable) {
                t.message?.let { allListCallBack.failure(false, 0, it) }
                allListCallBack.failure(false, 0, "")
            }
        })
    }
    fun switchUsersCall(allListCallBack: SwitchUserCallBack,token: String,id:String) {
        val endPoint = NetworkSetup().kidsModeSecondaryUsers(token).create<EnveuEndpoints>(EnveuEndpoints::class.java)

        val call = endPoint?.getKidsSwitchUser(id)
        call?.enqueue(object : Callback<SwitchUserDetails> {
            override fun onResponse(call: Call<SwitchUserDetails>, response: Response<SwitchUserDetails>) {
                allListCallBack.success(true, response)
            }

            override fun onFailure(call: Call<SwitchUserDetails>, t: Throwable) {
                t.message?.let { allListCallBack.failure(false, 0, it) }
                allListCallBack.failure(false, 0, "")
            }
        })
    }
}