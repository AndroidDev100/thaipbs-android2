package me.vipa.app.activities.contentPreference.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.vipa.app.R;
import me.vipa.app.databinding.UserPreferenceItemBinding;
import me.vipa.app.utils.config.bean.ContentPreference;

public class ContentPreferenceAdapter extends RecyclerView.Adapter<ContentPreferenceAdapter.SingleItemRowHolder> {



    private final Activity activity;
    //    String type;
    private final List<ContentPreference> arrayList;
    private int count = 0;

    public ContentPreferenceAdapter(Activity ctx, List<ContentPreference> list) {
        activity = ctx;
       this.arrayList = list;
    }

//    public ContentPreferenceAdapter(Activity ctx, ArrayList<ContentPreference> list/*, String type*/) {
//        activity = ctx;
//        this.arrayList = list;
//
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

        viewHolder.genreItemBinding.titleText.setText(arrayList.get(position).getName());

//        if (arrayList.get(position).getChecked()) {
//            viewHolder.genreItemBinding.titleText.setBackgroundResource(R.drawable.genre_selected);
//            viewHolder.genreItemBinding.titleText.setTextColor(activity.getResources().getColor(R.color.white));
//        } else {
//            viewHolder.genreItemBinding.titleText.setBackgroundResource(R.drawable.genre_unselected);
//            viewHolder.genreItemBinding.titleText.setTextColor(activity.getResources().getColor(R.color.black));
//        }
//        viewHolder.genreItemBinding.titleText.setText(arrayList.get(position).getName());
//        viewHolder.genreItemBinding.titleText.setOnClickListener(view -> {
//            if (count > 4) {
//                if (arrayList.get(position).getChecked()) {
//                    if (arrayList.get(position).getChecked()) {
//                        count--;
//                        arrayList.get(position).setChecked(false);
//                        notifyDataSetChanged();
//                    } else {
//                        count++;
//                        arrayList.get(position).setChecked(true);
//                        notifyDataSetChanged();
//                    }
//                }
//
//            } else {
//                if (arrayList.get(position).getChecked()) {
//                    count--;
//                    arrayList.get(position).setChecked(false);
//                    notifyDataSetChanged();
//                } else {
//                    count++;
//                    arrayList.get(position).setChecked(true);
//                    notifyDataSetChanged();
//                }
//            }
//
//        });
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