package com.mvhub.mvhubplus.fragments.player.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.JsonObject;
import com.brightcove.player.model.Video;
import com.mvhub.mvhubplus.Bookmarking.BookmarkingViewModel;
import com.mvhub.mvhubplus.SDKConfig;
import com.mvhub.mvhubplus.activities.article.ArticleActivity;
import com.mvhub.mvhubplus.activities.live.LiveActivity;
import com.mvhub.mvhubplus.activities.series.ui.SeriesDetailActivity;
import com.mvhub.mvhubplus.baseModels.BaseBindingFragment;
import com.mvhub.mvhubplus.enums.DownloadStatus;
import com.mvhub.mvhubplus.utils.MediaTypeConstants;
import com.mvhub.mvhubplus.utils.helpers.ActivityTrackers;
import com.mvhub.mvhubplus.utils.helpers.downloads.OnDownloadClickInteraction;
import com.mvhub.mvhubplus.R;
import com.mvhub.mvhubplus.activities.detail.ui.DetailActivity;
import com.mvhub.mvhubplus.activities.detail.ui.EpisodeActivity;
import com.mvhub.mvhubplus.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import com.mvhub.mvhubplus.databinding.DetailWatchlistLikeShareViewBinding;
import com.mvhub.mvhubplus.fragments.dialog.AlertDialogFragment;
import com.mvhub.mvhubplus.fragments.dialog.AlertDialogSingleButtonFragment;
import com.mvhub.mvhubplus.utils.commonMethods.AppCommonMethod;
import com.mvhub.mvhubplus.utils.constants.AppConstants;
import com.mvhub.mvhubplus.utils.helpers.CheckInternetConnection;

import com.mvhub.mvhubplus.utils.helpers.StringUtils;
import com.mvhub.mvhubplus.utils.helpers.ToastHandler;
import com.mvhub.mvhubplus.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;


import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.reactivex.annotations.NonNull;

