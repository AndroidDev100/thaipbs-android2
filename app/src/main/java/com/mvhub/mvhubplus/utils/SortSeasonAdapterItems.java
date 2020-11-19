package com.mvhub.mvhubplus.utils;

import com.mvhub.mvhubplus.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import com.mvhub.mvhubplus.utils.helpers.StringUtils;

import java.util.Comparator;

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
