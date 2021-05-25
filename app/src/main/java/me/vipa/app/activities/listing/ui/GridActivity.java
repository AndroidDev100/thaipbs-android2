package me.vipa.app.activities.listing.ui;


import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import me.vipa.app.activities.homeactivity.ui.HomeActivity;
import me.vipa.app.fragments.dialog.AlertDialogFragment;
import me.vipa.app.fragments.dialog.AlertDialogSingleButtonFragment;
import me.vipa.app.utils.helpers.CheckInternetConnection;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import me.vipa.baseCollection.baseCategoryModel.BaseCategory;
import me.vipa.bookmarking.bean.continuewatching.ContinueWatchingBookmark;
import me.vipa.enums.ImageType;
import me.vipa.app.Bookmarking.BookmarkingViewModel;
import me.vipa.app.activities.listing.callback.ItemClickListener;
import me.vipa.app.activities.listing.viewmodel.ListingViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.beanModelV3.continueWatching.DataItem;
import me.vipa.app.networking.apistatus.APIStatus;
import me.vipa.app.R;
import me.vipa.app.adapters.CommonListingAdapter;
import me.vipa.app.adapters.commonRails.CommonCircleAdapter;
import me.vipa.app.adapters.commonRails.CommonPosterLandscapeAdapter;
import me.vipa.app.adapters.commonRails.CommonPosterPotraitAdapter;
import me.vipa.app.adapters.commonRails.CommonPotraitAdapter;
import me.vipa.app.adapters.commonRails.CommonPotraitTwoAdapter;
import me.vipa.app.adapters.commonRails.LandscapeListingAdapter;
import me.vipa.app.adapters.commonRails.SquareListingAdapter;
import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import me.vipa.app.beanModel.responseModels.landingTabResponses.railData.ContentsItem;
import me.vipa.app.beanModel.responseModels.series.season.SeasonResponse;
import me.vipa.app.callbacks.commonCallbacks.CommonApiCallBack;
import me.vipa.app.databinding.ListingActivityBinding;
import me.vipa.app.layersV2.ContinueWatchingLayer;
import me.vipa.app.utils.MediaTypeConstants;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.cropImage.helpers.NetworkConnectivity;
import me.vipa.app.utils.cropImage.helpers.PrintLogging;
import me.vipa.app.utils.cropImage.helpers.ShimmerDataModel;
import me.vipa.app.utils.helpers.GridSpacingItemDecoration;
import me.vipa.app.utils.helpers.RailInjectionHelper;
import me.vipa.app.utils.helpers.RecyclerAnimator;

