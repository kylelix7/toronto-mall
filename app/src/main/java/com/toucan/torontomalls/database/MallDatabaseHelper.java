package com.toucan.torontomalls.database;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by rookiebird on 10/13/14.
 */
public class MallDatabaseHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "shopping.db";
    private static final int DATABASE_VERSION = 2;
    public MallDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
