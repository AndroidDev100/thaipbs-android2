package com.mvhub.mvhubplus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.mvhub.mvhubplus.R;
import com.mvhub.mvhubplus.baseModels.BaseBindingActivity;
import com.mvhub.mvhubplus.databinding.ActivityContactBinding;
import com.mvhub.mvhubplus.utils.constants.AppConstants;

import java.net.URISyntaxException;

public class ContactActivity extends BaseBindingActivity<ActivityContactBinding> {

    @Override
    public ActivityContactBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivityContactBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCLicks();
    }

    private void setCLicks() {
        getBinding().bank.setOnClickListener(v -> {
            Intent email= new Intent(Intent.ACTION_SENDTO);
            email.setData(Uri.parse("mailto:"));
            startActivity(email);
        });
        getBinding().line.setOnClickListener(v -> {
            Intent intent = new Intent();
            try {
                intent = Intent.parseUri(AppConstants.LINE_URI, Intent.URI_INTENT_SCHEME);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        });
    }
}