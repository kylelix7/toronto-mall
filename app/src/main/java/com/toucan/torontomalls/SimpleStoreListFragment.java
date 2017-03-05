package com.toucan.torontomalls;


import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.toucan.torontomalls.adapter.StoreCursorAdapter;
import com.toucan.torontomalls.contentprovider.MallContentProvider;


/**
 * A simple {@link Fragment} subclass.
 */
public class SimpleStoreListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private StoreCursorAdapter mAdapter;
    private String mCurFilter;
    private long mallId = -1;

    public SimpleStoreListFragment() {
        // Required empty public constructor
    }

    public static final String STORE_NAME = "name";
    public static final String STORE_PHONE = "phone";
    private static final String STORE_CAT = "category";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_simple_store_list, container, false);

        if (savedInstanceState != null) {
            mallId = savedInstanceState.getLong(EntryActivity.MALL_ID);
        }

        Activity containerActivity = getActivity();
        final ListView storeLV = (ListView) rootView.findViewById(R.id.store_list);
        String[] from = new String[]{STORE_NAME, STORE_CAT, STORE_PHONE};
        int[] to = new int[]{R.id.store_name, R.id.cat_name};
        mAdapter = new StoreCursorAdapter(containerActivity, R.layout.store_row, null, from, to, 0);
        storeLV.setAdapter(mAdapter);

        final EditText searchET = (EditText) rootView.findViewById(R.id.search_et);
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mCurFilter = TextUtils.isEmpty(charSequence) ? null : charSequence.toString();
                getLoaderManager().restartLoader(0, null, SimpleStoreListFragment.this);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        return rootView;
    }

    public void setMallId(long mallId) {
        this.mallId = mallId;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(EntryActivity.MALL_ID, mallId);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {StoreDirectoryActivity.STORE_NAME, StoreDirectoryActivity.CAT_NAME, StoreDirectoryActivity.STORE_PHONE};
        Uri baseUri = Uri.parse("content://" + MallContentProvider.AUTHORITY + "/mall/" + String.valueOf(mallId) + "/stores");
        if (mCurFilter != null) {
            baseUri = Uri.withAppendedPath(baseUri, "name/like/" + Uri.encode(mCurFilter));
        }
        CursorLoader cursorLoader = new CursorLoader(getActivity(), baseUri, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }
}
