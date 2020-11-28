package me.vipa.app.callbacks.commonCallbacks;

public interface NoInternetConnectionCallBack {
    void isOnline(boolean connected);

    void isOffline(boolean disconnected);
}
