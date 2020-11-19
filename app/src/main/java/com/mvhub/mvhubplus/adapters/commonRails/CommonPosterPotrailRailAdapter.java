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
import com.mvhub.mvhubplus.databinding.PosterPotraitItemBinding;
import com.mvhub.mvhubplus.utils.commonMethods.AppCommonMethod;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class CommonPosterPotrailRailAdapter extends RecyclerView.Adapter<CommonPosterPotrailRailAdapter.SingleItemRowHolder> {

    private long mLastClickTime = 0;
    private RailCommonData railCommonData;
    private List<EnveuVideoItemBean> videos;
    private CommonRailtItemClickListner listner;
    private Context mContext;

    public CommonPosterPotrailRailAdapter(Context context, RailCommonData railCommonData, CommonRailtItemClickListner listner) {
        this.mContext = context;
        this.railCommonData = railCommonData;
        this.videos = new ArrayList<>();
        this.videos = railCommonData.getEnveuVideoItemBeans();
        this.listner = listner;
    }


    @NonNull
    @Override
    public SingleItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        PosterPotraitItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.poster_potrait_item, parent, false);
        return new SingleItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonPosterPotrailRailAdapter.SingleItemRowHolder holder, int i) {
        PosterPotraitItemBinding itemBinding = holder.circularItemBinding;
        itemBinding.setPlaylistItem(videos.get(i));
        itemBinding.rippleView.setOnClickListener(v -> {
            itemClick(i);

        });
        try {
            AppCommonMethod.handleTags(videos.get(i).getIsVIP(),videos.get(i).getIsNewS(),
                    itemBinding.flExclusive,itemBinding.flNew,itemBinding.flEpisode,itemBinding.flNewMovie,videos.get(i).getAssetType());

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

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        final PosterPotraitItemBinding circularItemBinding;

        SingleItemRowHolder(PosterPotraitItemBinding circularItemBind) {
            super(circularItemBind.getRoot());
            this.circularItemBinding = circularItemBind;

        }

    }


}
