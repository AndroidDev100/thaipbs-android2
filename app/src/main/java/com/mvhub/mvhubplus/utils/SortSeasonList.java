package com.mvhub.mvhubplus.utils;

import com.mvhub.mvhubplus.beanModel.responseModels.series.SeasonsItem;

import java.util.Comparator;

public class SortSeasonList implements Comparator<SeasonsItem> {

    @Override
    public int compare(SeasonsItem searchedKeywords, SeasonsItem t1) {
        return (t1.getSeasonNo() - searchedKeywords.getSeasonNo());
    }
}

