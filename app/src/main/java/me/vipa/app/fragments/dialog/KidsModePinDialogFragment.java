package me.vipa.app.fragments.dialog;


import android.content.Context;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
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


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import me.vipa.app.R;

import me.vipa.app.activities.usermanagment.viewmodel.RegistrationLoginViewModel;
import me.vipa.app.beanModel.userProfile.UserProfileResponse;
import me.vipa.app.databinding.KidPinPopupLayoutBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.helpers.CheckInternetConnection;

import me.vipa.app.utils.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.SharedPrefHelper;
import me.vipa.app.utils.helpers.StringUtils;
import me.vipa.app.utils.helpers.ToastHandler;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;
import me.vipa.baseCollection.baseCategoryServices.BaseCategoryServices;
import me.vipa.userManagement.callBacks.LogoutCallBack;
import retrofit2.Response;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class KidsModePinDialogFragment extends DialogFragment {
    private KidPinPopupLayoutBinding kidPinPopupLayoutBinding;
    private KsPreferenceKeys preference;
    private RegistrationLoginViewModel viewModel;
    private boolean isloggedout = false;
    private UserProfileResponse newObject;
    private List<String> saved;
    private String name = "";
    private String phoneNumber = "";
    private String gender = "";
    private String dob = "";
    private String address = "";
    private String profilePic = "";
    private String via = "";
    private String pinGetFromApi = "";
    private boolean isNotificationEnable = false;
    private String contentPreference = "";
    private boolean fromMoreFragment;

    private KidsModePinDialogFragment.CallBackListenerOkClick callBackListenerOkClick;

  /*  public static KidsModePinDialogFragment getInstance(){
        if(mInstance == null){
            mInstance = new KidsModePinDialogFragment();

        }
        Log.e("PINGET",pin);

        return mInstance;
    }*/

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
        kidPinPopupLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.kid_pin_popup_layout, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return kidPinPopupLayoutBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof KidsModePinDialogFragment.CallBackListenerOkClick)
            callBackListenerOkClick = (KidsModePinDialogFragment.CallBackListenerOkClick) getActivity();

        setClicks();
        viewModel = ViewModelProviders.of(getActivity()).get(RegistrationLoginViewModel.class);
        preference = KsPreferenceKeys.getInstance();
        parseProfile();
        kidPinPopupLayoutBinding.pinViewNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
             /*   if (kidPinPopupLayoutBinding.pinViewNumber.getSelectionEnd()==4) {
                    kidPinPopupLayoutBinding.btContinue.setEnabled(true);
                    Log.e("afterTextChanged","afterTextChanged");

                }*/

            }
        });


    }

    private void setClicks() {
        kidPinPopupLayoutBinding.btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(kidPinPopupLayoutBinding.pinViewNumber.getText())) {
                    if (kidPinPopupLayoutBinding.pinViewNumber.getSelectionEnd() == 4) {
                        String pin = String.valueOf(kidPinPopupLayoutBinding.pinViewNumber.getText());
                        if (NetworkConnectivity.isOnline(getActivity())) {
                            String encodePin = StringUtils.getConvertBase64Data(pin);
                            Log.e("Encode pin", encodePin);
                            if (pinGetFromApi != null && !pinGetFromApi.isEmpty()) {
                                if (pinGetFromApi.equalsIgnoreCase(encodePin)) {
                                    callUpdateApi(encodePin);
                                } else {
                                    showDialog(getResources().getString(R.string.plz_enter_valid_pin));
                                }

                            } else {
                                callUpdateApi(encodePin);

                            }

                        } else {
                            new ToastHandler(getActivity()).show(getActivity().getResources().getString(R.string.no_internet_connection));
                        }

                    } else {
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

    private void showDialog(String msg) {

        ErrorDialogFragment newFragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putString("errorMsg", msg);
        newFragment.setArguments(args);
        newFragment.show(getActivity().getSupportFragmentManager(), "ErrorDialogFragment");
        newFragment.setCancelable(false);
      /*  LayoutInflater factory = LayoutInflater.from(getActivity());
        final View deleteDialogView = factory.inflate(R.layout.error_pop_up, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();
        deleteDialog.setView(deleteDialogView);
        TextView tv_error = deleteDialogView.findViewById(R.id.tv_error);
        tv_error.setText(msg);
        deleteDialogView.findViewById(R.id.tvOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });
        deleteDialog.show();

        deleteDialog.setCancelable(false);*/

    }

    private void callUpdateApi(String pin) {


        showLoading(kidPinPopupLayoutBinding.progressBar, true);
        String token = preference.getAppPrefAccessToken();
        Log.e("VIA", via);
        viewModel.hitUpdateProfile(getActivity(), token, name, phoneNumber, gender, dob, address, profilePic, via, contentPreference, isNotificationEnable, pin, true).observe(getActivity(), new Observer<UserProfileResponse>() {
            @Override
            public void onChanged(UserProfileResponse userProfileResponse) {
                dismissLoading(kidPinPopupLayoutBinding.progressBar);
                if (userProfileResponse != null) {
                    if (userProfileResponse.getStatus()) {
                        // Gson gson = new Gson();
                        // String userProfileData = gson.toJson(userProfileResponse);
                        Log.e("PINMODE DATA", new Gson().toJson(userProfileResponse));
                        kidPinPopupLayoutBinding.pinViewNumber.setText("");
                        if (fromMoreFragment) {
                            callBackListenerOkClick.onContinueClick();
                            dismiss();
                        } else {
                            dismiss();
                        }


                    } else {
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

                        } else if (userProfileResponse.getResponseCode() == 4019) {
                            showDialog(userProfileResponse.getDebugMessage().toString());
                        } else {
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
            AppCommonMethod.updateLanguage(strCurrentLanguage, getActivity());
            KsPreferenceKeys.getInstance().setBingeWatchEnable(isBingeWatchEnable);
            // new ActivityLauncher(this).homeScreen(this, HomeActivity.class);
        } catch (Exception e) {
            // new ActivityLauncher(this).homeScreen(this, HomeActivity.class);

        }
    }

    public void hitApiLogout(Context context, String token) {

        String isFacebook = KsPreferenceKeys.getInstance().getAppPrefLoginType();

        if (isFacebook.equalsIgnoreCase(AppConstants.UserLoginType.FbLogin.toString())) {
            LoginManager.getInstance().logOut();
        }


        BaseCategoryServices.Companion.getInstance().logoutService(token, new LogoutCallBack() {
            @Override
            public void failure(boolean status, int errorCode, String message) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(AppConstants.API_RESPONSE_CODE, 500);

            }

            @Override
            public void success(boolean status, Response<JsonObject> response) {
                if (status) {
                    try {
                        if (response.code() == 404) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty(AppConstants.API_RESPONSE_CODE, response.code());

                        }
                        if (response.code() == 403) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty(AppConstants.API_RESPONSE_CODE, response.code());

                        } else if (response.code() == 200) {
                            Objects.requireNonNull(response.body()).addProperty(AppConstants.API_RESPONSE_CODE, response.code());

                        } else if (response.code() == 401) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty(AppConstants.API_RESPONSE_CODE, response.code());

                        } else if (response.code() == 500) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty(AppConstants.API_RESPONSE_CODE, response.code());

                        }
                    } catch (Exception e) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty(AppConstants.API_RESPONSE_CODE, response.code());

                    }

                }
            }
        });


    }

    private void parseProfile() {
        try {

            Bundle bundle = getArguments();
            if (bundle != null) {
                pinGetFromApi = bundle.getString("pin");
                fromMoreFragment = bundle.getBoolean("fromMoreFragment");
            }
            Log.e("PIN :: API", pinGetFromApi);
            saved = new ArrayList<>();
            name = "";
            phoneNumber = "";
            gender = "";
            dob = "";
            address = "";
            profilePic = "";
            via = "";
            isNotificationEnable = false;
            contentPreference = "";
            Gson gson = new Gson();
            String json = KsPreferenceKeys.getInstance().getUserProfileData();
            newObject = gson.fromJson(json, UserProfileResponse.class);

            isNotificationEnable = new SharedPrefHelper(getActivity()).getNotificationEnable();
            via = new SharedPrefHelper(getActivity()).getVia();
            Log.e("GetDatainPinDialog", gson.toJson(newObject));
            if (newObject.getData().getCustomData() != null && newObject.getData().getCustomData().getContentPreferences() != null && !newObject.getData().getCustomData().getContentPreferences().isEmpty()
            ) {
                contentPreference = newObject.getData().getCustomData().getContentPreferences();
            }
            if (newObject.getData().getName() != null && !newObject.getData().getName().isEmpty()) {
                name = newObject.getData().getName();
            }
            if (newObject.getData().getPhoneNumber() != null && !newObject.getData().getPhoneNumber().equals("")) {
                phoneNumber = String.valueOf(newObject.getData().getPhoneNumber());
            }
            if (newObject.getData().getGender() != null && !newObject.getData().getGender().equals("")) {
                gender = String.valueOf(newObject.getData().getGender());
            }
            if (newObject.getData().getDateOfBirth() != null && !newObject.getData().getDateOfBirth().equals("")) {
                double longV = (double) newObject.getData().getDateOfBirth();
                DecimalFormat df = new DecimalFormat("#");
                df.setMaximumFractionDigits(0);
                long l = Long.parseLong(df.format(longV));
                dob = String.valueOf(l);
            }
            if (newObject.getData().getCustomData().getAddress() != null && !newObject.getData().getCustomData().getAddress().isEmpty()) {
                address = newObject.getData().getCustomData().getAddress();
            }
            if (newObject.getData().getProfilePicURL() != null && !newObject.getData().getProfilePicURL().equals("")) {
                profilePic = String.valueOf(newObject.getData().getProfilePicURL());
            }


        } catch (Exception ignored) {
            Log.w("savedata3", ignored.toString());
        }

    }

    public interface CallBackListenerOkClick {
        void onContinueClick();


    }


}



