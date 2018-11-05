package com.exceedgulf.alainzoo.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.exceedgulf.alainzoo.Interfaces.ApiCallback;
import com.exceedgulf.alainzoo.R;
import com.exceedgulf.alainzoo.activity.HomeActivity;
import com.exceedgulf.alainzoo.adapter.SearchItemAdapter;
import com.exceedgulf.alainzoo.adapter.SearchTypeAdapter;
import com.exceedgulf.alainzoo.custom.RecyclerViewEmptySupport;
import com.exceedgulf.alainzoo.databinding.FragmentSearchBinding;
import com.exceedgulf.alainzoo.managers.SearchManager;
import com.exceedgulf.alainzoo.models.SearchTypeModel;
import com.exceedgulf.alainzoo.utils.DisplayDialog;
import com.exceedgulf.alainzoo.utils.SnackbarUtils;
import com.exceedgulf.alainzoo.utils.Utils;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;

/**
 * Created by R.S. on 20/12/17
 */
public class SearchFragment extends BaseFragment implements SearchTypeAdapter.onTypeSelectionListener {
    public static final String KEY_SEARCH_KEYWORD = "SEARCH_KEYWORD";
    private static String ALL = "all";
    private static String WHATSNEW = "events";
    private static String ANIMALS = "animals";
    private static String EXPERIENCE = "experience";
    private SearchItemAdapter searchItemAdapter;
    private SearchTypeAdapter searchTypeAdapter;
    private FragmentSearchBinding searchBinding;
    private RecyclerViewEmptySupport rvResult;
    private RecyclerViewEmptySupport rvType;
    private String searchKeyword = "";
    private String strType;
    private int page = 0;
    private boolean isLoading = false;
    private boolean isLoadMore = true;
    private LinearLayoutManager layoutManager;
    private final RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!isLoading && isLoadMore) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    page++;
                    getSearchResultList(searchBinding.fragmentSearchEdtSearch.getText().toString(), strType);
                }
            }
        }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        searchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        return searchBinding.getRoot();
    }


    @Override
    public void initView(final View view) {
        ((HomeActivity) getActivity()).disableCollapse();
        ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.search), getResources().getColor(R.color.very_light_brown), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);
        searchBinding.fragmentSearchTvnoresult.setText(getString(R.string.oops_no_result_found));
        layoutManager = new LinearLayoutManager(getActivity());
        //searchBinding.fragmentSearchRvSearchResult.setEmptyView(searchBinding.frSearchTvEmptyView);
        searchBinding.fragmentSearchRvSearchResult.emptyObserver.onChanged();
        searchBinding.fragmentSearchRvSearchResult.setLayoutManager(layoutManager);
        searchBinding.fragmentSearchRvSearchResult.addOnScrollListener(recyclerViewOnScrollListener);
        searchItemAdapter = new SearchItemAdapter(getActivity());
        searchBinding.fragmentSearchRvSearchResult.setAdapter(searchItemAdapter);

        final FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);

        searchBinding.fragmentSearchRvSearchType.setLayoutManager(layoutManager);
        searchTypeAdapter = new SearchTypeAdapter(getActivity(), this);
        searchBinding.fragmentSearchRvSearchType.setAdapter(searchTypeAdapter);

        searchBinding.fragmentSearchEdtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Utils.getInstance().hideSoftKeyboard(getActivity());
                    final String str = searchBinding.fragmentSearchTvCancel.getText().toString();
                    if (str.equalsIgnoreCase(getString(R.string.search))) {
                        final String strSearch = searchBinding.fragmentSearchEdtSearch.getText().toString();
                        if (!TextUtils.isEmpty(strSearch)) {
                            searchBinding.fragmentSearchTvCancel.setText(getString(R.string.cancel));
                            searchItemAdapter.clearItems();
                            getSearchResultList(strSearch, strType);
                        }
                    } else {
                        searchBinding.fragmentSearchEdtSearch.setText("");
                        searchBinding.fragmentSearchTvCancel.setText(getString(R.string.search));
                        getSearchResultList("", strType);
                    }
                }
                return false;
            }
        });

        searchBinding.fragmentSearchEdtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //if (searchBinding.fragmentSearchEdtSearch.getText().toString().trim().length() == 0) {
                searchBinding.fragmentSearchTvCancel.setText(getString(R.string.search));
                if (searchBinding.fragmentSearchEdtSearch.getText().toString().length() == 0) {
                    Utils.getInstance().hideSoftKeyboard(getActivity());
                    getSearchResultList(searchBinding.fragmentSearchEdtSearch.getText().toString(), strType);
                }
                //}
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchBinding.fragmentSearchTvCancel.setOnClickListener(this);
        searchBinding.fragmentSearchRvSearchResult.emptyObserver.onChanged();

        final Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(KEY_SEARCH_KEYWORD)) {
            searchKeyword = bundle.getString(KEY_SEARCH_KEYWORD);
            if (searchKeyword.equalsIgnoreCase(getString(R.string.all))) {
                strType = ALL;
            } else if (searchKeyword.equalsIgnoreCase(getString(R.string.mAnimals))) {
                strType = ANIMALS;
            } else if (searchKeyword.equalsIgnoreCase(getString(R.string.mExperiences))) {
                strType = EXPERIENCE;
            } else if (searchKeyword.equalsIgnoreCase(getString(R.string.title_whats_new))) {
                strType = WHATSNEW;
            }
        } else {
            searchKeyword = getString(R.string.all);
            setTypeData();
        }
        getSearchResultList(searchBinding.fragmentSearchEdtSearch.getText().toString(), strType);
    }


    private void setTypeData() {
        final SearchTypeModel all = new SearchTypeModel(getString(R.string.all));
        all.setSelected(searchKeyword.equalsIgnoreCase(getString(R.string.all)));

        final SearchTypeModel animals = new SearchTypeModel(getString(R.string.mAnimals));
        animals.setSelected(searchKeyword.equalsIgnoreCase(getString(R.string.mAnimals)));

        final SearchTypeModel expareiences = new SearchTypeModel(getString(R.string.mExperiences));
        expareiences.setSelected(searchKeyword.equalsIgnoreCase(getString(R.string.mExperiences)));

        final SearchTypeModel educations = new SearchTypeModel(getString(R.string.title_whats_new));
        educations.setSelected(searchKeyword.equalsIgnoreCase(getString(R.string.title_whats_new)));


        final ArrayList<SearchTypeModel> models = new ArrayList<>();
        models.add(all);
        models.add(animals);
        models.add(expareiences);
        models.add(educations);

        searchTypeAdapter.addAllModel(models);
        if (searchKeyword.equalsIgnoreCase(getString(R.string.all))) {
            searchTypeAdapter.selectedPosition = String.valueOf(0);
        }
        // searchBinding.fragmentSearchRvSearchType.emptyObserver.onChanged();

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!isOnClick()) {
            return;
        }
        if (v == searchBinding.fragmentSearchTvCancel) {
            final String str = searchBinding.fragmentSearchTvCancel.getText().toString();
            if (str.equalsIgnoreCase(getString(R.string.search))) {
                final String strSearch = searchBinding.fragmentSearchEdtSearch.getText().toString().trim();
                if (!TextUtils.isEmpty(strSearch)) {
                    page = 0;
                    searchBinding.fragmentSearchTvCancel.setText(getString(R.string.cancel));
                    searchItemAdapter.clearItems();
                    getSearchResultList(strSearch, strType);
                }
            } else {
                if (!TextUtils.isEmpty(searchBinding.fragmentSearchEdtSearch.getText().toString().trim())) {
                    page = 0;
                    searchBinding.fragmentSearchEdtSearch.setText("");
                    searchBinding.fragmentSearchTvCancel.setText(getString(R.string.search));
                    searchItemAdapter.clearItems();
                    getSearchResultList("", strType);
                }
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HomeActivity) getActivity()).disableCollapse();
            ((HomeActivity) getActivity()).setToolbarWithCenterTitle(getString(R.string.search), getResources().getColor(R.color.very_light_brown), ContextCompat.getDrawable(getActivity(), R.drawable.ic_back_actionbar), false);

        }
    }

    public void getSearchResultList(final String title, final String type) {
        if (searchItemAdapter.getItemCount() == 0) {
            searchBinding.frSearchTvEmptyView.setVisibility(View.VISIBLE);
        }
        isLoading = true;
        DisplayDialog.getInstance().showProgressDialog(getActivity(), "Loading...", false);
        SearchManager.getSearchManager().getAllEntitiesData(page, title, type, new ApiCallback() {
            @Override
            public void onSuccess(ArrayList resultList, boolean isloadmore) {
                DisplayDialog.getInstance().dismissProgressDialog();
                isLoading = false;
                isLoadMore = isloadmore;
                if (isAdded()) {
                    searchItemAdapter.addItems(resultList);
                    if (searchItemAdapter.getItemCount() == 0) {
                        searchBinding.frSearchTvEmptyView.setVisibility(View.GONE);
                        //searchBinding.fragmentSearchRvSearchResult.setVisibility(View.GONE);
                        searchBinding.fragmentSearchLlEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        searchBinding.fragmentSearchLlEmptyView.setVisibility(View.GONE);
                        // searchBinding.fragmentSearchRvSearchResult.setVisibility(View.VISIBLE);
                        searchBinding.frSearchTvEmptyView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFaild(String message) {
                DisplayDialog.getInstance().dismissProgressDialog();
                isLoading = false;
                isLoadMore = false;
                SnackbarUtils.loadSnackBar(message, getActivity(), R.color.very_light_brown);
                if (isAdded()) {
                    if (searchItemAdapter.getItemCount() == 0) {
                        //searchBinding.fragmentSearchRvSearchResult.setVisibility(View.GONE);
                        searchBinding.fragmentSearchLlEmptyView.setVisibility(View.VISIBLE);
                        searchBinding.frSearchTvEmptyView.setVisibility(View.GONE);
                    } else {
                        searchBinding.fragmentSearchLlEmptyView.setVisibility(View.GONE);
                        searchBinding.frSearchTvEmptyView.setVisibility(View.GONE);
                        //searchBinding.fragmentSearchRvSearchResult.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onTypeClick(String name) {
        if (name.equalsIgnoreCase(getString(R.string.all))) {
            page = 0;
            strType = ALL;
            searchItemAdapter.clearItems();
        } else if (name.equalsIgnoreCase(getString(R.string.mAnimals))) {
            page = 0;
            strType = ANIMALS;
            searchItemAdapter.clearItems();
        } else if (name.equalsIgnoreCase(getString(R.string.mExperiences))) {
            page = 0;
            strType = EXPERIENCE;
            searchItemAdapter.clearItems();
        } else if (name.equalsIgnoreCase(getString(R.string.title_whats_new))) {
            page = 0;
            strType = WHATSNEW;
            searchItemAdapter.clearItems();
        }
        getSearchResultList(searchBinding.fragmentSearchEdtSearch.getText().toString(), strType);
    }
}
