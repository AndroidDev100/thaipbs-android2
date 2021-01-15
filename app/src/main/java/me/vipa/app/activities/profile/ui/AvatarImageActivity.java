package me.vipa.app.activities.profile.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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
import me.vipa.app.activities.usermanagment.ui.SignUpThirdPage;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.databinding.ActivityAvatarImageBinding;
import me.vipa.app.databinding.ActivityContentPreferenceBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.config.bean.PreferenceBean;
import me.vipa.app.utils.cropImage.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.GridSpacingItemDecoration;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;

public class AvatarImageActivity extends BaseBindingActivity<ActivityAvatarImageBinding> {
    private AvatarAdapter avatarAdapter;
    private static final int VERTICAL_ITEM_SPACE = 30;
    private static final int HORIONTAL_ITEM_SPACE = 20;
    private ArrayList<PreferenceBean> list;
    private ArrayList<PreferenceBean> selectedList;

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
        selectedList = new ArrayList<>();
        getBinding().btnUpdatePreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(adatperContentPreference.getGenreList() != null && adatperContentPreference.getGenreList().size()>0){
//                    selectedList = adatperContentPreference.getGenreList();
//                }

              //  new ActivityLauncher(ContentPreference.this).signUpThird(ContentPreference.this, SignUpThirdPage.class);
            }
        });

    }




    private void setAdapter() {

        Log.d("asesededde",new Gson().toJson(AppCommonMethod.getConfigResponse().getData().getAppConfig().getavatarImages()));
        avatarAdapter = new AvatarAdapter(AvatarImageActivity.this, AppCommonMethod.getConfigResponse().getData().getAppConfig().getavatarImages());
        getBinding().recyclerView.setAdapter(avatarAdapter);
    }

    private void uiInitialization() {
//        getBinding().recyclerView.hasFixedSize();
//        getBinding().recyclerView.setNestedScrollingEnabled(false);
//        getBinding().recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
////      staggeredGridLayoutManager = new StaggeredGridLayoutManager(3,1);
////      getBinding().recyclerview.setLayoutManager(staggeredGridLayoutManager);
//        getBinding().recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
//        getBinding().recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(HORIONTAL_ITEM_SPACE));
//        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
//        flowLayoutManager.setAutoMeasureEnabled(true);
////
//        getBinding().recyclerView.setLayoutManager(flowLayoutManager);
        getBinding().recyclerView.hasFixedSize();
        getBinding().recyclerView.setNestedScrollingEnabled(false);
        getBinding().recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 6, true));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(AvatarImageActivity.this, 3);

        getBinding().recyclerView.setLayoutManager(gridLayoutManager);


//        final GridLayoutManager layoutManager = new GridLayoutManager(this,3);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        getBinding().recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setAdapter(adapter);

//        getBinding().recyclerView.hasFixedSize();
//        getBinding().recyclerView.setNestedScrollingEnabled(false);
//        getBinding().recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
     //   getBinding().recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 5, true));


    }
}