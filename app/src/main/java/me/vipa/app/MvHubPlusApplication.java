package me.vipa.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.moengage.core.MoEngage;
import com.moengage.core.config.InAppConfig;
import com.moengage.firebase.MoEFireBaseHelper;
import com.moengage.inapp.MoEInAppHelper;

import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import io.branch.referral.Branch;
import me.vipa.app.activities.splash.ui.ActivitySplash;
import me.vipa.app.dependencies.DaggerEnveuComponent;
import me.vipa.app.dependencies.EnveuComponent;
import me.vipa.app.dependencies.modules.UserPreferencesModule;
import me.vipa.app.manager.MoEngageManager;
import me.vipa.app.utils.TrackerUtil.TrackerUtil;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;
import me.vipa.baseClient.BaseDeviceType;

/**
 * This is a subclass of {@link Application} used to provide shared objects for this app, such as
 * the {@link Tracker}.
 */
public class MvHubPlusApplication extends MultiDexApplication {
    private EnveuComponent enveuComponent;
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;
    private static MvHubPlusApplication mvHubPlusApplication;
    private FirebaseAnalytics mFirebaseAnalytics;

    public static MvHubPlusApplication getInstance() {
        return mvHubPlusApplication;
    }

    private void setupBaseClient() {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        String API_KEY = "";
        String DEVICE_TYPE = "";
        if (isTablet) {
            API_KEY = SDKConfig.API_KEY_TAB;
            DEVICE_TYPE = BaseDeviceType.tablet.name();
        } else {
            API_KEY = SDKConfig.API_KEY_MOB;
            DEVICE_TYPE = BaseDeviceType.mobile.name();
        }

        // BaseClient client = new BaseClient(this, BaseGateway.ENVEU, SDKConfig.BASE_URL, SDKConfig.SUBSCRIPTION_BASE_URL, DEVICE_TYPE, BasePlatform.android.name(), isTablet, AppCommonMethod.getDeviceId(getContentResolver()));
        //BaseConfiguration.Companion.getInstance().clientSetup(client);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mvHubPlusApplication = this;
        KsPreferenceKeys.getInstance();
        MultiDex.install(this);
      //  SharedPrefHelper.getInstance(mvHubPlusApplication).saveKidsMode("false");
        if (BuildConfig.FLAVOR.equals("dev"))
            Branch.enableTestMode();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TrackerUtil.getInstance(getApplicationContext());
                FirebaseApp.initializeApp(getApplicationContext());
            }
        });

        Branch.getAutoInstance(this);
        Branch.enableDebugMode();
        //  setupBaseClient();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseCrashlyticSetup();
        setupMoEngage();
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        MoEInAppHelper.getInstance().onConfigurationChanged();
    }
    private void firebaseCrashlyticSetup() {

        if (KsPreferenceKeys.getInstance().getAppPrefLoginStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            String userId = KsPreferenceKeys.getInstance().getAppPrefUserId();
            FirebaseCrashlytics.getInstance().setUserId(userId);
        }
    }

    private void setupMoEngage() {
        MoEngageManager.INSTANCE.init(this);
        MoEFireBaseHelper.getInstance().addEventListener(MoEngageManager.INSTANCE);
//        addMoengageScreenExclusions(MvHubPlusApplication.this);
    }
    private void addMoengageScreenExclusions(Application application) {
        Set<Class<?>> inAppOptOut = new HashSet<>();
        inAppOptOut.add(ActivitySplash.class);

        MoEngage.Builder builder = new MoEngage.Builder(application, BuildConfig.MOENGAGE_APP_ID)
                .configureInApps(new InAppConfig(true, inAppOptOut));
        MoEngage.initialise(builder.build());
    }
    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        try {
            if (sTracker == null) {
                sTracker = TrackerUtil.getAnalyticsInstance().newTracker(R.xml.global_tracker);

//            sTracker = TrackerUtil.getAnalyticsInstance().newTracker(R.xml.global_tracker);
            }
        } catch (Exception ignored) {

        }

        return sTracker;
    }

    public int getVersion() {
        int v = 0;
        try {
            v = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // Huh? Really?
        }
        return v;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static MvHubPlusApplication getApplicationContext(Context context) {
        return (MvHubPlusApplication) context.getApplicationContext();
    }

    public EnveuComponent getEnveuComponent() {
        if (this.enveuComponent == null) {
            this.enveuComponent = DaggerEnveuComponent.builder()
                    .userPreferencesModule(new UserPreferencesModule(this))
                    .build();
        }
        return this.enveuComponent;
    }

    public String getVersionName() {
        String v = "";
        try {
            v = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // Huh? Really?
        }
        return v;
    }



}
