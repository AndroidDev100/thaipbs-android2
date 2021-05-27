package me.vipa.app.repository.userManagement;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import me.vipa.app.beanModel.responseModels.listAllAccounts.AllSecondaryAccountDetails;
import me.vipa.app.beanModel.responseModels.secondaryUserDetails.SecondaryUserDetailsJavaPojo;
import me.vipa.app.beanModel.userProfile.UserProfileResponse;
import me.vipa.baseCollection.baseCategoryServices.BaseCategoryServices;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.userManagement.bean.allSecondaryDetails.AllSecondaryDetails;
import me.vipa.userManagement.bean.allSecondaryDetails.SecondaryUserDetails;
import me.vipa.userManagement.callBacks.AllListCallBack;
import me.vipa.userManagement.callBacks.ForgotPasswordCallBack;
import me.vipa.userManagement.callBacks.LoginCallBack;
import me.vipa.userManagement.callBacks.SecondaryUserCallBack;
import me.vipa.userManagement.callBacks.UserProfileCallBack;
import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import me.vipa.app.beanModel.connectFb.ResponseConnectFb;
import me.vipa.app.beanModel.forgotPassword.CommonResponse;
import me.vipa.app.beanModel.requestParamModel.RequestParamRegisterUser;
import me.vipa.app.beanModel.responseModels.LoginResponse.LoginResponseModel;
import me.vipa.app.beanModel.responseModels.RegisterSignUpModels.ResponseRegisteredSignup;
import me.vipa.app.beanModel.responseModels.SignUp.SignupResponseAccessToken;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.networking.intercepter.ErrorCodesIntercepter;
import me.vipa.app.MvHubPlusApplication;
import me.vipa.app.R;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.Logger;

