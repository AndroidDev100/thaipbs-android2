package com.mvhub.mvhubplus.activities.videoquality.adapter;


import android.app.Activity;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvhub.mvhubplus.MvHubPlusApplication;
import com.mvhub.mvhubplus.activities.videoquality.callBack.NotificationItemClickListner;
import com.mvhub.mvhubplus.R;
import com.mvhub.mvhubplus.activities.videoquality.bean.TrackItem;
import com.mvhub.mvhubplus.databinding.VideoQualityItemBinding;
import com.mvhub.mvhubplus.utils.commonMethods.AppCommonMethod;
import com.mvhub.mvhubplus.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import java.util.List;


public class VideoQualityAdapter extends RecyclerView.Adapter<VideoQualityAdapter.SingleItemRowHolder> {

    private final List<TrackItem> inboxMessages;
    private final NotificationItemClickListner itemClickListener;
    //   = KsPreferenceKeys(ApplicationMain.getAppContext()).getQualityPosition();
    private int pos;

    public VideoQualityAdapter(Activity activity, List<TrackItem> itemsList, NotificationItemClickListner listener) {
        this.inboxMessages = itemsList;
        this.itemClickListener = listener;
        pos = KsPreferenceKeys.getInstance().getQualityPosition();
        if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Thai") || KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("हिंदी") ){
            AppCommonMethod.updateLanguage("th", MvHubPlusApplication.getInstance());
        } else if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")){
            AppCommonMethod.updateLanguage("en", MvHubPlusApplication.getInstance());
        }

    }

    @NonNull
    @Override
    public SingleItemRowHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        VideoQualityItemBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.getContext()),
                R.layout.video_quality_item, viewGroup, false);

        return new SingleItemRowHolder(itemBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull final SingleItemRowHolder viewHolder, final int position) {

        if (pos == position) {
            viewHolder.notificationItemBinding.rightIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.notificationItemBinding.rightIcon.setVisibility(View.GONE);
        }

        viewHolder.notificationItemBinding.titleText.setText(inboxMessages.get(position).getTrackName());
        viewHolder.notificationItemBinding.secondTitleText.setVisibility(View.GONE);

        viewHolder.notificationItemBinding.parentLayout.setOnClickListener(view -> {

            pos = position;
            KsPreferenceKeys.getInstance().setQualityPosition(pos);
            KsPreferenceKeys.getInstance().setQualityName(inboxMessages.get(position).getUniqueId());
            itemClickListener.onClick("", "");

        });
    }


    @Override
    public int getItemCount() {
        return inboxMessages.size();
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        final VideoQualityItemBinding notificationItemBinding;

        SingleItemRowHolder(VideoQualityItemBinding binding) {
            super(binding.getRoot());
            this.notificationItemBinding = binding;

        }

    }


}
