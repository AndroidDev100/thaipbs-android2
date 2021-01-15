package me.vipa.app.utils.helpers.intentlaunchers;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;

import me.vipa.app.activities.contentPreference.UI.ContentPreference;
import me.vipa.app.activities.onBoarding.UI.OnBoarding;
import me.vipa.app.activities.profile.ui.AvatarImageActivity;
import me.vipa.app.activities.usermanagment.ui.SignUpThirdPage;
import me.vipa.baseCollection.baseCategoryModel.BaseCategory;
import me.vipa.app.activities.article.ArticleActivity;
import me.vipa.app.activities.detail.ui.DetailActivity;
import me.vipa.app.activities.detail.ui.EpisodeActivity;
import me.vipa.app.activities.downloads.MyDownloads;
import me.vipa.app.activities.homeactivity.ui.HomeActivity;
import me.vipa.app.activities.listing.listui.ListActivity;
import me.vipa.app.activities.listing.ui.GridActivity;
import me.vipa.app.activities.live.LiveActivity;
import me.vipa.app.activities.notification.ui.NotificationActivity;
import me.vipa.app.activities.profile.ui.ProfileActivity;
import me.vipa.app.activities.profile.ui.ProfileActivityNew;
import me.vipa.app.activities.search.ui.ActivityResults;
import me.vipa.app.activities.series.ui.SeriesDetailActivity;
import me.vipa.app.activities.usermanagment.ui.ChangePasswordActivity;
import me.vipa.app.activities.usermanagment.ui.ForceLoginFbActivity;
import me.vipa.app.activities.usermanagment.ui.ForgotPasswordActivity;
import me.vipa.app.activities.usermanagment.ui.LoginActivity;
import me.vipa.app.activities.usermanagment.ui.SignUpActivity;
import me.vipa.app.activities.usermanagment.ui.SkipActivity;
import me.vipa.app.activities.watchList.ui.WatchListActivity;
import me.vipa.app.beanModel.responseModels.SignUp.DataModel;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;
import me.vipa.app.activities.search.ui.ActivitySearch;

import me.vipa.app.utils.helpers.StringUtils;
import com.google.gson.Gson;

