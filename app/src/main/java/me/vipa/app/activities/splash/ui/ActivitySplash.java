package me.vipa.app.activities.splash.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import me.vipa.app.activities.onBoarding.UI.OnBoarding;
import me.vipa.app.activities.onBoarding.UI.OnBoardingTab;
import me.vipa.app.utils.cropImage.helpers.PrintLogging;
import me.vipa.app.utils.helpers.downloads.room.DownloadModel;
import me.vipa.baseClient.BaseClient;
import me.vipa.baseClient.BaseConfiguration;
import me.vipa.baseClient.BaseDeviceType;
import me.vipa.baseClient.BaseGateway;
import me.vipa.baseClient.BasePlatform;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.google.gson.Gson;
import com.mmtv.utils.helpers.downloads.DownloadHelper;
import me.vipa.app.activities.article.ArticleActivity;
import me.vipa.app.activities.homeactivity.ui.HomeActivity;
import me.vipa.app.activities.live.LiveActivity;
import me.vipa.app.activities.splash.dialog.ConfigFailDialog;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.callbacks.commonCallbacks.DialogInterface;
import me.vipa.app.dependencies.providers.DTGPrefrencesProvider;
import me.vipa.app.BuildConfig;
import me.vipa.app.MvHubPlusApplication;
import me.vipa.app.R;
import me.vipa.app.SDKConfig;
import me.vipa.app.activities.detail.ui.DetailActivity;
import me.vipa.app.activities.detail.ui.EpisodeActivity;
import me.vipa.app.activities.series.ui.SeriesDetailActivity;
import me.vipa.app.callbacks.apicallback.ApiResponseModel;
import me.vipa.app.callbacks.commonCallbacks.VersionValidator;
import me.vipa.app.databinding.ActivitySplashBinding;
import me.vipa.app.fragments.dialog.AlertDialogFragment;
import me.vipa.app.networking.errormodel.ApiErrorModel;
import me.vipa.app.utils.MediaTypeConstants;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.config.ConfigManager;
import me.vipa.app.utils.config.bean.ConfigBean;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.helpers.AnalyticsController;
import me.vipa.app.utils.helpers.ForceUpdateHandler;
import me.vipa.app.utils.helpers.NetworkConnectivity;

import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;
import javax.sql.DataSource;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import me.vipa.app.activities.article.ArticleActivity;
import me.vipa.app.activities.homeactivity.ui.HomeActivity;
import me.vipa.app.activities.splash.dialog.ConfigFailDialog;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.dependencies.providers.DTGPrefrencesProvider;
import me.vipa.app.networking.errormodel.ApiErrorModel;
import me.vipa.baseClient.BaseClient;
import me.vipa.baseClient.BaseConfiguration;
import me.vipa.baseClient.BaseDeviceType;
import me.vipa.baseClient.BaseGateway;
import me.vipa.baseClient.BasePlatform;

public class ActivitySplash extends BaseBindingActivity<ActivitySplashBinding> implements AlertDialogFragment.AlertDialogListener {
    private final String TAG = this.getClass().getSimpleName();
    @Inject
    DTGPrefrencesProvider dtgPrefrencesProvider;
    private ForceUpdateHandler forceUpdateHandler;
    private KsPreferenceKeys session;
    private MvHubPlusApplication appState;
    private boolean viaIntent;
    private long mLastClickTime = 0;
    private String currentLanguage;
    private ConfigBean configBean;
    private int configCall = 1;
    private int count = 0;
    int clapanimation=1;
    private String notid = "";
    private String notAssetType = "";
    private int notificationAssetId = 0;
    private Animation zoomInAnimation;
    private Animation rotateAnimation;
    private Animation translateAnimation;

