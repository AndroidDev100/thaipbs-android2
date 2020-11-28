package me.vipa.app.utils;

import com.vipa.BuildConfig;
import me.vipa.app.utils.config.bean.ConfigBean;
import me.vipa.app.utils.config.bean.ConfigBean;

public class MediaTypeConstants {
    private static MediaTypeConstants mediaTypeConstantsInstance;
    String Movie = "MOVIE";
    String Show = "SHOW";
    String Season = "SEASON";
    String Episode = "EPISODE";
    String Series = "SERIES";
    String Video = "VIDEO";
    String Live = "LIVE";
    String Article = "ARTICLE";
    ConfigBean configBean;
    public static String VIDEO="VIDEO";

    public String getMovie() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getMediaTypes().getMovie();
    }

    public String getShow() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getMediaTypes().getShow();
    }

    public String getEpisode() {
       return configBean == null ? "" : configBean.getData().getAppConfig().getMediaTypes().getEpisode();
    }

    public String getSeries() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getMediaTypes().getSeries();
    }

    public String getLive() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getMediaTypes().getLive();
    }

    public static String getVIDEO() {
        return VIDEO;
    }

    public static MediaTypeConstants getInstance() {
        if (mediaTypeConstantsInstance == null) {
            mediaTypeConstantsInstance = new MediaTypeConstants();
        }
        return (mediaTypeConstantsInstance);
    }

    public void setConfigObject(ConfigBean configBean) {
        this.configBean=configBean;
    }
}
