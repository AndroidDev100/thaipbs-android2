package me.vipa.app.activities.usermanagment.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.vipa.app.R;
import me.vipa.app.SDKConfig;
import me.vipa.app.activities.profile.ui.AvatarImageActivity;
import me.vipa.app.activities.profile.ui.ProfileActivityNew;
import me.vipa.app.activities.usermanagment.viewmodel.RegistrationLoginViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.beanModel.userProfile.UserProfileResponse;
import me.vipa.app.databinding.ActivitySignUpThirdPageBinding;
import me.vipa.app.fragments.dialog.AlertDialogFragment;
import me.vipa.app.fragments.dialog.AlertDialogSingleButtonFragment;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.helpers.CheckInternetConnection;
import me.vipa.app.utils.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.SharedPrefHelper;
import me.vipa.app.utils.helpers.StringUtils;
import me.vipa.app.utils.helpers.ToastHandler;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

public class SignUpThirdPage extends BaseBindingActivity<ActivitySignUpThirdPageBinding> implements AlertDialogFragment.AlertDialogListener {
    private RegistrationLoginViewModel viewModel;
    private KsPreferenceKeys preference;
    private boolean isloggedout = false;
    String spin_val;
    String contentPreference = "";
    String[] gender;
    String dateMilliseconds = "";
    private String imageUrlId = "";
    private String via = "";
    private AmazonS3 s3;
    private TransferUtility transferUtility;
    String imageToUpload = "";
    private String spinnerValue = "";
    private boolean isNotificationEnable;
    private boolean isSkipClicked = false;

    @Override
    public ActivitySignUpThirdPageBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivitySignUpThirdPageBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gender = getApplicationContext().getResources().getStringArray(R.array.gender_array);
        isNotificationEnable = getIntent().getExtras().getBoolean("IsNotiEnabled");
        contentPreference = getIntent().getStringExtra(AppConstants.CONTENT_PREFERENCE);
        connectionObserver();
    }

    private void connectionObserver() {
        preference = KsPreferenceKeys.getInstance();
        callModel();
        setToolbar();

        if (NetworkConnectivity.isOnline(SignUpThirdPage.this)) {
            connectionValidation(true);
        } else {
            connectionValidation(false);
        }
    }
    private void setToolbar(){
        getBinding().toolbar.backLayout.setVisibility(View.VISIBLE);
        getBinding().toolbar.titleToolbar.setVisibility(View.GONE);
        getBinding().toolbar.titleSkip.setText(getResources().getString(R.string.skip));
        getBinding().toolbar.titleSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSkipClicked = true;
                if (!contentPreference.equalsIgnoreCase("")){
                    callUpdateApi();
                }else {
                    preference.setAppPrefRegisterStatus(AppConstants.UserStatus.Login.toString());
                    onBackPressed();
                }

            }
        });
    }



    private void callModel() {
        viewModel = ViewModelProviders.of(SignUpThirdPage.this).get(RegistrationLoginViewModel.class);
    }

    private void connectionValidation(boolean connected) {
        if (connected) {
            getBinding().noConnectionLayout.setVisibility(View.GONE);
            credentialsProvider();
            setTransferUtility();
            setSpinner();
            setClicks();
        } else {
            noConnectionLayout();
        }
    }

    private void setSpinner() {
        getBinding().spinnerId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spin_val = gender[position];
                if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Thai")){
                    if (spin_val.equalsIgnoreCase(getResources().getString(R.string.gender))) {
                        spinnerValue = "";
                    } else {
                        spinnerValue = spin_val;
                        spinnerValue = setSpinnerValue(spinnerValue);
                    }
                }else {
                    if (spin_val.equalsIgnoreCase("GENDER")) {
                        spinnerValue = "";
                    } else {
                        spinnerValue = spin_val;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(SignUpThirdPage.this, R.layout.spinner_item, gender);

// setting adapters to spinners

        getBinding().spinnerId.setAdapter(spin_adapter);
    }

    private String setSpinnerValue(String value) {
        if (value.equalsIgnoreCase(getResources().getString(R.string.male))){
            spinnerValue = "MALE";
        }else if (value.equalsIgnoreCase(getResources().getString(R.string.female))){
            spinnerValue = "FEMALE";
        }else if (value.equalsIgnoreCase(getResources().getString(R.string.others))){
            spinnerValue = "OTHERS";
        }
        return spinnerValue;
    }

    private void setClicks() {
        getBinding().toolbar.backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getBinding().llLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkConnectivity.isOnline(SignUpThirdPage.this)) {
                    callUpdateApi();

                } else {
                    new ToastHandler(SignUpThirdPage.this).show(SignUpThirdPage.this.getResources().getString(R.string.no_internet_connection));
                }
            }
        });
        getBinding().etMobileNumber.setOnClickListener(view -> getBinding().errorMobile.setVisibility(View.INVISIBLE));

        getBinding().etMobileNumber.setLongClickable(false);
        getBinding().etMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getBinding().errorMobile.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        getBinding().etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog mDatePicker = new DatePickerDialog(
                        SignUpThirdPage.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker,
                                          int selectedyear, int selectedmonth,
                                          int selectedday) {
                        selectedyear = selectedyear;
                        mcurrentDate.set(Calendar.YEAR, selectedyear);
                        mcurrentDate.set(Calendar.MONTH, selectedmonth);
                        mcurrentDate.set(Calendar.DAY_OF_MONTH, selectedday);

                        int difference = mYear-selectedyear;
                        if (difference>=13) {

                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                            getBinding().etDob.setText(sdf.format(mcurrentDate.getTime()));
                            try {
                                Date d = sdf.parse(getBinding().etDob.getText().toString());
                                dateMilliseconds = String.valueOf(d.getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }else {
                            getBinding().etDob.setText("");
                            dateMilliseconds = "";
                            new ToastHandler(SignUpThirdPage.this).show(getResources().getString(R.string.date_difference));
                        }


                    }
                }, mYear, mMonth, mDay);
