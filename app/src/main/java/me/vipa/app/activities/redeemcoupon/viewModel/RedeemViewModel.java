package me.vipa.app.activities.redeemcoupon.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import me.vipa.app.repository.redeemCoupon.RedeemCouponLayer;
import me.vipa.app.repository.redeemCoupon.RedeemModel;
import me.vipa.app.repository.redeemCoupon.RedeemCouponLayer;
import me.vipa.app.repository.redeemCoupon.RedeemModel;

public class RedeemViewModel extends AndroidViewModel {
    public RedeemViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<RedeemModel> redeemCoupon(String token, String coupon) {
        return RedeemCouponLayer.getInstance().redeemCoupon(token, coupon);
    }
}