    @Override
    public ActivitySplashBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivitySplashBinding.inflate(inflater);
    }

    DownloadHelper downloadHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//setFullScreen();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (TextUtils.isEmpty(KsPreferenceKeys.getInstance().getQualityName())) {
            KsPreferenceKeys.getInstance().setQualityName("Auto");
            KsPreferenceKeys.getInstance().setQualityPosition(0);
        }

        session = KsPreferenceKeys.getInstance();
        //session.setDownloadOverWifi(1);
        AppCommonMethod.getPushToken(this);
        updateAndroidSecurityProvider(ActivitySplash.this);

        new AnalyticsController(ActivitySplash.this).callAnalytics("splash_screen", "Action", "Launch");
        // MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        currentLanguage = KsPreferenceKeys.getInstance().getAppLanguage();

        MvHubPlusApplication.getApplicationContext(this).getEnveuComponent().inject(this);
        //dtgPrefrencesProvider.saveExpiryDays(3);
        downloadHelper = new DownloadHelper(this);
        downloadHelper.deleteAllExpiredVideos();

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                downloadHelper.getCatalog().removeListeners();
            }
        },1000);*/

        notificationCheck();

        connectionObserver();
        getBinding().noConnectionLayout.retryTxt.setOnClickListener(view -> connectionObserver());
        getBinding().noConnectionLayout.btnMyDownloads.setOnClickListener(view -> new ActivityLauncher(this).launchMyDownloads());
        Logger.e("IntentData", new Gson().toJson(this.getIntent().getData()));

        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "me.vipa.app",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }*/

        //printHashKey();

    }


    private void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "me.vipa.app",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                PrintLogging.printLog("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            PrintLogging.printLog("Exception", "" + e);
        } catch (NoSuchAlgorithmException e) {
            PrintLogging.printLog("Exception", "" + e);
        }
    }

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


    private void callConfig(JSONObject jsonObject, String updateType) {
        ConfigManager.getInstance().getConfig(new ApiResponseModel() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object response) {
                Logger.e("Animation End","Config Call Started");
                boolean isTablet = getResources().getBoolean(R.bool.isTablet);
                configBean = AppCommonMethod.getConfigResponse();
                Gson gson = new Gson();
                String json = gson.toJson(configBean);
                Log.e("configResponseLog", json);
                AppCommonMethod.setConfigConstant(configBean, isTablet);

                String API_KEY = "";
                String DEVICE_TYPE = "";
                if (isTablet) {
                    API_KEY = SDKConfig.API_KEY_TAB;
                    DEVICE_TYPE = BaseDeviceType.tablet.name();
                } else {
                    API_KEY = SDKConfig.API_KEY_MOB;
                    DEVICE_TYPE = BaseDeviceType.mobile.name();
                }

                BaseClient client = new BaseClient(ActivitySplash.this, BaseGateway.ENVEU, SDKConfig.getInstance().getBASE_URL(), SDKConfig.getInstance().getSUBSCRIPTION_BASE_URL(), DEVICE_TYPE, API_KEY, BasePlatform.android.name(), isTablet, AppCommonMethod.getDeviceId(getContentResolver()));
                BaseConfiguration.Companion.getInstance().clientSetup(client);
                updateLanguage(configBean.getData().getAppConfig().getPrimaryLanguage());

                if (configBean != null) {
                    startClapAnimation(jsonObject, updateType,isTablet);

                } else {
                    configFailPopup();
                }
            }

            @Override
            public void onError(ApiErrorModel httpError) {
                configFailPopup();
            }

            @Override
            public void onFailure(ApiErrorModel httpError) {
                configFailPopup();
            }
        });

    }

    private void startClapAnimation(JSONObject jsonObject, String updateType, boolean isTablet) {
        if (KsPreferenceKeys.getInstance().getfirstTimeUser()){
            KsPreferenceKeys.getInstance().setfirstTimeUser(false);
            if (isTablet){
                new ActivityLauncher(ActivitySplash.this).onBoardingTab(ActivitySplash.this, OnBoardingTab.class);
            }else {
                new ActivityLauncher(ActivitySplash.this).onBoardingScreen(ActivitySplash.this, OnBoarding.class);
            }


        }else {

            Log.w("branchRedirectors", "onAnimationEnd1");

            if (jsonObject != null) {
                if (updateType != null && updateType.equalsIgnoreCase(ForceUpdateHandler.RECOMMENDED)) {
                    brachRedirections(jsonObject);
                } else {
                    boolean updateValue = getForceUpdateValue(jsonObject, 1);
                    if (!updateValue) {
                        brachRedirections(jsonObject);
                    }
                }
            } else {
                if (updateType != null && updateType.equalsIgnoreCase(ForceUpdateHandler.RECOMMENDED)) {
                    Log.w("branchRedirectors", "-->>config" + "");
                    // homeRedirection();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.w("branchRedirectors", "-->>non" + "");
                            //This logic is for now will update later
                        new ActivityLauncher(ActivitySplash.this).homeScreen(ActivitySplash.this, HomeActivity.class,false);

                            finish();
                        }
                    }, 1);
                } else {
                    boolean updateValue = getForceUpdateValue(null, 3);
                    if (!updateValue) {
                        Log.w("branchRedirectors", "-->>config" + "");
                        // homeRedirection();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.w("branchRedirectors", "-->>non" + "");
                                new ActivityLauncher(ActivitySplash.this).homeScreen(ActivitySplash.this, HomeActivity.class,false);

                                finish();
                            }
                        }, 1);
                    }
                }

            }
        }

    }

    private void notificationCheck() {
        Logger.w("notificationCheck","in");
        if (getIntent() != null) {
            Logger.w("notificationCheck","notnull");
            if (getIntent().getExtras() != null) {
                Logger.w("notificationCheck","extra");
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    notid = bundle.getString("id");
                    if (notid != null && !notid.equalsIgnoreCase("")) {
                        notAssetType = bundle.getString("contentType");
                        if (notAssetType != null && !notAssetType.equalsIgnoreCase("")) {
                            parseNotification(notid,notAssetType);
                        }else {
                            onNewIntent(getIntent());
                        }

                    } else {
                        Log.d ("myApplication--->>>", getIntent().toString());
                        onNewIntent(getIntent());
                    }

                } else {
                    onNewIntent(getIntent());
                }

            } else {
                Logger.w("notificationCheck","nonextra");
                onNewIntent(getIntent());
            }
        }else {
            Logger.w("notificationCheck","null");
        }

    }

    private void loadAnimations() {
        //Glide.with(this).load(R.raw.keep).asGif().into(imageView);
//        Glide.with(ActivitySplash.this).asGif().load(R.drawable.splash_img).into(getBinding().imgLogo);
        Uri video;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splashtab);
        }else {
            video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash);
        }



        getBinding().videoView.setVideoURI(video);
        getBinding().videoView.requestFocus();

        getBinding().videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                callNextForRedirection();
            }
        });

        getBinding().videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                callNextForRedirection();
                return false;
            }
        });

        getBinding().videoView.start();
    }


