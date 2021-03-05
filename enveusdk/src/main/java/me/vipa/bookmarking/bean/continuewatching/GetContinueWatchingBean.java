
package me.vipa.bookmarking.bean.continuewatching;

import com.google.gson.annotations.SerializedName;

public class GetContinueWatchingBean {

    @SerializedName("data")
    private Data mData;
    @SerializedName("debugMessage")
    private Object mDebugMessage;
    @SerializedName("responseCode")
    private Long mResponseCode;

    boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

    public Object getDebugMessage() {
        return mDebugMessage;
    }

    public void setDebugMessage(Object debugMessage) {
        mDebugMessage = debugMessage;
    }

    public Long getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(Long responseCode) {
        mResponseCode = responseCode;
    }

}
