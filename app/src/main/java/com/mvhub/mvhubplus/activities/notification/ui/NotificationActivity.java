package com.mvhub.mvhubplus.activities.notification.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.mvhub.mvhubplus.R;
import com.mvhub.mvhubplus.databinding.NotificationBinding;
import com.mvhub.mvhubplus.utils.helpers.ToolBarHandler;

public class NotificationActivity extends AppCompatActivity {

    NotificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callBinding();
    }

    private void callBinding() {

        binding = DataBindingUtil.setContentView(this, R.layout.notification);
        new ToolBarHandler(this).setAction(binding.toolbar, "notification");
    }


}
