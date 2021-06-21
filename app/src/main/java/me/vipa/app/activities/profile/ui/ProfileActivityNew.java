package me.vipa.app.activities.profile.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.vipa.app.R;
import me.vipa.app.SDKConfig;
import me.vipa.app.activities.usermanagment.ui.SignUpThirdPage;
import me.vipa.app.activities.usermanagment.viewmodel.RegistrationLoginViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.beanModel.userProfile.UserProfileResponse;
import me.vipa.app.databinding.ProfileActivityNewBinding;
import me.vipa.app.fragments.dialog.AlertDialogFragment;
import me.vipa.app.fragments.dialog.AlertDialogSingleButtonFragment;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.helpers.CheckInternetConnection;
import me.vipa.app.utils.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.SharedPrefHelper;
import me.vipa.app.utils.helpers.StringUtils;
import me.vipa.app.utils.helpers.ToastHandler;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;


public class ProfileActivityNew extends BaseBindingActivity<ProfileActivityNewBinding> implements AlertDialogFragment.AlertDialogListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private final String dateOfBirth = "";
    private final String userChoosenTask = "";
    String spin_val;
    String[] gender;
    String dateMilliseconds = "";
    ArrayAdapter<String> spin_adapter;
    String imageToUpload = "";
    private String spinnerValue = "";
    private RegistrationLoginViewModel viewModel;
    private KsPreferenceKeys preference;
    private boolean isloggedout = false;
    private String imageUrlId = "";
    private String via = "";
    private AmazonS3 s3;
    private TransferUtility transferUtility;
    private String contentPreference = "";
    private boolean isNotificationEnable;
    String compareValue="";

    @Override
    public ProfileActivityNewBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ProfileActivityNewBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gender = getApplicationContext().getResources().getStringArray(R.array.gender_array);
        connectionObserver();
    }

    private void connectionObserver() {
        preference = KsPreferenceKeys.getInstance();
        callModel();
        parseProfile();
        setToolbar();
        setOfflineData();
        connectionValidation(NetworkConnectivity.isOnline(ProfileActivityNew.this));
    }

    UserProfileResponse newObject;
    List<String> saved;
    private void parseProfile() {
        try {
            saved=new ArrayList<>();
            Gson gson = new Gson();
            String json = KsPreferenceKeys.getInstance().getUserProfileData();
            newObject = gson.fromJson(json, UserProfileResponse.class);
            Log.w("savedata3",newObject.getData().getCustomData().getContentPreferences());
            if (newObject.getData().getCustomData()!=null && newObject.getData().getCustomData().getContentPreferences()!=null
            ){
                contentPreference=newObject.getData().getCustomData().getContentPreferences();
            }

        }catch (Exception ignored){
            Log.w("savedata3",ignored.toString());
        }

    }

    private void setToolbar() {
        getBinding().toolbar.backLayout.setOnClickListener(view -> onBackPressed());
        getBinding().toolbar.llSearchIcon.setVisibility(View.GONE);
        getBinding().toolbar.homeIcon.setVisibility(View.GONE);
        getBinding().toolbar.mediaRouteButton.setVisibility(View.GONE);
        getBinding().toolbar.titleText.setVisibility(View.VISIBLE);
        getBinding().toolbar.backLayout.setVisibility(View.VISIBLE);
        getBinding().toolbar.screenText.setVisibility(View.VISIBLE);
        getBinding().toolbar.screenText.setText(getResources().getString(R.string.my_profile));
    }

    private void setOfflineData() {
        //  getBinding().userNameWords.setText(AppCommonMethod.getUserName(preference.getAppPrefUserName()));
        getBinding().etName.setText(preference.getAppPrefUserName());
        getBinding().etEmail.setText(preference.getAppPrefUserEmail());
    }

    private void callModel() {
        viewModel = ViewModelProviders.of(ProfileActivityNew.this).get(RegistrationLoginViewModel.class);
    }

    private void connectionValidation(boolean connected) {
        if (connected) {
//            AWSMobileClient.getInstance().initialize(this).execute();
            credentialsProvider();
            setTransferUtility();

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
                        if (spin_val.equalsIgnoreCase("Gender")) {
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
            spin_adapter = new ArrayAdapter<String>(ProfileActivityNew.this, R.layout.spinner_item, gender);
            getBinding().spinnerId.setAdapter(spin_adapter);


            String token = preference.getAppPrefAccessToken();
            viewModel.hitUserProfile(ProfileActivityNew.this, token).observe(ProfileActivityNew.this, new Observer<UserProfileResponse>() {
                @Override
                public void onChanged(UserProfileResponse userProfileResponse) {
                    if (userProfileResponse != null) {
                        if (userProfileResponse.getStatus()) {
                            updateUI(userProfileResponse);
                            Log.e("GETDATA",new Gson().toJson(userProfileResponse));
                       /*   String decodePin =  userProfileResponse.getData().getCustomData().getParentalPin();
                          String pin3=  StringUtils.getDataFromBase64(decodePin);
                            Log.e("decodePin",decodePin);
                            Log.e("pin3",pin3);*/


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
                            }else if (userProfileResponse.getResponseCode() == 4019){
                                showDialog(ProfileActivityNew.this.getResources().getString(R.string.error), ProfileActivityNew.this.getResources().getString(R.string.number_Cannot_change));
                            }else if (userProfileResponse.getResponseCode() == 4901){
                                showDialog(ProfileActivityNew.this.getResources().getString(R.string.error), ProfileActivityNew.this.getResources().getString(R.string.already_exist_number));
                            }else if (userProfileResponse.getDebugMessage() != null) {
                                showDialog(ProfileActivityNew.this.getResources().getString(R.string.error), userProfileResponse.getDebugMessage().toString());
                            } else {
                                showDialog(ProfileActivityNew.this.getResources().getString(R.string.error), ProfileActivityNew.this.getResources().getString(R.string.something_went_wrong));
                            }
                        }
                    }
                }
            });
        } else {

        }

        getBinding().llLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkConnectivity.isOnline(ProfileActivityNew.this)) {
                    callUpdateApi();

                } else {
                    new ToastHandler(ProfileActivityNew.this).show(ProfileActivityNew.this.getResources().getString(R.string.no_internet_connection));
                }
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
                        ProfileActivityNew.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker,
                                          int selectedyear, int selectedmonth,
                                          int selectedday) {
                        selectedyear = selectedyear;
                        mcurrentDate.set(Calendar.YEAR, selectedyear);
                        mcurrentDate.set(Calendar.MONTH, selectedmonth);
                        mcurrentDate.set(Calendar.DAY_OF_MONTH, selectedday);

                        int difference = mYear-selectedyear;
                        if (difference>=13) {

                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy ", Locale.getDefault());
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
                            new ToastHandler(ProfileActivityNew.this).show(getResources().getString(R.string.date_difference));
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



                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivityNew.this);
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
                        new ActivityLauncher(ProfileActivityNew.this).avatarActivity(ProfileActivityNew.this, AvatarImageActivity.class);
                    } else if (items[item].equals(getResources().getString(R.string.cancel))) {
                        dialog.dismiss();
                    }
                });
                builder.show();

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

    private void callUpdateApi() {
//        {"data":null,"responseCode":4019,"debugMessage":"Phone Number cannot be changed after setting once"}
        if (validateNameEmpty() && validatePhone()) {
            showLoading(getBinding().progressBar, true);
            String token = preference.getAppPrefAccessToken();
            viewModel.hitUpdateProfile(ProfileActivityNew.this, token, getBinding().etName.getText().toString(), getBinding().etMobileNumber.getText().toString(), spinnerValue, dateMilliseconds, getBinding().etAddress.getText().toString(), imageUrlId, via, contentPreference, isNotificationEnable,"",false).observe(ProfileActivityNew.this, new Observer<UserProfileResponse>() {
                @Override
                public void onChanged(UserProfileResponse userProfileResponse) {
                    dismissLoading(getBinding().progressBar);
                    if (userProfileResponse != null) {
                        if (userProfileResponse.getStatus()) {
                            Gson gson = new Gson();
                            String userProfileData = gson.toJson(userProfileResponse);
                            KsPreferenceKeys.getInstance().setUserProfileData(userProfileData);
                            Log.e("DATA update profile",userProfileData);
                            showDialog("", ProfileActivityNew.this.getResources().getString(R.string.profile_update_successfully));


                            updateUI(userProfileResponse);
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
                                showDialog(ProfileActivityNew.this.getResources().getString(R.string.error), userProfileResponse.getDebugMessage().toString());
                            }
                            else {
                                if (userProfileResponse.getDebugMessage() != null) {
                                    showDialog(ProfileActivityNew.this.getResources().getString(R.string.error), userProfileResponse.getDebugMessage().toString());
                                } else {
                                    showDialog(ProfileActivityNew.this.getResources().getString(R.string.error), ProfileActivityNew.this.getResources().getString(R.string.something_went_wrong));
                                }
                            }
                        }
                    }
                }
            });

        }
    }

    private boolean validatePhone() {

        int inputLenth=getBinding().etMobileNumber.getText().toString().trim().length();
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

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
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
        new SharedPrefHelper(this).saveVia(via);
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


    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivityNew.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Toast.makeText(MyUploadedVideosActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + ProfileActivityNew.this.getApplicationContext().getPackageName()));
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ProfileActivityNew.this.getApplicationContext().startActivity(intent);

        } else {
            ActivityCompat.requestPermissions(ProfileActivityNew.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(ProfileActivityNew.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
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

    public boolean validateNameEmpty() {
        boolean check = false;
        if (StringUtils.isNullOrEmptyOrZero(getBinding().etName.getText().toString().trim())) {
            getBinding().errorName.setText(getResources().getString(R.string.empty_name));
            getBinding().errorName.setVisibility(View.VISIBLE);
        } else {
            check = true;
            getBinding().errorName.setVisibility(View.INVISIBLE);
        }
        return check;
    }


    private void updateUI(UserProfileResponse userProfileResponse) {
        try {
            // getBinding().userNameWords.setText(AppCommonMethod.getUserName(userProfileResponse.getData().getName()));

            getBinding().etName.setText(userProfileResponse.getData().getName());
            getBinding().etName.setSelection(getBinding().etName.getText().length());
            getBinding().etEmail.setText(userProfileResponse.getData().getEmail());
            preference.setAppPrefUserName(String.valueOf(userProfileResponse.getData().getName()));
            // Log.d("sdsdsdsdsd",new Gson().toJson(userProfileResponse.getData().getCustomData().getProfileAvatar()));

            if (userProfileResponse.getData().getPhoneNumber() != null) {
                getBinding().etMobileNumber.setText(userProfileResponse.getData().getPhoneNumber() + "");
                getBinding().etMobileNumber.setEnabled(false);
                getBinding().etMobileNumber.setFocusable(false);
            }else {
                getBinding().etMobileNumber.setEnabled(true);
                getBinding().etMobileNumber.setFocusable(true);
            }

            if (userProfileResponse.getData().getDateOfBirth() != null) {
                double longV = (double) userProfileResponse.getData().getDateOfBirth();
                DecimalFormat df = new DecimalFormat("#");
                df.setMaximumFractionDigits(0);
                long l = Long.parseLong(df.format(longV));
                dateMilliseconds = String.valueOf(l);
                String dateString = DateFormat.format("dd MMMM yyyy", new Date(l)).toString();
                getBinding().etDob.setText(dateString);
            }

            if (userProfileResponse.getData().getGender() != null) {
                 compareValue = userProfileResponse.getData().getGender() + "";
                if (compareValue != null) {

                    if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Thai")){
                        String value = setSpinnerValueForThai(compareValue);
                        int spinnerPosition = spin_adapter.getPosition(value);
                        getBinding().spinnerId.setSelection(spinnerPosition);
                        if (getBinding().spinnerId.getSelectedItem().toString().equalsIgnoreCase(getResources().getString(R.string.gender))) {
                            spinnerValue = "";
                        } else {
                            spinnerValue = setSpinnerValue(getBinding().spinnerId.getSelectedItem().toString());
                        }

                    }else {
                        int spinnerPosition = spin_adapter.getPosition(compareValue);
                        getBinding().spinnerId.setSelection(spinnerPosition);
                        if (getBinding().spinnerId.getSelectedItem().toString().equalsIgnoreCase("Gender")) {
                            spinnerValue = "";
                        } else {
                            spinnerValue = getBinding().spinnerId.getSelectedItem().toString();
                        }
                    }
                }
            }

            if (userProfileResponse.getData().getCustomData() != null) {
                if (userProfileResponse.getData().getCustomData().getAddress() != null)
                    getBinding().etAddress.setText(userProfileResponse.getData().getCustomData().getAddress());
            }


            if (userProfileResponse.getData().getProfilePicURL() != null && userProfileResponse.getData().getProfilePicURL() != "") {
                imageUrlId = userProfileResponse.getData().getProfilePicURL().toString();
                via = "Gallery";
                new SharedPrefHelper(this).saveVia(via);

                String firstFiveChar = imageUrlId.substring(0, 5);
                if (firstFiveChar.equalsIgnoreCase("https")){
                    Glide.with(ProfileActivityNew.this).load(userProfileResponse.getData().getProfilePicURL())
                            .placeholder(R.drawable.default_profile_pic)
                            .error(R.drawable.default_profile_pic)
                            .into(getBinding().ivProfilePic);
                }else {

                    Glide.with(ProfileActivityNew.this).load(SDKConfig.getInstance().getCLOUD_FRONT_BASE_URL() + SDKConfig.getInstance().getProfileFolder() + userProfileResponse.getData().getProfilePicURL())
                            .placeholder(R.drawable.default_profile_pic)
                            .error(R.drawable.default_profile_pic)
                            .into(getBinding().ivProfilePic);
                }
            } else {

                if (userProfileResponse.getData().getCustomData().getProfileAvatar() != null) {
                    for (int i = 0; i < SDKConfig.getInstance().getAvatarImages().size(); i++) {
                        if (userProfileResponse.getData().getCustomData().getProfileAvatar().equalsIgnoreCase(SDKConfig.getInstance().getAvatarImages().get(i).getIdentifier())) {
                            imageUrlId = SDKConfig.getInstance().getAvatarImages().get(i).getIdentifier();
                            via = "Avatar";
                            new SharedPrefHelper(this).saveVia(via);

                            Glide.with(ProfileActivityNew.this).load(SDKConfig.getInstance().getAvatarImages().get(i).getUrl())
                                    .placeholder(R.drawable.default_profile_pic)
                                    .error(R.drawable.default_profile_pic)
                                    .into(getBinding().ivProfilePic);

                        }
                    }
                } else {
                    imageUrlId = SDKConfig.getInstance().getAvatarImages().get(0).getIdentifier();
                    via = "Avatar";
                    new SharedPrefHelper(this).saveVia(via);
                    Glide.with(ProfileActivityNew.this).load(SDKConfig.getInstance().getAvatarImages().get(0).getUrl())
                            .placeholder(R.drawable.default_profile_pic)
                            .error(R.drawable.default_profile_pic)
                            .into(getBinding().ivProfilePic);

                }
            }

        } catch (Exception e) {

        }
    }

    private String setSpinnerValueForThai(String compareValue) {
      if (compareValue.equalsIgnoreCase("MALE")){
          compareValue = getResources().getString(R.string.male);
      }else if (compareValue.equalsIgnoreCase("FEMALE")){
          compareValue = getResources().getString(R.string.female);
      }else if (compareValue.equalsIgnoreCase("OTHERS")){
          compareValue = getResources().getString(R.string.others);
      }
      return compareValue;
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
            new SharedPrefHelper(this).saveVia(via);
            Glide.with(ProfileActivityNew.this).load(AppCommonMethod.Url)
                    .placeholder(R.drawable.default_profile_pic)
                    .error(R.drawable.default_profile_pic)
                    .into(getBinding().ivProfilePic);
        }

        AppCommonMethod.Url = "";
        AppCommonMethod.UriId = "";
    }
}
