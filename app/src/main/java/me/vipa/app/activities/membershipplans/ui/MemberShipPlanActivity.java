package me.vipa.app.activities.membershipplans.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import me.vipa.app.activities.membershipplans.adapter.MembershipAdapter;
import me.vipa.app.activities.purchase.ui.VodOfferType;
import me.vipa.app.activities.purchase.ui.adapter.PurchaseShimmerAdapter;
import me.vipa.app.activities.purchase.ui.viewmodel.PurchaseViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.beanModel.cancelPurchase.ResponseCancelPurchase;
import me.vipa.app.beanModel.membershipAndPlan.ResponseMembershipAndPlan;
import me.vipa.app.beanModel.purchaseModel.PurchaseModel;
import me.vipa.app.beanModel.purchaseModel.PurchaseResponseModel;
import me.vipa.app.cms.HelpActivity;
import me.vipa.app.fragments.dialog.AlertDialogFragment;
import me.vipa.app.fragments.dialog.AlertDialogSingleButtonFragment;
import me.vipa.app.R;
import me.vipa.app.databinding.MembershipPlanBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.helpers.ImageHelper;
import me.vipa.app.utils.helpers.NetworkConnectivity;

import me.vipa.app.utils.helpers.StringUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import me.vipa.app.utils.helpers.ToastHandler;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.vipa.app.activities.membershipplans.adapter.MembershipAdapter;
import me.vipa.app.baseModels.BaseBindingActivity;

public class MemberShipPlanActivity extends BaseBindingActivity<MembershipPlanBinding> implements BillingProcessor.IBillingHandler, MembershipAdapter.OnPurchaseItemClick, AlertDialogFragment.AlertDialogListener {
    private static String selectedPurchaseOption;
    private static JsonObject jsonObj;
    PurchaseModel model;
    private BillingProcessor bp;
    private PurchaseViewModel viewModel;
    private KsPreferenceKeys preference;
    private MembershipAdapter adapterPurchase;
    private List<PurchaseModel> alPurchaseOptions;
    private List<String> subscribedPlanName;
    private List<PurchaseModel> playstorePurchaseOptions;
    private boolean isloggedout = false;
    private boolean isCardClickable = true;
    private String strToken;
    private long mLastClickTime = 0;
    private String localeCurrency;

