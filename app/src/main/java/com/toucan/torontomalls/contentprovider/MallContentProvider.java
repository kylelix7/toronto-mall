package com.toucan.torontomalls.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.toucan.torontomalls.database.MallDatabaseHelper;

import java.util.List;

/**
 * Created by rookiebird on 10/13/14.
 */
public class MallContentProvider extends ContentProvider {

    MallDatabaseHelper dbHelper;
    // used for the UriMacher
    private static final int MALLS = 10;
    private static final int MALL_ID = 20;
    private static final int MALL_NAME = 30;

    private static final int STORES = 40;
    private static final int STORE_ID = 50;
    private static final int STORE_NAME = 60;
    private static final int STORE_CAT = 70;
    private static final int MALL_ID_STORE_ID = 80;
    private static final int MALL_ID_STORES = 90;
    private static final int MALL_ID_STORENAME_LIKE = 100;

    private static final int CATS_MALL_ID = 110;
    private static final int STORE_CAT_NAME = 120;

    public static final String AUTHORITY = "com.toucan.torontomalls.contentprovider";

    private static final String BASE_PATH = "malls";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, "malls", MALLS);
        uriMatcher.addURI(AUTHORITY, "malls/#", MALL_ID);
        uriMatcher.addURI(AUTHORITY, "malls/name/*", MALL_NAME);
        uriMatcher.addURI(AUTHORITY, "stores", STORES);
        uriMatcher.addURI(AUTHORITY, "stores/#", STORE_ID);
        uriMatcher.addURI(AUTHORITY, "stores/name/*", STORE_NAME);
        uriMatcher.addURI(AUTHORITY, "stores/cat/*", STORE_CAT);
        uriMatcher.addURI(AUTHORITY, "mall/#/store/#", STORE_CAT);
        uriMatcher.addURI(AUTHORITY, "mall/#/stores", MALL_ID_STORES);
        uriMatcher.addURI(AUTHORITY, "mall/#/stores/name/like/*", MALL_ID_STORENAME_LIKE);
        uriMatcher.addURI(AUTHORITY, "mall/#/cats", CATS_MALL_ID);
        uriMatcher.addURI(AUTHORITY, "mall/#/stores/cat/in/*", STORE_CAT_NAME);
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new MallDatabaseHelper(context);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // make sure the _id field exists in projection, add it as alias if not exists.


        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if (uriType == MALL_ID_STORE_ID || uriType == MALL_ID_STORES || uriType == MALL_ID_STORENAME_LIKE || uriType == CATS_MALL_ID || uriType == STORE_CAT_NAME) {
            projection = getSafeProjection(projection, "store.id");
        } else {
            projection = getSafeProjection(projection, "id");
        }
        String groupBy = null;
        switch (uriType) {
            case MALL_ID:
                queryBuilder.appendWhere("_id=" + uri.getLastPathSegment());
                queryBuilder.setTables("mall");
                break;
            case MALL_NAME:
                queryBuilder.appendWhere("name=" + uri.getLastPathSegment());
                queryBuilder.setTables("mall");
                break;
            case MALLS:
                queryBuilder.setTables("mall");
                break;
            case STORE_ID:
                queryBuilder.appendWhere("_id=" + uri.getLastPathSegment());
                queryBuilder.setTables("store");
                break;
            case STORE_NAME:
                queryBuilder.appendWhere("name=" + uri.getLastPathSegment());
                queryBuilder.setTables("store");
                break;
            case STORE_CAT:
                queryBuilder.appendWhere("category=" + uri.getLastPathSegment());
                queryBuilder.setTables("store");
                break;
            case STORES:
                projection = getSafeProjection(projection, "id");
                queryBuilder.setTables("store");
                break;
            case MALL_ID_STORE_ID:
                queryBuilder.setTables("store join mall_store ON store.id = mall_store.store_id");
                List<String> segments = uri.getPathSegments();
                String mallId = segments.get(1);
                String storeId = segments.get(3);
                queryBuilder.appendWhere("mall_store.id=" + storeId + " AND mall_store.mall_id=" + mallId);
                break;
            case MALL_ID_STORES:
                queryBuilder.setTables("store join mall_store ON store.id = mall_store.store_id");
                segments = uri.getPathSegments();
                mallId = segments.get(1);
                queryBuilder.appendWhere("mall_store.mall_id=" + mallId);
                break;
            case MALL_ID_STORENAME_LIKE:
                queryBuilder.setTables("store join mall_store ON store.id = mall_store.store_id");
                segments = uri.getPathSegments();
                mallId = segments.get(1);
                String storename = segments.get(5);
                queryBuilder.appendWhere("mall_store.mall_id=" + mallId + " AND store.name LIKE '%" + storename + "%'");
                break;
            case CATS_MALL_ID:
                queryBuilder.setTables("store join mall_store ON store.id = mall_store.store_id");
                segments = uri.getPathSegments();
                mallId = segments.get(1);
                queryBuilder.appendWhere("mall_store.mall_id=" + mallId);
                groupBy = "store.category";
                break;
            case STORE_CAT_NAME:
                queryBuilder.setTables("store join mall_store ON store.id = mall_store.store_id");
                segments = uri.getPathSegments();
                mallId = segments.get(1);
                String catNames = segments.get(5);
                queryBuilder.appendWhere("mall_store.mall_id=" + mallId + " AND store.category in " + catNames);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, null, sortOrder);
        Context context = getContext();
        ContentResolver contentResolver = context.getContentResolver();
        cursor.setNotificationUri(contentResolver, uri);
        return cursor;
    }

    // This is used to workaround _id column is missing in tables. Alias is needed in this solution.
    private String[] getSafeProjection(String[] projection, String theId) {
        // check if the array has _id column
        for (String c : projection) {
            if (c.equals("_id")) {
                return projection;
            }
        }
        int len = projection.length;
        String[] newProjection = new String[len + 1];
        newProjection[0] = theId + " as _id";
        for (int i = 0; i < len; i++) {
            newProjection[i + 1] = projection[i];
        }
        return newProjection;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
