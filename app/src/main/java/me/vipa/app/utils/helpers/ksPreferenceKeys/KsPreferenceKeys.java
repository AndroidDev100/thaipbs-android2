package me.vipa.app.utils.helpers.ksPreferenceKeys;

import android.content.Context;

import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.constants.SharedPrefesConstants;
import me.vipa.app.MvHubPlusApplication;
import me.vipa.app.utils.helpers.SharedPrefHelper;

import org.json.JSONObject;

import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.constants.SharedPrefesConstants;


public class KsPreferenceKeys extends SharedPrefHelper {

    private static  KsPreferenceKeys mInstance ;

    private static final String CURRENT_THEME = "CurrentTheme";
    private static final String APP_LANGUAGE = "app_language";
    private static final String LANGUAGE_POS = "language_pos";
    private static final String NUMBER_EMPTY = "NumberEmpty";
    private static final String DOB_EMPTY = "DOBEmpty";
    private static final String NOTIFICATION_PAYLOAD = "notification_payload";
    public static final String ENTITLEMENT_STATUS = "entitlement_status";
    public static final String AUTO_ROTATE = "auto_rotate";
    public static final String AUTO_DURATION = "auto_rotate_duration";
    public static final String USER_DATA = "user_data_new";
    public static final String FILTER_SELECTED_GENRE = "FILTER_SELECTED_GENRE";
    public static final String FILTER_SELECTED_GENRE_VALUE = "FILTER_SELECTED_GENRE_VALUE";

    public static final String FILTER_SELECTED_SORT = "FILTER_SELECTED_SORT";
    public static final String FILTER_SELECTED_SORT_VALUE = "FILTER_SELECTED_SORT_VALUE";
    public static final String FILTER_APPLY = "FILTER_APPLY";
    public static final String KIDS_MODE_ID = "KIDS_MODE_ID";
    public static final String OVP_BASE_URL = "OVP_BASE_URL";




    private static final String DOWNLOADED_ITEM_DELETED = "download_item_deleted";

    public static KsPreferenceKeys getInstance(){

        if (mInstance == null){
            mInstance = new KsPreferenceKeys(MvHubPlusApplication.getInstance());
        }
        return mInstance;
    }

    public KsPreferenceKeys(Context context) {
        super(context);
    }

    public String getCurrentTheme() {
        return getString(CURRENT_THEME, AppConstants.DARK_THEME);
    }

    public void setCurrentTheme(String currentTheme) {
        setString(CURRENT_THEME, currentTheme);
    }

    public String getAppLanguage() {
        return getString(APP_LANGUAGE, "");
    }

    public void setAppLanguage(String AppLanguage) {
        setString(APP_LANGUAGE, AppLanguage);
    }

    public String getAppPrefLastConfigHit() {
        return getString(AppConstants.APP_PREF_LAST_CONFIG_HIT, "0");
    }

    public void setAppPrefLastConfigHit(String LastConfigHit) {
        setString(AppConstants.APP_PREF_LAST_CONFIG_HIT, LastConfigHit);
    }

    public String getAppPrefLoginType() {
        return getString(AppConstants.APP_PREF_LOGIN_TYPE, "");
    }

    public void setAppPrefLoginType(String LoginType) {
        setString(AppConstants.APP_PREF_LOGIN_TYPE, LoginType);
    }

    public String getAppPrefLoginStatus() {
        return getString(AppConstants.APP_PREF_LOGIN_STATUS, "");
    }

    public void setAppPrefLoginStatus(String LoginStatus) {
        setString(AppConstants.APP_PREF_LOGIN_STATUS, LoginStatus);
    }

    public String getAppPrefRegisterStatus() {
        return getString(AppConstants.FINAL_APP_PREF_LOGIN_STATUS, "");
    }

    public void setAppPrefRegisterStatus(String LoginStatus) {
        setString(AppConstants.FINAL_APP_PREF_LOGIN_STATUS, LoginStatus);
    }

    public String getAppPrefConfigResponse() {
        return getString(AppConstants.APP_PREF_CONFIG_RESPONSE, "");
    }

