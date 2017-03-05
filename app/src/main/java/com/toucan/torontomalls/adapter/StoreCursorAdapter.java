package com.toucan.torontomalls.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;

import com.toucan.torontomalls.R;

public class StoreCursorAdapter extends SimpleCursorAdapter {
    public static final int STORE_NAME_COL_NUM = 2;
    public static final int STORE_PHONE_COL_NUM = 3;
    private Context context;
    public StoreCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        final String phone = cursor.getString(STORE_PHONE_COL_NUM);
        ImageButton phoneButton = (ImageButton)view.findViewById(R.id.phone);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phone));
                context.startActivity(intent);
            }
        });
        return view;
    }
}
