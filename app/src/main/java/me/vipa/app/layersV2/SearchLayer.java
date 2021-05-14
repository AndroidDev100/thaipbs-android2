package me.vipa.app.layersV2;

import android.content.Context;

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




    public LiveData<List<RailCommonData>> getSearchData(Context context, String type, String keyword, int size, int page, boolean applyFilter) {
        return APIServiceLayer.getInstance().getSearchData(context,type,keyword,size,page,applyFilter);
    }

    public LiveData<RailCommonData> getSingleCategorySearch(String type, String keyword, int size, int page,boolean applyFilter, Context context) {
        return APIServiceLayer.getInstance().getSingleCategorySearch(type,keyword,size,page,applyFilter,context);
    }


}
