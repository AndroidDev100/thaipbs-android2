package me.vipa.app.activities.series.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import me.vipa.app.repository.series.SeriesRepository;
import me.vipa.app.beanModel.AssetHistoryContinueWatching.ResponseAssetHistory;
import me.vipa.app.beanModel.addComment.ResponseAddComment;
import me.vipa.app.beanModel.allComments.ResponseAllComments;
import me.vipa.app.beanModel.deleteComment.ResponseDeleteComment;
import me.vipa.app.beanModel.emptyResponse.ResponseEmpty;
import me.vipa.app.beanModel.isLike.ResponseIsLike;
import me.vipa.app.beanModel.isWatchList.ResponseContentInWatchlist;
import me.vipa.app.beanModel.like.ResponseAddLike;
import me.vipa.app.beanModel.responseModels.series.SeriesResponse;
import me.vipa.app.beanModel.responseModels.series.season.SeasonResponse;
import me.vipa.app.beanModel.watchList.ResponseWatchList;
import com.google.gson.JsonObject;

import java.util.List;

import me.vipa.app.repository.series.SeriesRepository;

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