    @Override
    public MembershipPlanBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return MembershipPlanBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preference = KsPreferenceKeys.getInstance();
        strToken = preference.getAppPrefAccessToken();
        getBinding().playerLayout.setOnClickListener(v -> {
            createBottomSheet();
            /*Intent intent = new Intent(MemberShipPlanActivity.this, ContactActivity.class);
            startActivity(intent);*/
        });
        getBinding().contact.setOnClickListener(v -> {
            if (NetworkConnectivity.isOnline(MemberShipPlanActivity.this)) {
                createBottomSheet();
            }else {
                new ToastHandler(MemberShipPlanActivity.this).show(getResources().getString(R.string.no_connection));
            }
        });
        setClicks();
        callBinding();
    }

    private void setClicks() {
        getBinding().terms.setOnClickListener(v -> {
            if (NetworkConnectivity.isOnline(MemberShipPlanActivity.this)) {
                Objects.requireNonNull(this).startActivity(new Intent(this, HelpActivity.class).putExtra("type", "1"));
            }else {
                new ToastHandler(MemberShipPlanActivity.this).show(getResources().getString(R.string.no_connection));
            }
        });
        getBinding().privacy.setOnClickListener(v -> {
            if (NetworkConnectivity.isOnline(MemberShipPlanActivity.this)) {
                Objects.requireNonNull(this).startActivity(new Intent(this, HelpActivity.class).putExtra("type", "2"));
            }else {
                new ToastHandler(MemberShipPlanActivity.this).show(getResources().getString(R.string.no_connection));
            }
        });
    }

    private void callBinding() {

        viewModel = ViewModelProviders.of(this).get(PurchaseViewModel.class);
        modelCall();
    }

    private void modelCall() {

        getBinding().toolbar.llSearchIcon.setVisibility(View.GONE);
        getBinding().toolbar.backLayout.setVisibility(View.VISIBLE);
        getBinding().toolbar.homeIcon.setVisibility(View.GONE);
        getBinding().toolbar.mediaRouteButton.setVisibility(View.GONE);
        getBinding().toolbar.titleText.setVisibility(View.VISIBLE);

        getBinding().toolbar.screenText.setText(getResources().getString(R.string.membership_plan));
        connectionObserver();

        getBinding().backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    private void connectionObserver() {
        if (NetworkConnectivity.isOnline(this)) {
            connectionValidation(true);
        } else {
            connectionValidation(false);
        }
    }

    private void connectionValidation(Boolean aBoolean) {
        if (aBoolean) {
            UIinitialization();
        } else {
            noConnectionLayout();
        }
    }

    public ResponseMembershipAndPlan planResponse;

    private void UIinitialization() {
        /*getBinding().llTop.setVisibility(View.VISIBLE);
        getBinding().noConnectionLayout.setVisibility(View.GONE);*/

        PurchaseShimmerAdapter shimmerAdapter = new PurchaseShimmerAdapter(this, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        getBinding().rvPurchase.setLayoutManager(mLayoutManager);
        getBinding().rvPurchase.setItemAnimator(new DefaultItemAnimator());
        getBinding().rvPurchase.setAdapter(shimmerAdapter);
        String tempBase64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk56pCLKhlJNSOVJo2dPCi4jwvmhxgS+lmFj5N/lc6SKbjH9D5vm/gRj7OgvSYN4sEWflKqZ3nD+eMfYYh8h679pzNHf8AJGxjyriZaaKprYXXsBTKRnOCEIQYzNMsZ4oLyr3sEjuR22fNb3sl2BZbM2sXK0sYFG05Dba9fHPIifYivqc5ci7QFiNJMDFL83Up4zz8jREwHPgeE6VAQvlnNn3NlSzZ1y6yx66pYN4pnqk2hzO/Wcp1ay7A5up+rU2OP4EtIeNBsfWPtZ40Bp9xEQUoeETt3+hSRMnQRlCxIyJK7AgypSAZHNHwrXi979UR7pi7NkDyNX3CTBxuP9NnwIDAQAB";

        bp = new BillingProcessor(this, tempBase64, this);
        bp.initialize();


        getBinding().btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkConnectivity.isOnline(MemberShipPlanActivity.this)) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (model!=null){
                        purchaseTVOD();
                    }
                }else {
                    new ToastHandler(MemberShipPlanActivity.this).show(getResources().getString(R.string.no_connection));
                }
            }
        });

        loadImage();
    }

    private void loadImage() {
        ImageHelper.getInstance(MemberShipPlanActivity.this).loadIAPImage(getBinding().bgImg);
    }

    private void purchaseTVOD() {
        showLoading(getBinding().progressBar, true);
        hitApiDoPurchase();

    }

    @Override
    public void onFinishDialog() {
        if (isloggedout) {
            forceLogout();
        }
    }

    public void forceLogout() {
        if (isloggedout) {
            isloggedout = false;
            hitApiLogout(MemberShipPlanActivity.this, preference.getAppPrefAccessToken());
        }

    }

    private void showDialog(String title, String message) {
        FragmentManager fm = getSupportFragmentManager();
        AlertDialogSingleButtonFragment alertDialog = AlertDialogSingleButtonFragment.newInstance(title, message, getResources().getString(R.string.ok));
        alertDialog.setCancelable(false);
        alertDialog.setAlertDialogCallBack(this);
        alertDialog.show(fm, "fragment_alert");
    }

    public void resetAdapter(List<PurchaseModel> list, List<String> selectedPlans) {
        isCardClickable = true;
        for (int i = 0; i < selectedPlans.size(); i++) {
            if (selectedPlans.get(i).equalsIgnoreCase("weekly Pack") || selectedPlans.get(i).equalsIgnoreCase("monthly") || selectedPlans.get(i).equalsIgnoreCase("daily"))
                isCardClickable = false;
        }
        if (!isCardClickable) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPurchaseOptions().equalsIgnoreCase("free"))
                    list.get(i).setSelected(false);
            }
        }

        if (!isCardClickable) {
            getBinding().issue.setVisibility(View.VISIBLE);
        }

        adapterPurchase = new MembershipAdapter(this, list, this, isCardClickable, localeCurrency);
        getBinding().rvPurchase.setAdapter(adapterPurchase);
    }

    private void noConnectionLayout() {
        /*getBinding().llTop.setVisibility(View.GONE);
        getBinding().noConnectionLayout.setVisibility(View.VISIBLE);
        getBinding().connection.retryTxt.setOnClickListener(view -> connectionObserver());*/
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    String billingError = "";

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        if (error != null && error.getMessage() != null) {
            billingError = error.getMessage();
            Log.w("billingError", error.getMessage());
        }
        updatePayment(billingError, "FAILED", "", paymentId);

    }
    public void createBottomSheet() {

        BottomSheetDialog dialog;
        RecyclerView recyclerView;
        TextView email;
        TextView line;

        View view1 = getLayoutInflater().inflate(R.layout.bottom_sheet_contact, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(view1);
        email=view1.findViewById(R.id.email);
        line=view1.findViewById(R.id.line);
        FrameLayout bottomSheet = (FrameLayout) dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheet).setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
        BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.show();

        email.setOnClickListener(v -> {
            Intent intentEmail= new Intent(Intent.ACTION_SENDTO);
            intentEmail.setData(Uri.parse("mailto:info@mvhub.com"));
            startActivity(intentEmail);

        });
        line.setOnClickListener(v -> {
            Intent intent = new Intent();
            try {
                intent = Intent.parseUri(AppConstants.LINE_URI, Intent.URI_INTENT_SCHEME);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        });

    }

    @Override
    public void onBillingInitialized() {
        try {
            boolean isOneTimePurchaseSupported = bp.isOneTimePurchaseSupported();
            if (!isOneTimePurchaseSupported) {
                Toast.makeText(this, "Your device doesn't support IN App Billing", Toast.LENGTH_LONG).show();
                getBinding().offerLayout.setVisibility(View.GONE);
                getBinding().noOfferLayout.setVisibility(View.VISIBLE);
                getBinding().bottomLay.setVisibility(View.GONE);
                return;
            }

            getPlansApi();

        } catch (NullPointerException e) {
            getBinding().progressBar.setVisibility(View.GONE);
        }


    }

    private void getPlansApi() {
        String token = preference.getAppPrefAccessToken();
        if (!StringUtils.isNullOrEmptyOrZero(token)) {
            viewModel.getPlans(token).observe(MemberShipPlanActivity.this, new Observer<ResponseMembershipAndPlan>() {
                @Override
                public void onChanged(@Nullable ResponseMembershipAndPlan responseMembershipAndPlan) {
                    Logger.e("MemberShipPlanActivity", "ResponseMembershipAndPlan" + responseMembershipAndPlan.toString());
                    if (responseMembershipAndPlan.isStatus()) {
                        if (responseMembershipAndPlan.getData().size() > 0) {
                            planResponse = responseMembershipAndPlan;
                            Logger.e("", "responseMembershipAndPlan" + responseMembershipAndPlan.getData().toString());
                            alPurchaseOptions = new ArrayList<>();
                            subscribedPlanName = new ArrayList<>();
                            for (int i = 0; i < responseMembershipAndPlan.getData().size(); i++) {
                                if (responseMembershipAndPlan.getData().get(i).getOfferType().contains(VodOfferType.RECURRING_SUBSCRIPTION​.name())) {
                                    PurchaseModel tempModel = new PurchaseModel();
                                    createRecurringSubscriptions(tempModel, i, alPurchaseOptions, "", "", VodOfferType.RECURRING_SUBSCRIPTION​.name());
                                    if (responseMembershipAndPlan.getData().get(i).getEntitlementState()) {
                                        tempModel.setSelected(true);
                                        isCardClickable = false;
                                        getBinding().btnBuy.setVisibility(View.INVISIBLE);
                                        subscribedPlanName.add(responseMembershipAndPlan.getData().get(i).getTitle());
                                    } else {
                                        tempModel.setSelected(false);
                                    }

                                }
                            }

                            if (alPurchaseOptions.size() > 0) {
                                getBinding().offerLayout.setVisibility(View.VISIBLE);
                                getBinding().noOfferLayout.setVisibility(View.GONE);
                                adapterPurchase = new MembershipAdapter(getApplicationContext(), alPurchaseOptions, MemberShipPlanActivity.this, isCardClickable, localeCurrency);
                                getBinding().rvPurchase.setAdapter(adapterPurchase);
                            } else {
                                getBinding().offerLayout.setVisibility(View.GONE);
                                getBinding().noOfferLayout.setVisibility(View.VISIBLE);
                                getBinding().bottomLay.setVisibility(View.GONE);
                            }

                            //resetAdapter(alPurchaseOptions, subscribedPlanName);
                        }

                    } else {
                        if (responseMembershipAndPlan.getResponseCode() == 401) {
                            isloggedout = true;
                            showDialog(MemberShipPlanActivity.this.getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));
                        } else {
                            showDialog(MemberShipPlanActivity.this.getResources().getString(R.string.error), getResources().getString(R.string.something_went_wrong_at_our_end_please_try_later));
                        }
                    }

                }
            });

        } else {
            Logger.e("MemberShipPlanActivity", "Token is empty/null");
        }
    }

    SkuDetails skuDetails;

    private void createRecurringSubscriptions(PurchaseModel purchaseModel, int i, List<PurchaseModel> alPurchaseOptions, String vodOfferType, String subscriptionOfferPeriod, String subscriptionType) {
        try {
            if (planResponse.getData().get(i).getRecurringOffer().getOfferPeriod().contains(VodOfferType.WEEKLY.name())) {
                try {
                    // skuDetails = bp.getSubscriptionListingDetails("monthly");
                    // skuDetails = bp.getSubscriptionListingDetails("vod_285ce97b_a26c_482b_b0b3_0777b411310c_tvod_price");
                    skuDetails = bp.getSubscriptionListingDetails(planResponse.getData().get(i).getIdentifier());
                    purchaseModel.setPrice("" + skuDetails.priceValue);
                    purchaseModel.setPurchaseOptions(subscriptionType);
                    purchaseModel.setOfferPeriod(VodOfferType.WEEKLY.name());
                    purchaseModel.setTitle(planResponse.getData().get(i).getTitle());
                    purchaseModel.setIdentifier(planResponse.getData().get(i).getIdentifier());
                    purchaseModel.setCurrency(skuDetails.currency);
                } catch (Exception e) {
                    // purchaseModel.setPrice("" + planResponse.getData().get(i).getPrice());
                    purchaseModel.setPurchaseOptions(subscriptionType);
                    purchaseModel.setOfferPeriod(VodOfferType.WEEKLY.name());
                    purchaseModel.setTitle(planResponse.getData().get(i).getTitle());
                    purchaseModel.setIdentifier(planResponse.getData().get(i).getIdentifier());
                    // purchaseModel.setCurrency(planResponse.getData().get(i).getCurrencyCode());

                }
                if (skuDetails != null)
                    alPurchaseOptions.add(purchaseModel);
            } else if (planResponse.getData().get(i).getRecurringOffer().getOfferPeriod().contains(VodOfferType.MONTHLY.name())) {
                try {
                    // skuDetails = bp.getSubscriptionListingDetails("monthly");
                    // skuDetails = bp.getSubscriptionListingDetails("svod_my_monthly_pack_with_trail");
                    skuDetails = bp.getSubscriptionListingDetails(planResponse.getData().get(i).getIdentifier());
                    purchaseModel.setPrice("" + skuDetails.priceValue);
                    purchaseModel.setPurchaseOptions(subscriptionType);
                    purchaseModel.setOfferPeriod(VodOfferType.MONTHLY.name());
                    purchaseModel.setTitle(planResponse.getData().get(i).getTitle());
                    purchaseModel.setIdentifier(planResponse.getData().get(i).getIdentifier());
                    purchaseModel.setCurrency(skuDetails.currency);
                } catch (Exception e) {
                    // purchaseModel.setPrice("" + planResponse.getData().get(i).getPrice());
                    purchaseModel.setPurchaseOptions(subscriptionType);
                    purchaseModel.setOfferPeriod(VodOfferType.MONTHLY.name());
                    purchaseModel.setTitle(planResponse.getData().get(i).getTitle());
                    purchaseModel.setIdentifier(planResponse.getData().get(i).getIdentifier());
                    // purchaseModel.setCurrency(planResponse.getData().get(i).getCurrencyCode());

                }
                if (skuDetails != null)
                    alPurchaseOptions.add(purchaseModel);
            } else if (planResponse.getData().get(i).getRecurringOffer().getOfferPeriod().contains(VodOfferType.QUARTERLY.name())) {
                try {
                    // skuDetails = bp.getSubscriptionListingDetails("monthly");
                    //skuDetails = bp.getSubscriptionListingDetails("svod_my_quaterly_pack_recurring");
                    skuDetails = bp.getSubscriptionListingDetails(planResponse.getData().get(i).getIdentifier());
                    purchaseModel.setPrice("" + skuDetails.priceValue);
                    purchaseModel.setPurchaseOptions(subscriptionType);
                    purchaseModel.setOfferPeriod(VodOfferType.QUARTERLY.name());
                    purchaseModel.setTitle(planResponse.getData().get(i).getTitle());
                    purchaseModel.setIdentifier(planResponse.getData().get(i).getIdentifier());
                    purchaseModel.setCurrency(skuDetails.currency);
                } catch (Exception e) {
                    // purchaseModel.setPrice("" + planResponse.getData().get(i).getPrice());
                    purchaseModel.setPurchaseOptions(subscriptionType);
                    purchaseModel.setOfferPeriod(VodOfferType.QUARTERLY.name());
                    purchaseModel.setTitle(planResponse.getData().get(i).getTitle());
                    purchaseModel.setIdentifier(planResponse.getData().get(i).getIdentifier());
                    // purchaseModel.setCurrency(planResponse.getData().get(i).getCurrencyCode());

                }
                if (skuDetails != null)
                    alPurchaseOptions.add(purchaseModel);
            } else if (planResponse.getData().get(i).getRecurringOffer().getOfferPeriod().contains(VodOfferType.HALF_YEARLY.name())) {
                try {
                    // skuDetails = bp.getSubscriptionListingDetails("monthly");
                    skuDetails = bp.getSubscriptionListingDetails(planResponse.getData().get(i).getIdentifier());
                    purchaseModel.setPrice("" + skuDetails.priceValue);
                    purchaseModel.setPurchaseOptions(subscriptionType);
                    purchaseModel.setOfferPeriod(VodOfferType.HALF_YEARLY.name());
                    purchaseModel.setTitle(planResponse.getData().get(i).getTitle());
                    purchaseModel.setIdentifier(planResponse.getData().get(i).getIdentifier());
                    purchaseModel.setCurrency(skuDetails.currency);
                } catch (Exception e) {
                    // purchaseModel.setPrice("" + planResponse.getData().get(i).getPrice());
                    purchaseModel.setPurchaseOptions(subscriptionType);
                    purchaseModel.setOfferPeriod(VodOfferType.HALF_YEARLY.name());
                    purchaseModel.setTitle(planResponse.getData().get(i).getTitle());
                    purchaseModel.setIdentifier(planResponse.getData().get(i).getIdentifier());
                    // purchaseModel.setCurrency(planResponse.getData().get(i).getCurrencyCode());

                }
                if (skuDetails != null)
                    alPurchaseOptions.add(purchaseModel);
            } else if (planResponse.getData().get(i).getRecurringOffer().getOfferPeriod().contains(VodOfferType.ANNUAL.name())) {
                try {
                    // skuDetails = bp.getSubscriptionListingDetails("monthly");
                    skuDetails = bp.getSubscriptionListingDetails(planResponse.getData().get(i).getIdentifier());
                    purchaseModel.setPrice("" + skuDetails.priceValue);
                    purchaseModel.setPurchaseOptions(subscriptionType);
                    purchaseModel.setOfferPeriod(VodOfferType.ANNUAL.name());
                    purchaseModel.setTitle(planResponse.getData().get(i).getTitle());
                    purchaseModel.setIdentifier(planResponse.getData().get(i).getIdentifier());
                    purchaseModel.setCurrency(skuDetails.currency);
                } catch (Exception e) {
                    // purchaseModel.setPrice("" + planResponse.getData().get(i).getPrice());
                    purchaseModel.setPurchaseOptions(subscriptionType);
                    purchaseModel.setOfferPeriod(VodOfferType.ANNUAL.name());
                    purchaseModel.setTitle(planResponse.getData().get(i).getTitle());
                    purchaseModel.setIdentifier(planResponse.getData().get(i).getIdentifier());
                    // purchaseModel.setCurrency(planResponse.getData().get(i).getCurrencyCode());

                }
                if (skuDetails != null)
                    alPurchaseOptions.add(purchaseModel);
            }


        } catch (Exception ignored) {

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            Toast.makeText(MemberShipPlanActivity.this, "" + data.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bp != null) {
            bp.release();
        }

    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
//         * Called when requested PRODUCT ID was successfully purchased
        showLoading(getBinding().progressBar, true);

        try {
           /* jsonObj = new JsonObject();

            jsonObj.addProperty("planName", selectedPurchaseOption);
            jsonObj.add("sku", null);
            jsonObj.add("appUserId", null);
            jsonObj.addProperty("purchaseSource", "ANDROID");


            JsonObject jsonObjPurchaseMetaData = new JsonObject();

            JsonObject jsonObjTransactionDetails = new JsonObject();

            jsonObjTransactionDetails.addProperty("productId", "" + details.productId);
            jsonObjTransactionDetails.addProperty("orderId", "" + details.orderId);
            jsonObjTransactionDetails.addProperty("purchaseToken", "" + details.purchaseToken);
            jsonObjTransactionDetails.addProperty("purchaseTime", details.purchaseTime.getTime());


            JsonObject jsonObjectPurchaseInfo = new JsonObject();
            jsonObjectPurchaseInfo.add("responseData", null);
            jsonObjectPurchaseInfo.addProperty("signature", "" + details.purchaseInfo.signature);
            JsonObject jsonObjectPurchaseData = new JsonObject();

            jsonObjectPurchaseData.addProperty("orderId", "" + details.purchaseInfo.purchaseData.orderId);
            jsonObjectPurchaseData.addProperty("packageName", "" + details.purchaseInfo.purchaseData.packageName);
            jsonObjectPurchaseData.addProperty("productId", "" + details.purchaseInfo.purchaseData.productId);
            jsonObjectPurchaseData.addProperty("purchaseTime", details.purchaseInfo.purchaseData.purchaseTime.getTime());
            jsonObjectPurchaseData.addProperty("purchaseState", "" + details.purchaseInfo.purchaseData.purchaseState);
            jsonObjectPurchaseData.addProperty("developerPayload", "" + details.purchaseInfo.purchaseData.developerPayload);
            jsonObjectPurchaseData.addProperty("purchaseToken", "" + details.purchaseInfo.purchaseData.purchaseToken);
            jsonObjectPurchaseData.addProperty("autoRenewing", "" + details.purchaseInfo.purchaseData.autoRenewing);

            jsonObjectPurchaseInfo.add("purchaseData", jsonObjectPurchaseData);
            jsonObjTransactionDetails.add("purchaseInfo", jsonObjectPurchaseInfo);
            jsonObjPurchaseMetaData.add("transactionDetails", jsonObjTransactionDetails);
            jsonObj.add("purchaseMeta", jsonObjPurchaseMetaData);
*/


            // hitCancelSubscription();

            if (details.purchaseToken != null) {
                updatePayment("", "PAYMENT_DONE", details.purchaseToken, paymentId);
            } else {
                updatePayment("", "FAILED", "inapp:com.enveu.demo:android.test.purchased", paymentId);
            }


        } catch (
                JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    private void updatePayment(String billinhError, String paymentStatus, String purchaseToken, String paymentId) {
        viewModel.updatePurchase(billingError, paymentStatus, strToken, purchaseToken, paymentId, orderId, model).observe(MemberShipPlanActivity.this, new Observer<PurchaseResponseModel>() {
            @Override
            public void onChanged(@Nullable PurchaseResponseModel responseCancelPurchase) {
                showLoading(getBinding().progressBar, false);
                if (responseCancelPurchase.getStatus()) {
                    if (responseCancelPurchase.getData().getOrderStatus() != null) {
                        if (responseCancelPurchase.getData().getOrderStatus().equalsIgnoreCase("COMPLETED")) {
                            AppCommonMethod.isPurchase = true;
                            Toast.makeText(MemberShipPlanActivity.this, MemberShipPlanActivity.this.getResources().getString(R.string.purchased_successfully), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            dismissLoading(getBinding().progressBar);
                            showDialog(MemberShipPlanActivity.this.getResources().getString(R.string.error), getResources().getString(R.string.payment_error) + " " + "support@mvhub.com");
                        }

                    }
                    // hitApiDoPurchase();
                } else if (responseCancelPurchase.getResponseCode() == 4302) {
                    isloggedout = true;
                    dismissLoading(getBinding().progressBar);
                    showDialog(MemberShipPlanActivity.this.getResources().getString(R.string.logged_out), responseCancelPurchase.getDebugMessage() == null ? "" : responseCancelPurchase.getDebugMessage().toString());

                } else if (responseCancelPurchase.getResponseCode() == 4011) {
                    dismissLoading(getBinding().progressBar);
                } else {
                    dismissLoading(getBinding().progressBar);
                    showDialog(MemberShipPlanActivity.this.getResources().getString(R.string.error), responseCancelPurchase.getDebugMessage() == null ? "" : responseCancelPurchase.getDebugMessage().toString());
                }

            }
        });

    }


    public void hitCancelSubscription() {

        viewModel.cancelPlan(strToken).observe(MemberShipPlanActivity.this, new Observer<ResponseCancelPurchase>() {
            @Override
            public void onChanged(@Nullable ResponseCancelPurchase responseCancelPurchase) {
                if (responseCancelPurchase.isStatus()) {
                    hitApiDoPurchase();
                } else {
                    if (responseCancelPurchase.getResponseCode() == 401) {
                        isloggedout = true;
                        showDialog(MemberShipPlanActivity.this.getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));
                    }
                }
            }
        });


    }

    String orderId;
    String assetSKU;

    public void hitApiDoPurchase() {
        viewModel.createNewPurchaseRequest(strToken, jsonObj, model, "").observe(MemberShipPlanActivity.this, purchaseResponseModel -> {

            if (purchaseResponseModel.getStatus()) {
                if (purchaseResponseModel.getData().getOrderId() != null && !purchaseResponseModel.getData().getOrderId().equalsIgnoreCase("")) {
                    orderId = purchaseResponseModel.getData().getOrderId();
                    Log.w("orderIdOf", orderId);
                    showLoading(getBinding().progressBar, false);
                    callInitiatePaymentApi(orderId);
                }
            } else if (purchaseResponseModel.getResponseCode() == 4302) {
                isloggedout = true;
                dismissLoading(getBinding().progressBar);
                showDialog(MemberShipPlanActivity.this.getResources().getString(R.string.logged_out), purchaseResponseModel.getDebugMessage() == null ? "" : purchaseResponseModel.getDebugMessage().toString());

            } else {
                dismissLoading(getBinding().progressBar);
                showDialog(MemberShipPlanActivity.this.getResources().getString(R.string.error), purchaseResponseModel.getDebugMessage() == null ? "" : purchaseResponseModel.getDebugMessage().toString());
            }
        });

    }


    @Override
    public void onPurchaseCardClick(boolean click, int poss, String planName, PurchaseModel purchaseModel) {
        if (isCardClickable) {
            model = purchaseModel;
            selectedPurchaseOption = planName;
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                getBinding().btnBuy.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.rounded_button));
            } else {
                getBinding().btnBuy.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button));
            }
            getBinding().btnBuy.setEnabled(true);
            getBinding().btnBuy.setClickable(true);

        } else {
            getBinding().btnBuy.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.disable_button_color));
            getBinding().btnBuy.setEnabled(false);
            getBinding().btnBuy.setClickable(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    String paymentId;

    private void callInitiatePaymentApi(String orderId) {
        viewModel.callInitiatePaymet(strToken, orderId).observe(MemberShipPlanActivity.this, purchaseResponseModel -> {
            try {
                if (purchaseResponseModel.getStatus()) {
                    if (purchaseResponseModel.getData().getPaymentId() != null && !purchaseResponseModel.getData().getPaymentId().toString().equalsIgnoreCase("")) {
                        paymentId = purchaseResponseModel.getData().getPaymentId().toString();
                        Log.w("orderIdOf", paymentId);
                        //updatePayment("sample_token",paymentId);
                        buySubscription();
                    }

                    // hitInitiatePayment();product
                    // finish();
                    //  new ActivityLauncher(PurchaseActivity.this).detailScreen(PurchaseActivity.this, DetailActivity.class, assetId, "0", true);
                } else if (purchaseResponseModel.getResponseCode() == 4302) {
                    isloggedout = true;
                    dismissLoading(getBinding().progressBar);
                    showDialog(MemberShipPlanActivity.this.getResources().getString(R.string.logged_out), purchaseResponseModel.getDebugMessage() == null ? "" : purchaseResponseModel.getDebugMessage().toString());

                } else {
                    dismissLoading(getBinding().progressBar);
                    showDialog(MemberShipPlanActivity.this.getResources().getString(R.string.error), purchaseResponseModel.getDebugMessage() == null ? "" : purchaseResponseModel.getDebugMessage().toString());
                }
            } catch (Exception e) {

            }


        });

    }

    public void buySubscription() {
        if (!StringUtils.isNullOrEmptyOrZero(model.getPurchaseOptions())) {
            showHideProgress(getBinding().progressBar);

            if (model.getPurchaseOptions().equalsIgnoreCase(VodOfferType.RECURRING_SUBSCRIPTION​.name())) {
                if (model.isSelected()) {
                    bp.subscribe(MemberShipPlanActivity.this, model.getIdentifier(), "DEVELOPER PAYLOAD HERE");
                } else {
                    Toast.makeText(MemberShipPlanActivity.this, getResources().getString(R.string.already_subscriber), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


}

