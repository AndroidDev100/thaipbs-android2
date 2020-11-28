package me.vipa.app.adapters.commonRails;


import android.app.Activity;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import me.vipa.app.activities.detail.ui.EpisodeActivity;
import me.vipa.app.activities.listing.callback.ItemClickListener;
import me.vipa.app.activities.series.ui.SeriesDetailActivity;
import me.vipa.app.beanModel.responseModels.landingTabResponses.railData.ContentsItem;
import me.vipa.app.beanModel.responseModels.series.season.ItemsItem;
import me.vipa.app.R;
import me.vipa.app.activities.detail.ui.DetailActivity;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import me.vipa.app.databinding.LandscapeListingItemBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.PrintLogging;
import me.vipa.app.utils.helpers.ImageHelper;
import me.vipa.app.utils.helpers.StringUtils;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;

import java.util.List;

import me.vipa.app.activities.detail.ui.DetailActivity;
import me.vipa.app.activities.detail.ui.EpisodeActivity;
import me.vipa.app.activities.listing.callback.ItemClickListener;
import me.vipa.app.activities.series.ui.SeriesDetailActivity;
import me.vipa.app.beanModel.responseModels.landingTabResponses.railData.ContentsItem;
import me.vipa.app.beanModel.responseModels.series.season.ItemsItem;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;

public class LandscapeListingAdapter extends RecyclerView.Adapter<LandscapeListingAdapter.SingleItemRowHolder> {

    final List<ItemsItem> itemsItems;
    final String contentType;
    private final List<EnveuVideoItemBean> itemsList;
    private final Activity mContext;
    boolean tabletSize;
    private long mLastClickTime = 0;
    private int itemWidth;
    private int itemHeight;
    private ItemClickListener listener;

    public LandscapeListingAdapter(Activity context, List<EnveuVideoItemBean> itemsList, List<ItemsItem> itemsItems, String contentType, ItemClickListener callBack) {
        this.itemsList = itemsList;
        this.itemsItems = itemsItems;
        this.mContext = context;
        this.contentType = contentType;
        this.listener = callBack;
        int num = 2;
        tabletSize = mContext.getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            //landscape
            if (mContext.getResources().getConfiguration().orientation == 2)
                num = 4;
            else
                num = 3;
        }
        DisplayMetrics displaymetrics = new DisplayMetrics();
        (mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //if you need three fix imageview in width
        itemWidth = (displaymetrics.widthPixels - 80) / num;
        itemHeight = (int) (itemWidth * 9) / 16;
    }

    public void notifydata(List<EnveuVideoItemBean> i) {

        this.itemsList.addAll(i);
        notifyDataSetChanged();

    }

    public void notifyEpisodedata(List<ItemsItem> i) {

        itemsItems.addAll(i);
        notifyDataSetChanged();

    }

