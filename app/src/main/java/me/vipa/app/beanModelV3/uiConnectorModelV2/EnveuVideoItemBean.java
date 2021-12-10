package me.vipa.app.beanModelV3.uiConnectorModelV2;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.vipa.app.MvHubPlusApplication;
import me.vipa.app.R;
import me.vipa.app.beanModelV3.continueWatching.DataItem;
import me.vipa.app.beanModelV3.playListModelV2.VideosItem;
import me.vipa.app.beanModelV3.searchV2.ItemsItem;
import me.vipa.app.beanModelV3.videoDetailsV2.EnveuVideoDetailsBean;
import me.vipa.app.utils.CustomeFields;
import me.vipa.app.utils.config.ImageLayer;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;
import me.vipa.brightcovelibrary.Logger;

public class EnveuVideoItemBean implements Serializable {
    private ArrayList seasons;
    private String description;


    private String longDescription;
    private List<String> assetKeywords;
    private int likeCount;
    private String title;
    private Object svod;
    private String contentProvider;
    private List<String> assetCast;
    private boolean premium;
    private String posterURL;
    private Object price;
    private List<String> assetGenres;
    private String season;
    private int id;
    private String sku;
    private boolean isNew;
    private Object tvod;
    private Object episodeNo;
    private String assetType;
    private int commentCount;
    private String uploadedAssetKey;
    private String brightcoveVideoId;
    private String series;
    private String seriesId;
    private Object plans;
    private long publishedDate;
    private String status;
    private int responseCode;
    private long duration;
    private String name;
    private int vodCount;
    private int seasonCount;
    private String thumbnailImage;

    private long videoPosition;
    private int contentOrder;
    private String seasonNumber;
    private String imageType;

    private String parentalRating;
    private String signedLangEnabled;
    private String isPodcast;
    private String is4k;
    private String isClosedCaption;
    private String isSoundTrack;
    private String isAudioDesc;
    private String seasonName;
    private String trailerReferenceId;

    public String getIs4k() {
        return is4k;
    }

    public void setIs4k(String is4k) {
        this.is4k = is4k;
    }

    public String getIsClosedCaption() {
        return isClosedCaption;
    }

    public void setIsClosedCaption(String isClosedCaption) {
        this.isClosedCaption = isClosedCaption;
    }

    public String getIsSoundTrack() {
        return isSoundTrack;
    }

    public void setIsSoundTrack(String isSoundTrack) {
        this.isSoundTrack = isSoundTrack;
    }

    public String getIsAudioDesc() {
        return isAudioDesc;
    }

