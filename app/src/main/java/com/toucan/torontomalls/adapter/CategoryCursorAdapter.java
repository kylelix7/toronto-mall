package com.toucan.torontomalls.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.toucan.torontomalls.R;

import java.util.ArrayList;

/**
 * Created by rookiebird on 10/15/14.
 */
public class CategoryCursorAdapter extends SimpleCursorAdapter {
    private Context context;
    private boolean[] selections;

    public CategoryCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            Activity activity = (Activity) context;
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.cat_row, null);
            viewHolder.catname = (TextView) convertView.findViewById(R.id.cat_name);
            viewHolder.number = (TextView) convertView.findViewById(R.id.store_count);
            viewHolder.selected = (CheckBox) convertView.findViewById(R.id.cat_checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        viewHolder.catname.setText(cursor.getString(1));
        int num = cursor.getInt(2);
        String s = num > 1 ? " stores" : " store";
        viewHolder.number.setText(String.valueOf(num) + s);
        viewHolder.selected.setChecked(selections[position]);
        viewHolder.selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox) view;
                selections[position] = cb.isChecked();
            }
        });
        return convertView;
    }

    static class ViewHolder {
        public TextView catname;
        public TextView number;
        public CheckBox selected;
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        Cursor cursor = super.swapCursor(c);
        // initialize a boolean array to keep checkbox state
        int size = c == null ? 0 : c.getCount();
        selections = new boolean[size];
        return cursor;
    }

    public ArrayList<String> getSelectedCategories() {
        ArrayList<String> categories = new ArrayList<String>();
        Cursor c = getCursor();
        if (c != null) {
            for (int i = 0; i < selections.length; i++) {
                if (selections[i]) {
                    c.moveToPosition(i);
                    String cat = c.getString(1);
                    categories.add(cat);
                }
            }
        }
        return categories;
    }
}
