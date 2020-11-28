package me.vipa.app.utils;


import me.vipa.app.beanModel.KeywordList;

import java.util.Comparator;

import me.vipa.app.beanModel.KeywordList;

class SortList implements Comparator<KeywordList> {

    @Override
    public int compare(KeywordList searchedKeywords, KeywordList t1) {

        return (int) (searchedKeywords.getTimeStamp() - t1.getTimeStamp());
    }
}
