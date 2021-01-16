package me.vipa.app.activities.profile.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.Gson;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;

import me.vipa.app.R;

import me.vipa.app.activities.contentPreference.adapter.ContentPreferenceAdapter;
import me.vipa.app.activities.listing.ui.GridActivity;
import me.vipa.app.activities.profile.adapter.AvatarAdapter;
import me.vipa.app.activities.usermanagment.ui.LoginActivity;
import me.vipa.app.activities.usermanagment.ui.SignUpActivity;
import me.vipa.app.activities.usermanagment.ui.SignUpThirdPage;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.callbacks.commonCallbacks.AvatarImageCallback;
import me.vipa.app.databinding.ActivityAvatarImageBinding;
import me.vipa.app.databinding.ActivityContentPreferenceBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.config.bean.PreferenceBean;
import me.vipa.app.utils.cropImage.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.GridSpacingItemDecoration;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;

public class AvatarImageActivity extends BaseBindingActivity<ActivityAvatarImageBinding> implements AvatarImageCallback {
    private AvatarAdapter avatarAdapter;
    private ArrayList<PreferenceBean> list;

    @Override
    public ActivityAvatarImageBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivityAvatarImageBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callBinding();
        connectObservors();
    }

    private void callBinding() {
        getBinding().toolbar.backLayout.setVisibility(View.VISIBLE);
        getBinding().toolbar.titleToolbar.setVisibility(View.GONE);
        getBinding().toolbar.titleSkip.setVisibility(View.GONE);
        getBinding().toolbar.backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void connectObservors() {
        uiInitialization();
        setAdapter();
        setClicks();
    }


    private void setClicks() {
        getBinding().btnUpdatePreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppCommonMethod.Url != ""){
//                    profileNew
                    onBackPressed();
                }
            }
        });

    }




    private void setAdapter() {
        avatarAdapter = new AvatarAdapter(AvatarImageActivity.this, AppCommonMethod.getConfigResponse().getData().getAppConfig().getavatarImages(), AvatarImageActivity.this);
        getBinding().recyclerView.setAdapter(avatarAdapter);
    }

    private void uiInitialization() {

        getBinding().recyclerView.hasFixedSize();
        getBinding().recyclerView.setNestedScrollingEnabled(false);
        getBinding().recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 6, true));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(AvatarImageActivity.this, 3);

        getBinding().recyclerView.setLayoutManager(gridLayoutManager);


    }

    @Override
    public void onClick(String url) {
        AppCommonMethod.Url = url;
    }
}