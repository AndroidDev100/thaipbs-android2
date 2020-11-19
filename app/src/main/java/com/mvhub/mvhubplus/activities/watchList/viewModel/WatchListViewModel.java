package com.mvhub.mvhubplus.activities.watchList.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mvhub.mvhubplus.repository.watchList.WatchListRepository;
import com.mvhub.mvhubplus.userAssetList.ResponseUserAssetList;
import com.mvhub.mvhubplus.beanModel.AssetHistoryContinueWatching.ResponseAssetHistory;
import com.mvhub.mvhubplus.beanModel.allWatchList.ResponseAllWatchList;
import com.mvhub.mvhubplus.beanModel.emptyResponse.ResponseEmpty;
import com.mvhub.mvhubplus.beanModel.watchHistory.ResponseWatchHistory;

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