    public void setAppPrefConfigResponse(String ConfigResponse) {
        setString(AppConstants.APP_PREF_CONFIG_RESPONSE, ConfigResponse);
    }

    public String getAppPrefAccessToken() {
        return getString(AppConstants.APP_PREF_ACCESS_TOKEN, "");
    }

    public void setAppPrefAccessToken(String AccessToken) {
        setString(AppConstants.APP_PREF_ACCESS_TOKEN, AccessToken);
    }

    public String getAppPrefVideoUrl() {
        return getString(AppConstants.APP_PREF_VIDEO_URL, "");
    }

    public void setAppPrefVideoUrl(String VideoUrl) {
        setString(AppConstants.APP_PREF_VIDEO_URL, VideoUrl);
    }

    public String getAppPrefAvailableVersion() {
        return getString(AppConstants.APP_PREF_AVAILABLE_VERSION, "");
    }

    public void setAppPrefAvailableVersion(String AvailableVersion) {
        setString(AppConstants.APP_PREF_AVAILABLE_VERSION, AvailableVersion);
    }

    public String getAppPrefCfep() {
        return getString(AppConstants.APP_PREF_CFEP, "");
    }

    public void setAppPrefCfep(String cfep) {
        setString(AppConstants.APP_PREF_CFEP, cfep);
    }

    public String getAppPrefConfigVersion() {
        return getString(AppConstants.APP_PREF_CONFIG_VERSION, "");
    }

    public void setAppPrefConfigVersion(String ConfigVersion) {
        setString(AppConstants.APP_PREF_CONFIG_VERSION, ConfigVersion);
    }

    public String getAppPrefServerBaseUrl() {
        return getString(AppConstants.APP_PREF_SERVER_BASE_URL, "");
    }

    public void setAppPrefServerBaseUrl(String BaseUrl) {
        setString(AppConstants.APP_PREF_SERVER_BASE_URL, BaseUrl);
    }

    public String getAppPrefJumpTo() {
        return getString(AppConstants.APP_PREF_JUMP_TO, "");
    }

    public void setAppPrefJumpTo(String BaseUrl) {
        setString(AppConstants.APP_PREF_JUMP_TO, BaseUrl);
    }

    public Boolean getAppPrefBranchIo() {
        return getBoolean("returnBack", false);
    }

    public void setAppPrefBranchIo(Boolean BranchIo) {
        setBoolean("returnBack", BranchIo);
    }

    public Boolean getfirstTimeUser() {
        return getBoolean("isFirstTimeUser", true);
    }

    public void setfirstTimeUser(Boolean firstTimeUser) {
        setBoolean("isFirstTimeUser", firstTimeUser);
    }

    public Boolean getfirstTimeUserForKidsPin() {
        return getBoolean("isFirstTimeUserKidsPin", true);
    }

    public void setfirstTimeUserForKidsPIn(Boolean firstTimeUserKidsPin) {
        setBoolean("isFirstTimeUserKidsPin", firstTimeUserKidsPin);
    }

    public void setPodId(String id, boolean reminderValue) {
        setBoolean("video_Id"+id, reminderValue);
    }

    public boolean getPodId(String id)
    {
        return getBoolean("video_Id"+id,false);
    }

    public Boolean getBingeWatchEnable() {
        return getBoolean("isBingeWatchEnable", true);
    }

    public void setBingeWatchEnable(Boolean isBingeWatchEnable) {
        setBoolean("isBingeWatchEnable", isBingeWatchEnable);
    }


    public int getAppPrefJumpBackId() {
        return getInt("returnBackId", 0);
    }

    public void setAppPrefJumpBackId(int BackId) { setInt("returnBackId", BackId); }

    public void setReturnTo(String video) {
        mInstance.setString(AppConstants.APP_PREF_JUMP_TO, video);
    }

    public Boolean getAppPrefJumpBack() {
        return getBoolean(AppConstants.APP_PREF_JUMP_BACK, false);
    }

    public void setAppPrefJumpBack(Boolean jumpBack) {
        setBoolean(AppConstants.APP_PREF_JUMP_BACK, jumpBack);
    }

