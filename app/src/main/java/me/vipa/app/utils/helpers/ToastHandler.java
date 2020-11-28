package me.vipa.app.utils.helpers;

import android.app.Activity;
import android.view.Gravity;

import androidx.core.content.ContextCompat;

import com.vipa.app.R;


public class ToastHandler {
    final Activity activity;

    public ToastHandler(Activity context) {
        this.activity = context;
    }

    public void show(String message) {
        new MaterialToast(activity).show(message, ContextCompat.getDrawable(activity, R.drawable.toast_drawable),
                ContextCompat.getColor(activity, R.color.blackColor),
                Gravity.BOTTOM);
    }


}
