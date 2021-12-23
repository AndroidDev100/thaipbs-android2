package me.vipa.app.utils.helpers;

import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.app.ActivityOptionsCompat;

import me.vipa.app.R;
import me.vipa.app.activities.notification.ui.NotificationActivity;
import me.vipa.app.activities.search.ui.ActivitySearch;
import me.vipa.app.databinding.ActivityHelpBinding;
import me.vipa.app.databinding.ActivityOtherApplicationBinding;
import me.vipa.app.databinding.ActivitySeriesDetailBinding;
import me.vipa.app.databinding.DetailScreenBinding;
import me.vipa.app.databinding.EpisodeScreenBinding;
import me.vipa.app.databinding.FragmentMoreBinding;
import me.vipa.app.databinding.LiveDetailBinding;
import me.vipa.app.databinding.LoginBinding;
import me.vipa.app.databinding.ProfileScreenBinding;
import me.vipa.app.databinding.ToolbarBinding;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import me.vipa.brightcovelibrary.Logger;
import me.vipa.utils.ClickHandler;

@SuppressWarnings("EmptyMethod")
public class ToolBarHandler {
    final Activity activity;
    private long mLastClickTime = 0;


    public ToolBarHandler(Activity context) {
        this.activity = context;
    }

    public void setAction(final DetailScreenBinding binding) {
        binding.lessButton.setOnClickListener(view -> {
            Logger.i("click less");
            //  binding.lessText.setText("Less");
        });
    }

    public void setAction(final LiveDetailBinding binding) {
        binding.lessButton.setOnClickListener(view -> {
            Logger.i("click less");
            //  binding.lessText.setText("Less");
        });
    }

    public void setEpisodeAction(final EpisodeScreenBinding binding) {
        binding.lessButton.setOnClickListener(view -> {
            Logger.i("click less");
            //  binding.lessText.setText("Less");
        });
    }

    public void setAction2(final ActivitySeriesDetailBinding binding) {
        binding.lessButton.setOnClickListener(view -> {
            Logger.i("click less");
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
                toolbar.screenText.setText(R.string.notification);
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
            if (ClickHandler.INSTANCE.allowClick()) {
                ActivityOptionsCompat activityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(context,
                                toolbar.searchIcon, "imageMain");
                Intent in = new Intent(context, ActivitySearch.class);
                context.startActivity(in, activityOptionsCompat.toBundle());
            }
        });
        toolbar.clNotification.setOnClickListener(view -> {
            if (ClickHandler.INSTANCE.allowClick()) {
                new ActivityLauncher(activity).notificationActivity(activity, NotificationActivity.class);
            }
        });
    }

    public void setNotificationAction(ToolbarBinding toolbar) {
        toolbar.backLayout.setOnClickListener(view -> activity.onBackPressed());
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

    public void setOtherAction(ActivityOtherApplicationBinding binding, String title) {
        binding.toolbar.llSearchIcon.setVisibility(View.GONE);
        binding.toolbar.backLayout.setVisibility(View.VISIBLE);
        binding.toolbar.homeIcon.setVisibility(View.GONE);
        binding.toolbar.mediaRouteButton.setVisibility(View.GONE);
        binding.toolbar.titleText.setVisibility(View.VISIBLE);
        binding.toolbar.screenText.setText(title);
    }
}