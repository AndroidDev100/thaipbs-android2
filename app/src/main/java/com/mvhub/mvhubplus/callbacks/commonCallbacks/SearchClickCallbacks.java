package com.mvhub.mvhubplus.callbacks.commonCallbacks;

import com.mvhub.mvhubplus.beanModel.enveuCommonRailData.RailCommonData;
import com.mvhub.mvhubplus.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import com.mvhub.mvhubplus.beanModel.popularSearch.ItemsItem;

public interface SearchClickCallbacks {
    void onEnveuItemClicked(EnveuVideoItemBean itemValue);
    void onShowAllItemClicked(RailCommonData itemValue);
    void onPopularSearchItemClicked(ItemsItem itemValue);
}
