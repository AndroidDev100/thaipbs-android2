package com.mvhub.mvhubplus.utils.helpers;

import android.content.Context;

import com.google.android.gms.ads.doubleclick.PublisherAdView;

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
}
