package com.mvhub.mvhubplus.repository.redeemCoupon;

import com.mvhub.mvhubplus.redeemcoupon.RedeemCouponResponseModel;

import retrofit2.Response;

public class RedeemModel {
    private boolean status;
    private String debugMessage;
    private int responseCode;
    private RedeemCouponResponseModel redeemCouponResponseModelResponse;

    public void setRedeemCouponResponseModelResponse(RedeemCouponResponseModel redeemCouponResponseModelResponse) {
        this.redeemCouponResponseModelResponse = redeemCouponResponseModelResponse;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public RedeemCouponResponseModel getRedeemCouponResponseModelResponse() {
        return redeemCouponResponseModelResponse;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
