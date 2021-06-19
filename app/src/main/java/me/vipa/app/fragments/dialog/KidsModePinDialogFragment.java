package me.vipa.app.fragments.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.vipa.app.R;
import me.vipa.app.activities.detail.ui.EpisodeActivity;
import me.vipa.app.activities.profile.ui.ProfileActivityNew;
import me.vipa.app.activities.usermanagment.viewmodel.RegistrationLoginViewModel;
import me.vipa.app.beanModel.userProfile.UserProfileResponse;
import me.vipa.app.databinding.KidPinPopupLayoutBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.helpers.CheckInternetConnection;
import me.vipa.app.utils.helpers.ImageHelper;
import me.vipa.app.utils.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.SharedPrefHelper;
import me.vipa.app.utils.helpers.ToastHandler;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;
import me.vipa.baseCollection.baseCategoryServices.BaseCategoryServices;
import me.vipa.userManagement.callBacks.LogoutCallBack;
import retrofit2.Response;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class KidsModePinDialogFragment extends DialogFragment {
    private KidPinPopupLayoutBinding kidPinPopupLayoutBinding;
    private static KidsModePinDialogFragment mInstance;
    private String pin="";
    private KsPreferenceKeys preference;
    private RegistrationLoginViewModel viewModel;
    private boolean isloggedout = false;



    UserProfileResponse newObject;
    List<String> saved;
    private String name="";
    private String phoneNumber="";
    private String gender="";
    private String dob="";
    private String address="";
    private String profilePic="";
    private String via="";
    private boolean isNotificationEnable=false;
    private String contentPreference = "";

    public static KidsModePinDialogFragment getInstance(){
        if(mInstance == null){
            mInstance = new KidsModePinDialogFragment();
        }
        return mInstance;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        if (getDialog() != null) {
            getDialog().getWindow().getAttributes().width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
            getDialog().getWindow().getAttributes().height = android.view.WindowManager.LayoutParams.MATCH_PARENT;
        }
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        kidPinPopupLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.kid_pin_popup_layout,container,false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return kidPinPopupLayoutBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setClicks();
        viewModel = ViewModelProviders.of(getActivity()).get(RegistrationLoginViewModel.class);
        preference = KsPreferenceKeys.getInstance();
        parseProfile();
    }

    private void setClicks() {
        kidPinPopupLayoutBinding.btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(kidPinPopupLayoutBinding.pinViewNumber.getText())) {
                    if (kidPinPopupLayoutBinding.pinViewNumber.getSelectionEnd()==4) {
                        pin  = String.valueOf(kidPinPopupLayoutBinding.pinViewNumber.getText());
                        if (NetworkConnectivity.isOnline(getActivity())) {
                            callUpdateApi(pin);

                        } else {
                            new ToastHandler(getActivity()).show(getActivity().getResources().getString(R.string.no_internet_connection));
                        }

                    }
                    else {
                        showDialog(getResources().getString(R.string.plz_enter_valid_pin));

                    }

                } else {
                    showDialog(getResources().getString(R.string.plz_enter_valid_pin));
                }



            }
        });
        kidPinPopupLayoutBinding.ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kidPinPopupLayoutBinding.pinViewNumber.setText("");
                dismiss();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

  /*  @Override
    public void onCancel(@NonNull @NotNull DialogInterface dialog) {
        super.onCancel(dialog);
        kidPinPopupLayoutBinding.pinViewNumber.setText("");

    }*/

    private void showDialog( String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.error_pop_up, null);
        dialog.setView(view);
        final AlertDialog alert = dialog.create();

        TextView ok = view.findViewById(R.id.tvOk);
        TextView tv_error = view.findViewById(R.id.tv_error);
        tv_error.setText(msg);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();


            }
        });
        alert.show();
        alert.getWindow().setLayout(750, 400);

        alert.setCancelable(false);
    }
    private void callUpdateApi( String pin) {
//        {"data":null,"responseCode":4019,"debugMessage":"Phone Number cannot be changed after setting once"}

        showLoading(kidPinPopupLayoutBinding.progressBar, true);
            String token = preference.getAppPrefAccessToken();
       // Log.e("Token",token);
        Log.e("VIA",via);
            viewModel.hitUpdateProfile(getActivity(), token, name, phoneNumber, gender, dob, address, profilePic, via, contentPreference, isNotificationEnable,pin,true).observe(getActivity(), new Observer<UserProfileResponse>() {
                @Override
                public void onChanged(UserProfileResponse userProfileResponse) {
                    dismissLoading(kidPinPopupLayoutBinding.progressBar);
                    if (userProfileResponse != null) {
                        if (userProfileResponse.getStatus()) {
                           // Gson gson = new Gson();
                           // String userProfileData = gson.toJson(userProfileResponse);
                            Log.e("PINMODE",new Gson().toJson(userProfileResponse));
                            kidPinPopupLayoutBinding.pinViewNumber.setText("");
                            dismiss();



                        }
                        else {
                            if (userProfileResponse.getResponseCode() == 4302) {
                                isloggedout = true;
                                logoutCall();
                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismiss();
                                        }
                                    });
                                } catch (Exception e) {

                                }
                                // showDialog(getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));

                            }
                            else if(userProfileResponse.getResponseCode() == 4019){
                                showDialog(userProfileResponse.getDebugMessage().toString());
                            }
                            else {
                                if (userProfileResponse.getDebugMessage() != null) {
                                    showDialog(userProfileResponse.getDebugMessage().toString());
                                } else {
                                    showDialog(getResources().getString(R.string.something_went_wrong));
                                }
                            }
                        }
                    }
                }
            });

    }

    private void showLoading(ProgressBar progressBar, boolean val) {
        if (val) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.bringToFront();
        }
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    private void dismissLoading(ProgressBar progressBar) {
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

    public void clearCredientials(KsPreferenceKeys preference) {
        try {
            String isFacebook = preference.getAppPrefLoginType();
            if (isFacebook.equalsIgnoreCase(AppConstants.UserLoginType.FbLogin.toString())) {
                LoginManager.getInstance().logOut();
            }
            String strCurrentTheme = KsPreferenceKeys.getInstance().getCurrentTheme();
            String strCurrentLanguage = KsPreferenceKeys.getInstance().getAppLanguage();
            boolean isBingeWatchEnable = KsPreferenceKeys.getInstance().getBingeWatchEnable();
            preference.setAppPrefRegisterStatus(AppConstants.UserStatus.Logout.toString());
            preference.clear();
            KsPreferenceKeys.getInstance().setfirstTimeUser(false);
            KsPreferenceKeys.getInstance().setCurrentTheme(strCurrentTheme);
            KsPreferenceKeys.getInstance().setAppLanguage(strCurrentLanguage);
            AppCommonMethod.updateLanguage(strCurrentLanguage,getActivity());
            KsPreferenceKeys.getInstance().setBingeWatchEnable(isBingeWatchEnable);
            // new ActivityLauncher(this).homeScreen(this, HomeActivity.class);
        }catch (Exception e){
            // new ActivityLauncher(this).homeScreen(this, HomeActivity.class);

        }
    }

    public void hitApiLogout(Context context, String token) {

        String isFacebook = KsPreferenceKeys.getInstance().getAppPrefLoginType();

        if (isFacebook.equalsIgnoreCase(AppConstants.UserLoginType.FbLogin.toString())) {
            LoginManager.getInstance().logOut();
        }
        /*hitApiConfig(context);
        ApiInterface endpoint = RequestConfig.getClientInterceptor(token).create(ApiInterface.class);

        Call<JsonObject> call = endpoint.getLogout(false);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                if (response.code() == 404) {
                    Logger.e("BaseActivity", "404 Error");
                } else if (response.code() == 200 || response.code() == 401) {
                    KsPreferenceKeys.getInstance().clear();
                    logoutBackpress = true;
                    hitApiConfig(context);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                //pDialog.call();
            }
        });*/

        BaseCategoryServices.Companion.getInstance().logoutService(token, new LogoutCallBack() {
            @Override
            public void failure(boolean status, int errorCode, String message) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(AppConstants.API_RESPONSE_CODE, 500);

            }

            @Override
            public void success(boolean status, Response<JsonObject> response) {
                if (status){
                    try {
                        if (response.code() == 404) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty(AppConstants.API_RESPONSE_CODE, response.code());

                        }if (response.code() == 403) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty(AppConstants.API_RESPONSE_CODE, response.code());

                        }
                        else if (response.code() == 200) {
                            Objects.requireNonNull(response.body()).addProperty(AppConstants.API_RESPONSE_CODE, response.code());

                        } else if (response.code() == 401) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty(AppConstants.API_RESPONSE_CODE, response.code());

                        } else if (response.code() == 500) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty(AppConstants.API_RESPONSE_CODE, response.code());

                        }
                    }catch (Exception e){
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty(AppConstants.API_RESPONSE_CODE, response.code());

                    }

                }
            }
        });



    }

    private void parseProfile() {
        try {
            saved=new ArrayList<>();
            name="";
            phoneNumber="";
            gender="";
            dob="";
            address="";
            profilePic="";
           via="";
          isNotificationEnable=false;
          contentPreference = "";
            Gson gson = new Gson();
            String json = KsPreferenceKeys.getInstance().getUserProfileData();
            newObject = gson.fromJson(json, UserProfileResponse.class);

            isNotificationEnable  = new SharedPrefHelper(getActivity()).getNotificationEnable();
            via  = new SharedPrefHelper(getActivity()).getVia();
            Log.e("GetDatainPinDialog",gson.toJson(newObject));
            if (newObject.getData().getCustomData()!=null && newObject.getData().getCustomData().getContentPreferences()!=null && !newObject.getData().getCustomData().getContentPreferences().isEmpty()
            ){
                contentPreference=newObject.getData().getCustomData().getContentPreferences();
            }
            if(newObject.getData().getName()!=null && !newObject.getData().getName().isEmpty()){
               name =newObject.getData().getName();
            }
            if(newObject.getData().getPhoneNumber()!=null && !newObject.getData().getPhoneNumber().equals("")){
                phoneNumber =    String.valueOf(newObject.getData().getPhoneNumber());
            }
            if(newObject.getData().getGender()!=null && !newObject.getData().getGender().equals("")){
                gender =String.valueOf(newObject.getData().getGender());
            }
            if(newObject.getData().getDateOfBirth()!=null && !newObject.getData().getDateOfBirth().equals("")){
              //  dob = String.valueOf(  newObject.getData().getDateOfBirth());

                double longV = (double) newObject.getData().getDateOfBirth();
                DecimalFormat df = new DecimalFormat("#");
                df.setMaximumFractionDigits(0);
                long l = Long.parseLong(df.format(longV));
                dob = String.valueOf(l);
            }
            if(newObject.getData().getCustomData().getAddress()!=null && !newObject.getData().getCustomData().getAddress().isEmpty()){
                address =newObject.getData().getCustomData().getAddress();
            }
            if(newObject.getData().getProfilePicURL()!=null && !newObject.getData().getProfilePicURL().equals("")){
                profilePic =  String.valueOf(newObject.getData().getProfilePicURL());
            }


        }catch (Exception ignored){
            Log.w("savedata3",ignored.toString());
        }

    }

}



