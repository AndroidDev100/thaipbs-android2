package me.vipa.app.activities.search.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

import me.vipa.app.R;
import me.vipa.app.beanModel.filterModel.SortModel;
import me.vipa.app.databinding.SortItemBinding;
import me.vipa.app.utils.constants.AppConstants;

public class SortAdapter extends RecyclerView.Adapter<SortAdapter.SingleItemRowHolder> {
    private List<SortModel> sortModels;
    private Activity context;
    private int selectedPostion = -1;
    private final SortByListener sortByListener;


    public SortAdapter(Activity ctx, List<SortModel> list, SortByListener sortByListener) {
        this.sortModels = list;
        this.context = ctx;
        this.sortByListener = sortByListener;

    }

/*public  void update(Activity ctx, List<SortModel> list){
        this.sortModels = list;
        this.context = ctx;

        notifyDataSetChanged();


        }*/

    @Override
    public SortAdapter.SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int i) {
        SortItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.sort_item, parent, false);
        return new SortAdapter.SingleItemRowHolder(binding);

    }

    @Override
    public void onBindViewHolder(SortAdapter.SingleItemRowHolder holder, int position) {
        holder.sortItemBinding.sortItem.setText(sortModels.get(position).getSortLists());
        if (sortModels.get(position).isSortChecked()) {
            holder.sortItemBinding.sortItem.setBackgroundResource(R.drawable.rounded_corner_filter_yellow);
            holder.sortItemBinding.sortItem.setTextColor(context.getResources().getColor(R.color.black));
            Typeface typeface = ResourcesCompat.getFont(context, R.font.sukhumvittadmai_medium);
            holder.sortItemBinding.sortItem.setTypeface(typeface);


        } else {
            holder.sortItemBinding.sortItem.setBackgroundResource(R.drawable.rounded_corner_filter_gray);
            holder.sortItemBinding.sortItem.setTextColor(context.getResources().getColor(R.color.black_filter_text));
            Typeface typeface = ResourcesCompat.getFont(context, R.font.sukhumvittadmai_normal);
            holder.sortItemBinding.sortItem.setTypeface(typeface);

        }


        holder.sortItemBinding.sortItem.setOnClickListener(v -> {
            for (int i = 0; i < sortModels.size(); i++) {
                if (sortModels.get(i).isSortChecked()) {
                    sortModels.get(i).setSortChecked(false);
                }
            }
            if (selectedPostion >= 0) {
                sortModels.get(selectedPostion).setSortChecked(false);
            }
            selectedPostion = position;
            sortModels.get(position).setSortChecked(true);
            notifyDataSetChanged();
            getSelectedValues(sortModels);


        });


    }

    String selectedSort = "";
    String selectedSort2 = "";

    private void getSelectedValues(List<SortModel> arrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilder1 = new StringBuilder();
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).isSortChecked()) {
                if (arrayList.get(i).getSortLists().equalsIgnoreCase("A to Z")) {
                    stringBuilder.append(AppConstants.SEARCH_SORT_CONSTATNT + "ASC");
                } else {
                    stringBuilder.append(AppConstants.SEARCH_SORT_CONSTATNT + "DESC");
                }
                stringBuilder1.append(arrayList.get(i).getSortLists()).append(", ");
            }
            if (stringBuilder.length() > 0) {
                selectedSort = stringBuilder.toString();
                // selectedSort = selectedSort.substring(0, selectedSort.length() - 2);
            } else {
                selectedSort = "";
            }

            if (stringBuilder1.length() > 0) {
                selectedSort2 = stringBuilder1.toString();
                selectedSort2 = selectedSort2.substring(0, selectedSort2.length() - 2);
            } else {
                selectedSort2 = "";
            }
        }
        sortByListener.setSortBy(selectedSort, selectedSort2);

        //  Log.e("selectedSort", selectedSort);
        //  Log.e("selectedSort2", selectedSort2);


    }


    @Override
    public int getItemCount() {
        return sortModels.size();
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        public final SortItemBinding sortItemBinding;

        public SingleItemRowHolder(SortItemBinding flightItemLayoutBinding) {
            super(flightItemLayoutBinding.getRoot());
            sortItemBinding = flightItemLayoutBinding;
        }
    }

    public interface SortByListener {
        void setSortBy(String selectedGenre, String selectedGenre2);
    }


}

