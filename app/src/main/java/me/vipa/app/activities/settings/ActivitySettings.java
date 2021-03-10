package me.vipa.app.activities.settings;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;

import me.vipa.app.BuildConfig;
import me.vipa.app.SDKConfig;
import me.vipa.app.activities.contentPreference.UI.ContentPreference;
import me.vipa.app.activities.contentPreference.UI.SettingContentPreferences;
import me.vipa.app.activities.settings.downloadsettings.DownloadSettings;
import me.vipa.app.activities.splash.ui.ActivitySplash;
import me.vipa.app.activities.videoquality.ui.ChangeLanguageActivity;
import me.vipa.app.activities.videoquality.ui.VideoQualityActivity;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.R;
import me.vipa.app.databinding.SettingsActivityBinding;
import me.vipa.app.utils.constants.AppConstants;

import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;
import me.vipa.app.activities.settings.downloadsettings.DownloadSettings;
import me.vipa.app.activities.videoquality.ui.ChangeLanguageActivity;
import me.vipa.app.activities.videoquality.ui.VideoQualityActivity;
import me.vipa.app.baseModels.BaseBindingActivity;


public class ActivitySettings extends BaseBindingActivity<SettingsActivityBinding> implements View.OnClickListener {
    private boolean isNotificationEnable;

    @Override
    public SettingsActivityBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return SettingsActivityBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (KsPreferenceKeys.getInstance().getCurrentTheme().equalsIgnoreCase(AppConstants.LIGHT_THEME)) {
            setTheme(R.style.MyMaterialTheme_Base_Light);

            getBinding().switchTheme.setChecked(false);
        } else {
            setTheme(R.style.MyMaterialTheme_Base_Dark);
            getBinding().switchTheme.setChecked(true);
        }

        try {
            boolean isTablet = ActivitySettings.this.getResources().getBoolean(R.bool.isTablet);
           // getBinding().buildNumber.setVisibility(View.GONE);
            if (!isTablet) {
                if (BuildConfig.FLAVOR.equalsIgnoreCase("dev")) {
                    getBinding().buildNumber.setText(ActivitySettings.this.getResources().getString(R.string.app_name) + "-QA" +"(" +BuildConfig.VERSION_NAME+")");
                }else {
                    getBinding().buildNumber.setText(ActivitySettings.this.getResources().getString(R.string.app_name) + "("+BuildConfig.VERSION_NAME+")");
                }
            }
        }catch (Exception ignored){

        }

