package com.mvhub.mvhubplus.utils;

import com.mvhub.mvhubplus.beanModel.responseModels.landingTabResponses.playlistResponse.PlaylistsItem;

import java.util.Comparator;

public class SortPlaylistId implements Comparator<PlaylistsItem> {

    @Override
    public int compare(PlaylistsItem listOne, PlaylistsItem listTwo) {

        return (int) (listOne.getId() - listTwo.getId());
    }
}