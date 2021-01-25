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

    @SerializedName("profileAvatar")
    @Expose
    private String profileAvatar;
}
