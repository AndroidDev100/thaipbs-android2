package me.vipa.app.activities.detail.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.brightcove.player.edge.OfflineCallback;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.model.Video;
import com.brightcove.player.network.DownloadStatus;
import com.brightcove.player.offline.MediaDownloadable;
import com.brightcove.player.pictureinpicture.PictureInPictureManager;

import me.vipa.bookmarking.bean.GetBookmarkResponse;

import me.vipa.app.activities.purchase.callBack.EntitlementStatus;
import me.vipa.app.activities.purchase.planslayer.GetPlansLayer;
import me.vipa.app.beanModel.entitle.EntitledAs;
import me.vipa.app.utils.helpers.CheckInternetConnection;
import me.vipa.app.utils.helpers.ImageHelper;

import com.google.gson.Gson;
import com.mmtv.utils.helpers.downloads.DownloadHelper;

import me.vipa.app.Bookmarking.BookmarkingViewModel;
import me.vipa.app.activities.chromecast.ExpandedControlsActivity;
import me.vipa.app.activities.detail.adapter.AllCommentAdapter;
import me.vipa.app.activities.downloads.NetworkHelper;
import me.vipa.app.activities.downloads.WifiPreferenceListener;
import me.vipa.app.activities.listing.listui.ListActivity;
import me.vipa.app.activities.usermanagment.ui.LoginActivity;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.beanModel.allComments.ItemsItem;
import me.vipa.app.beanModel.entitle.ResponseEntitle;
import me.vipa.app.beanModel.responseModels.detailPlayer.Data;
import me.vipa.app.beanModel.responseModels.detailPlayer.ResponseDetailPlayer;
import me.vipa.app.beanModel.responseModels.series.SeriesResponse;
import me.vipa.app.beanModel.responseModels.series.season.SeasonResponse;
import me.vipa.app.beanModel.selectedSeason.SelectedSeasonModel;
import me.vipa.app.callbacks.commonCallbacks.CommonRailtItemClickListner;
import me.vipa.app.callbacks.commonCallbacks.MoreClickListner;
import me.vipa.app.callbacks.commonCallbacks.NetworkChangeReceiver;
import me.vipa.app.fragments.dialog.AlertDialogFragment;
import me.vipa.app.fragments.dialog.AlertDialogSingleButtonFragment;
import me.vipa.app.fragments.player.ui.CommentsFragment;
import me.vipa.app.fragments.player.ui.NontonPlayerExtended;
import me.vipa.app.fragments.player.ui.RecommendationRailFragment;
import me.vipa.app.fragments.player.ui.SeasonTabFragment;
import me.vipa.app.fragments.player.ui.UserInteractionFragment;
import me.vipa.app.networking.apistatus.APIStatus;
import me.vipa.app.networking.responsehandler.ResponseModel;
import me.vipa.app.SDKConfig;

import me.vipa.app.utils.MediaTypeConstants;
import me.vipa.app.utils.constants.SharedPrefesConstants;
import me.vipa.app.utils.helpers.SharedPrefHelper;
import me.vipa.app.utils.helpers.downloads.OnDownloadClickInteraction;
import me.vipa.app.utils.helpers.downloads.VideoListListener;
import me.vipa.enums.Layouts;
import me.vipa.brightcovelibrary.BrightcovePlayerFragment;
import me.vipa.app.R;
import me.vipa.app.activities.detail.viewModel.DetailViewModel;
import me.vipa.app.activities.purchase.ui.PurchaseActivity;
import me.vipa.app.activities.purchase.ui.VodOfferType;
import me.vipa.app.adapters.player.EpisodeTabAdapter;
import me.vipa.app.beanModel.AppUserModel;
import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import me.vipa.app.databinding.EpisodeScreenBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.cropImage.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.RailInjectionHelper;

import me.vipa.app.utils.helpers.StringUtils;
import me.vipa.app.utils.helpers.ToastHandler;
import me.vipa.app.utils.helpers.ToolBarHandler;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import me.vipa.app.Bookmarking.BookmarkingViewModel;
import me.vipa.app.activities.chromecast.ExpandedControlsActivity;
import me.vipa.app.activities.detail.adapter.AllCommentAdapter;
import me.vipa.app.activities.detail.viewModel.DetailViewModel;
import me.vipa.app.activities.downloads.NetworkHelper;
import me.vipa.app.activities.downloads.WifiPreferenceListener;
import me.vipa.app.activities.listing.listui.ListActivity;
import me.vipa.app.activities.purchase.callBack.EntitlementStatus;
import me.vipa.app.activities.purchase.planslayer.GetPlansLayer;
import me.vipa.app.activities.purchase.ui.PurchaseActivity;
import me.vipa.app.activities.purchase.ui.VodOfferType;
import me.vipa.app.activities.usermanagment.ui.LoginActivity;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import me.vipa.app.networking.apistatus.APIStatus;
import me.vipa.app.networking.responsehandler.ResponseModel;
import me.vipa.bookmarking.bean.GetBookmarkResponse;
import me.vipa.enums.Layouts;

import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static com.google.android.material.tabs.TabLayout.INDICATOR_GRAVITY_BOTTOM;

public class EpisodeActivity extends BaseBindingActivity<EpisodeScreenBinding> implements AlertDialogFragment.AlertDialogListener, NetworkChangeReceiver.ConnectivityReceiverListener, AudioManager.OnAudioFocusChangeListener, CommonRailtItemClickListner, MoreClickListner, BrightcovePlayerFragment.OnPlayerInteractionListener, OnDownloadClickInteraction, MediaDownloadable.DownloadEventListener, VideoListListener, BrightcovePlayerFragment.ChromeCastStartedCallBack {
    public static boolean isActive = false;
    private long mLastClickTime = 0;
    private DetailViewModel viewModel;
    private KsPreferenceKeys preference;
    private NontonPlayerExtended fragment;
    private int assestId;
    private int seriesId;
    private int watchList = 0;
    private int watchListId = 0;
    private int likeCounter = 0;
    private String videoUrl = "";
    private String vastUrl = "";
    private String token;
    private ResponseDetailPlayer response;
    private String isLogin;
    private String bannerImage = "";
    private AllCommentAdapter commentAdapter;
    private String profilePicKey;
    public long videoPos = 0;
    public boolean isloggedout = false;
    private BottomSheetDialog dialog;
    public String userName = "";
    public int commentCounter = 0;
    private boolean commentLastPage = false;
    private boolean loadingComment = true;
    private List<ItemsItem> commentsList;
    private boolean isHitPlayerApi = false;
    private int selectedIdIntent = -1;
    private String tabId;
    private Long brightCoveVideoId;
    private RailInjectionHelper railInjectionHelper;
    private FragmentTransaction transaction;
    private String sharingUrl;
    private BrightcovePlayerFragment playerFragment;
    private AudioManager audioManager;
    private AudioFocusRequest focusRequest;
    private SeasonTabFragment seasonTabFragment;
    private EnveuVideoItemBean seriesDetailBean;
    private CommentsFragment commentsFragment;
    private Dialog seasonDialog;
    private static SeriesResponse mSeriesResponse;
    private static List<SeasonResponse> railSeriesResponse;
    private boolean isVOD;
    private int selectedSeasonId;
    private int totalSeasonCount;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private String TAG = "EpisodeActivity";
    private boolean hasSelectedSeasonId;
    private boolean isDataUpdated;
    private EpisodeTabAdapter episodeTabAdapter;
    public boolean isSeasonData = false;
    public boolean isRailData = false;
    private AlertDialogSingleButtonFragment errorDialog;
    private boolean errorDialogShown = false;
    private EnveuVideoItemBean videoDetails;
    private DownloadHelper downloadHelper;
    private Video downloadAbleVideo;
    private UserInteractionFragment userInteractionFragment;
    Bundle extras;
    private boolean isOfflineAvailable = false;
    private boolean isCastConnected = false;

    public static void closeActivity() {
    }

