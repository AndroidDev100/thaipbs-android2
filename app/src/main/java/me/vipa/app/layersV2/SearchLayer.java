package me.vipa.app.layersV2;

import androidx.lifecycle.LiveData;

import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;
import me.vipa.app.callbacks.apicallback.ApiResponseModel;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.networking.servicelayer.APIServiceLayer;

import java.util.List;

import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;
import me.vipa.app.callbacks.apicallback.ApiResponseModel;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.networking.servicelayer.APIServiceLayer;

public class SearchLayer {

    private static SearchLayer searchLayerInstance;
    private static ApiInterface endpoint;
    ApiResponseModel callBack;

    private SearchLayer() {

    }

    public static SearchLayer getInstance() {
        if (searchLayerInstance == null) {
            endpoint = RequestConfig.getEnveuClient().create(ApiInterface.class);
            searchLayerInstance = new SearchLayer();
        }
        return (searchLayerInstance);
    }


    public LiveData<List<RailCommonData>> getSearchData(String type, String keyword, int size, int page) {
        return APIServiceLayer.getInstance().getSearchData(type,keyword,size,page);
    }

    public LiveData<RailCommonData> getSingleCategorySearch(String type, String keyword, int size, int page) {
        return APIServiceLayer.getInstance().getSingleCategorySearch(type,keyword,size,page);
    }


}
