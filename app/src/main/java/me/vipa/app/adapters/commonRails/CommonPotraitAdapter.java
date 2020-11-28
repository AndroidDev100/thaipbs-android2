package me.vipa.app.adapters.commonRails;


import android.app.Activity;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import me.vipa.app.activities.listing.callback.ItemClickListener;
import me.vipa.app.activities.series.ui.SeriesDetailActivity;
import me.vipa.app.beanModel.ContinueRailModel.CommonContinueRail;
import com.vipa.app.R;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import com.vipa.app.databinding.PotraitItemBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.helpers.ImageHelper;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import java.util.ArrayList;
import java.util.List;

import me.vipa.app.activities.listing.callback.ItemClickListener;
import me.vipa.app.activities.series.ui.SeriesDetailActivity;
import me.vipa.app.beanModel.ContinueRailModel.CommonContinueRail;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;


public class CommonPotraitAdapter extends RecyclerView.Adapter<CommonPotraitAdapter.SingleItemRowHolder> {

    private final String contentType;
    private final List<EnveuVideoItemBean> itemsList;
    private final Activity mContext;
    private final ItemClickListener listener;
    int viewType;
    private long mLastClickTime = 0;
    private int itemWidth;
    private int itemHeight;
    private ArrayList<CommonContinueRail> continuelist;
    private boolean isContinueList;
    private String isLogin;
    private KsPreferenceKeys preference;


    public CommonPotraitAdapter(Activity context, List<EnveuVideoItemBean> itemsList, String contentType, ArrayList<CommonContinueRail> continuelist, int view, ItemClickListener listener) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.listener = listener;
        this.contentType = contentType;
        this.continuelist = continuelist;
        if (this.continuelist != null) {
            if (this.continuelist.size() > 0)
                isContinueList = true;
            else
                isContinueList = false;
        }