import me.vipa.app.activities.article.ArticleActivity;
import me.vipa.app.activities.detail.ui.DetailActivity;
import me.vipa.app.activities.detail.ui.EpisodeActivity;
import me.vipa.app.activities.downloads.MyDownloads;
import me.vipa.app.activities.homeactivity.ui.HomeActivity;
import me.vipa.app.activities.listing.listui.ListActivity;
import me.vipa.app.activities.listing.ui.GridActivity;
import me.vipa.app.activities.live.LiveActivity;
import me.vipa.app.activities.notification.ui.NotificationActivity;
import me.vipa.app.activities.profile.ui.ProfileActivity;
import me.vipa.app.activities.profile.ui.ProfileActivityNew;
import me.vipa.app.activities.search.ui.ActivityResults;
import me.vipa.app.activities.search.ui.ActivitySearch;
import me.vipa.app.activities.series.ui.SeriesDetailActivity;
import me.vipa.app.activities.usermanagment.ui.ChangePasswordActivity;
import me.vipa.app.activities.usermanagment.ui.ForceLoginFbActivity;
import me.vipa.app.activities.usermanagment.ui.ForgotPasswordActivity;
import me.vipa.app.activities.usermanagment.ui.LoginActivity;
import me.vipa.app.activities.usermanagment.ui.SignUpActivity;
import me.vipa.app.activities.usermanagment.ui.SkipActivity;
import me.vipa.app.activities.watchList.ui.WatchListActivity;
import me.vipa.app.beanModel.responseModels.SignUp.DataModel;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.baseCollection.baseCategoryModel.BaseCategory;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class ActivityLauncher {
    private final Activity activity;

    public ActivityLauncher(Activity context) {
        this.activity = context;
    }

    public void signUpActivity(Activity source, Class<SignUpActivity> destination, String from) {
        Intent intent = new Intent(source, destination);
        intent.putExtra("loginFrom",from);
        activity.startActivity(intent);
    }

    public void skipActivity(Activity source, Class<SkipActivity> destination, DataModel model) {

        Bundle args = new Bundle();
        args.putParcelable(AppConstants.STRING_USER_DETAIL, model);
        Intent intent = new Intent(source, destination);
        intent.putExtra(AppConstants.EXTRA_REGISTER_USER, args);

        activity.startActivity(intent);
    }

    public void ProfileActivity(Activity source, Class<ProfileActivity> destination) {

        Intent intent = new Intent(source, destination);
        activity.startActivity(intent);
    }
    public void avatarActivity(Activity source, Class<AvatarImageActivity> destination) {

        Intent intent = new Intent(source, destination);
        activity.startActivity(intent);
    }

    public void ProfileActivityNew(Activity source, Class<ProfileActivityNew> destination) {

        Intent intent = new Intent(source, destination);
        activity.startActivity(intent);
    }

    public void changePassword(Activity source, Class<ChangePasswordActivity> destination) {
        Intent intent = new Intent(source, destination);
        activity.startActivity(intent);

    }
    public void signUpThird(Activity source, Class<SignUpThirdPage> destination) {
        Intent intent = new Intent(source, destination);
        activity.startActivity(intent);

    }

    public void loginActivity(Activity source, Class<LoginActivity> destination) {
        if (source instanceof HomeActivity){
            Intent intent = new Intent(source, destination);
            intent.putExtra("loginFrom","home");
            activity.startActivity(intent);
        }else {
            Intent intent = new Intent(source, destination);
            intent.putExtra("loginFrom","");
            activity.startActivity(intent);
        }


    }

    public void searchActivity(Activity source, Class<ActivitySearch> destination) {
        Intent intent = new Intent(source, destination);
        activity.startActivity(intent);
    }

    public void homeScreen(Activity source, Class<HomeActivity> destination) {
        Intent intent = new Intent(source, destination);
        TaskStackBuilder.create(source).addNextIntentWithParentStack(intent).startActivities();
    }
    public void onBoardingScreen(Activity source, Class<OnBoarding> destination) {
        Intent intent = new Intent(source, destination);
        activity.startActivity(intent);
    }

    public void onContentScreen(Activity source, Class<ContentPreference> destination) {
        Intent intent = new Intent(source, destination);
        activity.startActivity(intent);
    }


    public void detailScreen(Activity source, Class<DetailActivity> destination, int id, String duration, boolean isPremium) {
        Bundle args = new Bundle();
        args.putInt(AppConstants.BUNDLE_ASSET_ID, id);
        args.putInt(AppConstants.BUNDLE_VIDEO_ID_BRIGHTCOVE, id);

        args.putBoolean(AppConstants.BUNDLE_IS_PREMIUM, isPremium);
        if (StringUtils.isNullOrEmpty(duration))
            args.putString(AppConstants.BUNDLE_DURATION, "0");
        else
            args.putString(AppConstants.BUNDLE_DURATION, duration);
        Intent intent = new Intent(source, destination);
        intent.putExtra(AppConstants.BUNDLE_ASSET_BUNDLE, args);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
        preference.setAppPrefAssetId(0);
        activity.startActivity(intent);
    }

    public void articleScreen(Activity source, Class<ArticleActivity> destination, int id, String duration, boolean isPremium) {
        Bundle args = new Bundle();
        args.putInt(AppConstants.BUNDLE_ASSET_ID, id);
        args.putInt(AppConstants.BUNDLE_VIDEO_ID_BRIGHTCOVE, id);

        args.putBoolean(AppConstants.BUNDLE_IS_PREMIUM, isPremium);
        if (StringUtils.isNullOrEmpty(duration))
            args.putString(AppConstants.BUNDLE_DURATION, "0");
        else
            args.putString(AppConstants.BUNDLE_DURATION, duration);
        Intent intent = new Intent(source, destination);
        intent.putExtra(AppConstants.BUNDLE_ASSET_BUNDLE, args);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
        preference.setAppPrefAssetId(0);
        activity.startActivity(intent);
    }



    public void detailScreenBrightCove(Activity source, Class<DetailActivity> destination, Long videoId, int id, String duration, boolean isPremium, String detailType) {
        Bundle args = new Bundle();
        args.putInt(AppConstants.BUNDLE_ASSET_ID, id);
        args.putLong(AppConstants.BUNDLE_VIDEO_ID_BRIGHTCOVE, videoId);

        args.putBoolean(AppConstants.BUNDLE_IS_PREMIUM, isPremium);
        args.putString(AppConstants.BUNDLE_DETAIL_TYPE, detailType);

        //this is for non-series launcher
        if (StringUtils.isNullOrEmpty(duration))
            args.putString(AppConstants.BUNDLE_DURATION, "0");
        else
            args.putString(AppConstants.BUNDLE_DURATION, duration);

        Intent intent = new Intent(source, destination);
        intent.putExtra(AppConstants.BUNDLE_ASSET_BUNDLE, args);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
        preference.setAppPrefAssetId(0);
        Logger.e("JSON SENT",new Gson().toJson(args));

        activity.startActivity(intent);


    }

    public void liveScreenBrightCove(Activity source, Class<LiveActivity> destination, Long videoId, int id, String duration, boolean isPremium, String detailType) {
        Bundle args = new Bundle();
        args.putInt(AppConstants.BUNDLE_ASSET_ID, id);
        args.putLong(AppConstants.BUNDLE_VIDEO_ID_BRIGHTCOVE, videoId);

        args.putBoolean(AppConstants.BUNDLE_IS_PREMIUM, isPremium);
        args.putString(AppConstants.BUNDLE_DETAIL_TYPE, detailType);

        //this is for non-series launcher
        if (StringUtils.isNullOrEmpty(duration))
            args.putString(AppConstants.BUNDLE_DURATION, "0");
        else
            args.putString(AppConstants.BUNDLE_DURATION, duration);

        Intent intent = new Intent(source, destination);
        intent.putExtra(AppConstants.BUNDLE_ASSET_BUNDLE, args);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        KsPreferenceKeys preference = KsPreferenceKeys.getInstance();
        preference.setAppPrefAssetId(0);
        Logger.e("JSON SENT",new Gson().toJson(args));
        activity.startActivity(intent);
    }


    public void episodeScreenBrightcove(Activity source, Class<EpisodeActivity> destination, Long videoId, int id, String duration, boolean isPremium) {
        KsPreferenceKeys preference = KsPreferenceKeys.getInstance();

        Bundle args = new Bundle();
        args.putInt(AppConstants.BUNDLE_ASSET_ID, id);
        args.putLong(AppConstants.BUNDLE_VIDEO_ID_BRIGHTCOVE, videoId);
        args.putBoolean(AppConstants.BUNDLE_IS_PREMIUM, isPremium);

        if (StringUtils.isNullOrEmpty(duration))
            args.putString(AppConstants.BUNDLE_DURATION, "0");
        else
            args.putString(AppConstants.BUNDLE_DURATION, duration);
        Intent intent = new Intent(source, destination);
        intent.putExtra(AppConstants.BUNDLE_ASSET_BUNDLE, args);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        preference.setAppPrefAssetId(0);

        activity.startActivity(intent);
    }


    public void episodeScreen(Activity source, Class<EpisodeActivity> destination, int id, String duration, boolean isPremium) {
        KsPreferenceKeys preference = KsPreferenceKeys.getInstance();

        Bundle args = new Bundle();
        args.putInt(AppConstants.BUNDLE_ASSET_ID, id);
        args.putBoolean(AppConstants.BUNDLE_IS_PREMIUM, isPremium);
        //this is for non-series launcher
        if (StringUtils.isNullOrEmpty(duration))
            args.putString(AppConstants.BUNDLE_DURATION, "0");
        else
            args.putString(AppConstants.BUNDLE_DURATION, duration);
        Intent intent = new Intent(source, destination);
        intent.putExtra(AppConstants.BUNDLE_ASSET_BUNDLE, args);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        preference.setAppPrefAssetId(0);

        activity.startActivity(intent);
    }


    public void portraitListing(Activity source, Class<GridActivity> destination, String i, String title, int flag, int type, BaseCategory baseCategory) {
        Intent intent = new Intent(source, destination);
        intent.putExtra("playListId", i);
        intent.putExtra("title", title);
        intent.putExtra("flag", flag);
        intent.putExtra("shimmerType", type);
        intent.putExtra("baseCategory", baseCategory);
        activity.startActivity(intent);
    }

    public void listActivity(Activity source, Class<ListActivity> destination, String i, String title, int flag, int type, BaseCategory baseCategory) {
        Intent intent = new Intent(source, destination);
        intent.putExtra("playListId", i);
        intent.putExtra("title", title);
        intent.putExtra("flag", flag);
        intent.putExtra("shimmerType", type);
        intent.putExtra("baseCategory", baseCategory);
        activity.startActivity(intent);
    }

    public void notificationActivity(Activity source, Class<NotificationActivity> destination) {
        Intent intent = new Intent(source, destination);
        activity.startActivity(intent);
    }

    public void forgotPasswordActivity(Activity source, Class<ForgotPasswordActivity> destination) {
        Intent intent = new Intent(source, destination);
        activity.startActivity(intent);
    }


    public void forceLogin(Activity source, Class<ForceLoginFbActivity> destination, String token, String fid, String name, String picUrl) {

        Bundle args = new Bundle();
        args.putString("fbName", name);
        args.putString("fbToken", token);
        args.putString("fbId", fid);
        args.putString("fbProfilePic", picUrl);
        Intent intent = new Intent(source, destination);
        intent.putExtra(AppConstants.EXTRA_REGISTER_USER, args);

        activity.startActivity(intent);
    }

    public void resultActivityBundle(Activity source, Class<ActivityResults> destination, String searchType, String searchKey, int total) {
        Bundle args = new Bundle();
        args.putString("Search_Show_All", searchType);
        args.putString("Search_Key", searchKey);
        args.putInt("Total_Result", total);
        Intent intent = new Intent(source, destination);
        intent.putExtra("SearchResult", args);
        activity.startActivity(intent);
    }

    public void seriesDetailScreen(Activity source, Class<SeriesDetailActivity> destination, int seriesId) {
        Intent intent = new Intent(source, destination);
        intent.putExtra("seriesId", seriesId);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public void watchHistory(Activity source, Class<WatchListActivity> destination, String type, boolean isWatchHistory) {
        Bundle args = new Bundle();
        args.putString("viewType", type);
        Intent intent = new Intent(source, destination);
        intent.putExtra("bundleId", args);
        intent.putExtra("isWatchHistory", isWatchHistory);
        // activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(source).toBundle());
        activity.startActivity(intent);
    }
    public void launchMyDownloads(){
        this.activity.startActivity(new Intent(this.activity, MyDownloads.class));
    }
}
