package me.vipa.app.activities.videoquality.adapter;


import android.app.Activity;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.vipa.app.MvHubPlusApplication;
import me.vipa.app.activities.videoquality.callBack.NotificationItemClickListner;
import me.vipa.app.R;
import me.vipa.app.activities.videoquality.bean.TrackItem;
import me.vipa.app.databinding.VideoQualityItemBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import java.util.List;

import me.vipa.app.activities.videoquality.callBack.NotificationItemClickListner;


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
        viewHolder.notificationItemBinding.secondTitleText.setText(inboxMessages.get(position).getDescription());

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
