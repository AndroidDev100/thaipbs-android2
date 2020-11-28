package me.vipa.app.baseModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import me.vipa.app.beanModel.AssetHistoryContinueWatching.ResponseAssetHistory;
import me.vipa.app.beanModel.responseModels.landingTabResponses.CommonRailData;
import me.vipa.app.beanModel.responseModels.landingTabResponses.playlistResponse.PlaylistsItem;
import me.vipa.app.beanModel.responseModels.landingTabResponses.railData.PlaylistRailData;

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

