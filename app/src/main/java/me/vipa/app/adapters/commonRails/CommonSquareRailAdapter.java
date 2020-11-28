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
import me.vipa.app.databinding.SquareItemBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.helpers.ImageHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import me.vipa.app.callbacks.commonCallbacks.CommonRailtItemClickListner;

public class CommonSquareRailAdapter extends RecyclerView.Adapter<CommonSquareRailAdapter.CustomViewHolder> {

    private long mLastClickTime = 0;
    private RailCommonData railCommonData;
    private List<EnveuVideoItemBean> videos;
    private CommonRailtItemClickListner listner;
    private Context mContext;

    public CommonSquareRailAdapter(Context context, RailCommonData railCommonData, CommonRailtItemClickListner listner) {
        this.mContext = context;
        this.railCommonData = railCommonData;
        this.videos = new ArrayList<>();
        this.videos = railCommonData.getEnveuVideoItemBeans();
        this.listner = listner;
    }


    @NonNull
    @Override
    public CommonSquareRailAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        SquareItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.square_item, parent, false);
        return new CustomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonSquareRailAdapter.CustomViewHolder holder, int i) {
        SquareItemBinding itemBinding = holder.itemBinding;

        itemBinding.setPlaylistItem(videos.get(i));
        itemBinding.rippleView.setOnClickListener(v -> {
            itemClick(i);
        });
        try {
            AppCommonMethod.handleTags(videos.get(i).getIsVIP(),videos.get(i).getIsNewS(),
                    holder.itemBinding.flExclusive,holder.itemBinding.flNew,holder.itemBinding.flEpisode,holder.itemBinding.flNewMovie,videos.get(i).getAssetType());

        }catch (Exception ignored){
            Logger.w("crashonTags",ignored.toString());
        }
        try {
            if (videos.get(i).getPosterURL() != null && !videos.get(i).getPosterURL().equalsIgnoreCase("")) {
                ImageHelper.getInstance(mContext).loadListSQRImage(holder.itemBinding.itemImage, AppCommonMethod.getListSQRImage(videos.get(i).getPosterURL(), mContext));
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
        SquareItemBinding itemBinding;

        CustomViewHolder(SquareItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

    }


}
