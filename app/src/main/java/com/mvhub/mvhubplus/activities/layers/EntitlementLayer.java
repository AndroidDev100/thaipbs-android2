package com.mvhub.mvhubplus.activities.layers;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mvhub.mvhubplus.beanModel.entitle.ResponseEntitle;
import com.mvhub.mvhubplus.networking.apiendpoints.RequestConfig;
import com.mvhub.mvhubplus.networking.detailPlayer.APIDetails;
import com.mvhub.mvhubplus.networking.intercepter.ErrorCodesIntercepter;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntitlementLayer {

    private static EntitlementLayer entitlement;
    public synchronized static EntitlementLayer getInstance() {
        if (entitlement == null) {
            entitlement = new EntitlementLayer();
        }
        return entitlement;
    }

    public LiveData<ResponseEntitle> hitApiEntitlement(String token, String sku) {
        MutableLiveData<ResponseEntitle> responseOutput = new MutableLiveData<>();

        APIDetails endpoint = RequestConfig.getUserInteration(token).create(APIDetails.class);
        Call<ResponseEntitle> call = endpoint.checkEntitlement(sku);
        call.enqueue(new Callback<ResponseEntitle>() {
            @Override
            public void onResponse(@NonNull Call<ResponseEntitle> call, @NonNull Response<ResponseEntitle> response) {
                if (response.code() == 200) {
                    ResponseEntitle responseEntitlement = new ResponseEntitle();

                    responseEntitlement.setResponseCode(response.code());
                    responseEntitlement.setStatus(true);
                    responseEntitlement.setData(Objects.requireNonNull(response.body()).getData());
                    responseOutput.postValue(responseEntitlement);
                } else {

                    ResponseEntitle responseModel = ErrorCodesIntercepter.getInstance().checkEntitlement(response);
                    responseOutput.postValue(responseModel);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseEntitle> call, @NonNull Throwable t) {
                ResponseEntitle responseEntitlement = new ResponseEntitle();
                responseEntitlement.setStatus(false);
                responseOutput.postValue(responseEntitlement);


            }
        });
        return responseOutput;
    }

}
