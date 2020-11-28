package me.vipa.app.beanModel.seriesVideoList;

import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;

import java.util.List;

import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;

public class Data {
    private Object seasonName;
    private List<EnveuVideoItemBean> videos;
    private String seasonNumber;

    public Object getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(Object seasonName) {
        this.seasonName = seasonName;
    }

    public List<EnveuVideoItemBean> getVideos() {
        return videos;
    }

    public void setVideos(List<EnveuVideoItemBean> videos) {
        this.videos = videos;
    }

    public String getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(String seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    @Override
    public String toString() {
        return
                "Data{" +
                        "seasonName = '" + seasonName + '\'' +
                        ",videos = '" + videos + '\'' +
                        ",seasonNumber = '" + seasonNumber + '\'' +
                        "}";
    }
}