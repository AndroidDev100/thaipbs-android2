package me.vipa.app.fragments.more.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import me.vipa.app.MvHubPlusApplication;
import me.vipa.app.SDKConfig;
import me.vipa.app.activities.ManageAccount.UI.ManageAccount;
import me.vipa.app.activities.OtherApplication.UI.OtherApplication;
import me.vipa.app.activities.downloads.MyDownloads;
import me.vipa.app.activities.homeactivity.ui.HomeActivity;
import me.vipa.app.activities.homeactivity.viewmodel.HomeViewModel;
import me.vipa.app.activities.notification.ui.NotificationActivity;
import me.vipa.app.activities.onBoarding.UI.OnBoardingTab;
import me.vipa.app.activities.profile.ui.ProfileActivityNew;
import me.vipa.app.activities.purchase.callBack.EntitlementStatus;
import me.vipa.app.activities.purchase.planslayer.GetPlansLayer;
import me.vipa.app.activities.redeemcoupon.RedeemCouponActivity;
import me.vipa.app.activities.search.ui.FilterIconActivity;
import me.vipa.app.activities.settings.ActivitySettings;
import me.vipa.app.activities.splash.ui.ActivitySplash;
import me.vipa.app.activities.usermanagment.ui.ChangePasswordActivity;
import me.vipa.app.activities.usermanagment.ui.LoginActivity;
import me.vipa.app.activities.usermanagment.viewmodel.RegistrationLoginViewModel;
import me.vipa.app.activities.watchList.ui.WatchListActivity;
import me.vipa.app.baseModels.BaseBindingFragment;
import me.vipa.app.beanModel.userProfile.UserProfileResponse;
import me.vipa.app.cms.HelpActivity;
import me.vipa.app.fragments.dialog.AlertDialogFragment;
import me.vipa.app.fragments.dialog.AlertDialogSingleButtonFragment;
import me.vipa.app.fragments.dialog.KidsModePinDialogFragment;
import me.vipa.app.fragments.more.adapter.MoreListAdapter;
import me.vipa.app.R;
import me.vipa.app.activities.membershipplans.ui.MemberShipPlanActivity;
import me.vipa.app.beanModel.AppUserModel;
import me.vipa.app.beanModel.configBean.ResponseConfig;
import me.vipa.app.beanModel.responseModels.RegisterSignUpModels.DataResponseRegister;
import me.vipa.app.callbacks.commonCallbacks.MoreItemClickListener;
import me.vipa.app.databinding.FragmentMoreBinding;
import me.vipa.app.networking.apiendpoints.ApiInterface;
import me.vipa.app.networking.apiendpoints.RequestConfig;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.helpers.CheckInternetConnection;

import me.vipa.app.utils.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.SharedPrefHelper;
import me.vipa.app.utils.helpers.StringUtils;
import me.vipa.app.utils.helpers.ToastHandler;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.mmtv.utils.helpers.downloads.DownloadHelper;

import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

//import com.webstreamindonesia.nonton.activities.membershipPlan.ui.MemberShipPlanActivity;


