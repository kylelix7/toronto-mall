package com.toucan.torontomalls;


import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.toucan.torontomalls.adapter.CategoryCursorAdapter;
import com.toucan.torontomalls.adapter.StoreCursorAdapter;
import com.toucan.torontomalls.contentprovider.MallContentProvider;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryStoreListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String CATEGORY_COL = "category";
    private static final String STORE_COUNT_COL = "store_num";
    private static final String STORE_COUNT_ALIAS = "count(store.category) AS store_num";
    private static final String STORE_NAME_COL = "name";
    private static final String STORE_PHONE_COL = "phone";

    private long mallId = -1;
    private DisplayMode mDisplayMode;
    private CategoryCursorAdapter mCatAdapter;
    private StoreCursorAdapter mStoreAdapter;

    private Button mSearchBtn;
    private ListView mCatLV;
    private Button mReCatBtn;
    private ListView mStoreLV;
    private FrameLayout mButtonPlaceholder;
    private FrameLayout mListPlaceholder;

    private static final String SEARCH_BTN_TAG = "SEARCH_BTN_TAG";
    private static final String CAT_LV_TAG = "CAT_LV_TAG";
    private static final String RECAT_BTN_TAG = "RECAT_BTN_TAG";
    private static final String STORE_LV_TAG = "STORE_LV_TAG";


    public CategoryStoreListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category_store_list, container, false);
        if (savedInstanceState != null) {
            mallId = savedInstanceState.getLong(EntryActivity.MALL_ID);
        }

        // init UI elements
        Activity containerActivity = getActivity();

        // Category List
        mCatLV = new ListView(containerActivity);
        mCatLV.setTag(CAT_LV_TAG);
        String[] from = new String[]{CATEGORY_COL, STORE_COUNT_COL};
        int[] to = new int[]{R.id.cat_name, R.id.store_count};
        mCatAdapter = new CategoryCursorAdapter(containerActivity, R.layout.cat_row, null, from, to, 0);
        mCatLV.setAdapter(mCatAdapter);


        // Search Button
        mSearchBtn = new Button(containerActivity);
        mSearchBtn.setText("Search");
        mSearchBtn.setTag(SEARCH_BTN_TAG);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mSearchBtn.setLayoutParams(layoutParams);
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDisplayMode = DisplayMode.SOTRE_LIST_RESULT_MODE;
                getLoaderManager().restartLoader(0, null, CategoryStoreListFragment.this);
            }
        });

        // Re-Categorize Button
        mReCatBtn = new Button(containerActivity);
        mReCatBtn.setText("Categorize");
        mReCatBtn.setTag(RECAT_BTN_TAG);
        mReCatBtn.setLayoutParams(layoutParams);
        mReCatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDisplayMode = DisplayMode.CAT_FILTER_MODE;
                getLoaderManager().restartLoader(0, null, CategoryStoreListFragment.this);
            }
        });

        // Result Store List
        mStoreLV = new ListView(containerActivity);
        mStoreLV.setTag(STORE_LV_TAG);
        from = new String[]{STORE_NAME_COL, CATEGORY_COL, STORE_PHONE_COL};
        to = new int[]{R.id.store_name, R.id.cat_name};
        mStoreAdapter = new StoreCursorAdapter(containerActivity, R.layout.store_row, null, from, to, 0);
        mStoreLV.setAdapter(mStoreAdapter);

        // place holders for a button and a list
        mButtonPlaceholder = (FrameLayout) rootView.findViewById(R.id.btn_placeholder);
        mListPlaceholder = (FrameLayout) rootView.findViewById(R.id.list_placeholder);

        mDisplayMode = DisplayMode.CAT_FILTER_MODE;

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(EntryActivity.MALL_ID, mallId);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void setMallId(long mallId) {
        this.mallId = mallId;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader cursorLoader = null;
        if (mDisplayMode == DisplayMode.CAT_FILTER_MODE) {
            String[] projection = {CATEGORY_COL, STORE_COUNT_ALIAS};
            Uri mallStoreUri = Uri.parse("content://" + MallContentProvider.AUTHORITY + "/mall/" + String.valueOf(mallId) + "/cats");
            cursorLoader = new CursorLoader(getActivity(), mallStoreUri, projection, null, null, null);
        } else if (mDisplayMode == DisplayMode.SOTRE_LIST_RESULT_MODE) {
            String[] projection = {STORE_NAME_COL, CATEGORY_COL, STORE_PHONE_COL};
            ArrayList<String> selectedCat = mCatAdapter.getSelectedCategories();
            Uri mallStoreUri;
            if (selectedCat.size() == 0) { // Show all if nothing selected
                mallStoreUri = Uri.parse("content://" + MallContentProvider.AUTHORITY + "/mall/" + String.valueOf(mallId) + "/stores");
            } else {
                //mall/#/stores/cat/in/*
                String in = Uri.encode(escapeAndJoin(selectedCat));
                mallStoreUri = Uri.parse("content://" + MallContentProvider.AUTHORITY + "/mall/" + String.valueOf(mallId) + "/stores/cat/in/" + in);
            }
            cursorLoader = new CursorLoader(getActivity(), mallStoreUri, projection, null, null, null);
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (mDisplayMode == DisplayMode.CAT_FILTER_MODE) {
            mCatAdapter.swapCursor(cursor);
            showCategoryList();
        } else if (mDisplayMode == DisplayMode.SOTRE_LIST_RESULT_MODE) {
            mStoreAdapter.swapCursor(cursor);
            showStoreResultList();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCatAdapter.swapCursor(null);
    }

    private static enum DisplayMode {
        CAT_FILTER_MODE, SOTRE_LIST_RESULT_MODE
    }

    private void showCategoryList() {
        mButtonPlaceholder.removeAllViews();
        mListPlaceholder.removeAllViews();
        mButtonPlaceholder.addView(mSearchBtn);
        mListPlaceholder.addView(mCatLV);
        mDisplayMode = DisplayMode.CAT_FILTER_MODE;
    }

    private void showStoreResultList() {
        mButtonPlaceholder.removeAllViews();
        mListPlaceholder.removeAllViews();
        mButtonPlaceholder.addView(mReCatBtn);
        mListPlaceholder.addView(mStoreLV);
        mDisplayMode = DisplayMode.SOTRE_LIST_RESULT_MODE;
    }

    private static String escapeAndJoin(List<String> list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            sb.append('(');
            int len = list.size();
            for (int i = 0; i < len; i++) {
                sb.append('\'').append(list.get(i).replace("'", "''")).append('\'');
                if (i != len - 1) sb.append(',');
            }
            sb.append(')');
        }
        return sb.toString();
    }
}
