package me.vipa.app.activities.series.ui;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.brightcove.player.edge.CatalogError;
import com.brightcove.player.edge.OfflineCallback;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.model.Video;
import com.brightcove.player.network.DownloadStatus;
import com.brightcove.player.offline.MediaDownloadable;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.mmtv.utils.helpers.downloads.DownloadHelper;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.vipa.app.R;
import me.vipa.app.SDKConfig;
import me.vipa.app.activities.downloads.NetworkHelper;
import me.vipa.app.activities.downloads.WifiPreferenceListener;
import me.vipa.app.activities.series.viewmodel.SeriesViewModel;
import me.vipa.app.activities.usermanagment.ui.LoginActivity;
import me.vipa.app.adapters.player.EpisodeTabAdapter;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.beanModel.AppUserModel;
import me.vipa.app.beanModel.AssetHistoryContinueWatching.ItemsItem;
import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;
import me.vipa.app.beanModel.selectedSeason.SelectedSeasonModel;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import me.vipa.app.databinding.ActivitySeriesDetailBinding;
import me.vipa.app.fragments.dialog.AlertDialogFragment;
import me.vipa.app.fragments.dialog.AlertDialogSingleButtonFragment;
import me.vipa.app.fragments.player.ui.CommentsFragment;
import me.vipa.app.fragments.player.ui.SeasonTabFragment;
import me.vipa.app.fragments.player.ui.UserInteractionFragment;
import me.vipa.app.networking.apistatus.APIStatus;
import me.vipa.app.networking.responsehandler.ResponseModel;
import me.vipa.app.utils.MediaTypeConstants;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.constants.SharedPrefesConstants;
import me.vipa.app.utils.cropImage.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.RailInjectionHelper;
import me.vipa.app.utils.helpers.SharedPrefHelper;
import me.vipa.app.utils.helpers.ToolBarHandler;
import me.vipa.app.utils.helpers.downloads.OnDownloadClickInteraction;
import me.vipa.app.utils.helpers.downloads.VideoListListener;
import me.vipa.app.utils.helpers.downloads.room.DownloadedEpisodes;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;
import me.vipa.brightcovelibrary.Logger;

