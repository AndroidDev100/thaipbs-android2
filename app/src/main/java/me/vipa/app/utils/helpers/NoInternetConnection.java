package me.vipa.app.utils.helpers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.vipa.app.callbacks.commonCallbacks.NoInternetConnectionCallBack;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.cropImage.helpers.NetworkConnectivity;
import com.vipa.app.databinding.NoConnectionBinding;

import me.vipa.app.callbacks.commonCallbacks.NoInternetConnectionCallBack;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.cropImage.helpers.NetworkConnectivity;


public class NoInternetConnection {
    final Activity activity;
    NoInternetConnectionCallBack noInternetConnectionCallBack;
    private final BroadcastReceiver ReceivefromService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkConnectivity.isOnline(activity)) {
                noInternetConnectionCallBack.isOnline(true);
            } else {
                noInternetConnectionCallBack.isOffline(true);
            }

        }
    };


    public NoInternetConnection(Activity context) {
        this.activity = context;
    }

    public void hanleAction(NoConnectionBinding connection, NoInternetConnectionCallBack callBack) {
        this.noInternetConnectionCallBack = callBack;

        if (NetworkConnectivity.isOnline(activity)) {
            noInternetConnectionCallBack.isOnline(true);
            try {
                if (ReceivefromService != null) {
                    activity.unregisterReceiver(ReceivefromService);
                }
            } catch (IllegalArgumentException e) {
                Logger.e("NoInternetConnection", "" + e.toString());
            }

        } else {
            noInternetConnectionCallBack.isOffline(true);
           /* IntentFilter filter= new IntentFilter();
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            activity.registerReceiver(ReceivefromService, filter);*/
        }
    }
}