        isNotificationEnable = areNotificationsEnabled();
        setSwitchForNotification();
        setSwitchForBingeWatch();
        toolBar();
        checkLanguage();
        if (KsPreferenceKeys.getInstance().getAppPrefLoginStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            if (SDKConfig.getInstance().isDownloadEnable()) {
                getBinding().downloadLayout.setVisibility(View.VISIBLE);
            } else {
                getBinding().downloadLayout.setVisibility(View.GONE);
            }
        }else {
            getBinding().downloadLayout.setVisibility(View.GONE);
        }
        if (KsPreferenceKeys.getInstance().getAppPrefLoginStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString())){
            getBinding().contentPreLayout.setVisibility(View.VISIBLE);
        }else {
            getBinding().contentPreLayout.setVisibility(View.GONE);
        }
        getBinding().downloadLayout.setOnClickListener(this);
        getBinding().contentPreLayout.setOnClickListener(this);
        getBinding().parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivitySettings.this, ChangeLanguageActivity.class);
                startActivity(intent);
            }
        });

        getBinding().videoQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivitySettings.this, VideoQualityActivity.class);
                startActivity(intent);
            }
        });

        getBinding().switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (getBinding().switchTheme.isChecked()) {


                    //  getBinding().switchTheme.setChecked(false);

                    KsPreferenceKeys.getInstance().setCurrentTheme(AppConstants.DARK_THEME);
                } else {
                    //    getBinding().switchTheme.setChecked(true);
                    KsPreferenceKeys.getInstance().setCurrentTheme(AppConstants.LIGHT_THEME);
                }

                recreate();
            }
        });

        getBinding().bingeSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {




                if (isChecked){
                    KsPreferenceKeys.getInstance().setBingeWatchEnable(true);
                    setSwitchForBingeWatch();
                }else {
                    KsPreferenceKeys.getInstance().setBingeWatchEnable(false);
                    setSwitchForBingeWatch();
                }


            }
        });




        getBinding().switchTheme.setOnClickListener(v -> {


        });


        getBinding().notificationSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    Intent intent = new Intent();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getApplicationContext().getPackageName());
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.putExtra("app_package", getApplicationContext().getPackageName());
                        intent.putExtra("app_uid", getApplicationContext().getApplicationInfo().uid);
                    } else {
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                    }
                    ActivitySettings.this.startActivity(intent);
                }

            }

        });
    }

    private void setSwitchForBingeWatch() {
        if (KsPreferenceKeys.getInstance().getBingeWatchEnable()){
            getBinding().bingeSetting.setChecked(true);

        }else {
            getBinding().bingeSetting.setChecked(false);

        }
    }

    private void setSwitchForNotification() {
        if (isNotificationEnable){
            getBinding().notificationSetting.setChecked(true);
        }else {
            getBinding().notificationSetting.setChecked(false);
        }

    }

    public boolean areNotificationsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (!manager.areNotificationsEnabled()) {
                return false;
            }
            List<NotificationChannel> channels = manager.getNotificationChannels();
            for (NotificationChannel channel : channels) {
                if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                    return false;
                }
            }
            return true;
        } else {
            return NotificationManagerCompat.from(getApplicationContext()).areNotificationsEnabled();
        }
    }

    private void toolBar() {
        getBinding().toolbar.llSearchIcon.setVisibility(View.GONE);
        getBinding().toolbar.backLayout.setVisibility(View.VISIBLE);
        getBinding().toolbar.homeIcon.setVisibility(View.GONE);
        getBinding().toolbar.titleText.setVisibility(View.VISIBLE);
        getBinding().toolbar.mediaRouteButton.setVisibility(View.GONE);
        getBinding().toolbar.screenText.setText(getResources().getString(R.string.setting_title));

        getBinding().toolbar.backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void checkLanguage() {

        KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
        String currentLanguage = preference.getAppLanguage();
        if (currentLanguage.isEmpty()) {
            getBinding().languageText.setText("English");
        } else {
            if (currentLanguage.equalsIgnoreCase("English")) {
                getBinding().languageText.setText(currentLanguage);

            } else {
                getBinding().languageText.setText(getString(R.string.language_thai));
            }
        }
        String qualityName = preference.getQualityName();
        setQualityText(qualityName);

    }

    private void setQualityText(String qualityName) {
        if (qualityName.isEmpty()) {
            getBinding().qualityText.setText(getString(R.string.auto));

        } else {
            if (qualityName.equalsIgnoreCase("Auto")){
                getBinding().qualityText.setText(getString(R.string.auto));
            }else if (qualityName.equalsIgnoreCase("Low")){
                getBinding().qualityText.setText(getString(R.string.low));
            }
            else if (qualityName.equalsIgnoreCase("Medium")){
                getBinding().qualityText.setText(getString(R.string.medium));
            }
            else if (qualityName.equalsIgnoreCase("High")){
                getBinding().qualityText.setText(getString(R.string.high));
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getBinding() != null) {
            setQualityText(KsPreferenceKeys.getInstance().getQualityName());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNotificationEnable = areNotificationsEnabled();
        setSwitchForNotification();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.downloadLayout: {
                Intent intent = new Intent(ActivitySettings.this, DownloadSettings.class);
                startActivity(intent);
            }
            break;
            case R.id.contentPreLayout: {
                Log.w("savedata3","-->>click");
                Intent intent = new Intent(ActivitySettings.this, SettingContentPreferences.class);
                startActivity(intent);
            }
            break;
            case R.id.parent_layout: {
                Intent intent = new Intent(ActivitySettings.this, ChangeLanguageActivity.class);
                startActivity(intent);
            }
            break;
        }
    }

//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        getBinding().notificationSetting.setOnCheckedChangeListener(null);
//        getBinding().bingeSetting.setOnCheckedChangeListener(null);
//        Intent intent = new Intent();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
//            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getApplicationContext().getPackageName());
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
//            intent.putExtra("app_package", getApplicationContext().getPackageName());
//            intent.putExtra("app_uid", getApplicationContext().getApplicationInfo().uid);
//        } else {
//            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            intent.addCategory(Intent.CATEGORY_DEFAULT);
//            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
//        }
//        getApplicationContext().startActivity(intent);
//    }
}
