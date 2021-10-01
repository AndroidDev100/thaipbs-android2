package me.vipa.app.activities.videoquality.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import me.vipa.app.activities.videoquality.callBack.ItemClick;
import me.vipa.app.R;
import me.vipa.app.activities.videoquality.bean.LanguageItem;

import me.vipa.app.databinding.VideoQualityItemBinding;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import java.util.List;

import me.vipa.app.activities.videoquality.callBack.ItemClick;


public class ChangeLanguageAdapter extends RecyclerView.Adapter<ChangeLanguageAdapter.SingleItemRowHolder> {

    private final List<LanguageItem> inboxMessages;
    private final ItemClick itemClickListener;
   // private int pos = new KsPreferenceKeys(ApplicationMain.getAppContext()).getQualityPosition();

    int pos=0;
    private KsPreferenceKeys preference;
    public ChangeLanguageAdapter(Activity activity, List<LanguageItem> itemsList, ItemClick listener) {
        preference = KsPreferenceKeys.getInstance();
        pos=preference.getAppPrefLanguagePos();
        this.inboxMessages = itemsList;
        this.itemClickListener = listener;

    }

    @NonNull
    @Override
    public SingleItemRowHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        VideoQualityItemBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.getContext()),
                R.layout.video_quality_item, viewGroup, false);

        return new SingleItemRowHolder(itemBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull final SingleItemRowHolder viewHolder, @SuppressLint("RecyclerView") final int position) {

        if (pos == position) {
            viewHolder.notificationItemBinding.rightIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.notificationItemBinding.rightIcon.setVisibility(View.INVISIBLE);
        }

        viewHolder.notificationItemBinding.titleText.setText(inboxMessages.get(position).getLanguageName());
        viewHolder.notificationItemBinding.secondTitleText.setText(inboxMessages.get(position).getDefaultLangageName());

        viewHolder.notificationItemBinding.parentLayout.setOnClickListener(view -> {
            pos = position;
          //  new KsPreferenceKeys(ApplicationMain.getAppContext()).setQualityPosition(pos);
          //  new KsPreferenceKeys(ApplicationMain.getAppContext()).setQualityName(inboxMessages.get(position).getLanguageName());
            itemClickListener.itemClicked(inboxMessages.get(position).getLanguageName(),pos);

        });
    }


    @Override
    public int getItemCount() {
        return inboxMessages.size();
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        final VideoQualityItemBinding notificationItemBinding;

        SingleItemRowHolder(VideoQualityItemBinding binding) {
            super(binding.getRoot());
            this.notificationItemBinding = binding;

        }

    }


}
