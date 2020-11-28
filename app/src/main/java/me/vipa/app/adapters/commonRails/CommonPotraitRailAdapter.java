package me.vipa.app.adapters.commonRails;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;
import me.vipa.app.callbacks.commonCallbacks.CommonRailtItemClickListner;
import me.vipa.app.R;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import me.vipa.app.databinding.PotraitItemBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.helpers.ImageHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import me.vipa.app.callbacks.commonCallbacks.CommonRailtItemClickListner;

public class CommonPotraitRailAdapter extends RecyclerView.Adapter<CommonPotraitRailAdapter.CustomViewHolder> {

    private long mLastClickTime = 0;
    private RailCommonData railCommonData;
    private List<EnveuVideoItemBean> videos;
    private CommonRailtItemClickListner listner;
    private Context mContext;

    public CommonPotraitRailAdapter(Context context, RailCommonData railCommonData, CommonRailtItemClickListner listner) {
        this.mContext = context;
        this.railCommonData = railCommonData;
        this.videos = new ArrayList<>();
        this.videos = railCommonData.getEnveuVideoItemBeans();
        this.listner = listner;
    }


    @NonNull
    @Override
    public CommonPotraitRailAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        PotraitItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.potrait_item, parent, false);
        return new CommonPotraitRailAdapter.CustomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonPotraitRailAdapter.CustomViewHolder holder, int i) {
        PotraitItemBinding itemBinding = holder.itemBinding;
        EnveuVideoItemBean enveuVideoItemBean=videos.get(i);
       // Logger.w("posterValue-->>",videos.get(i).getPosterURL());
        itemBinding.setPlaylistItem(videos.get(i));
        itemBinding.rippleView.setOnClickListener(v -> {
            itemClick(i);

        });

        try {
            AppCommonMethod.handleTags(enveuVideoItemBean.getIsVIP(),enveuVideoItemBean.getIsNewS(),
                    holder.itemBinding.flExclusive,holder.itemBinding.flNew,holder.itemBinding.flEpisode,holder.itemBinding.flNewMovie,enveuVideoItemBean.getAssetType());

        }catch (Exception ignored){

        }
        //AppCommonMethod.railBadgeVisibility(itemBinding.flNew, videos.get(i).isNew());
//        AppCommonMethod.railBadgeVisibility(itemBinding.flExclusive, videos.get(i).isPremium());
        try {
            if (enveuVideoItemBean.getPosterURL() != null && !enveuVideoItemBean.getPosterURL().equalsIgnoreCase("")) {
                ImageHelper.getInstance(mContext).loadImageTo(holder.itemBinding.itemImage, AppCommonMethod.getListPRImage(enveuVideoItemBean.getPosterURL(), mContext));
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
        PotraitItemBinding itemBinding;

        CustomViewHolder(PotraitItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

    }


}
