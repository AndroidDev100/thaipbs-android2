package me.vipa.app.activities.videoquality.viewModel;

import android.app.Application;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import me.vipa.app.R;
import me.vipa.app.activities.videoquality.bean.LanguageItem;
import me.vipa.app.activities.videoquality.bean.TrackItem;
import me.vipa.app.utils.cropImage.helpers.Logger;

import java.util.ArrayList;

public class VideoQualityViewModel extends AndroidViewModel {
    public VideoQualityViewModel(@NonNull Application application) {
        super(application);
    }


    public ArrayList<LanguageItem> getLanguageList() {
        Logger.e("Locale", getApplication().getString(R.string.language_english));
        ArrayList<LanguageItem> trackItems=new ArrayList<>();
        for (int i=0;i<3;i++){
            if (i==0){
                LanguageItem languageItem=new LanguageItem();
                languageItem.setLanguageName(getApplication().getString(R.string.language_english));
                trackItems.add(languageItem);
            }
            else if (i==1){
                LanguageItem languageItem=new LanguageItem();
                languageItem.setLanguageName(getApplication().getString(R.string.language_thai));
                trackItems.add(languageItem);
            }
        }
        return trackItems;
    }

    public ArrayList<TrackItem> getQualityList(Resources resources) {
        ArrayList<TrackItem> trackItems=new ArrayList<>();
        for (int i=0;i<4;i++){
            if (i==0){
                trackItems.add(new TrackItem(resources.getString(R.string.auto),"Auto"));
            }
            else if (i==1){
                trackItems.add(new TrackItem(resources.getString(R.string.low), "Low"));
            }else if (i==2){
                trackItems.add(new TrackItem(resources.getString(R.string.medium), "Medium"));
            }else if (i==3){
                {
                    trackItems.add(new TrackItem(resources.getString(R.string.high), "High"));
                }
            }

        }
        return trackItems;
    }
}