    public Boolean getAppPrefIsEpisode() {
        return getBoolean(AppConstants.APP_PREF_IS_EPISODE, false);
    }

    public void setAppPrefIsEpisode(Boolean IsEpisode) {
        setBoolean(AppConstants.APP_PREF_IS_EPISODE, IsEpisode);
    }

    public Boolean getAppPrefGotoPurchase() {
        return getBoolean(AppConstants.APP_PREF_GOTO_PURCHASE, false);
    }

    public void setAppPrefGotoPurchase(Boolean purchase) {
        setBoolean(AppConstants.APP_PREF_GOTO_PURCHASE, purchase);
    }

    public int getAppPrefAssetId() {
        return getInt(AppConstants.APP_PREF_ASSET_ID, 0);
    }

    public void setAppPrefAssetId(int assetId) { setInt(AppConstants.APP_PREF_ASSET_ID, assetId); }

    public String getAppPrefVideoPosition() {
        return getString(AppConstants.APP_PREF_VIDEO_POSITION, "");
    }

    public void setAppPrefVideoPosition(String position) {
        setString(AppConstants.APP_PREF_VIDEO_POSITION, position);
    }

    public String getAppPrefProfile() {
        return getString(AppConstants.APP_PREF_PROFILE, "");
    }

    public void setAppPrefProfile(String profile) {
        setString(AppConstants.APP_PREF_PROFILE, profile);
    }

    public String getAppPrefUserId() {
        return getString(AppConstants.APP_PREF_USER_ID, "");
    }

    public void setAppPrefUserId(String userId) {
        setString(AppConstants.APP_PREF_USER_ID, userId);
    }

    public String getAppPrefUserName() {
        return getString(AppConstants.USER_NAME, "");
    }

    public void setAppPrefUserName(String userName) {
        setString(AppConstants.USER_NAME, userName);
    }

    public String getAppPrefUserEmail() {
        return getString(AppConstants.USER_EMAIL, "");
    }

    public void setAppPrefUserEmail(String userEmail) {
        setString(AppConstants.USER_EMAIL, userEmail);
    }

    public String getAppPrefUser() {
        return getString(AppConstants.APP_PREF_GET_USER, "");
    }

    public void setAppPrefUser(String user) {
        setString(AppConstants.APP_PREF_GET_USER, user);
    }

    public Boolean getAppPrefIsRestoreState() {
        return getBoolean(AppConstants.APP_PREF_IS_RESTORE_STATE, false);
    }

    public void setAppPrefIsRestoreState(Boolean restoreState) {
        setBoolean(AppConstants.APP_PREF_IS_RESTORE_STATE, restoreState);
    }

    public Boolean getAppPrefHasSelectedId() {
        return getBoolean(AppConstants.APP_PREF_HAS_SELECTED_ID, false);
    }

    public void setAppPrefHasSelectedId(Boolean hasSelectedId) {
        setBoolean(AppConstants.APP_PREF_HAS_SELECTED_ID, hasSelectedId);
    }

    public int getAppPrefSelectodSeasonId() {
        return getInt(AppConstants.APP_PREF_SELECTED_SEASON_ID, -1);
    }

    public void setAppPrefSelectodSeasonId(int seasonId) { setInt(AppConstants.APP_PREF_SELECTED_SEASON_ID, seasonId); }

    public String getAppPrefUpdateType() {
        return getString("update_type", "");
    }

    public void setAppPrefUpdateType(String updateType) {
        setString("update_type", updateType);
    }

    public int getAppPrefCurrentVersion() {
        return getInt("current_version", 0);
    }

    public void setAppPrefCurrentVersion(int currentVersion) {
        setInt("current_version", currentVersion);
    }

    public Boolean getAppPrefDOB() {
        return getBoolean(DOB_EMPTY, false);
    }

    public void setAppPrefHasDOB(Boolean hasDOB) {
        setBoolean(DOB_EMPTY, hasDOB);
    }

    public Boolean getAppPrefHasNumberEmpty() {
        return getBoolean(NUMBER_EMPTY, false);
    }