public class UserInteractionFragment extends BaseBindingFragment<DetailWatchlistLikeShareViewBinding> implements AlertDialogFragment.AlertDialogListener, View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();
    private int assestId;
    private Context context;
    private KsPreferenceKeys preference;
    private String token;
    private int watchListCounter = 0;
    private int likeCounter = 0;
    private boolean loginStatus;
    private EnveuVideoItemBean seriesDetailBean;
    private BookmarkingViewModel bookmarkingViewModel;
    private long mLastClickTime = 0;
    private boolean isloggedout = false;
    private String videoId = "6081937244001";
    private OnDownloadClickInteraction onDownloadClickInteraction;
    /**
     * The policy key for the video cloud account.
     */
    private Video video;
    private String seriesId;


    public UserInteractionFragment() {

    }

    @Override
    protected DetailWatchlistLikeShareViewBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return DetailWatchlistLikeShareViewBinding.inflate(inflater);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (!(context instanceof OnDownloadClickInteraction))
            try {
                throw new Throwable("Activity doesnot implement OnDownloadClickInteraction");
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
    }


    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            getAssetId();
            hitApiIsLike();
            hitApiIsWatchList();
            isloggedout = false;
            if (context instanceof SeriesDetailActivity) {
                getBinding().watchList.setVisibility(View.GONE);
            }

            if (context instanceof LiveActivity) {
                getBinding().watchList.setVisibility(View.GONE);
                getBinding().llLike.setVisibility(View.GONE);
            }

        } catch (Exception e) {

        }
        setClickListeners();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onDownloadClickInteraction = (OnDownloadClickInteraction) getActivity();
    }

    private void setClickListeners() {
        // getBinding().llLike.setOnClickListener(this);
        getBinding().shareWith.setOnClickListener(this);
        getBinding().showComments.setOnClickListener(this);
        //  getBinding().watchList.setOnClickListener(this);
        getBinding().downloadVideo.setOnClickListener(this);
        getBinding().videoDownloaded.setOnClickListener(this);
        getBinding().videoDownloading.setProgress(0);
        getBinding().videoDownloading.setOnClickListener(this);
        getBinding().pauseDownload.setOnClickListener(this);
        getBinding().setDownloadStatus(DownloadStatus.START);
    }

    private void getAssetId() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.likeCounter = 0;
            this.assestId = bundle.getInt(AppConstants.BUNDLE_ASSET_ID);
            seriesDetailBean = (EnveuVideoItemBean) bundle.getSerializable(AppConstants.BUNDLE_SERIES_DETAIL);
            videoId = seriesDetailBean.getBrightcoveVideoId();
            seriesId = bundle.getString(AppConstants.BUNDLE_SERIES_ID);
        }

        if (getActivity() != null && preference == null)
            preference = KsPreferenceKeys.getInstance();
        bookmarkingViewModel = ViewModelProviders.of(this).get(BookmarkingViewModel.class);

        if (preference.getAppPrefLoginStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            token = preference.getAppPrefAccessToken();
        } else {
            resetLike();
            resetWatchList();
        }
        UIintialization();
    }


    public void UIintialization() {
        likeClick();
        watchListClick();
        //  Toast.makeText(getActivity(), "UI Initiala", Toast.LENGTH_LONG).show();

       /* getBinding().shareWith.setOnClickListener(v -> {

        });*/

        getBinding().shareWith.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        getBinding().showComments.setOnClickListener(v -> {
            //new ToastHandler(getActivity()).show(getActivity().getResources().getString(R.string.coming_soon));
            /*if(context instanceof  SeriesDetailActivity){
                ((SeriesDetailActivity) context).openCommentSection();
            }*/
        });

    }

    GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent event) {
            super.onLongPress(event);
            copyShareURL();
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            openShareDialogue();
            return true;

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }
    });

    private void copyShareURL() {
        String imgUrl = seriesDetailBean.getThumbnailImage();
        int id = seriesDetailBean.getId();
        String title = seriesDetailBean.getName();
        String assetType = "";
        if (context instanceof SeriesDetailActivity) {
            imgUrl = seriesDetailBean.getPosterURL();
            id = seriesDetailBean.getId();
            title = seriesDetailBean.getTitle();
            assetType = MediaTypeConstants.getInstance().getSeries();
            ((SeriesDetailActivity) context).seriesLoader();
        } else if (context instanceof EpisodeActivity) {
            imgUrl = seriesDetailBean.getPosterURL();
            id = seriesDetailBean.getId();
            title = seriesDetailBean.getTitle();
            assetType = MediaTypeConstants.getInstance().getEpisode();

        } else if (context instanceof DetailActivity) {
            imgUrl = seriesDetailBean.getPosterURL();
            id = seriesDetailBean.getId();
            title = seriesDetailBean.getTitle();
            assetType = seriesDetailBean.getAssetType();

        } else if (context instanceof ArticleActivity) {
            imgUrl = seriesDetailBean.getPosterURL();
            id = seriesDetailBean.getId();
            title = seriesDetailBean.getTitle();
            assetType = MediaTypeConstants.getInstance().getMovie();

        }
        else if (context instanceof LiveActivity) {
            imgUrl = seriesDetailBean.getPosterURL();
            id = seriesDetailBean.getId();
            title = seriesDetailBean.getTitle();
            assetType = MediaTypeConstants.getInstance().getLive();

        }
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        AppCommonMethod.copyShareURL(getActivity(), title, id, assetType, imgUrl, seriesDetailBean.getSeriesId()  == null ? "" : seriesDetailBean.getSeriesId(), seriesDetailBean.getSeason());

        new Handler().postDelayed(() -> {
            if (context instanceof SeriesDetailActivity) {
                ((SeriesDetailActivity) context).dismissLoading(((SeriesDetailActivity) context).getBinding().progressBar);
            }
        }, 2000);
    }


    private void goToLogin() {
        if (context instanceof SeriesDetailActivity) {
            ActivityTrackers.getInstance().setLauncherActivity("SeriesDetailActivity");
            ((SeriesDetailActivity) context).openLogin();
        } else if (context instanceof EpisodeActivity) {
            ((EpisodeActivity) context).openLoginPage(getResources().getString(R.string.please_login_play));
            ActivityTrackers.getInstance().setLauncherActivity("EpisodeActivity");
        } else if (context instanceof DetailActivity) {
            ActivityTrackers.getInstance().setLauncherActivity("DetailActivity");
            ((DetailActivity) context).openLoginPage(getResources().getString(R.string.please_login_play));
        }
    }

    public void watchListClick() {
        getBinding().watchList.setOnClickListener(v -> {
            if (getBinding().wProgressBar.getVisibility() != View.VISIBLE) {
                String isLogin = preference.getAppPrefLoginStatus();
                if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                    setWatchListForAsset(1);
                } else {
                    ActivityTrackers.getInstance().setAction(ActivityTrackers.WATCHLIST);
                    goToLogin();
                }
            }
        });
    }

    public void setWatchListForAsset(int from) {
        getBinding().wProgressBar.setVisibility(View.VISIBLE);
        getBinding().addIcon.setVisibility(View.GONE);
        if (watchListCounter == 0)
            hitApiAddWatchList(from);
        else {
            hitApiRemoveList();
        }
    }

    private void hitApiRemoveList() {
        bookmarkingViewModel.hitRemoveWatchlist(token, assestId).observe(this, responseEmpty -> {
            getBinding().wProgressBar.setVisibility(View.GONE);
            getBinding().addIcon.setVisibility(View.VISIBLE);
            if (Objects.requireNonNull(responseEmpty).isStatus()) {
                resetWatchList();
            } else {
                if (responseEmpty.getResponseCode() == 4302) {
                    isloggedout = true;
                    logoutCall();
                   // showDialog(getActivity().getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));
                } else if (responseEmpty.getResponseCode() == 500) {
                    showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));
                }
            }
        });

    }

    public void likeClick() {
        getBinding().llLike.setOnClickListener(view -> {
            setLikeForAsset(1);
        });

    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setLikeForAsset(int from) {
        if (getBinding().lProgressBar.getVisibility() != View.VISIBLE) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            if (preference.getAppPrefLoginStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                getBinding().lProgressBar.setVisibility(View.VISIBLE);
                getBinding().likeIcon.setVisibility(View.GONE);
                if (likeCounter == 0)
                    hitApiAddLike(from);
                else
                    hitApiRemoveLike();
            } else {
                ActivityTrackers.getInstance().setAction(ActivityTrackers.LIKE);
                goToLogin();
            }
        }
    }


    public void hitApiAddLike(int from) {
        bookmarkingViewModel.hitApiAddLike(token, assestId).observe(this, responseEmpty -> {
            getBinding().lProgressBar.setVisibility(View.GONE);
            getBinding().likeIcon.setVisibility(View.VISIBLE);
            if (Objects.requireNonNull(responseEmpty).isStatus()) {
                setLike();
            } else {
                if (responseEmpty.getResponseCode() == 4302) {
                    isloggedout = true;
                    logoutCall();
                  //  showDialog(getActivity().getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));
                } else if (responseEmpty.getResponseCode() == 4902) {
                    setLike();
                    String debugMessage = responseEmpty.getDebugMessage();
                    //from value will bedepends on how user click of watchlist icon-->>if loggedout=2 else=2
                    if (from == 1) {
                        showDialog(getActivity().getResources().getString(R.string.error), debugMessage);
                    }

                } else if (responseEmpty.getResponseCode() == 500) {
                    showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));
                }
            }

        });
    }

    public void hitApiRemoveLike() {

        bookmarkingViewModel.hitApiDeleteLike(token, assestId).observe(this, responseEmpty -> {
            getBinding().lProgressBar.setVisibility(View.GONE);
            getBinding().likeIcon.setVisibility(View.VISIBLE);
            if (Objects.requireNonNull(responseEmpty).isStatus()) {
                resetLike();
            } else {
                if (responseEmpty.getResponseCode() == 4302) {
                    isloggedout = true;
                    showDialog(getActivity().getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));
                } else if (responseEmpty.getResponseCode() == 500) {
                    showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));
                }
            }


        });


    }


    public void hitApiIsLike() {
        if (preference.getAppPrefLoginStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            JsonObject requestParam = new JsonObject();
            requestParam.addProperty(AppConstants.API_PARAM_LIKE_ID, assestId);
            requestParam.addProperty(AppConstants.API_PARAM_LIKE_TYPE, MediaTypeConstants.getInstance().getSeries());
            bookmarkingViewModel.hitApiIsLike(token, assestId).observe(this, responseEmpty -> {

                if (Objects.requireNonNull(responseEmpty).isStatus()) {
                    if (StringUtils.isNullOrEmptyOrZero(responseEmpty.getData().getId())) {
                        resetLike();
                    } else {
                        setLike();
                    }
                } else {
                    if (responseEmpty.getResponseCode() == 4302) {
                        isloggedout = true;
                        logoutCall();
                       // showDialog(getActivity().getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));
                    } else if (responseEmpty.getResponseCode() == 500) {
                        showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));
                    }
                }

            });
        }
    }


    public void hitApiIsWatchList() {
        if (preference.getAppPrefLoginStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            bookmarkingViewModel.hitApiIsWatchList(token, assestId).observe(getActivity(), responseEmpty -> {

                if (Objects.requireNonNull(responseEmpty).isStatus()) {
                    setWatchList();
                } else {
                    if (responseEmpty.getResponseCode() == 4302) {
                        isloggedout = true;
                        logoutCall();
                       // showDialog(getActivity().getResources().getString(R.string.logged_out), responseEmpty.getDebugMessage() + "");
                    } else if (responseEmpty.getResponseCode() == 500) {
                        showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));
                    }
                }
            });
        }


    }

    public void hitApiAddWatchList(int from) {
        if (context instanceof SeriesDetailActivity) {
            ((SeriesDetailActivity) context).seriesLoader();
        }
        bookmarkingViewModel.hitApiAddWatchList(token, assestId).observe(getActivity(), responseEmpty -> {
            getBinding().wProgressBar.setVisibility(View.GONE);
            getBinding().addIcon.setVisibility(View.VISIBLE);
            if (Objects.requireNonNull(responseEmpty).isStatus()) {
                setWatchList();
            } else {
                if (responseEmpty.getResponseCode() == 4302) {
                    isloggedout = true;
                    logoutCall();
                    //showDialog(getActivity().getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));
                } else if (responseEmpty.getResponseCode() == 4904) {
                    setWatchList();
                    String debugMessage = responseEmpty.getDebugMessage();
                    //from value will bedepends on how user click of watchlist icon-->>if loggedout=2 else=2
                    if (from == 1) {
                        showDialog(getActivity().getResources().getString(R.string.error), debugMessage);
                    }
                } else if (responseEmpty.getResponseCode() == 500) {
                    showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));
                }
            }

        });


    }


    private void openShareDialogue() {
        String imgUrl = seriesDetailBean.getThumbnailImage();
        int id = seriesDetailBean.getId();
        String title = seriesDetailBean.getName();
        String assetType = "";
        if (context instanceof SeriesDetailActivity) {
            imgUrl = seriesDetailBean.getPosterURL();
            id = seriesDetailBean.getId();
            title = seriesDetailBean.getTitle();
            assetType = MediaTypeConstants.getInstance().getSeries();
            ((SeriesDetailActivity) context).seriesLoader();
        } else if (context instanceof EpisodeActivity) {
            imgUrl = seriesDetailBean.getPosterURL();
            id = seriesDetailBean.getId();
            title = seriesDetailBean.getTitle();
            assetType = MediaTypeConstants.getInstance().getEpisode();

        } else if (context instanceof DetailActivity) {
            imgUrl = seriesDetailBean.getPosterURL();
            id = seriesDetailBean.getId();
            title = seriesDetailBean.getTitle();
            assetType = seriesDetailBean.getAssetType();

        } else if (context instanceof ArticleActivity) {
            imgUrl = seriesDetailBean.getPosterURL();
            id = seriesDetailBean.getId();
            title = seriesDetailBean.getTitle();
            assetType = MediaTypeConstants.getInstance().getMovie();

        }
        else if (context instanceof LiveActivity) {
            imgUrl = seriesDetailBean.getPosterURL();
            id = seriesDetailBean.getId();
            title = seriesDetailBean.getTitle();
            assetType = MediaTypeConstants.getInstance().getLive();

        }
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        AppCommonMethod.openShareDialog(getActivity(), title, id, assetType, imgUrl, seriesDetailBean.getSeriesId()  == null ? "" : seriesDetailBean.getSeriesId(), seriesDetailBean.getSeason());

        new Handler().postDelayed(() -> {
            if (context instanceof SeriesDetailActivity) {
                ((SeriesDetailActivity) context).dismissLoading(((SeriesDetailActivity) context).getBinding().progressBar);
            }
        }, 2000);
    }

   /* private AppConstants.ContentType getAssetType() {
        if (context instanceof SeriesDetailActivity) {
            return AppConstants.ContentType.SERIES;
        } else if (context instanceof EpisodeActivity) {
            return AppConstants.ContentType.EPISODE;
        } else {
            return AppConstants.ContentType.VIDEO;
        }
    }*/

    public boolean isLogin() {
        loginStatus = preference.getAppPrefLoginStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString());
        return loginStatus;
    }


    public void setLike() {
        getBinding().lProgressBar.setVisibility(View.GONE);
        getBinding().likeIcon.setVisibility(View.VISIBLE);
        likeCounter = 1;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions


            setLikeProperty();

        } else {

            setLikeProperty();
        }

    }

    private void setLikeProperty() {
        if (KsPreferenceKeys.getInstance().getCurrentTheme().equalsIgnoreCase(AppConstants.LIGHT_THEME)) {
            getBinding().tvLike.setTextColor(ContextCompat.getColor(getActivity(), R.color.more_text_color_dark));
            ImageViewCompat.setImageTintList(getBinding().likeIcon, ColorStateList.valueOf(getResources().getColor(R.color.more_text_color_dark)));
        } else {
            getBinding().tvLike.setTextColor(ContextCompat.getColor(getActivity(), R.color.more_text_color_dark));
            ImageViewCompat.setImageTintList(getBinding().likeIcon, ColorStateList.valueOf(getResources().getColor(R.color.more_text_color_dark)));

        }
    }

    public void resetLike() {
        getBinding().lProgressBar.setVisibility(View.GONE);
        getBinding().likeIcon.setVisibility(View.VISIBLE);
        likeCounter = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (KsPreferenceKeys.getInstance().getCurrentTheme().equalsIgnoreCase(AppConstants.LIGHT_THEME)) {
                getBinding().tvLike.setTextColor(ContextCompat.getColor(getActivity(), R.color.navy_blue));
                ImageViewCompat.setImageTintList(getBinding().likeIcon, ColorStateList.valueOf(getResources().getColor(R.color.navy_blue)));
            } else {
                getBinding().tvLike.setTextColor(ContextCompat.getColor(getActivity(), R.color.description_grey));
                ImageViewCompat.setImageTintList(getBinding().likeIcon, ColorStateList.valueOf(getResources().getColor(R.color.description_grey)));

            }

        } else {
            if (KsPreferenceKeys.getInstance().getCurrentTheme().equalsIgnoreCase(AppConstants.LIGHT_THEME)) {
                getBinding().tvLike.setTextColor(ContextCompat.getColor(getActivity(), R.color.navy_blue));
                ImageViewCompat.setImageTintList(getBinding().likeIcon, ColorStateList.valueOf(getResources().getColor(R.color.navy_blue)));
            } else {
                getBinding().tvLike.setTextColor(ContextCompat.getColor(getActivity(), R.color.description_grey));
                ImageViewCompat.setImageTintList(getBinding().likeIcon, ColorStateList.valueOf(getResources().getColor(R.color.description_grey)));

            }
        }

    }


    public void setWatchList() {
        getBinding().wProgressBar.setVisibility(View.GONE);
        getBinding().addIcon.setVisibility(View.VISIBLE);
        watchListCounter = 1;
        if (KsPreferenceKeys.getInstance().getCurrentTheme().equalsIgnoreCase(AppConstants.LIGHT_THEME)) {
            getBinding().addIcon.setImageResource(R.drawable.check_icon_navy_blue);
        } else {
            getBinding().addIcon.setImageResource(R.drawable.check_icon_navy_blue);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            setTextColor();
            // Do something for lollipop and above versions

        } else {
            setTextColor();
            // do something for phones running an SDK before lollipop
            //  ImageViewCompat.setImageTintList(getBinding().addIcon, ColorStateList.valueOf(getResources().getColor(R.color.dialog_green_color)));
        }

    }

    private void setTextColor() {
        if (KsPreferenceKeys.getInstance().getCurrentTheme().equalsIgnoreCase(AppConstants.LIGHT_THEME)) {
            //  ImageViewCompat.setImageTintList(getBinding().addIcon, ColorStateList.valueOf(getResources().getColor(R.color.dialog_green_color)));
            getBinding().tvWatch.setTextColor(ContextCompat.getColor(getActivity(), R.color.more_text_color_dark));
        } else {
            getBinding().tvWatch.setTextColor(ContextCompat.getColor(getActivity(), R.color.more_text_color_dark));
        }
    }

    public void resetWatchList() {
        getBinding().wProgressBar.setVisibility(View.GONE);
        getBinding().addIcon.setVisibility(View.VISIBLE);
        watchListCounter = 0;

        if (KsPreferenceKeys.getInstance().getCurrentTheme().equalsIgnoreCase(AppConstants.LIGHT_THEME)) {
            getBinding().addIcon.setImageResource(R.drawable.add_to_watchlist_navy_blue);
        } else {
            getBinding().addIcon.setImageResource(R.drawable.add_to_watchlist);
        }


       /* if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            setTextColor();
        } else {
            setTextColor();
        }*/

        if (KsPreferenceKeys.getInstance().getCurrentTheme().equalsIgnoreCase(AppConstants.LIGHT_THEME)) {
            //  ImageViewCompat.setImageTintList(getBinding().addIcon, ColorStateList.valueOf(getResources().getColor(R.color.dialog_green_color)));
            getBinding().tvWatch.setTextColor(ContextCompat.getColor(getActivity(), R.color.navy_blue));
        } else {
            getBinding().tvWatch.setTextColor(ContextCompat.getColor(getActivity(), R.color.description_grey));
        }

    }

    public void hideProgressBar() {
        if (context instanceof SeriesDetailActivity) {
            ((SeriesDetailActivity) context).isRailData = true;
            ((SeriesDetailActivity) context).stopShimmer();
            ((SeriesDetailActivity) context).dismissLoading(((SeriesDetailActivity) context).getBinding().progressBar);
        } else if (context instanceof EpisodeActivity) {
            ((EpisodeActivity)
                    context).dismissLoading(((EpisodeActivity) context).getBinding().progressBar);
            ((EpisodeActivity) context).isRailData = true;
            ((EpisodeActivity) context).stopShimmercheck();
        }
    }

    private void showDialog(String title, String message) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        AlertDialogSingleButtonFragment alertDialog = AlertDialogSingleButtonFragment.newInstance(title, message, getResources().getString(R.string.ok));
        alertDialog.setCancelable(false);
        alertDialog.setAlertDialogCallBack(this);
        alertDialog.show(fm, "fragment_alert");
    }

    @Override
    public void onFinishDialog() {
        if (isloggedout) {
            logoutCall();
        }
    }

    private void logoutCall() {
        if (CheckInternetConnection.isOnline(Objects.requireNonNull(getActivity()))) {
            clearCredientials(preference);
            hitApiLogout(getBaseActivity(), preference.getAppPrefAccessToken());
        } else {
            new ToastHandler(getActivity()).show(getActivity().getResources().getString(R.string.no_internet_connection));
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shareWith: {
                openShareDialogue();
            }
            break;
            case R.id.show_comments: {
            }
            break;
            case R.id.llLike: {
                likeClick();
            }
            break;
            case R.id.watchList: {
                watchListClick();
            }
            break;
            case R.id.download_video: {
                onDownloadClickInteraction.onDownloadClicked(null, 0, this);
            }
            break;
            case R.id.video_downloaded: {
                onDownloadClickInteraction.onDownloadCompleteClicked(view, this, null);
            }
            break;
            case R.id.video_downloading: {
                onDownloadClickInteraction.onProgressbarClicked(view, this, null);
            }
            break;
            case R.id.pause_download: {
                onDownloadClickInteraction.onPauseClicked(null, this);
            }
        }
    }

    public void setDownloadStatus(DownloadStatus downloadStatus) {
        if (getBinding() != null)
            getBinding().setDownloadStatus(downloadStatus);
    }

    public void setDownloadable(boolean isDownloadable) {
        if (getBinding() != null)
            getBinding().setIsDownloadable(false);
    }

    public void setDownloadProgress(float progress) {
        if (getBinding() != null)
            getBinding().videoDownloading.setProgress(progress);
    }

}
