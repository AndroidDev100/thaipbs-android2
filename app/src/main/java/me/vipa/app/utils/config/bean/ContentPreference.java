package me.vipa.app.utils.config.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContentPreference {
    @SerializedName("identifier")
    @Expose
    private String identifier;
    @SerializedName("displayName")
    @Expose
    private String name;

}
