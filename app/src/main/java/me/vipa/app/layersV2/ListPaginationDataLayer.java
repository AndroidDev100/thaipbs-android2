package me.vipa.app.layersV2;

import me.vipa.baseCollection.baseCategoryModel.BaseCategory;
import me.vipa.app.callbacks.apicallback.ApiResponseModel;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.networking.servicelayer.APIServiceLayer;
import me.vipa.app.callbacks.apicallback.ApiResponseModel;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.networking.servicelayer.APIServiceLayer;
import me.vipa.baseCollection.baseCategoryModel.BaseCategory;

public class ListPaginationDataLayer {

    private static ListPaginationDataLayer listPaginationDataLayerInstance;
    private static ApiInterface endpoint;
    private ApiResponseModel callBack;

    private ListPaginationDataLayer() {

    }

    public static ListPaginationDataLayer getInstance() {
        if (listPaginationDataLayerInstance == null) {
            endpoint = RequestConfig.getEnveuClient().create(ApiInterface.class);
            listPaginationDataLayerInstance = new ListPaginationDataLayer();
        }
        return (listPaginationDataLayerInstance);
    }


    public void getPlayListByWithPagination(String playlistID,
                                            int pageNumber,
                                            int pageSize,
                                            BaseCategory screenWidget, ApiResponseModel listener) {
        APIServiceLayer.getInstance().getPlayListByWithPagination(playlistID, pageNumber, pageSize, screenWidget, listener);
    }
}
