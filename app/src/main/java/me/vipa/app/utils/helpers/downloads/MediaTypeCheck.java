package me.vipa.app.utils.helpers.downloads;

import me.vipa.app.SDKConfig;
import me.vipa.app.utils.MediaTypeConstants;

public class MediaTypeCheck {


    public static boolean isMediaTypeSupported(String assetType) {
        boolean supported=false;
        if (MediaTypeConstants.getInstance().getMovie().equalsIgnoreCase(assetType)
            || MediaTypeConstants.getInstance().getEpisode().equalsIgnoreCase(assetType)
                || MediaTypeConstants.getInstance().getShow().equalsIgnoreCase(assetType)){
            supported=true;
        }
        return supported;
    }
}
