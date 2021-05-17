package me.vipa.app.activities.search.ui;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;
import java.util.List;

import me.vipa.app.R;
import me.vipa.app.activities.search.adapter.GenereSearchAdapter;
import me.vipa.app.activities.search.adapter.SortAdapter;
import me.vipa.app.baseModels.BaseBindingActivity;
import me.vipa.app.beanModel.filterModel.SortModel;
import me.vipa.app.databinding.ActivityFilterIconBinding;
import me.vipa.app.utils.commonMethods.AppCommonMethod;
import me.vipa.app.utils.config.bean.ConfigBean;
import me.vipa.app.utils.config.bean.Genre;
import me.vipa.app.utils.helpers.NetworkConnectivity;
import me.vipa.app.utils.helpers.SharedPrefHelper;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;

public class FilterIconActivity extends BaseBindingActivity<ActivityFilterIconBinding> implements GenereSearchAdapter.GenreListener, SortAdapter.SortByListener {
    private static final int VERTICAL_ITEM_SPACE = 30;
    private static final int HORIONTAL_ITEM_SPACE = 20;
    private GenereSearchAdapter genereSearchAdapter;
    private SortAdapter sortAdapter;

    private ConfigBean configBean;
    private String currentLanguage;
    //private List<GenereModel> genereModels = new ArrayList<>();

    // Genre work  List declarations
    private List<Genre> genereModels = new ArrayList<>();
    private List<String> filterGenreSelectedList;
    private List<String> filterGenreSavedList;
    private List<String> filterGenreSelectedListKeyForApi;
    private List<String> filterGenreSavedListKeyForApi;
    // Sort work lists declarations
    private List<SortModel> sortModels = new ArrayList<>();
    private List<String> filterSortSelectedList;
    private List<String> filterSortSavedList;
    private List<String> filterSortSelectedListKeyForApi;
    private List<String> filterSortSavedListKeyForApi;

