package com.mvhub.mvhubplus.layersV2;

import com.mvhub.mvhubplus.beanModel.enveuCommonRailData.RailCommonData;
import com.mvhub.mvhubplus.beanModelV3.playListModelV2.EnveuCommonResponse;
import com.mvhub.mvhubplus.callbacks.apicallback.ApiResponseModel;
import com.mvhub.mvhubplus.networking.apiendpoints.ApiInterface;
import com.mvhub.mvhubplus.networking.apiendpoints.RequestConfig;
import com.mvhub.mvhubplus.networking.errormodel.ApiErrorModel;
import com.mvhub.mvhubplus.networking.servicelayer.APIServiceLayer;

import retrofit2.Response;

public class SeasonEpisodesList {

    private static SeasonEpisodesList seasonEpisodesListInstance;
    private static ApiInterface endpoint;
    ApiResponseModel callBack;

    private SeasonEpisodesList() {

    }

    public static SeasonEpisodesList getInstance() {
        if (seasonEpisodesListInstance == null) {
            endpoint = RequestConfig.getEnveuClient().create(ApiInterface.class);
            seasonEpisodesListInstance = new SeasonEpisodesList();
        }
        return (seasonEpisodesListInstance);
    }

    public void getSeasonEpisodesV2(int seriesId, int pageNumber,
                                    int size, int seasonNumber, ApiResponseModel listener) {
        APIServiceLayer.getInstance().getSeasonEpisodesV2(seriesId, pageNumber, size, seasonNumber, listener);
    }

    public void getAllEpisodesV2(int seriesId, int pageNumber,
                                 int size, ApiResponseModel listener) {
        APIServiceLayer.getInstance().getAllEpisodesV2(seriesId, pageNumber, size, listener);
    }

    private void parseResponseAsRailCommonData(Response<EnveuCommonResponse> response) {
        if (response.body() != null && response.body().getData() != null) {
            RailCommonData railCommonData = new RailCommonData(response.body().getData());
            railCommonData.setStatus(true);
            callBack.onSuccess(railCommonData);
        } else {
            ApiErrorModel errorModel = new ApiErrorModel(response.code(), response.message());
            callBack.onError(errorModel);
        }

    }

}
