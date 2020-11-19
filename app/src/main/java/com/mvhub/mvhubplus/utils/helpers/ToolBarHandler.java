package com.mvhub.mvhubplus.utils.helpers;

import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.app.ActivityOptionsCompat;

import com.mvhub.mvhubplus.activities.search.ui.ActivitySearch;
import com.mvhub.mvhubplus.databinding.ActivityHelpBinding;
import com.mvhub.mvhubplus.utils.cropImage.helpers.Logger;
import com.mvhub.mvhubplus.utils.helpers.intentlaunchers.ActivityLauncher;
import com.mvhub.mvhubplus.R;
import com.mvhub.mvhubplus.databinding.ActivitySeriesDetailBinding;
import com.mvhub.mvhubplus.databinding.DetailScreenBinding;
import com.mvhub.mvhubplus.databinding.EpisodeScreenBinding;
import com.mvhub.mvhubplus.databinding.FragmentMoreBinding;
import com.mvhub.mvhubplus.databinding.LiveDetailBinding;
import com.mvhub.mvhubplus.databinding.LoginBinding;
import com.mvhub.mvhubplus.databinding.ProfileScreenBinding;
import com.mvhub.mvhubplus.databinding.ToolbarBinding;

@SuppressWarnings("EmptyMethod")
public class ToolBarHandler {
    final Activity activity;
    private long mLastClickTime = 0;


    public ToolBarHandler(Activity context) {
        this.activity = context;
    }

    public void setAction(final DetailScreenBinding binding) {
        binding.lessButton.setOnClickListener(view -> {
            Logger.i("click", "less");
            //  binding.lessText.setText("Less");
        });
    }

    public void setAction(final LiveDetailBinding binding) {
        binding.lessButton.setOnClickListener(view -> {
            Logger.i("click", "less");
            //  binding.lessText.setText("Less");
        });
    }

    public void setEpisodeAction(final EpisodeScreenBinding binding) {
        binding.lessButton.setOnClickListener(view -> {
            Logger.i("click", "less");
            //  binding.lessText.setText("Less");
        });
    }

    public void setAction2(final ActivitySeriesDetailBinding binding) {
        binding.lessButton.setOnClickListener(view -> {
            Logger.i("click", "less");
            //  binding.lessText.setText("Less");
        });
    }

    public void setAction(LoginBinding binding) {

       /* binding.signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ActivityLauncher(activity).registerActivity(activity, RegisterActivity.class);
            }
        });
*/
    }

    public void setAction(FragmentMoreBinding binding) {
/*
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ActivityLauncher(activity).loginActivity(activity, LoginActivity.class);
            }
        });
        binding.changepassText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ActivityLauncher(activity).changePassword(activity, ChangePasswordActivity.class);
            }
        });
        binding.profileTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ActivityLauncher(activity).ProfileActivity(activity, ProfileActivity.class);
            }
        });
        binding.notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ActivityLauncher(activity).notificationActivity(activity, NotificationActivity.class);
            }
        });
  */

    }