public class SeriesDetailActivity extends BaseBindingActivity<ActivitySeriesDetailBinding> implements AlertDialogFragment.AlertDialogListener, OnDownloadClickInteraction, MediaDownloadable.DownloadEventListener, VideoListListener {
    private final String TAG = this.getClass().getSimpleName();
    public String userName = "";
    public boolean isloggedout = false;
    private int seriesId, count;
    private long mLastClickTime = 0;
    private int watchListId = 0;
    private SeriesViewModel viewModel;
    private KsPreferenceKeys preference;
    private String token;
    private int watchListCounter = 0;
    private int likeCounter = 0;
    private String isLogin;
    private int shimmerCounter = 0;
    RailInjectionHelper railInjectionHelper;
    EnveuVideoItemBean seriesDetailBean;
    List<ItemsItem> assetListContinue;
    private CommentsFragment commentsFragment;
    private SeasonTabFragment seasonTabFragment;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
  //  private RecommendationRailFragment railFragment;
    private EpisodeTabAdapter episodeTabAdapter;
    private boolean newIntentCall = false;
    public boolean isSeasonData = false;
    public boolean isRailData = true;
    private DownloadHelper downloadHelper;
    private Video downloadAbleVideo;
    private UserInteractionFragment userInteractionFragment;
    private ArrayList<DownloadedEpisodes> downloadableEpisodes;
    private boolean mFlag = false;
    private boolean kidsMode;
    private String parentalRating = "";
    // private boolean kidsMode;
    private String tabId;
    @Override
    public ActivitySeriesDetailBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivitySeriesDetailBinding.inflate(inflater);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.black);
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            getBinding().sliderImage.getLayoutParams().width=WRAP_CONTENT;
//
//        }
//        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            getBinding().sliderImage.getLayoutParams().width=MATCH_PARENT;
//        }
       /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);*/
        shimmerCounter = 0;

        if (SDKConfig.getInstance().getSeriesDetailId().equalsIgnoreCase("")) {
            tabId = "10000";
        } else {
            tabId = SDKConfig.getInstance().getSeriesDetailId();
        }

        kidsMode  = SharedPrefHelper.getInstance(SeriesDetailActivity.this).getKidsMode();
        if (kidsMode) {
            parentalRating = AppCommonMethod.getParentalRating();
        }

        setupUI(getBinding().llParent);
        seriesId = getIntent().getIntExtra("seriesId", 0);
        new ToolBarHandler(this).setSeriesAction(getBinding());

        onSeriesCreate();
    }

    @Override
    protected void onPause() {
        dismissLoading(getBinding().progressBar);
        super.onPause();
    }

    public void onSeriesCreate() {
        if (shimmerCounter == 0) {
            callShimmer();
        }
        connectionObserver();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        shimmerCounter = 0;
        setupUI(getBinding().llParent);

        seriesId = intent.getIntExtra("seriesId", 0);
        new ToolBarHandler(this).setSeriesAction(getBinding());
        newIntentCall = true;
        onSeriesCreate();
    }

    private void callShimmer() {
        shimmerCounter = 1;
        getBinding().seriesShimmer.setVisibility(View.VISIBLE);
        getBinding().mShimmer.seriesShimmerScroll1.setEnabled(false);
        getBinding().mShimmer.seriesShimmerScroll2.setEnabled(false);
        getBinding().mShimmer.seriesShimmerScroll2.setEnabled(false);
        getBinding().llParent.setVisibility(View.GONE);
        getBinding().noConnectionLayout.setVisibility(View.GONE);
        getBinding().mShimmer.sfShimmer1.startShimmer();
        getBinding().mShimmer.sfShimmer2.startShimmer();
        getBinding().mShimmer.sfShimmer3.startShimmer();
        getBinding().mShimmer.flBackIconImage.bringToFront();
        getBinding().mShimmer.flBackIconImage.setOnClickListener(v -> onBackPressed());


    }

    public void stopShimmer() {
        if (isSeasonData && isRailData) {
            isSeasonData = false;
            isRailData = true;
            getBinding().seriesShimmer.setVisibility(View.GONE);
            getBinding().llParent.setVisibility(View.VISIBLE);
            getBinding().noConnectionLayout.setVisibility(View.GONE);
            getBinding().mShimmer.sfShimmer1.startShimmer();
            getBinding().mShimmer.sfShimmer2.startShimmer();
            getBinding().mShimmer.sfShimmer3.startShimmer();
            Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
            getBinding().llParent.startAnimation(aniFade);
            setExpandable();
        }

    }

    public void numberOfEpisodes(int size) {
        if (size == 1) {
            setCustomeFields(seriesDetailBean, size, getResources().getString(R.string.episode));
            // getBinding().vodCount.setText(size + " " + getResources().getString(R.string.episode));
        } else {
            setCustomeFields(seriesDetailBean, size, getResources().getString(R.string.episodes));
            //getBinding().vodCount.setText(size + " " + getResources().getString(R.string.episodes));
        }
    }

    private void setCustomeFields(EnveuVideoItemBean responseDetailPlayer, int size, String episode) {
        try {
            getBinding().vodCount.setText("");
            if (responseDetailPlayer.getParentalRating() != null && !responseDetailPlayer.getParentalRating().equalsIgnoreCase("")) {
                getBinding().vodCount.setText(responseDetailPlayer.getParentalRating() + " \u2022");
            }

            if (size > 0) {
                getBinding().vodCount.setText(getBinding().vodCount.getText().toString() + " " + size + " " + episode + " \u2022");
            }


            if (responseDetailPlayer.getCountry() != null && !responseDetailPlayer.getCountry().equalsIgnoreCase("")) {
                getBinding().vodCount.setText(getBinding().vodCount.getText().toString() + " " + responseDetailPlayer.getCountry() + " \u2022");
            }

            if (responseDetailPlayer.getCompany() != null && !responseDetailPlayer.getCompany().equalsIgnoreCase("")) {
                getBinding().vodCount.setText(getBinding().vodCount.getText().toString() + " " + responseDetailPlayer.getCompany() + " \u2022");
            }

            if (responseDetailPlayer.getYear() != null && !responseDetailPlayer.getYear().equalsIgnoreCase("")) {
                getBinding().vodCount.setText(getBinding().vodCount.getText().toString() + " " + responseDetailPlayer.getYear() + " \u2022");
            }

            if (getBinding().vodCount.getText().toString().trim().endsWith("\u2022")) {
                String customeF = getBinding().vodCount.getText().toString().substring(0, getBinding().vodCount.getText().toString().length() - 1);
                getBinding().vodCount.setText(customeF);
            }
            if (getBinding().vodCount.getText().toString().trim().equalsIgnoreCase("")) {
                // getBinding().customeFieldView.setVisibility(View.GONE);
            }
            updateVisibility(getBinding().iv4k, seriesDetailBean.getIs4k());
            updateVisibility(getBinding().ivSignLang, seriesDetailBean.getSignedLangEnabled());
            updateVisibility(getBinding().ivAudioDesc, seriesDetailBean.getIsAudioDesc());
            updateVisibility(getBinding().ivClosedCaption, seriesDetailBean.getIsClosedCaption());
            updateVisibility(getBinding().ivAudioTrack, seriesDetailBean.getIsSoundTrack());
        } catch (Exception ex) {
            Logger.w(ex);
        }
    }


    private void modelCall() {
        railInjectionHelper = ViewModelProviders.of(this).get(RailInjectionHelper.class);
        assetListContinue = new ArrayList<>();
        getBinding().noConnectionLayout.setVisibility(View.GONE);
        getBinding().player.setVisibility(View.VISIBLE);
        // getBinding().playerFooter.setSmoothScrollingEnabled(true);
        getBinding().playerFooter.setVisibility(View.VISIBLE);
        getBinding().flBackIconImage.setVisibility(View.VISIBLE);
        getBinding().bannerFrame.bringToFront();
        getBinding().backImage.bringToFront();
        getBinding().flBackIconImage.bringToFront();
        preference = KsPreferenceKeys.getInstance();
        isLogin = preference.getAppPrefLoginStatus();
        token = preference.getAppPrefAccessToken();
        viewModel = ViewModelProviders.of(this).get(SeriesViewModel.class);
/*
        kidsMode  = SharedPrefHelper.getInstance(SeriesDetailActivity.this).getKidsMode();


        if( !isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString()) && kidsMode ){
            getBinding().interactionSection.watchList.setVisibility(View.GONE);
            getBinding().interactionSection.llLike.setVisibility(View.GONE);

        }
        if(kidsMode){
            getBinding().interactionSection.download.setVisibility(View.GONE);
        }*/



        if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            AppUserModel signInResponseModel = AppUserModel.getInstance();
            if (signInResponseModel != null) {
                {
                    userName = signInResponseModel.getName();
                }

            }
        }
    }

    private void connectionObserver() {
        if (NetworkConnectivity.isOnline(this)) {
            connectionValidation(true);
        } else {
            connectionValidation(false);
        }
    }

    private void connectionValidation(Boolean aBoolean) {
        if (aBoolean) {
            getSeriesDetail();
        } else {
            noConnectionLayout();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dismissLoading(getBinding().progressBar);
        watchListCounter = 0;
        likeCounter = 0;
        AppCommonMethod.isSeriesPage = true;
        if (NetworkConnectivity.isOnline(this)) {
            Logger.d("isOnline");
        } else {
            noConnectionLayout();
        }

        if (preference != null && userInteractionFragment != null) {
            AppCommonMethod.callSocialAction(preference, userInteractionFragment);
        }
    }

    private void getSeriesDetail() {
        modelCall();
        postCommentClick();
        RailInjectionHelper railInjectionHelper = ViewModelProviders.of(this).get(RailInjectionHelper.class);
        railInjectionHelper.getSeriesDetailsV2(String.valueOf(seriesId),this).observe(SeriesDetailActivity.this, new Observer<ResponseModel>() {
            @Override
            public void onChanged(ResponseModel response) {
                if (response != null) {
                    if (response.getStatus().equalsIgnoreCase(APIStatus.START.name())) {

                    } else if (response.getStatus().equalsIgnoreCase(APIStatus.SUCCESS.name())) {
                        if (response.getBaseCategory() != null) {
                            RailCommonData enveuCommonResponse = (RailCommonData) response.getBaseCategory();
                            // enveuCommonResponse.getEnveuVideoItemBeans().get(0).getAssetCast();
                            parseSeriesData(enveuCommonResponse);
                        }
                    } else if (response.getStatus().equalsIgnoreCase(APIStatus.ERROR.name())) {
                        if (response.getErrorModel().getErrorCode() != 0) {
                            if (response.getErrorModel().getErrorCode() == AppConstants.RESPONSE_CODE_LOGOUT) {
                                if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                                    hitApiLogout();
                                }
                                // showDialog(SeriesDetailActivity.this.getResources().getString(R.string.error), getResources().getString(R.string.logged_out));
                            } else {
                                showDialog(SeriesDetailActivity.this.getResources().getString(R.string.error), getResources().getString(R.string.something_went_wrong));
                            }
                        }

                    } else if (response.getStatus().equalsIgnoreCase(APIStatus.FAILURE.name())) {
                        showDialog(SeriesDetailActivity.this.getResources().getString(R.string.error), getResources().getString(R.string.something_went_wrong));
                    }

                }
            }
        });
        /*railInjectionHelper.getSeriesDetails(String.valueOf(seriesId)).observe(SeriesDetailActivity.this, enveuCommonResponse -> {
            if (enveuCommonResponse != null && enveuCommonResponse.getEnveuVideoItemBeans().size() > 0) {
                if (enveuCommonResponse.getEnveuVideoItemBeans().get(0).getResponseCode() == AppConstants.RESPONSE_CODE_SUCCESS) {
                    Logger.e("enveuCommonResponse", "" + enveuCommonResponse.getEnveuVideoItemBeans().get(0).toString());
                    seriesDetailBean = enveuCommonResponse.getEnveuVideoItemBeans().get(0);
                    seriesId = seriesDetailBean.getId();
                    setUserInteractionFragment(seriesId);
                    setTabs();
                    setUiComponents(seriesDetailBean);
                    downloadHelper = new DownloadHelper(this, this);
                    downloadHelper.setAssetType(AppConstants.ContentType.EPISODE);

//                    downloadHelper.findVideo(seriesDetailBean.getBrightcoveVideoId());
                } else {
                    if (enveuCommonResponse.getEnveuVideoItemBeans().get(0).getResponseCode() == AppConstants.RESPONSE_CODE_LOGOUT) {
                        showDialog(SeriesDetailActivity.this.getResources().getString(R.string.error), getResources().getString(R.string.logged_out));
                    } else {
                        showDialog(SeriesDetailActivity.this.getResources().getString(R.string.error), getResources().getString(R.string.something_went_wrong));
                    }
                }
            }
        });*/
        getBinding().flBackIconImage.setOnClickListener(view -> onBackPressed());
    }

    private void parseSeriesData(RailCommonData enveuCommonResponse) {
        if (enveuCommonResponse != null && enveuCommonResponse.getEnveuVideoItemBeans().size() > 0) {
            Logger.d( "" + enveuCommonResponse.getEnveuVideoItemBeans().get(0).toString());
            seriesDetailBean = enveuCommonResponse.getEnveuVideoItemBeans().get(0);
            seriesId = seriesDetailBean.getId();
            setUserInteractionFragment(seriesId);
            setTabs();
            setUiComponents(seriesDetailBean);
            downloadHelper = new DownloadHelper(this, this);
            downloadHelper.setAssetType(MediaTypeConstants.getInstance().getEpisode());
            downloadHelper.setSeriesName(seriesDetailBean.getTitle());
           // ViewGroup.LayoutParams params = getBinding().tabLayout.getLayoutParams();
            //params.width = (int) getResources().getDimension(WindowManager.LayoutParams.MATCH_PARENT);
            getBinding().tabLayout.setTabIndicatorFullWidth(true);
            getBinding().tabLayout.setTabMode(TabLayout.MODE_FIXED);
            getBinding().tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            ViewGroup.LayoutParams params = getBinding().tabLayout.getLayoutParams();
            params.width = MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getBinding().tabLayout.setLayoutParams(params);


            //downloadHelper.findVideo(seriesDetailBean.getBrightcoveVideoId());
        } else {
            if (enveuCommonResponse.getEnveuVideoItemBeans().get(0).getResponseCode() == AppConstants.RESPONSE_CODE_LOGOUT) {
                if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                    hitApiLogout();
                }
                // showDialog(SeriesDetailActivity.this.getResources().getString(R.string.error), getResources().getString(R.string.logged_out));
            } else {
                showDialog(SeriesDetailActivity.this.getResources().getString(R.string.error), getResources().getString(R.string.something_went_wrong));
            }
        }
    }


    public void setUserInteractionFragment(int id) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putInt(AppConstants.BUNDLE_ASSET_ID, id);
        args.putSerializable(AppConstants.BUNDLE_SERIES_DETAIL, seriesDetailBean);
        args.putString(AppConstants.BUNDLE_TRAILER_REF_ID, seriesDetailBean.getTrailerReferenceId());

        userInteractionFragment = new UserInteractionFragment();
        userInteractionFragment.setArguments(args);
        transaction.replace(R.id.fragment_user_interaction, userInteractionFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        downloadHelper = new DownloadHelper(this, this, String.valueOf(seriesId), seriesDetailBean.getTitle(), MediaTypeConstants.getInstance().getEpisode(), seriesDetailBean,parentalRating);
        userInteractionFragment.setDownloadable(false);
    }

    public void setTabs() {
        if (newIntentCall) {
            newIntentCall = false;
            Bundle args = new Bundle();
            args.putString(AppConstants.BUNDLE_TAB_ID, tabId);
            //railFragment.setArguments(args);

            Bundle bundleSeason = new Bundle();
            bundleSeason.putInt(AppConstants.BUNDLE_ASSET_ID, seriesId);
            bundleSeason.putParcelableArrayList(AppConstants.BUNDLE_SEASON_ARRAY, seriesDetailBean.getSeasons());
            bundleSeason.putString(AppConstants.BUNDLE_SEASON_NAME, seriesDetailBean.getSeasonName());

            bundleSeason.putInt(AppConstants.BUNDLE_SEASON_COUNT, seriesDetailBean.getSeasonCount());
            seasonTabFragment.setArguments(bundleSeason);

            getSupportFragmentManager().beginTransaction().detach(seasonTabFragment).attach(seasonTabFragment).commit();
          //  getSupportFragmentManager().beginTransaction().detach(railFragment).attach(railFragment).commit();

            TabLayout.Tab tab = getBinding().tabLayout.getTabAt(0);
            tab.select();
        } else {
          //  railFragment = new RecommendationRailFragment();
            seasonTabFragment = new SeasonTabFragment();
            //  getBinding().tabLayout.setSelectedTabIndicatorGravity(INDICATOR_GRAVITY_BOTTOM);
            getBinding().tabLayout.setSelectedTabIndicatorGravity(TabLayout.INDICATOR_GRAVITY_TOP);
            episodeTabAdapter = new EpisodeTabAdapter(getSupportFragmentManager());

            Bundle args = new Bundle();
            args.putString(AppConstants.BUNDLE_TAB_ID, tabId);
           // railFragment.setArguments(args);

            Bundle bundleSeason = new Bundle();
            bundleSeason.putInt(AppConstants.BUNDLE_ASSET_ID, seriesId);
            bundleSeason.putParcelableArrayList(AppConstants.BUNDLE_SEASON_ARRAY, seriesDetailBean.getSeasons());
            bundleSeason.putString(AppConstants.BUNDLE_SEASON_NAME, seriesDetailBean.getSeasonName());
            bundleSeason.putInt(AppConstants.BUNDLE_SEASON_COUNT, seriesDetailBean.getSeasonCount());
            seasonTabFragment.setArguments(bundleSeason);

            episodeTabAdapter.addFragment(seasonTabFragment, getString(R.string.tab_heading_episodes));
           // episodeTabAdapter.addFragment(railFragment, getString(R.string.tab_heading_other));
            getBinding().viewPager.setAdapter(episodeTabAdapter);
            getBinding().viewPager.setOffscreenPageLimit(10);
            getBinding().tabLayout.setupWithViewPager(getBinding().viewPager);


            //AppCommonMethod.customTabWidth(getBinding().tabLayout);
            //AppCommonMethod.customTabWidth2(getBinding().tabLayout);

            downloadHelper.getAllEpisodesOfSeries(seriesDetailBean.getBrightcoveVideoId(), String.valueOf(seasonTabFragment.getSelectedSeason())).observe(this, new Observer<ArrayList<DownloadedEpisodes>>() {
                @Override
                public void onChanged(ArrayList<DownloadedEpisodes> downloadedEpisodes) {
                    downloadableEpisodes = downloadedEpisodes;

                    if (downloadedEpisodes.size() > 0) {
                        userInteractionFragment.setDownloadable(true);
                        setStatus();
                    } else
                        userInteractionFragment.setDownloadable(false);
                }
            });
        }

        getBinding().tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showLoading(getBinding().progressBar, true);
                new Handler().postDelayed(() -> dismissLoading(getBinding().progressBar), 1500);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getBinding().viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                getBinding().viewPager.measure(getBinding().viewPager.getMeasuredWidth(), getBinding().viewPager.getMeasuredHeight());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        changeTabsFont();




    }

    private void changeTabsFont() {
        //  Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/"+ Constants.FontStyle);
        try {
            ViewGroup vg = (ViewGroup) getBinding().tabLayout.getChildAt(0);
            int tabsCount = vg.getChildCount();
            for (int j = 0; j < tabsCount; j++) {
                ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
                int tabChildsCount = vgTab.getChildCount();
                for (int i = 0; i < tabChildsCount; i++) {
                    View tabViewChild = vgTab.getChildAt(i);
                    if (tabViewChild instanceof TextView) {
                        ((TextView) tabViewChild).setSingleLine();
                    }
                }
            }
        }catch (Exception ignored){

        }
    }

    private void setStatus() {
        for (DownloadedEpisodes episodes : downloadableEpisodes) {
            downloadHelper.getDownloadStatus(episodes.getVideoId(), new OfflineCallback<DownloadStatus>() {
                @Override
                public void onSuccess(DownloadStatus downloadStatus) {
                    if (!mFlag) {
                        switch (downloadStatus.getCode()) {
                            case DownloadStatus.STATUS_DOWNLOADING: {
                                userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.DOWNLOADING);
                                mFlag = true;
                            }
                            break;
                        }

                    }
                }

                @Override
                public void onFailure(Throwable throwable) {

                }
            });
        }
    }


    public void seriesLoader() {
        showLoading(getBinding().progressBar, true);
    }

    public void removeTab(int position) {
        if (getBinding().tabLayout.getTabCount() >= 1 && position <= getBinding().tabLayout.getTabCount()) {
            episodeTabAdapter.removeTabPage(position);
            ViewGroup.LayoutParams params = getBinding().tabLayout.getLayoutParams();
            params.width = (int) getResources().getDimension(R.dimen.tab_layout_single);


            getBinding().tabLayout.setLayoutParams(params);

        }
    }


    private void setUiComponents(EnveuVideoItemBean seriesResponse) {
        if (seriesResponse != null) {
            setCustomeFields(seriesDetailBean, 0, getResources().getString(R.string.episode));
            if (seriesResponse.getAssetCast().size() > 0 && !seriesResponse.getAssetCast().get(0).equalsIgnoreCase("")) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < seriesResponse.getAssetCast().size(); i++) {
                    if (i == seriesResponse.getAssetCast().size() - 1) {
                        stringBuilder = stringBuilder.append(seriesResponse.getAssetCast().get(i));

                    } else
                        stringBuilder = stringBuilder.append(seriesResponse.getAssetCast().get(i)).append(", ");
                }
                getBinding().setCasttext(" " + stringBuilder);
            } else {
                getBinding().llCastView.setVisibility(View.GONE);
            }
            if (seriesResponse.getAssetGenres().size() > 0 && !seriesResponse.getAssetGenres().get(0).equalsIgnoreCase("")) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < seriesResponse.getAssetGenres().size(); i++) {
                    if (i == seriesResponse.getAssetGenres().size() - 1) {
                        stringBuilder = stringBuilder.append(seriesResponse.getAssetGenres().get(i));
                    } else
                        stringBuilder = stringBuilder.append(seriesResponse.getAssetGenres().get(i)).append(", ");
                }
                getBinding().setCrewtext(" " + stringBuilder);
            } else {
                getBinding().llCrewView.setVisibility(View.GONE);
            }
            Logger.d("SeriesResponse: " + seriesResponse);

            getBinding().setPlaylistItem(seriesResponse);
            getBinding().bannerlabel.setText(seriesResponse.getName());
            getBinding().bannerlabel.setVisibility(View.INVISIBLE);
            getBinding().seriesTitle.setText(seriesResponse.getTitle());
           /* if (seriesResponse.getSeasonCount() == 0) {
                if (seriesResponse.getVodCount() == 1) {
                    getBinding().vodCount.setText(seriesResponse.getVodCount() + " " + getResources().getString(R.string.episode));
                } else {
                    getBinding().vodCount.setText(seriesResponse.getVodCount() + " " + getResources().getString(R.string.episodes));
                }
            } else {
                if (seriesResponse.getSeasonCount() == 1) {
                    getBinding().vodCount.setText(+seriesResponse.getSeasonCount() + " " + getResources().getString(R.string.season));
                } else {

                    getBinding().vodCount.setText(+seriesResponse.getSeasonCount() + " " + getResources().getString(R.string.seasons));
                }
            }*/
