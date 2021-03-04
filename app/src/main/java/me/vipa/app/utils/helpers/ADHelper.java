package me.vipa.app.utils.helpers;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.ads.doubleclick.PublisherAdView;

import me.vipa.app.activities.detail.ui.EpisodeActivity;

public class ADHelper {

    private static final ADHelper ourInstance = new ADHelper();
    private static PublisherAdView adView;

    public static ADHelper getInstance(Context context) {
        adView = new PublisherAdView(context);
        return ourInstance;
    }


    public PublisherAdView getPublisherView() {
        return adView;
    }

    Activity pipAct;
    public void pipActivity(Activity episodeActivity) {
        this.pipAct=episodeActivity;
    }

    public Activity getPipAct() {
        return pipAct;
    }
}