        int num = 3;
        boolean tabletSize = mContext.getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            //landscape
            if (mContext.getResources().getConfiguration().orientation == 2)
                num = 6;
            else
                num = 5;
        }
        preference = KsPreferenceKeys.getInstance();
        isLogin = preference.getAppPrefLoginStatus();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        (mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        //if you need three fix imageview in width
        itemWidth = (displaymetrics.widthPixels) / num;
        itemHeight = (int) (itemWidth * 16) / 9;
    }

    public void notifydata(List<EnveuVideoItemBean> i) {
        this.itemsList.addAll(i);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SingleItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        PotraitItemBinding binding = PotraitItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SingleItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleItemRowHolder holder, int position) {
        holder.potraitItemBinding.itemImage.getLayoutParams().width = itemWidth;
        holder.potraitItemBinding.itemImage.getLayoutParams().height = itemHeight;


        if (itemsList.size() > 0) {

            try {
                AppCommonMethod.handleTags(itemsList.get(position).getIsVIP(),itemsList.get(position).getIsNewS(),
                        holder.potraitItemBinding.flExclusive,holder.potraitItemBinding.flNew,holder.potraitItemBinding.flEpisode,holder.potraitItemBinding.flNewMovie,itemsList.get(position).getAssetType());

            }catch (Exception ignored){

            }

            holder.potraitItemBinding.llContinueProgress.setVisibility(View.GONE);
            holder.potraitItemBinding.ivContinuePlay.setVisibility(View.GONE);
            EnveuVideoItemBean contentsItem = itemsList.get(position);
            if (contentsItem != null) {

                holder.potraitItemBinding.setPlaylistItem(contentsItem);


//                if (preference.getAppLanguage().equalsIgnoreCase("English")){
//                    holder.potraitItemBinding.flExclusive.setVisibility(View.VISIBLE);
//
//                } else {
//                    holder.potraitItemBinding.flExclusive.setVisibility(View.GONE);
//                }
//
//                if (contentsItem.isNew()) {
//                    holder.potraitItemBinding.flNew.setVisibility(View.VISIBLE);
//                } else {
//                    holder.potraitItemBinding.flNew.setVisibility(View.GONE);
//                }



                if (contentType.equalsIgnoreCase(AppConstants.VOD)) {

                    holder.potraitItemBinding.itemImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onRowItemClicked(contentsItem, position);
                        }
                    });
                    /*ImageHelper.getInstance(mContext)
                            .loadImageTo(holder.potraitItemBinding.itemImage, contentsItem.getPosterURL());
                    holder.potraitItemBinding.tvTitle.setText(contentsItem.getTitle());*/

                    if (contentsItem.getPosterURL() != null && !contentsItem.getPosterURL().equalsIgnoreCase("")) {
                        ImageHelper.getInstance(mContext).loadImageTo(holder.potraitItemBinding.itemImage, AppCommonMethod.getListPRImage(contentsItem.getPosterURL(), mContext));
                    }

                    /*holder.potraitItemBinding.tvTitle.setText(contentsItem.getTitle());
                    ImageHelper.getInstance(mContext)
                            .loadImageTo(holder.potraitItemBinding.itemImage, contentsItem.getPosterURL());
                    holder.potraitItemBinding.itemImage.setOnClickListener(view -> {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        try {
                            if ((contentsItem.getAssetType()) != null) {
                                if (contentsItem.getAssetType().equalsIgnoreCase("EPISODE")) {
                                    new ActivityLauncher(mContext).episodeScreen(mContext, EpisodeActivity.class, contentsItem.getId(), "", contentsItem.isPremium());
                                } else {
                                    new ActivityLauncher(mContext).detailScreen(mContext, DetailActivity.class, contentsItem.getId(), "", contentsItem.isPremium());
                                }
                            }
                        } catch (Exception e) {
                            new ActivityLauncher(mContext).detailScreen(mContext, DetailActivity.class, contentsItem.getId(), "", contentsItem.isPremium());

                        }
                    });*/

                } else if (contentType.equalsIgnoreCase(AppConstants.SERIES)) {

                    holder.potraitItemBinding.itemImage.setOnClickListener(view -> {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        new ActivityLauncher(mContext).seriesDetailScreen(mContext, SeriesDetailActivity.class, contentsItem.getId());
                    });
                    holder.potraitItemBinding.tvTitle.setText(contentsItem.getTitle());
                    ImageHelper.getInstance(mContext)
                            .loadImageTo(holder.potraitItemBinding.itemImage, AppCommonMethod.getImageUrl(AppConstants.SERIES, "POTRAIT") + contentsItem.getPosterURL());


                } else if (contentType.equalsIgnoreCase(AppConstants.CAST_AND_CREW)) {
                    holder.potraitItemBinding.tvTitle.setText(contentsItem.getTitle());
                    ImageHelper.getInstance(mContext)
                            .loadImageTo(holder.potraitItemBinding.itemImage, AppCommonMethod.getImageUrl(AppConstants.CAST_AND_CREW, "POTRAIT") + contentsItem.getPosterURL());

                } else if (contentType.equalsIgnoreCase(AppConstants.GENRE)) {
                    ImageHelper.getInstance(mContext)
                            .loadImageTo(holder.potraitItemBinding.itemImage, AppCommonMethod.getImageUrl(AppConstants.GENRE, "POTRAIT") + contentsItem.getPosterURL());
                }
            }
        } else if (continuelist.size() > 0) {
            if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                holder.potraitItemBinding.llContinueProgress.setVisibility(View.VISIBLE);
                holder.potraitItemBinding.ivContinuePlay.setVisibility(View.VISIBLE);
                holder.potraitItemBinding.flNew.setVisibility(View.GONE);

//                if (continuelist.get(position).getUserAssetDetail().isPremium()) {
//                    holder.potraitItemBinding.flExclusive.setVisibility(View.VISIBLE);
//                } else {
//                    holder.potraitItemBinding.flExclusive.setVisibility(View.GONE);
//                }
                try {
                    ImageHelper.getInstance(mContext)
                            .loadImageTo(holder.potraitItemBinding.itemImage, AppCommonMethod.getImageUrl(AppConstants.VOD, "POTRAIT") + continuelist.get(position).getUserAssetDetail().getPortraitImage());
                } catch (Exception e) {

                }
                holder.potraitItemBinding.itemImage.setOnClickListener(v -> {
                    if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                        if (continuelist.size() > 0 && (continuelist.get(position).getUserAssetDetail().getAssetType()) != null) {
                           /* if (continuelist.get(position).getUserAssetDetail().getAssetType().equalsIgnoreCase("EPISODE")) {
                                AppCommonMethod.launchDetailScreen(mContext, 0l, AppConstants.Episode, continuelist.get(position).getUserAssetDetail().getId(), String.valueOf(continuelist.get(position).getUserAssetStatus().getPosition()), continuelist.get(position).getUserAssetDetail().isPremium());
                            } else {
                                AppCommonMethod.launchDetailScreen(mContext, 0l, AppConstants.Video, continuelist.get(position).getUserAssetDetail().getId(), String.valueOf(continuelist.get(position).getUserAssetStatus().getPosition()), continuelist.get(position).getUserAssetDetail().isPremium());
                            }*/
                            AppCommonMethod.launchDetailScreen(mContext, 0l, continuelist.get(position).getUserAssetDetail().getAssetType(), continuelist.get(position).getUserAssetDetail().getId(), String.valueOf(continuelist.get(position).getUserAssetStatus().getPosition()), continuelist.get(position).getUserAssetDetail().isPremium());
                        }
                    }
                });

            } else {
                holder.potraitItemBinding.llContinueProgress.setVisibility(View.GONE);
                holder.potraitItemBinding.ivContinuePlay.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public int getItemCount() {
        if (isContinueList)
            return (null != continuelist ? continuelist.size() : 0);
        else
            return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {
        final PotraitItemBinding potraitItemBinding;

        SingleItemRowHolder(PotraitItemBinding potraitItemBind) {
            super(potraitItemBind.getRoot());
            potraitItemBinding = potraitItemBind;
        }
    }


}

