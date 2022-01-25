package me.vipa.brightcovelibrary;


import static android.content.Context.TELEPHONY_SERVICE;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentTransaction;

import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.cast.GoogleCastEventType;
import com.brightcove.cast.model.BrightcoveCastCustomData;
import com.brightcove.cast.model.CustomData;
import com.brightcove.ima.GoogleIMAComponent;
import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.player.captioning.BrightcoveCaptionFormat;
import com.brightcove.player.captioning.BrightcoveCaptionStyle;
import com.brightcove.player.captioning.LoadCaptionsService;
import com.brightcove.player.captioning.preferences.CaptionConstants;
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent;
import com.brightcove.player.display.VideoDisplayComponent;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.CatalogError;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.management.BrightcoveClosedCaptioningManager;
import com.brightcove.player.model.CuePoint;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Source;
import com.brightcove.player.model.SourceCollection;
import com.brightcove.player.model.Video;
import com.brightcove.player.model.VideoFields;
import com.brightcove.player.pictureinpicture.PictureInPictureManager;
import com.brightcove.player.util.StringUtil;
import com.brightcove.player.view.BaseVideoView;
import com.bumptech.glide.Glide;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdError;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.cast.AdBreakClipInfo;
import com.google.android.gms.cast.AdBreakInfo;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.VastAdsRequest;
import com.google.android.gms.common.images.WebImage;
import com.vipa.brightcovelibrary.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.vipa.brightcovelibrary.callBacks.BackPressCallBack;
import me.vipa.brightcovelibrary.callBacks.PhoneListenerCallBack;
import me.vipa.brightcovelibrary.callBacks.PlayerCallbacks;
import me.vipa.brightcovelibrary.chromecast.ChromeCastCallback;
import me.vipa.brightcovelibrary.chromecast.ChromecastManager;
import me.vipa.brightcovelibrary.utils.ObjectHelper;

public class BrightcovePlayerFragment extends com.brightcove.player.appcompat.BrightcovePlayerFragment implements PlayerControlsFragment.OnFragmentInteractionListener, PlayerCallbacks, NetworkChangeReceiver.ConnectivityReceiverListener, PhoneListenerCallBack, BackPressCallBack, ChromeCastCallback {
    private static final String PROPERTY_APPLICATION_ID = "com.vipa.app";
    public static final String EXTRA_FROM_TRAILER = "extra_from_trailer";
    private static final String TAG = "BrightcovePlayer";
    private static final String VIDEO_VOD = "0";
    private static final String VIDEO_LIVE_TV = "1";

    private OnPlayerInteractionListener mListener;
    private ChromeCastStartedCallBack chromeCastStartedListener;
    private EventEmitter eventEmitter;
    private Video currentVideo;
    private NetworkChangeReceiver receiver = null;
    private Activity mActivity;
    private boolean enable = true;
    private boolean imaEnable = false;
    private boolean bingeWatch = false;
    private int bingeWatchTimer = 0;
    private GoogleIMAComponent googleIMAComponent;
    private boolean fromTrailer = false;
    String videoId = "";
    String assetType = "";
    String selected_track = "";
    String selected_lang = "";
    String applicationLanguage = "";
    private String adRulesURL = "";
    private String poster_image = "";
    private String poster_url = "";
    private int bottomMargin = 0;
    private int currentCaptionLanguage = 0;
    private int currentAudioLanguage = 0;
    private CharSequence[] availableCapitonsList;
    private CharSequence[] availableCapitonsListDisplay;
    private List<String> tracks;
    private CharSequence[] audioTracks;
    private Handler handler;
    private Runnable runnable;
    private long bookmarkPosition = 0l;
    private AppCompatImageView ivWatermark;
    private ProgressBar progressBar;
    private ImageView posterImage, posterImageDefault;
    private String brightcoveAccountId;
    private String brightcovePolicyKey;
    private PlayerControlsFragment playerControlsFragment;
    private String videoType = "0";
    List<CuePoint> cuePointsList;

    private boolean isOfflineVideo = false;
    private boolean isPictureinPictureEnabled = false;
    private boolean isInPictureinPicture = false;
    private FrameLayout container;
    private boolean isAdShowingToUser = true;
    private boolean isBingeWatchTimeCalculate = false;
    boolean isFirstCalled = true;
    private boolean isCastConnected = false;
    private int from = 0;
    private String signLangParentRefId = "";
    private String signLangRefId = "";
    private String isPodcast = "";
    private boolean isFragmentCalled = false;
    private boolean isOfflinePodcast = false;
    private AdsLoader adsLoader;
    String url = "";
    private String UhdUrl = "";
    private boolean isDeviceSupported = false;
    private boolean is4kSupported = false;