    //LandscapeListingItemBinding
    @NonNull
    @Override
    public SingleItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LandscapeListingItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.landscape_listing_item, parent, false);
        return new SingleItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleItemRowHolder holder, int i) {
        PrintLogging.printLog("", "onBindViewHolder" + "onBindViewHolder");
        if (tabletSize) {
            holder.landscapeItemBinding.itemImage.getLayoutParams().width = itemWidth;
            holder.landscapeItemBinding.itemImage.getLayoutParams().height = itemHeight;

        }
        if (itemsList.size() > 0) {
            PrintLogging.printLog("", "onBindViewHolder" + itemsList.get(i).getPosterURL());

            try {
                AppCommonMethod.handleTags(itemsList.get(i).getIsVIP(),itemsList.get(i).getIsNewS(),
                        holder.landscapeItemBinding.flExclusive,holder.landscapeItemBinding.flNew,holder.landscapeItemBinding.flEpisode,holder.landscapeItemBinding.flNewMovie,itemsList.get(i).getAssetType());

            }catch (Exception ignored){

            }

            EnveuVideoItemBean contentsItem = itemsList.get(i);
            if (contentsItem != null) {

                holder.landscapeItemBinding.setPlaylistItem(contentsItem);
//                if (contentsItem.isPremium()) {
//                    holder.landscapeItemBinding.flExclusive.setVisibility(View.VISIBLE);
//                } else {
//                    holder.landscapeItemBinding.flExclusive.setVisibility(View.GONE);
//                }
//
//                if (contentsItem.isNew()) {
//                    holder.landscapeItemBinding.flNew.setVisibility(View.VISIBLE);
//                } else {
//                    holder.landscapeItemBinding.flNew.setVisibility(View.GONE);
//                }

                holder.landscapeItemBinding.tvTitle.setText(itemsList.get(i).getTitle());
                if (contentType.equalsIgnoreCase(AppConstants.VOD)) {
                    // Glide.with(mContext).load(itemsList.get(i).getPosterURL()).into(holder.landscapeItemBinding.itemImage);
                    if (itemsList.get(i).getPosterURL() != null && !itemsList.get(i).getPosterURL().equalsIgnoreCase("")) {
                        ImageHelper.getInstance(mContext).loadListSQRImage(holder.landscapeItemBinding.itemImage, AppCommonMethod.getListLDSImage(itemsList.get(i).getPosterURL(), mContext));
                    }
                    holder.landscapeItemBinding.itemImage.setOnClickListener(view -> {
                        listener.onRowItemClicked(itemsList.get(i), i);
                       /* if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        try {
                            if ((itemsList.get(i).getAssetType()) != null) {
                                if (itemsList.get(i).getAssetType().equalsIgnoreCase("EPISODE")) {
                                    new ActivityLauncher(mContext).episodeScreen(mContext, EpisodeActivity.class, itemsList.get(i).getId(), "0", contentsItem.isPremium());
                                } else {
                                    new ActivityLauncher(mContext).detailScreen(mContext, DetailActivity.class, itemsList.get(i).getId(), "0", contentsItem.isPremium());
                                }
                            }
                        } catch (Exception e) {
                            new ActivityLauncher(mContext).detailScreen(mContext, DetailActivity.class, itemsList.get(i).getId(), "0", contentsItem.isPremium());
                        }*/

                    });

                } else if (contentType.equalsIgnoreCase(AppConstants.SERIES)) {
                    Glide.with(mContext).load(AppCommonMethod.getImageUrl(AppConstants.SERIES, "LANDSCAPE") + itemsList.get(i).getPosterURL()).into(holder.landscapeItemBinding.itemImage);
                    holder.landscapeItemBinding.itemImage.setOnClickListener(view -> {

                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        new ActivityLauncher(mContext).seriesDetailScreen(mContext, SeriesDetailActivity.class, itemsList.get(i).getId());

                    });

                } else if (contentType.equalsIgnoreCase(AppConstants.CAST_AND_CREW)) {
                    Glide.with(mContext).load(AppCommonMethod.getImageUrl(AppConstants.CAST_AND_CREW, "LANDSCAPE") + itemsList.get(i).getPosterURL()).into(holder.landscapeItemBinding.itemImage);

                }
            } else if (contentType.equalsIgnoreCase(AppConstants.GENRE)) {
                Glide.with(mContext).load(AppCommonMethod.getImageUrl(AppConstants.GENRE, "LANDSCAPE") + itemsList.get(i).getPosterURL()).into(holder.landscapeItemBinding.itemImage);
            }
        } else {
            ItemsItem sItem = itemsItems.get(i);
            Glide.with(mContext).load(AppCommonMethod.urlPoints + AppConstants.FILTER + AppConstants.QUALITY + "(100):format(webP):maxbytes(800)" + AppConstants.VIDEO_IMAGE_BASE_KEY + itemsItems.get(i).getLandscapeImage()).into(holder.landscapeItemBinding.itemImage);
            holder.landscapeItemBinding.itemImage.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (!StringUtils.isNullOrEmptyOrZero(sItem.getVideoType())) {
                    if (sItem.getVideoType().equalsIgnoreCase("EPISODE")) {
                        new ActivityLauncher(mContext).episodeScreen(mContext, EpisodeActivity.class, sItem.getId(), "", sItem.isPremium());
                    } else {
                        new ActivityLauncher(mContext).detailScreen(mContext, DetailActivity.class, sItem.getId(), "", sItem.isPremium());
                    }
                }


            });
        }
    }


    @Override
    public int getItemCount() {
        int itemCount = 0;
        if (itemsList.size() > 0) {
            itemCount = itemsList.size();
        } else if (itemsItems.size() > 0) {
            itemCount = itemsItems.size();
        }
        return itemCount;
    }

    public interface LandscapeListingListener {
        void onItemClicked(ContentsItem itemValue);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        public final LandscapeListingItemBinding landscapeItemBinding;

        public SingleItemRowHolder(LandscapeListingItemBinding flightItemLayoutBinding) {
            super(flightItemLayoutBinding.getRoot());
            landscapeItemBinding = flightItemLayoutBinding;
        }

    }
}

