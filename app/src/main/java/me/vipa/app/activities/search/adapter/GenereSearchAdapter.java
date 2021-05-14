package me.vipa.app.activities.search.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

import me.vipa.app.R;
import me.vipa.app.databinding.GenreItemBinding;
import me.vipa.app.utils.config.bean.Genre;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

public class GenereSearchAdapter extends RecyclerView.Adapter<GenereSearchAdapter.SingleItemRowHolder> {
    //private List<GenereModel> genereModels;
    private List<Genre> genereModels;
    private Context context;

    private final GenreListener genreListener;
    private String currentLanguage;

    public GenereSearchAdapter(Activity ctx, List<Genre> list, GenreListener genreListener) {
        this.genereModels = list;
        this.context = ctx;
        this.genreListener = genreListener;

    }

/*    public  void update(Activity ctx, List<GenereModel> list){
        this.genereModels = list;
        this.context = ctx;

        notifyDataSetChanged();


    }*/

    @Override
    public GenereSearchAdapter.SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int i) {
        GenreItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.genre_item, parent, false);
        return new GenereSearchAdapter.SingleItemRowHolder(binding);

    }

    @Override
    public void onBindViewHolder(GenereSearchAdapter.SingleItemRowHolder holder, int position) {
        currentLanguage = KsPreferenceKeys.getInstance().getAppLanguage();
        if (currentLanguage.equalsIgnoreCase("Thai")) {
            holder.commonItemBinding.itemsFilter.setText(genereModels.get(position).getDisplayName().getTh());
        } else if (currentLanguage.equalsIgnoreCase("English")) {
            holder.commonItemBinding.itemsFilter.setText(genereModels.get(position).getDisplayName().getEnUS());
        } else {
            holder.commonItemBinding.itemsFilter.setText(genereModels.get(position).getDisplayName().getEnUS());
        }


        if (genereModels.get(position).isGenereChecked()) {
            holder.commonItemBinding.itemsFilter.setBackgroundResource(R.drawable.rounded_corner_filter_yellow);
            holder.commonItemBinding.itemsFilter.setTextColor(context.getResources().getColor(R.color.black));
            Typeface typeface = ResourcesCompat.getFont(context, R.font.sukhumvittadmai_medium);
            holder.commonItemBinding.itemsFilter.setTypeface(typeface);

        } else {
            holder.commonItemBinding.itemsFilter.setBackgroundResource(R.drawable.rounded_corner_filter_gray);
            holder.commonItemBinding.itemsFilter.setTextColor(context.getResources().getColor(R.color.black_filter_text));
            Typeface typeface = ResourcesCompat.getFont(context, R.font.sukhumvittadmai_normal);
            holder.commonItemBinding.itemsFilter.setTypeface(typeface);

        }
        holder.commonItemBinding.itemsFilter.setOnClickListener(v -> {
            int value = KsPreferenceKeys.getInstance().getFilterGenre();
            if (value <= 4) {
                if (genereModels.get(position).isGenereChecked()) {
                    genereModels.get(position).setGenereChecked(false);
                    notifyDataSetChanged();
                    value--;
                } else {
                    genereModels.get(position).setGenereChecked(true);
                    notifyDataSetChanged();
                    value++;
                }

                KsPreferenceKeys.getInstance().setFilterGenre(value);
                //  Log.e("COUNT SAVED", String.valueOf(value));
                getSelectedValues(genereModels);

            } else {
                if (genereModels.get(position).isGenereChecked()) {
                    genereModels.get(position).setGenereChecked(false);
                    notifyDataSetChanged();
                    value--;
                    KsPreferenceKeys.getInstance().setFilterGenre(value);
                    getSelectedValues(genereModels);
                }


             /*value--;
             KsPreferenceKeys.getInstance().setFilterGenre(value);
             Log.e("COUNT Remove", String.valueOf(value));*/
            }


        });
     /*   holder.commonItemBinding.itemsFilter.setOnClickListener(v -> {
            if (genereModels.get(position).isGenereChecked()) {
                genereModels.get(position).setGenereChecked(false);
                notifyDataSetChanged();
            } else {
                genereModels.get(position).setGenereChecked(true);
                notifyDataSetChanged();
            }
            getSelectedValues(genereModels);
        });*/
    }


    String selectedGenre = "";
    String selectedGenre2 = "";

    private void getSelectedValues(List<Genre> arrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilder1 = new StringBuilder();
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).isGenereChecked()) {

                if (currentLanguage.equalsIgnoreCase("Thai")) {
                    stringBuilder.append(AppConstants.SEARCH_GENRE_CONSTATNT + arrayList.get(i).getDisplayName().getTh()).append(",  ");
                    stringBuilder1.append(arrayList.get(i).getDisplayName().getTh()).append(", ");
                } else if (currentLanguage.equalsIgnoreCase("English")) {
                    stringBuilder.append(AppConstants.SEARCH_GENRE_CONSTATNT + arrayList.get(i).getDisplayName().getEnUS()).append(",  ");
                    stringBuilder1.append(arrayList.get(i).getDisplayName().getEnUS()).append(", ");
                } else {
                    stringBuilder.append(AppConstants.SEARCH_GENRE_CONSTATNT + arrayList.get(i).getDisplayName().getEnUS()).append(",  ");
                    stringBuilder1.append(arrayList.get(i).getDisplayName().getEnUS()).append(", ");
                }

            }
            if (stringBuilder.length() > 0) {
                selectedGenre = stringBuilder.toString();
                selectedGenre = selectedGenre.substring(0, selectedGenre.length() - 2);
            } else {
                selectedGenre = "";
            }

            if (stringBuilder1.length() > 0) {
                selectedGenre2 = stringBuilder1.toString();
                selectedGenre2 = selectedGenre2.substring(0, selectedGenre2.length() - 2);
            } else {
                selectedGenre2 = "";
            }
        }
        genreListener.setGenre(selectedGenre, selectedGenre2);
        //  Log.e("selectedGenre",selectedGenre);
        //  Log.e("selectedGenre2",selectedGenre2);


    }


    @Override
    public int getItemCount() {
        return genereModels.size();
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {
        public final GenreItemBinding commonItemBinding;

        public SingleItemRowHolder(GenreItemBinding flightItemLayoutBinding) {
            super(flightItemLayoutBinding.getRoot());
            commonItemBinding = flightItemLayoutBinding;
        }
    }

    public interface GenreListener {
        void setGenre(String selectedGenre, String selectedGenre2);
    }

}

