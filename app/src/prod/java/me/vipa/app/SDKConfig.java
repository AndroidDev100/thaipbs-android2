package me.vipa.app;

import me.vipa.app.utils.MediaTypeConstants;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.config.bean.ConfigBean;

import java.util.ArrayList;
import java.util.List;

public class SDKConfig {

    private static SDKConfig sdkConfigInstance;
    boolean isTablet = MvHubPlusApplication.getInstance().getResources().getBoolean(R.bool.isTablet);
    ConfigBean configBean;

    private SDKConfig() {

    }

    public static SDKConfig getInstance() {
        if (sdkConfigInstance == null) {
            sdkConfigInstance = new SDKConfig();
        }
        return (sdkConfigInstance);
    }

    /*PROD keys*//*
     */

    public static String CONFIG_BASE_URL = "https://api.enveu.com/experience-manager-fe-api/app/api/v1/config/";
    public static String API_KEY_MOB = "oorgebfpjudysyrtdhwmlaqlavglgmtlvplaodpt";
    public static String API_KEY_TAB = "zzmnptygwjeybngjnumcrhcmwchszfjgkbeieflw";
    /* UAT keys*/
   /* public static String CONFIG_BASE_URL = "https://experience-manager-fe-api.uat.enveu.com/app/api/v1/config/";
    public static String API_KEY_MOB = "spkuaohqsngqcrvfrgforegkooveiobspgwwbmce";
    public static String API_KEY_TAB = "unwtftwilxazacjururwsdtwuepymqqxlvamahqf";*/
    public static int CONFIG_VERSION = 1;
    public static String ApplicationStatus = "disconnected";
    public static String TERMCONDITION = "https://www.mvhub.com/term.php";
    public static String PRIVACYPOLICY = "https://www.mvhub.com/privacy.php";
    public static String WEBP_QUALITY = "filters:format(webp):quality(60)/";

    /*uat keys*//*
    public static String CONFIG_BASE_URL = "https://experience-manager-fe-api.uat.enveu.com/app/api/v1/config/";
    public static String API_KEY_MOB = "zzSQzVoXQl9oA6d75OPCJ5UHowG2upc6520dJ3zs";
    public static String API_KEY_TAB = "2hpaUCvN395jJA6AMaamk8wyOQbvAGhNQaIuH2Nf";
    public static int CONFIG_VERSION = 1;
    public static String ApplicationStatus = "disconnected";*/

    public void setConfigObject(ConfigBean configResponse, boolean isTablet) {
        this.configBean = configResponse;
        MediaTypeConstants.getInstance().setConfigObject(configBean);
    }

    public String getBASE_URL() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getBaseUrl();
    }

    public String getOVP_BASE_URL() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getOvpBaseUrl();
    }

    public String getSEARCH_BASE_URL() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getSearchBaseUrl();
    }

    public String getCLOUD_FRONT_BASE_URL() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getCloudFrontBaseURL();
    }


    public String getSUBSCRIPTION_BASE_URL() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getSubscriptionBaseUrl();
    }

    public String getPAYMENT_BASE_URL() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getPaymentBaseUrl();
    }

    public String getCoupon_BASE_URL() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getCouponBaseUrl();
    }

    public String getTermCondition_URL() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getTermsConditionUrl();
    }

    public String getPrivay_Policy_URL() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getPrivacyPolicyUrl();
    }

    public String getFEEDBACK_URL() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getFeedbackUrl();
    }
    public String getFAQ_URL() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getFaqUrl();
    }
    public String getCONTACT_URL() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getContactUsUrl();
    }
    public String getABOUT_US_URL() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getAboutUsUrl();
    }

    public String getOvpApiKey() {
        if (isTablet) {
            return API_KEY_TAB;
        } else {
            return API_KEY_MOB;
        }
    }

    public String getFirstTabId() {
        return AppCommonMethod.getHomeTabId(configBean, "HOME");
    }

    public String getSecondTabId() {
        return AppCommonMethod.getHomeTabId(configBean, "PREMIUM");
    }

    public String getThirdTabId() {
        return AppCommonMethod.getHomeTabId(configBean, "FREE");
    }

    public String getFourthTabId() {
        return AppCommonMethod.getHomeTabId(configBean, "LIVE TV");
    }

    public String getMovieDetailId() {
        return AppCommonMethod.getHomeTabId(configBean, "MOVIES DETAIL");
    }

    public String getShowDetailId() {
        return AppCommonMethod.getHomeTabId(configBean, "SHOWS DETAIL");
    }

    public String getEpisodeDetailId() {
        return AppCommonMethod.getHomeTabId(configBean, "EPISODES DETAIL");
    }

    public String getSeriesDetailId() {
        return AppCommonMethod.getHomeTabId(configBean, "SERIES DETAIL");
    }

    public String getLiveDetailId() {
        return AppCommonMethod.getHomeTabId(configBean, "LIVETV DETAIL");
    }

    public String getPopularSearchId() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getPopularSearchId().toString();
    }

    public String getThaiLangCode() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getLanguageCodes().getThai();
    }

    public String getEnglishCode() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getLanguageCodes().getEnglish();
    }

    public List<String> getSupportedCurrencies() {
        return configBean == null ? new ArrayList<>() : configBean.getData().getAppConfig().getSupportedCurrencies();
    }

    public String getWebPUrl() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getImageTransformBaseURL();
    }

    public String getConfigVastTag() {
        return configBean == null ? "" : configBean.getData().getAppConfig().getVastTagUrl()==null ? "" : configBean.getData().getAppConfig().getVastTagUrl();
    }

    public boolean getBingeWatchingEnabled() {
        return configBean == null ? false : configBean.getData().getAppConfig().getBingeWatchingEnabled();
    }

    public int getTimer() {
        return configBean == null ? 0 : configBean.getData().getAppConfig().getTimer();
    }


}