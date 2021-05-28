package me.vipa.app.activities.contentPreference.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import me.vipa.app.R;
import me.vipa.app.SDKConfig;
import me.vipa.app.callbacks.commonCallbacks.ContentPreferenceCallback;
import me.vipa.app.databinding.UserPreferenceItemBinding;
import me.vipa.app.utils.config.bean.PreferenceBean;

public class ContentPreferenceAdapter extends RecyclerView.Adapter<ContentPreferenceAdapter.SingleItemRowHolder> {



    private final Activity activity;
    //    String type;
    private final ArrayList<PreferenceBean> arrayList;
    private int count = 0;
    private ContentPreferenceCallback callback;

    public ContentPreferenceAdapter(Activity ctx, int count, ArrayList<PreferenceBean> list, ContentPreferenceCallback callback) {
        activity = ctx;
       this.arrayList = list;
       this.callback = callback;
       this.count = count;
        //getGenreList();
    }

//    public ArrayList<PreferenceBean> getGenreList() {
//        return getSelectedList(arrayList);
//    }
//
//    private ArrayList<PreferenceBean> getSelectedList(ArrayList<PreferenceBean> arrayList) {
//        ArrayList<PreferenceBean> selectedList = new ArrayList<>();
//        for (int i = 0; i < arrayList.size(); i++) {
//            if (arrayList.get(i).getChecked()) {
//                count++;
//                selectedList.add(arrayList.get(i));
//            }
//        }
//        return selectedList;
//    }




    @NonNull
    @Override
    public ContentPreferenceAdapter.SingleItemRowHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        UserPreferenceItemBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.getContext()),
                R.layout.user_preference_item, viewGroup, false);

        return  new SingleItemRowHolder(itemBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull final SingleItemRowHolder viewHolder, final int position) {



        if (arrayList.get(position).getChecked()) {
            viewHolder.genreItemBinding.titleText.setBackgroundResource(R.drawable.genre_selected);
            viewHolder.genreItemBinding.titleText.setTextColor(activity.getResources().getColor(R.color.white));
        } else {
            if (count>4){
                viewHolder.genreItemBinding.titleText.setBackgroundResource(R.drawable.genre_unselected);
                viewHolder.genreItemBinding.titleText.setTextColor(activity.getResources().getColor(R.color.genre_unselected_text));
                viewHolder.genreItemBinding.titleText.getBackground().setAlpha(120);
            }else {
                viewHolder.genreItemBinding.titleText.setBackgroundResource(R.drawable.genre_unselected);
                viewHolder.genreItemBinding.titleText.setTextColor(activity.getResources().getColor(R.color.genre_unselected_text));
                viewHolder.genreItemBinding.titleText.getBackground().setAlpha(255);

            }

        }

        viewHolder.genreItemBinding.titleText.setText(arrayList.get(position).getName());

        viewHolder.genreItemBinding.titleText.setOnClickListener(view -> {
            if (count > 4) {
                if (arrayList.get(position).getChecked()) {
                    if (arrayList.get(position).getChecked()) {
                        count--;
                        arrayList.get(position).setChecked(false);
                        notifyDataSetChanged();
                    } else {
                        count++;
                        arrayList.get(position).setChecked(true);
                        notifyDataSetChanged();
                    }
                }

            } else {
                if (arrayList.get(position).getChecked()) {
                    count--;
                    arrayList.get(position).setChecked(false);
                    notifyDataSetChanged();
                } else {
                    count++;
                    arrayList.get(position).setChecked(true);
                    notifyDataSetChanged();
                }
            }
            callback.onClick(arrayList);

        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        final UserPreferenceItemBinding genreItemBinding;

        private SingleItemRowHolder(UserPreferenceItemBinding binding) {
            super(binding.getRoot());
            this.genreItemBinding = binding;
        }

    }


}