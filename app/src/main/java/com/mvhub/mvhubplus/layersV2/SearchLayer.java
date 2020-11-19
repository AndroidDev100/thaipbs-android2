package com.mvhub.mvhubplus.layersV2;

import androidx.lifecycle.LiveData;

import com.mvhub.mvhubplus.beanModel.enveuCommonRailData.RailCommonData;
import com.mvhub.mvhubplus.callbacks.apicallback.ApiResponseModel;
import com.mvhub.mvhubplus.networking.apiendpoints.ApiInterface;
import com.mvhub.mvhubplus.networking.apiendpoints.RequestConfig;
import com.mvhub.mvhubplus.networking.servicelayer.APIServiceLayer;

import java.util.List;

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
