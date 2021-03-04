package me.vipa.app.activities.contentPreference.UI;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;

import me.vipa.app.R;
import me.vipa.app.SDKConfig;
import me.vipa.app.activities.contentPreference.adapter.ContentPreferenceAdapter;
import me.vipa.app.activities.usermanagment.ui.ChangePasswordActivity;
import me.vipa.app.activities.usermanagment.ui.SignUpThirdPage;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.callbacks.commonCallbacks.ContentPreferenceCallback;
import me.vipa.app.databinding.ActivityContentPreferenceBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.config.bean.PreferenceBean;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;


public class ContentPreference extends BaseBindingActivity<ActivityContentPreferenceBinding> implements ContentPreferenceCallback {
    private ContentPreferenceAdapter adatperContentPreference;
    private static final int VERTICAL_ITEM_SPACE = 30;
    private static final int HORIONTAL_ITEM_SPACE = 20;
    private ArrayList<PreferenceBean> list;
    private ArrayList<PreferenceBean> selectedList;
    private String contentPreference = "";
    private KsPreferenceKeys preference;
    private boolean isNotificationEnable = false;
    @Override
    public ActivityContentPreferenceBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivityContentPreferenceBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // isNotificationEnable = getIntent().getExtras().getBoolean("IsNotiEnabled");
        callBinding();
        connectObservors();
    }

    private void callBinding() {
        preference = KsPreferenceKeys.getInstance();
        getBinding().toolbar.backLayout.setVisibility(View.GONE);
        getBinding().toolbar.titleToolbar.setVisibility(View.GONE);
        getBinding().toolbar.titleSkip.setText(getResources().getString(R.string.skip));
    }

    private void connectObservors() {
        connectionValidation(NetworkConnectivity.isOnline(this));
    }

    private void connectionValidation(boolean aBoolean) {
        if (aBoolean) {
            getBinding().noConnectionLayout.setVisibility(View.GONE);

            uiInitialization();
            setAdapter();
            setClicks();
        } else {
            noConnectionLayout();
        }
    }

    private void setClicks() {
        selectedList = new ArrayList<>();
        getBinding().btnUpdatePreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(adatperContentPreference.getGenreList() != null && adatperContentPreference.getGenreList().size()>0){
//                    selectedList = adatperContentPreference.getGenreList();
//                }

              new ActivityLauncher(ContentPreference.this).signUpThird(ContentPreference.this, SignUpThirdPage.class,contentPreference,isNotificationEnable);
            }
        });

        getBinding().toolbar.titleSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentPreference = "";
                new ActivityLauncher(ContentPreference.this).signUpThird(ContentPreference.this, SignUpThirdPage.class,contentPreference,isNotificationEnable);
            }
        });
    }

    private void noConnectionLayout() {
        getBinding().noConnectionLayout.setVisibility(View.VISIBLE);
        getBinding().connection.retryTxt.setOnClickListener(view -> connectObservors());
    }

    private void setAdapter() {
            list = new ArrayList<>();
            if (SDKConfig.getInstance().getContentPreference()!=null) {
                for (int i = 0; i < SDKConfig.getInstance().getContentPreference().size(); i++) {
                    PreferenceBean preferenceBean = new PreferenceBean();
                    preferenceBean.setIdentifier(SDKConfig.getInstance().getContentPreference().get(i).getIdentifier());
                    preferenceBean.setName(SDKConfig.getInstance().getContentPreference().get(i).getName());
                    preferenceBean.setChecked(false);
                    list.add(preferenceBean);

                }
            }


        adatperContentPreference = new ContentPreferenceAdapter(ContentPreference.this, list, ContentPreference.this);
        getBinding().recyclerView.setAdapter(adatperContentPreference);
    }

    private void uiInitialization() {
        getBinding().recyclerView.hasFixedSize();
        getBinding().recyclerView.setNestedScrollingEnabled(false);
        getBinding().recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
//      staggeredGridLayoutManager = new StaggeredGridLayoutManager(3,1);
//      getBinding().recyclerview.setLayoutManager(staggeredGridLayoutManager);
        getBinding().recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        getBinding().recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(HORIONTAL_ITEM_SPACE));
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        flowLayoutManager.setAutoMeasureEnabled(true);
//
        getBinding().recyclerView.setLayoutManager(flowLayoutManager);

    }

    @Override
    public void onClick(ArrayList<PreferenceBean> arrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i<arrayList.size(); i ++){
            if (arrayList.get(i).getChecked()){
                stringBuilder.append(arrayList.get(i).getIdentifier()).append(", ");

            }
            if (stringBuilder.length() > 0) {
                contentPreference = stringBuilder.toString();
                contentPreference = contentPreference.substring(0, contentPreference.length() - 2);
            } else {
                contentPreference = "";
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preference.getAppPrefRegisterStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            onBackPressed();
        }
    }

}