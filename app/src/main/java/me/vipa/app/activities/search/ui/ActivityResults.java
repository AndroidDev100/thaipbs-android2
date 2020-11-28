package me.vipa.app.activities.search.ui;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import me.vipa.app.R;
import me.vipa.app.activities.search.adapter.CommonSearchAdapter;
import me.vipa.app.activities.search.viewmodel.SearchViewModel;
import me.vipa.app.activities.series.ui.SeriesDetailActivity;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.beanModel.popularSearch.ItemsItem;
import me.vipa.app.activities.detail.ui.DetailActivity;
import me.vipa.app.activities.detail.ui.EpisodeActivity;
import me.vipa.app.activities.search.adapter.RowSearchAdapter;
import me.vipa.app.adapters.CommonShimmerAdapter;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import me.vipa.app.beanModel.popularSearch.ResponsePopularSearch;
import me.vipa.app.databinding.ActivityResultBinding;
import me.vipa.app.utils.MediaTypeConstants;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.Logger;
import me.vipa.app.utils.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.RailInjectionHelper;
import me.vipa.app.utils.helpers.RecyclerAnimator;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.vipa.app.activities.search.adapter.CommonSearchAdapter;
import me.vipa.app.activities.search.adapter.RowSearchAdapter;
import me.vipa.app.activities.search.viewmodel.SearchViewModel;
import me.vipa.app.activities.series.ui.SeriesDetailActivity;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;


public class ActivityResults extends BaseBindingActivity<ActivityResultBinding> implements CommonSearchAdapter.CommonSearchListener, RowSearchAdapter.RowSearchListener {


