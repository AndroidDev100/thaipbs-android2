package me.vipa.app.layersV2;

import android.content.Context;

import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;
import me.vipa.app.beanModelV3.videoDetailsV2.EnveuVideoDetails;
import me.vipa.app.beanModelV3.videoDetailsV2.EnveuVideoDetailsBean;
import me.vipa.app.callbacks.apicallback.ApiResponseModel;
import me.vipa.app.callbacks.commonCallbacks.CommonApiCallBack;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.networking.errormodel.ApiErrorModel;
import me.vipa.app.networking.servicelayer.APIServiceLayer;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.config.LanguageLayer;

import me.vipa.app.utils.helpers.SharedPrefHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoDetailLayer {

    private static VideoDetailLayer videoDetailLayerInstance;
    private static ApiInterface endpoint;
    ApiResponseModel callBack;

    private VideoDetailLayer() {

    }

    public static VideoDetailLayer getInstance() {
        if (videoDetailLayerInstance == null) {
            endpoint = RequestConfig.getEnveuClient().create(ApiInterface.class);
            videoDetailLayerInstance = new VideoDetailLayer();
        }
        return (videoDetailLayerInstance);
    }

    String languageCode;
    public void getVideoDetails(String manualImageAssetId, Context context, ApiResponseModel listener) {
        this.callBack = listener;
        callBack.onStart();
        languageCode= LanguageLayer.getCurrentLanguageCode();

        boolean isKidsMode  = new SharedPrefHelper(context).getKidsMode();
        if (isKidsMode) {
            String parentalRating = AppCommonMethod.getParentalRating();
            endpoint.getVideoDetailsPG(manualImageAssetId, languageCode,parentalRating).enqueue(new Callback<EnveuVideoDetailsBean>() {
                @Override
                public void onResponse(Call<EnveuVideoDetailsBean> call, Response<EnveuVideoDetailsBean> response) {
                    if (response.isSuccessful()) {

                        if (response.body().getData() instanceof EnveuVideoDetails) {
                            RailCommonData railCommonData = new RailCommonData();
                            AppCommonMethod.getAssetDetail(railCommonData, response);
                            callBack.onSuccess(railCommonData);
                        }

                    } else {
                        ApiErrorModel errorModel = new ApiErrorModel(response.code(), response.message());
                        callBack.onError(errorModel);
                    }

                }

                @Override
                public void onFailure(Call<EnveuVideoDetailsBean> call, Throwable t) {
                    ApiErrorModel errorModel = new ApiErrorModel(500, t.getMessage());
                    callBack.onFailure(errorModel);
                }
            });
        }else {

            endpoint.getVideoDetails(manualImageAssetId, languageCode).enqueue(new Callback<EnveuVideoDetailsBean>() {
                @Override
                public void onResponse(Call<EnveuVideoDetailsBean> call, Response<EnveuVideoDetailsBean> response) {
                    if (response.isSuccessful()) {

                        if (response.body().getData() instanceof EnveuVideoDetails) {
                            RailCommonData railCommonData = new RailCommonData();
                            AppCommonMethod.getAssetDetail(railCommonData, response);
                            callBack.onSuccess(railCommonData);
                        }

                    } else {
                        ApiErrorModel errorModel = new ApiErrorModel(response.code(), response.message());
                        callBack.onError(errorModel);
                    }

                }

                @Override
                public void onFailure(Call<EnveuVideoDetailsBean> call, Throwable t) {
                    ApiErrorModel errorModel = new ApiErrorModel(500, t.getMessage());
                    callBack.onFailure(errorModel);
                }
            });
        }
    }


    public void getAssetTypeHero(String manualImageAssetId, Context context, CommonApiCallBack commonApiCallBack) {
        APIServiceLayer.getInstance().getAssetTypeHero(manualImageAssetId, commonApiCallBack,context);
    }


}