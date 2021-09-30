package me.vipa.brightcovelibrary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.mediarouter.app.MediaRouteButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brightcove.player.event.EventType;

import me.vipa.brightcovelibrary.callBacks.PlayerCallbacks;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.gson.Gson;
import com.vipa.brightcovelibrary.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayerControlsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerControlsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerControlsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String ARG_PARAM1 = "param1";
    private final String ARG_PARAM2 = "param2";
    private ImageView pauseButton, forward, rewind, playerSettingIcon, signIcon;
    private MediaRouteButton media_route_button;
    private LinearLayout skipBtn, bingeBtn;
    private TextView skipduration;
    ConstraintLayout bingeLay;
    private FrameLayout replay;
    private TextView currentPosition;
    private TextView totalDuration, skipTxt;
    private DefaultTimeBar seekBar;
    private ProgressBar progressBar;
    private boolean mFlag = false;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private PlayerCallbacks playerCallbacks;
    private ConstraintLayout controlLayout;
    private ConstraintLayout childControl, progressView;
    private RelativeLayout audioTracks, subtitles;
    public ImageView backArrow;
    private View seekBarControl, settingControl;
    public ImageView fullscreen;
    private TextView liveTag;
    private long playbackDuration, playbackCurrentPosition;
    private String playerState = "";
    private Activity activity;
    private Runnable viewHideShowRunnable;
    private Handler viewHideShowTimeHandler;
    private boolean timer = true;
    private boolean tapControls = false;
    private String videoType = "1";
    private boolean isPipEnabled = false;
    private boolean isOffline = false;
    private LinearLayout settingLay;
    private CountDownTimer mTimer;
    private boolean isSignPlaying = false;
    private boolean isFromParentRef = false;
    private String signLangId = "";

    // private OnSizeRatioDown onSizeRatioDown;


    private OnFragmentInteractionListener mListener;
    private int aspectRatio;
