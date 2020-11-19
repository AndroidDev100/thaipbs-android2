package com.mvhub.mvhubplus.repository.redeemCoupon;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mvhub.mvhubplus.networking.apiendpoints.ApiInterface;
import com.mvhub.mvhubplus.networking.apiendpoints.RequestConfig;
import com.mvhub.mvhubplus.networking.intercepter.ErrorCodesIntercepter;
import com.mvhub.mvhubplus.redeemcoupon.RedeemCouponResponseModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RedeemCouponLayer {
    private static RedeemCouponLayer redeemCouponLayer;

    public static RedeemCouponLayer getInstance() {
        redeemCouponLayer = new RedeemCouponLayer();
        return redeemCouponLayer;
    }


    public LiveData<RedeemModel> redeemCoupon(String token, String coupon) {
        MutableLiveData<RedeemModel> responseData = new MutableLiveData<>();
        RedeemModel redeemModel = new RedeemModel();
        ApiInterface endpoint = RequestConfig.redeemCoupon(token).create(ApiInterface.class);
        endpoint.redeemCoupon(coupon).enqueue(new Callback<RedeemCouponResponseModel>() {
            @Override
            public void onResponse(Call<RedeemCouponResponseModel> call, Response<RedeemCouponResponseModel> response) {
                if (response.code() == 200) {
                    redeemModel.setStatus(true);
                    redeemModel.setRedeemCouponResponseModelResponse(response.body());
                    responseData.postValue(redeemModel);
                } else {
                    RedeemModel redeemMode = ErrorCodesIntercepter.getInstance().redeemCoupon(response);
                    responseData.postValue(redeemMode);
                }
            }

            @Override
            public void onFailure(Call<RedeemCouponResponseModel> call, Throwable t) {
                responseData.postValue(null);
            }
        });


        return responseData;
    }

}
