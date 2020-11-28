package me.vipa.app.fragments.more.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import me.vipa.app.MvHubPlusApplication;
import me.vipa.app.activities.homeactivity.viewmodel.HomeViewModel;
import me.vipa.app.activities.notification.ui.NotificationActivity;
import me.vipa.app.activities.profile.ui.ProfileActivityNew;
import me.vipa.app.activities.redeemcoupon.RedeemCouponActivity;
import me.vipa.app.activities.settings.ActivitySettings;
import me.vipa.app.activities.usermanagment.ui.ChangePasswordActivity;
import me.vipa.app.activities.watchList.ui.WatchListActivity;
import me.vipa.app.baseModels.BaseBindingFragment;
import me.vipa.app.cms.HelpActivity;
import me.vipa.app.fragments.dialog.AlertDialogFragment;
import me.vipa.app.fragments.dialog.AlertDialogSingleButtonFragment;
import me.vipa.app.fragments.more.adapter.MoreListAdapter;
import com.vipa.app.R;
import me.vipa.app.activities.membershipplans.ui.MemberShipPlanActivity;
import me.vipa.app.beanModel.AppUserModel;
import me.vipa.app.beanModel.configBean.ResponseConfig;
import me.vipa.app.beanModel.responseModels.RegisterSignUpModels.DataResponseRegister;
import me.vipa.app.beanModel.userProfile.UserProfileResponse;
import me.vipa.app.callbacks.commonCallbacks.MoreItemClickListener;
import com.vipa.app.databinding.FragmentMoreBinding;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.helpers.CheckInternetConnection;

import me.vipa.app.utils.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.StringUtils;
import me.vipa.app.utils.helpers.ToastHandler;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import me.vipa.app.activities.homeactivity.viewmodel.HomeViewModel;
import me.vipa.app.activities.membershipplans.ui.MemberShipPlanActivity;
import me.vipa.app.activities.notification.ui.NotificationActivity;
import me.vipa.app.activities.profile.ui.ProfileActivityNew;
import me.vipa.app.activities.redeemcoupon.RedeemCouponActivity;
import me.vipa.app.activities.settings.ActivitySettings;
import me.vipa.app.activities.usermanagment.ui.ChangePasswordActivity;
import me.vipa.app.activities.watchList.ui.WatchListActivity;
import me.vipa.app.baseModels.BaseBindingFragment;
import me.vipa.app.fragments.dialog.AlertDialogFragment;
import me.vipa.app.fragments.dialog.AlertDialogSingleButtonFragment;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.webstreamindonesia.nonton.activities.membershipPlan.ui.MemberShipPlanActivity;


@SuppressWarnings("StatementWithEmptyBody")
public class MoreFragment extends BaseBindingFragment<FragmentMoreBinding> implements MoreItemClickListener, AlertDialogFragment.AlertDialogListener {
    public IntentFilter intentFilter;
    boolean isloggedout = false;
    boolean isHomeDirect = false;
    private android.content.res.Resources res;
    private KsPreferenceKeys preference;
    private String isLogin;
    private List<String> mListVerify;
    private List<String> mListLogin;
    private AppSyncBroadcast appSyncBroadcast;
    private HomeViewModel viewModel;
    private boolean flagLogIn;
    private boolean flagVerify;
    private long mLastClickTime = 0;
    private MoreFragmentInteraction mListener;