//        Glide.with(this).asGif().load(R.drawable.splash_img).listener(new RequestListener<GifDrawable>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
//                callNextForRedirection();
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
//                resource.setLoopCount(1);
//                resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
//                    @Override
//                    public void onAnimationEnd(Drawable drawable) {
//                        callNextForRedirection();
//                    }
//                });
//                return false;
//            }
//
//
//        }).into(getBinding().imgLogo);





//        zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_zoom_in);
//        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_rotate_animation);
//        translateAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_zoom_out);
//        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                Log.w("branchRedirectors", "onAnimationEnd2");
//                getBinding().flapView1.setVisibility(View.VISIBLE);
//                getBinding().flapView1.startAnimation(translateAnimation);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        zoomInAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                if (viaIntent) {
//                    String notiVAlues = KsPreferenceKeys.getInstance().getNotificationPayload(notificationAssetId + "");
//                    try {
//                        Logger.e("Animation End","Config Call");
//                        JSONObject jsonObject = new JSONObject(notiVAlues);
//                        redirections(jsonObject);
//                    } catch (Exception e) {
//                        if (notificationObject!=null){
//                            redirections(notificationObject);
//                        }else {
//                            redirections(null);
//                        }
//                    }
//                }else{
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            Branch.getInstance().initSession(branchReferralInitListener, ActivitySplash.this.getIntent().getData(), ActivitySplash.this);
//                        }
//                    },1000);
//
//                }
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        new Handler().postDelayed(() -> {
//            getBinding().imgLogo.setVisibility(View.VISIBLE);
//            getBinding().imgLogo.startAnimation(zoomInAnimation);
//        }, 100);


    private void callNextForRedirection() {
        if (viaIntent) {
            String notiVAlues = KsPreferenceKeys.getInstance().getNotificationPayload(notificationAssetId + "");
            try {
                Logger.e("Animation End","Config Call");
                JSONObject jsonObject = new JSONObject(notiVAlues);
                redirections(jsonObject);
            } catch (Exception e) {
                if (notificationObject!=null){
                    redirections(notificationObject);
                }else {
                    redirections(null);
                }
            }
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Branch.getInstance().initSession(branchReferralInitListener, ActivitySplash.this.getIntent().getData(), ActivitySplash.this);
                }
            },1000);

        }
    }

    private void checkLanguage() {
        if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Hindi")) {
            AppCommonMethod.updateLanguage("hi", this);
        } else if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")) {
            AppCommonMethod.updateLanguage("en", this);
        } else if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Indonesia")) {
            AppCommonMethod.updateLanguage("in", this);
        }
    }

    private void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Logger.e("SecurityException", "Google Play Services not available.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            boolean isTablet = ActivitySplash.this.getResources().getBoolean(R.bool.isTablet);
            getBinding().buildNumber.setVisibility(View.GONE);
            if (!isTablet)
                getBinding().buildNumber.setText(getResources().getString(R.string.app_name) + "  V " + BuildConfig.VERSION_NAME);
        }catch (Exception ignored){

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
       // Branch.getInstance().initSession(branchReferralInitListener, ActivitySplash.this.getIntent().getData(), ActivitySplash.this);
        //Branch.sessionBuilder(this).withCallback(branchReferralInitListener).withData(getIntent() != null ? getIntent().getData() : null).init();
    }

    private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {
        @Override
        public void onInitFinished(@Nullable JSONObject referringParams, @Nullable BranchError error) {
            if (error == null) {
                Log.i("returnedObject er", referringParams + "");
                if (referringParams != null) {
                    Logger.e("BranchCall", String.valueOf(referringParams));
                    int assestId = 0;
                    String contentType = "";
                    if (referringParams.has("id") && referringParams.has("contentType")) {
                        try {
                            assestId = Integer.parseInt(referringParams.getString("id"));
                            contentType = referringParams.getString("contentType");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!contentType.equalsIgnoreCase("") && assestId > 0) {
                            Logger.e("ASSET TYPE", String.valueOf(viaIntent));
                            KsPreferenceKeys.getInstance().setAppPrefJumpTo(contentType);
                            KsPreferenceKeys.getInstance().setAppPrefBranchIo(true);
                            KsPreferenceKeys.getInstance().setAppPrefJumpBackId(assestId);
                            redirections(referringParams);
                            Log.w("redirectionss", "redirections");
                        } else {
                            redirectToHome();
                        }
                    } else {
                        redirectToHome();
                    }
                } else {
                    redirectToHome();

                }
            } else {
                redirectToHome();
                Log.i("returnedObject er", error.getMessage());
            }
        }
    };

    private void redirectToHome() {
        boolean updateValue = getForceUpdateValue(null, 2);
        if (!updateValue) {
            Log.w("branchRedirectors", "homeRedirection");
            homeRedirection();
        }
    }

    private void homeRedirection() {
        Log.w("branchRedirectors", configCall + "");
        if (configCall == 1) {
            Log.w("branchRedirectors", configCall + "");
            configCall = 2;
            callConfig(null, null);
        }
    }

    boolean configRetry = false;

    private void configFailPopup() {
        if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Thai") || KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("हिंदी")) {
            AppCommonMethod.updateLanguage("th", MvHubPlusApplication.getInstance());
        } else if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")) {
            AppCommonMethod.updateLanguage("en", MvHubPlusApplication.getInstance());
        }
        new ConfigFailDialog(ActivitySplash.this).showDialog(new DialogInterface() {
            @Override
            public void positiveAction() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getBinding().progressBar.setVisibility(View.VISIBLE);
                        configRetry = true;
                        callConfig(null, null);
                    }
                }, 200);
            }

            @Override
            public void negativeAction() {
                getBinding().progressBar.setVisibility(View.GONE);
                finish();
            }
        });




        /*BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(ActivitySplash.this);
        View sheetView = getLayoutInflater().inflate(R.layout.config_failure, null);
        mBottomSheetDialog.setContentView(sheetView);

        Button retrybutton = (Button) sheetView.findViewById(R.id.retryBtn);
        Button cancelbutton = (Button) sheetView.findViewById(R.id.cancenBtn);

        retrybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KsPreferenceKeys.getInstance().setString("DMS_Date", "mDate");
                mBottomSheetDialog.dismiss();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getBinding().progressBar.setVisibility(View.VISIBLE);
                        configRetry = true;
                        callConfig(null);
                    }
                }, 200);

            }
        });

        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBinding().progressBar.setVisibility(View.GONE);
                mBottomSheetDialog.dismiss();
                finish();
            }
        });
        mBottomSheetDialog.show();*/
    }


    private void redirections(JSONObject jsonObject) {
        try {
            callConfig(jsonObject, null);
        } catch (Exception e) {

        }
    }

    private void brachRedirections(JSONObject jsonObject) {
        try {
            Log.e("branchRedirectors", new Gson().toJson(jsonObject));
            if (jsonObject != null && jsonObject.has("contentType") && jsonObject.has("id")) {
                int assestId = 0;
                String contentType = jsonObject.getString("contentType");
                String id = jsonObject.getString("id");
                if (id != null && !id.equalsIgnoreCase("")) {
                    assestId = Integer.parseInt(id);
                    if (contentType.equalsIgnoreCase(MediaTypeConstants.getInstance().getSeries())) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        launchSeriesPage(contentType, assestId);
                        new ActivityLauncher(this).homeScreen(this, HomeActivity.class,false);
                        new ActivityLauncher(ActivitySplash.this).seriesDetailScreen(ActivitySplash.this, SeriesDetailActivity.class, assestId);
                        finish();
                    } else if (contentType.equalsIgnoreCase(AppConstants.ContentType.VIDEO.name()) || contentType.equalsIgnoreCase(MediaTypeConstants.getInstance().getMovie())) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        //new ActivityLauncher(ActivitySplash.this).articleScreen(ActivitySplash.this, ArticleActivity.class, assestId, "0", false);
                        new ActivityLauncher(this).homeScreen(this, HomeActivity.class,false);
                        new ActivityLauncher(ActivitySplash.this).detailScreen(ActivitySplash.this, DetailActivity.class, assestId, "0", false);
                        finish();
                    } else if (contentType.equalsIgnoreCase(MediaTypeConstants.getInstance().getShow())) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        //new ActivityLauncher(ActivitySplash.this).articleScreen(ActivitySplash.this, ArticleActivity.class, assestId, "0", false);
                        new ActivityLauncher(this).homeScreen(this, HomeActivity.class,false);
                        new ActivityLauncher(ActivitySplash.this).detailScreen(ActivitySplash.this, DetailActivity.class, assestId, "0", false);
                        finish();
                    } else if (contentType.equalsIgnoreCase(MediaTypeConstants.getInstance().getLive())) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        //new ActivityLauncher(ActivitySplash.this).articleScreen(ActivitySplash.this, ArticleActivity.class, assestId, "0", false);
                        new ActivityLauncher(this).homeScreen(this, HomeActivity.class,false);
                        new ActivityLauncher(ActivitySplash.this).liveScreenBrightCove(ActivitySplash.this, LiveActivity.class, 0l, assestId, "0", false, SDKConfig.getInstance().getLiveDetailId());
                        finish();
                    } else if (contentType.equalsIgnoreCase(MediaTypeConstants.getInstance().getEpisode())) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        new ActivityLauncher(this).homeScreen(this, HomeActivity.class,false);
                        new ActivityLauncher(ActivitySplash.this).episodeScreen(ActivitySplash.this, EpisodeActivity.class, assestId, "0", false);
                        finish();
                    } else if (contentType.equalsIgnoreCase(AppConstants.ContentType.ARTICLE.toString())) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        new ActivityLauncher(this).homeScreen(this, HomeActivity.class,false);
                        new ActivityLauncher(ActivitySplash.this).articleScreen(ActivitySplash.this, ArticleActivity.class, assestId, "0", false);
                        finish();
                    }
                } else {
                    new ActivityLauncher(this).homeScreen(this, HomeActivity.class,false);
                    finish();
                }

            }

        } catch (Exception e) {

        }
    }

    private void launchSeriesPage(String contentType, int assestId) {

    }


    private void connectionObserver() {
        if (NetworkConnectivity.isOnline(this)) {
            connectionValidation(true);
        } else {
            connectionValidation(false);
            try {

                    DownloadHelper downloadHelper = new DownloadHelper(ActivitySplash.this);
                    downloadHelper.getAllVideosFromDatabase().observe(ActivitySplash.this, new Observer<DownloadModel>() {
                        @Override
                        public void onChanged(DownloadModel downloadModel) {
                            if (downloadModel.getDownloadVideos().size()>0){
                                getBinding().noConnectionLayout.btnMyDownloads.setVisibility(View.VISIBLE);
                            }else {
                                getBinding().noConnectionLayout.btnMyDownloads.setVisibility(View.GONE);
                            }
                        }
                    });
            }catch (Exception ignored){
                getBinding().noConnectionLayout.btnMyDownloads.setVisibility(View.GONE);
            }
        }
    }

    private void connectionValidation(Boolean aBoolean) {
        if (aBoolean) {
            getBinding().noConnectionLayout.noConnectionLayout.setVisibility(View.GONE);
            loadAnimations();
        } else {
            getBinding().noConnectionLayout.noConnectionLayout.setVisibility(View.VISIBLE);
            getBinding().noConnectionLayout.noConnectionLayout.bringToFront();
            /*  showDialog(ActivitySplash.this.getResources().getString(R.string.error),getResources().getString(R.string.no_connection)); */
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
        try {
            notid = intent.getStringExtra("assetId");
            notAssetType=intent.getStringExtra("assetType");
            parseNotification(notid,notAssetType);

        } catch (Exception e) {

        }

    }

    JSONObject notificationObject=null;
    private void parseNotification(String notid,String assetType) {
        if (notid != null && !assetType.equalsIgnoreCase("")) {
            notificationAssetId = Integer.parseInt(notid);
            if (notificationAssetId > 0 && assetType!=null && !assetType.equalsIgnoreCase("")) {
                Logger.w("FCM_Payload_final --", notificationAssetId + "");
                notificationObject=AppCommonMethod.createNotificationObject(notid,assetType);
                viaIntent = true;
            }
        } else {
            Branch.getInstance().reInitSession(this, branchReferralInitListener);
        }
    }

    @Override
    public void onFinishDialog() {
        connectionObserver();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    boolean forceUpdate = false;

    private boolean getForceUpdateValue(JSONObject jsonObject, int type) {
        Log.i("branchRedirectors er", "forceupdate");
        if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Thai") || KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("हिंदी")) {
            AppCommonMethod.updateLanguage("th", MvHubPlusApplication.getInstance());
        } else if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")) {
            AppCommonMethod.updateLanguage("en", MvHubPlusApplication.getInstance());
        }
        forceUpdateHandler = new ForceUpdateHandler(ActivitySplash.this, configBean);
        forceUpdateHandler.checkCurrentVersion(new VersionValidator() {
            @Override
            public void version(boolean status, int currentVersion, int playstoreVersion, String updateType) {
                if (status) {
                    forceUpdate = true;
                    forceUpdateHandler.typeHandle(updateType, selection -> {
                        if (updateType.equals(ForceUpdateHandler.RECOMMENDED)) {
                            if (!selection) {
                                getBinding().progressBar.setVisibility(View.VISIBLE);
                                forceUpdateHandler.hideDialog();
                                clapanimation=1;
                                callConfig(null, updateType);
                            }/*else {

                            }
                            if (type == 1) {
                                forceUpdateHandler.hideDialog();
                                brachRedirections(jsonObject);
                            } else {
                                Log.w("branchRedirectors", "-->>force" + "");
                                homeRedirection();
                            }*/

                        }
                    });
                } else {
                    forceUpdate = false;
                }
            }
        });

        return forceUpdate;
    }


}