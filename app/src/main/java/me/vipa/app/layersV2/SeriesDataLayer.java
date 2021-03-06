package me.vipa.app.layersV2;

import android.content.Context;

import me.vipa.app.callbacks.apicallback.ApiResponseModel;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.networking.servicelayer.APIServiceLayer;
import me.vipa.app.callbacks.apicallback.ApiResponseModel;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.networking.servicelayer.APIServiceLayer;

public class SeriesDataLayer {

    private static SeriesDataLayer seriesDataLayerInstance;
    private static ApiInterface endpoint;
    ApiResponseModel callBack;

    private SeriesDataLayer() {

    }

    public static SeriesDataLayer getInstance() {
        if (seriesDataLayerInstance == null) {
            if (RequestConfig.getEnveuClient()!= null) {
                endpoint = RequestConfig.getEnveuClient().create(ApiInterface.class);
            }
            seriesDataLayerInstance = new SeriesDataLayer();
        }
        return (seriesDataLayerInstance);
    }


    public void getSeriesData(String assetID, Context context, ApiResponseModel listener) {
        APIServiceLayer.getInstance().getSeriesData(assetID, listener,context);
    }
}
