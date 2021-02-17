package me.vipa.app.activities.OtherApplication.UI;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import me.vipa.app.R;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.cms.HelpActivity;
import me.vipa.app.databinding.ActivityOtherApplicationBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.helpers.ToolBarHandler;

public class OtherApplication extends BaseBindingActivity<ActivityOtherApplicationBinding> implements View.OnClickListener {

    @Override
    public ActivityOtherApplicationBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivityOtherApplicationBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ToolBarHandler(this).setOtherAction(getBinding(), OtherApplication.this.getResources().getString(R.string.other_application));
        getBinding().toolbar.backLayout.setOnClickListener(v -> {
            onBackPressed();
        });

        getBinding().thaiPbs.setOnClickListener(this);
        getBinding().catchup.setOnClickListener(this);
        getBinding().podcast.setOnClickListener(this);
        getBinding().csite.setOnClickListener(this);
        getBinding().magazine.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.thai_pbs:
                openUrl(AppConstants.THAI_PBS);
                break;
            case R.id.catchup:
                openUrl(AppConstants.CATCHUP);
                break;
            case R.id.podcast:
                openUrl(AppConstants.PODCAST);
                break;
            case R.id.csite:
                openUrl(AppConstants.CSITE);
                break;
            case R.id.magazine:
                openUrl(AppConstants.MAGAZINE);
                break;
            default:
                break;
    }
}

    private void openUrl(String url) {
        AppCommonMethod.openUrl(OtherApplication.this, url);
    }
}