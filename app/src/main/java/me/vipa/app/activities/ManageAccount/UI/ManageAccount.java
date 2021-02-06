package me.vipa.app.activities.ManageAccount.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import me.vipa.app.R;
import me.vipa.app.activities.profile.ui.ProfileActivityNew;
import me.vipa.app.activities.settings.ActivitySettings;
import me.vipa.app.activities.settings.downloadsettings.DownloadSettings;
import me.vipa.app.activities.usermanagment.ui.ChangePasswordActivity;
import me.vipa.app.activities.videoquality.ui.ChangeLanguageActivity;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.databinding.ActivityManageAccountBinding;
import me.vipa.app.databinding.SettingsActivityBinding;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;

public class ManageAccount extends BaseBindingActivity<ActivityManageAccountBinding> {


    @Override
    public ActivityManageAccountBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivityManageAccountBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolBar();
        getBinding().myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActivityLauncher(ManageAccount.this).ProfileActivityNew(ManageAccount.this, ProfileActivityNew.class);
            }
        });

        getBinding().changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActivityLauncher(ManageAccount.this).changePassword(ManageAccount.this, ChangePasswordActivity.class);
            }
        });
    }

    private void toolBar() {
        getBinding().toolbar.llSearchIcon.setVisibility(View.GONE);
        getBinding().toolbar.backLayout.setVisibility(View.VISIBLE);
        getBinding().toolbar.homeIcon.setVisibility(View.GONE);
        getBinding().toolbar.titleText.setVisibility(View.VISIBLE);
        getBinding().toolbar.mediaRouteButton.setVisibility(View.GONE);
        getBinding().toolbar.screenText.setText(getResources().getString(R.string.manage_account));

        getBinding().toolbar.backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

}