import me.vipa.app.utils.helpers.StringUtils;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegistrationLoginRepository {


    private static RegistrationLoginRepository instance;

    private RegistrationLoginRepository() {
    }

    public static RegistrationLoginRepository getInstance() {
        if (instance == null) {
            instance = new RegistrationLoginRepository();
        }
        if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Thai") || KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("हिंदी")) {
            AppCommonMethod.updateLanguage("th", MvHubPlusApplication.getInstance());
        } else if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")) {
            AppCommonMethod.updateLanguage("en", MvHubPlusApplication.getInstance());
        }
        return (instance);
    }


    public LiveData<LoginResponseModel> getLoginAPIResponse(Context context, String username, String pwd) {
        final MutableLiveData<LoginResponseModel> responseApi;
        responseApi = new MutableLiveData<>();
        final JsonObject requestParam = new JsonObject();
        requestParam.addProperty(AppConstants.API_PARAM_EMAIL, username);
        requestParam.addProperty(AppConstants.API_PARAM_PASSWORD, pwd);

        BaseCategoryServices.Companion.getInstance().loginService(username, pwd, new LoginCallBack() {
            @Override
            public void success(boolean status, Response<me.vipa.userManagement.bean.LoginResponse.LoginResponseModel> response) {
                if (status) {
                    LoginResponseModel cl;
                    if (response.body() != null) {
                        String token = response.headers().get("x-auth");
                        KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
                        preference.setAppPrefAccessToken(token);
                        Gson gson = new Gson();
                        String tmp = gson.toJson(response.body());
                        LoginResponseModel loginItemBean = gson.fromJson(tmp, LoginResponseModel.class);
                        responseApi.postValue(loginItemBean);
                    } else {
                        LoginResponseModel responseModel = ErrorCodesIntercepter.getInstance().Login(response);
                        responseApi.postValue(responseModel);
                    }
                }
            }

            @Override
            public void failure(boolean status, int errorCode, String errorMessage) {
                Logger.e("", "LoginResponseToUI E" + errorMessage);
                LoginResponseModel cl = new LoginResponseModel();
                cl.setStatus(false);
                responseApi.postValue(cl);
            }
        });

        return responseApi;
    }


    public LiveData<SignupResponseAccessToken> getSignupAPIResponse(String name, String email, String pwd, boolean isNotificationEnable) {

        final MutableLiveData<SignupResponseAccessToken> responseApi;
        {
            responseApi = new MutableLiveData<>();

            ApiInterface endpoint = RequestConfig.getClient().create(ApiInterface.class);
            final JsonObject requestParam = new JsonObject();
            requestParam.addProperty(AppConstants.API_PARAM_NAME, "3221312@#@#@##!#!");
            requestParam.addProperty(AppConstants.API_PARAM_EMAIL, "");
            requestParam.addProperty(AppConstants.API_PARAM_PASSWORD, pwd);

            BaseCategoryServices.Companion.getInstance().registerService(name, email, pwd,isNotificationEnable, new LoginCallBack() {
                @Override
                public void success(boolean status, Response<me.vipa.userManagement.bean.LoginResponse.LoginResponseModel> response) {
                    if (status) {
                        try {
                            if (response != null && response.errorBody() == null) {
                                Log.w("responseValue", response.code() + "");
                            }

                        } catch (Exception e) {

                        }

                        if (response.code() == 200  && response.body() != null) {
                            Gson gson = new Gson();
                            String tmp = gson.toJson(response.body());
                            LoginResponseModel cl = gson.fromJson(tmp, LoginResponseModel.class);
                            cl.setResponseCode(200);
                            String token = response.headers().get("x-auth");
                            SignupResponseAccessToken responseModel = new SignupResponseAccessToken();
                            responseModel.setAccessToken(token);
                            responseModel.setResponseModel(cl);
                            Logger.e("manual", "nNontonToken" + token);

                            responseApi.postValue(responseModel);
                            Logger.e("", "REsponse" + response.body());
                        } else {
                            SignupResponseAccessToken responseModel = ErrorCodesIntercepter.getInstance().manualSignUp(response);
                            responseApi.postValue(responseModel);
                        }
                    }
                }

                @Override
                public void failure(boolean status, int errorCode, String errorMessage) {
                    Logger.e("", "LoginResponseToUI E" + errorMessage);
                    LoginResponseModel cl = new LoginResponseModel();
                    cl.setResponseCode(400);
                    SignupResponseAccessToken responseModel = new SignupResponseAccessToken();
                    responseModel.setResponseModel(cl);
                    if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")) {
                        responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.server_error));
                    } else {
                        responseModel.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.server_error));
                    }
                    responseApi.postValue(responseModel);
                }
            });
        }

        return responseApi;
    }


    public LiveData<ResponseRegisteredSignup> getSignupAPIResponse(Context context, RequestParamRegisterUser userDetails) {

        final MutableLiveData<ResponseRegisteredSignup> responseApi;
        responseApi = new MutableLiveData<>();
        boolean check;
        KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
        Logger.e("Token", "nNontonToken" + userDetails.getAccessToken());

        ApiInterface endpoint = RequestConfig.getClientInterceptor(userDetails.getAccessToken()).create(ApiInterface.class);
        final JsonObject requestParam = new JsonObject();
        requestParam.addProperty(AppConstants.API_PARAM_NAME, userDetails.getName());
        requestParam.addProperty(AppConstants.API_PARAM_IS_VERIFIED, userDetails.isVerified());
        requestParam.addProperty(AppConstants.API_PARAM_VERIFICATION_DATE, userDetails.getVerificationDate());

        if (StringUtils.isNullOrEmptyOrZero(userDetails.getProfilePicURL())) {
            requestParam.add(AppConstants.API_PARAM_PROFILE_PIC, JsonNull.INSTANCE);

        } else {
            requestParam.addProperty(AppConstants.API_PARAM_PROFILE_PIC, userDetails.getProfilePicURL());
        }
        check = preference.getAppPrefDOB();
        if (check)
            requestParam.add(AppConstants.API_PARAM_DOB, JsonNull.INSTANCE);
        else
            requestParam.addProperty(AppConstants.API_PARAM_DOB, userDetails.getDateOfBirth());

        check = preference.getAppPrefHasNumberEmpty();
        if (check)
            requestParam.add(AppConstants.API_PARAM_PHONE_NUMBER, JsonNull.INSTANCE);
        else
            requestParam.addProperty(AppConstants.API_PARAM_PHONE_NUMBER, String.valueOf(userDetails.getPhoneNumber()));

        requestParam.addProperty(AppConstants.API_PARAM_STATUS, userDetails.getStatus());
        requestParam.addProperty(AppConstants.API_PARAM_EXPIRY_DATE, userDetails.getExpiryDate());
        requestParam.addProperty(AppConstants.API_PARAM_GENDER, userDetails.getGender());
        requestParam.addProperty(AppConstants.API_PARAM_PROFILE_STEP, "STEP_2");

        Call<ResponseRegisteredSignup> call = endpoint.getRegistrationStep(requestParam);
        call.enqueue(new Callback<ResponseRegisteredSignup>() {
            @Override
            public void onResponse(@NonNull Call<ResponseRegisteredSignup> call, @NonNull Response<ResponseRegisteredSignup> response) {
                // SignUpResponseModel cl = response.body();

                if (response.code() == 200) {
                    ResponseRegisteredSignup temp = response.body();
                    temp.setStatus(true);
                    temp.setResponseCode(response.code());
                    responseApi.postValue(response.body());
                } else if (response.code() == 401) {
                    ResponseRegisteredSignup temp = new ResponseRegisteredSignup();
                    temp.setResponseCode(response.code());
                    temp.setStatus(false);
                    responseApi.postValue(temp);
                } else {
                    ResponseRegisteredSignup temp = new ResponseRegisteredSignup();
                    temp.setResponseCode(Objects.requireNonNull(response.code()));
                    temp.setStatus(false);
                    responseApi.postValue(temp);

                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseRegisteredSignup> call, @NonNull Throwable t) {
                Logger.e("error", "REsponse" + call);
                try {
                    if (call.execute().body() != null)
                        responseApi.postValue(call.execute().body());
                    else {

                        ResponseRegisteredSignup temp = new ResponseRegisteredSignup();
                        temp.setStatus(false);
                        temp.setResponseCode(500);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        return responseApi;
    }


    public LiveData<CommonResponse> getForgotPasswordAPIResponse(String email) {
        final MutableLiveData<CommonResponse> responseApi;
        {
            CommonResponse commonResponse = new CommonResponse();
            responseApi = new MutableLiveData<>();

            BaseCategoryServices.Companion.getInstance().forgotPasswordService(email, new ForgotPasswordCallBack() {
                @Override
                public void success(boolean status, Response<JsonObject> response) {

                    if (response.code() == 200) {
                        commonResponse.setCode(response.code());
                        responseApi.postValue(commonResponse);
                    } else {
                        String debugMessage = "";
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            debugMessage = jObjError.getString("debugMessage");
                            int errorcode = jObjError.getInt("responseCode");
                            if (errorcode==4401){
                                commonResponse.setDebugMessage(MvHubPlusApplication.getInstance().getString(R.string.user_does_not_exists));
                                commonResponse.setCode(response.code());
                            }else {
                                commonResponse.setDebugMessage(debugMessage);
                                commonResponse.setCode(response.code());
                            }
                            Logger.e("", "" + jObjError.getString("debugMessage"));
                        } catch (Exception e) {
                            Logger.e("RegistrationLoginRepo", "" + e.toString());
                        }


                        responseApi.postValue(commonResponse);
                    }


                }

                @Override
                public void failure(boolean status, int errorCode, String message) {
                    commonResponse.setDebugMessage("");
                    commonResponse.setCode(500);
                    responseApi.postValue(commonResponse);
                }
            });

        }

        return responseApi;
    }


    public LiveData<LoginResponseModel> getChangePwdAPIResponse(String pwd, String token, Context context) {
        final MutableLiveData<LoginResponseModel> responseApi;
        responseApi = new MutableLiveData<>();
        final JsonObject requestParam = new JsonObject();
        requestParam.addProperty(AppConstants.API_PARAM_NEW_PWD, pwd);

        BaseCategoryServices.Companion.getInstance().changePasswordService(requestParam, token, new LoginCallBack() {
            @Override
            public void success(boolean status, Response<me.vipa.userManagement.bean.LoginResponse.LoginResponseModel> response) {
                if (status) {
                    LoginResponseModel cl;
                    if (response.code() == 500) {
                        cl = new LoginResponseModel();
                        cl.setResponseCode(Objects.requireNonNull(response.code()));
                        responseApi.postValue(cl);

                    } else if (response.code() == 401 || response.code() == 404) {
                        cl = new LoginResponseModel();
                        cl.setResponseCode(response.code());
                        String debugMessage = "";
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            debugMessage = jObjError.getString("debugMessage");
                            Logger.e("", "" + jObjError.getString("debugMessage"));
                        } catch (Exception e) {
                            Logger.e("RegistrationLoginRepo", "" + e.toString());
                        }
                        cl.setDebugMessage(debugMessage);

                        responseApi.postValue(cl);
                    } else if (response.code() == 403) {
                        cl = new LoginResponseModel();
                        cl.setResponseCode(response.code());
                        String debugMessage = "";
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            debugMessage = jObjError.getString("debugMessage");
                            Logger.e("", "" + jObjError.getString("debugMessage"));
                        } catch (Exception e) {
                            Logger.e("RegistrationLoginRepo", "" + e.toString());
                        }
                        if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")) {
                            cl.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.username_must_be_loggedin));
                        } else {
                            cl.setDebugMessage(MvHubPlusApplication.getInstance().getResources().getString(R.string.username_must_be_loggedin));
                        }

                        responseApi.postValue(cl);
                    } else if (response.body() != null ) {
                        Logger.e("", "LoginResponseModel" + response.body());
                        String token = response.headers().get("x-auth");
                        KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
                        preference.setAppPrefAccessToken(token);
                        Gson gson = new Gson();
                        String tmp = gson.toJson(response.body());
                        LoginResponseModel loginItemBean = gson.fromJson(tmp, LoginResponseModel.class);
                        responseApi.postValue(loginItemBean);
                    }
                }
            }

            @Override
            public void failure(boolean status, int errorCode, String errorMessage) {
                Logger.e("", "LoginResponseToUI E" + errorMessage);
                LoginResponseModel cl = new LoginResponseModel();
                cl.setStatus(false);
                responseApi.postValue(cl);
            }
        });

           /* call.enqueue(new Callback<ResponseChangePassword>() {
                @Override
                public void onResponse(@NonNull Call<ResponseChangePassword> call, @NonNull ContinueWatchingModel<ResponseChangePassword> response) {
                    if (response.code() == 200) {
                        ResponseChangePassword model = response.body();
                        model.setAccessToken(response.headers().get("x-auth"));
                        model.setStatus(true);
                        responseApi.postValue(response.body());
                    } else {
                        ResponseChangePassword model = new ResponseChangePassword();
                        model.setStatus(false);
                        model.setResponseCode(Objects.requireNonNull(response.code()));
                        responseApi.postValue(model);

                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseChangePassword> call, @NonNull Throwable t) {
                    ResponseChangePassword model = new ResponseChangePassword();
                    model.setStatus(false);
                    responseApi.postValue(model);

                }
            });*/

        return responseApi;
    }


    public LiveData<LoginResponseModel> getFbLogin(Context context, String email, String fbToken, String name, String fbId, String profilePic, boolean isEmail) {
        final MutableLiveData<LoginResponseModel> responseApi;
        {
            responseApi = new MutableLiveData<>();
            ApiInterface endpoint = RequestConfig.getEnveuSubscriptionClient().create(ApiInterface.class);
            final JsonObject requestParam = new JsonObject();
            requestParam.addProperty(AppConstants.API_PARAM_FB_ID, fbId);
            requestParam.addProperty(AppConstants.API_PARAM_NAME, name);
            requestParam.addProperty(AppConstants.API_PARAM_EMAIL_ID, email);
            requestParam.addProperty(AppConstants.API_PARAM_FB_TOKEN, fbToken);
            requestParam.addProperty(AppConstants.API_PARAM_IS_FB_EMAIL, isEmail);
            if (!profilePic.equalsIgnoreCase("")) {
                requestParam.addProperty(AppConstants.API_PARAM_FB_PIC, profilePic);
            }
            Call<LoginResponseModel> call = endpoint.getFbLogin(requestParam);

            BaseCategoryServices.Companion.getInstance().fbLoginService(requestParam, new LoginCallBack() {
                @Override
                public void success(boolean status, Response<me.vipa.userManagement.bean.LoginResponse.LoginResponseModel> response) {
                    if (status) {
                        LoginResponseModel cl;

                        if (response.body() != null ) {
                            String token = response.headers().get("x-auth");
                            KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
                            preference.setAppPrefAccessToken(token);
                            Gson gson = new Gson();
                            String tmp = gson.toJson(response.body());
                            LoginResponseModel loginItemBean = gson.fromJson(tmp, LoginResponseModel.class);

                            responseApi.postValue(loginItemBean);
                        } else {
                            LoginResponseModel responseModel = ErrorCodesIntercepter.getInstance().fbLogin(response);
                            responseApi.postValue(responseModel);
                        }

                    }
                }

                @Override
                public void failure(boolean status, int errorCode, String message) {
                    Logger.e("ResponseError", "getFbLogin" + call.toString());
                    try {
                        responseApi.postValue(call.execute().body());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        return responseApi;
    }


    public LiveData<LoginResponseModel> getForceFbLogin(Context context, String email, String fbToken, String name, String fbId, String profilePic, boolean isEmail) {

        final MutableLiveData<LoginResponseModel> responseApi;
        {
            responseApi = new MutableLiveData<>();
            ApiInterface endpoint = RequestConfig.getEnveuSubscriptionClient().create(ApiInterface.class);
            final JsonObject requestParam = new JsonObject();
            requestParam.addProperty(AppConstants.API_PARAM_FB_ID, fbId);
            requestParam.addProperty(AppConstants.API_PARAM_NAME, name);
            requestParam.addProperty(AppConstants.API_PARAM_EMAIL_ID, email);
            requestParam.addProperty(AppConstants.API_PARAM_FB_TOKEN, fbToken);
            requestParam.addProperty(AppConstants.API_PARAM_IS_FB_EMAIL, isEmail);
            if (!profilePic.equalsIgnoreCase("")) {
                requestParam.addProperty(AppConstants.API_PARAM_FB_PIC, profilePic);
            }
            Call<LoginResponseModel> call = endpoint.getForceFbLogin(requestParam);

            BaseCategoryServices.Companion.getInstance().fbForceLoginService(requestParam, new LoginCallBack() {
                @Override
                public void success(boolean status, Response<me.vipa.userManagement.bean.LoginResponse.LoginResponseModel> response) {
                    LoginResponseModel cl;
                    if (status) {

                        if (response.body() != null ) {
                            Logger.e("", "LoginResponseModel" + response.body());
                            String token = response.headers().get("x-auth");
                            KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
                            preference.setAppPrefAccessToken(token);
                            Gson gson = new Gson();
                            String tmp = gson.toJson(response.body());
                            LoginResponseModel loginItemBean = gson.fromJson(tmp, LoginResponseModel.class);

                            responseApi.postValue(loginItemBean);
                        } else {
                            LoginResponseModel responseModel = ErrorCodesIntercepter.getInstance().fbLogin(response);
                            responseApi.postValue(responseModel);
                        }

                    } else {
                        cl = new LoginResponseModel();
                        cl.setResponseCode(response.code());
                        String debugMessage = context.getResources().getString(R.string.server_error);
                        cl.setDebugMessage(debugMessage);
                    }
                }

                @Override
                public void failure(boolean status, int errorCode, String message) {
                    Logger.e("ResponseError", "getFbLogin" + call.toString());
                    try {
                        responseApi.postValue(call.execute().body());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return responseApi;
    }


    public LiveData<ResponseConnectFb> getConnectFb(Context context, String token, JsonObject requestParam) {
        final MutableLiveData<ResponseConnectFb> responseApi;
        {
            responseApi = new MutableLiveData<>();
            ApiInterface endpoint = RequestConfig.getClientInterceptor(token).create(ApiInterface.class);
            endpoint.getConnectFb(requestParam).enqueue(new Callback<ResponseConnectFb>() {
                @Override
                public void onResponse(@NonNull Call<ResponseConnectFb> call, @NonNull Response<ResponseConnectFb> response) {
                    ResponseConnectFb model = new ResponseConnectFb();
                    if (response.code() == 200) {
                        model.setStatus(true);
                        model.setData(response.body().getData());
                        responseApi.postValue(model);
                        KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
                        preference.setAppPrefAccessToken(response.headers().get("x-auth-token"));
                    } else if (response.code() == 400) {
                        String debugMessage;
                        int code = 0;
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            debugMessage = jObjError.getString("debugMessage");
                            code = jObjError.getInt("responseCode");

                            model.setResponseCode(400);
                            model.setDebugMessage(debugMessage);
                            model.setStatus(false);
                            responseApi.postValue(model);
                            Logger.e("", "" + jObjError.getString("debugMessage"));
                        } catch (Exception e) {
                            Logger.e("RegistrationLoginRepo", "" + e.toString());
                        }
                    } else {
                        model.setStatus(false);
                        responseApi.postValue(model);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseConnectFb> call, @NonNull Throwable t) {
                    ResponseConnectFb model = new ResponseConnectFb();
                    model.setStatus(false);
                }
            });
            return responseApi;
        }
    }


    public LiveData<JsonObject> hitApiLogout(boolean session, String token) {
        final MutableLiveData<JsonObject> responseApi;
        {
            responseApi = new MutableLiveData<>();
            ApiInterface endpoint = RequestConfig.getClientInterceptor(token).create(ApiInterface.class);

            Call<JsonObject> call = endpoint.getLogout(session);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {

                    } catch (Exception e) {

                    }
                    if (response.code() == 404) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty(AppConstants.API_RESPONSE_CODE, response.code());
                        responseApi.postValue(jsonObject);
                    } else if (response.code() == 200) {
                        Objects.requireNonNull(response.body()).addProperty(AppConstants.API_RESPONSE_CODE, response.code());
                        responseApi.postValue(response.body());
                    } else if (response.code() == 401) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty(AppConstants.API_RESPONSE_CODE, response.code());
                        responseApi.postValue(jsonObject);
                    } else if (response.code() == 500) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty(AppConstants.API_RESPONSE_CODE, response.code());
                        responseApi.postValue(jsonObject);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });


        }

        return responseApi;
    }


    public LiveData<UserProfileResponse> getUserProfile(Context context, String token) {
        MutableLiveData<UserProfileResponse> mutableLiveData = new MutableLiveData<>();
        BaseCategoryServices.Companion.getInstance().userProfileService(token, new UserProfileCallBack() {
            @Override
            public void success(boolean status, Response<me.vipa.userManagement.bean.UserProfile.UserProfileResponse> response) {
                UserProfileResponse cl;
                if (status) {
                    if (response != null) {
                        if (response.code() == 200) {
                            Gson gson = new Gson();
                            String tmp = gson.toJson(response.body());
                            UserProfileResponse profileItemBean = gson.fromJson(tmp, UserProfileResponse.class);
                            profileItemBean.setStatus(true);
                            mutableLiveData.postValue(profileItemBean);
                        } else {

                            cl = ErrorCodesIntercepter.getInstance().userProfile(response);
                            mutableLiveData.postValue(cl);

                        }
                    }


                }
            }

            @Override
            public void failure(boolean status, int errorCode, String message) {
                UserProfileResponse cl = new UserProfileResponse();
                cl.setStatus(false);
                mutableLiveData.postValue(cl);
            }
        });
        return mutableLiveData;
    }

    public LiveData<UserProfileResponse> getUpdateProfile(Context context, String token, String name, String mobile, String spinnerValue, String dob, String address, String imageUrl, String via, String contentPreference, boolean isNotificationEnable) {
        MutableLiveData<UserProfileResponse> mutableLiveData = new MutableLiveData<>();
        BaseCategoryServices.Companion.getInstance().userUpdateProfileService(token, name,mobile,spinnerValue,dob,address,imageUrl,via,contentPreference,isNotificationEnable, new UserProfileCallBack() {
            @Override
            public void success(boolean status, Response<me.vipa.userManagement.bean.UserProfile.UserProfileResponse> response) {
                UserProfileResponse cl;
                if (status) {
                    if (response != null) {
                        if (response.code() == 200) {
                            Gson gson = new Gson();
                            String tmp = gson.toJson(response.body());
                            UserProfileResponse profileItemBean = gson.fromJson(tmp, UserProfileResponse.class);
                            profileItemBean.setStatus(true);
                            mutableLiveData.postValue(profileItemBean);
                        } else {
                            cl = ErrorCodesIntercepter.getInstance().userProfile(response);
                            mutableLiveData.postValue(cl);
                        }
                    }


                }
            }

            @Override
            public void failure(boolean status, int errorCode, String message) {
                UserProfileResponse cl = new UserProfileResponse();
                cl.setStatus(false);
                mutableLiveData.postValue(cl);
            }
        });
        return mutableLiveData;
    }

    /////
    public LiveData<AllSecondaryAccountDetails>  getSecondaryAPIResponse(Context context,String token) {
        final MutableLiveData<AllSecondaryAccountDetails> responseApi;
        responseApi = new MutableLiveData<>();


        BaseCategoryServices.Companion.getInstance().AllListService( token,new AllListCallBack() {
            @Override
            public void success(boolean status, Response<AllSecondaryDetails> response) {
                if (status) {
                    AllSecondaryAccountDetails cl;
                    if (response.body() != null) {
                     /*   String token = response.headers().get("x-auth");
                        KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
                        preference.setAppPrefAccessToken(token);*/
                        Gson gson = new Gson();
                        String tmp = gson.toJson(response.body());
                        AllSecondaryAccountDetails loginItemBean = gson.fromJson(tmp, AllSecondaryAccountDetails.class);
                        responseApi.postValue(loginItemBean);
                    } else {
                        AllSecondaryAccountDetails responseModel = ErrorCodesIntercepter.getInstance().allSecondaryAccountDetailsl(response);
                        responseApi.postValue(responseModel);
                    }
                }
            }

            @Override
            public void failure(boolean status, int errorCode, String errorMessage) {
                Logger.e("", "AllSecondaryResponse E" + errorMessage);
                AllSecondaryAccountDetails cl = new AllSecondaryAccountDetails();
                cl.setDebugMessage(errorMessage);
                responseApi.postValue(cl);
            }
        });

        return responseApi;
    }

    public LiveData<SecondaryUserDetailsJavaPojo> getSecondaryUserAPIReponse(String token) {
        final MutableLiveData<SecondaryUserDetailsJavaPojo> responseApi;
        responseApi = new MutableLiveData<>();

        BaseCategoryServices.Companion.getInstance().SecondaryUserService( token,new SecondaryUserCallBack() {
            @Override
            public void success(boolean status, Response<SecondaryUserDetails> response) {
                if (status) {
                    SecondaryUserDetails cl;
                    if (response.body() != null) {
                     /*   String token = response.headers().get("x-auth");
                        KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
                        preference.setAppPrefAccessToken(token);*/
                        Gson gson = new Gson();
                        String tmp = gson.toJson(response.body());
                        SecondaryUserDetailsJavaPojo loginItemBean = gson.fromJson(tmp, SecondaryUserDetailsJavaPojo.class);
                        responseApi.postValue(loginItemBean);
                    } else {
                        SecondaryUserDetailsJavaPojo responseModel = ErrorCodesIntercepter.getInstance().secondaryUserDetails(response);
                        responseApi.postValue(responseModel);
                    }
                }
            }

            @Override
            public void failure(boolean status, int errorCode, String errorMessage) {
                Logger.e("", "SecondaryUser E" + errorMessage);
                SecondaryUserDetailsJavaPojo cl = new SecondaryUserDetailsJavaPojo();
                cl.setDebugMessage(errorMessage);
                responseApi.postValue(cl);
            }
        });

        return responseApi;
    }

}