//    private SeekBarPreview seekBarPreviewSDCard;

    public PlayerControlsFragment() {
        // Required empty public constructor
    }


    public PlayerControlsFragment newInstance(String param1, String param2) {
        PlayerControlsFragment fragment = new PlayerControlsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void startHandler() {
        callHandler();

    }

    void setTotalDuration(int duration) {
        totalDuration.setText(Utils.stringForTime(duration));
        seekBar.setDuration(duration);
        playbackDuration = duration;
    }

    void setCurrentPosition(int currentposition) {
        currentPosition.setText(Utils.stringForTime(currentposition));
        updateSeekbar(currentposition);
        playbackCurrentPosition = currentposition;
        controlRewindAndForwardImageVisibility(playbackCurrentPosition, playbackDuration);
    }

    private void controlRewindAndForwardImageVisibility(long playbackCurrentPosition, long playbackDuration) {
        if (playbackCurrentPosition > 10000) {
            rewind.setVisibility(View.VISIBLE);
        } else {
            rewind.setVisibility(View.GONE);
        }

        long runningTime = playbackDuration - playbackCurrentPosition;
        if (videoType.equalsIgnoreCase("1")) {
            hideControlsForLive();
        } else {
            if (runningTime > 10000) {
                forward.setVisibility(View.VISIBLE);
            } else {
                forward.setVisibility(View.GONE);
            }
        }
    }


    private void updateSeekbar(int currentposition) {

        seekBar.setPosition(currentposition);

    }

    void SendPlayerPauseState(String type) {
        if (type == "pause" && pauseButton != null) {
            pauseButton.setBackgroundResource(R.drawable.play);
        }
    }

    void sendPlayerPlayState(String type) {
        if (type == "play" && pauseButton != null) {
            pauseButton.setBackgroundResource(R.drawable.pause);
        }
    }

    void sendPlayerCurrentPosition(int playerCurrentPosition) {
        seekBar.setPosition(playerCurrentPosition);
        if (playerCurrentPosition > playbackDuration) {
            currentPosition.setText(Utils.stringForTime(playbackDuration));
            if (videoType.equalsIgnoreCase("1")) {
                hideControlsForLive();
            } else {
                forward.setVisibility(View.GONE);
                rewind.setVisibility(View.VISIBLE);
            }
        } else if (playerCurrentPosition <= 0) {
            currentPosition.setText(Utils.stringForTime(0));
            if (videoType.equalsIgnoreCase("1")) {
                hideControlsForLive();
            } else {
                rewind.setVisibility(View.GONE);
                forward.setVisibility(View.VISIBLE);
            }
        } else {
            currentPosition.setText(Utils.stringForTime(playerCurrentPosition));
        }


    }


    boolean isCaptionAvailable = false;

    void sendCaptionAvailable(String type) {
        if (type.equalsIgnoreCase(EventType.CAPTIONS_LANGUAGES) || type.equalsIgnoreCase(EventType.CAPTIONS_AVAILABLE)) {
            isCaptionAvailable = true;
            if (subtitles.getVisibility() == View.VISIBLE) {

            } else {
                subtitles.setVisibility(View.GONE);
            }

            Log.w("captionHide", "sendCaptionAvailableif");
        } else {
            isCaptionAvailable = false;
            subtitles.setVisibility(View.GONE);
            Log.w("captionHide", "sendCaptionAvailableelse");
        }

    }

    boolean isAudioAvailable = false;

    void sendAudioAvailable(String type) {
        if (type.equalsIgnoreCase(EventType.AUDIO_TRACKS)) {
            isAudioAvailable = true;
            audioTracks.setVisibility(View.GONE);
        } else {
            isAudioAvailable = false;
            audioTracks.setVisibility(View.GONE);
        }
    }

    void sendPortraitCallback() {
        try {
            subtitles.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (videoType.equalsIgnoreCase("1")) {
            if (playerSettingIcon != null) {
                playerSettingIcon.setVisibility(View.INVISIBLE);
            }
        } else {
            if (playerSettingIcon != null) {
                playerSettingIcon.setVisibility(View.VISIBLE);
            }
        }

        // media_route_button.setVisibility(View.VISIBLE);
        audioTracks.setVisibility(View.GONE);
        fullscreen.setBackgroundResource(R.drawable.full_screen);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        seekBarControl.setLayoutParams(params);
        settingControl.setLayoutParams(params);

        // private RelativeLayout rlbackArrow,rlMedia,rlplayerSettingIcon;


        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params1.setMargins(0, 0, 0, 0);
        backArrow.setLayoutParams(params1);
        backArrow.requestFocus();


        try {
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params2.setMargins(0, 10, 60, 0);
            params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            settingLay.setLayoutParams(params2);
        } catch (Exception ignored) {

        }

        Utils.setParamsResetSkipButton(skipBtn);

        //  Utils.setParamstoSettinIcon(playerSettingIcon);


    }

    void sendLandscapeCallback() {
        try {
            fullscreen.setBackgroundResource(R.drawable.exit_full_screen);
            if (videoType.equalsIgnoreCase("1")) {
                playerSettingIcon.setVisibility(View.INVISIBLE);
            } else {
                playerSettingIcon.setVisibility(View.VISIBLE);
            }
//            media_route_button.setVsibility(View.VISIBLE);
            if (isCaptionAvailable) {
                Log.w("captionHide", "sendLandscapeCallbackif");
                subtitles.setVisibility(View.VISIBLE);
            } else {
                subtitles.setVisibility(View.GONE);
                Log.w("captionHide", "sendLandscapeCallbackelse");
            }
            if (isAudioAvailable) {
                audioTracks.setVisibility(View.VISIBLE);
            } else {
                audioTracks.setVisibility(View.GONE);
            }
            if (!getResources().getBoolean(R.bool.isTablet)) {


              /*  DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screen_height = displayMetrics.heightPixels;
                int screen_width = displayMetrics.widthPixels;*/

                // Log.e("RATUILANDHEIGHT...", String.valueOf(screen_height));
                //  Log.e("RATUILANDWIDTH...", String.valueOf(screen_width));

             /*   if (screen_width > 1280) {
                    Utils.setParamstoSeekBarControlRatio(seekBarControl);
                    Utils.setParamstoPlayerSettingControl(settingControl);
                    Utils.setParamstoBackArrowForRatio(backArrow);
                    Utils.setParamstoSettinIconRatio(settingLay);
                    Utils.setParamstoSkipButton(skipBtn);
                }
                else {
                    Utils.setParamstoSeekBarControl(seekBarControl);
                    Utils.setParamstoPlayerSettingControl(settingControl);
                    Utils.setParamstoBackArrow(backArrow);
                    Utils.setParamstoSettinIcon(settingLay);
                    Utils.setParamstoSkipButton(skipBtn);

                }*/
                if (videoType.equalsIgnoreCase("1")){
                    Utils.setParamstoSeekBarControl1(seekBarControl);
                    Utils.setParamstoPlayerSettingControl(settingControl);
                    Utils.setParamstoBackArrow(backArrow);
                    Utils.setParamstoSettinIcon(settingLay);
                    Utils.setParamstoSkipButton(skipBtn);
                }else {
                    Utils.setParamstoSeekBarControl(seekBarControl);
                    Utils.setParamstoPlayerSettingControl(settingControl);
                    Utils.setParamstoBackArrow(backArrow);
                    Utils.setParamstoSettinIcon(settingLay);
                    Utils.setParamstoSkipButton(skipBtn);
                }


            }


        } catch (Exception e) {

        }

    }

    void showControls() {
        Log.w("CONTROLSVIDEO", videoType);
        childControl.setVisibility(View.VISIBLE);
        backArrow.setVisibility(View.VISIBLE);
        if (videoType.equalsIgnoreCase("1")) {
            hideControlsForLive();
        } else {

        }
    }

    void hideControlsForLive() {
        replay.setVisibility(View.GONE);
        forward.setVisibility(View.GONE);
        rewind.setVisibility(View.GONE);
        currentPosition.setVisibility(View.INVISIBLE);
        totalDuration.setVisibility(View.INVISIBLE);
        seekBar.setVisibility(View.INVISIBLE);
        audioTracks.setVisibility(View.GONE);
        subtitles.setVisibility(View.GONE);
        pauseButton.setVisibility(View.GONE);
        Log.w("captionHide", "hideControlsForLive");
        settingControl.setVisibility(View.GONE);
        liveTag.setVisibility(View.VISIBLE);
        playerSettingIcon.setVisibility(View.INVISIBLE);
    }

    void hideControls() {
        Log.w("IMATAG", "hideControls");
        try {
            childControl.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (backArrow != null) {
            backArrow.setVisibility(View.GONE);
        }
    }

    void setFragmenmtVisibility() {
        Log.w("IMATAG", "setFragmenmtVisibility");
        //childControl.setVisibility(View.VISIBLE);
        if (videoType.equalsIgnoreCase("1")) {
            hideControlsForLive();
        } else {

        }

    }

    public void removeTimer() {
        Log.w("IMATAG", "removeTimer1");
        if (timer) {
            Log.w("IMATAG", "removeTimer2" + " " + timer);
            if (viewHideShowTimeHandler != null) {
                Log.w("IMATAG", "removeTimer3" + " " + viewHideShowTimeHandler + " " + timer);
                viewHideShowTimeHandler.removeCallbacks(viewHideShowRunnable);
            }
        }
    }

    void sendVideoCompletedState(String type) {
        if (timer) {
            viewHideShowTimeHandler.removeCallbacks(viewHideShowRunnable);
        }
        if (type.equalsIgnoreCase(EventType.COMPLETED)) {
            backArrow.setVisibility(View.VISIBLE);
            if (videoType.equalsIgnoreCase("1")) {
                replay.setVisibility(View.GONE);
            } else {
                replay.setVisibility(View.VISIBLE);
            }
        }
    }


    void sendTapCallBack(boolean b) {
        mFlag = b;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_player_controls, container, false);
        findId(view);
        performClick();
        return view;
    }

    private void performClick() {
        //Play pause control for player
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerCallbacks != null) {
                    playerCallbacks.playPause();
                }
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerCallbacks != null) {
                    playerCallbacks.skipIntro();
                }
            }
        });

        bingeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerCallbacks != null) {
                    playerCallbacks.bingeWatch();
                }
            }
        });

        bingeLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Seek player for 10 seconds from currentPosition
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerCallbacks != null) {
                    playerCallbacks.Forward();
                }
            }
        });
        //Rewind player for 10 seconds from currentPosition
        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerCallbacks != null) {
                    playerCallbacks.Rewind();
                }
            }
        });

        //Subtitles view click
        subtitles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerCallbacks != null) {
                    playerCallbacks.subtitlePopup();
                }
            }
        });

        //Audio track popup
        audioTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerCallbacks != null) {
                    playerCallbacks.audioTrackPopup();
                }
            }
        });

        //Back button click
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerCallbacks != null) {
                    playerCallbacks.finishPlayer();
                }
                playerCallbacks.checkOrientation(backArrow);
            }
        });

        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerCallbacks != null) {
                    playerCallbacks.checkOrientation(fullscreen);
                }
            }
        });

        //Replay video event
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerCallbacks != null) {
                    replay.setVisibility(View.GONE);
                    seekBar.setPosition(0);
                    playerCallbacks.replay();
                }
            }
        });

        //Seekbar callbacks
        seekBar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(TimeBar timeBar, long position) {

            }

            @Override
            public void onScrubMove(TimeBar timeBar, long position) {
                currentPosition.setText(Utils.stringForTime(position));
                hideSkipIntro();
            }

            @Override
            public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                if (playerCallbacks != null) {
                    seekBar.setPosition(position);
                    playerCallbacks.SeekbarLastPosition(position);
                }
            }
        });

        controlLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //  Toast.makeText(getActivity(),"playerViewClicked",Toast.LENGTH_LONG).show();

                if (!mFlag)
                    return false;
                if (replay.getVisibility() == View.VISIBLE) {
                    childControl.setVisibility(View.GONE);
                } else {
                    callAnimation();
                }

                return false;
            }
        });


        playerSettingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseVideoquality();
                //showSettingOptions();
            }
        });

        signIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSignPlaying) {
                    isSignPlaying = true;
                    signIcon.setImageBitmap(null);
                    signIcon.setBackgroundResource(R.drawable.ic_menu_green_sl);
                    playerCallbacks.playSignVideo(isSignPlaying, signLangId, isFromParentRef);
                } else {
                    isSignPlaying = false;
                    signIcon.setImageBitmap(null);
                    signIcon.setBackgroundResource(R.drawable.ic_sl_logo_black);
                    playerCallbacks.playSignVideo(isSignPlaying, signLangId, isFromParentRef);
                }

            }
        });


    }

    private void callAnimation() {
        if (timer) {
            viewHideShowTimeHandler.removeCallbacks(viewHideShowRunnable);
        }
        ShowAndHideView();
    }


    private void ShowAndHideView() {
        try {

            Animation animationFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
            Animation animationFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);

            animationFadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (playerCallbacks != null)
                        playerCallbacks.showPlayerController(true);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            animationFadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (playerCallbacks != null)
                        playerCallbacks.showPlayerController(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if (!isPipEnabled) {
                if (childControl.getVisibility() == View.VISIBLE) {
                    childControl.startAnimation(animationFadeOut);
                    backArrow.setVisibility(View.GONE);
                    childControl.setVisibility(View.GONE);


                    timer = true;


                } else {

                    Log.w("IMATAG", "handler");
                    childControl.setVisibility(View.VISIBLE);
                    backArrow.setVisibility(View.VISIBLE);
                    if (videoType.equalsIgnoreCase("1")) {
                        hideControlsForLive();
                    } else {

                    }
                    childControl.startAnimation(animationFadeIn);

                    callHandler();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callHandler() {
        Log.w("conditionCheck-->>", "in");
        timer = true;
        viewHideShowRunnable = () -> ShowAndHideView();

        viewHideShowTimeHandler = new Handler();
        viewHideShowTimeHandler.postDelayed(viewHideShowRunnable, 10000);

    }

    private void findId(View view) {
        //  controls =(PlayerControlView)view. findViewById(R.id.seek_bar);
        skipBtn = (LinearLayout) view.findViewById(R.id.skipBtn);
        skipduration = (TextView) view.findViewById(R.id.skip_duration);
        bingeBtn = (LinearLayout) view.findViewById(R.id.bingeBtn);
        bingeLay = (ConstraintLayout) view.findViewById(R.id.bingeLay);
        pauseButton = (ImageView) view.findViewById(R.id.pause_button);
        replay = (FrameLayout) view.findViewById(R.id.replay);
        forward = (ImageView) view.findViewById(R.id.forward);
        rewind = (ImageView) view.findViewById(R.id.rewind);
        playerSettingIcon = (ImageView) view.findViewById(R.id.playerSettingIcon);
        signIcon = (ImageView) view.findViewById(R.id.signIcon);
        media_route_button = (MediaRouteButton) view.findViewById(R.id.media_route_button);
        currentPosition = (TextView) view.findViewById(R.id.exo_position);
        totalDuration = (TextView) view.findViewById(R.id.exo_duration);
        skipTxt = (TextView) view.findViewById(R.id.skipTxt);
        seekBar = (DefaultTimeBar) view.findViewById(R.id.exo_progress);
        liveTag = (TextView) view.findViewById(R.id.tag);
        controlLayout = view.findViewById(R.id.controlslayout);
        childControl = (ConstraintLayout) view.findViewById(R.id.childControl);
        audioTracks = (RelativeLayout) view.findViewById(R.id.audio_tracks);
        subtitles = (RelativeLayout) view.findViewById(R.id.subtitle_parent_view);
        backArrow = (ImageView) view.findViewById(R.id.backArrow);
        seekBarControl = (View) view.findViewById(R.id.seekbarLayout);
        settingControl = (View) view.findViewById(R.id.playerSetting);
        fullscreen = (ImageView) view.findViewById(R.id.fullscreen);
        settingLay = (LinearLayout) view.findViewById(R.id.settingLay);
        if (isOffline && from == 1) {
            fullscreen.setVisibility(View.GONE);
            playerSettingIcon.setVisibility(View.INVISIBLE);
        } else
            fullscreen.setVisibility(View.VISIBLE);

        seekBar.setEnabled(true);

        CastButtonFactory.setUpMediaRouteButton(getActivity(), media_route_button);

        hideControls();
    }

    int from = 0;

    public void setIsOffline(boolean isOffline, int from) {
        this.isOffline = isOffline;
        this.from = from;
        if (fullscreen != null) {
            if (isOffline) {
                if (from == 1) {
                    fullscreen.setVisibility(View.GONE);
                }
            } else {
                fullscreen.setVisibility(View.VISIBLE);
            }
        }
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


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setPlayerCallBacks(PlayerCallbacks playerCallbacks) {
        this.playerCallbacks = playerCallbacks;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public void skipIntro(String titleOnButton) {
        if (childControl.getVisibility() != View.VISIBLE) {
            callAnimation();
        }

        if (skipBtn.getVisibility() == View.VISIBLE) {
            skipBtn.setVisibility(View.GONE);
        } else {
            skipBtn.setVisibility(View.VISIBLE);
        }

        if (titleOnButton.equalsIgnoreCase("")) {
            skipBtn.setVisibility(View.GONE);
        } else {
            //  skipTxt.setText(titleOnButton);
        }

    }

    public void hideSkipIntro() {
        skipBtn.setVisibility(View.GONE);
    }

    public void showBingeWatch(int position, boolean isFirstCalled) {
        try {
            removeTimer();
        } catch (Exception ignored) {

        }
        childControl.setVisibility(View.GONE);
        bingeLay.setVisibility(View.VISIBLE);
        bingeBtn.setVisibility(View.VISIBLE);
        skipduration.setVisibility(View.VISIBLE);
        backArrow.setVisibility(View.VISIBLE);
        if (isFirstCalled) {
            mTimer = new CountDownTimer(position, 1000) {
                public void onTick(long millisUntilFinished) {
                    skipduration.setText(Long.toString(millisUntilFinished / 1000));
                }

                public void onFinish() {
                    skipduration.setText("");
                }
            };

            mTimer.start();
        }
    }

    public void hideBingeWatch() {
        try {
            bingeLay.setVisibility(View.GONE);
            bingeBtn.setVisibility(View.GONE);
            backArrow.setVisibility(View.GONE);
        } catch (Exception e) {

        }

    }

    public void isInPip(boolean pipMode) {
        isPipEnabled = pipMode;
    }

    public void setIsSignEnable(String signLangParentRefId, String signLangRefId) {

        if (signLangParentRefId != null && signLangParentRefId != "") {
            signIcon.setBackgroundResource(R.drawable.ic_menu_green_sl);
            signIcon.setVisibility(View.VISIBLE);
            isSignPlaying = true;
            isFromParentRef = true;
            signLangId = signLangParentRefId;

        } else if (signLangRefId != null && signLangRefId != "") {

            signIcon.setVisibility(View.VISIBLE);
            signIcon.setBackgroundResource(R.drawable.ic_sl_logo_black);
            isSignPlaying = false;
            isFromParentRef = false;
            signLangId = signLangRefId;
        } else {
            signIcon.setVisibility(View.INVISIBLE);
        }
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    private Dialog dialogQuality;
    RecyclerView recycleview;

    private void chooseVideoquality() {
        if (playerCallbacks != null) {
            callHandler();
            dialogQuality = new Dialog(getActivity());
            dialogQuality.setContentView(R.layout.layout_dialog_settings);
            dialogQuality.setTitle("Video Quality");
            recycleview = dialogQuality.findViewById(R.id.recycleview);
            recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
            playerCallbacks.bitRateRequest();
            TextView titleView = dialogQuality.findViewById(R.id.title_video);
            titleView.setText(getString(R.string.select_video_quality));
            Button closeButton = dialogQuality.findViewById(R.id.close);
            closeButton.setText(getString(R.string.cancel));
            closeButton.setOnClickListener(v -> dialogQuality.cancel());
        }
    }

    List<Format> videoTrackArray;

    public void setVideoFormate(List<Format> array, String selectedTrack, String selectedLang) {
        if (selectedLang.equalsIgnoreCase("Thai")) {
            Utils.updateLanguage("th", getActivity());
        } else if (selectedLang.equalsIgnoreCase("English")) {
            Utils.updateLanguage("en", getActivity());
        }
        if (array.size() > 0) {
            videoTrackArray = array;
            ArrayList<TrackItem> arrayList = Utils.createTrackList(videoTrackArray, getActivity());
            VideoTracksAdapter videoTracksAdapter = new VideoTracksAdapter(arrayList, selectedTrack);
            recycleview.setAdapter(videoTracksAdapter);
            dialogQuality.show();
        } else {
//            Log.d("gtgtgtgt",selectedLang);
//            if (selectedLang.equalsIgnoreCase("Thai")) {
//                Utils.updateLanguage("th", getActivity());
//            } else if (selectedLang.equalsIgnoreCase("English")) {
//                Utils.updateLanguage("en", getActivity());
//            }
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.video_tracks_not_available), Toast.LENGTH_SHORT).show();
        }
    }

    private String selectedTrack = "Auto";

    class VideoTracksAdapter extends RecyclerView.Adapter<ViewHolder> {
        final List<TrackItem> tracks;
        private int selectedIndex = -1;

        private VideoTracksAdapter(List<TrackItem> videoTracks, String selectedTrck) {
            this.tracks = videoTracks;
            selectedTrack = selectedTrck;
            if (selectedTrack.equalsIgnoreCase(getActivity().getResources().getString(R.string.auto))) {
                selectedIndex = 0;
            } else if (selectedTrack.equalsIgnoreCase(getActivity().getResources().getString(R.string.low))) {
                selectedIndex = 1;
            } else if (selectedTrack.equalsIgnoreCase(getActivity().getResources().getString(R.string.medium))) {
                selectedIndex = 2;
            } else if (selectedTrack.equalsIgnoreCase(getActivity().getResources().getString(R.string.high))) {
                selectedIndex = 3;
            } else {
                selectedIndex = 0;
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playback_quality, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            try {

                String trackName = tracks.get(position).getTrackName() + "";
                holder.qualityText.setText(trackName);
                holder.description.setText(tracks.get(position).getTrackDescription());
                if (selectedTrack.equals("")) {
                    holder.playbackQualityRadio.setChecked(false);
                } else {
                    String compareName = tracks.get(position).getTrackName();
                    if (position == selectedIndex) {
                        selectedTrack = tracks.get(position).getTrackName();
                        holder.playbackQualityRadio.setChecked(true);
                    } else {
                        if (compareName.equalsIgnoreCase(selectedTrack)) {
                            selectedTrack = tracks.get(position).getTrackName();
                            holder.playbackQualityRadio.setChecked(true);
                        } else {
                            holder.playbackQualityRadio.setChecked(false);
                        }
                    }
                }

                holder.lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedTrack = tracks.get(position).getTrackName();
                        holder.playbackQualityRadio.setChecked(true);
                        if (playerCallbacks != null) {
                            playerCallbacks.changeBitRateRequest(selectedTrack, tracks.get(position).getBitRatePosition());
                            dialogQuality.dismiss();
                        }
                        notifyDataSetChanged();
                    }
                });

                holder.playbackQualityRadio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedTrack = tracks.get(position).getTrackName();
                        holder.playbackQualityRadio.setChecked(true);
                        if (playerCallbacks != null) {
                            playerCallbacks.changeBitRateRequest(selectedTrack, tracks.get(position).getBitRatePosition());
                            dialogQuality.dismiss();
                        }
                        notifyDataSetChanged();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return tracks.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView tick;
        private TextView qualityText, description;
        private AppCompatRadioButton playbackQualityRadio;
        private LinearLayout lay;

        private ViewHolder(View itemView) {
            super(itemView);
            qualityText = itemView.findViewById(R.id.quality_text);
            description = itemView.findViewById(R.id.description);
            tick = itemView.findViewById(R.id.tick);
            lay = itemView.findViewById(R.id.lay);
            playbackQualityRadio = itemView.findViewById(R.id.playbackQualityRadio);
        }
    }


}
