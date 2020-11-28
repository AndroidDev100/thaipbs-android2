package me.vipa.app.utils;

import me.vipa.app.beanModel.responseModels.landingTabResponses.playlistResponse.PlaylistsItem;

import java.util.Comparator;

import me.vipa.app.beanModel.responseModels.landingTabResponses.playlistResponse.PlaylistsItem;

public class SortPlaylistId implements Comparator<PlaylistsItem> {

    @Override
    public int compare(PlaylistsItem listOne, PlaylistsItem listTwo) {

        return (int) (listOne.getId() - listTwo.getId());
    }
}