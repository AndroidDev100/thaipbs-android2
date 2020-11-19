package com.mvhub.mvhubplus.activities.listing.callback;

import com.mvhub.mvhubplus.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;

public interface ItemClickListener {
    void onRowItemClicked(EnveuVideoItemBean itemValue, int position);

    default void onDeleteWatchListClicked(int assetId, int position){

    }
    default void onDeleteWatchHistoryClicked(int assetId, int position){

    }
}
