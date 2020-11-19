package com.mvhub.mvhubplus.baseModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mvhub.mvhubplus.beanModel.AssetHistoryContinueWatching.ResponseAssetHistory;
import com.mvhub.mvhubplus.beanModel.responseModels.landingTabResponses.CommonRailData;
import com.mvhub.mvhubplus.beanModel.responseModels.landingTabResponses.playlistResponse.PlaylistsItem;
import com.mvhub.mvhubplus.beanModel.responseModels.landingTabResponses.railData.PlaylistRailData;

import java.util.List;

public abstract class PremiumBaseViewModel extends AndroidViewModel {
    protected PremiumBaseViewModel(@NonNull Application application) {
        super(application);
    }

    public abstract LiveData<List<PlaylistsItem>> getAllChannel(int tabId);

    public abstract LiveData<PlaylistRailData> getChannelRail(int channelId);

    public abstract List<CommonRailData> getTabAds(int id);

    public abstract List<CommonRailData> getContinueAssetHistory(int tabId, String loginToken);

    public abstract List<CommonRailData> getContinueChannelRail(String loginToken, ResponseAssetHistory list, boolean isFetchData);


    public abstract void resetObject();

}

