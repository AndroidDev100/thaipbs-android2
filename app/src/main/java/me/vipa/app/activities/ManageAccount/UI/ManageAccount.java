package me.vipa.app.activities.ManageAccount.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import me.vipa.app.R;
import me.vipa.app.activities.profile.ui.ProfileActivityNew;
import me.vipa.app.activities.usermanagment.ui.ChangePasswordActivity;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.databinding.ActivityManageAccountBinding;
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
                ActivityLauncher.getInstance().ProfileActivityNew(ManageAccount.this, ProfileActivityNew.class);
            }
        });

        getBinding().changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.getInstance().changePassword(ManageAccount.this, ChangePasswordActivity.class);
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