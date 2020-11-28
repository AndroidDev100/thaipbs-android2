package me.vipa.app.utils;

import me.vipa.app.beanModel.responseModels.landingTabResponses.railData.PlaylistRailData;

import java.util.Comparator;

import me.vipa.app.beanModel.responseModels.landingTabResponses.railData.PlaylistRailData;

public class SortRailsList implements Comparator<PlaylistRailData> {

    @Override
    public int compare(PlaylistRailData listOne, PlaylistRailData listTwo) {

        return (int) (listOne.getData().getIndex() - listTwo.getData().getIndex());
    }
}
