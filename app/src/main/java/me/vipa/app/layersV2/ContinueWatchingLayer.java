package me.vipa.app.layersV2;

import android.app.Activity;
import android.content.Context;

import me.vipa.bookmarking.bean.continuewatching.ContinueWatchingBookmark;
import me.vipa.watchHistory.beans.ItemsItem;
import me.vipa.app.callbacks.apicallback.ApiResponseModel;
import me.vipa.app.callbacks.commonCallbacks.CommonApiCallBack;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.networking.servicelayer.APIServiceLayer;

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

    public void getContinueWatchingVideos(List<ContinueWatchingBookmark> continueWatchingBookmarkList, String manualImageAssetId,Context context, CommonApiCallBack commonApiCallBack) {
        APIServiceLayer.getInstance().getContinueWatchingVideos(continueWatchingBookmarkList, manualImageAssetId,context, commonApiCallBack);
    }

    public void getWatchHistoryVideos(List<ItemsItem> continueWatchingBookmarkList, String manualImageAssetId, Activity activity, CommonApiCallBack commonApiCallBack) {
        APIServiceLayer.getInstance().getWatchListVideos(continueWatchingBookmarkList, manualImageAssetId, commonApiCallBack,activity);
    }

}
