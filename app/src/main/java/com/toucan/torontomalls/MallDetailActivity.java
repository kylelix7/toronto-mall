package com.toucan.torontomalls;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.toucan.torontomalls.database.MallDatabaseHelper;

import java.io.IOException;
import java.util.List;


public class MallDetailActivity extends Activity {

    private long mallId = -1;
    private String name;
    private String hours;
    private String address;
    private String phone;
    private double lat;
    private double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_detail);

        if (savedInstanceState != null) {
            mallId = savedInstanceState.getLong(EntryActivity.MALL_ID);
        } else {
            mallId = getIntent().getExtras().getLong(EntryActivity.MALL_ID);
        }

        initUI();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(EntryActivity.MALL_ID, mallId);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mall_detail, menu);
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

    private void initUI() {
        MallDatabaseHelper databaseHelper = new MallDatabaseHelper(this);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables("mall");
        queryBuilder.appendWhere("id=" + mallId);
        Cursor cursor = queryBuilder.query(database, new String[]{EntryActivity.MALL_NAME, EntryActivity.MALL_HOURS, EntryActivity.MALL_ADDRESS, EntryActivity.MALL_PHONE, EntryActivity.MALL_LAT, EntryActivity.MALL_LNG}, null, null, null, null, null);
        cursor.moveToNext();
        name = cursor.getString(EntryActivity.NAME_COLUMN);
        hours = cursor.getString(EntryActivity.HOURS_COLUMN).replace("!", "\n");
        address = cursor.getString(EntryActivity.ADDRESS_COLUMN);
        phone = cursor.getString(EntryActivity.PHONE_COLUMN);
        lat = cursor.getDouble(EntryActivity.LAT_COLUMN);
        lng = cursor.getDouble(EntryActivity.LNG_COLUMN);
        cursor.close();
        database.close();
        databaseHelper.close();

        final TextView nameTV = (TextView) findViewById(R.id.mall_name);
        final TextView storesTV = (TextView) findViewById(R.id.stores);
        final TextView phoneTV = (TextView) findViewById(R.id.phone);
        final TextView addressTV = (TextView) findViewById(R.id.address);
        final TextView hoursTV = (TextView) findViewById(R.id.hours);
        nameTV.setText(name);
        phoneTV.setText(phone);
        phoneTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });
        addressTV.setText(address);
        addressTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String map = "http://maps.google.com/maps?q=" + address;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(intent);
            }
        });
        hoursTV.setText(hours);
        storesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MallDetailActivity.this, StoreDirectoryActivity.class);
                intent.putExtra(EntryActivity.MALL_ID, mallId);
                intent.putExtra(EntryActivity.MALL_NAME, name);
                startActivity(intent);
            }
        });

        LatLng location = new LatLng(lat, lng);
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        map.addMarker(new MarkerOptions().position(location).title(name));
        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
    }

}
