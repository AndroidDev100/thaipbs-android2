package me.vipa.app.callbacks.commonCallbacks;

public interface VersionValidator {

    void version(boolean status, int currentVersion, int playstoreVersion,String updateType);
}