//                mcurrentDate.set(mYear, mMonth, mDay);
//                long value = mcurrentDate.getTimeInMillis();
                //  mDatePicker.getDatePicker().setMinDate(value);
                mDatePicker.show();
            }
        });

        getBinding().profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {getResources().getString(R.string.take_photo), getResources().getString(R.string.select_from_library), getResources().getString(R.string.select_from_avtar),
                        getResources().getString(R.string.cancel)};

                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpThirdPage.this);
                builder.setTitle("");
                builder.setItems(items, (dialog, item) -> {
//            boolean result = Utility.checkPermission(MyUploadActivity.this);
                    if (items[item].equals(getResources().getString(R.string.take_photo))) {
                        // userChoosenTask = "Take Photo";

//                                cameraIntent();
                        galleryIntent();
                    } else if (items[item].equals(getResources().getString(R.string.select_from_library))) {
                        // userChoosenTask = "Choose from Library";
                        galleryIntent();
                    } else if (items[item].equals(getResources().getString(R.string.select_from_avtar))) {
                        // userChoosenTask = "Choose from Library";
                        new ActivityLauncher(SignUpThirdPage.this).avatarActivity(SignUpThirdPage.this, AvatarImageActivity.class);
                    } else if (items[item].equals(getResources().getString(R.string.cancel))) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });
    }

    private void callUpdateApi() {
        if (validateNameEmpty() && validatePhone()) {
            preference.setAppPrefRegisterStatus(AppConstants.UserStatus.Login.toString());
            showLoading(getBinding().progressBar, true);
            String token = preference.getAppPrefAccessToken();
            getBinding().errorMobile.setVisibility(View.INVISIBLE);
            viewModel.hitUpdateProfile(SignUpThirdPage.this, token, preference.getAppPrefUserName(), getBinding().etMobileNumber.getText().toString(), spinnerValue, dateMilliseconds, getBinding().etAddress.getText().toString(), imageUrlId, via,contentPreference,isNotificationEnable,"",false).observe(SignUpThirdPage.this, new Observer<UserProfileResponse>() {
                @Override
                public void onChanged(UserProfileResponse userProfileResponse) {
                    dismissLoading(getBinding().progressBar);
                    if (userProfileResponse != null) {
                        if (userProfileResponse.getStatus()) {
                            if (isSkipClicked){
                                preference.setAppPrefRegisterStatus(AppConstants.UserStatus.Login.toString());
                                onBackPressed();
                            }else {
                                showDialog("", SignUpThirdPage.this.getResources().getString(R.string.profile_update_successfully));
                            }
                            //updateUI(userProfileResponse);
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
                                showDialog(SignUpThirdPage.this.getResources().getString(R.string.error), userProfileResponse.getDebugMessage().toString());
                            }
                            else {
                                if (userProfileResponse.getDebugMessage() != null) {
                                    showDialog(SignUpThirdPage.this.getResources().getString(R.string.error), userProfileResponse.getDebugMessage().toString());
                                } else {
                                    showDialog(SignUpThirdPage.this.getResources().getString(R.string.error), SignUpThirdPage.this.getResources().getString(R.string.something_went_wrong));
                                }
                            }
                        }
                    }
                }
            });

        }
    }

    private void galleryIntent() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("My Crop")
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setCropMenuCropButtonTitle("Done")
                .setRequestedSize(400, 400)
                .setAspectRatio(1, 1)
                .setAutoZoomEnabled(true)
                .start(this);
    }

    private void credentialsProvider() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:5e744f74-b37d-42b5-8c0b-cefe9051826d", Regions.US_EAST_1

        );
        setAmazonS3Client(credentialsProvider);
    }

    private void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider) {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setMaxErrorRetry(10);
        clientConfiguration.setConnectionTimeout(50000); // default is 10 secs
        clientConfiguration.setSocketTimeout(50000);
        clientConfiguration.setMaxConnections(500);
        s3 = new AmazonS3Client(credentialsProvider, clientConfiguration);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
    }

    private void setTransferUtility() {
//        transferUtility = new TransferUtility(s3, getApplicationContext());
        transferUtility = TransferUtility.builder().s3Client(s3).context(getApplicationContext()).build();

    }

    private void setFileToUpload(File fileToUpload) {
//       String videoLink = "Android" + AppCommonMethod.getCurrentTimeStamp() + fileToUpload.getName() +".jpeg";
        imageToUpload = "Thumbnail_" + AppCommonMethod.getCurrentTimeStamp() + "Android" + ".jpg";
        imageUrlId = imageToUpload;
        via = "Gallery";
        new SharedPrefHelper(SignUpThirdPage.this).saveVia(via);


        TransferObserver transferObserver = transferUtility.upload(
                "thai-pbs/profile_picture", imageToUpload,
                fileToUpload

        );

        transferObserverListener(transferObserver);
    }

    private void transferObserverListener(TransferObserver transferObserver) {
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    dismissLoading(getBinding().progressBar);

                    //progressHandler.call();
                    // getBinding().progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int progress = (int) ((double) bytesCurrent * 100 / bytesTotal);
                // PrintLogging.printLog("", +progress + "");

                //getBinding().progressBar.setVisibility(View.VISIBLE);


            }

            @Override
            public void onError(int id, Exception ex) {
                // Log.e("error", "error");
            }

        });
    }

    private void noConnectionLayout() {
            getBinding().noConnectionLayout.setVisibility(View.VISIBLE);
            getBinding().connection.retryTxt.setOnClickListener(view -> connectionObserver());
        }




    public boolean validateNameEmpty() {
       boolean check = false;
        if (getBinding().etMobileNumber.getText().toString().trim().equalsIgnoreCase("")) {
            check = true;
            getBinding().errorMobile.setVisibility(View.INVISIBLE);
        } else if (getBinding().etMobileNumber.getText().toString().trim().length() ==11){
            String firstTwoChar = getBinding().etMobileNumber.getText().toString().substring(0,2);
            if (firstTwoChar.equalsIgnoreCase("66")) {
                check = true;
                getBinding().errorMobile.setVisibility(View.INVISIBLE);
            }else {
                check = false;
                getBinding().errorMobile.setVisibility(View.VISIBLE);
                getBinding().errorMobile.setText(getResources().getString(R.string.mobile_error));
            }
        }else {
            check = false;
            getBinding().errorMobile.setVisibility(View.VISIBLE);
            getBinding().errorMobile.setText(getResources().getString(R.string.mobile_error));
        }
       return check;
    }

    private boolean validatePhone() {
       /* boolean check = false;
        if (getBinding().etMobileNumber.getText().toString().trim().equalsIgnoreCase("")) {
            check = true;
            getBinding().errorMobile.setVisibility(View.INVISIBLE);
        } else if (getBinding().etMobileNumber.getText().toString().trim().length() == 11) {
            String firstTwoChar = getBinding().etMobileNumber.getText().toString().substring(0, 2);
            if (firstTwoChar.equalsIgnoreCase("66")) {
                check = true;
                getBinding().errorMobile.setVisibility(View.INVISIBLE);
            } else {
                check = false;
                getBinding().errorMobile.setVisibility(View.VISIBLE);
                getBinding().errorMobile.setText(getResources().getString(R.string.mobile_error));
            }
        } else {
            check = false;
            getBinding().errorMobile.setVisibility(View.VISIBLE);
            getBinding().errorMobile.setText(getResources().getString(R.string.mobile_error));
        }
        return check;*/

        boolean check = false;
        if (getBinding().etMobileNumber.getText().toString().trim().equalsIgnoreCase("")) {
            check = true;
            getBinding().errorMobile.setVisibility(View.INVISIBLE);
        }
        else {
            if(validateRegex(getBinding().etMobileNumber.getText().toString())){
                check = true;
                getBinding().errorMobile.setVisibility(View.INVISIBLE);
            }else {
                check = false;
                getBinding().errorMobile.setVisibility(View.VISIBLE);
                getBinding().errorMobile.setText(getResources().getString(R.string.mobile_error));
            }
        }

       /* else if (inputLenth>0){
            String firstChar = getBinding().etMobileNumber.getText().toString().substring(0, 1);
            if (firstChar.equalsIgnoreCase("0") && validateRegex(getBinding().etMobileNumber.getText().toString())){
                check = true;
                getBinding().errorMobile.setVisibility(View.INVISIBLE);
            }else {
                check = false;
                getBinding().errorMobile.setVisibility(View.VISIBLE);
                getBinding().errorMobile.setText(getResources().getString(R.string.mobile_error));
            }
        }
        else if (inputLenth>1){
            String firstTwoChar = getBinding().etMobileNumber.getText().toString().substring(0, 2);
            if (firstTwoChar.equalsIgnoreCase("66") && getBinding().etMobileNumber.getText().toString().trim().length() == 11){
                check = true;
                getBinding().errorMobile.setVisibility(View.INVISIBLE);
            }else {
                check = false;
                getBinding().errorMobile.setVisibility(View.VISIBLE);
                getBinding().errorMobile.setText(getResources().getString(R.string.mobile_error));
            }
        }
        else {
            check = false;
            getBinding().errorMobile.setVisibility(View.VISIBLE);
            getBinding().errorMobile.setText(getResources().getString(R.string.mobile_error));
        }*/
        return check;
    }

    private boolean validateRegex(String number) {
        String mobileRegex66="^(?=66)(?=.*?[0-9]).{11,11}$";
        String mobileRegex0="^(?=0)(?=.*?[0-9]).{10,10}$";

        Pattern pattern = Pattern.compile(mobileRegex66);
        Matcher matcher = pattern.matcher(number);

        Pattern pattern2 = Pattern.compile(mobileRegex0);
        Matcher matcher2 = pattern2.matcher(number);

        if (matcher.find()){
            return true;
        }else if (matcher2.find()){
            return true;
        }else {
            return false;
        }

    }



    private void showDialog(String title, String message) {
        FragmentManager fm = getSupportFragmentManager();
        AlertDialogSingleButtonFragment alertDialog = AlertDialogSingleButtonFragment.newInstance(title, message, getResources().getString(R.string.ok));
        alertDialog.setCancelable(false);
        alertDialog.setAlertDialogCallBack(this);
        alertDialog.show(fm, "fragment_alert");
    }

    private void logoutCall() {
        if (CheckInternetConnection.isOnline(Objects.requireNonNull(this))) {
            clearCredientials(preference);
            hitApiLogout(this, preference.getAppPrefAccessToken());
        } else {
            // new ToastHandler(this).show(getResources().getString(R.string.no_internet_connection));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                getBinding().ivProfilePic.setBackgroundResource(0);
                getBinding().ivProfilePic.setImageURI(resultUri);

                String imagePath = resultUri.getPath();
                File imageFilePath = new File(imagePath);

                // imageThumbnail = number + imageFilePath.getName();
                showLoading(getBinding().progressBar, true);
                setFileToUpload(imageFilePath);


//                imageThumbnail = number + imageFilePath.getName();
//                setFileToUpload(imageFilePath);
//                progressHandler.call();

                // c.close();
                //getBinding().webseriesimage.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onFinishDialog() {
        if (isloggedout) {
            logoutCall();
        } else {
            onBackPressed();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AppCommonMethod.Url != "") {

            imageUrlId = AppCommonMethod.UriId;
            via = "Avatar";
            new SharedPrefHelper(SignUpThirdPage.this).saveVia(via);
            Glide.with(SignUpThirdPage.this).load(AppCommonMethod.Url)
                    .placeholder(R.drawable.default_profile_pic)
                    .error(R.drawable.default_profile_pic)
                    .into(getBinding().ivProfilePic);
        }

        AppCommonMethod.Url = "";
        AppCommonMethod.UriId = "";
    }
}