    public void setIsAudioDesc(String isAudioDesc) {
        this.isAudioDesc = isAudioDesc;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public String getTrailerReferenceId() {
        return trailerReferenceId;
    }

    public void setTrailerReferenceId(String trailerReferenceId) {
        this.trailerReferenceId = trailerReferenceId;
    }

    public String getIscomingsoon() {
        return iscomingsoon;
    }

    public void setIscomingsoon(String iscomingsoon) {
        this.iscomingsoon = iscomingsoon;
    }

    public String getIsPodcast(){
        return isPodcast;
    }

    public void setIsPodcast(String isPodcast) {
        this.isPodcast = isPodcast;
    }

    public String getSignedLangEnabled() {
        return signedLangEnabled;
    }

    public void setSignedLangEnabled(String signedLangEnabled) {
        this.signedLangEnabled = signedLangEnabled;
    }

    public String getSignedLangParentRefId() {
        return signedLangParentRefId;
    }

    public void setSignedLangParentRefId(String signedLangParentRefId) {
        this.signedLangParentRefId = signedLangParentRefId;
    }

    private String signedLangParentRefId;
    private String signedLangRefId;

    public String getSignedLangRefId() {
        return signedLangRefId;
    }

    public void setSignedLangRefId(String signedLangRefId) {
        this.signedLangRefId = signedLangRefId;
    }

    private String iscomingsoon;
    private String widevineLicence;
    private String getWidevineURL;
    private String country;
    private String company;
    private String year;
    private String isNewS;
    private String isVIP;
    private String VastTag;
    private String islivedrm="false";

    public String getIslivedrm() {
        return islivedrm;
    }

    public void setIslivedrm(String islivedrm) {
        this.islivedrm = islivedrm;
    }

    public EnveuVideoItemBean() {
    }

    //description page - single asset parsing
    public EnveuVideoItemBean(EnveuVideoDetailsBean details) {
        try {
            this.title = details.getData().getTitle() == null ? "" : details.getData().getTitle();
            this.description = details.getData().getDescription() == null ? "" : details.getData().getDescription().trim();
            this.assetGenres = details.getData().getGenres() == null ? new ArrayList<>() : details.getData().getGenres();
            this.assetCast = details.getData().getCast() == null ? new ArrayList<>() : details.getData().getCast();

            //this.svod = details.getData().getSvod() == null ? "" : details.getData().getSvod();
            this.contentProvider = details.getData().getContentProvider() == null ? "" : details.getData().getContentProvider();
            this.premium = details.getData().getPremium();
           /* if (details.getData().getImages()!=null && details.getData().getImages().getPoster()!=null && details.getData().getImages().getPoster().getSources()!=null
                    && details.getData().getImages().getPoster().getSources().size()>0){
                this.posterURL = details.getData().getImages().getPoster().getSources().get(0).getSrc();
            }*/
            this.posterURL = ImageLayer.getInstance().getPosterImageUrl(details.getData());

            //this.price = details.getData().getPrice() == null ? "" : details.getData().getPrice();

            this.season = "";
            if (details.getData().getLinkedContent() != null) {
                this.seriesId = String.valueOf(details.getData().getLinkedContent().getId());
            }
            /*if (details.getData().getLinkedContent()!=null){
                Object linkedContent = details.getData().getLinkedContent();
                LinkedTreeMap<Object, Object> content = (LinkedTreeMap) linkedContent;
                if (content.containsKey("id")){
                    this.series = content.get("id").toString();
                }else {
                    this.series = "";
                }
            }else {
                this.series = "";
            }*/
            this.sku = details.getData().getSku() == null ? "" : details.getData().getSku();
            this.id = details.getData().getId();
            this.isNew = false;
            //this.tvod = details.getData().getTvod() == null ? "" : details.getData().getTvod();
            this.episodeNo = details.getData().getEpisodeNumber() == null ? "" : details.getData().getEpisodeNumber();
            this.assetType = details.getData().getContentType() == null ? "" : details.getData().getContentType();
            this.brightcoveVideoId = details.getData().getBrightcoveContentId() == null ? "" : details.getData().getBrightcoveContentId();
            this.series = String.valueOf(details.getData().getId());
            ;
            this.status = details.getData().getStatus() == null ? "" : details.getData().getStatus();


            Object customeFiled = details.getData().getCustomFields();
            LinkedTreeMap<Object, Object> t = (LinkedTreeMap) customeFiled;

            if (t.containsKey(CustomeFields.WIDEVINE_LICENCE)){
                String widevineLicence = t.get((CustomeFields.WIDEVINE_LICENCE)).toString();
                this.widevineLicence = widevineLicence;
            }
            if (t.containsKey(CustomeFields.ISLIVEDRM)){
                String isLiveDrm = t.get((CustomeFields.ISLIVEDRM)).toString();
                this.islivedrm = isLiveDrm;
            }

            if (t.containsKey(CustomeFields.WIDEVINE_URL)) {
                String widevineURL = t.get((CustomeFields.WIDEVINE_URL)).toString();
                this.getWidevineURL = widevineURL;
            }

            if (t.containsKey(CustomeFields.parentalRating)) {
                String parentalRating = t.get((CustomeFields.parentalRating)).toString();
                this.parentalRating = parentalRating;
            }

            if (t.containsKey(CustomeFields.is4k)) {
                String is4k = t.get((CustomeFields.is4k)).toString();
                this.is4k = is4k;
            }

            if (t.containsKey(CustomeFields.isComingSoon)) {
                String isComingSoon = t.get((CustomeFields.isComingSoon)).toString();
                this.iscomingsoon = isComingSoon;
            }

            if (t.containsKey(CustomeFields.Country)) {
                String country = t.get((CustomeFields.Country)).toString();
                this.country = country;
            }

            if (t.containsKey(CustomeFields.company)) {
                String company = t.get((CustomeFields.company)).toString();
                this.company = company;
            }

            if (t.containsKey(CustomeFields.year)) {
                String year = t.get((CustomeFields.year)).toString();
                this.year = year;
            }

            if (t.containsKey(CustomeFields.VastTag)) {
                String year = t.get((CustomeFields.VastTag)).toString();
                this.VastTag = year;
            }


            if (t.containsKey(CustomeFields.IsVip)) {
                String vip = t.get((CustomeFields.IsVip)).toString();
                this.isVIP = vip;
            }

            if (t.containsKey(CustomeFields.IsNew)) {
                String isNew = t.get((CustomeFields.IsNew)).toString();
                this.isNewS = isNew;
            }

            if (t.containsKey(CustomeFields.ISSIGNEDLANGUAGE)) {
                String isNew = t.get((CustomeFields.ISSIGNEDLANGUAGE)).toString();
                this.signedLangEnabled = isNew;
            }

            if (t.containsKey(CustomeFields.SIGNEDLANGUAGEPARENTREGRENCEID)) {
                String isNew = t.get((CustomeFields.SIGNEDLANGUAGEPARENTREGRENCEID)).toString();
                this.signedLangParentRefId = isNew;
            }

            if (t.containsKey(CustomeFields.SIGNEDLANGUAGEREFRENCEID)) {
                String isNew = t.get((CustomeFields.SIGNEDLANGUAGEREFRENCEID)).toString();
                this.signedLangRefId = isNew;
            }

            if (t.containsKey(CustomeFields.IS_PODCAST)) {
                String isNew = t.get((CustomeFields.IS_PODCAST)).toString();
                this.isPodcast = isNew;
            }

            if (t.containsKey(CustomeFields.isClosedCaption)) {
                final Object obj = t.get((CustomeFields.isClosedCaption));
                if (obj != null) {
                    this.isClosedCaption = obj.toString();
                }
            }

            if (t.containsKey(CustomeFields.isSoundTrack)) {
                final Object obj = t.get(CustomeFields.isSoundTrack);
                if (obj != null) {
                    this.isSoundTrack = obj.toString();
                }
            }

            if (t.containsKey(CustomeFields.isAudioDescription)) {
                final Object obj = t.get(CustomeFields.isAudioDescription);
                if (obj != null) {
                    this.isAudioDesc = obj.toString();
                }
            }

            if (t.containsKey(CustomeFields.seasonName)) {
                final Object obj = t.get(CustomeFields.seasonName);
                if (obj != null) {
                    this.seasonName = obj.toString();
                }
            }

            if (t.containsKey(CustomeFields.trailerReferenceId)) {
                final Object obj = t.get(CustomeFields.trailerReferenceId);
                if (obj != null) {
                    this.trailerReferenceId = obj.toString();
                }
            }

            this.longDescription = details.getData().getLongDescription() == null ? "" : details.getData().getLongDescription().toString().trim();


            //series realated data
            this.vodCount = 0;
            this.seasonNumber = details.getData().getSeasonNumber() == null ? "" : details.getData().getSeasonNumber().toString().replaceAll("\\.0*$", "");
            if (details.getData().getSeasons() != null) {
                ArrayList arrayList = (ArrayList) details.getData().getSeasons();
                this.seasons = arrayList;
                this.seasonCount = arrayList.size();
            }

            this.duration = details.getData().getDuration();

        } catch (Exception e) {
            Logger.e("parsing error", e.getMessage());
            Logger.w(e);
        }

    }

    //for asset details.......
    public EnveuVideoItemBean(VideosItem details, int contentOrder, String imageType) {

            try {

                this.title = details.getTitle() == null ? "" : details.getTitle();
                this.description = details.getDescription() == null ? "" : details.getDescription().trim();
                this.assetGenres = details.getGenres() == null ? new ArrayList<>() : details.getGenres();
                this.assetCast = details.getCast() == null ? new ArrayList<>() : details.getCast();
          //  this.svod = details.getSvod() == null ? "" : details.getSvod();
            this.contentProvider = details.getContentProvider() == null ? "" : details.getContentProvider();
            this.assetCast = details.getCast() == null ? new ArrayList<>() : details.getCast();
            this.premium = details.getPremium();

                this.posterURL = ImageLayer.getInstance().getPosterImageUrl(details);
               /* if (details.getImages()!=null && details.getImages().getPoster()!=null && details.getImages().getPoster().getSources()!=null
                        && details.getImages().getPoster().getSources().size()>0){
                     details.getImages().getPoster().getSources().get(0).getSrc();
                }*/
           // this.posterURL = details.getImages()== null ? "" : details.getImages().getPoster().getSources().toString();
          //  this.price = details.getPrice() == null ? "" : details.getPrice();
            this.assetGenres = details.getGenres() == null ? new ArrayList<>() : details.getGenres();
            this.season = "";
            this.id = details.getId();
            this.sku = details.getSku() == null ? "" : details.getSku();
            this.isNew = false;
          //  this.tvod = details.getTvod() == null ? "" : details.getTvod();
            this.episodeNo = details.getEpisodeNumber() == null ? "" : details.getEpisodeNumber();
            this.assetType = details.getContentType() == null ? "" : details.getContentType();
                Logger.w("assetType",assetType);
            this.brightcoveVideoId = details.getBrightcoveContentId() == null ? "" : details.getBrightcoveContentId();

            this.series = "";
            this.status = details.getStatus() == null ? "" : details.getStatus();
            this.contentOrder = contentOrder;
            if (imageType != null) {
                this.imageType = imageType;
            } else {
                this.imageType = "";
            }

            Object customeFiled = details.getCustomFields();
            LinkedTreeMap<Object, Object> t = (LinkedTreeMap) customeFiled;

            if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")) {

                if (t.containsKey(CustomeFields.parentalRating)){
                    String parentalRating = t.get((CustomeFields.parentalRating)).toString();
                    this.parentalRating = parentalRating;
                }else {
                    if (t.containsKey(CustomeFields.rating)){
                        String parentalRating = t.get((CustomeFields.rating)).toString();
                        this.parentalRating = parentalRating;
                    }
                }

                if (t.containsKey(CustomeFields.is4k)) {
                    String is4k = t.get((CustomeFields.is4k)).toString();
                    this.is4k = is4k;
                }

                if (t.containsKey(CustomeFields.isComingSoon)) {
                    String isComingSoon = t.get((CustomeFields.isComingSoon)).toString();
                    this.iscomingsoon = isComingSoon;
                }

                if (t.containsKey(CustomeFields.Country)){
                    String country = t.get((CustomeFields.Country)).toString();
                    this.country = country;
                }

                if (t.containsKey(CustomeFields.company)){
                    String company = t.get((CustomeFields.company)).toString();
                    this.company = company;
                }

                if (t.containsKey(CustomeFields.year)){
                    String year = t.get((CustomeFields.year)).toString();
                    this.year = year;
                }

                if (t.containsKey(CustomeFields.VastTag)){
                    String year = t.get((CustomeFields.VastTag)).toString();
                    this.VastTag = year;
                }

                if (t.containsKey(CustomeFields.ISSIGNEDLANGUAGE)) {
                    String isNew = t.get((CustomeFields.ISSIGNEDLANGUAGE)).toString();
                    this.signedLangEnabled = isNew;
                }
                if (t.containsKey(CustomeFields.SIGNEDLANGUAGEPARENTREGRENCEID)) {
                    String isNew = t.get((CustomeFields.SIGNEDLANGUAGEPARENTREGRENCEID)).toString();
                    this.signedLangParentRefId = isNew;
                }

                if (t.containsKey(CustomeFields.SIGNEDLANGUAGEREFRENCEID)) {
                    String isNew = t.get((CustomeFields.SIGNEDLANGUAGEREFRENCEID)).toString();
                    this.signedLangRefId = isNew;
                }

                if (t.containsKey(CustomeFields.IS_PODCAST)) {
                    String isNew = t.get((CustomeFields.IS_PODCAST)).toString();
                    this.isPodcast = isNew;
                }

            }else {

                if (t.containsKey(CustomeFields.rating)){
                    String parentalRating = t.get((CustomeFields.rating)).toString();
                    this.parentalRating = parentalRating;
                }
                if (t.containsKey(CustomeFields.is4k)) {
                    String is4k = t.get((CustomeFields.is4k)).toString();
                    this.is4k = is4k;
                }

                if (t.containsKey(CustomeFields.isComingSoon)) {
                    String isComingSoon = t.get((CustomeFields.isComingSoon)).toString();
                    this.iscomingsoon = isComingSoon;
                }

                if (t.containsKey(CustomeFields.Country)){
                    String country = t.get((CustomeFields.Country)).toString();
                    this.country = country;
                }

                if (t.containsKey(CustomeFields.company)){
                    String company = t.get((CustomeFields.company)).toString();
                    this.company = company;
                }

                if (t.containsKey(CustomeFields.year)){
                    String year = t.get((CustomeFields.year)).toString();
                    this.year = year;
                }

                if (t.containsKey(CustomeFields.VastTag)){
                    String year = t.get((CustomeFields.VastTag)).toString();
                    this.VastTag = year;
                }

                if (t.containsKey(CustomeFields.ISSIGNEDLANGUAGE)) {
                    String isNew = t.get((CustomeFields.ISSIGNEDLANGUAGE)).toString();
                    this.signedLangEnabled = isNew;
                }

                if (t.containsKey(CustomeFields.SIGNEDLANGUAGEPARENTREGRENCEID)) {
                    String isNew = t.get((CustomeFields.SIGNEDLANGUAGEPARENTREGRENCEID)).toString();
                    this.signedLangParentRefId = isNew;
                }

                if (t.containsKey(CustomeFields.SIGNEDLANGUAGEREFRENCEID)) {
                    String isNew = t.get((CustomeFields.SIGNEDLANGUAGEREFRENCEID)).toString();
                    this.signedLangRefId = isNew;
                }

                if (t.containsKey(CustomeFields.IS_PODCAST)) {
                    String isNew = t.get((CustomeFields.IS_PODCAST)).toString();
                    this.isPodcast = isNew;
                }

            }

        if (t.containsKey(CustomeFields.IsVip)){
            String vip = t.get((CustomeFields.IsVip)).toString();
            this.isVIP = vip;
        }

        if (t.containsKey(CustomeFields.IsNew)){
            String isNew = t.get((CustomeFields.IsNew)).toString();
            this.isNewS = isNew;
        }

            this.longDescription = details.getLongDescription() == null ? "" : details.getLongDescription().toString().trim();


            //series realated data
            if (details.getSeasons() != null) {
                ArrayList arrayList = (ArrayList) details.getSeasons();
                this.seasonCount = arrayList.size();
            }


            this.duration = (long) details.getDuration();

            }catch (Exception e){

            }
    }

