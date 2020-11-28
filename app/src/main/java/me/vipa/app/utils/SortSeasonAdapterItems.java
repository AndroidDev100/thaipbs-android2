package me.vipa.app.utils;

import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;

import java.util.Comparator;

import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;

public class SortSeasonAdapterItems implements Comparator<EnveuVideoItemBean> {

    @Override
    public int compare(EnveuVideoItemBean o1, EnveuVideoItemBean o2) {
        if (o1.getEpisodeNo()!=null && o1.getEpisodeNo() instanceof Double){
            return Double.compare((Double) o1.getEpisodeNo(), (Double) o2.getEpisodeNo());
        }else {
            return 0;
        }

    }
}
