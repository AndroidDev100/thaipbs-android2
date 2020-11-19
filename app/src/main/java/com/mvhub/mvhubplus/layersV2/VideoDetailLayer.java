package com.mvhub.mvhubplus.layersV2;

import com.mvhub.mvhubplus.beanModel.enveuCommonRailData.RailCommonData;
import com.mvhub.mvhubplus.beanModelV3.videoDetailsV2.EnveuVideoDetails;
import com.mvhub.mvhubplus.beanModelV3.videoDetailsV2.EnveuVideoDetailsBean;
import com.mvhub.mvhubplus.callbacks.apicallback.ApiResponseModel;
import com.mvhub.mvhubplus.callbacks.commonCallbacks.CommonApiCallBack;
import com.mvhub.mvhubplus.networking.apiendpoints.ApiInterface;
import com.mvhub.mvhubplus.networking.apiendpoints.RequestConfig;
import com.mvhub.mvhubplus.networking.errormodel.ApiErrorModel;
import com.mvhub.mvhubplus.networking.servicelayer.APIServiceLayer;
import com.mvhub.mvhubplus.utils.commonMethods.AppCommonMethod;
import com.mvhub.mvhubplus.utils.config.LanguageLayer;

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
    public void getVideoDetails(String manualImageAssetId, ApiResponseModel listener) {
        this.callBack = listener;
        callBack.onStart();
        languageCode= LanguageLayer.getCurrentLanguageCode();
        endpoint.getVideoDetails(manualImageAssetId,languageCode).enqueue(new Callback<EnveuVideoDetailsBean>() {
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


    public void getAssetTypeHero(String manualImageAssetId, CommonApiCallBack commonApiCallBack) {
        APIServiceLayer.getInstance().getAssetTypeHero(manualImageAssetId, commonApiCallBack);
    }


}