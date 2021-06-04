package me.vipa.app.activities.search.ui;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.util.Log;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import me.vipa.app.activities.detail.ui.DetailActivity;
import me.vipa.app.activities.search.adapter.CategoriedSearchAdapter;
import me.vipa.app.activities.search.adapter.CommonSearchAdapter;
import me.vipa.app.activities.search.adapter.RecentListAdapter;
import me.vipa.app.activities.search.viewmodel.SearchViewModel;
import me.vipa.app.activities.series.ui.SeriesDetailActivity;
import me.vipa.app.adapters.CommonShimmerAdapter;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.beanModel.KeywordList;
import me.vipa.app.beanModel.enveuCommonRailData.RailCommonData;
import me.vipa.app.beanModel.popularSearch.ItemsItem;
import me.vipa.app.callbacks.commonCallbacks.SearchClickCallbacks;
import me.vipa.app.utils.constants.AppConstants;
import me.vipa.app.utils.cropImage.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.AppPreference;
import me.vipa.app.utils.helpers.SharedPrefHelper;
import me.vipa.app.utils.helpers.intentlaunchers.ActivityLauncher;
import me.vipa.app.R;
import me.vipa.app.SDKConfig;
import me.vipa.app.beanModelV3.uiConnectorModelV2.EnveuVideoItemBean;
import me.vipa.app.databinding.ActivitySearchBinding;
import me.vipa.app.utils.MediaTypeConstants;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.helpers.RailInjectionHelper;
import me.vipa.app.utils.helpers.RecyclerAnimator;
import me.vipa.app.utils.helpers.ToastHandler;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static me.vipa.app.R.font.sukhumvittadmai_normal;

//import com.webstreamindonesia.nonton.db.search.SearchedKeywords;


@SuppressWarnings("StatementWithEmptyBody")
public class ActivitySearch extends BaseBindingActivity<ActivitySearchBinding> implements SearchClickCallbacks, RecentListAdapter.KeywordItemHolderListener, SearchView.OnQueryTextListener {

    private SearchViewModel viewModel;
    //    private List<SearchedKeywords> mList;
    private CategoriedSearchAdapter searchAdapter;
    private List<RailCommonData> model;
    private long mLastClickTime = 0;
    private boolean isShimmer;
    private RailInjectionHelper railInjectionHelper;
    private final int FILTER_REQUEST_CODE = 2000;
    private String searchText = "";
    private boolean applyFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(ActivitySearch.this).get(SearchViewModel.class);
        Typeface font = ResourcesCompat.getFont(this, sukhumvittadmai_normal);
        TextView searchText = (TextView) getBinding().toolbar.searchView.findViewById(R.id.search_src_text);
        if (KsPreferenceKeys.getInstance().getCurrentTheme().equalsIgnoreCase(AppConstants.LIGHT_THEME)) {
            searchText.setTextColor(getResources().getColor(R.color.white));
        }
        searchText.setTypeface(font);
        clickListner();
        connectionObserver();
        getBinding().toolbar.searchView.setOnQueryTextListener(this);

    }

    boolean searchResult = true;

    private void clickListner() {
        getBinding().noResult.setVisibility(View.GONE);
        // getBinding().noResult1.setVisibility(View.GONE);
        hitApiPopularSearch();
        setRecyclerProperties(getBinding().recentSearchRecycler);
        setRecentSearchAdapter("");

        getBinding().toolbar.backButton.setOnClickListener(view -> onBackPressed());
        getBinding().toolbar.clearText.setOnClickListener(view -> onBackPressed());
        getBinding().toolbar.filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitySearch.this, FilterIconActivity.class);
                startActivityForResult(intent, FILTER_REQUEST_CODE);


            }
        });

