package me.vipa.app.utils;

import me.vipa.app.beanModel.responseModels.series.SeasonsItem;

import java.util.Comparator;

import me.vipa.app.beanModel.responseModels.series.SeasonsItem;

public class SortSeasonList implements Comparator<SeasonsItem> {

    @Override
    public int compare(SeasonsItem searchedKeywords, SeasonsItem t1) {
        return (t1.getSeasonNo() - searchedKeywords.getSeasonNo());
    }
}

