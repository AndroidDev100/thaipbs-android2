package me.vipa.app.adapters.commonRails;

import android.app.Activity;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import me.vipa.app.activities.listing.callback.ItemClickListener;
import me.vipa.app.activities.series.ui.SeriesDetailActivity;
import me.vipa.app.beanModel.ContinueRailModel.CommonContinueRail;
import me.vipa.app.beanModel.responseModels.series.season.ItemsItem;
import me.vipa.app.R;
import me.vipa.app.activities.detail.ui.DetailActivity;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import me.vipa.app.databinding.PosterPotraitItemBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.helpers.ImageHelper;

import me.vipa.app.utils.helpers.StringUtils;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import java.util.ArrayList;
import java.util.List;

import me.vipa.app.activities.detail.ui.DetailActivity;
import me.vipa.app.activities.listing.callback.ItemClickListener;
import me.vipa.app.activities.series.ui.SeriesDetailActivity;
import me.vipa.app.beanModel.ContinueRailModel.CommonContinueRail;
import me.vipa.app.beanModel.responseModels.series.season.ItemsItem;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;

public class CommonPosterPotraitAdapter extends RecyclerView.Adapter<CommonPosterPotraitAdapter.SingleItemRowHolder> {

    private static List<EnveuVideoItemBean> itemsList;
    private final List<ItemsItem> seasonItems;
    private final String contentType;
    private final Activity mContext;
    private long mLastClickTime = 0;
    private int itemWidth;
    private int itemHeight;
    private ArrayList<CommonContinueRail> continuelist;
    private String isLogin;
    private KsPreferenceKeys preference;
    private ItemClickListener listener;