//        getBinding().toolbar.searchText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
//            if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
//                //  searchKeyWord(getBinding().toolbar.searchText.getText().toString().trim());
//                if (NetworkConnectivity.isOnline(ActivitySearch.this)) {
//                    if ((getBinding().toolbar.searchText.getText().toString().trim().length() > 0)) {
//                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                            return true;
//                        }
//                        mLastClickTime = SystemClock.elapsedRealtime();
//
//                        hideSoftKeyboard(getBinding().toolbar.searchText);
//                        try {
//                            AppCommonMethod.trackSearchFcmEvent(ActivitySearch.this, getBinding().toolbar.searchText.getText().toString().trim());
//                        } catch (Exception e) {
//
//                        }
//                        if (searchResult) {
//                            searchResult = false;
//
//                            hitApiSearchKeyword(getBinding().toolbar.searchText.getText().toString().trim());
//                            //  hitApiSearchSeries(getBinding().toolbar.searchText.getText().toString().trim());
//                        }
//                        //hitApiSearchSeries(getBinding().toolbar.searchText.getText().toString().trim());
//                    } else {
//                        getBinding().toolbar.searchText.setText("");
//                    }
//                } else {
//                    new ToastHandler(ActivitySearch.this).show(ActivitySearch.this.getResources().getString(R.string.no_internet_connection));
//                }
//
//            }
//
//            return false;
//        });
    }

    private void searchKeyWord(String trim) {

    }

    private void setRecentSearchAdapter(String s) {
        Gson gson = new Gson();
        String json = AppPreference.getInstance(this).getRecentSearchList();
//        String json = KsPreferenceKeys.getInstance().getRecentSearchList();
        if (json.isEmpty()) {
            getBinding().llRecentSearchLayout.setVisibility(View.GONE);
        } else {
            getBinding().llRecentSearchLayout.setVisibility(View.VISIBLE);
            Type type = new TypeToken<List<KeywordList>>() {
            }.getType();
            List<KeywordList> arrPackageData = gson.fromJson(json, type);

            Collections.reverse(arrPackageData);
            if (arrPackageData.size() > 0) {
                RecentListAdapter recentListAdapter = new RecentListAdapter(ActivitySearch.this, arrPackageData, ActivitySearch.this);
                getBinding().recentSearchRecycler.setAdapter(recentListAdapter);
            }
        }

        getBinding().deleteKeywords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeletion();
            }
        });
    }

    private void confirmDeletion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySearch.this, R.style.AlertDialogStyle);
        builder.setMessage(this.getResources().getString(R.string.delete_history_confirmation))
                .setCancelable(true)
                .setPositiveButton(this.getResources().getString(R.string.delete), (dialog, id) -> {
                    AppPreference.getInstance(ActivitySearch.this).setRecentSearchList("");
                    getBinding().llRecentSearchLayout.setVisibility(View.GONE);
                    dialog.cancel();
                })
                .setNegativeButton(this.getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
        Button bn = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        bn.setTextColor(ContextCompat.getColor(ActivitySearch.this, R.color.white));
        Button bp = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        bp.setTextColor(ContextCompat.getColor(ActivitySearch.this, R.color.moretitlecolor));

    }


    private void hitApiSearchKeyword(String searchKeyword, Context context, boolean applyFilter) {
        model = new ArrayList<>();
        callShimmer(getBinding().searchResultRecycler);
        setRecyclerProperties(getBinding().searchResultRecycler);

        getBinding().rootView.setVisibility(View.GONE);
        getBinding().noResult.setVisibility(View.GONE);
        //  getBinding().noResult1.setVisibility(View.GONE);
        // getBinding().progressBar.setVisibility(View.VISIBLE);
        getBinding().llSearchResultLayout.setVisibility(View.VISIBLE);
        railInjectionHelper.getSearch(context, searchKeyword, 4, 0, applyFilter).observe(ActivitySearch.this, data -> {
            searchResult = true;
            if (Objects.requireNonNull(data).size() > 0) {
                try {
                    getBinding().noResult.setVisibility(View.GONE);
                    // getBinding().noResult1.setVisibility(View.GONE);
                    getBinding().rootView.setVisibility(View.GONE);
                    getBinding().toolbar.filter.setVisibility(View.VISIBLE);
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).getPageTotal() > 0) {
                            if (data.get(i).getStatus()) {
                                RailCommonData temp = new RailCommonData();
                                temp.setEnveuVideoItemBeans(data.get(i).getEnveuVideoItemBeans());
                                if (data.get(i).getEnveuVideoItemBeans().size() > 0) {
                                    temp.setAssetType(data.get(i).getEnveuVideoItemBeans().get(0).getAssetType());
                                    temp.setStatus(true);
                                    if (data.get(i).getEnveuVideoItemBeans().get(0).getAssetType().equalsIgnoreCase(MediaTypeConstants.getInstance().getShow())) {
                                        temp.setLayoutType(0);
                                    } else if (data.get(i).getEnveuVideoItemBeans().get(0).getAssetType().equalsIgnoreCase(MediaTypeConstants.getInstance().getEpisode())) {
                                        temp.setLayoutType(1);
                                    } else if (data.get(i).getEnveuVideoItemBeans().get(0).getAssetType().equalsIgnoreCase(MediaTypeConstants.getInstance().getMovie())) {
                                        temp.setLayoutType(2);
                                    } else if (data.get(i).getEnveuVideoItemBeans().get(0).getAssetType().equalsIgnoreCase(MediaTypeConstants.getInstance().getSeries())) {
                                        temp.setLayoutType(3);
                                    } else if (data.get(i).getEnveuVideoItemBeans().get(0).getAssetType().equalsIgnoreCase(MediaTypeConstants.getInstance().getLive())) {
                                        temp.setLayoutType(4);
                                    }
                                    temp.setSearchKey(searchKeyword);
                                    temp.setTotalCount(data.get(i).getPageTotal());
                                    model.add(temp);
                                }

                            }
                        }
                    }
                } catch (Exception e) {

                }

                if (model.size() > 0) {
                    new RecyclerAnimator(this).animate(getBinding().searchResultRecycler);
                    searchAdapter = new CategoriedSearchAdapter(ActivitySearch.this, model, ActivitySearch.this);
                    getBinding().searchResultRecycler.setAdapter(searchAdapter);
                } else {
                    getBinding().noResult.setVisibility(View.VISIBLE);

                    List<String> filterGenreSavedListKeyForApi = new SharedPrefHelper(this).getDataGenreListKeyValue();
                    List<String> filterSortSavedListKeyForApi = new SharedPrefHelper(this).getDataSortListKeyValue();
                    if (filterGenreSavedListKeyForApi != null && filterGenreSavedListKeyForApi.size() > 0 || filterSortSavedListKeyForApi != null && filterSortSavedListKeyForApi.size() > 0) {
                        getBinding().toolbar.filter.setVisibility(View.VISIBLE);
                    } else {
                        getBinding().toolbar.filter.setVisibility(View.GONE);

                    }
                    getBinding().rootView.setVisibility(View.VISIBLE);
                    getBinding().llSearchResultLayout.setVisibility(View.GONE);
                }
            } else {
                getBinding().noResult.setVisibility(View.VISIBLE);
                //  getBinding().noResult1.setVisibility(View.VISIBLE);
                List<String> filterGenreSavedListKeyForApi = new SharedPrefHelper(this).getDataGenreListKeyValue();
                List<String> filterSortSavedListKeyForApi = new SharedPrefHelper(this).getDataSortListKeyValue();
                if (filterGenreSavedListKeyForApi != null && filterGenreSavedListKeyForApi.size() > 0 || filterSortSavedListKeyForApi != null && filterSortSavedListKeyForApi.size() > 0) {
                    getBinding().toolbar.filter.setVisibility(View.VISIBLE);
                } else {
                    getBinding().toolbar.filter.setVisibility(View.GONE);

                }
                getBinding().rootView.setVisibility(View.VISIBLE);
                getBinding().llSearchResultLayout.setVisibility(View.GONE);
            }
            createRecentSearch(searchKeyword);
            getBinding().progressBar.setVisibility(View.GONE);
        });


    }

    private void createRecentSearch(String searchKeyword) {
        if (searchKeyword.equalsIgnoreCase("")) {
            return;
        }
        KeywordList keywordList = new KeywordList();
        keywordList.setKeywords(searchKeyword);
        keywordList.setTimeStamp(AppCommonMethod.getCurrentTimeStamp());
        if (AppPreference.getInstance(this).getRecentSearchList().equalsIgnoreCase("")) {
            List<KeywordList> list = new ArrayList<>();
            list.add(keywordList);
            Gson gson = new Gson();
            String json = gson.toJson(list);
            AppPreference.getInstance(this).setRecentSearchList(json);
            setRecentSearchAdapter("");
        } else {
            Gson gson = new Gson();
            String json = AppPreference.getInstance(this).getRecentSearchList();
            if (json.isEmpty()) {

            } else {
                Type type = new TypeToken<List<KeywordList>>() {
                }.getType();
                List<KeywordList> arrPackageData = gson.fromJson(json, type);
                if (json.contains(searchKeyword)) {
                    return;
                }
                List<KeywordList> newL = new ArrayList<>(arrPackageData);
                if (newL.size() < 5) {
                    newL.add(keywordList);
                } else {
                    newL.remove(0);
                    newL.add(keywordList);
                }

                String jsons = gson.toJson(newL);
                AppPreference.getInstance(this).setRecentSearchList(jsons);
                Collections.reverse(newL);
                if (newL.size() > 0) {
                    RecentListAdapter recentListAdapter = new RecentListAdapter(ActivitySearch.this, newL, ActivitySearch.this);
                    getBinding().recentSearchRecycler.setAdapter(recentListAdapter);
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean containsName(final List<KeywordList> list, final String name) {
        return list.stream().map(KeywordList::getKeywords).filter(name::equals).findFirst().isPresent();
    }



   /* public void setNoResultLayout() {
        getBinding().noResult.setVisibility(View.VISIBLE);
        getBinding().rootView.setVisibility(View.VISIBLE);
        getBinding().llSearchResultLayout.setVisibility(View.GONE);
        searchAdapter.clearList(true);
    }*/


    String popularSearchId = "";

    private void hitApiPopularSearch() {
        railInjectionHelper = ViewModelProviders.of(this).get(RailInjectionHelper.class);
        if (!SDKConfig.getInstance().getPopularSearchId().equalsIgnoreCase("")) {
            railInjectionHelper.getPlayListDetailsWithPagination(this, SDKConfig.getInstance().getPopularSearchId(), 0, 5, null).observe(this, playlistRailData -> {
                if (Objects.requireNonNull(playlistRailData) != null) {
                    setUiComponents(playlistRailData);
                }
            });
        }

    }

    private void setUiComponents(RailCommonData jsonObject) {
        isShimmer = false;
        if (jsonObject.getStatus()) {
            getBinding().tvPopularSearch.setVisibility(View.VISIBLE);
        } else {
            getBinding().popularSearchRecycler.setVisibility(View.GONE);
        }

        getBinding().popularSearchRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        //new RecyclerAnimator(this).animate(getBinding().popularSearchRecycler);
        getBinding().popularSearchRecycler.setAdapter(new CommonSearchAdapter(ActivitySearch.this, jsonObject, this));
    }

    private void setRecyclerProperties(RecyclerView recyclerView) {
        recyclerView.hasFixedSize();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
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
            //pDialog.call();
            //  loadDataFromModel();
        } else {
            noConnectionLayout();
        }
    }


    private void UIinitialization() {
        getBinding().searchLayout.setVisibility(View.VISIBLE);
        getBinding().noConnectionLayout.setVisibility(View.GONE);
        getBinding().tvPopularSearch.setVisibility(View.GONE);
        getBinding().toolbar.searchView.onActionViewExpanded();
        getBinding().toolbar.searchView.requestFocus();
        getBinding().toolbar.searchText.setTextColor(Color.parseColor("#ffffff"));
        callShimmer(getBinding().popularSearchRecycler);

        clickListner();
    }


    private void callShimmer(RecyclerView recyclerView) {
        isShimmer = true;
        CommonShimmerAdapter adapterPurchase = new CommonShimmerAdapter(this, true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterPurchase);
    }

    private void noConnectionLayout() {
        getBinding().searchLayout.setVisibility(View.GONE);

        getBinding().progressBar.setVisibility(View.GONE);
        getBinding().noConnectionLayout.setVisibility(View.VISIBLE);
        getBinding().connection.retryTxt.setOnClickListener(view -> connectionObserver());
    }


    @Override
    public ActivitySearchBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivitySearchBinding.inflate(inflater);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onEnveuItemClicked(EnveuVideoItemBean itemValue) {
        try {
            AppCommonMethod.trackFcmEvent(itemValue.getTitle(), itemValue.getAssetType(), ActivitySearch.this, 0);
        } catch (Exception e) {

        }
        if (AppCommonMethod.getCheckBCID(itemValue.getBrightcoveVideoId())) {
            Long getVideoId = Long.parseLong(itemValue.getBrightcoveVideoId());
            AppCommonMethod.launchDetailScreen(this, getVideoId, itemValue.getAssetType(), itemValue.getId(), "0", false);
        }
    }

    @Override
    public void onShowAllItemClicked(RailCommonData itemValue) {

        if (itemValue != null && itemValue.getStatus()) {
            applyFilter = Boolean.parseBoolean(KsPreferenceKeys.getInstance().getFilterApply());
            new ActivityLauncher(ActivitySearch.this).resultActivityBundle(ActivitySearch.this, ActivityResults.class, itemValue.getAssetType(), itemValue.getSearchKey(), itemValue.getTotalCount(),applyFilter);

        }
    }

    @Override
    public void onPopularSearchItemClicked(ItemsItem itemValue) {
        try {
            AppCommonMethod.trackFcmEvent(itemValue.getName(), itemValue.getType(), ActivitySearch.this, 0);
        } catch (Exception e) {

        }
        if (itemValue.getType().equalsIgnoreCase("SERIES")) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (NetworkConnectivity.isOnline(ActivitySearch.this)) {
                new ActivityLauncher(ActivitySearch.this).seriesDetailScreen(ActivitySearch.this, SeriesDetailActivity.class, itemValue.getId());
            } else {
                new ToastHandler(ActivitySearch.this).show(ActivitySearch.this.getResources().getString(R.string.no_internet_connection));
            }
        } else if (itemValue.getType().equalsIgnoreCase("SEASON")) {
        } else {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            if (NetworkConnectivity.isOnline(ActivitySearch.this)) {
                new ActivityLauncher(ActivitySearch.this).detailScreen(ActivitySearch.this, DetailActivity.class, itemValue.getId(), "0", false);
            } else
                new ToastHandler(ActivitySearch.this).show(ActivitySearch.this.getResources().getString(R.string.no_internet_connection));
        }

    }

    @Override
    public void onItemClicked(KeywordList itemValue) {
        if (NetworkConnectivity.isOnline(ActivitySearch.this)) {
            hideSoftKeyboard(getBinding().toolbar.searchText);
            try {
                AppCommonMethod.trackSearchFcmEvent(ActivitySearch.this, itemValue.getKeywords().trim());
            } catch (Exception e) {

            }
            if (searchResult) {
                searchResult = false;
                getBinding().toolbar.searchView.setQuery(itemValue.getKeywords().trim(), true);
//                getBinding().toolbar.searchText.setText(itemValue.getKeywords().trim());
//                hitApiSearchKeyword(itemValue.getKeywords().trim());
            }
            //hitApiSearchSeries(getBinding().toolbar.searchText.getText().toString().trim());

        } else {
            new ToastHandler(ActivitySearch.this).show(ActivitySearch.this.getResources().getString(R.string.no_internet_connection));
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (NetworkConnectivity.isOnline(ActivitySearch.this)) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return true;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            hideSoftKeyboard(getBinding().toolbar.searchText);
            try {
                AppCommonMethod.trackSearchFcmEvent(ActivitySearch.this, query.trim());
            } catch (Exception e) {

            }
            if (query!=null && query.trim().length()>2){
                applyFilter = Boolean.parseBoolean(KsPreferenceKeys.getInstance().getFilterApply());
                searchText = query.trim();
                Log.e("SEARCHYTEXT", searchText);
                hitApiSearchKeyword(query.trim(), ActivitySearch.this, applyFilter);

            }

            //  hitApiSearchSeries(getBinding().toolbar.searchText.getText().toString().trim());
            //hitApiSearchSeries(getBinding().toolbar.searchText.getText().toString().trim());

        } else {
            new ToastHandler(ActivitySearch.this).show(ActivitySearch.this.getResources().getString(R.string.no_internet_connection));
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILTER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.e("RETURNWITHDATA", "RETURNWITHDATA");

                if (searchText != null && searchText.trim().length() > 2) {
                    applyFilter = Boolean.parseBoolean(KsPreferenceKeys.getInstance().getFilterApply());
                    hitApiSearchKeyword(searchText.trim(), ActivitySearch.this, applyFilter);
                }

            }
            else {
                applyFilter = Boolean.parseBoolean(KsPreferenceKeys.getInstance().getFilterApply());
                hitApiSearchKeyword(searchText.trim(), ActivitySearch.this, applyFilter);


            }
        }
    }
}

