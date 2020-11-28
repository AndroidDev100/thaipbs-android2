package me.vipa.app.repository.premium;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import me.vipa.app.userAssetList.ResponseUserAssetList;
import me.vipa.app.beanModel.AssetHistoryContinueWatching.ResponseAssetHistory;
import me.vipa.app.beanModel.mobileAds.DataItem;
import me.vipa.app.beanModel.mobileAds.ResponseMobileAds;
import me.vipa.app.beanModel.responseModels.landingTabResponses.CommonRailData;
import me.vipa.app.beanModel.responseModels.landingTabResponses.playlistResponse.PlaylistResponses;
import me.vipa.app.beanModel.responseModels.landingTabResponses.playlistResponse.PlaylistsItem;
import me.vipa.app.beanModel.responseModels.landingTabResponses.railData.PlaylistRailData;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.PrintLogging;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.vipa.app.userAssetList.ResponseUserAssetList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PremiumFragmentRepository {
    private static PremiumFragmentRepository projectRepository;
    public List<CommonRailData> adsRail;
    Activity activity;
    List<PlaylistsItem> playlist;
    int count;
    List<PlaylistRailData> commonRailData;
    List<CommonRailData> commonRailDataList;
    ResponseMobileAds adsResponse;
    private ArrayList<PlaylistRailData> railData;

    public synchronized static PremiumFragmentRepository getInstance() {
        if (projectRepository == null) {
            projectRepository = new PremiumFragmentRepository();
        }
        return projectRepository;
    }

    public LiveData<List<CommonRailData>> getPlaylist(int id, List<CommonRailData> adsRail) {
        count = 0;
        this.adsRail = adsRail;
        MutableLiveData<List<CommonRailData>> playlistResponsesMutableLiveData = new MutableLiveData<>();
        ApiInterface endpoint = RequestConfig.getClientHeader().create(ApiInterface.class);
        Call<PlaylistResponses> call = endpoint.getPlaylists(id);
        call.enqueue(new Callback<PlaylistResponses>() {
            @Override
            public void onResponse(@NonNull Call<PlaylistResponses> call, @NonNull Response<PlaylistResponses> response) {
                if (response.body() != null) {
                    playlist = response.body().getData().getPlaylists();
                    commonRailData = new ArrayList<>();

                    if (playlist.size() > 0)
                        getRailData(playlist, playlistResponsesMutableLiveData);
                    else
                        playlistResponsesMutableLiveData.postValue(new ArrayList<>());
                } else {
                    playlistResponsesMutableLiveData.postValue(new ArrayList<>());

                }
                //playlistResponsesMutableLiveData.postValue(commonRailDataList);
            }

            @Override
            public void onFailure(@NonNull Call<PlaylistResponses> call, @NonNull Throwable t) {
                playlistResponsesMutableLiveData.postValue(new ArrayList<>());
                PrintLogging.printLog("", t.toString());
            }
        });
        return playlistResponsesMutableLiveData;
    }

    private void getRailData(List<PlaylistsItem> playlist, MutableLiveData<List<CommonRailData>> playlistResponsesMutableLiveData) {


        if (count != playlist.size()) {
            int id = 0;
            try {
                id = playlist.get(count).getId();
            } catch (Exception ex) {
                PrintLogging.printLog("", ex.getMessage() + "Error message");
            }

            getPlaylistData(id, 0, 10, playlistResponsesMutableLiveData);
        } /*else {
         *//*commonRailDataList = new ArrayList<>();
            commonRailDataList = AppCommonMethod.getHomeRailData(commonRailData, adsRail);
            playlistResponsesMutableLiveData.postValue(commonRailDataList);*//*
        }
*/
    }

    public void getPlaylistData(int playlistId, int pageNo, int length, MutableLiveData<List<CommonRailData>> playlistResponsesMutableLiveData) {

        ApiInterface endpoint = RequestConfig.getClientHeader().create(ApiInterface.class);
        Call<PlaylistRailData> call = endpoint.getPlaylistsData(playlistId, pageNo, length);

        call.enqueue(new Callback<PlaylistRailData>() {
            @Override
            public void onResponse(@NonNull Call<PlaylistRailData> call, @NonNull Response<PlaylistRailData> response) {
                getPlaylistRailData(response, playlistResponsesMutableLiveData);
                // playlistResponsesMutableLiveData.postValue(commonRailDataList);
            }

            @Override
            public void onFailure(@NonNull Call<PlaylistRailData> call, @NonNull Throwable t) {

            }
        });

    }

    private void getPlaylistRailData(Response<PlaylistRailData> response, MutableLiveData<List<CommonRailData>> playlistResponsesMutableLiveData) {


        if (response.body() != null) {
            commonRailData.add(response.body());
            commonRailDataList = new ArrayList<>();
            commonRailDataList = AppCommonMethod.getHomeRailData(commonRailData, adsRail, playlist.size());
            playlistResponsesMutableLiveData.postValue(commonRailDataList);
        }
        count++;
        getRailData(playlist, playlistResponsesMutableLiveData);
    }

    public LiveData<ResponseMobileAds> getAdData(int screenId) {
        MutableLiveData<ResponseMobileAds> ads = new MutableLiveData<>();
        ApiInterface endpoint = RequestConfig.getClientHeader().create(ApiInterface.class);
        Call<ResponseMobileAds> call = endpoint.getAdsData(screenId, AppConstants.PLATFORM);
        call.enqueue(new Callback<ResponseMobileAds>() {
            @Override
            public void onResponse(@NonNull Call<ResponseMobileAds> call, @NonNull Response<ResponseMobileAds> response) {

                if (response.code() == 200) {
                    adsResponse = new ResponseMobileAds();
                    adsResponse.setResponseCode(response.code());
                    adsResponse.setStatus(true);
                    adsResponse.setData(response.body().getData());
                    ads.postValue(adsResponse);
                } else {
                    adsResponse = new ResponseMobileAds();
                    adsResponse.setResponseCode(response.code());
                    adsResponse.setStatus(false);
                    ArrayList<DataItem> dataItem = new ArrayList<>();
                    adsResponse.setData(dataItem);
                    ads.postValue(adsResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseMobileAds> call, @NonNull Throwable t) {
                adsResponse = new ResponseMobileAds();
                adsResponse.setStatus(false);
                ads.postValue(adsResponse);
            }
        });


        return ads;
    }


    public LiveData<List<PlaylistsItem>> getAllHomeList(int id) {

        MutableLiveData<List<PlaylistsItem>> liveData = new MutableLiveData<>();
        ApiInterface endpoint = RequestConfig.getClientHeader().create(ApiInterface.class);
        Call<PlaylistResponses> call = endpoint.getPlaylists(id);
        playlist = new ArrayList<>();
        call.enqueue(new Callback<PlaylistResponses>() {
            @Override
            public void onResponse(@NonNull Call<PlaylistResponses> call, @NonNull Response<PlaylistResponses> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        playlist = response.body().getData().getPlaylists();
                        liveData.postValue(playlist);
                    } else {
                        liveData.postValue(playlist);
                        // liveData.postValue(playlist);
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<PlaylistResponses> call, @NonNull Throwable t) {
                liveData.postValue(playlist);
            }
        });
        return liveData;
    }

    public LiveData<List<PlaylistRailData>> multiRequestHome(int size, List<PlaylistsItem> playlist) {

        MutableLiveData<List<PlaylistRailData>> responseHome;
        railData = new ArrayList<>();
        ApiInterface endpoint = RequestConfig.getClientHeader().create(ApiInterface.class);
        List<Observable<?>> requests = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            requests.add(endpoint.getPlaylistsDataHome(playlist.get(i).getId(), 0, 10));
        }
        responseHome = new MutableLiveData<>();
        Observable.zip(requests, (Function<Object[], Object>) objects -> {
                    for (Object object : objects) {
                        PlaylistRailData tempData = (PlaylistRailData) object;
                        railData.add(tempData);
                    }

                    return railData;
                }

        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Object o) {
                        // mModel = new ArrayList<>();
                        // mModel.addAll(data);
                        if (railData.size() == ((ArrayList) o).size())
                            responseHome.postValue(railData);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return responseHome;
    }

    public LiveData<ResponseAssetHistory> getAssetHistory(String token) {
        MutableLiveData<ResponseAssetHistory> assetContinue = new MutableLiveData<>();

        ApiInterface endpoint = RequestConfig.getClientInterceptor(token).create(ApiInterface.class);

        final JsonObject requestParam = new JsonObject();
        requestParam.addProperty(AppConstants.API_PARAM_PAGE, 0);
        requestParam.addProperty(AppConstants.API_PARAM_SIZE, 100);
        requestParam.add(AppConstants.API_PARAM_SERIES_ID, JsonNull.INSTANCE);
        requestParam.add(AppConstants.API_PARAM_SEASON_ID, JsonNull.INSTANCE);

        Call<ResponseAssetHistory> call = endpoint.getAssetHistory(requestParam);

        call.enqueue(new Callback<ResponseAssetHistory>() {
            @Override
            public void onResponse(Call<ResponseAssetHistory> call, Response<ResponseAssetHistory> response) {
                if (response.code() == 200) {
                    response.body().setStatus(true);
                    assetContinue.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseAssetHistory> call, Throwable t) {
                assetContinue.postValue(new ResponseAssetHistory());
            }
        });
        return assetContinue;
    }


    public LiveData<ResponseUserAssetList> getAssetList(String token, ResponseAssetHistory list) {
        ApiInterface endpoint = RequestConfig.getClientInterceptor(token).create(ApiInterface.class);
        MutableLiveData<ResponseUserAssetList> mModel = new MutableLiveData<>();
        JsonObject jsonObj = new JsonObject();
        JsonArray jsonArr = new JsonArray();
        try {
            for (int i = 0; i < list.getData().getItems().size(); i++) {
                JsonObject pnObj = new JsonObject();
                pnObj.addProperty("id", list.getData().getItems().get(i).getId());
                pnObj.addProperty("type", list.getData().getItems().get(i).getType());
                jsonArr.add(pnObj);
            }
            jsonObj.addProperty(AppConstants.API_PARAM_FETCH_DATA, false);
            jsonObj.addProperty(AppConstants.API_PARAM_PAGE, 0);
            jsonObj.addProperty(AppConstants.API_PARAM_SIZE, 20);
            jsonObj.add("userAssetListDTOList", jsonArr);
        } catch (Exception e) {

        }

        Call<ResponseUserAssetList> call = endpoint.getAssetList(jsonObj);

        call.enqueue(new Callback<ResponseUserAssetList>() {
            @Override
            public void onResponse(Call<ResponseUserAssetList> call, Response<ResponseUserAssetList> response) {
                if (response.code() == 200) {
                    response.body().setStatus(true);
                    mModel.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseUserAssetList> call, Throwable t) {
                ResponseUserAssetList response = new ResponseUserAssetList();
                response.setStatus(false);
                mModel.postValue(response);
            }
        });
        return mModel;
    }


}