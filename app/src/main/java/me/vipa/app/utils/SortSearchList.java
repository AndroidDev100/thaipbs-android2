package me.vipa.app.utils;

import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;

import java.util.Comparator;

import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;

public class SortSearchList implements Comparator<RailCommonData> {

    @Override
    public int compare(RailCommonData searchedKeywords, RailCommonData t1) {

        return (searchedKeywords.getLayoutType() - t1.getLayoutType());
    }
}
