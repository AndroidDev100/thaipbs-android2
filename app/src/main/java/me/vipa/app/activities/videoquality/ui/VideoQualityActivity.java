package me.vipa.app.activities.videoquality.ui;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.vipa.app.R;
import me.vipa.app.activities.videoquality.adapter.VideoQualityAdapter;
import me.vipa.app.activities.videoquality.bean.TrackItem;
import me.vipa.app.activities.videoquality.callBack.NotificationItemClickListner;
import me.vipa.app.activities.videoquality.viewModel.VideoQualityViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;
import com.vipa.app.databinding.VideoQualityActivityBinding;
import me.vipa.app.utils.helpers.NetworkConnectivity;

import java.util.ArrayList;

import me.vipa.app.activities.videoquality.adapter.VideoQualityAdapter;
import me.vipa.app.activities.videoquality.bean.TrackItem;
import me.vipa.app.activities.videoquality.callBack.NotificationItemClickListner;
import me.vipa.app.activities.videoquality.viewModel.VideoQualityViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;


public class VideoQualityActivity extends BaseBindingActivity<VideoQualityActivityBinding> implements NotificationItemClickListner {
    private VideoQualityViewModel viewModel;
    private VideoQualityAdapter notificationAdapter;

    @Override
    public VideoQualityActivityBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return VideoQualityActivityBinding.inflate(inflater);


    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callModel();
        connectionObserver();
        toolBar();
        //new ToolBarHandler(this).setVideoQuality(getBinding(),"Streaming Settings");

    }
    private void toolBar() {
        getBinding().toolbar.mediaRouteButton.setVisibility(View.GONE);
        getBinding().toolbar.llSearchIcon.setVisibility(View.GONE);
        getBinding().toolbar.backLayout.setVisibility(View.VISIBLE);
        getBinding().toolbar.homeIcon.setVisibility(View.GONE);
        getBinding().toolbar.titleText.setVisibility(View.VISIBLE);

        getBinding().toolbar.screenText.setText(getResources().getString(R.string.streaming_settings));

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

    private ArrayList<TrackItem> arrayList;

    private void connectionValidation(Boolean aBoolean) {
        if (aBoolean) {
            getBinding().noConnectionLayout.setVisibility(View.GONE);
            arrayList = viewModel.getQualityList(getResources());
            uiInitialization();
            setAdapter();

        } else {
            noConnectionLayout();
        }


    }

    private void setAdapter() {
        notificationAdapter = new VideoQualityAdapter(VideoQualityActivity.this, arrayList, VideoQualityActivity.this);
        getBinding().recyclerview.setAdapter(notificationAdapter);
    }

    private void uiInitialization() {
        getBinding().recyclerview.hasFixedSize();
        getBinding().recyclerview.setNestedScrollingEnabled(false);
        getBinding().recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    private void noConnectionLayout() {
        getBinding().noConnectionLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(String id, String status) {
        notificationAdapter.notifyDataSetChanged();
    }
}