package com.toucan.torontomalls;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class StoreDirectoryActivity extends Activity {

    private static final String SIMPLE_STORE_TAG = "SIMPLE_STORE_TAG";
    private static final String CAT_STORE_TAG = "CAT_STORE_TAG";
    public static final String STORE_NAME = "name";
    public static final String STORE_PHONE = "phone";
    public static final String CAT_NAME = "category";

    private long mallId = -1;
    private String mallName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_directory);

        if (savedInstanceState != null) {
            mallId = savedInstanceState.getLong(EntryActivity.MALL_ID);
            mallName = savedInstanceState.getString(EntryActivity.MALL_NAME);
        } else {
            mallId = getIntent().getExtras().getLong(EntryActivity.MALL_ID);
            mallName = getIntent().getExtras().getString(EntryActivity.MALL_NAME);
        }

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        ActionBar.Tab tab = actionBar.newTab().setText("ALL").setTabListener(new TabListener<SimpleStoreListFragment>(
                this, SIMPLE_STORE_TAG, SimpleStoreListFragment.class));
        actionBar.addTab(tab);
        tab = actionBar.newTab().setText("CATEGORIES").setTabListener(new TabListener<CategoryStoreListFragment>(
                this, CAT_STORE_TAG, CategoryStoreListFragment.class));
        actionBar.addTab(tab);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(EntryActivity.MALL_ID, mallId);
        savedInstanceState.putString(EntryActivity.MALL_NAME, mallName);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.store_directory, menu);
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

    public class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment mFragment;
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;

        /**
         * Constructor used each time a new tab is created.
         *
         * @param activity The host Activity, used to instantiate the fragment
         * @param tag      The identifier tag for the fragment
         * @param clz      The fragment's Class, used to instantiate the fragment
         */
        public TabListener(Activity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
        }

    /* The following are each of the ActionBar.TabListener callbacks */

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            // Check if the fragment is already initialized
            if (mFragment == null) {
                // If not, instantiate and add it to the activity
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                // If it exists, simply attach it in order to show it
                ft.attach(mFragment);
            }

            if (mFragment instanceof SimpleStoreListFragment) {
                SimpleStoreListFragment f = (SimpleStoreListFragment) mFragment;
                f.setMallId(mallId);
            } else if (mFragment instanceof CategoryStoreListFragment) {
                CategoryStoreListFragment f = (CategoryStoreListFragment) mFragment;
                f.setMallId(mallId);
            }
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                // Detach the fragment, because another one is being attached
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            // User selected the already selected tab. Usually do nothing.
        }
    }
}
