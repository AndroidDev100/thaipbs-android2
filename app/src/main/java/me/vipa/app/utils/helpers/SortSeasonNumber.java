package me.vipa.app.utils.helpers;

import me.vipa.app.beanModel.responseModels.series.SeasonsItem;

import java.util.Comparator;

import me.vipa.app.beanModel.responseModels.series.SeasonsItem;

public class SortSeasonNumber implements Comparator<SeasonsItem> {

    @Override
    public int compare(SeasonsItem searchedKeywords, SeasonsItem t1) {
        return (searchedKeywords.getSeasonNo() - t1.getSeasonNo());
    }
}