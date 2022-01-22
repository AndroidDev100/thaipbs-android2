package me.vipa.app.networking.servicelayer;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;
import me.vipa.app.beanModelV3.continueWatching.ContinueWatchingModel;
import me.vipa.app.beanModelV3.continueWatching.DataItem;
import me.vipa.app.beanModelV3.playListModelV2.EnveuCommonResponse;
import me.vipa.app.beanModelV3.searchV2.Data;
import me.vipa.app.beanModelV3.searchV2.ResponseSearch;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import me.vipa.app.beanModelV3.videoDetailsV2.EnveuVideoDetails;
import me.vipa.app.beanModelV3.videoDetailsV2.EnveuVideoDetailsBean;
import me.vipa.app.callbacks.apicallback.ApiResponseModel;
import me.vipa.app.callbacks.commonCallbacks.CommonApiCallBack;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.networking.errormodel.ApiErrorModel;
import me.vipa.app.utils.MediaTypeConstants;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.config.ImageLayer;
import me.vipa.app.utils.config.LanguageLayer;
import me.vipa.app.utils.cropImage.helpers.PrintLogging;
import me.vipa.app.utils.helpers.SharedPrefHelper;
import me.vipa.baseCollection.baseCategoryModel.BaseCategory;
import me.vipa.baseCollection.baseCategoryServices.BaseCategoryServices;
import me.vipa.bookmarking.bean.continuewatching.ContinueWatchingBookmark;
import me.vipa.brightcovelibrary.Logger;
import me.vipa.brightcovelibrary.utils.ObjectHelper;
import me.vipa.callBacks.EnveuCallBacks;
import me.vipa.watchHistory.beans.ItemsItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APIServiceLayer {

    private static APIServiceLayer projectRepository;
    private static ApiInterface endpoint;
    private ApiResponseModel callBack;
    private List<EnveuVideoItemBean> enveuVideoItemBeans;

    public synchronized static APIServiceLayer getInstance() {
        if (projectRepository == null) {
            if (RequestConfig.getEnveuClient()!=null) {
                endpoint = RequestConfig.getEnveuClient().create(ApiInterface.class);
            }

            projectRepository = new APIServiceLayer();
        }
        return projectRepository;
    }


    public LiveData<List<BaseCategory>> getCategories(String screenId) {
        MutableLiveData<List<BaseCategory>> liveData = new MutableLiveData<>();
        BaseCategoryServices.Companion.getInstance().categoryService(screenId, new EnveuCallBacks() {
            @Override
            public void success(boolean status, List<BaseCategory> categoryList) {
                if (status) {
                    Collections.sort(categoryList, new Comparator<BaseCategory>() {
                        @Override
                        public int compare(BaseCategory o1, BaseCategory o2) {
                            return o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
                        }
                    });
                    liveData.postValue(categoryList);
                }
            }

            @Override
            public void failure(boolean status, int errorCode, String errorMessage) {
                liveData.postValue(new ArrayList<>());
            }
        });
        return liveData;
    }

    String languageCode = "";

    public MutableLiveData<EnveuCommonResponse> getPlayListById(String playListId, int pageNumber, int pageSize, Activity activity) {
        MutableLiveData<EnveuCommonResponse> enveuCommonResponseMutableLiveData = new MutableLiveData<>();
        languageCode = LanguageLayer.getCurrentLanguageCode();
        if (endpoint!=null) {
           boolean isKidsMode  = SharedPrefHelper.getInstance(activity).getKidsMode();
           if (isKidsMode){
               String parentalRating = AppCommonMethod.getParentalRating();
               endpoint.getPlaylistDetailsByIdWithPG(playListId, languageCode, pageNumber, pageSize,parentalRating).enqueue(new Callback<EnveuCommonResponse>() {
                   @Override
                   public void onResponse(Call<EnveuCommonResponse> call, Response<EnveuCommonResponse> response) {
                       if (response.isSuccessful()) {
                           if (response.body() != null && response.body().getData() != null && response.body().getData().getItems().size()>0) {
                               if (response.body().getResponseCode() == 2000){
                                   enveuCommonResponseMutableLiveData.postValue(response.body());
                               }
                               else {
                                   enveuCommonResponseMutableLiveData.postValue(null);
                               }
                           }
                           else {
                               enveuCommonResponseMutableLiveData.postValue(null);
                           }
                       } else {
                           enveuCommonResponseMutableLiveData.postValue(null);
                       }
                   }

                   @Override
                   public void onFailure(Call<EnveuCommonResponse> call, Throwable t) {
                       enveuCommonResponseMutableLiveData.postValue(null);
                   }
               });
           }else {
               endpoint.getPlaylistDetailsById(playListId, languageCode, pageNumber, pageSize).enqueue(new Callback<EnveuCommonResponse>() {
                   @Override
                   public void onResponse(Call<EnveuCommonResponse> call, Response<EnveuCommonResponse> response) {
                       if (response.isSuccessful()) {
                           if (response.body() != null && response.body().getData() != null && response.body().getData().getItems().size()>0) {
                               if (response.body().getResponseCode() == 2000){
                                   enveuCommonResponseMutableLiveData.postValue(response.body());
                               }
                               else {
                                   enveuCommonResponseMutableLiveData.postValue(null);
                               }
                           }
                           else {
                               enveuCommonResponseMutableLiveData.postValue(null);

                           }
                       } else {
                           enveuCommonResponseMutableLiveData.postValue(null);
                       }
                   }

                   @Override
                   public void onFailure(Call<EnveuCommonResponse> call, Throwable t) {
                       enveuCommonResponseMutableLiveData.postValue(null);
                   }
               });
           }
        }
        return enveuCommonResponseMutableLiveData;
    }

    public void getAssetTypeHero(String manualImageAssetId, CommonApiCallBack commonApiCallBack, Context context) {
        languageCode = LanguageLayer.getCurrentLanguageCode();
        if (endpoint!=null) {

            boolean isKidsMode  = SharedPrefHelper.getInstance(context).getKidsMode();
            if (isKidsMode) {
                String parentalRating = AppCommonMethod.getParentalRating();
                endpoint.getVideoDetailsPG(manualImageAssetId, languageCode,parentalRating).enqueue(new Callback<EnveuVideoDetailsBean>() {
                    @Override
                    public void onResponse(Call<EnveuVideoDetailsBean> call, Response<EnveuVideoDetailsBean> response) {
                        if (response.isSuccessful()) {
                            commonApiCallBack.onSuccess(response.body().getData());
                        } else {
                            commonApiCallBack.onFailure(new Throwable("Details Not Found"));

                        }
                    }

                    @Override
                    public void onFailure(Call<EnveuVideoDetailsBean> call, Throwable t) {
                        commonApiCallBack.onFailure(new Throwable("Details Not Found"));

                    }
                });
            }else {

                endpoint.getVideoDetails(manualImageAssetId, languageCode).enqueue(new Callback<EnveuVideoDetailsBean>() {
                    @Override
                    public void onResponse(Call<EnveuVideoDetailsBean> call, Response<EnveuVideoDetailsBean> response) {
                        if (response.isSuccessful()) {
                            commonApiCallBack.onSuccess(response.body().getData());
                        } else {
                            commonApiCallBack.onFailure(new Throwable("Details Not Found"));

                        }
                    }

                    @Override
                    public void onFailure(Call<EnveuVideoDetailsBean> call, Throwable t) {
                        commonApiCallBack.onFailure(new Throwable("Details Not Found"));

                    }
                });
            }
        }
    }

    public void getPlayListByWithPagination(String playlistID,
                                            int pageNumber,
                                            int pageSize,
                                            BaseCategory screenWidget, Context context,ApiResponseModel listener) {
        this.callBack = listener;
        callBack.onStart();
        languageCode = LanguageLayer.getCurrentLanguageCode();
        if (endpoint!=null) {

            boolean isKidsMode  = SharedPrefHelper.getInstance(context).getKidsMode();
            if (isKidsMode){
                String parentalRating = AppCommonMethod.getParentalRating();
                endpoint.getPlaylistDetailsByIdWithPG(playlistID, languageCode, pageNumber, pageSize,parentalRating).enqueue(new Callback<EnveuCommonResponse>() {
                    @Override
                    public void onResponse(Call<EnveuCommonResponse> call, Response<EnveuCommonResponse> response) {
                        if (response.body() != null && response.body().getData() != null) {
                            RailCommonData railCommonData = new RailCommonData(response.body().getData(), screenWidget, true);
                            railCommonData.setStatus(true);
                            callBack.onSuccess(railCommonData);
                        } else {
                            ApiErrorModel errorModel = new ApiErrorModel(response.code(), response.message());
                            callBack.onError(errorModel);
                        }
                    }

                    @Override
                    public void onFailure(Call<EnveuCommonResponse> call, Throwable t) {
                        ApiErrorModel errorModel = new ApiErrorModel(500, t.getMessage());
                        callBack.onFailure(errorModel);
                    }
                });
            }else {

                endpoint.getPlaylistDetailsById(playlistID, languageCode, pageNumber, pageSize).enqueue(new Callback<EnveuCommonResponse>() {
                    @Override
                    public void onResponse(Call<EnveuCommonResponse> call, Response<EnveuCommonResponse> response) {
                        if (response.body() != null && response.body().getData() != null) {
                            RailCommonData railCommonData = new RailCommonData(response.body().getData(), screenWidget, true);
                            railCommonData.setStatus(true);
                            callBack.onSuccess(railCommonData);
                        } else {
                            ApiErrorModel errorModel = new ApiErrorModel(response.code(), response.message());
                            callBack.onError(errorModel);
                        }
                    }

                    @Override
                    public void onFailure(Call<EnveuCommonResponse> call, Throwable t) {
                        ApiErrorModel errorModel = new ApiErrorModel(500, t.getMessage());
                        callBack.onFailure(errorModel);
                    }
                });
            }
        }

    }


    public void getSeasonEpisodesV2(int seriesId, int pageNumber,
                                    int size, int seasonNumber, FragmentActivity activity, ApiResponseModel listener) {
        this.callBack = listener;
        callBack.onStart();
        languageCode = LanguageLayer.getCurrentLanguageCode();
        if (endpoint!=null) {

            boolean isKidsMode  = SharedPrefHelper.getInstance(activity).getKidsMode();
            if (isKidsMode) {
                String parentalRating = AppCommonMethod.getParentalRating();
                endpoint.getRelatedContentPG(seriesId, seasonNumber, pageNumber, size, languageCode,parentalRating).enqueue(new Callback<EnveuCommonResponse>() {
                    @Override
                    public void onResponse(Call<EnveuCommonResponse> call, Response<EnveuCommonResponse> response) {
                        parseResponseAsRailCommonData(response);
                    }

                    @Override
                    public void onFailure(Call<EnveuCommonResponse> call, Throwable t) {
                        ApiErrorModel errorModel = new ApiErrorModel(500, t.getMessage());
                        callBack.onFailure(errorModel);
                    }
                });
            }else {

                endpoint.getRelatedContent(seriesId, seasonNumber, pageNumber, size, languageCode).enqueue(new Callback<EnveuCommonResponse>() {
                    @Override
                    public void onResponse(Call<EnveuCommonResponse> call, Response<EnveuCommonResponse> response) {
                        parseResponseAsRailCommonData(response);
                    }

                    @Override
                    public void onFailure(Call<EnveuCommonResponse> call, Throwable t) {
                        ApiErrorModel errorModel = new ApiErrorModel(500, t.getMessage());
                        callBack.onFailure(errorModel);
                    }
                });
            }
        }

    }

    public void getAllEpisodesV2(int seriesId, int pageNumber,
                                 int size, FragmentActivity activity, ApiResponseModel listener) {
        this.callBack = listener;
        callBack.onStart();
        languageCode = LanguageLayer.getCurrentLanguageCode();
        if (endpoint!=null) {
            boolean isKidsMode  = SharedPrefHelper.getInstance(activity).getKidsMode();
            if (isKidsMode) {
                String parentalRating = AppCommonMethod.getParentalRating();
                endpoint.getRelatedContentWithoutSNoPG(seriesId, pageNumber, size, languageCode,parentalRating).enqueue(new Callback<EnveuCommonResponse>() {
                    @Override
                    public void onResponse(Call<EnveuCommonResponse> call, Response<EnveuCommonResponse> response) {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        parseResponseAsRailCommonData(response);
                    }

                    @Override
                    public void onFailure(Call<EnveuCommonResponse> call, Throwable t) {
                        ApiErrorModel errorModel = new ApiErrorModel(500, t.getMessage());
                        callBack.onFailure(errorModel);
                    }
                });
            }else {

                endpoint.getRelatedContentWithoutSNo(seriesId, pageNumber, size, languageCode).enqueue(new Callback<EnveuCommonResponse>() {
                    @Override
                    public void onResponse(Call<EnveuCommonResponse> call, Response<EnveuCommonResponse> response) {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        parseResponseAsRailCommonData(response);
                    }

                    @Override
                    public void onFailure(Call<EnveuCommonResponse> call, Throwable t) {
                        ApiErrorModel errorModel = new ApiErrorModel(500, t.getMessage());
                        callBack.onFailure(errorModel);
                    }
                });
            }
        }

    }

    private void parseResponseAsRailCommonData(Response<EnveuCommonResponse> response) {
        if (response.body() != null && response.body().getData() != null) {
            if (response.body() != null && response.body().getData().getPageNumber() == 0 && response.body() != null && response.body().getData().getTotalElements() == 0) {
                ApiErrorModel errorModel = new ApiErrorModel(500, "");
                callBack.onError(errorModel);
            } else {
                RailCommonData railCommonData = new RailCommonData(response.body().getData());
                railCommonData.setStatus(true);
                try {
                    railCommonData.setTotalCount(response.body().getData().getTotalElements());
                    railCommonData.setPageTotal(response.body().getData().getTotalPages());
                } catch (Exception ignore) {

                }
                callBack.onSuccess(railCommonData);
            }

        } else {
            ApiErrorModel errorModel = new ApiErrorModel(response.code(), response.message());
            callBack.onError(errorModel);
        }

    }

    public void getSeriesData(String assetID, ApiResponseModel listener, Context context) {
        this.callBack = listener;
        callBack.onStart();
        languageCode = LanguageLayer.getCurrentLanguageCode();
        if (endpoint!=null) {
            boolean isKidsMode  = SharedPrefHelper.getInstance(context).getKidsMode();
            if (isKidsMode) {
                String parentalRating = AppCommonMethod.getParentalRating();
                endpoint.getVideoDetailsPG(assetID, languageCode,parentalRating).enqueue(new Callback<EnveuVideoDetailsBean>() {
                    @Override
                    public void onResponse(Call<EnveuVideoDetailsBean> call, Response<EnveuVideoDetailsBean> response) {
                        if (response.isSuccessful()) {
                            Gson gson = new Gson();
                            String json = gson.toJson(response.body());

                            if (response.body().getData() instanceof EnveuVideoDetails) {
                                RailCommonData railCommonData = new RailCommonData();
                                AppCommonMethod.getAssetDetail(railCommonData, response);
                                callBack.onSuccess(railCommonData);
                            }

                        } else {
                            ApiErrorModel errorModel = new ApiErrorModel(response.code(), response.message());
                            callBack.onError(errorModel);
                        }
                    }

                    @Override
                    public void onFailure(Call<EnveuVideoDetailsBean> call, Throwable t) {
                        ApiErrorModel errorModel = new ApiErrorModel(500, t.getMessage());
                        callBack.onFailure(errorModel);
                    }
                });
            }else {
                endpoint.getVideoDetails(assetID, languageCode).enqueue(new Callback<EnveuVideoDetailsBean>() {
                    @Override
                    public void onResponse(Call<EnveuVideoDetailsBean> call, Response<EnveuVideoDetailsBean> response) {
                        if (response.isSuccessful()) {
                            Gson gson = new Gson();
                            String json = gson.toJson(response.body());

                            if (response.body().getData() instanceof EnveuVideoDetails) {
                                RailCommonData railCommonData = new RailCommonData();
                                AppCommonMethod.getAssetDetail(railCommonData, response);
                                callBack.onSuccess(railCommonData);
                            }

                        } else {
                            ApiErrorModel errorModel = new ApiErrorModel(response.code(), response.message());
                            callBack.onError(errorModel);
                        }
                    }

                    @Override
                    public void onFailure(Call<EnveuVideoDetailsBean> call, Throwable t) {
                        ApiErrorModel errorModel = new ApiErrorModel(500, t.getMessage());
                        callBack.onFailure(errorModel);
                    }
                });
            }
        }

    }

    public MutableLiveData<RailCommonData> getSearchPopularPlayList(String playlistID,
                                                                    int pageNumber, int pageSize, BaseCategory screenWidget, Context context) {
        MutableLiveData<RailCommonData> railCommonDataMutableLiveData = new MutableLiveData<>();
        languageCode = LanguageLayer.getCurrentLanguageCode();
        if (endpoint!=null) {

            boolean isKidsMode  = SharedPrefHelper.getInstance(context).getKidsMode();
            if (isKidsMode){
                String parentalRating = AppCommonMethod.getParentalRating();
                endpoint.getPlaylistDetailsByIdWithPG(playlistID, languageCode, pageNumber, pageSize,parentalRating).enqueue(new Callback<EnveuCommonResponse>() {
                    @Override
                    public void onResponse(Call<EnveuCommonResponse> call, Response<EnveuCommonResponse> response) {
                        if (response.body() != null && response.body().getData() != null) {
                            RailCommonData railCommonData = new RailCommonData(response.body().getData(), screenWidget, true);
                            railCommonData.setStatus(true);
                            railCommonDataMutableLiveData.postValue(railCommonData);
                        } else {
                            RailCommonData railCommonData = new RailCommonData();
                            railCommonData.setStatus(false);
                            railCommonDataMutableLiveData.postValue(railCommonData);
                        }
                    }

                    @Override
                    public void onFailure(Call<EnveuCommonResponse> call, Throwable t) {
                        RailCommonData railCommonData = new RailCommonData();
                        railCommonData.setStatus(false);
                        railCommonDataMutableLiveData.postValue(railCommonData);
                    }
                });
            }
            else {
                endpoint.getPlaylistDetailsById(playlistID, languageCode, pageNumber, pageSize).enqueue(new Callback<EnveuCommonResponse>() {
                    @Override
                    public void onResponse(Call<EnveuCommonResponse> call, Response<EnveuCommonResponse> response) {
                        if (response.body() != null && response.body().getData() != null) {
                            RailCommonData railCommonData = new RailCommonData(response.body().getData(), screenWidget, true);
                            railCommonData.setStatus(true);
                            railCommonDataMutableLiveData.postValue(railCommonData);
                        } else {
                            RailCommonData railCommonData = new RailCommonData();
                            railCommonData.setStatus(false);
                            railCommonDataMutableLiveData.postValue(railCommonData);
                        }
                    }

                    @Override
                    public void onFailure(Call<EnveuCommonResponse> call, Throwable t) {
                        RailCommonData railCommonData = new RailCommonData();
                        railCommonData.setStatus(false);
                        railCommonDataMutableLiveData.postValue(railCommonData);
                    }
                });
            }
        }
        return railCommonDataMutableLiveData;

    }

    public void getContinueWatchingVideos(List<ContinueWatchingBookmark> continueWatchingBookmarkList, String manualImageAssetId, Context context, CommonApiCallBack commonApiCallBack) {
        languageCode = LanguageLayer.getCurrentLanguageCode();
        if (endpoint!=null) {

            boolean isKidsMode  = SharedPrefHelper.getInstance(context).getKidsMode();
            if (isKidsMode) {
                String parentalRating = AppCommonMethod.getParentalRating();
                endpoint.getVideosPG(manualImageAssetId, languageCode,parentalRating).enqueue(new Callback<ContinueWatchingModel>() {
                    @Override
                    public void onResponse(Call<ContinueWatchingModel> call, Response<ContinueWatchingModel> response) {
                        if (response.isSuccessful()) {
                            ArrayList<DataItem> enveuVideoDetailsArrayList = new ArrayList<>();
                            ArrayList<DataItem> enveuVideoDetails = (ArrayList<DataItem>) response.body().getData();

                            for (ContinueWatchingBookmark continueWatchingBookmark : continueWatchingBookmarkList) {
                                for (DataItem enveuVideoDetail : enveuVideoDetails) {

                                    if (continueWatchingBookmark.getAssetId().intValue() == enveuVideoDetail.getId()) {
                                        if (continueWatchingBookmark.getPosition() != null)
                                            enveuVideoDetail.setPosition(continueWatchingBookmark.getPosition());
                                        enveuVideoDetailsArrayList.add(enveuVideoDetail);
                                    }
                                }
                            }
                            commonApiCallBack.onSuccess(enveuVideoDetailsArrayList);
                        } else {
                            commonApiCallBack.onFailure(new Throwable("Details Not Found"));

                        }
                    }

                    @Override
                    public void onFailure(Call<ContinueWatchingModel> call, Throwable t) {
                        commonApiCallBack.onFailure(new Throwable("Details Not Found"));

                    }
                });
            }else {

                endpoint.getVideos(manualImageAssetId, languageCode).enqueue(new Callback<ContinueWatchingModel>() {
                    @Override
                    public void onResponse(Call<ContinueWatchingModel> call, Response<ContinueWatchingModel> response) {
                        if (response.isSuccessful()) {
                            ArrayList<DataItem> enveuVideoDetailsArrayList = new ArrayList<>();
                            ArrayList<DataItem> enveuVideoDetails = (ArrayList<DataItem>) response.body().getData();

                            for (ContinueWatchingBookmark continueWatchingBookmark : continueWatchingBookmarkList) {
                                for (DataItem enveuVideoDetail : enveuVideoDetails) {

                                    if (continueWatchingBookmark.getAssetId().intValue() == enveuVideoDetail.getId()) {
                                        if (continueWatchingBookmark.getPosition() != null)
                                            enveuVideoDetail.setPosition(continueWatchingBookmark.getPosition());
                                        enveuVideoDetailsArrayList.add(enveuVideoDetail);
                                    }
                                }
                            }
                            commonApiCallBack.onSuccess(enveuVideoDetailsArrayList);
                        } else {
                            commonApiCallBack.onFailure(new Throwable("Details Not Found"));

                        }
                    }

                    @Override
                    public void onFailure(Call<ContinueWatchingModel> call, Throwable t) {
                        commonApiCallBack.onFailure(new Throwable("Details Not Found"));

                    }
                });
            }
        }
    }

    public void getWatchListVideos(List<ItemsItem> continueWatchingBookmarkList, String manualImageAssetId, CommonApiCallBack commonApiCallBack, Activity activity) {
        languageCode = LanguageLayer.getCurrentLanguageCode();
        if (endpoint!=null) {

            boolean isKidsMode  = SharedPrefHelper.getInstance(activity).getKidsMode();
            if (isKidsMode) {
                String parentalRating = AppCommonMethod.getParentalRating();
                endpoint.getVideosPG(manualImageAssetId, languageCode,parentalRating).enqueue(new Callback<ContinueWatchingModel>() {
                    @Override
                    public void onResponse(Call<ContinueWatchingModel> call, Response<ContinueWatchingModel> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            ArrayList<DataItem> enveuVideoDetailsArrayList = new ArrayList<>();
                            ArrayList<DataItem> enveuVideoDetails = (ArrayList<DataItem>) response.body().getData();

                            for (ItemsItem item :
                                    continueWatchingBookmarkList) {
                                for (DataItem enveuVideoDetail :
                                        enveuVideoDetails) {
                                    if (item.getAssetId() == enveuVideoDetail.getId()) {
                                        enveuVideoDetailsArrayList.add(enveuVideoDetail);
                                    }
                                }
                            }
                            commonApiCallBack.onSuccess(enveuVideoDetailsArrayList);
                        } else {
                            commonApiCallBack.onFailure(new Throwable("Details Not Found"));

                        }
                    }

                    @Override
                    public void onFailure(Call<ContinueWatchingModel> call, Throwable t) {
                        commonApiCallBack.onFailure(new Throwable("Details Not Found"));

                    }
                });
            }else {

                endpoint.getVideos(manualImageAssetId, languageCode).enqueue(new Callback<ContinueWatchingModel>() {
                    @Override
                    public void onResponse(Call<ContinueWatchingModel> call, Response<ContinueWatchingModel> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            ArrayList<DataItem> enveuVideoDetailsArrayList = new ArrayList<>();
                            ArrayList<DataItem> enveuVideoDetails = (ArrayList<DataItem>) response.body().getData();

                            for (ItemsItem item :
                                    continueWatchingBookmarkList) {
                                for (DataItem enveuVideoDetail :
                                        enveuVideoDetails) {
                                    if (item.getAssetId() == enveuVideoDetail.getId()) {
                                        enveuVideoDetailsArrayList.add(enveuVideoDetail);
                                    }
                                }
                            }
                            commonApiCallBack.onSuccess(enveuVideoDetailsArrayList);
                        } else {
                            commonApiCallBack.onFailure(new Throwable("Details Not Found"));

                        }
                    }

                    @Override
                    public void onFailure(Call<ContinueWatchingModel> call, Throwable t) {
                        commonApiCallBack.onFailure(new Throwable("Details Not Found"));

                    }
                });
            }
        }

    }

    private List<RailCommonData> mModel;

    public LiveData<List<RailCommonData>> getSearchData(Context context, String type, String keyword, int size, int page, boolean applyFilter) {
        languageCode = LanguageLayer.getCurrentLanguageCode();
        List<String> filterGenreSavedListKeyForApi = SharedPrefHelper.getInstance(context).getDataGenreListKeyValue();
        List<String> filterSortSavedListKeyForApi = SharedPrefHelper.getInstance(context).getDataSortListKeyValue();

        MutableLiveData<List<RailCommonData>> responsePopular = new MutableLiveData<>();
        try {
            ApiInterface endpoint = RequestConfig.getClientSearch().create(ApiInterface.class);

            List<String> contentTypes = Arrays.asList(MediaTypeConstants.getInstance().getMovie(),
                    MediaTypeConstants.getInstance().getSeries(),
                    MediaTypeConstants.getInstance().getLive(),
                    MediaTypeConstants.getInstance().getShow());

            Observable<ResponseSearch> programCall = null;
            Observable<ResponseSearch> episodeCall = null;

            if (applyFilter) {
                if (filterGenreSavedListKeyForApi != null
                        && filterGenreSavedListKeyForApi.size() > 0
                        || filterSortSavedListKeyForApi != null
                        && filterSortSavedListKeyForApi.size() > 0) {
                    programCall = endpoint.getSearchByFilters(keyword, contentTypes, size, page,
                            languageCode, filterGenreSavedListKeyForApi,
                            filterSortSavedListKeyForApi)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io());

                    episodeCall = endpoint.getSearchByFilters(keyword,
                            MediaTypeConstants.getInstance().getEpisode(), size, page, languageCode,
                            filterGenreSavedListKeyForApi, filterSortSavedListKeyForApi)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io());
                }
            } else {
                programCall = endpoint.getSearch(keyword, contentTypes, size, page, languageCode);
                episodeCall = endpoint.getSearch(keyword,
                        MediaTypeConstants.getInstance().getEpisode(), size, page, languageCode)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io());
            }

            Observable<List<ResponseSearch>> combined = Observable.zip(programCall, episodeCall, (programResponse, episodeResponse) -> {
                List<ResponseSearch> combinedList = new ArrayList<>();
                combinedList.add(programResponse);
                combinedList.add(episodeResponse);
                return combinedList;
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            combined.subscribe(new Observer<List<ResponseSearch>>() {

                @Override
                public void onSubscribe(Disposable d) {
                    Logger.d("on subscribe");
                }

                @Override
                public void onNext(@NonNull List<ResponseSearch> responseSearchList) {
                    Logger.d("response search: " + responseSearchList);
                    mModel = new ArrayList<>();
                    try {
                        final int dataSize = ObjectHelper.getSize(responseSearchList);
                        for (int i = 0; i < dataSize; i++) {
                            RailCommonData railCommonData = new RailCommonData();
                            final Data data = responseSearchList.get(i).getData();
                            if (data != null && data.getItems() != null) {
                                railCommonData.setStatus(true);
                                List<me.vipa.app.beanModelV3.searchV2.ItemsItem> searchItems =
                                        data.getItems();
                                List<EnveuVideoItemBean> enveuVideoItemBeans = new ArrayList<>();
                                for (me.vipa.app.beanModelV3.searchV2.ItemsItem videoItem : searchItems) {
                                    EnveuVideoItemBean enveuVideoItemBean = new EnveuVideoItemBean(
                                            videoItem);
                                    enveuVideoItemBean.setPosterURL(
                                            ImageLayer.getInstance().getPosterImageUrl(videoItem));
                                    enveuVideoItemBeans.add(enveuVideoItemBean);
                                }
                                railCommonData.setEnveuVideoItemBeans(enveuVideoItemBeans);
                                railCommonData.setPageTotal(data.getPageInfo().getTotal());
                                railCommonData.setStatus(true);
                            } else {
                                railCommonData.setStatus(false);
                            }
                            mModel.add(railCommonData);
                        }
                    } catch (Exception e) {
                        RailCommonData railCommonData = new RailCommonData();
                        railCommonData.setStatus(false);
                        mModel.add(railCommonData);
                    }
                    responsePopular.postValue(mModel);
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    Logger.w(e);
                    responsePopular.postValue(new ArrayList<>());
                }

                @Override
                public void onComplete() {
                    Logger.d("completed");
                }
            });
        } catch (Exception e) {
            mModel = new ArrayList<>();
            RailCommonData railCommonData = new RailCommonData();
            railCommonData.setStatus(false);
            mModel.add(railCommonData);
            responsePopular.postValue(mModel);
        }
        return responsePopular;
    }

    public LiveData<RailCommonData> getSingleCategorySearch(String keyword, String type, int size, int page,boolean applyFilter, Context context) {
        MutableLiveData<RailCommonData> responsePopular;
        Call<ResponseSearch> call = null;
        responsePopular = new MutableLiveData<>();
        {
            languageCode = LanguageLayer.getCurrentLanguageCode();
            try {
                // keyword= URLEncoder.encode(keyword, "UTF-8");
                ApiInterface backendApi = RequestConfig.getClientSearch().create(ApiInterface.class);

                PrintLogging.printLog("", "SearchValues-->>" + keyword + " " + type + " " + size + " " + page);


                if(applyFilter){
                    List<String> filterGenreSavedListKeyForApi = SharedPrefHelper.getInstance(context).getDataGenreListKeyValue();
                    List<String> filterSortSavedListKeyForApi = SharedPrefHelper.getInstance(context).getDataSortListKeyValue();
                    if ( filterGenreSavedListKeyForApi != null && filterGenreSavedListKeyForApi.size() > 0||filterSortSavedListKeyForApi != null && filterSortSavedListKeyForApi.size() > 0) {
                        call = backendApi.getSearchResultsByFilters(keyword, type, size, page, languageCode,filterGenreSavedListKeyForApi,filterSortSavedListKeyForApi);
                    }

                }
                else {
                    call = backendApi.getSearchResults(keyword, type, size, page, languageCode);

                }
                call.enqueue(new Callback<ResponseSearch>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseSearch> call, @NonNull Response<ResponseSearch> data) {
                        if (data.code() == 200) {
                            RailCommonData railCommonData = null;
                            if (data != null) {
                                railCommonData = new RailCommonData();
                                if (data.body().getData() != null && data.body().getData().getItems() != null) {
                                    railCommonData.setStatus(true);
                                    List<me.vipa.app.beanModelV3.searchV2.ItemsItem> itemsItem = data.body().getData().getItems();
                                    enveuVideoItemBeans = new ArrayList<>();
                                    for (me.vipa.app.beanModelV3.searchV2.ItemsItem videoItem : itemsItem) {
                                        EnveuVideoItemBean enveuVideoItemBean = new EnveuVideoItemBean(videoItem);
                                        enveuVideoItemBean.setPosterURL(ImageLayer.getInstance().getPosterImageUrl(videoItem));
                                        if (type.equalsIgnoreCase(MediaTypeConstants.getInstance().getSeries()) && videoItem.getSeasons() != null)
                                            enveuVideoItemBean.setSeasonCount(videoItem.getSeasons().size());

                                        enveuVideoItemBeans.add(enveuVideoItemBean);
                                    }

                                    railCommonData.setEnveuVideoItemBeans(enveuVideoItemBeans);
                                    railCommonData.setPageTotal(data.body().getData().getPageInfo().getTotal());
                                    railCommonData.setStatus(true);
                                    responsePopular.postValue(railCommonData);
                                } else {
                                    railCommonData.setStatus(false);
                                    responsePopular.postValue(railCommonData);
                                }

                            } else {
                                responsePopular.postValue(railCommonData);
                            }
                        } else {
                            responsePopular.postValue(new RailCommonData());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseSearch> call, @NonNull Throwable t) {
                        responsePopular.postValue(new RailCommonData());
                    }
                });

            } catch (Exception e) {
                responsePopular.postValue(new RailCommonData());
            }


        }
        return responsePopular;
    }

    public LiveData<RailCommonData> getProgramSearch(String keyword, int size, int page,boolean applyFilter, Context context) {
        MutableLiveData<RailCommonData> responsePopular;
        Call<ResponseSearch> call = null;
        responsePopular = new MutableLiveData<>();

        languageCode = LanguageLayer.getCurrentLanguageCode();
        try {
            ApiInterface backendApi = RequestConfig.getClientSearch().create(ApiInterface.class);

            PrintLogging.printLog("", "SearchValues-->>" + keyword + " " + size + " " + page);

            List<String> contentTypes = Arrays.asList(MediaTypeConstants.getInstance().getMovie(),
                    MediaTypeConstants.getInstance().getSeries(),
                    MediaTypeConstants.getInstance().getLive(),
                    MediaTypeConstants.getInstance().getShow());

            if (applyFilter) {
                List<String> filterGenreSavedListKeyForApi = SharedPrefHelper.getInstance(context).getDataGenreListKeyValue();
                List<String> filterSortSavedListKeyForApi = SharedPrefHelper.getInstance(context).getDataSortListKeyValue();
                if (filterGenreSavedListKeyForApi != null && filterGenreSavedListKeyForApi.size() > 0 || filterSortSavedListKeyForApi != null && filterSortSavedListKeyForApi.size() > 0) {
                    call = backendApi.getSearchResultsByFilters(keyword, contentTypes, size, page, languageCode, filterGenreSavedListKeyForApi, filterSortSavedListKeyForApi);
                }
            } else {
                call = backendApi.getSearchResults(keyword, contentTypes, size, page, languageCode);
            }

            if (call != null) call.enqueue(new Callback<ResponseSearch>() {
                @Override
                public void onResponse(@NonNull Call<ResponseSearch> call, @NonNull Response<ResponseSearch> response) {
                    if (response.code() == 200) {
                        RailCommonData railCommonData = null;
                        final ResponseSearch body = response.body();
                        if (body != null) {
                            railCommonData = new RailCommonData();
                            final Data data = body.getData();
                            if (data != null && data.getItems() != null) {
                                railCommonData.setStatus(true);
                                List<me.vipa.app.beanModelV3.searchV2.ItemsItem> itemsItem =
                                        data.getItems();
                                enveuVideoItemBeans = new ArrayList<>();
                                for (me.vipa.app.beanModelV3.searchV2.ItemsItem videoItem : itemsItem) {
                                    EnveuVideoItemBean enveuVideoItemBean = new EnveuVideoItemBean(
                                            videoItem);
                                    enveuVideoItemBean.setPosterURL(
                                            ImageLayer.getInstance().getPosterImageUrl(videoItem));
                                    if (videoItem.getSeasons() != null)
                                        enveuVideoItemBean.setSeasonCount(
                                                videoItem.getSeasons().size());

                                    enveuVideoItemBeans.add(enveuVideoItemBean);
                                }

                                railCommonData.setEnveuVideoItemBeans(enveuVideoItemBeans);
                                railCommonData.setPageTotal(data.getPageInfo().getTotal());
                                railCommonData.setStatus(true);
                            } else {
                                railCommonData.setStatus(false);
                            }
                        }
                        responsePopular.postValue(railCommonData);
                    } else {
                        responsePopular.postValue(new RailCommonData());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseSearch> call, @NonNull Throwable t) {
                    responsePopular.postValue(new RailCommonData());
                }
            });
            else Logger.e("Call not sent");

        } catch (Exception e) {
            responsePopular.postValue(new RailCommonData());
        }

        return responsePopular;
    }

}