    public void setAction(final ToolbarBinding toolbar, final String currentActivity) {

        switch (currentActivity) {
            case "home":
                toolbar.backLayout.setVisibility(View.GONE);
                toolbar.screenText.setVisibility(View.GONE);
                toolbar.homeIcon.setVisibility(View.VISIBLE);
                toolbar.llSearchIcon.setVisibility(View.VISIBLE);
                break;
            case "search":
                toolbar.backLayout.setVisibility(View.VISIBLE);
                toolbar.screenText.setVisibility(View.GONE);
                toolbar.homeIcon.setVisibility(View.GONE);
                toolbar.llSearchIcon.setVisibility(View.GONE);
                break;
            case "skip":
                toolbar.backLayout.setVisibility(View.VISIBLE);
                toolbar.screenText.setVisibility(View.GONE);
                toolbar.homeIcon.setVisibility(View.GONE);
                toolbar.llSearchIcon.setVisibility(View.GONE);
                break;
            case "potrait":
                toolbar.backLayout.setVisibility(View.VISIBLE);
                toolbar.screenText.setText("Movies");
                toolbar.homeIcon.setVisibility(View.GONE);
                toolbar.llSearchIcon.setVisibility(View.GONE);
                break;
            case "notification":
                toolbar.backLayout.setVisibility(View.VISIBLE);
                toolbar.screenText.setText("Notification");
                toolbar.titleText.setVisibility(View.VISIBLE);
                toolbar.llSearchIcon.setVisibility(View.GONE);
                toolbar.homeIcon.setVisibility(View.GONE);
                break;
            case "password":
                toolbar.backLayout.setVisibility(View.VISIBLE);
                toolbar.screenText.setText("Set New Password");
                toolbar.llSearchIcon.setVisibility(View.GONE);
                toolbar.homeIcon.setVisibility(View.GONE);
                break;
            case "profile":
                toolbar.backLayout.setVisibility(View.VISIBLE);
                toolbar.screenText.setText("User Profile");
                toolbar.llSearchIcon.setVisibility(View.GONE);
                toolbar.homeIcon.setVisibility(View.GONE);
                break;
            case "detail":
                toolbar.backLayout.setVisibility(View.VISIBLE);
                toolbar.screenText.setText("3 Srikandi");
                toolbar.homeIcon.setVisibility(View.GONE);
                toolbar.llSearchIcon.setVisibility(View.GONE);
                break;
            case "MoreFragment":
                toolbar.backLayout.setVisibility(View.GONE);
                toolbar.screenText.setVisibility(View.GONE);
                toolbar.homeIcon.setVisibility(View.VISIBLE);
                toolbar.llSearchIcon.setVisibility(View.GONE);
                toolbar.searchIcon.setVisibility(View.GONE);
                break;
            case "ForgotPasswordActivity":
                toolbar.backLayout.setVisibility(View.VISIBLE);
                toolbar.titleText.setVisibility(View.VISIBLE);
                toolbar.screenText.setText(activity.getResources().getString(R.string.forgot_password));
                toolbar.mediaRouteButton.setVisibility(View.GONE);
                toolbar.llSearchIcon.setVisibility(View.GONE);
                toolbar.homeIcon.setVisibility(View.GONE);
                break;
            default:
                toolbar.backLayout.setVisibility(View.VISIBLE);
                toolbar.homeIcon.setVisibility(View.GONE);
                break;
        }
        toolbar.llSearchIcon.setOnClickListener(view ->
                {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    new ActivityLauncher(activity).searchActivity(activity, ActivitySearch.class);
                }
        );

        toolbar.backLayout.setOnClickListener(view -> activity.onBackPressed());

    }

    public void setMoreListner(LinearLayout more_text, int id, String title, int flag, int type) {

        more_text.setOnClickListener(view -> {

            if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            // new ActivityLauncher(activity).portraitListing(activity, ListingActivity.class, id, title, flag, type);
        });
    }

    public void setHomeAction(ToolbarBinding toolbar, Activity context) {
        toolbar.llSearchIcon.setOnClickListener(view -> {
            //  if (NetworkConnectivity.isOnline(context)) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context,toolbar.searchIcon,"imageMain");
            Intent in = new Intent(context,ActivitySearch.class);
            context.startActivity(in,activityOptionsCompat.toBundle());
           // new ActivityLauncher(activity).searchActivity(activity, ActivitySearch.class);
            // }
            //       else
            //        new ToastHandler(activity).show(activity.getResources().getString(R.string.no_internet_connection));

        });
    }

    public void setSeriesAction(ActivitySeriesDetailBinding binding) {
    }

    public void profileAction(ProfileScreenBinding binding) {
        binding.toolbar.backLayout.setVisibility(View.VISIBLE);
        binding.toolbar.titleText.setVisibility(View.VISIBLE);
        binding.toolbar.searchIcon.setVisibility(View.GONE);
        binding.toolbar.homeIcon.setVisibility(View.GONE);
        binding.toolbar.screenText.setVisibility(View.VISIBLE);
        binding.toolbar.screenText.setText("My Profile");
    }

    public void setHelpAction(ActivityHelpBinding binding, String title) {
        binding.toolbar.llSearchIcon.setVisibility(View.GONE);
        binding.toolbar.backLayout.setVisibility(View.VISIBLE);
        binding.toolbar.homeIcon.setVisibility(View.GONE);
        binding.toolbar.mediaRouteButton.setVisibility(View.GONE);
        binding.toolbar.titleText.setVisibility(View.VISIBLE);
        binding.toolbar.screenText.setText(title);
    }
}