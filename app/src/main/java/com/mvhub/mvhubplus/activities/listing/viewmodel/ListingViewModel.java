package com.mvhub.mvhubplus.activities.listing.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mvhub.mvhubplus.repository.series.SeriesRepository;
import com.mvhub.mvhubplus.beanModel.responseModels.series.season.SeasonResponse;

public class ListingViewModel extends AndroidViewModel {

    public ListingViewModel(@NonNull Application application) {
        super(application);

    }

   /* public LiveData<PlaylistRailData> getRailData(int playlistId, int page, int length, BaseCategory baseCategory){
        return ListingRepository.getInstance().getRailData(playlistId,page,length);
    }*/


    public LiveData<SeasonResponse> getVOD(int seriesID, int pageNo, int length) {
        return SeriesRepository.getInstance().getVOD(seriesID, pageNo, length);
    }

    public LiveData<SeasonResponse> getSeasonDetail(int seasonId, int pageNo, int length) {
        return SeriesRepository.getInstance().getSeasonDetail(seasonId, pageNo, length);
    }
}
