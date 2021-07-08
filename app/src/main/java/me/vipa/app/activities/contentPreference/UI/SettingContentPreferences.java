package me.vipa.app.activities.contentPreference.UI;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.vipa.app.R;
import me.vipa.app.SDKConfig;
import me.vipa.app.activities.contentPreference.adapter.ContentPreferenceAdapter;
import me.vipa.app.activities.contentPreference.viewModel.ContentPrefernceViewModel;
import me.vipa.app.activities.profile.ui.ProfileActivityNew;
import me.vipa.app.activities.usermanagment.ui.SignUpThirdPage;
import me.vipa.app.activities.usermanagment.viewmodel.RegistrationLoginViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.beanModel.userProfile.UserProfileResponse;
import me.vipa.app.callbacks.commonCallbacks.ContentPreferenceCallback;
import me.vipa.app.databinding.ActivityContentPrefSettingsBinding;
import me.vipa.app.databinding.ActivityContentPreferenceBinding;
import me.vipa.app.fragments.dialog.AlertDialogFragment;
import me.vipa.app.fragments.dialog.AlertDialogSingleButtonFragment;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.config.bean.PreferenceBean;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.CheckInternetConnection;
import me.vipa.app.utils.helpers.StringUtils;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

public class SettingContentPreferences extends BaseBindingActivity<ActivityContentPrefSettingsBinding> implements ContentPreferenceCallback , AlertDialogFragment.AlertDialogListener {
    private ContentPreferenceAdapter adatperContentPreference;
    private static final int VERTICAL_ITEM_SPACE = 30;
    private static final int HORIONTAL_ITEM_SPACE = 20;
    private ArrayList<PreferenceBean> list;
    private ArrayList<PreferenceBean> selectedList;
    private String contentPreference = "";
    private KsPreferenceKeys preference;
    private boolean isNotificationEnable = false;
    private boolean isloggedout = false;
    private int count = 0;
    private String encodePin="";
    @Override
    public ActivityContentPrefSettingsBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivityContentPrefSettingsBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // isNotificationEnable = getIntent().getExtras().getBoolean("IsNotiEnabled");
        callBinding();
        connectObservors();
    }

    private void callBinding() {
        preference = KsPreferenceKeys.getInstance();
        getBinding().toolbar.llSearchIcon.setVisibility(View.GONE);
        getBinding().toolbar.backLayout.setVisibility(View.VISIBLE);
        getBinding().toolbar.homeIcon.setVisibility(View.GONE);
        getBinding().toolbar.mediaRouteButton.setVisibility(View.GONE);
        getBinding().toolbar.titleText.setVisibility(View.VISIBLE);
        getBinding().toolbar.screenText.setText(getResources().getString(R.string.content_prefrences));
        getBinding().toolbar.backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void connectObservors() {
        callModel();
        connectionValidation(NetworkConnectivity.isOnline(this));
    }

    private ContentPrefernceViewModel viewModel;
    private void callModel() {
        viewModel = ViewModelProviders.of(SettingContentPreferences.this).get(ContentPrefernceViewModel.class);
    }

    private void connectionValidation(boolean aBoolean) {
        if (aBoolean) {
            getBinding().noConnectionLayout.setVisibility(View.GONE);

            parseProfile();
            uiInitialization();
            setAdapter();
            setClicks();
        } else {
            noConnectionLayout();
        }
    }
    UserProfileResponse newObject;
    List<String> saved;
    private void parseProfile() {
        try {
            saved=new ArrayList<>();
            Gson gson = new Gson();
            String json = KsPreferenceKeys.getInstance().getUserProfileData();
            newObject = gson.fromJson(json, UserProfileResponse.class);
            if(newObject.getData().getCustomData().getParentalPin()!=null && !newObject.getData().getCustomData().getParentalPin().isEmpty()){
                encodePin =  newObject.getData().getCustomData().getParentalPin();
                String pin3=  StringUtils.getDataFromBase64(encodePin);
                //Log.e("decodePin",encodePin);
               // Log.e("pin3Content",pin3);
            }

            else {
                encodePin="";
                //Log.e("pin3elseContent",encodePin);

            }
            Log.w("data3SettingPrefence",newObject.getData().getCustomData().getContentPreferences());
            if (newObject.getData().getCustomData()!=null && newObject.getData().getCustomData().getContentPreferences()!=null){
                contentPreference=newObject.getData().getCustomData().getContentPreferences();
                saved=AppCommonMethod.createPrefrenceList(newObject);
            }
            setUserImage(newObject);



        }catch (Exception ignored){
            Log.w("savedata3",ignored.toString());
        }

    }

    private void setClicks() {
        selectedList = new ArrayList<>();
        getBinding().btnUpdatePreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 getBinding().progressBar.setVisibility(View.VISIBLE);
                  updateProfileHit(encodePin);
            }
        });


    }

    private void updateProfileHit( String encodePin) {
        String token = preference.getAppPrefAccessToken();
        viewModel.hitUpdateProfile(SettingContentPreferences.this, token, AppCommonMethod.getProfileUserName(newObject), AppCommonMethod.getProfileUserNumber(newObject), AppCommonMethod.getProfileUserGender(newObject), AppCommonMethod.getProfileUserDOB(newObject), AppCommonMethod.getProfileUserAddress(newObject), imageUrlId, via, contentPreference, isNotificationEnable,encodePin).observe(SettingContentPreferences.this, new Observer<UserProfileResponse>() {
            @Override
            public void onChanged(UserProfileResponse userProfileResponse) {
                dismissLoading(getBinding().progressBar);
                if (userProfileResponse != null) {
                    if (userProfileResponse.getStatus()) {
                        Gson gson = new Gson();
                        String userProfileData = gson.toJson(userProfileResponse);
                        KsPreferenceKeys.getInstance().setUserProfileData(userProfileData);
                        showDialog("", SettingContentPreferences.this.getResources().getString(R.string.profile_update_successfully));
                    } else {
                        if (userProfileResponse.getResponseCode() == 4302) {
                            isloggedout = true;
                            logoutCall();
                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        onBackPressed();
                                    }
                                });
                            } catch (Exception e) {

                            }
                            // showDialog(getResources().getString(R.string.logged_out), getResources().getString(R.string.you_are_logged_out));

                        } else if(userProfileResponse.getResponseCode() == 4019){
                            showDialog(SettingContentPreferences.this.getResources().getString(R.string.error), userProfileResponse.getDebugMessage().toString());
                        }
                        else {
                            if (userProfileResponse.getDebugMessage() != null) {
                                showDialog(SettingContentPreferences.this.getResources().getString(R.string.error), userProfileResponse.getDebugMessage().toString());
                            } else {
                                showDialog(SettingContentPreferences.this.getResources().getString(R.string.error), SettingContentPreferences.this.getResources().getString(R.string.something_went_wrong));
                            }
                        }
                    }
                }
            }
        });

    }

    private String imageUrlId = "";
    private String via = "";
    private void setUserImage(UserProfileResponse userProfileResponse) {
        try {
            if (userProfileResponse.getData().getProfilePicURL() != null && userProfileResponse.getData().getProfilePicURL() != "") {
                imageUrlId = userProfileResponse.getData().getProfilePicURL().toString();
                via = "Gallery";

                String firstFiveChar = imageUrlId.substring(0, 5);
                if (firstFiveChar.equalsIgnoreCase("https")){

                }else {

                }
            } else {

                if (userProfileResponse.getData().getCustomData().getProfileAvatar() != null) {

                    for (int i = 0; i < SDKConfig.getInstance().getAvatarImages().size(); i++) {
                        if (userProfileResponse.getData().getCustomData().getProfileAvatar().equalsIgnoreCase(SDKConfig.getInstance().getAvatarImages().get(i).getIdentifier())) {
                            imageUrlId = SDKConfig.getInstance().getAvatarImages().get(i).getIdentifier();
                            via = "Avatar";

                        }
                    }
                } else {

                    imageUrlId = SDKConfig.getInstance().getAvatarImages().get(0).getIdentifier();
                    via = "Avatar";

                }
            }

        }catch (Exception ignored){

        }
    }


    private void noConnectionLayout() {
        getBinding().noConnectionLayout.setVisibility(View.VISIBLE);
        getBinding().connection.retryTxt.setOnClickListener(view -> connectObservors());
    }

    private void setAdapter() {
        list = new ArrayList<>();
        if (SDKConfig.getInstance().getContentPreference()!=null) {
            for (int i = 0; i < SDKConfig.getInstance().getContentPreference().size(); i++) {
                PreferenceBean preferenceBean = new PreferenceBean();
                preferenceBean.setIdentifier(SDKConfig.getInstance().getContentPreference().get(i).getIdentifier());
                preferenceBean.setName(SDKConfig.getInstance().getContentPreference().get(i).getName());
                if (newObject!=null && newObject.getData()!=null && newObject.getData().getCustomData()!=null && newObject.getData().getCustomData().getContentPreferences()!=null
                && !newObject.getData().getCustomData().getContentPreferences().equalsIgnoreCase("")){
                   // Log.w("savedata4",newObject.getData().getCustomData().getContentPreferences()+" ---- "+SDKConfig.getInstance().getContentPreference().get(i).getIdentifier());
                    if(AppCommonMethod.check(SDKConfig.getInstance().getContentPreference().get(i).getIdentifier(),saved)){
                        Log.w("savedata5",newObject.getData().getCustomData().getContentPreferences());
                        preferenceBean.setChecked(true);
                        count++;
                    }else {
                        preferenceBean.setChecked(false);
                    }

                }else {
                    preferenceBean.setChecked(false);
                }

                list.add(preferenceBean);

            }
        }


        adatperContentPreference = new ContentPreferenceAdapter(SettingContentPreferences.this,count, list,SettingContentPreferences.this);
        getBinding().recyclerView.setAdapter(adatperContentPreference);
    }

    private void uiInitialization() {
        getBinding().recyclerView.hasFixedSize();
        getBinding().recyclerView.setNestedScrollingEnabled(false);
        getBinding().recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
//      staggeredGridLayoutManager = new StaggeredGridLayoutManager(3,1);
//      getBinding().recyclerview.setLayoutManager(staggeredGridLayoutManager);
        getBinding().recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        getBinding().recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(HORIONTAL_ITEM_SPACE));
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        flowLayoutManager.setAutoMeasureEnabled(true);
//
        getBinding().recyclerView.setLayoutManager(flowLayoutManager);

    }

    @Override
    public void onClick(ArrayList<PreferenceBean> arrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i<arrayList.size(); i ++){
            if (arrayList.get(i).getChecked()){
                stringBuilder.append(arrayList.get(i).getIdentifier()).append(", ");

            }
            if (stringBuilder.length() > 0) {
                contentPreference = stringBuilder.toString();
                contentPreference = contentPreference.substring(0, contentPreference.length() - 2);
            } else {
                contentPreference = "";
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preference.getAppPrefRegisterStatus().equalsIgnoreCase(AppConstants.UserStatus.Login.toString())) {
           // onBackPressed();
        }
    }

    private void showDialog(String title, String message) {
        FragmentManager fm = getSupportFragmentManager();
        AlertDialogSingleButtonFragment alertDialog = AlertDialogSingleButtonFragment.newInstance(title, message, getResources().getString(R.string.ok));
        alertDialog.setCancelable(false);
        alertDialog.setAlertDialogCallBack(SettingContentPreferences.this);
        alertDialog.show(fm, "fragment_alert");
    }

    @Override
    public void onFinishDialog() {
        if (isloggedout) {
            logoutCall();
        } else {
            finish();
        }
    }

    private void logoutCall() {
        if (CheckInternetConnection.isOnline(Objects.requireNonNull(this))) {
            clearCredientials(preference);
            hitApiLogout(this, preference.getAppPrefAccessToken());
        } else {
            // new ToastHandler(this).show(getResources().getString(R.string.no_internet_connection));
        }
    }

}