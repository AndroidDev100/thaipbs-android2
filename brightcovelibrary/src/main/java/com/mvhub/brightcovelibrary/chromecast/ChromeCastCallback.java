package com.mvhub.brightcovelibrary.chromecast;

public interface ChromeCastCallback {
    void onChromeCastConnecting();
    void onChromeCastConnected();
    void onChromeCastDisconnected();
    void onVideoEnded();
    void onStatusUpdate();
}
