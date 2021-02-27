package me.vipa.app.beanModel.userProfile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomData {
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("NotificationCheck")
    @Expose
    private String isNotification;

    @SerializedName("contentPreferences")
    @Expose
    private String contentPreferences;

    public String getIsNotification() {
        return isNotification;
    }

    public void setIsNotification(String isNotification) {
        this.isNotification = isNotification;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfileAvatar() {
        return profileAvatar;
    }

    public void setProfileAvatar(String profileAvatar) {
        this.profileAvatar = profileAvatar;
    }

    public void setContentPreferences(String contentPreferences) {
        this.contentPreferences = contentPreferences;
    }

    public String getContentPreferences() {
        return contentPreferences;
    }

    @SerializedName("profileAvatar")
    @Expose
    private String profileAvatar;
}
