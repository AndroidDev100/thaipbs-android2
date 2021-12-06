package me.vipa.app.callbacks.commonCallbacks;

import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;
import me.vipa.app.beanModel.popularSearch.ItemsItem;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;

public interface SearchClickCallbacks {
    void onEnveuItemClicked(EnveuVideoItemBean itemValue);
    void onShowAllItemClicked(RailCommonData itemValue);
    void onShowAllProgramClicked(RailCommonData itemValue);
    void onPopularSearchItemClicked(ItemsItem itemValue);
}
