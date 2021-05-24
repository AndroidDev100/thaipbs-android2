 package me.vipa.app.activities.homeactivity.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import me.vipa.app.activities.usermanagment.ui.LoginActivity;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.R;
import me.vipa.app.callbacks.commonCallbacks.FragmentClickNetwork;
import me.vipa.app.callbacks.commonCallbacks.HomeClickNetwork;
import me.vipa.app.callbacks.commonCallbacks.OriginalFragmentClick;
import me.vipa.app.callbacks.commonCallbacks.PremiumClick;
import me.vipa.app.callbacks.commonCallbacks.SinetronClick;
import me.vipa.app.databinding.ActivityMainBinding;
import me.vipa.app.fragments.home.ui.HomeFragment;
import me.vipa.app.fragments.more.ui.MoreFragment;
import me.vipa.app.fragments.news.ui.NewsFragment;
import me.vipa.app.fragments.shows.ui.ShowsFragment;
import me.vipa.app.fragments.movies.ui.MovieFragment;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.helpers.ActivityTrackers;
import me.vipa.app.utils.helpers.AnalyticsController;

import me.vipa.app.utils.helpers.SharedPrefHelper;
import me.vipa.app.utils.helpers.StringUtils;
import me.vipa.app.utils.helpers.ToolBarHandler;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;
import me.vipa.app.utils.inAppUpdate.AppUpdateCallBack;
import me.vipa.app.utils.inAppUpdate.ApplicationUpdateManager;

import org.jetbrains.annotations.NotNull;

import me.vipa.app.activities.usermanagment.ui.LoginActivity;
import me.vipa.app.baseModels.BaseBindingActivity;


public class HomeActivity extends BaseBindingActivity<ActivityMainBinding> implements MoreFragment.MoreFragmentInteraction, AppUpdateCallBack {

    public Fragment active;
    private KsPreferenceKeys preference;
    private Fragment homeFragment;
    private Fragment originalFragment;
    private Fragment premiumFragment;
    private Fragment sinetronFragment;
    private Fragment moreFragment;
    private FragmentManager fragmentManager;


    private FragmentClickNetwork updatfrag;
    private HomeClickNetwork homeClickNetwork;
    private OriginalFragmentClick originalFragmentClick;
    private PremiumClick premiumClick;
    private SinetronClick sinetronClick;
    private String strCurrentTheme = "";
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (active instanceof HomeFragment)
                        return true;
                    switchToHomeFragment();
                    return true;
                case R.id.navigation_originals:

                    if (active instanceof NewsFragment)
                        return true;

                    if (originalFragment == null) {
                        originalFragment = new NewsFragment();
                        fragmentManager.beginTransaction().add(R.id.content_frame, originalFragment, "3").hide(originalFragment).commit();
                        switchToOriginalFragment();
                    } else {
                        switchToOriginalFragment();
                        try {
                            if (originalFragmentClick != null)
                                originalFragmentClick.updatefrag(true);
                        } catch (Exception e) {
                        }

                    }
                    return true;
                case R.id.navigation_premium:

                    if (active instanceof ShowsFragment)
                        return true;
                    if (premiumFragment == null) {
                        premiumFragment = new ShowsFragment();
                        fragmentManager.beginTransaction().add(R.id.content_frame, premiumFragment, "2").hide(premiumFragment).commit();
                        switchToPremiumFragment();

                    } else {
                        switchToPremiumFragment();
                        if (premiumClick != null)
                            premiumClick.updatefrag(true);
                    }

                    return true;
                case R.id.navigation_sinetron:
                    if (active instanceof MovieFragment)
                        return true;

                    if (sinetronFragment == null) {
                        sinetronFragment = new MovieFragment();
                        fragmentManager.beginTransaction().add(R.id.content_frame, sinetronFragment, "4").hide(sinetronFragment).commit();
                        switchToSinetronFragment();
                    } else {
                        switchToSinetronFragment();
                        if (sinetronClick != null)
                            sinetronClick.updatefrag(true);
                    }
                    return true;
                case R.id.navigation_more:

