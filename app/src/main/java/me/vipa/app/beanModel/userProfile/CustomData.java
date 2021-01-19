package me.vipa.app.beanModel.userProfile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomData {
    @SerializedName("address")
    @Expose
    private String address;

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