    private SearchViewModel viewModel;
    private String searchKeyword;
    private String searchType;
    private LinearLayoutManager mLayoutManager;
    private boolean loading = true;
    private List<EnveuVideoItemBean> singleSectionItems;
    private RowSearchAdapter itemListDataAdapter1;
    private int counter = 0;
    private boolean isLastPage = false;
    private int totalCount;
    private long mLastClickTime = 0;
    private int firstVisiblePosition, pastVisiblesItems, visibleItemCount, totalItemCount;
    private RailInjectionHelper railInjectionHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundleValue();
        modelCall();
    }

    private void modelCall() {
        viewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        connectionObserver();
    }

    private void getBundleValue() {
        mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        if (getIntent().hasExtra("SearchResult")) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                try {
                    extras = extras.getBundle("SearchResult");
                    searchType = Objects.requireNonNull(extras).getString("Search_Show_All");
                    searchKeyword = extras.getString("Search_Key");
                    totalCount = extras.getInt("Total_Result");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        } else {
            throw new IllegalArgumentException("Activity cannot find  extras " + "Search_Show_All");
        }
    }

    private void connectionObserver() {
        if (NetworkConnectivity.isOnline(this)) {
            connectionValidation(true);
        } else {
            connectionValidation(false);
        }
    }

    private void connectionValidation(Boolean aBoolean) {
        if (aBoolean) {
            UIinitialization();
        } else {

            noConnectionLayout();
        }
    }

    private void UIinitialization() {
        railInjectionHelper = ViewModelProviders.of(this).get(RailInjectionHelper.class);
        getBinding().noConnectionLayout.setVisibility(View.GONE);
        getBinding().root.setVisibility(View.VISIBLE);

        getBinding().progressBar.setVisibility(View.VISIBLE);
        counter = 0;
        loading = true;
        singleSectionItems = new ArrayList<>();
        itemListDataAdapter1 = new RowSearchAdapter(ActivityResults.this, singleSectionItems, false, this);
        setRecyclerProperties(getBinding().resultRecycler);
        callShimmer(getBinding().resultRecycler);
        hitApiSearchKeyword(searchKeyword, searchType);
        /*if (searchType.equalsIgnoreCase("SERIES")) {
            hitApiPopularSearch(searchKeyword);
        } else {

            hitApiSearchKeyword(searchKeyword, searchType);
        }*/

        recyclerViewScroll();
/*
        getBinding().resultRecycler.addOnScrollListener(new EndlessScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                if (loading) {
                    if (!isLastPage) {
                        loading = false;
                        counter = counter + 2;
                        getBinding().progressBar.setVisibility(View.VISIBLE);
                        hitApiSearchKeyword(searchKeyword, searchType);
                    }
                }
            }

            @Override
            public int getTotalPageCount() {
                return 0;
            }

            @Override
            public boolean isLastPage() {
                return false;
            }

            @Override
            public boolean isLoading() {
                return false;
            }
        });
*/


        //  getBinding().toolbar.tvSearchResultHeader.setText(getResources().getString(R.string.all) + " " + searchType.concat("S"));
        localizeHeader(searchType);

        getBinding().toolbar.homeIconBack.setOnClickListener(view -> onBackPressed());


    }

    private void localizeHeader(String searchType) {
        getBinding().toolbar.tvSearchResultHeader.setAllCaps(true);
        if (searchType.equalsIgnoreCase(MediaTypeConstants.getInstance().getSeries())){
            getBinding().toolbar.tvSearchResultHeader.setText(getString(R.string.series));
        }else if (searchType.equalsIgnoreCase(MediaTypeConstants.getInstance().getEpisode())){
            getBinding().toolbar.tvSearchResultHeader.setText(getString(R.string.tab_heading_episodes));
        }
        else if (searchType.equalsIgnoreCase(MediaTypeConstants.getInstance().getMovie())){
            getBinding().toolbar.tvSearchResultHeader.setText(getString(R.string.heading_movies));
        }
        else if (searchType.equalsIgnoreCase(MediaTypeConstants.getInstance().getShow())){
            getBinding().toolbar.tvSearchResultHeader.setText(getString(R.string.heading_shows));
        }
        else if (searchType.equalsIgnoreCase(MediaTypeConstants.getInstance().getLive())){
            getBinding().toolbar.tvSearchResultHeader.setText(getString(R.string.heading_live));
        }else {
            getBinding().toolbar.tvSearchResultHeader.setText("");
        }

    }

    private void recyclerViewScroll() {
        getBinding().resultRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                try {
                    LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                    firstVisiblePosition = Objects.requireNonNull(layoutManager).findFirstVisibleItemPosition();
                    if (dy > 0) {
                        visibleItemCount = layoutManager.getChildCount();
                        totalItemCount = layoutManager.getItemCount();
                        pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                        if (loading) {
                            if (!isLastPage) {
                                loading = false;
                                counter = counter + AppConstants.PAGE_SIZE;
                                getBinding().progressBar.setVisibility(View.VISIBLE);
                                hitApiSearchKeyword(searchKeyword, searchType);
                               /* if (searchType.equalsIgnoreCase("SERIES")) {
                                    hitApiPopularSearch(searchKeyword);
                                } else {
                                    hitApiSearchKeyword(searchKeyword, searchType);
                                }*/

                            }
                        }
                    }
                } catch (Exception e) {
                    Logger.e("ListActivity", "" + e.toString());

                }
            }
        });

    }

    private void setRecyclerProperties(RecyclerView recyclerView) {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void hitApiSearchKeyword(String type, String searchKeyword) {

        railInjectionHelper.getSearchSingleCategory(type, searchKeyword, AppConstants.PAGE_SIZE, counter).observe(ActivityResults.this, data -> {
            if (data != null) {
                if (counter == 0) {
                    new RecyclerAnimator(this).animate(getBinding().resultRecycler);
                    getBinding().resultRecycler.setAdapter(itemListDataAdapter1);
                }

                Logger.e("Search", "" + data.getEnveuVideoItemBeans());
                singleSectionItems = data.getEnveuVideoItemBeans();
                itemListDataAdapter1.notifyAdapter(singleSectionItems);
                loading = true;
                if (itemListDataAdapter1.getItemCount() == totalCount)
                    isLastPage = true;
            }
            getBinding().progressBar.setVisibility(View.GONE);
        });

    }

    private void callShimmer(RecyclerView recyclerView) {
        CommonShimmerAdapter adapterPurchase;
        adapterPurchase = new CommonShimmerAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterPurchase);
    }


    private void setUiComponents(ResponsePopularSearch jsonObject) {
        getBinding().resultRecycler.setLayoutManager(mLayoutManager);
        // getBinding().resultRecycler.setAdapter(new CommonSearchAdapter(ActivityResults.this, jsonObject, this));
    }

    private void noConnectionLayout() {
        getBinding().root.setVisibility(View.GONE);
        getBinding().noConnectionLayout.setVisibility(View.VISIBLE);
        getBinding().connection.retryTxt.setOnClickListener(view -> connectionObserver());
    }

    @Override
    public ActivityResultBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivityResultBinding.inflate(inflater);
    }

    @Override
    public void onItemClicked(ItemsItem itemValue) {

        if (itemValue.getType().equalsIgnoreCase(MediaTypeConstants.getInstance().getSeries())) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            new ActivityLauncher(ActivityResults.this).seriesDetailScreen(ActivityResults.this, SeriesDetailActivity.class, itemValue.getId());

        } else if (itemValue.getType().equalsIgnoreCase(MediaTypeConstants.getInstance().getEpisode())) {
            Logger.e("ActivityResults", "Season click");

            if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            new ActivityLauncher(ActivityResults.this).episodeScreen(ActivityResults.this, EpisodeActivity.class, itemValue.getId(), "0", false);

        }else if (itemValue.getType().equalsIgnoreCase(MediaTypeConstants.getInstance().getLive())) {
            Logger.e("ActivityResults", "Season click");

            if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            AppCommonMethod.launchDetailScreen(ActivityResults.this, Long.valueOf(itemValue.getId()), itemValue.getType(),itemValue.getId(), "0", false);

        } else {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            new ActivityLauncher(ActivityResults.this).detailScreen(ActivityResults.this, DetailActivity.class, itemValue.getId(), "0", false);


        }
    }

    @Override
    public void onRowItemClicked(EnveuVideoItemBean itemValue) {
        try {
            AppCommonMethod.trackFcmEvent(itemValue.getTitle(), itemValue.getAssetType(), ActivityResults.this, 0);
        } catch (Exception e) {

        }
        if (AppCommonMethod.getCheckBCID(itemValue.getBrightcoveVideoId())) {
            Long getVideoId = Long.parseLong(itemValue.getBrightcoveVideoId());
            AppCommonMethod.launchDetailScreen(this, getVideoId, itemValue.getAssetType(), itemValue.getId(), "0", false);
        } else {
            AppCommonMethod.launchDetailScreen(this, 0l, itemValue.getAssetType(), itemValue.getId(), "0", false);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