                    if (moreFragment == null) {
                        moreFragment = new MoreFragment();
                        fragmentManager.beginTransaction().add(R.id.content_frame, moreFragment, "5").hide(moreFragment).commit();
                        switchToMoreFragment();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((MoreFragment)moreFragment).clickEvent();
                            }
                        },200);

                    } else {
                        ((MoreFragment)moreFragment).clickEvent();
                        switchToMoreFragment();
                        if (updatfrag != null)
                            updatfrag.updatefrag(true);

                    }
                    return true;
            }
            return false;
        }
    };
    private BottomNavigationView navigation;
    private boolean kidsMode=false;

    @SuppressLint("RestrictedApi")
    public static void removeNavigationShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        menuView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        menuView.buildMenuView();
    }

    public void updateHome(HomeClickNetwork clickNetwork) {
        this.homeClickNetwork = clickNetwork;
    }

    public void updateApi(FragmentClickNetwork listener) {

        this.updatfrag = listener;
    }

    public void updateOriginal(OriginalFragmentClick listener) {
        this.originalFragmentClick = listener;
    }

    public void updatePremium(PremiumClick listener) {
        this.premiumClick = listener;
    }

    public void updateSinetron(SinetronClick listener) {
        this.sinetronClick = listener;
    }

    @Override
    public ActivityMainBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivityMainBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setupCrashlytics();

        strCurrentTheme = KsPreferenceKeys.getInstance().getCurrentTheme();


        //kidsMode  = new SharedPrefHelper(HomeActivity.this).getKidsMode();
        if (getIntent().getExtras() != null) {
            kidsMode = getIntent().getExtras().getBoolean( AppConstants.KIDS_MODE);

        }
        Logger.d("CurrentThemeIs",strCurrentTheme);
        if (KsPreferenceKeys.getInstance().getCurrentTheme().equalsIgnoreCase(AppConstants.LIGHT_THEME)) {
            setTheme(R.style.MyMaterialTheme_Base_Light);
        } else {
            setTheme(R.style.MyMaterialTheme_Base_Dark);

        }




        preference = KsPreferenceKeys.getInstance();
        callBinding();
        ApplicationUpdateManager.getInstance(getApplicationContext()).setAppUpdateCallBack(this);
        // Before starting an update, register a listener for updates.

        ApplicationUpdateManager.getInstance(getApplicationContext()).getAppUpdateManager().registerListener(listener);

        ApplicationUpdateManager.getInstance(getApplicationContext()).isUpdateAvailable();

        // Checks that the update is not stalled during 'onResume()'.
        // However, you should execute this check at all entry points into the app.
        ApplicationUpdateManager.getInstance(getApplicationContext()).getAppUpdateManager().getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {

            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                // If the update is downloaded but not installed,
                // notify the user to complete the update.

                popupSnackbarForCompleteUpdate();


                // When status updates are no longer needed, unregister the listener.
                ApplicationUpdateManager.getInstance(getApplicationContext()).getAppUpdateManager().unregisterListener(listener);
            }
        });

        new AnalyticsController(HomeActivity.this).callAnalytics("home_activity", "Action", "Launch");
    }

    private void setupCrashlytics() {
        throw new RuntimeException("Test crash");
    }

    private void callBinding() {
        callIntials();
    }

    public void callIntials() {
        if (StringUtils.isNullOrEmptyOrZero(AppCommonMethod.urlPoints)) {
            AppCommonMethod.urlPoints = preference.getAppPrefCfep();
        }

        initialFragment();
     // changeFragment(new HomeFragment(), "HomeFragment");
        getBinding().navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        new ToolBarHandler(HomeActivity.this).setHomeAction(getBinding().toolbar, HomeActivity.this);


    }

    public void switchToHomeFragment() {
        getBinding().toolbar.llSearchIcon.setVisibility(View.VISIBLE);
        fragmentManager.beginTransaction().hide(active).show(homeFragment).commit();
        active = homeFragment;
        ((HomeFragment)homeFragment).updateList();
        ((HomeFragment)homeFragment).updateAdList();
    }

    public void switchToOriginalFragment() {
        getBinding().toolbar.llSearchIcon.setVisibility(View.VISIBLE);
        fragmentManager.beginTransaction().hide(active).show(originalFragment).commit();
        active = originalFragment;


    }

    public void switchToMoreFragment() {
        getBinding().toolbar.llSearchIcon.setVisibility(View.INVISIBLE);
        Bundle bundle = new Bundle();
        bundle.putBoolean( AppConstants.KIDS_MODE,kidsMode);
        moreFragment.setArguments(bundle);
        fragmentManager.beginTransaction().hide(active).show(moreFragment).commit();
        active = moreFragment;


    }

    public void switchToPremiumFragment() {
        getBinding().toolbar.llSearchIcon.setVisibility(View.VISIBLE);
        fragmentManager.beginTransaction().hide(active).show(premiumFragment).commit();
        active = premiumFragment;

    }

    public void switchToSinetronFragment() {
        getBinding().toolbar.llSearchIcon.setVisibility(View.VISIBLE);
        fragmentManager.beginTransaction().hide(active).show(sinetronFragment).commit();
        active = sinetronFragment;

    }

    private void initialFragment() {
        homeFragment = new HomeFragment();
        active = homeFragment;
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_frame, homeFragment, "1").hide(homeFragment).commitAllowingStateLoss();
        fragmentManager.beginTransaction().hide(active).show(homeFragment).commitAllowingStateLoss();
        UIinitialization();
    }

    private void UIinitialization() {
        navigation = findViewById(R.id.navigation);
        if(kidsMode){
            getBinding().toolbar.rlToolBar.setBackgroundColor(HomeActivity.this.getResources().getColor(R.color.blue));
            getBinding().toolbar.homeIcon.setBackground(HomeActivity.this.getResources().getDrawable(R.drawable.vipa_logo_kids));
            getBinding().toolbar.homeIcon.getLayoutParams().height = 100;
            getBinding().toolbar.homeIcon.requestLayout();

            navigation.getMenu().findItem(R.id.navigation_originals).setVisible(false);
            navigation.getMenu().findItem(R.id.navigation_premium).setVisible(false);
            navigation.getMenu().findItem(R.id.navigation_sinetron).setVisible(false);
        }
        else {
            getBinding().toolbar.rlToolBar.setBackgroundColor(HomeActivity.this.getResources().getColor(R.color.black));
            getBinding().toolbar.homeIcon.setBackground(HomeActivity.this.getResources().getDrawable(R.drawable.vipalogo180));
            getBinding().toolbar.homeIcon.getLayoutParams().height = 80;
            getBinding().toolbar.homeIcon.requestLayout();
            navigation.getMenu().findItem(R.id.navigation_originals).setVisible(true);
            navigation.getMenu().findItem(R.id.navigation_premium).setVisible(true);
            navigation.getMenu().findItem(R.id.navigation_sinetron).setVisible(true);
        }

        removeNavigationShiftMode(navigation);
        if (active instanceof HomeFragment) {
            navigation.getMenu().findItem(R.id.navigation_home).setChecked(true);
            navigation.setSelectedItemId(R.id.navigation_home);
            navigation.setSelected(true);
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume","onResume");
        AppCommonMethod.resetFilter(HomeActivity.this);
        if (preference == null)
            preference = KsPreferenceKeys.getInstance();

        String isLogin = preference.getAppPrefLoginStatus();
        if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("PAUSE","PAUSE");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("onStop","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy","onDestroy");
        if (preference.getAppPrefIsRestoreState()) {
            preference.setAppPrefIsRestoreState(false);
        }

    }


    @Override
    protected void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (preference == null)
            preference = KsPreferenceKeys.getInstance();

        preference.setAppPrefIsRestoreState(true);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        callBinding();

        //  new ToastHandler(HomeActivity.this).show("onRestoreInstanceState");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onLoginClicked() {

       // Blurry.with(HomeActivity.this).radius(25).sampling(2).onto(getBinding().blurredBackgroundImageView);
        ActivityTrackers.getInstance().setAction("");
        new ActivityLauncher(this).loginActivity(this, LoginActivity.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            ((HomeFragment)homeFragment).updateAdList();
        }catch (Exception e){

        }
    }

    InstallStateUpdatedListener listener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            popupSnackbarForCompleteUpdate();
        }
    };

    private AppUpdateInfo appUpdateInfo;
    @Override
    public void getAppUpdateCallBack(AppUpdateInfo appUpdateInfo) {

        this.appUpdateInfo = appUpdateInfo;
        if (appUpdateInfo != null) {

            ApplicationUpdateManager.getInstance(getApplicationContext()).startUpdate(appUpdateInfo, AppUpdateType.FLEXIBLE, this, ApplicationUpdateManager.APP_UPDATE_REQUEST_CODE);
        } else {
            Logger.w("InApp update", "NoUpdate available");
        }
    }

    /* Displays the snackbar notification and call to action. */
    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(getBinding().blurredBackgroundImageView, getResources().getString(R.string.update_has_downloaded), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getResources().getString(R.string.restart), view -> ApplicationUpdateManager.getInstance(getApplicationContext()).getAppUpdateManager().completeUpdate());
        snackbar.setActionTextColor(getResources().getColor(R.color.moretitlecolor));
        snackbar.show();
    }



}