    public BrightcovePlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChromecastManager.getInstance().setCallBacks(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            try {

                // Logger.w("IMATAG " + videoId);
                fromTrailer = bundle.getBoolean(EXTRA_FROM_TRAILER, false);
                assetType = bundle.getString("assetType");
                selected_track = bundle.getString("selected_track");
                //  Logger.e("Selectedtrack " + selected_track);
                //Logger.d("asasasasas " + selected_track);
                selected_lang = bundle.getString("selected_lang");
                adRulesURL = bundle.getString("config_vast_tag");
                bingeWatch = bundle.getBoolean("binge_watch");

                signLangParentRefId = bundle.getString("signLangParentRefId");
                signLangRefId = bundle.getString("signLangId");

                if (bundle.getString("podcast") != null) {
                    isPodcast = bundle.getString("podcast");
                }


                bingeWatchTimer = bundle.getInt("binge_watch_timer");


                isAdShowingToUser = bundle.getBoolean("ads_visibility");

                if (bundle.getString("posterUrl") != null) {
                    poster_url = bundle.getString("posterUrl");
                }
                // Logger.w("config_vast_tag" + adRulesURL);

//            Logger.w("IMATAG " + bundle.getString("vast_tag"));
                if (bundle.getString("vast_tag") != null && !bundle.getString("vast_tag").equalsIgnoreCase("")) {
                    adRulesURL = bundle.getString("vast_tag");
                }
                if (bundle.getString("poster_image") != null && !bundle.getString("poster_image").equalsIgnoreCase("")) {
                    poster_image = bundle.getString("poster_image");

                }


                bookmarkPosition = bundle.getLong("bookmarkPosition");
                if (bundle.containsKey("isOffline")) {
                    isOfflineVideo = bundle.getBoolean("isOffline");
                    isOfflinePodcast = bundle.getBoolean("isOfflinePodcast");
                    from = bundle.getInt("from");
                    currentVideo = bundle.getParcelable("videoId");
                }

                if ((isOfflineVideo || fromTrailer) && mActivity != null) {
                    if (from == 1) {
                        Logger.d("setting landscape");
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    }
                } else {
                    if (mActivity != null) {
                        Logger.d("setting orientation no sensor");
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                    }
                }
                if (isOfflineVideo) {

                } else {
                    videoId = bundle.getString("videoId");
                }

            } catch (Exception e) {
                Logger.e(e);
            }
        }
        int is4kValue = isUHD();
        Logger.d(is4kValue + "");
        if (is4kValue == 1) {
            isDeviceSupported = true;
        } else {
            isDeviceSupported = false;
        }

    }

    public int isUHD() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point displaySize = getDisplaySize(display);
        return (displaySize.x >= 3840 && displaySize.y >= 2160) ? 1 : 0;
    }

    private static Point getDisplaySize(Display display) {
        Point displaySize = new Point();
        if (Util.SDK_INT >= 23) {
            getDisplaySizeV23(display, displaySize);
        } else if (Util.SDK_INT >= 17) {
            getDisplaySizeV17(display, displaySize);
        } else if (Util.SDK_INT >= 16) {
            getDisplaySizeV16(display, displaySize);
        } else {
            getDisplaySizeV9(display, displaySize);
        }
        return displaySize;
    }

    @TargetApi(23)
    private static void getDisplaySizeV23(Display display, Point outSize) {
        Display.Mode[] modes = display.getSupportedModes();
        if (modes.length > 0) {
            Display.Mode mode = modes[0];
            outSize.x = mode.getPhysicalWidth();
            outSize.y = mode.getPhysicalHeight();
        }
    }

    @TargetApi(17)
    private static void getDisplaySizeV17(Display display, Point outSize) {
        display.getRealSize(outSize);
    }

    @TargetApi(16)
    private static void getDisplaySizeV16(Display display, Point outSize) {
        display.getSize(outSize);
    }

    private static void getDisplaySizeV9(Display display, Point outSize) {
        outSize.x = display.getWidth();
        outSize.y = display.getHeight();
    }


    private void requestAudioFocus() {

        AudioManager mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        AudioAttributes mAudioAttributes =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mAudioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
        }
        AudioFocusRequest mAudioFocusRequest =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(mAudioAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(new AudioManager.OnAudioFocusChangeListener() {
                        @Override
                        public void onAudioFocusChange(int i) {
                            if (i == AUDIOFOCUS_LOSS) {
                                Logger.w("windowFocusChanged=== " + AUDIOFOCUS_LOSS + " " + exitFromPIP);
                                if (!exitFromPIP) {
                                    baseVideoView.pause();
                                }
                            }
                        }
                    }) // Need to implement listener
                    .build();
        }
        int focusRequest = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            focusRequest = mAudioManager.requestAudioFocus(mAudioFocusRequest);
        }
        switch (focusRequest) {
            case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                // don’t start playback
            case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                // actually start playback
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       /* if (mActivity != null) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }*/
        View result = inflater.inflate(R.layout.fragment_blank, container, false);
        Logger.w("IMATAG view");
        findPlayerId(result);
        super.onCreateView(inflater, container, savedInstanceState);
        bottomMargin = (int) getResources().getDimension(R.dimen.caption_margin);
        return result;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isOfflineVideo || fromTrailer) {
            FrameLayout.LayoutParams captionParams = (FrameLayout.LayoutParams) container.getLayoutParams();
            captionParams.bottomMargin = (int) getResources().getDimension(R.dimen.offline_player_padding);
            captionParams.topMargin = (int) getResources().getDimension(R.dimen.offline_player_padding);
            container.setLayoutParams(captionParams);
        }

    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        mListener = (OnPlayerInteractionListener) mActivity;
        try {
            chromeCastStartedListener = (ChromeCastStartedCallBack) mActivity;
        } catch (Exception e) {

        }


        try {
            ApplicationInfo ai = mActivity.getPackageManager().getApplicationInfo(mActivity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            if (bundle != null) {
                brightcoveAccountId = bundle.getString("brightcove_account_id");
                brightcovePolicyKey = bundle.getString("brightcove_policy_key");
                Logger.w("IMATAG " +brightcoveAccountId + "  " + brightcovePolicyKey);

            }
        } catch (
                PackageManager.NameNotFoundException | NullPointerException e) {

        }
        if (isOfflineVideo) {
            setPlayerWithCallBacks(true, "");
        } else {
            setPlayerWithCallBacks(false, videoId);
        }

        if ((isOfflineVideo || fromTrailer) && mActivity != null && from == 1) {
            Logger.d("setting landscape");
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (VIDEO_VOD.equals(videoType)) {
            ivWatermark.setVisibility(View.VISIBLE);
        } else {
            ivWatermark.setVisibility(View.GONE);
        }

        try {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                FrameLayout.LayoutParams captionParams = (FrameLayout.LayoutParams) container.getLayoutParams();
                captionParams.bottomMargin = (int) getResources().getDimension(R.dimen.live_full_screen_bottom);
                captionParams.topMargin = (int) getResources().getDimension(R.dimen.live_full_screen_bottom);
                container.setLayoutParams(captionParams);

            }

        } catch (Exception ignored) {

        }
    }

    @Override
    public BaseVideoView getBaseVideoView() {
        if (baseVideoView != null) {
            return baseVideoView;
        }
        return null;
    }

    DefaultTrackSelector trackSelector;
    DefaultBandwidthMeter bandwidthMeter;
    private TrackGroupArray trackGroups;
    private MappingTrackSelector.MappedTrackInfo mappedTrackInfo;
    boolean isContentCompleted = false;
    boolean willBingWatchShow = false;

    private void setPlayerWithCallBacks(boolean isOffline, String videoId) {
        bandwidthMeter = new DefaultBandwidthMeter();

        PictureInPictureManager.getInstance().registerActivity(mActivity, baseVideoView);
        baseVideoView.setMediaController((MediaController) null);
        baseVideoView.setupClosedCaptioningRendering();
       /* BrightcoveCaptionStyle brightcoveCaptionStyle=BrightcoveClosedCaptioningManager.getInstance(getActivity()).getStyle();
        int greenColorValue = Color.parseColor("#9F272727");
        brightcoveCaptionStyle.createCaptionStyle("6",
                "sans-serif",greenColorValue,greenColorValue,greenColorValue,greenColorValue,greenColorValue,greenColorValue,greenColorValue,greenColorValue);
        BrightcoveClosedCaptioningManager.getInstance(getActivity()).setStyle(brightcoveCaptionStyle);*/
        if (baseVideoView != null && baseVideoView.getEventEmitter() != null) {
            eventEmitter = baseVideoView.getEventEmitter();

        }

        eventEmitter.enable();
        if (adRulesURL == null || adRulesURL.equalsIgnoreCase("")) {
            imaEnable = false;
        }
        if (imaEnable && !isOffline) {
            if (isAdShowingToUser) {
                setupGoogleIMA();
                Map<String, String> options = new HashMap<>();
                List<String> values = new ArrayList<String>(Arrays.asList(VideoFields.DEFAULT_FIELDS));
                values.remove(VideoFields.HLS_URL);
                options.put("video_fields", StringUtil.join(values, ","));
            }
        }


        if (brightcoveAccountId.isEmpty() || brightcovePolicyKey.isEmpty()) {
            showErrorDialog(getString(R.string.implement_brightcove_creds));
            return;
        }

        if (assetType != null && assetType.equalsIgnoreCase("LIVETV")) {
            //videoId="6125432335001";
            videoType = VIDEO_LIVE_TV;
        } else {
            videoType = VIDEO_VOD;
        }

        if (!isOffline) {
            Logger.w("IMATAG catalog");
            progressBar.setVisibility(View.VISIBLE);
            Catalog catalog = new Catalog(eventEmitter, brightcoveAccountId, brightcovePolicyKey);
            catalog.findVideoByID(videoId, new VideoListener() {
                @Override
                public void onVideo(Video video) {
                    Logger.w("IMATAG catalogin");
                    progressBar.setVisibility(View.VISIBLE);
                    currentVideo = video;
                    cuePointsList = new ArrayList<>();
                    createCuePointList(video);
                    baseVideoView.add(video);
                    isContentCompleted = false;
                    willBingWatchShow = false;
                    baseVideoView.start();
                    baseVideoView.seekTo((int) (bookmarkPosition * 1000));
                    callPlayerControlsFragment();

                    /*if (playerControlsFragment != null)
                        playerControlsFragment.sendTapCallBack(false);*/
                    if (playerControlsFragment != null) {
                        playerControlsFragment.sendTapCallBack(false);
                        playerControlsFragment.setIsOffline(false, from);
                        playerControlsFragment.setFromTrailer(fromTrailer);
                    }


                    Logger.w("videoFound inUp");
                    /*if (ChromecastManager.getInstance().getRunningVideoId()!=null && !ChromecastManager.getInstance().getRunningVideoId().equalsIgnoreCase("")){
                        if (ChromecastManager.getInstance().getRunningVideoId().equalsIgnoreCase(videoId)){
                            onStatusUpdate();
                        }else {
                            castEnabling(video);
                        }
                    }else {
                        castEnabling(video);
                    }*/
                    castEnabling(video);
                    // cast(video);
                }

//                @Override
//                public void onError(String error) {
//                    super.onError(error);
//                    if (!baseVideoView.isPlaying())
//                        showErrorDialog(error);
//                }

                @Override
                public void onError(@NonNull List<CatalogError> errors) {
                    super.onError(errors);
                    if (!baseVideoView.isPlaying())
                        Logger.d("gtgtgtgt " + errors.get(0).getCatalogErrorSubcode());
                    showErrorDialog(errors.get(0).getCatalogErrorSubcode());
                }
            });
        } else {
            baseVideoView.stopPlayback();
            baseVideoView.clear();

            if (baseVideoView != null && baseVideoView.getEventEmitter() != null) {
                eventEmitter = baseVideoView.getEventEmitter();

            }
            eventEmitter.enable();
            baseVideoView.setMediaController((MediaController) null);
            baseVideoView.seekTo((int) (bookmarkPosition * 1000));


            baseVideoView.add(currentVideo);
            baseVideoView.start();

            callPlayerControlsFragment();
            if (playerControlsFragment != null) {
                playerControlsFragment.sendTapCallBack(false);
                playerControlsFragment.setIsOffline(true, from);
            }
        }

        eventEmitter.on(EventType.DID_SET_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Logger.w("IMATAG DID_SET_VIDEO");
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                isFirstCalled = true;
                VideoDisplayComponent videoDisplayComponent = baseVideoView.getVideoDisplay();

                if (videoDisplayComponent instanceof ExoPlayerVideoDisplayComponent) {
                    //Get ExoPlayer
                    ExoPlayer exoPlayer = ((ExoPlayerVideoDisplayComponent) videoDisplayComponent).getExoPlayer();
                    if (exoPlayer instanceof SimpleExoPlayer) {
                        //Get SimpleExoPlayer
                        SimpleExoPlayer simpleExoPlayer = (SimpleExoPlayer) exoPlayer;
                        ((SimpleExoPlayer) exoPlayer).addVideoListener(new com.google.android.exoplayer2.video.VideoListener() {
                            @Override
                            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

                            }

                            @Override
                            public void onRenderedFirstFrame() {

                            }
                        });
                        // Logger.v("onDID_SET_VIDEO: ExoPlayer = " + simpleExoPlayer+" "+trackGroupArray.get(0));


                    }
                }
            }

        });
        eventEmitter.on(EventType.VIDEO_SIZE_KNOWN, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Logger.d("VIDEOSIZEKNOWN_EVENT " + event.getType());
//                float width = event.getIntegerProperty(Event.VIDEO_WIDTH);
//                float height = event.getIntegerProperty(Event.VIDEO_HEIGHT);
//                float aspectRatio = height/width;
//
//                //Get the display metrics
//                DisplayMetrics metrics = new DisplayMetrics();
//                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                int videoWidth = metrics.widthPixels; // We cover the entire display's width
//                int videoHeight = (int) (videoWidth*aspectRatio); //Calculate the height based on the ratio
//
//                // Set the layout params
//                baseVideoView.setLayoutParams(new FrameLayout.LayoutParams(videoWidth,videoHeight));
            }
        });

        //Callback for player progress
        eventEmitter.on(EventType.PROGRESS, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (baseVideoView.getDuration() != 0) {
                    if (playerControlsFragment != null) {
                        playerControlsFragment.setTotalDuration(baseVideoView.getDuration());
                        playerControlsFragment.setCurrentPosition(baseVideoView.getCurrentPosition());
//                        Logger.d("Progress playerProgress");
                        if (mListener != null) {
                            mListener = (OnPlayerInteractionListener) mActivity;
                            mListener.onPlayerInProgress();
                        }
                    }

                    double currentPosition = baseVideoView.getCurrentPosition();
                    if (currentPosition == skipHideTime || currentPosition > skipHideTime) {
                        if (playerControlsFragment != null) {
                            skipHideTime = 0;
                            playerControlsFragment.hideSkipIntro();
                        }
                    }
//                    Logger.d("progressValuess " + bingeWatch + " " + currentPosition + " " + bingeWatchTimer + "  " + baseVideoView.getDuration());
                    if (bingeWatch && bingeWatchTimer > 0) {
                        if (currentPosition >= bingeWatchTimer) {
                            willBingWatchShow = true;
                            if (!isInPictureinPicture) {
                                playerControlsFragment.showBingeWatch(baseVideoView.getDuration() - baseVideoView.getCurrentPosition(), isFirstCalled);
                                isFirstCalled = false;
                            }

                        }
                    }
                }

            }
        });

        //Callback for player pause
        eventEmitter.on(EventType.PAUSE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (playerControlsFragment != null) {
                    playerControlsFragment.SendPlayerPauseState(event.getType());
                }
            }
        });
        //Callback for play
        eventEmitter.on(EventType.PLAY, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Logger.w("IMATAG PLAY");

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (playerControlsFragment != null) {
                    playerControlsFragment.sendPlayerPlayState(event.getType());
                }
                try {
                    if (mListener != null) {
                        mListener = (OnPlayerInteractionListener) mActivity;
                        if (baseVideoView != null && !baseVideoView.isPlaying()) {
                            mListener.onPlayerStart();
                            mListener.notifyMoEngageOnPlayerStart();
                        }

                        Logger.w("Playerstart PLAY");

                        if (playerControlsFragment != null) {
                            if (!isInPictureinPicture) {
                                playerControlsFragment.sendTapCallBack(true);
                                playerControlsFragment.startHandler();
                            }
                        }
                    }
                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        if (playerControlsFragment != null && playerControlsFragment.fullscreen != null) {
                            playerControlsFragment.fullscreen.setBackgroundResource(R.drawable.exit_full_screen);
                        }
                    }

                    Map<DeliveryType, SourceCollection> map = currentVideo.getSourceCollections();
                    SourceCollection sor = map.get(DeliveryType.valueOf("DASH"));
                    Set<Source> se = sor.getSources();
                    Object[] ar = se.toArray();
                    Logger.d("dffdfddf " + ar.length);
                    for (int i = 0; i < ar.length; i++) {

                        Source hp = (Source) ar[i];
                        Map<String, Object> ma = hp.getProperties();
                        url = (String) ma.get("url");
                        if (url.contains("avc1_hvc1_mp4a")) {
                            Logger.d("dffdfddf " + url);
                            UhdUrl = url;
                            break;
                        }
                    }



                    if (!UhdUrl.equalsIgnoreCase("") && UhdUrl.contains("avc1_hvc1_mp4a")) {
                        is4kSupported = true;
                        playerControlsFragment.set4kSupported(is4kSupported);
                    } else {
                        is4kSupported = false;
                    }



//

                } catch (Exception e) {

                }
            }
        });
        //Callback for buffering started
        eventEmitter.on(EventType.BUFFERING_STARTED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (Network.HaveNetworkConnection(mActivity)) {
                    Logger.w("IMATAG BUFFERING_STARTED");
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    showErrorDialog(getString(R.string.no_internet));
                }
            }
        });
        //Callback for buffering completed
        eventEmitter.on(EventType.BUFFERING_COMPLETED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Logger.w("IMATAG BUFFERING_COMPLETE");
                progressBar.setVisibility(View.GONE);
//                if (mListener != null) {
//                    mListener = (OnPlayerInteractionListener) mActivity;
//                    mListener.OnPlayerCompleted();
//                }
                if (playerControlsFragment != null) {
                    playerControlsFragment.setTotalDuration(baseVideoView.getDuration());
                }
            }
        });

        //Callback for Playback finish
        eventEmitter.on(EventType.COMPLETED, new EventListener() {
            @Override
            public void processEvent(Event event) {

                baseVideoView.stopPlayback();
                isContentCompleted = true;
                willBingWatchShow = false;
                baseVideoView.seekTo(baseVideoView.getDuration());
                if (playerControlsFragment != null) {
                    mListener.notifyMoEngageOnPlayerEnd();
                    playerControlsFragment.hideControls();
                    if (!isInPictureinPicture) {
                        if (playerControlsFragment.bingeLay.getVisibility() == View.VISIBLE) {
                            playerControlsFragment.backArrow.setVisibility(View.VISIBLE);
                        } else {
                            playerControlsFragment.sendVideoCompletedState(event.getType());
                        }

                    }
                }
                progressBar.setVisibility(View.GONE);
                eventEmitter.disable();
                mListener.onBookmarkFinish();
                handler.removeCallbacksAndMessages(runnable);
                if (bingeWatch) {
                    isBingeWatchTimeCalculate = false;
                    mListener.bingeWatchCall(brightcoveAccountId);
                }

            }
        });

        //Callback for Playback error
        eventEmitter.on(EventType.ERROR, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Exception exception = (Exception) event.properties.get(Event.ERROR);
                Logger.w("IMATAG " + exception);
                if (assetType != null && assetType.equalsIgnoreCase("LIVETV")) {

                } else {
                    if (exception != null) {
                        if (exception instanceof AdError) {

                            /*if (((AdError) exception).getErrorCodeNumber() == 402){

                            }
                            else if (((AdError) exception).getErrorCodeNumber() == 1005){

                            }else if (((AdError) exception).getErrorCodeNumber() == 900){

                            }
                            else {
                                showErrorDialog("");
                            }*/

                        } else {
                            showErrorDialog("");
                        }
                    } else {
                        showErrorDialog("");
                    }
                }


                String message = (String) event.properties.get(Event.ERROR_MESSAGE);
                Video video = (Video) event.properties.get(Event.VIDEO);
                Source source = (Source) event.properties.get(Event.SOURCE);
                if (event.properties.containsKey("error_code")) {
                    String errorCode = (String) event.properties.get("error_code");
                }
            }
        });

        eventEmitter.on(EventType.DID_REMOVE_VIDEO_STILL, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (playerControlsFragment != null) {
                    //progressBar.setVisibility(View.VISIBLE);
                    playerControlsFragment.setFragmenmtVisibility();
                }
                //progressBar.setVisibility(View.GONE);
            }
        });
        eventEmitter.on(EventType.CAPTIONS_AVAILABLE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Logger.w("captionHide CAPTIONS_AVAILABLE");
                if (baseVideoView.getClosedCaptioningController().checkIfCaptionsExist(currentVideo)) {
                    Logger.w("captionHide in");
                    captionStyleUpdate();
                    if (playerControlsFragment != null) {
                        playerControlsFragment.sendCaptionAvailable(event.getType());
                    }
                }
            }
        });
        eventEmitter.on(EventType.CAPTIONS_LANGUAGES, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Logger.w("captionHide CAPTIONS_LANGUAGES");
                List captions = (List) event.properties.get("languages");
                if (captions.size() >= 1)
                    captionStyleUpdate();
                if (playerControlsFragment != null) {
                    playerControlsFragment.sendCaptionAvailable(event.getType());
                }

            }
        });
        eventEmitter.on(EventType.CAPTIONS_DIALOG_SETTINGS, new EventListener() {
            @Override
            public void processEvent(Event event) {
            }
        });

        //Checking Audio Availabilty
        eventEmitter.on(EventType.AUDIO_TRACKS, event -> {
            Logger.d(EventType.AUDIO_TRACKS + " properties: " + event);
            tracks = (List) event.properties.get(Event.TRACKS);
            if (tracks.size() > 1) {
                if (playerControlsFragment != null) {
                    playerControlsFragment.sendAudioAvailable(event.getType());
                }
            }

            String selectedTrack = (String) event.properties.get(Event.SELECTED_TRACK);
            if (selectedTrack != null) {
                for (int i = 0; i < tracks.size(); ++i) {
                    if (tracks.get(i).equals(selectedTrack)) {
                        currentAudioLanguage = i;
                        break;
                    }
                }
            }
            audioTracks = new CharSequence[tracks.size()];
            for (int i = 0; i < this.tracks.size(); ++i) {
                audioTracks[i] = this.tracks.get(i);
            }

        });

        eventEmitter.on(EventType.DID_PLAY, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (playerControlsFragment != null) {
                    if (!isBingeWatchTimeCalculate) {
                        Logger.w("totalDuartion " + baseVideoView.getDuration());
                        Logger.w("totalDuartion " + bingeWatchTimer * 1000);
                        Logger.w("totalDuartion " + (baseVideoView.getDuration() - bingeWatchTimer * 1000));
                        int timeCalculation = baseVideoView.getDuration() - bingeWatchTimer * 1000;
                        if (timeCalculation > bingeWatchTimer) {
                            isBingeWatchTimeCalculate = true;
                            bingeWatchTimer = baseVideoView.getDuration() - bingeWatchTimer * 1000;
                        }

                    }

                    playerControlsFragment.setTotalDuration(baseVideoView.getDuration());
                    playerControlsFragment.setCurrentPosition(baseVideoView.getCurrentPosition());
                    playerControlsFragment.sendTapCallBack(true);
                }
                startBookmarking();

            }
        });

        //Check if media is loaded
        eventEmitter.on(EventType.READY_TO_PLAY, new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (!isOffline) {
                    getSelectedTrack();
                    if (!fromTrailer) {
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    }
                }
            }
        });


        eventEmitter.on(EventType.DID_ENTER_PICTURE_IN_PICTURE_MODE, new EventListener() {
            @Override
            public void processEvent(Event event) {

                if (playerControlsFragment != null) {
                    // playerControlsFragment.sendTapCallBack(false);
                    if (!baseVideoView.isPlaying()) {
                        baseVideoView.start();
                        /*if (mListener!=null && playerControlsFragment!=null){
                            mListener.isInPip(true);
                            playerControlsFragment.hideControls();
                            playerControlsFragment.isInPip(true);
                        }*/
                    }
                }
            }
        });
        eventEmitter.on(EventType.DID_EXIT_PICTURE_IN_PICTURE_MODE, new EventListener() {
            @Override
            public void processEvent(Event event) {

                if (playerControlsFragment != null) {
                    //  baseVideoView.stopPlayback();
                }
            }
        });

        eventEmitter.on(GoogleCastEventType.CAST_SESSION_STARTED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                boolean playing = ChromecastManager.getInstance().updatePlayingId(BrightcovePlayerFragment.this.videoId);
                Logger.w("videoFound inDown");
                ChromecastManager.getInstance().loadRemoteMediaOtt(BrightcovePlayerFragment.this.videoId);
                if (baseVideoView != null) {
                    baseVideoView.pause();
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        eventEmitter.on(GoogleCastEventType.LOAD_MEDIA_INFO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Logger.w("mediaInfo " + event.toString());
                //googleCastComponent.loadMediaInfo(mediaInfo);
            }
        });


        eventEmitter.on(GoogleCastEventType.CAST_SESSION_ENDED, new EventListener() {
            @Override
            public void processEvent(Event event) {

            }
        });
        CustomData customData = new BrightcoveCastCustomData.Builder(getActivity())
                .setAccountId(brightcoveAccountId)
                // Set your account’s policy key
                .setPolicyKey(brightcovePolicyKey)
                // Optional: Set your Edge Playback Authorization (EPA) JWT token here
                // Note that if you set the EPA token, you will not need to set the Policy Key
                .setBrightcoveAuthorizationToken(null)
                // Optional: For SSAI videos, set your adConfigId here
                // Set your Analytics application ID here
                .setApplicationId(PROPERTY_APPLICATION_ID)
                .build();

        googleCastComponent = new GoogleCastComponent.Builder(eventEmitter, getActivity())
                .setAutoPlay(true)
                .setEnableCustomData(true)
                .setCustomData(customData)
                .build();
        googleCastComponent.isSessionAvailable();

    }

    private void captionStyleUpdate() {
        BrightcoveClosedCaptioningManager manager = BrightcoveClosedCaptioningManager.getInstance(getActivity());
        BrightcoveCaptionStyle captionStyle = BrightcoveCaptionStyle.builder()
                .preset(CaptionConstants.PRESET_CUSTOM) // building a custom preset
                .fontSize("0.8") // or your desired font size
                .typeface("Arial") // or your desired typeFace
                .foregroundColor(Color.WHITE) // or your desired text foreground color
                .foregroundOpacity(CaptionConstants.DEFAULT_FOREGROUND_OPACITY) // or your desired text foreground opacity
                .edgeColor(Color.GREEN) // or your desired text edge color
                .edgeType(CaptionConstants.DEFAULT_EDGE_TYPE) // or your desired text edge type
                .backgroundColor(Color.BLACK) // or your desired text background color
                .backgroundOpacity(Color.BLACK) // or your desired text foreground opacity
                .windowColor(Color.MAGENTA) // or your desired text window color)
                .windowOpacity(CaptionConstants.DEFAULT_WINDOW_OPACITY) // or your desired text window opacity
                .build();
        manager.setStyle(captionStyle);
    }

    GoogleCastComponent googleCastComponent;

    private void cast(Video video) {
        ChromecastManager.getInstance().setupCastListener();
    }

    MediaInfo mediaInfo;

    private void castEnabling(Video video) {
        try {
            Map<DeliveryType, SourceCollection> map = video.getSourceCollections();
            SourceCollection sor = map.get(DeliveryType.valueOf("DASH"));
            Set<Source> se = sor.getSources();
            Object[] ar = se.toArray();
            Source hp = (Source) ar[0];
            Map<String, Object> ma = hp.getProperties();
            url = (String) ma.get("url");
            MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
            movieMetadata.putString(MediaMetadata.KEY_TITLE, video.getName());
            movieMetadata.addImage(new WebImage(Uri.parse(video.getPosterImage().toString())));
            //movieMetadata.addImage(new WebImage(Uri.parse(mSelectedMedia.getImage(1))));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("adTagUrl", adRulesURL);

            Logger.w("chromecastUrl " + url);

            List<AdBreakClipInfo> adBreakClipInfoList = new ArrayList<>();
            VastAdsRequest vastRequest = new VastAdsRequest.Builder().setAdTagUrl("https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpreonly&cmsid=496&vid=short_onecue&correlator=").build();
            AdBreakClipInfo clipInfo1 = new AdBreakClipInfo.Builder("100").setVastAdsRequest(vastRequest).build();
            adBreakClipInfoList.add(clipInfo1);

            List<AdBreakInfo> adBreakInfoList = new ArrayList<>();
            final String[] breakClipIds = new String[]{"100"};
            AdBreakInfo adBreakInfo1 = new AdBreakInfo.Builder(0).setBreakClipIds(breakClipIds).setId("101").build();
            adBreakInfoList.add(adBreakInfo1);

            mediaInfo = new MediaInfo.Builder(url)
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setContentType("videos/mp4")
                    .setMetadata(movieMetadata)
                    .setStreamDuration(video.getDuration() * 1000)
                    .build();

            Logger.w("mediaInfo " + mediaInfo.toString());
            if (mediaInfo != null) {
                loadMediaInfo(eventEmitter, video);
            }

        } catch (Exception e) {
            if (ChromecastManager.getInstance().getmCastContext(getActivity()) != null && ChromecastManager.getInstance().getmCastContext(getActivity()).getSessionManager() != null) {
                ChromecastManager.getInstance().getmCastContext(getActivity()).getSessionManager().endCurrentSession(true);
            }
        }

    }

    private void loadMediaInfo(EventEmitter emitter, Video video) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> properties = new HashMap<>();
                properties.put(GoogleCastComponent.CAST_MEDIA_INFO, mediaInfo);
                properties.put(GoogleCastComponent.CAST_MEDIA_PLAY_POSITION, 0);
                emitter.emit(GoogleCastEventType.LOAD_MEDIA_INFO, properties);

                Map<String, Object> properties2 = new HashMap<>();
                properties2.put("value", brightcoveAccountId);
                emitter.emit("account", properties2);
            }
        }, 2000);

    }


    private void hideProgres() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Logger.w("IMATAG BUFFERING_COMPLETED");
                        progressBar.setVisibility(View.GONE);
                    }
                }, 1400);
            }
        });
    }

    private void startBookmarking() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (baseVideoView != null) {
                    double totalDuration = baseVideoView.getDuration();
                    double currentPosition = baseVideoView.getCurrentPosition();
                    double percentagePlayed = ((currentPosition / totalDuration) * 100L);
                    if (percentagePlayed > 10 && percentagePlayed <= 95) {
                        if (mListener != null) {
                            mListener = (OnPlayerInteractionListener) mActivity;
                            mListener.onBookmarkCall(baseVideoView.getCurrentPosition());
                        }
                        if (handler != null) {
                            handler.postDelayed(this, 10000);
                        }
                    } else if (percentagePlayed > 95) {
                        if (mListener != null) {
                            mListener = (OnPlayerInteractionListener) mActivity;
                            mListener.onBookmarkFinish();
                        }
                        Logger.d("PercentagePlayed " + percentagePlayed + "");
                    } else {
                        if (handler != null) {
                            handler.postDelayed(this, 10000);
                        }
                    }
                }
            }
        };
        handler.postDelayed(runnable, 10000);
    }

    private void showAudioTracksDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity, R.style.CustomDialogTheme);
        String[] labeledTrack = new String[audioTracks.length];
        for (int i = 0, audioTracksLength = audioTracks.length; i < audioTracksLength; i++) {
            labeledTrack[i] = getLanguageLabel(audioTracks[i].toString());
        }
        alertDialog.setTitle(getString(R.string.audio_track_selection)).setSingleChoiceItems(labeledTrack, currentAudioLanguage, (dialog, which) -> {
            currentAudioLanguage = which;
            selectAudioTrack(which);

        }).setPositiveButton(mActivity.getResources().getString(R.string.ok), (dialog, which) -> dialog.dismiss()).show();
    }

    private String getLanguageLabel(String code) {
        if (ObjectHelper.isEmpty(code)) {
            return "";
        } else if (ObjectHelper.isSame(code, "en-US") || ObjectHelper.startWith(code, "en")) {
            return getString(R.string.lang_english);
        } else if (ObjectHelper.isSame(code, "th-Th") || ObjectHelper.startWith(code, "th")) {
            return getString(R.string.lang_thai);
        } else if (ObjectHelper.isSame(code, "zh-Hans")) {
            return getString(R.string.lang_chinese_simp);
        } else if (ObjectHelper.isSame(code, "zh-Hant")) {
            return getString(R.string.lang_chinese_trad);
        } else if (ObjectHelper.isSame(code, "de")) {
            return getString(R.string.lang_german);
        } else if (ObjectHelper.isSame(code, "ja-JP") || ObjectHelper.startWith(code, "ja")) {
            return getString(R.string.lang_japanese);
        } else {
            return code;
        }
    }

    protected void selectAudioTrack(int trackIndex) {
        String track = this.tracks.get(trackIndex);
        Map<String, Object> properties = new HashMap<>();
        properties.put(Event.SELECTED_TRACK, track);
        this.eventEmitter.emit(EventType.SELECT_AUDIO_TRACK, properties);
    }

    private void findPlayerId(View view) {
        baseVideoView = view.findViewById(R.id.brightcove_video_view);
        ivWatermark = view.findViewById(R.id.iv_watermark);
        progressBar = view.findViewById(R.id.pBar);
        posterImage = view.findViewById(R.id.poster_image);
        posterImageDefault = view.findViewById(R.id.poster_image_default);
        Logger.w("IMATAG viewFind");
        progressBar.setVisibility(View.VISIBLE);
        container = view.findViewById(R.id.container);
        if (!isOfflineVideo) {
            if (isPodcast.equalsIgnoreCase("true")) {
                if (poster_url != "" && poster_url != null) {
                    posterImageDefault.setVisibility(View.VISIBLE);
                    posterImage.setVisibility(View.GONE);

                    Glide.with(getActivity())
                            .load(poster_url)
                            .placeholder(R.drawable.splash)
                            .into(posterImageDefault);
                } else {
                    posterImageDefault.setVisibility(View.GONE);
                    posterImage.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(poster_url)
                            .placeholder(R.drawable.splash)
                            .into(posterImage);
                }
            } else {
                posterImage.setVisibility(View.GONE);
                posterImageDefault.setVisibility(View.GONE);
            }
        } else {
            if (isOfflineVideo) {
                if (isOfflinePodcast) {
                    Glide.with(getActivity())
                            .load(R.drawable.splash)
                            .placeholder(R.drawable.splash)
                            .into(posterImage);
                    posterImageDefault.setVisibility(View.GONE);
                    posterImage.setVisibility(View.VISIBLE);
                } else {
                    posterImageDefault.setVisibility(View.GONE);
                    posterImage.setVisibility(View.GONE);
                }
            }
        }

       /* progressBar.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == View.VISIBLE) {
                    if (playerControlsFragment!=null) {
                        playerControlsFragment.hideControls();
                    }
                } else {
                    if (playerControlsFragment!=null) {
                        playerControlsFragment.showControls();
                    }
                }
            }
        });*/
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mActivity != null && !(mActivity instanceof OnPlayerInteractionListener)) {
            try {
                throw new Exception("Activity (" + mActivity + ") must implement OnPlayerInteractionListener");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void showErrorDialog(String exception) {
        if (mListener != null) {
            mListener.onPlayerError(exception);
        }
        try {
            if (baseVideoView != null) {
                if (!isOfflineVideo) {
                    baseVideoView.stopPlayback();
                    baseVideoView.removeListeners();
                }

            }
        } catch (Exception ex) {
            Logger.w(ex);
        }

    }

    private void callPlayerControlsFragment() {
        Logger.d("method called from " + Logger.getTag());
        if (!isAdded()) return;

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        if (playerControlsFragment == null) {
            try {
                playerControlsFragment = new PlayerControlsFragment();
                transaction.add(R.id.container, playerControlsFragment, "PlayerControlsFragment");
                transaction.addToBackStack(null);
                transaction.commit();
                playerControlsFragment.setVideoType(videoType);
                playerControlsFragment.setPlayerCallBacks(this);
                playerControlsFragment.setListener(this);

                new Handler(Looper.myLooper()).postDelayed(() -> {
                    if (playerControlsFragment != null) {
                        playerControlsFragment.setIsSignEnable(signLangParentRefId, signLangRefId);
                    }
                }, 1500);

                FrameLayout.LayoutParams captionParams = (FrameLayout.LayoutParams) container.getLayoutParams();
                captionParams.bottomMargin = (int) 0;
                captionParams.topMargin = (int) 0;
                container.setLayoutParams(captionParams);
            } catch (Exception ex) {
                Logger.w(ex);
            }


        }
    }

    @Override
    public void playPause() {
        Logger.w("windowFocusChanged=3 " + exitFromPIP);
        if (baseVideoView != null) {
            if (baseVideoView.isPlaying()) {
                baseVideoView.pause();
            } else {
                baseVideoView.start();
            }

        }

    }

    public boolean isPlaying() {
        if (baseVideoView != null) {
            return baseVideoView.isPlaying();
        } else {
            return false;
        }
    }

    @Override
    public void Forward() {
        if (baseVideoView != null) {
            baseVideoView.seekTo(baseVideoView.getCurrentPosition() + 10000);
            if (playerControlsFragment != null) {
                playerControlsFragment.sendPlayerCurrentPosition(baseVideoView.getCurrentPosition());
            }
        }
    }

    @Override
    public void Rewind() {
        if (baseVideoView != null) {
            baseVideoView.seekTo(baseVideoView.getCurrentPosition() - 10000);
            if (playerControlsFragment != null) {
                playerControlsFragment.sendPlayerCurrentPosition(baseVideoView.getCurrentPosition());
            }
        }
    }

    @Override
    public void skipIntro() {
        if (baseVideoView != null) {
            baseVideoView.seekTo(skipIntroPosition);
            if (playerControlsFragment != null) {
                skipIntroPosition = 0;
                playerControlsFragment.skipIntro(titleOnButton);
                playerControlsFragment.sendPlayerCurrentPosition(baseVideoView.getCurrentPosition());
            }
        }
    }

    @Override
    public void bingeWatch() {
        mListener.bingeWatchCall(brightcoveAccountId);
    }

    @Override
    public void SeekbarLastPosition(long position) {
        if (baseVideoView != null) {
            baseVideoView.seekTo((int) position);
        }
    }

    @Override
    public void subtitlePopup() {
        if (baseVideoView != null) {
            if (baseVideoView.getClosedCaptioningController().checkIfCaptionsExist(currentVideo)) {
                showCaptionDialog();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showCaptionDialog() {
        baseVideoView.setClosedCaptioningEnabled(true);
        List<Pair<Uri, BrightcoveCaptionFormat>> pairs = (List) baseVideoView.getCurrentVideo().getProperties().get("captionSources");
        availableCapitonsList = new CharSequence[pairs.size() + 1];
        availableCapitonsListDisplay = new CharSequence[pairs.size() + 1];

        int i = 0;
        availableCapitonsList[i] = getString(R.string.none);
        availableCapitonsListDisplay[i] = getString(R.string.none);
        for (Pair<Uri, BrightcoveCaptionFormat> pair : pairs) {
            i++;
            availableCapitonsList[i] = pair.second.language();
            availableCapitonsListDisplay[i] = getLanguageLabel(pair.second.language());
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity, R.style.CustomDialogTheme);
        alertDialog.setTitle(getString(R.string.caption_selection)).setSingleChoiceItems(availableCapitonsListDisplay, currentCaptionLanguage, (dialog, which) -> {
            currentCaptionLanguage = which;
            selectCaptions(currentCaptionLanguage);
        }).setPositiveButton(mActivity.getResources().getString(R.string.ok), (dialog, which) -> dialog.dismiss()).show();
    }

    void selectCaptions(int trackIndex) {
        if (trackIndex != 0 && this.availableCapitonsList != null && this.availableCapitonsList.length > 0) {
            baseVideoView.getClosedCaptioningController().saveClosedCaptioningState(true);
            baseVideoView.getClosedCaptioningController().setShouldImportSystemSettings(true);
            String selectedLanguageCode = (String) this.availableCapitonsList[trackIndex];
            LoadCaptionsService loadCaptionsService = baseVideoView.getClosedCaptioningController().getLoadCaptionsService();
            if (baseVideoView.getClosedCaptioningController().getLoadCaptionsService() != null) {
                Pair<Uri, BrightcoveCaptionFormat> pair = getCaptionsForLanguageCode(selectedLanguageCode);
                HashMap properties;
                if (!((Uri) pair.first).equals(Uri.EMPTY)) {
                    if (!((Uri) pair.first).toString().startsWith("brightcove://")) {
                        loadCaptionsService.loadCaptions(pair.first, pair.second.type());
                    }

                    properties = new HashMap();
                    properties.put("captionFormat", pair.second);
                    properties.put("captionUri", pair.first);

                    this.eventEmitter.emit(EventType.SELECT_CLOSED_CAPTION_TRACK, properties);
                } else {
                    properties = new HashMap();
                    properties.put("boolean", true);
                    this.eventEmitter.emit(EventType.TOGGLE_CLOSED_CAPTIONS, properties);
                }
            }
        } else {
            baseVideoView.getClosedCaptioningController().saveClosedCaptioningState(false);
        }

    }

    private Pair<Uri, BrightcoveCaptionFormat> getCaptionsForLanguageCode(String languageCode) {
        List pairs = (List) baseVideoView.getCurrentVideo().getProperties().get("captionSources");
        if (pairs != null) {
            Iterator var3 = pairs.iterator();

            while (var3.hasNext()) {
                Pair<Uri, BrightcoveCaptionFormat> pair = (Pair) var3.next();
                if (pair.second.language().equals(languageCode)) {
                    return pair;
                }
            }
        }
        return null;
    }


    @Override
    public void audioTrackPopup() {
        if (baseVideoView != null) {
//            baseVideoView.getAudioTracksController().showAudioTracksDialog();
            if (selected_lang.equalsIgnoreCase("Thai")) {
                Utils.updateLanguage("th", getActivity());
            } else if (selected_lang.equalsIgnoreCase("English")) {
                Utils.updateLanguage("en", getActivity());
            }
            showAudioTracksDialog();
        }
    }

    @Override
    public void finishPlayer() {
        checkBackButtonClickOrientation();
    }

    public void stopPlayer() {
        if (baseVideoView != null) {
            baseVideoView.stopPlayback();
        }
    }

    private void checkBackButtonClickOrientation() {

        int orientation = getResources().getConfiguration().orientation;
        if (isOfflineVideo || fromTrailer) {
            if (mListener != null) {
                mListener.finishActivity();
            } else {
                mActivity.finish();
            }
        } else {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            } else {

                if (baseVideoView != null) {
                    baseVideoView.stopPlayback();
                }
            }
        }
    }

    @Override
    public void checkOrientation(ImageView id) {
        FrameLayout.LayoutParams captionParams = (FrameLayout.LayoutParams) container.getLayoutParams();
        captionParams.bottomMargin = (int) 0;
        captionParams.topMargin = (int) 0;
        container.setLayoutParams(captionParams);
        if (id.getId() == R.id.backArrow) {
            _isOrienataion(1, id);
        } else {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                id.setBackgroundResource(R.drawable.full_screen);
            } else {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                id.setBackgroundResource(R.drawable.exit_full_screen);
            }
        }
    }

    private void _isOrienataion(int i, ImageView id) {
        int orientation = mActivity.getResources().getConfiguration().orientation;
        if (isOfflineVideo || fromTrailer) {
            if (mListener != null) {
                mListener.finishActivity();
            } else {
                mActivity.finish();
            }
        }
        {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (mListener != null) {
                    mListener.finishActivity();
                } else {
                    mActivity.finish();
                }
            } else {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                //id.setBackgroundResource(R.drawable.full_screen);
            }
        }
    }

    @Override
    public void replay() {
        isContentCompleted = false;
        eventEmitter.enable();
        baseVideoView.seekTo(0);
        baseVideoView.start();
    }


    @Override
    public void showPlayerController(boolean isVisible) {

        if (baseVideoView.getClosedCaptioningView() != null) {
            FrameLayout.LayoutParams captionParams = (FrameLayout.LayoutParams) baseVideoView.getClosedCaptioningView().getLayoutParams();
            if (isVisible)
                captionParams.bottomMargin = (int) getResources().getDimension(R.dimen.caption_margin_default);
            else
                captionParams.bottomMargin = (int) getResources().getDimension(R.dimen.caption_margin_default);
            //  baseVideoView.getClosedCaptioningView().setLayoutParams(captionParams);
        }
    }


    @Override
    public void onPause() {
        super.onPause();

//        if (mActivity != null) {
//            if (receiver != null)
//                mActivity.unregisterReceiver(receiver);
//        }


        if (handler != null) {
            handler.removeCallbacksAndMessages(runnable);
            handler = null;
        }
//        if (isApplicationSentToBackground(mActivity)) {
//            // Do what you want to do on detecting Home Key being Pressed
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                if (enable) {
//
//                    if (playerControlsFragment!=null) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            if(!baseVideoView.isPlaying()){
//                                baseVideoView.start();
//                            }
//                            // playerControlsFragment.sendTapCallBack(false);
//                            //  playerControlsFragment.hideControls();
//
//                        }
//                    }
//
//               //     PictureInPictureManager.getInstance().enterPictureInPictureMode();
//                    // PictureInPictureManager.getInstance().setOnUserLeaveEnabled(true);
////                    PictureInPictureManager.getInstance().onUserLeaveHint();
//                } else {
//                    baseVideoView.pause();
//                }
//            }
//        } else {
//            baseVideoView.pause();
//        }
    }


    public boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestAudioFocus();
//        registerBroadcast();
    }

    private void registerBroadcast() {
        receiver = NetworkChangeReceiver.getInstance();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        mActivity.registerReceiver(receiver, filter);
        receiver.setConnectivityReceiverListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            if (baseVideoView != null) {
                baseVideoView.pause();
                showErrorDialog(getString(R.string.no_internet));
            }
        }
    }

    @Override
    public void onCallStateRinging() {
        if (baseVideoView != null) {
            baseVideoView.pause();
        }
    }

    @Override
    public void onCallStateIdle(int state) {

        if (baseVideoView != null) {
            baseVideoView.start();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            TelephonyManager mgr = (TelephonyManager) mActivity.getApplicationContext().getSystemService(TELEPHONY_SERVICE);
            if (mgr != null) {
                mgr.listen(PhoneStateListenerHelper.getInstance(this), PhoneStateListener.LISTEN_CALL_STATE);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        TelephonyManager mgr = (TelephonyManager) mActivity.getApplicationContext().getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(PhoneStateListenerHelper.getInstance(this), PhoneStateListener.LISTEN_NONE);
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(runnable);
            handler = null;
        }
        baseVideoView.stopPlayback();
        if (mActivity != null && !mActivity.isFinishing()) {
            if (assetType != null) {
                if (assetType.equalsIgnoreCase("EPISODES") || assetType.equalsIgnoreCase("EPISODE")) {

                } else {
                    finishPlayer();
                }
            }

        }


    }


    Configuration currentConfig = null;

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        Logger.d("sbdbhdbsh " + exitFromPIP+"");
        if (!isCastConnected) {
            super.onConfigurationChanged(newConfig);
            if (!isOfflineVideo || !fromTrailer) {
                Utils.updateLanguage(applicationLanguage, mActivity);
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Logger.d("sbdbhdbsh " + "enter1");
                    mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    if (playerControlsFragment != null) {
                        playerControlsFragment.sendLandscapeCallback();
                    }
                    bottomMargin = (int) getResources().getDimension(R.dimen.caption_margin_landscape);
                    currentConfig = newConfig;
                    if (adStarted) {
                        baseVideoView.setPadding(0, 15, 10, 15);
                    }

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int screenHeight = displayMetrics.heightPixels;
                    double ratio = 1.78;
                    Double screenWidth = screenHeight * ratio;

                    Logger.d("screen dimen h: " + screenHeight + " | w: " + screenWidth);
                    baseVideoView.setLayoutParams(new FrameLayout.LayoutParams(screenWidth.intValue(), screenHeight, Gravity.CENTER));

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            Gravity.CENTER
                    );
                    params.setMargins(0, 40, 0, 0);
                    ivWatermark.setLayoutParams(params);
                } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    Logger.d("sbdbhdbsh " + "enter2");
                    if (playerControlsFragment != null) {
                        playerControlsFragment.sendPortraitCallback();
                    }
                    bottomMargin = (int) getResources().getDimension(R.dimen.caption_margin);
                    mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    currentConfig = newConfig;
                    baseVideoView.setPadding(0, 0, 0, 0);
                    baseVideoView.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.CENTER));
                    ivWatermark.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.CENTER));
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    boolean exitFromPIP = false;

    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);


        this.isInPictureinPicture = isInPictureInPictureMode;
        Logger.w("windowFocusChanged=2 " + isInPictureInPictureMode);
        if (isInPictureInPictureMode) {
            exitFromPIP = true;
            if (playerControlsFragment != null) {
                mListener.isInPip(true);
                playerControlsFragment.removeTimer();
                playerControlsFragment.hideControls();
                playerControlsFragment.hideBingeWatch();
                playerControlsFragment.isInPip(true);

            }
        } else {
            if (playerControlsFragment != null) {
                final Handler handler = new Handler();
                mListener.isInPip(false);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Logger.w("windowFocusChanged=4 " + isInPictureInPictureMode);
                        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        baseVideoView.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER));

                        //Do something after 100ms

                       /* Video video=baseVideoView.getCurrentVideo();
                        Logger.w("currentVideo",video+"");*/
                        if (!adRunning) {
                            Logger.w("conditionCheck " + willBingWatchShow + " " + isContentCompleted + " " + adRunning);
                            adRunning = false;
                            if (isContentCompleted) {
                                playerControlsFragment.sendVideoCompletedState(EventType.COMPLETED);
                            } else {
                                if (willBingWatchShow) {
                                    playerControlsFragment.showBingeWatch(baseVideoView.getDuration() - baseVideoView.getCurrentPosition(), isFirstCalled);
                                } else {
                                    playerControlsFragment.sendTapCallBack(true);
                                    playerControlsFragment.startHandler();
                                    playerControlsFragment.showControls();
                                }
                            }

                        }
                        playerControlsFragment.isInPip(false);
                    }
                }, 500);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitFromPIP = false;
                    }
                }, 1000);


            }
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void BackPressClicked(int value) {
        checkBackButtonOrientation(value);
    }

    private void checkBackButtonOrientation(int value) {
        FrameLayout.LayoutParams captionParams = (FrameLayout.LayoutParams) container.getLayoutParams();
        captionParams.bottomMargin = (int) 0;
        captionParams.topMargin = (int) 0;
        container.setLayoutParams(captionParams);
        if (value == 2) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            if (baseVideoView != null) {
                baseVideoView.stopPlayback();
                if (mListener != null) {
                    mListener.finishActivity();
                } else {
                    mActivity.finish();
                }
            }
        }
    }

    public void hideControls() {
        if (playerControlsFragment != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                PictureInPictureManager.getInstance().enterPictureInPictureMode();

                //playerControlsFragment.hideControls();
                // playerControlsFragment.sendTapCallBack(false);
                //  playerControlsFragment.hideControls();

            }
            Logger.d("Progress playerProgress");

        }
    }

    @Override
    public void onChromeCastConnecting() {
        if (baseVideoView != null) {
            baseVideoView.pause();
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onChromeCastConnected() {
        if (getActivity() != null) {
            progressBar.setVisibility(View.GONE);
            chromeCastStartedListener.chromeCastViewConnected(true);
        }
    }

    @Override
    public void onChromeCastDisconnected() {
        if (chromeCastStartedListener != null)
            chromeCastStartedListener.chromeCastViewConnected(false);
        isCastConnected = false;
    }

    @Override
    public void onVideoEnded() {

    }

    @Override
    public void onStatusUpdate() {
//        if (chromeCastStartedListener != null)
//            chromeCastStartedListener.chromeCastViewConnected(true);
    }

    public void updateEpisodesList(String seasonEpisodes) {

    }

    public void updateLanguage(String language) {
        applicationLanguage = language;
    }

    int totalEpisodes = 0;

    public void totalEpisodes(int size) {
        totalEpisodes = size;
    }

    int runningEpisodes = 0;

    public void currentEpisodes(int i) {
        runningEpisodes = i;
        Logger.w("totalZies " + totalEpisodes + " " + runningEpisodes);
        if (bingeWatch) {
            if (runningEpisodes < totalEpisodes) {
                bingeWatch = true;
            } else {
                bingeWatch = false;
            }
        }
        updateNextPreviousVisibility();
    }

    @Override
    public void updateNextPreviousVisibility() {
        Logger.d(Logger.getTag() + " running Episodes: " + runningEpisodes + " | totalEpisodes: " + totalEpisodes);
        if (totalEpisodes == 1) {
            playerControlsFragment.showPlayPrevious(false);
            playerControlsFragment.showPlayNext(false);
            Logger.d("1");
        } else if (runningEpisodes == 1 && totalEpisodes > runningEpisodes) {
            playerControlsFragment.showPlayPrevious(false);
            playerControlsFragment.showPlayNext(true);
            Logger.d("2");
        } else if (runningEpisodes > 1 && totalEpisodes > runningEpisodes) {
            playerControlsFragment.showPlayPrevious(true);
            playerControlsFragment.showPlayNext(true);
            Logger.d("3");
        } else if (runningEpisodes > 1 && totalEpisodes == runningEpisodes) {
            playerControlsFragment.showPlayPrevious(true);
            playerControlsFragment.showPlayNext(false);
            Logger.d("4");
        }
    }

    public void bingeWatchStatus(boolean b) {
        bingeWatch = b;
    }

    public String getRunningAssetId() {
        return brightcoveAccountId;
    }


    public interface OnPlayerInteractionListener {

        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void onPlayerError(String error);

        void onBookmarkCall(int currentPosition);

        void onBookmarkFinish();

        void onPlayerStart();

        void onPlayerInProgress();

        void onAdStarted();


        default void isInPip(boolean status) {

        }

        default void bingeWatchCall(String brightcoveId) {

        }

        default String nextVideoId() {
            return null;
        }

        default String previousVideoId() {
            return null;
        }

        default void onEpisodeSkip() {}

        void finishActivity();

        void notifyMoEngageOnPlayerStart();

        void notifyMoEngageOnPlayerEnd();
    }

    public interface ChromeCastStartedCallBack {
        void chromeCastViewConnected(boolean status);
    }


    AdDisplayContainer containers;
    boolean adStarted = false;
    boolean adRunning = false;

    private void setupGoogleIMA() {

        // Defer adding cue points until the set video event is triggered.
        eventEmitter.on(EventType.DID_SET_SOURCE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Logger.w("IMATAG " + (Source) event.properties.get(Event.SOURCE) + "");
                Logger.w("IMATAG " + event.properties + "");
                // setupCuePoints((Source) event.properties.get(Event.SOURCE),0);
            }
        });

        // Establish the Google IMA SDK factory instance.
        final ImaSdkFactory sdkFactory = ImaSdkFactory.getInstance();

        // Enable logging of ad starts
        eventEmitter.on(EventType.AD_STARTED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Logger.w("IMATAG AD_STARTED");
                if (playerControlsFragment != null) {
                    progressBar.setVisibility(View.GONE);
                    adStarted = true;
                    adRunning = true;
                    if (mListener != null) {
                        mListener.onAdStarted();
                    }

                    if (currentConfig != null && currentConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        baseVideoView.setPadding(0, 15, 10, 15);
                    }
                    if (playerControlsFragment != null) {
                        playerControlsFragment.sendTapCallBack(false);
                        playerControlsFragment.removeTimer();
                        playerControlsFragment.hideControls();
                    }
                }
            }
        });

        eventEmitter.on(EventType.AD_ERROR, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Logger.w("IMATAG AD_ERROR");
                if (playerControlsFragment != null) {
                    progressBar.setVisibility(View.GONE);
                    adRunning = false;
                    playerControlsFragment.showControls();
                }
            }
        });

        // Enable logging of any failed attempts to play an ad.
        eventEmitter.on(GoogleIMAEventType.DID_FAIL_TO_PLAY_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Logger.w("IMATAG DID_FAIL_TO_PLAY_AD");
//                 playerControlsFragment.showControls();
            }
        });

        // Enable logging of ad completions.
        eventEmitter.on(EventType.AD_COMPLETED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Logger.w("IMATAG AD_COMPLETED");
                adRunning = false;
//                 playerControlsFragment.showControls();
            }
        });

        // Set up a listener for initializing AdsRequests. The Google IMA plugin emits an ad
        // request event in response to each cue point event.  The event processor (handler)
        // illustrates how to play ads back to back.
        eventEmitter.on(GoogleIMAEventType.ADS_REQUEST_FOR_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Logger.w("IMATAG " + event.properties + "");
                Logger.w("IMATAG ADS_REQUEST_FOR_VIDEO");
                progressBar.setVisibility(View.VISIBLE);
                if (playerControlsFragment != null) {
                    playerControlsFragment.removeTimer();
                    playerControlsFragment.hideControls();

                }
                /*containers = sdkFactory.createAdDisplayContainer();
                containers.setPlayer(googleIMAComponent.getVideoAdPlayer());
                containers.setAdContainer(baseVideoView);*/

                ImaSdkSettings imaSdkSettings = sdkFactory.createImaSdkSettings();

                containers = sdkFactory.createAdDisplayContainer(baseVideoView, googleIMAComponent.getVideoAdPlayer());
                adsLoader = sdkFactory.createAdsLoader(getActivity(), imaSdkSettings, containers);
                AdsRequest adsRequest = sdkFactory.createAdsRequest();

                adsRequest.setAdTagUrl(adRulesURL);
                // adsRequest.setAdDisplayContainer(containers);
                ArrayList<AdsRequest> adsRequests = new ArrayList<AdsRequest>(1);
                adsRequests.add(adsRequest);
                event.properties.put(GoogleIMAComponent.ADS_REQUESTS, adsRequests);
                eventEmitter.respond(event);
            }
        });

        // Create the Brightcove IMA Plugin and register the event emitter so that the plugin
        // can deal with video events.
        googleIMAComponent = new GoogleIMAComponent(baseVideoView, eventEmitter, true);
    }

    private void setupCuePoints(Source source, int type) {
        String cuePointType = "ad";
        Map<String, Object> properties = new HashMap<String, Object>();
        Map<String, Object> details = new HashMap<String, Object>();


        try {
            CuePoint cuePoint = new CuePoint(CuePoint.PositionType.POINT_IN_TIME, cuePointType, properties);
            details.put(Event.CUE_POINT, cuePoint);
            eventEmitter.emit(EventType.SET_CUE_POINT, details);
        } catch (Exception e) {
            Logger.w(e);
        }

        // preroll
        CuePoint cuePoint = new CuePoint(CuePoint.PositionType.BEFORE, cuePointType, properties);
        details.put(Event.CUE_POINT, cuePoint);
        eventEmitter.emit(EventType.SET_CUE_POINT, details);

//        // postroll
        cuePoint = new CuePoint(CuePoint.PositionType.AFTER, cuePointType, properties);
        details.put(Event.CUE_POINT, cuePoint);
        eventEmitter.emit(EventType.SET_CUE_POINT, details);
    }

    /**
     * Provide a sample illustrative ad.
     */
    private String[] googleAds = {
            // Honda Pilot
            "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=[timestamp]"
    };

    private void createCuePointList(Video video) {
        if (video.getCuePoints().size() > 0) {
            // new CuePointPlugin(baseVideoView.getEventEmitter(), getActivity(), baseVideoView);
            setUpDataCuePoint();
            for (CuePoint cuePoint : video.getCuePoints()) {
                cuePointsList.add(cuePoint);
            }

        }
    }


    int skipIntroPosition = 0;
    int skipHideTime = 0;
    String titleOnButton = "";

    private void setUpDataCuePoint() {

        eventEmitter.on(EventType.DID_SET_SOURCE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                setupCuePoints((Source) event.properties.get(Event.SOURCE), 1);
            }
        });

        eventEmitter.on(EventType.CUE_POINT, new EventListener() {
            @Override
            public void processEvent(Event event) {

                int startTime = (int) event.getProperties().get("startTime");
                if (startTime > 0) {
                    if (cuePointsList.size() > 0) {
                        int value;
                        for (int i = 0; i < cuePointsList.size(); i++) {
                            if (baseVideoView.getCurrentPosition() > cuePointsList.get(i).getPosition()) {
                                value = baseVideoView.getCurrentPosition() - cuePointsList.get(i).getPosition();
                            } else {
                                value = cuePointsList.get(i).getPosition() - baseVideoView.getCurrentPosition();
                            }

                            if (value < 1000) {
                                if (cuePointsList.get(i).getProperties().get(Constants.META_DATA).toString().contains(Constants.SKIPINTRO)) {

                                    skipIntroPosition = Utils.getSkipPosition(cuePointsList.get(i).getProperties().get(Constants.META_DATA).toString());
                                    skipHideTime = Utils.getSkipHideTime(cuePointsList.get(i).getProperties().get(Constants.META_DATA).toString());
                                    titleOnButton = Utils.getSkipBtnText(cuePointsList.get(i).getProperties().get(Constants.META_DATA).toString());
                                    int currentPosition = baseVideoView.getCurrentPosition();
                                    skipHideTime = skipHideTime + currentPosition;

                                    if (playerControlsFragment != null) {
                                        playerControlsFragment.skipIntro(titleOnButton);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });


    }

    @Override
    public void changeBitRateRequest(String title, int position) {
        selected_track = title;
        VideoDisplayComponent videoDisplayComponent = baseVideoView.getVideoDisplay();
        if (videoDisplayComponent instanceof ExoPlayerVideoDisplayComponent) {
            Logger.d("SelectedTrackName -->> " + title + "  " + position);
            if (title.equalsIgnoreCase(getActivity().getResources().getString(R.string.auto)) || title.equalsIgnoreCase(Constants.AUTO)) {
                Logger.d("SelectedTrackName -->> " + title + "  " + Constants.AUTO + "  " + trackSelector);
                updateVideoTrack(position);

            } else {
                updateVideoTrack(position);
                // ((ExoPlayerVideoDisplayComponent) baseVideoView.getVideoDisplay()).setPeakBitrate(videoTrackArray.get(position).bitrate);

            }

        }

    }

    private void updateVideoTrack(int position) {
        try {
            VideoDisplayComponent component = baseVideoView.getVideoDisplay();
            if (component instanceof ExoPlayerVideoDisplayComponent) {
                ExoPlayerVideoDisplayComponent exoComponent = (ExoPlayerVideoDisplayComponent) component;

                ExoPlayer exoPlayer = exoComponent.getExoPlayer();
                MappingTrackSelector mappingTrackSelector = exoComponent.getTrackSelector();
                if (mappingTrackSelector instanceof DefaultTrackSelector) {
                    trackSelector = (DefaultTrackSelector) mappingTrackSelector;

                    mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                    if (mappedTrackInfo != null) {
                        if (mappedTrackInfo.length > 0) {
                            trackGroups = mappedTrackInfo.getTrackGroups(0);
                            DefaultTrackSelector.Parameters pp = trackSelector.getParameters();
                            trackSelectedPosition = position;
                            DefaultTrackSelector.SelectionOverride selectiveBitRateOverride = null;
                            if (position > 0) {
                                Logger.d("selectedPosition " + position + "  " + videoTrackArray.get(position - 1).height + "  " + videoTrackArray.get(position - 1).bitrate);
                                Logger.w(videoTrackArray.get(position - 1).bitrate + "");
                                selectiveBitRateOverride = new DefaultTrackSelector.SelectionOverride(0, position - 1);

                            } else {
                                int[] trackIndices = new int[trackGroups.get(0).length];

                                for (int idx = 0; idx < trackGroups.get(0).length; idx++) {
                                    trackIndices[idx] = idx;
                                }
                                selectiveBitRateOverride = new DefaultTrackSelector.SelectionOverride(0, trackIndices);
                            }

                            trackSelector.setParameters(trackSelector.buildUponParameters()
                                    .setSelectionOverride(0, trackGroups, selectiveBitRateOverride));

                        }

                    }

                }
            }
        } catch (Exception ignored) {

        }

    }

    @Override
    public void bitRateRequest() {
        if (selected_lang.equalsIgnoreCase("Thai")) {
            Utils.updateLanguage("th", getActivity());
        } else if (selected_lang.equalsIgnoreCase("English")) {
            Utils.updateLanguage("en", getActivity());
        }
        gettingVideoTracks("");
    }

    @Override
    public void playPrevious() {
        String previousId = mListener.previousVideoId();
        Logger.d("playPrevious: " + previousId);
        if (previousId != null) {
            skipEpisodeTo(previousId);
        }
    }

    @Override
    public void playNext() {
        String nextId = mListener.nextVideoId();
        Logger.d("playNext: " + nextId);
        if (nextId != null) {
            skipEpisodeTo(nextId);
        }
    }

    private void skipEpisodeTo(@Nullable String videoId) {
        if (videoId != null) {
            baseVideoView.stopPlayback();
            new Handler(Looper.myLooper()).postDelayed(() -> mListener.onEpisodeSkip(), 200);
        }
    }

    @Override
    public void playSignVideo(boolean isSignPlaying, String signLangId, boolean isFromParentRef) {
        if (isFromParentRef) {
            if (isSignPlaying) {
                startPlayer(videoId);
            } else {
                startPlayer(signLangId);
            }
        } else {
            if (isSignPlaying) {
                startPlayer(signLangId);
            } else {
                startPlayer(videoId);
            }
        }
    }

    private void startPlayer(String videoId) {
        if (baseVideoView != null) {
            bookmarkPosition = baseVideoView.getCurrentPosition();
            baseVideoView.stopPlayback();
            baseVideoView.clear();
            //setPlayerWithCallBacks(false,signLangId);

            progressBar.setVisibility(View.VISIBLE);
            Catalog catalog = new Catalog(eventEmitter, brightcoveAccountId, brightcovePolicyKey);
            catalog.findVideoByID(videoId, new VideoListener() {
                @Override
                public void onVideo(Video video) {
                    Logger.w("IMATAG catalogin");
                    progressBar.setVisibility(View.VISIBLE);
                    currentVideo = video;
                    cuePointsList = new ArrayList<>();
                    createCuePointList(video);
                    baseVideoView.add(video);
                    isContentCompleted = false;
                    willBingWatchShow = false;
                    baseVideoView.start();
                    baseVideoView.seekTo((int) (bookmarkPosition));
                    // callPlayerControlsFragment();
                    /*if (playerControlsFragment != null)
                        playerControlsFragment.sendTapCallBack(false);*/
                    if (playerControlsFragment != null) {
                        playerControlsFragment.sendTapCallBack(false);
                        playerControlsFragment.setIsOffline(false, from);

                    }


                    Logger.w("videoFound inUp");
                    /*if (ChromecastManager.getInstance().getRunningVideoId()!=null && !ChromecastManager.getInstance().getRunningVideoId().equalsIgnoreCase("")){
                        if (ChromecastManager.getInstance().getRunningVideoId().equalsIgnoreCase(videoId)){
                            onStatusUpdate();
                        }else {
                            castEnabling(video);
                        }
                    }else {
                        castEnabling(video);
                    }*/
                    castEnabling(video);
                }
            });
        }
    }

    private void getSelectedTrack() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int position = Utils.getSelectedTrackPosition(selected_track, getActivity());
                Logger.w("selectedPositionNew " + position + " " + selected_track);

                getVideoTracks();
                if (videoTrackArray != null) {
                    ArrayList<TrackItem> arrayList = Utils.createTrackList(videoTrackArray, getActivity(), is4kSupported);
                    if (position > 0) {
                        if (position < arrayList.size()) {
                            updateVideoTrack(position);
                        } else {
                            selected_track = getActivity().getResources().getString(R.string.auto);
                            // Toast.makeText(getActivity(), "Streaming setting not available", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }
        }, 3000);


    }

    private void getVideoTracks() {
        try {
            if (videoTrackArray != null || videoTrackArray == null) {
                videoTrackArray = new ArrayList<>();
                VideoDisplayComponent component = baseVideoView.getVideoDisplay();
                if (component instanceof ExoPlayerVideoDisplayComponent) {
                    ExoPlayerVideoDisplayComponent exoComponent = (ExoPlayerVideoDisplayComponent) component;

                    ExoPlayer exoPlayer = exoComponent.getExoPlayer();
                    MappingTrackSelector mappingTrackSelector = exoComponent.getTrackSelector();
                    if (mappingTrackSelector instanceof DefaultTrackSelector) {
                        trackSelector = (DefaultTrackSelector) mappingTrackSelector;

                        mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                        if (mappedTrackInfo != null) {
                            if (mappedTrackInfo.length > 0) {
                                trackGroups = mappedTrackInfo.getTrackGroups(0);

                                for (int i = 0; i < trackGroups.length; i++) {
                                    group = trackGroups.get(i);

                                    for (int j = 0; j < trackGroups.get(i).length; j++) {
                                        Logger.d("selectedPosition " + trackGroups.get(i).getFormat(j).height + " " + trackGroups.get(i).getFormat(j).width + "  " + trackGroups.get(i).getFormat(j).bitrate);
                                        videoTrackArray.add(trackGroups.get(i).getFormat(j));
                                    }
                                }


                            }

                        }


                    }
                }
            }

        } catch (Exception ignored) {

        }

    }

    TrackGroup group;
    int position = 1;
    int trackSelectedPosition = 0;
    List<Format> videoTrackArray;

    private void gettingVideoTracks(String msg) {

        if (videoTrackArray != null || videoTrackArray == null) {
            videoTrackArray = new ArrayList<>();
            Logger.d("DID_SET_VIDEO " + msg);
            VideoDisplayComponent component = baseVideoView.getVideoDisplay();
            if (component instanceof ExoPlayerVideoDisplayComponent) {
                ExoPlayerVideoDisplayComponent exoComponent = (ExoPlayerVideoDisplayComponent) component;

                ExoPlayer exoPlayer = exoComponent.getExoPlayer();
                MappingTrackSelector mappingTrackSelector = exoComponent.getTrackSelector();
                if (mappingTrackSelector instanceof DefaultTrackSelector) {
                    trackSelector = (DefaultTrackSelector) mappingTrackSelector;

                    mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();

                    if (mappedTrackInfo != null) {
                        if (mappedTrackInfo.length > 0) {
                            trackGroups = mappedTrackInfo.getTrackGroups(0);

                            for (int i = 0; i < trackGroups.length; i++) {
                                group = trackGroups.get(i);

                                for (int j = 0; j < trackGroups.get(i).length; j++) {
                                    Logger.d("selectedPosition " + trackGroups.get(i).getFormat(j).height + " " + trackGroups.get(i).getFormat(j).width + "  " + trackGroups.get(i).getFormat(j).bitrate);
                                    videoTrackArray.add(trackGroups.get(i).getFormat(j));
                                }
                            }
                        /*270P - 360P = Low
                        540P - 720P = Medium
                        1080P = High*/
                            DefaultTrackSelector.Parameters pp = trackSelector.getParameters();
                            trackSelectedPosition = position;
                            if (playerControlsFragment != null) {
                                playerControlsFragment.setVideoFormate(videoTrackArray, selected_track, selected_lang,isDeviceSupported);
                            }
                        }

                    }


                }
            }
        }


    }
}