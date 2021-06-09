package me.vipa.app.layersV2;

import androidx.fragment.app.FragmentActivity;

import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;
import me.vipa.app.beanModelV3.playListModelV2.EnveuCommonResponse;
import me.vipa.app.callbacks.apicallback.ApiResponseModel;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.networking.errormodel.ApiErrorModel;
import me.vipa.app.networking.servicelayer.APIServiceLayer;

import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;
import me.vipa.app.beanModelV3.playListModelV2.EnveuCommonResponse;
import me.vipa.app.callbacks.apicallback.ApiResponseModel;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.networking.errormodel.ApiErrorModel;
import me.vipa.app.networking.servicelayer.APIServiceLayer;
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
                                    int size, int seasonNumber, FragmentActivity activity, ApiResponseModel listener) {
        APIServiceLayer.getInstance().getSeasonEpisodesV2(seriesId, pageNumber, size, seasonNumber, activity,listener);
    }

    public void getAllEpisodesV2(int seriesId, int pageNumber,
                                 int size, FragmentActivity activity, ApiResponseModel listener) {
        APIServiceLayer.getInstance().getAllEpisodesV2(seriesId, pageNumber, size, activity,listener);
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