    public void setAppPrefHasNumberEmpty(Boolean hasNumberEmpty) {
        setBoolean(NUMBER_EMPTY, hasNumberEmpty);
    }

    public int getAppPrefLanguagePos() {
        return getInt(LANGUAGE_POS, 0);
    }

    public void setAppPrefLanguagePos(int languagePos) {
        setInt(LANGUAGE_POS, languagePos);
    }

    public String getAppPrefFcmToken() {
        return getString(AppConstants.FCM_TOKEN, "");
    }

    public void setAppPrefFcmToken(String fcmToken) {
        setString(AppConstants.FCM_TOKEN, fcmToken);
    }

    public void setNotificationPayload(String assetId, JSONObject payload) {
        setString(assetId+NOTIFICATION_PAYLOAD, payload.toString());
    }

    public String getNotificationPayload(String assetId) {
        return getString(assetId+NOTIFICATION_PAYLOAD, "");
    }

    public int getQualityPosition() {
        return getInt("video_quality_position", 0);
    }

    public void setQualityPosition(int qualityPosition) {
        setInt("video_quality_position", qualityPosition);
    }

    public String getQualityName() {
        return getString("video_quality_name", "");
    }

    public void setQualityName(String qualityName) {
       setString("video_quality_name", qualityName);
    }

    public void setDownloadOverWifi(Integer value){
        setInt(SharedPrefesConstants.DOWNLOAD_OVER_WIFI, value);
    }

    public int getDownloadOverWifi() {
        return getInt(SharedPrefesConstants.DOWNLOAD_OVER_WIFI, 1);
    }

    public void setEntitlementState(boolean status) {
        setBoolean(ENTITLEMENT_STATUS, status);
    }

    public Boolean getEntitlementStatus() {
        return getBoolean(ENTITLEMENT_STATUS,false);
    }

    public int getAutoDuration() {
        return getInt(AUTO_DURATION, 0);
    }

    public void setAutoDuration(int value) {
        setInt(AUTO_DURATION, value);
    }

    public boolean getAutoRotation() {
        return getBoolean(AUTO_ROTATE, true);
    }

    public void setAutoRotation(boolean value) {
        setBoolean(AUTO_ROTATE, value);
    }

    public void setUserProfileData(String profileData) {
        setString(USER_DATA, profileData);
    }

    public String getUserProfileData() {
        return getString(USER_DATA, "");
    }


    public void setFilterGenre(int profileData) {
        mInstance.setInt(FILTER_SELECTED_GENRE, profileData);
    }

    public int getFilterGenre() {
        return mInstance.getInt(FILTER_SELECTED_GENRE, 1 );
    }


    public void setFilterGenreSelection(String profileData) {
        mInstance.setString(FILTER_SELECTED_GENRE_VALUE, profileData);
    }

    public String getFilterGenreSelection() {
        return mInstance.getString(FILTER_SELECTED_GENRE_VALUE, "");
    }

    /////////////////////////////////////
    public void setFilterSort(String profileData) {
        mInstance.setString(FILTER_SELECTED_SORT, profileData);
    }

    public String getFilterSort() {
        return mInstance.getString(FILTER_SELECTED_SORT, "");
    }


    public void setFilterSortSelection(String profileData) {
        mInstance.setString(FILTER_SELECTED_SORT_VALUE, profileData);
    }

    public String getFilterSortSelection() {
        return mInstance.getString(FILTER_SELECTED_SORT_VALUE, "");
    }

    public void setFilterApply(String profileData) {
        mInstance.setString(FILTER_APPLY, profileData);
    }

    public String getFilterApply() {
        return mInstance.getString(FILTER_APPLY, "");
    }

    public void setKidsModeId(String id) {
        mInstance.setString(KIDS_MODE_ID, id);
    }

    public String getKidsModeId() {
        return mInstance.getString(KIDS_MODE_ID, "");
    }


    public String getOVPBASEURL() {
        return getString(OVP_BASE_URL, "");
    }

    public void setOVPBASEURL(String AppLanguage) {
        setString(OVP_BASE_URL, AppLanguage);
    }


}
