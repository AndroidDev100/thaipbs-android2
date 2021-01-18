package me.vipa.app.activities.profile.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
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

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import me.vipa.app.R;
import me.vipa.app.activities.usermanagment.viewmodel.RegistrationLoginViewModel;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.beanModel.userProfile.UserProfileResponse;
import me.vipa.app.databinding.ProfileActivityNewBinding;
import me.vipa.app.fragments.dialog.AlertDialogFragment;
import me.vipa.app.fragments.dialog.AlertDialogSingleButtonFragment;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.helpers.CheckInternetConnection;
import me.vipa.app.utils.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.StringUtils;
import me.vipa.app.utils.helpers.ToastHandler;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;


public class ProfileActivityNew extends BaseBindingActivity<ProfileActivityNewBinding> implements AlertDialogFragment.AlertDialogListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    String spin_val;
    String[] gender = {"Gender", "MALE", "FEMALE", "OTHERS"};
    String dateMilliseconds = "";
    private String spinnerValue = "";
    private final String dateOfBirth = "";
    private RegistrationLoginViewModel viewModel;
    private KsPreferenceKeys preference;
    private boolean isloggedout = false;
    private final String userChoosenTask = "";
    private String imageUrlId = "";
    private String via = "";
    ArrayAdapter<String> spin_adapter;

    @Override
    public ProfileActivityNewBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ProfileActivityNewBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionObserver();
    }

    private void connectionObserver() {
        preference = KsPreferenceKeys.getInstance();
        callModel();
        setToolbar();
        setOfflineData();
        connectionValidation(NetworkConnectivity.isOnline(ProfileActivityNew.this));
    }

    private void setToolbar() {
        getBinding().toolbar.backLayout.setOnClickListener(view -> onBackPressed());
        getBinding().toolbar.llSearchIcon.setVisibility(View.GONE);
        getBinding().toolbar.homeIcon.setVisibility(View.GONE);
        getBinding().toolbar.mediaRouteButton.setVisibility(View.GONE);
        getBinding().toolbar.titleText.setVisibility(View.VISIBLE);
        getBinding().toolbar.backLayout.setVisibility(View.VISIBLE);
        getBinding().toolbar.screenText.setVisibility(View.VISIBLE);
        getBinding().toolbar.screenText.setText(getResources().getString(R.string.manage_account));
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

            getBinding().spinnerId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spin_val = gender[position];
                    spinnerValue = spin_val;
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
                            }
                            if (userProfileResponse.getDebugMessage() != null) {
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
                    if (validateNameEmpty()) {
                        showLoading(getBinding().progressBar, true);
                        String token = preference.getAppPrefAccessToken();
                        viewModel.hitUpdateProfile(ProfileActivityNew.this, token, getBinding().etName.getText().toString(), getBinding().etMobileNumber.getText().toString(), spinnerValue, dateMilliseconds, getBinding().etAddress.getText().toString(), imageUrlId, via).observe(ProfileActivityNew.this, new Observer<UserProfileResponse>() {
                            @Override
                            public void onChanged(UserProfileResponse userProfileResponse) {
                                dismissLoading(getBinding().progressBar);
                                if (userProfileResponse != null) {
                                    if (userProfileResponse.getStatus()) {
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

                                        } else {
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
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                        getBinding().etDob.setText(sdf.format(mcurrentDate.getTime()));
                        try {
                            Date d = sdf.parse(getBinding().etDob.getText().toString());
                            dateMilliseconds = String.valueOf(d.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }
                }, mYear - 18, mMonth, mDay);
                mcurrentDate.set(mYear - 18, mMonth, mDay);
                long value = mcurrentDate.getTimeInMillis();
                mDatePicker.getDatePicker().setMinDate(value);
                mDatePicker.show();
            }
        });

        getBinding().profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {"Take Photo", "Select from Library", "Select from avatar",
                        "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivityNew.this);
                builder.setTitle("");
                builder.setItems(items, (dialog, item) -> {
//            boolean result = Utility.checkPermission(MyUploadActivity.this);
                    if (items[item].equals("Take Photo")) {
                        // userChoosenTask = "Take Photo";

//                                cameraIntent();
                        galleryIntent();
                    } else if (items[item].equals("Select from Library")) {
                        // userChoosenTask = "Choose from Library";
                        galleryIntent();
                    } else if (items[item].equals("Select from avatar")) {
                        // userChoosenTask = "Choose from Library";
                        new ActivityLauncher(ProfileActivityNew.this).avatarActivity(ProfileActivityNew.this, AvatarImageActivity.class);
                    } else if (items[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });



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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case 123:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (userChoosenTask.equals("Take Photo"))
//                        cameraIntent();
//                    else if (userChoosenTask.equals("Choose from Library"))
//                        galleryIntent();
//                } else {
//                    //code for deny
//                }
//                break;
//        }
//    }

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
//        try {
        // getBinding().userNameWords.setText(AppCommonMethod.getUserName(userProfileResponse.getData().getName()));
        getBinding().etName.setText(userProfileResponse.getData().getName());
        getBinding().etName.setSelection(getBinding().etName.getText().length());
        getBinding().etEmail.setText(userProfileResponse.getData().getEmail());
        preference.setAppPrefUserName(String.valueOf(userProfileResponse.getData().getName()));
        // Log.d("sdsdsdsdsd",new Gson().toJson(userProfileResponse.getData().getCustomData().getProfileAvatar()));

        if (userProfileResponse.getData().getPhoneNumber() != null) {
            getBinding().etMobileNumber.setText(userProfileResponse.getData().getPhoneNumber() + "");
        }

        if (userProfileResponse.getData().getDateOfBirth() != null) {
            double longV = (double) userProfileResponse.getData().getDateOfBirth();
            DecimalFormat df = new DecimalFormat("#");
            df.setMaximumFractionDigits(0);
            long l = Long.parseLong(df.format(longV));
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(l)).toString();
            getBinding().etDob.setText(dateString);
        }

        if (userProfileResponse.getData().getGender()!=null){
            String compareValue = userProfileResponse.getData().getGender()+"";
            if (compareValue != null) {
                int spinnerPosition = spin_adapter.getPosition(compareValue.toUpperCase());
                getBinding().spinnerId.setSelection(spinnerPosition);
                spinnerValue = getBinding().spinnerId.getSelectedItem().toString();
            }
        }


        if (userProfileResponse.getData().getProfilePicURL() != null) {
            Glide.with(ProfileActivityNew.this).load(userProfileResponse.getData().getProfilePicURL())
                    .placeholder(R.drawable.default_profile_pic)
                    .error(R.drawable.default_profile_pic)
                    .into(getBinding().ivProfilePic);
        } else {

            imageUrlId = AppCommonMethod.getConfigResponse().getData().getAppConfig().getavatarImages().get(0).getIdentifier();
            via = "Avatar";

            Glide.with(ProfileActivityNew.this).load(AppCommonMethod.getConfigResponse().getData().getAppConfig().getavatarImages().get(0).getUrl())
                    .placeholder(R.drawable.default_profile_pic)
                    .error(R.drawable.default_profile_pic)
                    .into(getBinding().ivProfilePic);
        }

//        } catch (Exception e) {
//
//        }
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
            Glide.with(ProfileActivityNew.this).load(AppCommonMethod.Url)
                    .placeholder(R.drawable.default_profile_pic)
                    .error(R.drawable.default_profile_pic)
                    .into(getBinding().ivProfilePic);
        }

        AppCommonMethod.Url = "";
        AppCommonMethod.UriId = "";
    }
}