@SuppressWarnings("StatementWithEmptyBody")
public class MoreFragment extends BaseBindingFragment<FragmentMoreBinding> implements MoreItemClickListener, AlertDialogFragment.AlertDialogListener, KidsModePinDialogFragment.CallBackListenerOkClick {
    public IntentFilter intentFilter;
    boolean isloggedout = false;
    boolean isHomeDirect = false;
    private android.content.res.Resources res;
    private KsPreferenceKeys preference;
    private String isLogin;
    private List<String> mListVerify;
    private List<String> mListLogin;
    private List<String> mListKidsMode;
    private AppSyncBroadcast appSyncBroadcast;
    private HomeViewModel viewModel;
    private RegistrationLoginViewModel registrationLoginViewModel;
    private boolean flagLogIn;
    private boolean flagVerify;
    private long mLastClickTime = 0;
    private MoreFragmentInteraction mListener;
    private boolean isKidsMode;
    private String pin = "";

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
                getBinding().titleLayout.setVisibility(View.GONE);
                getBinding().ivProfilePic.setVisibility(View.VISIBLE);
//                getBinding().userNameWords.setText(AppCommonMethod.getUserName(preference.getAppPrefUserName()));
                getBinding().usernameTv.setText(preference.getAppPrefUserName());
                clickEvent();
                //getBinding().titleLayout.setVisibility(View.VISIBLE);
                //getBinding().ivProfilePic.setVisibility(View.GONE);
            } else {
                getBinding().titleLayout.setVisibility(View.VISIBLE);
                getBinding().ivProfilePic.setVisibility(View.GONE);
                // getBinding().userNameWords.setVisibility(View.GONE);

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

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void modelCall() {

        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(HomeViewModel.class);
        registrationLoginViewModel = ViewModelProviders.of(getActivity()).get(RegistrationLoginViewModel.class);

        String[] label1 = getActivity().getResources().getStringArray(R.array.more_with_verify);
        String[] label2 = getActivity().getResources().getStringArray(R.array.more_with_login);
        String[] label3 = getActivity().getResources().getStringArray(R.array.more_logout);
        String[] label4 = getActivity().getResources().getStringArray(R.array.more_kids_mode_login);
        String[] label6 = getActivity().getResources().getStringArray(R.array.more_kids_mode_login_watchlist_condition);
        String[] label5 = getActivity().getResources().getStringArray(R.array.more_kids_mode_logout);


        mListVerify = new ArrayList<>();
        mListVerify.addAll(Arrays.asList(label1));
        List<String> mListLogOut = new ArrayList<>(Arrays.asList(label3));
        mListLogin = new ArrayList<>();
        mListLogin.addAll(Arrays.asList(label2));
        preference = KsPreferenceKeys.getInstance();
        isLogin = preference.getAppPrefLoginStatus();
        isKidsMode = new SharedPrefHelper(getActivity()).getKidsMode();
        mListKidsMode = new ArrayList<>();

        if (isKidsMode) {
            getBinding().loginBtn.setVisibility(View.GONE);
        } else {
            getBinding().loginBtn.setVisibility(View.VISIBLE);
        }

        if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString()) && isKidsMode) {
                mListKidsMode.addAll(Arrays.asList(label6));

            } else {
                mListKidsMode.addAll(Arrays.asList(label4));

            }

        } else {
            mListKidsMode.addAll(Arrays.asList(label5));

        }


        if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
            String tempResponse = preference.getAppPrefUser();
            if (!StringUtils.isNullOrEmptyOrZero(tempResponse)) {
                setVerify(getActivity());
            } else {
                String tempResponseApi = preference.getAppPrefProfile();
                setVerifyApi(tempResponseApi);
            }

        } else {
            // hide login mange kis mode

            //getBinding().loginBtn.setVisibility(View.VISIBLE);
//            AppCommonMethod.guestTitle(getBaseActivity(),getBinding().userNameWords, getBinding().usernameTv, preference);
//            getBinding().usernameTv.setVisibility(View.VISIBLE);
            getBinding().usernameTv.setVisibility(View.GONE);

            if (isKidsMode) {
                setUIComponets(mListKidsMode, false, isKidsMode);
            } else {
                setUIComponets(mListLogOut, false, isKidsMode);

            }


        }
    }

    private void setUIComponets(List<String> mList, boolean isLogin, boolean isKidsMode) {

        MoreListAdapter adapter = new MoreListAdapter(getActivity(), mList, MoreFragment.this, isLogin, isKidsMode);
        getBinding().recyclerViewMore.hasFixedSize();
        getBinding().recyclerViewMore.setNestedScrollingEnabled(false);
        getBinding().recyclerViewMore.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        getBinding().recyclerViewMore.setAdapter(adapter);

        //  getBinding().loginBtn.setOnClickListener(v -> );
        getBinding().loginBtn.setOnClickListener(v -> {
            mListener.onLoginClicked();
        });
    }

    public void clickEvent() {
        try {
            preference = KsPreferenceKeys.getInstance();
            isLogin = preference.getAppPrefLoginStatus();
            if (isLogin.equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {

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
            } else {
                getBinding().titleLayout.setVisibility(View.VISIBLE);
                getBinding().ivProfilePic.setVisibility(View.GONE);
            }


        } catch (Exception ignored) {
            Log.w("ProfileClick", ignored.toString());
        }

    }

    private void updateUI(UserProfileResponse userProfileResponse) {
        try {
            preference.setAppPrefUserName(preference.getAppPrefUserName());
            preference.setAppPrefUserEmail(String.valueOf(userProfileResponse.getData().getEmail()));
            //   getBinding().userNameWords.setText(AppCommonMethod.getUserName(preference.getAppPrefUserName()));
            getBinding().usernameTv.setText(preference.getAppPrefUserName());
            getBinding().titleLayout.setVisibility(View.VISIBLE);
            getBinding().ivProfilePic.setVisibility(View.GONE);
            setUserImage(userProfileResponse);
            Gson gson = new Gson();
            String userProfileData = gson.toJson(userProfileResponse);
            KsPreferenceKeys.getInstance().setUserProfileData(userProfileData);
            Log.w("savedata2", KsPreferenceKeys.getInstance().getUserProfileData());
            String json = KsPreferenceKeys.getInstance().getUserProfileData();
            UserProfileResponse newObject = gson.fromJson(json, UserProfileResponse.class);
            Log.w("savedata3", newObject.toString());


        } catch (Exception e) {

        }
    }

    private String imageUrlId = "";
    private String via = "";

    private void setUserImage(UserProfileResponse userProfileResponse) {
        try {
            if (userProfileResponse.getData().getProfilePicURL() != null && userProfileResponse.getData().getProfilePicURL() != "") {
                getBinding().titleLayout.setVisibility(View.GONE);
                getBinding().ivProfilePic.setVisibility(View.VISIBLE);
                imageUrlId = userProfileResponse.getData().getProfilePicURL().toString();
                via = "Gallery";

                String firstFiveChar = imageUrlId.substring(0, 5);
                if (firstFiveChar.equalsIgnoreCase("https")) {
                    Glide.with(getActivity()).load(userProfileResponse.getData().getProfilePicURL())
                            .placeholder(R.drawable.ic_person_24dp)
                            .error(R.drawable.ic_person_24dp)
                            .into(getBinding().ivProfilePic);
                } else {

                    Glide.with(getActivity()).load(SDKConfig.getInstance().getCLOUD_FRONT_BASE_URL() + SDKConfig.getInstance().getProfileFolder() + userProfileResponse.getData().getProfilePicURL())
                            .placeholder(R.drawable.ic_person_24dp)
                            .error(R.drawable.ic_person_24dp)
                            .into(getBinding().ivProfilePic);
                }
            } else {

                if (userProfileResponse.getData().getCustomData().getProfileAvatar() != null) {
                    getBinding().titleLayout.setVisibility(View.GONE);
                    getBinding().ivProfilePic.setVisibility(View.VISIBLE);
                    for (int i = 0; i < SDKConfig.getInstance().getAvatarImages().size(); i++) {
                        if (userProfileResponse.getData().getCustomData().getProfileAvatar().equalsIgnoreCase(SDKConfig.getInstance().getAvatarImages().get(i).getIdentifier())) {
                            imageUrlId = SDKConfig.getInstance().getAvatarImages().get(i).getIdentifier();
                            via = "Avatar";

                            Glide.with(getActivity()).load(SDKConfig.getInstance().getAvatarImages().get(i).getUrl())
                                    .placeholder(R.drawable.ic_person_24dp)
                                    .error(R.drawable.ic_person_24dp)
                                    .into(getBinding().ivProfilePic);

                        }
                    }
                } else {
                    getBinding().titleLayout.setVisibility(View.GONE);
                    getBinding().ivProfilePic.setVisibility(View.VISIBLE);
                    imageUrlId = SDKConfig.getInstance().getAvatarImages().get(0).getIdentifier();
                    via = "Avatar";
                    Glide.with(getActivity()).load(SDKConfig.getInstance().getAvatarImages().get(0).getUrl())
                            .placeholder(R.drawable.ic_person_24dp)
                            .error(R.drawable.ic_person_24dp)
                            .into(getBinding().ivProfilePic);

                }
            }

        } catch (Exception ignored) {

        }
    }

    @Override
    public void onClick(@NotNull String caption) {
        boolean loginStatus = preference.getAppPrefLoginStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString());
        String isFacebook = preference.getAppPrefLoginType();
        if (caption.equals(getString(R.string.manage_account))) {
            if (loginStatus)
                new ActivityLauncher(getActivity()).manageAccount(getActivity(), ManageAccount.class);
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
        } else if (caption.equals(getString(R.string.contact_us))) {
            Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), HelpActivity.class).putExtra("type", "3"));
        } else if (caption.equals(getString(R.string.faq))) {
            Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), HelpActivity.class).putExtra("type", "4"));
        } else if (caption.equals(getString(R.string.about_us))) {
            Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), HelpActivity.class).putExtra("type", "5"));
        } else if (caption.equals(getString(R.string.feedback))) {
            Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), HelpActivity.class).putExtra("type", "6"));
        } else if (caption.equals(getString(R.string.other_application))) {
            Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), HelpActivity.class).putExtra("type", "7"));
            //  new ActivityLauncher(getActivity()).otherActivity(getActivity(), OtherApplication.class);
        } else if (caption.equals(getString(R.string.my_watchlist))) {

            if (loginStatus)
                new ActivityLauncher(getActivity()).watchHistory(getActivity(), WatchListActivity.class, getString(R.string.my_watchlist), false);
            else
                mListener.onLoginClicked();

        } else if (caption.equals(getString(R.string.my_download))) {

            if (loginStatus)
                new ActivityLauncher(getActivity()).launchMyDownloads();
            else
                mListener.onLoginClicked();

        } else if (caption.equals(getString(R.string.vipa_kids))) {
            if (loginStatus) {
                String secondaryId = new SharedPrefHelper(getActivity()).getSecondaryAccountId();
                if (secondaryId != null && !secondaryId.isEmpty()) {
                    KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
                    String authToken = preference.getAppPrefAccessToken();
                    switchUserApi(authToken, secondaryId, true);
                } else {
                    KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
                    String authToken = preference.getAppPrefAccessToken();
                    callAllSecondaryAccount(authToken, true);
                }
            } else {
                new SharedPrefHelper(getActivity()).saveKidsMode(true);
                new ActivityLauncher(getActivity()).homeScreen(getActivity(), HomeActivity.class);
                getActivity().setTheme(R.style.MyMaterialTheme_Base_Light);
                KsPreferenceKeys.getInstance().setCurrentTheme(AppConstants.LIGHT_THEME);

            }
        } else if (caption.equals(getString(R.string.leave_kids))) {
            if (loginStatus) {
                getProfileApi();

            } else {
                new SharedPrefHelper(getActivity()).saveKidsMode(false);
                new ActivityLauncher(getActivity()).homeScreen(getActivity(), HomeActivity.class);
                getActivity().setTheme(R.style.MyMaterialTheme_Base_Dark);
                KsPreferenceKeys.getInstance().setCurrentTheme(AppConstants.DARK_THEME);
            }


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
                } else {
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
                boolean bingeWatchEnable = KsPreferenceKeys.getInstance().getBingeWatchEnable();
                showLoading(getBinding().progressBar, true, getActivity());
                dismissLoading(getBinding().progressBar, getActivity());
                String strCurrentTheme = KsPreferenceKeys.getInstance().getCurrentTheme();
                String strCurrentLanguage = KsPreferenceKeys.getInstance().getAppLanguage();
                int languagePosition = KsPreferenceKeys.getInstance().getAppPrefLanguagePos();
                preference.setAppPrefRegisterStatus(AppConstants.UserStatus.Logout.toString());
                preference.clear();
                KsPreferenceKeys.getInstance().setCurrentTheme(strCurrentTheme);
                KsPreferenceKeys.getInstance().setAppLanguage(strCurrentLanguage);
                KsPreferenceKeys.getInstance().setAppPrefLanguagePos(languagePosition);
                KsPreferenceKeys.getInstance().setfirstTimeUser(false);
                KsPreferenceKeys.getInstance().setBingeWatchEnable(bingeWatchEnable);
                getBinding().titleLayout.setVisibility(View.VISIBLE);
                getBinding().ivProfilePic.setVisibility(View.GONE);
                deleteDownloadedVideos();

                //TODO Handle Content Preference Data On Logout
                // AppCommonMethod.getConfigResponse().getData().getAppConfig().setContentPreference(AppCommonMethod.getConfigResponse().getData().getAppConfig().getContentPreference());
                modelCall();
                Logger.w("currentLang-->>", strCurrentLanguage);
                if (strCurrentLanguage.equalsIgnoreCase("Thai") || KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("हिंदी")) {
                    AppCommonMethod.updateLanguage("th", MvHubPlusApplication.getInstance());
                } else if (strCurrentLanguage.equalsIgnoreCase("English")) {
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

    private void deleteDownloadedVideos() {
        try {
            if (getActivity() != null) {
                DownloadHelper downloadHelper = new DownloadHelper(getActivity());
                downloadHelper.deleteAllVideos(getActivity());
            }

        } catch (Exception ignored) {

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
                    Glide.with(getActivity()).load(getActivity().getResources().getDrawable(R.drawable.ic_person_24dp)).into(getBinding().ivProfilePic);
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
        if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Thai") || KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("हिंदी")) {
            AppCommonMethod.updateLanguage("th", MvHubPlusApplication.getInstance());
        } else if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")) {
            AppCommonMethod.updateLanguage("en", MvHubPlusApplication.getInstance());
        }
        if (flagLogIn) {
//            AppCommonMethod.guestTitle(getBaseActivity(),getBinding().userNameWords, getBinding().usernameTv, preference);
            hitApiLogout();

            flagLogIn = false;
            //  getBinding().userNameWords.setVisibility(View.GONE);
        } else if (flagVerify) {
            hitApiVerifyUser();
            flagVerify = false;
        } else if (isloggedout) {
            hitApiLogout();

            //  getBinding().userNameWords.setVisibility(View.GONE);
            isHomeDirect = true;
            isloggedout = false;

        }
    }

    public void setVerify(Context context) {
        String tempResponse = preference.getAppPrefUser();
        if (!StringUtils.isNullOrEmptyOrZero(tempResponse)) {
            AppUserModel dataModel = new Gson().fromJson(tempResponse, AppUserModel.class);
            getBinding().loginBtn.setVisibility(View.GONE);

            //   getBinding().userNameWords.setText(AppCommonMethod.getUserName(dataModel.getName()));
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
                if (isKidsMode) {
                    setUIComponets(mListKidsMode, true, isKidsMode);
                } else {
                    setUIComponets(mListLogin, true, isKidsMode);
                }

            } else {
                if (isKidsMode) {
                    setUIComponets(mListKidsMode, true, isKidsMode);
                } else {
                    setUIComponets(mListLogin, true, isKidsMode);
                }


            }

        }
    }

    public void setVerifyApi(String tempResponse) {
        DataResponseRegister ddModel;
        ddModel = new Gson().fromJson(tempResponse, DataResponseRegister.class);

        if (!StringUtils.isNullOrEmptyOrZero(tempResponse)) {
            getBinding().loginBtn.setVisibility(View.GONE);
            getBinding().usernameTv.setVisibility(View.VISIBLE);

            // getBinding().userNameWords.setText(AppCommonMethod.getUserName(preference.getAppPrefUserName()));
            getBinding().usernameTv.setText(preference.getAppPrefUserName());

            if (isKidsMode) {
                setUIComponets(mListKidsMode, true, isKidsMode);

            } else {
                setUIComponets(mListLogin, true, isKidsMode);

            }

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

    @Override
    public void onContinueClick() {
        Log.e("More Fragmnet CONTINUE", "More Fragmnet CONTINUE");
        String primaryAccountId = new SharedPrefHelper(getActivity()).getPrimaryAccountId();
        if (primaryAccountId != null && !primaryAccountId.isEmpty()) {
            KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
            String authToken = preference.getAppPrefAccessToken();
            switchUserApi(authToken, primaryAccountId, false);
        } else {
            KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
            String authToken = preference.getAppPrefAccessToken();
            callAllSecondaryAccount(authToken, false);

        }
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

    public void switchUserApi(String authToken, String accountId, boolean vipaMode) {
        if (NetworkConnectivity.isOnline(getActivity()) && isNetworkAvailable(getActivity())) {
            showLoading(getBinding().progressBar, true, getActivity());
            viewModel.hitSwitchUser(authToken, accountId).observe(getActivity(), switchUserDetails -> {
                if (switchUserDetails != null) {
                    if (switchUserDetails.getResponseCode() != null) {
                        if (switchUserDetails.getResponseCode() == 2000) {
                            dismissLoading(getBinding().progressBar, getActivity());
                            if (vipaMode) {
                                new SharedPrefHelper(getActivity()).saveKidsMode(true);
                                new ActivityLauncher(getActivity()).homeScreen(getActivity(), HomeActivity.class);
                                getActivity().setTheme(R.style.MyMaterialTheme_Base_Light);
                                KsPreferenceKeys.getInstance().setCurrentTheme(AppConstants.LIGHT_THEME);
                            } else {
                                new SharedPrefHelper(getActivity()).saveKidsMode(false);
                                new ActivityLauncher(getActivity()).homeScreen(getActivity(), HomeActivity.class);
                                getActivity().setTheme(R.style.MyMaterialTheme_Base_Dark);
                                KsPreferenceKeys.getInstance().setCurrentTheme(AppConstants.DARK_THEME);

                            }
                        } else {
                            if (switchUserDetails.getDebugMessage() != null && !switchUserDetails.getDebugMessage().equals("")) {
                                dismissLoading(getBinding().progressBar, getActivity());
                                showDialog(getActivity().getResources().getString(R.string.error), switchUserDetails.getDebugMessage().toString());
                            } else {
                                dismissLoading(getBinding().progressBar, getActivity());
                                showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));

                            }

                        }
                    } else {
                        if (switchUserDetails.getDebugMessage() != null && !switchUserDetails.getDebugMessage().equals("")) {
                            dismissLoading(getBinding().progressBar, getActivity());
                            showDialog(getActivity().getResources().getString(R.string.error), switchUserDetails.getDebugMessage().toString());
                        } else {
                            dismissLoading(getBinding().progressBar, getActivity());
                            showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));

                        }

                    }


                } else {
                    if (switchUserDetails.getDebugMessage() != null && !switchUserDetails.getDebugMessage().equals("")) {
                        dismissLoading(getBinding().progressBar, getActivity());
                        showDialog(getActivity().getResources().getString(R.string.error), switchUserDetails.getDebugMessage().toString());
                    } else {
                        dismissLoading(getBinding().progressBar, getActivity());
                        showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));

                    }

                }

            });


        } else {
            dismissLoading(getBinding().progressBar, getActivity());

            new ToastHandler(getActivity()).show(getActivity().getResources().getString(R.string.no_internet_connection));
            // new ToastHandler(LoginActivity.this).show(LoginActivity.this.getResources().getString(R.string.no_internet_connection));

        }
    }

    public void callAllSecondaryAccount(String token, boolean vipaMode) {
        if (CheckInternetConnection.isOnline(getActivity())) {
            showLoading(getBinding().progressBar, true, getActivity());
            registrationLoginViewModel.hitAllSecondaryApi(getActivity(), token).observe(getActivity(), allSecondaryAccountDetails -> {
                if (allSecondaryAccountDetails != null) {
                    if (allSecondaryAccountDetails.getResponseCode() != null) {
                        if (Objects.requireNonNull(allSecondaryAccountDetails).getResponseCode() == 2000) {
                            if (allSecondaryAccountDetails.getData() != null && !allSecondaryAccountDetails.getData().isEmpty()) {
                               // Log.e("allSecondaryAcco", new Gson().toJson(allSecondaryAccountDetails));
                                String primaryAccountId = "";
                                String secondaryId = "";
                                for (int i = 0; i < allSecondaryAccountDetails.getData().size(); i++) {
                                    if (allSecondaryAccountDetails.getData().get(0).getKidsAccount()) {
                                        primaryAccountId = allSecondaryAccountDetails.getData().get(0).getPrimaryAccountRef().getAccountId();
                                        secondaryId = allSecondaryAccountDetails.getData().get(0).getAccountId();
                                    }
                                }
                                //Log.e("alllistApiPrimaryid", primaryAccountId);
                                //Log.e("allListApiSecondid", secondaryId);
                                new SharedPrefHelper(getActivity()).savePrimaryAccountId(primaryAccountId);
                                new SharedPrefHelper(getActivity()).saveSecondaryAccountId(secondaryId);
                                if (vipaMode) {
                                    switchUserApi(token, secondaryId, vipaMode);

                                } else {
                                    switchUserApi(token, primaryAccountId, vipaMode);

                                }


                            } else {
                                Log.e("allSecondaryEMPTY", new Gson().toJson(allSecondaryAccountDetails));
                                addSecondaryUserApi(token, vipaMode);
                            }

                        } else {
                            if (allSecondaryAccountDetails.getDebugMessage() != null) {
                                dismissLoading(getBinding().progressBar, getActivity());
                                showDialog(getActivity().getResources().getString(R.string.error), allSecondaryAccountDetails.getDebugMessage().toString());
                            } else {
                                dismissLoading(getBinding().progressBar, getActivity());
                                showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));

                            }

                        }

                    } else {
                        if (allSecondaryAccountDetails.getDebugMessage() != null) {
                            dismissLoading(getBinding().progressBar, getActivity());
                            showDialog(getActivity().getResources().getString(R.string.error), allSecondaryAccountDetails.getDebugMessage().toString());
                        } else {
                            dismissLoading(getBinding().progressBar, getActivity());
                            showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));

                        }

                    }

                } else {
                    if (allSecondaryAccountDetails.getDebugMessage() != null) {
                        dismissLoading(getBinding().progressBar, getActivity());
                        showDialog(getActivity().getResources().getString(R.string.error), allSecondaryAccountDetails.getDebugMessage().toString());
                    } else {
                        dismissLoading(getBinding().progressBar, getActivity());
                        showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));

                    }

                }


            });


        } else {
            dismissLoading(getBinding().progressBar, getActivity());

            new ToastHandler(getActivity()).show(getActivity().getResources().getString(R.string.no_internet_connection));


        }
    }

    public void addSecondaryUserApi(String token, boolean vipaMode) {
        if (CheckInternetConnection.isOnline(getActivity())) {
            showLoading(getBinding().progressBar, true, getActivity());
            String userName = KsPreferenceKeys.getInstance().getAppPrefUserName();
            registrationLoginViewModel.hitSecondaryUser(token, userName).observe(getActivity(), secondaryUserDetails -> {

                if (secondaryUserDetails.getResponseCode() != null) {
                    if (Objects.requireNonNull(secondaryUserDetails).getResponseCode() == 2000) {

                        String primaryAccountId = secondaryUserDetails.getData().getPrimaryAccountRef().getAccountId();
                        String secondaryAccountId = secondaryUserDetails.getData().getAccountId();
                        Log.e("addSecondaryApPrimaryid", primaryAccountId);
                        Log.e("addSecondaryApiSecondid", secondaryAccountId);
                        new SharedPrefHelper(getActivity()).savePrimaryAccountId(primaryAccountId);
                        new SharedPrefHelper(getActivity()).saveSecondaryAccountId(secondaryAccountId);
                        if (vipaMode) {
                            switchUserApi(token, secondaryAccountId, true);

                        } else {
                            switchUserApi(token, primaryAccountId, false);

                        }


                    } else {
                        if (secondaryUserDetails.getDebugMessage() != null) {
                            dismissLoading(getBinding().progressBar, getActivity());
                            showDialog(getActivity().getResources().getString(R.string.error), secondaryUserDetails.getDebugMessage().toString());
                        } else {
                            dismissLoading(getBinding().progressBar, getActivity());
                            showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));

                        }

                    }

                } else {
                    if (secondaryUserDetails.getDebugMessage() != null) {
                        dismissLoading(getBinding().progressBar, getActivity());
                        showDialog(getActivity().getResources().getString(R.string.error), secondaryUserDetails.getDebugMessage().toString());
                    } else {
                        dismissLoading(getBinding().progressBar, getActivity());
                        showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));

                    }

                }


            });


        } else {
            dismissLoading(getBinding().progressBar, getActivity());

            new ToastHandler(getActivity()).show(getActivity().getResources().getString(R.string.no_internet_connection));
            // new ToastHandler(LoginActivity.this).show(LoginActivity.this.getResources().getString(R.string.no_internet_connection));

        }
    }

    public boolean isNetworkAvailable(Context context) {
        boolean isOnline = false;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
                isOnline = capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            } else {
                NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
                isOnline = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOnline;
    }


    private void getProfileApi() {
        if (NetworkConnectivity.isOnline(getActivity()) && isNetworkAvailable(getActivity())) {
            showLoading(getBinding().progressBar, true, getActivity());
            KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
            String authToken = preference.getAppPrefAccessToken();
            viewModel.hitUserProfile(getActivity(), authToken).observe(getActivity(), new Observer<UserProfileResponse>() {
                @Override
                public void onChanged(UserProfileResponse userProfileResponse) {
                    if (userProfileResponse != null) {
                        if (userProfileResponse.getStatus()) {
                            updateUI(userProfileResponse);
                            dismissLoading(getBinding().progressBar, getActivity());
                              Log.e("DATA IN LRAVE KIDS",new Gson().toJson(userProfileResponse));
                            if (userProfileResponse.getData().getPrimaryAccountRef() != null) {
                                if (userProfileResponse.getData().getPrimaryAccountRef().getCustomData() != null) {
                                    if (userProfileResponse.getData().getPrimaryAccountRef().getCustomData().getParentalPin() != null && !userProfileResponse.getData().getPrimaryAccountRef().getCustomData().getParentalPin().isEmpty()) {
                                        String decodePin = userProfileResponse.getData().getPrimaryAccountRef().getCustomData().getParentalPin();
                                        pin = StringUtils.getDataFromBase64(decodePin);

                                      // Log.e("pin3", pin);
                                        KidsModePinDialogFragment newFragment = new KidsModePinDialogFragment();
                                        Bundle args = new Bundle();
                                        args.putString("pin", pin);
                                        newFragment.setArguments(args);
                                        newFragment.setTargetFragment(MoreFragment.this, 0);
                                        newFragment.show(getFragmentManager(), "KidsModePinDialogFragments");
                                        newFragment.setCancelable(false);

                                    } else {
                                        // when no pin choose from Pin Ui fragmnet
                                        String primaryAccountId = new SharedPrefHelper(getActivity()).getPrimaryAccountId();
                                        if (primaryAccountId != null && !primaryAccountId.isEmpty()) {
                                            KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
                                            String authToken = preference.getAppPrefAccessToken();
                                            switchUserApi(authToken, primaryAccountId, false);
                                        } else {
                                            KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
                                            String authToken = preference.getAppPrefAccessToken();
                                            callAllSecondaryAccount(authToken, false);

                                        }

                                    }

                                }
                                else {
                                    showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));

                                }


                            } else {
                                showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));
                            }


                        } else {
                            if (userProfileResponse.getResponseCode() == 4302) {
                                isloggedout = true;
                                logoutCall();
                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getActivity().onBackPressed();
                                        }
                                    });
                                } catch (Exception e) {

                                }

                                // showDialog(getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));
                            } else if (userProfileResponse.getResponseCode() == 4019) {
                                showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.number_Cannot_change));
                            } else if (userProfileResponse.getResponseCode() == 4901) {
                                showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.already_exist_number));
                            } else if (userProfileResponse.getDebugMessage() != null) {
                                showDialog(getActivity().getResources().getString(R.string.error), userProfileResponse.getDebugMessage().toString());
                            } else {
                                showDialog(getActivity().getResources().getString(R.string.error), getActivity().getResources().getString(R.string.something_went_wrong));
                            }
                        }
                    }
                }
            });
        }
        else {
            dismissLoading(getBinding().progressBar, getActivity());

            new ToastHandler(getActivity()).show(getActivity().getResources().getString(R.string.no_internet_connection));
        }
    }

    private void logoutCall() {
        if (CheckInternetConnection.isOnline(Objects.requireNonNull(getActivity()))) {
            clearCredientials(preference);
            hitApiLogout(getActivity(), preference.getAppPrefAccessToken());
        } else {
            // new ToastHandler(this).show(getResources().getString(R.string.no_internet_connection));
        }
    }


}
