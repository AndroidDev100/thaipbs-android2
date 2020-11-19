package com.mvhub.mvhubplus.activities.series.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mvhub.mvhubplus.repository.series.SeriesRepository;
import com.mvhub.mvhubplus.beanModel.AssetHistoryContinueWatching.ResponseAssetHistory;
import com.mvhub.mvhubplus.beanModel.addComment.ResponseAddComment;
import com.mvhub.mvhubplus.beanModel.allComments.ResponseAllComments;
import com.mvhub.mvhubplus.beanModel.deleteComment.ResponseDeleteComment;
import com.mvhub.mvhubplus.beanModel.emptyResponse.ResponseEmpty;
import com.mvhub.mvhubplus.beanModel.isLike.ResponseIsLike;
import com.mvhub.mvhubplus.beanModel.isWatchList.ResponseContentInWatchlist;
import com.mvhub.mvhubplus.beanModel.like.ResponseAddLike;
import com.mvhub.mvhubplus.beanModel.responseModels.series.SeriesResponse;
import com.mvhub.mvhubplus.beanModel.responseModels.series.season.SeasonResponse;
import com.mvhub.mvhubplus.beanModel.watchList.ResponseWatchList;
import com.google.gson.JsonObject;

import java.util.List;

public class SeriesViewModel extends AndroidViewModel {
    public SeriesViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<SeriesResponse> getSeriesDetail(int seriesId) {
        return SeriesRepository.getInstance().getSeriesDetail(seriesId);
    }


    public LiveData<SeasonResponse> getSeasonDetail(int seasonId, int pageNo, int length) {
        return SeriesRepository.getInstance().getSeasonDetail(seasonId, pageNo, length);
    }

    public LiveData<SeasonResponse> getVOD(int seriesID, int pageNo, int length) {
        return SeriesRepository.getInstance().getVOD(seriesID, pageNo, length);
    }

    public LiveData<ResponseWatchList> hitApiAddWatchList(String token, JsonObject data) {
        return SeriesRepository.getInstance().hitApiAddToWatchList(token, data);
    }


    public LiveData<ResponseEmpty> hitApiRemoveWatchList(String token, String data) {
        return SeriesRepository.getInstance().hitApiRemoveFromWatchList(token, data);
    }


    public LiveData<ResponseContentInWatchlist> hitApiIsWatchList(String token, JsonObject data) {
        return SeriesRepository.getInstance().hitApiIsToWatchList(token, data);
    }


    public LiveData<ResponseAddLike> hitApiAddLike(String token, JsonObject data) {
        return SeriesRepository.getInstance().hitApiAddLike(token, data);
    }

    public LiveData<ResponseEmpty> hitApiUnLike(String token, JsonObject data) {
        return SeriesRepository.getInstance().hitApiUnLike(token, data);
    }


    public LiveData<ResponseAddComment> hitApiAddComment(String token, JsonObject data) {
        return SeriesRepository.getInstance().hitApiAddComment(token, data);
    }

    public LiveData<ResponseDeleteComment> hitApiDeleteComment(String token, String data) {
        return SeriesRepository.getInstance().hitApiDeleteComment(token, data);
    }

    public LiveData<ResponseIsLike> hitApiIsLike(String token, JsonObject data) {
        return SeriesRepository.getInstance().hitApiIsLike(token, data);
    }

    public LiveData<ResponseAllComments> hitApiAllComents(String size, int page, JsonObject data) {
        return SeriesRepository.getInstance().hitApiAllComment(size, page, data);
    }

    public LiveData<List<SeasonResponse>> hitMultiRequestSeries(int size, SeriesResponse data) {
        return SeriesRepository.getInstance().multiRequestSeries(size, data);
    }

    public LiveData<ResponseAssetHistory> getMultiAssetHistory(String token, JsonObject data) {
        return SeriesRepository.getInstance().getMultiAssetHistory(token, data);
    }

    public LiveData<SeasonResponse> singleRequestSeries(int id, int page, int size) {
        return SeriesRepository.getInstance().singleRequestSeries(id, page, size);
    }

    public LiveData<JsonObject> hitLogout(boolean session, String token) {
        return SeriesRepository.getInstance().hitApiLogout(session, token);
    }

}