import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class GridActivity extends BaseBindingActivity<ListingActivityBinding> implements ItemClickListener, AlertDialogFragment.AlertDialogListener {
    String playListId;
    BaseCategory baseCategory;
    int enveuLayoutType = 0;
    LinearLayoutManager linearLayoutManager;
    int spacing;
    private ListingViewModel listingViewModel;
    private int counter = 0, flag, firstVisiblePosition, pastVisiblesItems, visibleItemCount, totalItemCount;
    private CommonCircleAdapter commonCircleAdapter;
    private LandscapeListingAdapter commonLandscapeAdapter;
    private CommonPotraitAdapter commonPotraitAdapter;
    private CommonPotraitTwoAdapter commonPotraitTwoAdapter;
    private CommonPosterLandscapeAdapter commonPosterLandscapeAdapter;
    private CommonPosterPotraitAdapter commonPosterPotraitAdapter;
    private GridLayoutManager gridLayoutManager;
    private List<ContentsItem> contentsItems;
    private SquareListingAdapter squareCommonAdapter;
    private String title;
    private boolean isContinueWatchingEnable = false;
    private boolean mIsLoading = true, isScrolling = false;
    private int mScrollY;
    private int shimmerType;
    private RailCommonData listData;
    private long mLastClickTime = 0;
    private boolean isloggedout = false;
    private KsPreferenceKeys preference;

    @Override
    public ListingActivityBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ListingActivityBinding.inflate(inflater);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preference = KsPreferenceKeys.getInstance();
        title = getIntent().getStringExtra("title");
        isContinueWatchingEnable = getIntent().getExtras().getBoolean("isContinueWatching");
        playListId = getIntent().getStringExtra("playListId");
        flag = getIntent().getIntExtra("flag", 0);
        shimmerType = getIntent().getIntExtra("shimmerType", 0);
        baseCategory = getIntent().getExtras().getParcelable("baseCategory");
        modelCall();
        UiIntilization();
        connectionObserver();
    }

    private void connectionObserver() {
        if (NetworkConnectivity.isOnline(GridActivity.this)) {
            getBinding().noConnectionLayout.setVisibility(View.GONE);
            connectionValidation(true);
        } else {
            connectionValidation(false);
        }
    }

    private void connectionValidation(Boolean aBoolean) {
        if (aBoolean) {
            if (counter == 0)
                callShimmer();
            setClicks();
            try {
                getRailData();
            }catch (Exception ignored){

            }

        } else {
            noConnectionLayout();
        }
    }

    private void callShimmer() {
        CommonListingAdapter shimmerAdapter = new CommonListingAdapter(GridActivity.this);
        getBinding().listRecyclerview.hasFixedSize();
        getBinding().listRecyclerview.setNestedScrollingEnabled(false);
        int num = 2;
        if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.CIR.name()) || baseCategory.getContentImageType().equalsIgnoreCase(ImageType.PR1.name())
                || baseCategory.getContentImageType().equalsIgnoreCase(ImageType.PR2.name())
                || baseCategory.getContentImageType().equalsIgnoreCase(ImageType.SQR.name())) {
            boolean tabletSize = GridActivity.this.getResources().getBoolean(R.bool.isTablet);
            num = 3;
            if (tabletSize) {
                if (GridActivity.this.getResources().getConfiguration().orientation == 2)
                    num = 5;
                else
                    num = 4;
            }
            shimmerAdapter.setDataList(new ShimmerDataModel(GridActivity.this).getList(5));
        } else if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.LDS.name())
                || baseCategory.getContentImageType().equalsIgnoreCase(ImageType.LDS2.name())) {
            boolean tabletSize = GridActivity.this.getResources().getBoolean(R.bool.isTablet);
            num = 2;
            if (tabletSize) {
                if (GridActivity.this.getResources().getConfiguration().orientation == 2)
                    num = 4;
                else
                    num = 3;
            }
            shimmerAdapter.setDataList(new ShimmerDataModel(GridActivity.this).getList(4));
        }

        getBinding().listRecyclerview.addItemDecoration(new GridSpacingItemDecoration(num, 6, true));
        gridLayoutManager = new GridLayoutManager(GridActivity.this, num);

        getBinding().listRecyclerview.setLayoutManager(gridLayoutManager);
        getBinding().listRecyclerview.setAdapter(shimmerAdapter);
        getBinding().listRecyclerview.setVisibility(View.VISIBLE);

    }

    private void setRecyclerProperties(int enveuLayoutType, int num) {
        if (enveuLayoutType == 0) {
            gridLayoutManager = new GridLayoutManager(this, num);
            getBinding().listRecyclerview.setItemAnimator(new DefaultItemAnimator());
            getBinding().listRecyclerview.setLayoutManager(gridLayoutManager);

        } else {
            linearLayoutManager = new LinearLayoutManager(this);
            getBinding().listRecyclerview.setItemAnimator(new DefaultItemAnimator());
            getBinding().listRecyclerview.setLayoutManager(linearLayoutManager);
        }
    }

    private void UiIntilization() {
        getBinding().toolbar.backLayout.setOnClickListener(view -> GridActivity.this.finish());
        getBinding().toolbar.titleText.setVisibility(View.VISIBLE);
        if (isContinueWatchingEnable){
            boolean loginStatus = preference.getAppPrefLoginStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString());
            if (loginStatus) {
                getBinding().toolbar.screenText.setText(title + " " + "for" + " " + KsPreferenceKeys.getInstance().getAppPrefUserName());
            }else {
                new ActivityLauncher(GridActivity.this).homeScreen(GridActivity.this, HomeActivity.class);
            }
        }else {
            getBinding().toolbar.screenText.setText(title);
        }
        getBinding().toolbar.homeIcon.setVisibility(View.GONE);
        getBinding().toolbar.mediaRouteButton.setVisibility(View.GONE);
        getBinding().toolbar.backLayout.setVisibility(View.VISIBLE);
        getBinding().toolbar.searchIcon.setVisibility(View.GONE);
        getBinding().toolbar.screenText.setVisibility(View.VISIBLE);
        getBinding().listRecyclerview.hasFixedSize();
        getBinding().listRecyclerview.setNestedScrollingEnabled(false);
        getBinding().listRecyclerview.addItemDecoration(new GridSpacingItemDecoration(3, 5, true));

    }

    private void setClicks() {

        getBinding().listRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                try {
                    if (enveuLayoutType == 0) {
                        GridLayoutManager layoutManager = ((GridLayoutManager) recyclerView.getLayoutManager());
                        firstVisiblePosition = Objects.requireNonNull(layoutManager).findFirstVisibleItemPosition();
                        if (dy > 0) {
                            visibleItemCount = layoutManager.getChildCount();
                            totalItemCount = layoutManager.getItemCount();
                            pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                            if (mIsLoading) {
                                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                    // PrintLogging.printLog("","slidingValues"+getBinding().listRecyclerview.getAdapter().getItemCount()+" "+counter);
                                    int adapterSize = getBinding().listRecyclerview.getAdapter().getItemCount();
                                    if (adapterSize > 8) {
                                        mIsLoading = false;
                                        counter++;
                                        isScrolling = true;
                                        mScrollY += dy;
                                        connectionObserver();
                                    }
                                }
                            }
                        }
                    } else {
                        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                        firstVisiblePosition = Objects.requireNonNull(layoutManager).findFirstVisibleItemPosition();
                        if (dy > 0) {
                            visibleItemCount = layoutManager.getChildCount();
                            totalItemCount = layoutManager.getItemCount();
                            pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                            if (mIsLoading) {
                                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                    // PrintLogging.printLog("","slidingValues"+getBinding().listRecyclerview.getAdapter().getItemCount()+" "+counter);
                                    int adapterSize = getBinding().listRecyclerview.getAdapter().getItemCount();
                                    if (adapterSize > 8) {
                                        mIsLoading = false;
                                        counter++;
                                        isScrolling = true;
                                        mScrollY += dy;
                                        connectionObserver();
                                    }
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    Logger.e("ListingActivity", "" + e.toString());

                }
            }
        });

        getBinding().transparentLayout.setVisibility(View.VISIBLE);
        getBinding().transparentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void getRailData() {
        try {
            if (flag == 0) {
                if (baseCategory.getReferenceName() != null && (baseCategory.getReferenceName().equalsIgnoreCase(AppConstants.ContentType.CONTINUE_WATCHING.name()) || baseCategory.getReferenceName().equalsIgnoreCase("special_playlist"))) {
                    KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
                    String isLogin = preference.getAppPrefLoginStatus();
                    if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                        String token = preference.getAppPrefAccessToken();
                        BookmarkingViewModel bookmarkingViewModel = ViewModelProviders.of(this).get(BookmarkingViewModel.class);
                        bookmarkingViewModel.getContinueWatchingData(token, counter, AppConstants.PAGE_SIZE).observe(this, getContinueWatchingBean -> {
                            getBinding().transparentLayout.setVisibility(View.GONE);
                            if (getContinueWatchingBean != null) {
                                if (getContinueWatchingBean.isStatus()) {

                                    String videoIds = "";
                                    try {
                                        List<ContinueWatchingBookmark> continueWatchingBookmarkLists = getContinueWatchingBean.getData().getContinueWatchingBookmarks();
                                        List<ContinueWatchingBookmark> continueWatchingBookmarkList = removeDuplicates(continueWatchingBookmarkLists);
                                        for (ContinueWatchingBookmark continueWatchingBookmark : continueWatchingBookmarkList
                                        ) {
                                            videoIds = videoIds.concat(String.valueOf(continueWatchingBookmark.getAssetId())).concat(",");
                                        }
                                        Logger.w("assetIds", videoIds);

                                        ContinueWatchingLayer.getInstance().getContinueWatchingVideos(continueWatchingBookmarkList, videoIds, new CommonApiCallBack() {
                                            @Override
                                            public void onSuccess(Object item) {
                                                getBinding().transparentLayout.setVisibility(View.GONE);
                                                if (item instanceof List) {
                                                    ArrayList<DataItem> enveuVideoDetails = (ArrayList<DataItem>) item;
                                                    RailCommonData railCommonData = new RailCommonData();
                                                    railCommonData.setContinueWatchingData(baseCategory, true, enveuVideoDetails, new CommonApiCallBack() {
                                                        @Override
                                                        public void onSuccess(Object item) {
                                                            setRail(railCommonData);
                                                        }

                                                        @Override
                                                        public void onFailure(Throwable throwable) {
                                                        }

                                                        @Override
                                                        public void onFinish() {

                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onFailure(Throwable throwable) {
                                            }

                                            @Override
                                            public void onFinish() {

                                            }
                                        });

                                    } catch (Exception ignored) {

                                    }
                                } else if (getContinueWatchingBean.getResponseCode() == 4302){
                                    isloggedout = true;
                                    logoutCall();
                                    try {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                new ActivityLauncher(GridActivity.this).homeScreen(GridActivity.this, HomeActivity.class);
                                            }
                                        });
                                    } catch (Exception e) {

                                    }
                                }else if (getContinueWatchingBean.getResponseCode() == 500) {
                                    showDialog(GridActivity.this.getResources().getString(R.string.error), GridActivity.this.getResources().getString(R.string.something_went_wrong));
                                }
                            }

                        });
                    }
                } else {
                    RailInjectionHelper railInjectionHelper = ViewModelProviders.of(this).get(RailInjectionHelper.class);
                /*railInjectionHelper.getPlayListDetailsWithPagination(this, playListId, counter, AppConstants.PAGE_SIZE, baseCategory).observe(this, playlistRailData -> {
                    getBinding().transparentLayout.setVisibility(View.GONE);
                    if (Objects.requireNonNull(playlistRailData) != null) {
                        try {
                            if (title == null || title.equalsIgnoreCase("")) {
                                getBinding().toolbar.screenText.setText(playlistRailData.getDisplayName());
                            }
                        } catch (Exception e) {

                        }
                        listData = playlistRailData;
                        setRail(playlistRailData);
                    }
                });*/
                    railInjectionHelper.getPlayListDetailsWithPaginationV2(this, playListId, counter, AppConstants.PAGE_SIZE, baseCategory).observe(this, playlistRailData -> {
                        if (playlistRailData.getStatus().equalsIgnoreCase(APIStatus.START.name())) {

                        } else if (playlistRailData.getStatus().equalsIgnoreCase(APIStatus.SUCCESS.name())) {
                            if (Objects.requireNonNull(playlistRailData) != null) {
                                if (playlistRailData.getBaseCategory() != null) {
                                    RailCommonData railCommonData = (RailCommonData) playlistRailData.getBaseCategory();
                                    try {
                                        if (title == null || title.equalsIgnoreCase("")) {
                                            getBinding().toolbar.screenText.setText(railCommonData.getDisplayName());
                                        }
                                    } catch (Exception e) {

                                    }
                                    listData = railCommonData;
                                    setRail(railCommonData);
                                    Logger.e("RAIL DATA", String.valueOf(listData.isSeries()));
                                }
                            }
                        } else if (playlistRailData.getStatus().equalsIgnoreCase(APIStatus.ERROR.name())) {

                        } else if (playlistRailData.getStatus().equalsIgnoreCase(APIStatus.FAILURE.name())) {

                        }

                    });

                }

            } else {

            }
        }catch (Exception ignored){

        }

    }

    private void logoutCall() {
        if (CheckInternetConnection.isOnline(Objects.requireNonNull(this))) {
            clearCredientials(preference);
            hitApiLogout(this, preference.getAppPrefAccessToken());
        } else {
            // new ToastHandler(this).show(getResources().getString(R.string.no_internet_connection));
        }
    }

    private List<ContinueWatchingBookmark> removeDuplicates(List<ContinueWatchingBookmark> continueWatchingBookmarkList) {
        List<ContinueWatchingBookmark> noRepeat = new ArrayList<ContinueWatchingBookmark>();
        try {
            for (ContinueWatchingBookmark event : continueWatchingBookmarkList) {
                boolean isFound = false;
                // check if the event name exists in noRepeat
                for (ContinueWatchingBookmark e : noRepeat) {
                    if (e.getAssetId().equals(event.getAssetId()) || (e.equals(event))) {
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) noRepeat.add(event);
            }
        }catch (Exception ignored){

        }


        return noRepeat;
    }

    private void setSeasonData(SeasonResponse seasonResponse) {
        if (!isScrolling) {
            PrintLogging.printLog("", isScrolling + "isScrolling");
            commonLandscapeAdapter = new LandscapeListingAdapter(this, new ArrayList<>(), seasonResponse.getData().getItems(), AppConstants.VOD, this,baseCategory);
            int num = 2;
            boolean tabletSize = GridActivity.this.getResources().getBoolean(R.bool.isTablet);
            if (tabletSize) {
                //landscape
                if (GridActivity.this.getResources().getConfiguration().orientation == 2)
                    num = 4;
                else
                    num = 3;
            }
            itemDecoration(num, "");


            getBinding().listRecyclerview.setAdapter(commonLandscapeAdapter);
            getBinding().progressBar.setVisibility(View.GONE);
        } else {
            commonLandscapeAdapter.notifyEpisodedata(seasonResponse.getData().getItems());
            mIsLoading = seasonResponse.getData().getPageInfo().getTotal() != commonLandscapeAdapter.getItemCount();
            getBinding().listRecyclerview.scrollToPosition(mScrollY);
        }
    }

    private void setRail(RailCommonData playlistRailData) {
        getBinding().transparentLayout.setVisibility(View.GONE);
        if (isScrolling) {
            if (playlistRailData.getEnveuVideoItemBeans().size()>0 && playlistRailData.getEnveuVideoItemBeans()!=null) {
                setUiComponents(playlistRailData);
            }
            getBinding().progressBar.setVisibility(View.GONE);
        } else {
            getBinding().progressBar.setVisibility(View.GONE);
            mIsLoading = true;
            int num = 3;
            if (playlistRailData != null) {
                if (baseCategory.getReferenceName() != null && (baseCategory.getReferenceName().equalsIgnoreCase(AppConstants.ContentType.CONTINUE_WATCHING.name()) || baseCategory.getReferenceName().equalsIgnoreCase("special_playlist"))) {
                    if (commonPosterLandscapeAdapter == null) {
                        new RecyclerAnimator(this).animate(getBinding().listRecyclerview);
                        commonPosterLandscapeAdapter = new CommonPosterLandscapeAdapter(this, playlistRailData.getEnveuVideoItemBeans(), new ArrayList<>(), "VIDEO", new ArrayList<>(),baseCategory);
                        num = 2;
                        boolean tabletSize = GridActivity.this.getResources().getBoolean(R.bool.isTablet);
                        if (tabletSize) {
                            //landscape
                            if (GridActivity.this.getResources().getConfiguration().orientation == 2)
                                num = 4;
                            else
                                num = 3;
                        }
                        itemDecoration(num, baseCategory.getContentImageType());

                        //  getBinding().listRecyclerview.addItemDecoration(new SpacingItemDecoration(2, SpacingItemDecoration.HORIZONTAL));
                        getBinding().listRecyclerview.setAdapter(commonPosterLandscapeAdapter);
                    }
                } else {
                    if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.CIR.name())) {
                        if (commonCircleAdapter == null) {
                            new RecyclerAnimator(this).animate(getBinding().listRecyclerview);
                            commonCircleAdapter = new CommonCircleAdapter(this, playlistRailData.getEnveuVideoItemBeans(), "VIDEO", new ArrayList<>(), this);

                            num = 3;
                            boolean tabletSize = GridActivity.this.getResources().getBoolean(R.bool.isTablet);
                            if (tabletSize) {
                                //landscape
                                if (GridActivity.this.getResources().getConfiguration().orientation == 2)
                                    num = 5;
                                else
                                    num = 4;
                            }
                            itemDecoration(num, baseCategory.getContentImageType());
                            getBinding().listRecyclerview.setAdapter(commonCircleAdapter);
                        } else
                            commonCircleAdapter.notifydata(playlistRailData.getEnveuVideoItemBeans());

                        mIsLoading = playlistRailData.getMaxContent() != commonCircleAdapter.getItemCount();
                    } else if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.SQR.name())) {
                        if (squareCommonAdapter == null) {
                            new RecyclerAnimator(this).animate(getBinding().listRecyclerview);
                            squareCommonAdapter = new SquareListingAdapter(this, playlistRailData.getEnveuVideoItemBeans(), "VIDEO", this);

                            num = 3;
                            boolean tabletSize = GridActivity.this.getResources().getBoolean(R.bool.isTablet);
                            if (tabletSize) {
                                //landscape
                                if (GridActivity.this.getResources().getConfiguration().orientation == 2)
                                    num = 5;
                                else
                                    num = 4;
                            }
                            itemDecoration(num, baseCategory.getContentImageType());
                            getBinding().listRecyclerview.setAdapter(squareCommonAdapter);
                        } else
                            squareCommonAdapter.notifydata(playlistRailData.getEnveuVideoItemBeans());

                        mIsLoading = playlistRailData.getMaxContent() != squareCommonAdapter.getItemCount();
                    } else if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.LDS.name())) {

                        if (commonLandscapeAdapter == null) {
                            new RecyclerAnimator(this).animate(getBinding().listRecyclerview);
                            commonLandscapeAdapter = new LandscapeListingAdapter(this, playlistRailData.getEnveuVideoItemBeans(), new ArrayList<>(), "VIDEO", this,baseCategory);

                            num = 2;
                            boolean tabletSize = GridActivity.this.getResources().getBoolean(R.bool.isTablet);
                            if (tabletSize) {
                                //landscape
                                if (GridActivity.this.getResources().getConfiguration().orientation == 2)
                                    num = 4;
                                else
                                    num = 3;
                            }
                            //itemDecoration(num,baseCategory.getContentImageType());

                            getBinding().listRecyclerview.setAdapter(commonLandscapeAdapter);
                        } else
                            commonLandscapeAdapter.notifydata(playlistRailData.getEnveuVideoItemBeans());

                        mIsLoading = playlistRailData.getMaxContent() != commonLandscapeAdapter.getItemCount();
                    } else if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.LDS2.name())) {

                        if (commonLandscapeAdapter == null) {
                            new RecyclerAnimator(this).animate(getBinding().listRecyclerview);
                            commonLandscapeAdapter = new LandscapeListingAdapter(this, playlistRailData.getEnveuVideoItemBeans(), new ArrayList<>(), "VIDEO", this,baseCategory);

                            num = 2;
                            boolean tabletSize = GridActivity.this.getResources().getBoolean(R.bool.isTablet);
                            if (tabletSize) {
                                //landscape
                                if (GridActivity.this.getResources().getConfiguration().orientation == 2)
                                    num = 4;
                                else
                                    num = 3;
                            }
                            itemDecoration(num, baseCategory.getContentImageType());

                            getBinding().listRecyclerview.setAdapter(commonLandscapeAdapter);
                        } else
                            commonLandscapeAdapter.notifydata(playlistRailData.getEnveuVideoItemBeans());

                        mIsLoading = playlistRailData.getMaxContent() != commonLandscapeAdapter.getItemCount();
                    } else if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.PR1.name())) {

                        if (commonPotraitAdapter == null) {
                            new RecyclerAnimator(this).animate(getBinding().listRecyclerview);
                            commonPotraitAdapter = new CommonPotraitAdapter(this, playlistRailData.getEnveuVideoItemBeans(), "VIDEO", new ArrayList<>(), 0, this,baseCategory);

                            num = 3;
                            boolean tabletSize = GridActivity.this.getResources().getBoolean(R.bool.isTablet);
                            if (tabletSize) {
                                //landscape
                                if (GridActivity.this.getResources().getConfiguration().orientation == 2)
                                    num = 5;
                                else
                                    num = 4;
                            }
                            //  itemDecoration(num,baseCategory.getContentImageType());


                            getBinding().listRecyclerview.setAdapter(commonPotraitAdapter);
                        } else
                            commonPotraitAdapter.notifydata(playlistRailData.getEnveuVideoItemBeans());

                        mIsLoading = playlistRailData.getMaxContent() != commonPotraitAdapter.getItemCount();
                    } else if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.PR2.name())) {

                        if (commonPotraitTwoAdapter == null) {
                            new RecyclerAnimator(this).animate(getBinding().listRecyclerview);
                            commonPotraitTwoAdapter = new CommonPotraitTwoAdapter(this, playlistRailData.getEnveuVideoItemBeans(), "VIDEO", new ArrayList<>(), 0, this,baseCategory);

                            num = 3;
                            boolean tabletSize = GridActivity.this.getResources().getBoolean(R.bool.isTablet);
                            if (tabletSize) {
                                //landscape
                                if (GridActivity.this.getResources().getConfiguration().orientation == 2)
                                    num = 5;
                                else
                                    num = 4;
                            }
                            itemDecoration(num, baseCategory.getContentImageType());


                            getBinding().listRecyclerview.setAdapter(commonPotraitTwoAdapter);
                        } else
                            commonPotraitTwoAdapter.notifydata(playlistRailData.getEnveuVideoItemBeans());

                        mIsLoading = playlistRailData.getMaxContent() != commonPotraitTwoAdapter.getItemCount();
                    } else if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.LDS.name())) {

                        if (commonPosterLandscapeAdapter == null) {
                            new RecyclerAnimator(this).animate(getBinding().listRecyclerview);
                            commonPosterLandscapeAdapter = new CommonPosterLandscapeAdapter(this, playlistRailData.getEnveuVideoItemBeans(), new ArrayList<>(), "VIDEO", new ArrayList<>(),baseCategory);

                            num = 2;
                            boolean tabletSize = GridActivity.this.getResources().getBoolean(R.bool.isTablet);
                            if (tabletSize) {
                                //landscape
                                if (GridActivity.this.getResources().getConfiguration().orientation == 2)
                                    num = 4;
                                else
                                    num = 3;
                            }
                            itemDecoration(num, baseCategory.getContentImageType());

                            //  getBinding().listRecyclerview.addItemDecoration(new SpacingItemDecoration(2, SpacingItemDecoration.HORIZONTAL));
                            getBinding().listRecyclerview.setAdapter(commonPosterLandscapeAdapter);
                        } else
                            commonPosterLandscapeAdapter.notifydata(playlistRailData.getEnveuVideoItemBeans());

                        mIsLoading = playlistRailData.getMaxContent() != commonPosterLandscapeAdapter.getItemCount();
                    } else if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.PR1.name())) {
                        if (commonPosterPotraitAdapter == null) {
                            new RecyclerAnimator(this).animate(getBinding().listRecyclerview);
                            commonPosterPotraitAdapter = new CommonPosterPotraitAdapter(this, playlistRailData.getEnveuVideoItemBeans(), new ArrayList<>(), "VIDEO", new ArrayList<>(), this,baseCategory);
                            num = 3;
                            boolean tabletSize = GridActivity.this.getResources().getBoolean(R.bool.isTablet);
                            if (tabletSize) {
                                //landscape
                                if (GridActivity.this.getResources().getConfiguration().orientation == 2)
                                    num = 5;
                                else
                                    num = 4;
                            }
                            itemDecoration(num, baseCategory.getContentImageType());
                            //     getBinding().listRecyclerview.addItemDecoration(new SpacingItemDecoration(12, SpacingItemDecoration.HORIZONTAL));
                            getBinding().listRecyclerview.setAdapter(commonPosterPotraitAdapter);
                        } else
                            commonPosterPotraitAdapter.notifydata(playlistRailData.getEnveuVideoItemBeans());

                        mIsLoading = playlistRailData.getMaxContent() != commonPosterPotraitAdapter.getItemCount();
                    }

                    getBinding().listRecyclerview.scrollToPosition(mScrollY);
                }
            }
        }
    }

    private void setUiComponents(RailCommonData playlistRailData) {
        if (playlistRailData.getEnveuVideoItemBeans()!=null) {
            if (playlistRailData.getEnveuVideoItemBeans().size() > 0) {
                if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.CIR.name())) {
                    commonCircleAdapter.notifydata(playlistRailData.getEnveuVideoItemBeans());
                    mIsLoading = playlistRailData.getMaxContent() != commonCircleAdapter.getItemCount();

                } else if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.SQR.name())) {
                    if (squareCommonAdapter != null) {
                        squareCommonAdapter.notifydata(playlistRailData.getEnveuVideoItemBeans());
                        mIsLoading = playlistRailData.getMaxContent() != squareCommonAdapter.getItemCount();
                    }

                } else if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.PR1.name())) {

                    commonPotraitAdapter.notifydata(playlistRailData.getEnveuVideoItemBeans());
                    mIsLoading = playlistRailData.getMaxContent() != commonPotraitAdapter.getItemCount();

                } else if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.PR2.name())) {

                    commonPotraitTwoAdapter.notifydata(playlistRailData.getEnveuVideoItemBeans());
                    mIsLoading = playlistRailData.getMaxContent() != commonPotraitTwoAdapter.getItemCount();

                } else if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.LDS.name())) {
                        if (commonLandscapeAdapter!=null) {
                            commonLandscapeAdapter.notifydata(playlistRailData.getEnveuVideoItemBeans());
                            mIsLoading = playlistRailData.getMaxContent() != commonLandscapeAdapter.getItemCount();
                        }


                } else if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.LDS2.name())) {

                    commonLandscapeAdapter.notifydata(playlistRailData.getEnveuVideoItemBeans());
                    mIsLoading = playlistRailData.getMaxContent() != commonLandscapeAdapter.getItemCount();

                } else if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.PR1.name())) {
                    commonPosterPotraitAdapter.notifydata(playlistRailData.getEnveuVideoItemBeans());
                    mIsLoading = playlistRailData.getMaxContent() != commonPosterPotraitAdapter.getItemCount();

                } else {
                    commonLandscapeAdapter.notifydata(playlistRailData.getEnveuVideoItemBeans());
                    mIsLoading = playlistRailData.getMaxContent() != commonLandscapeAdapter.getItemCount();

                }
            }else {
                mIsLoading=false;
            }
        }else {
            mIsLoading=false;
        }
    }

    private void itemDecoration(int i, String contentImageType) {
        /*if (baseCategory.getContentImageType().equalsIgnoreCase(ImageType.LDS.name())){
            spacing= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        }else {
             spacing= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());
        }


        getBinding().listRecyclerview.addItemDecoration(new EqualSpacingItemDecoration(spacing, EqualSpacingItemDecoration.GRID));
        if (enveuLayoutType == 0) {
            gridLayoutManager = new GridLayoutManager(this, i);
            getBinding().listRecyclerview.setLayoutManager(gridLayoutManager);
        } else {
            linearLayoutManager = new LinearLayoutManager(this);
            getBinding().listRecyclerview.setLayoutManager(linearLayoutManager);
        }*/

    }

    private void noConnectionLayout() {
        getBinding().noConnectionLayout.setVisibility(View.VISIBLE);
        getBinding().connection.retryTxt.setOnClickListener(view -> connectionObserver());
    }

    private void modelCall() {
        listingViewModel = ViewModelProviders.of(this).get(ListingViewModel.class);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRowItemClicked(EnveuVideoItemBean itemValue, int position) {
//        Bundle args = new Bundle();
//        args.putInt(AppConstants.BUNDLE_ASSET_ID, itemValue.getId());
//        args.putBoolean(AppConstants.BUNDLE_IS_PREMIUM, itemValue.isPremium());
//        args.putString(AppConstants.BUNDLE_DETAIL_TYPE, itemValue.getAssetType());
//        args.putString(AppConstants.BUNDLE_DURATION, "0");
//        if (listData.isSeries() && itemValue.getBrightcoveVideoId() != null) {
//            Long getVideoId = Long.parseLong(itemValue.getBrightcoveVideoId());
//            args.putLong(AppConstants.BUNDLE_VIDEO_ID_BRIGHTCOVE, getVideoId);
//            args.putString(AppConstants.BUNDLE_ASSET_TYPE, AppConstants.Series);
//        } else {
//            Long getVideoId = Long.parseLong(itemValue.getBrightcoveVideoId());
//            args.putLong(AppConstants.BUNDLE_VIDEO_ID_BRIGHTCOVE, getVideoId);
//
//            if (itemValue.getAssetType() != null) {
//                args.putString(AppConstants.BUNDLE_ASSET_TYPE, itemValue.getAssetType());
//            } else {
//                args.putString(AppConstants.BUNDLE_ASSET_TYPE, AppConstants.Video);
//                AppCommonMethod.launchDetailScreen(this, getVideoId, AppConstants.Video, itemValue.getId(), "0", false);
//            }
//        }
//        Intent intent = new Intent();
//        intent.putExtra(AppConstants.BUNDLE_ASSET_BUNDLE, args);
//        setResult(10001, intent);
//        finish();


        if (listData.isSeries() && AppCommonMethod.getCheckBCID(itemValue.getBrightcoveVideoId())) {
            Long getVideoId = Long.parseLong(itemValue.getBrightcoveVideoId());
            AppCommonMethod.launchDetailScreen(this, getVideoId, MediaTypeConstants.getInstance().getSeries(), itemValue.getId(), "0", false);
        } else {
            if (AppCommonMethod.getCheckBCID(itemValue.getBrightcoveVideoId())) {
                Long getVideoId = Long.parseLong(itemValue.getBrightcoveVideoId());
                if (itemValue.getAssetType() != null) {
                    AppCommonMethod.launchDetailScreen(this, getVideoId, itemValue.getAssetType(), itemValue.getId(), "0", false);
                } else {
                    AppCommonMethod.launchDetailScreen(this, getVideoId, AppConstants.Video, itemValue.getId(), "0", false);
                }
            } else {
                if (itemValue.getAssetType() != null) {
                    AppCommonMethod.launchDetailScreen(this, 0l, itemValue.getAssetType(), itemValue.getId(), "0", false);
                } else {
                    AppCommonMethod.launchDetailScreen(this, 0l, AppConstants.Video, itemValue.getId(), "0", false);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public void onFinishDialog() {
        if (isloggedout) {
            logoutCall();
        } else {
            onBackPressed();
        }
    }

    private void showDialog(String title, String message) {
        FragmentManager fm = getSupportFragmentManager();
        AlertDialogSingleButtonFragment alertDialog = AlertDialogSingleButtonFragment.newInstance(title, message, getResources().getString(R.string.ok));
        alertDialog.setCancelable(false);
        alertDialog.setAlertDialogCallBack(this);
        alertDialog.show(fm, "fragment_alert");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}



