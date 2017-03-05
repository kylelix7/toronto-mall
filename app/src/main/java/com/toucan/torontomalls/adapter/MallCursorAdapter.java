package com.toucan.torontomalls.adapter;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

/**
 * Created by rookiebird on 10/13/14.
 */
public class MallCursorAdapter extends SimpleCursorAdapter {
    public MallCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

}
