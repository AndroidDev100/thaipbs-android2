package com.mvhub.mvhubplus.adapters.commonRails;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mvhub.mvhubplus.beanModel.enveuCommonRailData.RailCommonData;
import com.mvhub.mvhubplus.beanModel.responseModels.landingTabResponses.railData.ContentsItem;
import com.mvhub.mvhubplus.callbacks.commonCallbacks.CommonRailtItemClickListner;
import com.mvhub.mvhubplus.R;
import com.mvhub.mvhubplus.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import com.mvhub.mvhubplus.databinding.CircleItemBinding;
import com.mvhub.mvhubplus.utils.commonMethods.AppCommonMethod;
import com.mvhub.mvhubplus.utils.helpers.ImageHelper;

import java.util.ArrayList;
import java.util.List;

public class CommonCircleRailAdapter extends RecyclerView.Adapter<CommonCircleRailAdapter.SingleItemRowHolder> {

    private long mLastClickTime = 0;
    private RailCommonData railCommonData;
    private List<EnveuVideoItemBean> videos;
    private CommonRailtItemClickListner listner;
    private Context mContext;

    public CommonCircleRailAdapter(Context context, RailCommonData railCommonData, CommonRailtItemClickListner listner) {
        this.mContext = context;
        this.railCommonData = railCommonData;
        this.videos = new ArrayList<>();
        this.videos = railCommonData.getEnveuVideoItemBeans();
        this.listner = listner;
    }

    public void notifydata(List<ContentsItem> i) {

        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public SingleItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        CircleItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.circle_item, parent, false);
        return new SingleItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonCircleRailAdapter.SingleItemRowHolder holder, int i) {
        CircleItemBinding itemBinding = holder.circularItemBinding;
        itemBinding.setPlaylistItem(videos.get(i));
        itemBinding.rippleView.setOnClickListener(v -> {
            itemClick(i);

        });

        AppCommonMethod.railBadgeVisibility(itemBinding.flNew, videos.get(i).isNew());
        AppCommonMethod.railBadgeVisibility(itemBinding.flExclusive, videos.get(i).isPremium());

        try {
            if (videos.get(i).getPosterURL() != null && !videos.get(i).getPosterURL().equalsIgnoreCase("")) {
                ImageHelper.getInstance(mContext).loadCircleImageTo(itemBinding.itemImage, AppCommonMethod.getListCIRCLEImage(videos.get(i).getPosterURL(), mContext));
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

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        final CircleItemBinding circularItemBinding;

        SingleItemRowHolder(CircleItemBinding circularItemBind) {
            super(circularItemBind.getRoot());
            this.circularItemBinding = circularItemBind;
        }

    }


}