    //for continue watching.......
    public EnveuVideoItemBean(DataItem details) {

            try {

                    //  this.svod = details.getSvod() == null ? "" : details.getSvod();
                    this.title = details.getTitle() == null ? "" : details.getTitle();
                    this.description = details.getDescription() == null ? "" : details.getDescription().trim();
                    this.assetGenres = details.getGenres() == null ? new ArrayList<>() : details.getGenres();
                    this.assetCast = details.getCast() == null ? new ArrayList<>() : details.getCast();

                    this.contentProvider = details.getContentProvider() == null ? "" : details.getContentProvider();
                    this.assetCast = details.getCast() == null ? new ArrayList<>() : details.getCast();
                    //  this.premium = details.isPremium();

                    this.posterURL = ImageLayer.getInstance().getPosterImageUrl(details);
               /* if (details.getImages()!=null && details.getImages().getPoster()!=null && details.getImages().getPoster().getSources()!=null
                        && details.getImages().getPoster().getSources().size()>0){
                     details.getImages().getPoster().getSources().get(0).getSrc();
                }*/
                    // this.posterURL = details.getImages()== null ? "" : details.getImages().getPoster().getSources().toString();
                    //  this.price = details.getPrice() == null ? "" : details.getPrice();
                    this.assetGenres = details.getGenres() == null ? new ArrayList<>() : details.getGenres();
                    this.season = "";
                    this.id = details.getId();
                    this.sku = details.getSku() == null ? "" : details.getSku();
                    this.isNew = false;
                    //  this.tvod = details.getTvod() == null ? "" : details.getTvod();
                    this.episodeNo = details.getEpisodeNumber() == null ? "" : details.getEpisodeNumber();
                    this.assetType = details.getContentType() == null ? "" : details.getContentType();
                    this.brightcoveVideoId = details.getBrightcoveContentId() == null ? "" : details.getBrightcoveContentId();
                    this.series = "";
                    this.status = details.getStatus() == null ? "" : details.getStatus();
                    if (details.getPosition()!=null){
                    this.videoPosition = details.getPosition();
                    }

                    this.contentOrder = contentOrder;
                    if (imageType != null) {
                        this.imageType = imageType;
                    } else {
                        this.imageType = "";
                    }

                    Object customeFiled = details.getCustomFields();
                    LinkedTreeMap<Object, Object> t = (LinkedTreeMap) customeFiled;


                    if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")) {


                        if (t.containsKey(CustomeFields.parentalRating)) {
                            String parentalRating = t.get((CustomeFields.parentalRating)).toString();
                            this.parentalRating = parentalRating;
                        } else {
                            if (t.containsKey(CustomeFields.rating)) {
                                String parentalRating = t.get((CustomeFields.rating)).toString();
                                this.parentalRating = parentalRating;
                            }
                        }
                        if (t.containsKey(CustomeFields.is4k)) {
                            String is4k = t.get((CustomeFields.is4k)).toString();
                            this.is4k = is4k;
                        }

                        if (t.containsKey(CustomeFields.isComingSoon)) {
                            String isComingSoon = t.get((CustomeFields.isComingSoon)).toString();
                            this.iscomingsoon = isComingSoon;
                        }

                        if (t.containsKey(CustomeFields.Country)) {
                            String country = t.get((CustomeFields.Country)).toString();
                            this.country = country;
                        }

                        if (t.containsKey(CustomeFields.company)) {
                            String company = t.get((CustomeFields.company)).toString();
                            this.company = company;
                        }

                        if (t.containsKey(CustomeFields.year)) {
                            String year = t.get((CustomeFields.year)).toString();
                            this.year = year;
                        }

                        if (t.containsKey(CustomeFields.VastTag)) {
                            String year = t.get((CustomeFields.VastTag)).toString();
                            this.VastTag = year;
                        }

                        if (t.containsKey(CustomeFields.ISSIGNEDLANGUAGE)) {
                            String isNew = t.get((CustomeFields.ISSIGNEDLANGUAGE)).toString();
                            this.signedLangEnabled = isNew;
                        }
                        if (t.containsKey(CustomeFields.SIGNEDLANGUAGEPARENTREGRENCEID)) {
                            String isNew = t.get((CustomeFields.SIGNEDLANGUAGEPARENTREGRENCEID)).toString();
                            this.signedLangParentRefId = isNew;
                        }

                        if (t.containsKey(CustomeFields.SIGNEDLANGUAGEREFRENCEID)) {
                            String isNew = t.get((CustomeFields.SIGNEDLANGUAGEREFRENCEID)).toString();
                            this.signedLangRefId = isNew;
                        }

                        if (t.containsKey(CustomeFields.IS_PODCAST)) {
                            String isNew = t.get((CustomeFields.IS_PODCAST)).toString();
                            this.isPodcast = isNew;
                        }


                    } else {

                        if (t.containsKey(CustomeFields.rating)) {
                            String parentalRating = t.get((CustomeFields.rating)).toString();
                            this.parentalRating = parentalRating;
                        }
                        if (t.containsKey(CustomeFields.is4k)) {
                            String is4k = t.get((CustomeFields.is4k)).toString();
                            this.is4k = is4k;
                        }

                        if (t.containsKey(CustomeFields.isComingSoon)) {
                            String isComingSoon = t.get((CustomeFields.isComingSoon)).toString();
                            this.iscomingsoon = isComingSoon;
                        }

                        if (t.containsKey(CustomeFields.Country)) {
                            String country = t.get((CustomeFields.Country)).toString();
                            this.country = country;
                        }

                        if (t.containsKey(CustomeFields.company)) {
                            String company = t.get((CustomeFields.company)).toString();
                            this.company = company;
                        }

                        if (t.containsKey(CustomeFields.year)) {
                            String year = t.get((CustomeFields.year)).toString();
                            this.year = year;
                        }

                        if (t.containsKey(CustomeFields.VastTag)) {
                            String year = t.get((CustomeFields.VastTag)).toString();
                            this.VastTag = year;
                        }

                        if (t.containsKey(CustomeFields.ISSIGNEDLANGUAGE)) {
                            String isNew = t.get((CustomeFields.ISSIGNEDLANGUAGE)).toString();
                            this.signedLangEnabled = isNew;
                        }

                        if (t.containsKey(CustomeFields.SIGNEDLANGUAGEPARENTREGRENCEID)) {
                            String isNew = t.get((CustomeFields.SIGNEDLANGUAGEPARENTREGRENCEID)).toString();
                            this.signedLangParentRefId = isNew;
                        }

                        if (t.containsKey(CustomeFields.SIGNEDLANGUAGEREFRENCEID)) {
                            String isNew = t.get((CustomeFields.SIGNEDLANGUAGEREFRENCEID)).toString();
                            this.signedLangRefId = isNew;
                        }

                        if (t.containsKey(CustomeFields.IS_PODCAST)) {
                            String isNew = t.get((CustomeFields.IS_PODCAST)).toString();
                            this.isPodcast = isNew;
                        }


                    }

                    if (t.containsKey(CustomeFields.IsVip)) {
                        String vip = t.get((CustomeFields.IsVip)).toString();
                        this.isVIP = vip;
                    }

                    if (t.containsKey(CustomeFields.IsNew)) {
                        String isNew = t.get((CustomeFields.IsNew)).toString();
                        this.isNewS = isNew;
                    }

                    //meditype tyep - 1 --->>> series ----isnew-true----new series ---episode- new episode --- movie-new movie
                    //
                    this.longDescription = details.getLongDescription() == null ? "" : details.getLongDescription().toString().trim();

                    //series realated data
                    if (details.getSeasons() != null) {
                        ArrayList arrayList = (ArrayList) details.getSeasons();
                        this.seasonCount = arrayList.size();
                    }
                this.duration = (long) details.getDuration();
            } catch (Exception ignored) {
            Logger.e("ContinueWatching", ignored.getMessage());
        }

    }

    //for search data.......
    public EnveuVideoItemBean(ItemsItem details) {

        try {

            //  this.svod = details.getSvod() == null ? "" : details.getSvod();
            this.title = details.getTitle() == null ? "" : details.getTitle();
            this.description = details.getDescription() == null ? "" : details.getDescription().trim();
            this.assetGenres = details.getGenres() == null ? new ArrayList<>() : details.getGenres();
            this.assetCast = details.getCast() == null ? new ArrayList<>() : details.getCast();

            this.contentProvider = details.getContentProvider() == null ? "" : details.getContentProvider();
            this.assetCast = details.getCast() == null ? new ArrayList<>() : details.getCast();
            //  this.premium = details.isPremium();

            this.posterURL = ImageLayer.getInstance().getPosterImageUrl(details);
               /* if (details.getImages()!=null && details.getImages().getPoster()!=null && details.getImages().getPoster().getSources()!=null
                        && details.getImages().getPoster().getSources().size()>0){
                     details.getImages().getPoster().getSources().get(0).getSrc();
                }*/
            // this.posterURL = details.getImages()== null ? "" : details.getImages().getPoster().getSources().toString();
            //  this.price = details.getPrice() == null ? "" : details.getPrice();
            this.assetGenres = details.getGenres() == null ? new ArrayList<>() : details.getGenres();
            this.season = "";
            this.id = details.getId();
            this.sku = details.getSku() == null ? "" : details.getSku();
            this.isNew = false;
            //  this.tvod = details.getTvod() == null ? "" : details.getTvod();
            this.episodeNo = details.getEpisodeNumber() == null ? "" : details.getEpisodeNumber();
            this.assetType = details.getContentType() == null ? "" : details.getContentType();
            this.brightcoveVideoId = details.getBrightcoveContentId() == null ? "" : details.getBrightcoveContentId();
            this.series = "";
            this.status = details.getStatus() == null ? "" : details.getStatus();
            this.contentOrder = contentOrder;
            if (imageType != null) {
                this.imageType = imageType;
            } else {
                this.imageType = "";
            }

            Object customeFiled = details.getCustomFields();
            LinkedTreeMap<Object, Object> t = (LinkedTreeMap) customeFiled;


            if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")) {


                if (t.containsKey(CustomeFields.parentalRating)) {
                    String parentalRating = t.get((CustomeFields.parentalRating)).toString();
                    this.parentalRating = parentalRating;
                } else {
                    if (t.containsKey(CustomeFields.rating)) {
                        String parentalRating = t.get((CustomeFields.rating)).toString();
                        this.parentalRating = parentalRating;
                    }
                }
                if (t.containsKey(CustomeFields.is4k)) {
                    String is4k = t.get((CustomeFields.is4k)).toString();
                    this.is4k = is4k;
                }

                if (t.containsKey(CustomeFields.isComingSoon)) {
                    String isComingSoon = t.get((CustomeFields.isComingSoon)).toString();
                    this.iscomingsoon = isComingSoon;
                }

                if (t.containsKey(CustomeFields.Country)) {
                    String country = t.get((CustomeFields.Country)).toString();
                    this.country = country;
                }

                if (t.containsKey(CustomeFields.company)) {
                    String company = t.get((CustomeFields.company)).toString();
                    this.company = company;
                }

                if (t.containsKey(CustomeFields.year)) {
                    String year = t.get((CustomeFields.year)).toString();
                    this.year = year;
                }

                if (t.containsKey(CustomeFields.VastTag)) {
                    String year = t.get((CustomeFields.VastTag)).toString();
                    this.VastTag = year;
                }

                if (t.containsKey(CustomeFields.ISSIGNEDLANGUAGE)) {
                    String isNew = t.get((CustomeFields.ISSIGNEDLANGUAGE)).toString();
                    this.signedLangEnabled = isNew;
                }

                if (t.containsKey(CustomeFields.SIGNEDLANGUAGEPARENTREGRENCEID)) {
                    String isNew = t.get((CustomeFields.SIGNEDLANGUAGEPARENTREGRENCEID)).toString();
                    this.signedLangParentRefId = isNew;
                }

                if (t.containsKey(CustomeFields.SIGNEDLANGUAGEREFRENCEID)) {
                    String isNew = t.get((CustomeFields.SIGNEDLANGUAGEREFRENCEID)).toString();
                    this.signedLangRefId = isNew;
                }

                if (t.containsKey(CustomeFields.IS_PODCAST)) {
                    String isNew = t.get((CustomeFields.IS_PODCAST)).toString();
                    this.isPodcast = isNew;
                }


            } else {

                if (t.containsKey(CustomeFields.rating)) {
                    String parentalRating = t.get((CustomeFields.rating)).toString();
                    this.parentalRating = parentalRating;
                }
                if (t.containsKey(CustomeFields.is4k)) {
                    String is4k = t.get((CustomeFields.is4k)).toString();
                    this.is4k = is4k;
                }

                if (t.containsKey(CustomeFields.isComingSoon)) {
                    String isComingSoon = t.get((CustomeFields.isComingSoon)).toString();
                    this.iscomingsoon = isComingSoon;
                }

                if (t.containsKey(CustomeFields.Country)) {
                    String country = t.get((CustomeFields.Country)).toString();
                    this.country = country;
                }

                if (t.containsKey(CustomeFields.company)) {
                    String company = t.get((CustomeFields.company)).toString();
                    this.company = company;
                }

                if (t.containsKey(CustomeFields.year)) {
                    String year = t.get((CustomeFields.year)).toString();
                    this.year = year;
                }

                if (t.containsKey(CustomeFields.VastTag)) {
                    String year = t.get((CustomeFields.VastTag)).toString();
                    this.VastTag = year;
                }
                if (t.containsKey(CustomeFields.ISSIGNEDLANGUAGE)) {
                    String isNew = t.get((CustomeFields.ISSIGNEDLANGUAGE)).toString();
                    this.signedLangEnabled = isNew;
                }

                if (t.containsKey(CustomeFields.SIGNEDLANGUAGEPARENTREGRENCEID)) {
                    String isNew = t.get((CustomeFields.SIGNEDLANGUAGEPARENTREGRENCEID)).toString();
                    this.signedLangParentRefId = isNew;
                }

                if (t.containsKey(CustomeFields.SIGNEDLANGUAGEREFRENCEID)) {
                    String isNew = t.get((CustomeFields.SIGNEDLANGUAGEREFRENCEID)).toString();
                    this.signedLangRefId = isNew;
                }

                if (t.containsKey(CustomeFields.IS_PODCAST)) {
                    String isNew = t.get((CustomeFields.IS_PODCAST)).toString();
                    this.isPodcast = isNew;
                }


            }

            if (t.containsKey(CustomeFields.IsVip)) {
                String vip = t.get((CustomeFields.IsVip)).toString();
                this.isVIP = vip;
            }

            if (t.containsKey(CustomeFields.IsNew)) {
                String isNew = t.get((CustomeFields.IsNew)).toString();
                this.isNewS = isNew;
            }
            this.longDescription = details.getLongDescription() == null ? "" : details.getLongDescription().toString().trim();

            //series realated data
            if (details.getSeasons() != null) {
                ArrayList arrayList = (ArrayList) details.getSeasons();
                this.seasonCount = arrayList.size();
            }
            this.duration = (long) details.getDuration();
        }catch (Exception ignored){

        }
    }


    public String getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(String seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public int getContentOrder() {
        return contentOrder;
    }

    public void setContentOrder(int contentOrder) {
        this.contentOrder = contentOrder;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public ArrayList getSeasons(){
        return seasons;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVodCount() {
        return vodCount;
    }

    public void setVodCount(int vodCount) {
        this.vodCount = vodCount;
    }

    public int getSeasonCount() {
        return seasonCount;
    }

    public void setSeasonCount(int seasonCount) {
        this.seasonCount = seasonCount;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

   /* public Object getVastTag() {
        return vastTag;
    }

    public void setVastTag(Object vastTag) {
        this.vastTag = vastTag;
    }*/

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public List<String> getAssetKeywords() {
        return assetKeywords;
    }

    public void setAssetKeywords(List<String> assetKeywords) {
        this.assetKeywords = assetKeywords;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getSvod() {
        return svod;
    }

    public void setSvod(Object svod) {
        this.svod = svod;
    }

    public String getContentProvider() {
        return contentProvider;
    }

    public void setContentProvider(String contentProvider) {
        this.contentProvider = contentProvider;
    }

    public List<String> getAssetCast() {
        return assetCast;
    }

    public void setAssetCast(List<String> assetCast) {
        this.assetCast = assetCast;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public Object getPrice() {
        return price;
    }

    public void setPrice(Object price) {
        this.price = price;
    }

    public List<String> getAssetGenres() {
        return assetGenres;
    }

    public void setAssetGenres(List<String> assetGenres) {
        this.assetGenres = assetGenres;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public boolean isNew() {
        return isNew;
    }

    //	public void setThumbnailURL(String thumbnailURL){
//		this.thumbnailURL = thumbnailURL;
//	}
//
//	public String getThumbnailURL(){
//		return thumbnailURL;
//	}
//
    public void setNew(boolean aNew) {
        this.isNew = aNew;
    }

    public Object getTvod() {
        return tvod;
    }

    public void setTvod(Object tvod) {
        this.tvod = tvod;
    }

    public Object getEpisodeNo() {
        return episodeNo;
    }

    public void setEpisodeNo(Object episodeNo) {
        this.episodeNo = episodeNo;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getUploadedAssetKey() {
        return uploadedAssetKey;
    }

    public void setUploadedAssetKey(String uploadedAssetKey) {
        this.uploadedAssetKey = uploadedAssetKey;
    }

    public String getBrightcoveVideoId() {
        return brightcoveVideoId;
    }

    public void setBrightcoveVideoId(String brightcoveVideoId) {
        this.brightcoveVideoId = brightcoveVideoId;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public Object getPlans() {
        return plans;
    }

    public void setPlans(Object plans) {
        this.plans = plans;
    }

    public long getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(long publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getVideoPosition() {
        return videoPosition;
    }

    public void setVideoPosition(long videoPosition) {
        this.videoPosition = videoPosition;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImageType() {
        return imageType;
    }

    public String getParentalRating(){
        return parentalRating;
    }

    public void setParentalRating(String parentalRating) {
        this.parentalRating = parentalRating;
    }

    public String getComingSoon(){
        return iscomingsoon;
    }

    public void setComingSoon(String iscomingsoon) {
        this.iscomingsoon = iscomingsoon;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getIsNewS() {
        return isNewS;
    }

    public void setIsNewS(String isNewS) {
        this.isNewS = isNewS;
    }

    public String getIsVIP() {
        return isVIP;
    }

    public void setIsVIP(String isVIP) {
        this.isVIP = isVIP;
    }

    public void setVastTag(String VastTag) {
        this.VastTag = VastTag;
    }

    public String getVastTag() {
        return VastTag;
    }

    public String getWidevineLicence() {
        return widevineLicence;
    }

    public void setWidevineLicence(String widevineLicence) {
        this.widevineLicence = widevineLicence;
    }

    public String getGetWidevineURL() {
        return getWidevineURL;
    }

    public void setGetWidevineURL(String getWidevineURL) {
        this.getWidevineURL = getWidevineURL;
    }

    @Override
    public String toString() {
        return
                "VideosItem{" +
                        "vastTag = '" + VastTag + '\'' +
                        ",description = '" + description + '\'' +
                        ",assetKeywords = '" + assetKeywords + '\'' +
                        ",likeCount = '" + likeCount + '\'' +
                        ",title = '" + title + '\'' +
                        ",svod = '" + svod + '\'' +
                        ",contentProvider = '" + contentProvider + '\'' +
                        ",assetCast = '" + assetCast + '\'' +
                        ",premium = '" + premium + '\'' +
                        ",posterURL = '" + posterURL + '\'' +
                        ",price = '" + price + '\'' +
                        ",assetGenres = '" + assetGenres + '\'' +
                        ",season = '" + season + '\'' +
                        ",id = '" + id + '\'' +
                        ",sku = '" + sku + '\'' +
                        ",new = '" + isNew + '\'' +
                        ",tvod = '" + tvod + '\'' +
                        ",episodeNo = '" + episodeNo + '\'' +
                        ",assetType = '" + assetType + '\'' +
                        ",commentCount = '" + commentCount + '\'' +
                        ",uploadedAssetKey = '" + uploadedAssetKey + '\'' +
                        ",brightcoveVideoId = '" + brightcoveVideoId + '\'' +
                        ",series = '" + series + '\'' +
                        ",plans = '" + plans + '\'' +
                        ",publishedDate = '" + publishedDate + '\'' +
                        ",status = '" + status + '\'' +
                        ",trailerReferenceId = '" + trailerReferenceId + '\'' +
                        "}";
    }



    public Drawable getVipImageDrawable() {
        MvHubPlusApplication application = MvHubPlusApplication.getInstance();
        try {
            if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")) {
                return ContextCompat.getDrawable(application, R.drawable.vip_icon_120);
            } else if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Thai")) {
                return ContextCompat.getDrawable(application, R.drawable.vip_icon_120);
            } else {
                return ContextCompat.getDrawable(application, R.drawable.vip_icon_120);
            }
        } catch (Exception e) {
            return ContextCompat.getDrawable(application, R.drawable.vip_icon_120);
        }
    }

    public Drawable getNewSeriesImageDrawable() {
        MvHubPlusApplication application = MvHubPlusApplication.getInstance();
        try {
            if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")) {
                return ContextCompat.getDrawable(application, R.drawable.series_icon_120);
            } else if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Thai")) {
                return ContextCompat.getDrawable(application, R.drawable.series_thai_icon);
            } else {
                return ContextCompat.getDrawable(application, R.drawable.series_icon_120);
            }
        } catch (Exception e) {
            return ContextCompat.getDrawable(application, R.drawable.series_icon_120);
        }
    }

    public Drawable getEpisodeImageDrawable() {
        MvHubPlusApplication application = MvHubPlusApplication.getInstance();
        try {
            if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")) {
                return ContextCompat.getDrawable(application, R.drawable.episode_icon_120);
            } else if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Thai")) {
                return ContextCompat.getDrawable(application, R.drawable.episode_thai_icon);
            } else {
                return ContextCompat.getDrawable(application, R.drawable.episode_icon_120);
            }
        } catch (Exception e) {
            return ContextCompat.getDrawable(application, R.drawable.episode_icon_120);
        }
    }

    public Drawable getNewMoviesDrawable() {
        MvHubPlusApplication application = MvHubPlusApplication.getInstance();
        try {
            if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("English")) {
                return ContextCompat.getDrawable(application, R.drawable.new_movie_120);
            } else if (KsPreferenceKeys.getInstance().getAppLanguage().equalsIgnoreCase("Thai")) {
                return ContextCompat.getDrawable(application, R.drawable.new_movie_thai120);
            } else {
                return ContextCompat.getDrawable(application, R.drawable.new_movie_120);
            }
        } catch (Exception e) {
            return ContextCompat.getDrawable(application, R.drawable.new_movie_120);
        }
    }
}