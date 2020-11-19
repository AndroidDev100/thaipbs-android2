package com.mvhub.mvhubplus.adapters.commonRails;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mvhub.mvhubplus.beanModel.enveuCommonRailData.RailCommonData;
import com.mvhub.mvhubplus.callbacks.commonCallbacks.CommonRailtItemClickListner;
import com.mvhub.mvhubplus.R;
import com.mvhub.mvhubplus.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import com.mvhub.mvhubplus.databinding.LandscapeItemBinding;
import com.mvhub.mvhubplus.utils.commonMethods.AppCommonMethod;
import com.mvhub.mvhubplus.utils.helpers.ImageHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class CommonLandscapeRailAdapter extends RecyclerView.Adapter<CommonLandscapeRailAdapter.CustomViewHolder> {

    private long mLastClickTime = 0;
    private RailCommonData railCommonData;
    private List<EnveuVideoItemBean> videos;
    private CommonRailtItemClickListner listner;
    private Context mContext;

    public CommonLandscapeRailAdapter(Context context, RailCommonData railCommonData, CommonRailtItemClickListner listner) {
        this.mContext = context;
        this.railCommonData = railCommonData;
        this.videos = new ArrayList<>();
        this.videos = railCommonData.getEnveuVideoItemBeans();
        this.listner = listner;
    }


    @NonNull
    @Override
    public CommonLandscapeRailAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LandscapeItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.landscape_item, parent, false);
        return new CommonLandscapeRailAdapter.CustomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonLandscapeRailAdapter.CustomViewHolder holder, int i) {
        LandscapeItemBinding itemBinding = holder.itemBinding;

        itemBinding.setPlaylistItem(videos.get(i));
        itemBinding.rippleView.setOnClickListener(v -> {
            itemClick(i);

        });

        try {
            AppCommonMethod.handleTags(videos.get(i).getIsVIP(),videos.get(i).getIsNewS(),
                    holder.itemBinding.flExclusive,holder.itemBinding.flNew,holder.itemBinding.flEpisode,holder.itemBinding.flNewMovie,videos.get(i).getAssetType());

        }catch (Exception ignored){

        }
        if (videos.get(i).getVideoPosition() > 0) {
            AppCommonMethod.railBadgeVisibility(itemBinding.llContinueProgress, true);
            double totalDuration = videos.get(i).getDuration();
            double currentPosition = videos.get(i).getVideoPosition() * 1000;
            double percentagePlayed = ((currentPosition / totalDuration) * 100L);
            itemBinding.setProgress((int) percentagePlayed);
        } else {
            AppCommonMethod.railBadgeVisibility(itemBinding.llContinueProgress, false);
        }

        try {
            if (videos.get(i).getPosterURL() != null && !videos.get(i).getPosterURL().equalsIgnoreCase("")) {
                ImageHelper.getInstance(mContext).loadListImage(holder.itemBinding.itemImage, AppCommonMethod.getListLDSImage(videos.get(i).getPosterURL(), mContext));
            }
        }catch (Exception ignored){

        }
    }

    public void itemClick(int position) {

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        listner.railItemClick(railCommonData, position);

    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        LandscapeItemBinding itemBinding;

        CustomViewHolder(LandscapeItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

    }


}
