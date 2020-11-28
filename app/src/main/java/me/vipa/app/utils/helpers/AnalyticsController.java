package me.vipa.app.utils.helpers;

import android.app.Activity;

import me.vipa.app.MvHubPlusApplication;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class AnalyticsController {
    private final Activity activity;

    public AnalyticsController(Activity context) {
        activity = context;
    }

    public void callAnalytics(String screenname, String category, String action) {
        MvHubPlusApplication application = (MvHubPlusApplication) activity.getApplication();
        Tracker mTracker = application.getDefaultTracker();
        if (mTracker!=null){
            mTracker.setScreenName("Screen--" + screenname);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());

            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .build());
        }

    }
}