//            getBinding().seriesCount.setVisibility(View.GONE);
            if (seriesResponse.getDescription()!=null && seriesResponse.getDescription().equalsIgnoreCase("")){
                getBinding().descriptionText.setVisibility(View.GONE);
            }
            getBinding().setResponseApi(seriesResponse.getDescription().trim());
            count = 0;

            getBinding().interactionSection.llLike.setOnClickListener(view -> {
                String isLogin = preference.getAppPrefLoginStatus();
                if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                    showLoading(getBinding().progressBar, true);

                    if (likeCounter == 0)
                        hitApiAddLike();
                    else
                        hitApiRemoveLike();
                } else {
                    openLogin();
                }
            });

            getBinding().interactionSection.watchList.setOnClickListener(view -> {
                String isLogin = preference.getAppPrefLoginStatus();
                if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                    if (watchListCounter == 0)
                        hitApiAddWatchList();
                    else
                        hitApiRemoveList();
                } else {
                    openLogin();
                }
            });

            getBinding().interactionSection.shareWith.setOnClickListener(view -> {
                showLoading(getBinding().progressBar, false);
                if (SystemClock.elapsedRealtime() - mLastClickTime < 900) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                openShareDialogue();
            });
        }
    }

    private void setExpandable() {
        getBinding().expandableLayout.collapse();
        if (getBinding().descriptionText.isExpanded()) {
            getBinding().descriptionText.setEllipsize(null);
        } else {
            getBinding().descriptionText.setEllipsize(TextUtils.TruncateAt.END);
        }
        getBinding().setExpandabletext(getResources().getString(R.string.more));
        getBinding().expandableLayout.setOnExpansionUpdateListener(expansionFraction -> getBinding().lessButton.setRotation(0 * expansionFraction));
        getBinding().lessButton.setOnClickListener(this::clickExpandable);
    }

    public void clickExpandable(View view) {
        getBinding().descriptionText.toggle();
        getBinding().descriptionText.setEllipsis("...");
        if (getBinding().descriptionText.isExpanded()) {
            getBinding().descriptionText.setEllipsize(null);
        } else {
            getBinding().descriptionText.setEllipsize(TextUtils.TruncateAt.END);
        }

        if (getBinding().expandableLayout.isExpanded()) {
            getBinding().setExpandabletext(getResources().getString(R.string.more));

        } else {
            getBinding().setExpandabletext(getResources().getString(R.string.less));
        }
        if (view != null) {
            getBinding().expandableLayout.expand();
        }
        getBinding().expandableLayout.collapse();

    }

    public void hitApiIsWatchList() {
        JsonObject requestParam = new JsonObject();
        requestParam.addProperty(AppConstants.API_PARAM_CONTENT_ID, seriesId);
        requestParam.addProperty(AppConstants.API_PARAM_CONTENT_TYPE, "SERIES");

        viewModel.hitApiIsWatchList(token, requestParam).observe(SeriesDetailActivity.this, responseContentInWatchlist -> {
            if (responseContentInWatchlist.isStatus()) {
                watchListId = responseContentInWatchlist.getData().getId();
                setWatchList();
            } else {
                resetWatchList();
                if (responseContentInWatchlist.getResponseCode() == 401) {
                    isloggedout = true;
                    if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                        hitApiLogout();
                    }
                    //   showDialog(SeriesDetailActivity.this.getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));

                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        AppCommonMethod.isSeriesPage = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppCommonMethod.seasonId = -1;
        preference.setAppPrefAssetId(0);
        preference.setAppPrefJumpTo("");
        preference.setAppPrefBranchIo(false);
    }


    public void hitApiAddLike() {

        JsonObject requestParam = new JsonObject();
        requestParam.addProperty(AppConstants.API_PARAM_LIKE_ID, seriesId);
        requestParam.addProperty(AppConstants.API_PARAM_LIKE_TYPE, MediaTypeConstants.getInstance().getSeries());
        viewModel.hitApiAddLike(token, requestParam).observe(SeriesDetailActivity.this, responseContentInWatchlist -> {
            dismissLoading(getBinding().progressBar);
            if (responseContentInWatchlist.isStatus()) {
                setLike();
            } else {
                if (responseContentInWatchlist.getResponseCode() == 401) {
                    isloggedout = true;
                    if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                        hitApiLogout();
                    }
                    // showDialog(SeriesDetailActivity.this.getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));
                }
            }
        });

    }

    public void hitApiIsLike() {
        JsonObject requestParam = new JsonObject();
        requestParam.addProperty(AppConstants.API_PARAM_LIKE_ID, seriesId);
        requestParam.addProperty(AppConstants.API_PARAM_LIKE_TYPE, MediaTypeConstants.getInstance().getSeries());
        viewModel.hitApiIsLike(token, requestParam).observe(SeriesDetailActivity.this, responseContentInWatchlist -> {
            if (responseContentInWatchlist.isStatus()) {
                if (responseContentInWatchlist.getData().isIsLike()) {
                    setLike();
                } else {
                    resetLike();
                }
            } else {
               /* if (responseContentInWatchlist.getResponseCode() == 401) {
                    isloggedout = true;
                    showDialog(SeriesDetailActivity.this.getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));
                }*/
            }
        });
    }

    public void setLike() {
        likeCounter = 1;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            ImageViewCompat.setImageTintList(getBinding().interactionSection.likeIcon, ColorStateList.valueOf(getResources().getColor(R.color.dialog_green_color)));
            getBinding().interactionSection.tvLike.setTextColor(ContextCompat.getColor(SeriesDetailActivity.this, R.color.dialog_green_color));
        } else {
            // do something for phones running an SDK before lollipop
            ImageViewCompat.setImageTintList(getBinding().interactionSection.likeIcon, ColorStateList.valueOf(getResources().getColor(R.color.dialog_green_color)));
            getBinding().interactionSection.tvLike.setTextColor(getResources().getColor(R.color.dialog_green_color));
        }

    }

    public void resetLike() {
        likeCounter = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            getBinding().interactionSection.tvLike.setTextColor(ContextCompat.getColor(SeriesDetailActivity.this, R.color.white));

            ImageViewCompat.setImageTintList(getBinding().interactionSection.likeIcon, ColorStateList.valueOf(getResources().getColor(R.color.white)));
        } else {
            // do something for phones running an SDK before lollipop
            getBinding().interactionSection.tvLike.setTextColor(getResources().getColor(R.color.white));
            ImageViewCompat.setImageTintList(getBinding().interactionSection.likeIcon, ColorStateList.valueOf(getResources().getColor(R.color.white)));
        }

    }

    private void openShareDialogue() {
        //   String imgUrl = AppCommonMethod.urlPoints + AppConstants.SERIES_IMAGES_BASE_KEY + seriesDetailBean..getPicture();
        String imgUrl = seriesDetailBean.getPosterURL();
        int id = seriesDetailBean.getId();
        String title = seriesDetailBean.getTitle();
        Logger.d("seriesDetailBean: " + seriesDetailBean);
        imgUrl = AppCommonMethod.getBranchUrl(imgUrl,SeriesDetailActivity.this);
        AppCommonMethod.openShareDialog(SeriesDetailActivity.this, title, id, MediaTypeConstants.getInstance().getSeries(), imgUrl, String.valueOf(seriesId), seriesDetailBean.getSeason());
        new Handler().postDelayed(() -> dismissLoading(getBinding().progressBar), 2000);
    }

    private void noConnectionLayout() {
        stopShimmer();
        getBinding().llParent.setVisibility(View.GONE);
        getBinding().noConnectionLayout.setVisibility(View.VISIBLE);
        getBinding().connection.retryTxt.setOnClickListener(view -> {
            callShimmer();
            connectionObserver();
        });
    }


    public void hitApiRemoveList() {
        showLoading(getBinding().progressBar, true);
        JsonObject requestParam = new JsonObject();
        requestParam.addProperty(AppConstants.API_PARAM_WATCHLIST_ID, watchListId);
        viewModel.hitApiRemoveWatchList(token, String.valueOf(watchListId)).observe(SeriesDetailActivity.this, responseWatchList -> {
            dismissLoading(getBinding().progressBar);
            if (responseWatchList.isStatus()) {
                getBinding().interactionSection.addIcon.setImageResource(R.drawable.add_to_watchlist);
                resetWatchList();
                Logger.d( "hitApiAddWatchList");
            } else {
                if (responseWatchList.getResponseCode() == 401) {
                    isloggedout = true;
                    if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                        hitApiLogout();
                    }
                    //   showDialog(SeriesDetailActivity.this.getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));
                }

                Logger.d("something went wrong");
                // new ToastHandler(SeriesDetailActivity.this).show(SeriesDetailActivity.this.getResources().getString(R.string.something_went_wrong));
            }
        });

    }

    public void hitApiAddWatchList() {
        showLoading(getBinding().progressBar, true);
        JsonObject requestParam = new JsonObject();
        requestParam.addProperty(AppConstants.API_PARAM_CONTENT_ID, seriesId);
        requestParam.addProperty(AppConstants.API_PARAM_CONTENT_TYPE, "SERIES");
        viewModel.hitApiAddWatchList(token, requestParam).observe(SeriesDetailActivity.this, responseWatchList -> {
            dismissLoading(getBinding().progressBar);
            if (responseWatchList.isStatus()) {
                watchListId = responseWatchList.getData().getId();
                setWatchList();
            } else {
                if (responseWatchList.getResponseCode() == 401) {
                    isloggedout = true;
                    if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                        hitApiLogout();
                    }
                    //  showDialog(SeriesDetailActivity.this.getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));
                }
                Logger.d("hitApiAddWatchList");
            }
        });

    }

    private void showDialog(String title, String message) {
        FragmentManager fm = getSupportFragmentManager();
        AlertDialogSingleButtonFragment alertDialog = AlertDialogSingleButtonFragment.newInstance(title, message, getResources().getString(R.string.ok));
        alertDialog.setCancelable(false);
        alertDialog.setAlertDialogCallBack(this);
        alertDialog.show(fm, "fragment_alert");
    }

    public void setWatchList() {
        watchListCounter = 1;
        getBinding().interactionSection.addIcon.setImageResource(R.drawable.check_icon);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            ImageViewCompat.setImageTintList(getBinding().interactionSection.addIcon, ColorStateList.valueOf(getResources().getColor(R.color.dialog_green_color)));
            getBinding().interactionSection.tvWatch.setTextColor(ContextCompat.getColor(SeriesDetailActivity.this, R.color.dialog_green_color));
        } else {
            // do something for phones running an SDK before lollipop
            ImageViewCompat.setImageTintList(getBinding().interactionSection.addIcon, ColorStateList.valueOf(getResources().getColor(R.color.dialog_green_color)));
            getBinding().interactionSection.tvWatch.setTextColor(getResources().getColor(R.color.dialog_green_color));
        }

    }

    public void resetWatchList() {
        watchListCounter = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            getBinding().interactionSection.tvWatch.setTextColor(ContextCompat.getColor(SeriesDetailActivity.this, R.color.white));
            ImageViewCompat.setImageTintList(getBinding().interactionSection.addIcon, ColorStateList.valueOf(getResources().getColor(R.color.white)));
        } else {
            // do something for phones running an SDK before lollipop
            getBinding().interactionSection.tvWatch.setTextColor(getResources().getColor(R.color.white));
            ImageViewCompat.setImageTintList(getBinding().interactionSection.addIcon, ColorStateList.valueOf(getResources().getColor(R.color.white)));
        }
    }

    public void hitApiRemoveLike() {
        JsonObject requestParam = new JsonObject();
        requestParam.addProperty(AppConstants.API_PARAM_LIKE_ID, seriesId);
        requestParam.addProperty(AppConstants.API_PARAM_LIKE_TYPE, MediaTypeConstants.getInstance().getSeries());
        viewModel.hitApiUnLike(token, requestParam).observe(SeriesDetailActivity.this, responseContentInWatchlist -> {
            dismissLoading(getBinding().progressBar);
            if (responseContentInWatchlist.isStatus()) {
                resetLike();
            } else {
                if (responseContentInWatchlist.getResponseCode() == 401) {
                    isloggedout = true;
                    if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                        hitApiLogout();
                    }
                    //    showDialog(SeriesDetailActivity.this.getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));
                }
                Logger.d("hitApiAddWatchList");
            }
        });
    }


    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        if (commentsFragment != null) {
            removeCommentFragment();
        } else {
            if (preference == null)
                preference = KsPreferenceKeys.getInstance();
            if (preference.getAppPrefJumpBack()) {
                preference.setAppPrefJumpBackId(0);
                preference.setAppPrefJumpBack(false);
            }
            // fragment.releasePlayer();
            AppCommonMethod.seasonId = -1;
            AppCommonMethod.isSeasonCount = false;
            this.finish();
        }

    }


    public void commentFragment(int id) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        commentsFragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putInt(AppConstants.BUNDLE_ID_FOR_COMMENTS, id);
        args.putString(AppConstants.BUNDLE_TYPE_FOR_COMMENTS, MediaTypeConstants.getInstance().getSeries());
        commentsFragment.setArguments(args);
        transaction.replace(R.id.fragment_comment, commentsFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void removeCommentFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (commentsFragment != null) {
            transaction.remove(commentsFragment);
            transaction.commit();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            commentsFragment = null;
            getBinding().playerFooter.setVisibility(View.VISIBLE);
            getBinding().fragmentComment.setVisibility(View.GONE);
            // getBinding().interactionSection
            getBinding().interactionSection.llLike.setVisibility(View.VISIBLE);
            getBinding().interactionSection.shareWith.setVisibility(View.VISIBLE);
            // getBinding().interactionSection.showComments.setVisibility(View.VISIBLE);
        }
    }


    public void postCommentClick() {
     /*   getBinding().interactionSection.showComments.setOnClickListener(v -> {
            openCommentSection();
        });*/
    }

    public void openCommentSection() {
        commentFragment(163);
        getBinding().interactionSection.showComments.setVisibility(View.GONE);
        getBinding().playerFooter.setVisibility(View.GONE);
        getBinding().fragmentComment.setVisibility(View.VISIBLE);
    }


    public void openLoginPage(String message) {
        preference.setAppPrefJumpTo(MediaTypeConstants.getInstance().getSeries());
        preference.setAppPrefJumpBack(true);
        preference.setAppPrefJumpBackId(seriesId);
        ActivityLauncher.getInstance().loginActivity(SeriesDetailActivity.this, LoginActivity.class);

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            Log.d("Daiya", "ORIENTATION_LANDSCAPE");

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("Daiya", "ORIENTATION_PORTRAIT");
        }
    }

    @Override
    public void onFinishDialog() {
        isloggedout = false;
        if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            hitApiLogout();
        }
        finish();
    }


    public void hitApiLogout() {
        isloggedout = false;
        if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            hitApiLogout(SeriesDetailActivity.this, preference.getAppPrefAccessToken());
        }
    }


    public void openLogin() {
        preference.setAppPrefJumpTo(getResources().getString(R.string.series));
        preference.setAppPrefJumpBack(true);
        preference.setAppPrefJumpBackId(seriesId);
        ActivityLauncher.getInstance().loginActivity(SeriesDetailActivity.this, LoginActivity.class);

    }

    public void showSeasonList(ArrayList<SelectedSeasonModel> list, int selectedSeasonId) {
        getBinding().transparentLayout.setVisibility(View.VISIBLE);

        SeasonListAdapter listAdapter = new SeasonListAdapter(list, selectedSeasonId);
        builder = new AlertDialog.Builder(SeriesDetailActivity.this);
        LayoutInflater inflater = LayoutInflater.from(SeriesDetailActivity.this);
        View content = inflater.inflate(R.layout.season_custom_dialog, null);
        builder.setView(content);
        RecyclerView mRecyclerView = content.findViewById(R.id.my_recycler_view);
        ImageView imageView = content.findViewById(R.id.close);
        imageView.setOnClickListener(v -> {
            alertDialog.cancel();
            getBinding().transparentLayout.setVisibility(View.GONE);
        });

        //Creating Adapter to fill data in Dialog
        mRecyclerView.setLayoutManager(new LinearLayoutManager(SeriesDetailActivity.this));
        mRecyclerView.setAdapter(listAdapter);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(ActivityCompat.getDrawable(SeriesDetailActivity.this, R.color.transparent_series));
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;

        alertDialog.show();
        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(alertDialog.getWindow().getAttributes());
        lWindowParams.width = MATCH_PARENT; // this is where the magic happens
        lWindowParams.height = MATCH_PARENT;
        alertDialog.getWindow().setAttributes(lWindowParams);

    }

    @Override
    public void onDownloadClicked(String videoId, Object position, Object source) {
        try {

            boolean loginStatus = preference.getAppPrefLoginStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString());
            if (!loginStatus)
                ActivityLauncher.getInstance().loginActivity(this, LoginActivity.class);
            else {
                int videoQuality = SharedPrefHelper.getInstance(this).getInt(SharedPrefesConstants.DOWNLOAD_QUALITY_INDEX, 4);
                if (source instanceof UserInteractionFragment) {
                    if (KsPreferenceKeys.getInstance().getDownloadOverWifi() == 1 && NetworkHelper.INSTANCE.isWifiEnabled(this)) {
                        downloadHelper.findVideo(seriesDetailBean.getBrightcoveVideoId(), new VideoListener() {
                            @Override
                            public void onVideo(Video video) {
                                downloadHelper.startSeriesDownload(seriesDetailBean.getBrightcoveVideoId(), seasonTabFragment.getSelectedSeason(), seasonTabFragment.getSeasonAdapter().getSeasonEpisodes(), videoQuality);
                            }

                            @Override
                            public void onError(String error) {
                                super.onError(error);
                                Logger.d(error);
                            }

                            @Override
                            public void onError(@NonNull List<CatalogError> errors) {
                                super.onError(errors);
                                for (CatalogError error : errors) {
                                    Logger.d("Error: " + error);
                                }
                            }
                        });
                    } else {
                        //Toast.makeText(SeriesDetailActivity.this, "NoWifi", Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (KsPreferenceKeys.getInstance().getDownloadOverWifi() == 1 && NetworkHelper.INSTANCE.isWifiEnabled(this)) {
                        downloadHelper.findVideo(String.valueOf(videoId), new VideoListener() {
                            @Override
                            public void onVideo(Video video) {
                                if ((KsPreferenceKeys.getInstance().getDownloadOverWifi() == 1)) {
                                    if (NetworkHelper.INSTANCE.isWifiEnabled(SeriesDetailActivity.this)) {
                                        if (videoQuality != 4) {
                                            downloadHelper.startEpisodeDownload(video, String.valueOf(seriesDetailBean.getBrightcoveVideoId()), seasonTabFragment.getSelectedSeason(), seasonTabFragment.getSeasonAdapter().getEpisodeNumber(videoId), videoQuality,parentalRating);
                                        } else {
                                            selectDownloadVideoQuality(video, videoId);
                                        }
                                    } else {
                                        //  Toast.makeText(SeriesDetailActivity.this, "NoWifi", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    if (videoQuality != 4) {
                                        downloadHelper.startEpisodeDownload(video, String.valueOf(seriesDetailBean.getBrightcoveVideoId()), seasonTabFragment.getSelectedSeason(), seasonTabFragment.getSeasonAdapter().getEpisodeNumber(videoId), videoQuality,parentalRating);
                                    } else {
                                        selectDownloadVideoQuality(video, videoId);
                                    }
                                }
                            }

                            @Override
                            public void onError(String error) {
                                super.onError(error);
                                Logger.d(error);
                            }
                        });
                    } else {
                        if (KsPreferenceKeys.getInstance().getDownloadOverWifi() == 1) {
                            showWifiSettings(videoId, videoQuality);
                        } else {
                            downloadHelper.findVideo(String.valueOf(videoId), new VideoListener() {
                                @Override
                                public void onVideo(Video video) {
                                    if (videoQuality != 4) {
                                        if (downloadHelper.getCatalog() != null) {
                                            downloadHelper.allowedMobileDownload();
                                            if (seasonTabFragment!=null){
                                                seasonTabFragment.updateStatus();
                                            }
                                            downloadHelper.startEpisodeDownload(video, String.valueOf(seriesDetailBean.getBrightcoveVideoId()), seasonTabFragment.getSelectedSeason(), seasonTabFragment.getSeasonAdapter().getEpisodeNumber(videoId), videoQuality,parentalRating);
                                        }
                                    } else {
                                        if (downloadHelper.getCatalog() != null) {
                                            downloadHelper.allowedMobileDownload();
                                            selectDownloadVideoQuality(video, videoId);
                                        }

                                    }

                                }

                                @Override
                                public void onError(String error) {
                                    super.onError(error);
                                    Logger.d(error);
                                }
                            });

                        }

                    }

                }
            }
        }catch (Exception exception){

        }
    }

    private void showWifiSettings(String videoId, int videoQuality) {
        downloadHelper.changeWifiSetting(new WifiPreferenceListener() {
            @Override
            public void actionP(int value) {
                if (value == 0) {
                    if (downloadHelper.getCatalog() != null) {
                        downloadHelper.allowedMobileDownload();
                        downloadHelper.findVideo(String.valueOf(videoId), new VideoListener() {
                            @Override
                            public void onVideo(Video video) {
                                if ((KsPreferenceKeys.getInstance().getDownloadOverWifi() == 1)) {
                                    if (NetworkHelper.INSTANCE.isWifiEnabled(SeriesDetailActivity.this)) {
                                        if (videoQuality != 4) {
                                            downloadHelper.startEpisodeDownload(video, String.valueOf(seriesDetailBean.getBrightcoveVideoId()), seasonTabFragment.getSelectedSeason(), seasonTabFragment.getSeasonAdapter().getEpisodeNumber(videoId), videoQuality,parentalRating);
                                        } else {
                                            selectDownloadVideoQuality(video, videoId);
                                        }
                                    } else {
                                        Toast.makeText(SeriesDetailActivity.this, "NoWifi", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    if (videoQuality != 4) {
                                        downloadHelper.startEpisodeDownload(video, String.valueOf(seriesDetailBean.getBrightcoveVideoId()), seasonTabFragment.getSelectedSeason(), seasonTabFragment.getSeasonAdapter().getEpisodeNumber(videoId), videoQuality,parentalRating);
                                    } else {
                                        selectDownloadVideoQuality(video, videoId);
                                    }
                                }
                            }

                            @Override
                            public void onError(String error) {
                                super.onError(error);
                                Logger.d(error);
                            }
                        });
                    } else {
                        Toast.makeText(SeriesDetailActivity.this, SeriesDetailActivity.this.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }


    private void selectDownloadVideoQuality(Video video, String videoId) {
        downloadHelper.selectVideoQuality(position -> {
            if (seasonTabFragment!=null){
                seasonTabFragment.updateStatus();
            }
            downloadHelper.startEpisodeDownload(video, String.valueOf(seriesDetailBean.getBrightcoveVideoId()), seasonTabFragment.getSelectedSeason(), seasonTabFragment.getSeasonAdapter().getEpisodeNumber(videoId), position,parentalRating);
        });
    }

    private List<EnveuVideoItemBean> getDownloadableEpisodes
            (List<EnveuVideoItemBean> seasonEpisodes) {
        List<EnveuVideoItemBean> downloadableEpisodes = new ArrayList<>();
        for (EnveuVideoItemBean enveuVideoItemBean : seasonEpisodes) {
            downloadHelper.findVideo(enveuVideoItemBean.getBrightcoveVideoId(), new VideoListener() {
                @Override
                public void onVideo(Video video) {
                    if (video.isOfflinePlaybackAllowed()) {
                        Logger.d(String.valueOf(video.isOfflinePlaybackAllowed()));
                        downloadableEpisodes.add(enveuVideoItemBean);
                    }
                }
            });
        }
        return downloadableEpisodes;
    }

    @Override
    public void onProgressbarClicked(View view, Object source, String videoId) {
        if (source instanceof UserInteractionFragment) {
            AppCommonMethod.showPopupMenu(this, view, R.menu.download_menu, item -> {
                switch (item.getItemId()) {
                    case R.id.cancel_download:
                        downloadHelper.cancelVideo(downloadAbleVideo.getId());
                        break;
                    case R.id.pause_download:
                        downloadHelper.pauseVideo();
                        break;
                }
                return false;
            });
        } else {
            AppCommonMethod.showPopupMenu(this, view, R.menu.download_menu, item -> {
                switch (item.getItemId()) {
                    case R.id.cancel_download:
                        downloadHelper.cancelVideo(videoId);
                        if (videoId.equals(String.valueOf(seriesId)))
                            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.START);
                        break;
                    case R.id.pause_download:
                        downloadHelper.pauseVideo(videoId);
                        if (videoId.equals(String.valueOf(seriesId)))
                            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.PAUSE);
                        break;
                }
                return false;
            });
        }
    }

    @Override
    public void onDownloadCompleteClicked(View view, Object source, String videoId) {
        if (source instanceof UserInteractionFragment) {
            AppCommonMethod.showPopupMenu(this, view, R.menu.delete_menu, item -> {
                switch (item.getItemId()) {
                    case R.id.delete_download:
                        downloadHelper.deleteVideo(downloadAbleVideo);
                        break;
                    case R.id.my_Download:
                        ActivityLauncher.getInstance().launchMyDownloads(this);
                        break;
                }
                return false;
            });
        } else {
            if (videoId.equals(String.valueOf(seriesId)))
                userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.START);
        }
    }

    @Override
    public void onPauseClicked(String videoId, Object source) {
        if (source instanceof UserInteractionFragment) {
            downloadHelper.resumeDownload(downloadAbleVideo.getId());
        } else {
            downloadHelper.resumeDownload(videoId);
        }
    }

    @Override
    public void onDownloadRequested(@androidx.annotation.NonNull Video video) {
        Logger.d(String.format(
                "Starting to process '%s' video download request", video.getName()));
        if (seasonTabFragment.getSeasonAdapter() != null) {
            seasonTabFragment.getSeasonAdapter().onDownloadRequested(video);
        }
    }

    @Override
    public void onDownloadStarted(@androidx.annotation.NonNull Video video, long l,
                                  @androidx.annotation.NonNull Map<String, Serializable> map) {
        Logger.d("onDownloadStarted");
        if (downloadAbleVideo != null && video.getId().equals(downloadAbleVideo.getId()))
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.DOWNLOADING);
        if (seasonTabFragment.getSeasonAdapter() != null) {
            seasonTabFragment.getSeasonAdapter().onDownloadStarted(video, l, map);
        }
        if (seasonTabFragment!=null){
            seasonTabFragment.updateStatus();
        }
    }

    @Override
    public void onDownloadProgress(@androidx.annotation.NonNull Video
                                           video, @androidx.annotation.NonNull com.brightcove.player.network.DownloadStatus
                                           downloadStatus) {
        Logger.d("onDownloadProgress" + downloadStatus.getProgress());
        if (downloadAbleVideo != null && video.getId().equals(downloadAbleVideo.getId())) {
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.DOWNLOADING);
            userInteractionFragment.setDownloadProgress((float) downloadStatus.getProgress());
        }
        if (seasonTabFragment.getSeasonAdapter() != null) {
            seasonTabFragment.getSeasonAdapter().onDownloadProgress(video, downloadStatus);
        }
    }

    @Override
    public void onDownloadPaused(@androidx.annotation.NonNull Video
                                         video, @androidx.annotation.NonNull com.brightcove.player.network.DownloadStatus
                                         downloadStatus) {
        Logger.d("onDownloadPaused");
        if (downloadAbleVideo != null && video.getId().equals(downloadAbleVideo.getId()))
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.PAUSE);
        if (seasonTabFragment.getSeasonAdapter() != null) {
            seasonTabFragment.getSeasonAdapter().onDownloadPaused(video, downloadStatus);
        }
    }

    @Override
    public void onDownloadCompleted(@androidx.annotation.NonNull Video
                                            video, @androidx.annotation.NonNull com.brightcove.player.network.DownloadStatus
                                            downloadStatus) {
        Logger.d("onDownloadCompleted");
        if (downloadAbleVideo != null && video.getId().equals(downloadAbleVideo.getId()))
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.DOWNLOADED);
        seasonTabFragment.getSeasonAdapter().onDownloadCompleted(video, downloadStatus);
        downloadHelper.updateVideoStatus(com.brightcove.player.network.DownloadStatus.STATUS_COMPLETE, video.getId());
    }

    @Override
    public void onDownloadCanceled(@androidx.annotation.NonNull Video video) {
        Logger.d("onDownloadCanceled");
        if (downloadAbleVideo != null && video.getId().equals(downloadAbleVideo.getId())) {
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.START);
            userInteractionFragment.setDownloadProgress(0);
        }
        if (seasonTabFragment.getSeasonAdapter() != null) {
            seasonTabFragment.getSeasonAdapter().onDownloadCanceled(video);
        }
    }

    @Override
    public void onDownloadDeleted(@androidx.annotation.NonNull Video video) {
        Logger.d("onDownloadDeleted");
        if (downloadAbleVideo != null && video.getId().equals(downloadAbleVideo.getId()))
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.START);
        seasonTabFragment.getSeasonAdapter().onDownloadDeleted(video);
    }

    @Override
    public void onDownloadFailed(@androidx.annotation.NonNull Video
                                         video, @androidx.annotation.NonNull com.brightcove.player.network.DownloadStatus
                                         downloadStatus) {
        Logger.d("onDownloadFailed");

    }

    @Override
    public void downloadVideo(@androidx.annotation.NonNull Video video) {

    }

    @Override
    public void pauseVideoDownload(Video video) {
        if (downloadAbleVideo != null && video.getId().equals(downloadAbleVideo.getId()))
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.DOWNLOADING);

    }

    @Override
    public void resumeVideoDownload(Video video) {
        if (downloadAbleVideo != null && video.getId().equals(downloadAbleVideo.getId()))
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.PAUSE);
    }

    @Override
    public void deleteVideo(@androidx.annotation.NonNull Video video) {
        downloadHelper = new DownloadHelper(this, this);
        downloadHelper.setAssetType(MediaTypeConstants.getInstance().getEpisode());
        downloadHelper.setSeriesName(seriesDetailBean.getTitle());
    }

    @Override
    public void alreadyDownloaded(@androidx.annotation.NonNull Video video) {
        if (downloadAbleVideo != null && video.getId().equals(downloadAbleVideo.getId()))
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.DOWNLOADED);
    }

    @Override
    public void downloadedVideos(@org.jetbrains.annotations.Nullable List<? extends
            Video> p0) {

    }

    @Override
    public void videoFound(Video video) {
//        this.downloadAbleVideo = video;
//        userInteractionFragment.setDownloadable(downloadAbleVideo.isOfflinePlaybackAllowed());
    }

    @Override
    public void downloadStatus(String videoId, DownloadStatus downloadStatus) {
        Logger.d("onDownloadFailed" + downloadStatus);
    }

    @Override
    public void onDownloadDeleted(@NotNull String videoId, @NotNull Object source) {
        Logger.d("onDownloadDeleted" + videoId);
        try {
            downloadHelper = new DownloadHelper(this, this);
            downloadHelper.setAssetType(MediaTypeConstants.getInstance().getEpisode());
            downloadHelper.setSeriesName(seriesDetailBean.getTitle());
        } catch (Exception ignored) {

        }

    }

    class SeasonListAdapter extends RecyclerView.Adapter<SeasonListAdapter.ViewHolder> {
        private final ArrayList<SelectedSeasonModel> list;
        private int selectedPos;

        //TrackGroup list;
        public SeasonListAdapter(ArrayList<SelectedSeasonModel> list, int selectedPos) {
            this.list = list;
            this.selectedPos = selectedPos;
        }

        @NonNull
        @Override
        public SeasonListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_season_listing, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            holder.season.setText(list.get(position).getList());
            if (list.get(position).isSelected()) {
                holder.season.setTextColor(getResources().getColor(R.color.description_title_yellow));
                Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                holder.season.setTypeface(boldTypeface);

            } else {
                holder.season.setTextColor(getResources().getColor(R.color.bottom_nav_color_f));
                Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.NORMAL);
                holder.season.setTypeface(boldTypeface);

            }

            holder.season.setOnClickListener(v -> {
                alertDialog.cancel();
                getBinding().transparentLayout.setVisibility(View.GONE);
                if (seasonTabFragment != null) {
                    seasonTabFragment.updateTotalPages();
                    seasonTabFragment.setSeasonAdapter(null);
                    seasonTabFragment.setSelectedSeason(list.get(position).getSelectedId());
                    showLoading(getBinding().progressBar, true);
                    seasonTabFragment.getSeasonEpisodes();
                }

            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            TextView season;

            public ViewHolder(View itemView) {
                super(itemView);
                season = itemView.findViewById(R.id.season_name);
            }
        }
    }


}
