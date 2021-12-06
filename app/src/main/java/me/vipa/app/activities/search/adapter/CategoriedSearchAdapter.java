package me.vipa.app.activities.search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.vipa.app.R;
import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import me.vipa.app.callbacks.commonCallbacks.SearchClickCallbacks;
import me.vipa.app.databinding.RowSearchCategoryBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.cropImage.helpers.PrintLogging;
import me.vipa.brightcovelibrary.Logger;

public class CategoriedSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RowSearchAdapter.RowSearchListener {
    private final Context context;
    private final List<RailCommonData> list;
    private final SearchClickCallbacks listener;

    public CategoriedSearchAdapter(Context context, List<RailCommonData> demoList, SearchClickCallbacks listener) {
        this.context = context;
        this.list = demoList;
        this.listener = listener;
    }


    public void clearList(boolean clearList) {
        if (clearList) {
            this.list.clear();
            notifyDataSetChanged();
        }


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowSearchCategoryBinding binding;
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.row_search_category, parent, false);
        Logger.d("ViewType :" + viewType);
        if (viewType == 1) {
            return new EpisodeTypeViewHolder(binding);
        } else {
            return new ProgramTypeViewHolder(binding);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int pos) {
        final int position = pos;
        final List<EnveuVideoItemBean> singleSectionItems = list.get(position).getEnveuVideoItemBeans();
        RowSearchAdapter itemListDataAdapter1 = new RowSearchAdapter(context, singleSectionItems, true, this);

        if (viewHolder instanceof EpisodeTypeViewHolder) {
            try {
                setRecyclerProperties(((EpisodeTypeViewHolder) viewHolder).binding.recyclerView);
                ((EpisodeTypeViewHolder) viewHolder).binding.recyclerView.setAdapter(itemListDataAdapter1);
                ((EpisodeTypeViewHolder) viewHolder).initData(list.get(position).getTotalCount());
                ((EpisodeTypeViewHolder) viewHolder).binding.showAllSearch.setOnClickListener(view -> listener.onShowAllItemClicked(list.get(position)));
            } catch (ClassCastException e) {
                Logger.w(e);
            }
        } else if (viewHolder instanceof ProgramTypeViewHolder) {
            try {
                setRecyclerProperties(((ProgramTypeViewHolder) viewHolder).binding.recyclerView);
                ((ProgramTypeViewHolder) viewHolder).binding.recyclerView.setAdapter(itemListDataAdapter1);
                ((ProgramTypeViewHolder) viewHolder).initData(list.get(position).getTotalCount());
                ((ProgramTypeViewHolder) viewHolder).binding.showAllSearch.setOnClickListener(view -> listener.onShowAllProgramClicked(list.get(position)));
            } catch (ClassCastException e) {
                Logger.w(e);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getLayoutType();
    }


    private void setRecyclerProperties(RecyclerView recyclerView) {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class EpisodeTypeViewHolder extends RecyclerView.ViewHolder {
        final RowSearchCategoryBinding binding;

        EpisodeTypeViewHolder(RowSearchCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void initData(int totalCount) {
            if (totalCount == 1)
                binding.tvTitle.setText(context.getString(R.string.heading_episodes) + " - " + totalCount + " " + context.getResources().getString(R.string.result_caps));
            else
                binding.tvTitle.setText(context.getString(R.string.heading_episodes) + " - " + totalCount + " " + context.getResources().getString(R.string.results_caps));

            if (totalCount < 5) {
                binding.showAllSearch.setVisibility(View.GONE);
            }
        }
    }

    class ProgramTypeViewHolder extends RecyclerView.ViewHolder {
        final RowSearchCategoryBinding binding;

        ProgramTypeViewHolder(RowSearchCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        void initData(int totalCount) {
            if (totalCount == 1)
                binding.tvTitle.setText(context.getString(R.string.heading_program) + " - " + totalCount + " " + context.getResources().getString(R.string.result_caps));
            else
                binding.tvTitle.setText(context.getString(R.string.heading_program) + " - " + totalCount + " " + context.getResources().getString(R.string.results_caps));

            if (totalCount < 5) {
                binding.showAllSearch.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onRowItemClicked(EnveuVideoItemBean itemValue) {
        try {
            AppCommonMethod.trackFcmEvent(itemValue.getTitle(), itemValue.getAssetType(), context, 0);
        } catch (Exception e) {
            Logger.w(e);
        }
        if (AppCommonMethod.getCheckBCID(itemValue.getBrightcoveVideoId())) {
            Long getVideoId = Long.parseLong(itemValue.getBrightcoveVideoId());
            PrintLogging.printLog("", "SearchAssetType-->>" + itemValue.getAssetType());
            AppCommonMethod.launchDetailScreen(context, getVideoId, itemValue.getAssetType(), itemValue.getId(), "0", false);
        } else {
            AppCommonMethod.launchDetailScreen(context, 0l, itemValue.getAssetType(), itemValue.getId(), "0", false);

        }
    }
}
