package com.toucan.torontomalls;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.toucan.torontomalls.adapter.MallCursorAdapter;
import com.toucan.torontomalls.contentprovider.MallContentProvider;


public class EntryActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String MALL_ID = "MALL_ID";
    private MallCursorAdapter adapter;

    public static final int NAME_COLUMN = 0;
    public static final int HOURS_COLUMN = 1;
    public static final int ADDRESS_COLUMN = 2;
    public static final int PHONE_COLUMN = 3;
    public static final int LAT_COLUMN = 4;
    public static final int LNG_COLUMN = 5;

    public static final String MALL_NAME = "name";
    public static final String MALL_HOURS = "hours";
    public static final String MALL_ADDRESS = "address";
    public static final String MALL_PHONE = "phone";
    public static final String MALL_LAT = "lat";
    public static final String MALL_LNG = "lng";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        getLoaderManager().initLoader(0, null, this);
        final ListView mallLV = (ListView) findViewById(R.id.mall_list);
        mallLV.setDividerHeight(2);

        String[] from = new String[]{MALL_NAME}; //{MALL_ID, MALL_NAME, MALL_HOURS, MALL_ADDRESS, MALL_PHONE};
        int[] to = new int[]{R.id.mall_name};
        adapter = new MallCursorAdapter(this, R.layout.mall_row, null, from, to, 0);
        mallLV.setAdapter(adapter);
        mallLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(EntryActivity.this, MallDetailActivity.class);
                intent.putExtra(MALL_ID, id);

                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setMessage("This app uses Android SQLiteAssetHelper library that is under Apache License, Version 2.0. Click Yes to go to the library's page.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jgilfelt/android-sqlite-asset-helper"));
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {MALL_NAME};
        CursorLoader cursorLoader = new CursorLoader(this,
                MallContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }

}
