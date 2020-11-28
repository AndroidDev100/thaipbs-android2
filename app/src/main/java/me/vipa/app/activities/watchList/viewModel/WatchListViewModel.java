package me.vipa.app.activities.watchList.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import me.vipa.app.repository.watchList.WatchListRepository;
import me.vipa.app.userAssetList.ResponseUserAssetList;
import me.vipa.app.beanModel.AssetHistoryContinueWatching.ResponseAssetHistory;
import me.vipa.app.beanModel.allWatchList.ResponseAllWatchList;
import me.vipa.app.beanModel.emptyResponse.ResponseEmpty;
import me.vipa.app.beanModel.watchHistory.ResponseWatchHistory;
import me.vipa.app.repository.watchList.WatchListRepository;
import me.vipa.app.userAssetList.ResponseUserAssetList;

public class WatchListViewModel extends AndroidViewModel {
    // private Context context;
    private final WatchListRepository watchListRepository;

    public WatchListViewModel(@NonNull Application application) {
        super(application);
        //  this.context = application;
        watchListRepository = WatchListRepository.getInstance();

    }

    public LiveData<ResponseAllWatchList> hitApiAllWatchList(String token, int page, int length) {
        return watchListRepository.hitApiAllWatchList(token, page, length);
    }

    public LiveData<ResponseWatchHistory> hitApiWatchHistory(String token, int page, int length) {
        return watchListRepository.hitApiWatchHistory(token, page, length);
    }

    public LiveData<ResponseEmpty> hitApiRemoveWatchList(String token, String data) {
        return watchListRepository.hitApiRemoveFromWatchList(token, data);
    }

    public LiveData<ResponseAssetHistory> getAssetHistory(String token, int page, int size) {
        return watchListRepository.getAssetHistory(token, page, size);
    }

    public LiveData<ResponseUserAssetList> getContinueList(String token, ResponseAssetHistory list, boolean isFetchData) {
        return watchListRepository.getAssetList(token, list, isFetchData);
    }

    public void callCleared() {
        onCleared();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}

