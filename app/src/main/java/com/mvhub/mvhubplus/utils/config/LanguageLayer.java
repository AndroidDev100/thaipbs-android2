package com.mvhub.mvhubplus.utils.config;

import com.mvhub.mvhubplus.utils.cropImage.helpers.Logger;
import com.mvhub.mvhubplus.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;
import com.mvhub.mvhubplus.SDKConfig;

public class LanguageLayer {
    private static LanguageLayer languageLayerInstance;

    private LanguageLayer() {

    }

    public static LanguageLayer getInstance() {
        if (languageLayerInstance == null) {
            languageLayerInstance = new LanguageLayer();
        }
        return (languageLayerInstance);
    }



    public static String getCurrentLanguageCode() {
        String languageCode="";
        Logger.w("languageValues",SDKConfig.getInstance().getThaiLangCode());
        if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Thai") || KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("हिंदी") ){
            languageCode= SDKConfig.getInstance().getThaiLangCode();
        }else {
            languageCode= SDKConfig.getInstance().getEnglishCode();
        }
        return languageCode;
    }
}

