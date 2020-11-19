package com.mvhub.mvhubplus.layersV2;

import com.mvhub.bookmarking.bean.continuewatching.ContinueWatchingBookmark;
import com.mvhub.watchHistory.beans.ItemsItem;
import com.mvhub.mvhubplus.callbacks.apicallback.ApiResponseModel;
import com.mvhub.mvhubplus.callbacks.commonCallbacks.CommonApiCallBack;
import com.mvhub.mvhubplus.networking.apiendpoints.ApiInterface;
import com.mvhub.mvhubplus.networking.apiendpoints.RequestConfig;
import com.mvhub.mvhubplus.networking.servicelayer.APIServiceLayer;

import java.util.List;

public class ContinueWatchingLayer {

    private static ContinueWatchingLayer videoDetailLayerInstance;
    private static ApiInterface endpoint;
    ApiResponseModel callBack;

    private ContinueWatchingLayer() {

    }

    public static ContinueWatchingLayer getInstance() {
        if (videoDetailLayerInstance == null) {
            endpoint = RequestConfig.getEnveuClient().create(ApiInterface.class);
            videoDetailLayerInstance = new ContinueWatchingLayer();
        }
        return (videoDetailLayerInstance);
    }

    public void getContinueWatchingVideos(List<ContinueWatchingBookmark> continueWatchingBookmarkList, String manualImageAssetId, CommonApiCallBack commonApiCallBack) {
        APIServiceLayer.getInstance().getContinueWatchingVideos(continueWatchingBookmarkList, manualImageAssetId, commonApiCallBack);
    }

    public void getWatchHistoryVideos(List<ItemsItem> continueWatchingBookmarkList, String manualImageAssetId, CommonApiCallBack commonApiCallBack) {
        APIServiceLayer.getInstance().getWatchListVideos(continueWatchingBookmarkList, manualImageAssetId, commonApiCallBack);
    }

}