    @Override
    public FragmentMoreBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return FragmentMoreBinding.inflate(inflater);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        res = getResources();
        FacebookSdk.sdkInitialize(getActivity());
//
//        ((HomeActivity) Objects.requireNonNull(getActivity())).updateApi(click -> {
//            if (click) {
//                if (!NetworkConnectivity.isOnline(Objects.requireNonNull(getActivity()))) {
//                    getBinding().noConnectionLayout.setVisibility(View.VISIBLE);
//                    getBinding().profileFrame.setVisibility(View.GONE);
//                }
//            }
//        });

    }

    public void startMoreFragment() {
        modelCall();
//
//        if (NetworkConnectivity.isOnline(Objects.requireNonNull(getActivity()))) {
//            getBinding().noConnectionLayout.setVisibility(View.GONE);
//            getBinding().profileFrame.setVisibility(View.VISIBLE);
//            modelCall();
//        } else {
//            getBinding().noConnectionLayout.setVisibility(View.VISIBLE);
//            getBinding().profileFrame.setVisibility(View.GONE);
//        }

        getBinding().connection.retryTxt.setOnClickListener(view -> startMoreFragment());

    }

    @Override
    public void onStart() {
        super.onStart();
        if (preference != null) {
            isLogin = preference.getAppPrefLoginStatus();
            if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
                getBinding().userNameWords.setText(AppCommonMethod.getUserName(preference.getAppPrefUserName()));
                getBinding().usernameTv.setText(preference.getAppPrefUserName());
            }
        }

        Logger.i("TAG", "Tried to unregister the reciver when it's not registered");

    }

    @Override
    public void onResume() {
        super.onResume();
        intentFilter = new IntentFilter();
        intentFilter.addAction("NONTON_PROFILE_UPDATE");
        appSyncBroadcast = new AppSyncBroadcast();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).registerReceiver(appSyncBroadcast, intentFilter);
        isloggedout = false;
        startMoreFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).unregisterReceiver(appSyncBroadcast);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                Logger.i("TAG", "Tried to unregister the reciver when it's not registered");
            } else {
                throw e;
            }
        }
    }

    private void modelCall() {

        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(HomeViewModel.class);

        String[] label1 = getActivity().getResources().getStringArray(R.array.more_with_verify);
        String[] label2 = getActivity().getResources().getStringArray(R.array.more_with_login);
        String[] label3 = getActivity().getResources().getStringArray(R.array.more_logout);

        mListVerify = new ArrayList<>();
        mListVerify.addAll(Arrays.asList(label1));
        List<String> mListLogOut = new ArrayList<>(Arrays.asList(label3));
        mListLogin = new ArrayList<>();
        mListLogin.addAll(Arrays.asList(label2));

        preference = KsPreferenceKeys.getInstance();
        isLogin = preference.getAppPrefLoginStatus();
        if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            String tempResponse = preference.getAppPrefUser();
            if (!StringUtils.isNullOrEmptyOrZero(tempResponse)) {
                setVerify(getActivity());
            } else {
                String tempResponseApi = preference.getAppPrefProfile();
                setVerifyApi(tempResponseApi);
            }
        } else {
            getBinding().loginBtn.setVisibility(View.VISIBLE);
            AppCommonMethod.guestTitle(getBaseActivity(),getBinding().userNameWords, getBinding().usernameTv, preference);
            getBinding().usernameTv.setVisibility(View.VISIBLE);
            setUIComponets(mListLogOut, false);
        }
    }

    private void setUIComponets(List<String> mList, boolean isLogin) {

        MoreListAdapter adapter = new MoreListAdapter(getActivity(), mList, MoreFragment.this, isLogin);
        getBinding().recyclerViewMore.hasFixedSize();
        getBinding().recyclerViewMore.setNestedScrollingEnabled(false);
        getBinding().recyclerViewMore.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        getBinding().recyclerViewMore.setAdapter(adapter);

//        getBinding().loginBtn.setOnClickListener(v -> );
        getBinding().loginBtn.setOnClickListener(v -> {
            mListener.onLoginClicked();
        });
    }

    public void clickEvent() {
        try {
            preference = KsPreferenceKeys.getInstance();
            String token = preference.getAppPrefAccessToken();
            viewModel.hitUserProfile(getContext(), token).observe(getActivity(), userProfileResponse -> {
                if (userProfileResponse != null) {
                    if (userProfileResponse.getStatus()) {
                        updateUI(userProfileResponse);
                    } else {
                        if (userProfileResponse.getDebugMessage() != null) {

                        } else {

                        }
                    }
                }
            });

        }catch (Exception ignored){

        }

    }

    private void updateUI(UserProfileResponse userProfileResponse) {
        try {
            preference.setAppPrefUserName(String.valueOf(userProfileResponse.getData().getName()));
            preference.setAppPrefUserEmail(String.valueOf(userProfileResponse.getData().getEmail()));
            getBinding().userNameWords.setText(AppCommonMethod.getUserName(preference.getAppPrefUserName()));
            getBinding().usernameTv.setText(preference.getAppPrefUserName());
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(@NotNull String caption) {
        boolean loginStatus = preference.getAppPrefLoginStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString());
        String isFacebook = preference.getAppPrefLoginType();
        if (caption.equals(getString(R.string.profile))) {
            if (loginStatus)
                new ActivityLauncher(getActivity()).ProfileActivityNew(getActivity(), ProfileActivityNew.class);
            else
                mListener.onLoginClicked();
        } else if (caption.equals(getString(R.string.change_password))) {
            if (loginStatus)
              /*  if (isFacebook.equalsIgnoreCase(AppConstants.UserLoginType.FbLogin.toString())) {

                } else {*/
                    new ActivityLauncher(getActivity()).changePassword(getActivity(), ChangePasswordActivity.class);
               /* }*/

            else
                mListener.onLoginClicked();
        } else if (caption.equals(getString(R.string.notification))) {
            if (loginStatus)
                new ActivityLauncher(getActivity()).notificationActivity(getActivity(), NotificationActivity.class);
            else
                mListener.onLoginClicked();
        } else if (caption.equals(getString(R.string.term_condition))) {
            Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), HelpActivity.class).putExtra("type", "1"));
        } else if (caption.equals(getString(R.string.privacy_policy))) {
            Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), HelpActivity.class).putExtra("type", "2"));
        } else if (caption.equals(getString(R.string.my_watchlist))) {

            if (loginStatus)
                new ActivityLauncher(getActivity()).watchHistory(getActivity(), WatchListActivity.class, getString(R.string.my_watchlist), false);
            else
                mListener.onLoginClicked();

        } else if (caption.equals(getString(R.string.my_history))) {
            if (loginStatus)
                new ActivityLauncher(getActivity()).watchHistory(getActivity(), WatchListActivity.class, Objects.requireNonNull(getActivity()).getResources().getString(R.string.my_history), true);
            else
                mListener.onLoginClicked();
        } else if (caption.equals(getString(R.string.my_downloads))) {
            if (loginStatus)
                new ActivityLauncher(getActivity()).launchMyDownloads();
            else
                mListener.onLoginClicked();
        } else if (caption.equals(Objects.requireNonNull(getActivity()).getResources().getString(R.string.sign_out))) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            flagLogIn = true;
            flagVerify = false;
            showAlertDialog(getResources().getString(R.string.sign_out), getResources().getString(R.string.logout_confirmation));
        } else if (caption.equalsIgnoreCase(getActivity().getResources().getString(R.string.membership_plan))) {
            if (loginStatus) {
                if (NetworkConnectivity.isOnline(getActivity())) {
                    Intent intent = new Intent(getActivity(), MemberShipPlanActivity.class);
                    startActivity(intent);
                }else {
                    new ToastHandler(getActivity()).show(getResources().getString(R.string.no_connection));
                }

            } else {
                mListener.onLoginClicked();
            }

        } else if (caption.equalsIgnoreCase(getActivity().getResources().getString(R.string.redeem_coupon))) {
            if (loginStatus) {
                Intent intent = new Intent(getActivity(), RedeemCouponActivity.class);
                startActivity(intent);
            } else {
                mListener.onLoginClicked();
            }

        } else if (caption.equalsIgnoreCase(getActivity().getResources().getString(R.string.setting_title))) {

            Intent intent = new Intent(getActivity(), ActivitySettings.class);
            startActivity(intent);
        }

    }


    public void hitApiVerifyUser() {
        getBinding().progressBar.setVisibility(View.VISIBLE);
        getBinding().progressBar.bringToFront();
        if (CheckInternetConnection.isOnline(Objects.requireNonNull(getActivity()))) {
            String token = preference.getAppPrefAccessToken();
            getBinding().progressBar.setVisibility(View.VISIBLE);
            viewModel.hitVerify(token).observe(getActivity(), jsonObject ->
                    {
                        getBinding().progressBar.setVisibility(View.GONE);
                        if (jsonObject.isStatus()) {

                        } else {
                            if (jsonObject.getResponseCode() == 401) {
                                isloggedout = true;
                                flagVerify = false;
                                showDialog(getActivity().getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));
                            }
                        }
                    }
            );
        } else
            new ToastHandler(getActivity()).show(getActivity().getResources().getString(R.string.no_internet_connection));
    }

    private void showAlertDialog(String title, String msg) {
        FragmentManager fm = getFragmentManager();
        AlertDialogFragment alertDialog = AlertDialogFragment.newInstance(title, msg, getResources().getString(R.string.ok), getResources().getString(R.string.cancel));
        alertDialog.setAlertDialogCallBack(this);
        alertDialog.show(Objects.requireNonNull(fm), "fragment_alert");
    }

    private void showDialog(String title, String message) {
        FragmentManager fm = getFragmentManager();
        AlertDialogSingleButtonFragment alertDialog = AlertDialogSingleButtonFragment.newInstance(title, message, getResources().getString(R.string.ok));
        alertDialog.setCancelable(false);
        alertDialog.setAlertDialogCallBack(this);
        alertDialog.show(Objects.requireNonNull(fm), "fragment_alert");
    }

    public void hitApiLogout() {
        if (getActivity() != null)
            if (CheckInternetConnection.isOnline(Objects.requireNonNull(getActivity()))) {
                String isFacebook = preference.getAppPrefLoginType();
                if (isFacebook.equalsIgnoreCase(AppConstants.UserLoginType.FbLogin.toString())) {
                    LoginManager.getInstance().logOut();
                }
                String token = preference.getAppPrefAccessToken();
                showLoading(getBinding().progressBar, true, getActivity());
                dismissLoading(getBinding().progressBar, getActivity());
                String strCurrentTheme = KsPreferenceKeys.getInstance().getCurrentTheme();
                String strCurrentLanguage = KsPreferenceKeys.getInstance().getAppLanguage();
                int languagePosition=KsPreferenceKeys.getInstance().getAppPrefLanguagePos();
                preference.clear();
                KsPreferenceKeys.getInstance().setCurrentTheme(strCurrentTheme);
                KsPreferenceKeys.getInstance().setAppLanguage(strCurrentLanguage);
                KsPreferenceKeys.getInstance().setAppPrefLanguagePos(languagePosition);
                modelCall();
                Logger.w("currentLang-->>",strCurrentLanguage);
                if (strCurrentLanguage.equalsIgnoreCase("Thai") || KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("हिंदी") ){
                    AppCommonMethod.updateLanguage("th", MvHubPlusApplication.getInstance());
                } else if (strCurrentLanguage.equalsIgnoreCase("English")){
                    AppCommonMethod.updateLanguage("en", MvHubPlusApplication.getInstance());
                }
                //AppCommonMethod.updateLanguage(strCurrentLanguage, getActivity());
                viewModel.hitLogout(false, token).observe(getActivity(), jsonObject -> {
                    int msg = Objects.requireNonNull(jsonObject).get(AppConstants.API_RESPONSE_CODE).getAsInt();
                    if (msg == AppConstants.RESPONSE_CODE_ERROR) {
                        flagLogIn = false;
                        dismissLoading(getBinding().progressBar, getActivity());
                    } else {

                    }
                });

            } else {
                dismissLoading(getBinding().progressBar, getActivity());

                new ToastHandler(getActivity()).show(getActivity().getResources().getString(R.string.no_internet_connection));
            }
    }

    public void hitApiConfig() {
        ApiInterface endpoint = RequestConfig.getClient().create(ApiInterface.class);
        Call<ResponseConfig> call = endpoint.getConfiguration("true");
        call.enqueue(new Callback<ResponseConfig>() {
            @Override
            public void onResponse(@NonNull Call<ResponseConfig> call, @NonNull Response<ResponseConfig> response) {
                dismissLoading(getBinding().progressBar, getActivity());
                if (response.body() != null) {
                    AppCommonMethod.urlPoints = response.body().getData().getCloudFrontEndpoint();
                    ResponseConfig cl = response.body();
                    KsPreferenceKeys ksPreferenceKeys = KsPreferenceKeys.getInstance();
                    Gson gson = new Gson();
                    String json = gson.toJson(cl);
                    AppCommonMethod.urlPoints = /*AppConstants.PROFILE_URL +*/ response.body().getData().getImageTransformationEndpoint();

                    ksPreferenceKeys.setAppPrefLastConfigHit(String.valueOf(System.currentTimeMillis()));
                    ksPreferenceKeys.setAppPrefLoginStatus(AppConstants.UserStatus.Logout.toString());
                    ksPreferenceKeys.setAppPrefAccessToken("");
                    ksPreferenceKeys.setAppPrefConfigResponse(json);
                    ksPreferenceKeys.setAppPrefVideoUrl(response.body().getData().getCloudFrontVideoEndpoint());
                    ksPreferenceKeys.setAppPrefAvailableVersion(response.body().getData().getUpdateInfo().getAvailableVersion());
                    ksPreferenceKeys.setAppPrefCfep(AppCommonMethod.urlPoints);
                    ksPreferenceKeys.setAppPrefConfigVersion(String.valueOf(response.body().getData().getConfigVersion()));
                    ksPreferenceKeys.setAppPrefServerBaseUrl(response.body().getData().getServerBaseURL());


                    // versionValidator.version(false, currentVersion, currentVersion);
                    Glide.with(getActivity()).load(getActivity().getResources().getDrawable(R.drawable.default_profile_pic)).into(getBinding().ivProfilePic);
                    modelCall();
                    if (isHomeDirect) {

                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseConfig> call, @NonNull Throwable t) {
                dismissLoading(getBinding().progressBar, getActivity());
            }
        });

    }

    public void updateAppSync(Context context) {
        isLogin = preference.getAppPrefLoginStatus();
        if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            setVerify(context);
        }

    }

    @Override
    public void onFinishDialog() {
        if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Thai") || KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("हिंदी") ){
            AppCommonMethod.updateLanguage("th", MvHubPlusApplication.getInstance());
        } else if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")){
            AppCommonMethod.updateLanguage("en", MvHubPlusApplication.getInstance());
        }
        if (flagLogIn) {
            AppCommonMethod.guestTitle(getBaseActivity(),getBinding().userNameWords, getBinding().usernameTv, preference);
            hitApiLogout();
            flagLogIn = false;
        } else if (flagVerify) {
            hitApiVerifyUser();
            flagVerify = false;
        } else if (isloggedout) {
            hitApiLogout();
            isHomeDirect = true;
            isloggedout = false;

        }
    }

    public void setVerify(Context context) {
        String tempResponse = preference.getAppPrefUser();
        if (!StringUtils.isNullOrEmptyOrZero(tempResponse)) {
            AppUserModel dataModel = new Gson().fromJson(tempResponse, AppUserModel.class);
            getBinding().loginBtn.setVisibility(View.GONE);
            getBinding().usernameTv.setVisibility(View.VISIBLE);
            getBinding().userNameWords.setText(AppCommonMethod.getUserName(dataModel.getName()));
            getBinding().usernameTv.setText(dataModel.getName());
            if (!StringUtils.isNullOrEmpty(dataModel.getProfilePicURL()))
                try {
                    setProfilePic(dataModel.getProfilePicURL());
                } catch (Exception e) {
                    Logger.e("MoreFragment", "" + e.toString());
                }

            if (dataModel != null) {
                /*if (dataModel.isVerified()) {
                    setUIComponets(mListLogin, true);
                } else
                    setUIComponets(mListVerify, true);*/
                setUIComponets(mListLogin, true);
            } else {
                setUIComponets(mListLogin, true);
            }

        }
    }

    public void setVerifyApi(String tempResponse) {
        DataResponseRegister ddModel;
        ddModel = new Gson().fromJson(tempResponse, DataResponseRegister.class);

        if (!StringUtils.isNullOrEmptyOrZero(tempResponse)) {
            getBinding().loginBtn.setVisibility(View.GONE);
            getBinding().usernameTv.setVisibility(View.VISIBLE);
            getBinding().userNameWords.setText(AppCommonMethod.getUserName(preference.getAppPrefUserName()));
            getBinding().usernameTv.setText(preference.getAppPrefUserName());
            setUIComponets(mListLogin, true);
           /* if (ddModel.isVerified()) {
                setUIComponets(mListLogin, true);
            } else
                setUIComponets(mListVerify, true);*/
        }

    }

    public void setProfilePic(String key) {
        String url1 = preference.getAppPrefCfep();
        StringBuilder stringBuilder = new StringBuilder();
        if (key.contains("http")) {
            stringBuilder.append(url1).append("/fit-in/200x200/filters:quality(100):max_bytes(400)/").append(key);
        } else {
            if (StringUtils.isNullOrEmpty(url1)) {
                url1 = AppCommonMethod.urlPoints;
                preference.setAppPrefCfep(url1);
            }
            String url2 = AppConstants.PROFILE_FOLDER;
            stringBuilder = stringBuilder.append(url1).append(url2).append(key);
        }
        Glide.with(Objects.requireNonNull(getActivity()))
                .asBitmap()
                .load(stringBuilder.toString())
                .apply(AppCommonMethod.options)
                .into(getBinding().ivProfilePic);

    }

    public class AppSyncBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                updateAppSync(context);
            } catch (Exception e) {
                Logger.e("MoreFragment", "" + e.toString());

            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (MoreFragmentInteraction) getActivity();
    }

    public interface MoreFragmentInteraction {
        void onLoginClicked();
    }

}