package me.vipa.app.activities.videoquality.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import me.vipa.app.activities.videoquality.adapter.ChangeLanguageAdapter;
import me.vipa.app.activities.videoquality.bean.LanguageItem;
import me.vipa.app.activities.videoquality.callBack.ItemClick;
import me.vipa.app.activities.videoquality.viewModel.VideoQualityViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.R;
import me.vipa.app.activities.homeactivity.ui.HomeActivity;
import me.vipa.app.databinding.VideoQualityActivityBinding;
import me.vipa.app.fragments.dialog.ChangeLanguageDialog;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.helpers.NetworkConnectivity;

import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import java.util.ArrayList;

import me.vipa.app.activities.videoquality.adapter.ChangeLanguageAdapter;
import me.vipa.app.activities.videoquality.bean.LanguageItem;
import me.vipa.app.activities.videoquality.callBack.ItemClick;
import me.vipa.app.activities.videoquality.viewModel.VideoQualityViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;

public class ChangeLanguageActivity extends BaseBindingActivity<VideoQualityActivityBinding> implements ItemClick, ChangeLanguageDialog.AlertDialogListener {
    private VideoQualityViewModel viewModel;
    private ChangeLanguageAdapter notificationAdapter;

    @Override
    public VideoQualityActivityBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return VideoQualityActivityBinding.inflate(inflater);
    }

    private KsPreferenceKeys preference;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preference = KsPreferenceKeys.getInstance();
        Configuration config = new Configuration(getResources().getConfiguration());
        Logger.e("Locale", config.locale.getDisplayLanguage());
        toolBar();
        callModel();
        connectionObserver();

    }

    private void toolBar() {
        getBinding().toolbar.mediaRouteButton.setVisibility(View.GONE);
        getBinding().toolbar.llSearchIcon.setVisibility(View.GONE);
        getBinding().toolbar.backLayout.setVisibility(View.VISIBLE);
        getBinding().toolbar.homeIcon.setVisibility(View.GONE);
        getBinding().toolbar.titleText.setVisibility(View.VISIBLE);

        getBinding().toolbar.screenText.setText(getResources().getString(R.string.change_language));

        getBinding().toolbar.backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void callModel() {
        viewModel = ViewModelProviders.of(this).get(VideoQualityViewModel.class);
    }

    private void connectionObserver() {

        if (NetworkConnectivity.isOnline(this)) {
            connectionValidation(true);
        } else {
            connectionValidation(false);
        }
    }

    private ArrayList<LanguageItem> arrayList;

    private void connectionValidation(Boolean aBoolean) {
        if (aBoolean) {
            getBinding().noConnectionLayout.setVisibility(View.GONE);
            ArrayList<LanguageItem> trackItems = new ArrayList<>();
            Logger.e("LanguageList", getString(R.string.language_english));
            for (int i = 0; i < 3; i++) {
                if (i == 1) {
                    LanguageItem languageItem = new LanguageItem();
                    languageItem.setLanguageName(getString(R.string.language_english_title));
                    languageItem.setDefaultLangageName(getString(R.string.language_english));
                    trackItems.add(languageItem);
                } else if (i == 0) {
                    LanguageItem languageItem = new LanguageItem();
                    languageItem.setLanguageName(getString(R.string.language_thai_title));
                    languageItem.setDefaultLangageName(getString(R.string.language_thai));
                    trackItems.add(languageItem);
                }
            }
            arrayList = trackItems;
            Logger.e("LanguageList", new Gson().toJson(arrayList));

            uiInitialization();
            setAdapter();

        } else {
            noConnectionLayout();
        }


    }

    private void setAdapter() {
        notificationAdapter = new ChangeLanguageAdapter(ChangeLanguageActivity.this, arrayList, ChangeLanguageActivity.this);
        getBinding().recyclerview.setAdapter(notificationAdapter);
    }

    private void uiInitialization() {
        getBinding().recyclerview.hasFixedSize();
        getBinding().recyclerview.setNestedScrollingEnabled(false);
        getBinding().recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void noConnectionLayout() {
        getBinding().noConnectionLayout.setVisibility(View.VISIBLE);
        // getBinding().connection.closeButton.setOnClickListener(view -> onBackPressed());
        getBinding().connection.retryTxt.setOnClickListener(view -> connectionObserver());
    }

    private void showDialog(String title, String message) {
        FragmentManager fm = getSupportFragmentManager();
        ChangeLanguageDialog alertDialog = ChangeLanguageDialog.newInstance(title, message, getResources().getString(R.string.ok));
        alertDialog.setCancelable(false);
        alertDialog.setAlertDialogCallBack(this);
        alertDialog.show(fm, "fragment_alert");
    }

    String lanName;
    int langPos;

    @Override
    public void itemClicked(String name, int position) {
        if (position==0){
            lanName="Thai";
        }else if (position==1){
            lanName="English";
        }
        langPos = position;
        if (preference.getAppLanguage().equalsIgnoreCase(getString(R.string.language_thai_title))) {
            AppCommonMethod.updateLanguage("th", ChangeLanguageActivity.this);
        } else if (preference.getAppLanguage().equalsIgnoreCase(getString(R.string.language_english_title))) {
            AppCommonMethod.updateLanguage("en", ChangeLanguageActivity.this);
        }
        showDialog("", ChangeLanguageActivity.this.getResources().getString(R.string.change_language_message));
    }

    @Override
    public void onFinishDialog(boolean click) {
        if (click) {
            preference.setAppLanguage(lanName);
            preference.setAppPrefLanguagePos(langPos);
            if (preference.getAppLanguage().equalsIgnoreCase(getString(R.string.language_thai_title))) {
                AppCommonMethod.updateLanguage("th", ChangeLanguageActivity.this);
            } else if (preference.getAppLanguage().equalsIgnoreCase(getString(R.string.language_english_title))) {
                AppCommonMethod.updateLanguage("en", ChangeLanguageActivity.this);
            }
            new ActivityLauncher(this).homeScreen(this, HomeActivity.class);

        } else {

        }
    }
}