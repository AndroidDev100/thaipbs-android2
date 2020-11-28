package me.vipa.app.activities.purchase.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import me.vipa.app.beanModel.cancelPurchase.ResponseCancelPurchase;
import me.vipa.app.beanModel.membershipAndPlan.ResponseMembershipAndPlan;
import me.vipa.app.beanModel.purchaseModel.PurchaseModel;
import me.vipa.app.beanModel.purchaseModel.PurchaseResponseModel;
import me.vipa.app.repository.purchase.PurchaseRepository;
import com.google.gson.JsonObject;

import me.vipa.app.repository.purchase.PurchaseRepository;


public class PurchaseViewModel extends AndroidViewModel {

    public PurchaseViewModel(@NonNull Application application) {
        super(application);
    }


    public LiveData<PurchaseResponseModel> createNewPurchaseRequest(String token, JsonObject data, PurchaseModel model,String sku) {
        return PurchaseRepository.getInstance().createNewPurchaseRequest(token, data,model,sku);
    }

    public LiveData<PurchaseResponseModel> callInitiatePaymet(String token, String orderId) {
        return PurchaseRepository.getInstance().callInitiatePaymet(token, orderId);
    }


    public LiveData<ResponseMembershipAndPlan> getPlans(String token) {
        return PurchaseRepository.getInstance().getPlans(token);
    }

    public LiveData<ResponseCancelPurchase> cancelPlan(String token) {
        return PurchaseRepository.getInstance().cancelPlan(token);
    }

    public LiveData<PurchaseResponseModel> updatePurchase(String billingError,String paymentStatus,String token,String purchaseToken,String paymentId,String orderId,PurchaseModel purchaseModel) {
        return PurchaseRepository.getInstance().updatePurchase(billingError,paymentStatus,token,purchaseToken,paymentId,orderId,purchaseModel);
    }


}