    @Override
    public EpisodeScreenBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return EpisodeScreenBinding.inflate(inflater);
    }

    boolean isLoggedIn = false;

    private void setFullScreen() {
        Log.e("Tag", "Inset: " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
            WindowManager.LayoutParams attribs = getWindow().getAttributes();
            attribs.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
            getWindow().getDecorView().setOnApplyWindowInsetsListener((view, windowInsets) -> {
                        DisplayCutout inset = windowInsets.getDisplayCutout();
                        Log.d("Tag", "Inset: " + inset);
                        return windowInsets;
                    }
            );
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isActive = true;
        getWindow().setBackgroundDrawableResource(R.color.black);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        if (SDKConfig.getInstance().getEpisodeDetailId().equalsIgnoreCase("")) {
            //tabId = AppConstants.EPISODE_ENVEU;
            tabId = "10000";
        } else {
            tabId = SDKConfig.getInstance().getEpisodeDetailId();
        }

        preference = KsPreferenceKeys.getInstance();
        if (preference.getAppPrefLoginStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            isLoggedIn = true;
        }
        viewModel = ViewModelProviders.of(EpisodeActivity.this).get(DetailViewModel.class);
        setupUI(getBinding().llParent);
        commentCounter = 0;
        isHitPlayerApi = false;
        AppCommonMethod.isPurchase = false;

        if (getIntent().hasExtra(AppConstants.BUNDLE_ASSET_BUNDLE)) {
            extras = getIntent().getExtras();
            if (extras != null) {
                extras = extras.getBundle(AppConstants.BUNDLE_ASSET_BUNDLE);
                assestId = Objects.requireNonNull(extras).getInt(AppConstants.BUNDLE_ASSET_ID);
                videoPos = TimeUnit.SECONDS.toMillis(Long.parseLong(extras.getString(AppConstants.BUNDLE_DURATION)));
                brightCoveVideoId = Objects.requireNonNull(extras).getLong(AppConstants.BUNDLE_VIDEO_ID_BRIGHTCOVE);

            }
        } else {
            throw new IllegalArgumentException("Activity cannot find  extras " + "Search_Show_All");
        }
        //end basic settings
        callBinding();
    }

    private void callBinding() {
        modelCall();
    }

    private void modelCall() {

        new ToolBarHandler(this).setEpisodeAction(getBinding());
        getBinding().connection.retryTxt.setOnClickListener(view -> {
            getBinding().llParent.setVisibility(View.VISIBLE);
            getBinding().noConnectionLayout.setVisibility(View.GONE);
            connectionObserver();
        });

        getBinding().backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        connectionObserver();
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
            UIinitialization();
        } else {
            noConnectionLayout();
        }
    }


    public void postCommentClick() {
      /*  getBinding().interactionSection.showComments.setOnClickListener(v -> {
            commentFragment(163);
            getBinding().interactionSection.showComments.setVisibility(View.GONE);
            getBinding().rootScroll.setVisibility(View.GONE);
            getBinding().fragmentComment.setVisibility(View.VISIBLE);
        });*/
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


    @Override
    public void onPlayerInProgress() {

    }

    public void removeCommentFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (commentsFragment != null) {
            transaction.remove(commentsFragment);
            transaction.commit();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            commentsFragment = null;
            getBinding().rootScroll.setVisibility(View.VISIBLE);
            getBinding().fragmentComment.setVisibility(View.GONE);
        }
    }

    RecommendationRailFragment railFragment;

    public void setTabs(String seasonNumber, boolean noEpisode) {
        if (seriesDetailBean != null) {
            downloadHelper = new DownloadHelper(this, this, seriesDetailBean.getBrightcoveVideoId(), seriesDetailBean.getTitle(), MediaTypeConstants.getInstance().getEpisode(), videoDetails);
            downloadHelper.findVideo(videoDetails.getBrightcoveVideoId());
        }
       // getBinding().tabLayout.setSelectedTabIndicatorGravity(INDICATOR_GRAVITY_BOTTOM);
        getBinding().tabLayout.setSelectedTabIndicatorGravity(TabLayout.INDICATOR_GRAVITY_TOP);
        if (episodeTabAdapter == null) {
            episodeTabAdapter = new EpisodeTabAdapter(getSupportFragmentManager());
            railFragment = new RecommendationRailFragment();
            Bundle args = new Bundle();
            args.putString(AppConstants.BUNDLE_TAB_ID, tabId);
            railFragment.setArguments(args);
            seasonTabFragment = new SeasonTabFragment();
            Bundle bundleSeason = new Bundle();

            if (noEpisode) {
                bundleSeason.putInt(AppConstants.BUNDLE_ASSET_ID, seriesId);
                bundleSeason.putInt(AppConstants.BUNDLE_CURRENT_ASSET_ID, assestId);
                bundleSeason.putParcelableArrayList(AppConstants.BUNDLE_SEASON_ARRAY, seriesDetailBean.getSeasons());
                bundleSeason.putInt(AppConstants.BUNDLE_SEASON_COUNT, seriesDetailBean.getSeasonCount());
                if (seriesDetailBean.getSeasonCount() > 0)
                    bundleSeason.putInt(AppConstants.BUNDLE_SELECTED_SEASON, Integer.parseInt(seasonNumber));

            } else {
                bundleSeason.putInt(AppConstants.BUNDLE_ASSET_ID, -1);
            }

            seasonTabFragment.setArguments(bundleSeason);
            episodeTabAdapter.addFragment(seasonTabFragment, getString(R.string.tab_heading_episodes));
            episodeTabAdapter.addFragment(railFragment, getString(R.string.tab_heading_other));
            getBinding().viewPager.setAdapter(episodeTabAdapter);
            getBinding().viewPager.setOffscreenPageLimit(2);
            getBinding().tabLayout.setupWithViewPager(getBinding().viewPager);
            //AppCommonMethod.customTabWidth(getBinding().tabLayout);
            //AppCommonMethod.customTabWidth2(getBinding().tabLayout);
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

        } else {
            Bundle bundleSeason = new Bundle();

            if (noEpisode) {
                bundleSeason.putInt(AppConstants.BUNDLE_ASSET_ID, seriesId);
                bundleSeason.putInt(AppConstants.BUNDLE_CURRENT_ASSET_ID, assestId);
                bundleSeason.putParcelableArrayList(AppConstants.BUNDLE_SEASON_ARRAY, seriesDetailBean.getSeasons());

                bundleSeason.putInt(AppConstants.BUNDLE_SEASON_COUNT, seriesDetailBean.getSeasonCount());
                if (seriesDetailBean.getSeasonCount() > 0)
                    bundleSeason.putInt(AppConstants.BUNDLE_SELECTED_SEASON, Integer.parseInt(seasonNumber));

            } else {
                bundleSeason.putInt(AppConstants.BUNDLE_ASSET_ID, -1);
            }

            Bundle args = new Bundle();
            args.putString(AppConstants.BUNDLE_TAB_ID, tabId);
            railFragment.getVideoRails(args);

            seasonTabFragment.getVideoRails(bundleSeason);
        }

    }


    public void stopShimmercheck() {
        if (isSeasonData && isRailData) {
            isSeasonData = false;
            isRailData = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopShimmer();
                }
            }, 1000);

        }
    }

    public void numberOfEpisodes(int size) {

    }


    public void removeTab(int position) {
        if (getBinding().tabLayout.getTabCount() >= 1 && position <= getBinding().tabLayout.getTabCount()) {
            episodeTabAdapter.removeTabPage(position);
            ViewGroup.LayoutParams params = getBinding().tabLayout.getLayoutParams();
            params.width = (int) getResources().getDimension(R.dimen.tab_layout_single);//(int) getResources().getDimension(R.dimen.tab_layout_single);
            getBinding().tabLayout.setLayoutParams(params);
            //  getBinding().tabLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));


        }
    }


    private void callShimmer() {
        getBinding().seriesShimmer.setVisibility(View.VISIBLE);
        getBinding().mShimmer.seriesShimmerScroll1.setEnabled(false);
        getBinding().mShimmer.seriesShimmerScroll2.setEnabled(false);
        getBinding().llParent.setVisibility(View.GONE);
        getBinding().noConnectionLayout.setVisibility(View.GONE);
        getBinding().mShimmer.sfShimmer1.startShimmer();
        getBinding().mShimmer.sfShimmer2.startShimmer();
        getBinding().mShimmer.sfShimmer3.startShimmer();
        getBinding().mShimmer.flBackIconImage.bringToFront();
        getBinding().mShimmer.flBackIconImage.setOnClickListener(v -> onBackPressed());

    }


    private void stopShimmer() {
        getBinding().seriesShimmer.setVisibility(View.GONE);
        getBinding().llParent.setVisibility(View.VISIBLE);
        getBinding().noConnectionLayout.setVisibility(View.GONE);
        getBinding().mShimmer.sfShimmer1.startShimmer();
        getBinding().mShimmer.sfShimmer2.startShimmer();
        getBinding().mShimmer.sfShimmer3.startShimmer();
        // playPlayerWhenShimmer();
    }

    public void playPlayerWhenShimmer() {
        getBinding().pBar.setVisibility(View.VISIBLE);
        viewModel.getBookMarkByVideoId(token, videoDetails.getId()).observe(this, new Observer<GetBookmarkResponse>() {
            @Override
            public void onChanged(GetBookmarkResponse getBookmarkResponse) {
                getBinding().backButton.setVisibility(View.GONE);
                long bookmarkPosition = 0l;
                if (getBookmarkResponse != null && getBookmarkResponse.getBookmarks() != null) {
                    bookmarkPosition = getBookmarkResponse.getBookmarks().get(0).getPosition();
                }
                transaction = getSupportFragmentManager().beginTransaction();
                playerFragment = new BrightcovePlayerFragment();
                Logger.e(TAG, String.valueOf(isOfflineAvailable));
                if (isOfflineAvailable) {
                    long bookmarkPosition2 = bookmarkPosition;
                    downloadHelper.findOfflineVideoById(String.valueOf(brightCoveVideoId), new OfflineCallback<Video>() {
                        @Override
                        public void onSuccess(Video video) {
                            if (!video.isClearContent()) {
                                if (video.getLicenseExpiryDate().getTime() >= System.currentTimeMillis()) {
                                    Logger.e("License", "Expiry" + video.getLicenseExpiryDate());
                                    setPlayerFragment(video, true, bookmarkPosition2);
                                } else {
                                    downloadHelper.deleteVideo(video);
                                    setPlayerFragment(null, false, bookmarkPosition2);
                                }
                            } else {
                                setPlayerFragment(video, true, bookmarkPosition2);
                            }
                        }

                        @Override
                        public void onFailure(Throwable throwable) {

                        }
                    });
                } else {
                    setPlayerFragment(null, false, bookmarkPosition);

                }
            }
        });
    }

    private void setPlayerFragment(Video video, boolean isOffline, Long bookmarkPosition) {
        Bundle args = new Bundle();
        if (isOffline) {
            args.putBoolean("isOffline", isOfflineAvailable);
            args.putParcelable(AppConstants.BUNDLE_VIDEO_ID_BRIGHTCOVE, video);
        } else {
            args.putString(AppConstants.BUNDLE_VIDEO_ID_BRIGHTCOVE, String.valueOf(brightCoveVideoId));
        }
        args.putLong(AppConstants.BOOKMARK_POSITION, bookmarkPosition);
        args.putString("selected_track", KsPreferenceKeys.getInstance().getQualityName());
        args.putString("selected_lang", KsPreferenceKeys.getInstance().getAppLanguage());
        args.putBoolean("ads_visibility", isAdShowingToUser);
        if (videoDetails != null) {
            args.putString("vast_tag", videoDetails.getVastTag());
        }
        if (videoDetails.getAssetType() != null) {
            args.putString("assetType", videoDetails.getAssetType());
        }
        Log.w("totalZies", KsPreferenceKeys.getInstance().getBingeWatchEnable() + "");
        args.putString("config_vast_tag", SDKConfig.getInstance().getConfigVastTag());
        args.putBoolean("binge_watch", KsPreferenceKeys.getInstance().getBingeWatchEnable());
        args.putInt("binge_watch_timer", SDKConfig.getInstance().getTimer());
        args.putBoolean("from_binge", fromBingWatch);
        args.putInt("player_orientation", playerOrientation);
        setArgsForEvent(args);

        if (videoDetails.isPremium() && videoDetails.getThumbnailImage() != null) {
            args.putString(AppConstants.BUNDLE_BANNER_IMAGE, videoDetails.getThumbnailImage());
        }
        playerFragment.setArguments(args);
        transaction.replace(R.id.player_frame, playerFragment, "PlayerFragment");
        transaction.addToBackStack(null);
        transaction.commit();
        getBinding().pBar.setVisibility(View.VISIBLE);
    }

    private void setArgsForEvent(Bundle args) {
        try {
            if (videoDetails != null) {
                if (videoDetails.getName() != null) {
                    args.putString(AppConstants.PLAYER_ASSET_TITLE, videoDetails.getName());
                }
                if (videoDetails.getAssetType() != null) {
                    args.putString(AppConstants.PLAYER_ASSET_MEDIATYPE, videoDetails.getAssetType());
                }
            }
        } catch (Exception e) {

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        requestAudioFocus();
        boolean isTablet = EpisodeActivity.this.getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            AppCommonMethod.isResumeDetail = true;
        }
        isloggedout = false;
        dismissLoading(getBinding().progressBar);
        if (AppCommonMethod.isPurchase) {
            AppCommonMethod.isPurchase = false;
            seriesId = AppCommonMethod.seriesId;
            isHitPlayerApi = false;
            refreshDetailPage(assestId);
        }

        if (!isLoggedIn) {
            if (preference.getAppPrefLoginStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                isLoggedIn = true;
                AppCommonMethod.isPurchase = false;
                seriesId = AppCommonMethod.seriesId;
                isHitPlayerApi = false;
                fromBingWatch = false;
                //  seasonTabFragment=null;
                seasonTabFragment.setSeasonAdapter(null);
                refreshDetailPage(assestId);
            }
        }
        setBroadcast();
        if (preference != null && userInteractionFragment != null) {
            AppCommonMethod.callSocialAction(preference, userInteractionFragment);
        }

    }


    public void requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int negativeVal = -1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            focusRequest =
                    new AudioFocusRequest.Builder(AudioManager.STREAM_MUSIC)
                            .setAudioAttributes(playbackAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(
                                    new AudioManager.OnAudioFocusChangeListener() {
                                        @Override
                                        public void onAudioFocusChange(int i) {

                                        }
                                    })
                            .build();
            // audioManager.requestAudioFocus(focusRequest);

            audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            switch (audioManager.requestAudioFocus(focusRequest)) {
                case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                    // don’t start playback
                {
                    if (fragment != null) {
                        //  fragment.pauseOnOtherAudio();
                    }
                }
                break;
                case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                    // actually start playback
            }


        } else {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            //    Toast.makeText(EpisodeActivity.this,"AUDIOFOCUS_GAIN_TRANSIENT" ,Toast.LENGTH_SHORT).show();

        }
    }


    void releaseAudioFocusForMyApp(final Context context) {
        // Toast.makeText(EpisodeActivity.this,"releaseAudioFocusForMyApp" ,Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(focusRequest);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (playerFragment != null) {
            if (!hasFocus) {
                if (playerFragment.isPlaying()) {
                    playerFragment.playPause();
                }
            } else {
                if (!playerFragment.isPlaying()) {
                    playerFragment.playPause();
                }
            }
        }
    }

    public void setBroadcast() {
        receiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        EpisodeActivity.this.registerReceiver(receiver, filter);
        setConnectivityListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(AppConstants.BUNDLE_ASSET_BUNDLE)) {
            isHitPlayerApi = false;
            Bundle extras = intent.getExtras();
            if (extras != null) {
                assestId = Objects.requireNonNull(intent.getExtras().getBundle(AppConstants.BUNDLE_ASSET_BUNDLE)).getInt(AppConstants.BUNDLE_ASSET_ID);
                AppCommonMethod.seasonId = -1;
                if (commentAdapter != null) {
                    commentAdapter.clearList();
                    if (commentsList != null) {
                        commentsList.clear();
                    }
                }
                refreshDetailPage(assestId);
            }
        } else {
            throw new IllegalArgumentException("Activity cannot find  extras " + "Search_Show_All");
        }
    }


    public void refreshDetailPage(int assestId) {
        this.assestId = assestId;
        if (playerFragment != null)
            playerFragment.stopPlayer();
        if (preference.getAppPrefHasSelectedId()) {
            preference.setAppPrefHasSelectedId(false);
            int tempId = preference.getAppPrefSelectodSeasonId();
            if (tempId != -1) {
                hasSelectedSeasonId = true;
                selectedIdIntent = tempId;
                preference.setAppPrefSelectodSeasonId(-1);
            }
        }

        if (getBinding().expandableLayout.isExpanded())
            clickExpandable(getBinding().lessButton);

        callBinding();
    }


    public void comingSoon() {
        if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            //showDialog(EpisodeActivity.this.getResources().getString(R.string.error), getResources().getString(R.string.you_are_not_entitled));
            AppCommonMethod.assetId = assestId;
            AppCommonMethod.seriesId = seriesId;
            if (responseEntitlementModel != null && responseEntitlementModel.getStatus()) {
                Intent intent = new Intent(EpisodeActivity.this, PurchaseActivity.class);
                intent.putExtra("response", videoDetails);
                intent.putExtra("assestId", assestId);
                intent.putExtra("contentType", MediaTypeConstants.getInstance().getEpisode());
                intent.putExtra("responseEntitlement", responseEntitlementModel);
                startActivity(intent);
            }

        } else {
            preference.setAppPrefGotoPurchase(true);
            openLoginPage(getResources().getString(R.string.please_login_play));
        }
    }

    public void openLoginPage(String message) {

        AppCommonMethod.seasonId = selectedSeasonId;
        preference.setAppPrefJumpTo(MediaTypeConstants.getInstance().getEpisode());
        preference.setAppPrefJumpBack(true);
        preference.setAppPrefIsEpisode(true);
        preference.setAppPrefJumpBackId(assestId);
        //   preference.setString(AppConstants.APP_PREF_VIDEO_POSITION, String.valueOf(TimeUnit.MILLISECONDS.toSeconds(fragment.getCurrentPosition())));

        preference.setAppPrefHasSelectedId(true);
        preference.setAppPrefSelectodSeasonId(selectedSeasonId);
        new ActivityLauncher(EpisodeActivity.this).loginActivity(EpisodeActivity.this, LoginActivity.class);
    }

    public void UIinitialization() {
        if (!fromBingWatch) {
            callShimmer();
        }
        railInjectionHelper = ViewModelProviders.of(this).get(RailInjectionHelper.class);
        loadingComment = true;
        commentCounter = 0;
//        getBinding().tvBuyNow.setVisibility(View.GONE);
        postCommentClick();
//        getBinding().tvPurchased.setVisibility(View.GONE);
//        getBinding().tvPremium.setVisibility(View.GONE);
        getBinding().setDuration("");
        getBinding().setCasttext("");
        getBinding().setCrewtext("");
        EnveuVideoItemBean player = new EnveuVideoItemBean();
        Data data = new Data();
        data.setContentTitle("");
        getBinding().setResponseApi(player);
        setupUI(getBinding().llParent);

        response = new ResponseDetailPlayer();
        preference.setAppPrefAssetId(assestId);
        watchList = 0;
        likeCounter = 0;
        isLogin = preference.getAppPrefLoginStatus();
        token = preference.getAppPrefAccessToken();
        Logger.e("", "APP_PREF_ACCESS_TOKEN" + token);


        showLoading(getBinding().progressBar, false);

        if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            AppUserModel signInResponseModel = AppUserModel.getInstance();
            if (signInResponseModel != null) {
                userName = signInResponseModel.getName();
                profilePicKey = signInResponseModel.getProfilePicURL();
            }
        }

        getBinding().noConnectionLayout.setVisibility(View.GONE);
        getBinding().header.setVisibility(View.VISIBLE);

        // clickSharing();

        if (!isHitPlayerApi) {
            getEpisodeDetails();
           /* if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                hitApiWatchHistory();

            }*/
        }
        BuyNowClick();
    }

    //***********************call episode data from below API***************************************//
    boolean isAdShowingToUser = true;

    public void getEpisodeDetails() {

        isHitPlayerApi = true;
        if (fromBingWatch) {
            parseVideoDetails(nextEpisode);
        } else {
            railInjectionHelper.getSeriesDetailsV2(String.valueOf(assestId)).observe(EpisodeActivity.this, new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel response) {
                    if (response != null) {
                        if (response.getStatus().equalsIgnoreCase(APIStatus.START.name())) {

                        } else if (response.getStatus().equalsIgnoreCase(APIStatus.SUCCESS.name())) {
                            if (response.getBaseCategory() != null) {
                                RailCommonData enveuCommonResponse = (RailCommonData) response.getBaseCategory();

                                videoDetails = enveuCommonResponse.getEnveuVideoItemBeans().get(0);
                                parseVideoDetails(videoDetails);

                            }
                        } else if (response.getStatus().equalsIgnoreCase(APIStatus.ERROR.name())) {
                            if (response.getErrorModel().getErrorCode() != 0) {
                                stopShimmer();
                            }

                        } else if (response.getStatus().equalsIgnoreCase(APIStatus.FAILURE.name())) {
                            stopShimmer();
                        }

                    }
                }
            });

        }


    }

    private void parseVideoDetails(EnveuVideoItemBean videoDetails) {
        dismissLoading(getBinding().progressBar);
        sharingClick(videoDetails);
        ImageHelper.getInstance(EpisodeActivity.this).loadListImage(getBinding().playerImage, videoDetails.getPosterURL());
//        if (videoDetails.isPremium()) {
//            getBinding().tvPurchased.setVisibility(View.GONE);
//            getBinding().tvPremium.setVisibility(View.GONE);
//            getBinding().tvBuyNow.setVisibility(View.VISIBLE);
//            getBinding().mPremiumStatus.setVisibility(View.VISIBLE);
//            getBinding().backButton.setVisibility(View.VISIBLE);
//            hitApiEntitlement(videoDetails.getSku());
//
//        }

//        else {
//            if (AppCommonMethod.getCheckBCID(videoDetails.getBrightcoveVideoId())) {
//                isLogin = preference.getAppPrefLoginStatus();
//                if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
//                    if (!preference.getEntitlementStatus()) {
//                        GetPlansLayer.getInstance().getEntitlementStatus(preference, token, new EntitlementStatus() {
//                            @Override
//                            public void entitlementStatus(boolean entitlementStatus, boolean apiStatus) {
//                                if (entitlementStatus && apiStatus) {
//                                    isAdShowingToUser = false;
//                                }
//                                brightCoveVideoId = Long.parseLong(videoDetails.getBrightcoveVideoId());
//                                playPlayerWhenShimmer();
//                            }
//                        });
//                    } else {
//                        getBinding().pBar.setVisibility(View.GONE);
//                        brightCoveVideoId = Long.parseLong(videoDetails.getBrightcoveVideoId());
//                        playPlayerWhenShimmer();
//                    }
//
//                } else {
//                    brightCoveVideoId = Long.parseLong(videoDetails.getBrightcoveVideoId());
//                    playPlayerWhenShimmer();
//                }
//
//            }
//
//        }

        if (fromBingWatch) {
            getBinding().playIcon.setVisibility(View.GONE);
            if (AppCommonMethod.getCheckBCID(videoDetails.getBrightcoveVideoId())) {
                isLogin = preference.getAppPrefLoginStatus();
                if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                    if (!preference.getEntitlementStatus()) {
                        GetPlansLayer.getInstance().getEntitlementStatus(preference, token, new EntitlementStatus() {
                            @Override
                            public void entitlementStatus(boolean entitlementStatus, boolean apiStatus) {
                                getBinding().pBar.setVisibility(View.GONE);
                                if (entitlementStatus && apiStatus) {
                                    isAdShowingToUser = false;
                                }
                                brightCoveVideoId = Long.parseLong(videoDetails.getBrightcoveVideoId());
                                playPlayerWhenShimmer();
                            }
                        });
                    } else {
                        getBinding().pBar.setVisibility(View.GONE);
                        brightCoveVideoId = Long.parseLong(videoDetails.getBrightcoveVideoId());
                        playPlayerWhenShimmer();
                    }

                } else {
                    brightCoveVideoId = Long.parseLong(videoDetails.getBrightcoveVideoId());
                    playPlayerWhenShimmer();
                }

            }
        }

        getBinding().playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBinding().playIcon.setVisibility(View.GONE);
                getBinding().backButton.setVisibility(View.GONE);
                if (AppCommonMethod.getCheckBCID(videoDetails.getBrightcoveVideoId())) {
                    isLogin = preference.getAppPrefLoginStatus();
                    if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                        if (!preference.getEntitlementStatus()) {
                            GetPlansLayer.getInstance().getEntitlementStatus(preference, token, new EntitlementStatus() {
                                @Override
                                public void entitlementStatus(boolean entitlementStatus, boolean apiStatus) {
                                    getBinding().pBar.setVisibility(View.GONE);
                                    if (entitlementStatus && apiStatus) {
                                        isAdShowingToUser = false;
                                    }
                                    brightCoveVideoId = Long.parseLong(videoDetails.getBrightcoveVideoId());
                                    playPlayerWhenShimmer();
                                }
                            });
                        } else {
                            getBinding().pBar.setVisibility(View.GONE);
                            brightCoveVideoId = Long.parseLong(videoDetails.getBrightcoveVideoId());
                            playPlayerWhenShimmer();
                        }

                    } else {
                        getBinding().pBar.setVisibility(View.GONE);
                        brightCoveVideoId = Long.parseLong(videoDetails.getBrightcoveVideoId());
                        playPlayerWhenShimmer();
                    }

                }else {
                    getBinding().pBar.setVisibility(View.GONE);
                }
            }
        });

        if (StringUtils.isNullOrEmptyOrZero(videoDetails.getSeries())) {
            seriesId = -1;
        } else {
            if (videoDetails.getSeriesId() != null) {
                seriesId = Integer.parseInt(videoDetails.getSeriesId());
            } else {
                seriesId = -1;
            }
        }
        if (!fromBingWatch) {
            if (videoDetails.getSeasonNumber() != null && !videoDetails.getSeasonNumber().equalsIgnoreCase("")) {
                getSeriesDetail(seriesId, videoDetails.getSeasonNumber());
            } else {
                getSeriesDetail(seriesId, "1");
            }
        } else {
            if (seasonTabFragment != null) {
                seasonTabFragment.updateCurrentAsset(videoDetails.getId());
            }

        }

        setUI(videoDetails);
    }
    //***********************^||||call episode data from above API\\\\\^***************************************//


    //***********************call series data from below API***************************************//
    public void getSeriesDetail(int seriesId, String seasonNumber) {
        if (seriesId == -1) {
            setTabs(seasonNumber, false);
        } else {

            railInjectionHelper.getSeriesDetailsV2(String.valueOf(seriesId)).observe(EpisodeActivity.this, new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel response) {
                    if (response != null) {
                        if (response.getStatus().equalsIgnoreCase(APIStatus.START.name())) {

                        } else if (response.getStatus().equalsIgnoreCase(APIStatus.SUCCESS.name())) {
                            if (response.getBaseCategory() != null) {
                                RailCommonData enveuCommonResponse = (RailCommonData) response.getBaseCategory();
                                //parseSeriesData(enveuCommonResponse);
                                seriesDetailBean = enveuCommonResponse.getEnveuVideoItemBeans().get(0);
                                setTabs(seasonNumber, true);
                                setUserInteractionFragment(assestId, seriesDetailBean);
                            }
                        } else if (response.getStatus().equalsIgnoreCase(APIStatus.ERROR.name())) {
                            if (response.getErrorModel().getErrorCode() != 0) {
                                /*if (response.getErrorModel().getErrorCode() == AppConstants.RESPONSE_CODE_LOGOUT) {
                                    showDialog(EpisodeActivity.this.getResources().getString(R.string.error), getResources().getString(R.string.logged_out));
                                } else {
                                    showDialog(EpisodeActivity.this.getResources().getString(R.string.error), getResources().getString(R.string.something_went_wrong));
                                }*/
                                stopShimmer();
                                showDialog(EpisodeActivity.this.getResources().getString(R.string.error), getResources().getString(R.string.something_went_wrong));
                            }

                        } else if (response.getStatus().equalsIgnoreCase(APIStatus.FAILURE.name())) {
                            stopShimmer();
                            showDialog(EpisodeActivity.this.getResources().getString(R.string.error), getResources().getString(R.string.something_went_wrong));
                            // showDialog(EpisodeActivity.this.getResources().getString(R.string.error), getResources().getString(R.string.something_went_wrong));
                        }

                    }
                }
            });

        }
    }
    //***********************^||||call series data from above API\\\\\^***************************************//


    public void sharingClick(EnveuVideoItemBean videoDetails) {
        String imgUrl = videoDetails.getPosterURL();
        int id = videoDetails.getId();
        String title = videoDetails.getTitle();
        sharingUrl = AppCommonMethod.getSharingURL(this, title, id, "VIDEO", imgUrl, getApplicationContext(), seriesId);
    }


    public void hitApiWatchHistory() {
        //  token = "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6IjE5NDciLCJleHBpcnlUaW1lIjoxNTY4ODA4Mjk4MTMzLCJlbWFpbCI6InBhcnZlZW4ubWFuaUBnbWFpbC5jb20iLCJhY2NvdW50SWQiOm51bGwsIm5hbWUiOiJNYW5pIn0.lNTYXsMw3YQfADXNcwOOmYK-JlTrPhEs7WJD5_2mWiiHDxVke8JtzhEe6303E4v552L7XtAo8Yxirnw8J6EUdQ";
        railInjectionHelper.hitApiAddWatchHistory(token, assestId).observe(this, responseEmpty ->
        {
            if (responseEmpty.isStatus()) {
                if (response.getResponseCode() == AppConstants.RESPONSE_CODE_REGISTER) {

                } else if (response.getResponseCode() == AppConstants.RESPONSE_CODE_ERROR) {


                }
            }

        });


    }


    ResponseEntitle responseEntitlementModel;

    public void hitApiEntitlement(String sku) {

        viewModel.hitApiEntitlement(token, sku).observe(EpisodeActivity.this, responseEntitlement -> {
            responseEntitlementModel = responseEntitlement;
            if (responseEntitlement.getStatus()) {
                if (responseEntitlement.getData().getEntitled()) {
                    getBinding().tvBuyNow.setVisibility(View.GONE);
                    if (responseEntitlement.getData() != null) {
                        updateBuyNowText(responseEntitlement, 1);
                    }
                } else {
                    if (responseEntitlement.getData() != null) {
                        updateBuyNowText(responseEntitlement, 2);
                    }
                }
            } else {
                if (responseEntitlementModel != null && responseEntitlementModel.getResponseCode() != null && responseEntitlementModel.getResponseCode() > 0 && responseEntitlementModel.getResponseCode() == 4302) {
                    isloggedout = true;
                    logoutUser();
                    // showDialog(EpisodeActivity.this.getResources().getString(R.string.logged_out), responseEntitlementModel.getDebugMessage() == null ? "" : responseEntitlementModel.getDebugMessage().toString());
                }
            }
        });
    }

    private void updateBuyNowText(ResponseEntitle responseEntitlement, int type) {
        try {
            if (type == 1) {
                if (responseEntitlement.getData().getEntitledAs() != null) {
                    List<EntitledAs> alpurchaseas = responseEntitlement.getData().getEntitledAs();
                    String vodOfferType = alpurchaseas.get(0).getVoDOfferType();
                    String subscriptionOfferPeriod = null;
                    if (alpurchaseas.get(0).getOfferType() != null) {
                        subscriptionOfferPeriod = (String) alpurchaseas.get(0).getOfferType();
                    }

                    if (vodOfferType != null) {
                        if (vodOfferType.contains(VodOfferType.PERPETUAL.name())) {
                            getBinding().tvPurchased.setVisibility(View.VISIBLE);
                            getBinding().tvPurchased.setText("" + getResources().getString(R.string.purchased));
                        } else if (vodOfferType.contains(VodOfferType.RENTAL.name())) {
                            getBinding().tvPurchased.setVisibility(View.VISIBLE);
                            getBinding().tvPurchased.setText("" + getResources().getString(R.string.rented));
                        } else {

                        }
                    } else {
                        if (subscriptionOfferPeriod != null) {
                            getBinding().tvPurchased.setVisibility(View.VISIBLE);
                            getBinding().tvPurchased.setText("" + getResources().getString(R.string.subscribed));
                        } else {

                        }
                    }
                    if (responseEntitlement.getData().getBrightcoveVideoId() != null) {
                        brightCoveVideoId = Long.parseLong(responseEntitlement.getData().getBrightcoveVideoId());
                    }
                    isAdShowingToUser = false;
                    preference.setEntitlementState(true);
                    playPlayerWhenShimmer();

                }
            } else {

            }

        } catch (Exception e) {

        }
    }


    private void BuyNowClick() {
        getBinding().tvBuyNow.setOnClickListener(view -> comingSoon());
    }

    public void setUserInteractionFragment(int id, EnveuVideoItemBean seriesDetailBean) {
        String seriesId = seriesDetailBean.getBrightcoveVideoId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putInt(AppConstants.BUNDLE_ASSET_ID, id);
        args.putSerializable(AppConstants.BUNDLE_SERIES_DETAIL, videoDetails);
        args.putString(AppConstants.BUNDLE_SERIES_ID, seriesId);

        userInteractionFragment = new UserInteractionFragment();
        userInteractionFragment.setArguments(args);
        transaction.replace(R.id.fragment_user_interaction, userInteractionFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        downloadHelper = new DownloadHelper(this, this, seriesId, seriesDetailBean.getTitle(), MediaTypeConstants.getInstance().getEpisode(), videoDetails);
        downloadHelper.findVideo(videoDetails.getBrightcoveVideoId());
    }


    public void setUI(EnveuVideoItemBean responseDetailPlayer) {
        if (responseDetailPlayer.getAssetCast().size() > 0 && !responseDetailPlayer.getAssetCast().get(0).equalsIgnoreCase("")) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < responseDetailPlayer.getAssetCast().size(); i++) {
                if (i == responseDetailPlayer.getAssetCast().size() - 1) {
                    stringBuilder = stringBuilder.append(responseDetailPlayer.getAssetCast().get(i));

                } else
                    stringBuilder = stringBuilder.append(responseDetailPlayer.getAssetCast().get(i)).append(", ");
            }
            getBinding().setCasttext(" " + stringBuilder);
        } else {
            getBinding().llCastView.setVisibility(View.GONE);
        }
        if (responseDetailPlayer.getAssetGenres().size() > 0 && !responseDetailPlayer.getAssetGenres().get(0).equalsIgnoreCase("")) {

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < responseDetailPlayer.getAssetGenres().size(); i++) {
                if (i == responseDetailPlayer.getAssetGenres().size() - 1) {
                    stringBuilder = stringBuilder.append(responseDetailPlayer.getAssetGenres().get(i));
                } else
                    stringBuilder = stringBuilder.append(responseDetailPlayer.getAssetGenres().get(i)).append(", ");
            }
            getBinding().setCrewtext(" " + stringBuilder);
        } else {
            getBinding().llCrewView.setVisibility(View.GONE);
        }
        setDetails(responseDetailPlayer);
    }

    public void logoutUser() {
        isloggedout = false;
        if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            if (CheckInternetConnection.isOnline(Objects.requireNonNull(EpisodeActivity.this))) {
                clearCredientials(preference);
                hitApiLogout(EpisodeActivity.this, preference.getAppPrefAccessToken());
            }
        }
    }


    private void showDialog(String title, String message) {
        FragmentManager fm = getSupportFragmentManager();
        AlertDialogSingleButtonFragment alertDialog = AlertDialogSingleButtonFragment.newInstance(title, message, getResources().getString(R.string.ok));
        alertDialog.setCancelable(false);
        alertDialog.setAlertDialogCallBack(this);
        alertDialog.show(fm, "fragment_alert");
    }

    public void setDetails(EnveuVideoItemBean responseDetailPlayer) {

        if (responseDetailPlayer.getAssetType() != null && responseDetailPlayer.getDuration() > 0) {
            String tempTag1 = responseDetailPlayer.getAssetType();
            String bullet = "\u2022";
            String tempTag2 = AppCommonMethod.calculateTimein_hh_mm_format(responseDetailPlayer.getDuration());

            Spannable WordtoSpan = new SpannableString(bullet);
            WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 0, WordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // StringBuilder stringBuilder = new StringBuilder(tempTag1 + "  " + WordtoSpan + " " + tempTag2);

            setCustomeFields(responseDetailPlayer, tempTag2);
        } else {
            setCustomeFields(responseDetailPlayer, "");
            new ToastHandler(EpisodeActivity.this).show(EpisodeActivity.this.getResources().getString(R.string.can_not_play_error));
        }
        if (responseDetailPlayer.getDescription() != null && responseDetailPlayer.getDescription().equalsIgnoreCase("")) {
            getBinding().descriptionText.setVisibility(View.GONE);
        }
        getBinding().setResponseApi(responseDetailPlayer);
        setExpandable();
        if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString()))
            addToWatchHistory();
    }

    private void setCustomeFields(EnveuVideoItemBean responseDetailPlayer, String duration) {
        try {
            getBinding().tag.setText("");
            if (responseDetailPlayer.getParentalRating() != null && !responseDetailPlayer.getParentalRating().equalsIgnoreCase("")) {
                getBinding().tag.setText(responseDetailPlayer.getParentalRating() + " \u2022");
            }

            getBinding().tag.setText(getBinding().tag.getText().toString() + " " + duration + " \u2022");

            if (responseDetailPlayer.getCountry() != null && !responseDetailPlayer.getCountry().equalsIgnoreCase("")) {
                getBinding().tag.setText(getBinding().tag.getText().toString() + " " + responseDetailPlayer.getCountry() + " \u2022");
            }

            if (responseDetailPlayer.getCompany() != null && !responseDetailPlayer.getCompany().equalsIgnoreCase("")) {
                getBinding().tag.setText(getBinding().tag.getText().toString() + " " + responseDetailPlayer.getCompany() + " \u2022");
            }

            if (responseDetailPlayer.getYear() != null && !responseDetailPlayer.getYear().equalsIgnoreCase("")) {
                getBinding().tag.setText(getBinding().tag.getText().toString() + " " + responseDetailPlayer.getYear() + " \u2022");
            }

            if (getBinding().tag.getText().toString().trim().endsWith("\u2022")) {
                String customeF = getBinding().tag.getText().toString().substring(0, getBinding().tag.getText().toString().length() - 1);
                getBinding().tag.setText(customeF);
            }
            if (getBinding().tag.getText().toString().trim().equalsIgnoreCase("")) {
                // getBinding().customeFieldView.setVisibility(View.GONE);
            }

            if (fromBingWatch) {
                getBinding().playIcon.setVisibility(View.GONE);
                getBinding().backButton.setVisibility(View.GONE);
            } else {
                if (responseDetailPlayer.getComingSoon() != null && !responseDetailPlayer.getComingSoon().equalsIgnoreCase("") && responseDetailPlayer.getComingSoon().equalsIgnoreCase("true")) {
                    getBinding().playIcon.setVisibility(View.GONE);
                    getBinding().backButton.setVisibility(View.VISIBLE);

                } else {
                    getBinding().playIcon.setVisibility(View.VISIBLE);
                    getBinding().backButton.setVisibility(View.GONE);
                }
            }


        } catch (Exception ignored) {

        }

    }


    private void addToWatchHistory() {
        BookmarkingViewModel bookmarkingViewModel = ViewModelProviders.of(this).get(BookmarkingViewModel.class);
        bookmarkingViewModel.addToWatchHistory(token, assestId);
    }

    private void noConnectionLayout() {
        getBinding().llParent.setVisibility(View.GONE);
        getBinding().noConnectionLayout.setVisibility(View.VISIBLE);


    }


    private void setExpandable() {
        getBinding().expandableLayout.collapse();
        if (getBinding().descriptionText.isExpanded()) {
            getBinding().descriptionText.toggle();
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

    private NetworkChangeReceiver receiver = null;


    @Override
    public void onBackPressed() {
        if (commentsFragment != null) {
            removeCommentFragment();
        } else {
            //AppCommonMethod.isPurchase = true;
            if (preference.getAppPrefJumpBack()) {
                preference.setAppPrefJumpBackId(0);
                preference.setAppPrefVideoPosition(String.valueOf(0));
                preference.setAppPrefJumpBack(false);
                preference.setAppPrefGotoPurchase(false);
                preference.setAppPrefIsEpisode(false);
            }
            preference.setAppPrefAssetId(0);
            AppCommonMethod.seasonId = -1;
            int orientation = this.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                finish();
            } else {
                if (playerFragment != null) {
                    playerFragment.BackPressClicked(2);
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
//        if (fragment != null)
//            fragment.onPause();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
            if (NetworkChangeReceiver.connectivityReceiverListener != null)
                NetworkChangeReceiver.connectivityReceiverListener = null;
        }
        releaseAudioFocusForMyApp(EpisodeActivity.this);


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Logger.e("EpisodeActivity", "onDestroy");
        super.onDestroy();

        preference.setAppPrefAssetId(0);
        preference.setAppPrefJumpTo("");
        preference.setAppPrefBranchIo(false);
        AppCommonMethod.seasonId = -1;
        isActive = false;
    }


    @Override
    public void onFinishDialog() {
        Logger.w("onfinishdialog", "episode");
        if (isloggedout)
            logoutUser();

        if (isPlayerError) {
            getBinding().playerImage.setVisibility(View.VISIBLE);
            ImageHelper.getInstance(EpisodeActivity.this).loadListImage(getBinding().playerImage, videoDetails.getPosterURL());
            isPlayerError = false;

        } else {
            finish();
        }
    }

    public void setConnectivityListener(NetworkChangeReceiver.ConnectivityReceiverListener listener) {
        NetworkChangeReceiver.connectivityReceiverListener = listener;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        //fragment.playVideo();
        //AppCommonMethod.isInternet = fragment != null;

    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        AudioManager manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (manager.isMusicActive()) {

            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    Logger.i(TAG, "AUDIOFOCUS_GAIN");
                    //restart/resume your sound
                    break;
                case AUDIOFOCUS_LOSS:
                    Logger.e(TAG, "AUDIOFOCUS_LOSS");
                    //Loss of audio focus for a long time
                    //Stop playing the sound
//                    if (fragment != null)
//                        fragment.pauseNontonPlayer();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    Logger.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                    //Loss of audio focus for a short time
                    //Pause playing the sound
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    Logger.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                    //Loss of audio focus for a short time.
                    //But one can duck. Lower the volume of playing the sound
                    break;

                default:
                    //
            }

            // do something - or do it not
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (!isCastConnected) {
            super.onConfigurationChanged(newConfig);
            AppCommonMethod.isOrientationChanged = true;
            if (newConfig.orientation == 2) {
                hideVideoDetail();
            } else {
                showVideoDetail();
            }
        }
    }

    public void showVideoDetail() {
        //  getBinding().rootScroll.setBackgroundColor(getResources().getColor(R.color.black_theme_color));
        getBinding().rootScroll.setVisibility(View.VISIBLE);
//
        ViewGroup.LayoutParams layoutParams = getBinding().header.getLayoutParams();
        layoutParams.height = AppCommonMethod.convertDpToPixel(0);
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getBinding().header.setLayoutParams(layoutParams);
        getBinding().header.requestLayout();
    }


    public void hideVideoDetail() {
        // getBinding().rootScroll.setBackgroundColor(Color.BLACK);
        getBinding().rootScroll.setVisibility(View.GONE);

        ViewGroup.LayoutParams layoutParams = getBinding().header.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getBinding().header.setLayoutParams(layoutParams);
        getBinding().header.requestLayout();
    }

    @Override
    public void railItemClick(RailCommonData item, int position) {
        if (item.getScreenWidget().getType() != null && item.getScreenWidget().getLayout().equalsIgnoreCase(Layouts.HRO.name())) {
            Toast.makeText(EpisodeActivity.this, item.getScreenWidget().getLandingPageType(), Toast.LENGTH_LONG).show();
        } else {
            if (AppCommonMethod.getCheckBCID(item.getEnveuVideoItemBeans().get(position).getBrightcoveVideoId())) {
                Long getVideoId = Long.parseLong(item.getEnveuVideoItemBeans().get(position).getBrightcoveVideoId());
                AppCommonMethod.launchDetailScreen(this, getVideoId, AppConstants.Video, item.getEnveuVideoItemBeans().get(position).getId(), "0", false);
            }
        }
    }

    @Override
    public void moreRailClick(RailCommonData data, int position) {
        if (data.getScreenWidget() != null && data.getScreenWidget().getContentID() != null) {
            String playListId = data.getScreenWidget().getContentID();
            String screenName = "";
            if (data.getScreenWidget().getName() != null) {
                screenName = (String) data.getScreenWidget().getName();
            }
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra("playListId", playListId);
            intent.putExtra("title", screenName);
            intent.putExtra("flag", 0);
            intent.putExtra("shimmerType", 0);
            intent.putExtra("baseCategory", data.getScreenWidget());
            startActivityForResult(intent, 1001);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    boolean fromBingWatch = false;
    int playerOrientation = -1;
    EnveuVideoItemBean nextEpisode = null;

    @Override
    public void bingeWatchCall(String brightcoveId) {
        int nextEpisdoeId = 0;
        int iValue = -1;
        if (seasonEpisodesList != null && seasonEpisodesList.size() > 0) {
            for (int i = 0; i < seasonEpisodesList.size(); i++) {
                int id = seasonEpisodesList.get(i).getId();
                if (id == assestId) {
                    nextEpisdoeId = seasonEpisodesList.get(i + 1).getId();
                    iValue = i + 1;
                    break;
                }
            }
            if (iValue > -1) {
                nextEpisode = seasonEpisodesList.get(iValue);
                getBinding().backButton.setVisibility(View.VISIBLE);
                getBinding().playerImage.setVisibility(View.VISIBLE);
                ImageHelper.getInstance(EpisodeActivity.this).loadListImage(getBinding().playerImage, seasonEpisodesList.get(iValue).getPosterURL());
                AppCommonMethod.isPurchase = false;
                seriesId = AppCommonMethod.seriesId;
                isHitPlayerApi = false;
                fromBingWatch = true;
                //playerOrientation=playerFragment.getCurrentOrientation();
                refreshDetailPage(nextEpisdoeId);
            }

        }

    }

    List<EnveuVideoItemBean> seasonEpisodesList;

    public void episodesList(List<EnveuVideoItemBean> seasonEpisodes) {
        try {
            if (brightCoveVideoId > 0) {
                seasonEpisodesList = new ArrayList<>();
                if (seasonEpisodes != null && seasonEpisodes.size() > 0) {
                    for (int i = 0; i < seasonEpisodes.size(); i++) {
                        if (seasonEpisodes.get(i).getBrightcoveVideoId() != null) {
                            String id = seasonEpisodes.get(i).getBrightcoveVideoId();
                            if (id.equalsIgnoreCase(String.valueOf(brightCoveVideoId))) {
                                seasonEpisodesList.addAll(seasonEpisodes);
                                if (playerFragment != null) {
                                    playerFragment.totalEpisodes(seasonEpisodesList.size());
                                    playerFragment.currentEpisodes(i + 1);
                                }

                                break;
                            }
                        }
                    }

                }
            }

        } catch (Exception ignored) {

        }


    }

    public void showSeasonList(ArrayList<SelectedSeasonModel> list, int selectedSeasonId) {
        getBinding().transparentLayout.setVisibility(View.VISIBLE);

        EpisodeActivity.SeasonListAdapter listAdapter = new EpisodeActivity.SeasonListAdapter(list, selectedSeasonId);
        builder = new AlertDialog.Builder(EpisodeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(EpisodeActivity.this);
        View content = inflater.inflate(R.layout.season_custom_dialog, null);
        builder.setView(content);
        RecyclerView mRecyclerView = content.findViewById(R.id.my_recycler_view);
        ImageView imageView = content.findViewById(R.id.close);
        imageView.setOnClickListener(v -> {
            alertDialog.cancel();
            getBinding().transparentLayout.setVisibility(View.GONE);
        });

        //Creating Adapter to fill data in Dialog
        mRecyclerView.setLayoutManager(new LinearLayoutManager(EpisodeActivity.this));
        mRecyclerView.setAdapter(listAdapter);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(ActivityCompat.getDrawable(EpisodeActivity.this, R.color.transparent_series));
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;


        alertDialog.show();
        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(alertDialog.getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT; // this is where the magic happens
        lWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        alertDialog.getWindow().setAttributes(lWindowParams);

    }

    class SeasonListAdapter extends RecyclerView.Adapter<EpisodeActivity.SeasonListAdapter.ViewHolder> {
        private final ArrayList<SelectedSeasonModel> list;
        private int selectedPos;

        public SeasonListAdapter(ArrayList<SelectedSeasonModel> list, int selectedPos) {
            this.list = list;
            this.selectedPos = selectedPos;
        }

        @NonNull
        @Override
        public EpisodeActivity.SeasonListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_season_listing, parent, false);

            return new EpisodeActivity.SeasonListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final EpisodeActivity.SeasonListAdapter.ViewHolder holder, final int position) {
            holder.season.setText(list.get(position).getList());
            if (list.get(position).isSelected()) {
                holder.season.setTextColor(getResources().getColor(R.color.moretitlecolor));
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
                    seasonTabFragment.updateTotalPages();
                    seasonTabFragment.getSeasonEpisodes();

                }

            });
            //TODO TrackerEvent Selection implementation

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (supportsPiPMode()) {
            try {
                PictureInPictureManager.getInstance().onUserLeaveHint();
                if (playerFragment != null) {
                    playerFragment.hideControls();
                }
            } catch (Exception ignored) {

            }

        }
    }

    @Override
    public void isInPip(boolean status) {
        DetailActivity.isBackStacklost = status;
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (supportsPiPMode()) {
            PictureInPictureManager.getInstance().onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
            playerFragment.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        }
    }

    public boolean supportsPiPMode() {
//        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
        boolean isPipSupported = false;
//        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            isPipSupported = true;
        } else {
            isPipSupported = false;
        }
        return isPipSupported;

    }

    boolean isPlayerError = false;

    @Override
    public void onPlayerError(String error) {
        try {
            getBinding().backButton.setVisibility(View.VISIBLE);
            getBinding().pBar.setVisibility(View.GONE);
            String errorMessage = getString(R.string.player_error);
            if (!NetworkConnectivity.isOnline(this)) {
                if (!isOfflineAvailable) {
                    playerFragment.getBaseVideoView().pause();
                }
            } else {
                if (!errorDialogShown) {
                    isPlayerError = true;
                    errorDialogShown = true;
                    FragmentManager fm = getSupportFragmentManager();
                    errorDialog = AlertDialogSingleButtonFragment.newInstance("", errorMessage, getResources().getString(R.string.ok));
                    errorDialog.setCancelable(false);
                    errorDialog.setAlertDialogCallBack(new AlertDialogFragment.AlertDialogListener() {
                        @Override
                        public void onFinishDialog() {
                            getBinding().backButton.setVisibility(View.VISIBLE);
                            getBinding().playerImage.setVisibility(View.VISIBLE);
                            ImageHelper.getInstance(EpisodeActivity.this).loadListImage(getBinding().playerImage, videoDetails.getPosterURL());
                            isPlayerError = false;

                        }
                    });
                    errorDialog.show(fm, "fragment_alert");
                }
            }
        } catch (Exception e) {

        }

    }


    @Override
    public void onBookmarkCall(int currentPosition) {
        if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            BookmarkingViewModel bookmarkingViewModel = ViewModelProviders.of(this).get(BookmarkingViewModel.class);
            bookmarkingViewModel.bookmarkVideo(token, assestId, (currentPosition / 1000));
        }
    }

    @Override
    public void onBookmarkFinish() {
        if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            BookmarkingViewModel bookmarkingViewModel = ViewModelProviders.of(this).get(BookmarkingViewModel.class);
            bookmarkingViewModel.finishBookmark(token, assestId);
        }
    }

    @Override
    public void onPlayerStart() {
        try {
            Log.w("onPlayerStart", "");
            getBinding().backButton.setVisibility(View.GONE);
            getBinding().playerImage.setVisibility(View.GONE);
            getBinding().pBar.setVisibility(View.GONE);
            String name = "";
            String mediaType = "";
            if (videoDetails.getTitle() != null) {
                name = videoDetails.getTitle();
            }
            if (videoDetails.getAssetType() != null) {
                mediaType = videoDetails.getAssetType();
            }
            AppCommonMethod.trackFcmEvent(name, mediaType, EpisodeActivity.this, 0);
            if (playerFragment != null && seasonEpisodesList != null && seasonEpisodesList.size() > 0) {
                playerFragment.totalEpisodes(seasonEpisodesList.size());
                for (int i = 0; i < seasonEpisodesList.size(); i++) {

                    int id = seasonEpisodesList.get(i).getId();
                    Log.w("episodesId", id + "  " + assestId + "  " + i);
                    if (id == assestId) {
                        playerFragment.currentEpisodes(i + 1);
                        break;
                    } else {
                        // playerFragment.bingeWatchStatus(false);
                    }
                }
            }
            if (seasonEpisodesList.size() > 0) {

            } else {
                if (playerFragment != null) {
                    playerFragment.bingeWatchStatus(false);
                }
            }

        } catch (Exception e) {

        }
    }

    @Override
    public void onAdStarted() {
        try {
            getBinding().pBar.setVisibility(View.GONE);
            getBinding().playerImage.setVisibility(View.GONE);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onDownloadDeleted(@NotNull String videoId, @NotNull Object source) {

    }


    @Override
    public void onDownloadClicked(String videoId, Object position, Object source) {
        boolean loginStatus = preference.getAppPrefLoginStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString());
        if (!loginStatus)
            new ActivityLauncher(this).loginActivity(this, LoginActivity.class);
        else {
            int videoQuality = new SharedPrefHelper(this).getInt(SharedPrefesConstants.DOWNLOAD_QUALITY_INDEX, 4);
            if (KsPreferenceKeys.getInstance().getDownloadOverWifi() == 1 && NetworkHelper.INSTANCE.isWifiEnabled(this)) {
                if (source instanceof UserInteractionFragment) {
                    if (videoQuality != 4) {
                        downloadHelper.startEpisodeDownload(downloadAbleVideo, String.valueOf(seriesDetailBean.getBrightcoveVideoId()), Integer.parseInt(videoDetails.getSeasonNumber()), (String) videoDetails.getEpisodeNo(), videoQuality);
                    } else {
                        selectDownloadVideoQuality(downloadAbleVideo, String.valueOf(seriesDetailBean.getBrightcoveVideoId()), Integer.parseInt(videoDetails.getSeasonNumber()), (String) videoDetails.getEpisodeNo());
                    }
                } else {
                    downloadHelper.findVideo(String.valueOf(videoId), new VideoListener() {
                        @Override
                        public void onVideo(Video video) {
                            if (videoQuality != 4) {
                                downloadHelper.startEpisodeDownload(downloadAbleVideo, String.valueOf(seriesDetailBean.getBrightcoveVideoId()), Integer.parseInt(videoDetails.getSeasonNumber()), (String) videoDetails.getEpisodeNo(), videoQuality);
                            } else {
                                selectDownloadVideoQuality(video, String.valueOf(seriesDetailBean.getBrightcoveVideoId()), Integer.parseInt(videoDetails.getSeasonNumber()), position.toString());
                            }
                        }
                    });
                }

            } else {

                if (KsPreferenceKeys.getInstance().getDownloadOverWifi() == 0) {
                    if (source instanceof UserInteractionFragment) {
                        if (videoQuality != 4) {
                            downloadHelper.startEpisodeDownload(downloadAbleVideo, String.valueOf(seriesDetailBean.getBrightcoveVideoId()), Integer.parseInt(videoDetails.getSeasonNumber()), (String) videoDetails.getEpisodeNo(), videoQuality);
                        } else {
                            selectDownloadVideoQuality(downloadAbleVideo, String.valueOf(seriesDetailBean.getBrightcoveVideoId()), Integer.parseInt(videoDetails.getSeasonNumber()), videoDetails.getEpisodeNo() + "");
                        }
                    } else {
                        downloadHelper.findVideo(String.valueOf(videoId), new VideoListener() {
                            @Override
                            public void onVideo(Video video) {
                                if (videoQuality != 4) {
                                    downloadHelper.startEpisodeDownload(downloadAbleVideo, String.valueOf(seriesDetailBean.getBrightcoveVideoId()), Integer.parseInt(videoDetails.getSeasonNumber()), videoDetails.getEpisodeNo() + "", videoQuality);
                                } else {
                                    selectDownloadVideoQuality(video, String.valueOf(seriesDetailBean.getBrightcoveVideoId()), Integer.parseInt(videoDetails.getSeasonNumber()), position.toString());
                                }
                            }
                        });
                    }

                } else {
                    showWifiSettings(downloadAbleVideo,
                            String.valueOf(seriesDetailBean.getBrightcoveVideoId()),
                            Integer.parseInt(videoDetails.getSeasonNumber()),
                            videoDetails.getEpisodeNo() + "", videoQuality);
                }
                Toast.makeText(this, "NoWifi", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showWifiSettings(Video video, String brightcoveVideoId, int seasonNumber, String episodeNumber, int videoQuality) {
        downloadHelper.changeWifiSetting(new WifiPreferenceListener() {
            @Override
            public void actionP(int value) {
                if (value == 0) {
                    if (downloadHelper.getCatalog() != null) {
                        downloadHelper.allowedMobileDownload();
                        if (videoQuality != 4) {
                            downloadHelper.startEpisodeDownload(downloadAbleVideo, String.valueOf(seriesDetailBean.getBrightcoveVideoId()), Integer.parseInt(videoDetails.getSeasonNumber()), videoDetails.getEpisodeNo() + "", videoQuality);
                        } else {
                            selectDownloadVideoQuality(video, String.valueOf(seriesDetailBean.getBrightcoveVideoId()), Integer.parseInt(videoDetails.getSeasonNumber()), episodeNumber);
                        }
                    }
                }
            }
        });
    }

    private void selectDownloadVideoQuality(Video video, String brightcoveVideoId, int seasonNumber, String episodeNumber) {
        downloadHelper.selectVideoQuality(position -> {
            String[] array = getResources().getStringArray(R.array.download_quality);
            Logger.e("SeriesId", String.valueOf(seriesDetailBean.getBrightcoveVideoId()));
            Logger.e("SeasonNumber", String.valueOf(videoDetails.getSeasonNumber()));
            downloadHelper.startEpisodeDownload(video, brightcoveVideoId, seasonNumber, episodeNumber, position);
        });
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
                        if (videoId.equals(String.valueOf(brightCoveVideoId)))
                            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.START);
                        break;
                    case R.id.pause_download:
                        downloadHelper.pauseVideo(videoId);
                        if (videoId.equals(String.valueOf(brightCoveVideoId)))
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
                        new ActivityLauncher(this).launchMyDownloads();
                        break;
                }
                return false;
            });
        } else {
            if (videoId.equals(String.valueOf(brightCoveVideoId)))
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
        Logger.i(TAG, String.format(
                "Starting to process '%s' video download request", video.getName()));
        if (seasonTabFragment.getSeasonAdapter() != null) {
            seasonTabFragment.getSeasonAdapter().onDownloadRequested(video);
        }
    }

    @Override
    public void onDownloadStarted(@androidx.annotation.NonNull Video video, long l, @androidx.annotation.NonNull Map<String, Serializable> map) {
        Logger.e(TAG, "onDownloadStarted");
        if (video.getId().equals(downloadAbleVideo.getId()))
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.DOWNLOADING);
        if (seasonTabFragment.getSeasonAdapter() != null) {
            seasonTabFragment.getSeasonAdapter().onDownloadStarted(video, l, map);
        }
    }

    @Override
    public void onDownloadProgress(@androidx.annotation.NonNull Video video, @androidx.annotation.NonNull com.brightcove.player.network.DownloadStatus downloadStatus) {
        Logger.e(TAG, "onDownloadProgress" + downloadStatus.getProgress());
        if (video.getId().equals(downloadAbleVideo.getId()))
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.DOWNLOADING);
        userInteractionFragment.setDownloadProgress((float) downloadStatus.getProgress());
        if (seasonTabFragment.getSeasonAdapter() != null) {
            seasonTabFragment.getSeasonAdapter().onDownloadProgress(video, downloadStatus);
        }
    }

    @Override
    public void onDownloadPaused(@androidx.annotation.NonNull Video video, @androidx.annotation.NonNull com.brightcove.player.network.DownloadStatus downloadStatus) {
        Logger.e(TAG, "onDownloadPaused");
        if (video.getId().equals(downloadAbleVideo.getId()))
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.PAUSE);
        if (seasonTabFragment.getSeasonAdapter() != null) {
            seasonTabFragment.getSeasonAdapter().onDownloadPaused(video, downloadStatus);
        }
    }

    @Override
    public void onDownloadCompleted(@androidx.annotation.NonNull Video video, @androidx.annotation.NonNull com.brightcove.player.network.DownloadStatus downloadStatus) {
        Logger.e(TAG, "onDownloadCompleted");
        if (video.getId().equals(downloadAbleVideo.getId()))
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.DOWNLOADED);
        seasonTabFragment.getSeasonAdapter().onDownloadCompleted(video, downloadStatus);
        downloadHelper.updateVideoStatus(com.brightcove.player.network.DownloadStatus.STATUS_COMPLETE, video.getId());
    }

    @Override
    public void onDownloadCanceled(@androidx.annotation.NonNull Video video) {
        Logger.e(TAG, "onDownloadCanceled");
        if (video.getId().equals(downloadAbleVideo.getId())) {
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.START);
            userInteractionFragment.setDownloadProgress(0);
        }
        if (seasonTabFragment.getSeasonAdapter() != null) {
            seasonTabFragment.getSeasonAdapter().onDownloadCanceled(video);
        }
    }

    @Override
    public void onDownloadDeleted(@androidx.annotation.NonNull Video video) {
        Logger.e(TAG, "onDownloadDeleted");
        if (video.getId().equals(downloadAbleVideo.getId()))
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.START);
        seasonTabFragment.getSeasonAdapter().onDownloadDeleted(video);
    }

    @Override
    public void onDownloadFailed(@androidx.annotation.NonNull Video video, @androidx.annotation.NonNull com.brightcove.player.network.DownloadStatus downloadStatus) {
        Logger.e(TAG, "onDownloadFailed");

    }

    @Override
    public void downloadVideo(@androidx.annotation.NonNull Video video) {

    }

    @Override
    public void pauseVideoDownload(Video video) {
        if (video.getId().equals(downloadAbleVideo.getId()))
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.DOWNLOADING);

    }

    @Override
    public void resumeVideoDownload(Video video) {
        if (video.getId().equals(downloadAbleVideo.getId()))
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.PAUSE);
    }

    @Override
    public void deleteVideo(@androidx.annotation.NonNull Video video) {

    }

    @Override
    public void alreadyDownloaded(@androidx.annotation.NonNull Video video) {
        if (video.getId().equals(downloadAbleVideo.getId())) {
            userInteractionFragment.setDownloadStatus(me.vipa.app.enums.DownloadStatus.DOWNLOADED);
            isOfflineAvailable = true;
        }
    }

    @Override
    public void downloadedVideos(@org.jetbrains.annotations.Nullable List<? extends Video> p0) {

    }

    @Override
    public void videoFound(Video video) {
        this.downloadAbleVideo = video;
        // userInteractionFragment.setDownloadable(true);
        userInteractionFragment.setDownloadable(downloadAbleVideo.isOfflinePlaybackAllowed());
    }

    @Override
    public void downloadStatus(String videoId, DownloadStatus downloadStatus) {

    }

    @Override
    public void chromeCastViewConnected(boolean status) {
        if (status) {
            Intent intent = new Intent(this, ExpandedControlsActivity.class);
            intent.putExtra("Asset", videoDetails);
            startActivity(intent);
            finish();
            isCastConnected = true;
        }
    }

}