package me.vipa.app.activities.listing.callback;

import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;

public interface ItemClickListener {
    void onRowItemClicked(EnveuVideoItemBean itemValue, int position);

    default void onDeleteWatchListClicked(int assetId, int position){

    }
    default void onDeleteWatchHistoryClicked(int assetId, int position){

    }
}
