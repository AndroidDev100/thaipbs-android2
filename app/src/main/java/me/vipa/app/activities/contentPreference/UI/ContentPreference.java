package me.vipa.app.activities.contentPreference.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;

import me.vipa.app.R;
import me.vipa.app.activities.contentPreference.adapter.ContentPreferenceAdapter;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.databinding.ActivityContentPreferenceBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.config.bean.PreferenceBean;
import me.vipa.app.utils.cropImage.helpers.NetworkConnectivity;


public class ContentPreference extends BaseBindingActivity<ActivityContentPreferenceBinding> {
    private ContentPreferenceAdapter adatperContentPreference;
    private static final int VERTICAL_ITEM_SPACE = 30;
    private static final int HORIONTAL_ITEM_SPACE = 20;
    private ArrayList<PreferenceBean> list;

    @Override
    public ActivityContentPreferenceBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivityContentPreferenceBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callBinding();
        connectObservors();
    }

    private void callBinding() {
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
        } else {
            noConnectionLayout();
        }
    }

    private void noConnectionLayout() {
        getBinding().noConnectionLayout.setVisibility(View.VISIBLE);
        getBinding().connection.retryTxt.setOnClickListener(view -> connectObservors());
    }

    private void setAdapter() {
            list = new ArrayList<>();
        for (int i = 0; i< AppCommonMethod.getConfigResponse().getData().getAppConfig().getContentPreference().size(); i ++){
            PreferenceBean preferenceBean = new PreferenceBean();
            preferenceBean.setIdentifier(AppCommonMethod.getConfigResponse().getData().getAppConfig().getContentPreference().get(i).getIdentifier());
            preferenceBean.setName(AppCommonMethod.getConfigResponse().getData().getAppConfig().getContentPreference().get(i).getName());
            preferenceBean.setChecked(false);
            list.add(preferenceBean);

        }


        adatperContentPreference = new ContentPreferenceAdapter(ContentPreference.this, list);
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
}