    @Override
    public ActivityFilterIconBinding inflateBindingLayout(@NonNull LayoutInflater inflater) {
        return ActivityFilterIconBinding.inflate(inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        filterGenreSelectedList = new ArrayList<>();
        filterGenreSavedList = new ArrayList<>();
        filterGenreSelectedListKeyForApi = new ArrayList<>();
        filterGenreSavedListKeyForApi = new ArrayList<>();

        filterSortSelectedList = new ArrayList<>();
        filterSortSavedList = new ArrayList<>();
        filterSortSelectedListKeyForApi = new ArrayList<>();
        filterSortSavedListKeyForApi = new ArrayList<>();
        clickListner();
        connectionObserver();
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
        getBinding().noConnectionLayout.setVisibility(View.GONE);
        setupToolbar();
        currentLanguage = KsPreferenceKeys.getInstance().getAppLanguage();
        configBean = AppCommonMethod.getConfigResponse();
        filterGenreSavedList = new SharedPrefHelper(FilterIconActivity.this).getDataGenreList();
        filterGenreSelectedList = filterGenreSavedList;
        filterGenreSavedListKeyForApi = new SharedPrefHelper(FilterIconActivity.this).getDataGenreListKeyValue();
        filterGenreSelectedListKeyForApi = filterGenreSavedListKeyForApi;


        filterSortSavedList = new SharedPrefHelper(FilterIconActivity.this).getDataSortList();
        filterSortSelectedList = filterSortSavedList;

        filterSortSavedListKeyForApi = new SharedPrefHelper(FilterIconActivity.this).getDataSortListKeyValue();
        filterSortSelectedListKeyForApi = filterSortSavedListKeyForApi;

        getGenreData();
        getSortData();
        clickListner();
    }

    private void noConnectionLayout() {

        getBinding().noConnectionLayout.setVisibility(View.VISIBLE);
        getBinding().connection.retryTxt.setOnClickListener(view -> connectionObserver());
    }


    private void clickListner() {
        getBinding().toolbar.clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < genereModels.size(); i++) {
                    genereModels.get(i).setGenereChecked(false);
                }
                for (int i = 0; i < sortModels.size(); i++) {
                    sortModels.get(i).setSortChecked(false);
                }
                genereSearchAdapter.notifyDataSetChanged();
                sortAdapter.notifyDataSetChanged();
                AppCommonMethod.resetFilter(FilterIconActivity.this);
            }
        });
        getBinding().tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkSelections()) {
                    KsPreferenceKeys.getInstance().setFilterApply("true");
                    if (filterGenreSelectedList != null && filterGenreSelectedListKeyForApi != null) {
                        new SharedPrefHelper(FilterIconActivity.this).saveDataGenre(filterGenreSelectedList);
                        new SharedPrefHelper(FilterIconActivity.this).saveDataGenreKeyValue(filterGenreSelectedListKeyForApi);
                        Log.e("SELECTEDLIST", filterGenreSelectedList.toString());
                        Log.e("SELECTEDLISTKEYValue", filterGenreSelectedListKeyForApi.toString());
                    }

                    if (filterSortSelectedList != null && filterSortSelectedListKeyForApi != null) {
                        new SharedPrefHelper(FilterIconActivity.this).saveDataSort(filterSortSelectedList);
                        new SharedPrefHelper(FilterIconActivity.this).saveDataSortKeyValue(filterSortSelectedListKeyForApi);
                        Log.e("SELECTEDSort", filterSortSelectedList.toString());
                        Log.e("SELECTEDSortKEYvalue", filterSortSelectedListKeyForApi.toString());


                    }

                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    for (int i = 0; i < genereModels.size(); i++) {
                        genereModels.get(i).setGenereChecked(false);
                    }
                    for (int i = 0; i < sortModels.size(); i++) {
                        sortModels.get(i).setSortChecked(false);
                    }
                    genereSearchAdapter.notifyDataSetChanged();
                    sortAdapter.notifyDataSetChanged();
                    AppCommonMethod.resetFilter(FilterIconActivity.this);
                    finish();
                   // Toast.makeText(FilterIconActivity.this, getResources().getString(R.string.select_one), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (KsPreferenceKeys.getInstance().getFilterApply().equalsIgnoreCase("false")) {
            AppCommonMethod.resetFilter(FilterIconActivity.this);

        }

    }

    private void setupToolbar() {
        getBinding().toolbar.llSearchIcon.setVisibility(View.GONE);
        getBinding().toolbar.backLayout.setVisibility(View.VISIBLE);
        getBinding().toolbar.homeIcon.setVisibility(View.GONE);
        getBinding().toolbar.titleText.setVisibility(View.VISIBLE);
        getBinding().toolbar.clearText.setVisibility(View.VISIBLE);
        getBinding().toolbar.screenText.setText(getString(R.string.filters));

        getBinding().toolbar.backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void getGenreData() {
/*        genereModels.add(new GenereModel("Entertainment"));
        genereModels.add(new GenereModel("Horror"));
        genereModels.add(new GenereModel("kids"));
        genereModels.add(new GenereModel("Lifestyle"));
        genereModels.add(new GenereModel("Movie"));
        genereModels.add(new GenereModel("Music"));
        genereModels.add(new GenereModel("News"));
        genereModels.add(new GenereModel("Relegious"));
        genereModels.add(new GenereModel("FoxMovies"));
        genereModels.add(new GenereModel("Romance"));
        genereModels.add(new GenereModel("Sad"));
        genereModels.add(new GenereModel("Science"));
        genereModels.add(new GenereModel("Thriller"));
        genereModels.add(new GenereModel("ille"));
        genereModels.add(new GenereModel("Vlog"));*/
        if (configBean.getData().getAppConfig().getSearchFilters() != null) {
            if (configBean.getData().getAppConfig().getSearchFilters().getGenres() != null) {
                genereModels = configBean.getData().getAppConfig().getSearchFilters().getGenres();
            } else {
                getBinding().tvGenre.setVisibility(View.GONE);
                getBinding().vGenre.setVisibility(View.GONE);
                getBinding().genreRecyclerView.setVisibility(View.GONE);
            }

        } else {
            getBinding().tvGenre.setVisibility(View.GONE);
            getBinding().vGenre.setVisibility(View.GONE);

            getBinding().genreRecyclerView.setVisibility(View.GONE);

        }


        setRecyclerProperties(getBinding().genreRecyclerView, true);

        if (filterGenreSavedList != null) {
            for (int i = 0; i < genereModels.size(); i++) {

                for (int j = 0; j < filterGenreSavedList.size(); j++) {
                    if (currentLanguage.equalsIgnoreCase("Thai")) {
                        if (genereModels.get(i).getDisplayName().getTh().equalsIgnoreCase(filterGenreSavedList.get(j))) {
                            genereModels.get(i).setGenereChecked(true);
                        }
                    } else if (currentLanguage.equalsIgnoreCase("English")) {
                        if (genereModels.get(i).getDisplayName().getEnUS().equalsIgnoreCase(filterGenreSavedList.get(j))) {
                            genereModels.get(i).setGenereChecked(true);
                        }
                    } else {
                        if (genereModels.get(i).getDisplayName().getEnUS().equalsIgnoreCase(filterGenreSavedList.get(j))) {
                            genereModels.get(i).setGenereChecked(true);
                        }
                    }

                }

            }

        }

        loadDataFromModelGenere();

    }

    private void getSortData() {
        sortModels.add(new SortModel("A to Z"));
        sortModels.add(new SortModel("Z to A"));
        setRecyclerProperties(getBinding().sortRecyclerView, false);
        if (filterSortSavedList != null) {
            for (int i = 0; i < sortModels.size(); i++) {
                for (int j = 0; j < filterSortSavedList.size(); j++) {
                    if (sortModels.get(i).getSortLists().equalsIgnoreCase(filterSortSavedList.get(j))) {
                        sortModels.get(i).setSortChecked(true);
                    }
                }
            }
        }
        loadDataFromModelSorts();
    }

    private void loadDataFromModelGenere() {
        genereSearchAdapter = new GenereSearchAdapter(FilterIconActivity.this, genereModels, (GenereSearchAdapter.GenreListener) this);
        getBinding().genreRecyclerView.setAdapter(genereSearchAdapter);
        getBinding().genreRecyclerView.getAdapter().notifyDataSetChanged();
        /*   else {
            ((GenereSearchAdapter)getBinding().genreRecyclerView.getAdapter()).update(FilterIconActivity.this,  genereModels);
            getBinding().genreRecyclerView.getAdapter().notifyDataSetChanged();
        }*/
    }

    private void loadDataFromModelSorts() {
        sortAdapter = new SortAdapter(FilterIconActivity.this, sortModels, (SortAdapter.SortByListener) this);
        getBinding().sortRecyclerView.setAdapter(sortAdapter);
        getBinding().sortRecyclerView.getAdapter().notifyDataSetChanged();

       /* else {
            ((SortAdapter)getBinding().sortRecyclerView.getAdapter()).update(FilterIconActivity.this,  sortModels);
            getBinding().sortRecyclerView.getAdapter().notifyDataSetChanged();
        }*/
    }


    private void setRecyclerProperties(RecyclerView recyclerView, boolean space) {
        recyclerView.hasFixedSize();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(FilterIconActivity.this, RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(HORIONTAL_ITEM_SPACE));
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        flowLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(flowLayoutManager);
    }

    @Override
    public void setGenre(String selectedGenre1, String selectedGenre3) {
        filterGenreSelectedListKeyForApi = AppCommonMethod.createFilterGenreList(selectedGenre1);
        filterGenreSelectedList = AppCommonMethod.createFilterGenreList(selectedGenre3);
    }

    @Override
    public void setSortBy(String selectedSort1, String selectedSort3) {
        // selectedSort = selectedGenre1;
        filterSortSelectedListKeyForApi = AppCommonMethod.createFilterGenreList(selectedSort1);
        filterSortSelectedList = AppCommonMethod.createFilterGenreList(selectedSort3);
    }


    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }

    public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int horizontalItemSpace;

        public HorizontalSpaceItemDecoration(int horiontalItemSpace) {
            this.horizontalItemSpace = horiontalItemSpace;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            outRect.left = horizontalItemSpace;
        }
    }

    private boolean checkSelections() {
        boolean selected;
        for (int i = 0; i < genereModels.size(); i++) {
            if (genereModels.get(i).isGenereChecked()) {
                selected = true;
                return selected;
            }

        }
        for (int k = 0; k < sortModels.size(); k++) {
            if (sortModels.get(k).isSortChecked()) {
                selected = true;
                return selected;
            }

        }
        return false;

      /*  if(filterGenreSavedList!=null && filterGenreSavedList.size()>0){
            if (filterGenreSavedList.isEmpty() ) {
                selected = false;
                return selected;
            } else {
                return true;
            }

        }*/
       /* else {
            if (filterGenreSelectedList.isEmpty() ) {
                selected = false;
                return selected;
            } else {
                return true;
            }

        }*/


    }

}


