package me.vipa.app.adapters.commonRails;


import android.app.Activity;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.vipa.app.R;
import me.vipa.app.activities.listing.callback.ItemClickListener;
import me.vipa.app.activities.series.ui.SeriesDetailActivity;
import me.vipa.app.beanModel.responseModels.landingTabResponses.railData.ContentsItem;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import me.vipa.app.databinding.SquareListingItemBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.helpers.ImageHelper;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;


public class SquareListingAdapter extends RecyclerView.Adapter<SquareListingAdapter.SingleItemRowHolder> {

    private final String contentType;
    private final List<EnveuVideoItemBean> itemsList;
    private final Activity mContext;
    ItemClickListener listener;
    private long mLastClickTime = 0;

    public SquareListingAdapter(Activity context, List<EnveuVideoItemBean> itemsList, String contentType, ItemClickListener callBack) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.contentType = contentType;
        listener = callBack;
    }

    public void notifydata(List<EnveuVideoItemBean> i) {

        this.itemsList.addAll(i);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public SingleItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        SquareListingItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.square_listing_item, parent, false);
        return new SingleItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleItemRowHolder holder, int i) {
        if (itemsList.size() > 0) {
            EnveuVideoItemBean contentsItem = itemsList.get(i);
            if (contentsItem != null) {
                holder.squareItemBinding.setPlaylistItem(contentsItem);

                try {
                    AppCommonMethod.handleTags(itemsList.get(i).getIsVIP(),itemsList.get(i).getIsNewS(),
                            holder.squareItemBinding.flExclusive,holder.squareItemBinding.flNew,holder.squareItemBinding.flEpisode,holder.squareItemBinding.flNewMovie,itemsList.get(i).getAssetType());

                }catch (Exception ignored){

                }

//                if (contentsItem.isPremium()) {
//                    holder.squareItemBinding.flExclusive.setVisibility(View.VISIBLE);
//                } else {
//                    holder.squareItemBinding.flExclusive.setVisibility(View.GONE);
//                }
//
//                if (contentsItem.isNew()) {
//                    holder.squareItemBinding.flNew.setVisibility(View.VISIBLE);
//                } else {
//                    holder.squareItemBinding.flNew.setVisibility(View.GONE);
//                }

                holder.squareItemBinding.tvTitle.setText(itemsList.get(i).getTitle());
                if (contentType.equalsIgnoreCase(AppConstants.VOD)) {
                    holder.squareItemBinding.itemImage.setOnClickListener(view -> {
                        listener.onRowItemClicked(itemsList.get(i), i);
                    });

                    if (itemsList.get(i).getPosterURL() != null && !itemsList.get(i).getPosterURL().equalsIgnoreCase("")) {
                        ImageHelper.getInstance(mContext).loadImageTo(holder.squareItemBinding.itemImage, AppCommonMethod.getListSQRImage(itemsList.get(i).getPosterURL(), mContext));
                    }
                    //Glide.with(mContext).load(itemsList.get(i).getPosterURL()).into(holder.squareItemBinding.itemImage);
                } else if (contentType.equalsIgnoreCase(AppConstants.SERIES)) {
                    Glide.with(mContext).load(AppCommonMethod.getImageUrl(AppConstants.SERIES, "SQUARE") + itemsList.get(i).getPosterURL()).into(holder.squareItemBinding.itemImage);
                    holder.squareItemBinding.itemImage.setOnClickListener(view -> {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        ActivityLauncher.getInstance().seriesDetailScreen(mContext, SeriesDetailActivity.class, itemsList.get(i).getId());

                    });

                } else if (contentType.equalsIgnoreCase(AppConstants.CAST_AND_CREW)) {
                    Glide.with(mContext).load(AppCommonMethod.getImageUrl(AppConstants.CAST_AND_CREW, "SQUARE") + itemsList.get(i).getPosterURL()).into(holder.squareItemBinding.itemImage);

                } else if (contentType.equalsIgnoreCase(AppConstants.GENRE)) {
                    Glide.with(mContext).load(AppCommonMethod.getImageUrl(AppConstants.GENRE, "SQUARE") + itemsList.get(i).getPosterURL()).into(holder.squareItemBinding.itemImage);
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public interface SquareListingListener {
        void onItemClicked(ContentsItem itemValue);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        final SquareListingItemBinding squareItemBinding;

        SingleItemRowHolder(SquareListingItemBinding squareItemBind) {
            super(squareItemBind.getRoot());
            squareItemBinding = squareItemBind;

        }

    }


}