    public CommonPosterPotraitAdapter(Activity context, List<EnveuVideoItemBean> itemsList, List<ItemsItem> itemsItems, String contentType, ArrayList<CommonContinueRail> continuelist, ItemClickListener callBack) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.seasonItems = itemsItems;
        this.contentType = contentType;
        this.continuelist = continuelist;
        this.listener = callBack;
        preference = KsPreferenceKeys.getInstance();
        isLogin = preference.getAppPrefLoginStatus();
        int num = 3;
        boolean tabletSize = mContext.getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            if (mContext.getResources().getConfiguration().orientation == 2)
                num = 5;
            else
                num = 4;
        }
        DisplayMetrics displaymetrics = new DisplayMetrics();
        (mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //if you need three fix imageview in width
        itemWidth = (displaymetrics.widthPixels) / num;
        itemHeight = (int) (itemWidth * 3) / 2;
    }

    public void notifydata(List<EnveuVideoItemBean> i) {
        this.itemsList.addAll(i);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommonPosterPotraitAdapter.SingleItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        PosterPotraitItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.poster_potrait_item, parent, false);
        return new CommonPosterPotraitAdapter.SingleItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonPosterPotraitAdapter.SingleItemRowHolder holder, int i) {

        // holder.itemBinding.itemImage.getLayoutParams().width = itemWidth;
        // holder.itemBinding.itemImage.getLayoutParams().height = itemHeight;
        holder.itemBinding.llContinueProgress.setVisibility(View.GONE);
        holder.itemBinding.ivContinuePlay.setVisibility(View.GONE);

        if (itemsList.size() > 0) {
            EnveuVideoItemBean contentsItem = itemsList.get(i);
            if (contentsItem != null) {

                //setContinueWatching(holder.itemBinding.pbProcessing,14180,i*1500);

//                if (contentsItem.isPremium()) {
//                    holder.itemBinding.flExclusive.setVisibility(View.VISIBLE);
//                } else {
//                    holder.itemBinding.flExclusive.setVisibility(View.GONE);
//                }
//
//                if (contentsItem.isNew()) {
//                    holder.itemBinding.flNew.setVisibility(View.VISIBLE);
//                } else {
//                    holder.itemBinding.flNew.setVisibility(View.GONE);
//                }
                holder.itemBinding.tvTitle.setText(itemsList.get(i).getTitle());
                if (contentType.equalsIgnoreCase(AppConstants.VOD)) {

                    if (itemsList.get(i).getPosterURL() != null && !itemsList.get(i).getPosterURL().equalsIgnoreCase("")) {
                        ImageHelper.getInstance(mContext).loadImageTo(holder.itemBinding.itemImage, AppCommonMethod.getListCIRCLEImage(itemsList.get(i).getPosterURL(), mContext));
                    }
                    /*ImageHelper.getInstance(holder.itemBinding.itemImage.getContext())
                            .loadImageTo(holder.itemBinding.itemImage, itemsList.get(i).getPosterURL());
*/
                    holder.itemBinding.itemImage.setOnClickListener(view -> {
                        listener.onRowItemClicked(itemsList.get(i), i);
                    });

                } else if (contentType.equalsIgnoreCase(AppConstants.SERIES)) {
                    holder.itemBinding.itemImage.setOnClickListener(view -> {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        new ActivityLauncher(mContext).seriesDetailScreen(mContext, SeriesDetailActivity.class, itemsList.get(i).getId());

                    });

                    ImageHelper.getInstance(holder.itemBinding.itemImage.getContext())
                            .loadImageTo(holder.itemBinding.itemImage, AppCommonMethod.getImageUrl(AppConstants.SERIES, "POTRIAT") + itemsList.get(i).getPosterURL());


                } else if (contentType.equalsIgnoreCase(AppConstants.CAST_AND_CREW)) {

                    ImageHelper.getInstance(holder.itemBinding.itemImage.getContext())
                            .loadImageTo(holder.itemBinding.itemImage, AppCommonMethod.getImageUrl(AppConstants.CAST_AND_CREW, "POTRIAT") + itemsList.get(i).getPosterURL());

                } else if (contentType.equalsIgnoreCase(AppConstants.GENRE)) {

                    ImageHelper.getInstance(holder.itemBinding.itemImage.getContext())
                            .loadImageTo(holder.itemBinding.itemImage, AppCommonMethod.getImageUrl(AppConstants.GENRE, "POTRIAT") + seasonItems.get(i).getLandscapeImage());

                }


            }
        } else if (seasonItems.size() > 0) {

            ItemsItem sItem = seasonItems.get(i);
            if (sItem.isPremium()) {
                holder.itemBinding.flExclusive.setVisibility(View.VISIBLE);
            } else {
                holder.itemBinding.flExclusive.setVisibility(View.GONE);
            }

            if (sItem.isNew()) {
                holder.itemBinding.flNew.setVisibility(View.VISIBLE);
            } else {
                holder.itemBinding.flNew.setVisibility(View.GONE);
            }

            ImageHelper.getInstance(holder.itemBinding.itemImage.getContext())
                    .loadImageTo(holder.itemBinding.itemImage, AppCommonMethod.urlPoints + AppConstants.FILTER + AppConstants.QUALITY + "(100):format(webP):maxbytes(800)" + AppConstants.VIDEO_IMAGE_BASE_KEY + seasonItems.get(i).getLandscapeImage());


            holder.itemBinding.itemImage.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (!StringUtils.isNullOrEmptyOrZero(sItem.getVideoType())) {

                    if (sItem.getVideoType().equalsIgnoreCase("EPISODE")) {
                  //      new ActivityLauncher(mContext).episodeScreen(mContext, EpisodeActivity.class, sItem.getId(), "", sItem.isPremium());
                    } else {
                        new ActivityLauncher(mContext).detailScreen(mContext, DetailActivity.class, sItem.getId(), "", sItem.isPremium());
                    }
                }
                //  new ActivityLauncher(mContext).detailScreen(mContext, DetailActivity.class, sItem.getId(), "", sItem.isPremium());

            });
        } else if (continuelist.size() > 0) {
            if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                holder.itemBinding.llContinueProgress.setVisibility(View.VISIBLE);
                holder.itemBinding.ivContinuePlay.setVisibility(View.VISIBLE);
                holder.itemBinding.flNew.setVisibility(View.GONE);

//                if (continuelist.get(i).getUserAssetDetail().isPremium()) {
//                    holder.itemBinding.flExclusive.setVisibility(View.VISIBLE);
//                } else {
//                    holder.itemBinding.flExclusive.setVisibility(View.GONE);
//                }

                ImageHelper.getInstance(holder.itemBinding.itemImage.getContext())
                        .loadImageTo(holder.itemBinding.itemImage, AppCommonMethod.getImageUrl(AppConstants.VOD, "POTRIAT") + continuelist.get(i).getUserAssetDetail().getPosterPortraitImage());


                holder.itemBinding.itemImage.setOnClickListener(v -> {
                    if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                        if (continuelist.size() > 0 && (continuelist.get(i).getUserAssetDetail().getAssetType()) != null) {
                           /* if (continuelist.get(i).getUserAssetDetail().getAssetType().equalsIgnoreCase("EPISODE")) {
                                AppCommonMethod.launchDetailScreen(mContext,0l,AppConstants.Episode, continuelist.get(i).getUserAssetDetail().getId(), String.valueOf(continuelist.get(i).getUserAssetStatus().getPosition()), continuelist.get(i).getUserAssetDetail().isPremium());
                            } else {
                                AppCommonMethod.launchDetailScreen(mContext,0l,AppConstants.Video, continuelist.get(i).getUserAssetDetail().getId(), String.valueOf(continuelist.get(i).getUserAssetStatus().getPosition()), continuelist.get(i).getUserAssetDetail().isPremium());
                            }*/
                            AppCommonMethod.launchDetailScreen(mContext,0l,continuelist.get(i).getUserAssetDetail().getAssetType(), continuelist.get(i).getUserAssetDetail().getId(), String.valueOf(continuelist.get(i).getUserAssetStatus().getPosition()), continuelist.get(i).getUserAssetDetail().isPremium());
                        }
                    }
                });

            } else {
                holder.itemBinding.llContinueProgress.setVisibility(View.GONE);
                holder.itemBinding.ivContinuePlay.setVisibility(View.GONE);
            }
        }

    }


    @Override
    public int getItemCount() {
        int itemCount = 0;
        if (itemsList.size() > 0) {
            itemCount = itemsList.size();
        }/* else if (seasonItems.size() > 0) {
            itemCount = seasonItems.size();
        }*/ else if (continuelist != null && continuelist.size() > 0) {
            itemCount = continuelist.size();
        }
        return itemCount;
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        PosterPotraitItemBinding itemBinding;

        SingleItemRowHolder(PosterPotraitItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
    }

}


