package com.mvhub.mvhubplus.tarcker;

import android.content.Context;

import com.mvhub.mvhubplus.utils.TrackerUtil.PlatformType;
import com.mvhub.mvhubplus.utils.TrackerUtil.TrackerEvent;
import com.mvhub.mvhubplus.utils.TrackerUtil.TrackerUtil;
import com.google.gson.JsonObject;

import java.util.Arrays;

public class FCMEvents {

    private static FCMEvents instance;
    Context context;

    private FCMEvents() {
    }

    public static FCMEvents getInstance() {
        if (instance == null) {
            instance = new FCMEvents();
        }
        return (instance);
    }

    public void trackEvent(int eventType, JsonObject requestParam) {
        switch (eventType) {
            case 1:
                /*TrackerUtil.getInstance(context).track(TrackerEvent.REGISTER, requestParam,
                        Arrays.asList(PlatformType.FCM));*/
                break;
            case 2:
                /*TrackerUtil.getInstance(context).track(TrackerEvent.CLICK_CONTENT, requestParam,
                        Arrays.asList(PlatformType.FCM));*/
                break;
            case 3:
               /* TrackerUtil.getInstance(context).track(TrackerEvent.PLAY_CONTENT, requestParam,
                        Arrays.asList(PlatformType.FCM));*/
                break;

            case 4:
                /*TrackerUtil.getInstance(context).track(TrackerEvent.SEARCH, requestParam,
                        Arrays.asList(PlatformType.FCM));*/
            case 5:
                /*TrackerUtil.getInstance(context).track(TrackerEvent.LOGIN, requestParam,
                        Arrays.asList(PlatformType.FCM));*/
                break;
            default:
                break;
        }
    }

    public FCMEvents setContext(Context context) {
        this.context=context;
        return this;
    }
}
