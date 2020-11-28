package me.vipa.app.activities.notification.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.vipa.app.R;
import com.vipa.app.databinding.NotificationBinding;
import me.vipa.app.utils.helpers.ToolBarHandler;

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
